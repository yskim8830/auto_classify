package kr.co.proten.manager.statistics.service;

import java.util.List;
import java.util.Map;

public interface StatisticsService {

	/**
	 * 통계 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	Map<String, List<Map<String, Object>>> selectStatisticsDataList(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 시계열 기준 리스트 생성
	 * @param stdField
	 * @param startDt
	 * @param endDt
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> generateTrendList(String stdField, String startDt, String endDt) throws Exception;
	
	/**
	 * 시계열 데이터 병합
	 * @param trendList
	 * @param resultMap
	 * @throws Exception
	 */
	void mergeTrendListValue(List<Map<String, Object>> trendList, List<Map<String, Object>> resultListMap) throws Exception;
	
	/**
	 * terms 데이터 변환
	 * @param resultMap
	 * @param resultListMap
	 * @throws Exception
	 */
	void convertTermsValue(Map<String, Object> resultMap, List<Map<String, Object>> resultListMap) throws Exception;
	
	/**
	 * category 데이터 변환
	 * @param resultMap
	 * @param resultListMap
	 * @throws Exception
	 */
	void convertCategoryValue(List<Map<String, Object>> resultList, List<Map<String, Object>> resultListMap) throws Exception;
}
