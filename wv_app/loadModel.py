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
    
    entity_t_result = {}
    entity_matcher_t_result = {}
    for entity in entitys:
        entity_result = {}
        entity_matcher_result = {}
        sub_nlp = nlp
        phrase_matcher = PhraseMatcher(sub_nlp.vocab)
        for num, versions in enumerate(reversed(entitys[entity])):
            if(num > 2):
                break
            for sub in entitys[entity][versions]:
                for sub_key, sub_val in sub.items():
                    various = [sub_nlp.make_doc(text) for text in sub_val.split(',')]
                    phrase_matcher.add(sub_key,None, *various)
            entity_result[versions] = sub_nlp
            entity_matcher_result[versions] = phrase_matcher
        #운영 
        if entitys[entity].get('-1') != None:
            for sub in entitys[entity]['-1']:
                for sub_key, sub_val in sub.items():
                    various = [sub_nlp.make_doc(text) for text in sub_val.split(',')]
                    phrase_matcher.add(sub_key,None, *various)
            entity_result['-1'] = sub_nlp
            entity_matcher_result['-1'] = phrase_matcher
           
        entity_t_result[entity] = entity_result
        entity_matcher_t_result[entity] = entity_matcher_result
        
    rule_t_result = {}
    rule_category_t_result = {}
    for rule in rules:
        rule_result = {}
        rule_category_result = {}
        sub_nlp = nlp
        ruler = sub_nlp.add_pipe("entity_ruler")
        for num, versions in enumerate(reversed(rules[rule])):
            if(num > 2):
                break
            patterns = []
            category = {}
            for sub in rules[rule][versions]:
                various = {'label' : sub['categoryNo'], 'pattern' : sub['rule'].replace(',' , ' ')}
                patterns.append(various)
                various2 = {'categoryNm' : sub['categoryNm'], 'fullItem' : sub['fullItem']}
                category[sub['categoryNo']] = various2
            ruler.add_patterns(patterns)
            rule_result[versions] = sub_nlp
            rule_category_result[versions] = category
        #운영
        if rules[rule].get('-1') != None:
            patterns = []
            category = {}
            for sub in rules[rule]['-1']:
                various = {'label' : sub['categoryNo'], 'pattern' : sub['rule'].replace(',' , ' ')}
                patterns.append(various)
                various2 = {'categoryNm' : sub['categoryNm'], 'fullItem' : sub['fullItem']}
                category[sub['categoryNo']] = various2
            ruler.add_patterns(patterns)
            rule_result['-1'] = sub_nlp
            rule_category_result['-1'] = category
        rule_t_result[rule] = rule_result
        rule_category_t_result[rule] = rule_category_result
        
    #global 함수로 담기
    global rule_data
    rule_data['entity'] = entity_t_result
    rule_data['matcher'] = entity_matcher_t_result
    rule_data['rule'] = rule_t_result
    rule_data['category'] = rule_category_t_result