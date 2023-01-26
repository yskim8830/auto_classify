from elasticsearch import Elasticsearch, helpers

class elastic_util:
    def __init__(self, host='127.0.0.1', port='6251'):
        self.host = host
        self.port = port
        server_list = [ {'host':host, 'port':port}]
        self.es = Elasticsearch( server_list )
    
    #get es object
    def getEs(self):
        return self.es
    
    #getHealth
    def getHealth(self):
        return self.es.indices()
    
    #getInfo
    def getInfo(self):
        return self.es.info()
    
    #create template
    def createtemplate(self, name, mapping):
        if self.es.indices.exists_template(name=name):
            pass
        else:
            return self.es.indices.put_template(name=name, body=mapping)
    
    #existIndex
    def existIndex(self, idx):
        return self.es.indices.exists(index=idx)
    
    #createindex
    def createindex(self, idx, mapping):
        if self.es.indices.exists(index=idx):
            pass
        else:
            return self.es.indices.create(index=idx, body=mapping)
    
    #deleteindex
    def deleteindex(self, idx):
        return self.es.indices.delete(index=idx, ignore=[400, 404])
    
    #existAlias
    def getAlias(self, aidx):
        return self.es.indices.get_alias(name=aidx)
    
    #createAlias
    def createAlias(self, aidx, idx):
        if self.es.indices.exists_alias(name=aidx):
            pass
        return self.es.indices.put_alias(name=aidx, index=idx)
    
    #changeAlias : alias, index, removeIndex
    def changeAlias(self, aidx, idx, ridx):
        body = {
            "actions": [
                {
                "remove": {
                    "index": ridx,
                    "alias": aidx
                }
                },
                {
                "add": {
                    "index": idx,
                    "alias": aidx
                }
                }
            ]
        }
        
        return self.es.indices.update_aliases(body=body)
        
    #reIndex
    def reIndex(self, sidx, tidx, mapping):
        body = {
            "source": {
                "index": sidx,
                "query": mapping['query']
            },
            "dest": {
                "index": tidx
            }
        }
        self.es.reindex(body=body, request_timeout=1000)
    
    #searchAll
    def searchAll(self, idx, size=10):
        response = self.es.search(index=idx, size=size, body={"query": {"match_all": {}}})
        fetched = len(response['hits']['hits'])
        result = []
        for i in range(fetched):
            result.append(response['hits']['hits'][i])
        return result
    
    #searchById
    def searchById(self, idx, id):
        response = self.es.search(index=idx, body={"query": {"match": { "_id" : id}}})
        fetched = len(response['hits']['hits'])
        result = []
        for i in range(fetched):
            result.append(response['hits']['hits'][i])
        return result
    
    #search
    def search(self, idx, body):
        response = self.es.search(index=idx, body=body)
        fetched = len(response['hits']['hits'])
        result = []
        for i in range(fetched):
            result.append(response['hits']['hits'][i])
        return result
    
    #search_aggregations for word2vec
    def search_avg(self, idx, body):
        response = self.es.search(index=idx, body=body)
        fetched = len(response['aggregations'])
        result = []
        for i in range(fetched):
            result.append(response['aggregations']['sum_'+str(i)]['value'])
        return result
    
    #search_vector for word2vec
    def search_vector(self, idx, body):
        response = self.es.search(index=idx, body=body)
        fetched = len(response['hits']['hits'])
        result = []
        if(response['hits']['total']['value'] > 0):
            for i in range(0,100):
                result.append(response['hits']['hits'][0]['_source']['dm_'+str(i)])
        else:
            return -1
        return result
    
    #countBySearch
    def countBySearch(self, idx, body):
        response = self.es.count(index=idx, body=body)
        return response['count']
    
    #scroll search
    def search_srcoll(self, idx, body):
        _KEEP_ALIVE_LIMIT='30s'
        response = self.es.search(index=idx, body=body, scroll=_KEEP_ALIVE_LIMIT, size = 100,)
        
        sid = response['_scroll_id']
        fetched = len(response['hits']['hits'])
        result = []
        for i in range(fetched):
            result.append(response['hits']['hits'][i])
        while(fetched>0): 
            response = self.es.scroll(scroll_id=sid, scroll=_KEEP_ALIVE_LIMIT)
            fetched = len(response['hits']['hits'])
            for i in range(fetched):
                result.append(response['hits']['hits'][i])
        return result
    
    def msearch(self, body, type):
        response = self.es.msearch(body=body)
        result = []
        if type == 'agg':
            for i in response['responses']:
                result.append(i['aggregations']) 
        else :
            for i in response['responses']:
                result.append(response[i]['hits']['hits'][0]) 
        return result
    
    def close(self):
        self.es.close()
    
    def refresh(self,idx):
        return self.es.indices.refresh(index=idx)
    
    #insertData
    def insertData(self, idx, id, doc):
        return self.es.index(index=idx, id=id, body=doc)
    
    #updateData
    def updateData(self, idx, id, doc):
        return self.es.update(index=idx, id=id, body={"doc" : doc })
    
    #updateAllData
    def updateAllData(self, idx, doc):
        return self.es.update_by_query(index=idx, body=doc)
    
    #deleteData
    def deleteData(self, idx, id):
        return self.es.delete(index=idx, id=id)
        
    #deleteAllData
    def deleteAllData(self, idx):
        return self.es.delete_by_query(index=idx, body={"query":{"match_all":{}}})
    
    #bulk
    def bulk(self, body):
        """예시
            body.append({
            '_index': [인덱스_이름],
            '_source': {
                "category": "test"
                "c_key": "test"
                "status": "test"
                "price": 1111
                "@timestamp": datetime.utcnow().strftime('%Y-%m-%dT%H:%M:%S.%f')[:-3] + 'Z'
                }
            })
        """
        return helpers.bulk(self.es, body)
    
    def question_aggr_query_to_vector(self, version, query_string):
        aggregations = {}
        for dm in range(0,100):
            aggr = {
                "sum" : {
                    "field" : "dm_"+str(dm)
                }
            }
            aggregations["sum_"+str(dm)] = aggr
        
        source = {
            "from": 0,
            "size": 0,
            "query": {
                "bool": {
                    "must": [
                        {
                            "query_string" : {
                                "query" : query_string,
                                "fields": ["term"],
                                "type": "best_fields",
                                "default_operator": "or",
                                "max_determinized_states": 10000,
                                "enable_position_increments": True,
                                "fuzziness": "AUTO",
                                "fuzzy_prefix_length": 0,
                                "fuzzy_max_expansions": 50,
                                "phrase_slop": 0,
                                "escape": False,
                                "auto_generate_synonyms_phrase_query": True,
                                "fuzzy_transpositions": True,
                                "boost": 10000
                            }
                        }
                    ]
                }
            },
            "aggregations": aggregations
        }
        if int(version) > -1 :
            filter = [
                {
                    "query_string": {
                        "query": "version:" + version
                    }
                }
            ]
            source['query']['bool']['filter'] = filter
        return source
    
    def question_query_to_vector(self, version, query_string):
        source = {
                "from": 0,
                "size": 1,
                "query": {
                    "bool": {
                        "must": [
                            {
                                "query_string" : {
                                    "query" : query_string,
                                    "fields": ["term"]
                                }
                            }
                        ]
                    }
                }
            }
        if int(version) > -1 :
            filter = [
                {
                    "query_string": {
                        "query": "version:" + version
                    }
                }
            ]
            source['query']['bool']['filter'] = filter
        return source
    
    def question_vector_query(self, version, vector: list, sfrom=0, size=5):
        source = {
            "from": sfrom,
            "size": size,
            "query": {
                "bool": {
                    "must": [
                        {
                            "elastiknn_nearest_neighbors": {
                                "field": "question_vec",
                                "similarity": "L2",
                                "model": "lsh",
                                "candidates": 50,
                                "vec": vector
                            }
                        }
                    ],"must_not": {
                        "term": {
                            "categoryNo": {
                                "value": "0"
                            }
                        }
                    }
                }
            },
            "collapse": {
                "field": "categoryNo"
            }
        }
        if int(version) > -1 :
            filter = [
                {
                    "query_string": {
                        "query": "version:" + version
                    }
                }
            ]
            source['query']['bool']['filter'] = filter
        
        return source

    def question_index_template(self):
        return {
            "settings": {
                "similarity": {
                "pro_tfidf": {
                    "type": "scripted",
                    "script": {
                        "source": "double norm = (doc.freq); return query.boost  *norm;"
                    }
                }
                },
                "index": {
                "number_of_shards": "5",
                "elastiknn": True,
                "auto_expand_replicas": "0-1",
                "analysis": {
                    "analyzer": {
                    "whitespace_analyzer": {
                        "filter": [
                        "lowercase",
                        "trim"
                        ],
                        "tokenizer": "my_whitespace"
                    }
                    },
                    "tokenizer": {
                        "my_whitespace": {
                            "type": "whitespace",
                            "max_token_length": "60"
                            }
                        }
                    }
                },
                "index.mapping.total_fields.limit": 99999999
            },
            "mappings": {
                "properties": {
                    "id": {
                        "analyzer": "whitespace_analyzer",
                        "type": "text"
                    },
                    "version": {
                        "type": "long"
                    },
                    "siteNo": {
                        "type": "long"
                    },
                    "intentNo": {
                        "type": "long"
                    },
                    "categoryNo": {
                        "type": "long"
                    },
                    "categoryNm": {
                        "type" : "keyword",
                    },
                    "fullItem": {
                        "type" : "text",
                    },
                    "dialogNm": {
                        "type": "text",
                        "analyzer": "pro10_kr",
                        "search_analyzer": "pro10_search"
                    },
                    "question": {
                        "type": "text",
                        "analyzer": "pro10_kr",
                        "search_analyzer": "pro10_search"
                    },
                    "question_vec": {
                        "type": "elastiknn_dense_float_vector",
                        #WORD2VEC
                        "elastiknn": {
                            "dims": 100,
                            "model": "lsh",
                            "similarity": "l2",
                            "L": 99,
                            "k": 1,
                            "w": 3
                        }
                    },
                    "term": {
                        "analyzer": "whitespace_analyzer",
                        "type": "text",
                        "similarity": "pro_tfidf"
                    },
                    "term_syn": {
                        "analyzer": "whitespace_analyzer",
                        "type": "text",
                        "similarity": "pro_tfidf"
                    },
                    "terms": {
                        "analyzer": "whitespace_analyzer",
                        "type": "text"
                    },
                    "termNo": {
                        "type": "long"
                    },
                    "keywords": {
                        "analyzer": "whitespace_analyzer",
                        "type": "text"
                    },
                    "modifyDate": {
                        "type": "date",
                        "format": "yyyyMMddHHmmssSSS"
                    },
                    "createDate": {
                        "type": "date",
                        "format": "yyyyMMddHHmmssSSS"
                    },
                    "createUser": {
                        "analyzer": "whitespace_analyzer",
                        "type": "text"
                    },
                    "createUserNm": {
                        "analyzer": "whitespace_analyzer",
                        "type": "text"
                    },
                    "modifyUser": {
                        "analyzer": "whitespace_analyzer",
                        "type": "text"
                    },
                    "modifyUserNm": {
                        "analyzer": "whitespace_analyzer",
                        "type": "text"
                    },
                    "desc": {
                        "analyzer": "whitespace_analyzer",
                        "type": "text"
                    },
                    "useYn": {
                        "type": "keyword"
                    }
                }
            }
        }
        
        
    def train_state_template(self):
        return {
            "index_patterns": [
                "$classify_train_state*"
            ],
            "settings": {
                "index.mapping.ignore_malformed": True,
                "index": {
                "number_of_shards": "5",
                "elastiknn": True,
                "auto_expand_replicas": "0-1",
                "analysis": {
                    "analyzer": {
                        "whitespace_analyzer": {
                            "filter": [
                            "lowercase",
                            "trim"
                            ],
                            "tokenizer": "my_whitespace"
                        }
                    },
                    "tokenizer": {
                        "my_whitespace": {
                            "type": "whitespace",
                            "max_token_length": "60"
                        }
                    }
                }
                },
                "index.mapping.total_fields.limit": 99999999
            },
            "mappings": {
                "dynamic_templates": [
                    {
                        "search_string_01": {
                            "match": "id",
                            "match_mapping_type": "long",
                            "mapping": {
                            "type": "long"
                            }
                        }
                    },
                    {
                        "search_string_02": {
                            "match": "*site*",
                            "match_mapping_type": "string",
                            "mapping": {
                            "analyzer": "whitespace_analyzer",
                            "type": "text"
                            }
                        }
                    },
                    {
                        "search_string_03": {
                            "match": "*version*",
                            "match_mapping_type": "long",
                            "mapping": {
                            "type": "long"
                            }
                        }
                    },
                    {
                        "search_string_04": {
                            "match": "state",
                            "match_mapping_type": "string",
                            "mapping": {
                            "analyzer": "whitespace_analyzer",
                            "type": "text"
                            }
                        }
                    },
                    {
                        "search_string_05": {
                            "match": "*keyword*",
                            "match_mapping_type": "string",
                            "mapping": {
                            "analyzer": "whitespace_analyzer",
                            "type": "text"
                            }
                        }
                    },
                    {
                        "search_string_06": {
                            "match": "*_date",
                            "match_mapping_type": "string",
                            "mapping": {
                            "type": "date",
                            "format": "yyyyMMddHHmmssSSS"
                            }
                        }
                    },
                    {
                        "search_string_07": {
                            "match": "*_user",
                            "match_mapping_type": "string",
                            "mapping": {
                            "analyzer": "whitespace_analyzer",
                            "type": "text"
                            }
                        }
                    },
                    {
                        "search_string_07": {
                            "match": "status",
                            "match_mapping_type": "string",
                            "mapping": {
                            "analyzer": "whitespace_analyzer",
                            "type": "text"
                            }
                        }
                    },
                    {
                        "search_string_08": {
                            "match": "worker_id",
                            "match_mapping_type": "string",
                            "mapping": {
                            "analyzer": "whitespace_analyzer",
                            "type": "text"
                            }
                        }
                    }
                ]
            }
        }
        
        
    def doc_template(self):
        return {
            "index_patterns": [
                "$*_document_*"
            ],
            "settings": {
                "index.mapping.ignore_malformed": True,
                "index": {
                "number_of_shards": "5",
                "elastiknn": True,
                "auto_expand_replicas": "0-1",
                "analysis": {
                    "analyzer": {
                        "whitespace_analyzer": {
                            "filter": [
                            "lowercase",
                            "trim"
                            ],
                            "tokenizer": "my_whitespace"
                        }
                    },
                    "tokenizer": {
                        "my_whitespace": {
                            "type": "whitespace",
                            "max_token_length": "60"
                        }
                    }
                },
                "similarity": {
                    "pro_tfidf": {
                    "type": "scripted",
                    "script": {
                        "source": "double norm = (doc.freq); return query.boost  *norm;"
                        }
                    }
                }
                },
                "index.mapping.total_fields.limit": 99999999
            },
            "mappings": {
                "dynamic_templates": [
                    {
                        "search_string_01": {
                            "match": "id",
                            "match_mapping_type": "string",
                            "mapping": {
                            "analyzer": "whitespace_analyzer",
                            "type": "text"
                            }
                        }
                    },
                    {
                        "search_string_02": {
                            "match": "title",
                            "match_mapping_type": "string",
                            "mapping": {
                            "analyzer": "whitespace_analyzer",
                            "type": "text"
                            }
                        }
                    },
                    {
                        "search_string_03": {
                            "match": "content",
                            "match_mapping_type": "string",
                            "mapping": {
                            "analyzer": "whitespace_analyzer",
                            "type": "text"
                            }
                        }
                    },
                    {
                        "search_string_04": {
                            "match": "*No",
                            "match_mapping_type": "long",
                            "mapping": {
                            "type": "long"
                            }
                        }
                    },
                    {
                        "search_string_05": {
                            "match": "*version*",
                            "match_mapping_type": "long",
                            "mapping": {
                            "type": "long"
                            }
                        }
                    },
                    {
                        "search_string_06": {
                            "match": "*term*",
                            "match_mapping_type": "string",
                            "mapping": {
                            "analyzer": "whitespace_analyzer",
                            "type": "text",
                            "similarity": "pro_tfidf"
                            }
                        }
                    },
                    {
                        "search_string_07": {
                            "match": "*Date",
                            "match_mapping_type": "string",
                            "mapping": {
                            "type": "date",
                            "format": "yyyyMMddHHmmssSSS"
                            }
                        }
                    },
                    {
                        "search_string_08": {
                            "match": "*User",
                            "match_mapping_type": "string",
                            "mapping": {
                            "analyzer": "whitespace_analyzer",
                            "type": "text"
                            }
                        }
                    }
                ]
            }
        }
        
        
    def classify_template(self):
        return {
            "index_patterns": [
                "$*_classify_*"
            ],
            "settings": {
                "index.mapping.ignore_malformed": True,
                "index": {
                "number_of_shards": "5",
                "auto_expand_replicas": "0-1",
                "analysis": {
                    "analyzer": {
                        "whitespace_analyzer": {
                            "filter": [
                            "lowercase",
                            "trim"
                            ],
                            "tokenizer": "my_whitespace"
                        }
                    },
                    "tokenizer": {
                        "my_whitespace": {
                            "type": "whitespace",
                            "max_token_length": "60"
                        }
                    }
                },
                "similarity": {
                    "pro_tfidf": {
                    "type": "scripted",
                    "script": {
                        "source": "double norm = (doc.freq); return query.boost  *norm;"
                    }
                    }
                }
                },
                "index.mapping.total_fields.limit": 99999999
            },
            "mappings": {
                "dynamic_templates": [
                    {
                        "search_string_01": {
                            "match": "id",
                            "match_mapping_type": "string",
                            "mapping": {
                            "analyzer": "whitespace_analyzer",
                            "type": "text"
                            }
                        }
                    },
                    {
                        "search_string_02": {
                            "match": "term",
                            "match_mapping_type": "string",
                            "mapping": {
                            "analyzer": "whitespace_analyzer",
                            "type": "text"
                            }
                        }
                    },
                    {
                        "search_string_03": {
                            "match": "*site*",
                            "match_mapping_type": "string",
                            "mapping": {
                            "analyzer": "whitespace_analyzer",
                            "type": "text"
                            }
                        }
                    },
                    {
                        "search_string_04": {
                            "match": "dm_*",
                            "match_mapping_type": "double",
                            "mapping": {
                            "type": "float"
                            }
                        }
                    },
                    {
                        "search_string_05": {
                            "match": "*No",
                            "match_mapping_type": "long",
                            "mapping": {
                            "type": "long"
                            }
                        }
                    },
                    {
                        "search_string_06": {
                            "match": "*version*",
                            "match_mapping_type": "long",
                            "mapping": {
                            "type": "long"
                            }
                        }
                    },
                    {
                        "search_string_07": {
                            "match": "*_date",
                            "match_mapping_type": "string",
                            "mapping": {
                            "type": "date",
                            "format": "yyyyMMddHHmmssSSS"
                            }
                        }
                    }
                ]
            }
        }
        
    def rule_template(self):
        return {
            "index_patterns": [
                "$*_rule_*"
            ],
            "settings": {
                "index.mapping.ignore_malformed": True,
                "index": {
                "number_of_shards": "5",
                "auto_expand_replicas": "0-1",
                "analysis": {
                    "analyzer": {
                        "whitespace_analyzer": {
                            "filter": [
                            "lowercase",
                            "trim"
                            ],
                            "tokenizer": "my_whitespace"
                        },
                        "pattern_analyzer" : {
                            "filter" : [
                                "lowercase"
                            ],
                            "tokenizer" : "my_pattern"
                        }
                    },
                    "tokenizer": {
                        "my_whitespace": {
                            "type": "whitespace",
                            "max_token_length": "60"
                        },
                        "my_pattern" : {
                            "pattern" : [
                                ",",
                                "whitespace"
                            ],
                            "type" : "pattern"
                        }
                    }
                },
                "similarity": {
                    "pro_tfidf": {
                    "type": "scripted",
                    "script": {
                        "source": "double norm = (doc.freq); return query.boost  *norm;"
                    }
                    }
                }
                },
                "index.mapping.total_fields.limit": 99999999
            },
            "mappings": {
                "dynamic_templates": [
                    {
                        "search_string_01": {
                            "match": "id",
                            "match_mapping_type": "string",
                            "mapping": {
                            "analyzer": "whitespace_analyzer",
                            "type": "text"
                            }
                        }
                    },
                    {
                        "search_string_02": {
                            "match": "rule",
                            "match_mapping_type": "string",
                            "mapping": {
                            "analyzer": "pattern_analyzer",
                            "type": "text",
                            "fields" : {
                                    "keyword" : {
                                    "type" : "text",
                                    "analyzer" : "whitespace_analyzer"
                                    }
                                }
                            }
                        }
                    },
                    {
                        "search_string_03": {
                            "match": "*site*",
                            "match_mapping_type": "string",
                            "mapping": {
                            "analyzer": "whitespace_analyzer",
                            "type": "text"
                            }
                        }
                    },
                    {
                        "search_string_04": {
                            "match": "*categoryNm*",
                            "match_mapping_type": "string",
                            "mapping": {
                            "analyzer": "whitespace_analyzer",
                            "type": "text",
                            "similarity": "pro_tfidf"
                            }
                        }
                    },
                    {
                        "search_string_05": {
                            "match": "*No",
                            "match_mapping_type": "long",
                            "mapping": {
                            "type": "long"
                            }
                        }
                    },
                    {
                        "search_string_06": {
                            "match": "*version*",
                            "match_mapping_type": "long",
                            "mapping": {
                            "type": "long"
                            }
                        }
                    },
                    {
                        "search_string_07": {
                            "match": "*_date",
                            "match_mapping_type": "string",
                            "mapping": {
                            "type": "date",
                            "format": "yyyyMMddHHmmssSSS"
                            }
                        }
                    }
                ]
            }
        }
        
    def entity_template(self):
        return {
            "index_patterns": [
                "$*_entity_*"
            ],
            "settings": {
                "index.mapping.ignore_malformed": True,
                "index": {
                "number_of_shards": "5",
                "auto_expand_replicas": "0-1",
                "analysis": {
                    "analyzer": {
                        "whitespace_analyzer": {
                            "filter": [
                            "lowercase",
                            "trim"
                            ],
                            "tokenizer": "my_whitespace"
                        },
                        "pattern_analyzer" : {
                            "filter" : [
                                "lowercase"
                            ],
                            "tokenizer" : "my_pattern"
                        }
                    },
                    "tokenizer": {
                        "my_whitespace": {
                            "type": "whitespace",
                            "max_token_length": "60"
                        },
                        "my_pattern" : {
                            "pattern" : [
                                ",",
                                "whitespace"
                            ],
                            "type" : "pattern"
                        }
                    }
                },
                "similarity": {
                    "pro_tfidf": {
                    "type": "scripted",
                    "script": {
                        "source": "double norm = (doc.freq); return query.boost  *norm;"
                    }
                    }
                }
                },
                "index.mapping.total_fields.limit": 99999999
            },
            "mappings": {
                "dynamic_templates": [
                    {
                        "search_string_01": {
                            "match": "id",
                            "match_mapping_type": "string",
                            "mapping": {
                            "analyzer": "whitespace_analyzer",
                            "type": "text"
                            }
                        }
                    },
                    {
                        "search_string_02": {
                            "match": "entry",
                            "match_mapping_type": "string",
                            "mapping": {
                            "analyzer": "pattern_analyzer",
                            "type": "text",
                            "fields" : {
                                    "keyword" : {
                                    "type" : "text",
                                    "analyzer" : "whitespace_analyzer"
                                    }
                                }
                            }
                        }
                    },
                    {
                        "search_string_03": {
                            "match": "*site*",
                            "match_mapping_type": "string",
                            "mapping": {
                            "analyzer": "whitespace_analyzer",
                            "type": "text"
                            }
                        }
                    },
                    {
                        "search_string_04": {
                            "match": "*entity*",
                            "match_mapping_type": "string",
                            "mapping": {
                            "analyzer": "whitespace_analyzer",
                            "type": "text",
                            "similarity": "pro_tfidf"
                            }
                        }
                    },
                    {
                        "search_string_05": {
                            "match": "*No",
                            "match_mapping_type": "long",
                            "mapping": {
                            "type": "long"
                            }
                        }
                    },
                    {
                        "search_string_06": {
                            "match": "*version*",
                            "match_mapping_type": "long",
                            "mapping": {
                            "type": "long"
                            }
                        }
                    },
                    {
                        "search_string_07": {
                            "match": "*_date",
                            "match_mapping_type": "string",
                            "mapping": {
                            "type": "date",
                            "format": "yyyyMMddHHmmssSSS"
                            }
                        }
                    }
                ]
            }
        }

#es = elastic_util('192.168.0.5', '6251')
#print(es.countBySearch('@prochat_dic', ''))
#print(es.question_query_to_vector("1",'안녕'))