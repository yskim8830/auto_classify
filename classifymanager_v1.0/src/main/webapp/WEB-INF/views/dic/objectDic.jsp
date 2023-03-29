<%@include file="/WEB-INF/views/common/header.jsp"%>
<%@include file="/WEB-INF/views/common/menu.jsp"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" type="text/css" href="${CTX_PATH}/static/package/assets/libs/select2/dist/css/select2.min.css" />
<link rel="stylesheet" type="text/css" href="${CTX_PATH}/static/package/assets/extra-libs/toastr/dist/build/toastr.min.css" />
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
				                엔티티 사전 관리
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
				                        엔티티 사전 관리
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
                <div class="container-fluid page-content" id="objContent">
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
	                                        <h5 class="card-title m-b-0"><i class="m-r-10 mdi mdi-code-greater-than"></i> 엔티티 사전 관리</h5>
	                                    </div>
                                    </div>
                                    <div class="row">
                                    	<div class="col-md-12 text-end">
                                    		<div class="mb-3">
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
									    <div class="col-md-2">
									        <label class="control-label">목록수</label>
									        <select class="form-select" id="lineNo" name="lineNo" onchange="getChangeItem();">
									            <option value="10">10</option>
									            <option value="20">20</option>
									            <option value="30">30</option>
									        </select>
										</div>
										<div class="col-md-3">
									        <label class="control-label">정렬</label>
									        <select class="form-select" id="" name="sortType" onchange="getChangeItem();">
									            <option value="modifyDate">최종수정일</option>
									            <option value="entity">단어순</option>
									        </select>
										</div>
										<div class="col-md-4">
									        <label class="control-label">검색어</label>
										    <div class="input-group">
									        	<select class="form-select" id="searchField" name="searchField" >
										            <option value="entity.search,entry.search">전체</option>
										            <option value="entity.search">엔티티</option>
										            <option value="entry.search">엔트리</option>
										        </select>
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
                                                        <th scope="col"><input type="checkbox" id="dicCheckBox" name="" class="ml_5 radio" /></th>
                                                        <!-- th scope="col">카테고리</th> -->
                                                        <th scope="col">엔티티</th>
                                                        <th scope="col">엔트리목록</th>
                                                        <th scope="col">사용여부</th>
                                                        <th scope="col">등록자</th>
                                                        <th scope="col">수정자</th>
                                                        <th scope="col">등록/수정일</th>
                                                        <th scope="col">Action</th>
                                                    </tr>
                                                    </thead>
                                                    <tbody id="tbodydicList">
                                                    </tbody>
                                                    <tr>
                                                        <td colspan=8></td>
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
<script src="${CTX_PATH}/static/package/assets/libs/select2/dist/js/select2.full.min.js"></script>
<script src="${CTX_PATH}/static/package/assets/libs/select2/dist/js/select2.min.js"></script>
<script src="${CTX_PATH}/static/package/assets/extra-libs/toastr/dist/build/toastr.min.js"></script>
<script src="${CTX_PATH}/static/package/assets/libs/jquery-steps/build/jquery.custom.steps.js"></script>
<!-- 정보 Template Start   -->
<!-- 정보 리스트 출력  -->
<script id="dicListRowTmpl" type="text/x-jquery-tmpl">
    {{each(i, gUList) dicList}}
        <tr id="dicListRow\${gUList.entity}">
            <td><input type="checkbox" name="dic_chk" value="\${gUList.entityNo}" class="ml_5 radio gDicChk" /></td>
            <!--td>\${gUList.categoryNm}</td-->
            <td>\${gUList.entity}</td>
            <td>\${truncStr(gUList.entry,20)}</td>
            <td>\${gUList.useYn}</td>
            <td>\${gUList.createUserNm}</td>
			<td>\${gUList.modifyUserNm}</td>
			<td>\${GetDateMMDDSS(gUList.modifyDate)}</td>
            <td><a href="#" id="modify\${gUList.entityNo}" data-bs-toggle="modal" data-bs-target="#edit-event" class="btn btn-danger btn-sm" >수정</a></td>
			
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
                    	<input type="hidden" id="serviceId" name="serviceId" value="objectDic">
		                <div style="margin-top:5px;"><small><code>엑셀파일</code>을 선택한 후에 업로드 실행 버튼을 클릭 해 주세요.</small></div>
		                <div style="margin-top:5px;"><small>&#8251; <code>엔티티명</code>을 기준으로 업로드가 진행됩니다. 그러므로 엔티티명은 변경이 안됩니다.</small></div>
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
   	<input type="hidden" id="serviceId" name="serviceId" value="objectDic">
   	<input type="hidden" id="taskId" name="taskId" value="">
</form>


                <div class="modal fade" id="newAdd" tabindex="-1" data-bs-backdrop="static" aria-labelledby="addModalLabel">
                    <div class="modal-dialog" role="document">
                        <div class="modal-content">
                            <div class="modal-header d-flex align-items-center">
			                    <h5 class="modal-title" id="addModalLabel">신규 등록</h5>
				                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			                </div>
                            <div class="modal-body">
	                            <div class="mb-3">
	                                <label for="fname" class="control-label">엔티티</label>
	                                <input id="entity" type="text" data-toggle="tooltip" title="단어를 입력하세요" class="form-control"  placeholder="예)@프로서치  ">
	                                <span style="font-size:9px;"> 2~12자 이하</span>
	                            </div>
	                            <div class="mb-3">
	                                <label for="fname" class="control-label">엔트리명</label>
	                                <div class="input-group mb-1">
								        <input type="text" class="form-control" aria-label="Text input with checkbox" id="object_ipt" onkeypress="if(event.keyCode==13) {addObj(''); return false;}">
								        <button class="btn btn-light-info text-info font-weight-medium" type="button" onClick="addObj('')">ADD</button>
								    </div>
				    				<div class="overflow-auto mb-md-0 mr-md-3 bg-light" style="max-height:200px;" id="tbodyObjList">
									</div>
	                            </div>
	                            <div class="mb-3">
	                                <label for="fname" class="col-sm-3 text-right control-label col-form-label">사용여부</label>
	                                <input id="useYn1" name="useYn" type="radio" value="y" checked="checked"/><span> 사용</span>
                        			<input id="useYn2" name="useYn" type="radio" value="n"/><span> 미사용</span>
	                            </div>
                            </div>
			                <div class="modal-footer">
			                    <button type="button" class="btn btn-success" id="save" aria-hidden="false">저장</button>
			                	<button type="button" class="btn btn-light-danger text-danger font-weight-medium" data-bs-dismiss="modal">취소</button>
			                </div>
                        </div>
                    </div>
                </div>

                <div class="modal fade" id="edit-event" tabindex="-1" data-bs-backdrop="static" aria-labelledby="editModalLabel">
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
                                        <label for="fname" class="control-label">엔티티</label>
                                        <input id="entity\${replaceAll(gUList.entityNo,'@','')}" type="text" value="\${gUList.entity}" data-toggle="tooltip" title="단어를 입력하세요." class="form-control"  readonly>
										<span style="font-size:9px;"> 2~12자 이하</span>
                                    </div>
                                    <div class="col-md-12 mb-3">
                                        <label for="fname" class="control-label">엔트리명</label>
	                                	<div class="input-group mb-1">
								        	<input type="text" class="form-control" aria-label="Text input with checkbox" id="object_ipt\${replaceAll(gUList.entityNo,'@','')}"  onkeypress="if(event.keyCode==13) {addObj('\${replaceAll(gUList.entityNo,'@','')}'); return false;}">
								        	<button class="btn btn-light-info text-info font-weight-medium" type="button" onClick="addObj('\${replaceAll(gUList.entityNo,'@','')}')">ADD</button>
								    	</div>
				    					<div class="overflow-auto mb-md-0 mr-md-3 bg-light" style="max-height:200px;" id="tbodyObjList\${replaceAll(gUList.entityNo,'@','')}">

										 	{{each(j, objectEdit) gUList.entry.split(',')}}
												{{if objectEdit != ''}}
											<div class="input-group mb-1" id="obj_\${objectEdit}">
												<input type="text" class="form-control" aria-label="Text input with checkbox" value="\${objectEdit}" readonly >
												<button class="btn btn-success" type="button" onclick="getSynonyms('\${objectEdit}');">동의어</button>
												<button class="btn btn-light-danger text-danger" type="button" onclick="delObj('\${objectEdit}');">DEL</button>
											</div>
												{{/if}}
											{{/each}}
										</div>
                                    </div>
                                    <div class="col-md-12 mb-3">
                                        <label for="fname" class="col-sm-3 text-right control-label col-form-label">사용여부</label>
										<input id="useYn1\${replaceAll(gUList.entityNo,'@','')}" name="useYn\${replaceAll(gUList.entityNo,'@','')}" type="radio" value="y" {{if gUList.useYn == "y" }}checked="checked"{{/if}}/><span> 사용</span>
                        				<input id="useYn1\${replaceAll(gUList.entityNo,'@','')}" name="useYn\${replaceAll(gUList.entityNo,'@','')}" type="radio" value="n" {{if gUList.useYn != "y" }}checked="checked"{{/if}}/><span> 미사용</span>
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
			                    		<button type="button" class="btn btn-success" id="edit\${gUList.entityNo}" aria-hidden="false">저장</button>
			                			<button type="button" class="btn btn-light-danger text-danger font-weight-medium" data-bs-dismiss="modal">취소</button>
			                		</div>
									<!--input type="hidden" id="categoryValue" value="\${gUList.categoryNm}"/-->
                                </div>
								{{/each}}
                            </script>
                        </div>
                    </div>
                </div>
<script type="text/javascript">

$(document).on("click","a[id^=modify]",function(e) {
    var id = $(this).attr("id");
    id = id.replace("modify@","");
    btnViewContent(id);
});

$(document).on("click","#save",function(e) {
	$('#save').attr('disabled',true);
    btnSaveDicProc();
});

$(document).on("click","button[id^=edit]",function(e) {
    var orgId = $(this).attr("id");
    id = orgId.replace("edit@","");
    $(this).attr('disabled',true);
    btnDicModProc(id, orgId);
});

//화면 로딩 처리 함수 
$(document).ready(function(){

    dicList();
    
    //사전의 삭제 버튼 클릭 이벤트
    $('#dicDel').click(function(){
        dicDel();
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
    query = $.trim(replaceAll($('#searchKeyword').val(),'@',''));
    var param = { pageNo : pageNo
                  , searchKeyword : query
                  , lineNo : $('select[name=lineNo]').val()
                  , sort : $('select[name=sortType]').val()
                  
                  , searchField : $('select[name=searchField]').val()
                 }; 

    var dicListBack = function(data) {
    	$("#dicCheckBox").prop("checked",false);
        var paginationHtml = getPaginationHtml(pageNo, data.totalCnt, $('select[name=lineNo]').val(), '10', 'dicList');
        $('#tbodydicList').html('');
        $("#dicListRowTmpl").tmpl(data).appendTo("#tbodydicList");
        $("#totalCnt").html(data.totalCnt);
        $("#pagination").html( paginationHtml );        
    };
    
    callAjax("/dic/objDicAjax.ps", param, dicListBack);
    
}


function newAdd() {
    $('#entity').val('');
    $('#entry').val('');  
    $('#object_ipt').val('');
    $('#tbodyObjList').html('');
    $('#save').attr('disabled',false);
}

//수정
function btnViewContent( entityNo ) {
    var url  = "/dic/objDicAjaxEdit.ps";
    var param = {
    		entityNo : '@'+entityNo
    };

    $('#dicEditDiv').html('');

    var dicEditBack = function(data) {
        $("#dicEditRowTmpl").tmpl(data).appendTo("#dicEditDiv");
    };
    callAjax(url, param, dicEditBack);

}


//폼에서의 저장 버튼 클릭 이벤트
function btnSaveDicProc() {
    var param = {
    		entity : $.trim($('#entity').val())             
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
	
	callAjaxAsync("/dic/dupObjDic.ps", param, serviceBack);
}


//추가 이벤트
function dicInsertProc(){
	$('#tbodyObjList').find('div[id^=obj_]').attr('id')
    var param = {
		entity : '@'+$.trim(replaceAll($('#entity').val(),'@',''))
            
            ,entry : $('#tbodyObjList').find('div[id^=obj_]').map(function(){ return $(this).attr('id').replace('obj_','');}).toArray().join(",")
            ,useYn : $(':input:radio[name=useYn]:checked').val() 
    };
    var serviceBack = function(data) {
		if (data.success ) {
			location.reload();
		}else{
			$('#save').attr('disabled',false);
			if ( data && data.msg ) {
				alert(data.msg);
			} 
			else {
				alert('저장이 실패하였습니다.');
			}
		}
    }; 
	
    callAjaxAsync("/dic/saveObjDic.ps", param, serviceBack);

}


//편집ROW의 수정 버튼 클릭 이벤트
function btnDicModProc(id){
    var param = {
    		entity : $.trim($('#entity'+ id).val())
            
            ,entry : $('#tbodyObjList'+ id).find('div[id^=obj_]').map(function(){ return $(this).attr('id').replace('obj_','');}).toArray().join(",")
            ,useYn : $(':input:radio[name=useYn'+ id+']:checked').val()
            ,entityNo : '@'+id
            ,add : 'N'
    };
    if ( !validateDic(id) ) {
    	$('#'+orgId).attr('disabled',false);
        return;
    }
    var serviceBack = function(data) {
		if (data.success ) {
			location.reload();
		}else{
			$('#'+orgId).attr('disabled',false);
			if ( data && data.msg ) {
				alert(data.msg);
			} 
			else {
				alert('저장이 실패하였습니다.');
			}
		}
    }; 
	
    callAjaxAsync("/dic/saveObjDic.ps", param, serviceBack);

}

// 사전저장 저장/수정 시 입력값 검증
function validateDic(id) {
    var ids = '';
    if(id != undefined){
        ids = id
    }

    var word     = $('#entity'+ ids).val();
    
    if ( !patternHanEngNumRange.test($('#entity' + ids).val()) ) {
    	Swal.fire({
    		type: "warning",
    		text: "엔티티명은 '한글',영문','숫자','_','-','@' 를 포함하여 2~12자까지 입력해주세요.\n특수문자/공백은 제외해주세요.",
    	});
        $('#entity' + ids).focus();
        return false;
    }
    return checkNotEmpty(
            [
                 ["entity" + ids  , "엔티티를 입력해 주세요"]
                 
            ]
    );
}

//서비스  삭제
function dicDel() {
    if ( $('.gDicChk:checked').length == 0 ) {
        Swal.fire({
        	type: "warning",
        	text: "삭제하려는 사전을 체크 하세요.",
        });
        return;
    }  
    var param = { entityNo : $('.gDicChk:checked')
            .map(function(){ return $(this).val();}).toArray().join(":")    };
        
    var serviceBack = function(data) {
		if (data.success) {
			var classifyRuleReferencedIdList = $.map(data.result.classifyRuleReferencedIdInfoMap, function(value, key){
				return key;
			});
			
			console.log(data);
			
			if(classifyRuleReferencedIdList.length > 0 ){
				$('#deletedDataHtml').html('');
				$('#checkRuleDataHtml').html('');
				
				var noReferIdSet = new Set();
				var deletedDataHtml = '';
				for(var idx in data.result.noReferencedIdList){
					var deletedId = data.result.noReferencedIdList[idx];
					noReferIdSet.add(deletedId);
					deletedDataHtml += '<tr><td>';
					deletedDataHtml += '	<div class="d-flex align-items-center">';
					deletedDataHtml += '	<span class="ms-3 fw-normal">' + deletedId + '</span>';
					deletedDataHtml += '	</div>';
					deletedDataHtml += '</td></tr>';
				}
				$('#deletedDataHtml').html(deletedDataHtml);
				
				var checkRuleDataHtml = '';
				var checkId;
				var ruleList;
				for(var idx in classifyRuleReferencedIdList){
					checkId = classifyRuleReferencedIdList[idx];
					ruleList = data.result.classifyRuleReferencedIdInfoMap[checkId];

					checkRuleDataHtml += '<tr>';
					checkRuleDataHtml += '	<td rowspan="' + ruleList.length + '">';
					checkRuleDataHtml += '		<div class="d-flex align-items-center">';
					checkRuleDataHtml += '			<span class="ms-3 fw-normal">' + checkId + '</span>';
					checkRuleDataHtml += '		</div>';
					checkRuleDataHtml += '	</td>';
					for(var voIdx in ruleList){
						if(voIdx > 0) {
							checkRuleDataHtml += '<tr>';
						}
						checkRuleDataHtml += '	<td style="max-width:250px;">' + ruleList[voIdx].classifyRuleNm + '</td>';
						checkRuleDataHtml += '	<td>';
						if(noReferIdSet.has(ruleList[voIdx].classifyRuleEntityNo)){
							checkRuleDataHtml += '다시 삭제 시도를 해주세요.';	
						} else {
							checkRuleDataHtml += '		<button class="btn btn-light-danger text-danger font-weight-medium waves-effect text-start" type="button" onclick="javascript:goRefRule(\'' + ruleList[voIdx].classifyRuleCategoryNo + '\');">바로가기</button>';							
						}
						checkRuleDataHtml += '	</td>';
						checkRuleDataHtml += '</tr>';
					}
				}
				
				if(classifyRuleReferencedIdList.length > 0){
					$('#checkRuleDataHtml').html(checkRuleDataHtml);
					$('#checkRuleData').show();
				} else {
					$('#checkRuleData').hide();
				}
				
				$('#delete-result-modal').modal('show');
			} else {
				$("#dicCheckBox").prop("checked",false);
				dicList($('#pageNo').val());			
			}
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
		  console.log(result);
		  if(result.value){
			  callAjaxAsync("/dic/delObjDic.ps", param, serviceBack); 
		  } else {
			  return;
		  }
	});
}

function addObj(id) {
	id = id || '';
	if ( !patternHanEngNumSpaceRange2.test($('#object_ipt' + id).val()) ) {
        Swal.fire({
        	type: "warning",
        	text: "엔트리명은 '한글','알파벳','숫자'를 포함하여 2~50자까지 입력해주세요.\n특수문자는 제외해주세요.",
        });
        return false;
    }
	if($('#obj_'+$("#object_ipt" + id).val()).length > 0){
		Swal.fire({
        	type: "warning",
        	text: "엔트리가 이미 존재합니다.",
        });
		return;
	}
	console.log('check point 1');
	if($("#object_ipt" + id).val() == ''){
		Swal.fire({
        	type: "warning",
        	text: "엔트리명을 입력해 주세요.",
        });
		return;
	}
	str = '';
	str += '<div class="input-group mb-1" id="obj_'+$("#object_ipt" + id).val()+'">';
	str += '<input type="text" class="form-control" aria-label="Text input with checkbox" value="'+$("#object_ipt" + id).val()+'" readonly >';
	str += '<button class="btn btn-success" type="button" onclick="getSynonyms(\''+$("#object_ipt" + id).val()+'\');">동의어</button>';
	str += '<button class="btn btn-light-danger text-danger" type="button" onclick="delObj(\''+$("#object_ipt" + id).val()+'\');">DEL</button>';
	str += '</div>';
	
	$("#tbodyObjList"+ id).append(str);
	$("#object_ipt" + id).val("");
}

function delObj(id) {
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
			$('#obj_'+id).remove();
		} else {
			return;
		}
	});
	
}

function getSynonyms(id){
	if($('#obj_'+id).find('div').length > 0){
		return;
	}
    var param = {
            dicNo : id
    };
    var listBack = function(data) {
    	synonyms = '';
    	if(data.dicEdit.length > 0 ){
    		synonyms = data.dicEdit[0].synonyms;
    	}
    	if($('#obj_'+id).length < 1){
    		Swal.fire({
    			type: "warning",
    			text: "동의어를 등록할 수 없는 엔트리 입니다.",
    		});
    		return;
    	}
    	str = '';
    	str += '<div class="input-group ms-4 mt-1 mb-1">';
    	str += '    <input type="text" class="form-control" aria-label="Text input" id="synonyms_'+id+'" value="'+synonyms+'" data-toggle="tooltip" title="동의어를 구분자(,)로 여러개 입력하세요" placeholder="예) chat,채팅 ">';
    	str += '    <button class="btn btn-success" type="button" onclick="saveSynonyms(\''+id+'\')">저장</button>';
    	str += '</div>';
    	$('#obj_'+id).find('div').remove();
    	$('#obj_'+id).append(str);
    };
    callAjax("/dic/dicAjaxEdit.ps", param, listBack);
}

function saveSynonyms(id){
	
	if ( $.trim($('#synonyms_'+id).val())  != "" ) {
        if ( !patternHanEngNumComma.test($('#synonyms_' + id).val()) ) {
            Swal.fire({
            	type: "warning",
            	text: "동의어는 '한글','알파벳','숫자' 를 입력할 수 있으며  동의어 단어간은 구분자(,)를 입력해주세요,.\n특수문자/공백은 제외해주세요. (전체 글자길이 100자 이내)",
            });
            $('#synonyms_' + id).focus();
            return false;
        }
    }else{
    	$('#obj_'+id).find('div').remove();
    	return;
    }
	
	var param = {
            dicNo : id
            ,synonyms : $.trim($('#synonyms_'+ id).val())
            ,word : id
            ,add : 'N'
    };
    var serviceBack = function(data) {
		if (data.result.success ) {
			$('#obj_'+id).find('div').remove();
			toastr.success("저장 되었습니다.", "동의어 저장", {
				closeButton: true
				, positionClass: "toastr toast-bottom-left"
				, containerId: "toast-bottom-left",
			});
		}else{
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

function goRefRule(categoryNo){    
    $('#categoryNo').val(categoryNo);
    $('#ruleFrm').attr('target', '_blank');
	$('#ruleFrm').attr('action','${CTX_PATH}/classify/rule.ps').submit();
}

function deleteResultConfirm(){
	$('#delete-result-modal').modal('hide');
	$("#dicCheckBox").prop("checked",false);
	dicList($('#pageNo').val());
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
<!-- 삭제정보 modal area -->
<div id="delete-result-modal" class="modal fade" aria-hidden="true" data-bs-backdrop="static" data-bs-keyboard="false" aria-labelledby="api-req-modalLabel" style="z-index:1050;">
	<div class="modal-dialog modal-xl">
		<div class="modal-content">
			<div class="modal-header d-flex align-items-center">
				<h4 class="modal-title" id="apiReqModalLabel">삭제 결과 정보</h4>
				<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			</div>
			<div class="modal-body" style="margin-bottom:-20px;">
				<form class="row">
					<div class="col-4">
						<div class="card">
            				<div class="card-body">
              					<h4 class="card-title">정상삭제</h4>
              					<h6 class="card-subtitle lh-base">자동분류관리의 분류 룰에 참조되지 않아 정상 삭제된 데이터 입니다.</h6>
            				</div>
            				<div class="table-responsive">
              					<table class="table customize-table mb-0 v-middle">
                					<thead class="table-light">
                  						<tr>
                    						<th class="border-bottom border-top">삭제 데이터명</th>
                  						</tr>
                					</thead>
                					<tbody id="deletedDataHtml">
                					</tbody>
              					</table>
            				</div>
          				</div>
					</div>
					<div class="col-8">
						<div class="card" id="checkRuleData">
            				<div class="card-body">
              					<h4 class="card-title">삭제검토</h4>
              					<h6 class="card-subtitle lh-base">자동분류관리의 분류 룰에 참조되어있으며, 해당 데이터를 삭제하기 위해서는 분류 룰을 먼저 편집해주셔야 합니다.</h6>
            				</div>
            				<div class="table-responsive">
              					<table class="table customize-table mb-0 v-middle">
                					<thead class="table-light">
                  						<tr>
                    						<th class="border-bottom border-top">검토 데이터명</th>
                    						<th class="border-bottom border-top">참조 분류 룰</th>
                    						<th class="border-bottom border-top">Actions</th>
                  						</tr>
                					</thead>
                					<tbody id="checkRuleDataHtml">
                					</tbody>
              					</table>
            				</div>
          				</div>
					</div>
				</form>
			</div>

			<div class="modal-footer">
				<button class="btn btn-light-info text-info font-weight-medium" type="button" onclick="javascript:deleteResultConfirm();">확인</button>
			</div>
		</div>
		<!--
		</form>
		-->
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<form id="ruleFrm" name="ruleFrm" method="post" style="display:none;">
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
    <input type="hidden" name="categoryNo" id="categoryNo" value="" />
    <input type="hidden" name="isPopup" id="isPopup" value="true" />
</form>
<%@include file="/WEB-INF/views/common/end.jsp"%>