<%@include file="/WEB-INF/views/common/header.jsp"%>
<%@include file="/WEB-INF/views/common/menu.jsp"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<link rel="stylesheet" type="text/css" href="${CTX_PATH}/static/package/assets/libs/select2/dist/css/select2.min.css" />
<link href="${CTX_PATH}/static/package/assets/libs/jquery-steps/jquery.steps.css" rel="stylesheet" type="text/css" />
<link href="${CTX_PATH}/static/package/assets/libs/jquery-steps/steps.css" rel="stylesheet" type="text/css" />

<c:set var="CTX_PATH" value="${pageContext.request.contextPath}" scope="request"/>
<style type="text/css">
.empty-space {
    padding-left: 15px;
}
.select2-search__field:placeholder-shown {
	    width: 200% !important; /*makes the placeholder to be 100% of the width while there are no options selected*/
}
.select2-selection--single, .select2-selection__rendered, .select2-selection__placeholder, .select2-selection__arrow  {
/* 	  height: 35px !important; */
	  line-height : 33px !important;
}
</style>                
<!-- ============================================================== -->
<!-- Bread crumb and right sidebar toggle -->
<!-- ============================================================== -->
<div class="page-breadcrumb border-bottom">
	<div class="row">
		<div class="col-lg-3 col-md-4 col-xs-12 justify-content-start d-flex align-items-center">
			<h5 class="font-weight-medium text-uppercase mb-0">분류 데이터</h5>
		</div>
		<div class="col-lg-9 col-md-8 col-xs-12 d-flex justify-content-start justify-content-md-end align-self-center">
			<nav aria-label="breadcrumb" class="mt-2">
				<ol class="breadcrumb mb-0 p-0">
					<li class="breadcrumb-item"><a href="#">자동분류관리</a></li>
					<li class="breadcrumb-item active" aria-current="page">분류 데이터</li>
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
		<div class="col-md-3">
			<div class="card">
				<div class="card-body">
					<div class="row">
						<div class="col-md-6">
							<h5 class="card-title m-b-0"><i class="m-r-10 mdi mdi-code-greater-than"></i> 카테고리</h5>
						</div>
						<div class="row">
                			<div class="col-md-12">
                       			<div class="input-group mb-2">
       								<input type="text" class="form-control" id="input-search"  placeholder="검색 할 단어를 입력하세요." onkeypress="if(event.keyCode==13) {cateSrch(); return false;}" >
    								<button class="btn btn-light-secondary text-secondary font-weight-medium" id="btn-search" type="button"><i data-feather="search" class="feather-sm fill-white"></i></button>
								</div>
                    			<div id="treeview-searchable"></div>
                    		</div>
                   		</div>
					</div>
				</div>
			</div>
		</div>
		<div class="col-md-9">
			<div class="card">
				<div class="card-body">
					<div class="row">
						<div class="col-md-6">
							<h5 class="card-title m-b-0"><i class="m-r-10 mdi mdi-code-greater-than"></i> 분류 데이터 목록</h5>
						</div>
						<div class="col-md-6 text-end">
                        	<a href="#" id="upload" class="btn btn-primary"><i data-feather="upload" class="feather-sm fill-white me-1"></i>일괄 업로드</a>
                            <a href="#" id="download" class="btn btn-primary"><i data-feather="download" class="feather-sm fill-white me-1"></i>일괄 다운로드</a>
					    </div>
						<!-- 목록 start -->
						<input type="hidden" name="pageNo" id="pageNo" value="1">
						<input type="hidden" name="lineNo" id="lineNo" value="5">
                                
                                <div class="card" style="margin-top:10px;margin-bottom:15px;">
                                	<div style="margin-bottom:10px;">
										<div class="alert alert-info bg-info text-white border-0" role="alert">
										    <strong id="fullItem">● 카테고리를 먼저 선택해 주세요.</strong>
										</div>
										<small>카테고리 별 <b>분류 데이터</b>를 기반으로 학습을 진행하기 위한 대상 데이터를 관리합니다.</small>
									</div>
	                             	<div id="addDataBtnArea" class="p-2 border-top" style="display:none;">
					              		<div class="row justify-content-center" style="margin-top:10px;">
											<div class="col-md-12 col-lg-6 d-grid gap-2">
										    	<button type="button" id="addDataBtn" class="btn waves-effect waves-light btn-outline-info" onclick="openDataActionNewBox();">분류 데이터 추가</button>
										    </div>
										</div>
					            	</div>
					            	<div id="categoryNotifyArea">	            	
					              		<div class="alert customize-alert  alert-dismissible border-secondary text-secondary fade show" role="alert">
    										<h4 class="alert-heading">참고사항</h4>
										    <p>선택한 카테고리에 대한 분류 데이터의 목록을 확인 및 편집 할 수 있습니다. </p>
										    <hr>
										    <p class="mb-0">등록 된 카테고리가 없을 경우 자동분류관리 > 카테고리 메뉴로 이동하여 카테고리를 편집 작업을 선행 해주세요. </p>
										</div>
					            	</div>
					            	<div id="noDataNotifyArea" style="display:none;margin-top:10px;">	            	
					              		<div class="alert customize-alert  alert-dismissible border-secondary text-secondary fade show" role="alert">
    										<h4 class="alert-heading">선택된 카테고리에 등록되어 있는 분류 데이터가 없습니다.</h4>
    										<hr>
										    <p class="mb-0">상단에 '분류 데이터 추가' 버튼을 클릭하여 편집해 주세요.</p>
										</div>
					            	</div>
								</div>
								
                                <div class="row" id="addDataActionRootArea" style="display:none;">
               						<div class="card">
										<div class="card-body" style="background-color: rgba(0,0,0,.02);">
											<div class="row" >
								                <div class="col-md-12 mb-3">
								                	<div>
				                                        <button class="btn btn-outline-info" type="button" onclick="javascript:saveProc();">
				                                        	<i data-feather="save" class="feather-sm fill-white"></i> 분류 데이터 저장
				                                        </button>
				                                        <button class="btn btn-outline-danger" type="button" onclick="javascript:exitEdit();">
				                                            <i data-feather="x" class="feather-sm"></i> 편집 취소
				                                        </button>
			                                        </div>
								                </div>
							                </div>
									        <form class="form-material mt-4">
									            <div class="mb-3">
									            	<label>문서</label>
									                <textarea id="newDocument" class="form-control" rows="5" style="min-height:110px;" placeholder="내용을 입력해 주세요."></textarea>
									            </div>
									        </form>
									    </div>
									</div>         
			                	</div>
			                	
			                	<div id="dataListArea">
			                	</div> 

                                <div class="row">
                                	<div class="col-sm-12 col-md-5">
                                    	<strong> 총 <span id="totalCnt">0</span>건</strong>
                                    </div>
                                    <div class="col-sm-12 col-md-7" >
                                       <div class="dataTables_paginate paging_simple_numbers" id="zero_config_paginate">
                                           <ul class="pagination justify-content-end" id="dataPagination"  >
                                           </ul>
                                       </div>
                                    </div>
                                </div>
						<!-- 목록 end -->
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<input type="hidden" name="editDataNo" id="editDataNo" value="0">
<form id="modifyForm" name="modifyForm" method="post" style="display: none;">
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
    <input type="hidden" name="dataNo" id="dataNo" value="" />
</form>
<%@include file="/WEB-INF/views/common/footer.jsp"%>
<script src="${CTX_PATH}/static/package/assets/libs/select2/dist/js/select2.full.min.js"></script>
<script src="${CTX_PATH}/static/package/assets/libs/select2/dist/js/select2.min.js"></script>
<script src="${CTX_PATH}/static/package/assets/libs/jquery-steps/build/jquery.custom.steps.js"></script>

<!-- 데이터 리스트 정보 출력  -->
<script id="dataListRowTmpl" type="text/x-jquery-tmpl">
	{{each(i, gUList) dataList}}
		<div class="row" id="dataListRow\${gUList.dataNo}">
			<div class="card">
				<div class="card-body" style="background-color: rgba(0,0,0,.02);">
					<div class="row" >
						<div class="col-md-12 mb-3">
							<div id="dataEditMode\${gUList.dataNo}" style="display:none;">
								<button class="btn btn-outline-info" type="button" onclick="javascript:saveProc();">
									<i data-feather="save" class="feather-sm fill-white"></i> 분류 데이터 저장
								</button>
								<button class="btn btn-outline-danger" type="button" onclick="javascript:deleteData('\${gUList.dataNo}');">
									<i data-feather="trash-2" class="feather-sm fill-white"></i> 분류 데이터 삭제
								</button>
								<button class="btn btn-outline-danger" type="button" onclick="javascript:exitEdit();">
									<i data-feather="x" class="feather-sm"></i> 편집 취소
								</button>
							</div>
							<div id="dataListMode\${gUList.dataNo}">
								<button class="btn btn-outline-secondary" type="button" onclick="javascript:startEdit('\${gUList.dataNo}');">
									<i class="fas fa-cog"></i> 편집
								</button>
							</div>
						</div>
					</div>
					<form class="form-material mt-4">
						<div class="mb-3">
							<label>문서</label>
							<textarea id="document\${gUList.dataNo}" class="form-control" rows="5" style="min-height:50px;" disabled>\${document}</textarea>
						</div>
					</form>
				</div>
			</div>   
		</div>
	{{/each}}
</script>

<!-- 업로드 모달 -->
<div class="modal fade" id="uploadModal" tabindex="-1" data-bs-backdrop="static" role="dialog" aria-labelledby="uploadLabel">
     <div class="modal-dialog modal-lg" role="document">
         <div class="modal-content">
             <div class="modal-header d-flex align-items-center">
                 <h5 class="modal-title">일괄 업로드 </h5>
                 <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
             </div>
             <div class="modal-body" style="margin-bottom:-20px;">
                 <form id="frmUpload" name="frmUpload" action="" method="post" enctype="multipart/form-data">
                     <div class="mb-3" style="padding-left:20px;padding-right:20px;padding-bottom:10px;">
                     	<label for="fileUpload" class="control-label">업로드 파일 선택</label>
                    	<input class="form-control" type="file" onchange="onChangeFile();" id="fileUpload" name="fileUpload" accept=".xls,.xlsx">
                    	<input type="hidden" id="serviceId" name="serviceId" value="classifyData">
		                <div style="margin-top:5px;"><small><code>엑셀파일</code>을 선택한 후에 업로드 실행 버튼을 클릭 해 주세요.</small></div>
		                <div style="margin-top:5px;"><small>&#8251; <code>룰</code>을 기준으로 업로드가 진행됩니다.</small></div>
                     </div>
                 </form>
                 
                 <div class="row justify-content-center">
					<div class="col-md-4 col-lg-3 d-grid gap-2">
				    	<button type="button" id="templateDownloadBtn" class="btn waves-effect waves-light btn-outline-info">템플릿 다운로드</button>
				    </div>
				    <div class="col-md-4 col-lg-3 d-grid gap-2">
				    	<button type="button" id="uploadBtn" class="btn waves-effect waves-light btn-outline-success">업로드 실행</button>
				    </div>
				    <div class="col-md-4 col-lg-3 d-grid gap-2">
				    	<button type="button" id="reportDownloadBtn" class="btn waves-effect waves-light btn-outline-danger">에러 리포팅 다운로드</button>
				    </div>
				</div>
				               
                <div class="card-body wizard-content" style="width:760px;display:block;">
                 	<h6 class="card-subtitle mb-3"></h6>
                 	<form action="#" class="tab-wizard wizard-circle">
                    	<!-- Step 1 -->
                    	<h6>파일검증</h6>
                    	<section>
                    	</section>
                    	<!-- Step 2 -->
                    	<h6>데이터검증</h6>
                    	<section>
                    	</section>
                    	<!-- Step 3 -->
                    	<h6>데이터적재</h6>
                    	<section>
                    	</section>
                    	<!-- Step 4 -->
                    	<h6>리포팅</h6>
                    	<section>
                    	</section>
						<!-- Step 5 -->
                    	<h6>완료</h6>
                    	<section>
                    	</section>
                    	<div class="row">
                       		<div id="uploadSuccess" style="display:block;"><h3><span class="badge bg-success">Success</span></h3></div>
					    	<div id="uploadError" style="display:block;"><h3><span class="badge bg-danger">Error</span></h3></div>
					    	<pre id="uploadResult" style="font-size: 15px;"></pre>
                     	</div>
                  	</form>
                </div> 
             </div>
             
             <div class="modal-footer">
             	<button type="button" id="uploadModalCloseBtn" class="btn btn-light-danger text-danger font-weight-medium">닫기</button>
             </div>
         </div>
     </div>
</div>
<form id="frmDownload" name="frmDownload" action="" method="post">
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
   	<input type="hidden" id="serviceId" name="serviceId" value="classifyData">
   	<input type="hidden" id="taskId" name="taskId" value="">
</form>

<!--This page JavaScript -->
<script src="${CTX_PATH}/static/package/assets/extra-libs/treeview/dist/bootstrap-treeview.min.js"></script>
<script type="text/javascript">
var snapshotObj;
var categoryArr = '';
var curCategoryNo = 0;
var curCategoryNodeId = 0;
var $searchableTree;
var cateSrch = function (e) {
	var pattern = $("#input-search").val();
	var options = {
	  ignoreCase: true,
	  exactMatch: false,
	  revealResults: true,
	};
	var results = $searchableTree.treeview("search", [pattern, options]);
};

//select2 multiple의 순서 지키기
$("select").on("select2:select", function (evt) {

  $('.select2-search--inline').css('width', '');
  $('.select2-search__field').css('width', '');
  var element = evt.params.data.element;
  var $element = $(element);
  
  $element.detach();
  $(this).append($element);
  $(this).trigger("change");
});

//화면 로딩 처리 함수 
$(document).ready(function(){
	
	categoryList();
	
	//카테고리 검색버튼
	$("#btn-search").on("click", cateSrch);
	$("#input-search").on("keyup", function (e) {
		if($(this).val() == ''){
			$searchableTree.treeview("clearSearch");	
		}
	});
	
	uploadStepInit();
	//템플릿 다운로드
	$('#templateDownloadBtn').click(function(){
		console.log('templateDownloadBtn clicked!!');
		$('#templateDownloadBtn').attr("disabled", true);
		excelTemplateDownload();
	});
	
	// 업로드 실행
	$('#uploadBtn').click(function(){
		excelUpload();
	});

	// 리포팅 다운로드
	$('#reportDownloadBtn').click(function(){
		$('#reportDownloadBtn').attr("disabled", true);
		excelFeedBackDownload();
	});
	
	$('#uploadModalCloseBtn').click(function(){
		$('#uploadModal').modal('hide');
		dataList(1);
	});
});

// 카테고리 목록
var dataNodeIdArr = new Array();
function categoryList(){	
	var param = {}; 
	var listBack = function(data) {
		$searchableTree = $("#treeview-searchable").treeview({
		    selectedBackColor: "#03a9f3",
		    onhoverColor: "rgba(0, 0, 0, 0.05)",
		    expandIcon: "ti-plus",
		    collapseIcon: "ti-minus",
		    nodeIcon: "fa fa-folder",
		    showTags: true,
		    emptyIcon: "empty-space",
		    data: data.categoryList,
		    onNodeSelected: function (event, node) {
		    	console.log(event);
		    	viewData(event, node, "on");
		    },
		    onNodeUnselected: function (event, node) {
		        //clickNode(event, node, "off");
		    },
		});
		
		var node;
		if(typeof curCategoryNodeId != "undefined" && curCategoryNodeId != null && curCategoryNodeId != 0){
			dataNodeIdArr = new Array();
			node = $searchableTree.treeview('getNode', curCategoryNodeId);
			getParentNodeId(node);
			$searchableTree.treeview('expandNode', [dataNodeIdArr, { silent: true }]);
			$searchableTree.treeview('selectNode', [node.nodeId, { silent: true }]);
		}
	};
	
	callAjax("/classify/categoryAjax.ps", param, listBack);	
}

//상세조회
var categoryIdArr = new Array();
function viewData(event, data, type) {
   	if( type == 'on' ){
   		categoryIdArr = new Array();
		curCategoryNodeId = data.nodeId;
		
   		if(data.categoryNo != 0){
			console.log(data);
   			//getNodeCategoryId(data);
   			//categoryArr = categoryIdArr.join(',')
   			curCategoryNo = data.categoryNo;
   			dataList(1);
   			$('#fullItem').html("● " + data.fullItem);
   			$('#addDataBtnArea').show();
   			$('#categoryNotifyArea').hide();
   		} else {
   			// 전체 선택 시
   			//categoryArr = '';
   			$('#fullItem').html("● 카테고리를 먼저 선택해 주세요.");
   			$('#addDataBtnArea').hide();
   			$('#dataPagination').html('');
   			$("#totalCnt").html('0');
   			$('#dataListArea').html('');
   			$('#categoryNotifyArea').show();
   			$("#noDataNotifyArea").hide();
   		}
   	}
}

// 상위 노드 아이디 추출을 위한 재귀함수
function getParentNodeId(node){
	dataNodeIdArr.push(node.nodeId);
	if(node.parentId == 0){
		return;
	} else {
		var parentNode = $searchableTree.treeview('getNode', node.parentId);
		getParentNodeId(parentNode);
	}
}

// 하위 카테고리 아이디 추출을 위한 재귀함수
function getNodeCategoryId(node) {
	if(typeof node.nodes == "undefined" || node.nodes.length == 0){
		categoryIdArr.push(node.categoryNo);
		return;
	} else {
		node.nodes.forEach(function(element){
			if(typeof element.nodes == "undefined" || element.nodes.length == 0){
				categoryIdArr.push(element.categoryNo);
			} else {
				getNodeCategoryId(element);			
			}
		});		
	}
	categoryIdArr.push(node.categoryNo);
}

// =========================================================================================================================
// 분류 데이터 목록
function dataList(pageNo){
	if(curCategoryNo == 0) {
		return;
	}
	if(typeof pageNo == "undefined" || pageNo == null || pageNo == ""){
		pageNo = 1;	
	}
	$('#pageNo').val(pageNo);
	var param = { 
			pageNo : pageNo
			, lineNo : $('#lineNo').val()
            , categoryNo : curCategoryNo
			}; 
	
    var dataListBack = function(data) {
    	if(pageNo > 1 && data.totalCnt == 0){
    		dataList(1);
    	} else {
			var paginationHtml = getPaginationHtml(pageNo, data.totalCnt, $('#lineNo').val(), '5', 'dataList');
			$('#dataListArea').html('');
			$("#dataListRowTmpl").tmpl(data).appendTo("#dataListArea");
			$("#totalCnt").html(data.totalCnt);
			$("#dataPagination").html( paginationHtml );  
    	}
    	
    	if(data.totalCnt == 0){
    		$("#noDataNotifyArea").show();
    		$('#dataPagination').html('');
    	} else {
    		$("#noDataNotifyArea").hide();
    	}
    	$('#addDataBtn').attr("disabled", false);
	};
	callAjax("/classify/learningDataAjax.ps", param, dataListBack);
}

// 저장 프로세스
function saveProc() {
	if ( ! validation() ) {
		return;
	} else {
		saveData();	
	}
}

function validation() {
	var result = true;
	var dataNo = $("#editDataNo").val();
	var document;
	
	if(dataNo == '0'){
		document = filteringXSS($.trim($('#newDocument').val()));
	} else {
		document = filteringXSS($.trim($('#document' + dataNo).val()))
	}
	if(document == ''){
		Swal.fire({
			type: "warning",
			title: "확인",
			text: "내용을 입력해 주세요.",
		  });
		result = false;
	}	
	return result;
}

function saveData(param) {
	var dataNo = $("#editDataNo").val();
	var param;
	
	if(dataNo == '0'){
		param = {
				categoryNo : curCategoryNo
				, document : filteringXSS($.trim($('#newDocument').val()))
				, action : 'new'
			};
	} else {
		param = {
				dataNo : dataNo
				, categoryNo : curCategoryNo
				, document : filteringXSS($.trim($('#document' + dataNo).val()))
				, action : 'mod'
			};
	}
	
	var serviceBack = function(data) {
		if (data.success ) {
			Swal.fire({
				type: "info",
				title: "저장 완료",
				text: '저장이 완료되었습니다.',
			  }).then((result) => {
				  exitEdit();
				  dataList(1); 
			  });
		}else{
			if ( data && data.msg ) {
				Swal.fire({
					type: "error",
					title: "저장 실패",
					text: data.msg,
				  });
			} else {
				Swal.fire({
					type: "error",
					title: "저장 실패",
					text: "저장이 실패하였습니다.",
				  });
			}
		}
    }; 
	
    callAjaxAsync("/classify/saveLearningData.ps", param, serviceBack);
}

// 삭제
function deleteData(dataNo) {
	var param = { 
			dataNo : dataNo
		};
	var serviceBack = function(data) {
		if (data.success ) {
			dataList($('#pageNo').val());			
		}else{
			if ( data && data.msg ) {
				Swal.fire({
					type: "error",
					title: "삭제 실패",
					text: data.msg,
				  });
			} 
			else {
				Swal.fire({
					type: "error",
					title: "삭제 실패",
					text: "삭제 처리가 실패하였습니다.",
				  });
			}
		}
    };
    
    Swal.fire({
		title: "삭제",
		text: '삭제 하시겠습니까?',
		type: 'warning', 
		showCancelButton: true, 
		confirmButtonColor: '#3085d6', 
		cancelButtonColor: '#d33', 
		confirmButtonText: '삭제', 
		cancelButtonText: '취소'
	  }).then((result) => { 
		  if(result.value){
			  callAjaxAsync("/classify/delLearningData.ps", param, serviceBack);  
		  } else {
			  return;
		  }
	});
}

function openDataActionNewBox(){
	$("#editDataNo").val('0');
	$('#addDataActionRootArea').show();
	$('#addDataBtn').attr("disabled", true);
}

function startEdit(dataNo) {
	if($("div[id^='dataEditMode']").is(':visible') || $('#addDataActionRootArea').is(':visible')){
		Swal.fire({
			type: "warning",
			title: "확인",
			text: "편집중인 분류 데이터가 있습니다. 편집 후에 진행 해주세요.",
		  });
		return;
	}
	$('#addDataBtn').attr("disabled", true);
	
	$("#editDataNo").val(dataNo);
	$('#dataEditMode'+dataNo).show();
	$('#dataListMode'+dataNo).hide();
	
	snapshotObj = new Object();
	snapshotObj.dataNo = dataNo;
	snapshotObj.document = $('#document' + dataNo).val();
	$('#document' + dataNo).attr("disabled", false);
}

function exitEdit() {
	var dataNo = $("#editDataNo").val();
	if(dataNo == '0'){
		// 신규 데이터 생성
		$('#addDataActionRootArea').hide();
		$('#newDocument').val('');
	} else {
		// 기존 데이터 수정
		$('#dataEditMode'+dataNo).hide();
		$('#dataListMode'+dataNo).show();
		
		$('#document' + dataNo).val(snapshotObj.document);
		$('#document' + dataNo).attr("disabled", true);
	}
	$('#addDataBtn').attr("disabled", false);
}

/*
 * 업로드 / 다운로드 관련 function start
 */
function onChangeFile(){
	uploadModalReset();
}

function uploadStepInit(){	
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

function uploadModalReset(){
	$('#uploadBtn').attr("disabled", false);
	$(".tab-wizard").steps('first');
	$(".tab-wizard ul li").each(function(index, item){
		if(index == 0){
			$(this).removeClass();
			$(this).addClass('first').addClass('current');
		} else {
			$(this).removeClass();
			$(this).addClass('disabled');
			if(index == 4){
				$(this).addClass('last');
			}
		}
	});
	
	$('#uploadSuccess').hide();
	$('#uploadError').hide();
	$('#uploadResult').html('');
	$('#reportDownloadBtn').attr("disabled", true);
}

// 일괄 다운로드
$('#download').click(function(){
    excelDownload();
});

// 다운로드 실행
function excelDownload() {
	$('#frmDownload').attr('action','${CTX_PATH}/common/excelDownload.ps').submit();
}

// 템플릿 다운로드 실행
function excelTemplateDownload() {
	$('#frmDownload').attr('action','${CTX_PATH}/common/excelTemplateDownload.ps').submit();
}

// 리포팅 다운로드 실행
function excelFeedBackDownload() {
	$('#frmDownload').attr('action','${CTX_PATH}/common/excelFeedBackDownload.ps').submit();
}

//일괄 업로드
$('#upload').click(function(){
	$('#templateDownloadBtn').attr("disabled", false);
	$('#fileUpload').val('');
	uploadModalReset();
	$('#uploadModal').modal('show');
});

//일괄업로드
var timerId = null;
function excelUpload() {
	var path = $('#fileUpload').val();
	if(path.indexOf('.xls') < 0 && path.indexOf('.xlsx') < 0) {
		 Swal.fire({
		    	type: "warning",
		    	text: "엑셀 파일을 선택해 주세요.",
		    });
		    return;
	}
	Swal.fire({
		title: "업로드",
		text: '업로드 하시겠습니까?',
		type: 'warning', 
		showCancelButton: true,
	    confirmButtonColor: 'btn-info',
	    cancelButtonColor: 'btn-danger',
		confirmButtonText: '업로드', 
		cancelButtonText: '취소'
	}).then((result) => { 
		if(result.value){
			$('#uploadBtn').attr("disabled", true);
			var param = new FormData($('#frmUpload')[0]);
			callAjaxUpload("/common/excelUpload.ps?_csrf=${csrf}", param, listBack);
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
		var currentIndex = $(".tab-wizard").steps('getCurrentIndex');
		console.log(data);
		console.log('currentIndex : ' + currentIndex);
				
		if(data.result.status == 'success'){
			if(data.result.uploadStep == 'fileValidation'){ // index : 0
			} else if(data.result.uploadStep == 'dataValidation'){ // index : 1
				if(currentIndex == 0){$(".tab-wizard").steps('next');}
			} else if(data.result.uploadStep == 'dataImport'){ // index : 2
				if(currentIndex == 0){$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');}
				if(currentIndex == 1){$(".tab-wizard").steps('next');}
			} else if(data.result.uploadStep == 'reporting'){ // index : 3
				if(currentIndex == 0){$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');}
				if(currentIndex == 1){$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');}
				if(currentIndex == 2){$(".tab-wizard").steps('next');}
			} else if(data.result.uploadStep == 'finished'){ // index : 4
				if(currentIndex == 0){$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');}
				if(currentIndex == 1){$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');}
				if(currentIndex == 2){$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');}
				if(currentIndex == 3){$(".tab-wizard").steps('next');}
				$(".tab-wizard").steps('finish');
				clearInterval(timerId);
				$('#uploadSuccess').show();
				var message = '정상 업로드 건수는 ' + data.result.importCount + '건, ';
				if(data.result.failedCount > 0){
					message += '실패한 업로드 건수 ' + data.result.failedCount + '건 입니다.<br>';
					message += '실패한 데이터에대한 피드백은 [에러 리포팅 다운로드] 버튼을 클릭하여 확인해주세요.';
					$('#reportDownloadBtn').attr("disabled", false);					
				} else {
					message += '실패한 업로드 건수는 없습니다.';
				}
				$('#uploadResult').html(message);
			} else {
			}
		} else {
			clearInterval(timerId);
			$('#uploadError').show();
			$('#uploadResult').html(data.result.errorMessage);
			if(data.result.uploadStep == 'fileValidation'){ // index : 0
			} else if(data.result.uploadStep == 'dataValidation'){ // index : 1
				if(currentIndex == 0){$(".tab-wizard").steps('next');}
			} else if(data.result.uploadStep == 'dataImport'){ // index : 2
				if(currentIndex == 0){$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');}
				if(currentIndex == 1){$(".tab-wizard").steps('next');}
			} else if(data.result.uploadStep == 'reporting'){ // index : 3
				if(currentIndex == 0){$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');}
				if(currentIndex == 1){$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');}
				if(currentIndex == 2){$(".tab-wizard").steps('next');}
			} else if(data.result.uploadStep == 'finished'){ // index : 4
				if(currentIndex == 0){$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');}
				if(currentIndex == 1){$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');}
				if(currentIndex == 2){$(".tab-wizard").steps('next');$(".tab-wizard").steps('next');}
				if(currentIndex == 3){$(".tab-wizard").steps('next');}
			} else {
			}
		}
    }; 
	
    callAjaxAsync("/common/uploadStepAjax.ps", param, serviceBack);
}
/*
 * 업로드 / 다운로드 관련 function end
 */

 
function filteringXSS(origin) {
	return origin.replace(/\<|\>|\"|\'|\%|\;|\(|\)|\&|\+|\-/g, "");
}

</script>

<%@include file="/WEB-INF/views/common/end.jsp"%>