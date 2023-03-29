package kr.co.proten.manager.classify.service.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

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
import kr.co.proten.manager.common.vo.ClassifyRuleReferecedInfoVo;
import kr.co.proten.manager.common.vo.ReferencedResultVo;
import kr.co.proten.manager.login.model.LoginModel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClassifyRuleServiceImpl implements ClassifyRuleService {

	private static final Logger log = LoggerFactory.getLogger(ClassifyRuleServiceImpl.class);
	
	private final ElasticSearchSelectRepository elasticSearchSelectRepository;
	
	private final ElasticSearchInsertRepository elasticSearchInsertRepository;
	
	private final ElasticSearchUpdateRepository elasticSearchUpdateRepository;
	
	private final ElasticSearchDeleteRepository elasticSearchDeleteRepository;

	@Override
	public List<Map<String, Object>> selectRuleList(Map<String, Object> paramMap) throws Exception {
		List<Map<String, Object>> resultList = null;
		
		int from = Integer.parseInt(StringUtil.nvl(paramMap.get("start"), "0"));
		int rownum = Integer.parseInt(StringUtil.nvl(paramMap.get("lineNo"),"10"));
		String sortType = StringUtil.nvl(paramMap.get("sort"),"ruleNo");
		String sortOrder = sortType.contains("Date") ? ElasticSearchConstant.DESC : ElasticSearchConstant.ASC;
		
		StringBuilder queryString = new StringBuilder();
		if(!StringUtil.nvl(paramMap.get("siteNo")).equals("")) {
			queryString.append( " siteNo:"+paramMap.get("siteNo"));
		}
		if(!StringUtil.nvl(paramMap.get("categoryNo")).equals("") && !paramMap.get("categoryNo").equals("0")) {
			String categoryNos = (String)paramMap.get("categoryNo");
			String[] categoryArr = categoryNos.split(",");
			if(queryString.length()!=0) {
				queryString.append(" AND ");
			}
			queryString.append(" categoryNo:(");
			StringBuilder subString = new StringBuilder();
			for ( int i=0; i<categoryArr.length; i++) {				 
				if(subString.length()!=0) {
					subString.append(" OR ");
				}
				subString.append(Integer.parseInt(categoryArr[i]));
			}
			queryString.append(subString);
			queryString.append(" ) ");
		}	
		resultList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_CLASSIFY_RULE, from, rownum, sortType, sortOrder, queryString.toString());
		
		return resultList;
	}
	
	@Override
	public List<Map<String, Object>> selectRuleListAll(Map<String, Object> paramMap) throws Exception {
		List<Map<String, Object>> resultList = null;
		
		int from = 0;
		int rownum = 100000;
		String sortType = StringUtil.nvl(paramMap.get("sort"),"ruleNo");
		String sortOrder = ElasticSearchConstant.ASC;
		
		StringBuilder queryString = new StringBuilder();
		if(!StringUtil.nvl(paramMap.get("siteNo")).equals("")) {
			queryString.append( " siteNo:"+paramMap.get("siteNo"));
		}
		resultList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_CLASSIFY_RULE, from, rownum, sortType, sortOrder, queryString.toString());
		
		return resultList;
	}

	@Override
	public boolean insertRuleInfo(Map<String, Object> paramMap, HttpSession session) throws Exception {
		boolean result = true;
		LoginModel login = (LoginModel) session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("createUser", login.getUserId());
		paramMap.put("createUserNm", login.getUserNm());
		paramMap.put("createDate", DateUtil.getCurrentDateTimeMille());
		paramMap.put("modifyUser", login.getUserId());
		paramMap.put("modifyUserNm", login.getUserNm());
		paramMap.put("modifyDate", DateUtil.getCurrentDateTimeMille());
		paramMap.put("siteNo", login.getSiteNo());
		
		int key = elasticSearchSelectRepository.selectMaxId(ElasticSearchIndex.INDEX_NAME_CLASSIFY_RULE, "ruleNo");
		paramMap.put("ruleNo", key);
		result = elasticSearchInsertRepository.insertData(ElasticSearchIndex.INDEX_NAME_CLASSIFY_RULE, String.valueOf(key), paramMap);
		return result;
	}

	@Override
	public boolean updateRuleInfo(Map<String, Object> paramMap, HttpSession session) throws Exception {
		boolean result = true;
		LoginModel login = (LoginModel) session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("modifyUser", login.getUserId());
		paramMap.put("modifyUserNm", login.getUserNm());
		paramMap.put("modifyDate", DateUtil.getCurrentDateTimeMille());
		paramMap.put("siteNo", login.getSiteNo());
		
		result = elasticSearchUpdateRepository.updateData(ElasticSearchIndex.INDEX_NAME_CLASSIFY_RULE, String.valueOf(paramMap.get("ruleNo")), paramMap);
		return result;
	}

	@Override
	public boolean deleteRuleInfo(Map<String, Object> paramMap) throws Exception {
		boolean result = true;
		String ruleNo = StringUtil.nvl(paramMap.get("ruleNo"));
		result = elasticSearchDeleteRepository.deleteDataById(ElasticSearchIndex.INDEX_NAME_CLASSIFY_RULE, ruleNo);
		return result;
	}

	@Override
	public boolean dupRuleInfo(Map<String, Object> paramMap) throws Exception {
		boolean result = false;
		List<Map<String, Object>> resultList = null;
		
		int from = 0;
		int rownum = 1;	
		String sortType = "";
		String sortOrder = "";
		
		StringBuilder queryString = new StringBuilder();
		if(!StringUtil.nvl(paramMap.get("siteNo")).equals("")) {
			queryString.append( " siteNo:"+paramMap.get("siteNo"));
		} 
		if(!StringUtil.nvl(paramMap.get("patternCount")).equals("")) {
			if(queryString.length()!=0) {
				queryString.append(" AND ");
			}
			queryString.append(" patternCount:"+paramMap.get("patternCount"));
		}
		if(!StringUtil.nvl(paramMap.get("rule")).equals("")) {
			if(queryString.length()!=0) {
				queryString.append(" AND ");
			}
			queryString.append(" rule.keyword:"+paramMap.get("rule"));
		}		
		resultList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_CLASSIFY_RULE, from, rownum, sortType, sortOrder, queryString.toString());
		if (resultList.size() > 0) {
			result = true;
		}
		return result;
	}

	@Override
	public boolean insertRuleBulk(List<Map<String, Object>> listMap) throws Exception {
		boolean result = true;
		result = elasticSearchInsertRepository.insertBulkData(ElasticSearchIndex.INDEX_NAME_CLASSIFY_RULE, listMap);
		return result;
	}

	@Override
	public int selectMaxId() throws Exception {
		int key = elasticSearchSelectRepository.selectMaxId(ElasticSearchIndex.INDEX_NAME_CLASSIFY_RULE, "ruleNo");
		return key;
	}

	@Override
	public ReferencedResultVo refIdCheckAtRule(int siteNo, List<String> entityNoArray, String refField) throws Exception {
		List<Map<String, Object>> resultList = null;
		
		int from = 0;
		int rownum = 100000;
		String sortType = "ruleNo";
		String sortOrder = ElasticSearchConstant.ASC;
		
		ReferencedResultVo resultVo = new ReferencedResultVo();
		List<String> noReferencedIdList = new ArrayList<String>();
		Map<String, List<ClassifyRuleReferecedInfoVo>> referencedIdInfoMap = new HashMap<String, List<ClassifyRuleReferecedInfoVo>>();
		List<ClassifyRuleReferecedInfoVo> referecedInfoList;
		ClassifyRuleReferecedInfoVo referecedInfoVo;
		
		StringBuilder queryString = new StringBuilder();
		queryString.append( " siteNo:"+siteNo);
		if(entityNoArray.size()>0) {
			
			if(queryString.length()!=0) {
				queryString.append(" AND ");
			}
			queryString.append(" "+refField+":(");
			
			StringBuilder subString = new StringBuilder();
			
			for ( int i=0; i<entityNoArray.size(); i++) {
				if(subString.length()!=0) {
					subString.append(" OR ");
				}
				subString.append(entityNoArray.get(i));
			}
			queryString.append(subString);
			queryString.append(" ) ");

			resultList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_CLASSIFY_RULE, from, rownum, sortType, sortOrder, queryString.toString());
		}
		
		if(resultList != null && resultList.size() > 0) {
			String refIds = "";
			String[] refArrIds = null;
			boolean refStatus = false;
			for ( int i=0; i<entityNoArray.size(); i++) {
				referecedInfoList = new ArrayList<ClassifyRuleReferecedInfoVo>();
				refStatus = false;
				
				for(Map<String, Object> resultMap : resultList) {
					refIds = String.valueOf(resultMap.get(refField));
					refArrIds = refIds.split("[,\\s]");
					for(String refId : refArrIds) {
						refId = refId.trim();
						if(refId.equals(entityNoArray.get(i))) {
							referecedInfoVo = new ClassifyRuleReferecedInfoVo();
							referecedInfoVo.setClassifyRuleCategoryNo(String.valueOf(resultMap.get("categoryNo")));
							referecedInfoVo.setClassifyRuleEntityNo(refId);
							referecedInfoVo.setClassifyRuleNo(String.valueOf(resultMap.get("ruleNo")));
							referecedInfoVo.setClassifyRuleNm(String.valueOf(resultMap.get("rule")));
							referecedInfoList.add(referecedInfoVo);
							refStatus = true;
							break;
						}
					}
				}
				
				if(refStatus) {
					referencedIdInfoMap.put(entityNoArray.get(i), referecedInfoList);
				} else {
					noReferencedIdList.add(entityNoArray.get(i));
				}
			}
			resultVo.setNoReferencedIdList(noReferencedIdList);
		} else {
			resultVo.setNoReferencedIdList(entityNoArray);
		}
		resultVo.setClassifyRuleReferencedIdInfoMap(referencedIdInfoMap);
		
		return resultVo;
	}
	
}
