<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" 		uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" 		uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" 	uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="CTX_PATH" value="${pageContext.request.contextPath}" scope="request"/>
<!DOCTYPE html>
<html dir="ltr" lang="ko">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <!-- Tell the browser to be responsive to screen width -->
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="Probot">
    <meta name="author" content="ProTen">
    <meta id="_csrf" name="_csrf" th:content="${_csrf.token}"/>
	<!-- default header name is X-CSRF-TOKEN -->
	<meta id="_csrf_header" name="_csrf_header" th:content="${_csrf.headerName}"/>
    <c:set var="csrf" value="${_csrf.token}"/>
    <title>ProChat-Manager</title>
    <!-- Favicon icon -->
    <!-- 
    <link rel="icon" type="image/png" sizes="16x16" href="${CTX_PATH}/static/images/favicon.png">
    -->
    <link rel="icon" type="image/png" sizes="16x16" href="${CTX_PATH}/static/images/favicon_chatbot.png">
    
    <!-- Custom CSS -->
    <link href="${CTX_PATH}/static/package/assets/libs/fullcalendar/dist/fullcalendar.min.css" rel="stylesheet" />
    <link href="${CTX_PATH}/static/package/assets/extra-libs/calendar/calendar.css" rel="stylesheet" />
    <!-- needed css -->
    <link href="${CTX_PATH}/static/package/dist/css/style.min.css" rel="stylesheet" />
	
	<!-- Datepicker CSS -->
    <link href="${CTX_PATH}/static/package/assets/libs/daterangepicker/daterangepicker.css" rel="stylesheet" type="text/css" />



    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    
    <!-- -------------------------------------------------------------- -->
    <!-- All Jquery -->
    <!-- -------------------------------------------------------------- -->
    <script src="${CTX_PATH}/static/package/assets/libs/jquery/dist/jquery.min.js"></script>
    <script src="${CTX_PATH}/static/package/assets/extra-libs/taskboard/js/jquery-ui.min.js"></script>

    <!-- Datepicker JS -->
    <script src="${CTX_PATH}/static/package/assets/libs/moment/moment_kr.js"></script>
    <script src="${CTX_PATH}/static/package/assets/libs/daterangepicker/daterangepicker.js"></script>

	<!-- jQuery Template-->
	<script type="text/javascript" src="${CTX_PATH}/static/js/jquery.tmpl.js"></script>
</head>
<body>
    <!-- ============================================================== -->
    <!-- Preloader - style you can find in spinners.css -->
    <!-- ============================================================== -->
    <div class="preloader">
		<svg class="tea lds-ripple" width="37" height="48" viewbox="0 0 37 48" fill="none" xmlns="http://www.w3.org/2000/svg">
		    <path d="M27.0819 17H3.02508C1.91076 17 1.01376 17.9059 1.0485 19.0197C1.15761 22.5177 1.49703 29.7374 2.5 34C4.07125 40.6778 7.18553 44.8868 8.44856 46.3845C8.79051 46.79 9.29799 47 9.82843 47H20.0218C20.639 47 21.2193 46.7159 21.5659 46.2052C22.6765 44.5687 25.2312 40.4282 27.5 34C28.9757 29.8188 29.084 22.4043 29.0441 18.9156C29.0319 17.8436 28.1539 17 27.0819 17Z" stroke="#20222a" stroke-width="2"></path>
		    <path d="M29 23.5C29 23.5 34.5 20.5 35.5 25.4999C36.0986 28.4926 34.2033 31.5383 32 32.8713C29.4555 34.4108 28 34 28 34" stroke="#20222a" stroke-width="2"></path>
		    <path id="teabag" fill="#20222a" fill-rule="evenodd" clip-rule="evenodd" d="M16 25V17H14V25H12C10.3431 25 9 26.3431 9 28V34C9 35.6569 10.3431 37 12 37H18C19.6569 37 21 35.6569 21 34V28C21 26.3431 19.6569 25 18 25H16ZM11 28C11 27.4477 11.4477 27 12 27H18C18.5523 27 19 27.4477 19 28V34C19 34.5523 18.5523 35 18 35H12C11.4477 35 11 34.5523 11 34V28Z"></path>
		    <path id="steamL" d="M17 1C17 1 17 4.5 14 6.5C11 8.5 11 12 11 12" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" stroke="#20222a"></path>
		    <path id="steamR" d="M21 6C21 6 21 8.22727 19 9.5C17 10.7727 17 13 17 13" stroke="#20222a" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"></path>
		</svg>
    </div>

    <!-- ============================================================== -->
    <!-- Main wrapper - style you can find in pages.scss -->
    <!-- ============================================================== -->
    <c:if test="${sessionScope.ID != 'SKIP'}">
    <div id="main-wrapper">
        <!-- ============================================================== -->
        <!-- Topbar header - style you can find in pages.scss -->
        <!-- ============================================================== -->
        <header class="topbar">
            <nav class="navbar top-navbar navbar-expand-md navbar-dark">
                <div class="navbar-header border-end">
                    <!-- This is for the sidebar toggle which is visible on mobile only -->
                    <a class="nav-toggler waves-effect waves-light d-block d-md-none" href="javascript:void(0)"><i class="ti-menu ti-close"></i></a>
                    <!-- ============================================================== -->
                    <!-- Logo -->
                    <!-- ============================================================== -->
                    <a class="navbar-brand" href="${CTX_PATH}/main/main.ps">
                        <!-- Logo icon -->
                        <b class="logo-icon">
                            <!--You can put here icon as well // <i class="wi wi-sunset"></i> //-->
				            <!-- Dark Logo icon -->
				            <img src="${CTX_PATH}/static/images/proclassify-logo-icon.png" width="40" height="40" alt="homepage" class="dark-logo" />
				            <!-- Light Logo icon -->
				            <img src="${CTX_PATH}/static/images/proclassify-logo-icon.png" width="40" height="40" alt="homepage" class="light-logo" />
                        </b>
                        <!--End Logo icon -->
                        <!-- Logo text -->
                        <span class="logo-text">
                            <!-- dark Logo text -->
				            <img src="${CTX_PATH}/static/images/logo-light-text.png" width="175" height="40" alt="homepage" class="dark-logo" />
				            <!-- Light Logo text -->
				            <img src="${CTX_PATH}/static/images/logo-light-text.png" width="175" height="40" class="light-logo" alt="homepage" />
                        </span>
                    </a>
                    <!-- ============================================================== -->
                    <!-- End Logo -->
                    <!-- ============================================================== -->
                    
                    <!-- ============================================================== -->
                    <!-- Toggle which is visible on mobile only -->
                    <!-- ============================================================== -->
                   <a class="topbartoggler d-block d-md-none waves-effect waves-light" href="javascript:void(0)" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation"><i class="ti-more"></i></a>
                </div>
                <!-- ============================================================== -->
                <!-- End Logo -->
                <!-- ============================================================== -->
                <div class="navbar-collapse collapse" id="navbarSupportedContent">
                    <!-- ============================================================== -->
                    <!-- toggle and nav items -->
                    <!-- ============================================================== -->
                    <ul class="navbar-nav me-auto">
                		<li class="nav-item d-none d-md-block">
							<a class="nav-link sidebartoggler waves-effect waves-light" href="javascript:void(0)" data-sidebartype="mini-sidebar"><i class="mdi mdi-menu fs-5"></i></a>
						</li>
						<!-- ============================================================== -->
						<!-- Comment -->
						<!-- ============================================================== -->
						<li class="nav-item dropdown">
							<a class="nav-link dropdown-toggle waves-effect waves-dark" href="#" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
							    <span class="d-none d-md-block">사이트 <i class="icon-options-vertical"></i></span>
							    <span class="d-block d-md-none"><i class="mdi mdi-dialpad font-24"></i></span>
							</a>
							<div class="dropdown-menu dropdown-menu-start dropdown-menu-animate-up">
							    <ul class="list-style-none">
							        <li>
							            <div class="border-bottom rounded-top py-3 px-4">
							                <div class="mb-0 font-weight-medium fs-4">
							                    사이트 목록
							                </div>
							            </div>
							        </li>
							        <li>
							            <div class="message-center notifications position-relative" id="mySite">
							            <c:forEach var="name" items="${loginInfo.siteList}" varStatus="status">
							            	<c:choose>
							            	<c:when test="${name.siteNo == loginInfo.siteNo}">
											    <a href="javascript:void(0)" class="message-item d-flex align-items-center border-bottom px-3 py-2" id="siteListRow<c:out value="${name.siteNo}"/>">
													<div class="w-300 d-inline-block v-middle ps-3">
														<h5 class="message-title mb-0 mt-1 fs-3 fw-bold">
															<i class="mdi mdi-check-circle fs-5"></i>
															<c:out value="${name.siteNm}"/>(<c:out value="${name.site}"/>)
														</h5>
													</div>
												</a>
											</c:when>
											<c:otherwise>
												<a href="javascript:chageSession('<c:out value="${name.siteNo}"/>','<c:out value="${name.siteNm}"/>');" class="message-item d-flex align-items-center border-bottom px-3 py-2" id="siteListRow<c:out value="${name.siteNo}"/>">
												<div class="w-300 d-inline-block v-middle ps-3">
													<h5 class="message-title mb-0 mt-1 fs-3 fw-bold">
														<c:if test="${name.siteNo == loginInfo.siteNo}"><i class="mdi mdi-check-circle fs-5"></i></c:if>
														<c:out value="${name.siteNm}"/>(<c:out value="${name.site}"/>)
													</h5>
												</div>
											</a>
											</c:otherwise>
											</c:choose>
										</c:forEach>
							            </div>
							        </li>
							    </ul>
							</div>
						</li>
						<!-- ============================================================== -->
						<!-- End Comment -->
						<!-- ============================================================== -->
                    </ul>
                    <!-- ============================================================== -->
                    <!-- Right side toggle and nav items -->
                    <!-- ============================================================== -->
                    <ul class="navbar-nav">
                        <!-- ============================================================== -->
                        <!-- User profile and search -->
                        <!-- ============================================================== -->
                        <li class="nav-item dropdown">
						    <a class="nav-link dropdown-toggle waves-effect waves-dark" href="#" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
						        <img src="${CTX_PATH}/static/package/assets/images/users/1.jpg" alt="user" class="rounded-circle" width="36" />
						        <span class="ms-2 font-weight-medium"><c:out value="${loginInfo.userId}"/></span><span class="fas fa-angle-down ms-2"></span>
						    </a>
						    <div class="dropdown-menu dropdown-menu-end user-dd animated flipInY">
						        <div class="d-flex no-block align-items-center p-3 bg-info text-white mb-2">
						            <div class="">
						                <img src="${CTX_PATH}/static/package/assets/images/users/1.jpg" alt="user" class="rounded-circle" width="60" />
						            </div>
						            <div class="ms-2">
						                <h4 class="mb-0 text-white"><c:out value="${loginInfo.userNm}"/></h4>
						                <p class="mb-0"><c:out value="${loginInfo.userId}"/></p>
						            </div>
						        </div>
						        <a class="dropdown-item" href="${CTX_PATH}/system/user.ps"><i data-feather="user" class="feather-sm text-info me-1 ms-1"></i>
						            내 정보 관리</a>
						        <a class="dropdown-item" href="${CTX_PATH}/system/site.ps"><i data-feather="credit-card" class="feather-sm text-info me-1 ms-1"></i>
						            사이트 관리</a>
						        <div class="dropdown-divider"></div>
						        <a class="dropdown-item" href="#" data-bs-toggle="modal" data-bs-target="#versionInfoModal" ><i data-feather="settings" class="feather-sm text-warning me-1 ms-1"></i>
						            버전 정보</a>
						        <div class="dropdown-divider"></div>
						        <a class="dropdown-item" href="${CTX_PATH}/logOut.ps"><i data-feather="log-out" class="feather-sm text-danger me-1 ms-1"></i>
						            로그아웃</a>
						    </div>
						</li>
                        <!-- ============================================================== -->
                        <!-- User profile and search -->
                        <!-- ============================================================== -->
                    </ul>
                </div>
            </nav>
        </header>
          <!-- ============================================================== -->
          <!-- End Topbar header -->
          <!-- ============================================================== -->
<!-- 버전정보 모달 -->
<div class="modal fade" id="versionInfoModal" tabindex="-1" data-bs-backdrop="static" role="dialog" aria-labelledby="versionInfoModal">
     <div class="modal-dialog" role="document">
         <div class="modal-content">
             <div class="modal-header d-flex align-items-center">
                 <h5 class="modal-title">챗봇 관리도구</h5>
                 <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
             </div>
             <div class="modal-body">
	             <div class="mb-3">
	             	<label for="fileUpload" class="control-label">버전정보</label>
	            	<input class="form-control" type="text" value="<c:out value="${fn:trim(loginInfo.bldVersion)}"/>" readonly="readonly">
	             </div>
	             <div class="mb-3">
	             	<label for="fileUpload" class="control-label">최근빌드날짜</label>
	             	<fmt:parseDate var="bldTimestamp" value="${fn:trim(loginInfo.bldTimestamp)}" pattern="yyyyMMddHHmmssSSS" />
                    <input type="text" class="form-control" value="<fmt:formatDate value="${bldTimestamp}" pattern="yyyy-MM-dd HH:mm:ss.SSS" />" readonly="readonly"/>
	             </div>
             </div>
             <div class="modal-footer">
             	<button type="button" class="btn btn-success" data-bs-dismiss="modal">확인</button>
             </div>
         </div>
     </div>
</div>
    </c:if>
