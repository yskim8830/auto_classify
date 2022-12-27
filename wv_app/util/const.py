import configparser

class const():
    def __init__(self):
        self._train_state = '$classify_train_state'
        self._dev_idx = '$xdev_'
        self._svc_idx = '$xsvc_'
        self._als_idx = '$xals_'
        self._classify = 'classify_'
        self._document = 'document_'
        self._rule = 'rule_'
        self._question = 'question_'
        
        properties = configparser.ConfigParser()
        properties.read('proclassify.ini')
        self._proclassify_path = properties["CONFIG"]["proclassify_path"]
        self._proclassify_dic_path = properties["CONFIG"]["proclassify_dic_path"]
    
    @property    
    def train_state(self):
        return self._train_state    
    @property    
    def dev_idx(self):
        return self._dev_idx
    @property    
    def svc_idx(self):
        return self._svc_idx
    @property    
    def als_idx(self):
        return self._als_idx
    @property    
    def classify(self):
        return self._classify
    @property    
    def document(self):
        return self._document
    @property    
    def rule(self):
        return self._rule
    @property    
    def question(self):
        return self._question
    @property    
    def proclassify_path(self):
        return self._proclassify_path
    @property    
    def proclassify_dic_path(self):
        return self._proclassify_dic_path