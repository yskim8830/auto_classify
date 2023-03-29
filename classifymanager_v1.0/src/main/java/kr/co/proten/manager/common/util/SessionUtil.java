package kr.co.proten.manager.common.util;

import java.util.HashMap;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * session Util
 * - Spring에서 제공하는 RequestContextHolder 를 이용하여
 * request 객체를 service까지 전달하지 않고 사용할 수 있게 해줌
 *
 */
public class SessionUtil {
    /**
     * attribute 값을 가져 오기 위한 method
     *
     * @param name  attribute key name
     * @return Object attribute obj
     */
    public static Object getAttribute(String name) throws Exception {
        return (Object)RequestContextHolder.getRequestAttributes().getAttribute(name, RequestAttributes.SCOPE_SESSION);
    }

    /**
     * attribute 설정 method
     *
     * @param String  attribute key name
     * @param Object  attribute obj
     * @return void
     */
    public static void setAttribute(String name, Object object) throws Exception {
        RequestContextHolder.getRequestAttributes().setAttribute(name, object, RequestAttributes.SCOPE_SESSION);
    }

    /**
     * 설정한 attribute 삭제
     *
     * @param String  attribute key name
     * @return void
     */
    public static void removeAttribute(String name) throws Exception {
        RequestContextHolder.getRequestAttributes().removeAttribute(name, RequestAttributes.SCOPE_SESSION);
    }

    /**
     * session id
     *
     * @param void
     * @return String SessionId 값
     */
    public static String getSessionId() throws Exception  {
        return RequestContextHolder.getRequestAttributes().getSessionId();
    }
    
    /**
     * USER ID
     *
     * @param void
     * @return String SessionId 값
     */
	@SuppressWarnings("unchecked")
    public static String getUserId() throws Exception  {
    	
		HashMap<String, String> user = (HashMap<String, String>) getAttribute("USER"); 
        //return user.get("userId"); 
        return "TEST"; 
    }
    
    
}