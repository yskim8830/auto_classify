package kr.co.proten.manager.common.repository;

import java.io.IOException;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.springframework.stereotype.Repository;

import kr.co.proten.manager.common.elastic.ElasticSearchIndex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ElasticSearchDeleteRepository extends ElasticSearchCommon {
	
	private static final Logger log = LoggerFactory.getLogger(ElasticSearchDeleteRepository.class);

    private final RestHighLevelClient restHighLevelClient;
    
    /**
     * 데이터 삭제 - 쿼리 기반
     * @param index
     * @param dataValue
     * @return
     * @throws IOException
     */
    public boolean deleteDataByQuery(ElasticSearchIndex elasticSearchIndex, String dataValue) throws IOException {
    	DeleteByQueryRequest request = new DeleteByQueryRequest(elasticSearchIndex.getIndexName());
    	
    	BoolQueryBuilder searchQuery = new BoolQueryBuilder();
		String [] data = dataValue.split(",");
		for(String s : data) {
			String [] value = s.split(":");
			if(value.length > 1) {
				MatchQueryBuilder match = QueryBuilders.matchQuery(value[0], value[1]);
				searchQuery.should(match);
			}
		}
		log.debug("[ElasticSearchDeleteRepository] [deleteDataByQuery] index : {}", elasticSearchIndex.getIndexName());
		log.debug("[ElasticSearchDeleteRepository] [deleteDataByQuery] searchQuery : {}", searchQuery.toString());
		request.setQuery(searchQuery);
		request.setRefresh(true);
    	
    	BulkByScrollResponse response = restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
    	if(response.getBulkFailures().size() > 0) {
    		response.getBulkFailures().stream().forEach(f -> {
    			log.error("[ElasticSearchDeleteRepository] [deleteDataByQuery] bulk fail message :: {}", f.getMessage());
    		});
    		return false;
    	}
    	return true;
    }
    
    /**
     * 데이터 삭제 - ID 기반
     * @param index
     * @param id
     * @return
     * @throws IOException
     */
    public boolean deleteDataById(ElasticSearchIndex elasticSearchIndex, String id) throws IOException{
    	log.debug("[ElasticSearchDeleteRepository] [deleteDataById] index : {}", elasticSearchIndex.getIndexName());
		log.debug("[ElasticSearchDeleteRepository] [deleteDataById] id : {}", id);
        DeleteRequest request = new DeleteRequest(elasticSearchIndex.getIndexName(), id);
        request.timeout("1s");
        request.setRefreshPolicy(RefreshPolicy.IMMEDIATE);
        DeleteResponse deleteResponse = restHighLevelClient.delete(request,RequestOptions.DEFAULT);
        return deleteResponse.getShardInfo().getSuccessful() > 0;
    }
}
