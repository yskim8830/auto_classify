package kr.co.proten.manager.system.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

public interface SiteService {

	/**
	 * 사이트 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectSiteList(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 사이트 저장
	 * @param paramMap
	 * @param session
	 * @return
	 * @throws Exception
	 */
	boolean insertSiteInfo(Map<String, Object> paramMap, HttpSession session) throws Exception;
	
	/**
	 * 사이트 수정
	 * @param paramMap
	 * @param session
	 * @return
	 * @throws Exception
	 */
	boolean updateSiteInfo(Map<String, Object> paramMap, HttpSession session) throws Exception;
	
	/**
	 * 사이트 삭제
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	boolean deleteSiteInfo(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 사이트 중복체크
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	boolean dupSiteInfo(Map<String, Object> paramMap) throws Exception;
}
