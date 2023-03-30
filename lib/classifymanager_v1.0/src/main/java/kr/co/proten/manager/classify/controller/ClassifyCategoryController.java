package kr.co.proten.manager.classify.controller;

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

import kr.co.proten.manager.classify.service.ClassifyCategoryService;
import kr.co.proten.manager.common.ClassifyManagerConstant;
import kr.co.proten.manager.common.CommonMAV;
import kr.co.proten.manager.common.util.StringUtil;
import kr.co.proten.manager.login.model.LoginModel;
import lombok.RequiredArgsConstructor;

/**
 * 카테고리  관리
 * @author Proten
 *
 */
@Controller
@RequestMapping("classify")
@RequiredArgsConstructor
public class ClassifyCategoryController {
	 
	private static final Logger log = LoggerFactory.getLogger(ClassifyCategoryController.class);
	
	private final ClassifyCategoryService classifyCategoryService;
	
	/**
	 * 카테고리 메인
	 * 
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "category.ps")
	public ModelAndView category(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		log.debug( "Get {}", request.getRequestURI() );
		CommonMAV mav = new CommonMAV(request, "category");
		
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		//카테고리1뎁스 조회
		paramMap.put("depth", "1");
		paramMap.put("siteNo", Integer.toString(login.getSiteNo()));
		mav.setViewName("/classify/category");
		return mav;
	}

	/**
	 * 카테고리 목록
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "categoryAjax.ps")
	public String categoryAjax(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response,HttpSession session) {

		log.debug( "Get {}", request.getRequestURI() );
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", Integer.toString(login.getSiteNo()));
		List<Map<String, Object>> resultList = null;
		List<Map<String, Object>> categoryList = null;
		
		try {
			resultList = classifyCategoryService.selectCategoryList(paramMap, true);
			categoryList = classifyCategoryService.convertCategoryListConvHierarchyData(resultList);
		} catch (Exception e) {
			log.error("[ClassifyCategoryController] [categoryAjax] {}", e.getMessage());
		}
		
		result.addAttribute("categoryList", categoryList);
		result.addAttribute("cateList", resultList);

		return "jsonView";
	}
	
	/**
	 * 카테고리 조회
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "dtlCategory.ps")
	public String dtlCategory(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session){

		log.debug( "Get {}", request.getRequestURI() );
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", login.getSiteNo());
		List<Map<String, Object>> list = null;
		try {
			list = classifyCategoryService.selectViewCategory(paramMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		result.addAttribute("dtlCategory", list);

		return "jsonView";
	}
	
	/**
	 * 카테고리 수정
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "saveCategory.ps")
	public String saveCategory(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session){
		log.debug( "Get {}", request.getRequestURI() );
		Map<String,Object> ret = new HashMap<String,Object>();
		String add = StringUtil.nvl(paramMap.get("add"));	
		paramMap.remove("add");
		if("Y".equals(add)){
			LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
			paramMap.put("siteNo", login.getSiteNo());
			try {
				ret = classifyCategoryService.insertCategoryInfo(paramMap, session);
			} catch (Exception e) {
				log.error(e.getMessage());
			} //추가
		}else{
			try {
				ret = classifyCategoryService.updateCategoryInfo(paramMap, session);
			} catch (Exception e) {
				log.error(e.getMessage());
			} //수정
		}
		result.addAttribute("success", ret.get("success"));

		return "jsonView";
	}
	
	/**
	 * 카테고리명 중복 체크
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return Json
	 */
	@RequestMapping(value = "checkDupCategoryName.ps")
	public String checkDupCategoryName(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {

		log.debug( "Get {}", request.getRequestURI() );
		
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", login.getSiteNo());
		
		boolean isDup = classifyCategoryService.dupCategoryInfo(paramMap);
		if(isDup) {
			result.addAttribute("msg", "동일한 카테고리명이 존재합니다. 다른 이름으로 추가하십시오. ");
		}
		result.addAttribute("isDup", isDup);

		return "jsonView";
	}
	
	/**
	 * 카테고리 삭제
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "delCategory.ps")
	public String delCategory(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response){

		log.debug( "Get {}", request.getRequestURI() );
		boolean success = true;
		try {
			success = classifyCategoryService.deleteCategoryInfo(paramMap);
		} catch (Exception e) {
			success = false;
			result.addAttribute("msg", "카테고리 삭제를 실패하였습니다.");
		}
		result.addAttribute("success", success);
		return "jsonView";
	}

}
