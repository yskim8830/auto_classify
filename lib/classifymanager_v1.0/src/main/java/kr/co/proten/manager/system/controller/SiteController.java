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
import kr.co.proten.manager.system.service.SiteService;
import lombok.RequiredArgsConstructor;

/**
 * 사이트 관리
 */
@Controller
@RequestMapping("system")
@RequiredArgsConstructor
public class SiteController {

	private static final Logger log = LoggerFactory.getLogger(SiteController.class);
	
	private final SiteService siteService;

	private final GroupService groupService;
	
	
	/**
	 * 사이트 관리 메인
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "site.ps")
	public ModelAndView site(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		log.debug( "Get {}", request.getRequestURI() );
		CommonMAV mav = new CommonMAV(request, "system");
		mav.setViewName("/system/site");	
		return mav;
	}

	/**
	 * 사이트 목록
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "siteAjax.ps")
	public String siteAjax(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		log.debug( "Get {}", request.getRequestURI() );

		int lineNo = StringUtil.parseInt((String) paramMap.get("lineNo"), 10);
		paramMap.put("lineNo", StringUtil.nvl(lineNo));
		paramMap.put("start", (StringUtil.nvl(paramMap.get("pageNo"), 1)-1 )*StringUtil.nvl(lineNo, 10 )+"");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		
		if(!login.getAdminYn().equals("y")) {
			Map<String, Object> _paramMap = new HashMap<String, Object>();
			_paramMap.put("groupNo", login.getGroupNo());
			try {
				list = groupService.selectSiteToGroup(_paramMap);
			} catch (Exception e) {
				log.error("[SiteController] [siteAjax] {}", e.getMessage());
			}
		}else {
			try {
				list = siteService.selectSiteList(paramMap);
			} catch (Exception e) {
				log.error("[SiteController] [siteAjax] {}", e.getMessage());
			}			
		}
		
		result.addAttribute("siteList", list);
		result.addAttribute("totalCnt", list.size() > 0 ? list.get(0).get("totalCount") : 0);
		result.addAttribute("pageNo", StringUtil.nvl(paramMap.get("pageNo"), "1"));

		return "jsonView";
	}
	
	/**
	 * 사이트 상세
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "siteAjaxEdit.ps")
	public String siteAjaxEdit(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response){

		log.debug( "Get {}", request.getRequestURI() );

		List<Map<String, Object>> siteList = null;
		try {
			siteList = siteService.selectSiteList(paramMap);
		} catch (Exception e) {
			log.error("[SiteController] [siteAjaxEdit] {}", e.getMessage());
		}
		result.addAttribute("siteList", siteList);

		return "jsonView";
	}
	
	/**
	 * 사이트 중복 체크
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return Json
	 */
	@RequestMapping(value = "dupSite.ps")
	public String dupSite(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		log.debug( "Get {}", request.getRequestURI() );

		boolean success = true;	
		boolean dup = false; 
		try {
			dup = siteService.dupSiteInfo(paramMap);
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
	 * 사이트 정보 저장
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return Json
	 */
	@RequestMapping(value = "saveSite.ps")
	public String siteSave(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		log.debug( "Get {}", request.getRequestURI() );
		log.debug( "paramMap {}", paramMap );
		boolean success = true;		
		String add = StringUtil.nvl(paramMap.get("add"));
		paramMap.remove("add");
		if("Y".equals(add)) {
			try {
				success = siteService.insertSiteInfo(paramMap, session);
			} catch (Exception e) {
				success = false;
			}
			if(!success) {
				result.addAttribute("msg", "사이트 저장에 실패하였습니다.");
			}
		} else {
			try {
				siteService.updateSiteInfo(paramMap, session);
			} catch (Exception e) {
				success = false;
			}
			if(!success) {
				result.addAttribute("msg", "사이트 수정에 실패하였습니다.");
			}
		}
		
		result.addAttribute("success", success);

		return "jsonView";
	}
	
	/**
	 * 사이트 삭제
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return Json
	 */
	@RequestMapping(value = "delSite.ps")
	public String siteDel(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response){

		log.debug( "Get {}", request.getRequestURI() );

		boolean success = true;
		try {
			success = siteService.deleteSiteInfo(paramMap);
		} catch (Exception e) {
			success = false;
			result.addAttribute("msg", "사이트 삭제를 실패하였습니다.");
		}
		result.addAttribute("success", success);
		return "jsonView";
	}
}
