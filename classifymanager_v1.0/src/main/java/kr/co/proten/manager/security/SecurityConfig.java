package kr.co.proten.manager.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/service/**");  //로그인 세션 제외
		web.httpFirewall(allowHttpFirewall());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http
			.logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logOut.ps"))
			.logoutSuccessUrl("/login.ps")
			.invalidateHttpSession(true)
			.deleteCookies("JSESSIONID")
			.and()
		//	.addFilterAfter(new CSRFTokenGeneratorFilter(), CsrfFilter.class)
			.authorizeRequests()
			.antMatchers("/login**").permitAll()
			.antMatchers("/stomp/**").permitAll()
			.antMatchers("/common/**").permitAll()
			.antMatchers("/static/**").permitAll()
			.antMatchers("/main/**").permitAll()
			.antMatchers("/classify/**").permitAll()
			.antMatchers("/dic/**").permitAll()
			.antMatchers("/learning/**").permitAll()
			.antMatchers("/statistics/**").permitAll()
			.antMatchers("/system/**").permitAll()
			.antMatchers("/logOut.ps").permitAll()
			.anyRequest().authenticated()
			.and()

			.formLogin().loginPage("/login.ps").permitAll()
			.usernameParameter("userId")
			.passwordParameter("password");
		http
			.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint());

		http
			.headers()
			.defaultsDisabled()
			.cacheControl()
			.and()
			.xssProtection();
	}

	@Bean
	AuthenticationEntryPoint authenticationEntryPoint() {
		return new SecurityAuthEntryPoint("/login.ps");
	}


	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}

	@Bean
	public HttpFirewall allowHttpFirewall() {
		DefaultHttpFirewall firewall = new DefaultHttpFirewall();
		firewall.setAllowUrlEncodedSlash(true);
		return firewall;
	}




}