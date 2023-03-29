package kr.co.proten.manager.common;

import kr.co.proten.manager.common.util.SessionUtil;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor extends HandlerInterceptorAdapter {


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		// ----------------------------------------------------------------------------------------------------
		// TODO : 세션 BYPASS 처리하였으므로 추후   관리도구와 SSO 인증처리하여야 함
		// ----------------------------------------------------------------------------------------------------

		String reqUrl = request.getRequestURL().toString();
//		String session = (String) SessionUtil.getAttribute(kr.co.proten.manager.common.util.Const.LOGIN_SESSEION_NAME);
//
//		if ( reqUrl.indexOf("login") != -1 || reqUrl.indexOf("/service/")>-1 || reqUrl.indexOf("/log/" ) != -1  ) {
//			return true;
//		}
//
//		if ( session == null || "".equals(session))  {
//			response.sendRedirect("/login.ps");
//			return false;
//		}
//
		
		if (reqUrl.endsWith(".ps")) {
			if (reqUrl.indexOf("login") != -1 || reqUrl.indexOf("sample") != -1 || reqUrl.indexOf("/service/") != -1 || reqUrl.indexOf("/log/")  != -1 ) {
                return true;
			} else {

				Object session = SessionUtil.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
				Object csrf = SessionUtil.getAttribute("CSRF_TOKEN");

				if( session == null ||  csrf == null || session.equals("") || csrf.equals("")) {
				if( session == null || session.equals("") )
					response.sendRedirect(request.getContextPath() + "/login.ps");
					return false;
				}

			}
		} else {
			return true;
		}

		return true;
	}
}
