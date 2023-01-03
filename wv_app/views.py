import json
import time
from django.shortcuts import render
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from .util import run_util
#from .learn import learning
from .learn import distribute
from .learn import w2v_question

from django.http import Http404

from .tasks import *

c_type='application/json; charset=utf-8'
class training_start(APIView):
    def post(self , request):
        data = json.loads(request.body) #파라미터 로드
        #질의를 embedding 하여 저장
        results = start_learning.delay(data) #celery start
        result = {}
        try: 
            result = {'code' : '200', 'message' : '학습 시작', 'worker_id' : results.id}
            data['worker_id'] = result['worker_id']
            data['state'] = 'n'
            data['status'] = '01' #started
            run_util.init_train_state(data)
        except Exception as e:
            result = {'code' : '499', 'message' : e, 'worker_id' : ''}
        return Response({'status' : result},content_type=c_type)
    
class training_stop(APIView):
    def post(self , request):
        data = json.loads(request.body) #파라미터 로드
        result = celery_stop(str(data['worker_id']))
        
        return Response({'status' : result},content_type=c_type)
    
class training_status(APIView):
    def post(self , request):
        data = json.loads(request.body) #파라미터 로드
        result = {}
        worker = run_util.get_worker_site(data)
        if worker != '' :
            worker_status = 'PENDING'
            if str(worker['worker_id']) != '' :
                worker_status = celery_state(str(worker['worker_id']))
            worker_site = {'running' : worker['state'], 'step' : worker['status'], 'version' : worker['version']}
            result = {'code' : '200', 'message' : '조회 성공'}
            return Response({'status' : result, 'site_status' : worker_site, 'worker_status' : worker_status},content_type=c_type)

        else :
            result = {'code' : '399', 'message' : '사이트 정보가 없습니다.'}    
        return Response({'status' : result},content_type=c_type)

class training_clear(APIView):
    def post(self , request):
        data = json.loads(request.body) #파라미터 로드
        result = run_util.recoverySite(data)
        return Response({'status' : result},content_type=c_type)

class distribute_service(APIView):
    def post(self , request):
        data = json.loads(request.body) #파라미터 로드
        siteNo = str(data['siteNo'])
        result_dic = {} #결과 set
        # dev 학습 데이터를 서비스로 전송
        send = distribute.dist('siteNo_'+siteNo)
        result_dic = send.run(data)
        return Response(result_dic,content_type=c_type)

class distribute_dictionary(APIView):
    def post(self , request):
        data = json.loads(request.body) #파라미터 로드
        result_dic = {} #결과 set
        result_dic = run_util.save_dict(data)
            
        return Response(result_dic,content_type=c_type)
   
#엘라스틱서치
class classify(APIView):
    def get(self , request):
        w2v_query = w2v_question.question(request.query_params.dict())
        question = request.query_params.get('query')
        question_title = request.query_params.get('query_title')
        result_answer = w2v_query.word2vec_question(question,question_title)
        
        return Response(result_answer,content_type=c_type)
    
    def post(self , request):
        data = json.loads(request.body) #파라미터 로드
        w2v_query = w2v_question.question(data)
        question = str(data['query'])
        question_title = None
        if data.get('question_title') != None :
            question_title = str(data['query_title'])
        result_answer = w2v_query.word2vec_question(question,question_title)
        return Response(result_answer,content_type=c_type)
#모델에 직접 질의
class classify2(APIView):
    def get(self , request):
        site_no = request.query_params.get('siteNo')
        question = request.query_params.get('query')
        searchip = request.query_params.get('searchIp')
        version = request.query_params.get('version')
        dic_path = request.query_params.get('dicPath')
        mecab_dic_path = request.query_params.get('mecabDicPath')
        size = request.query_params.get('size')
        w2v_query = w2v_question.vec_dic_question(site_no, searchip, version, dic_path, mecab_dic_path, size)
        result_answer = w2v_query.word2vec_question(question)
        
        
        return Response(result_answer,content_type=c_type)
    
    def post(self , request):
        data = json.loads(request.body) #파라미터 로드
        site_no = str(data['siteNo'])
        question = str(data['query'])
        searchip = str(data['searchIp'])
        version = str(data['version'])
        dic_path = str(data['dicPath'])
        mecab_dic_path = str(data['mecabDicPath'])
        size = str(data['size'])
        w2v_query = w2v_question.vec_dic_question(site_no, searchip, version, dic_path, mecab_dic_path, size)
        result_answer = w2v_query.word2vec_question(question)
        return Response(result_answer,content_type=c_type)