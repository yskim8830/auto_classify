package kr.co.proten.manager.common.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Repository;

import kr.co.proten.manager.common.elastic.ElasticSearchConstant;
import kr.co.proten.manager.common.elastic.ElasticSearchIndex;
import kr.co.proten.manager.common.util.GsonUtil;
import kr.co.proten.manager.common.util.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ElasticSearchSelectRepository extends ElasticSearchCommon {
	
	private static final Logger log = LoggerFactory.getLogger(ElasticSearchSelectRepository.class);

    private final RestHighLevelClient restHighLevelClient;
    
    /**
     * 헬스체크
     * @return
     */
    public boolean healthCheck() {
    	boolean result = true;
    	try {
			result = restHighLevelClient.ping(RequestOptions.DEFAULT);
		} catch (IOException e) {
			result = false;
		}    	
    	return result;
    }
    
    /**
     * 엘라스틱 노드 조회
     * @return
     */
    public Map<String,Object> getCatNodes() {
    	Map<String,Object> resultMap = new HashMap<>();
    	Response response = null;
    	int status = 0;
    	try {
    		response = restHighLevelClient.getLowLevelClient().performRequest(new Request("GET", "/_cat/nodes?format=json"));
    		status = response.getStatusLine().getStatusCode();
    		
    		if(status == 200) {
    			String responseBody = EntityUtils.toString(response.getEntity());
    			resultMap.put("result", GsonUtil.toListHashMap(responseBody));
    			resultMap.put("success", true);
    			resultMap.put("msg", "");
    		} else {
    			resultMap.put("success", false);
    			resultMap.put("msg", "Error : Response Error Code [" + status + "]" );
    		}
		} catch (IOException e) {
			resultMap.put("success", false);
			resultMap.put("msg", "Error : Response Error Code [" + status + "]" );
			log.error("[ElasticSearchSelectRepository] [getCatNodes] {}", e.getMessage());
		}
    	return resultMap;
    }
    
    /**
     * 엘라스틱 디스크 조회
     * @return
     */
    public Map<String,Object> getCatAllocation() {
    	Map<String,Object> resultMap = new HashMap<>();
    	Response response = null;
    	int status = 0;
    	try {
    		response = restHighLevelClient.getLowLevelClient().performRequest(new Request("GET", "/_cat/allocation?format=json"));
    		status = response.getStatusLine().getStatusCode();
    		
    		if(status == 200) {
    			String responseBody = EntityUtils.toString(response.getEntity());
    			resultMap.put("result", GsonUtil.toListHashMap(responseBody));
    			resultMap.put("success", true);
    			resultMap.put("msg", "");
    		} else {
    			resultMap.put("success", false);
    			resultMap.put("msg", "Error : Response Error Code [" + status + "]" );
    		}
		} catch (IOException e) {
			resultMap.put("success", false);
			resultMap.put("msg", "Error : Response Error Code [" + status + "]" );
			log.error("[ElasticSearchSelectRepository] [getCatNodes] {}", e.getMessage());
		}
    	return resultMap;
    }
    
    
    
    /**
     * 키 조회
     * @param indexName
     * @param keyName
     * @return
     * @throws Exception
     */
    public int selectMaxId(ElasticSearchIndex elasticSearchIndex, String keyName) throws Exception {
    	int id = 0;
    	SearchRequest searchRequest = new SearchRequest(elasticSearchIndex.getIndexName());
    	SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    	
    	sourceBuilder.sort(keyName, SortOrder.DESC);
    	sourceBuilder.size(1);
    	
    	log.debug("[ElasticSearchSelectRepository] [selectMaxId] index : {}", elasticSearchIndex.getIndexName());
		log.debug("[ElasticSearchSelectRepository] [selectMaxId] sourceBuilder : {}", sourceBuilder.toString());
		
		searchRequest.source(sourceBuilder);
    	
    	SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		SearchHit[] hits = searchResponse.getHits().getHits();

		Optional<SearchHit> searchHit = Arrays.stream(hits).findFirst();
		
		Map<String,Object> dataMap = null;
		if(searchHit.isPresent()) {
			dataMap = searchHit.get().getSourceAsMap();
			id = (int) dataMap.get(keyName);
		} 
		id++;
    	
    	return id;
    }
	
    /**
     * 키 기반 데이터 조회
     * @param indexName
     * @param keyName
     * @param keyValue
     * @return
     * @throws Exception
     */
	public Map<String,Object> selectDataById(ElasticSearchIndex elasticSearchIndex, String keyName, String keyValue) throws Exception {
		
		int from = 0;
		
		SearchRequest searchRequest = new SearchRequest(elasticSearchIndex.getIndexName());
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		
		MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(keyName, keyValue);
		
		sourceBuilder.query(matchQueryBuilder);
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		sourceBuilder.sort(ElasticSearchConstant.SCORE_SORT_FIELD, SortOrder.DESC);
		sourceBuilder.size(1);
		sourceBuilder.from(from);
		
		log.debug("[ElasticSearchSelectRepository] [selectDataById] index : {}", elasticSearchIndex.getIndexName());
		log.debug("[ElasticSearchSelectRepository] [selectDataById] sourceBuilder : {}", sourceBuilder.toString());
		
		searchRequest.source(sourceBuilder);

		SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		SearchHit[] hits = searchResponse.getHits().getHits();

		Optional<SearchHit> searchHit = Arrays.stream(hits).findFirst();
		
		Map<String,Object> dataMap = null;
		if(searchHit.isPresent()) {
			dataMap = searchHit.get().getSourceAsMap();
		} 

		return dataMap;
	}
	
	/**
	 * 데이터 카운트 조회
	 * @param indexName
	 * @param keyName
	 * @param keyValue
	 * @return
	 * @throws Exception
	 */
	public long selectDataCountById(ElasticSearchIndex elasticSearchIndex, String keyName, String keyValue) throws Exception {
		
		int from = 0;
		
		SearchRequest searchRequest = new SearchRequest(elasticSearchIndex.getIndexName());
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		
		MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(keyName, keyValue);
		
		sourceBuilder.query(matchQueryBuilder);
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		sourceBuilder.sort(ElasticSearchConstant.SCORE_SORT_FIELD, SortOrder.DESC);
		sourceBuilder.size(1);
		sourceBuilder.from(from);
		
		log.debug("[ElasticSearchSelectRepository] [selectDataCountById] index : {}", elasticSearchIndex.getIndexName());
		log.debug("[ElasticSearchSelectRepository] [selectDataCountById] sourceBuilder : {}", sourceBuilder.toString());
		
		searchRequest.source(sourceBuilder);

		SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		long totalCount = searchResponse.getHits().getTotalHits().value;

		return totalCount;
	}
	
	/**
	 * 데이터 ID 조회 - boolQuery
	 * @param index
	 * @param boolQueryList
	 * @return
	 * @throws IOException
	 */
	public String selectDataIdByBoolQuery(ElasticSearchIndex elasticSearchIndex, List<Map<String, Object>> boolQueryList) throws IOException {
		
		String id = "";
		SearchRequest searchRequest = new SearchRequest(elasticSearchIndex.getIndexName());
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		
		if(boolQueryList != null && boolQueryList.size() != 0) {
			setBoolQueryList(boolQuery, boolQueryList);
		}
		
		sourceBuilder.query(boolQuery);
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		sourceBuilder.sort(ElasticSearchConstant.SCORE_SORT_FIELD, SortOrder.DESC);
		sourceBuilder.size(1);
		sourceBuilder.from(0);
		
		log.debug("[ElasticSearchSelectRepository] [selectDataIdByBoolQuery] index : {}", elasticSearchIndex.getIndexName());
		log.debug("[ElasticSearchSelectRepository] [selectDataIdByBoolQuery] sourceBuilder : {}", sourceBuilder.toString());
		
		searchRequest.source(sourceBuilder);

		SearchResponse searchResponse = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);		
		int length = searchResponse.getHits().getHits().length;
		if(length > 0) {
			id = searchResponse.getHits().getHits()[0].getId();
		}
		
		return id;
	}	
	
	/**
	 * 데이터 카운트 조회 - boolQuery
	 * @param index
	 * @param boolQueryList
	 * @return
	 * @throws IOException
	 */
	public long selectDataCountByBoolQuery(ElasticSearchIndex elasticSearchIndex, List<Map<String, Object>> boolQueryList) throws IOException {
		
		SearchRequest searchRequest = new SearchRequest(elasticSearchIndex.getIndexName());
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		
		if(boolQueryList != null && boolQueryList.size() != 0) {
			setBoolQueryList(boolQuery, boolQueryList);
		}
		
		sourceBuilder.query(boolQuery);
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		sourceBuilder.sort(ElasticSearchConstant.SCORE_SORT_FIELD, SortOrder.DESC);
		sourceBuilder.size(1);
		sourceBuilder.from(0);
		
		log.debug("[ElasticSearchSelectRepository] [selectDataCountByBoolQuery] index : {}", elasticSearchIndex.getIndexName());
		log.debug("[ElasticSearchSelectRepository] [selectDataCountByBoolQuery] sourceBuilder : {}", sourceBuilder.toString());
		
		searchRequest.source(sourceBuilder);
		
		SearchResponse searchResponse = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);		
		long totalCount = searchResponse.getHits().getTotalHits().value;
		
		return totalCount;
	}	
	
	/**
	 * 데이터 리스트 조회 - boolQuery
	 * @param index
	 * @param start
	 * @param listCount
	 * @param boolQueryList
	 * @return
	 * @throws IOException
	 */
	public List<Map<String, Object>> selectDataListByBoolQuery(ElasticSearchIndex elasticSearchIndex, int start, int listCount, List<Map<String,Object>> boolQueryList) throws IOException {
		return selectDataListByAllCondition(elasticSearchIndex, start, listCount, ElasticSearchConstant.EMPTY_STRING, ElasticSearchConstant.EMPTY_STRING, ElasticSearchConstant.SCORE_SORT_FIELD, ElasticSearchConstant.DESC, boolQueryList, ElasticSearchConstant.EMPTY_STRING);
	}
	
	/**
	 * 데이터 리스트 조회 - boolQuery
	 * @param index
	 * @param start
	 * @param listCount
	 * @param sort
	 * @param sortOrder
	 * @param boolQueryList
	 * @return
	 * @throws IOException
	 */
	public List<Map<String, Object>> selectDataListByBoolQuery(ElasticSearchIndex elasticSearchIndex, int start, int listCount, String sort, String sortOrder, List<Map<String,Object>> boolQueryList) throws IOException {
		return selectDataListByAllCondition(elasticSearchIndex, start, listCount, ElasticSearchConstant.EMPTY_STRING, ElasticSearchConstant.EMPTY_STRING, sort, sortOrder, boolQueryList, ElasticSearchConstant.EMPTY_STRING);
	}
	
	/**
	 * 데이터 리스트 조회 - boolQuery
	 * @param index
	 * @param start
	 * @param listCount
	 * @param searchFields
	 * @param query
	 * @param sort
	 * @param sortOrder
	 * @param boolQueryList
	 * @return
	 * @throws IOException
	 */
	public List<Map<String, Object>> selectDataListByBoolQuery(ElasticSearchIndex elasticSearchIndex, int start, int listCount, String searchFields, String query, String sort, String sortOrder, List<Map<String,Object>> boolQueryList) throws IOException {
		return selectDataListByAllCondition(elasticSearchIndex, start, listCount, searchFields, query, sort, sortOrder, boolQueryList, ElasticSearchConstant.EMPTY_STRING);
	}
	
	/**
	 * 데이터 리스트 조회 - queryString
	 * @param index
	 * @param start
	 * @param listCount
	 * @param searchFields
	 * @param query
	 * @param sort
	 * @param sortOrder
	 * @param filterQuery
	 * @return
	 * @throws IOException
	 */
	public List<Map<String, Object>> selectDataListByQueryString(ElasticSearchIndex elasticSearchIndex, int start, int listCount, String searchFields, String query, String sort, String sortOrder, String filterQuery) throws IOException {
		return selectDataListByAllCondition(elasticSearchIndex, start, listCount, searchFields, query, sort, sortOrder, null, filterQuery);
	}
	
	/**
	 * 데이터 리스트 조회 - queryString
	 * @param index
	 * @param start
	 * @param listCount
	 * @param sort
	 * @param sortOrder
	 * @param filterQuery
	 * @return
	 * @throws IOException
	 */
	public List<Map<String, Object>> selectDataListByQueryString(ElasticSearchIndex elasticSearchIndex, int start, int listCount, String sort, String sortOrder, String filterQuery) throws IOException {
		return selectDataListByAllCondition(elasticSearchIndex, start, listCount, ElasticSearchConstant.EMPTY_STRING, ElasticSearchConstant.EMPTY_STRING, sort, sortOrder, null, filterQuery);
	}
	
	/**
	 * 데이터 리스트 조회
	 * @param index
	 * @param start
	 * @param listCount
	 * @param searchFields
	 * @param query
	 * @param sort
	 * @param sortOrder
	 * @param filterQuery
	 * @return
	 * @throws IOException
	 */
	public List<Map<String, Object>> selectDataListByAllCondition(ElasticSearchIndex elasticSearchIndex, int start, int listCount,
			String searchFields, String query, String sort, String sortOrder, List<Map<String,Object>> boolQueryList, String filterQuery) throws IOException {
		
		SearchRequest searchRequest = new SearchRequest(elasticSearchIndex.getIndexName());
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		BoolQueryBuilder searchQuery = new BoolQueryBuilder();
		
		if("".equals(query)) {
			searchQuery.must(QueryBuilders.matchAllQuery());
		} else {
			List<QueryBuilder> listQuery = new ArrayList<QueryBuilder>();
			QueryBuilder common = setSimpleQueryBuilder(searchFields, query, 1);
			if(common != null) {
				listQuery.add(common);
			}
			for(QueryBuilder qBuild : listQuery) {
				searchQuery.should(qBuild);
			}
		}
		boolQuery.must(searchQuery);
		if(!"".equals(filterQuery)) {
			boolQuery.must(QueryBuilders.queryStringQuery(filterQuery));
		}
		if(boolQueryList != null && boolQueryList.size() != 0) {
			setBoolQueryList(boolQuery, boolQueryList);
		}
		
		sourceBuilder.query(boolQuery);
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		setSortBuilder(sourceBuilder, sort, sortOrder);
		sourceBuilder.size(listCount);
		sourceBuilder.from(start);
		
		log.debug("[ElasticSearchSelectRepository] [selectDataListByAllCondition] index : {}", elasticSearchIndex.getIndexName());
		log.debug("[ElasticSearchSelectRepository] [selectDataListByAllCondition] sourceBuilder : {}", sourceBuilder.toString());
		searchRequest.source(sourceBuilder);

		SearchResponse searchResponse = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
		SearchHit[] hits = searchResponse.getHits().getHits();
		
		long totalCount = searchResponse.getHits().getTotalHits().value;
		
		List<Map<String, Object>> resultList = Arrays.stream(hits).map((searchHit) -> {
			Map<String, Object> hit = (Map<String, Object>) searchHit.getSourceAsMap();
			hit.put("totalCount", totalCount);
			return hit;
		}).collect(Collectors.toList());
		
		return resultList;
	}
	
	/**
	 * 데이터 집계 조회
	 * @param elasticSearchIndex
	 * @param boolQueryList
	 * @param aggregationList
	 * @param subAggregationList
	 * @return
	 * @throws IOException
	 */
	public Map<String, List<Map<String, Object>>> selectAggregationByBoolQuery(ElasticSearchIndex elasticSearchIndex, List<Map<String,Object>> boolQueryList, List<Map<String,Object>> aggregationList, List<Map<String,Object>> subAggregationList) throws IOException {
		
		SearchRequest searchRequest = new SearchRequest(elasticSearchIndex.getIndexName());
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		if(boolQueryList != null && boolQueryList.size() != 0) {
			setBoolQueryList(boolQuery, boolQueryList);
		}
		
		sourceBuilder.query(boolQuery);
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		setSortBuilder(sourceBuilder, ElasticSearchConstant.SCORE_SORT_FIELD, ElasticSearchConstant.DESC);
		sourceBuilder.size(1);
		sourceBuilder.from(0);
		
		setAggregationBuilder(sourceBuilder, aggregationList, subAggregationList);		
		log.debug("[ElasticSearchSelectRepository] [selectAggregationByBoolQuery] index : {}", elasticSearchIndex.getIndexName());
		log.debug("[ElasticSearchSelectRepository] [selectAggregationByBoolQuery] sourceBuilder : {}", sourceBuilder.toString());
		searchRequest.source(sourceBuilder);

		SearchResponse searchResponse = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
		Map<String, List<Map<String, Object>>> resultAggregationMap = convertBucketToListMap(searchResponse, aggregationList, subAggregationList);
		
		return resultAggregationMap;
	}
	
	/**
	 * 데이터 집계 조회
	 * @param elasticSearchIndex
	 * @param boolQueryList
	 * @param aggregationList
	 * @param subAggregationList
	 * @return
	 * @throws IOException
	 */
	public Map<String, List<Map<String, Object>>> selectSystemAggregationByBoolQuery(ElasticSearchIndex elasticSearchIndex, List<Map<String,Object>> boolQueryList, List<Map<String,Object>> aggregationList, List<Map<String,Object>> subAggregationList) throws IOException {
		
		SearchRequest searchRequest = new SearchRequest(elasticSearchIndex.getIndexName());
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		if(boolQueryList != null && boolQueryList.size() != 0) {
			setBoolQueryList(boolQuery, boolQueryList);
		}
		
		sourceBuilder.query(boolQuery);
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		setSortBuilder(sourceBuilder, ElasticSearchConstant.SCORE_SORT_FIELD, ElasticSearchConstant.DESC);
		sourceBuilder.size(1);
		sourceBuilder.from(0);
		
		setAggregationBuilder(sourceBuilder, aggregationList, subAggregationList);
		searchRequest.source(sourceBuilder);

		SearchResponse searchResponse = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
		Map<String, List<Map<String, Object>>> resultAggregationMap = convertBucketToListMap(searchResponse, aggregationList, subAggregationList);
		
		return resultAggregationMap;
	}
}
