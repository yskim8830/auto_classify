package kr.co.proten.manager.classify.controller;

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

import kr.co.proten.manager.common.CommonMAV;
import kr.co.proten.manager.common.util.StringUtil;
import kr.co.proten.manager.dic.service.ObjectService;
import kr.co.proten.manager.classify.service.ClassifyRuleService;
import kr.co.proten.manager.common.ClassifyManagerConstant;
import kr.co.proten.manager.login.model.LoginModel;
import lombok.RequiredArgsConstructor;

/**
 * 룰 관리
 * @author Proten
 *
 */
@Controller
@RequestMapping("classify")
@RequiredArgsConstructor
public class ClassifyRuleController {
	 
	private static final Logger log = LoggerFactory.getLogger(ClassifyRuleController.class);
	
	private final ClassifyRuleService classifyRuleService;
	
	private final ObjectService objectDicService;

	/**
	 * 룰 초기화면
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "rule.ps")
	public ModelAndView dic(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		log.debug( "Get {}", request.getRequestURI() );
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", Integer.toString(login.getSiteNo()));
		
		//개체 사전 조회
		List<Map<String, Object>> dicList = null; 
		try {
			dicList = objectDicService.selectObjDicListAll(paramMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		result.addAttribute("dicList", dicList);
		result.addAttribute("isPopup", paramMap.get("isPopup"));
		result.addAttribute("categoryNo", paramMap.get("categoryNo"));
				
		CommonMAV mav = new CommonMAV(request, "rule");
		mav.setViewName("/classify/rule");
		return mav;
	} 
	
	/**
	 * 분류 룰 목록
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "ruleAjax.ps")
	public String ruleAjax(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response,HttpSession session){

		log.debug( "Get {}", request.getRequestURI() );
		int lineNo = StringUtil.parseInt((String) paramMap.get("lineNo"), 10);
		
		paramMap.put("lineNo", StringUtil.nvl(lineNo));
		paramMap.put("start", (StringUtil.nvl(paramMap.get("pageNo"), 1)-1 )*StringUtil.nvl(lineNo, 10 )+"");		
		paramMap.put("sort", "modifyDate");
		
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", login.getSiteNo());

		List<Map<String, Object>> resultList = null;		
		try {
			resultList = classifyRuleService.selectRuleList(paramMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		result.addAttribute("ruleList", resultList);
		if(resultList != null && resultList.size() > 0) {
			result.addAttribute("totalCnt", resultList.get(0).get("totalCount"));
		} else {
			result.addAttribute("totalCnt", 0);
		}
		result.addAttribute("pageNo", StringUtil.nvl(paramMap.get("pageNo"), "1"));

		return "jsonView";
	}
	
	/**
	 * 분류 룰 정보 저장
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return Json
	 */
	@RequestMapping(value = "saveRule.ps")
	public String saveRule(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		log.debug( "Get {}", request.getRequestURI() );
		log.debug( "paramMap {}", paramMap );
		boolean success = true;		
		String action = StringUtil.nvl(paramMap.get("action"));
		paramMap.remove("action");
		if("new".equals(action)) {
			try {
				success = classifyRuleService.insertRuleInfo(paramMap, session);
			} catch (Exception e) {
				log.error(e.getMessage());
				success = false;
			}
			if(!success) {
				result.addAttribute("msg", "분류 룰 저장에 실패하였습니다.");
			}
		} else {
			try {
				classifyRuleService.updateRuleInfo(paramMap, session);
			} catch (Exception e) {
				log.error(e.getMessage());
				success = false;
			}
			if(!success) {
				result.addAttribute("msg", "분류 룰 수정에 실패하였습니다.");
			}
		}
		
		result.addAttribute("success", success);

		return "jsonView";
	}
	
	/**
	 * 분류 룰 중복 체크
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return Json
	 */
	@RequestMapping(value = "dupRule.ps")
	public String dupRule(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		log.debug( "Get {}", request.getRequestURI() );

		boolean success = true;	
		boolean dup = false; 
		try {
			dup = classifyRuleService.dupRuleInfo(paramMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		if(dup) {
			success = false;
			result.addAttribute("msg", "분류 룰이 중복되었습니다.");
		} 
		result.addAttribute("success", success);

		return "jsonView";
	}
	
	/**
	 * 분류 룰 삭제
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return Json
	 */
	@RequestMapping(value = "deleteRule.ps")
	public String deleteRule(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response){

		log.debug( "Get {}", request.getRequestURI() );

		boolean success = true;
		try {
			success = classifyRuleService.deleteRuleInfo(paramMap);
		} catch (Exception e) {
			success = false;
			result.addAttribute("msg", "분류 룰 삭제를 실패하였습니다.");
		}
		result.addAttribute("success", success);
		return "jsonView";
	}
}
