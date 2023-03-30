package kr.co.proten.manager.common.schedule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import kr.co.proten.manager.common.elastic.ElasticSearchConstant;
import kr.co.proten.manager.common.elastic.ElasticSearchIndex;
import kr.co.proten.manager.common.repository.ElasticSearchSelectRepository;
import kr.co.proten.manager.common.util.DateUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrafficPublisherService {

	private static final Logger log = LoggerFactory.getLogger(TrafficPublisherService.class);
	
	private final SimpMessageSendingOperations messagingTemplate;
	
	private final ElasticSearchSelectRepository elasticSearchSelectRepository;	
	
	private String executeTime;
	private String lastExecuteTime;
	
	@Scheduled(fixedDelay=1000)	// 1000(1초)
	public void trafficPublisher() {
		
		// 마지막 실행시간 ~ 현재까지 트래픽 사이트별 갯수
		executeTime = DateUtil.getCurrentDateTimeMille();
		
		if(lastExecuteTime == null || lastExecuteTime.equals("")) {
			lastExecuteTime = executeTime;
		}
				
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			log.error("Exception TrafficPublish Sleep Error " + e.getMessage(), e);
		}
		
		Map<String, List<Map<String, Object>>> resultAggregationListMap = null;
		
		List<Map<String,Object>> aggregationList = new ArrayList<>();
		List<Map<String,Object>> subAggregationList = new ArrayList<>();
		Map<String,Object> aggregationMap = null;

		aggregationMap = new HashMap<String, Object>();
		aggregationMap.put("type", "terms");
		aggregationMap.put("aggregationKey", ElasticSearchConstant.AGGREGATION_TRAFFIC);
		aggregationMap.put("aggregationField", "siteNo");
		aggregationMap.put("size", 1000);
		aggregationList.add(aggregationMap);
		
		
		List<Map<String,Object>> matchList = new ArrayList<Map<String,Object>>(); //filter Option
		HashMap<String, Object> match = new HashMap<String, Object>();
		match.put("type", "range");
		match.put("key", "createDate");
		match.put("from", lastExecuteTime);
		//match.put("from", "20230101000000000");
		match.put("to", executeTime);
		matchList.add(match);
		
		try {
			resultAggregationListMap = elasticSearchSelectRepository.selectSystemAggregationByBoolQuery(ElasticSearchIndex.INDEX_NAME_CLASSIFY_HISTORY, matchList, aggregationList, subAggregationList);
			List<Map<String, Object>> resultListMap = resultAggregationListMap.get(ElasticSearchConstant.AGGREGATION_TRAFFIC);
			for(Map<String, Object> resultMap : resultListMap) {
				for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
					publishTrafficInfo(entry.getKey(), (long) entry.getValue());
				}
			}
			if(resultListMap.size() > 0) {
				lastExecuteTime = executeTime;			
			}
		} catch (IOException e) {
			log.error("[TrafficPublisherService] [trafficPublisher] {}", e.getMessage());
		}
	}
	
	/**
	 * 웹소켓 publish traffic
	 * @param siteNo
	 * @param count
	 */
	private void publishTrafficInfo(String siteNo, long count) {
		int resultCount = (int) count;
		int maxCount = 100;
		if(resultCount > maxCount) {
			resultCount = maxCount;
		}
		for(int i=0; i<resultCount; i++) {
			messagingTemplate.convertAndSend("/topic/" + siteNo, "TRAFFIC");
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				log.error("Exception TrafficPublish Sleep Error " + e.getMessage(), e);
			}
		}
	}
	
	
}