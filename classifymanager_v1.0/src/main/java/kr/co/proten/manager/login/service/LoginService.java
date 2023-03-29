package kr.co.proten.manager.login.service;

import java.util.Map;

import kr.co.proten.manager.login.model.LoginModel;

public interface LoginService {

	/**
	 * 엘라스틱 상태 확인
	 * @return
	 */
	boolean healthElasticSearch();
	
	/**
	 * 사용자 정보 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	LoginModel selectUserInfo(String userId) throws Exception;
	
	/**
	 * 로그인 카운트 업데이트
	 * @param paramMap
	 * @return
	 */
	boolean updateLoginCount(String userNo, Map<String, Object> paramMap) ;
	
	/**
	 * 시스템 로그 적재
	 * @param systemTime
	 * @param sucessYn
	 * @param message
	 * @return
	 */
	boolean insertSystemLog(long systemTime, String sucessYn, String message);
	
}
