import os
import logging
import platform
from datetime import datetime
from konlpy.tag import Mecab
from gensim.models import Word2Vec
from ..util import run_util
from ..util import file_util
from ..util import string_util
from ..util.const import const
from ..util.file_util import dic
from ..util.es_util import elastic_util
import time

logger = logging.getLogger('my')
index = const()
class learn():
    def __init__(self,t_name):
        self.name = t_name
        
    def run(self, data):
        try:
            result_code = '200'
            error_msg = ''
            modify_date = datetime.now().strftime('%Y%m%d%H%M%S%f')[:-3]
            es_urls = str(data['esUrl']).split(':')
            #검색엔진에 연결한다.
            es = elastic_util(es_urls[0], es_urls[1])
            
            site_no = data['siteNo']
            dic_path = index.proclassify_dic_path
            if data.get('dicPath') != None :
                dic_path = data['dicPath']
            mecab_dic_path = '/usr/local/lib/mecab/dic'
            if data.get('mecabDicPath') != None :
                mecab_dic_path = data['mecabDicPath']
            userId = data['userId']
            
            # es.createtemplate('proclassify_template00', es.train_state_template())
            # es.createindex(index.train_state,'') #$train_state 존재여부 확인 후 생성
            #현재 사이트가 학습 중 인지 확인한다. (worker가 템플릿 존재 여부를 확인 하고 sleep)
            wait = 5
            logger.info("[trainToDev] Initial Wait "+ str(wait) +" Seconds..")
            time.sleep(wait)
            version = run_util.isRunning(es,site_no)
            ruleCount = 0
            dataCount = 0
            if version > -1:
                logger.info("[trainToDev] model not running : "+str(site_no))
                # Index 생성여부를 확인하고 해당 데이터를 만든다.
                run_util.createQuestionIndex(es,site_no)
                
                # $train_state 상태를 업데이트 한다.
                mapData = {}
                mapData['version'] = version ##학습중인 상태를 나타냄. -1
                mapData['siteNo'] = site_no
                mapData['state'] = 'y'
                mapData['status'] = '02' #전처리
                es.updateData(index.train_state, site_no, mapData)
                
                #ES 사전 정보 파일로 저장
                dicList = es.search_srcoll('@proclassify_dic','')
                result = string_util.save_dictionary(dic_path,dicList)
                if not result:
                    result_code = '320'
                    error_msg = '사전 정보가 올바르게 저장되지 않았습니다.'
                    raise Exception(error_msg)
                #사전파일 가져오기
                dics = dic(dic_path)
                stopwords = dics.stopword()
                synonyms = dics.synonym()
                userdefine = dics.compound()
                new_version = version + 1 #업데이트 되는 버전 정보
                query_string = {
                    "query": {
                        "query_string": {
                            "query": "siteNo:" + str(site_no)
                        }
                    }
                }
                #카테고리정보 로드
                cate_body = {
                            "query": {
                                "query_string": {
                                    "query": "siteNo:" + str(site_no) + " AND useYn:y"
                                }
                            }
                        }
                try:
                    category = es.search_srcoll('@proclassify_classify_category', cate_body)
                    if len(category) == 0 :
                        raise Exception(error_msg)
                except:
                    if len(category) == 0 :
                        result_code = '299'
                        error_msg = '학습 할 카테고리 정보가 없습니다.'
                    else:
                        result_code = '330'
                        error_msg = '카테고리 정보 로드 중 오류 발생'
                    raise Exception(error_msg)
                
                if userdefine.get(str(site_no)):
                    try:
                        #사용자 사전 export
                        file_util.export_user_dic(mecab_dic_path,userdefine[str(site_no)])
                        #사용자 사전 적용 실행
                        if platform.system() == 'Windows':
                            file_util.run_power_shell(mecab_dic_path)
                        else:
                            file_util.run_lnx_shell(mecab_dic_path)
                    except:
                        result_code = '340'
                        error_msg = 'Mecab 로드 중 오류 발생'
                        raise Exception(error_msg)
                # 사전 저장 경로에 자신이 mecab-ko-dic를 저장한 위치를 적는다. (default: "/usr/local/lib/mecab/dic/mecab-ko-dic") https://lsjsj92.tistory.com/612        
                m = Mecab(dicpath=os.path.join(mecab_dic_path, 'mecab-ko-dic')) 
                
                #룰(패턴) 정보 저장
                mapData = {} # $train_state 상태를 업데이트 한다.
                mapData['status'] = '03' #학습데이터 준비 (사전 정제작업 등)
                es.updateData(index.train_state, site_no, mapData)
                
                
                devPattern = []
                patternMapList = es.search_srcoll('@proclassify_entity_dic',query_string)
                ruleCount = len(patternMapList)
                logger.info("[trainToDev] pattern to dev start [ site : "+str(site_no) +" /  pattern count : "+str(ruleCount)+" ] ")
                
                for pattern in patternMapList:
                    pattern = pattern['_source']
                    key = str(pattern['entityNo'])
                    body = {}
                    _source = {}
                    body['_index'] = index.dev_idx + index.rule + str(site_no)
                    body['_action'] = 'index'
                    body['_id'] = str(site_no)+'_'+str(new_version)+'_'+key
                    _source['id'] = key
                    _source['rule'] = key
                    _source['siteNo'] = str(site_no)
                    _source['version'] = new_version
                    
                    #불용어 제거 stopwords
                    sList = []
                    for entry in str(pattern['pattern']).split(','):
                        if stopwords.get(str(site_no)):
                            if entry not in stopwords[str(site_no)]:
                                sList.append(entry)
                        else:
                            sList.append(entry)
                            
                    #동의어 처리
                    reList = []
                    for entry in sList:
                        if synonyms.get(str(site_no)):
                            if entry in synonyms[str(site_no)]: 
                                reList.append(synonyms[str(site_no)][entry])
                            else:
                                reList.append(entry)
                        else:
                            reList.append(entry)
                    _source['term'] = ' '.join(reList)
                    body['_source'] = _source
                    devPattern.append(body)
                    es.bulk(devPattern)
                logger.info("[trainToDev] pattern to dev end [ site : "+str(site_no) +" ] ")
                
                #문서
                questionList = []
                dataMapList = es.search_srcoll('@proclassify_classify_data',query_string)
                dataCount = len(dataMapList)
                logger.info("[trainToDev] data to dev start [ site : "+str(site_no) +" /  data count : "+str(dataCount)+" ] ")
                devQuestion = []
                for dataMap in dataMapList:
                    dataMap = dataMap['_source']
                    body = {}
                    _source = {}
                    
                    body['_index'] = index.dev_idx + index.question + str(site_no)
                    body['_action'] = 'index'
                    body['_id'] = str(site_no)+'_'+str(new_version)+'_'+str(dataMap['dataNo'])
                    # full_contents = str(dataMap['title']).lower() + str(dataMap['content']).lower()
                    full_contents = str(dataMap['document']).lower()
                    sentence = string_util.filterSentence(full_contents)
                    morphList = getWordStringList(m, sentence, 'EC,JX,ETN')
                    #불용어 제거 stopwords
                    sList = []
                    for morph in morphList:
                        if stopwords.get(str(site_no)):
                            if morph not in stopwords[str(site_no)]:
                                sList.append(morph)
                        else:
                            sList.append(morph)    
                    orgTerm = ' '.join(sList)
                    
                    #동의어 처리
                    reList = []
                    for morph in morphList:
                        if synonyms.get(str(site_no)):
                            if morph in synonyms[str(site_no)]: 
                                reList.append(synonyms[str(site_no)][morph])
                            else:
                                reList.append(morph)
                        else:
                            reList.append(morph)
                    synonymTerm = ' '.join(reList)
                    
                    _source['dataNo']  = dataMap['dataNo']
                    _source['version']  = new_version
                    _source['siteNo']  = site_no
                    # _source['title']  = dataMap['title']
                    # _source['content']  = dataMap['content']
                    _source['document']  = dataMap['document']
                    _source['categoryNo']  = dataMap['categoryNo']
                    category_info = (item for item in category if item['_id'] == str(dataMap['categoryNo']))
                    row_category = next(category_info, False)
                    if row_category != False:
                        _source['fullItem']  = row_category['_source']['fullItem']
                        _source['categoryNm']  = row_category['_source']['categoryNm']
                    _source['term']  = orgTerm
                    _source['term_syn']  = synonymTerm
                    _source['keywords']  = string_util.specialReplace(sentence).replace(' ','')
                    
                    #questionList.append(synonymTerm)
                    questionList.append(reList)
                    if(orgTerm != synonymTerm):
                        #questionList.append(orgTerm)
                        questionList.append(sList)
                        _source['terms']  = orgTerm.replace(' ','') + ' ' + synonymTerm.replace(' ','')
                    else:
                        _source['terms']  = orgTerm.replace(' ','')
                    
                    body['_source'] = _source
                    devQuestion.append(body)

                #start word2Vec
                mapData = {} # $train_state 상태를 업데이트 한다.
                mapData['status'] = '04' #word2vec 시작
                es.updateData(index.train_state, site_no, mapData)
                
                logger.info("[word2vecTrain] start [ siteNo :"+str(site_no)+" / size : "+str(len(questionList))+"]")
                model = Word2Vec(sentences=questionList, vector_size=100, window=2, min_count=1, workers=10, sg=0)
                model_name = 'word2vec_'+str(site_no)
                model.save(os.path.join(dic_path, model_name))
                
                dev_vector = []
                
                for element in range(0, len(model.wv)):
                    body = {}
                    _source = {}
                    if str(model.wv.index_to_key[element]).strip() != '':
                        body['_index'] = index.dev_idx + index.classify + str(site_no)
                        body['_action'] = 'index'
                        body['_id'] = str(site_no)+'_'+str(new_version)+'_'+model.wv.index_to_key[element]
                        _source['siteNo'] = site_no
                        _source['version'] = new_version
                        _source['term'] = model.wv.index_to_key[element]
                        #mean = np.array(model.wv[element])
                        mean = model.wv.get_vector(element)
                        for el in range(0, len(mean)):
                            _source['dm_'+str(el)] = mean[el]
                        body['_source'] = _source
                        dev_vector.append(body)
                if len(dev_vector) > 0:
                    es.bulk(dev_vector)
                logger.info("[word2vecTrain] end")
                #학습결과를 ES 인덱스에 넣는다.
                logger.info("[trainToDev] question to dev search vector list start [ site : "+str(site_no) +" ]")
                
                totalDevQuestion = []
                # vector를 devQuestion에 넣는다.    
                if len(devQuestion) > 0:
                    for question_row in devQuestion:
                        source = question_row['_source']
                        terms = source['term_syn']
                        if terms != '':
                            #morphList = m.morphs(terms)
                            # morphList = getWordStringList(m, terms, 'EC,JX,ETN')
                            # source['question_vec'] = model.wv.get_mean_vector(morphList)
                            source['question_vec'] = model.wv.get_mean_vector(str(terms).split(' '))
                            
                        question_row['_source'] = source
                        totalDevQuestion.append(question_row)
                logger.info("complete set vector")
                    
                if len(totalDevQuestion) >0:   
                    es.bulk(totalDevQuestion)
                logger.info("[trainToDev] question to dev search vector list end [ site : "+str(site_no) +" /  count : "+str(len(totalDevQuestion))+" ]")
                #end word2Vec
                
                #학습이 완료 되었으므로 버전을 올린다.
                version = new_version
            elif version == -1:
                status = "[trainToDev] model running : "+str(site_no) +" [check $train_state index check]"
                logger.info(status)
                return {'result' : 'fail', 'error_msg' : status}
        except Exception as e:
            version = -1
            result_code = '999'
            error_msg = str(e)
            logger.error(e)
            return {'status' : {'code' : result_code, 'message' : error_msg} , 'version' : version}
        finally:
            end_date = datetime.now().strftime('%Y%m%d%H%M%S%f')[:-3]
            #$train_state 상태를 변경한다.
            mapData['version'] = version
            mapData['state'] = 'n'
            # mapData['worker_id'] = ''
            if error_msg != '':
                mapData['status'] = '00' #준비상태로 되돌림
            else : 
                mapData['status'] = '05' #학습완료
            mapData['modify_date'] = end_date
            es.updateData(index.train_state, site_no, mapData)
            
            #learning log에 학습결과를 적재한다.
            log_data = {}
            id = str(site_no) + '_' + str(version) + '_' + str(end_date)
            log_data['learningLogNo'] = id
            log_data['siteNo'] = site_no
            log_data['service'] = 'n'
            log_data['version'] = version
            log_data['createUser'] = userId
            log_data['modifyUser'] = userId
            runtime = (datetime.strptime(end_date, '%Y%m%d%H%M%S%f')-datetime.strptime(modify_date, '%Y%m%d%H%M%S%f')).total_seconds()
            log_data['runtime'] = runtime
            log_data['runStartDate'] = modify_date
            log_data['runEndDate'] = end_date
            log_data['createDate'] = modify_date
            log_data['modifyDate'] = modify_date
            log_data['ruleCnt'] = ruleCount
            log_data['dataCnt'] = dataCount
            log_data['order'] = 0
            log_data['result_code'] = result_code
            if error_msg != '':
                log_data['state'] = 'error'
                log_data['message'] = error_msg
            else:
                log_data['state'] = 'success'
                log_data['message'] = 'success'
                error_msg = '학습 성공'
                logger.info("[trainToDev] model running success.")
                
            es.insertData('@proclassify_learning_log', id, log_data)
            es.close()
        return {'status' : {'code' : result_code, 'message' : error_msg} , 'version' : version}

def getWordStringList(mec, sentence, stopTag):
    result = []
    morphs = mec.pos(sentence)
    for word in morphs:
        if str(stopTag).find(word[1]) == -1:
            result.append(word[0])
    return result
                
    





    