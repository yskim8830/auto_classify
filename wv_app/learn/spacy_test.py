import spacy
from spacy.lang.ko import Korean

nlp = spacy.load("en_core_web_sm")
ruler = nlp.add_pipe("entity_ruler", after="ner")
patterns = [{"label": "라벨1", "pattern": "@지역 @질병"},
            {"label": "라벨1", "pattern": "@델타 @변이 @확산"},
            {"label": "라벨3", "pattern": {"LOWER": {"IN": "@델타 @변이"}}}, 
            {"label": "라벨4", "pattern": [{"LOWER": "@델타"}, {"LOWER": "@변이"}, {"OP" : "*"}]}]

ruler.add_patterns(patterns)

doc = nlp("@델타 @변이 @확산 @민노총 @지역 @질병") 
print([{ent.label_ : ent.text} for ent in doc.ents])

