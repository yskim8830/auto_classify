package kr.co.proten.manager.learning.vo;

public class ResponseClassifyRootVo {
	private ResponseClassifyStatusVo status;
	private ResponseClassifyAnalysisResultVo analysisResult;
	private int siteNo;
	private double runtime;
	 
	public ResponseClassifyStatusVo getStatus() {
		return status;
	}
	public void setStatus(ResponseClassifyStatusVo status) {
		this.status = status;
	}
	public ResponseClassifyAnalysisResultVo getAnalysisResult() {
		return analysisResult;
	}
	public void setAnalysisResult(ResponseClassifyAnalysisResultVo analysisResult) {
		this.analysisResult = analysisResult;
	}
	public int getSiteNo() {
		return siteNo;
	}
	public void setSiteNo(int siteNo) {
		this.siteNo = siteNo;
	}
	public double getRuntime() {
		return runtime;
	}
	public void setRuntime(double runtime) {
		this.runtime = runtime;
	}
	 
}
