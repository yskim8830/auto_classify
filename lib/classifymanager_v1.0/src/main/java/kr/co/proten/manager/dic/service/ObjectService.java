package kr.co.proten.manager.dic.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import kr.co.proten.manager.common.vo.ReferencedResultVo;

public interface ObjectService {

	/**
	 * 개체사전 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectObjDicList(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 개체사전 전체 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectObjDicListAll(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 개체사전 저장
	 * @param paramMap
	 * @param session
	 * @return
	 * @throws Exception
	 */
	boolean insertObjDicInfo(Map<String, Object> paramMap, HttpSession session) throws Exception;
	
	/**
	 * 개체사전 수정
	 * @param paramMap
	 * @param session
	 * @return
	 * @throws Exception
	 */
	boolean updateObjDicInfo(Map<String, Object> paramMap, HttpSession session) throws Exception;
	
	/**
	 * 개체사전 삭제
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	ReferencedResultVo deleteObjDicInfo(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 개체사전 벌크 저장
	 * @param listMap
	 * @return
	 * @throws Exception
	 */
	boolean insertObjDicBulk(List<Map<String, Object>> listMap) throws Exception;
	
	/**
	 * 개체사전 벌크 수정
	 * @param listMap
	 * @return
	 * @throws Exception
	 */
	boolean updateObjDicBulk(List<Map<String, Object>> listMap) throws Exception;
	
	/**
	 * 개체사전 중복체크
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	boolean dupObjDicInfo(Map<String, Object> paramMap) throws Exception;
	
	
}
