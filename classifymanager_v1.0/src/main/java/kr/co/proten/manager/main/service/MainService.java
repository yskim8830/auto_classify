package kr.co.proten.manager.main.service;

import java.util.List;
import java.util.Map;

import kr.co.proten.manager.login.model.LoginModel;

public interface MainService {

	/**
	 * 사이트 변경
	 * @param model
	 * @param siteNo
	 * @throws Exception
	 */
	void modifyUserSiteInfo(LoginModel model, int siteNo) throws Exception;
	
	/**
	 * System Info
	 * @return
	 * @throws Exception
	 */
	Map<String,Object> getCatNodes() throws Exception;
	
	/**
	 * Disk 사용률
	 * @return
	 * @throws Exception
	 */
	Map<String,Object> getCatAllocation() throws Exception;
	
	/**
	 * 통계 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	Map<String, List<Map<String, Object>>> selectStatisticsDataList(String siteNo, String startDate, String endDate, String stdType) throws Exception;
}
