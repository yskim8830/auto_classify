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
    def __init__(self,site_no,searchip,version,mecab_dic_path,size):
        self.site_no = site_no
        self.searchip = searchip
        self.version = version
        self.mecab_dic_path = mecab_dic_path
        if size != None:
            self.size = size
        else:
            self.size = 5
        if mecab_dic_path != None:
            self.mecab = Mecab(dicpath=self.mecab_dic_path+'/mecab-ko-dic') # 사전 저장 경로에 자신이 mecab-ko-dic를 저장한 위치를 적는다. (default: "/usr/local/lib/mecab/dic/mecab-ko-dic") https://lsjsj92.tistory.com/612
        else:
            self.mecab = Mecab()
    def word2vec_question(self, question, question_title):
        starttime = datetime.now().strftime('%Y%m%d%H%M%S%f')[:-3]
        es_urls = self.searchip.split(':')
        #검색엔진에 연결한다.
        es = elastic_util(es_urls[0], es_urls[1])
        
        question_idx = index.als_idx + index.question + str(self.site_no)
        #intent_idx = index.als_idx + index.document + str(self.site_no)
        model_idx = index.als_idx + index.classify + str(self.site_no)
        #개발 일 경우 version >-1 이상이고 version -1 일 경우 운영
        if int(self.version) > -1:
            question_idx = index.dev_idx + index.question + str(self.site_no)
            #intent_idx = index.dev_idx + index.document + str(self.site_no)
            model_idx = index.dev_idx + index.classify + str(self.site_no)
        
        #질의어의 벡터 평균을 뽑는다.
            
        r_question = getWordStringList(self.mecab, string_util.filterSentence(question.lower()), 'EC,JX,ETN')
        
        """
        #1.aggregations 방식
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
        #2.mean_vector 방식
        weights = np.ones(len(r_question))
        #제목을 추가할경우 가중치를 주고 append 한다.
        if question_title != None:
            r_question_title = getWordStringList(self.mecab, string_util.filterSentence(question_title.lower()), 'EC,JX,ETN')
            r_question.extend(r_question_title)
            title_weight = np.ones(len(r_question_title))*1.1
            weights = np.append(weights, title_weight)

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
        #knn 검색 결과를 리턴한다.
        total_results = {}
        if mean[0] != 0.0:
            knn_body = es.question_vector_query(version=self.version,vector=mean,size=self.size)
            total_results = es.search(question_idx,knn_body)
            results = []
            for idx, rst in enumerate(total_results):
                # results.append(rst)
                results.append({"categoryNo" : rst['_source']['categoryNo'], "categoryNm" : rst['_source']['categoryNm']
                        , "fullItem" : rst['_source']['fullItem'], "score" : rst['_score'], "reliability" : "{:.2f}%".format(rst['_score'])})
            #results = result_category(total_results,self.size)
            
            endtime = datetime.now().strftime('%Y%m%d%H%M%S%f')[:-3]
            runtime = (datetime.strptime(endtime, '%Y%m%d%H%M%S%f')-datetime.strptime(starttime, '%Y%m%d%H%M%S%f')).total_seconds()
            total_results = {"result" : results, "runtime" : runtime}
        else:
            total_results = {'error' : 'No matching value found.'}
        return total_results

#2. word2vec model file search
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