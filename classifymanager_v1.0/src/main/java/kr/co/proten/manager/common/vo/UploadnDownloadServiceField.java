package kr.co.proten.manager.common.vo;

public enum UploadnDownloadServiceField {
	CLASSIFY_DATA("classifyData"),	// 분류룰
	CLASSIFY_RULE("classifyRule"),	// 분류룰
	CLASSIFY_CATEGORY("category"),	// 카테고리
	BULK_SIMULATION("bulkSimulation"),	// 시뮬레이션
	OBJECT_DIC("objectDic"),	// 엔티티사전
	USER_DIC("userDic");	// 사용자사전
	
	private String serviceId;
	
	UploadnDownloadServiceField(String serviceId){
		this.serviceId = serviceId;
	}
	
	public String toString() {
		return this.serviceId;
	}
}
