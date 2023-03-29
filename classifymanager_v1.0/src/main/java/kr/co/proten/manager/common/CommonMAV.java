package kr.co.proten.manager.common;

import kr.co.proten.manager.login.model.LoginModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class CommonMAV extends ModelAndView
{	
	private static final Logger log = LoggerFactory.getLogger(CommonMAV.class);

	public CommonMAV(){
		super();
	}
	
	/**
	 * 공통 정보를 JSP 화면으로 보낸다
	 * @param request
	 * @param menu	대메뉴 구분
	 */
	public CommonMAV(HttpServletRequest request, String menu){
		super();
		HttpSession session = request.getSession(false);
		Object session1 = session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);

		if(session != null && session1 != null  ) {
			LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
			login.setMenuInfo(menu);
			this.addObject("loginInfo", login);
		}

		this.addObject("menuInfo", menu);

	}
	
}
