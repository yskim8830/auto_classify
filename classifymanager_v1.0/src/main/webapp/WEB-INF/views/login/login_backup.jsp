<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" 		uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" 		uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="CTX_PATH" value="${pageContext.request.contextPath}" scope="request"/>
<c:set var="csrf" value="${_csrf.token}"/>
<!DOCTYPE html>
<html dir="ltr" language="ko">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <!-- Tell the browser to be responsive to screen width -->
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="Probot Manager">
    <meta name="author" content="Proten">
    <meta name="_csrf_parameter" content="${_csrf.parameterName}" />
    <meta name="_csrf_header" content="${_csrf.headerName}" />
    <meta name="_csrf" content="${_csrf.token}" />

    <!-- Favicon icon -->
    <!-- 
    <link rel="icon" type="image/png" sizes="16x16" href="${CTX_PATH}/static/images/favicon.png">
    -->
    <link rel="icon" type="image/png" sizes="16x16" href="${CTX_PATH}/static/images/favicon_chatbot.png">
    <title>ProClassify-Manager</title>
    <!-- Custom CSS -->
    <link href="${CTX_PATH}/static/package/dist/css/style.min.css" rel="stylesheet" />
	<script src="${CTX_PATH}/static/package/assets/libs/jquery/dist/jquery.min.js"></script>
    
	<script type="text/javascript">
	<!--
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	
	$(function () {
	
	    $(document).ajaxSend(function(e, xhr, options) {
	        if(token && header) {
	            xhr.setRequestHeader(header, token);
	        }
	    });
	});
	
	
	$(document).ready(function () {
	
	
	        $("#submit").click(function (e) {
	
	            if ( ! validateLogin() ) {
	                return;
	            }
	
	            var userId = $("#userId").val();
	            var password = $("#password").val();
	
	
	            var rsaPublicKeyModules ;
	            var rsaPublicKeyExponent;
	
	            try {
	                rsaPublicKeyModules = document.getElementById("rsaPublicKeyModules").value;
	                rsaPublicKeyExponent = document.getElementById("rsaPublicKeyExponent").value;
	            } catch(err) {
	                alert(err);
	            }
	
	            var rsa = new RSAKey();
	            rsa.setPublic(rsaPublicKeyModules, rsaPublicKeyExponent);
	
	            var securedUserId = rsa.encrypt(userId);
	            var securedPassword = rsa.encrypt(password);
	
	
		        var loginData = { userId : securedUserId , password : securedPassword , _csrf : token};
	
	            e.preventDefault();
	
	            $.ajax({
	                url: "${CTX_PATH}/loginCheck.ps",
	                method: 'POST',
	        	    data : loginData,
	                dataType: 'JSON',
	                success: function (data) {
	                  var retStr = data["loginSuccess"];
	                  if(retStr == true){
	                      if ( "searchtool" == userId  ) {
	                          location.href = "${CTX_PATH}/searchtool/searchmain.ps";
	                      } else {
	                          location.href = "${CTX_PATH}/main/main.ps";
	                      }
	
	                  }else{
	                     var errorMsg = data["errorMsg"];
	                     if ( errorMsg != null && errorMsg != "") {
	                        alert(errorMsg);
	                     } else {
	                        alert("아이디 혹은 패스워드를 확인하세요.");
	                     }
	                    location.reload();
	                  }
	
	              },
	              error : function(xhr, exMessage) {
	                  alert("아이디/패스워드 입력시간을 초과하였거나 시스템 오류입니다.");
	                  location.reload();
	              }
	            });
	
	        });
	});
	function validateLogin() {
	
		var chk = checkNotEmpty(
			[
				 ["userId", "아이디를 입력해 주세요."]
				,["password", "비밀번호를 입력해 주세요."]
			]
		);
	
		if ( !chk ) {
			return;
		}
	
		return true;
	}
	-->
	</script>

</head>
<body>
    <div class="main-wrapper">
        <!-- ============================================================== -->
        <!-- Preloader - style you can find in spinners.css -->
        <!-- ============================================================== -->
		<div class="preloader">
		    <svg class="tea lds-ripple" width="37" height="48" viewbox="0 0 37 48" fill="none" xmlns="http://www.w3.org/2000/svg">
		        <path d="M27.0819 17H3.02508C1.91076 17 1.01376 17.9059 1.0485 19.0197C1.15761 22.5177 1.49703 29.7374 2.5 34C4.07125 40.6778 7.18553 44.8868 8.44856 46.3845C8.79051 46.79 9.29799 47 9.82843 47H20.0218C20.639 47 21.2193 46.7159 21.5659 46.2052C22.6765 44.5687 25.2312 40.4282 27.5 34C28.9757 29.8188 29.084 22.4043 29.0441 18.9156C29.0319 17.8436 28.1539 17 27.0819 17Z" stroke="#2962FF" stroke-width="2"></path>
		        <path d="M29 23.5C29 23.5 34.5 20.5 35.5 25.4999C36.0986 28.4926 34.2033 31.5383 32 32.8713C29.4555 34.4108 28 34 28 34" stroke="#2962FF" stroke-width="2"></path>
		        <path id="teabag" fill="#2962FF" fill-rule="evenodd" clip-rule="evenodd" d="M16 25V17H14V25H12C10.3431 25 9 26.3431 9 28V34C9 35.6569 10.3431 37 12 37H18C19.6569 37 21 35.6569 21 34V28C21 26.3431 19.6569 25 18 25H16ZM11 28C11 27.4477 11.4477 27 12 27H18C18.5523 27 19 27.4477 19 28V34C19 34.5523 18.5523 35 18 35H12C11.4477 35 11 34.5523 11 34V28Z"></path>
		        <path id="steamL" d="M17 1C17 1 17 4.5 14 6.5C11 8.5 11 12 11 12" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" stroke="#2962FF"></path>
		        <path id="steamR" d="M21 6C21 6 21 8.22727 19 9.5C17 10.7727 17 13 17 13" stroke="#2962FF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"></path>
		    </svg>
		</div>
        <!-- ============================================================== -->
        <!-- Preloader - style you can find in spinners.css -->
        <!-- ============================================================== -->
        <!-- ============================================================== -->
        <!-- Login box.scss -->
        <!-- ============================================================== -->
        <div class="row auth-wrapper gx-0">
        	
			<div class="col-lg-4 col-xl-3 bg-info auth-box-2 on-sidebar" style="background-color: #1e2a56!important;">
			    <div class="h-100 d-flex align-items-center justify-content-center">
			        <div class="row justify-content-center text-center" style="background-color: #1e2a56;">
			            <div class="col-md-7 col-lg-12 col-xl-9">
			                <h2 class="text-white mt-4 fw-light">
			                    <img src="${CTX_PATH}/static/images/prochat_logo.png" alt="logo" style="max-width: 100%;"/>
			                </h2>
			                <p class="op-5 text-white fs-4 mt-4">
			                    ProClassify V2 MANAGER
			                </p>
			            </div>
			        </div>
			    </div>
			</div>
			<div class="col-lg-8 col-xl-9 d-flex align-items-center justify-content-center">
			    <div class="row justify-content-center w-100 mt-4 mt-lg-0">
			        <div class="col-lg-6 col-xl-3 col-md-7">
			            <div class="card" id="loginform">
			                <div class="card-body">
			                    <form class="form-horizontal mt-4 needs-validation" id="frm" method="post" novalidate>
			                    	<input type="hidden" id="rsaPublicKeyModules" value="${publicKeyModulus}" />
                    				<input type="hidden" id="rsaPublicKeyExponent" value="${publicKeyExponent}" />
                    
			                        <div class="form-floating mb-3">
			                            <input type="text" class="form-control form-input-bg" id="userId" name="userId" autocomplete="off" placeholder="ID 입력" title="아이디 입력" required />
			                            <label for="tb-id">ID</label>
			                            <div class="invalid-feedback">ID is required</div>
			                        </div>
			
			                        <div class="form-floating mb-3">
			                            <input type="password" class="form-control form-input-bg" id="password" name="password" title="비밀번호 입력" autocomplete="new-password" placeholder="Password" required />
			                            <label for="text-password">Password</label>
			                            <div class="invalid-feedback">Password is required</div>
			                        </div>
			                        
			                        <div class="d-flex align-items-stretch button-group mt-4 pt-2">
			                            <button type="submit" id="submit" class="btn btn-info btn-lg px-4">
			                                Login
			                            </button>
			                        </div>
			                    </form>
			                    <form id="securedLoginForm" name="securedLoginForm" action="${CTX_PATH}/loginCheck.ps" method="post" style="display: none;">
			                        <input type="hidden" name="securedUserId" id="securedUserId" value="" />
			                        <input type="hidden" name="securedPassword" id="securedPassword" value="" />
			                    </form>
			                </div>
			            </div>
			        </div>
			    </div>
			</div>
        </div>
        <!-- ============================================================== -->
        <!-- Login box.scss -->
        <!-- ============================================================== -->
        <!-- ============================================================== -->
        <!-- Page wrapper scss in scafholding.scss -->
        <!-- ============================================================== -->
        <!-- ============================================================== -->
        <!-- Page wrapper scss in scafholding.scss -->
        <!-- ============================================================== -->
        <!-- ============================================================== -->
        <!-- Right Sidebar -->
        <!-- ============================================================== -->
        <!-- ============================================================== -->
        <!-- Right Sidebar -->
        <!-- ============================================================== -->
    </div>
    <!-- ============================================================== -->
    <!-- All Required js -->
    <!-- ============================================================== -->
    <!-- Bootstrap tether Core JavaScript -->
    <script src="${CTX_PATH}/static/package/assets/libs/bootstrap/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${CTX_PATH}/static/js/rsa/jsbn.js"></script>
    <script src="${CTX_PATH}/static/js/rsa/rsa.js"></script>
    <script src="${CTX_PATH}/static/js/rsa/prng4.js"></script>
    <script src="${CTX_PATH}/static/js/rsa/rng.js"></script>
	<script src="${CTX_PATH}/static/js/common.js" charset="utf-8" ></script>
    <!-- ============================================================== -->
    <!-- This page plugin js -->
    <!-- ============================================================== -->
    <script>
	    $(".preloader").fadeOut();
	
	    (function () {
	      "use strict";
	
	      // Fetch all the forms we want to apply custom Bootstrap validation styles to
	      var forms = document.querySelectorAll(".needs-validation");
	
	      // Loop over them and prevent submission
	      Array.prototype.slice.call(forms).forEach(function (form) {
	        form.addEventListener(
	          "submit",
	          function (event) {
	            if (!form.checkValidity()) {
	              event.preventDefault();
	              event.stopPropagation();
	            }
	
	            form.classList.add("was-validated");
	          },
	          false
	        );
	      });
	    })();
    </script>
</body>
</html>
