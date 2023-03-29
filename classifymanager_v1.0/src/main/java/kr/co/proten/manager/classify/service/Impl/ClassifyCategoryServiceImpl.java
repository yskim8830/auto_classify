package kr.co.proten.manager.classify.service.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.buf.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kr.co.proten.manager.classify.service.ClassifyCategoryService;
import kr.co.proten.manager.common.ClassifyManagerConstant;
import kr.co.proten.manager.common.elastic.ElasticSearchConstant;
import kr.co.proten.manager.common.elastic.ElasticSearchIndex;
import kr.co.proten.manager.common.repository.ElasticSearchDeleteRepository;
import kr.co.proten.manager.common.repository.ElasticSearchInsertRepository;
import kr.co.proten.manager.common.repository.ElasticSearchSelectRepository;
import kr.co.proten.manager.common.repository.ElasticSearchUpdateRepository;
import kr.co.proten.manager.common.util.DateUtil;
import kr.co.proten.manager.common.util.StringUtil;
import kr.co.proten.manager.login.model.LoginModel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClassifyCategoryServiceImpl implements ClassifyCategoryService {

	private static final Logger log = LoggerFactory.getLogger(ClassifyCategoryServiceImpl.class);
	
	private final ElasticSearchSelectRepository elasticSearchSelectRepository;
	
	private final ElasticSearchInsertRepository elasticSearchInsertRepository;
	
	private final ElasticSearchUpdateRepository elasticSearchUpdateRepository;
	
	private final ElasticSearchDeleteRepository elasticSearchDeleteRepository;
	
	@Override
	public List<Map<String, Object>> selectCategoryList(Map<String, Object> paramMap, boolean withRootData) throws Exception {
		List<Map<String, Object>> resultList = null;
		
		int from = 0;
		int rownum = 100000;
		String sortType = StringUtil.nvl(paramMap.get("sort"),"categoryNo");
		String sortOrder = ElasticSearchConstant.ASC;
		
		StringBuilder queryString = new StringBuilder();
		if(!StringUtil.nvl(paramMap.get("siteNo")).equals("")) {
			queryString.append( " siteNo:"+paramMap.get("siteNo"));
		}
		if(queryString.length() > 0 && withRootData) {
			queryString.append(" OR siteNo:0");
		}
		resultList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_CLASSIFY_CATEGORY, from, rownum, sortType, sortOrder, queryString.toString());
		
		return resultList;
	}
	
	@Override
	public List<Map<String, Object>> convertCategoryListConvHierarchyData(List<Map<String, Object>> categoryList) throws Exception {
		List<Map<String, Object>> hierarchyData = new ArrayList<Map<String, Object>>();
		for(Map<String,Object> dep0 : categoryList) {
			if((int)dep0.get("depth") == 0) {
				List<Map<String, Object>> depth1 = new ArrayList<Map<String,Object>>();
				for(Map<String,Object> dep1 : categoryList) {
					if((int)dep1.get("depth") == 1) {
						List<Map<String, Object>> depth2 = new ArrayList<Map<String,Object>>();
						for(Map<String,Object> dep2 : categoryList) {
							if((int)dep2.get("depth") == 2) {
								if(dep1.get("categoryNo") != null && dep2.get("pCategoryNo") != null && (int)dep1.get("categoryNo") == (int)dep2.get("pCategoryNo")) {
									List<Map<String, Object>> depth3 = new ArrayList<Map<String,Object>>();
									for(Map<String,Object> dep3 : categoryList) {
										if((int)dep3.get("depth") == 3) {
											if(dep2.get("categoryNo") != null && dep3.get("pCategoryNo") != null && (int)dep2.get("categoryNo") == (int)dep3.get("pCategoryNo")) {
												dep3.put("nodes", new ArrayList<>());
												depth3.add(dep3);
											}
										}
									}
									if(depth3.size() > 0) {
										dep2.put("nodes", depth3);										
									}
									depth2.add(dep2);
								}
							}
						}
						if(depth2.size() > 0) {
							dep1.put("nodes", depth2);							
						}
						depth1.add(dep1);
					}
				}
				if(depth1.size() > 0) {
					dep0.put("nodes", depth1);					
				}
				hierarchyData.add(dep0);
			}
		}
		return hierarchyData;
	}
	
	@Override
	public List<Map<String, Object>> selectViewCategory(Map<String, Object> paramMap) throws Exception {
		List<Map<String, Object>> resultList = null;
		
		int from = Integer.parseInt(StringUtil.nvl(paramMap.get("start"), "0"));
		int rownum = Integer.parseInt(StringUtil.nvl(paramMap.get("lineNo"),"1000"));
		String sortType = StringUtil.nvl(paramMap.get("sort"),"categoryNo");
		String sortOrder = ElasticSearchConstant.ASC;
		String searchFields = StringUtil.nvl(paramMap.get("searchField"),"categoryNm");
		String query = StringUtil.nvl(paramMap.get("searchKeyword"),"");
		
		StringBuilder queryString = new StringBuilder();
		if(!StringUtil.nvl(paramMap.get("siteNo")).equals("")) {
			queryString.append( " siteNo:"+paramMap.get("siteNo"));
		}
		if(!StringUtil.nvl(paramMap.get("categoryNo")).equals("")) {
			if(queryString.length()!=0) {
				queryString.append(" AND ");
			}
			queryString.append( " categoryNo:"+StringUtil.getMatchToString(paramMap.get("categoryNo").toString()));
		}
		resultList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_CLASSIFY_CATEGORY, from, rownum, searchFields, query, sortType, sortOrder, queryString.toString());
		if(!StringUtil.nvl(paramMap.get("depth")).equals("")) {
			paramMap.replace("depth", Integer.parseInt((String)paramMap.get("depth")));
			if((int)paramMap.get("depth") < 3) {
				resultList.addAll(getCategoryChild(paramMap));
			}
		}
		
		return resultList;
	}
	
	@Override
	public List<Map<String, Object>> getCategoryChild(Map<String, Object> paramMap) throws Exception {
		List<Map<String, Object>> resultList = null;
		
		int from = Integer.parseInt(StringUtil.nvl(paramMap.get("start"), "0"));
		int rownum = Integer.parseInt(StringUtil.nvl(paramMap.get("lineNo"),"1000"));
		String sortType = StringUtil.nvl(paramMap.get("sort"),"categoryNo");
		String sortOrder = ElasticSearchConstant.ASC;
		String searchFields = StringUtil.nvl(paramMap.get("searchField"),"categoryNm");
		String query = StringUtil.nvl(paramMap.get("searchKeyword"),"");
		
		StringBuilder queryString = new StringBuilder();
		if(!StringUtil.nvl(paramMap.get("siteNo")).equals("")) {
			queryString.append( " siteNo:"+paramMap.get("siteNo"));
		}
		if(!StringUtil.nvl(paramMap.get("categoryNo")).equals("")) {
			if(queryString.length()!=0) {
				queryString.append(" AND ");
			}
			queryString.append( " pCategoryNo:"+paramMap.get("categoryNo"));
		}
		resultList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_CLASSIFY_CATEGORY, from, rownum, searchFields, query, sortType, sortOrder, queryString.toString());
		int depth = 0;
		if(!StringUtil.nvl(paramMap.get("depth")).equals("")) {
			depth = (int)paramMap.get("depth") + 1;
			List<Map<String, Object>> childResultList = new ArrayList<Map<String,Object>>();
			for(int i = depth ; depth < 3; depth++) {
				for(Map<String, Object> rst : resultList) {
					Map<String, Object> cateParam = new HashMap<String, Object>();
					cateParam.put("categoryNo", rst.get("categoryNo"));
					childResultList.addAll(getCategoryChild(cateParam));
				}
			}
			resultList.addAll(childResultList);
		}
		
		return resultList;
	}

	@Override
	public Map<String,Object> insertCategoryInfo(Map<String, Object> paramMap, HttpSession session) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		boolean actionRet = true;
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", login.getSiteNo());
		
		paramMap.put("createUser", login.getUserId());
		paramMap.put("createUserNm", login.getUserNm());
		paramMap.put("createDate", DateUtil.getCurrentDateTimeMille());
		paramMap.put("modifyUser", login.getUserId());
		paramMap.put("modifyUserNm", login.getUserNm());
		paramMap.put("modifyDate", DateUtil.getCurrentDateTimeMille());
		
		if(paramMap.get("depth").equals("1")) {
			paramMap.replace("item", (String)paramMap.get("categoryNm"));
		}
		String fullItem = ((String)paramMap.get("fullItem")).replace("&gt;", ">") + ">"+(String)paramMap.get("categoryNm");
		paramMap.replace("fullItem", fullItem);
		
		int key = elasticSearchSelectRepository.selectMaxId(ElasticSearchIndex.INDEX_NAME_CLASSIFY_CATEGORY, "categoryNo");
		paramMap.put("categoryNo", key);
		actionRet = elasticSearchInsertRepository.insertData(ElasticSearchIndex.INDEX_NAME_CLASSIFY_CATEGORY, String.valueOf(key), paramMap);
		result.put("id", key);
		result.put("success", actionRet);
		
		return result;
	}

	@Override
	public Map<String,Object> updateCategoryInfo(Map<String, Object> paramMap, HttpSession session) throws Exception {
		Map<String,Object> result = new HashMap<String,Object>();
		LoginModel login = (LoginModel) session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		
		paramMap.put("siteNo", login.getSiteNo());
		paramMap.put("modifyUser", login.getUserId());
		paramMap.put("modifyUserNm", login.getUserNm());
		paramMap.put("modifyDate", DateUtil.getCurrentDateTimeMille());
		
		String categoryNo = (String)paramMap.get("categoryNo");
		int depth = Integer.parseInt((String)paramMap.get("depth"));
		if(depth == 1) {
			paramMap.replace("item", (String)paramMap.get("categoryNm"));
		}
		String fullItem = ((String)paramMap.get("fullItem")).replace("&gt;", ">");
		String [] reFullItem = fullItem.split(">");
		reFullItem[depth] = (String)paramMap.get("categoryNm"); //전체항목을 변경된 String 으로 바꿈
		paramMap.replace("fullItem", String.join(">", reFullItem));
		
		boolean action = elasticSearchUpdateRepository.updateData(ElasticSearchIndex.INDEX_NAME_CLASSIFY_CATEGORY, String.valueOf(paramMap.get("categoryNo")), paramMap);
		
		Map<String,Object> childPparamMap = new HashMap<String,Object>();
		childPparamMap.put("categoryNo", categoryNo);
		childPparamMap.put("depth", depth);
		childPparamMap.put("siteNo", login.getSiteNo());
		List<Map<String, Object>> chdList = getCategoryChild(childPparamMap);
		
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> child : chdList) {
			String childCate  = String.valueOf(child.get("categoryNo"));
			if((int)child.get("depth") > depth) {
				if(depth == 1) {
					child.replace("item", String.valueOf(paramMap.get("categoryNm")));
				}
				String chdFullItem = (String)child.get("fullItem");
				String [] reChdFullItem = chdFullItem.split(">");
				reChdFullItem[depth] = (String)paramMap.get("categoryNm"); //전체항목을 변경된 String 으로 바꿈
				child.replace("fullItem", String.join(">", reChdFullItem));
				child.put("_id", childCate);
				mapList.add(child);
			} 
		}
		if(mapList.size() > 0 && action) {
			action = elasticSearchUpdateRepository.updateBulkData(ElasticSearchIndex.INDEX_NAME_CLASSIFY_CATEGORY, mapList);
		}
		
		result.put("id", categoryNo);
		result.put("success", action);
		return result;
	}
	
	@Override
	public boolean insertCategoryBulk(List<Map<String, Object>> listMap) throws Exception {
		boolean result = true;
		result = elasticSearchInsertRepository.insertBulkData(ElasticSearchIndex.INDEX_NAME_CLASSIFY_CATEGORY, listMap);
		return result;
	}

	@Override
	public boolean updateCategoryBulk(List<Map<String, Object>> listMap) throws Exception {
		boolean result = true;
		result = elasticSearchUpdateRepository.updateBulkData(ElasticSearchIndex.INDEX_NAME_CLASSIFY_CATEGORY, listMap);
		return result;
	}

	@Override
	public boolean deleteCategoryInfo(Map<String, Object> paramMap) throws Exception {
		boolean result = true;
		List<String> categoryArray = new ArrayList<String>();
		String categoryNo = StringUtil.nvl(paramMap.get("categoryNo"));
		if(categoryNo.contains(":")) {
			String[] userArr = StringUtil.split(categoryNo,":");
			for(String uNo : userArr){
				categoryArray.add("categoryNo:"+StringUtil.nvl(uNo));
			}
		} else if( !"".equals(categoryNo)){
			categoryArray.add("categoryNo:"+StringUtil.nvl(categoryNo));
		}
		if (categoryArray.size() > 0) {
			result = elasticSearchDeleteRepository.deleteDataByQuery(ElasticSearchIndex.INDEX_NAME_CLASSIFY_CATEGORY, StringUtils.join(categoryArray,','));
		}
		return result;
	}

	@Override
	public boolean dupCategoryInfo(Map<String, Object> paramMap) throws Exception {
		boolean result = false;
		List<Map<String, Object>> resultList = null;
		
		int from = 0;
		int rownum = 1;	
		String sortType = "";
		String sortOrder = "";
		
		List<Map<String,Object>> matchList = new ArrayList<Map<String,Object>>(); //filter Option
		Map<String,Object> match = null;
		
		if(paramMap.get("categoryNm") != null && !paramMap.get("categoryNm").equals("")) {
			match = new HashMap<String,Object>();
			match.put("type", "match");
			match.put("key", "categoryNm"); //match.put("key", "categoryNm.keyword");
			match.put("value", paramMap.get("categoryNm"));
			matchList.add(match);
		}
		if(paramMap.get("depth") != null && !paramMap.get("depth").equals("")) {
			match = new HashMap<String,Object>();
			match.put("type", "match");
			match.put("key", "depth");
			match.put("value", paramMap.get("depth"));
			matchList.add(match);
		}
		if(paramMap.get("categoryNo") != null && !paramMap.get("categoryNo").equals("")) {
			match = new HashMap<String,Object>();
			match.put("type", "not");
			match.put("key", "categoryNo");
			match.put("value", Integer.parseInt(String.valueOf(paramMap.get("categoryNo"))));
			matchList.add(match);
		}
		if(paramMap.get("siteNo") != null) {
			match = new HashMap<String,Object>();
			match.put("type", "match");
			match.put("key", "siteNo");
			match.put("value", paramMap.get("siteNo"));
			matchList.add(match);
		}
		if(paramMap.get("pCategoryNo") != null) {
			match = new HashMap<String,Object>();
			match.put("type", "match");
			match.put("key", "pCategoryNo");
			match.put("value", paramMap.get("pCategoryNo"));
			matchList.add(match);
		}
		
		resultList = elasticSearchSelectRepository.selectDataListByBoolQuery(ElasticSearchIndex.INDEX_NAME_CLASSIFY_CATEGORY, from, rownum, sortType, sortOrder, matchList);
		
		if (resultList.size() > 0) {
			result = true;
		}
		return result;
	}

	@Override
	public int selectMaxId() throws Exception {
		int key = elasticSearchSelectRepository.selectMaxId(ElasticSearchIndex.INDEX_NAME_CLASSIFY_CATEGORY, "categoryNo");
		return key;
	}
}
