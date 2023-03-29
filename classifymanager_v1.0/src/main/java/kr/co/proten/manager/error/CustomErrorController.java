package kr.co.proten.manager.error;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    private static final String PATH = "/error"; // configure 에서 Redirect 될 path

    @RequestMapping(value = "/error")
    public String error(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (String.valueOf(status).equalsIgnoreCase(HttpStatus.NOT_FOUND.toString())) {
            return "/common/error/404"; // /WEB-INF/errors/404.jsp
        }
        if (String.valueOf(status).equalsIgnoreCase("400")) {
            return "/common/error/400"; // /WEB-INF/errors/404.jsp
        }
        if (String.valueOf(status).equalsIgnoreCase("500")) {
            return "/common/error/500"; // /WEB-INF/errors/404.jsp
        }
        return "/common/error/404";
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}