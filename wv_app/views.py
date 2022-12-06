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


class train(APIView):
    def post(self , request):
        data = json.loads(request.body) #파라미터 로드
        mode = str(data['mode'])
        siteNo = str(data['siteNo'])
        result_dic = {} #결과 set
        
        if mode == 'run' or mode == 'train':
            #질의를 embedding 하여 저장
            run = learning.learn('siteNo_'+siteNo)
            result_dic = run.learningBERT(data)
        
        elif mode == 'send' or mode == 'dist':
            # 생성된 사전 모델을 엘라스틱으로 insert
            send = distribute.dist('siteNo_'+siteNo)
            result_dic = send.distributeBERT(data)
            
        elif mode == 'status':
            result_dic = run_util.status(data)
            
        elif mode == 'runstop':
            run = learning.learn('siteNo_'+siteNo)
            print('stop main')
            result = run.raise_exception()
            if result == True :
                run.join()
                result_dic = {'result' :  'success'}
            else :
                result_dic = {'result' : 'fail, learning is not working'}
                
        elif mode == 'clear':
            result_dic = run_util.recoverySite(data)
            
        elif mode == 'dic':
            result_dic = run_util.save_dict(data)
            
        return Response(result_dic)

class question(APIView):
    def get(self , request):
        site_no = request.query_params.get('siteNo')
        question = request.query_params.get('query')
        searchip = request.query_params.get('searchIp')
        version = request.query_params.get('version')
        dic_path = request.query_params.get('dicPath')
        mecab_dic_path = request.query_params.get('mecabDicPath')
        w2v_query = w2v_question.question(site_no, searchip, version, mecab_dic_path)
        # w2v_query = w2v_question.vec_dic_question(site_no, searchip, version, dic_path, mecab_dic_path)
        result_answer = w2v_query.word2vec_question(question)
        
        
        return Response(result_answer)
    
    def post(self , request):
        data = json.loads(request.body) #파라미터 로드
        site_no = str(data['siteNo'])
        question = str(data['query'])
        searchip = str(data['searchIp'])
        version = str(data['version'])
        dic_path = str(data['dicPath'])
        mecab_dic_path = str(data['mecabDicPath'])
        w2v_query = w2v_question.question(site_no, searchip, version, mecab_dic_path)
        # w2v_query = w2v_question.vec_dic_question(site_no, searchip, version, dic_path, mecab_dic_path)
        result_answer = w2v_query.word2vec_question(question)
        return Response(result_answer)

class question2(APIView):
    def get(self , request):
        site_no = request.query_params.get('siteNo')
        question = request.query_params.get('query')
        searchip = request.query_params.get('searchIp')
        version = request.query_params.get('version')
        dic_path = request.query_params.get('dicPath')
        mecab_dic_path = request.query_params.get('mecabDicPath')
        # w2v_query = w2v_question.question(site_no, searchip, version, mecab_dic_path)
        w2v_query = w2v_question.vec_dic_question(site_no, searchip, version, dic_path, mecab_dic_path)
        result_answer = w2v_query.word2vec_question(question)
        
        
        return Response(result_answer)
    
    def post(self , request):
        data = json.loads(request.body) #파라미터 로드
        site_no = str(data['siteNo'])
        question = str(data['query'])
        searchip = str(data['searchIp'])
        version = str(data['version'])
        dic_path = str(data['dicPath'])
        mecab_dic_path = str(data['mecabDicPath'])
        # w2v_query = w2v_question.question(site_no, searchip, version, mecab_dic_path)
        w2v_query = w2v_question.vec_dic_question(site_no, searchip, version, dic_path, mecab_dic_path)
        result_answer = w2v_query.word2vec_question(question)
        return Response(result_answer)