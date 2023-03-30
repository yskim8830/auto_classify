package kr.co.proten.manager.common.elastic;

public enum ElasticSearchIndex { 
	INDEX_NAME_ALL_("@proclassify_*",""),
	INDEX_NAME_CLASSIFY_CATEGORY("@proclassify_classify_category","siteNo,categoryNo,depth,pCategoryNo"),
	INDEX_NAME_CLASSIFY_RULE("@proclassify_classify_rule","siteNo,ruleNo,categoryNo"),
	INDEX_NAME_CLASSIFY_DATA("@proclassify_classify_data","siteNo,dataNo,categoryNo"),
	INDEX_NAME_CLASSIFY_HISTORY("@proclassify_classify_history","siteNo"),
	INDEX_NAME_LEARNING_LOG("@proclassify_learning_log","siteNo,version,dataCnt,runtime"),
	INDEX_NAME_USER("@proclassify_user","groupNo,loginCount,userNo"),
	INDEX_NAME_MENU("@proclassify_menu","menuId,depth,orderSeq,parentMenuId"),
	INDEX_NAME_ENTITY_DIC("@proclassify_entity_dic","siteNo"),
	INDEX_NAME_DIC("@proclassify_dic","siteNo"),
	INDEX_NAME_SITE("@proclassify_site","siteNo,threshold"),
	INDEX_NAME_SITE_MAPPING("@proclassify_site_mapping","id,menuId,siteNo"),
	INDEX_NAME_GROUP("@proclassify_group","groupNo,loginCount"),
	INDEX_NAME_GROUP_MAPPING("@proclassify_group_mapping","mappingNo,groupNo,siteNo"),
	INDEX_NAME_SIMULATION_HISTORY("@proclassify_simulation_history","siteNo"),
	INDEX_NAME_SIMULATION_SUMMARY("@proclassify_simulation_summary","siteNo,version,dataCnt,failedCnt,matchedDataCnt,ruleMatchedDataCnt,classifyMatchedDataCnt,runtime"),
	INDEX_NAME_SYSTEM_LOG("@proclassify_system_log","fail,sucess,took,total"),
	INDEX_NAME_UPLOAD_INVALID_DATA("@proclassify_upload_invalid","siteNo");
	
	private String index;
	private String numberTypeFields;
	
	ElasticSearchIndex(String index, String numberTypeFields){
		this.index = index;
		this.numberTypeFields = numberTypeFields;
	}
	
	public String getIndexName() {
		return this.index;
	}

	public String getNumberTypeFields() {
		return numberTypeFields;
	}
}
