package kr.co.proten.manager.common;

import java.util.ArrayList;
import java.util.HashMap;

public class SiteTreeModel {

	private String id;
	private String root_id;
	private String parent_id;
	private int node_level;
	private String node_path;
	private String name;
	private String leaf;
//	private String bsnsType;
//	private String isAnswer;
	private ArrayList<SiteTreeModel> children = new ArrayList<SiteTreeModel>();
	private int leafChildCount;

	public SiteTreeModel() {}
	
	public SiteTreeModel(String categoryRootId, String categoryRootName, String categoryRootPath) {
		this.id 		= categoryRootId;
		this.name 		= categoryRootName;
		this.node_path 	= categoryRootPath;
	}
	
	public SiteTreeModel(HashMap<String, Object> map) {
		this.id 		= map.get("SITE_ID").toString();
		this.name 		= map.get("SITE_NAME").toString();
		this.node_level = Integer.parseInt(map.get("NODE_LEVEL").toString());
		this.leaf 		= map.get("IS_LEAF").toString();
		this.node_path 	= map.get("NODE_PATH").toString();
		this.root_id 	= map.get("ROOT_ID").toString();
		
		if(map.get("PARENT_SITE_ID") != null)
			this.parent_id = map.get("PARENT_SITE_ID").toString();
//		if(map.get("BSNS_TYPE") != null)
//			this.bsnsType  = map.get("BSNS_TYPE").toString();
//		if(map.get("IS_ANSWER") != null)
//			this.isAnswer  = map.get("IS_ANSWER").toString();
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParent_id() {
		return parent_id;
	}
	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}
	public String getRoot_id() {
		return root_id;
	}
	public void setRoot_id(String root_id) {
		this.root_id = root_id;
	}
	public int getNode_level() {
		return node_level;
	}
	public void setNode_level(int node_level) {
		this.node_level = node_level;
	}
	public String getNode_path() {
		return node_path;
	}
	public void setNode_path(String node_path) {
		this.node_path = node_path;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<SiteTreeModel> getChildren() {
		return children;
	}
	public void setChildren(ArrayList<SiteTreeModel> children) {
		this.children = children;
	}
	public String getLeaf() {
		return leaf;
	}
	public void setLeaf(String leaf) {
		this.leaf = leaf;
	}
//	public String getBsnsType() {
//		return bsnsType;
//	}
//	public void setBsnsType(String bsnsType) {
//		this.bsnsType = bsnsType;
//	}
//	public String getIsAnswer() {
//		return isAnswer;
//	}
//	public void setIsAnswer(String isAnswer) {
//		this.isAnswer = isAnswer;
//	}
	
	public int getLeafChildCount() {
		return leafChildCount;
	}

	public void setLeafChildCount(int leafChildCount) {
		this.leafChildCount = leafChildCount;
	}
	
	@Override
	public String toString(){
		/*return "TreeModel [id="+this.id+", name="+this.name+", parent_id="+this.parent_id+", root_id="+this.root_id
				+", node_level="+this.node_level+", node_path="+this.node_path+",childeren size="+this.children.size()
				+", leaf="+this.leaf+", bsnsType="+this.bsnsType+", isAnswer="+this.isAnswer+", leafChildCount="+this.leafChildCount+"]";*/
		
		return "TreeModel [id="+this.id+", name="+this.name+", parent_id="+this.parent_id+", root_id="+this.root_id
				+", node_level="+this.node_level+", node_path="+this.node_path+",childeren size="+this.children.size()
				+", leaf="+this.leaf+", leafChildCount="+this.leafChildCount+"]";
	}
}
