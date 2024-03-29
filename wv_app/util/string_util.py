import re
from .file_util import dicFile

def splitDictionary(str, sep):
    sb = ''
    s_len = len(str)
    w_len = len(sep)
    
    if sep == '' or sep == '0':
        return sb
    if w_len > 0:
        offset = 0
        for i in range(0, w_len):
            o_len = int(sep[i])
            if offset + o_len <= s_len:
                sb += str[offset:offset + o_len]
            offset = offset + o_len
            
            if i + 1 < w_len:
                sb += ','
    return sb

#print(splitDictionary('안녕하세요','23'))

# 사전파일을 파일로 저장한다.
def save_dictionary(dicpath, dicList):
    try:
        stopword = dicFile(dicpath)
        synonym = dicFile(dicpath)
        compound = dicFile(dicpath)
        stop_str = ''
        synm_str = ''
        comp_str = ''
        
        for dic in dicList:
            dic = dic['_source']
            siteNo = str(dic['siteNo'])
            word = dic['word']
            nosearchYn = dic['nosearchYn']
            synonyms = dic['synonyms']
            wordSep = dic['wordSep']
            
            if siteNo is None or word is None or word == '' or nosearchYn is None or nosearchYn == '':
                continue
            if nosearchYn == 'y':
                stop_str += siteNo + '\t' + word + '\n'
                if synonyms != '':
                    for stop in synonyms.split(','):
                        if stop != '':
                            stop_str += siteNo + '\t' + stop + '\n'
            else:
                if synonyms != '':
                    synm_str += siteNo + '\t' + word + '\t' + synonyms + '\n'
                split_word = splitDictionary(word,wordSep)
                comp_str += siteNo + '\t' + word + '\t' + split_word + '\n'
                
        stopword.create_dic_file(dic='stopword',str=stop_str, site_no='')
        synonym.create_dic_file(dic='synonym',str=synm_str, site_no='')
        compound.create_dic_file(dic='compound',str=comp_str, site_no='')
    except Exception as e:
        print(str(e))
        return False
    return True

#엔티티 사전 파일 저장
def save_entity_dictionary(dicpath, dicList, site_no, version):
    try:
        entity_path = dicFile(dicpath)
        entity_str = ''
        for dic in dicList:
            dic = dic['_source']
            entity = str(dic['entity'])
            entry = str(dic['entry'])
            entity_str += str(version) + '\t' + entity + '\t' + entry + '\n'
        if version == -1:
            entity_path.service_dic_file(dic='entity',str=entity_str, site_no=site_no)
        else:
            entity_path.create_dic_file(dic='entity',str=entity_str, site_no=site_no)
    except Exception as e:
        print(str(e))
        return False
    return True    

#룰 파일 저장
def save_rule_dictionary(dicpath, dicList, site_no, version):
    try:
        rule_path = dicFile(dicpath)
        rule_str = ''
        for dic in dicList:
            dic = dic['_source']
            categoryNo = str(dic['categoryNo'])
            categoryNm = str(dic['categoryNm'])
            fullItem = str(dic['fullItem'])
            ruleNo = str(dic['ruleNo'])
            for rule in str(dic['rule']).split(' '):
                rule_str += str(version) + '\t' +categoryNo + '\t' + categoryNm + '\t' + fullItem + '\t' + rule + '\t' + ruleNo  + '\n'
        if version == -1:    
            rule_path.service_dic_file(dic='rule',str=rule_str, site_no=site_no)
        else:
            rule_path.create_dic_file(dic='rule',str=rule_str, site_no=site_no)
    except Exception as e:
        return False
    return True    

def filterSentence(sentence):
    JSON_REMOVE_PATTERN = "(\\r\\n|\\r|\\n|\\n\\r|(\\t)+|(\\s)+)"
    match = "[^A-Za-z0-9가-힣]" 
    ret_str = re.sub(JSON_REMOVE_PATTERN, ' ', sentence)
    ret_str = re.sub(match, ' ', ret_str)
    return ret_str

def specialReplace(sentence):
    match = "[^\uAC00-\uD7A30-9a-zA-Z]"
    ret_str = re.sub(match, ' ', sentence)
    return ret_str