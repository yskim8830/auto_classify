package kr.co.proten.manager.system.service.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.buf.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;

import kr.co.proten.manager.common.ClassifyManagerConstant;
import kr.co.proten.manager.common.elastic.ElasticSearchConstant;
import kr.co.proten.manager.common.elastic.ElasticSearchIndex;
import kr.co.proten.manager.common.repository.ElasticSearchDeleteRepository;
import kr.co.proten.manager.common.repository.ElasticSearchInsertRepository;
import kr.co.proten.manager.common.repository.ElasticSearchSelectRepository;
import kr.co.proten.manager.common.repository.ElasticSearchUpdateRepository;
import kr.co.proten.manager.common.util.AESCryptoHelper;
import kr.co.proten.manager.common.util.DateUtil;
import kr.co.proten.manager.common.util.StringUtil;
import kr.co.proten.manager.login.model.LoginModel;
import kr.co.proten.manager.login.repository.LoginRepository;
import kr.co.proten.manager.system.service.GroupService;
import kr.co.proten.manager.system.service.SiteService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

	private static final Logger log = LoggerFactory.getLogger(GroupServiceImpl.class);
	
	private final ElasticSearchSelectRepository elasticSearchSelectRepository;
	
	private final ElasticSearchInsertRepository elasticSearchInsertRepository;
	
	private final ElasticSearchUpdateRepository elasticSearchUpdateRepository;
	
	private final ElasticSearchDeleteRepository elasticSearchDeleteRepository;
	
	private final LoginRepository loginRepository;
	
	private final SiteService siteService;
	
	@Override
	public List<Map<String, Object>> selectGroupList(Map<String, Object> paramMap) throws Exception {
		
		List<Map<String, Object>> resultList = null;
		int from = Integer.parseInt(StringUtil.nvl(paramMap.get("start"), "0"));
		int rownum = Integer.parseInt(StringUtil.nvl(paramMap.get("lineNo"),"10"));
		String sortType = StringUtil.nvl((String)paramMap.get("sort"),"groupNo");
		String sortOrder = ElasticSearchConstant.ASC;
		
		StringBuilder queryString = new StringBuilder();
		if(!StringUtil.nvl(paramMap.get("groupNo")).equals("")) {
			queryString.append( " groupNo:"+paramMap.get("groupNo"));
		}
		if(!StringUtil.nvl(paramMap.get("useYn")).equals("")) {
			if(queryString.length()!=0) {
				queryString.append(" AND ");
			}
			queryString.append( " useYn:"+paramMap.get("useYn"));
		}
		resultList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_GROUP, from, rownum, sortType, sortOrder, queryString.toString());
		
		if(StringUtil.nvl(paramMap.get("siteList")).equals("Y")) {
			List<Map<String, Object>> _resultList = new ArrayList<Map<String, Object>>();
			Map<String, Object> _paramMap = new HashMap<String, Object>();
			for(Map<String, Object> group : resultList) {
				_paramMap.put("groupNo", group.get("groupNo"));
				List<Map<String, Object>> siteList = selectSiteToGroup(_paramMap);
				group.put("siteList", siteList);
				_resultList.add(group);
			}
			return _resultList;
		}
		return resultList;
	}
	
	@Override
	public List<Map<String, Object>> selectSiteToGroup(Map<String, Object> paramMap) throws Exception {
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		
		List<Integer> groupSiteList = loginRepository.selectGroupMappingSiteList(String.valueOf(paramMap.get("groupNo")));
		
		if(groupSiteList.size() > 0) {
			String site = Joiner.on(',').join(groupSiteList);
			paramMap.put("siteNo", site);
			resultList = siteService.selectSiteList(paramMap);			
		}
		return resultList;
	}

	@Override
	public boolean insertGroupInfo(Map<String, Object> paramMap, HttpSession session) throws Exception {
		boolean result = true;
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("pno", AESCryptoHelper.encryptThisString((String)paramMap.get("pno") , (String) paramMap.get("userId")));
		paramMap.put("createUser", login.getUserId());
		paramMap.put("createUserNm", login.getUserNm());
		paramMap.put("createDate", DateUtil.getCurrentDateTimeMille());
		paramMap.put("modifyUser", login.getUserId());
		paramMap.put("modifyUserNm", login.getUserNm());
		paramMap.put("modifyDate", DateUtil.getCurrentDateTimeMille());
		paramMap.put("loginCount",0);
		
		int key = elasticSearchSelectRepository.selectMaxId(ElasticSearchIndex.INDEX_NAME_GROUP, "groupNo");
		paramMap.put("groupNo", key);
		result = elasticSearchInsertRepository.insertData(ElasticSearchIndex.INDEX_NAME_GROUP, String.valueOf(key), paramMap);
		return result;
	}

	@Override
	public boolean updateGroupInfo(Map<String, Object> paramMap, HttpSession session) throws Exception {
		boolean result = true;
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("modifyUser", login.getUserId());
		paramMap.put("modifyUserNm", login.getUserNm());
		paramMap.put("modifyDate", DateUtil.getCurrentDateTimeMille());
		
		result = elasticSearchUpdateRepository.updateData(ElasticSearchIndex.INDEX_NAME_GROUP, String.valueOf(paramMap.get("groupNo")), paramMap);
		return result;
	}

	@Override
	public Map<String,Object> deleteGroupInfo(Map<String, Object> paramMap) throws Exception {
		Map<String,Object> result = new HashMap<String,Object>();
		List<String> groupArray = new ArrayList<String>();
		
		String groupNo = StringUtil.nvl(paramMap.get("groupNo"));
		if(groupNo.contains(":")) {
			String[] arr = StringUtil.split(groupNo,":");
			for(String no : arr){
				groupArray.add("groupNo:"+no);
				List<Integer> groupMapping = loginRepository.selectGroupMappingSiteList(no);
				if(groupMapping.size() > 0) {
					result.put("msg", "삭제하려는 그룹중 권한을 가진 그룹은 삭제할 수 없습니다.");
					result.put("success", false);
					return result;
				}
			}
		}else if(!"".equals(groupNo)){
			groupArray.add("groupNo:"+groupNo);
			List<Integer> groupMapping = loginRepository.selectGroupMappingSiteList(groupNo);
			if(groupMapping.size() > 0) {
				result.put("msg", "권한을 가진 그룹은 삭제할 수 없습니다.");
				result.put("success", false);
				return result;
			}
		}
		if (groupArray.size() > 0) {
			boolean ret = elasticSearchDeleteRepository.deleteDataByQuery(ElasticSearchIndex.INDEX_NAME_GROUP, StringUtils.join(groupArray,','));
			result.put("success", ret);
		}
		return result;
	}

	@Override
	public boolean dupGroupInfo(Map<String, Object> paramMap) throws Exception {
		boolean result = false;
		long totalCount = elasticSearchSelectRepository.selectDataCountById(ElasticSearchIndex.INDEX_NAME_GROUP, "group", String.valueOf(paramMap.get("group")));
		if (totalCount > 0) {
			result = true;
		}
		return result;
	}
	
	
	@Override
	public boolean insertAuthInfo(Map<String, Object> paramMap, HttpSession session) throws Exception {
		boolean result = true;
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("loginUser", login.getUserId());
		
		String _id = dupAuthInfo(paramMap);
		if("".equals(_id)) {
			paramMap.put("createUser", login.getUserId());
			paramMap.put("createUserNm", login.getUserNm());
			paramMap.put("createDate", DateUtil.getCurrentDateTimeMille());
			paramMap.put("modifyUser", login.getUserId());
			paramMap.put("modifyUserNm", login.getUserNm());
			paramMap.put("modifyDate", DateUtil.getCurrentDateTimeMille());
			
			int key = elasticSearchSelectRepository.selectMaxId(ElasticSearchIndex.INDEX_NAME_GROUP_MAPPING, "mappingNo");
			paramMap.put("mappingNo", key);
			result = elasticSearchInsertRepository.insertData(ElasticSearchIndex.INDEX_NAME_GROUP_MAPPING, String.valueOf(key), paramMap);
		} else {
			result = deleteAuthInfo(paramMap);
		}
		
		return result;
	}
	
	@Override
	public boolean deleteAuthInfo(Map<String, Object> paramMap) throws Exception {
		boolean result = true;
		
		String _id = dupAuthInfo(paramMap);
		
		if(!"".equals(_id)) {
			result = elasticSearchDeleteRepository.deleteDataByQuery(ElasticSearchIndex.INDEX_NAME_GROUP_MAPPING, "_id:"+StringUtil.nvl(_id));
		}
		return result;
	}
	
	@Override
	public String dupAuthInfo(Map<String, Object> paramMap) throws Exception {
		List<Map<String,Object>> matchList = new ArrayList<Map<String,Object>>(); //filter Option
		if(!StringUtil.nvl(paramMap.get("groupNo")).equals("")) {
			HashMap<String,Object> match = new HashMap<String,Object>();
			match.put("type", "match");
			match.put("key", "groupNo");
			match.put("value", paramMap.get("groupNo"));
			match.put("format", "");
			matchList.add(match);
		}
		if(!StringUtil.nvl(paramMap.get("siteNo")).equals("")) {
			HashMap<String,Object> match = new HashMap<String,Object>();
			match.put("type", "match");
			match.put("key", "siteNo");
			match.put("value", paramMap.get("siteNo"));
			match.put("format", "");
			matchList.add(match);
		}
		String id = elasticSearchSelectRepository.selectDataIdByBoolQuery(ElasticSearchIndex.INDEX_NAME_GROUP_MAPPING, matchList);
		return id;
	}
	
	@Override
	public boolean insertAuthSiteInfo(Map<String, Object> paramMap, HttpSession session) throws Exception {
		boolean result = true;
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		
		String[] menuIds = (String[]) paramMap.get("menuIds");
		
		for (int i = 0; i < menuIds.length; i++) {
			String [] value = StringUtil.split(menuIds[i],"_");
			String menuId = value[0];
			String authYn = value[1];
			Map<String, Object> siteMappingParamMap = new HashMap<String, Object>();
			siteMappingParamMap.put("menuId", menuId);
			siteMappingParamMap.put("siteNo", (String)paramMap.get("siteNo"));
			
			if(authYn.equals("Y")) {
				String _id = dupAuthSiteInfo(siteMappingParamMap);
				if("".equals(_id)) {
					siteMappingParamMap.put("createUser", login.getUserId());
					siteMappingParamMap.put("createUserNm", login.getUserNm());
					siteMappingParamMap.put("createDate", DateUtil.getCurrentDateTimeMille());
					siteMappingParamMap.put("modifyUser", login.getUserId());
					siteMappingParamMap.put("modifyUserNm", login.getUserNm());
					siteMappingParamMap.put("modifyDate", DateUtil.getCurrentDateTimeMille());
					
					int key = elasticSearchSelectRepository.selectMaxId(ElasticSearchIndex.INDEX_NAME_SITE_MAPPING, "id");
					siteMappingParamMap.put("id", key);
					result = elasticSearchInsertRepository.insertData(ElasticSearchIndex.INDEX_NAME_SITE_MAPPING, String.valueOf(key), siteMappingParamMap);
				}
			} else {
				result = deleteAuthSiteInfo(siteMappingParamMap);
			}
		}		
		return result;
	}
	
	@Override
	public boolean deleteAuthSiteInfo(Map<String, Object> paramMap) throws Exception {
		boolean result = true;
		
		String _id = dupAuthSiteInfo(paramMap);
		
		if(!"".equals(_id)) {
			result = elasticSearchDeleteRepository.deleteDataByQuery(ElasticSearchIndex.INDEX_NAME_SITE_MAPPING, "id:"+StringUtil.nvl(_id));
		}
		return result;
	}
	
	@Override
	public String dupAuthSiteInfo(Map<String, Object> paramMap) throws Exception {
		List<Map<String,Object>> matchList = new ArrayList<Map<String,Object>>(); //filter Option
		if(!StringUtil.nvl(paramMap.get("menuId")).equals("")) {
			HashMap<String,Object> match = new HashMap<String,Object>();
			match.put("type", "match");
			match.put("key", "menuId");
			match.put("value", paramMap.get("menuId"));
			match.put("format", "");
			matchList.add(match);
		}
		if(!StringUtil.nvl(paramMap.get("siteNo")).equals("")) {
			HashMap<String,Object> match = new HashMap<String,Object>();
			match.put("type", "match");
			match.put("key", "siteNo");
			match.put("value", paramMap.get("siteNo"));
			match.put("format", "");
			matchList.add(match);
		}		
		String id = elasticSearchSelectRepository.selectDataIdByBoolQuery(ElasticSearchIndex.INDEX_NAME_SITE_MAPPING, matchList);
		return id;
	}
	
	@Override
	public List<Map<String, Object>> selectAuthMenuList(Map<String, Object> paramMap) throws Exception {
		
		List<Map<String, Object>> resultList = null;
		List<Integer> mappingList = null;
		
		int from = Integer.parseInt(StringUtil.nvl(paramMap.get("start"), "0"));
		int rownum = Integer.parseInt(StringUtil.nvl(paramMap.get("lineNo"),"1000"));
		String sortType = StringUtil.nvl(paramMap.get("sort"),"");
		String sortOrder = ElasticSearchConstant.ASC;
		
		StringBuilder queryString = new StringBuilder();
		queryString.append( " delYn:N");
		
		resultList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_MENU, from, rownum, sortType, sortOrder, queryString.toString());
		mappingList = loginRepository.selectUserMappingMenuList(String.valueOf(paramMap.get("siteNo")));
		
		for(Map<String, Object> result : resultList) {
			int id = (int) result.get("menuId");
			result.put("userNo", paramMap.get("userNo"));
			//권한이 있으면 auth 값을 1로 한다.
			if ( mappingList.contains(id) ) {
				result.put("auth", 1);
			} else {
				result.put("auth", 0);
			}
		}
		resultList = StringUtil.mapComparator(resultList,"orderSeq");
		return resultList;
	}
}
