package kr.co.proten.manager.login.service.Impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;

import kr.co.proten.manager.common.elastic.ElasticSearchConstant;
import kr.co.proten.manager.common.elastic.ElasticSearchIndex;
import kr.co.proten.manager.common.repository.ElasticSearchInsertRepository;
import kr.co.proten.manager.common.repository.ElasticSearchSelectRepository;
import kr.co.proten.manager.common.repository.ElasticSearchUpdateRepository;
import kr.co.proten.manager.common.util.StringUtil;
import kr.co.proten.manager.login.model.LoginModel;
import kr.co.proten.manager.login.repository.LoginRepository;
import kr.co.proten.manager.login.service.LoginService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
	
	private static final Logger log = LoggerFactory.getLogger(LoginServiceImpl.class);
	
	private final ElasticSearchSelectRepository elasticSearchSelectRepository;
	
	private final ElasticSearchInsertRepository elasticSearchInsertRepository;
	
	private final ElasticSearchUpdateRepository elasticSearchUpdateRepository;
		
	private final LoginRepository loginRepository;
	
	@Value("${project.version}") 
	String version;
	
	@Value("${project.timestamp}") 
	String bldTimestamp;

	@Override
	public boolean healthElasticSearch() {
		return elasticSearchSelectRepository.healthCheck();
	}

	@Override
	public LoginModel selectUserInfo(String userId) throws Exception {
		
		LoginModel loginModel = new LoginModel();
		Map<String,Object> userInfoMap = elasticSearchSelectRepository.selectDataById(ElasticSearchIndex.INDEX_NAME_USER, "userId", userId);
		
		if(userInfoMap != null) {
			loginModel.setUserNo((int)userInfoMap.get("userNo"));
			loginModel.setUserId((String)userInfoMap.get("userId"));
			loginModel.setPno((String)userInfoMap.get("pno"));
			loginModel.setUserNm((String)userInfoMap.get("userNm"));
			loginModel.setGroupNo(StringUtil.nvl(userInfoMap.get("groupNo"),0));
			loginModel.setCreateUser((String)userInfoMap.get("createUser"));
			loginModel.setCreateUserNm((String)userInfoMap.get("createUserNm"));
			loginModel.setCreateDate(String.valueOf(userInfoMap.get("createDate")));
			loginModel.setModifyUser((String)userInfoMap.get("modifyUser"));
			loginModel.setModifyUserNm((String)userInfoMap.get("modifyUserNm"));
			loginModel.setModifyDate(String.valueOf(userInfoMap.get("modifyDate")));
			loginModel.setAdminYn((String)userInfoMap.get("adminYn"));
			loginModel.setLoginCount(StringUtil.nvl(userInfoMap.get("loginCount"),0));
			loginModel.setUseYn(StringUtil.nvl(userInfoMap.get("useYn"),"y"));
			
			/** 로그인 후 아이디별 권한설정 **/
			//그룹 조회
			List<Integer> groupSiteList = loginRepository.selectGroupMappingSiteList(String.valueOf(loginModel.getGroupNo()));
			int siteNo = (int) groupSiteList.get(0);
			//그룹에 속한 1번째 사이트 선택
			loginModel.setSiteNo(siteNo);
			
			List<Map<String, Object>> menuList = elasticSearchSelectRepository.selectDataListByBoolQuery(ElasticSearchIndex.INDEX_NAME_MENU, 0, 100, null);
			List<Integer> mappingList = loginRepository.selectUserMappingMenuList(String.valueOf(siteNo));
			List<Map<String, Object>> menuAuthList = new ArrayList<>();
			for(Map<String, Object> result : menuList) {
				int id = (int) result.get("menuId");
				result.put("auth", 0);
				result.put("userNo", loginModel.getUserNo());

				if ( mappingList.contains(id) ) {
					menuAuthList.add(result);
				}
			}
			menuAuthList = StringUtil.mapComparator(menuAuthList,"orderSeq");
			loginModel.setMenuList(menuAuthList);
			StringBuilder queryString = new StringBuilder();
			for (int stNo : groupSiteList) {
				queryString.append(" siteNo:" + StringUtil.getMatchToString(String.valueOf(stNo)) + "");
			}
			
			List<Map<String, Object>> siteList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_SITE, 0, 100, "siteNo", ElasticSearchConstant.ASC, queryString.toString());
			loginModel.setSiteList(siteList);
		}
		
		/** 빌드정보 **/
		loginModel.setBldVersion(version);
		loginModel.setBldTimestamp(bldTimestamp);
		return loginModel;
	}

	@Override
	public boolean updateLoginCount(String userNo, Map<String, Object> paramMap) {
		boolean result = true;
		try {
			result = elasticSearchUpdateRepository.updateData(ElasticSearchIndex.INDEX_NAME_USER, userNo, paramMap);
		} catch (IOException e) {
			log.error(e.getMessage());
			return false;
		}
		return result;
	}

	@Override
	public boolean insertSystemLog(long systemTime, String sucessYn, String message) {
		boolean result = true;
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("systemLogNo", systemTime);
		paramMap.put("type", "ETC");
		paramMap.put("sucessYn", sucessYn);
		paramMap.put("rtype", "STA");
		paramMap.put("total", 0);
		paramMap.put("sucess", 0);
		paramMap.put("fail", 0);
		paramMap.put("took", 0);
		paramMap.put("indexName", "");
		paramMap.put("etc",  message);
		paramMap.put("source", "");
		paramMap.put("createDate", systemTime);
		
		try {
			result = elasticSearchInsertRepository.insertData(ElasticSearchIndex.INDEX_NAME_SYSTEM_LOG, String.valueOf(systemTime), paramMap);
		} catch (IOException e) {
			log.error(e.getMessage());
			return false;
		}
		return result;
	}
}
