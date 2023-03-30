package kr.co.proten.manager.learning.vo;

public class RequestClassifyParamVo {

	private String site;
	private String query;
	private String version;
	private String esUrl;
	private String size = "5";
	
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getEsUrl() {
		return esUrl;
	}
	public void setEsUrl(String esUrl) {
		this.esUrl = esUrl;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
}
