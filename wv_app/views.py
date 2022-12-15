import json
import time
from django.shortcuts import render
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from .util import run_util
from .learn import learning
from .learn import distribute
from .learn import w2v_question

from django.http import Http404


class training_start(APIView):
    def post(self , request):
        data = json.loads(request.body) #파라미터 로드
        siteNo = str(data['siteNo'])
        result_dic = {} #결과 set
        #질의를 embedding 하여 저장
        run = learning.learn('siteNo_'+siteNo)
        result_dic = run.learningBERT(data)
        return Response(result_dic)
    
class training_stop(APIView):
    def post(self , request):
        data = json.loads(request.body) #파라미터 로드
        siteNo = str(data['siteNo'])
        result_dic = {} #결과 set
        run = learning.learn('siteNo_'+siteNo)
        #print('stop main')
        result = run.raise_exception()
        if result == True :
            run.join()
            result_dic = {'result' :  'success'}
        else :
            result_dic = {'result' : 'fail', 'msg' : 'learning is not working'}
            
        return Response(result_dic)
    
class training_status(APIView):
    def post(self , request):
        data = json.loads(request.body) #파라미터 로드
        result_dic = {} #결과 set
        result_dic = run_util.status(data)
        return Response(result_dic)

class training_clear(APIView):
    def post(self , request):
        data = json.loads(request.body) #파라미터 로드
        result_dic = {} #결과 set
        result_dic = run_util.recoverySite(data)
        return Response(result_dic)

class distribute_service(APIView):
    def post(self , request):
        data = json.loads(request.body) #파라미터 로드
        siteNo = str(data['siteNo'])
        result_dic = {} #결과 set
        # dev 학습 데이터를 서비스로 전송
        send = distribute.dist('siteNo_'+siteNo)
        result_dic = send.distributeBERT(data)
        return Response(result_dic)

class distribute_dictionary(APIView):
    def post(self , request):
        data = json.loads(request.body) #파라미터 로드
        result_dic = {} #결과 set
        result_dic = run_util.save_dict(data)
            
        return Response(result_dic)
   
#엘라스틱서치
class classify(APIView):
    def get(self , request):
        site_no = request.query_params.get('siteNo')
        question = request.query_params.get('query')
        searchip = request.query_params.get('esURl')
        version = request.query_params.get('version')
        mecab_dic_path = request.query_params.get('mecabDicPath')
        size = request.query_params.get('size')
        w2v_query = w2v_question.question(site_no, searchip, version, mecab_dic_path, size)
        result_answer = w2v_query.word2vec_question(question)
        
        return Response(result_answer)
    
    def post(self , request):
        data = json.loads(request.body) #파라미터 로드
        site_no = str(data['siteNo'])
        question = str(data['query'])
        searchip = str(data['esURl'])
        version = str(data['version'])
        mecab_dic_path = str(data['mecabDicPath'])
        size = str(data['size'])
        w2v_query = w2v_question.question(site_no, searchip, version, mecab_dic_path, size)
        result_answer = w2v_query.word2vec_question(question)
        return Response(result_answer)
#모델에 직접 질의
class classify2(APIView):
    def get(self , request):
        site_no = request.query_params.get('siteNo')
        question = request.query_params.get('query')
        searchip = request.query_params.get('esURl')
        version = request.query_params.get('version')
        dic_path = request.query_params.get('dicPath')
        mecab_dic_path = request.query_params.get('mecabDicPath')
        size = request.query_params.get('size')
        w2v_query = w2v_question.vec_dic_question(site_no, searchip, version, dic_path, mecab_dic_path, size)
        result_answer = w2v_query.word2vec_question(question)
        
        
        return Response(result_answer)
    
    def post(self , request):
        data = json.loads(request.body) #파라미터 로드
        site_no = str(data['siteNo'])
        question = str(data['query'])
        searchip = str(data['esURl'])
        version = str(data['version'])
        dic_path = str(data['dicPath'])
        mecab_dic_path = str(data['mecabDicPath'])
        size = str(data['size'])
        w2v_query = w2v_question.vec_dic_question(site_no, searchip, version, dic_path, mecab_dic_path, size)
        result_answer = w2v_query.word2vec_question(question)
        return Response(result_answer)