<%@include file="/WEB-INF/views/common/header.jsp"%>
<%@include file="/WEB-INF/views/common/menu.jsp"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<link href="${CTX_PATH}/static/package/assets/libs/jquery-steps/jquery.steps.css" rel="stylesheet" type="text/css" />
<link href="${CTX_PATH}/static/package/assets/libs/jquery-steps/steps.css" rel="stylesheet" type="text/css" />
<style type="text/css">
.empty-space {
    padding-left: 15px;
}
</style>

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
				                카테고리
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
				                    <li class="breadcrumb-item"><a href="#">대화관리</a></li>
				                    <li class="breadcrumb-item active" aria-current="page">
				                        카테고리
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
                <form id="listForm" name="listForm" action="" method="post">
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
                            		<div class="row">
		                                <div class="col-md-6">
		                                    <h5 class="card-title m-b-0"><i class="m-r-10 mdi mdi-code-greater-than"></i> 카테고리 상세</h5>
		                                </div>
	                                </div>
	                                <div class="row">
		                                <div class="col-md-12 text-end">
		                                	<a href="#" id="upload" class="btn btn-primary"><i data-feather="upload" class="feather-sm fill-white me-1"></i>일괄 업로드</a>
		                                    <a href="#" id="download" class="btn btn-primary"><i data-feather="download" class="feather-sm fill-white me-1"></i>일괄 다운로드</a>
		                                    <a href="#" id="newAdd" class="btn btn-success">신규등록</a>
										    <a href="#" id="dataDel" class="btn btn-danger">삭제</a>
		                                </div>
	                                </div>
	                                <div class="row">
		                                <div class="col-sm-12 mt-3">
						                    <label for="categoryNm" class="control-label">카테고리 명 <span style="font-size:9px;">2~12자 이하</span></label>
						                    <input type="text" class="form-control" id="categoryNm" data-toggle="tooltip" title="카테고리명을 입력하세요"  placeholder="예) 챗봇기본1"/>
		                                </div>
										 <input type="hidden" class="form-control" id="item" data-toggle="tooltip" title="항목을 선택하세요" readonly/>
										<div class="col-sm-12 mt-3">
					                    <label for="fullItem" class="control-label">전체 항목 <span style="font-size:9px;">신규 추가시 좌측 상위 카테고리 1 or 2 뎁스를 선택하고 신규등록 버튼을 클릭하세요.</span></label>
					                     <select class="form-select" id="fullItem" name="fullItem" onChange='onChangeItem()' disabled>
									     </select>
									     
									      
	                                </div>
	                                	<div class="col-sm-12 mt-3">
						                    <label for="desc" class="control-label">설명</label>
						                    <textarea class="form-control" id="desc" rows="3"></textarea>
					                    </div>
		                                <div class="col-sm-12 d-grid gap-2 mt-3">
						                    <button type="button" class="btn btn-success" id="save">저장</button>
		                                </div>
	                                </div>
	                                <input type="hidden" id="categoryNo" name="categoryNo" value=""/>
	                                <input type="hidden" id="depth" name="depth" value=""/>
	                                <input type="hidden" id="pCategoryNo" name="pCategoryNo" value=""/>
	                                
                                </div>
					        </div>
    					</div>
					</div>
                </div>
                </form>
            </div>
<%@include file="/WEB-INF/views/common/footer.jsp"%>
<script src="${CTX_PATH}/static/package/assets/libs/jquery-steps/build/jquery.custom.steps.js"></script>

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
                    	<input type="hidden" id="serviceId" name="serviceId" value="category">
		                <div style="margin-top:5px;"><small><code>엑셀파일</code>을 선택한 후에 업로드 실행 버튼을 클릭 해 주세요.</small></div>
					    <div style="margin-top:5px;"><small>&#8251; <code>카테고리명</code>을 기준으로 업로드가 진행됩니다. 그러므로 카테고리명은 변경이 안됩니다.</small></div>
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
   	<input type="hidden" id="serviceId" name="serviceId" value="category">
   	<input type="hidden" id="taskId" name="taskId" value="">
</form>

<!--This page JavaScript -->
<script src="${CTX_PATH}/static/package/assets/extra-libs/treeview/dist/bootstrap-treeview.min.js"></script>
<script type="text/javascript">
var add = false;
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

$(document).on("click","#newAdd",function(e) {
	newAdd();
});

$(document).on("click","#dataDel",function(e) {
	existData();
});
$(document).on("click","#save",function(e) {
	if ( ! validation() ) {
		if($("#categoryNo").val()==""){
			add = false;
		}
		return;
	} else {
		checkDupName();		
	}	
});
$("#categoryNm").on("keyup",function(key){
    if(key.keyCode==13) {
    	if ( ! validation() ) {
    		return;
    	} else {
    		checkDupName();		
    	}
    }
});
$("#btn-search").on("click", cateSrch);

$("#input-search").on("keyup", function (e) {
	if($(this).val() == ''){
		$searchableTree.treeview("clearSearch");	
	}
});

$(document).on('keypress', '#categoryNm', function() {
	$('#categoryNm').removeClass('is-invalid');
});

//화면 로딩 처리 함수 
$(document).ready(function(){
	categoryList();
	init();
	
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
		// reload!!
		$('#uploadModal').modal('hide');
		categoryList();
		init();
	});
});

function init(){
	add = false;
	$('#pCategoryNo').val('');
	$('#categoryNo').val('');
	$('#categoryNm').val('');
	$('#desc').val('');
	$('#createUserNm').val('');
	$('#modifyUserNm').val('');
	$('#createDate').val('');
	$('#modifyDate').val('');
	$('#item').val('');
}

//목록
function categoryList(){
	var param = {}; 
	
	var listBack = function(data) {
		$("#listRowTmpl").tmpl(data).appendTo("#fullItem");
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
		    	viewData(event, node, "on");
		      	$('#fullItem').attr("disabled","disabled");
		    },
		    onNodeUnselected: function (event, node) {
		        //clickNode(event, node, "off");
		    },
		    
		});
	};
	
	callAjax("/classify/categoryAjax.ps", param, listBack);
}
function newAdd(){
	if($('#depth').val() == '3'){
		Swal.fire({
			type: "warning",
			title: "카테고리 등록",
			text: "카테고리는 3뎁스까지 지원합니다.",
		  });
		return;
	}
	
	if(!add){
		$('#pCategoryNo').val($('#categoryNo').val());
		add = true;
	}	
	$('#categoryNo').val("");
	$('#categoryNm').val("").removeAttr('readonly');
	$('#desc').val("");
  	$('#createUserNm').val('');
  	$('#modifyUserNm').val('');
  	$('#createDate').val('');
  	$('#modifyDate').val('');
  	$('#categoryNm').focus();
  	$('#fullItem').removeAttr('disabled');
}

//상세조회
function viewData(event, data, type) {
	add = false;
   	if( type == 'on'){
   		$('#categoryNo').val(data.categoryNo);
   		$('#categoryNm').val(data.categoryNm).removeClass('is-invalid');
   		if(data.categoryNo == '0'){
   			$('#categoryNm').attr('readonly',true);
   		}else{
   			$('#categoryNm').removeAttr('readonly');
   		}
   		$('#item').val(data.item);
   		//$('#fullItem').val(data.fullItem);
   		$('#depth').val(data.depth);
   		$('#pCategoryNo').val(data.pCategoryNo);
   		$('#desc').val(data.desc);
   		$('#createUserNm').val(data.createUserNm);
   		$('#modifyUserNm').val(data.modifyUserNm);
   		$('#createDate').val(GetDateMMDDSS(data.createDate));
   		if(typeof data.modifyUserNm != "undefined" && data.modifyUserNm != null && data.modifyUserNm != '')
   			$('#modifyDate').val(GetDateMMDDSS(data.modifyDate));
   		else
   			$('#modifyDate').val('');
		
		$('#fullItem option').each(function(){ 
			console.log(this.value +"/"+ data.categoryNo);
			if (this.value == data.categoryNo) {
				this.selected = true;
			}
		});
   	}
}

function validation() {
    if ( !patternHanEngNumRange.test($('#categoryNm').val()) ) {
        $('#categoryNm').addClass('is-invalid');
        Swal.fire({
			type: "warning",
			text: "카테고리명은 '한글',영문','숫자','_','-','@' 를 포함하여 2~12자까지 입력해주세요.\n특수문자/공백은 제외해주세요.",
		});
        $('#categoryNm').focus();
        return false;
    }else{
    	$('#categoryNm').removeClass('is-invalid');
    }
    
    
	return checkNotEmpty(
			[
				 ["categoryNm" , "서비스라벨ID를 입력해주세요."]
				,["item", "상위 카테고리를 선택해 주세요."]
			]
	);
}

function checkDupName() {
	var result = true;
    var url  = "${CTX_PATH}/classify/checkDupCategoryName.ps";
    var type = "post";
    var depth = 0;
    if($("#categoryNo").val()==""){
    	depth = Number($('#depth').val()) + 1;
    } else {
    	depth = Number($('#depth').val());
    }
    var data = {
    		categoryNo : $('#categoryNo').val()
    		,categoryNm : $('#categoryNm').val()
    		,pCategoryNo : $('#pCategoryNo').val()
    		,depth : depth
    };
    $.ajax({
        url  : url,
        type : type,
        data : data,
        beforeSend: function(xhr){xhr.setRequestHeader(header,token);},
        success : function(data) {
            if ( data && data.isDup ) {
        		Swal.fire({
					type: "error",
					title: "카테고리명 중복",
					text: data.msg,
				  });
            } else {
            	if($("#categoryNo").val()==""){
            		addData();
            	}else{
            		modifyData();
            	}
            }
        },
        error : function(xhr, exMessage) {
        	Swal.fire({
				type: "error",
				title: "시스템 오류",
				text: '시스템 오류가 발생하였습니다.',
			  });
            console.log('시스템 오류가 발생하였습니다.'+ exMessage );
        }
    });
}

//추가
function addData() {
	$('#depth').val(Number($('#depth').val()) + 1);
    
	Swal.fire({
		title: "저장",
		text: '저장 하시겠습니까?',
		type: 'warning', 
		showCancelButton: true,
	        confirmButtonColor: 'btn-info',
	        cancelButtonColor: 'btn-danger',
		confirmButtonText: '저장', 
		cancelButtonText: '취소'
	}).then((result) => { 
		if(result.value){
			callAjax("/classify/saveCategory.ps", param, listBack);
		} else {
			return;
		}
	});
	var param = {
		add : 'Y'
		,categoryNm : $('#categoryNm').val()
		,item : $('#depth').val() == '0' ? $('#categoryNm').val() : $('#item').val()
		,fullItem :  $("#fullItem option:selected").text()

 
		,depth : $('#depth').val() + ''
		,pCategoryNo : $('#pCategoryNo').val()
		,useYn : "y"
		,desc : $('#desc').val()
	}; 
	
	var listBack = function(data) {
		if (data.success ) {
			location.reload();
		}else{
			alert('저장이 실패하였습니다.');
		}
	};
}
//수정
function modifyData( id ) {
	if(id == '0'){
		Swal.fire({
			type: "warning",
			text: "최상위 카테고리는 수정 할 수 없습니다.",
		});
	}
	
	var param = {
		add : 'N'
		,categoryNo : $('#categoryNo').val()
		,categoryNm : $('#categoryNm').val()
		,item : $('#item').val()
		,fullItem : $("#fullItem option:selected").text()
		,depth : $('#depth').val()
		,pCategoryNo : $('#pCategoryNo').val()
		,desc : $('#desc').val()
	}; 

	Swal.fire({
		title: "수정",
		text: '수정 하시겠습니까?',
		type: 'warning', 
		showCancelButton: true,
	        confirmButtonColor: 'btn-info',
	        cancelButtonColor: 'btn-danger',
		confirmButtonText: '수정', 
		cancelButtonText: '취소'
	}).then((result) => { 
		if(result.value){
			callAjax("/classify/saveCategory.ps", param, listBack);
		} else {
			return;
		}
	});
	var listBack = function(data) {
		if (data.success ) {
			location.reload();
		}else{
			alert('저장이 실패하였습니다.');
		}
	};
	
	
}


/*
 * 아이템이 변경 될 경우
 */
function onChangeItem(){
	var categoryNo = $("#fullItem option:selected").val();
	var fullItem = $("#fullItem option:selected").text();
	var count = fullItem.split('>').length - 1;
	$('#pCategoryNo').val(categoryNo); 
	$('#depth').val(count); 
}


//카테고리삭제
function existData() {
	if($('#categoryNo').val() == ""){
		Swal.fire({
			type: "warning",
			text: "카테고리를 선택 해주세요.",
		});
    	return false;
	}
	
	if($('#categoryNo').val() == "0"){
		Swal.fire({
			type: "warning",
			text: "최상위 카테고리는 삭제할 수 없습니다.",
		});
    	return false;
	}
	
	var param = {
		categoryNo : $('#categoryNo').val()
		,depth : $('#depth').val()
	}; 
	
	var listBack = function(data) {
		if (data.dtlCategory.length > 1 ) {
			Swal.fire({
				title: "삭제",
				text: '하위 카테고리까지 삭제 됩니다.\n그래도 삭제 하시겠습니까?',
				type: 'warning', 
				showCancelButton: true, 
			        confirmButtonColor: 'btn-info',
			        cancelButtonColor: 'btn-danger',
				confirmButtonText: '삭제', 
				cancelButtonText: '취소'
			}).then((result) => { 
				if(result.value){
					var delcateList = '';
					$.each(data.dtlCategory, function(index, item){ 
						delcateList += ":" + item.categoryNo;
					});
					delData(delcateList);
				} else {
					return;
				}
			});
		}else{
			Swal.fire({
				title: "삭제",
				text: '선택한 카테고리를 삭제 하시겠습니까?',
				type: 'warning', 
				showCancelButton: true, 
			        confirmButtonColor: 'btn-info',
			        cancelButtonColor: 'btn-danger',
				confirmButtonText: '삭제', 
				cancelButtonText: '취소'
			}).then((result) => { 
				if(result.value){
					delData($('#categoryNo').val());
				} else {
					return;
				}
			});
		}
	};
	
	callAjax("/classify/dtlCategory.ps", param, listBack);
}

//삭제
function delData( id ) {
	var param = {
		categoryNo : id
	}; 
	
	var listBack = function(data) {
		if (data.success ) {
			location.reload();
		}else{
			alert('삭제 처리가 실패하였습니다.');
		}
	};
	
	callAjax("/classify/delCategory.ps", param, listBack);
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

</script>
<script id="listRowTmpl" type="text/x-jquery-tmpl">
    {{each(i, gUList) cateList}} 
		<option value="\${gUList.categoryNo}">\${gUList.fullItem}</option>
		 
    {{/each}}
</script>
<%@include file="/WEB-INF/views/common/end.jsp"%>