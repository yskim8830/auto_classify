package kr.co.proten.manager.dic.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

public interface DicService {
	
	/**
	 * 사용자 사전 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectDicList(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 사용자 사전 전체 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectDicListAll(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 사용자 사전 저장
	 * @param paramMap
	 * @param session
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> insertDicInfo(Map<String, Object> paramMap, HttpSession session) throws Exception;
	
	/**
	 * 사용자 사전 수정
	 * @param paramMap
	 * @param session
	 * @return
	 * @throws Exception
	 */
	Map<String, Object> updateDicInfo(Map<String, Object> paramMap, HttpSession session) throws Exception;
	
	/**
	 * 사용자 사전 삭제
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	boolean deleteDicInfo(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 사용자 사전 벌크 저장
	 * @param listMap
	 * @return
	 * @throws Exception
	 */
	boolean insertDicBulk(List<Map<String, Object>> listMap) throws Exception;
	
	/**
	 * 사용자 사전 벌크 수정
	 * @param listMap
	 * @return
	 * @throws Exception
	 */
	boolean updateDicBulk(List<Map<String, Object>> listMap) throws Exception;
	
	/**
	 * 사용자 사전 중복체크
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	boolean dupDicInfo(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 동의어 중복체크
	 * @param param
	 * @param id
	 * @param siteNo
	 * @return
	 * @throws Exception
	 */
	String dupSynonymDicInfo(String param, String id, int siteNo) throws Exception;
	
	/**
	 * 사전배포 API 요청
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	String requestDistDic() throws Exception;
	
}
