package kr.co.proten.manager.classify.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import kr.co.proten.manager.common.vo.ReferencedResultVo;

public interface ClassifyRuleService {
	
	/**
	 * 룰 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectRuleList(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 룰 전체 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectRuleListAll(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 룰 저장
	 * @param paramMap
	 * @param session
	 * @return
	 * @throws Exception
	 */
	boolean insertRuleInfo(Map<String, Object> paramMap, HttpSession session) throws Exception;
	
	/**
	 * 룰 수정
	 * @param paramMap
	 * @param session
	 * @return
	 * @throws Exception
	 */
	boolean updateRuleInfo(Map<String, Object> paramMap, HttpSession session) throws Exception;
	
	/**
	 * 룰 삭제
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	boolean deleteRuleInfo(Map<String, Object> paramMap) throws Exception;
		
	/**
	 * 룰 중복체크
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	boolean dupRuleInfo(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 룰 벌크 저장
	 * @param listMap
	 * @return
	 * @throws Exception
	 */
	boolean insertRuleBulk(List<Map<String, Object>> listMap) throws Exception;
	
	/**
	 * 아이디 발급
	 * @return
	 * @throws Exception
	 */
	int selectMaxId() throws Exception; 
	
	/**
	 * 엔티티 참조 여부 조회
	 * @return
	 * @throws Exception
	 */
	ReferencedResultVo refIdCheckAtRule(int siteNo, List<String> entityNoArray, String refField) throws Exception;
}
