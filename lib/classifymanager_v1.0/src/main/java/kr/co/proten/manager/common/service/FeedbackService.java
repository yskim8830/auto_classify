package kr.co.proten.manager.common.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

public interface FeedbackService {

	/**
	 * 피드백 데이터 조회
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectFeedbackData(String taskId) throws Exception;
	
	/**
	 * 피드백 데이터 삭제
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	boolean deleteFeedbackData(String taskId) throws Exception;
	
	/**
	 * 리포트 파일 작성
	 * @param header
	 * @param dataList
	 * @param response
	 * @throws Exception
	 */
	void writeFeedbackReport(String[] header, List<Map<String,Object>> dataList, HttpServletResponse response) throws Exception;
	
}
