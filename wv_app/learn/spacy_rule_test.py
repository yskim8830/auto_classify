import re
import spacy
#import kss
from spacy.matcher import PhraseMatcher
from konlpy.tag import Mecab
from nltk import sent_tokenize

def getWordPosStringList(mec, sentence, useTag):
    result = []
    morphs = mec.pos(sentence)
    print(morphs)
    for word in morphs:
        if str(useTag).find(word[1]) != -1 and len(word[0]) > 1:
            result.append(word[0])
    return ' '.join(result)

def removePostpositionSentence(mec, sentence, stopTag):
    morphs = mec.pos(sentence)
    noun = ''
    print(morphs)
    for word in morphs:
        if str(stopTag).find(word[1]) > -1:
            if noun != '':
                sentence = str(sentence).replace(str(noun)+str(word[0]), str(noun))
                # noun = ''
        else:
            noun = word[0]
    return sentence

def filterSentence(sentence):
    JSON_REMOVE_PATTERN = "(\\r\\n|\\r|\\n|\\n\\r|(\\t)+|(\\s)+)"
    match = "[^A-Za-z0-9가-힣]" 
    ret_str = re.sub(JSON_REMOVE_PATTERN, ' ', sentence)
    ret_str = re.sub(match, ' ', ret_str)
    return ret_str

mecab = Mecab()
# nlp = spacy.load("ko_core_news_sm")
# nlp2 = spacy.load("en_core_web_sm")
nlp = spacy.blank("en")
# nlp = spacy.load("en_core_web_sm")

#tagging
tagging = nlp
phrase_matcher = PhraseMatcher(tagging.vocab)
countries = [tagging.make_doc(text) for text in ['수도권', '충청', '전라', '경기', '강원']]
days = [tagging.make_doc(text) for text in ['대림역', '신림역']]
disease = [tagging.make_doc(text) for text in ['코로나19', '감기']]
days2 = [tagging.make_doc(text) for text in ['테스트']]
test1 = [tagging.make_doc(text) for text in ['델타변이']]
test2 = [tagging.make_doc(text) for text in ['확산']]

phrase_matcher.add("@지역권역명",None, *countries)
phrase_matcher.add("@지하철역",None, *days)
phrase_matcher.add("@테스트",None, *days2)
phrase_matcher.add("@질병",None, *disease)
phrase_matcher.add("@분류1",None, *test1)
phrase_matcher.add("@분류2",None, *test2)

#rule
# from spacy.pipeline import EntityRuler
rule = nlp
# ruler = EntityRuler(rule, overwrite_ents=True)
ruler = rule.add_pipe("entity_ruler")
patterns = [
            # {"label": "라벨1", "pattern": [{"LOWER": "@분류1"}, {"LOWER": "@분류2"},{"OP":"*"},{"LOWER": "@지역권역명"}, {"LOWER": "@지하철역"}]},
            # {"label": "라벨2", "pattern": [{"LOWER": "@지역권역명"}, {"LOWER": "@지하철역"},{"OP":"*"},{"LOWER": "@테스트"}, {"LOWER": "@질병"}, {"LOWER": "@분류2"}]},
            # {"label": "라벨2", "pattern": [{"LOWER": "@분류1"}, {"LOWER": "@분류2"}], "id" : "1"},
            {"label": "1", "pattern": "@지역권역명 @테스트"},
            {"label": "1", "pattern": "@지하철역 @테스트"},
            {"label": "2", "pattern": "@지역권역명 @지하철역"},
            
            # {"label": "라벨3", "pattern": "@분류1 @분류2", "id" : "1"},
            # {"label": "라벨4", "pattern": "@지역권역명 @지하철역", "id" : "1"},
            # {"label": "라벨5", "pattern": "@테스트 @질병 @분류2", "id" : "2"},
            # {"label": "라벨6", "pattern": {"LEMMA": {"IN": ["@정부", "@거듭"]}}},
            {"label": "라벨7", "pattern": [{"TEXT" : "@정부 @거듭"},{"TEXT" : "@거듭 @만류"}]}]

# ruler.initialize(lambda: [], nlp=rule, patterns=patterns)
# ruler.clear()
ruler.add_patterns(patterns)

#imsi test 
# ent_ruler = rule.add_pipe("entity_ruler", name="ent_ruler", config={"overwrite_ents" : True})
# ent_pattern = [
#     {"label": "최종라벨1", "pattern": [{"ENT_TYPE": "라벨3"},{"OP":"*"}, {"ENT_TYPE": "라벨4"}]},
#     {"label": "최종라벨2", "pattern": [{"ENT_TYPE": "라벨4"},{"OP":"*"}, {"ENT_TYPE": "라벨5"}]}
# ]
# ent_ruler.add_patterns(ent_pattern)

#question
r_question = '델타변이 확산에도…민노총 3일 충청 대림역에 1만명 모인다 테스트 코로나19 확산세를 고려해 집회를 멈춰 달라는 정부의 거듭된 만류'
r_question = '안녕하세요 수도권의 테스트를 하고 대림역 테스트 할래요'
print(r_question)

tokenized_text = sent_tokenize(r_question)

for t_text in tokenized_text:
    #명사만 추출
    # text = getWordPosStringList(mecab, filterSentence(t_text.lower()), 'NNG,NNP,NNB,UM,NR,NP')    
    #조사 제거
    text = removePostpositionSentence(mecab, filterSentence(t_text.lower()), 'JKS,JKC,JKG,JKO,JKB,JKV,JKQ,JX,JC')
    # text = t_text
    print('text :: ' + text)
    # print('text2:: ' + text2)

    #태깅
    g_text = ''
    doc = tagging(text)
    matches = phrase_matcher(doc)
    for match_id, start, end in matches:
        string_id = tagging.vocab.strings[match_id]
        span = doc[start:end]
        text = text.replace(span.text, string_id + ' ')
        text = text.replace('  ', ' ')
        text = text.replace('@@', '@')
    print(text) #태깅결과
    
    #룰
    
    doc2 = rule(text)
    # print([(ent.text, ent.label_, ent.id_) for ent in doc2.ents]) #룰 결과
    
    # for ent in doc2.ents:
    #     if id == '' :
    #         id = ent.id_
    #     elif id != ent.id_ :
    #         results[id] = result
    #         result = []
    #         id = ent.id_
    #     result.append({ent.label_ : ent.text})
    # results[id] = result #last
    
    print([(ent.text, ent.label_) for ent in doc2.ents]) #룰 결과
    
    id = ''
    result = []
    results = {}
    for ent in doc2.ents:
        if id == '' :
            id = ent.label_
        elif id != ent.label_ :
            if results.get(id) != None:
                results[id] += result
            else:
                results[id] = result
            result = []
            id = ent.label_
        result.append(ent.text)
    #last
    if results.get(id) != None: 
        results[id] += result
    else:
        results[id] = result
        
    print(results)
    
    rule_id = ''
    rule_result = []
    rule_results = {}
    for ent in doc2.ents:
        if rule_id == '' :
            rule_id = ent.label_
        elif rule_id != ent.label_ :
            if rule_results.get(rule_id) != None:
                rule_results[rule_id] += rule_result
            else:
                rule_results[rule_id] = rule_result
            rule_result = []
            rule_id = ent.label_
        rule_result.append(str(ent.text).replace(' ', ','))
    #last
    if rule_results.get(rule_id) != None: 
        rule_results[rule_id] += rule_result
    else:
        rule_results[rule_id] = rule_result
    rule_query_strings = []
    for ruleNo in rule_results:
        ruleNoList = rule_results[ruleNo]
        rule_query_string = {
            "bool": {
                "must": [
                    {
                    "simple_query_string": {
                        "fields": [
                        "rule.keyword"
                        ],
                        "query": ' '.join(ruleNoList),
                        "default_operator": "AND"
                    }
                    },{
                    "match": {
                        "patternCount": len(ruleNoList)
                    }
                    }
                ]
            }
        }
        rule_query_strings.append(rule_query_string)
            
    
    rules_query_string = {
        "query": {
            "bool": {
            "must": [
                {
                    "bool": {
                        "should": rule_query_strings
                    }
                },
                    {
                    "match": {
                        "version": 18
                    }
                }
            ]
            }
        }
    }
    
    print(rules_query_string)