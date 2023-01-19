from django.apps import AppConfig
import os
import re
import spacy
import configparser
from spacy.matcher import PhraseMatcher
from nltk import sent_tokenize
from .util.const import const
from .util.file_util import dic

index = const()
global rule_data
rule_data = {}

class ClassifyAppConfig(AppConfig):
    name = 'wv_app'
    verbose_name = 'ProClassify'
    def ready(self):
        if not os.environ.get('APP'):
            os.environ['APP'] = 'True'
            setRuleModel()
        print('start')

#룰패턴에 필요한 정보를 로드한다.
def setRuleModel():
    print('load spacy.')
    nlp = spacy.load("en_core_web_sm")
    print('load spacy success.')
    #사전파일 map 으로 저장
    dics = dic(index.proclassify_dic_path)
    entitys = dics.entity()
    rules = dics.rule()
    
    entity_result = {}
    entity_matcher_result = {}
    for entity in entitys:
        sub_nlp = nlp
        phrase_matcher = PhraseMatcher(sub_nlp.vocab)
        for sub in entitys[entity]:
            various = [sub_nlp.make_doc(text) for text in entitys[entity][sub].split(',')]
            phrase_matcher.add(sub,None, *various)
        entity_result[entity] = sub_nlp
        entity_matcher_result[entity] = phrase_matcher
        
    rule_result = {}
    rule_category_result = {}
    for rule in rules:
        sub_nlp = nlp
        ruler = sub_nlp.add_pipe("entity_ruler")
        patterns = []
        category = {}
        for sub in rules[rule]:
            various = {'label' : sub['categoryNo'], 'pattern' : sub['rule'].replace(',' , ' ')}
            patterns.append(various)
            various2 = {'categoryNm' : sub['categoryNm'], 'fullItem' : sub['fullItem']}
            category[sub['categoryNo']] = various2
        ruler.add_patterns(patterns)
        rule_result[rule] = sub_nlp
        rule_category_result[rule] = category
        
    #global 함수로 담기
    global rule_data
    rule_data['entity'] = entity_result
    rule_data['matcher'] = entity_matcher_result
    rule_data['rule'] = rule_result
    rule_data['category'] = rule_category_result