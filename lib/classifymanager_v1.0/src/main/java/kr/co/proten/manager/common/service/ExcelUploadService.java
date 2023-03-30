package kr.co.proten.manager.common.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import kr.co.proten.manager.common.config.AsyncConfig;
import kr.co.proten.manager.common.util.DateUtil;
import kr.co.proten.manager.common.vo.BulkSimulationResultRootVo;
import kr.co.proten.manager.common.vo.BulkSimulationStepField;
import kr.co.proten.manager.common.vo.UploadStepField;
import kr.co.proten.manager.common.vo.UploadStepResultVo;
import kr.co.proten.manager.common.vo.UploadnDownloadServiceField;
import kr.co.proten.manager.login.model.LoginModel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExcelUploadService {
	
	private static final Logger log = LoggerFactory.getLogger(ExcelUploadService.class);
	
	private Map<String, UploadStepResultVo> taskStepMap = new HashMap<String, UploadStepResultVo>();
	private Map<String, BulkSimulationResultRootVo> simulationProgressMap = new HashMap<String, BulkSimulationResultRootVo>();
	
	private final Map<String, UploadDataService> uploadDataServiceMap;
	
	private final BulkSimulationService bulkSimulationService;
	
	private final CommonService commonService;
	
	private final AsyncConfig asyncConfig;
	
	@Async("proworker")
	public void upload(String taskId, LoginModel login, String serviceId, File file) {	
		log.info("[ExcelUploadService] [upload] uploader start!!");
		UploadStepResultVo stepResultVo = new UploadStepResultVo();
		
		stepResultVo.setUploadStep(UploadStepField.FILE_VALIDATION.toString());
		stepResultVo.setUploadFile(file);
		taskStepMap.put(taskId, stepResultVo);
		
		UploadDataService uploadDataService = getUploadDataServiceInstance(serviceId);
		
		// STEP 1. 파일 검증
		UploadStepResultVo fileValidationResultVo = new UploadStepResultVo();
		try {
			fileValidationResultVo = uploadDataService.doFileValidation(file);
			stepResultVo.setErrorMessage(fileValidationResultVo.getErrorMessage());
			stepResultVo.setStatus(fileValidationResultVo.getStatus());
		} catch (Exception e) {
			stepResultVo.setErrorMessage(e.getMessage());
			stepResultVo.setStatus("fail");
		}
		
		if(stepResultVo.getStatus().equals("fail")) {
			stepResultVo.getUploadFile().deleteOnExit();
			stepResultVo.getInValidDataList().clear();
			stepResultVo.getValidDataList().clear();
			log.error("[ExcelUploadService] [upload] fileValidation fail. {}", stepResultVo.getErrorMessage());
			return;
		}
		
		// STEP 2. 데이터 검증
		stepResultVo.setUploadStep(UploadStepField.DATA_VALIDATION.toString());
		UploadStepResultVo dataValidationResultVo = new UploadStepResultVo();
		try {
			dataValidationResultVo = uploadDataService.doDataValidation(login, file);	
			stepResultVo.setErrorMessage(dataValidationResultVo.getErrorMessage());
			stepResultVo.setValidDataList(dataValidationResultVo.getValidDataList());
			stepResultVo.setInValidDataList(dataValidationResultVo.getInValidDataList());
			stepResultVo.setStatus(dataValidationResultVo.getStatus());
			
		} catch (Exception e) {
			stepResultVo.setErrorMessage(e.getMessage());
			stepResultVo.setStatus("fail");
		}
		
		if(stepResultVo.getStatus().equals("fail")) {
			stepResultVo.getUploadFile().deleteOnExit();
			stepResultVo.getInValidDataList().clear();
			stepResultVo.getValidDataList().clear();
			log.error("[ExcelUploadService] [upload] dataValidation fail. {}", stepResultVo.getErrorMessage());
			return;
		}
		
		// STEP 3. 데이터 반영
		stepResultVo.setUploadStep(UploadStepField.DATA_IMPORT.toString());
		List<Map<String,Object>> validDataList = stepResultVo.getValidDataList();
		List<Map<String,Object>> inValidDataList = stepResultVo.getInValidDataList();
		
		if(validDataList.size() > 0) {
			UploadStepResultVo dataImportResultVo = new UploadStepResultVo();
			dataImportResultVo = uploadDataService.doImportData(validDataList);
			stepResultVo.setErrorMessage(dataImportResultVo.getErrorMessage());
			stepResultVo.setImportCount(dataImportResultVo.getImportCount());
			stepResultVo.setStatus(dataImportResultVo.getStatus());
			
			if(stepResultVo.getStatus().equals("fail")) {
				stepResultVo.getUploadFile().deleteOnExit();
				stepResultVo.getInValidDataList().clear();
				stepResultVo.getValidDataList().clear();
				log.error("[ExcelUploadService] [upload] data import fail. {}", stepResultVo.getErrorMessage());
				return;
			}
		}
		
		// STEP 4. 리포팅(inValid data import)
		stepResultVo.setUploadStep(UploadStepField.REPORT.toString());
		if(inValidDataList.size() > 0) {
			stepResultVo.setFailedCount(inValidDataList.size());		
			UploadStepResultVo reportResultVo = new UploadStepResultVo();
			reportResultVo = doResultReport(taskId, login.getSiteNo(), serviceId, uploadDataService.getFeedbackHeader(), inValidDataList);
			stepResultVo.setErrorMessage(reportResultVo.getErrorMessage());
			stepResultVo.setStatus(reportResultVo.getStatus());
			
			if(stepResultVo.getStatus().equals("fail")) {
				stepResultVo.getUploadFile().deleteOnExit();
				stepResultVo.getInValidDataList().clear();
				stepResultVo.getValidDataList().clear();
				log.error("[ExcelUploadService] [upload] data report fail. {}", stepResultVo.getErrorMessage());
				return;
			}
		}
		
		// STEP 5. 완료
		stepResultVo.setUploadStep(UploadStepField.FINISHED.toString());
		stepResultVo.getUploadFile().deleteOnExit();
		log.info("[ExcelUploadService] [upload] uploader end!!");
	}
	
	@Async("proworker")
	public void bulkSimulation(LoginModel login, String taskId, int version, int threshold, File file) {	
		log.info("[ExcelUploadService] [bulkSimulation] uploader start!!");
		BulkSimulationResultRootVo simulationResultVo = new BulkSimulationResultRootVo();
		simulationResultVo.setUploadFile(file);
		simulationResultVo.setSiteNo(login.getSiteNo());
		simulationResultVo.setUserId(login.getUserId());
		simulationResultVo.getSummaryVo().setSiteNo(login.getSiteNo());
		simulationResultVo.getSummaryVo().setTaskId(taskId);	
		simulationResultVo.getSummaryVo().setVersion(version);
		simulationResultVo.getSummaryVo().setThreshold(threshold);
		simulationResultVo.getSummaryVo().setRunStartDate(DateUtil.getCurrentDateTimeMille());
		simulationProgressMap.put(taskId, simulationResultVo);
		long simulationStart = System.currentTimeMillis();
		
		String siteCode = "";
		for(Map<String,Object> site : login.getSiteList()) {
			if((int)site.get("siteNo") == login.getSiteNo()) {
				siteCode = (String) site.get("site");
			}
		}
		
		// STEP 1. 파일 검증
		try {
			simulationResultVo.setSimulationStep(BulkSimulationStepField.FILE_VALIDATION.toString());
			if(simulationResultVo.isRunning()) {
				bulkSimulationService.doFileValidation(file, simulationResultVo);				
			}
		} catch (Exception e) {
			simulationResultVo.setErrorMessage(e.getMessage());
			simulationResultVo.setStatus("fail");
		}
		
		if(simulationResultVo.getStatus().equals("fail")) {
			simulationResultVo.getUploadFile().deleteOnExit();
			log.error("[ExcelUploadService] [bulkSimulation] fileValidation fail. {}", simulationResultVo.getErrorMessage());
			return;
		}
		
		// STEP 2. 데이터 추출
		try {
			simulationResultVo.setSimulationStep(BulkSimulationStepField.READ_FILE.toString());
			if(simulationResultVo.isRunning()) {
				bulkSimulationService.readFile(file, simulationResultVo);						
			}
		} catch (Exception e) {
			simulationResultVo.setErrorMessage(e.getMessage());
			simulationResultVo.setStatus("fail");
		}
		
		if(simulationResultVo.getStatus().equals("fail")) {
			simulationResultVo.getUploadFile().deleteOnExit();
			simulationResultVo.getDataQueue().clear();
			log.error("[ExcelUploadService] [bulkSimulation] readFile fail. {}", simulationResultVo.getErrorMessage());
			return;
		}
		
		// STEP 3. 시뮬레이션 실행
		try {
			int availableThreadCount = asyncConfig.availableThreadCount();
			int maxWorkerSize = 10;
			int workerSize = 0;
			if(simulationResultVo.getSummaryVo().getDataCnt() > maxWorkerSize) {
				workerSize = maxWorkerSize;
			} else {
				workerSize = simulationResultVo.getSummaryVo().getDataCnt();
			}
			simulationResultVo.setSimulationStep(BulkSimulationStepField.CREATE_THREAD.toString());
			while(availableThreadCount < workerSize) {
				availableThreadCount = asyncConfig.availableThreadCount();
				Thread.sleep(1000);
				log.debug("[ExcelUploadService] [bulkSimulation] can not create new threads now. wait 1 seconds.");
			}
			simulationResultVo.setSimulationStep(BulkSimulationStepField.SIMULATION.toString());
			if(simulationResultVo.isRunning()) {
				List<Future> futures = new ArrayList<>();
				int threadCnt = 0;
				for(int i=0; i<workerSize; i++) {
					futures.add(bulkSimulationService.doSimulation(siteCode, simulationResultVo));
					threadCnt++;
				}
				log.debug("[ExcelUploadService] [bulkSimulation] request {} threads! then wait for all proworker's job.", threadCnt);
				for(Future future : futures) {
					try {
						// AsyncResult 와 같은 Future object 들은 get 을 이용해 결과 값을 받을때까지 wait 할 수 있다.
						// 이 예제에선 void 형태의 리턴값 없는 비동기 메소드였지만, 결과 값이 필요한 경우 비동기메소드의 return 에서 AsyncResult 에 넣어주고
						// 아래 Future.get() 을 이용해 해당 값을 받아내서 처리할 수 있다.
						future.get();
					} catch(ExecutionException e) {
						simulationResultVo.setErrorMessage(e.getMessage());
						simulationResultVo.setStatus("fail");
					} catch(InterruptedException e) {
						simulationResultVo.setErrorMessage(e.getMessage());
						simulationResultVo.setStatus("fail");
					}
				}
				log.debug("[ExcelUploadService] [bulkSimulation] all proworkers are done.");
			}
		} catch (Exception e) {
			simulationResultVo.setErrorMessage(e.getMessage());
			simulationResultVo.setStatus("fail");
		}
		
		if(simulationResultVo.getStatus().equals("fail")) {
			simulationResultVo.getUploadFile().deleteOnExit();
			simulationResultVo.getResultDataList().clear();
			simulationResultVo.getDataQueue().clear();
			log.error("[ExcelUploadService] [bulkSimulation] doSimulation fail. {}", simulationResultVo.getErrorMessage());
			return;
		}
		
		// STEP 4. 데이터 반영
		long simulationEnd = System.currentTimeMillis();
		simulationResultVo.getSummaryVo().setRunEndDate(DateUtil.getCurrentDateTimeMille());
		simulationResultVo.getSummaryVo().setRuntime(simulationEnd - simulationStart);
		simulationResultVo.setSimulationStep(BulkSimulationStepField.IMPORT_DATA.toString());
		if(simulationResultVo.getResultDataList().size() > 0 && simulationResultVo.isRunning()) {
			try {
				bulkSimulationService.doImportData(simulationResultVo);
			} catch (Exception e) {
				simulationResultVo.setErrorMessage(e.getMessage());
				simulationResultVo.setStatus("fail");
			}
			
			if(simulationResultVo.getStatus().equals("fail")) {
				simulationResultVo.getUploadFile().deleteOnExit();
				simulationResultVo.getResultDataList().clear();
				log.error("[ExcelUploadService] [bulkSimulation] data import fail. {}", simulationResultVo.getErrorMessage());
				return;
			}
		}
		// STEP 5. 완료
		simulationResultVo.setSimulationStep(BulkSimulationStepField.FINISHED.toString());
		simulationResultVo.setTaskComplete(true);
		simulationResultVo.getUploadFile().deleteOnExit();
		log.info("[ExcelUploadService] [bulkSimulation] uploader end!!");
	}
	
	/**
	 * bean 인스턴스 할당
	 * @param serviceId
	 * @return
	 */
	private UploadDataService getUploadDataServiceInstance(String serviceId) {
		String beanName = "";
		if(UploadnDownloadServiceField.OBJECT_DIC.toString().equals(serviceId)) {
			beanName = "uploadObjectDicService";
		} else if(UploadnDownloadServiceField.USER_DIC.toString().equals(serviceId)) {
			beanName = "uploadUserDicService";
		} else if(UploadnDownloadServiceField.CLASSIFY_CATEGORY.toString().equals(serviceId)) {
			beanName = "uploadCategoryService";
		} else if(UploadnDownloadServiceField.CLASSIFY_RULE.toString().equals(serviceId)) {
			beanName = "uploadClassifyRuleService";
		} else if(UploadnDownloadServiceField.CLASSIFY_DATA.toString().equals(serviceId)) {
			beanName = "uploadClassifyDataService";
		}
		UploadDataService instance = uploadDataServiceMap.get(beanName);
		return instance;
	}
	
	/**
	 * taskId에 해당하는 진행 상태 요청
	 * @param taskId
	 * @return
	 */
	public UploadStepResultVo getStep(String taskId) {
		UploadStepResultVo returnResultVo = new UploadStepResultVo();
		UploadStepResultVo stepResultVo = null;
		
		if(taskStepMap.containsKey(taskId)) {
			stepResultVo = taskStepMap.get(taskId);
			String step = stepResultVo.getUploadStep();
			String status = stepResultVo.getStatus();
			
			returnResultVo.setUploadStep(step);
			returnResultVo.setStatus(status);
			returnResultVo.setErrorMessage(stepResultVo.getErrorMessage());
			returnResultVo.setFailedCount(stepResultVo.getFailedCount());
			returnResultVo.setImportCount(stepResultVo.getImportCount());
			if(step.equals(UploadStepField.FINISHED.toString()) || status.equals("fail")) {
				// 완료 또는 실패된 태스크는 맵에서 삭제한다
				// 업로드 파일 삭제
				stepResultVo.getUploadFile().deleteOnExit();
				stepResultVo.getInValidDataList().clear();
				stepResultVo.getValidDataList().clear();
				taskStepMap.remove(taskId);
			} 
		} else {
			returnResultVo = new UploadStepResultVo();
			returnResultVo.setUploadStep(UploadStepField.EXPIRED.toString());
		}
		
		return returnResultVo;
	}
	
	/**
	 * 결과 리포팅
	 * @param taskId
	 * @param siteNo
	 * @param serviceId
	 * @param header
	 * @param invalidDataList
	 * @return
	 */
	public UploadStepResultVo doResultReport(String taskId, int siteNo, String serviceId, String[] header, List<Map<String,Object>> invalidDataList) {
		UploadStepResultVo stepResultVo = new UploadStepResultVo();
		
		Gson gson = new Gson();
		List<Map<String,Object>> listMap = new ArrayList<>();
		Map<String,Object> dataMap = null;
		
		try {
			int i = 0;
			for(Map<String,Object> _dataMap : invalidDataList) {
				dataMap = new HashMap<String, Object>();
				String id = taskId + "_" + i;
				dataMap.put("_id", id);
				dataMap.put("id", id);
				dataMap.put("siteNo", siteNo);
				dataMap.put("taskId", taskId);
				dataMap.put("serviceId", serviceId);
				dataMap.put("header", gson.toJson(header)); 
				dataMap.put("data", gson.toJson(_dataMap));
				dataMap.put("createDate", kr.co.proten.manager.common.util.DateUtil.getCurrentDateTimeMille());
				dataMap.put("modifyDate", kr.co.proten.manager.common.util.DateUtil.getCurrentDateTimeMille());
				listMap.add(dataMap);
				i++;
			}
			commonService.insertUploadInvalidDataBulk(listMap);
			stepResultVo.setStatus("success");
		} catch(Exception e) {
			log.error(e.getMessage());
			stepResultVo.setStatus("fail");
			stepResultVo.setErrorMessage(e.getMessage());
		}
		
		return stepResultVo;
	}
	
	/**
	 * taskId에 해당하는 진행 상태 요청
	 * @param taskId
	 * @return
	 */
	public BulkSimulationResultRootVo getBulkSimulationProgressInfo(String taskId) {
		BulkSimulationResultRootVo returnResultVo = new BulkSimulationResultRootVo();
		BulkSimulationResultRootVo progressVo = null;
		
		if(simulationProgressMap.containsKey(taskId)) {
			progressVo = simulationProgressMap.get(taskId);
			returnResultVo.setStatus(progressVo.getStatus());
			returnResultVo.setErrorMessage(progressVo.getErrorMessage());
			returnResultVo.setProgress(progressVo.getProgress());
			returnResultVo.setTaskComplete(progressVo.isTaskComplete());
			returnResultVo.setSimulationStep(progressVo.getSimulationStep());
			
			String simulationStep = progressVo.getSimulationStep();
			
			String status = progressVo.getStatus();
			boolean isTaskComplete = progressVo.isTaskComplete();
			
			double totalCnt = progressVo.getSummaryVo().getDataCnt();
			double executedCnt = progressVo.getResultDataList().size();
			double progress = (executedCnt / totalCnt) * 100;
			
			int completeProgress = 100;
			int fileValidationProgress = 5;
			int readFileProgress = 10;
			int simulationProgress = 10;
			int importDataProgress = 90;
			
			if(isTaskComplete) {
				returnResultVo.setProgress(completeProgress);								
			} else if(simulationStep.equals(BulkSimulationStepField.FILE_VALIDATION.toString())) {
				returnResultVo.setProgress(fileValidationProgress);
			} else if(simulationStep.equals(BulkSimulationStepField.READ_FILE.toString())) {
				returnResultVo.setProgress(readFileProgress);
			} else if(simulationStep.equals(BulkSimulationStepField.CREATE_THREAD.toString())) {
				returnResultVo.setProgress(readFileProgress);
			} else if(simulationStep.equals(BulkSimulationStepField.SIMULATION.toString())) {
				int intProgress = 0;
				intProgress = (int)(progress * 0.8) + simulationProgress;
				returnResultVo.setProgress(intProgress);
			} else if(simulationStep.equals(BulkSimulationStepField.IMPORT_DATA.toString())) {
				returnResultVo.setProgress(importDataProgress);
			} 
			log.debug("[ExcelUploadService] [getBulkSimulationProgressInfo] simulation progress [{}%]", returnResultVo.getProgress());
			if(isTaskComplete || status.equals("fail")) {
				// 완료 또는 실패된 태스크는 맵에서 삭제한다, 업로드 파일 삭제
				progressVo.getUploadFile().deleteOnExit();
				progressVo.getResultDataList().clear();
				simulationProgressMap.remove(taskId);
			} 
		} else {
			returnResultVo.setTaskComplete(true);
		}
		
		return returnResultVo;
	}
	
	/**
	 * taskId에 해당하는 진행 상태 요청
	 * @param taskId
	 * @return
	 */
	public boolean terminateBulkSimulation(String taskId) {
		BulkSimulationResultRootVo progressVo = null;
		
		if(simulationProgressMap.containsKey(taskId)) {
			progressVo = simulationProgressMap.get(taskId);
			progressVo.setRunning(false);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 시뮬레이션 리스트
	 * @param siteNo
	 * @return
	 */
	public List<String> getBulkSimulationTaskIdList(LoginModel login) {
		List<String> taskIdList = new ArrayList<>();
		
		simulationProgressMap.forEach((key, value)->{
			if(login.getSiteNo() == value.getSiteNo() && login.getUserId().equals(value.getUserId())) {
				taskIdList.add(key);
			}
		});
		
		return taskIdList;
	}
}
