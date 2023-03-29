package kr.co.proten.manager.classify.service.Impl;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kr.co.proten.manager.classify.service.ClassifyDataService;
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
public class ClassifyDataServiceImpl implements ClassifyDataService {

	private static final Logger log = LoggerFactory.getLogger(ClassifyDataServiceImpl.class);
	
	private final ElasticSearchSelectRepository elasticSearchSelectRepository;
	
	private final ElasticSearchInsertRepository elasticSearchInsertRepository;
	
	private final ElasticSearchUpdateRepository elasticSearchUpdateRepository;
	
	private final ElasticSearchDeleteRepository elasticSearchDeleteRepository;

	@Override
	public List<Map<String, Object>> selectDataList(Map<String, Object> paramMap) throws Exception {
		List<Map<String, Object>> resultList = null;
		
		int from = Integer.parseInt(StringUtil.nvl(paramMap.get("start"), "0"));
		int rownum = Integer.parseInt(StringUtil.nvl(paramMap.get("lineNo"),"10"));
		String sortType = StringUtil.nvl(paramMap.get("sort"),"dataNo");
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
		resultList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_CLASSIFY_DATA, from, rownum, sortType, sortOrder, queryString.toString());
		
		return resultList;
	}
	
	@Override
	public List<Map<String, Object>> selectDataListAll(Map<String, Object> paramMap) throws Exception {
		List<Map<String, Object>> resultList = null;
		
		int from = 0;
		int rownum = 100000;
		String sortType = StringUtil.nvl(paramMap.get("sort"),"dataNo");
		String sortOrder = ElasticSearchConstant.ASC;
		
		StringBuilder queryString = new StringBuilder();
		if(!StringUtil.nvl(paramMap.get("siteNo")).equals("")) {
			queryString.append( " siteNo:"+paramMap.get("siteNo"));
		}
		resultList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_CLASSIFY_DATA, from, rownum, sortType, sortOrder, queryString.toString());
		
		return resultList;
	}

	@Override
	public boolean insertDataInfo(Map<String, Object> paramMap, HttpSession session) throws Exception {
		boolean result = true;
		LoginModel login = (LoginModel) session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("createUser", login.getUserId());
		paramMap.put("createUserNm", login.getUserNm());
		paramMap.put("createDate", DateUtil.getCurrentDateTimeMille());
		paramMap.put("modifyUser", login.getUserId());
		paramMap.put("modifyUserNm", login.getUserNm());
		paramMap.put("modifyDate", DateUtil.getCurrentDateTimeMille());
		paramMap.put("siteNo", login.getSiteNo());
		
		int key = elasticSearchSelectRepository.selectMaxId(ElasticSearchIndex.INDEX_NAME_CLASSIFY_DATA, "dataNo");
		paramMap.put("dataNo", key);
		result = elasticSearchInsertRepository.insertData(ElasticSearchIndex.INDEX_NAME_CLASSIFY_DATA, String.valueOf(key), paramMap);
		return result;
	}

	@Override
	public boolean updateDataInfo(Map<String, Object> paramMap, HttpSession session) throws Exception {
		boolean result = true;
		LoginModel login = (LoginModel) session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("modifyUser", login.getUserId());
		paramMap.put("modifyUserNm", login.getUserNm());
		paramMap.put("modifyDate", DateUtil.getCurrentDateTimeMille());
		paramMap.put("siteNo", login.getSiteNo());
		
		result = elasticSearchUpdateRepository.updateData(ElasticSearchIndex.INDEX_NAME_CLASSIFY_DATA, String.valueOf(paramMap.get("dataNo")), paramMap);
		return result;
	}

	@Override
	public boolean deleteDataInfo(Map<String, Object> paramMap) throws Exception {
		boolean result = true;
		String dataNo = StringUtil.nvl(paramMap.get("dataNo"));
		result = elasticSearchDeleteRepository.deleteDataById(ElasticSearchIndex.INDEX_NAME_CLASSIFY_DATA, dataNo);
		return result;
	}

	@Override
	public boolean insertDataBulk(List<Map<String, Object>> listMap) throws Exception {
		boolean result = true;
		result = elasticSearchInsertRepository.insertBulkData(ElasticSearchIndex.INDEX_NAME_CLASSIFY_DATA, listMap);
		return result;
	}

	@Override
	public int selectMaxId() throws Exception {
		int key = elasticSearchSelectRepository.selectMaxId(ElasticSearchIndex.INDEX_NAME_CLASSIFY_DATA, "dataNo");
		return key;
	}
	
}
