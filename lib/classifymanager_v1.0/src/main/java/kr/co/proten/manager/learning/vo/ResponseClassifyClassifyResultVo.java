package kr.co.proten.manager.learning.vo;

public class ResponseClassifyClassifyResultVo {
	private int categoryNo;
	private String categoryNm;
	private String fullItem;
	private double score;
	private String reliability;
	
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
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public String getReliability() {
		return reliability;
	}
	public void setReliability(String reliability) {
		this.reliability = reliability;
	}
}
