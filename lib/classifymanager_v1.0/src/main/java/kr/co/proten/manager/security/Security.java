package kr.co.proten.manager.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import kr.co.proten.manager.login.model.LoginModel;

@Component
public class Security {

	public LoginModel getLoginUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return null;
		}
		if (!(authentication.getPrincipal() instanceof LoginModel)) {
			return null;
		}
		return (LoginModel) authentication.getPrincipal();
	}

	public Integer getLoginUserKey() {
		LoginModel dto = getLoginUser();
		if (dto == null) {
			return null;
		}
		return dto.getUserNo();
	}

}