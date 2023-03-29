package kr.co.proten.manager.learning.vo;

public class ResponseClassifyRuleResultVo {
	private int categoryNo;
	private String categoryNm;
	private String fullItem;
	private int ruleNo;
	private String rule;
	
	public int getCategoryNo() {
		return categoryNo;
	}
	public void setCategoryNo(int categoryNo) {
		this.categoryNo = categoryNo;
	}
	public String getCategoryNm() {
		return categoryNm;
	}
	public void setCategoryNm(String categoryNm) {
		this.categoryNm = categoryNm;
	}
	public String getFullItem() {
		return fullItem;
	}
	public void setFullItem(String fullItem) {
		this.fullItem = fullItem;
	}
	public int getRuleNo() {
		return ruleNo;
	}
	public void setRuleNo(int ruleNo) {
		this.ruleNo = ruleNo;
	}
	public String getRule() {
		return rule;
	}
	public void setRule(String rule) {
		this.rule = rule;
	}
}
