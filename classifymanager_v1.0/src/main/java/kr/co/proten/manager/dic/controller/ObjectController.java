package kr.co.proten.manager.dic.controller;

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
import kr.co.proten.manager.common.vo.ReferencedResultVo;
import kr.co.proten.manager.dic.service.ObjectService;
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
public class ObjectController {
	 
	private static final Logger log = LoggerFactory.getLogger(ObjectController.class);
	
	private final ObjectService objectDicService;

	/**
	 * 개체사전 초기화면
	 * 
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "objectDic.ps")
	public ModelAndView objectDic(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		log.debug( "Get {}", request.getRequestURI() );
		CommonMAV mav = new CommonMAV(request, "objectDic");
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", Integer.toString(login.getSiteNo()));
		mav.setViewName("/dic/objectDic");
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
	@RequestMapping(value = "objDicAjax.ps")
	public String objDicAjax(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		log.debug( "Get {}", request.getRequestURI() );
		
		int lineNo = StringUtil.parseInt((String) paramMap.get("lineNo"), 10);
		paramMap.put("lineNo", StringUtil.nvl(lineNo));
		paramMap.put("start", (StringUtil.nvl(paramMap.get("pageNo"), 1)-1 )*StringUtil.nvl(lineNo, 10 )+"");
		
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", login.getSiteNo());
		
		List<Map<String, Object>> list = null; 
		try {
			list = objectDicService.selectObjDicList(paramMap);
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
	@RequestMapping(value = "objDicAjaxEdit.ps")
	public String dicAjaxEdit(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request) {
		log.debug( "Get {}", request.getRequestURI() );
		List<Map<String, Object>> list = null; 
		try {
			list = objectDicService.selectObjDicList(paramMap);
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
	@RequestMapping(value = "saveObjDic.ps")
	public String saveObjDic(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		log.debug( "Get {}", request.getRequestURI() );
		boolean success = true;		
		String add = StringUtil.nvl(paramMap.get("add"));
		paramMap.remove("add");
		if("".equals(add)){			
			try {
				success = objectDicService.insertObjDicInfo(paramMap, session);
			} catch (Exception e) {
				success = false;
			}
			if(!success) {
				result.addAttribute("msg", "엔티티명이 중복되었습니다.");
			}
		} else {
			try {
				success = objectDicService.updateObjDicInfo(paramMap, session);
			} catch (Exception e) {
				success = false;
			}
			if(!success) {
				result.addAttribute("msg", "엔티티명이 중복되었습니다.");
			}
		}
		result.addAttribute("success", success);

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
	@RequestMapping(value = "dupObjDic.ps")
	public String dupObjDic(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		log.debug( "Get {}", request.getRequestURI());
		boolean success = true;
		boolean dup = false; 
		
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", login.getSiteNo());
		paramMap.put("wordUnq", StringUtil.removeTrimChar((String)paramMap.get("word")));
		try {
			dup = objectDicService.dupObjDicInfo(paramMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		if(dup) {
			success = false;
			result.addAttribute("msg", "엔티티명이 중복되었습니다. \n(단어의 띄어쓰기 및 특수기호가 없어야 됩니다.)");
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
	@RequestMapping(value = "delObjDic.ps")
	public String delObjDic(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		boolean success = true;
		log.debug( "Get {}", request.getRequestURI() );
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		paramMap.put("siteNo", login.getSiteNo());
		ReferencedResultVo resultVo = null; 
		try {
			resultVo = objectDicService.deleteObjDicInfo(paramMap);
		} catch (Exception e) {
			success = false;
			log.error(e.getMessage());
		}
		result.addAttribute("success", success);
		result.addAttribute("result", resultVo);
		return "jsonView";
	}
}
