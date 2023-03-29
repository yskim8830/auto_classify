package kr.co.proten.manager.learning.service.Impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import kr.co.proten.manager.common.ClassifyManagerConstant;
import kr.co.proten.manager.common.elastic.ElasticSearchConstant;
import kr.co.proten.manager.common.elastic.ElasticSearchIndex;
import kr.co.proten.manager.common.repository.ElasticSearchSelectRepository;
import kr.co.proten.manager.common.service.RestTemplateService;
import kr.co.proten.manager.common.util.StringUtil;
import kr.co.proten.manager.learning.service.SimulationService;
import kr.co.proten.manager.learning.vo.RequestClassifyParamVo;
import kr.co.proten.manager.learning.vo.ResponseClassifyAnalysisResultVo;
import kr.co.proten.manager.learning.vo.ResponseClassifyRootVo;
import kr.co.proten.manager.learning.vo.ResponseClassifyStatusVo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SimulationServiceImpl implements SimulationService {
	
	private static final Logger log = LoggerFactory.getLogger(SimulationServiceImpl.class);
	
	private final RestTemplateService<String> restTemplateService;
	
	private final ElasticSearchSelectRepository elasticSearchSelectRepository;
	
	@Value("${training.ip}") 
	public String TRAINING_IP;
    @Value("${training.port}") 
    public int TRAINING_PORT;
    @Value("${training.scheme}")
    public String TRAINING_SCHEME;
    
    @Value("${elasticsearch.ip}")
    private String ELASTICSEARCH_IP;
    @Value("${elasticsearch.port}")
    private int ELASTICSEARCH_PORT;
    @Value("${elasticsearch.scheme}")
    private String ELASTICSEARCH_SCHEME;

	@Override
	public String requestSimulationApi(String siteCode, String query, String version) throws Exception {
		GsonBuilder gsonBuilder = new GsonBuilder();  
		gsonBuilder.serializeNulls();  
		Gson gson = gsonBuilder.create();
		
		HttpHeaders header = null;
		UriComponents builder = null;
		
		StringBuffer requestUrl = new StringBuffer();
		String requestBody = "";
		RequestClassifyParamVo paramVo = new RequestClassifyParamVo();
		
		paramVo.setSite(siteCode);
		paramVo.setQuery(query);
		paramVo.setVersion(version);
		paramVo.setEsUrl(ELASTICSEARCH_IP+":"+ELASTICSEARCH_PORT);
		
		requestBody = gson.toJson(paramVo);
		
		requestUrl.append(TRAINING_SCHEME);
		requestUrl.append("://");
		requestUrl.append(TRAINING_IP).append(":").append(TRAINING_PORT);
		requestUrl.append(ClassifyManagerConstant.CLASSIFY_API_URL);
		
		builder = UriComponentsBuilder.fromHttpUrl(requestUrl.toString()).encode().build();
		header = restTemplateService.getTrainingApiHeader();
		String result = restTemplateService.httpPost(builder.toUriString(), header, requestBody, String.class).getBody();
		return result;
	}

	@Override
	public List<Map<String, Object>> selectSimulationSummaryList(Map<String, Object> paramMap) throws Exception {
		List<Map<String, Object>> resultList = null;
		List<Map<String, Object>> customResultList = null;
		
		int from = Integer.parseInt(StringUtil.nvl(paramMap.get("start"), "0"));
		int rownum = Integer.parseInt(StringUtil.nvl(paramMap.get("lineNo"),"5"));
		String sortType = "createDate";
		String sortOrder = ElasticSearchConstant.DESC;
		
		StringBuilder queryString = new StringBuilder();
		if(!StringUtil.nvl(paramMap.get("siteNo")).equals("")) {
			queryString.append( " siteNo:"+paramMap.get("siteNo"));
		} 
		resultList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_SIMULATION_SUMMARY, from, rownum, sortType, sortOrder, queryString.toString());
		
		customResultList = resultList.stream().map(summaryMap -> {
			double dataCnt = Integer.parseInt(StringUtil.nvl(summaryMap.get("dataCnt"), "0"));
			double matchedDataCnt = Integer.parseInt(StringUtil.nvl(summaryMap.get("matchedDataCnt"), "0"));
			double matchedRate = 0;
			if(dataCnt > 0 && matchedDataCnt > 0) {
				matchedRate = (matchedDataCnt / dataCnt) * 100;
				summaryMap.put("matchedRate", String.format("%.1f", matchedRate) + "%");
			} else {
				summaryMap.put("matchedRate", "0%");				
			}
			return summaryMap;
		}).collect(Collectors.toList());
		
		return customResultList;
	}

	@Override
	public List<Map<String, Object>> selectSimulationHistoryList(Map<String, Object> paramMap) throws Exception {
		List<Map<String, Object>> resultList = null;
		List<Map<String, Object>> customResultList = null;
		int from = Integer.parseInt(StringUtil.nvl(paramMap.get("start"), "0"));
		int rownum = Integer.parseInt(StringUtil.nvl(paramMap.get("lineNo"),"10"));
		String sortType = "createDate";
		String sortOrder = ElasticSearchConstant.DESC;
		
		StringBuilder queryString = new StringBuilder();
		if(!StringUtil.nvl(paramMap.get("siteNo")).equals("")) {
			queryString.append( " siteNo:"+paramMap.get("siteNo"));
		} 
		if(!StringUtil.nvl(paramMap.get("taskId")).equals("")) {
			if(queryString.length()!=0) {
				queryString.append(" AND ");
			}
			queryString.append(" taskId:"+paramMap.get("taskId"));
		}
		resultList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_SIMULATION_HISTORY, from, rownum, sortType, sortOrder, queryString.toString());
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

	@Override
	public void convertSimulationApiMessage(ResponseClassifyRootVo vo) throws Exception {
		ResponseClassifyStatusVo status = vo.getStatus();
		if(status.getCode().equals("200")) {
			ResponseClassifyAnalysisResultVo analysisResult = vo.getAnalysisResult();
			String resultType = analysisResult.getResultType();
			if(resultType.equals("matched")) {
				String matchedType = analysisResult.getMatchedType();
				int size = 0;
				String matchedCategoryStr = "";
				if(matchedType.equals("rule")) {
					size = analysisResult.getRuleResult().size();
					matchedCategoryStr = analysisResult.getRuleResult().get(0).getFullItem();
					if(size > 1) {
						matchedCategoryStr = matchedCategoryStr + " 외 " + (size - 1) + "건";
					}
				} else if(matchedType.equals("classify")) {
					size = analysisResult.getClassifyResult().size();
					matchedCategoryStr = analysisResult.getClassifyResult().get(0).getFullItem();
				}
				analysisResult.setMatchedCategory(matchedCategoryStr);
			}
		}
	}

}
