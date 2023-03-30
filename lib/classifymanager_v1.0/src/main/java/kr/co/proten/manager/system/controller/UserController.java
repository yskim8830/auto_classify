package kr.co.proten.manager.system.controller;

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
import kr.co.proten.manager.login.model.LoginModel;
import kr.co.proten.manager.system.service.UserService;
import lombok.RequiredArgsConstructor;

/**
 * 시스템관리 > 사용자 관리
 */
@Controller
@RequestMapping("system")
@RequiredArgsConstructor
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
	private final UserService userService;

	
	/**
	 * 사용자관리 메인
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "user.ps")
	public ModelAndView userMain(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		log.debug( "Get {}", request.getRequestURI() );
		CommonMAV mav = new CommonMAV(request, "user");
		mav.setViewName("/system/user");
		return mav;
	}

	/**
	 * 수정화면
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "userAjaxEdit.ps")
	public String userAjaxEdit(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response){

		log.debug( "Get {}", request.getRequestURI() );
		List<Map<String, Object>> userList = null;
		try {
			userList = userService.selectUserList(paramMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		result.addAttribute("userList", userList);
		return "jsonView";
	}

	/**
	 * 사용자 목록
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "userAjax.ps")
	public String userAjax(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response,HttpSession session){

		log.debug( "Get {}", request.getRequestURI() );

		int lineNo = StringUtil.parseInt((String) paramMap.get("lineNo"), 10);
		paramMap.put("lineNo", StringUtil.nvl(lineNo));
		paramMap.put("start", (StringUtil.nvl(paramMap.get("pageNo"), 1)-1 )*StringUtil.nvl(lineNo, 10 )+"");

		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);

		if ( !"y".equals(login.getAdminYn()) )  {
			paramMap.put("userNo", String.valueOf(login.getUserNo()));
		}

		List<Map<String, Object>> userList = null;
		try {
			userList = userService.selectUserList(paramMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		result.addAttribute("userList", userList);
		result.addAttribute("totalCnt", userList.size() > 0 ? userList.get(0).get("totalCount") : 0);
		result.addAttribute("pageNo", StringUtil.nvl(paramMap.get("pageNo"), "1"));

		return "jsonView";
	}
	
	/**
	 * 사용자 상세정보 저장
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "saveUser.ps")
	public String saveUser(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session){

		log.debug( "Get {}", request.getRequestURI() );

		boolean success = true;
		String userNo = StringUtil.nvl(paramMap.get("userNo"),"");
		if("".equals(userNo)){
			try {
				success = userService.insertUserInfo(paramMap, session);
			} catch (Exception e) {
				success = false;
				log.error(e.getMessage());
			}
		} else {
			LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
			String adminYn = StringUtil.nvl(login.getAdminYn(),"N").toUpperCase();

			if ( "N".equals(adminYn) && login.getUserNo() == Integer.parseInt(userNo) ) {
				try {
					success = userService.isUserPasswordCheck(paramMap);
				} catch (Exception e) {
					success = false;
					log.error(e.getMessage());
				}
				if (!success) {
					result.addAttribute("errorMsg", "입력하신 현재의 비밀번호가 일치하지 않습니다.");
				}
			} else if ( "Y".equals(adminYn) && login.getUserNo() == Integer.parseInt(userNo) ){
				try {
					success = userService.isUserPasswordCheck(paramMap);
				} catch (Exception e) {
					success = false;
					log.error(e.getMessage());
				}
				if (!success) {
					result.addAttribute("errorMsg", "입력하신 현재의 비밀번호가 일치하지 않습니다.");
				}
            }

			if (success) {
				try {
					success = userService.updateUserInfo(paramMap, session);
				} catch (Exception e) {
					success = false;
					log.error(e.getMessage());
				}    //수정
				if (!success) {
					result.addAttribute("errorMsg", "사용자 정보 수정이 실패 했습니다.");
				}
			}
		}
		result.addAttribute("success", success);
		return "jsonView";
	}

	/**
	 * 사용자  중복 체크
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return Json
	 */
	@RequestMapping(value = "dupUser.ps")
	public String dupUser(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		log.debug( "Get {}", request.getRequestURI() );

		boolean success = true;
		boolean isDup = false;
		try {
			isDup = userService.dupUserInfo(paramMap);
		} catch (Exception e) {
			isDup = false;
			log.error(e.getMessage());
		}  //id가 존재하면 true 가 return 된다.
		
		if(isDup) {
			success = false;
			result.addAttribute("msg", "동일한 사용자ID 가 존재합니다. 다른 ID로 추가하십시오. ");
		}
		
		result.addAttribute("success", success);
		return "jsonView";
	}

	/**
	 * 사용자 삭제
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "delUser.ps")
	public String delUser(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session){

		log.debug( "Get {}", request.getRequestURI() );

		boolean success = false;
		try {
			success = userService.deleteUserInfo(paramMap);
		} catch (Exception e) {
			success = false;
			log.error(e.getMessage());
		}
		result.addAttribute("success", success);
		return "jsonView";
	}
}
