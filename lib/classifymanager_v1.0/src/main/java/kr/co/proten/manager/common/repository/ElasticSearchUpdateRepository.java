package kr.co.proten.manager.common.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Repository;

import kr.co.proten.manager.common.elastic.ElasticSearchIndex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ElasticSearchUpdateRepository extends ElasticSearchCommon {
	
	private static final Logger log = LoggerFactory.getLogger(ElasticSearchUpdateRepository.class);

    private final RestHighLevelClient restHighLevelClient;
    
    private final int FETCH_COUNT = 10000;
    
    /**
     * 데이터 수정
     * @param index
     * @param _id
     * @param numberTypeFields
     * @param dataMap
     * @return
     * @throws IOException
     */
    public boolean updateData(ElasticSearchIndex elasticSearchIndex, String _id, Map<String, Object> dataMap) throws IOException {
    	log.debug("[ElasticSearchUpdateRepository] [updateData] index : {}", elasticSearchIndex.getIndexName());
		log.debug("[ElasticSearchUpdateRepository] [updateData] sourceBuilder : {}", dataMap.toString());
		UpdateRequest request = new UpdateRequest(elasticSearchIndex.getIndexName(), _id);
		setIntegerMapValue(elasticSearchIndex.getNumberTypeFields(), dataMap);
		request.setRefreshPolicy(RefreshPolicy.IMMEDIATE);
        request.doc(dataMap, XContentType.JSON);
        UpdateResponse response = restHighLevelClient.update(request, RequestOptions.DEFAULT);
        return response.getShardInfo().getSuccessful() > 0;
	}
    
    /**
     * 벌크 데이터 수정
     * @param index
     * @param dataMapList
     * @return
     * @throws IOException
     */
    public boolean updateBulkData(ElasticSearchIndex elasticSearchIndex, List<Map<String, Object>> dataMapList) throws IOException {	
    	List<Map<String, Object>> fetchMapList = new ArrayList<>();
		Map<String, Object> dataMap = null;
		boolean result = true; 
		
		for(int i=0; i<dataMapList.size(); i++) {
			dataMap = dataMapList.get(i);
			fetchMapList.add(dataMap);
			if(i % FETCH_COUNT == 0) {
				result = loopBulkExecutor(elasticSearchIndex, fetchMapList);
				fetchMapList.clear();
				fetchMapList = new ArrayList<>();
				if(!result) {
					log.error("[ElasticSearchUpdateRepository] [updateBulkData] {} bulk insert fail!!", elasticSearchIndex.getIndexName());
					return result;
				}
			}
		}
		if(fetchMapList.size() > 0) {
			result = loopBulkExecutor(elasticSearchIndex, fetchMapList);
		}
		log.info("[ElasticSearchUpdateRepository] [updateBulkData] complete!!");
		return result;
    }
    
    /**
     * 패치 카운트 벌크 수정
     * @param elasticSearchIndex
     * @param dataMapList
     * @return
     * @throws IOException
     */
    private boolean loopBulkExecutor(ElasticSearchIndex elasticSearchIndex, List<Map<String, Object>> dataMapList) throws IOException {
    	BulkRequest bulkRequest = new BulkRequest();
		UpdateRequest updateRequest = null;
		
		for(Map<String, Object> dataMap : dataMapList) {
			updateRequest = new UpdateRequest(elasticSearchIndex.getIndexName(), String.valueOf(dataMap.get("_id")));
			dataMap.remove("_id");
			updateRequest.doc(dataMap, XContentType.JSON);
			bulkRequest.add(updateRequest);
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
