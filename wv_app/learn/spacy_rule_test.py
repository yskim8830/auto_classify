import re
import spacy
#import kss
from spacy.matcher import PhraseMatcher
from konlpy.tag import Mecab
from nltk import sent_tokenize

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

mecab = Mecab()
# nlp = spacy.load("ko_core_news_sm")
# nlp2 = spacy.load("en_core_web_sm")
nlp = spacy.load("en_core_web_sm")

#tagging
tagging = nlp
phrase_matcher = PhraseMatcher(tagging.vocab)
countries = [tagging.make_doc(text) for text in ['수도권', '충청', '전라', '경기', '강원']]
days = [tagging.make_doc(text) for text in ['대림역', '신림역']]
days2 = [tagging.make_doc(text) for text in ['테스트']]

phrase_matcher.add("@지역권역명",None, *countries)
phrase_matcher.add("@지하철역",None, *days)
phrase_matcher.add("@테스트",None, *days2)

#rule
rule = nlp
ruler = rule.add_pipe("entity_ruler")
patterns = [{"label": "라벨1", "pattern": "@지역권역명 @지하철역"},
            {"label": "라벨2", "pattern": "@지역 @숫자"},
            {"label": "라벨3", "pattern": {"LEMMA": {"IN": ["@정부", "@거듭"]}}},
            {"label": "라벨4", "pattern": [{"TEXT" : "@정부 @거듭"},{"TEXT" : "@거듭 @만류"}]}]

ruler.add_patterns(patterns)

#question
r_question = '델타변이 확산에도…민노총 3일 충청 대림역에 1만명 모인다 테스트 코로나19 확산세를 고려해 집회를 멈춰 달라는 정부의 거듭된 만류'
print(r_question)

tokenized_text = sent_tokenize(r_question)

for t_text in tokenized_text:
    #명사만 추출
    text = getWordPosStringList(mecab, filterSentence(t_text.lower()), 'NNG,NNP,NNB,UM,NR,NP') 
    print(text)

    #태깅
    g_text = ''
    doc = tagging(text)
    matches = phrase_matcher(doc)
    for match_id, start, end in matches:
        string_id = tagging.vocab.strings[match_id]
        span = doc[start:end]
        text = text.replace(span.text, string_id)
    print(text) #태깅결과
    
    #룰
    doc2 = rule(text)
    print([(ent.text, ent.label_) for ent in doc2.ents]) #룰 결과
