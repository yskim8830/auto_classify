package kr.co.proten.manager.common.vo;

public enum UploadStepField { 
	FILE_VALIDATION("fileValidation"),	// 파일 검증
	DATA_VALIDATION("dataValidation"),	// 데이터 검증
	DATA_IMPORT("dataImport"),			// 데이터 반영
	REPORT("reporting"),				// 리포팅
	FINISHED("finished"),
	EXPIRED("expired")
	;
	
	private String step;
	
	UploadStepField(String step){
		this.step = step;
	}
	
	public String toString() {
		return this.step;
	}
}
