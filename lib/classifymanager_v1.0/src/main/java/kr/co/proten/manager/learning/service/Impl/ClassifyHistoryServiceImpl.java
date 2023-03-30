package kr.co.proten.manager.learning.service.Impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import kr.co.proten.manager.common.elastic.ElasticSearchConstant;
import kr.co.proten.manager.common.elastic.ElasticSearchIndex;
import kr.co.proten.manager.common.repository.ElasticSearchSelectRepository;
import kr.co.proten.manager.common.util.StringUtil;
import kr.co.proten.manager.learning.service.ClassifyHistoryService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClassifyHistoryServiceImpl implements ClassifyHistoryService {
	
	private static final Logger log = LoggerFactory.getLogger(ClassifyHistoryServiceImpl.class);
	
	private final ElasticSearchSelectRepository elasticSearchSelectRepository;
	
	@Override
	public List<Map<String, Object>> selectHistoryList(Map<String, Object> paramMap) throws Exception {
		
		List<Map<String, Object>> resultList = null;
		List<Map<String, Object>> customResultList = null;
		int from = Integer.parseInt(StringUtil.nvl(paramMap.get("start"), "0"));
		int rownum = Integer.parseInt(StringUtil.nvl(paramMap.get("lineNo"),"10"));
		String sortType = StringUtil.nvl((String)paramMap.get("sort"),"createDate");
		String sortOrder = ElasticSearchConstant.DESC;
		
		StringBuilder queryString = new StringBuilder();
		if(!StringUtil.nvl(paramMap.get("siteNo")).equals("")) {
			queryString.append( " siteNo:"+paramMap.get("siteNo"));
		} 
		if(!StringUtil.nvl(paramMap.get("resultType")).equals("")) {
			if(queryString.length()!=0) {
				queryString.append(" AND ");
			}
			queryString.append( " resultType:"+paramMap.get("resultType"));
		}
		if(!StringUtil.nvl(paramMap.get("matchedType")).equals("")) {
			if(queryString.length()!=0) {
				queryString.append(" AND ");
			}
			queryString.append( " matchedType:"+paramMap.get("matchedType"));
		}
		
		resultList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_CLASSIFY_HISTORY, from, rownum, sortType, sortOrder, queryString.toString());
		
		final int maxLength = 100;
		Gson gson = new Gson();
		customResultList = resultList.stream().map(m -> {
			String orgSentence = StringUtil.nvl(m.get("orgSentence"), "");
			if(orgSentence.length() > maxLength) {
				orgSentence = orgSentence.substring(0, 100) + "...";				
			}
			m.put("convertDoc", orgSentence);
			
			String resultType = StringUtil.nvl(m.get("resultType"), "");
			String matchedType = StringUtil.nvl(m.get("matchedType"), "");
			JsonArray array = null;
			
			if(resultType.equals("matched")) {
				if(matchedType.equals("rule")) {
					String ruleResult = StringUtil.nvl(m.get("ruleResult"), "");
					ruleResult = StringEscapeUtils.unescapeHtml3(ruleResult);
					array = gson.fromJson(ruleResult, JsonArray.class);
					String matchedCategoryStr = "";
					matchedCategoryStr = array.get(0).getAsJsonObject().get("fullItem").getAsString();
					if(array.size() > 1) {
						matchedCategoryStr = matchedCategoryStr + " 외 " + (array.size() - 1) + "건";
					}
					m.put("matchedCategory", matchedCategoryStr);		
					m.put("analResult", gson.toJson(array));	
				} else if(matchedType.equals("classify")) {
					String classifyResult = StringUtil.nvl(m.get("classifyResult"), "");
					classifyResult = StringEscapeUtils.unescapeHtml3(classifyResult);
					array = gson.fromJson(classifyResult, JsonArray.class); 
					m.put("analResult", gson.toJson(array));	
				}
			} else if(resultType.equals("error")) {
				JsonObject errorObj = new JsonObject();
				errorObj.addProperty("error", StringUtil.nvl(m.get("failedMessage"), ""));
				m.put("analResult", gson.toJson(errorObj));
			}
			
			return m;
		}).collect(Collectors.toList());
		return customResultList;
	}
}
