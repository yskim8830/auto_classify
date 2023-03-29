package kr.co.proten.manager.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import kr.co.proten.manager.common.vo.UploadnDownloadServiceField;
import kr.co.proten.manager.login.model.LoginModel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExcelDownloadService {
	
	private static final Logger log = LoggerFactory.getLogger(ExcelDownloadService.class);
	
	private String DOWNLOAD_DATA_TASK = "data";
	private String DOWNLOAD_TEMPLATE_TASK = "template";
	
	private final FeedbackService feedbackService;
	
	private final Map<String, DownloadDataService> downloadServiceMap;
	
	/**
	 * 데이터 다운로드
	 * @param response
	 * @param login
	 * @param serviceId
	 * @return
	 */
	public boolean download(HttpServletResponse response, LoginModel login, String serviceId) {
		log.info("[ExcelDownloadService] [download] start!!");
		boolean result = true;
		
		DownloadDataService downloadDataService = getDownloadDataServiceInstance(serviceId); 
		
		// STEP 1. 데이터 조회
		List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
		try {
			dataList = downloadDataService.getDataList(login);
		} catch (Exception e) {
			log.error("[ExcelDownloadService] [download] get dataList failed!! " + e.getMessage(),e);
			result = false;
		}
		
		// STEP 2. 파일 쓰기
		if(result) {
			try {
				downloadDataService.writeExcelFile(DOWNLOAD_DATA_TASK, dataList, login, response);
			} catch (Exception e) {
				log.error("[ExcelDownloadService] [download] writeExcelFile failed!! " + e.getMessage(),e);
				result = false;
			}
		}
		
		log.info("[ExcelDownloadService] [download] end!!");
		return result;
	}
	
	/**
	 * 템플릿 다운로드
	 * @param response
	 * @param login
	 * @param serviceId
	 * @return
	 */
	public boolean downloadTemplate(HttpServletResponse response, LoginModel login, String serviceId) {
		log.info("[ExcelDownloadService] [downloadTemplate] start!!");
		boolean result = true;
		
		DownloadDataService downloadDataService = getDownloadDataServiceInstance(serviceId); 
		
		// STEP 1. 파일 쓰기
		try {
			downloadDataService.writeExcelFile(DOWNLOAD_TEMPLATE_TASK, null, login, response);
		} catch (Exception e) {
			log.error("[ExcelDownloadService] [downloadTemplate] writeExcelFile failed!! " + e.getMessage(),e);
			result = false;
		}
		
		log.info("[ExcelDownloadService] [downloadTemplate] end!!");
		return result;
	}
	
	/**
	 * bean 인스턴스 할당
	 * @param serviceId
	 * @return
	 */
	private DownloadDataService getDownloadDataServiceInstance(String serviceId) {
		String beanName = "";
		if(UploadnDownloadServiceField.OBJECT_DIC.toString().equals(serviceId)) {
			beanName = "downloadObjectDicService";
		} else if(UploadnDownloadServiceField.USER_DIC.toString().equals(serviceId)) {
			beanName = "downloadUserDicService";
		} else if(UploadnDownloadServiceField.CLASSIFY_CATEGORY.toString().equals(serviceId)) {
			beanName = "downloadCategoryService";
		} else if(UploadnDownloadServiceField.CLASSIFY_RULE.toString().equals(serviceId)) {
			beanName = "downloadClassifyRuleService";
		} else if(UploadnDownloadServiceField.CLASSIFY_DATA.toString().equals(serviceId)) {
			beanName = "downloadClassifyDataService";
		} else if(UploadnDownloadServiceField.BULK_SIMULATION.toString().equals(serviceId)) {
			beanName = "downloadBulkSimulationService";
		}
		DownloadDataService instance = downloadServiceMap.get(beanName);
		return instance;
	}
	
	/**
	 * 피드백 파일 다운로드
	 * @param response
	 * @param taskId
	 * @throws Exception
	 */
	public void downloadFeedback(HttpServletResponse response, String taskId) throws Exception {
		// 데이터 조회 및 변환
		List<Map<String, Object>> dataMapList = feedbackService.selectFeedbackData(taskId);
		
		String[] header = null;
		List<Map<String,Object>> invalidDataList = new ArrayList<>();
		Map<String, Object> dataMap = null;
		if(dataMapList != null && dataMapList.size() > 0) {
			Gson gson = new Gson();
			String headerStr = "";
			String dataStr = "";
			
			for(Map<String, Object> _dataMap : dataMapList) {
				headerStr = String.valueOf(_dataMap.get("header"));
				dataStr = String.valueOf(_dataMap.get("data"));
				dataMap = gson.fromJson(dataStr, new TypeToken<HashMap<String, Object>>(){}.getType());
				invalidDataList.add(dataMap);
			}
			
			header = gson.fromJson(headerStr, String[].class);
			// excel write
			try {
				feedbackService.writeFeedbackReport(header, invalidDataList, response);				
			} catch(Exception e) {
				log.error("[ExcelDownloadService] [downloadFeedback] writeExcelFile failed!! " + e.getMessage(),e);
			}
		}
		// 데이터 삭제
		feedbackService.deleteFeedbackData(taskId);
	}
}
