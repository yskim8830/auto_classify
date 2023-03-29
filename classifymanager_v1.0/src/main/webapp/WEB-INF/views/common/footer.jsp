<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


            <!-- ============================================================== -->
            <!-- footer -->
            <!-- ============================================================== -->
            <footer class="footer text-center">
                <b>PROCHAT</b> : Classify Solution. All Rights Reserved by ProTen CO,.LTD.  <a href="http://www.proten.co.kr">www.proten.co.kr</a>
            </footer>

            <!-- ============================================================== -->
            <!-- End footer -->
            <!-- ============================================================== -->
        </div>
        <!-- ============================================================== -->
        <!-- End Page wrapper  -->
        <!-- ============================================================== -->
    </div>
    <!-- ============================================================== -->
    <!-- End Wrapper -->
    <!-- ============================================================== -->
    <!-- Bootstrap tether Core JavaScript -->
    <script src="${CTX_PATH}/static/package/assets/libs/bootstrap/dist/js/bootstrap.bundle.min.js"></script>
    <!-- apps -->
    <script src="${CTX_PATH}/static/package/dist/js/app.min.js"></script>
    <script src="${CTX_PATH}/static/package/dist/js/app.init.js"></script>
    <script src="${CTX_PATH}/static/package/dist/js/app-style-switcher.js"></script>
    <!-- slimscrollbar scrollbar JavaScript -->
    <script src="${CTX_PATH}/static/package/assets/libs/perfect-scrollbar/dist/perfect-scrollbar.jquery.min.js"></script>
    <script src="${CTX_PATH}/static/package/assets/extra-libs/sparkline/sparkline.js"></script>
    <!--Wave Effects -->
    <script src="${CTX_PATH}/static/package/dist/js/waves.js"></script>
    <!--Menu sidebar -->
    <script src="${CTX_PATH}/static/package/dist/js/sidebarmenu.js"></script>
    <!--Custom JavaScript -->
    <script src="${CTX_PATH}/static/package/dist/js/feather.min.js"></script>
    <script src="${CTX_PATH}/static/package/dist/js/custom.min.js"></script>
    <!-- This Page JS -->
    <script src="${CTX_PATH}/static/package/assets/libs/sweetalert2/dist/sweetalert2.all.min.js"></script>
    <script src="${CTX_PATH}/static/package/dist/js/pages/forms/sweetalert2/sweet-alert.init.js"></script>
    <script src="${CTX_PATH}/static/package/assets/libs/moment/min/moment.min.js"></script>
    <script src="${CTX_PATH}/static/package/assets/libs/bootstrap-material-datetimepicker/js/bootstrap-material-datetimepicker-custom.js"></script>
	
	
	<!-- D3 -->
	<link href="${CTX_PATH}/static/js/chart/c3.min.css" rel="stylesheet"/>
	<script type="text/javascript" charset="utf-8" src="${CTX_PATH}/static/js/d3.v2.js"></script>
	<script type="text/javascript" src="${CTX_PATH}/static/js/chart/d3.min.js"></script>
	<script type="text/javascript" src="${CTX_PATH}/static/js/chart/d3.layout.cloud.js"></script>
	<script type="text/javascript" src="${CTX_PATH}/static/js/chart/c3.min.js"></script>
	<script type="text/javascript" src="${CTX_PATH}/static/js/chart/radar.js"></script>
	
    <!--This page JavaScript -->
	<script type="text/javascript" charset="utf-8" src="${CTX_PATH}/static/js/common.js"></script>
	<script type="text/javascript" charset="utf-8" src="${CTX_PATH}/static/js/underscore-min.js"></script>
	<script src="${CTX_PATH}/static/package/assets/libs/block-ui/jquery.blockUI.js"></script>
<%
    int getMaxInactiveinterval = (session.getMaxInactiveInterval())*1000;
%>
<script type="text/javascript">
<!--
 /* ;(function($){
 	$.fn.datepicker.dates['kr'] = {
 		days: ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일"],
 		daysShort: ["일", "월", "화", "수", "목", "금", "토", "일"],
 		daysMin: ["일", "월", "화", "수", "목", "금", "토", "일"],
 		months: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
 		monthsShort: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
 		minDate: new Date("1970-12-31"),
 		maxDate: 0
 	};
	
 }(jQuery)); */

var token = $("meta[name='_csrf']").attr("th:content");
var header = $("meta[name='_csrf_header']").attr("th:content");

/*
setTimeout(function() {
    alert('세션이 종료되었습니다.');
    top.location.href = "${CTX_PATH}/login.ps";

}, <%=getMaxInactiveinterval%>);
*/

function callAjax(url, param, callback) {
    $.ajax({
      url : "${CTX_PATH}" + url,
      type : "post",
      data : param,
      cache : false,
      traditional:true,
      beforeSend: function(xhr){xhr.setRequestHeader(header,token);$.blockUI(blockUi);},
      complete: function(){$.unblockUI(blockUi);},
      success : function(data) { callback(data); },
      error : function(xhr, exMessage) {
    	if(xhr.status == 403){
	        alert('error Code : '+ xhr.status + '\nmessage : 장시간 미사용으로 인하여 로그아웃 되었습니다.\n'+ exMessage );
	        location.href = '${CTX_PATH}/login.ps';
    	}else{
	        alert('error Code : '+ xhr.status + '\nmessage : 시스템 오류가 발생하였습니다.! '+ xhr.responseText +'\n'+ exMessage );
    	}
        //history.go(-1);
      }
	});
}

function callAjaxNoLdng(url, param, callback) {
    $.ajax({
      url : "${CTX_PATH}" + url,
      type : "post",
      data : param,
      cache : false,
      traditional:true,
      beforeSend: function(xhr){xhr.setRequestHeader(header,token);},
      complete: function(){},
      success : function(data) { callback(data); },
      error : function(xhr, exMessage) {
	  	alert('error Code : '+ xhr.status + '\nmessage : 시스템 오류가 발생하였습니다.! '+ xhr.responseText +'\n'+ exMessage );
      }
	});
}

function callAjaxAsync(url, param, callback) {
    $.ajax({
      url : "${CTX_PATH}" + url,
      type : "post",
      data : param,
      async : false,
      cache : false,
      beforeSend: function(xhr){xhr.setRequestHeader(header,token);$.blockUI(blockUi);},
      complete: function(){$.unblockUI(blockUi);},
      success : function(data) { callback(data); },
      error : function(xhr, exMessage) {
    	if(xhr.status == 403){
  	        alert('error Code : '+ xhr.status + '\n message : 장시간 미사용으로 인하여 로그아웃 되었습니다. \n'+ exMessage );
  	        location.href = '${CTX_PATH}/login.ps';
      	}else{
  	        alert('error Code : '+ xhr.status + '\n message : 시스템 오류가 발생하였습니다.! \n'+ exMessage );
      	}
      }
	});
}

function callAjaxUpload(url, param, callback) {
    $.ajax({
      url : "${CTX_PATH}" + url,
      type : "post",
      data : param,
      processData: false,
      contentType: false,
      cache : false,
      beforeSend: function(xhr){xhr.setRequestHeader(header,token);$.blockUI(blockUi);},
      complete: function(){$.unblockUI(blockUi);},
      success : function(data) { callback(data); },
      error : function(xhr, exMessage) {
    	if(xhr.status == 403){
	        alert('error Code : '+ xhr.status + '\nmessage : 장시간 미사용으로 인하여 로그아웃 되었습니다.\n'+ exMessage );
	        location.href = '${CTX_PATH}/login.ps';
    	}else{
	        alert('error Code : '+ xhr.status + '\nmessage : 시스템 오류가 발생하였습니다.!\n'+ exMessage );
    	}
        //history.go(-1);
      }
	});
}

function callAjaxUploadProgress(url, param, callback, target) {
	target = target || '';
    $.ajax({
      url : "${CTX_PATH}" + url,
      type : "post",
      data : param,
      processData: false,
      contentType: false,
      cache : false,
      beforeSend: function(xhr){xhr.setRequestHeader(header,token);},
      complete: function(){},
      xhr: function() { //XMLHttpRequest 재정의 가능
    	  var xhr = $.ajaxSettings.xhr();
    	  xhr.upload.onprogress = function(e) { //progress 이벤트 리스너 추가
    	  	var percent = (e.loaded * 100 / e.total) / 2;
    	  	setProgress(target, percent);
    	  };
    	  return xhr;
	  },
      success : function(data) { 
  	  	  setProgress(target, '100');
    	  setTimeout(function(){
    	  	callback(data);
  		  },500);
   	  },
      error : function(xhr, exMessage) {
    	if(xhr.status == 403){
	        alert('error Code : '+ xhr.status + '\nmessage : 장시간 미사용으로 인하여 로그아웃 되었습니다.\n'+ exMessage );
	        location.href = '${CTX_PATH}/login.ps';
    	}else{
	        alert('error Code : '+ xhr.status + '\nmessage : 시스템 오류가 발생하였습니다.!\n'+ exMessage );
    	}
        //history.go(-1);
      }
	});
}

function setProgress(target, percent){
	setTimeout(function(){
		$('#'+target).css('width', percent + '%');
		$('#'+target).text(percent + '%');
	},1000);
}

//사이트 변경
function chageSession(siteId,siteNm){
	
	Swal.fire({
		title : "",
		text : "'"+siteNm+"'(으)로 사이트를 변경합니다.",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: 'btn-info',
        cancelButtonColor: 'btn-danger',
		confirmButtonText : "예",
		cancelButtonText : "아니오"
    }).then((result) => {
        if (result.value) {
			var param = { siteId : siteId }; 
		    var callBack = function(data) {
		    	location.href = '${CTX_PATH}/main/main.ps';
			};
			
			callAjax("/main/changeSession.ps", param, callBack);
        }
    });
}

var blockUi = {
		  message: '<i class="fas fa-spin fa-sync text-white"></i>',
		  overlayCSS: {
		    backgroundColor: "#000",
		    opacity: 0.5,
		    cursor: "wait",
		  },
		  css: {
		    border: 0,
		    padding: 0,
		    color: "#333",
		    backgroundColor: "transparent",
		  },
		};
-->
</script>