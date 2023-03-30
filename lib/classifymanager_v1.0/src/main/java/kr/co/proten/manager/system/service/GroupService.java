package kr.co.proten.manager.system.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

public interface GroupService {
	
	/**
	 * 그룹 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectGroupList(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 사이트 그룹 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectSiteToGroup(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 그룹 저장
	 * @param paramMap
	 * @param session
	 * @return
	 * @throws Exception
	 */
	boolean insertGroupInfo(Map<String, Object> paramMap, HttpSession session) throws Exception;
	
	/**
	 * 그룹 수정
	 * @param paramMap
	 * @param session
	 * @return
	 * @throws Exception
	 */
	boolean updateGroupInfo(Map<String, Object> paramMap, HttpSession session) throws Exception;
	
	/**
	 * 그룹 삭제
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	Map<String,Object> deleteGroupInfo(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 그룹 중복 체크
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	boolean dupGroupInfo(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 권한 저장
	 * @param paramMap
	 * @param session
	 * @return
	 * @throws Exception
	 */
	boolean insertAuthInfo(Map<String, Object> paramMap, HttpSession session) throws Exception;
	
	/**
	 * 권한 삭제
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	boolean deleteAuthInfo(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 권한 중복체크
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	String dupAuthInfo(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 사이트 권한 저장
	 * @param paramMap
	 * @param session
	 * @return
	 * @throws Exception
	 */
	boolean insertAuthSiteInfo(Map<String, Object> paramMap, HttpSession session) throws Exception;
	
	/**
	 * 사이트 권한 삭제
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	boolean deleteAuthSiteInfo(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 사이트 권한 중복체크
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	String dupAuthSiteInfo(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 메뉴별 사용자 권한 리스트 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectAuthMenuList(Map<String, Object> paramMap) throws Exception;
	
}
