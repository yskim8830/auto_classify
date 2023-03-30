<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" 		uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="CTX_PATH" value="${pageContext.request.contextPath}" scope="request"/>
<%
    response.sendRedirect( request.getContextPath() + "/login.ps");
%>