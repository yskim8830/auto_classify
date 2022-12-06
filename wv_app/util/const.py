
class const():
    def __init__(self):
        
        self._train_state = '$train_state'
        self._dev_idx = '$xdev_'
        self._svc_idx = '$xsvc_'
        self._als_idx = '$xals_'
        self._model = 'model_'
        self._intent = 'intent_'
        self._question = 'question_'
    
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
    def model(self):
        return self._model
    @property    
    def intent(self):
        return self._intent
    @property    
    def question(self):
        return self._question