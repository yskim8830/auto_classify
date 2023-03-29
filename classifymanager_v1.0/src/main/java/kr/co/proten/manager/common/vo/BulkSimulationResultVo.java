package kr.co.proten.manager.common.vo;

public class BulkSimulationResultVo {
	private String _id;
	private String id;
	private int siteNo;
	private String taskId;

	private String resultType;
	private String matchedType;
	private String matchedCategory;
	private String ruleResult;
	private String classifyResult;
	
	private String orgSentence;
	private String tagSentence;
	private String createDate;
	private String failedMessage;
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getResultType() {
		return resultType;
	}
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	public String getMatchedType() {
		return matchedType;
	}
	public void setMatchedType(String matchedType) {
		this.matchedType = matchedType;
	}
	public String getRuleResult() {
		return ruleResult;
	}
	public void setRuleResult(String ruleResult) {
		this.ruleResult = ruleResult;
	}
	public String getClassifyResult() {
		return classifyResult;
	}
	public void setClassifyResult(String classifyResult) {
		this.classifyResult = classifyResult;
	}
	public String getOrgSentence() {
		return orgSentence;
	}
	public void setOrgSentence(String orgSentence) {
		this.orgSentence = orgSentence;
	}
	public String getTagSentence() {
		return tagSentence;
	}
	public void setTagSentence(String tagSentence) {
		this.tagSentence = tagSentence;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getFailedMessage() {
		return failedMessage;
	}
	public void setFailedMessage(String failedMessage) {
		this.failedMessage = failedMessage;
	}
	public String getMatchedCategory() {
		return matchedCategory;
	}
	public void setMatchedCategory(String matchedCategory) {
		this.matchedCategory = matchedCategory;
	}
}
