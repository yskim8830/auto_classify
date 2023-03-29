package kr.co.proten.manager.main.service.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kr.co.proten.manager.common.elastic.ElasticSearchConstant;
import kr.co.proten.manager.common.elastic.ElasticSearchIndex;
import kr.co.proten.manager.common.repository.ElasticSearchSelectRepository;
import kr.co.proten.manager.common.util.StringUtil;
import kr.co.proten.manager.login.model.LoginModel;
import kr.co.proten.manager.login.repository.LoginRepository;
import kr.co.proten.manager.main.service.MainService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

	private static final Logger log = LoggerFactory.getLogger(MainServiceImpl.class);
	
	private final ElasticSearchSelectRepository elasticSearchSelectRepository;
	
	private final LoginRepository loginRepository;
	
	@Override
	public void modifyUserSiteInfo(LoginModel loginModel, int siteNo) throws Exception {
		/** 로그인 후 아이디별 권한설정 **/
		if(loginModel != null) {
			loginModel.setSiteNo(siteNo);
			List<Map<String, Object>> menuList = elasticSearchSelectRepository.selectDataListByBoolQuery(ElasticSearchIndex.INDEX_NAME_MENU, 0, 100, null);
			List<Integer> mappingList = loginRepository.selectUserMappingMenuList(String.valueOf(siteNo));
			List<Map<String, Object>> menuAuthList = new ArrayList<>();
			for(Map<String, Object> result : menuList) {
				int id = (int) result.get("menuId");
				result.put("auth", 0);
				result.put("userNo", loginModel.getUserNo());

				if ( mappingList.contains(id) ) {
					menuAuthList.add(result);
				}
			}
			menuAuthList = StringUtil.mapComparator(menuAuthList,"orderSeq");
			loginModel.setMenuList(menuAuthList);
		}
	}

	@Override
	public Map<String,Object> getCatNodes() throws Exception {
		return elasticSearchSelectRepository.getCatNodes();
	}

	@Override
	public Map<String,Object> getCatAllocation() throws Exception {
		return elasticSearchSelectRepository.getCatAllocation();
	}

	@Override
	public Map<String, List<Map<String, Object>>> selectStatisticsDataList(String siteNo, String startDate, String endDate, String stdType) throws Exception {
		
		Map<String, List<Map<String, Object>>> resultAggregationListMap = null;
		
		List<Map<String,Object>> matchList = new ArrayList<Map<String,Object>>(); //filter Option
		List<Map<String,Object>> aggregationList = new ArrayList<>();
		List<Map<String,Object>> subAggregationList = new ArrayList<>();
		Map<String,Object> aggregationMap = null;
		Map<String,Object> subAggregationMap = null;
		Map<String,Object> match = null;
		
		match = new HashMap<String,Object>();
		match.put("type", "match");
		match.put("key", "siteNo");
		match.put("value", siteNo);
		matchList.add(match);
		
		// 기간
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
			aggregationMap.put("intervalType", stdType);
			aggregationMap.put("dateFormat", "yyyyMMdd");
			
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
}
