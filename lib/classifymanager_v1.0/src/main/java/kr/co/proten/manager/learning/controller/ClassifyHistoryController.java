package kr.co.proten.manager.learning.controller;

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
import kr.co.proten.manager.learning.service.ClassifyHistoryService;
import kr.co.proten.manager.login.model.LoginModel;
import lombok.RequiredArgsConstructor;
/**
 * 학습관리
 */
@Controller
@RequestMapping("learning")
@RequiredArgsConstructor
public class ClassifyHistoryController {

	private static final Logger log = LoggerFactory.getLogger(ClassifyHistoryController.class);
	
	private final ClassifyHistoryService classifyHistoryService;
	
	/**
	 * 자동분류 이력 메인
	 * 
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "history.ps")
	public ModelAndView resplog(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		log.debug( "Get {}", request.getRequestURI() );
		CommonMAV mav = new CommonMAV(request, "history");
		mav.setViewName("/learning/history");
		return mav;
	}
	
	/**
	 * 목록
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "historyAjax.ps")
	public String historyAjax(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session){
		
		log.debug( "Get {}", request.getRequestURI() );
		int lineNo = StringUtil.parseInt((String) paramMap.get("lineNo"), 10);
		paramMap.put("lineNo", StringUtil.nvl(lineNo));
		paramMap.put("start", (StringUtil.nvl(paramMap.get("pageNo"), 1)-1 )*StringUtil.nvl(lineNo, 10 )+"");
		
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", login.getSiteNo());
		
		List<Map<String, Object>> list = null;
		
		try {
			list = classifyHistoryService.selectHistoryList(paramMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		result.addAttribute("historyList", list);
		result.addAttribute("totalCnt", list.size() > 0 ? list.get(0).get("totalCount") : 0);
		result.addAttribute("pageNo", StringUtil.nvl(paramMap.get("pageNo"), "1"));
		return "jsonView";
	}
}
