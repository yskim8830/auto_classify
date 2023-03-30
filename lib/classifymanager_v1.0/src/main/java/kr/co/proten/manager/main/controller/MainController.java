package kr.co.proten.manager.main.controller;

import kr.co.proten.manager.common.CommonMAV;
import kr.co.proten.manager.common.elastic.ElasticSearchConstant;
import kr.co.proten.manager.common.util.DateUtil;
import kr.co.proten.manager.common.ClassifyManagerConstant;
import kr.co.proten.manager.login.model.LoginModel;
import kr.co.proten.manager.main.service.MainService;
import kr.co.proten.manager.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("main")
public class MainController {

	private static final Logger log = LoggerFactory.getLogger(MainController.class);
	
	private final MainService mainService;
	
	private final StatisticsService statisticsService;

	@RequestMapping(value = "main.ps")
	public ModelAndView main(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		log.debug( "Get {}", request.getRequestURI() );
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		CommonMAV mav = new CommonMAV(request, "main");
		mav.addObject("siteNo", login.getSiteNo());	
		mav.setViewName("/main/main");
		return mav;
	}
	
	@RequestMapping(value = "getSystemInfo.ps")
	public String getSystemInfo(Model result, @RequestParam Map<String, String> paramMap,HttpServletRequest request) throws Exception {

		log.debug( "Get {}", request.getRequestURI() );
		result.addAttribute("allocation", mainService.getCatAllocation());
		result.addAttribute("nodes", mainService.getCatNodes());
		return "jsonView";

	}
	
	/**
	 * 세션 체인지
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	@RequestMapping(value = "changeSession.ps")
	public ModelAndView changeSession(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws NumberFormatException, Exception{

		log.info( "change Session : {}", request.getRequestURI() );
		ModelAndView mav = new ModelAndView();
		
		LoginModel login = new LoginModel();
		login = (LoginModel) session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		
		mainService.modifyUserSiteInfo(login, Integer.parseInt((String)paramMap.get("siteId")));
		session.removeAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		
		List<Map<String, Object>> menu = login.getMenuList();

		if (menu.size() > 0) {
			result.addAttribute("menuUrl", menu.get(0).get("menuUrl"));    //화면
		}
		session.setAttribute("CSRF_TOKEN", UUID.randomUUID().toString());
		session.setAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME, login);
		session.setAttribute("userId", login.getUserId());
		session.setAttribute("menu", menu);
		log.info("Change Session success : {}, siteId : {}", login.getUserId(), Integer.parseInt((String)paramMap.get("siteId")));

		mav.setViewName("redirect:/main/main.ps");
		
		return mav;
	}
	
	@RequestMapping(value = "getStatisticsInfo.ps")
	public String getStatisticsInfo(Model result, @RequestParam Map<String, String> paramMap, HttpServletRequest request, HttpSession session) throws Exception {

		log.debug( "Get {}", request.getRequestURI() );
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		
		String startDate = DateUtil.getPreDate(30, "yyyyMMdd") + "000000000";
		String todayStartDate = DateUtil.getCurrentDate() + "000000000";
		String endDate = DateUtil.getCurrentDate() + "235959999";
		
		Map<String, List<Map<String, Object>>> todayAggregationListMap = null;
		Map<String, List<Map<String, Object>>> trendAggregationListMap = null;
		List<Map<String, Object>> trendResultList = new ArrayList<>();
		List<Map<String, Object>> categoryResultList = new ArrayList<>();
		try {
			trendAggregationListMap = mainService.selectStatisticsDataList(Integer.toString(login.getSiteNo()), startDate, endDate, "day");
			todayAggregationListMap = mainService.selectStatisticsDataList(Integer.toString(login.getSiteNo()), todayStartDate, endDate, "category");
			
			List<Map<String, Object>> resultMatchedCatgegoryListMap = todayAggregationListMap.get(ElasticSearchConstant.AGGREGATION_MATCHED_CATEGORY);
			statisticsService.convertCategoryValue(categoryResultList, resultMatchedCatgegoryListMap);
			Map<String, Object> resultTypeMap = new HashMap<>();
			Map<String, Object> matchedTypeMap = new HashMap<>();
			List<Map<String, Object>> resultResultTypeListMap = todayAggregationListMap.get(ElasticSearchConstant.AGGREGATION_RESULT_TYPE);
			statisticsService.convertTermsValue(resultTypeMap, resultResultTypeListMap);
			List<Map<String, Object>> resultMatchedTypeListMap = todayAggregationListMap.get(ElasticSearchConstant.AGGREGATION_MATCHED_TYPE);
			statisticsService.convertTermsValue(matchedTypeMap, resultMatchedTypeListMap);
			
			trendResultList = statisticsService.generateTrendList("day", DateUtil.getPreDate(30, "yyyyMMdd"), DateUtil.getCurrentDate());
			List<Map<String, Object>> resultTrendListMap = trendAggregationListMap.get(ElasticSearchConstant.AGGREGATION_DATE);
			statisticsService.mergeTrendListValue(trendResultList, resultTrendListMap);
			result.addAttribute("trendList", trendResultList);
			result.addAttribute("categoryMapList", categoryResultList);
			result.addAttribute("resultResultTypeMap", resultTypeMap);
			result.addAttribute("resultMatchedTypeMap", matchedTypeMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return "jsonView";

	}
	
}
