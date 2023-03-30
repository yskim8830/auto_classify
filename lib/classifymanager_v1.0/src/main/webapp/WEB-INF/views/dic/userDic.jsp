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
				                사용자 사전 관리
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
				                    <li class="breadcrumb-item"><a href="#">사전관리</a></li>
				                    <li class="breadcrumb-item active" aria-current="page">
				                        사용자 사전 관리
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
                    <input type="hidden" name="pageNo" id="pageNo" value="1">
                    <div class="row">
                        <div class="col-12">
                            <div class="card">
                                <div class="card-body">
                                    <div class="row">
	                                    <div class="col-md-6">
	                                        <h5 class="card-title m-b-0"><i class="m-r-10 mdi mdi-code-greater-than"></i> 사용자 사전 관리</h5>
	                                    </div>
                                    </div>
                                    <div class="row">
                                    	<div class="col-md-12 text-end">
                                    		<div class="mb-3">
		                                        <a href="#" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#reflectionModal">서비스 반영</a>
		                                        <a href="#" id="upload" class="btn btn-primary"><i data-feather="upload" class="feather-sm fill-white me-1"></i>일괄 업로드</a>
		                                    	<a href="#" id="download" class="btn btn-primary"><i data-feather="download" class="feather-sm fill-white me-1"></i>일괄 다운로드</a>
										        <a href="#" onclick="javascript:newAdd();" class="btn btn-success" data-bs-toggle="modal" data-bs-target="#newAdd">신규등록</a>
										        <a href="#" id="dicDel" class="btn btn-danger">삭제</a>
	                                        </div>
	                                    </div>
	                               </div>
                                    <div style="height:10px;">
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
									        <label class="control-label">정렬</label>
									        <select class="form-select" id="sortType" name="sortType" onchange="getChangeItem();">
									            <option value="modifyDate">최종수정일</option>
									            <option value="word">단어순</option>
									        </select>
										</div>
										<div class="col-md-3">
									        <label class="control-label">검색어</label>
									        <div class="input-group">
										        <input type="text" class="form-control" id="searchKeyword" name="searchKeyword" placeholder="검색 할 단어를 입력하세요." onkeypress="if(event.keyCode==13) {dicList(); return false;}" >
										        <button class="btn btn-light-secondary text-secondary font-weight-medium" id="searchBtn" type="button"><i data-feather="search" class="feather-sm fill-white"></i></button>
										    </div>
									    </div>
									</div>
                                    <div class="row">
                                         <div class="col-sm-12 col-md-12">
                                            <table id="zero_config" class="table">
                                             <thead class="thead-light">
                                                    <tr>
                                                        <th scope="row"><input type="checkbox" id="dicCheckBox" name="" class="ml_5 radio" /></th>
                                                        <th scope="row">단어</th>
                                                        <th scope="row">분석정보</th>
                                                        <th scope="row">동의어</th>
                                                        <th scope="col">금칙어</th>
                                                        <th scope="col">등록자</th>
                                                        <th scope="col">수정자</th>
                                                        <th scope="col">등록/수정일</th>
                                                        <th scope="col">Action</th>
                                                    </tr>
                                                    </thead>
                                                    <tbody id="tbodydicList">
                                                    </tbody>
                                                    <tr>
                                                        <td colspan=9></td>
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
                    </form>
                </div>

<%@include file="/WEB-INF/views/common/footer.jsp"%>
<script src="${CTX_PATH}/static/package/assets/libs/jquery-steps/build/jquery.custom.steps.js"></script>
<!-- 정보 Template Start   -->
<!-- 정보 리스트 출력  -->
<script id="dicListRowTmpl" type="text/x-jquery-tmpl">
    {{each(i, gUList) dicList}}
        <tr id="dicListRow\${i}">
            <td><input type="checkbox" name="dic_chk" value="\${gUList.dicNo}" class="ml_5 radio gDicChk" /></td>
            <td>\${gUList.word}</td>
            <td>\${gUList.wordSep}</td>
            <td>\${gUList.synonyms}</td>
            <td>\${gUList.nosearchYn}</td>  
            <td>\${gUList.createUserNm}</td>
			<td>\${gUList.modifyUserNm}</td>
			<td>\${GetDateMMDDSS(gUList.modifyDate)}</td>
            <td><a href="#" id="modify\${gUList.dicNo}" data-bs-toggle="modal" data-bs-target="#edit-event" class="btn btn-danger btn-sm" >수정</a></td>
        </tr>
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
                    	<input type="hidden" id="serviceId" name="serviceId" value="userDic">
		                <div style="margin-top:5px;"><small><code>엑셀파일</code>을 선택한 후에 업로드 실행 버튼을 클릭 해 주세요.</small></div>
		                <div style="margin-top:5px;"><small>&#8251; <code>스크립트명</code>을 기준으로 업로드가 진행됩니다. 그러므로 스크립트명은 변경이 안됩니다.</small></div>
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
   	<input type="hidden" id="serviceId" name="serviceId" value="userDic">
   	<input type="hidden" id="taskId" name="taskId" value="">
</form>

                <div class="modal fade" id="reflectionModal" tabindex="-1" data-bs-backdrop="static" role="dialog" aria-labelledby="reflectionLabel" aria-hidden="true">
                    <div class="modal-dialog" role="document">
                        <div class="modal-content">
                            <div class="modal-header d-flex align-items-center">
				                <h5 class="modal-title">서비스 반영 </h5>
				                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
				            </div>
                            <div class="modal-body">
                                서비스에 반영하겠습니까 ?
                            </div>
                            <div class="modal-footer">
				             	<button type="button" class="btn btn-success" aria-hidden="false" id="dicDist">적용</button>
	                			<button type="button" class="btn btn-light-danger text-danger font-weight-medium" data-bs-dismiss="modal">취소</button>
	                		</div>
                        </div>
                    </div>
                </div>


                <div class="modal fade" id="newAdd" data-bs-backdrop="static" tabindex="-1" aria-labelledby="addModalLabel">
                    <div class="modal-dialog" role="document">
                        <div class="modal-content">
                            <div class="modal-header d-flex align-items-center">
			                    <h5 class="modal-title" id="addModalLabel">신규 등록</h5>
				                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			                </div>
                            <div class="modal-body">
	                            <div class="mb-3">
	                                <label for="fname" class="control-label">단어</label>
	                                <input id="word" type="text" data-toggle="tooltip" title="단어를 입력하세요" class="form-control"  placeholder="예)프로서치  ">
	                                <span style="font-size:9px;"> 2~12자 이하</span>
	                            </div>
	                            <div class="mb-3">
	                                <label for="fname" class="control-label">분석정보</label>
	                                <input id="wordSep" type="text" data-toggle="tooltip" title="숫자로 분석정보를 입력하세요" class="form-control"  placeholder="예)0  ">
	                                <span style="font-size:9px;">- 0 : 입력글자를 한단어로 인식</span>
	                                <span style="font-size:9px;">- 22 : 2글자 2글자를 분리</span>
	                            </div>
	                            <div class="mb-3">
	                                <label for="fname" class="control-label">동의어</label>
                                    <input id="synonyms" type="text" data-toggle="tooltip" title="동의어를 구분자(,)로 여러개 입력하세요" class="form-control" placeholder="예) chat,채팅 ">
                                    <span style="font-size:9px;"> 단어 구분자 콤마(,)</span>
	                            </div>
	                            <div class="mb-3">
	                                <label for="fname" class="col-sm-3 text-right control-label col-form-label">금칙어</label>
	                                <input id="nosearchYn1" name="nosearchYn" type="radio" value="y" /><span> 제외</span>
                        			<input id="nosearchYn2" name="nosearchYn" type="radio" value="n" checked="checked"/><span> 미제외</span>
	                                <span style="font-size:9px;"> (검색제외)</span>
	                            </div>
                            </div>
			                <div class="modal-footer">
			                    <button type="button" class="btn btn-success" id="save" aria-hidden="false">저장</button>
			                	<button type="button" class="btn btn-light-danger text-danger font-weight-medium" data-bs-dismiss="modal">취소</button>
			                </div>
                        </div>
                    </div>
                </div>

                <div class="modal fade" id="edit-event" tabindex="-1" data-bs-backdrop="static" aria-labelledby="editModalLabel" aria-hidden="true">
                    <div class="modal-dialog" role="document">
                        <div class="modal-content">
                            <div class="modal-header d-flex align-items-center">
			                    <h5 class="modal-title" id="editModalLabel">수정</h5>
				                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			                </div>
                            <div class="modal-body" id="dicEditDiv">
                            </div>
                            <script id="dicEditRowTmpl" type="text/x-jquery-tmpl">
                                {{each(i, gUList) dicEdit}}
								<div class="row">
                                    <div class="col-md-12 mb-3">
                                        <label for="fname" class="control-label">단어</label>
                                        <input id="word\${gUList.dicNo}" type="text" value="\${gUList.word}" data-toggle="tooltip" title="단어를 입력하세요." class="form-control"  readonly>
										<span style="font-size:9px;"> 2~12자 이하</span>
                                    </div>
                                    <div class="col-md-12 mb-3">
                                        <label for="fname" class="control-label">분석정보</label>
                                        <input id="wordSep\${gUList.dicNo}" type="text" value="\${gUList.wordSep}"  data-toggle="tooltip" title="숫자로 분석정보 입력하세요" class="form-control"  placeholder="예)0" >
                                        <span style="font-size:9px;">- 0 : 입력글자를 한단어로 인식</span>
	                               		<span style="font-size:9px;">- 22 : 2글자 2글자를 분리</span>
                                    </div>
                                    <div class="col-md-12 mb-3">
                                        <label for="fname" class="control-label">동의어</label>
                                        <input id="synonyms\${gUList.dicNo}" type="text" value="\${gUList.synonyms}" data-toggle="tooltip" title="동의어를 구분자(,)로 여러개 입력하세요" class="form-control"  placeholder="예) chat,채팅 ">
										<span style="font-size:9px;"> 단어 구분자 콤마(,)</span>
                                    </div>
                                    <div class="col-md-12 mb-3">
                                        <label for="fname" class="col-sm-3 text-right control-label col-form-label">금칙어</label>
										<input id="nosearchYn1\${gUList.dicNo}" name="nosearchYn\${gUList.dicNo}" type="radio" value="y" {{if gUList.nosearchYn == "y" }}checked="checked"{{/if}}/><span> 제외</span>
                        				<input id="nosearchYn2\${gUList.dicNo}" name="nosearchYn\${gUList.dicNo}" type="radio" value="n" {{if gUList.nosearchYn != "y" }}checked="checked"{{/if}}/><span> 미제외</span>
                                        <span style="font-size:9px;"> (검색제외)</span>
                                    </div>
									<div class="col-md-6 mb-3">
					    				<label for="createUser" class="control-label">등록자</label>
					    				<input type="text" class="form-control" value="\${gUList.createUserNm}" readonly/>
									</div>
									<div class="col-md-6 mb-3">
					    				<label for="createDate" class="control-label">등록일</label>
					    				<input type="text" class="form-control" value="\${GetDateMMDDSS(gUList.createDate)}" readonly/>
									</div>
									<div class="col-md-6 mb-3">
					    				<label for="modifyUserNm" class="control-label">수정자</label>
					    				<input type="text" class="form-control" value="\${gUList.modifyUserNm}" readonly/>
									</div>
									<div class="col-md-6 mb-3">
					    				<label for="modifyDate" class="control-label">수정일</label>
					    				<input type="text" class="form-control" value="{{if gUList.modifyUserNm != null}}\${GetDateMMDDSS(gUList.modifyDate)}{{/if}}" readonly/>
									</div>
									<div class="modal-footer">
			                    		<button type="button" class="btn btn-success" id="edit\${gUList.dicNo}" aria-hidden="false">저장</button>
			                			<button type="button" class="btn btn-light-danger text-danger font-weight-medium" data-bs-dismiss="modal">취소</button>
			                		</div>
								</div>
                                {{/each}}
                            </script>
                        </div>
                    </div>
                </div>
<script type="text/javascript">

$(document).on("click","a[id^=modify]",function(e) {
    var id = $(this).attr("id");
    id = id.replace("modify","");
    btnViewContent(id);
});

$(document).on("click","#save",function(e) {
	$('#save').attr('disabled',true);
    btnSaveDicProc();
});

$(document).on("click","button[id^=edit]",function(e) {
    var orgId = $(this).attr("id");
    var id = orgId.replace("edit","");
    $(this).attr('disabled',true);
    btnDicModProc(id, orgId);
});



//화면 로딩 처리 함수 
$(document).ready(function(){

    dicList();

    //사전의 삭제 버튼 클릭 이벤트
    $('#dicDel').click(function(){
        dicDel();
        if($("#dicCheckBox").is(":checked")){
            $("input[type=checkbox]").prop("checked",false);
        }
    });

    //사전의 배포 버튼 클릭 이벤트
    $('#dicDist').click(function(){
        var url = "/dic/distDic.ps";
        param = "";
        var dicBack = function(data) {
            $('#reflectionModal').modal('hide');
            if ( data && data.success ) {
    			var resultStr = data.resultJson;
    			var resultObj = JSON.parse(resultStr);
    			if(resultObj.status.code == '200'){
    				Swal.fire({
    					type: "info",
    					text: "사전 패포 완료",
    				});
    			} else {
    				Swal.fire({
    					type: "error",
    					title: "사전 배포 실패",
    					text: resultObj.status.message,
    				  });
    			}
    		} else{
    			// API 요청 실패
    			Swal.fire({
    					type: "error",
    					title: "사전 배포 요청 실패",
    					text: data.msg,
    				  });
    		}
        };
        callAjax(url, param, dicBack);
    });

    $('#searchBtn').click(function(){
        dicList();
    });

    //사전의 Checkbox 클릭 이벤트
    $("#dicCheckBox").click(function(){
        if($("#dicCheckBox").is(":checked")){
            $("input[type=checkbox]").prop("checked",true);
        }else{
            $("input[type=checkbox]").prop("checked",false);
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
		// reload!!
		$('#uploadModal').modal('hide');
		dicList(1);
	});
});

function getChangeItem(){
    dicList(1);
}

//목록
function dicList(pageNo){
    pageNo = pageNo || 1;
    $('#pageNo').val(pageNo);
    var param = { pageNo : pageNo
                  , searchKeyword :$('#searchKeyword').val()
                  , lineNo : $('select[name=lineNo]').val()
                  , sort : $('select[name=sortType]').val()
                  , searchField : "wordUnq,synonyms"
                 }; 

    var dicListBack = function(data) {
    	$("#dicCheckBox").prop("checked",false);
        var paginationHtml = getPaginationHtml(pageNo, data.totalCnt, $('select[name=lineNo]').val(), '10', 'dicList');
        $('#tbodydicList').html('');
        $("#dicListRowTmpl").tmpl(data).appendTo("#tbodydicList");
        $("#totalCnt").html(data.totalCnt);
        $("#pagination").html( paginationHtml );        
    };
    
    callAjax("/dic/dicAjax.ps", param, dicListBack);
    
}


function newAdd() {
    $('#word').val('');
    $('#synonyms').val('');
    $('#wordSep').val('');
    $('#save').attr('disabled',false);
}


//수정
function btnViewContent( dicNo ) {

    $('#word').val('');
    $('#synonyms').val('');
    $('#wordSep').val('');

    var url  = "/dic/dicAjaxEdit.ps";
    var param = {
            dicNo : dicNo
    };

    $('#dicEditDiv').html('');

    var dicEditBack = function(data) {
        //console.log(data.dicEdit);
        $("#dicEditRowTmpl").tmpl(data).appendTo("#dicEditDiv");
    };
    callAjax(url, param, dicEditBack);

}


//신규등록 폼에서의 저장 버튼 클릭 이벤트
function btnSaveDicProc() {
    var param = {
            word : $.trim($('#word').val())             
    };
     
    if ( !validateDic() ) {
    	$('#save').attr('disabled',false);
        return false;
    } 
    var serviceBack = function(data) {
		if ( data && data.success ) {
			dicInsertProc();
		}
		else {
			$('#save').attr('disabled',false);
			if ( data && data.msg ) {
				alert(data.msg);
			}else {
				alert('저장이 실패하였습니다.');
			}
		}
    }; 
	
	callAjaxAsync("/dic/dupDic.ps", param, serviceBack);
}


//추가 이벤트
function dicInsertProc(){

    var param = {
    		word : $.trim($('#word').val())
            ,wordSep : $.trim($('#wordSep').val())
            ,synonyms : $.trim($('#synonyms').val())
            ,nosearchYn : $(':input:radio[name=nosearchYn]:checked').val() 
    };
    var serviceBack = function(data) {
		if (data.result.success ) {
			location.reload();
		}else{
			$('#save').attr('disabled',false);
			if ( data && data.result.msg ) {
				alert(data.result.msg);
			} 
			else {
				alert('저장이 실패하였습니다.');
			}
		}
    }; 
	
    callAjaxAsync("/dic/saveDic.ps", param, serviceBack);

}


//편집ROW의 수정 버튼 클릭 이벤트
function btnDicModProc(id, orgId){
    var param = {
    		word : $.trim($('#word'+ id).val())
            ,wordSep : $.trim($('#wordSep'+ id).val())
            ,synonyms : $.trim($('#synonyms'+ id).val())
            ,nosearchYn : $(':input:radio[name=nosearchYn'+ id+']:checked').val()
            ,dicNo : id
            ,add : 'N'
    };
    if ( !validateDic(id) ) {
    	$('#'+orgId).attr('disabled',false);
        return;
    }
    var serviceBack = function(data) {
    	if (data.result.success ) {
			location.reload();
		}else{
			$('#'+orgId).attr('disabled',false);
			if ( data && data.result.msg ) {
				alert(data.result.msg);
			} 
			else {
				alert('저장이 실패하였습니다.');
			}
		}
    }; 
	
    callAjaxAsync("/dic/saveDic.ps", param, serviceBack);

}

// 사전저장 저장/수정 시 입력값 검증
function validateDic(id) {

    var gIdPattern = /^[0-9]+$|^$/g;
    var ids = '';
    if(id != undefined){
        ids = id
    }

    var sepValue = $('#wordSep'+ ids).val();
    var word     = $('#word'+ ids).val();
    
    if(sepValue.indexOf("Y")>-1 ||sepValue.indexOf("X")>-1||sepValue.indexOf("C")>-1||sepValue.indexOf("B")>-1||sepValue.indexOf("J")>-1){

    } else {

        if ( !patternHanEngNumNoSpRange.test($('#word' + ids).val()) ) {
            Swal.fire({
            	type: "warning",
            	text: "단어는 '한글','알파벳','숫자'를 포함하여 2~12자까지 입력해주세요.\n특수문자/공백은 제외해주세요.",
            });
            $('#word' + ids).focus();
            return false;
        }

        if ( sepValue.search(gIdPattern) == -1 ) {
            $('#wordSep' + ids).focus();
            Swal.fire({
            	type: "warning",
            	text: "분석정보는 '숫자'만 입력 가능합니다.",
            });
            return false;
        }

        var wlen = sepValue.length;

        if(wlen>0){
            var totalCnt = 0;
            for (var i = 0; i < wlen; i++) {
                oneChar = sepValue.charAt(i);
                if ( oneChar.search(gIdPattern) != -1 ) {
                    totalCnt+=parseInt(oneChar); 
                }
            }
            if(totalCnt!=0){
                var wordLen = word.length;
                if(totalCnt!=wordLen){
                    Swal.fire({
                    	type: "warning",
                    	text: "분석정보의 숫자가 맞지 않습니다. 다시 확인하세요.",
                    });
                    return false;
                }
            }
        }
    }

    if ( $.trim($('#synonyms').val())  != "" ) {
        if ( !patternHanEngNumComma.test($('#synonyms' + ids).val()) ) {
            Swal.fire({
            	type: "warning",
            	text: "동의어는 '한글','알파벳','숫자' 를 입력할 수 있으며  동의어 단어간은 구분자(,)를 입력해주세요,.\n특수문자/공백은 제외해주세요. (전체 글자길이 100자 이내)",
            });
            $('#synonyms' + ids).focus();
            return false;
        }
    }
    return checkNotEmpty(
            [
                 ["word" + ids  , "단어를 입력해주세요"]
                ,["wordSep" + ids, "분석정보를 입력하여주세요."]
            ]
    );
}

//서비스  삭제
function dicDel() {
    if ( $('.gDicChk:checked').length == 0 ) {
        Swal.fire({
        	type: "warning",
        	text: "삭제하려는 사전을  체크 하세요.",
        });
        return;
    }  
    var param = { dicNo : $('.gDicChk:checked')
            .map(function(){ return $(this).val();}).toArray().join(":")    };
    
    Swal.fire({
    	title: "삭제",
    	text: '삭제 하시겠습니까?',
    	type: 'warning', 
    	showCancelButton: true, 
            confirmButtonColor: 'btn-info',
            cancelButtonColor: 'btn-danger',
    	confirmButtonText: '삭제', 
    	cancelButtonText: '취소'
    }).then((result) => { 
    	if(result.value){
    		callAjax("/dic/delDic.ps", param, listBack);
    	} else {
    		return;
    	}
    });
    var listBack = function(data) {
		if (data.success ) {
			location.reload();
		}else{
			alert('삭제 처리가 실패하였습니다.');
		}
	};
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
<%@include file="/WEB-INF/views/common/end.jsp"%>