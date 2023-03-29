package kr.co.proten.manager.dic.service.Impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.buf.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kr.co.proten.manager.classify.service.ClassifyRuleService;
import kr.co.proten.manager.common.ClassifyManagerConstant;
import kr.co.proten.manager.common.elastic.ElasticSearchConstant;
import kr.co.proten.manager.common.elastic.ElasticSearchIndex;
import kr.co.proten.manager.common.repository.ElasticSearchDeleteRepository;
import kr.co.proten.manager.common.repository.ElasticSearchInsertRepository;
import kr.co.proten.manager.common.repository.ElasticSearchSelectRepository;
import kr.co.proten.manager.common.repository.ElasticSearchUpdateRepository;
import kr.co.proten.manager.common.util.DateUtil;
import kr.co.proten.manager.common.util.StringUtil;
import kr.co.proten.manager.common.vo.ReferencedResultVo;
import kr.co.proten.manager.dic.service.ObjectService;
import kr.co.proten.manager.login.model.LoginModel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ObjectServiceImpl implements ObjectService {
	
	private static final Logger log = LoggerFactory.getLogger(ObjectServiceImpl.class);
	
	private final ElasticSearchSelectRepository elasticSearchSelectRepository;
	
	private final ElasticSearchInsertRepository elasticSearchInsertRepository;
	
	private final ElasticSearchUpdateRepository elasticSearchUpdateRepository;
	
	private final ElasticSearchDeleteRepository elasticSearchDeleteRepository;
	
	private final ClassifyRuleService classifyRuleService;
	
	
	@Override
	public List<Map<String, Object>> selectObjDicList(Map<String, Object> paramMap) throws Exception {
		List<Map<String, Object>> resultList = null;
		
		int from = Integer.parseInt(StringUtil.nvl(paramMap.get("start"), "0"));
		int rownum = Integer.parseInt(StringUtil.nvl(paramMap.get("lineNo"),"10"));
		String sortType = StringUtil.nvl(paramMap.get("sort"),"entityNo");
		String sortOrder = sortType.contains("Date") ? ElasticSearchConstant.DESC : ElasticSearchConstant.ASC;
		
		String query = StringUtil.nvl(paramMap.get("searchKeyword"),"");
		String searchFields = StringUtil.nvl(paramMap.get("searchField"),"entityUnq");
		
		StringBuilder queryString = new StringBuilder();
		if(!StringUtil.nvl(paramMap.get("siteNo")).equals("")) {
			queryString.append( " siteNo:"+paramMap.get("siteNo"));
		} 
		if(!StringUtil.nvl(paramMap.get("entityNo")).equals("")) {
			if(queryString.length()!=0) {
				queryString.append(" AND ");
			}
			queryString.append(" entityNo:"+paramMap.get("entityNo"));
		}
		resultList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_ENTITY_DIC, from, rownum, searchFields, query, sortType, sortOrder, queryString.toString());
		
		return resultList;
	}

	@Override
	public List<Map<String, Object>> selectObjDicListAll(Map<String, Object> paramMap) throws Exception {
		List<Map<String, Object>> resultList = null;
		
		int from = 0;
		int rownum = 100000;
		String sortType = StringUtil.nvl(paramMap.get("sort"),"entityNo");
		String sortOrder = ElasticSearchConstant.ASC;
		
		StringBuilder queryString = new StringBuilder();
		if(!StringUtil.nvl(paramMap.get("siteNo")).equals("")) {
			queryString.append( " siteNo:"+paramMap.get("siteNo"));
		} 
		if(!StringUtil.nvl(paramMap.get("useYn")).equals("")) {
			if(queryString.length()!=0) {
				queryString.append(" AND ");
			}
			queryString.append( " useYn:"+paramMap.get("useYn"));
		} 		
		resultList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_ENTITY_DIC, from, rownum, sortType, sortOrder, queryString.toString());
		
		return resultList;
	}
	
	@Override
	public boolean insertObjDicInfo(Map<String, Object> paramMap, HttpSession session) throws Exception {
		boolean result = true;
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", login.getSiteNo());
		paramMap.put("entityUnq", StringUtil.removeTrimChar((String)paramMap.get("entity")));
		paramMap.put("entityNo", StringUtil.removeTrimChar((String)paramMap.get("entity")));
		
		paramMap.put("createUser", login.getUserId());
		paramMap.put("createUserNm", login.getUserNm());
		paramMap.put("createDate", DateUtil.getCurrentDateTimeMille());
		paramMap.put("modifyUser", login.getUserId());
		paramMap.put("modifyUserNm", login.getUserNm());
		paramMap.put("modifyDate", DateUtil.getCurrentDateTimeMille());
		
		result = elasticSearchInsertRepository.insertData(ElasticSearchIndex.INDEX_NAME_ENTITY_DIC, String.valueOf(paramMap.get("entityNo")), paramMap);
		
		return result;
	}

	@Override
	public boolean updateObjDicInfo(Map<String, Object> paramMap, HttpSession session) throws Exception {
		boolean result = true;
		LoginModel login = (LoginModel) session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", login.getSiteNo());
		paramMap.put("entityUnq", StringUtil.removeTrimChar((String)paramMap.get("entity")));
		paramMap.put("entityNo", StringUtil.removeTrimChar((String)paramMap.get("entity")));
		paramMap.put("modifyUser", login.getUserId());
		paramMap.put("modifyUserNm", login.getUserNm());
		paramMap.put("modifyDate", DateUtil.getCurrentDateTimeMille());
		
		result = elasticSearchUpdateRepository.updateData(ElasticSearchIndex.INDEX_NAME_ENTITY_DIC, String.valueOf(paramMap.get("entityNo")), paramMap);
		return result;
	}
	
	@Override
	public boolean insertObjDicBulk(List<Map<String, Object>> listMap) throws Exception {
		boolean result = true;
		result = elasticSearchInsertRepository.insertBulkData(ElasticSearchIndex.INDEX_NAME_ENTITY_DIC, listMap);
		return result;
	}

	@Override
	public boolean updateObjDicBulk(List<Map<String, Object>> listMap) throws Exception {
		boolean result = true;
		result = elasticSearchUpdateRepository.updateBulkData(ElasticSearchIndex.INDEX_NAME_ENTITY_DIC, listMap);
		return result;
	}

	@Override
	public boolean dupObjDicInfo(Map<String, Object> paramMap) throws Exception {
		boolean result = false;
		List<Map<String, Object>> resultList = null;
		String query = StringUtil.nvl(paramMap.get("searchKeyword"));
		String searchFields = "entityUnq";
		
		int from = 0;
		int rownum = 1;	
		String sortType = "";
		String sortOrder = "";
		
		StringBuilder queryString = new StringBuilder();
		if(!StringUtil.nvl(paramMap.get("siteNo")).equals("")) {
			queryString.append( " siteNo:"+paramMap.get("siteNo"));
		} 
		if(!StringUtil.nvl(paramMap.get("entry")).equals("")) {
			if(queryString.length()!=0) {
				queryString.append(" AND ");
			}
			queryString.append(" entry:"+paramMap.get("entry"));
		}
		if(!StringUtil.nvl(paramMap.get("entity")).equals("")) {
			if(queryString.length()!=0) {
				queryString.append(" AND ");
			}
			queryString.append(" entityUnq.keyword:"+paramMap.get("entity"));
		}	
		resultList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_ENTITY_DIC, from, rownum, searchFields, query, sortType, sortOrder, queryString.toString());
		if (resultList.size() > 0) {
			result = true;
		}
		return result;
	}

	@Override
	public ReferencedResultVo deleteObjDicInfo(Map<String, Object> paramMap) throws Exception {
		ReferencedResultVo resultVo = new ReferencedResultVo();
		List<String> dicArray = new ArrayList<String>();
		List<String> entityNoArray = new ArrayList<String>();
		String entityNo = StringUtil.nvl(paramMap.get("entityNo"));
		if(entityNo.contains(":")) {
			String[] arr = StringUtil.split(entityNo,":");
			for(String uNo : arr){
				entityNoArray.add(StringUtil.nvl(uNo));
			}
		} else if( !"".equals(entityNo)){
			entityNoArray.add(StringUtil.nvl(entityNo));
		}
		
		if(entityNoArray.size() > 0){
			int siteNo = (int) paramMap.get("siteNo");
			resultVo = classifyRuleService.refIdCheckAtRule(siteNo, entityNoArray, "rule");
			
			if(resultVo.getNoReferencedIdList().size() > 0) {
				for(String uNo : resultVo.getNoReferencedIdList()) {
					dicArray.add("_id:"+StringUtil.nvl(uNo));
				}
				elasticSearchDeleteRepository.deleteDataByQuery(ElasticSearchIndex.INDEX_NAME_ENTITY_DIC, StringUtils.join(dicArray,','));	
			}
		}
		return resultVo;
	}	
}
