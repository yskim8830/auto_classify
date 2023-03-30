package kr.co.proten.manager.learning.service;

import java.util.List;
import java.util.Map;

import kr.co.proten.manager.learning.vo.ResponseClassifyRootVo;

public interface SimulationService {
	
	/**
	 * 시뮬레이션 API 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	String requestSimulationApi(String siteCode, String query, String version) throws Exception;
	
	/**
	 * 단건 시뮬레이션 API 결과 메세지 변환
	 * @param vo
	 * @throws Exception
	 */
	void convertSimulationApiMessage(ResponseClassifyRootVo vo) throws Exception;
	
	/**
	 * 시뮬레이션 요약 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectSimulationSummaryList(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 시뮬레이션 상세 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectSimulationHistoryList(Map<String, Object> paramMap) throws Exception;
}
