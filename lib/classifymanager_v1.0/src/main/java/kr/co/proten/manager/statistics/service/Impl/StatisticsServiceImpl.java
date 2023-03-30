package kr.co.proten.manager.statistics.service.Impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kr.co.proten.manager.common.elastic.ElasticSearchConstant;
import kr.co.proten.manager.common.elastic.ElasticSearchIndex;
import kr.co.proten.manager.common.repository.ElasticSearchSelectRepository;
import kr.co.proten.manager.common.util.DateUtil;
import kr.co.proten.manager.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

	private static final Logger log = LoggerFactory.getLogger(StatisticsServiceImpl.class);
	
	private final ElasticSearchSelectRepository elasticSearchSelectRepository;

	@Override
	public Map<String, List<Map<String, Object>>> selectStatisticsDataList(Map<String, Object> paramMap) throws Exception {

		Map<String, List<Map<String, Object>>> resultAggregationListMap = null;
		
		List<Map<String,Object>> matchList = new ArrayList<Map<String,Object>>(); //filter Option
		List<Map<String,Object>> aggregationList = new ArrayList<>();
		List<Map<String,Object>> subAggregationList = new ArrayList<>();
		Map<String,Object> aggregationMap = null;
		Map<String,Object> subAggregationMap = null;
		Map<String,Object> match = null;
		if(paramMap.get("siteNo") != null) {
			match = new HashMap<String,Object>();
			match.put("type", "match");
			match.put("key", "siteNo");
			match.put("value", paramMap.get("siteNo"));
			matchList.add(match);
		}
		// 기간
		String startDate = paramMap.get("startDate") == null ? DateUtil.getCurrentDate() : ((String) paramMap.get("startDate")).replaceAll("-","") + "000000000";
		String endDate = paramMap.get("endDate") == null ? DateUtil.getCurrentDate() : ((String) paramMap.get("endDate")).replaceAll("-","") + "235959999";
		match = new HashMap<String,Object>();
		match.put("type", "range");
		match.put("key", "createDate");
		match.put("from", startDate);
		match.put("to", endDate);
		matchList.add(match);
		
		/*
		 * aggregationList map key
		 * type             (dateHistogram, terms)
		 * aggregationKey   (dateHistogram, terms)
		 * aggregationField (dateHistogram, terms)
		 * size             (terms)
		 * intervalType     (dateHistogram)
		 * dateFormat       (dateHistogram)
		 */
		// stdType : 통계 기준 (hour | day | month | category)
		String stdType = (String) paramMap.get("stdType");
		if(stdType.equals("category")) {
			aggregationMap = new HashMap<String, Object>();
			aggregationMap.put("type", "terms");
			aggregationMap.put("aggregationKey", ElasticSearchConstant.AGGREGATION_MATCHED_CATEGORY);
			aggregationMap.put("aggregationField", "matchedCategory");
			aggregationMap.put("size", 1000);
			aggregationList.add(aggregationMap);
			
			aggregationMap = new HashMap<String, Object>();
			aggregationMap.put("type", "terms");
			aggregationMap.put("aggregationKey", ElasticSearchConstant.AGGREGATION_RESULT_TYPE);
			aggregationMap.put("aggregationField", "resultType");
			aggregationMap.put("size", 5);
			aggregationList.add(aggregationMap);
			aggregationMap = new HashMap<String, Object>();
			aggregationMap.put("type", "terms");
			aggregationMap.put("aggregationKey", ElasticSearchConstant.AGGREGATION_MATCHED_TYPE);
			aggregationMap.put("aggregationField", "matchedType");
			aggregationMap.put("size", 5);
			aggregationList.add(aggregationMap);
			
		} else {
			aggregationMap = new HashMap<String, Object>();
			aggregationMap.put("type", "dateHistogram");
			aggregationMap.put("aggregationKey", ElasticSearchConstant.AGGREGATION_DATE);
			aggregationMap.put("aggregationField", "createDate");
			if(stdType.equals("hour")) {
				aggregationMap.put("intervalType", stdType);
				aggregationMap.put("dateFormat", "yyyyMMddHH");
			} else if(stdType.equals("day")) {
				aggregationMap.put("intervalType", stdType);
				aggregationMap.put("dateFormat", "yyyyMMdd");
			} else if(stdType.equals("month")) {
				aggregationMap.put("intervalType", stdType);
				aggregationMap.put("dateFormat", "yyyyMM");
			}
			aggregationList.add(aggregationMap);
			subAggregationMap = new HashMap<String, Object>(); 
			subAggregationMap.put("type", "terms");
			subAggregationMap.put("aggregationKey", ElasticSearchConstant.SUB_AGGREGATION_RESULT_TYPE);
			subAggregationMap.put("parentAggregationKey", "date_group_by");
			subAggregationMap.put("aggregationField", "resultType");
			subAggregationMap.put("size", 5);
			subAggregationList.add(subAggregationMap);
			subAggregationMap = new HashMap<String, Object>(); 
			subAggregationMap.put("type", "terms");
			subAggregationMap.put("aggregationKey", ElasticSearchConstant.SUB_AGGREGATION_MATCHED_TYPE);
			subAggregationMap.put("parentAggregationKey", "date_group_by");
			subAggregationMap.put("aggregationField", "matchedType");
			subAggregationMap.put("size", 5);
			subAggregationList.add(subAggregationMap);
			
			aggregationMap = new HashMap<String, Object>();
			aggregationMap.put("type", "terms");
			aggregationMap.put("aggregationKey", ElasticSearchConstant.AGGREGATION_RESULT_TYPE);
			aggregationMap.put("aggregationField", "resultType");
			aggregationMap.put("size", 5);
			aggregationList.add(aggregationMap);
			aggregationMap = new HashMap<String, Object>();
			aggregationMap.put("type", "terms");
			aggregationMap.put("aggregationKey", ElasticSearchConstant.AGGREGATION_MATCHED_TYPE);
			aggregationMap.put("aggregationField", "matchedType");
			aggregationMap.put("size", 5);
			aggregationList.add(aggregationMap);
			
		}
		resultAggregationListMap = elasticSearchSelectRepository.selectAggregationByBoolQuery(ElasticSearchIndex.INDEX_NAME_CLASSIFY_HISTORY, matchList, aggregationList, subAggregationList);
		return resultAggregationListMap;
	}

	@Override
	public List<Map<String, Object>> generateTrendList(String stdField, String startDt, String endDt) throws Exception {
		List<Map<String, Object>> trendList = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date startDate = sdf.parse(startDt);
		Date endDate = sdf.parse(endDt);
		Calendar startCal = Calendar.getInstance();
		Calendar endCal = Calendar.getInstance();
		startCal.setTime(startDate);
		endCal.setTime(endDate);
		endCal.add(Calendar.DATE, 1);
		
		String y = "";
		String m = "";
		String d = "";
		String h = "";
		String value = "";
		String viewValue = "";
		
		Map<String, Object> trendMap = null;
		while(startCal.before(endCal)) {
			y = String.valueOf(startCal.get(Calendar.YEAR));
			m = DateUtil.lpadDate(startCal.get(Calendar.MONTH) + 1);
			d = DateUtil.lpadDate(startCal.get(Calendar.DAY_OF_MONTH));
			h = DateUtil.lpadDate(startCal.get(Calendar.HOUR_OF_DAY));
			if(stdField.equals("hour")) {
				value = y+m+d+h;
				viewValue = d + "일 " + h + "시";
				startCal.add(Calendar.HOUR_OF_DAY, 1);
			} else if(stdField.equals("day")) {
				value = y+m+d;
				viewValue = m + "월 " + d + "일";
				startCal.add(Calendar.DAY_OF_MONTH, 1);
			} else if(stdField.equals("month")) {
				value = y+m;
				viewValue = y + "년 " + m + "월";
				startCal.add(Calendar.MONTH, 1);
			}
			trendMap = new HashMap<>();
			trendMap.put("stdField", stdField);
			trendMap.put("stdValue", value);
			trendMap.put("stdViewValue", viewValue);
			trendList.add(trendMap);
		}		
		return trendList;
	}

	@Override
	public void mergeTrendListValue(List<Map<String, Object>> trendList, List<Map<String, Object>> resultListMap) throws Exception {
		String stdValue = "";
		boolean matched = false;
		for(Map<String, Object> dataMap : trendList) {
			stdValue = (String) dataMap.get("stdValue");
			matched = false;
			
			for(Map<String, Object> _resultMap : resultListMap) {
				if(_resultMap.containsKey(stdValue)) {
					double docCnt = Double.parseDouble(String.valueOf(_resultMap.get(stdValue)));
					double matchedCnt = _resultMap.containsKey("matched") ? Double.parseDouble(String.valueOf(_resultMap.get("matched"))) : (double) 0;
					double matchedRate = (matchedCnt / docCnt) * 100;
					int intMatchedRate = (int) matchedRate;
					
					dataMap.put("trend", _resultMap.get(stdValue));
					dataMap.put("matched", _resultMap.containsKey("matched") ? _resultMap.get("matched") : 0);
					dataMap.put("not_matched", _resultMap.containsKey("not_matched") ? _resultMap.get("not_matched") : 0);
					dataMap.put("rule", _resultMap.containsKey("rule") ? _resultMap.get("rule") : 0);
					dataMap.put("classify", _resultMap.containsKey("classify") ? _resultMap.get("classify") : 0);
					dataMap.put("matched_rate", intMatchedRate);
					matched = true;
				}
			}
			
			if(!matched) {
				dataMap.put("trend", 0);
				dataMap.put("matched", 0);
				dataMap.put("not_matched", 0);
				dataMap.put("rule", 0);
				dataMap.put("classify", 0);
				dataMap.put("matched_rate", 0);
			}
		}
	}

	@Override
	public void convertTermsValue(Map<String, Object> resultMap, List<Map<String, Object>> resultListMap) throws Exception {
		for(Map<String, Object> _resultMap : resultListMap) {
			resultMap.putAll(_resultMap);
		}
	}

	@Override
	public void convertCategoryValue(List<Map<String, Object>> resultList, List<Map<String, Object>> resultListMap) throws Exception {
		Map<String, Object> resultMap = null;
		
		for(Map<String, Object> _resultMap : resultListMap) {
			for (Map.Entry<String, Object> entry : _resultMap.entrySet()) {
				resultMap = new HashMap<>();
				resultMap.put("categoryName", entry.getKey());
				resultMap.put("count", entry.getValue());
				resultList.add(resultMap);
			}
		}
	}	
}
