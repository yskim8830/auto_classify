package kr.co.proten.manager.common.vo;

public enum BulkSimulationStepField { 
	FILE_VALIDATION("fileValidation"),	// 파일 검증
	READ_FILE("readFile"),				// 파일 파싱
	CREATE_THREAD("createThread"),		// 쓰레드 생성
	SIMULATION("simulation"),			// 시뮬레이션
	IMPORT_DATA("importData"),
	FINISHED("finished")
	;
	
	private String step;
	
	BulkSimulationStepField(String step){
		this.step = step;
	}
	
	public String toString() {
		return this.step;
	}
}
