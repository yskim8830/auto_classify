package kr.co.proten.manager.system.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

public interface UserService {

	/**
	 * 사용자 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectUserList(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 사용자 저장
	 * @param paramMap
	 * @param session
	 * @return
	 * @throws Exception
	 */
	boolean insertUserInfo(Map<String, Object> paramMap, HttpSession session) throws Exception;
	
	/**
	 * 사용자 수성
	 * @param paramMap
	 * @param session
	 * @return
	 * @throws Exception
	 */
	boolean updateUserInfo(Map<String, Object> paramMap, HttpSession session) throws Exception;
	
	/**
	 * 사용자 삭제
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	boolean deleteUserInfo(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 아이디 중복체크
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	boolean dupUserInfo(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 패스워드 검증
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	boolean isUserPasswordCheck(Map<String, Object> paramMap) throws Exception;
	
	
}
