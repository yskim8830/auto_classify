package kr.co.proten.manager.login.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class LoginModel implements Serializable {

	private static final long serialVersionUID = 188066137958650658L;
	private int userNo;
	private String userId;
	private String pno;
	private String userNm;
	private int groupNo;
	private int siteNo;
	private String createUser;
	private String createUserNm;
	private String createDate;
	private String modifyUser;
	private String modifyUserNm;
	private String modifyDate;	
	private String adminYn;
	private String useYn;
	private int loginCount;
	private List<Map<String, Object>> menuList;
	private List<Map<String, Object>> siteList;
	private String menuInfo;
	private String bldVersion;
	private String bldTimestamp;

}