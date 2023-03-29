package kr.co.proten.manager.learning.service;

import java.util.List;
import java.util.Map;

public interface ModelingService {
	
	/**
	 * 모델대상 목록 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> modelingList(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 학습이력 목록 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> learningList(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 학습시작 API 요청
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	String requestStartTraining(String siteNo) throws Exception;
	
	/**
	 * 학습상태 API 요청
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	String requestStatusTraining(String siteNo) throws Exception;
	
	/**
	 * 학습중지 API 요청
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	String requestStopTraining(String siteNo) throws Exception;
	
	/**
	 * 서비스배포 API 요청
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	String requestDistModelToService(String siteNo, String version) throws Exception;
	
}
