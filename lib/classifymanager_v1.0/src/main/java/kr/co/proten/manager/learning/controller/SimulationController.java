package kr.co.proten.manager.learning.controller;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kr.co.proten.manager.common.ClassifyManagerConstant;
import kr.co.proten.manager.common.CommonMAV;
import kr.co.proten.manager.common.util.StringUtil;
import kr.co.proten.manager.learning.service.ModelingService;
import kr.co.proten.manager.learning.service.SimulationService;
import kr.co.proten.manager.learning.vo.ResponseClassifyRootVo;
import kr.co.proten.manager.login.model.LoginModel;
import lombok.RequiredArgsConstructor;

/**
 * 학습관리
 */
@Controller
@RequestMapping("learning")
@RequiredArgsConstructor
public class SimulationController {

	private static final Logger log = LoggerFactory.getLogger(SimulationController.class);
	
	private final SimulationService simulationService;
	
	private final ModelingService modelingService; 
	
	/**
	 * 시뮬레이션 메인
	 * 
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "simulation.ps")
	public ModelAndView simulation(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		log.debug( "Get {}", request.getRequestURI() );
		CommonMAV mav = new CommonMAV(request, "simulation");
		
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		Map<String, Object> modelParam = new HashMap<String, Object>();
		modelParam.put("state", "success");
		modelParam.put("siteNo", login.getSiteNo());
		List<Map<String, Object>> list = null;
		
		try {
			list = modelingService.modelingList(paramMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		result.addAttribute("modelingList", list);
		mav.setViewName("/learning/simulation");
		return mav;
	}
	
	/**
	 * 단건시뮬레이션
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "simulationAjax.ps")
	public String simulationAjax(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session){
		
		log.debug( "Get {}", request.getRequestURI() );
		GsonBuilder gsonBuilder = new GsonBuilder();  
		gsonBuilder.serializeNulls();  
		Gson gson = gsonBuilder.create();
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		
		String query = StringUtil.nvl(paramMap.get("query"), "");
		String version = StringUtil.nvl(paramMap.get("version"), "");
		
		String siteCode = "";
		for(Map<String,Object> site : login.getSiteList()) {
			if((int)site.get("siteNo") == login.getSiteNo()) {
				siteCode = (String) site.get("site");
			}
		}
		
		ResponseClassifyRootVo apiResultVo = null;
		String apiResult = "";
		try {
			apiResult = simulationService.requestSimulationApi(siteCode, query, version);
			apiResultVo = gson.fromJson(apiResult, ResponseClassifyRootVo.class);
			simulationService.convertSimulationApiMessage(apiResultVo);
			result.addAttribute("success", true);
		} catch (Exception e) {
			result.addAttribute("msg", "API 요청이 정상적으로 이루어지지 않았습니다. API 서버의 상태를 확인해 주세요.");
			result.addAttribute("success", false);
			log.error(e.getMessage());
			
		}		
		result.addAttribute("apiResult", apiResultVo);
		
		return "jsonView";
	}
	
	/**
	 * 다건시뮬레이션 summary list
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "bulkSummaryAjax.ps")
	public String bulkSummaryAjax(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session){
		
		log.debug( "Get {}", request.getRequestURI() );
		int lineNo = StringUtil.parseInt((String) paramMap.get("lineNo"), 5);
		
		paramMap.put("lineNo", lineNo);
		paramMap.put("start", (StringUtil.nvl(paramMap.get("pageNo"), 1)-1)*lineNo+"");
		
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", login.getSiteNo());

		List<Map<String, Object>> resultList = null;		
		try {
			resultList = simulationService.selectSimulationSummaryList(paramMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		result.addAttribute("summaryList", resultList);
		if(resultList != null && resultList.size() > 0) {
			result.addAttribute("totalCnt", resultList.get(0).get("totalCount"));
		} else {
			result.addAttribute("totalCnt", 0);
		}
		result.addAttribute("pageNo", StringUtil.nvl(paramMap.get("pageNo"), "1"));

		return "jsonView";
	}	
	
	/**
	 * 다건시뮬레이션 history list
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "bulkHistoryAjax.ps")
	public String bulkHistoryAjax(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session){
		
		log.debug( "Get {}", request.getRequestURI() );
		int lineNo = StringUtil.parseInt((String) paramMap.get("lineNo"), 10);
		
		paramMap.put("lineNo", lineNo);
		paramMap.put("start", (StringUtil.nvl(paramMap.get("pageNo"), 1)-1)*lineNo+"");
		
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", login.getSiteNo());

		List<Map<String, Object>> resultList = null;		
		try {
			resultList = simulationService.selectSimulationHistoryList(paramMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		result.addAttribute("historyList", resultList);
		if(resultList != null && resultList.size() > 0) {
			result.addAttribute("totalCnt", resultList.get(0).get("totalCount"));
		} else {
			result.addAttribute("totalCnt", 0);
		}
		result.addAttribute("pageNo", StringUtil.nvl(paramMap.get("pageNo"), "1"));

		return "jsonView";
	}	
	
}
