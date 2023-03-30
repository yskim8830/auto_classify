package kr.co.proten.manager.statistics.controller;

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

import kr.co.proten.manager.common.CommonMAV;
import kr.co.proten.manager.common.elastic.ElasticSearchConstant;
import kr.co.proten.manager.common.service.DownloadDataService;
import kr.co.proten.manager.common.ClassifyManagerConstant;
import kr.co.proten.manager.common.util.DateUtil;
import kr.co.proten.manager.common.util.StringUtil;
import kr.co.proten.manager.common.vo.UploadnDownloadServiceField;
import kr.co.proten.manager.login.model.LoginModel;
import kr.co.proten.manager.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;

/**
 * 분류통계
 * @author Proten
 *
 */
@Controller
@RequestMapping("statistics")
@RequiredArgsConstructor
public class StatisticsController {
	 
	private static final Logger log = LoggerFactory.getLogger(StatisticsController.class);
	
	private final StatisticsService statisticsService;
	
	private final DownloadDataService downloadStatisticsService;

	/**
	 * 분류통계 초기화면
	 * 
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "classifyStatistics.ps")
	public ModelAndView classifyStatistics(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		log.debug( "Get {}", request.getRequestURI() );
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", Integer.toString(login.getSiteNo()));
		CommonMAV mav = new CommonMAV(request, "classifyStatistics");
		mav.setViewName("/statistics/classifyStatistics");
		return mav;
	} 
	
	/**
	 * 통계 조회
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "classifyStatisticsAjax.ps")
	public String classifyStatisticsAjax(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		log.debug( "Get {}", request.getRequestURI() );

		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", Integer.toString(login.getSiteNo()));
        
		String startDate = paramMap.get("startDate") == null ? DateUtil.getCurrentDate() : ((String) paramMap.get("startDate")).replaceAll("-","");
		String endDate = paramMap.get("endDate") == null ? DateUtil.getCurrentDate() : ((String) paramMap.get("endDate")).replaceAll("-","");
		
		// stdType : 통계 기준 (hour | day | month | category)
		String stdType = (String) paramMap.get("stdType");
		Map<String, List<Map<String, Object>>> resultAggregationListMap = null;
		List<Map<String, Object>> resultList = new ArrayList<>();
		try {
			resultAggregationListMap = statisticsService.selectStatisticsDataList(paramMap);
			if(stdType.equals("category")) {
				List<Map<String, Object>> resultMatchedCatgegoryListMap = resultAggregationListMap.get(ElasticSearchConstant.AGGREGATION_MATCHED_CATEGORY);
				statisticsService.convertCategoryValue(resultList, resultMatchedCatgegoryListMap);
				result.addAttribute("categoryMapList", resultList);
			} else {
				resultList = statisticsService.generateTrendList(stdType, startDate, endDate);
				List<Map<String, Object>> resultTrendListMap = resultAggregationListMap.get(ElasticSearchConstant.AGGREGATION_DATE);
				statisticsService.mergeTrendListValue(resultList, resultTrendListMap);
				result.addAttribute("trendList", resultList);
			}
			Map<String, Object> resultTypeMap = new HashMap<>();
			Map<String, Object> matchedTypeMap = new HashMap<>();
			List<Map<String, Object>> resultResultTypeListMap = resultAggregationListMap.get(ElasticSearchConstant.AGGREGATION_RESULT_TYPE);
			statisticsService.convertTermsValue(resultTypeMap, resultResultTypeListMap);
			List<Map<String, Object>> resultMatchedTypeListMap = resultAggregationListMap.get(ElasticSearchConstant.AGGREGATION_MATCHED_TYPE);
			statisticsService.convertTermsValue(matchedTypeMap, resultMatchedTypeListMap);
			result.addAttribute("resultResultTypeMap", resultTypeMap);
			result.addAttribute("resultMatchedTypeMap", matchedTypeMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		return "jsonView";
	}
	
	@RequestMapping(value="/excelDownload.ps")
	public void excelDownload(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		log.debug( "Get {}", request.getRequestURI() );
		
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", Integer.toString(login.getSiteNo()));
        
		String startDate = paramMap.get("startDate") == null ? DateUtil.getCurrentDate() : ((String) paramMap.get("startDate")).replaceAll("-","");
		String endDate = paramMap.get("endDate") == null ? DateUtil.getCurrentDate() : ((String) paramMap.get("endDate")).replaceAll("-","");
		
		// stdType : 통계 기준 (hour | day | month | category)
		String stdType = (String) paramMap.get("stdType");
		Map<String, List<Map<String, Object>>> resultAggregationListMap = null;
		List<Map<String, Object>> resultList = new ArrayList<>();
		try {
			resultAggregationListMap = statisticsService.selectStatisticsDataList(paramMap);
			if(stdType.equals("category")) {
				List<Map<String, Object>> resultMatchedCatgegoryListMap = resultAggregationListMap.get(ElasticSearchConstant.AGGREGATION_MATCHED_CATEGORY);
				statisticsService.convertCategoryValue(resultList, resultMatchedCatgegoryListMap);
				
				downloadStatisticsService.writeExcelFile("category", resultList, login, response);
			} else {
				resultList = statisticsService.generateTrendList(stdType, startDate, endDate);
				List<Map<String, Object>> resultTrendListMap = resultAggregationListMap.get(ElasticSearchConstant.AGGREGATION_DATE);
				statisticsService.mergeTrendListValue(resultList, resultTrendListMap);
				
				downloadStatisticsService.writeExcelFile("trend", resultList, login, response);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
	}
}
