package kr.co.proten.manager.learning.service.Impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import kr.co.proten.manager.common.ClassifyManagerConstant;
import kr.co.proten.manager.common.elastic.ElasticSearchConstant;
import kr.co.proten.manager.common.elastic.ElasticSearchIndex;
import kr.co.proten.manager.common.repository.ElasticSearchSelectRepository;
import kr.co.proten.manager.common.service.RestTemplateService;
import kr.co.proten.manager.common.util.GsonUtil;
import kr.co.proten.manager.common.util.StringUtil;
import kr.co.proten.manager.learning.service.ModelingService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ModelingServiceImpl implements ModelingService {
	
	private static final Logger log = LoggerFactory.getLogger(ModelingServiceImpl.class);
	
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
	public List<Map<String, Object>> modelingList(Map<String, Object> paramMap) throws Exception {
    	List<Map<String, Object>> resultList = null;
		
		int from = 0;
		int rownum = 3;
		String sortType = "service,version";
		String sortOrder = ElasticSearchConstant.DESC+","+ElasticSearchConstant.DESC;
		
		StringBuilder queryString = new StringBuilder();
		if(!StringUtil.nvl(paramMap.get("siteNo")).equals("")) {
			queryString.append( " siteNo:"+paramMap.get("siteNo"));
		}
		
		if(!StringUtil.nvl(paramMap.get("state")).equals("")) {
			if(queryString.length()!=0) {
				queryString.append(" AND ");
			}
			queryString.append(" state:"+paramMap.get("state"));
		}
		
		resultList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_LEARNING_LOG, from, rownum, sortType, sortOrder, queryString.toString());
		
		return resultList;
	}

	@Override
	public List<Map<String, Object>> learningList(Map<String, Object> paramMap) throws Exception {
		List<Map<String, Object>> resultList = null;
		
		int from = Integer.parseInt(StringUtil.nvl(paramMap.get("start"), "0"));
		int rownum = Integer.parseInt(StringUtil.nvl(paramMap.get("lineNo"),"10"));
		String sortType = StringUtil.nvl(paramMap.get("sort"),"createDate");
		String sortOrder = ElasticSearchConstant.DESC;
		
		StringBuilder queryString = new StringBuilder();
		if(!StringUtil.nvl(paramMap.get("siteNo")).equals("")) {
			queryString.append( " siteNo:"+paramMap.get("siteNo"));
		}
		resultList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_LEARNING_LOG, from, rownum, sortType, sortOrder, queryString.toString());
		
		return resultList;
	}

	@Override
	public String requestStartTraining(String siteNo) throws Exception {
		GsonBuilder gsonBuilder = new GsonBuilder();  
		gsonBuilder.serializeNulls();  
		Gson gson = gsonBuilder.create();
		
		HttpHeaders header = null;
		UriComponents builder = null;
		
		StringBuffer requestUrl = new StringBuffer();
		String requestBody = "";
		JsonObject requestObj = new JsonObject();
		// 파라미터 세팅
		requestObj.addProperty("siteNo", siteNo);
		requestObj.addProperty("esUrl", ELASTICSEARCH_IP+":"+ELASTICSEARCH_PORT);
		requestObj.addProperty("userId", "promanager");
		
		requestBody = gson.toJson(requestObj);
		
		requestUrl.append(TRAINING_SCHEME);
		requestUrl.append("://");
		requestUrl.append(TRAINING_IP).append(":").append(TRAINING_PORT);
		requestUrl.append(ClassifyManagerConstant.START_TRAINING_API_URL);
		
		log.debug("[ModelingServiceImpl][requestStartModeling] requestUrl : {}", requestUrl.toString());
		log.debug("[ModelingServiceImpl][requestStartModeling] requestBody : {}", requestBody);
		
		builder = UriComponentsBuilder.fromHttpUrl(requestUrl.toString()).encode().build();
		header = restTemplateService.getTrainingApiHeader();
		String result = restTemplateService.httpPost(builder.toUriString(), header, requestBody, String.class).getBody();
		log.debug("[ModelingServiceImpl][requestStartModeling] result : {}", result);
		return result;
	}

	@Override
	public String requestStatusTraining(String siteNo) throws Exception {
		GsonBuilder gsonBuilder = new GsonBuilder();  
		gsonBuilder.serializeNulls();  
		Gson gson = gsonBuilder.create();
		
		HttpHeaders header = null;
		UriComponents builder = null;
		
		StringBuffer requestUrl = new StringBuffer();
		String requestBody = "";
		JsonObject requestObj = new JsonObject();
		// 파라미터 세팅
		requestObj.addProperty("siteNo", siteNo);
		requestObj.addProperty("esUrl", ELASTICSEARCH_IP+":"+ELASTICSEARCH_PORT);
		
		requestBody = gson.toJson(requestObj);
		
		requestUrl.append(TRAINING_SCHEME);
		requestUrl.append("://");
		requestUrl.append(TRAINING_IP).append(":").append(TRAINING_PORT);
		requestUrl.append(ClassifyManagerConstant.STATUS_TRAINING_API_URL);
		
		log.debug("[ModelingServiceImpl][requestStatusModeling] requestUrl : {}", requestUrl.toString());
		log.debug("[ModelingServiceImpl][requestStatusModeling] requestBody : {}", requestBody);
		
		builder = UriComponentsBuilder.fromHttpUrl(requestUrl.toString()).encode().build();
		header = restTemplateService.getTrainingApiHeader();
		String result = restTemplateService.httpPost(builder.toUriString(), header, requestBody, String.class).getBody();
		log.debug("[ModelingServiceImpl][requestStatusModeling] result : {}", result);
		return result;
	}

	@Override
	public String requestStopTraining(String siteNo) throws Exception {
		GsonBuilder gsonBuilder = new GsonBuilder();  
		gsonBuilder.serializeNulls();  
		Gson gson = gsonBuilder.create();
		
		HttpHeaders header = null;
		UriComponents builder = null;
		
		StringBuffer requestUrl = new StringBuffer();
		String requestBody = "";
		JsonObject requestObj = new JsonObject();
		// 파라미터 세팅
		requestObj.addProperty("siteNo", siteNo);
		
		requestBody = gson.toJson(requestObj);
		
		requestUrl.append(TRAINING_SCHEME);
		requestUrl.append("://");
		requestUrl.append(TRAINING_IP).append(":").append(TRAINING_PORT);
		requestUrl.append(ClassifyManagerConstant.STOP_TRAINING_API_URL);
		
		log.debug("[ModelingServiceImpl][requestStopModeling] requestUrl : {}", requestUrl.toString());
		log.debug("[ModelingServiceImpl][requestStopModeling] requestBody : {}", requestBody);
		
		builder = UriComponentsBuilder.fromHttpUrl(requestUrl.toString()).encode().build();
		header = restTemplateService.getTrainingApiHeader();
		String result = restTemplateService.httpPost(builder.toUriString(), header, requestBody, String.class).getBody();
		log.debug("[ModelingServiceImpl][requestStopModeling] result : {}", result);
		return result;
	}

	@Override
	public String requestDistModelToService(String siteNo, String version) throws Exception {
		GsonBuilder gsonBuilder = new GsonBuilder();  
		gsonBuilder.serializeNulls();  
		Gson gson = gsonBuilder.create();
		
		HttpHeaders header = null;
		UriComponents builder = null;
		
		StringBuffer requestUrl = new StringBuffer();
		String requestBody = "";
		JsonObject requestObj = new JsonObject();
		// 파라미터 세팅
		requestObj.addProperty("siteNo", siteNo);
		requestObj.addProperty("version", version);
		requestObj.addProperty("esUrl", ELASTICSEARCH_IP+":"+ELASTICSEARCH_PORT);
		requestObj.addProperty("userId", "promanager");
		
		requestBody = gson.toJson(requestObj);
		
		requestUrl.append(TRAINING_SCHEME);
		requestUrl.append("://");
		requestUrl.append(TRAINING_IP).append(":").append(TRAINING_PORT);
		requestUrl.append(ClassifyManagerConstant.DIST_MODEL_TO_SERVICE_API_URL);
		
		log.debug("[ModelingServiceImpl][requestDistModeling] requestUrl : {}", requestUrl.toString());
		log.debug("[ModelingServiceImpl][requestDistModeling] requestBody : {}", requestBody);
		
		builder = UriComponentsBuilder.fromHttpUrl(requestUrl.toString()).encode().build();
		header = restTemplateService.getTrainingApiHeader();
		String result = restTemplateService.httpPost(builder.toUriString(), header, requestBody, String.class).getBody();
		log.debug("[ModelingServiceImpl][requestDistModeling] result : {}", result);
		return result;
	}

}
