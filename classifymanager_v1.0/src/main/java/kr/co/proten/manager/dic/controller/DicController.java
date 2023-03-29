package kr.co.proten.manager.dic.controller;

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
import kr.co.proten.manager.common.ClassifyManagerConstant;
import kr.co.proten.manager.common.util.StringUtil;
import kr.co.proten.manager.dic.service.DicService;
import kr.co.proten.manager.login.model.LoginModel;
import lombok.RequiredArgsConstructor;

/**
 * 사전  관리
 * @author Proten
 *
 */
@Controller
@RequestMapping("dic")
@RequiredArgsConstructor
public class DicController {
	 
	private static final Logger log = LoggerFactory.getLogger(DicController.class);
	
	private final DicService dicService;

	/**
	 * 사용자사전 초기화면
	 * 
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "userDic.ps")
	public ModelAndView dic(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		log.debug( "Get {}", request.getRequestURI() );
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", Integer.toString(login.getSiteNo()));
		CommonMAV mav = new CommonMAV(request, "userDic");
		mav.setViewName("/dic/userDic");
		return mav;
	} 
	
	/**
	 * 사전 목록
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "dicAjax.ps")
	public String dicAjax(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response,HttpSession session) {

		log.debug( "Get {}", request.getRequestURI() );
		
		int lineNo = StringUtil.parseInt((String) paramMap.get("lineNo"), 10);
		paramMap.put("lineNo", StringUtil.nvl(lineNo));
		paramMap.put("start", (StringUtil.nvl(paramMap.get("pageNo"), 1)-1 )*StringUtil.nvl(lineNo, 10 )+"");

		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", Integer.toString(login.getSiteNo()));
		
		List<Map<String, Object>> list = null;
		try {
			list = dicService.selectDicList(paramMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		result.addAttribute("dicList", list);
		result.addAttribute("totalCnt", list.size() > 0 ? list.get(0).get("totalCount") : 0);
		result.addAttribute("pageNo", StringUtil.nvl(paramMap.get("pageNo"), "1"));

		return "jsonView";
	}

	/**
	 * 사전 목록
	 * @param result
	 * @param paramMap
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "dicAjaxEdit.ps")
	public String dicAjaxEdit(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request) {
		log.debug( "Get {}", request.getRequestURI() );
		List<Map<String, Object>> list = null;
		try {
			list = dicService.selectDicList(paramMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		result.addAttribute("dicEdit", list);
		return "jsonView";
	}

	/**
	 * 사전 정보 저장
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return Json
	 */
	@RequestMapping(value = "saveDic.ps")
	public String saveDic(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		Map<String,Object> ret = new HashMap<String,Object>();
		log.debug( "Get {}", request.getRequestURI() );
		String add = StringUtil.nvl(paramMap.get("add"));
		paramMap.remove("add");
		try {
			if("".equals(add)){
				ret = dicService.insertDicInfo(paramMap, session);
			} else {
				ret = dicService.updateDicInfo(paramMap, session); //수정
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		result.addAttribute("result", ret);

		return "jsonView";
	}
	/**
	 * 사전 중복 체크
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return Json
	 */
	@RequestMapping(value = "dupDic.ps")
	public String dupDic(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		log.debug( "Get {}", request.getRequestURI() );
		boolean success = true;	
		boolean dup = false;	
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", login.getSiteNo());
		paramMap.put("wordUnq", StringUtil.removeTrimChar((String)paramMap.get("word")));
		
		try {
			dup = dicService.dupDicInfo(paramMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		if(dup) {
			success = false;
			result.addAttribute("msg", "단어가 중복되었습니다. \n(단어의 띄어쓰기 및 특수기호가 없어야 됩니다.)");
		} 
		result.addAttribute("success", success);
 
		return "jsonView";
	}
	
	/**
	 * 사전 정보 삭제
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return Json
	 */
	@RequestMapping(value = "delDic.ps")
	public String delDic(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response) {
		boolean success = true;
		log.debug( "Get {}", request.getRequestURI() );
		try {
			success = dicService.deleteDicInfo(paramMap);
		} catch (Exception e) {
			success = false;
			result.addAttribute("msg", "사전 삭제를 실패하였습니다.");
		}
		result.addAttribute("success", success);
		return "jsonView";
	}
		
	/**
	 * 배포
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return Json
	 */
	@RequestMapping(value = "distDic.ps")
	public String distDic(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response) {

		log.debug( "Get {}", request.getRequestURI() );
		
		String apiResult = "";
		try {
			apiResult = dicService.requestDistDic();
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
