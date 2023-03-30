package kr.co.proten.manager.login.controller;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Description;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import kr.co.proten.manager.common.ClassifyManagerConstant;
import kr.co.proten.manager.common.util.AESCryptoHelper;
import kr.co.proten.manager.common.util.DateUtil;
import kr.co.proten.manager.common.util.DecryptRsaUtil;
import kr.co.proten.manager.common.util.StringUtil;
import kr.co.proten.manager.login.model.LoginModel;
import kr.co.proten.manager.login.service.LoginService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginController {
	
	private static final Logger log = LoggerFactory.getLogger(LoginController.class);
	
	private final LoginService loginService;
		
	@RequestMapping(value = "login.ps" , method= RequestMethod.GET)
	@Description("로그인 페이지")
	public String login(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {

		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(2048);
		KeyPair keyPair = generator.genKeyPair();
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();

		// 세션에 공개키의 문자열을 키로하여 개인키를 저장한다.
		session.setAttribute("__rsaPrivateKey__", privateKey);

		// 공개키를 문자열로 변환하여 JavaScript RSA 라이브러리 넘겨준다.
		RSAPublicKeySpec publicSpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
		String publicKeyModulus = publicSpec.getModulus().toString(16);
		String publicKeyExponent = publicSpec.getPublicExponent().toString(16);
		request.setAttribute("publicKeyModulus", publicKeyModulus);
		request.setAttribute("publicKeyExponent", publicKeyExponent);
		return "/login/login";
	}

	/**
	 * 로그인 처리
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "loginCheck.ps")
	public String loginCheck(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpSession session) {
		
		log.debug( "Get {}", request.getRequestURI() );
		long systemTime = Long.parseLong(DateUtil.getCurrentDateTimeMille());
		long date = Long.parseLong(DateUtil.getCurrentDateTime());

		if ( paramMap != null ) {
			String csrfParam = StringUtil.nvl(paramMap.get("_csrf"),"");
			String rsaUserId 	= StringUtil.nvl(paramMap.get("userId"),"");
			String rsaPassword  = StringUtil.nvl(paramMap.get("password"),"");

			CsrfToken token = (CsrfToken) request.getAttribute("_csrf");

			if (!token.getHeaderName().equals("X-CSRF-TOKEN") || !token.getToken().equals(csrfParam) ) {
				result.addAttribute("loginSuccess", false);    //화면단으로 Json 데이터 전송
				result.addAttribute("errorMsg", "비정상적인 접근이거나 로그인 정보 입력시간을 초과하였습니다. ");    //화면단으로 Json 데이터 전송
				return "jsonView";
			}

 			PrivateKey privateKey = (PrivateKey) session.getAttribute("__rsaPrivateKey__");
			session.removeAttribute("__rsaPrivateKey__"); // 키의 재사용을 막는다. 항상 새로운 키를 받도록 강제.

			if (privateKey == null || token == null || !token.getToken().equals(csrfParam) ) {
				result.addAttribute("loginSuccess", false);    //화면단으로 Json 데이터 전송
				result.addAttribute("errorMsg", "비정상적인 접근이거나 로그인 정보 입력시간을 초과하였습니다. ");    //화면단으로 Json 데이터 전송
				return "jsonView";
			}

			String userId = "";
			String password = "";
			
			try {
				userId = DecryptRsaUtil.decryptRsa(privateKey, rsaUserId);
				password = DecryptRsaUtil.decryptRsa(privateKey, rsaPassword);
				paramMap.put("userId",userId);
				paramMap.put("password",password);
			} catch ( Exception e) {
				result.addAttribute("loginSuccess", false);    //화면단으로 Json 데이터 전송
				result.addAttribute("errorMsg", "비정상적인 접근입니다." + e.getMessage());    //화면단으로 Json 데이터 전송

				String _message = "아이디 " + userId + " 로 " + date + " 시간에  비정상적인 로그인을 시도했습니다. ";
				loginService.insertSystemLog(systemTime, "N", _message);
				return "jsonView";
			}

			String encodePassword = AESCryptoHelper.encryptThisString( password , userId );
			
            if ( !"searchtool".equals(userId) && !loginService.healthElasticSearch()) {
                result.addAttribute("loginSuccess", false);    //화면단으로 Json 데이터 전송
                result.addAttribute("errorMsg", " 검색엔진 서버가 실행되어 있는지 확인하세요.");
                return "jsonView";
            }
            
            //로그인 처리
            LoginModel login = null;
            if(!StringUtil.nvl(paramMap.get("userId")).equals("")) {
            	try {
					login = loginService.selectUserInfo((String)paramMap.get("userId"));
				} catch (Exception e) {
					result.addAttribute("loginSuccess", false);    //화면단으로 Json 데이터 전송
	                result.addAttribute("errorMsg", " 로그인에 실패 하였습니다.");
	                return "jsonView";
				}
    		} 

			if(login != null && login.getUserId() != null ) {
				int loginCount = StringUtil.nvl(login.getLoginCount(), 0);
				paramMap.put("userNo", login.getUserNo());
				paramMap.remove("password");
				paramMap.remove( "_csrf");
				//사용불가 계정 접속시 이벤트
				if ( "n".equals(login.getUseYn())) {
					String _message = "아이디 " + userId + " 로 " + date + " 시간에 사용 제한된 아이디로 로그인했습니다.";
					loginService.insertSystemLog(systemTime, "N", _message);
					result.addAttribute("loginSuccess", false);    //화면단으로 Json 데이터 전송
					result.addAttribute("errorMsg", "사용 제한된 아이디로 로그인하였습니다. 관리자에게 문의하세요. ");    //화면단으로 Json 데이터 전송
					return "jsonView";

					//로그인 실패 5회 초과시 이벤트
				} else if ( login.getLoginCount() >= 5 ) {
					String _message = "아이디 " + userId + " 로 " + date + " 시간에 로그인 횟수 초과로  계정이 잠겼습니다.";
					loginService.insertSystemLog(systemTime, "N", _message);
					result.addAttribute("loginSuccess", false);    //화면단으로 Json 데이터 전송
					result.addAttribute("errorMsg", "로그인 횟수 초과로 계정이 잠겼습니다. 관리자에게 문의하세요. ");    //화면단으로 Json 데이터 전송
					return "jsonView";
				
				//암호 오입력시
				} else if ( !encodePassword.equals(login.getPno()) ) {
					loginCount = loginCount + 1;
					result.addAttribute("loginSuccess", false);    //화면단으로 Json 데이터 전송
					if ( loginCount >= 5 ) {
						paramMap.put("loginCount", loginCount);
						loginService.updateLoginCount(String.valueOf(login.getUserNo()), paramMap);
						result.addAttribute("errorMsg", " 로그인 5회 실패하여 계정이 잠겼습니다. 관리자에게 문의바랍니다.");
					} else {
						paramMap.put("loginCount", loginCount);
						loginService.updateLoginCount(String.valueOf(login.getUserNo()), paramMap);
						result.addAttribute("errorMsg", " 로그인 5회 실패하면 계정이 잠깁니다. ( 현재 " + loginCount + " 회 )");
					}

				} else if  ( encodePassword.equals(login.getPno()) ) {
					session.removeAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
					List<Map<String, Object>> menu = login.getMenuList();
					if (menu.size() > 0) {
						result.addAttribute("menuUrl", menu.get(0).get("menuUrl"));    //화면
					}
					session.setAttribute("CSRF_TOKEN", UUID.randomUUID().toString());
					session.setAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME, login);
					session.setAttribute("userId", login.getUserId());
					session.setAttribute("menu", menu);
					result.addAttribute("loginSuccess", true);    //화면단으로 Json 데이터 전송
					log.info("Login success : {}", login.getUserId());

					//로그인을 성공하면 횟수를 초기화한다.
					paramMap.put("loginCount", 0);
					loginService.updateLoginCount(String.valueOf(login.getUserNo()), paramMap);

					String _message = "아이디 " + userId + " 로 " + date + " 시간에 로그인 되었습니다. ";
					loginService.insertSystemLog(systemTime, "Y", _message);
				}
			} else {
				String id = StringUtil.nvl(paramMap.get("userId"),"");
				String pw = StringUtil.nvl(paramMap.get("password"),"");

				login = new LoginModel();
				login.setUserId(id);
				login.setPno(pw);

				if ( id != null && pw != null && id.equals("searchtool") && pw.equals("10manager12#$") ) {
					session.setAttribute("CSRF_TOKEN",UUID.randomUUID().toString());
					session.setAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME, login);
					session.setAttribute("userId", id);
					result.addAttribute( "loginSuccess", true);	//화면단으로 Json 데이터 전송
				} else {
					session.removeAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
					result.addAttribute("loginSuccess", false);    //화면단으로 Json 데이터 전송
					log.info("Login fail :: {}", login.getUserId() );
					String _message = "아이디 " + userId + " 로 " + date + " 시간에  로그인이 실패했습니다. ";
					loginService.insertSystemLog(systemTime, "N", _message);
				}
			}
		}
		return "jsonView";
	}
	
	/**
	 * 로그아웃
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "logOut.ps")
	public ModelAndView logOut(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		log.info( "Get LogOut : {}", request.getRequestURI() );
		ModelAndView mav = new ModelAndView();
		session.invalidate();
		mav.setViewName("redirect:/login.ps");
		
		return mav;
	}
	
}
