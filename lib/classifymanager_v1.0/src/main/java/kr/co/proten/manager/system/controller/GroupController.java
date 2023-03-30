package kr.co.proten.manager.system.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import kr.co.proten.manager.common.ClassifyManagerConstant;
import kr.co.proten.manager.common.CommonMAV;
import kr.co.proten.manager.common.util.StringUtil;
import kr.co.proten.manager.login.model.LoginModel;
import kr.co.proten.manager.system.service.GroupService;
import lombok.RequiredArgsConstructor;

/**
 * 시스템관리 > 그룹 관리
 */
@Controller
@RequestMapping("system")
@RequiredArgsConstructor
public class GroupController {
	
	private static final Logger log = LoggerFactory.getLogger(GroupController.class);
	
	private final GroupService groupService;
	
	/**
	 * 그룹관리 메인
	 * 
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "group.ps")
	public ModelAndView group(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		log.debug( "Get {}", request.getRequestURI() );
		CommonMAV mav = new CommonMAV(request, "group");
		mav.setViewName("/system/group");
		return mav;
	}
	
	/**
	 * 그룹 목록
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "groupAjax.ps")
	public String groupAjax(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response,HttpSession session){

		log.debug( "Get {}", request.getRequestURI() );

		int lineNo = StringUtil.parseInt((String) paramMap.get("lineNo"), 10);
		paramMap.put("lineNo", StringUtil.nvl(lineNo));
		paramMap.put("start", (StringUtil.nvl(paramMap.get("pageNo"), 1)-1 )*StringUtil.nvl(lineNo, 10 )+"");
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		
		if(!login.getAdminYn().equals("y")) {
			paramMap.put("groupNo", login.getGroupNo());
		}
		
		List<Map<String, Object>> list = new ArrayList<>();
		try {
			list = groupService.selectGroupList(paramMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		result.addAttribute("groupList", list);
		result.addAttribute("totalCnt", list.size() > 0 ? list.get(0).get("totalCount") : 0);
		result.addAttribute("pageNo", StringUtil.nvl(paramMap.get("pageNo"), "1"));

		return "jsonView";
	}
	
	/**
	 * 그룹 상세
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "groupAjaxEdit.ps")
	public String groupAjaxEdit(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response){

		log.debug( "Get {}", request.getRequestURI() );

		List<Map<String, Object>> list = new ArrayList<>();
		try {
			list = groupService.selectGroupList(paramMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		result.addAttribute("groupList", list);

		return "jsonView";
	}
	
	/**
	 * 그룹 중복 체크
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return Json
	 */
	@RequestMapping(value = "dupGroup.ps")
	public String dupGroup(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		log.debug( "Get {}", request.getRequestURI() );
		boolean success = true;
		boolean dup = false; 
		
		try {
			dup = groupService.dupGroupInfo(paramMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		if(dup) {
			success = false;
			result.addAttribute("msg", "서비스코드가 중복되었습니다.");
		} 
		result.addAttribute("success", success);

		return "jsonView";
	}
   
	/**
	 * 그룹 정보 저장
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return Json
	 */
	@RequestMapping(value = "saveGroup.ps")
	public String groupSave(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		log.debug( "Get {}", request.getRequestURI() );
		log.debug( "paramMap {}", paramMap );
		boolean success = true;		
		String add = StringUtil.nvl(paramMap.get("add"));
		paramMap.remove("add");
		if("Y".equals(add)){
			try {				
				success = groupService.insertGroupInfo(paramMap, session);
			} catch(Exception e) {
				log.error(e.getMessage());
				success = false;
			}
			if(!success) {
				result.addAttribute("msg", "그룹 정보 저장에 실패하였습니다.");
			}
		}else{
			try {
				success = groupService.updateGroupInfo(paramMap, session);
			} catch (Exception e) {
				log.error(e.getMessage());
				success = false;
			} //수정
			if(!success) {
				result.addAttribute("msg", "그룹 정보 수정에 실패하였습니다.");
			}
		}
		result.addAttribute("success", success);

		return "jsonView";
	}
	
	/**
	 * 그룹 삭제
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return Json
	 */
	@RequestMapping(value = "delGroup.ps")
	public String groupDel(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response){

		log.debug( "Get {}", request.getRequestURI() );
		Map<String,Object> results = new HashMap<String,Object>();
		try {
			results = groupService.deleteGroupInfo(paramMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		result.addAttribute("delData", results);
		return "jsonView";
	}
	
	/**
	 * 메뉴 권한 정보 리스트
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "siteAuthAjax.ps")
	public String authAjax(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response){
		log.debug( "Get {}", request.getRequestURI() );
		paramMap.put("userNo", StringUtil.nvl(paramMap.get("userNo"), "101"));

		List<Map<String, Object>> authList = new ArrayList<Map<String, Object>>();
		try {
			authList = groupService.selectAuthMenuList(paramMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		log.debug(authList.toString());
		
		result.addAttribute("authList", authList);
		result.addAttribute("authTotalCnt", authList.size());

		return "jsonView";
	}
	
	/**
	 * 사이트 권한 정보 저장
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "saveAuthGroupSite.ps")
	public String saveAuthGroupSite(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session){

		log.debug( "Get {}", request.getRequestURI() );
		boolean success = false;
		try {
			success = groupService.insertAuthInfo(paramMap, session);
		} catch (Exception e) {
			log.error(e.getMessage());
		}	//수정
		result.addAttribute("success", success);

		return "jsonView";
	}
	/**
	 * 메뉴 권한 정보 저장
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "saveAuthMenu.ps")
	public String saveAuthMenu(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session){
		log.debug( "Get {}", request.getRequestURI() );
		boolean success = false;
		
		String[] menuIds = request.getParameterValues("menuIds");
		paramMap.put("menuIds" , menuIds);
		paramMap.remove("menuIds[]");
		
		try {
			success = groupService.insertAuthSiteInfo(paramMap,session);
		} catch (Exception e) {
			log.error(e.getMessage());
		}	//수정
		result.addAttribute("success", success);
		
		return "jsonView";
	}
}
