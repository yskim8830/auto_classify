<%@include file="/WEB-INF/views/common/header.jsp"%>
<%@include file="/WEB-INF/views/common/menu.jsp"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<link href="${CTX_PATH}/static/package/assets/libs/jquery-steps/jquery.steps.css" rel="stylesheet" type="text/css" />
<link href="${CTX_PATH}/static/package/assets/libs/jquery-steps/steps.css" rel="stylesheet" type="text/css" />
                <!-- ============================================================== -->
                <!-- Bread crumb and right sidebar toggle -->
                <!-- ============================================================== -->
				<div class="page-breadcrumb border-bottom">
				    <div class="row">
				        <div class="
				                col-lg-3 col-md-4 col-xs-12
				                justify-content-start
				                d-flex
				                align-items-center
				              ">
				            <h5 class="font-weight-medium text-uppercase mb-0">
				                학습&모델
				            </h5>
				        </div>
				        <div class="
				                col-lg-9 col-md-8 col-xs-12
				                d-flex
				                justify-content-start justify-content-md-end
				                align-self-center
				              ">
				            <nav aria-label="breadcrumb" class="mt-2">
				                <ol class="breadcrumb mb-0 p-0">
				                    <li class="breadcrumb-item"><a href="#">학습관리</a></li>
				                    <li class="breadcrumb-item active" aria-current="page">
				                        학습&모델
				                    </li>
				                </ol>
				            </nav>
				        </div>
				    </div>
				</div>
                <!-- ============================================================== -->
                <!-- End Bread crumb and right sidebar toggle -->
                <!-- ============================================================== -->
                <!-- ============================================================== -->
                <!-- Container fluid  -->
                <!-- ============================================================== -->
                <div class="container-fluid page-content">
                    <!-- ============================================================== -->
                    <!-- Start Page Content -->
                    <!-- ============================================================== -->
                <div class="row">
					<div class="col-12">
                        <div class="card">
                            <div class="card-body">
                            	<div class="row">
	                                <div class="col-md-6 mb-2">
	                                    <h5 class="card-title m-b-0"><i class="m-r-10 mdi mdi-code-greater-than"></i> 모델 배포</h5>
	                                </div>
                                	<div class="row mb-2">
					                    
					                    <div id="trainStateArea">         	
						              		<div class="alert customize-alert  alert-dismissible border-secondary text-secondary fade show" role="alert" >
	    										<h4 class="alert-heading">${siteNm} 사이트</h4>
	    										<div class="row justify-content-center">
												    <div class="col-md-4 col-lg-6 d-grid gap-2">
												    	<button type="button" onclick="startTraining();" id="startLearnBtn" class="btn waves-effect waves-light btn-outline-success" disabled>학습 실행</button>
												    </div>
												    <div class="col-md-4 col-lg-6 d-grid gap-2">
												    	<button type="button" onclick="stopTraining();" id="stopLearnBtn" class="btn waves-effect waves-light btn-outline-danger" disabled>학습 중지</button>
												    </div>
												</div>   
											  	<div class="card-body wizard-content" style="display:block;padding-bottom:0px;">
								                 	<h5 class="card-subtitle mb-3">학습상태</h5>
								                 	<form action="#" class="tab-wizard wizard-circle">
								                 		<!-- Step 1 -->
								                    	<h6>대기</h6>
								                    	<section>
								                    	</section>
								                    	<!-- Step 1 -->
								                    	<h6>학습데이터 준비</h6>
								                    	<section>
								                    	</section>
								                    	<!-- Step 2 -->
								                    	<h6>전처리</h6>
								                    	<section>
								                    	</section>
								                    	<!-- Step 3 -->
								                    	<h6>학습진행</h6>
								                    	<section>
								                    	</section>
								                    	<!-- Step 4 -->
								                    	<h6>후처리</h6>
								                    	<section>
								                    	</section>
														<!-- Step 5 -->
								                    	<h6>학습 완료</h6>
								                    	<section>
								                    	</section>
								                    	<div class="row">
													    	<div id="trainError" style="display:none;"><h3><span class="badge bg-danger">Error</span></h3></div>
													    	<pre id="trainResult" style="font-size: 15px;"></pre>
								                     	</div>
								                  	</form>
								                </div>
								                <!--  
											    <div class="mb-1 mb-0 text-end" >
						                			<button type="button" id="confirmLearnBtn" style="width:100px;" class="btn btn-success" aria-hidden="false" onclick="trainReset();" disabled>확인</button>
						                		</div>
						                		-->
											</div>
						            	</div>
					                    
					                    
					                </div>
	                                <div class="row">
                                    	<div class="col-md-12">
                                        	<table id="zero_config" class="table">
                                         		<thead class="thead-light">
	                                            	<tr>
	                                                	<th scope="col">버전</th>
	                                                	<th scope="col">학습 요청자</th>
	                                                	<th scope="col">학습 실행 시간</th>
	                                                	<th scope="col">학습 소요시간</th>
	                                                	<th scope="col">서비스 반영</th>
	                                                </tr>
	                                            </thead>
	                                            <tbody id="tbodyModelList">
	                                            </tbody>
	                                            <tr>
	                                                <td colspan=6></td>
	                                            </tr>
                                        	</table>
                                    	</div>
                                 	</div>
                                </div>
					        </div>
    					</div>
					</div>
                </div>
			    <input type="hidden" name="pageNo" id="pageNo" value="1">
                <div class="row">
					<div class="col-12">
                        <div class="card">
                            <div class="card-body">
                            	<div class="row">
	                                <div class="col-md-6 mb-2">
	                                    <h5 class="card-title m-b-0"><i class="m-r-10 mdi mdi-code-greater-than"></i> 학습 이력</h5>
	                                </div>
                                    <div class="row mb-2">
									    <div class="col-md-3">
									        <label class="control-label">목록수</label>
									        <select class="form-select" id="lineNo" name="lineNo" onchange="getChangeItem();">
									            <option value="10">10</option>
									            <option value="20">20</option>
									            <option value="30">30</option>
									            <option value="50">50</option>
									        </select>
										</div>
									</div>
	                                <div class="row">
                                    	<div class="col-md-12">
                                        	<table id="zero_config" class="table">
                                         		<thead class="thead-light">
	                                            	<tr>
	                                                	<th scope="col">버전</th>
	                                                	<th scope="col">분류 데이터 건수</th>
	                                                	<th scope="col">학습 요청자</th>
	                                                	<th scope="col">학습 실행 시간</th>
	                                                	<th scope="col">학습 종료 시간</th>
	                                                	<th scope="col">학습 소요시간</th>
	                                                	<th scope="col">학습 상태</th>
	                                                </tr>
	                                            </thead>
	                                            <tbody id="tbodyLearnList">
	                                            </tbody>
	                                            <tr>
	                                                <td colspan=7></td>
	                                            </tr>
                                        	</table>
                                    	</div>
                                 	</div>
	                                <div class="row">
	                                    <div class="col-sm-12 col-md-5">
	                                    	<strong> 총 <span id="totalCnt">0</span>건</strong>
	                                    </div>
	                                    <div class="col-sm-12 col-md-7" >
	                                       <div class="dataTables_paginate paging_simple_numbers" id="zero_config_paginate">
	                                           <ul class="pagination justify-content-end" id="pagination"  >
	                                           </ul>
	                                       </div>
	                                    </div>
	                                </div>
                                </div>
					        </div>
    					</div>
					</div>
                </div>
            </div>

<%@include file="/WEB-INF/views/common/footer.jsp"%>

<script src="${CTX_PATH}/static/package/assets/libs/jquery-steps/build/jquery.custom.steps.js"></script>

<!-- 정보 Template Start   -->
<!-- 정보 리스트 출력  -->
<script id="modelListRowTmpl" type="text/x-jquery-tmpl">
    {{each(i, gUList) modelingList}}
        <tr id="listRow\${i}">
            <td>\${gUList.version}</td>
            <td>\${gUList.createUser}</td>
            <td>\${GetDateMMDDSS(gUList.runStartDate)}</td>
            <td>\${GetInterval(gUList.runtime)}</td>
			{{if gUList.service == 'y'}}
				<td><a href="#" id="model_\${gUList.version}" class="btn btn-success btn-sm">서비스 중</a></td>
			{{/if}}
			{{if gUList.service != 'y'}}
				<td><a href="#" id="model_\${gUList.version}" class="btn btn-danger btn-sm" onclick="distModelingToService('\${gUList.version}');" >서비스 반영</a></td>
			{{/if}}
        </tr>
    {{/each}}
</script>
<!-- 정보 리스트 출력  -->
<script id="learnListRowTmpl" type="text/x-jquery-tmpl">
    {{each(i, gUList) learningList}}
        <tr id="listRow\${i}">
            <td>\${gUList.version}</td>
            <td>\${numberWithCommas(gUList.dataCnt)} 건</td>
            <td>\${gUList.createUser}</td>
            <td>\${GetDateMMDDSS(gUList.runStartDate)}</td>
            <td>\${GetDateMMDDSS(gUList.runEndDate)}</td>
            <td>\${GetInterval(gUList.runtime)}</td>
			<td>\${gUList.state}</td>
        </tr>
    {{/each}}
</script>
<script type="text/javascript">
var running;

//화면 로딩 처리 함수 
$(document).ready(function(){
	init();
});

function init(){
	modelingList();
	learningList();
	trainStepInit();
	timerId = setInterval(statusTraining,1000);
}

function getChangeItem(){
	learningList();
}

//최근 학습 모델 목록
function modelingList(){
	var param = {
		state : 'success'
	}; 
	var listBack = function(data) {
		$('#tbodyModelList').html('');
		$("#modelListRowTmpl").tmpl(data).appendTo("#tbodyModelList");
	};
	
	callAjax("/learning/modelingAjax.ps", param, listBack);
}

//학습이력 목록
function learningList(pageNo){
	pageNo = pageNo || 1;
	$('#pageNo').val(pageNo);
	var param = {
			pageNo : pageNo
			, lineNo : $('select[name=lineNo]').val()
	}; 
	var listBack = function(data) {
		var paginationHtml = getPaginationHtml(pageNo, data.totalCnt, $('select[name=lineNo]').val(), '10', 'learningList');
		$('#tbodyLearnList').html('');
		$("#learnListRowTmpl").tmpl(data).appendTo("#tbodyLearnList");
		$("#totalCnt").html(data.totalCnt);
		$("#pagination").html( paginationHtml );
	};
	callAjax("/learning/learningAjax.ps", param, listBack);
}

var timerId = null;
//학습시작
function startTraining() {
	if(running == 'Y'){
		Swal.fire({
			type: "warning",
			text: "학습 진행중입니다.\n학습이 종료되면 다시 시도해 주세요.",
		});
		return;
	}
	var param = {
	}; 

	Swal.fire({
		title: "학습",
		text: '학습을 시작 하시겠습니까?',
		type: 'warning', 
		showCancelButton: true,
	        confirmButtonColor: 'btn-info',
	        cancelButtonColor: 'btn-danger',
		confirmButtonText: '시작', 
		cancelButtonText: '취소'
	}).then((result) => { 
		if(result.value){
			callAjax("/learning/startTrainingAjax.ps", param, listBack);
		} else {
			return;
		}
	});
	var listBack = function(data) {
		if ( data && data.success ) {
			var resultStr = data.resultJson;
			var resultObj = JSON.parse(resultStr);
			if(resultObj.status.code == '200'){
				running = 'Y';	
				if(timerId == null){
					timerId = setInterval(statusTraining,1000);					
				}
			} else {
				$('#trainError').show();
				$('#trainResult').html(resultObj.status.message);
			}
		} else{
			// API 요청 실패
			$('#trainError').show();
			$('#trainResult').html(data.msg);
		}
	};
}

//학습중지
function stopTraining() {
	if(running == 'N'){
		Swal.fire({
			type: "warning",
			text: "학습 진행 상태가 아닙니다.",
		});
		return;
	}
	var param = {
	};
	Swal.fire({
		title: "학습중지",
		text: '학습을 정말로 중지 하시겠습니까?',
		type: 'warning', 
		showCancelButton: true,
	        confirmButtonColor: 'btn-info',
	        cancelButtonColor: 'btn-danger',
		confirmButtonText: '중지', 
		cancelButtonText: '취소'
	}).then((result) => { 
		if(result.value){
			callAjax("/learning/stopTrainingAjax.ps", param, listBack);
		} else {
			return;
		}
	});
	var listBack = function(data) {
		if ( data && data.success ) {
			var resultStr = data.resultJson;
			var resultObj = JSON.parse(resultStr);
			if(resultObj.status.code == '200'){
				running = 'N';	
				clearTimer();
				setTimeout(function() {
					trainReset();
				}, 3000);
			} else {
				$('#trainError').show();
				$('#trainResult').html(resultObj.status.message);
			}
		} else{
			// API 요청 실패
			$('#trainError').show();
			$('#trainResult').html(data.msg);
		}
	};
}

function distModelingToService(version) {
	if(running == 'Y'){
		Swal.fire({
			type: "warning",
			text: "학습 진행중입니다.\n학습이 종료되면 다시 시도해 주세요.",
		});
		return;
	}
	
	var param = {
			version : version
	}; 
	
	Swal.fire({
		title: "서비스 반영",
		text: '해당 학습 데이터를 서비스 반영 하시겠습니까?',
		type: 'warning', 
		showCancelButton: true,
	    confirmButtonColor: 'btn-info',
	    cancelButtonColor: 'btn-danger',
		confirmButtonText: '반영', 
		cancelButtonText: '취소'
	}).then((result) => { 
		if(result.value){
			callAjax("/learning/distModelToServiceAjax.ps", param, listBack);
		} else {
			return;
		}
	});
	
	var listBack = function(data) {
		if ( data && data.success ) {
			var resultStr = data.resultJson;
			var resultObj = JSON.parse(resultStr);
			if(resultObj.status.code == '200'){
				Swal.fire({
					type: "info",
					text: resultObj.status.message,
				}).then((result) => { 
					trainReset();
				});
			} else {
				$('#trainError').show();
				$('#trainResult').html(resultObj.status.message);
			}
		} else{
			// API 요청 실패
			$('#trainError').show();
			$('#trainResult').html(data.msg);
		}
	};
}

//학습진행상태 조회
function statusTraining(){
	var param = {}; 
	var listBack = function(data) {
		console.log(data);
		if ( data && data.success ) {
			console.log(data.resultJson);
			var resultStr = data.resultJson;
			var resultObj = JSON.parse(resultStr);
			
			var step = '';
			console.log(resultObj);
			if(resultObj.status.code == '200'){
				if(resultObj.site_status.running == 'y'){
					running = 'Y';
					step = resultObj.site_status.step;
					$('#startLearnBtn').attr("disabled", true);
					$('#stopLearnBtn').attr("disabled", false);
					var currentIndex = $(".tab-wizard").steps('getCurrentIndex');
					console.log('currentIndex : ' + currentIndex);
									
					if(step == '00'){ // index : 0
						$('#trainResult').html('학습 대기 상태입니다. "학습 실행" 버튼을 클릭하면 학습을 진행 할 수 있습니다.');
					} else if(step == '01'){ 
						if(currentIndex == 0){$(".tab-wizard").steps('next');}
						$('#trainResult').html('학습을 위한 데이터 로딩 중입니다.');
					} else if(step == '02'){ // preprocessing
						if(currentIndex == 0){$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');}
						if(currentIndex == 1){$(".tab-wizard").steps('next');}
						$('#trainResult').html('학습데이터 전처리 작업 중입니다.');
					} else if(step == '03'){ // training
						if(currentIndex == 0){$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');}
						if(currentIndex == 1){$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');}
						if(currentIndex == 2){$(".tab-wizard").steps('next');}
						$('#trainResult').html('학습 진행중입니다. 일정 시간이 소요됩니다.');
					} else if(step == '04'){ // postprocessing
						if(currentIndex == 0){$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');}
						if(currentIndex == 1){$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');}
						if(currentIndex == 2){$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');}
						if(currentIndex == 3){$(".tab-wizard").steps('next');}
						$('#trainResult').html('학습 결과 후처리 작업 중입니다.');
					} else if(step == '05'){ // finished
						if(currentIndex == 0){$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');}
						if(currentIndex == 1){$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');}
						if(currentIndex == 2){$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');}
						if(currentIndex == 3){$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');}
						if(currentIndex == 4){$(".tab-wizard").steps('next');}
						$(".tab-wizard").steps('finish');
						$('#trainResult').html('학습이 완료 되었습니다.');
						clearTimer();
						setTimeout(function() {
							trainReset();
						}, 3000);
					} else {
						$('#trainResult').html('');
					}
				} else {
					if(running == 'Y'){
						step = resultObj.site_status.step;
						if(step == '05'){
							var currentIndex = $(".tab-wizard").steps('getCurrentIndex');
							console.log('currentIndex : ' + currentIndex);
							
							if(currentIndex == 0){$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');}
							if(currentIndex == 1){$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');}
							if(currentIndex == 2){$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');}
							if(currentIndex == 3){$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');}
							if(currentIndex == 4){$(".tab-wizard").steps('next');}
							$(".tab-wizard").steps('finish');
							$('#trainResult').html('학습이 완료 되었습니다.');
							$('#startLearnBtn').attr("disabled", false);
							$('#stopLearnBtn').attr("disabled", true);
							clearTimer();
							setTimeout(function() {
								trainReset();
							}, 3000);
						}
					} else {
						running = 'N';
						$('#trainResult').html('학습 대기 상태입니다. "학습 실행" 버튼을 클릭하면 학습을 진행 할 수 있습니다.');
						$('#startLearnBtn').attr("disabled", false);
						$('#stopLearnBtn').attr("disabled", true);
						clearTimer();
					}
				}
			} else if(resultObj.status.code == '510'){
				running = 'N';
				$('#trainResult').html("현재 사이트에 학습된 이력이 없습니다.");
				$('#startLearnBtn').attr("disabled", false);
				$('#stopLearnBtn').attr("disabled", true);
				clearTimer();
			} else {
				$('#trainError').show();
				$('#trainResult').html(resultObj.status.message);
				clearTimer();
			}
		} else {
			// API 요청 실패
			$('#trainError').show();
			$('#trainResult').html(data.msg);
			clearTimer();
		}
	};
	callAjax("/learning/statusTrainingAjax.ps", param, listBack);
}

function clearTimer(){
	console.log('[clearTimer] timerId : ' + timerId);
	if(timerId != null){
		clearInterval(timerId);
		timerId = null;
	}
}

function trainStepInit(){	
	$(".tab-wizard").steps({
	      headerTag: "h6",
	      bodyTag: "section",
	      transitionEffect: "fade",
	      startIndex: 0,
	      enablePagination: false,
	      titleTemplate: '<span class="step">#index#</span> #title#',
	      onInit: function (event, currentIndex) {
	    	  //console.log('onInit : ' + currentIndex);
	      },
	      onFinished: function (event, currentIndex) {
	          //console.log('onFinished : ' + currentIndex);
	      }
	    });
}

function trainReset() {
	running = 'N';
	$('#startLearnBtn').attr("disabled", false);
	$(".tab-wizard").steps('first');
	$(".tab-wizard ul li").each(function(index, item){
		if(index == 0){
			$(this).removeClass();
			$(this).addClass('first').addClass('current');
		} else {
			$(this).removeClass();
			$(this).addClass('disabled');
			if(index == 5){
				$(this).addClass('last');
			}
		}
	});
	$('#trainError').hide();
	$('#trainResult').html('학습 대기 상태입니다. "학습 실행" 버튼을 클릭하면 학습을 진행 할 수 있습니다.');
	$('#stopLearnBtn').attr("disabled", true);
	modelingList();
	learningList();
}
</script>

<%@include file="/WEB-INF/views/common/end.jsp"%>