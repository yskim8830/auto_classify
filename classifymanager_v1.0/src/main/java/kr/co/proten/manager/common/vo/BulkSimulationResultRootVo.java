package kr.co.proten.manager.common.vo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BulkSimulationResultRootVo {
	private String status = "success";
	private String errorMessage;
	
	private File uploadFile;
	
	private String simulationStep;
	private String progressMessage;
	private boolean taskComplete = false;
	private int progress;
	
	private boolean running = true;
	
	private int siteNo; 
	private String userId;
	
	private BulkSimulationResultSummaryVo summaryVo = new BulkSimulationResultSummaryVo();
	private ConcurrentLinkedQueue<BulkSimulationResultVo> dataQueue = new ConcurrentLinkedQueue<BulkSimulationResultVo>();
	private List<BulkSimulationResultVo> resultDataList = new ArrayList<BulkSimulationResultVo>();
	
	public int getSiteNo() {
		return siteNo;
	}
	public void setSiteNo(int siteNo) {
		this.siteNo = siteNo;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public File getUploadFile() {
		return uploadFile;
	}
	public void setUploadFile(File uploadFile) {
		this.uploadFile = uploadFile;
	}
	public boolean isTaskComplete() {
		return taskComplete;
	}
	public void setTaskComplete(boolean taskComplete) {
		this.taskComplete = taskComplete;
	}
	public int getProgress() {
		return progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}
	public BulkSimulationResultSummaryVo getSummaryVo() {
		return summaryVo;
	}
	public void setSummaryVo(BulkSimulationResultSummaryVo summaryVo) {
		this.summaryVo = summaryVo;
	}
	public List<BulkSimulationResultVo> getResultDataList() {
		return resultDataList;
	}
	public void addResultData(BulkSimulationResultVo dataVo) {
		this.resultDataList.add(dataVo);
	}
	public ConcurrentLinkedQueue<BulkSimulationResultVo> getDataQueue() {
		return dataQueue;
	}
	public void addDataQueue(BulkSimulationResultVo dataVo) {
		this.dataQueue.add(dataVo);
	}
	public boolean isRunning() {
		return running;
	}
	public void setRunning(boolean running) {
		this.running = running;
	}
	public String getProgressMessage() {
		return progressMessage;
	}
	public void setProgressMessage(String progressMessage) {
		this.progressMessage = progressMessage;
	}
	public String getSimulationStep() {
		return simulationStep;
	}
	public void setSimulationStep(String simulationStep) {
		this.simulationStep = simulationStep;
	}
}
