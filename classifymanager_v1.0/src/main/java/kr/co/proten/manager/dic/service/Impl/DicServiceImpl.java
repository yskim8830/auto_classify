package kr.co.proten.manager.dic.service.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.buf.StringUtils;
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
import kr.co.proten.manager.common.repository.ElasticSearchDeleteRepository;
import kr.co.proten.manager.common.repository.ElasticSearchInsertRepository;
import kr.co.proten.manager.common.repository.ElasticSearchSelectRepository;
import kr.co.proten.manager.common.repository.ElasticSearchUpdateRepository;
import kr.co.proten.manager.common.service.RestTemplateService;
import kr.co.proten.manager.common.util.DateUtil;
import kr.co.proten.manager.common.util.StringUtil;
import kr.co.proten.manager.dic.service.DicService;
import kr.co.proten.manager.login.model.LoginModel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DicServiceImpl implements DicService {

	private static final Logger log = LoggerFactory.getLogger(DicServiceImpl.class);
	
	private final ElasticSearchSelectRepository elasticSearchSelectRepository;
	
	private final ElasticSearchInsertRepository elasticSearchInsertRepository;
	
	private final ElasticSearchUpdateRepository elasticSearchUpdateRepository;
	
	private final ElasticSearchDeleteRepository elasticSearchDeleteRepository;
	
	private final RestTemplateService<String> restTemplateService;
	
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
	public List<Map<String, Object>> selectDicList(Map<String, Object> paramMap) throws Exception {
		List<Map<String, Object>> resultList = null;
		
		int from = Integer.parseInt(StringUtil.nvl(paramMap.get("start"), "0"));
		int rownum = Integer.parseInt(StringUtil.nvl(paramMap.get("lineNo"),"10"));
		String sortType = StringUtil.nvl(paramMap.get("sort"),"");
		String sortOrder = sortType.contains("Date") ? ElasticSearchConstant.DESC : ElasticSearchConstant.ASC;
		
		String query = StringUtil.nvl(paramMap.get("searchKeyword"),"");
		String searchFields = StringUtil.nvl(paramMap.get("searchField"),"wordUnq"); 
		
		StringBuilder queryString = new StringBuilder();
		if(!StringUtil.nvl(paramMap.get("siteNo")).equals("")) {
			queryString.append( " siteNo:"+paramMap.get("siteNo"));
		} 
		if(!StringUtil.nvl(paramMap.get("dicNo")).equals("")) {
			if(queryString.length()!=0) {
				queryString.append(" AND ");
			}
			queryString.append(" dicNo:"+paramMap.get("dicNo"));
		}
		if(!StringUtil.nvl(paramMap.get("synonyms")).equals("")) {
			if(queryString.length()!=0) {
				queryString.append(" AND ");
			}
			queryString.append(" synonyms:"+paramMap.get("synonyms"));
		}
		resultList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_DIC, from, rownum, searchFields, query, sortType, sortOrder, queryString.toString());
		
		return resultList;
	}

	@Override
	public List<Map<String, Object>> selectDicListAll(Map<String, Object> paramMap) throws Exception {
		List<Map<String, Object>> resultList = null;
		
		int from = 0;
		int rownum = 100000;
		String sortType = StringUtil.nvl(paramMap.get("sort"),"dicNo");
		String sortOrder = ElasticSearchConstant.ASC;
		
		StringBuilder queryString = new StringBuilder();
		if(!StringUtil.nvl(paramMap.get("siteNo")).equals("")) {
			queryString.append( " siteNo:"+paramMap.get("siteNo"));
		}
		resultList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_DIC, from, rownum, sortType, sortOrder, queryString.toString());
		
		return resultList;
	}
	
	@Override
	public Map<String, Object> insertDicInfo(Map<String, Object> paramMap, HttpSession session) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		boolean actionRet = true;
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", login.getSiteNo());
		paramMap.put("wordUnq", StringUtil.removeTrimChar((String)paramMap.get("word")));
		paramMap.put("dicNo", StringUtil.removeTrimChar((String)paramMap.get("word")));
		
		paramMap.put("createUser", login.getUserId());
		paramMap.put("createUserNm", login.getUserNm());
		paramMap.put("createDate", DateUtil.getCurrentDateTimeMille());
		paramMap.put("modifyUser", login.getUserId());
		paramMap.put("modifyUserNm", login.getUserNm());
		paramMap.put("modifyDate", DateUtil.getCurrentDateTimeMille());
		
		//중복되는 동의어가 있는지 확인
		String synonyms = "";
		if(!StringUtil.nvl(paramMap.get("synonyms")).equals("")) {
			int siteNo = login.getSiteNo();
			synonyms = dupSynonymDicInfo((String)paramMap.get("synonyms"),"",siteNo);
			if(!synonyms.equals("")) {
				result.put("msg", "'"+synonyms +"' 동의어가 이미 등록되어 있어 저장 할 수 없습니다.");
				result.put("success",false);
				return result;
			}
		}
		
		actionRet = elasticSearchInsertRepository.insertData(ElasticSearchIndex.INDEX_NAME_DIC, String.valueOf(paramMap.get("dicNo")), paramMap);
		result.put("success", actionRet);
		
		return result;
	}

	@Override
	public Map<String,Object> updateDicInfo(Map<String, Object> paramMap, HttpSession session) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		boolean actionRet = true;
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", login.getSiteNo());
		paramMap.put("wordUnq", StringUtil.removeTrimChar((String)paramMap.get("word")));
		paramMap.put("dicNo", StringUtil.removeTrimChar((String)paramMap.get("word")));
		
		paramMap.put("modifyUser", login.getUserId());
		paramMap.put("modifyUserNm", login.getUserNm());
		paramMap.put("modifyDate", DateUtil.getCurrentDateTimeMille());
		
		//중복되는 동의어가 있는지 확인
		String synonyms = "";
		if(!StringUtil.nvl(paramMap.get("synonyms")).equals("")) {
			int siteNo = login.getSiteNo();
			synonyms = dupSynonymDicInfo((String)paramMap.get("synonyms"), (String)paramMap.get("dicNo"), siteNo);
			if(!synonyms.equals("")) {
				result.put("msg", "'"+synonyms +"' 동의어가 이미 등록되어 있어 저장 할 수 없습니다.");
				result.put("success",false);
				return result;
			}
		}
		
		if(paramMap.get("wordSep") == null) { //wordSep 값이 없을시에 신규로 간주
			paramMap.put("wordSep", "0");
			paramMap.put("nosearchYn", "n");
			paramMap.put("createUser", login.getUserId());
			paramMap.put("createUserNm", login.getUserNm());
			paramMap.put("createDate", DateUtil.getCurrentDateTimeMille());
			
			actionRet = elasticSearchInsertRepository.insertData(ElasticSearchIndex.INDEX_NAME_DIC, String.valueOf(paramMap.get("dicNo")), paramMap);
		} else {
			actionRet = elasticSearchUpdateRepository.updateData(ElasticSearchIndex.INDEX_NAME_DIC, String.valueOf(paramMap.get("dicNo")), paramMap);
		}
		result.put("success", actionRet);
		
		return result;
	}
	
	@Override
	public boolean deleteDicInfo(Map<String, Object> paramMap) throws Exception {
		boolean result = true;
		List<String> dicArray = new ArrayList<String>();
		String dicNo = StringUtil.nvl(paramMap.get("dicNo"));
		if(dicNo.contains(":")) {
			String[] userArr = StringUtil.split(dicNo,":");
			for(String uNo : userArr){
				dicArray.add("_id:"+StringUtil.nvl(uNo));
			}
		} else if( !"".equals(dicNo)){
			dicArray.add("_id:"+StringUtil.nvl(dicNo));
		}
		if (dicArray.size() > 0) {
			result = elasticSearchDeleteRepository.deleteDataByQuery(ElasticSearchIndex.INDEX_NAME_DIC, StringUtils.join(dicArray,','));
		}
		return result;
	}
	
	@Override
	public boolean insertDicBulk(List<Map<String, Object>> listMap) throws Exception {
		boolean result = true;
		result = elasticSearchInsertRepository.insertBulkData(ElasticSearchIndex.INDEX_NAME_DIC, listMap);
		return result;
	}
	
	@Override
	public boolean updateDicBulk(List<Map<String, Object>> listMap) throws Exception {
		boolean result = true;
		result = elasticSearchUpdateRepository.updateBulkData(ElasticSearchIndex.INDEX_NAME_DIC, listMap);
		return result;
	}
	
	@Override
	public boolean dupDicInfo(Map<String, Object> paramMap) throws Exception {
		boolean result = false;
		List<Map<String, Object>> resultList = null;
		String query = StringUtil.nvl(paramMap.get("searchKeyword"));
		String searchFields = "wordUnq";
		
		int from = 0;
		int rownum = 1;	
		String sortType = "";
		String sortOrder = "";
		
		StringBuilder queryString = new StringBuilder();
		if(!StringUtil.nvl(paramMap.get("siteNo")).equals("")) {
			queryString.append( " siteNo:"+paramMap.get("siteNo"));
		} 
		if(!StringUtil.nvl(paramMap.get("synonyms")).equals("")) {
			if(queryString.length()!=0) {
				queryString.append(" AND ");
			}
			queryString.append(" synonyms:"+paramMap.get("synonyms"));
		}
		if(!StringUtil.nvl(paramMap.get("word")).equals("")) {
			if(queryString.length()!=0) {
				queryString.append(" AND ");
			}
			queryString.append(" word.keyword:"+paramMap.get("word"));
		}		
		resultList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_DIC, from, rownum, searchFields, query, sortType, sortOrder, queryString.toString());
		if (resultList.size() > 0) {
			result = true;
		}
		return result;
	}

	@Override
	public String dupSynonymDicInfo(String param, String id, int siteNo) throws Exception {
		List<String> synonyms = new ArrayList<String>(); //중복 검출된 동의어
		List<Map<String, Object>> resultList = null;
		
		String searchFields = "synonyms";
		int from = 0;
		int rownum = 1;	
		String sortType = "";
		String sortOrder = "";
		
		List<Map<String,Object>> matchList = new ArrayList<Map<String,Object>>(); //filter Option
		Map<String,Object> match = null;
		
		if(!id.equals("")) {
			match = new HashMap<String,Object>();
			match.put("type", "not");
			match.put("key", "dicNo");
			match.put("value", id);
			matchList.add(match);
		}		
		match = new HashMap<String,Object>();
		match.put("type", "match");
		match.put("key", "siteNo");
		match.put("value", siteNo);
		matchList.add(match);
		
		for(String synonym : param.split(",")) {
			resultList = elasticSearchSelectRepository.selectDataListByBoolQuery(ElasticSearchIndex.INDEX_NAME_DIC, from, rownum, searchFields, synonym, sortType, sortOrder, matchList);
			if (resultList.size() > 0) {
				synonyms.add(synonym);
			}
		}
		
		return String.join(",", synonyms);
	}

	@Override
	public String requestDistDic() throws Exception {
		GsonBuilder gsonBuilder = new GsonBuilder();  
		gsonBuilder.serializeNulls();  
		Gson gson = gsonBuilder.create();
		
		HttpHeaders header = null;
		UriComponents builder = null;
		
		StringBuffer requestUrl = new StringBuffer();
		String requestBody = "";
		JsonObject requestObj = new JsonObject();
		// 파라미터 세팅
		requestObj.addProperty("esUrl", ELASTICSEARCH_IP+":"+ELASTICSEARCH_PORT);
		
		requestBody = gson.toJson(requestObj);
		
		requestUrl.append(TRAINING_SCHEME);
		requestUrl.append("://");
		requestUrl.append(TRAINING_IP).append(":").append(TRAINING_PORT);
		requestUrl.append(ClassifyManagerConstant.DIST_DICTIONARY_API_URL);
		
		log.debug("[DicServiceImpl][requestDistDic] requestUrl : {}", requestUrl.toString());
		log.debug("[DicServiceImpl][requestDistDic] requestBody : {}", requestBody);
		
		builder = UriComponentsBuilder.fromHttpUrl(requestUrl.toString()).encode().build();
		header = restTemplateService.getTrainingApiHeader();
		String result = restTemplateService.httpPost(builder.toUriString(), header, requestBody, String.class).getBody();
		log.debug("[DicServiceImpl][requestDistDic] result : {}", result);
		return result;
	}
}
