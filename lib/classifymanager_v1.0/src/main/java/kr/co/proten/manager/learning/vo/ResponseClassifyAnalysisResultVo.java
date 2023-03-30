package kr.co.proten.manager.learning.vo;

import java.util.List;

public class ResponseClassifyAnalysisResultVo {
	
	private String tagSentence;
	private String resultType;
	private String matchedType;
	private String matchedCategory;
	private List<ResponseClassifyRuleResultVo> ruleResult;
	private List<ResponseClassifyClassifyResultVo> classifyResult;
	
	public String getTagSentence() {
		return tagSentence;
	}
	public void setTagSentence(String tagSentence) {
		this.tagSentence = tagSentence;
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
	public String getMatchedCategory() {
		return matchedCategory;
	}
	public void setMatchedCategory(String matchedCategory) {
		this.matchedCategory = matchedCategory;
	}
	public List<ResponseClassifyRuleResultVo> getRuleResult() {
		return ruleResult;
	}
	public void setRuleResult(List<ResponseClassifyRuleResultVo> ruleResult) {
		this.ruleResult = ruleResult;
	}
	public List<ResponseClassifyClassifyResultVo> getClassifyResult() {
		return classifyResult;
	}
	public void setClassifyResult(List<ResponseClassifyClassifyResultVo> classifyResult) {
		this.classifyResult = classifyResult;
	}
}
