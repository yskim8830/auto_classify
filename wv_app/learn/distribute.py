import logging
from datetime import datetime
from ..util import run_util
from ..util.const import const
from ..util.es_util import elastic_util

logger = logging.getLogger('my')
index = const()
class dist():
    def __init__(self,name):
        self.name = name #Thread Name
        
    def run(self, data):
        try:
            result_code = '200'
            error_msg = ""
            es_urls = str(data['esUrl']).split(':')
            #검색엔진에 연결한다.
            es = elastic_util(es_urls[0], es_urls[1])
            
            site_no = data['siteNo']
            userId = data['userId']
            version = int(data['version'])
            if version == 0:
                result_code = '999'
                error_msg = '학습된 데이터가 아닙니다.'
                raise Exception(error_msg)
            #현재 사이트가 학습 중 인지 확인한다.
            if version == -1:
                version = run_util.isRunning(es,site_no)
                
            
            logger.info("[devToSvc] start [ userId : "+userId +" / siteNo :"+str(site_no)+" / version :"+str(version)+"]")
            
            if version > -1:
                # $train_state 상태를 업데이트 한다.
                mapData = {}
                mapData['id'] = site_no
                mapData['version'] = version ##학습중인 상태를 나타냄.
                mapData['siteNo'] = site_no
                mapData['state'] = 'y'
                mapData['status'] = '06' #배포중
                mapData['modify_date'] = datetime.now().strftime('%Y%m%d%H%M%S%f')[:-3]
                es.updateData(index.train_state, site_no, mapData)
                
                def runIndexDevToSvc(indexName):
                    logger.info("[runIndexDevToSvc] start [ indexName : "+indexName +" / siteNo : "+str(site_no)+" / version : "+str(version)+" ]")
                
                    query_string = {
                        "query": {
                            "query_string": {
                                "query": "siteNo:" + str(site_no) + " AND version:" + str(version)
                            }
                        }
                    }
                    devMIndex = index.dev_idx + indexName + str(site_no)
                    prdMIndex = index.svc_idx + indexName + str(site_no) + '_1'
                    bckMIndex = ''
                    prdMAlias = index.als_idx + indexName + str(site_no)
                    mTotCount = es.countBySearch(devMIndex, query_string)
                    aliasInfo = es.getAlias(prdMAlias)
                    if len(aliasInfo) > 0 :
                        if list(aliasInfo.keys())[0].find(str(site_no)+'_1') > -1:
                            prdMIndex = index.svc_idx+indexName+str(site_no)+"_0"
                            bckMIndex = index.svc_idx+indexName+str(site_no)+"_1"
                        else:
                            prdMIndex = index.svc_idx+indexName+str(site_no)+"_1"
                            bckMIndex = index.svc_idx+indexName+str(site_no)+"_0"
                        #데이터 삭제 처리
                        es.deleteAllData(prdMIndex)
                        
                    #데이터 이전
                    if mTotCount>0 :
                        es.reIndex(devMIndex,prdMIndex,query_string)
                    logger.info("[runIndexDevToSvc] end [ total : "+str(mTotCount) +"]")
                    return [prdMAlias, prdMIndex, bckMIndex]
                    
                #Question 데이터를 개발 -> 운영으로 변경 한다.
                results = []
                indexNames = [index.question, index.classify, index.rule]
                for idxName in indexNames:
                    results.append(runIndexDevToSvc(idxName))
                
                #learning log update
                learning_query_string1 = {
                    "query": {
                        "query_string": {
                            "query": "siteNo:" + str(site_no)
                        }
                    }, "script" : {
                        "source" : "ctx._source.service = 'n'"
                    }
                }
                es.updateAllData('@proclassify_learning_log',learning_query_string1)
                es.refresh('@proclassify_learning_log')
                learning_query_string2 = {
                    "query": {
                        "query_string": {
                            "query": "siteNo:" + str(site_no)+" version:"+ str(version)+" state:success"
                            , "default_operator": "and"
                        }
                    }, "script" : {
                        "source" : "ctx._source.service = 'y'"
                    }
                }
                es.updateAllData('@proclassify_learning_log',learning_query_string2)
                #alias 변경
                for prdMAlias,prdMIndex,bckMIndex in results:
                    es.changeAlias(prdMAlias,prdMIndex,bckMIndex)
            else:
                status = "모델이 수행중 입니다. : "+str(site_no) +" $classify_train_state 인덱스를 확인 하세요."
                logger.info(status)
                result_code = '710'
                return {'status' : {'code' : result_code, 'message' : status} , 'version' : version}
        except Exception as e:
            result_code = '999'
            error_msg = str(e)
            logger.error(e)
            return {'status' : {'code' : result_code, 'message' : error_msg} , 'version' : version}
        
        finally:
            logger.info("[devToSvc] end]")
            if version > -1:
                end_date = datetime.now().strftime('%Y%m%d%H%M%S%f')[:-3]
                #$train_state 상태를 변경한다.
                mapData['state'] = 'n'
                mapData['status'] = '00'
                mapData['modify_date'] = end_date
                es.updateData(index.train_state, site_no, mapData)
                es.close()
        return {'status' : {'code' : result_code, 'message' : '배포성공'} , 'version' : version}
