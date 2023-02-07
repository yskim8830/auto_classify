# import spacy
# from spacy.lang.ko import Korean

# nlp = spacy.load("en_core_web_sm")
# ruler = nlp.add_pipe("entity_ruler", after="ner")
# patterns = [{"label": "라벨1", "pattern": "@지역 @질병"},
#             {"label": "라벨1", "pattern": "@델타 @변이 @확산"},
#             {"label": "라벨3", "pattern": {"LOWER": {"IN": "@델타 @변이"}}}, 
#             {"label": "라벨4", "pattern": [{"LOWER": "@델타"}, {"LOWER": "@변이"}, {"OP" : "*"}]}]

# ruler.add_patterns(patterns)

# doc = nlp("@델타 @변이 @확산 @민노총 @지역 @질병") 
# print([{ent.label_ : ent.text} for ent in doc.ents])

# import spacy
# from spacy.lang.ko.examples import sentences 

# nlp = spacy.load("ko_core_news_sm")
# doc = nlp('델타변이 확산에도…민노총 3일 충청 대림역에 1만명 모인다 테스트 코로나19의 확산세를 고려해 집회를 멈춰 달라는 정부의 거듭된 만류')
# print(doc.text)
# for token in doc:
#     print(token.text, token.pos_, token.dep_, token.lemma_)

# import spacy
# from spacy.matcher import PhraseMatcher

# nlp = spacy.load('en_core_web_sm')

# patterns = [{"label":"FLYING","pattern":[{"LEMMA":"land"},{}, {"ENT_TYPE":"GPE"}]}]

# ruler = nlp.add_pipe("entity_ruler", config={"overwrite_ents": True})
# ruler.add_patterns(patterns)

# print(f'spaCy Pipelines: {nlp.pipe_names}')

# doc = nlp("The student landed in Baltimore for the holidays.")

    
# for ent in doc.ents:
#     print(ent.text, ent.label_)
"""
import re
from konlpy.tag import Mecab

mecab = Mecab()

def filterSentence(sentence):
    JSON_REMOVE_PATTERN = "(\\r\\n|\\r|\\n|\\n\\r|(\\t)+|(\\s)+)"
    match = "[^A-Za-z0-9가-힣]" 
    ret_str = re.sub(JSON_REMOVE_PATTERN, ' ', sentence)
    ret_str = re.sub(match, ' ', ret_str)
    return ret_str

def removePostpositionSentence(mec, sentence, stopTag):
    morphs = mec.pos(sentence)
    noun = ''
    pos = ''
    print(morphs)
    for word in morphs:
        if str(stopTag).find(word[1]) > -1:
            if noun != '':
                if str(noun)+' '+str(word[0])+' ' in sentence: #명사와 조사가 띄어쓰기 되어있을때
                    sentence = str(sentence).replace(str(noun)+' '+str(word[0]), str(noun))
                else:
                    sentence = str(sentence).replace(str(noun)+str(word[0]), str(noun))
        else:
            noun = word[0]
            pos = word[1]
        if (pos == 'NNG' or pos == 'NNP') and (word[1] == 'NNG' or word[1] == 'NNP'):
                sentence = str(sentence).replace(str(noun), str(noun)+' ')
    print('sentence :: ' + sentence)
    
t_text = '안녕하세요 대림역테스트를 하고 수도권테스트도 할래요'
text = removePostpositionSentence(mecab, filterSentence(t_text.lower()), 'JKS,JKC,JKG,JKO,JKB,JKV,JKQ,JX,JC')
"""

import numpy as np
from sklearn.metrics import f1_score

mean = np.array(np.ones(4023, np.uint8))
fault =  np.array(np.zeros(977, np.uint8))
y = np.array(np.ones(5000, np.uint8))
p = np.concatenate((mean, fault))

f1 = f1_score(y,p)
print(f1)

#0.8917211570431121
#0.9176317783309883