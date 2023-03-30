package kr.co.proten.manager.common.vo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UploadStepResultVo {
	private String status = "success";
	private String errorMessage;
	private String uploadStep;
	private File uploadFile;
	private List<Map<String,Object>> validDataList = new ArrayList<>();
	private List<Map<String,Object>> inValidDataList = new ArrayList<>();
	private int importCount;
	private int failedCount;
	
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
	public String getUploadStep() {
		return uploadStep;
	}
	public void setUploadStep(String uploadStep) {
		this.uploadStep = uploadStep;
	}
	public File getUploadFile() {
		return uploadFile;
	}
	public void setUploadFile(File uploadFile) {
		this.uploadFile = uploadFile;
	}
	public List<Map<String, Object>> getValidDataList() {
		return validDataList;
	}
	public void setValidDataList(List<Map<String, Object>> validDataList) {
		this.validDataList = validDataList;
	}
	public List<Map<String, Object>> getInValidDataList() {
		return inValidDataList;
	}
	public void setInValidDataList(List<Map<String, Object>> inValidDataList) {
		this.inValidDataList = inValidDataList;
	}
	public int getImportCount() {
		return importCount;
	}
	public void setImportCount(int importCount) {
		this.importCount = importCount;
	}
	public int getFailedCount() {
		return failedCount;
	}
	public void setFailedCount(int failedCount) {
		this.failedCount = failedCount;
	}
	
}
