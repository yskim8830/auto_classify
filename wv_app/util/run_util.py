import logging
from datetime import datetime
from . import string_util
from .const import const
from .es_util import elastic_util

logger = logging.getLogger('my')
index = const()
def isRunning(es, site_no):
    body = {
        "query": {
            "query_string": {
                "query": "siteNo:" + str(site_no) + " "
            }
        }
    }
    isLearnig = es.search(index.train_state,body)
    version = -1
    if len(isLearnig) > 0:
        siteInfo = isLearnig[0]['_source']
        if siteInfo['state'] == 'n' or (siteInfo['state'] == 'y' and siteInfo['status'] == '01'):
            if siteInfo.get('version') != None:
                version = int(siteInfo['version'])
            else :
                version = 0
    else:
        version = 0
    return version

def createQuestionIndex(es, site_no):
    #use word2vec
    if not es.existIndex(index.dev_idx + index.classify + str(site_no)):
        es.createtemplate('proclassify_template01', es.classify_template())
        es.createindex(index.dev_idx + index.classify + str(site_no), '')
        es.createindex(index.svc_idx + index.classify + str(site_no) + "_0", '')
        es.createindex(index.svc_idx + index.classify + str(site_no) + "_1", '')
        es.createAlias(index.als_idx + index.classify + str(site_no) ,index.svc_idx + index.classify + str(site_no) + "_1")

    if not es.existIndex(index.dev_idx + index.rule + str(site_no)):
        es.createtemplate('proclassify_template02', es.rule_template())
        es.createindex(index.dev_idx + index.rule + str(site_no), '')
        es.createindex(index.svc_idx + index.rule + str(site_no) + "_0", '')
        es.createindex(index.svc_idx + index.rule + str(site_no) + "_1", '')
        es.createAlias(index.als_idx + index.rule + str(site_no) ,index.svc_idx + index.rule + str(site_no) + "_1")
        
    if not es.existIndex(index.dev_idx + index.entity + str(site_no)):
        es.createtemplate('proclassify_template03', es.entity_template())
        es.createindex(index.dev_idx + index.entity + str(site_no), '')
        es.createindex(index.svc_idx + index.entity + str(site_no) + "_0", '')
        es.createindex(index.svc_idx + index.entity + str(site_no) + "_1", '')
        es.createAlias(index.als_idx + index.entity + str(site_no) ,index.svc_idx + index.entity + str(site_no) + "_1")
    """  
    if not es.existIndex(index.dev_idx + index.document + str(site_no)):
        es.createtemplate('proclassify_template03', es.doc_template())
        es.createindex(index.dev_idx + index.document + str(site_no), '')
        es.createindex(index.svc_idx + index.document + str(site_no) + "_0", '')
        es.createindex(index.svc_idx + index.document + str(site_no) + "_1", '')
        es.createAlias(index.als_idx + index.document + str(site_no) ,index.svc_idx + index.document + str(site_no) + "_1")
    """  
    try:
        if not es.existIndex(index.dev_idx + index.question + str(site_no)):
            es.createindex(index.dev_idx + index.question + str(site_no), es.question_index_template())
            es.createindex(index.svc_idx + index.question + str(site_no) + "_0", es.question_index_template())
            es.createindex(index.svc_idx + index.question+ str(site_no) + "_1", es.question_index_template())
            es.createAlias(index.als_idx + index.question + str(site_no) ,index.svc_idx + index.question + str(site_no) + "_1")
    except Exception as e:
        logger.error("elastiknn plugin not installed.", e)
        
def get_worker_site(data):
    try:
        es_urls = str(data['esUrl']).split(':')
        site_no = data['siteNo']
        #검색엔진에 연결한다.
        es = elastic_util(es_urls[0], es_urls[1])
        body = {
            "query": {
                "query_string": {
                    "query": "siteNo:" + str(site_no) + " "
                }
            }
        }
        isLearnig = es.search(index.train_state,body)
        es.close()
        if len(isLearnig) > 0:
            siteInfo = isLearnig[0]['_source']
        else :
            siteInfo = ''
        es.close()
    except Exception as e:
        error_msg = str(e)
        logger.error(e)
        return {'status' : {'code' : '999', 'message' : error_msg }, 'siteInfo' : ''} 
    return {'status' : {'code' : '200', 'message' : '성공'}, 'siteInfo' : siteInfo}

def save_dict(data):
    es_urls = str(data['esUrl']).split(':')
    #검색엔진에 연결한다.
    es = elastic_util(es_urls[0], es_urls[1])
    #ES 사전 정보 파일로 저장
    dicList = es.search_srcoll('@proclassify_dic','')
    dic_path = data['dicPath']
    result = string_util.save_dictionary(dic_path,dicList)
    es.close()
    if not result:
        return {'result' : 'fail', 'msg' : 'dictionary Error.'}
    return {'result' : 'success'}

def recoverySite(data):
    #프로세서가 비정상 종료일시 상태값을 복구한다.
    
    #프로세서를 완전히 종료한다.
    
    
    #프로세서상태를 복구한다.
    try:
        es_urls = str(data['esUrl']).split(':')
        #검색엔진에 연결한다.
        es = elastic_util(es_urls[0], es_urls[1])
        
        mapData = {}
        mapData['id'] = data['siteNo']
        mapData['siteNo'] = data['siteNo']
        mapData['state'] = 'n'
        mapData['worker_id'] = ''
        mapData['status'] = '00'
        mapData['modify_date'] = datetime.now().strftime('%Y%m%d%H%M%S%f')[:-3]
        es.updateData(index.train_state, data['siteNo'], mapData)
    except Exception as e:
        error_msg = str(e)
        logger.error(e)
        return {'code' : '610', 'message' : error_msg}
    finally :
        es.close()
    return {'code' : '200', 'message' : '성공'}

def update_train_state(es, data,type):
    try:
        data['modify_date'] = datetime.now().strftime('%Y%m%d%H%M%S%f')[:-3]
        if type == 'in':
            es.insertData(index.train_state, data['siteNo'], data)
        elif type == 'up':
            es.updateData(index.train_state, data['siteNo'], data)
        else :
            return {'code' : '499', 'message' : '엘라스틱 데이터를 insert 하는 도중 문제가 발생했습니다.'}
    except Exception as e:
        error_msg = str(e)
        logger.error(e)
        return {'code' : '499', 'message' : error_msg}
    finally :
        es.close()
    return ''

def init_train_state(data):
    try:
        es_urls = str(data['esUrl']).split(':')
        #검색엔진에 연결한다.
        es = elastic_util(es_urls[0], es_urls[1])
        es.createtemplate('proclassify_template00', es.train_state_template())
        ret = es.createindex(index.train_state,'') #$train_state 존재여부 확인 후 생성
        data.pop('esUrl')
        version = isRunning(es, str(data['siteNo']))
        if version == 0:
            update_train_state(es, data,'in')
        else :
            update_train_state(es, data,'up')
    except Exception as e:
        error_msg = str(e)
        logger.error(e)
        return {'code' : '499', 'message' : error_msg}
    finally :
        es.close()
