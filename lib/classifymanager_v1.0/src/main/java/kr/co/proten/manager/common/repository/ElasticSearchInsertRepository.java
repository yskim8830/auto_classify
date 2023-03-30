package kr.co.proten.manager.common.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kr.co.proten.manager.common.elastic.ElasticSearchIndex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ElasticSearchInsertRepository extends ElasticSearchCommon {
	
	private static final Logger log = LoggerFactory.getLogger(ElasticSearchInsertRepository.class);

    private final RestHighLevelClient restHighLevelClient;
    
    private final int FETCH_COUNT = 10000;
    
    /**
     * 데이터 저장
     * @param index
     * @param _id
     * @param numberTypeFields
     * @param dataMap
     * @return
     * @throws IOException
     */
    public boolean insertData(ElasticSearchIndex elasticSearchIndex, String _id, Map<String, Object> dataMap) throws IOException {
    	log.debug("[ElasticSearchInsertRepository] [insertData] index : {}", elasticSearchIndex.getIndexName());
		log.debug("[ElasticSearchInsertRepository] [insertData] sourceBuilder : {}", dataMap.toString());
		GsonBuilder gsonBuilder = new GsonBuilder();  
		gsonBuilder.serializeNulls();  
		Gson gson = gsonBuilder.create();
		
		IndexRequest request = new IndexRequest(elasticSearchIndex.getIndexName());
		setIntegerMapValue(elasticSearchIndex.getNumberTypeFields(), dataMap);
		request.id(_id);
        request.timeout("1s");
        request.setRefreshPolicy(RefreshPolicy.IMMEDIATE);
        request.source(gson.toJson(dataMap), XContentType.JSON);
        IndexResponse response = restHighLevelClient.index(request,RequestOptions.DEFAULT);
        return response.getShardInfo().getSuccessful() > 0 ? true : false;
	}
    
    /**
     * 데이터 벌크 저장
     * @param index
     * @param dataMapList
     * @return
     * @throws IOException
     */
    public boolean insertBulkData(ElasticSearchIndex elasticSearchIndex, List<Map<String, Object>> dataMapList) throws IOException {
    	
		List<Map<String, Object>> fetchMapList = new ArrayList<>();
		Map<String, Object> dataMap = null;
		boolean result = true; 
		
		for(int i=0; i<dataMapList.size(); i++) {
			dataMap = dataMapList.get(i);
			fetchMapList.add(dataMap);
			if(i > 0 && i % FETCH_COUNT == 0) {
				result = loopBulkExecutor(elasticSearchIndex, fetchMapList);
				fetchMapList.clear();
				fetchMapList = new ArrayList<>();
				if(!result) {
					log.error("[ElasticSearchInsertRepository] [insertBulkData] {} bulk insert fail!!", elasticSearchIndex.getIndexName());
					return result;
				}
			} 
		}
		if(fetchMapList.size() > 0) {
			result = loopBulkExecutor(elasticSearchIndex, fetchMapList);
			fetchMapList.clear();
		}
		log.info("[ElasticSearchInsertRepository] [insertBulkData] complete!!");
		return result;
    }
    
    /**
     * 패치 카운트 벌크 저장
     * @param elasticSearchIndex
     * @param dataMapList
     * @return
     * @throws IOException
     */
    private boolean loopBulkExecutor(ElasticSearchIndex elasticSearchIndex, List<Map<String, Object>> dataMapList) throws IOException {
    	GsonBuilder gsonBuilder = new GsonBuilder();  
		gsonBuilder.serializeNulls();  
		Gson gson = gsonBuilder.create();
		
		// BulkProcessor
		BulkRequest bulkRequest = new BulkRequest();
		IndexRequest indexRequest = null;

		for(Map<String, Object> dataMap : dataMapList) {
			indexRequest = new IndexRequest(elasticSearchIndex.getIndexName());
			indexRequest.id(String.valueOf(dataMap.get("_id")));
			dataMap.remove("_id");
			indexRequest.source(gson.toJson(dataMap), XContentType.JSON);
			bulkRequest.add(indexRequest);
		}
		
		BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
		
		if(bulkResponse.hasFailures()) {
			for(BulkItemResponse bulkItemResponse : bulkResponse) {
				if(bulkItemResponse.isFailed()) {
					BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
					log.error(failure.toString());
	            }
	        }
			return false;
		} else {
			return true;
		}
    }
}
