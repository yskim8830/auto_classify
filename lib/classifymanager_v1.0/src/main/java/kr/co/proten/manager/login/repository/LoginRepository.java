package kr.co.proten.manager.login.repository;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Repository;

import kr.co.proten.manager.common.elastic.ElasticSearchIndex;
import kr.co.proten.manager.common.repository.ElasticSearchCommon;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LoginRepository extends ElasticSearchCommon {
	
	private static final Logger log = LoggerFactory.getLogger(LoginRepository.class);

	private final RestHighLevelClient restHighLevelClient;
	
	/**
	 * 그룹 매핑 정보 조회
	 * @param groupNo
	 * @return
	 * @throws Exception
	 */
	public List<Integer> selectGroupMappingSiteList(String groupNo) throws Exception {
		int from = 0;
		int listCount = 100;
		
		SearchRequest searchRequest = new SearchRequest(ElasticSearchIndex.INDEX_NAME_GROUP_MAPPING.getIndexName());
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		
		MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("groupNo", groupNo);
		
		sourceBuilder.query(matchQueryBuilder);
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		sourceBuilder.sort("mappingNo", SortOrder.ASC);
		sourceBuilder.size(listCount);
		sourceBuilder.from(from);
		
		searchRequest.source(sourceBuilder);

		SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		SearchHit[] hits = searchResponse.getHits().getHits();

		List<Integer> resultList = Arrays.stream(hits).map((searchHit) -> {
			String _no = String.valueOf(searchHit.getSourceAsMap().get("siteNo"));
			return Integer.parseInt(_no);
		}).collect(Collectors.toList());
		
		return resultList;
	}    
	
	/**
	 * 사이트 매핑 정보 조회
	 * @param siteNo
	 * @return
	 * @throws Exception
	 */
	public List<Integer> selectUserMappingMenuList(String siteNo) throws Exception {
		int from = 0;
		int listCount = 100;
		
		SearchRequest searchRequest = new SearchRequest(ElasticSearchIndex.INDEX_NAME_SITE_MAPPING.getIndexName());
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		
		MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("siteNo", siteNo);
		
		sourceBuilder.query(matchQueryBuilder);
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		sourceBuilder.sort("menuId", SortOrder.ASC);
		sourceBuilder.size(listCount);
		sourceBuilder.from(from);
		
		searchRequest.source(sourceBuilder);

		SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		SearchHit[] hits = searchResponse.getHits().getHits();

		List<Integer> resultList = Arrays.stream(hits).map((searchHit) -> {
			String _no = String.valueOf(searchHit.getSourceAsMap().get("menuId"));
			return Integer.parseInt(_no);
		}).collect(Collectors.toList());
		
		return resultList;
	}
}
