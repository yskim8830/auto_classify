<%@include file="/WEB-INF/views/common/header.jsp"%>
<%@include file="/WEB-INF/views/common/menu.jsp"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link href="${CTX_PATH}/static/css/json-path-picker/json-path-picker-styles.css" rel="stylesheet" />
<link href="${CTX_PATH}/static/css/json-path-picker/json-path-picker.css" rel="stylesheet" />
<link rel="stylesheet" type="text/css" href="${CTX_PATH}/static/package/assets/libs/bootstrap-touchspin/dist/jquery.bootstrap-touchspin.min.css"/>

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
				                시뮬레이션
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
				                        시뮬레이션
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
                                	<div class="col-md-6">
                                    	<h5 class="card-title m-b-0"><i class="m-r-10 mdi mdi-code-greater-than"></i> 시뮬레이션</h5>
                                	</div>
                                </div>
                                <div class="row">
                                	<div class="col-md-12">
                                		<ul class="nav nav-pills" role="tablist">
										    <li class="nav-item">
										        <a class="nav-link active" data-bs-toggle="tab" href="#navpill-1" role="tab" onclick="setSingleSimulation();">
										            <span>단건 시뮬레이션</span>
										        </a>
										    </li>
										    <li class="nav-item">
										        <a class="nav-link" data-bs-toggle="tab" href="#navpill-2" role="tab" onclick="setBulkSimulation();">
										            <span>일괄 시뮬레이션</span>
										        </a>
										    </li>
										</ul>
                                	</div>
                                </div>
                                <div class="row" >
                                	<div class="col-md-12">
										<div class="tab-content mt-2">
										    <div class="tab-pane active p-3" id="navpill-1" role="tabpanel">
										       <div class="row">
										            <div class="col-md-5">
										            	<div class="input-group mb-2">
													        <select class="form-select mr-sm-2" id="singleModelVersion">
												        		<c:forEach var="modeling" items="${modelingList}" varStatus="status">
																    <option value="${modeling.version}">
																    	<c:out value="${modeling.version}"/>
																    	<c:if test="${modeling.service == 'y'}">
																    	(서비스 중)
																    	</c:if>
																    </option>
																</c:forEach>
													        </select>
											        		<button type="button" id="singleSimulationBtn" class="btn waves-effect waves-light btn-outline-success" ">분석하기</button>
												        </div>
										            	<div class="layer-content">
													        <div class="card">
																<div class="card-body" style="background-color: rgba(0,0,0,.02);">
															        <form class="form-material mt-4">
															            <div class="mb-3">
															            	<label>문서</label>
															                <textarea id="singleDocument" class="form-control" rows="5" style="min-height:400px;" placeholder="내용을 입력해 주세요."></textarea>
															            </div>
															        </form>
															    </div>
															</div>   
													    </div>
										            </div>
										            <div class="col-md-7">
										        		<div class="row border">
                                							<div class="col-md-12 mt-1">
                                								<ul class="nav nav-tabs" role="tablist">
											    					<li class="nav-item">
											        					<a class="nav-link active" data-bs-toggle="tab" href="#tabResponse" role="tab">
											            					<span>Response</span>
											        					</a>
											    					</li>
																</ul>
																<div class="tab-content" style="min-height:200px;">
											    					<div class="tab-pane active" id="tabResponse" role="tabpanel">
											    						<div class="row">
											    							<div class="controls">
												                    			<label for="rst_tagSentence" class="control-label col-form-label">태깅된 문서</label>
												                    			<textarea id="rst_tagSentence" name="rst_tagSentence" class="form-control" rows="5" style="min-height:150px;" readonly></textarea>
													                  		</div>
													    					<div class="col-sm-12 col-md-4">
													                  			<div class="controls">
													                    			<label for="rst_resultType" class="control-label col-form-label">분석결과</label>
													                    			<input type="text" class="form-control" id="rst_resultType" name="rst_resultType" readonly value=""/>
													                  			</div>
													                		</div>
													    					<div class="col-sm-12 col-md-4">
													                  			<div class="controls">
													                    			<label for="rst_matchedType" class="control-label col-form-label">매칭타입</label>
													                    			<input type="text" class="form-control" id="rst_matchedType" name="rst_matchedType" readonly value=""/>
													                  			</div>
													                		</div>
													    					<div class="col-sm-12 col-md-4">
													                  			<div class="controls">
													                    			<label for="rst_matchedCategory" class="control-label col-form-label">카테고리</label>
													                    			<input type="text" class="form-control" id="rst_matchedCategory" name="rst_matchedCategory" readonly value=""/>
													                  			</div>
													                		</div>
													                		<div class="controls" style="padding-top:10px;">
														                		<div id="singleSuccess" style="display:none;"><h3><span class="badge bg-success">Success</span></h3></div>
			    																<div id="singleError" style="display:none;"><h3><span class="badge bg-danger">Error</span></h3></div>
													    						<pre id="apiResult" style="font-size: 15px;"></pre>
													                		</div>
											    						</div>
											    					</div>
																</div>
														    </div>
										        		</div>
										            </div>
										        </div>
										    </div>
										    <div class="tab-pane p-3" id="navpill-2" role="tabpanel">
												<form id="frmUpload" name="frmUpload" action="" method="post" enctype="multipart/form-data">
													<div class="mb-3 row">
													    <label for="bulkModelVersion" class="col-md-1 col-form-label">학습버전</label>
													    <div class="col-md-3">
											            	<select class="form-select" id="bulkModelVersion" name="bulkModelVersion">
													        	<c:forEach var="modeling" items="${modelingList}" varStatus="status">
																	<option value="${modeling.version}">
																		<c:out value="${modeling.version}"/>
																		<c:if test="${modeling.service == 'y'}">
																	 	(서비스 중)
																		</c:if>
																	</option>
																</c:forEach>
													        </select>
													    </div>
													    <label for="mThreshold" class="col-md-1 col-form-label">임계값</label>
													    <div class="col-md-2">
													    	<input id="mThreshold" type="text" value="80" name="mThreshold" />
													    </div>
													    <div class="col-md-3">
													        <input class="form-control" type="file" onchange="onChangeFile();" id="fileUpload" name="fileUpload" accept=".xls,.xlsx">
													    </div>
													    <div class="col-md-2">
													        <div class="input-group">
													        	<button type="button" class="btn waves-effect waves-light btn-outline-success" id="bulkSimulationBtn" >시뮬레이션 시작</button>
										                      	<button type="button" class="btn btn-outline-success text-success dropdown-toggle dropdown-toggle-split" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
										                        	<span class="sr-only"></span>
										                      	</button>
	                      										<div class="dropdown-menu">
	                        										<a class="dropdown-item" href="#none" id="templateDownloadBtn">템플릿 다운로드</a>
	                      										</div>
	                    									</div>
													    </div>
													</div>
													<input type="hidden" id="serviceId" name="serviceId" value="bulkSimulation">
												</form>
												<div class="mb-3 row" id="bulkSimulationProgressArea" style="display:none;">
												    <label for="progressArea" class="col-md-2 col-form-label" id="bulkSimulationProgressLabel">시뮬레이션 진행중...</label>
												    <div class="col-md-8">
												    	<div class="progress mt-2" style="height: 20px;">
													    	<div class="progress-bar bg-success" style="width: 0%;" role="progressbar" id="bulkSimulationProgressPercent">0%</div>
													    </div>
												    </div>
												    <div class="col-md-2">
												    	<button class="btn btn-outline-danger" type="button" onclick="javascript:terminateBulkSimulation();">시뮬레이션 중지</button>
												    </div>
												</div>
												<div class="mb-3 row">
												    <div class="row border mt-2" style="overflow-y:auto; max-height:450px;">
												    	<table class="table table-sm mb-0">
														    <thead>
														        <tr>
														            <th scope="col">버전</th>
														            <th scope="col">문서 건수</th>
														            <th scope="col">매칭 건수</th>
														            <th scope="col">매칭률</th>
														            <th scope="col">룰 매칭 건수</th>
														            <th scope="col">분류 매칭 건수</th>
														            <th scope="col">실패 건수</th>
														            <th scope="col">분석시작일시</th>
														            <th scope="col">분석시간</th>
														            <th scope="col">데이터 상세보기</th>
														        </tr>
														    </thead>
														    <tbody id="bulkSimulationSummaryList">
														    </tbody>
														</table>
												    </div>
												</div>
												<div class="row">
			                                        <div class="col-sm-12 col-md-5">
			                                                <strong> 총 <span id="summaryTotalCnt">0</span>건</strong>
			                                        </div>
			
			                                        <div class="col-sm-12 col-md-7" >
			                                           <div class="dataTables_paginate paging_simple_numbers" id="summary_zero_config_paginate">
			                                               <ul class="pagination justify-content-end" id="summaryPagination"  >
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
					</div>
                </div>
                <input type="hidden" name="summaryPageNo" id="summaryPageNo" value="1">
                <input type="hidden" name="historyPageNo" id="historyPageNo" value="1">
                <input type="hidden" id="taskId" name="taskId" value="">
                <input type="hidden" id="historyTaskId" name="historyTaskId" value="">
                <div class="row" id="bulkSimulationListArea" style="display:none;">
					<div class="col-12">
                        <div class="card">
                            <div class="card-body">
                            	<div class="row">
	                                <div class="col-md-6 mb-2">
	                                    <h5 class="card-title m-b-0"><i class="m-r-10 mdi mdi-code-greater-than"></i> 시뮬레이션 이력</h5>
	                                </div>
                                    <div class="row mb-5">
									    <div class="col-md-3">
									        <label class="control-label">목록수</label>
									        <select class="form-select" id="lineNo" name="lineNo" onchange="getChangeItem();">
									            <option value="10">10</option>
									            <option value="20">20</option>
									            <option value="30">30</option>
									            <option value="50">50</option>
									        </select>
										</div>
									    <div class="col-md-3">
									        <label class="control-label">분석결과</label>
									        <select class="form-select" id="resultType" name="resultType" onchange="getChangeItem();">
									            <option value="">전체</option>
									            <option value="matched">분류</option>
									            <option value="not_matched">미분류</option>
									        </select>
										</div>
									    <div class="col-md-3">
									        <label class="control-label">매칭타입</label>
									        <select class="form-select" id="matchedType" name="matchedType" onchange="getChangeItem();">
									            <option value="">전체</option>
									            <option value="rule">룰 매칭</option>
									            <option value="classify">분류 매칭</option>
									        </select>
										</div>
									</div>
	                                <div class="row">
	                                     <div class="col-sm-12 col-md-12">
	                                        <table id="zero_config" class="table">
	                                         <thead class="thead-light">
	                                                <tr>
	                                                    <th scope="col">이력일</th>
	                                                    <th scope="col">문서</th>
	                                                    <th scope="col">분석결과</th>
	                                                    <th scope="col">매칭타입</th>
	                                                    <th scope="col">카테고리</th>
	                                                    <th scope="col">상세보기</th>
	                                                </tr>
	                                                </thead>
	                                                <tbody id="bulkSimulationHistoryList">
	                                                </tbody>
	                                        </table>
	                                     </div>
	                                </div>
	                                <div class="row">
	                                    <div class="col-sm-12 col-md-5">
	                                    	<strong> 총 <span id="historyTotalCnt">0</span>건</strong>
	                                    </div>
	                                    <div class="col-sm-12 col-md-7" >
	                                       <div class="dataTables_paginate paging_simple_numbers" id="history_zero_config_paginate">
	                                           <ul class="pagination justify-content-end" id="historyPagination"  >
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
			

<!-- jsonview 모달 -->
<div class="modal fade" id="viewJsonModal" tabindex="-1" data-bs-backdrop="static" role="dialog" aria-labelledby="viewJsonModal">
     <div class="modal-dialog modal-lg" role="document">
         <div class="modal-content">
             <div class="modal-header d-flex align-items-center">
                 <h5 class="modal-title">JSON </h5>
                 <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
             </div>
             <div class="modal-body">
             	<div class="col-md-12 mt-1" style="min-height: 460px;">
	               	<ul class="nav nav-tabs" role="tablist">
	   					<li class="nav-item">
	       					<a class="nav-link active" data-bs-toggle="tab" href="#modalResponse" role="tab">
	           					<span>Response</span>
	       					</a>
	   					</li>
					</ul>
					<div class="tab-content" style="min-height:200px;">
	   					<div class="tab-pane active" id="modalResponse" role="tabpanel">
	    					<pre id="modelApiResult" style="font-size: 15px;"></pre>
	   					</div>
					</div>
			    </div>
             </div>
			<div class="modal-footer">
      			<button type="button" class="btn btn-light-danger text-danger font-weight-medium" data-bs-dismiss="modal">닫기</button>
			</div>
		</div>
     </div>
</div>
<form id="frmDownload" name="frmDownload" action="" method="post">
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
   	<input type="hidden" id="serviceId" name="serviceId" value="bulkSimulation">
</form>

<%@include file="/WEB-INF/views/common/footer.jsp"%>
<script src="${CTX_PATH}/static/js/json-path-picker/json-path-picker.js"></script>
<script src="${CTX_PATH}/static/package/assets/libs/bootstrap-touchspin/dist/jquery.bootstrap-touchspin.min.js"></script>

<!-- 정보 Template Start   -->
<!-- 요약 정보 리스트 출력  -->
<script id="summaryListRowTmpl" type="text/x-jquery-tmpl">
    {{each(i, gUList) summaryList}}
        <tr id="listRow\${i}">
            <td>\${gUList.version}</td>
            <td>\${numberWithCommas(gUList.dataCnt)} 건</td>
			<td>\${numberWithCommas(gUList.matchedDataCnt)} 건</td>
			<td>\${gUList.matchedRate}</td>
			<td>\${numberWithCommas(gUList.ruleMatchedDataCnt)} 건</td>
			<td>\${numberWithCommas(gUList.classifyMatchedDataCnt)} 건</td>
			<td>\${numberWithCommas(gUList.failedCnt)} 건</td>
            <td>\${GetDateMMDDSS(gUList.runStartDate)}</td>
            <td>\${GetInterval(gUList.runtime)}</td>
			<td><a href="#" class="btn btn-danger btn-sm" onclick="setHistoryTaskId('\${gUList.taskId}');" >이력상세보기</a></td>
        </tr>
    {{/each}}
</script>
<!-- 상세 리스트 출력  -->
<script id="historyListRowTmpl" type="text/x-jquery-tmpl">
    {{each(i, gUList) historyList}}
        <tr id="listRow\${i}">
            <td>\${GetDateMMDDSS(gUList.createDate)}</td>
            <td style="max-width:500px;">\${gUList.convertDoc}</td>
            <td>{{if gUList.resultType =='matched'}}분류{{/if}}{{if gUList.resultType =='not_matched'}}미분류{{/if}}{{if gUList.resultType =='error'}}에러{{/if}}</td>
            <td>{{if gUList.resultType !='matched'}}-{{/if}}{{if gUList.matchedType =='rule'}}룰 매칭{{/if}}{{if gUList.matchedType =='classify'}}분류 매칭{{/if}}</td>
			<td>{{if gUList.resultType =='matched'}}\${gUList.matchedCategory}{{/if}}{{if gUList.resultType !='matched'}}-{{/if}}</td>
			<td><a href="#" id="view\${i}" onclick="viewJson('\${i}');" data-bs-toggle="modal" data-bs-target="#viewJsonModal" class="btn btn-danger btn-sm" >보기</a></td>
        </tr>
		<input type="hidden" id="analResult\${i}" value="\${gUList.analResult}"/>
    {{/each}}
</script>

<script type="text/javascript">

//화면 로딩 처리 함수 
$(document).ready(function(){
	
	$("#mThreshold").TouchSpin({
		  min: 50,
		  max: 100,
		  step: 1,
		  boostat: 5,
		  maxboostedstep: 10,
		  postfix: "%",
		  initval: $("#threshold").val()
		});
	
	$('#templateDownloadBtn').click(function(){
		excelTemplateDownload();
	});
	
	// 시뮬레이션 실행
	$('#bulkSimulationBtn').click(function(){
		if(bulkSimulationValidation()){
			doBulkSimulation();			
		}
	});
	
	// 시뮬레이션 실행
	$('#singleSimulationBtn').click(function(){
		if(singleSimulationValidation()){
			doSingleSimulation();			
		}
	});
	
	summaryList();
});

function setSingleSimulation() {
	$('#taskId').val('');
	$('#bulkSimulationListArea').hide();
	$('#bulkSimulationHistoryList').html('');
	$('#singleSuccess').hide();
	$('#singleError').hide();
	$('#apiResult').html('');
	$('#rst_tagSentence').val('');
	$('#rst_resultType').val('');
	$('#rst_matchedType').val('');
	$('#rst_matchedCategory').val('');
	
	if(timerId != null){
		clearInterval(timerId);
		timerId = null;		
	}
}

function setBulkSimulation() {
	var param = {}; 
	var listBack = function(data) {
		var taskIdList = data.result;
		console.log(taskIdList);
		var taskId = '';
		$.each(taskIdList, function(index,item) {
			taskId = item;
		});
		if(taskId != ''){
			$('#taskId').val(taskId);
			$('#bulkSimulationProgressPercent').html('0%');
			$('#bulkSimulationProgressPercent').width('0%');
			$('#bulkSimulationProgressArea').show();
			$('#bulkSimulationBtn').attr("disabled", true);
			timerId = setInterval(uploadStepAjax,1000);			
		}
	};
	
	callAjax("/common/simulationTaskIdListAjax.ps", param, listBack);
}

function terminateBulkSimulation() {
	var param = {
		taskId : $('#taskId').val()
	}; 
	
	Swal.fire({
		title: "시뮬레이션",
		text: '진행중인 시뮬레이션을 중지 하시겠습니까?',
		type: 'warning', 
		showCancelButton: true,
	    confirmButtonColor: 'btn-info',
	    cancelButtonColor: 'btn-danger',
		confirmButtonText: '네', 
		cancelButtonText: '아니요'
	}).then((result) => { 
		if(result.value){
			callAjax("/common/terminateBulkSimulationAjax.ps", param, listBack);
		} else {
			return;
		}
	});
	
	var listBack = function(data) {
		var ret = data.result;
		if(ret){
			Swal.fire({
				type: "warning",
				text: "시뮬레이션 중지 완료.",
			  });
			$('#bulkSimulationProgressLabel').html('시뮬레이션 진행 중지');
			if(timerId != null){
				clearInterval(timerId);
				timerId = null;		
			}
			setTimeout(function() {
				  summaryList();
			}, 3000);
		} else {
			Swal.fire({
				type: "error",
				text: "시뮬레이션 중지를 실패 하였습니다.",
			  });
		}
	};
}

//단건 시뮬레이션 
function doSingleSimulation(){
	var doc = $('#singleDocument').val();
	var version = $('#singleModelVersion').val();
	
	$('#singleSuccess').hide();
	$('#singleError').hide();
	$('#apiResult').html('');
	
	var param = { 
			query : doc
            , version : version
           }; 
	var listBack = function(data) {
		console.log(data);
		if ( data && data.success ) {
			if(data.apiResult.status.code == '200'){
				var resultType = data.apiResult.analysisResult.resultType;
				var matchedType = data.apiResult.analysisResult.matchedType;
				var tagSentence = data.apiResult.analysisResult.tagSentence;
				if(resultType == 'matched'){
					$('#rst_resultType').val('분류');
					if(matchedType == 'rule'){
						$('#rst_matchedType').val('룰 매칭');
						$('#rst_matchedCategory').val(data.apiResult.analysisResult.matchedCategory);
					} else {
						$('#rst_matchedType').val('분류 매칭');
						$('#rst_matchedCategory').val(data.apiResult.analysisResult.matchedCategory);
					}
				} else {
					$('#rst_resultType').val('미분류');
					$('#rst_matchedType').val('--');
					$('#rst_matchedCategory').val('--');
				}
				$('#rst_tagSentence').val(tagSentence);
				renderJson('apiResult',data.apiResult);
			} else {
				$('#singleError').show();
				$('#apiResult').html(data.msg);
			}
		} else{
			// API 요청 실패
			$('#singleError').show();
			$('#apiResult').html(data.msg);
		}
	};
	
	callAjax("/learning/simulationAjax.ps", param, listBack);
}

// 단건 시뮬레이션 파라미터 체크
function singleSimulationValidation() {
	var result = true;
	
	var doc = $('#singleDocument').val();
	var version = $('#singleModelVersion').val();
	
	if(typeof doc == "undefined" || doc == null || doc.trim() == "" ){
		Swal.fire({
			type: "warning",
			text: "문서 내용을 입력해 주세요.",
		  });
		result = false;
		return result;
	}
	if(typeof version == "undefined" || version == null || version == ""){
		Swal.fire({
			type: "warning",
			text: "학습버전을 선택해 주세요.",
		  });
		result = false;
		return result;
	}
	
	return result;
}

function onChangeFile(){
	$('#bulkSimulationBtn').attr("disabled", false);
}

// 최근 시뮬레이션 목록
function summaryList(pageNo){
	pageNo = pageNo || 1;
    $('#summaryPageNo').val(pageNo);
	var param = { 
			pageNo : pageNo
            , lineNo : '5'
           }; 
	var listBack = function(data) {
		$('#bulkSimulationProgressArea').hide();
		$('#bulkSimulationListArea').hide();
        var paginationHtml = getPaginationHtml(pageNo, data.totalCnt, '5', '10', 'summaryList');
		$('#bulkSimulationSummaryList').html('');
		$("#summaryListRowTmpl").tmpl(data).appendTo("#bulkSimulationSummaryList");
        $("#summaryTotalCnt").html(data.totalCnt);
        $("#summaryPagination").html( paginationHtml );   
	};
	
	callAjax("/learning/bulkSummaryAjax.ps", param, listBack);
}

function setHistoryTaskId(taskId){
	$('#historyTaskId').val(taskId);
	historyList('1');
}


function historyList(pageNo){
	pageNo = pageNo || 1;
    $('#historyPageNo').val(pageNo);
	var param = { 
			pageNo : pageNo
			, taskId : $('#historyTaskId').val()
            , lineNo : '10'
           }; 
	var listBack = function(data) {
		console.log(data);
        $('#bulkSimulationListArea').show();
        var paginationHtml = getPaginationHtml(pageNo, data.totalCnt, '10', '10', 'historyList');
		$('#bulkSimulationHistoryList').html('');
		$("#historyListRowTmpl").tmpl(data).appendTo("#bulkSimulationHistoryList");
        $("#historyTotalCnt").html(data.totalCnt);
        $("#historyPagination").html( paginationHtml );   
	};
	
	callAjax("/learning/bulkHistoryAjax.ps", param, listBack);
}

/*
 * 벌크 시뮬레이션 파일 업로드 function start
 */
//파라미터 체크
function bulkSimulationValidation() {
	var result = true;
	
	var path = $('#fileUpload').val();
	var version = $('#bulkModelVersion').val();
	
	if(path.indexOf('.xls') < 0 && path.indexOf('.xlsx') < 0) {
		 Swal.fire({
		    	type: "warning",
		    	text: "엑셀 파일을 선택해 주세요.",
		    });
		result = false;
		return result;
	}
	if(typeof version == "undefined" || version == null || version == ""){
		Swal.fire({
			type: "warning",
			text: "학습버전을 선택해 주세요.",
		  });
		result = false;
		return result;
	}
	return result;
}

var timerId = null;
function doBulkSimulation() {
	Swal.fire({
		title: "시뮬레이션",
		text: '시뮬레이션을 실행 하시겠습니까?',
		type: 'warning', 
		showCancelButton: true,
	    confirmButtonColor: 'btn-info',
	    cancelButtonColor: 'btn-danger',
		confirmButtonText: '실행', 
		cancelButtonText: '취소'
	}).then((result) => { 
		if(result.value){
			$('#bulkSimulationProgressPercent').html('0%');
			$('#bulkSimulationProgressPercent').width('0%');
			$('#bulkSimulationProgressArea').show();
			$('#bulkSimulationBtn').attr("disabled", true);
			var param = new FormData($('#frmUpload')[0]);
			callAjaxUpload("/common/bulkSimulation.ps?_csrf=${csrf}", param, listBack);
		} else {
			return;
		}
	});
 
	var listBack = function(data) {
		if(data.result.status == 'success'){
			var taskId = data.result.taskId;
			$('#taskId').val(taskId);
			timerId = setInterval(uploadStepAjax,1000);
		} else {
			Swal.fire({
				type: "error",
				title: "시스템 오류",
				text: data.result.errorMessage,
			  });
		}
	};
}

function uploadStepAjax(){
	var param = {
			taskId : $('#taskId').val()
	};
	var serviceBack = function(data) {
		if(data.result.status == 'success'){
			$('#bulkSimulationProgressPercent').html(data.result.progress + '%');
			$('#bulkSimulationProgressPercent').width(data.result.progress + '%');
			
			if(data.result.simulationStep == 'fileValidation') {	
				$('#bulkSimulationProgressLabel').html('업로드 파일 검증');
			} else if(data.result.simulationStep == 'readFile') {
				$('#bulkSimulationProgressLabel').html('파일 읽기');
			} else if(data.result.simulationStep == 'createThread') {
				$('#bulkSimulationProgressLabel').html('스레드 요청 대기');
			} else if(data.result.simulationStep == 'simulation') {
				$('#bulkSimulationProgressLabel').html('시뮬레이션 진행');
			} else if(data.result.simulationStep == 'importData') {
				$('#bulkSimulationProgressLabel').html('결과 처리');
			}
			
			if(data.result.taskComplete){
				$('#bulkSimulationProgressLabel').html('시뮬레이션 완료');
				clearInterval(timerId);
				timerId = null;
				setTimeout(function() {
					  summaryList();
				}, 3000);
			}
		} else {
			clearInterval(timerId);
			timerId = null;
			Swal.fire({
				type: "error",
				title: "시뮬레이션 오류",
				text: data.result.errorMessage,
			  });
			setTimeout(function() {
				  summaryList();
			}, 3000);
		}
    }; 
	
    callAjax("/common/simulationProgressAjax.ps", param, serviceBack);
}

function viewJson(id){
	$('#modalResponse').text('');
	var response = JSON.parse(replaceAll( $('#analResult'+id).val(),'\n','\\n'));
	renderJson('modalResponse',response);
}

function renderJson(target,json) {
	try {
		var input = json;
     	var options = {
     			pathNotation: 'brackets',
     		    pathQuotesType: 'single',
     		    outputWithQuotes: true,
     		   	collapsed: false,
       			rootCollapsable: false,
       			picker: false
      	};
      	$('#'+target).jsonPathPicker(input, options, null);
    } catch (error) {
    	console.log('[renderJson] Cannot eval JSON: ' + error);
    	renderText(target,json);
    }
}

//템플릿 다운로드 실행
function excelTemplateDownload() {
	$('#frmDownload').attr('action','${CTX_PATH}/common/excelTemplateDownload.ps').submit();
}
</script>

<%@include file="/WEB-INF/views/common/end.jsp"%>