package kr.co.proten.manager.system.service.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.apache.tomcat.util.buf.StringUtils;

import kr.co.proten.manager.common.ClassifyManagerConstant;
import kr.co.proten.manager.common.elastic.ElasticSearchConstant;
import kr.co.proten.manager.common.elastic.ElasticSearchIndex;
import kr.co.proten.manager.common.repository.ElasticSearchSelectRepository;
import kr.co.proten.manager.common.repository.ElasticSearchUpdateRepository;
import kr.co.proten.manager.common.repository.ElasticSearchDeleteRepository;
import kr.co.proten.manager.common.repository.ElasticSearchInsertRepository;
import kr.co.proten.manager.common.util.DateUtil;
import kr.co.proten.manager.common.util.StringUtil;
import kr.co.proten.manager.login.model.LoginModel;
import kr.co.proten.manager.system.service.SiteService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService {

	private static final Logger log = LoggerFactory.getLogger(SiteServiceImpl.class);

	private final ElasticSearchSelectRepository elasticSearchSelectRepository;
	
	private final ElasticSearchInsertRepository elasticSearchInsertRepository;
	
	private final ElasticSearchUpdateRepository elasticSearchUpdateRepository;
	
	private final ElasticSearchDeleteRepository elasticSearchDeleteRepository;
	
	@Override
	public List<Map<String, Object>> selectSiteList(Map<String, Object> paramMap) throws Exception {
		
		List<Map<String, Object>> resultList = null;
		
		int from = Integer.parseInt(StringUtil.nvl(paramMap.get("start"), "0"));
		int rownum = Integer.parseInt(StringUtil.nvl(paramMap.get("lineNo"), "100"));
		String sortType = StringUtil.nvl(paramMap.get("sort"), "siteNo");
		String sortOrder = sortType.contains("Date") ? ElasticSearchConstant.DESC : ElasticSearchConstant.ASC;
		
		StringBuilder queryString = new StringBuilder();
		if (!StringUtil.nvl(paramMap.get("siteNo")).equals("")) {
			String[] siteNos = StringUtil.nvl(paramMap.get("siteNo")).split(",");
			for (String stNo : siteNos) {
				queryString.append(" siteNo:" + StringUtil.getMatchToString(stNo) + "");
			}
		}
		if (!StringUtil.nvl(paramMap.get("site")).equals("")) {
			if (queryString.length() != 0) {
				queryString.append(" AND ");
			}
			queryString.append(" site:" + paramMap.get("site"));
		}
		if (!StringUtil.nvl(paramMap.get("useYn")).equals("")) {
			if (queryString.length() != 0) {
				queryString.append(" AND ");
			}
			queryString.append(" useYn:" + paramMap.get("useYn"));
		}
		resultList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_SITE, from, rownum, sortType, sortOrder, queryString.toString());
		return resultList;
	}
	
	@Override
	public boolean insertSiteInfo(Map<String, Object> paramMap, HttpSession session) throws Exception {
		boolean result = true;
		LoginModel login = (LoginModel) session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("createUser", login.getUserId());
		paramMap.put("createUserNm", login.getUserNm());
		paramMap.put("createDate", DateUtil.getCurrentDateTimeMille());
		paramMap.put("modifyUser", login.getUserId());
		paramMap.put("modifyUserNm", login.getUserNm());
		paramMap.put("modifyDate", DateUtil.getCurrentDateTimeMille());
		
		int key = elasticSearchSelectRepository.selectMaxId(ElasticSearchIndex.INDEX_NAME_SITE, "siteNo");
		paramMap.put("siteNo", key);
		result = elasticSearchInsertRepository.insertData(ElasticSearchIndex.INDEX_NAME_SITE, String.valueOf(key), paramMap);
		return result;
	}

	@Override
	public boolean updateSiteInfo(Map<String, Object> paramMap, HttpSession session) throws Exception {
		boolean result = true;
		LoginModel login = (LoginModel) session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("modifyUser", login.getUserId());
		paramMap.put("modifyUserNm", login.getUserNm());
		paramMap.put("modifyDate", DateUtil.getCurrentDateTimeMille());
		
		result = elasticSearchUpdateRepository.updateData(ElasticSearchIndex.INDEX_NAME_SITE, String.valueOf(paramMap.get("siteNo")), paramMap);
		return result;
	}

	@Override
	public boolean deleteSiteInfo(Map<String, Object> paramMap) throws Exception {
		boolean result = true;
		List<String> siteArray = new ArrayList<String>();
		String siteNo = StringUtil.nvl(paramMap.get("siteNo"));
		if (siteNo.contains(":")) {
			String[] siteArr = StringUtil.split(siteNo, ":");
			for (String no : siteArr) {
				siteArray.add("siteNo:"+StringUtil.nvl(no));
			}
		} else if ("".equals(siteNo) == false) {
			siteArray.add("siteNo:"+StringUtil.nvl(siteNo));
		}
		if (siteArray.size() > 0) {
			result = elasticSearchDeleteRepository.deleteDataByQuery(ElasticSearchIndex.INDEX_NAME_ALL_, StringUtils.join(siteArray,','));
		}
		return result;
	}
	
	@Override
	public boolean dupSiteInfo(Map<String, Object> paramMap) throws Exception {
		boolean result = false;
		long totalCount = elasticSearchSelectRepository.selectDataCountById(ElasticSearchIndex.INDEX_NAME_SITE, "site", String.valueOf(paramMap.get("site")));
		if (totalCount > 0) {
			result = true;
		}
		return result;
	}
}
