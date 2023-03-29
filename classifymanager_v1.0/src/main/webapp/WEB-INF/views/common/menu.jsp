<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <c:if test="${sessionScope.ID != 'SKIP'}">
        <aside class="left-sidebar" data-sidebarbg="skin6">
            <!-- Sidebar scroll-->
            <div class="scroll-sidebar">
                <!-- Sidebar navigation-->
                <nav class="sidebar-nav">
                    <ul id="sidebarnav">
                        <c:forEach var="menuList" items="${loginInfo.menuList}" varStatus="status">
                            <c:if test="${menuList.depth eq 1}">
                                <c:if test="${status.count ne 1}">
                                	</ul>
                                </li>
                                </c:if>
                                <c:set var="menuIcon" value="mdi mdi-receipt" />
                                <c:choose>
                                	<c:when test="${not empty menuList.emoji}">
                                		 <c:set var="menuIcon" value="mdi ${menuList.emoji}" />
                                	</c:when>
                                	<c:otherwise>
                                		<c:set var="menuIcon" value="mdi mdi-library-books" />
                                	</c:otherwise>
                                </c:choose>
                                <li class="sidebar-item pt-2"> <a class="sidebar-link waves-effect waves-dark sidebar-link" href="javascript:void(0)" aria-expanded="false"><i class="${menuIcon}"></i><span class="hide-menu">${menuList.menuNm} </span></a>
                                    <ul aria-expanded="false" class="collapse  first-level">
                            </c:if>
	                        <c:if test="${menuList.depth eq 2}">
	                            <c:set var="menuSubIcon" value="mdi mdi-note-outline" />
	                            <c:choose>
                                	<c:when test="${not empty menuList.emoji}">
                                		 <c:set var="menuSubIcon" value="mdi ${menuList.emoji}" />
                                	</c:when>
                                	<c:otherwise>
                                		<c:set var="menuSubIcon" value="mdi mdi-library-books" />
                                	</c:otherwise>
                                </c:choose>
	                            <li class="sidebar-item"><a href="${CTX_PATH}${menuList.menuUrl}" class="sidebar-link"><i class="${menuSubIcon}"></i><span class="hide-menu"> ${menuList.menuNm} </span></a></li>
	                        </c:if>
	                    </c:forEach>
                        <c:if test="${fn:length(loginInfo.menuList) ne 0}">
                               </ul>
                          </li>
                        </c:if>
                    </ul>
                </nav>
                <!-- End Sidebar navigation -->
            </div>
            <!-- End Sidebar scroll-->
        </aside>
    </c:if>

    <c:if test="${sessionScope.ID != 'SKIP'}">
		<div class="page-wrapper">
    </c:if>
    <c:if test="${sessionScope.ID == 'SKIP'}">
		<div class="page-none">
    </c:if>