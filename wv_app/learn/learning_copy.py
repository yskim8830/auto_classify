import os
import logging
import platform
import threading
import ctypes
import numpy as np
from datetime import datetime
from konlpy.tag import Mecab
from gensim.models import Word2Vec
from ..util import run_util
from ..util import file_util
from ..util import string_util
from ..util.const import const
from ..util.file_util import dic
from ..util.es_util import elastic_util

logger = logging.getLogger('my')
index = const()
class learn(threading.Thread):
    def __init__(self,name):
        threading.Thread.__init__(self)
        self.name = name #Thread Name
        
    def learningBERT(self, data):
        #debug = data['debug']
        
        es_urls = str(data['esUrl']).split(':')
        #검색엔진에 연결한다.
        es = elastic_util(es_urls[0], es_urls[1])
        
        site_no = data['siteNo']
        dic_path = data['dicPath']
        mecab_dic_path = '/usr/local/lib/mecab/dic'
        if data.get('mecabDicPath') != None :
            mecab_dic_path = data['mecabDicPath']
        userId = data['userId']
        
        error_msg = ""
        es.createtemplate('proclassify_template00', es.train_state_template())
        es.createindex(index.train_state,'') #$train_state 존재여부 확인 후 생성
        
        #현재 사이트가 학습 중 인지 확인한다.
        version = run_util.isRunning(es,site_no)
        modify_date = datetime.now().strftime('%Y%m%d%H%M%S%f')[:-3]
        
        intentCount = 0
        questionCount = 0
        dialogCount = 0
        
        if version > -1:
            logger.debug("[trainToDev] model not running : "+str(site_no))
            # Index 생성여부를 확인하고 해당 데이터를 만든다.
            run_util.createQuestionIndex(es,site_no)
            # $train_state 상태를 업데이트 한다.
            mapData = {}
            mapData['id'] = site_no
            mapData['version'] = version ##학습중인 상태를 나타냄. -1
            mapData['siteNo'] = site_no
            mapData['state'] = 'y'
            mapData['modify_date'] = datetime.now().strftime('%Y%m%d%H%M%S%f')[:-3]
            es.insertData(index.train_state, site_no, mapData)
            
            #ES 사전 정보 파일로 저장
            dicList = es.search_srcoll('@proclassify_dic','')
            result = string_util.save_dictionary(dic_path,dicList)
            if not result:
                return {'result' : 'fail'}
            #사전파일 가져오기
            dics = dic(dic_path)
            stopwords = dics.stopword()
            synonyms = dics.synonym()
            userdefine = dics.compound()
            
            try:
                #start_date = datetime.now().strftime('%Y%m%d%H%M%S%f')[:-3]
                new_version = version + 1 #업데이트 되는 버전 정보
                query_string = {
                    "query": {
                        "query_string": {
                            "query": "siteNo:" + str(site_no) + " AND useYn:y"
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
                category = es.search_srcoll('@prochat_category', cate_body)
                
                if userdefine.get(str(site_no)):
                    #사용자 사전 export
                    file_util.export_user_dic(mecab_dic_path,userdefine[str(site_no)])
                    #사용자 사전 적용 실행
                    if platform.system() == 'Windows':
                        file_util.run_power_shell(mecab_dic_path)
                    else:
                        file_util.run_lnx_shell(mecab_dic_path)
                
                #룰(패턴) 정보 저장
                devPattern = []
                patternMapList = es.search_srcoll('@proclassify_entity_dic',query_string)
                logger.info("[trainToDev] pattern to dev start [ site : "+str(site_no) +" /  pattern count : "+str(len(patternMapList))+" ] ")
                
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
                try:
                    es.bulk(devPattern)
                except Exception as e:
                    logger.error(e)
                logger.info("[trainToDev] pattern to dev end [ site : "+str(site_no) +" ] ")
                
                # 사전 저장 경로에 자신이 mecab-ko-dic를 저장한 위치를 적는다. (default: "/usr/local/lib/mecab/dic/mecab-ko-dic") https://lsjsj92.tistory.com/612        
                m = Mecab(dicpath=os.path.join(mecab_dic_path, 'mecab-ko-dic')) 
                
                #문서(인텐트)
                intentNameMap = dict()
                categoryMap = dict()
                intentMapList = es.search_srcoll('@proclassify_classify_data',query_string)
                intentCount = len(intentMapList)
                logger.info("[trainToDev] intent to dev start [ site : "+str(site_no) +" /  intent count : "+str(intentCount)+" ] ")
                devIntent = []
                questionList = []
                for intent in intentMapList:
                    intent = intent['_source']
                    id = str(intent['dataNo'])
                    #dialogNm = str(intent['dialogNm'])
                    intentNameMap[id] = dialogNm
                    categoryMap[id] = str(intent['categoryNo'])
                    body = {}
                    _source = {}
                    
                    body['_index'] = index.dev_idx + index.document + str(site_no)
                    body['_action'] = 'index'
                    body['_id'] = str(site_no)+'_'+str(new_version)+'_'+id
                    _source['dataNo'] = id
                    _source['title'] = intent['title']
                    _source['content'] = intent['content']
                    _source['siteNo'] = str(site_no)
                    _source['categoryNo'] = intent['categoryNo']
                    _source['desc'] = intent['desc']
                    _source['version'] = new_version
                    _source['keywords'] = str(intent['keywords']).replace(',',' ')
                    
                    sentence = string_util.filterSentence(dialogNm.lower())
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
                    orgTermCnt = len(sList)
                    
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
                    
                    #questionList.append(synonymTerm)
                    questionList.append(reList)
                    if(orgTerm != synonymTerm):
                        #questionList.append(orgTerm)
                        questionList.append(sList)
                        
                    _source['term'] = orgTerm
                    _source['term_syn'] = synonymTerm
                    _source['termNo'] = orgTermCnt
                    
                    body['_source'] = _source
                    devIntent.append(body)
                    
                try:
                    es.bulk(devIntent)
                except Exception as e:
                    logger.error("[trainToDev] template index not install : "+ e)
                logger.info("[trainToDev] intent to dev end [ site : "+str(site_no) +" ]")
                
                #질의 question
                questionMapList = es.search_srcoll('@prochat_dialog_question',query_string)
                logger.info("[trainToDev] question to dev start [ site : "+str(site_no) +" /  question count : "+str(len(questionMapList))+" ] ")
                devQuestion = []
                for question in questionMapList:
                    question = question['_source']
                    body = {}
                    _source = {}
                    
                    id = str(question['dialogNo'])
                    sentence = string_util.filterSentence(str(question['question']).lower())
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
                    orgTermCnt = len(sList)
                    
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
                    
                    dialogNm = ''
                    if id in intentNameMap:
                        dialogNm = intentNameMap[id]
                    
                    categoryNo = '0'
                    if id in categoryMap:
                        categoryNo = categoryMap[id]
                    
                    body['_index'] = index.dev_idx + index.question + str(site_no)
                    body['_action'] = 'index'
                    body['_id'] = str(site_no)+'_'+str(new_version)+'_'+str(question['questionNo'])
                    
                    _source['questionNo']  = question['questionNo']
                    _source['question']  = question['question']
                    _source['version']  = new_version
                    _source['siteNo']  = site_no
                    _source['dialogNo']  = question['dialogNo']
                    _source['dialogNm']  = dialogNm
                    _source['categoryNo']  = int(categoryNo)
                    category_info = (item for item in category if item['_id'] == str(categoryNo))
                    row_category = next(category_info, False)
                    if row_category != False:
                        _source['fullItem']  = row_category['_source']['fullItem']
                        _source['categoryNm']  = row_category['_source']['categoryNm']
                    _source['termNo']  = orgTermCnt
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
                    try:
                        es.bulk(dev_vector)
                    except Exception as e:
                        logger.error(e)
                logger.info("[word2vecTrain] end")
                #학습결과를 ES 인덱스에 넣는다.
                logger.info("[trainToDev] question to dev search vector list start [ site : "+str(site_no) +" ]")
                
                totalDevQuestion = []
                #Intent를 totalDevQuestion에 넣는다.
                if intentCount > 0:
                    for intent_row in devIntent:
                        _source = {}
                        dIntent = intent_row['_source']
                        intent_row['_index'] = index.dev_idx + index.question + str(site_no)
                        intent_row['_action'] = 'index'
                        intent_row['_id'] = str(site_no)+'_'+str(new_version)+'_D_'+str(dIntent['dialogNo'])
                        _source['question']  = dIntent['dialogNm']
                        _source['keywords']  =  dIntent['dialogNm'].replace(' ','')
                        _source['questionNo']  = -1
                        _source['dialogNo']  = dIntent['dialogNo']
                        _source['dialogNm']  = dIntent['dialogNm']
                        _source['version']  = new_version
                        _source['siteNo']  = site_no
                        _source['categoryNo']  = dIntent['categoryNo']
                        category_info = (item for item in category if item['_id'] == str(dIntent['categoryNo']))
                        row_category = next(category_info, False)
                        if row_category != False:
                            _source['fullItem']  = row_category['_source']['fullItem']
                            _source['categoryNm']  = row_category['_source']['categoryNm']
                        _source['term_syn']  = dIntent['term_syn']
                        _source['terms']  = dIntent['term_syn'].replace(' ','')
                        #morphList = m.morphs(dIntent['term_syn'])
                        if dIntent['term_syn'] != '':
                            # morphList = getWordStringList(m, dIntent['term_syn'], 'EC,JX,ETN')
                            # _source['question_vec']  = model.wv.get_mean_vector(morphList)
                            _source['question_vec']  = model.wv.get_mean_vector(str(dIntent['term_syn']).split(' '))
                        _source['term']  = dIntent['term']
                        _source['termNo']  = ''
                        intent_row['_source'] = _source
                        totalDevQuestion.append(intent_row)
                        
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
                    try:
                        es.bulk(totalDevQuestion)
                    except Exception as e:
                        logger.error(e)
                logger.info("[trainToDev] question to dev search vector list end [ site : "+str(site_no) +" /  count : "+str(len(totalDevQuestion))+" ]")
                #end word2Vec
                #모델 개수를 count 한다. (학습상관없음)
                dialogCount = es.countBySearch('@prochat_dialog_model', query_string)
                
                #학습이 완료 되었으므로 버전을 올린다.
                version = new_version
                
            except Exception as e:
                error_msg = str(e)
                logger.error(e)
            finally:
                end_date = datetime.now().strftime('%Y%m%d%H%M%S%f')[:-3]
                #$train_state 상태를 변경한다.
                mapData['version'] = version
                mapData['state'] = 'n'
                mapData['modify_date'] = end_date
                es.updateData(index.train_state, site_no, mapData)
                
                
                #learning log에 학습결과를 적재한다.
                log_data = {}
                id = str(site_no) + '_' + str(version)
                log_data['learningLogNo'] = id
                log_data['version'] = version
                log_data['siteNo'] = site_no
                log_data['service'] = 'n'
                log_data['createUser'] = userId
                log_data['modifyUser'] = userId
                runtime = (datetime.strptime(end_date, '%Y%m%d%H%M%S%f')-datetime.strptime(modify_date, '%Y%m%d%H%M%S%f')).total_seconds()
                log_data['runtime'] = runtime
                log_data['runStartDate'] = modify_date
                log_data['runEndDate'] = end_date
                log_data['createDate'] = modify_date
                log_data['modifyDate'] = modify_date
                log_data['intentCnt'] = intentCount
                log_data['dialogCnt'] = dialogCount
                log_data['questionCnt'] = questionCount
                log_data['order'] = 0
                if error_msg != '':
                    log_data['state'] = 'error'
                    log_data['message'] = error_msg
                else:
                    log_data['state'] = 'success'
                es.insertData('@prochat_learning_log', id, log_data)
                es.close()
        elif version == -1:
            status = "[trainToDev] model running : "+str(site_no) +" [check $train_state index check]"
            logger.info(status)
            return {'result' : 'fail', 'error_msg' : status}
        return {'result' : 'success', 'version' : version}
    
    def get_id(self): 
        # returns id of the respective thread 
        if hasattr(self, '_thread_id'): 
            return self._thread_id 
        for id, thread in threading._active.items(): 
            if thread is self: 
                return id
   
    def raise_exception(self): 
        thread_id = self.get_id()
        res = ctypes.pythonapi.PyThreadState_SetAsyncExc(thread_id, 
            ctypes.py_object(SystemExit)) 
        if res > 1: 
            ctypes.pythonapi.PyThreadState_SetAsyncExc(thread_id, 0) 
            print('Exception raise failure')
            
def getWordStringList(mec, sentence, stopTag):
    result = []
    morphs = mec.pos(sentence)
    for word in morphs:
        if str(stopTag).find(word[1]) == -1:
            result.append(word[0])
    return result
                
    





    