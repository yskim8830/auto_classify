package kr.co.proten.manager.classify.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

public interface ClassifyDataService {
	
	/**
	 * 데이터 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectDataList(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 데이터 전체 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectDataListAll(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 데이터 저장
	 * @param paramMap
	 * @param session
	 * @return
	 * @throws Exception
	 */
	boolean insertDataInfo(Map<String, Object> paramMap, HttpSession session) throws Exception;
	
	/**
	 * 데이터 수정
	 * @param paramMap
	 * @param session
	 * @return
	 * @throws Exception
	 */
	boolean updateDataInfo(Map<String, Object> paramMap, HttpSession session) throws Exception;
	
	/**
	 * 데이터 삭제
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	boolean deleteDataInfo(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 데이터 벌크 저장
	 * @param listMap
	 * @return
	 * @throws Exception
	 */
	boolean insertDataBulk(List<Map<String, Object>> listMap) throws Exception;
	
	/**
	 * 아이디 발급
	 * @return
	 * @throws Exception
	 */
	int selectMaxId() throws Exception; 
}
