package kr.co.proten.manager.system.service.Impl;

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
import kr.co.proten.manager.system.service.UserService;
import lombok.RequiredArgsConstructor;

import org.apache.tomcat.util.buf.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

	private final ElasticSearchSelectRepository elasticSearchSelectRepository;
	
	private final ElasticSearchInsertRepository elasticSearchInsertRepository;
	
	private final ElasticSearchUpdateRepository elasticSearchUpdateRepository;
	
	private final ElasticSearchDeleteRepository elasticSearchDeleteRepository;
	
	@Override
	public List<Map<String, Object>> selectUserList(Map<String, Object> paramMap) throws Exception {
		
		List<Map<String, Object>> resultList = null;
		
		int from = Integer.parseInt(StringUtil.nvl(paramMap.get("start"), "0"));
		int rownum = Integer.parseInt(StringUtil.nvl(paramMap.get("lineNo"),"10"));
		String sortType = StringUtil.nvl(paramMap.get("sort"),"");
		String sortOrder = ElasticSearchConstant.ASC;
		
		StringBuilder queryString = new StringBuilder();
		if(!StringUtil.nvl(paramMap.get("userNo")).equals("")) {
			queryString.append( " userNo:"+paramMap.get("userNo"));
		}
		resultList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_USER, from, rownum, sortType, sortOrder, queryString.toString());
		
		return resultList;
	}
	
	@Override
	public boolean insertUserInfo(Map<String, Object> paramMap, HttpSession session) throws Exception {
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
		
		int key = elasticSearchSelectRepository.selectMaxId(ElasticSearchIndex.INDEX_NAME_USER, "userNo");
		paramMap.put("userNo", key);
		result = elasticSearchInsertRepository.insertData(ElasticSearchIndex.INDEX_NAME_USER, String.valueOf(key), paramMap);
		return result;
	}
	
	@Override
	public boolean updateUserInfo(Map<String, Object> paramMap, HttpSession session) throws Exception {
		boolean result = true;
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		if (paramMap.containsKey("pno") && !"".equals(StringUtil.nvl(paramMap.get("pno"),"")) ) {
			paramMap.put("pno", AESCryptoHelper.encryptThisString((String) paramMap.get("pno"), (String) paramMap.get("userId")));
		}
		if (paramMap.containsKey("loginCount")  ) {
			paramMap.put("loginCount", paramMap.get("loginCount"));
		}
		if (paramMap.containsKey("nowpno") ) {
			paramMap.remove("nowpno");
		}
		paramMap.put("modifyUser", login.getUserId());
		paramMap.put("modifyUserNm", login.getUserNm());
		paramMap.put("modifyDate", DateUtil.getCurrentDateTimeMille());
		result = elasticSearchUpdateRepository.updateData(ElasticSearchIndex.INDEX_NAME_USER, String.valueOf(paramMap.get("userNo")), paramMap);
		return result;
	}

	@Override
	public boolean deleteUserInfo(Map<String, Object> paramMap) throws Exception {
		boolean result = true;
		List<String> userArray = new ArrayList<String>();
		String userNo = StringUtil.nvl(paramMap.get("userNo"));
		if (userNo.contains(":")) {
			String[] userArr = StringUtil.split(userNo, ":");
			for (String no : userArr) {
				userArray.add("userNo:"+StringUtil.nvl(no));
			}
		} else if ("".equals(userNo) == false) {
			userArray.add("userNo:"+StringUtil.nvl(userNo));
		}
		if (userArray.size() > 0) {
			result = elasticSearchDeleteRepository.deleteDataByQuery(ElasticSearchIndex.INDEX_NAME_USER, StringUtils.join(userArray,','));
		}
		return result;
	}

	@Override
	public boolean dupUserInfo(Map<String, Object> paramMap) throws Exception {
		boolean result = false;
		long totalCount = elasticSearchSelectRepository.selectDataCountById(ElasticSearchIndex.INDEX_NAME_USER, "userId", String.valueOf(paramMap.get("userId")));
		if (totalCount > 0) {
			result = true;
		}
		return result;
	}
	
	@Override
	public boolean isUserPasswordCheck(Map<String, Object> paramMap) throws Exception {
		boolean result = false;
		if ( paramMap.containsKey("nowpno")  ) {
			paramMap.put("nowpno", AESCryptoHelper.encryptThisString((String) paramMap.get("nowpno"), (String) paramMap.get("userId")));
		}
		
		List<Map<String, Object>> resultList = null;
		
		int from = Integer.parseInt(StringUtil.nvl(paramMap.get("start"), "0"));
		int rownum = Integer.parseInt(StringUtil.nvl(paramMap.get("lineNo"),"10"));
		String sortType = StringUtil.nvl(paramMap.get("sort"),"");
		String sortOrder = ElasticSearchConstant.ASC;
		
		StringBuilder queryString = new StringBuilder();
		if(!StringUtil.nvl(paramMap.get("userNo")).equals("")) {
			queryString.append( " userNo:"+paramMap.get("userNo"));
		}
		if(!StringUtil.nvl(paramMap.get("nowpno")).equals("")) {
			if(queryString.length()!=0) {
				queryString.append(" AND ");
			}
			queryString.append( " pno:"+paramMap.get("nowpno"));
		}		
		resultList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_USER, from, rownum, sortType, sortOrder, queryString.toString());
		if (resultList.size() > 0) {
			result = true;
		}
		return result;
	}	
}
