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

import com.google.gson.JsonObject;

import kr.co.proten.manager.common.ClassifyManagerConstant;
import kr.co.proten.manager.common.CommonMAV;
import kr.co.proten.manager.common.util.DateUtil;
import kr.co.proten.manager.common.util.StringUtil;
import kr.co.proten.manager.learning.service.ModelingService;
import kr.co.proten.manager.login.model.LoginModel;
import lombok.RequiredArgsConstructor;

/**
 * 학습관리
 */
@Controller
@RequestMapping("learning")
@RequiredArgsConstructor
public class ModelingController {

	private static final Logger log = LoggerFactory.getLogger(ModelingController.class);
	
	private final ModelingService modelingService; 
	
	/**
	 * 학습&모델 메인 
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "modeling.ps")
	public ModelAndView modeling(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		log.debug( "Get {}", request.getRequestURI() );
		CommonMAV mav = new CommonMAV(request, "modeling");
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		String siteNm = "";
		for(Map<String,Object> site : login.getSiteList()) {
			if((int)site.get("siteNo") == login.getSiteNo()) {
				siteNm = (String) site.get("siteNm");
			}
		}
		result.addAttribute("siteNm", siteNm);		
		mav.setViewName("/learning/modeling");
		return mav;
	}
	
	/**
	 * 모델대상 목록
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "modelingAjax.ps")
	public String modelingAjax(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session){
		
		log.debug( "Get {}", request.getRequestURI() );
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", login.getSiteNo());
		List<Map<String, Object>> list = null;
		
		try {
			list = modelingService.modelingList(paramMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		result.addAttribute("modelingList", list);
		return "jsonView";
	}
	
	/**
	 * 학습이력 목록
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "learningAjax.ps")
	public String learningAjax(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session){
		
		log.debug( "Get {}", request.getRequestURI() );
		
		int lineNo = StringUtil.parseInt((String) paramMap.get("lineNo"), 10);
		paramMap.put("lineNo", StringUtil.nvl(lineNo));
		paramMap.put("start", (StringUtil.nvl(paramMap.get("pageNo"), 1)-1 )*StringUtil.nvl(lineNo, 10 )+"");
		
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", login.getSiteNo());
		
		List<Map<String, Object>> list = null;
		try {
			list = modelingService.learningList(paramMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		result.addAttribute("learningList", list);
		result.addAttribute("totalCnt", list.size() > 0 ? list.get(0).get("totalCount") : 0);
		result.addAttribute("pageNo", StringUtil.nvl(paramMap.get("pageNo"), "1"));

		return "jsonView";
	}
	
	/**
	 * 학습시작요청
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "startTrainingAjax.ps")
	public String startTrainingAjax(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session){
		
		log.debug( "Get {}", request.getRequestURI() );
		
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		String siteNo = String.valueOf(login.getSiteNo());
				
		String apiResult = "";
		try {
			apiResult = modelingService.requestStartTraining(siteNo);
			result.addAttribute("success", true);
		} catch (Exception e) {
			result.addAttribute("success", false);
			result.addAttribute("msg", "API 요청이 정상적으로 이루어지지 않았습니다. 학습서버의 상태를 확인해 주세요.");
			log.error(e.getMessage());
		}
		
		result.addAttribute("resultJson", apiResult);
		
		return "jsonView";
	}
	
	/**
	 * 학습상태요청
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "statusTrainingAjax.ps")
	public String statusTrainingAjax(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session){
		
		log.debug( "Get {}", request.getRequestURI() );
		
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		String siteNo = String.valueOf(login.getSiteNo());
		
		String apiResult = "";
		try {
			apiResult = modelingService.requestStatusTraining(siteNo);
			result.addAttribute("success", true);
		} catch (Exception e) {
			result.addAttribute("success", false);
			result.addAttribute("msg", "API 요청이 정상적으로 이루어지지 않았습니다. 학습서버의 상태를 확인해 주세요.");
			log.error(e.getMessage());
		}
		
		result.addAttribute("resultJson", apiResult);
		
		return "jsonView";
	}
	
	/**
	 * 학습중지요청
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "stopTrainingAjax.ps")
	public String stopTrainingAjax(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session){
		
		log.debug( "Get {}", request.getRequestURI() );
		
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		String siteNo = String.valueOf(login.getSiteNo());
		
		String apiResult = "";
		try {
			apiResult = modelingService.requestStopTraining(siteNo);
			result.addAttribute("success", true);
		} catch (Exception e) {
			result.addAttribute("success", false);
			result.addAttribute("msg", "API 요청이 정상적으로 이루어지지 않았습니다. 학습서버의 상태를 확인해 주세요.");
			log.error(e.getMessage());
		}
		
		result.addAttribute("resultJson", apiResult);
		
		return "jsonView";
	}
	
	
	/**
	 * 서비스배포요청
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "distModelToServiceAjax.ps")
	public String distModelToServiceAjax(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session){
		
		log.debug( "Get {}", request.getRequestURI() );
		
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		String siteNo = String.valueOf(login.getSiteNo());
		String version = String.valueOf(paramMap.get("version"));
		
		String apiResult = "";
		try {
			apiResult = modelingService.requestDistModelToService(siteNo, version);
			result.addAttribute("success", true);
		} catch (Exception e) {
			result.addAttribute("success", false);
			result.addAttribute("msg", "API 요청이 정상적으로 이루어지지 않았습니다. 학습서버의 상태를 확인해 주세요.");
			log.error(e.getMessage());
		}
		
		result.addAttribute("resultJson", apiResult);
		
		return "jsonView";
	}
}
