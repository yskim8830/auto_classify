import spacy
import os
import re
from spacy.matcher import PhraseMatcher
from nltk import sent_tokenize
from konlpy.tag import Mecab

# index = const()
global rule_data
rule_data = {}


#룰패턴에 필요한 정보를 로드한다.
def setRuleModel():
    print('load spacy.')
    nlp = spacy.load("en_core_web_sm")
    print('load spacy success.')
    #사전파일 map 으로 저장
    # dics = dic(index.proclassify_dic_path)
    dics = dic('/home/tensor/dic/prosearch')
    entitys = dics.entity()
    rules = dics.rule()
    
    entity_result = {}
    entity_matcher_result = {}
    for entity in entitys:
        entity_nlp = nlp
        phrase_matcher = PhraseMatcher(entity_nlp.vocab)
        for sub in entitys[entity]:
            various = [entity_nlp.make_doc(text) for text in entitys[entity][sub].split(',')]
            phrase_matcher.add(sub,None, *various)
        entity_result[entity] = entity_nlp
        entity_matcher_result[entity] = phrase_matcher
        
    rule_result = {}
    rule_category_result = {}
    for rule in rules:
        rule_nlp = nlp
        ruler = rule_nlp.add_pipe("entity_ruler")
        patterns = []
        category = {}
        for idx, sub in enumerate(rules[rule]):
            various = {'label' : sub['categoryNo'], 'pattern' : sub['rule'].replace(',' , ' ')}
            patterns.append(various)
            various2 = {'categoryNm' : sub['categoryNm'], 'fullItem' : sub['fullItem']}
            category[sub['categoryNo']] = various2
        ruler.add_patterns(patterns)
        rule_result[rule] = rule_nlp
        rule_category_result[rule] = category
        
    #global 함수로 담기
    global rule_data
    rule_data['entity'] = entity_result
    rule_data['matcher'] = entity_matcher_result
    rule_data['rule'] = rule_result
    rule_data['category'] = rule_category_result
    
    testQuestion()
    
def testQuestion():
    mecab = Mecab()
    site_no = '1'
    question = '델타변이 확산에도…민노총 3일 충청 대림역에 1만명 모인다 테스트 코로나19 확산세를 고려해 집회를 멈춰 달라는 정부의 거듭된 만류'
    global rule_data
    if rule_data['entity'].get(str(site_no)) != None:
        tagging = rule_data['entity'][str(site_no)]
        phrase_matcher = rule_data['matcher'][str(site_no)] #PhraseMatcher(tagging.vocab)
        rule = rule_data['rule'][str(site_no)]
        category = rule_data['category'][str(site_no)]
        tokenized_text = sent_tokenize(question)
        for t_text in tokenized_text:
            #명사만 추출
            text = getWordPosStringList(mecab, filterSentence(t_text.lower()), 'NNG,NNP,NNB,UM,NR,NP') 
            print(text)
    #1. 태깅 - start
            doc = tagging(text)
            matches = phrase_matcher(doc)
            for match_id, start, end in matches:
                string_id = tagging.vocab.strings[match_id]
                span = doc[start:end]
                text = text.replace(span.text, string_id)
            print(text) #태깅결과
            
    #2. 룰 매칭 - start
            doc2 = rule(text)
            results = [{ent.label_ : ent.text} for ent in doc2.ents]
            print([(ent.text, ent.label_) for ent in doc2.ents]) #룰 결과
            for value in results:
                print(category[list(value.keys())[0]])


def getWordPosStringList(mec, sentence, useTag):
    result = []
    morphs = mec.pos(sentence)
    for word in morphs:
        if str(useTag).find(word[1]) != -1 and len(word[0]) > 1:
            result.append(word[0])
    return ' '.join(result)

def filterSentence(sentence):
    JSON_REMOVE_PATTERN = "(\\r\\n|\\r|\\n|\\n\\r|(\\t)+|(\\s)+)"
    match = "[^A-Za-z0-9가-힣]" 
    ret_str = re.sub(JSON_REMOVE_PATTERN, ' ', sentence)
    ret_str = re.sub(match, ' ', ret_str)
    return ret_str

def testTagging(tagging,phrase_matcher,text):
    doc = tagging(text)
    matches = phrase_matcher(doc)
    for match_id, start, end in matches:
        string_id = tagging.vocab.strings[match_id]
        span = doc[start:end]
        text = text.replace(span.text, string_id)

class dic:
    def __init__(self, path):
        self.path = path
    def stopword(self):
        self.filename = 'classify_stopword.txt'
        site = ''
        dic_map = {}
        setMap = set()
        with open(os.path.join(self.path,self.filename),'r') as f:
            for line in f:
                line = line.strip()
                if line[0] != '#':
                    data = line.split('\t')
                    if(len(data) > 1):
                        if(site == ''):
                            site = data[0]
                        elif site != data[0]:
                            dic_map[site] = setMap
                            setMap = set()
                            site = data[0]
                        setMap.add(data[1].strip())
                if len(setMap) > 0:
                    dic_map[site] = setMap
        return dic_map
    def synonym(self):
        self.filename = 'classify_sysnonym.txt'
        site = ''
        dic_map = {}
        setMap = {}
        with open(os.path.join(self.path,self.filename),'r') as f:
            for line in f:
                line = line.strip()
                if line[0] != '#':
                    data = line.split('\t')
                    if(len(data) > 1):
                        if(site == ''):
                            site = data[0]
                        elif site != data[0]:
                            dic_map[site] = setMap
                            setMap = {}
                            site = data[0]
                        for synm in data[2].split(','):
                            setMap[synm] = data[1].strip()
                if len(setMap) > 0:
                    dic_map[site] = setMap
        return dic_map
    def compound(self):
        self.filename = 'classify_user_define.txt'
        site = ''
        dic_map = {}
        setMap = {}
        with open(os.path.join(self.path,self.filename),'r') as f:
            for line in f:
                line = line.strip()
                if line[0] != '#':
                    data = line.split('\t')
                    if(len(data) > 1):
                        if(site == ''):
                            site = data[0]
                        elif site != data[0]:
                            dic_map[site] = setMap
                            setMap = {}
                            site = data[0]
                            
                        chk_key = data[1][0] #1개음절을 setmap 키로 담음
                        dic_data = []
                        dic_data.append(data[1])
                        
                        if len(data) > 2:
                            dic_data.append(data[2])
                        else:
                            dic_data.append(data[1])
                            
                        value_data = []
                        for setMap_key in setMap:
                            if setMap_key == chk_key:
                                value_data = setMap[chk_key]
                        value_data.append(dic_data)
                        setMap[chk_key] = value_data
                        
                if len(setMap) > 0:
                    dic_map[site] = setMap
        return dic_map
    def entity(self):
        file_name = 'classify_entity.txt'
        file_list = [_ for _ in os.listdir(self.path) if _.endswith(file_name)]
        entity_list = {}
        for entity_file in file_list:
            site_no = entity_file.replace('_'+file_name, '')
            entity = {}
            with open(os.path.join(self.path,entity_file),'r') as f:
                for line in f:
                    line = line.strip()
                    if line[0] != '#':
                        data = line.split('\t')
                        if(len(data) > 1):
                            entity[data[0]] = data[1]
            
            entity_list[site_no] = entity
        return entity_list
    
    def rule(self):
        file_name = 'classify_rule.txt'
        file_list = [_ for _ in os.listdir(self.path) if _.endswith(file_name)]
        rule_list = {}
        for rule_file in file_list:
            site_no = rule_file.replace('_'+file_name, '')
            with open(os.path.join(self.path,rule_file),'r') as f:
                sub = []
                for line in f:
                    line = line.strip()
                    if line[0] != '#':
                        data = line.split('\t')
                        if(len(data) == 5):
                            rule = {}
                            rule['categoryNo'] = data[0]
                            rule['categoryNm'] = data[1]
                            rule['fullItem'] = data[2]
                            rule['rule'] = data[3]
                            rule['count'] = data[4]
                            sub.append(rule)
                rule_list[site_no] = sub
        return rule_list

setRuleModel()