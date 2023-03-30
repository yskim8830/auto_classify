package kr.co.proten.manager.common.vo;

public class BulkSimulationResultSummaryVo {
	private int siteNo;
	private String taskId;
	private int version;
	private int threshold;
	private int dataCnt;
	private int failedCnt;
	private int matchedDataCnt;
	private int ruleMatchedDataCnt;
	private int classifyMatchedDataCnt;
	private String createDate;
	private String runStartDate;
	private String runEndDate;
	private long runtime;
	
	public synchronized void addResultCnt(boolean failed, boolean matched, boolean ruleMatched, boolean classifyMatched) {
		if(failed) {
			failedCnt++;
		}
		if(matched) {
			matchedDataCnt++;
		}
		if(ruleMatched) {
			ruleMatchedDataCnt++;
		}
		if(classifyMatched) {
			classifyMatchedDataCnt++;
		}
	}
	public int getSiteNo() {
		return siteNo;
	}
	public void setSiteNo(int siteNo) {
		this.siteNo = siteNo;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public int getThreshold() {
		return threshold;
	}
	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
	public int getDataCnt() {
		return dataCnt;
	}
	public void setDataCnt(int dataCnt) {
		this.dataCnt = dataCnt;
	}
	public int getFailedCnt() {
		return failedCnt;
	}
	public void setFailedCnt(int failedCnt) {
		this.failedCnt = failedCnt;
	}
	public int getMatchedDataCnt() {
		return matchedDataCnt;
	}
	public void setMatchedDataCnt(int matchedDataCnt) {
		this.matchedDataCnt = matchedDataCnt;
	}
	public int getRuleMatchedDataCnt() {
		return ruleMatchedDataCnt;
	}
	public void setRuleMatchedDataCnt(int ruleMatchedDataCnt) {
		this.ruleMatchedDataCnt = ruleMatchedDataCnt;
	}
	public int getClassifyMatchedDataCnt() {
		return classifyMatchedDataCnt;
	}
	public void setClassifyMatchedDataCnt(int classifyMatchedDataCnt) {
		this.classifyMatchedDataCnt = classifyMatchedDataCnt;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getRunStartDate() {
		return runStartDate;
	}
	public void setRunStartDate(String runStartDate) {
		this.runStartDate = runStartDate;
	}
	public String getRunEndDate() {
		return runEndDate;
	}
	public void setRunEndDate(String runEndDate) {
		this.runEndDate = runEndDate;
	}
	public long getRuntime() {
		return runtime;
	}
	public void setRuntime(long runtime) {
		this.runtime = runtime;
	}	
}
