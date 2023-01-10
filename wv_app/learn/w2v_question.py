import logging
import os
from datetime import datetime
from konlpy.tag import Mecab
import numpy as np
from gensim.models import Word2Vec
from ..util.const import const
from ..util.es_util import elastic_util
from ..util import string_util

logger = logging.getLogger('my')
index = const()

#1. elasticsearch model index search
class question():
    def __init__(self,data):
        try:
            self.site_id = data['site']
            self.searchip = data['esUrl']
            self.version = str(data['version'])
            self.site_no = -1
            self.size = 5
            if data.get('size') != None :
                self.size = data['size']

            self.mecab = Mecab()
            self.threshold = 0.00
            if data.get('threshold') != None :
                self.threshold = data['threshold']
        except Exception as e:
            return str(e)
            
    def word2vec_question(self, question, question_title):
        total_results = {}
        result_code = ""
        result_message = ""
        analysisResult_tagSentence = ""
        analysisResult_resultType = "not_matched"
        analysisResult_matchedType = ""
        analysisResult_ruleResult = []
        analysisResult_classifyResult = []
        runtime = 0
        starttime = datetime.now().strftime('%Y%m%d%H%M%S%f')[:-3]
        try:
            es_urls = self.searchip.split(':')
            #검색엔진에 연결한다.
            es = elastic_util(es_urls[0], es_urls[1])
            query_string = {
                "query": {
                    "match": {
                        "site": self.site_id
                    }
                }
            }
            
            siteList = es.search('@proclassify_site',query_string)
        
            if len(siteList) == 0:
                result_code = "810"
                result_message = "사이트를 찾을 수 없습니다."
                raise Exception
            
            self.site_no = es.search('@proclassify_site',query_string)[0]['_source']['siteNo']
            question_idx = index.als_idx + index.question + str(self.site_no)
            #intent_idx = index.als_idx + index.document + str(self.site_no)
            model_idx = index.als_idx + index.classify + str(self.site_no)
            #개발 일 경우 version >-1 이상이고 version -1 일 경우 운영
            if int(self.version) > -1:
                question_idx = index.dev_idx + index.question + str(self.site_no)
                #intent_idx = index.dev_idx + index.document + str(self.site_no)
                model_idx = index.dev_idx + index.classify + str(self.site_no)
            
            #1. 태깅 - start
            analysisResult_tagSentence = question
            #2. 룰 매칭 - start
            
            #3. 자동분류 - start
            r_question = getWordStringList(self.mecab, string_util.filterSentence(question.lower()), 'EC,JX,ETN') #질의어의 벡터 평균을 뽑는다.
            """
            #3-1.w2v 모델파일 search
            #아래 vec_dic_question 함수
            #3-2.aggregations 방식
            aggr_body = es.question_aggr_query_to_vector(self.version, ' '.join(r_question))
            query_float = es.search_avg(model_idx,aggr_body)
            #knn 검색 결과를 리턴한다.
            result = {}
            if query_float[0] != None:
                knn_body = es.question_vector_query(self.version,query_float)
                result = es.search(question_idx,knn_body)
            else:
                result = {'error' : 'No matching value found.'}
            return result
            """
            #3-3.mean_vector 방식
            weights = np.ones(len(r_question))
            """
            #제목을 추가할경우 가중치를 주고 append 한다.
            if question_title != None:
                r_question_title = getWordStringList(self.mecab, string_util.filterSentence(question_title.lower()), 'EC,JX,ETN')
                r_question.extend(r_question_title)
                title_weight = np.ones(len(r_question_title))*1.1
                weights = np.append(weights, title_weight)
            """
            mean = np.zeros(100, np.float32)
            total_weight = 0
            for idx, key in enumerate(r_question):
                query_body = es.question_query_to_vector(self.version,key)
                result = es.search_vector(model_idx,query_body)
                if result != -1:
                    result_float = np.array(result)
                    norms = np.linalg.norm(result_float, axis=0, ord=2) #L2 선형함수 값 구하기
                    query_float = result_float / norms
                    mean += weights[idx] * query_float
                    total_weight += abs(weights[idx])

            if(total_weight > 0):
                mean = mean / total_weight
            
            if mean[0] != 0.0:
                #knn 검색 결과를 리턴한다.
                knn_body = es.question_vector_query(version=self.version,vector=mean,size=self.size)
                knn_results = es.search(question_idx,knn_body)
                for idx, rst in enumerate(knn_results):
                    reliability = 0.00
                    try:
                        reliability = float("{:.2f}".format(rst['_score']*100))
                        if reliability > float(self.threshold):
                            analysisResult_classifyResult.append({"categoryNo" : rst['_source']['categoryNo'], "categoryNm" : rst['_source']['categoryNm']
                                , "fullItem" : rst['_source']['fullItem'], "score" : rst['_score'], "reliability" : str(reliability)+'%'})
                    except Exception as e:
                        result_code = "820"
                        result_message = "임계치(threshold) 값을 올바르게 입력해주세요."
                        raise Exception

                analysisResult_resultType = "matched"
                analysisResult_matchedType = "classify"
            result_code = "200"
            result_message = "성공"
        except Exception as e:
            logger.error(e)
            if result_code == '':
                result_code = "999"
                result_message = "Error. " + str(e)
            analysisResult_resultType = "error"
        finally:
            endtime = datetime.now().strftime('%Y%m%d%H%M%S%f')[:-3]
            runtime = (datetime.strptime(endtime, '%Y%m%d%H%M%S%f')-datetime.strptime(starttime, '%Y%m%d%H%M%S%f')).total_seconds()
            total_results = {
                            "status" : {
                                "code" : result_code,
                                "message" : result_message
                            },
                            "siteNo" : self.site_no,
                            "analysisResult" : {
                                "tagSentence" : analysisResult_tagSentence,
                                "resultType" : analysisResult_resultType,
                                "matchedType" : analysisResult_matchedType,
                                "ruleResult" : analysisResult_ruleResult,
                                "classifyResult" : analysisResult_classifyResult,
                            },
                            "runtime" : runtime
                         }
            #classify_history에 분류결과를 적재한다. (서비스 데이터일 경우에만)
            if str(self.version) == '-1':
                log_data = {}
                id = str(self.site_no) + '_' + str(endtime)
                log_data['id'] = id
                log_data['siteNo'] = self.site_no
                log_data['orgSentence'] = question
                log_data['taggSentence'] = analysisResult_tagSentence
                log_data['resultType'] = analysisResult_resultType
                log_data['matchedType'] = analysisResult_matchedType
                log_data['ruleResult'] = str(analysisResult_ruleResult)
                log_data['classifyResult'] = str(analysisResult_classifyResult)
                log_data['createDate'] = endtime
                if result_code != '200' :
                    log_data['failedMessage'] = result_message
                if 'es' in vars() :
                    es.insertData('@proclassify_classify_history', id, log_data)
            if 'es' in vars() :
                es.close()
        return total_results

#2. word2vec model file search (임시)
class vec_dic_question():
    def __init__(self,site_no,searchip,version,dic_path,mecab_dic_path,size):
        self.site_no = site_no
        self.searchip = searchip
        self.version = version
        self.dic_path = dic_path
        self.mecab_dic_path = mecab_dic_path
        if size != None:
            self.size = size
        else:
            self.size = 5
        self.model = Word2Vec.load(fname=os.path.join(self.dic_path,'word2vec_'+str(self.site_no)))
        if mecab_dic_path != None:
            self.mecab = Mecab(dicpath=self.mecab_dic_path+'/mecab-ko-dic') # 사전 저장 경로에 자신이 mecab-ko-dic를 저장한 위치를 적는다. (default: "/usr/local/lib/mecab/dic/mecab-ko-dic") https://lsjsj92.tistory.com/612
        else:
            self.mecab = Mecab()
    def word2vec_question(self,question):
        es_urls = self.searchip.split(':')
        #검색엔진에 연결한다.
        es = elastic_util(es_urls[0], es_urls[1])
        question_idx = index.als_idx + index.question + str(self.site_no)
        #intent_idx = index.als_idx + index.document + str(self.site_no)
        #model_idx = index.als_idx + index.classify + str(self.site_no)
        #개발 일 경우 version >-1 이상이고 version -1 일 경우 운영
        if int(self.version) > -1:
            question_idx = index.dev_idx + index.question + str(self.site_no)
            #intent_idx = index.dev_idx + index.document + str(self.site_no)
            #model_idx = index.dev_idx + index.classify + str(self.site_no)
        r_question = getWordStringList(self.mecab, question, 'EC,JX,ETN')
        
        query_float = self.model.wv.get_mean_vector(r_question)
        #knn 검색 결과를 리턴한다.
        result = {}
        if query_float[0] != 0.0:
            knn_body = es.question_vector_query(version=self.version,vector=query_float,size=self.size)
            result = es.search(question_idx,knn_body)
        else:
            result = {'error' : 'No matching value found.'}
        return result
    
def getWordStringList(mec, sentence, stopTag):
    result = []
    morphs = mec.pos(sentence)
    for word in morphs:
        if str(stopTag).find(word[1]) == -1:
            result.append(word[0])
    return result

#not use
def result_category(total_results,size):
    results = []
    results_count = []
    for idx, rst in enumerate(total_results):
        # results.append(rst)
        if(rst['_source']['categoryNo'] != 0):
            if(results_count.count(rst['_source']['categoryNo']) == 0):
                results_count.append(rst['_source']['categoryNo'])
                results.append({"categoryNo" : rst['_source']['categoryNo'], "categoryNm" : rst['_source']['categoryNm']
                                , "fullItem" : rst['_source']['fullItem'], "score" : rst['_score'], "reliability" : "{:.2f}%".format(rst['_score'])})
        if len(results_count) == size:
            break
    return results