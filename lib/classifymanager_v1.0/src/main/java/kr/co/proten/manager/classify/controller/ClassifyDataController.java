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
import kr.co.proten.manager.classify.service.ClassifyDataService;
import kr.co.proten.manager.common.ClassifyManagerConstant;
import kr.co.proten.manager.login.model.LoginModel;
import lombok.RequiredArgsConstructor;

/**
 * 데이터 관리
 * @author Proten
 *
 */
@Controller
@RequestMapping("classify")
@RequiredArgsConstructor
public class ClassifyDataController {
	 
	private static final Logger log = LoggerFactory.getLogger(ClassifyDataController.class);
	
	private final ClassifyDataService classifyDataService;

	/**
	 * 데이터 초기화면
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "learningData.ps")
	public ModelAndView learningData(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		log.debug( "Get {}", request.getRequestURI() );
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", Integer.toString(login.getSiteNo()));
		CommonMAV mav = new CommonMAV(request, "learningData");
		mav.setViewName("/classify/learningData");
		return mav;
	} 
	
	/**
	 * 분류 데이터 목록
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "learningDataAjax.ps")
	public String learningDataAjax(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response,HttpSession session){

		log.debug( "Get {}", request.getRequestURI() );
		int lineNo = StringUtil.parseInt((String) paramMap.get("lineNo"), 10);
		
		paramMap.put("lineNo", StringUtil.nvl(lineNo));
		paramMap.put("start", (StringUtil.nvl(paramMap.get("pageNo"), 1)-1 )*StringUtil.nvl(lineNo, 10 )+"");		
		paramMap.put("sort", "modifyDate");
		
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", login.getSiteNo());

		List<Map<String, Object>> resultList = null;		
		try {
			resultList = classifyDataService.selectDataList(paramMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		result.addAttribute("dataList", resultList);
		if(resultList != null && resultList.size() > 0) {
			result.addAttribute("totalCnt", resultList.get(0).get("totalCount"));
		} else {
			result.addAttribute("totalCnt", 0);
		}
		result.addAttribute("pageNo", StringUtil.nvl(paramMap.get("pageNo"), "1"));

		return "jsonView";
	}
	
	/**
	 * 분류 데이터 정보 저장
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return Json
	 */
	@RequestMapping(value = "saveLearningData.ps")
	public String saveLearningData(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		log.debug( "Get {}", request.getRequestURI() );
		log.debug( "paramMap {}", paramMap );
		boolean success = true;		
		String action = StringUtil.nvl(paramMap.get("action"));
		paramMap.remove("action");
		if("new".equals(action)) {
			try {
				success = classifyDataService.insertDataInfo(paramMap, session);
			} catch (Exception e) {
				success = false;
			}
			if(!success) {
				result.addAttribute("msg", "분류 데이터 저장에 실패하였습니다.");
			}
		} else {
			try {
				classifyDataService.updateDataInfo(paramMap, session);
			} catch (Exception e) {
				success = false;
			}
			if(!success) {
				result.addAttribute("msg", "분류 데이터 수정에 실패하였습니다.");
			}
		}
		
		result.addAttribute("success", success);

		return "jsonView";
	}
	
	/**
	 * 분류 데이터 삭제
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return Json
	 */
	@RequestMapping(value = "delLearningData.ps")
	public String delLearningData(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response){

		log.debug( "Get {}", request.getRequestURI() );

		boolean success = true;
		try {
			success = classifyDataService.deleteDataInfo(paramMap);
		} catch (Exception e) {
			success = false;
			result.addAttribute("msg", "분류 데이터 삭제를 실패하였습니다.");
		}
		result.addAttribute("success", success);
		return "jsonView";
	}

}
