package kr.co.proten.manager.learning.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

public interface ClassifyHistoryService {
	
	/**
	 * 분류이력 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectHistoryList(Map<String, Object> paramMap) throws Exception;
}
