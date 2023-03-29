package kr.co.proten.manager.common.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

import kr.co.proten.manager.common.elastic.ElasticSearchConstant;
import kr.co.proten.manager.common.util.StringUtil;

public abstract class ElasticSearchCommon {

	/**
	 * 쿼리 생성
	 * @param fields
	 * @param query
	 * @param andor
	 * @return
	 */
	protected QueryBuilder setSimpleQueryBuilder(String fields, String query, int andor) {
		Map<String, Float> fieldInfo = new HashMap<String, Float>();
		if("".equals(fields)) {
			return null;
		}
		String[] arField = StringUtil.split(fields,",");
		for(String af:arField) {
			if(af.indexOf("/")>-1) {
				String _field[]=StringUtil.split(af,"/");
				float boost = 1.0f;
				try {
					boost = Float.parseFloat(_field[1]);
				}catch(Exception ex) {
					boost = 1.0f;
				}

				fieldInfo.put(_field[0], boost);
			}else {
				fieldInfo.put(af, 1.0f);
			}
		}
		Operator op = Operator.AND;
		if(andor == 0) {
			op = Operator.OR;
		}
		QueryBuilder queryBuilder = QueryBuilders.simpleQueryStringQuery(query).fields(fieldInfo).defaultOperator(op);
		return queryBuilder;
	}
	
	/**
	 * 숫자형 필드 변환
	 * @param numberTypeFields
	 * @param requestMap
	 */
	protected void setIntegerMapValue(String numberTypeFields, Map<String, Object> requestMap) {
		if(!numberTypeFields.equals("")) { 
			for(String field : numberTypeFields.split(",")) {
				if (requestMap.containsKey(field)) {
					Object _obj = requestMap.get(field);
					if(_obj instanceof String) {
						String value = StringUtil.nvl((String)requestMap.get(field),"0");
						if(value.indexOf(".") >= 0) {
							requestMap.replace(field, Long.parseLong(value));		
						} else {
							requestMap.replace(field, Integer.parseInt(value));							
						}
					}
				}
			}			
		}
	}
	
	/**
	 * 정렬 설정
	 * @param sourceBuilder
	 * @param sort
	 * @param sortOrder
	 */
	protected void setSortBuilder(SearchSourceBuilder sourceBuilder, String sort, String sortOrder) {
		try {
			if(!sort.equals("")) {
				String sorts[]=StringUtil.split(sort, ",");
				String sortOrders[]=StringUtil.split(sortOrder, ",");
				for(int idx  =0 ; idx < sorts.length;idx++) {
					String _sort =  sorts[idx];
					String _sortOrder =  sortOrders[idx];
					if("".equals(_sort)) {
		 				continue;
					} 
					if("rank".equals(_sort)) {
						sourceBuilder.sort(ElasticSearchConstant.SCORE_SORT_FIELD, SortOrder.DESC);
					} else {
						if(_sortOrder.equalsIgnoreCase(ElasticSearchConstant.DESC)) {
							sourceBuilder.sort(_sort, SortOrder.DESC);
						} else {
							sourceBuilder.sort(_sort, SortOrder.ASC);
						}
					}	
				}		
			} else {
				sourceBuilder.sort(ElasticSearchConstant.SCORE_SORT_FIELD, SortOrder.DESC);
			}
		} catch(Exception e) {
			sourceBuilder.sort(ElasticSearchConstant.SCORE_SORT_FIELD, SortOrder.DESC);
		}
	}
	
	/**
	 * 시간 집계 기준
	 * @param period
	 * @return
	 */
	protected DateHistogramInterval getHistogramInterval(String period) {
		DateHistogramInterval interval = DateHistogramInterval.DAY;
	    if(period.equals("month")) {
	    	interval = DateHistogramInterval.MONTH;
	    }else if(period.equals("week")) {
	    	interval = DateHistogramInterval.WEEK;
	    }else if(period.equals("hour")) {
	    	interval = DateHistogramInterval.HOUR;
	    }else if(period.equals("day")) {
	    	interval =DateHistogramInterval.DAY;
	    }
	    return interval;
	}
	
	/**
	 * bool query 설정
	 * @param boolQuery
	 * @param boolQueryList
	 */
	protected void setBoolQueryList(BoolQueryBuilder boolQuery, List<Map<String,Object>> boolQueryList) {
		String type = "";
		String key = "";
		Object value = null;
		
		for(Map<String,Object> condition : boolQueryList) {
			type = (String) condition.get("type");
			if(type.equals("match")) {
				key = (String) condition.get("key");
				value = condition.get("value");
				boolQuery.must().add(QueryBuilders.termQuery(key, value));
			} else if(type.equals("not")) {
				key = (String) condition.get("key");
				value = condition.get("value");
				boolQuery.mustNot().add(QueryBuilders.termQuery(key, value));
			} else if(type.equals("range")) {
				key = (String) condition.get("key");
				String from = (String) condition.get("from");
				String to = (String) condition.get("to");
				boolQuery.must().add(QueryBuilders.rangeQuery(key).gte(from).lte(to));
			}
		}
	}
	
	/**
	 * 집계 빌더 설정
	 * @param sourceBuilder
	 * @param aggregationList
	 * @param subAggregationList
	 */
	protected void setAggregationBuilder(SearchSourceBuilder sourceBuilder, List<Map<String,Object>> aggregationList, List<Map<String,Object>> subAggregationList) {
		/*
		 * aggregationList map key
		 * type             (dateHistogram, terms)
		 * aggregationKey   (dateHistogram, terms)
		 * aggregationField (dateHistogram, terms)
		 * size             (terms)
		 * intervalType     (dateHistogram)
		 * dateFormat       (dateHistogram)
		 */
		Map<String, TermsAggregationBuilder> termsAggregationBuilderMap = new HashMap<>();
		Map<String, DateHistogramAggregationBuilder> dateHistogramAggregationBuilderMap = new HashMap<>();
		String type = "";
		String aggregationKey = "";
		String aggregationField = "";
		String intervalType = "";
		String dateFormat = "";
		int size = 0;
		if(aggregationList != null) {
			for(Map<String,Object> aggregationMap : aggregationList) {
				type = (String) aggregationMap.get("type");
				if(type.equals("dateHistogram")) {
					aggregationKey = (String) aggregationMap.get("aggregationKey");
					aggregationField = (String) aggregationMap.get("aggregationField");
					intervalType = (String) aggregationMap.get("intervalType");
					dateFormat = (String) aggregationMap.get("dateFormat");
					dateHistogramAggregationBuilderMap.put(aggregationKey, AggregationBuilders.dateHistogram(aggregationKey).field(aggregationField).calendarInterval(getHistogramInterval(intervalType)).format(dateFormat));
				} else if(type.equals("terms")) {
					aggregationKey = (String) aggregationMap.get("aggregationKey");
					aggregationField = (String) aggregationMap.get("aggregationField");
					size = (int) aggregationMap.get("size");
					termsAggregationBuilderMap.put(aggregationKey, AggregationBuilders.terms(aggregationKey).field(aggregationField).size(size));
				}
			}			
		}
		
		/*
		 * subAggregationList map key
		 * type             	(dateHistogram, terms)
		 * parentAggregationKey (dateHistogram, terms)
		 * aggregationKey   	(dateHistogram, terms)
		 * aggregationField 	(dateHistogram, terms)
		 * size             	(terms)
		 * intervalType     	(dateHistogram)
		 * dateFormat       	(dateHistogram)
		 */
		String parentAggregationKey = "";
		if(subAggregationList != null) {
			for(Map<String,Object> subAggregationMap : subAggregationList) {
				type = (String) subAggregationMap.get("type");
				parentAggregationKey = (String) subAggregationMap.get("parentAggregationKey");
				if(type.equals("dateHistogram")) {
					aggregationKey = (String) subAggregationMap.get("aggregationKey");
					aggregationField = (String) subAggregationMap.get("aggregationField");
					intervalType = (String) subAggregationMap.get("intervalType");
					dateFormat = (String) subAggregationMap.get("dateFormat");
					if(dateHistogramAggregationBuilderMap.containsKey(parentAggregationKey)) {
						dateHistogramAggregationBuilderMap.get(parentAggregationKey).subAggregation(AggregationBuilders.dateHistogram(aggregationKey).field(aggregationField).calendarInterval(getHistogramInterval(intervalType)).format(dateFormat));
					} else if(termsAggregationBuilderMap.containsKey(parentAggregationKey)) {
						termsAggregationBuilderMap.get(parentAggregationKey).subAggregation(AggregationBuilders.dateHistogram(aggregationKey).field(aggregationField).calendarInterval(getHistogramInterval(intervalType)).format(dateFormat));
					}
					
				} else if(type.equals("terms")) {
					aggregationKey = (String) subAggregationMap.get("aggregationKey");
					aggregationField = (String) subAggregationMap.get("aggregationField");
					size = (int) subAggregationMap.get("size");
					if(dateHistogramAggregationBuilderMap.containsKey(parentAggregationKey)) {
						dateHistogramAggregationBuilderMap.get(parentAggregationKey).subAggregation(AggregationBuilders.terms(aggregationKey).field(aggregationField).size(size));
					} else if(termsAggregationBuilderMap.containsKey(parentAggregationKey)) {
						termsAggregationBuilderMap.get(parentAggregationKey).subAggregation(AggregationBuilders.terms(aggregationKey).field(aggregationField).size(size));
					}
				}
			}
		}
		
		termsAggregationBuilderMap.forEach((_aggregationKey, _builder) -> {
			sourceBuilder.aggregation(_builder);
		});
		
		dateHistogramAggregationBuilderMap.forEach((_aggregationKey, _builder) -> {
			sourceBuilder.aggregation(_builder);
		});
	}
	
	/**
	 * 집계 결과 처리
	 * @param searchResponse
	 * @param aggregationList
	 * @param subAggregationList
	 * @return
	 */
	protected Map<String, List<Map<String, Object>>> convertBucketToListMap(SearchResponse searchResponse, List<Map<String,Object>> aggregationList, List<Map<String,Object>> subAggregationList) {
		Map<String, List<Map<String, Object>>> resultAggregationListMap = new HashMap<>();
		List<Map<String, Object>> resultAggregationList = null;
		Map<String, Object> resultMap = null;
		
		String type = "";
		String aggregationKey = "";
		for(Map<String,Object> aggregationMap : aggregationList) {
			resultAggregationList = new ArrayList<>(); 
			type = (String) aggregationMap.get("type");
			aggregationKey = (String) aggregationMap.get("aggregationKey");
			if(type.equals("dateHistogram")) {
				ParsedDateHistogram aggs = searchResponse.getAggregations().get(aggregationKey);
				for(org.elasticsearch.search.aggregations.bucket.histogram.Histogram.Bucket bucket : aggs.getBuckets()) {
					if(bucket.getKeyAsString().equals("")) {
						continue;
					}
					resultMap = new HashMap<>();
					resultMap.put(bucket.getKeyAsString(), bucket.getDocCount());
					String _type = "";
					String _aggregationKey = "";
					String _parentAggregationKey = "";
					if(subAggregationList != null) {
						for(Map<String,Object> subAggregationMap : subAggregationList) {
							_type = (String) subAggregationMap.get("type");
							_aggregationKey = (String) subAggregationMap.get("aggregationKey");
							_parentAggregationKey = (String) subAggregationMap.get("parentAggregationKey");
							if(_parentAggregationKey.equals(aggregationKey)) {
								if(_type.equals("dateHistogram")) {
									ParsedDateHistogram _aggs = bucket.getAggregations().get(_aggregationKey);
									for(org.elasticsearch.search.aggregations.bucket.histogram.Histogram.Bucket _bucket : _aggs.getBuckets()) {
										if(_bucket.getKeyAsString().equals("")) {
											continue;
										}
										resultMap.put(_bucket.getKeyAsString(), _bucket.getDocCount());
									}
								} else if(_type.equals("terms")) {
									ParsedTerms _aggs = bucket.getAggregations().get(_aggregationKey);
									for(org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket _bucket : _aggs.getBuckets()) {
										if(_bucket.getKeyAsString().equals("")) {
											continue;
										}
										resultMap.put(_bucket.getKeyAsString(), _bucket.getDocCount());
									}
								}
							}
						}
					}
					resultAggregationList.add(resultMap);
				}
			} else if(type.equals("terms")) {
				ParsedTerms aggs = searchResponse.getAggregations().get(aggregationKey);
				for(org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket bucket : aggs.getBuckets()) {
					if(bucket.getKeyAsString().equals("")) {
						continue;
					}
					resultMap = new HashMap<>();
					resultMap.put(bucket.getKeyAsString(), bucket.getDocCount());
					String _type = "";
					String _aggregationKey = "";
					String _parentAggregationKey = "";
					for(Map<String,Object> subAggregationMap : subAggregationList) {
						_type = (String) subAggregationMap.get("type");
						_aggregationKey = (String) subAggregationMap.get("aggregationKey");
						_parentAggregationKey = (String) subAggregationMap.get("parentAggregationKey");
						if(_parentAggregationKey.equals(aggregationKey)) {
							if(_type.equals("dateHistogram")) {
								ParsedDateHistogram _aggs = bucket.getAggregations().get(_aggregationKey);
								for(org.elasticsearch.search.aggregations.bucket.histogram.Histogram.Bucket _bucket : _aggs.getBuckets()) {
									if(_bucket.getKeyAsString().equals("")) {
										continue;
									}
									resultMap.put(_bucket.getKeyAsString(), _bucket.getDocCount());
								}
							} else if(_type.equals("terms")) {
								ParsedTerms _aggs = bucket.getAggregations().get(_aggregationKey);
								for(org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket _bucket : _aggs.getBuckets()) {
									if(_bucket.getKeyAsString().equals("")) {
										continue;
									}
									resultMap.put(_bucket.getKeyAsString(), _bucket.getDocCount());
								}
							}
						}
					}
					resultAggregationList.add(resultMap);
				}
			}
			
			resultAggregationListMap.put(aggregationKey, resultAggregationList);
		}
		return resultAggregationListMap;
	}
}
