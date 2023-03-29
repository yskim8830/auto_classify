<%@include file="/WEB-INF/views/common/header.jsp"%>
<%@include file="/WEB-INF/views/common/menu.jsp"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" type="text/css" href="${CTX_PATH}/static/css/switch.css" />
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
				                사용자 관리
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
				                    <li class="breadcrumb-item"><a href="#">설정</a></li>
				                    <li class="breadcrumb-item active" aria-current="page">
				                        사용자 관리
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
			    <input type="hidden" name="pageNo" id="pageNo" value="0">
                <div class="row">
                    <div class="col-12">
                        <div class="card">
                            <div class="card-body">
                            	<div class="row">
	                                <div class="col-md-6">
	                                    <h5 class="card-title m-b-0"><i class="m-r-10 mdi mdi-code-greater-than"></i> 사용자 관리</h5>
	                                </div>
                                </div>
                                <div class="row mb-3">
                                	<c:if test="${fn:trim(loginInfo.adminYn) == 'y'}">
                                    <div class="col-md-12 text-end mt-4">
								        <a href="#" onclick="javascript:newAdd();" id="userAdd" class="btn btn-success" data-bs-toggle="modal" data-bs-target="#newAdd">신규등록</a>
								        <a href="#" id="userDel" class="btn btn-danger">삭제</a>
								    </div>
								    </c:if>
                                </div>
	                            <div class="row">
	                                 <div class="col-md-12">
	                                    <table id="zero_config" class="table">
	                                    <thead class="thead-light">
	                                    <tr>
	                                        <th>
                                                <input type="checkbox" id="userCheckBox" />
                                                <span class="checkmark"></span>
	                                        </th>
	                                        <th scope="col">사용자ID</th>
	                                        <th scope="col">사용자명</th>
                                            <th scope="col">등록자</th>
                                            <th scope="col">수정자</th>
                                            <th scope="col">등록/수정일</th>
	                                        <th scope="col">관리자여부</th>
	                                        <th scope="col">사용여부</th>
	                                        <th scope="col">Action</th>
	                                    </tr>
	                                    </thead>
	                                    <tbody id="tbodyUserList">
	                                    </tbody>
	                                    </table>
	                                 </div>
	                            </div>
	                            <div class="row">
	                                <div class="col-md-3">
	                                        <strong> 총 <span id="totalCnt">0</span>건</strong>
	                                </div>
	                                <div class="col-md-9">
	                                   <div class="dataTables_paginate paging_simple_numbers" id="zero_config_paginate">
	                                       <ul class="pagination justify-content-end" id="userPagination" >
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
<!-- 사용자 정보 Template Start	 -->
<!-- 사용자 정보 리스트 출력  -->
<script id="userListRowTmpl" type="text/x-jquery-tmpl">
	{{each(i, gUList) userList}}
		<tr id="userListRow\${i}">
			<td>
			{{if (gUList.adminYn != 'y' && gUList.userId != '<c:out value="${fn:trim(loginInfo.userId)}"/>') && gUList.userId != 'promanager'}}
				<input type="checkbox" name="user_chk" value="\${gUList.userNo}" class="ml_5 radio gCodeChk" />
			{{/if}}
            </td>
			<td>\${gUList.userId}</td>
			<td>\${gUList.userNm}</td>
			<td>\${gUList.createUserNm}</td>
			<td>\${gUList.modifyUserNm}</td>
			<td>\${GetDateMMDDSS(gUList.modifyDate)}</td>
			<td>\${gUList.adminYn}</td>
			<td>\${gUList.useYn}</td>
			<td>
			    <a href="#" id="modify\${gUList.userNo}"  data-bs-toggle="modal" data-bs-target="#edit-event" class="btn btn-danger btn-sm">수정</a>
				<a href="#" id="modPno\${gUList.userNo}"  data-bs-toggle="modal" data-bs-target="#edit-pno" class="btn btn-danger btn-sm">암호변경</a>
            </td>
		</tr>
	{{/each}}
</script>
<!-- 그룹 정보 리스트 출력  -->
<script id="groupListRowTmpl" type="text/x-jquery-tmpl">
	{{each(i, gUList) groupList}}
		<div class="form-check mt-1 ms-3">
			<input class="form-check-input" type="radio" name="groupRadio" id="groupRadio\${i}" value="\${gUList.groupNo}">
			<label class="form-check-label" for="inlineRadio\${i}">\${gUList.groupNm} (\${gUList.group})</label>
		</div>
	{{/each}}
</script>

    <!-- 신규 등록 팝업 Start  -->
    <div class="modal fade" id="newAdd" tabindex="-1" data-bs-backdrop="static" aria-labelledby="addModalLabel">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header d-flex align-items-center">
                    <h5 class="modal-title" id="addModalLabel">신규 등록</h5>
	                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
	                <div class="mb-3">
	                    <label for="fname" class="control-label">사용자ID</label>
	                    <input id="userId" type="text" data-toggle="tooltip" title="사용자ID를 입력하세요" class="form-control"  placeholder="예) admin"/>
	                    <span style="font-size:9px;"> 2~12자 이하</span>
	                </div>
	                <div class="mb-3">
	                    <label for="fname" class="control-label">사용자명</label>
	                    <input id="userNm" type="text" data-toggle="tooltip" title="사용자명을 입력하세요" class="form-control"  placeholder="예) 홍길동"/>
	                    <span style="font-size:9px;"> 2~12자 이하</span>
	                </div>
	                <div class="mb-3">
	                    <label for="fname" class="control-label">비밀번호</label>
                        <input id="pno" type="password" data-toggle="tooltip" title="비밀번호를 입력하세요" class="form-control"  placeholder=""/>
                        <span style="font-size:9px;">영문+숫자+특수기호 혼합한 8~15자 이하</span>
	                </div>
	                <c:if test="${fn:trim(loginInfo.adminYn) == 'y'}">
	                <div class="mb-3">
	                    <label for="fname" class="col-sm-3 text-right control-label col-form-label">계정사용여부</label>
                        <input id="useYn1" name="useYn" type="radio" value="y" checked="checked"/><span> 사용</span>
                        <input id="useYn2" name="useYn" type="radio" value="n"/><span> 사용안함</span>
	                </div>
	                <div class="mb-3">
	                    <label for="fname" class="col-sm-3 text-right control-label col-form-label">관리자여부</label>
                        <input id="adminYn1" name="adminYn" type="radio" value="y" /><span> Y</span>
                        <input id="adminYn2" name="adminYn" type="radio" value="n" checked="checked"/><span> N</span>
	                </div>
	                </c:if>
	                <div class="mb-3">
	                	<label for="fname" class="col-sm-3 text-right control-label col-form-label">그룹</label>
	                	<div class="overflow-auto mb-md-0 mr-md-3 bg-light" style="max-height:100px;" id="tbodyGroupList">
						</div>
	                </div>
	                <input id="useYn_check" name="useYn_check" type="hidden" value="y" />
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-success" id="save" aria-hidden="false">저장</button>
                	<button type="button" class="btn btn-light-danger text-danger font-weight-medium" data-bs-dismiss="modal">취소</button>
                </div>
            </div>
        </div>
    </div>
    <!-- 신규 등록 팝업 End  -->


    <!-- 수정 팝업 Start  -->
    <div class="modal fade" id="edit-event" tabindex="-1" data-bs-backdrop="static" aria-labelledby="editModalLabel">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header d-flex align-items-center">
                    <h5 class="modal-title" id="editModalLabel">수정</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body" id="editDiv"></div>
                    <script id="editRowTmpl" type="text/x-jquery-tmpl">
                    {{each(i, gUList) userList}}
					<div class="row">
                        <div class="col-md-12 mb-3">
                            <label for="fname" class="col-sm-3 text-right control-label col-form-label">사용자ID</label>
                            <input id="userId\${gUList.userNo}" value="\${gUList.userId}" type="text" data-toggle="tooltip" title="사용자ID를 입력하세요" class="form-control"  disabled="disabled"/>
                        </div>
                        <div class="col-md-12 mb-3">
                            <label for="fname" class="col-sm-3 text-right control-label col-form-label">사용자명</label>
                            <input id="userNm\${gUList.userNo}" value="\${gUList.userNm}" type="text" data-toggle="tooltip" title="사용자명을 입력하세요" class="form-control"  placeholder="예) 홍길동"/><span style="font-size:9px;"> 2~12자 이하</span>
                        </div>
                        <c:if test="${fn:trim(loginInfo.adminYn) == 'y'}">
                        <div class="col-md-12 mb-3">
                            <label for="fname" class="col-sm-3 text-right control-label col-form-label">계정사용여부</label>
                            <input id="useYn1_\${gUList.userNo}" name="useYn\${gUList.userNo}" type="radio" value="y" {{if gUList.useYn == "y" }}checked="checked"{{/if}}/> 사용
							<input id="useYn2_\${gUList.userNo}" name="useYn\${gUList.userNo}" type="radio" value="n" {{if gUList.useYn != "y" }}checked="checked"{{/if}}/> 사용안함
						</div>
						<div class="col-md-12 mb-3">
	                    	<label for="fname" class="col-sm-3 text-right control-label col-form-label">관리자여부</label>
                        	<input id="adminYn1_\${gUList.userNo}" name="adminYn\${gUList.userNo}" type="radio" value="y" {{if gUList.adminYn == "y" }} checked="checked"{{/if}}/><span> Y</span>
                        	<input id="adminYn2_\${gUList.userNo}" name="adminYn\${gUList.userNo}" type="radio" value="n" {{if gUList.adminYn != "y" }} checked="checked"{{/if}}/><span> N</span>
	                	</div>
						<div class="col-md-12 mb-3">
                            <label for="fname" class="col-sm-3 text-right control-label col-form-label">계정잠김</label>
            				<input id="loginCount1_\${gUList.userNo}" name="loginCount\${gUList.userNo}" type="radio" value="0" {{if gUList.loginCount < 5}}checked="checked"{{/if}}/> 사용
    						<input id="loginCount2_\${gUList.userNo}" name="loginCount\${gUList.userNo}" type="radio" value="5" {{if gUList.loginCount >= 5}}checked="checked"{{/if}}/> 잠김
                        </div>
                        </c:if>
						<div class="col-md-12 mb-3">
	                		<label for="fname" class="col-sm-3 text-right control-label col-form-label">그룹</label>
	                		<div class="overflow-auto mb-md-0 mr-md-3 bg-light" style="max-height:100px;" id="tbodyGroupList2">
							</div>
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
                        <input id="adminY\${gUList.userNo}" name="adminYn\${gUList.userNo}" type="hidden" value="\${gUList.adminYn}"/>
                        <input id="groupNo" name="groupNo" type="hidden" value="\${gUList.groupNo}"/>
                    </div>
                    <div class="modal-footer">
						<button type="button" class="btn btn-success" id="edit\${gUList.userNo}" aria-hidden="false">저장</button>
                		<button type="button" class="btn btn-light-danger text-danger font-weight-medium" data-bs-dismiss="modal">취소</button>
                    </div>
                {{/each}}
            </script>
            </div>
        </div>
    </div>
    
    <!-- 암호 수정 팝업 Start  -->
    <div class="modal fade" id="edit-pno" tabindex="-1" data-bs-backdrop="static" aria-labelledby="editPnoModalLabel">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header d-flex align-items-center">
                    <h5 class="modal-title" id="editPnoModalLabel">패스워드 수정</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body" id="editPnoDiv"></div>
                <script id="editPnoRowTmpl" type="text/x-jquery-tmpl">
                    {{each(i, gUList) userList}}
                        {{if gUList.userId == '<c:out value="${fn:trim(loginInfo.userId)}"/>' || '<c:out value="${fn:trim(loginInfo.adminYn)}"/>' == 'n' }}
                        <div class="mb-3">
                            <label for="fname" class="col-sm-3 text-right control-label col-form-label">현재 비밀번호</label>
                            <div class="col-sm-9">
                                <input id="nowpno\${gUList.userNo}" type="password" style="width:200px" value="" data-toggle="tooltip" title="현재 비밀번호를 입력하세요" class="form-control"  placeholder=""/>
                            </div>
                        </div>
                        {{/if}}
                        <div class="mb-3">
                            <label for="fname" class="col-sm-3 text-right control-label col-form-label">변경할 비밀번호</label>
                            <div class="col-sm-9">
                                <input id="pno\${gUList.userNo}" type="password" style="width:200px" value="" data-toggle="tooltip" title="비밀번호를 입력하세요" class="form-control"  placeholder=""/>
                                <span style="font-size:9px;">영문+숫자+특수기호 혼합한 8~15자 이하</span>
                            </div>
                        </div>
					</div>
					<input id="pnoUserId\${gUList.userNo}" name="pnoUserId\${gUList.userNo}" type="hidden" value="\${gUList.userId}"/>
					<div class="modal-footer">
						<button type="button" class="btn btn-success" id="pnoEdit\${gUList.userNo}" aria-hidden="false">저장</button>
                		<button type="button" class="btn btn-light-danger text-danger font-weight-medium" data-bs-dismiss="modal">취소</button>
                	{{/each}}
            </script>
            </div>
        </div>
    </div>
    <!-- 신규 등록 팝업 End  -->
<script type="text/javascript">
$(document).on("click","a[id^=modify]",function(e) {
    var id = $(this).attr("id");
    id = id.replace("modify","");
    btnViewContent(id);
});
$(document).on("click","a[id^=modPno]",function(e) {
    var id = $(this).attr("id");
    id = id.replace("modPno","");
    btnViewPno(id);
});

$(document).on("click","#save",function(e) {
    btnSaveUserProc();
});

$(document).on("click","button[id^=edit]",function(e) {
    var id = $(this).attr("id");
    id = id.replace("edit","");
    btnUserModProc(id,"");
});

$(document).on("click","button[id^=pnoEdit]",function(e) {
    var id = $(this).attr("id");
    id = id.replace("pnoEdit","");
    btnUserModProc(id,"pno");
});


$(document).on("click","input[id^=useYn]",function(e) {
	if($(this).val()=='n'){
		$('#useYn1').prop('checked', false);
		$('#useYn2').prop('checked', true);
		$('#useYn_check').val('n');
	}else{
		$('#useYn1').prop('checked', true);
		$('#useYn2').prop('checked', false);
		$('#useYn_check').val('y');
	}
    
});

$('#newAdd').on('shown.bs.modal', function() {
	$('#newAdd input:radio[name=groupRadio]').eq(0).attr("checked", true);
});

$('#edit-event').on('shown.bs.modal', function() {
	if($('#groupNo').val() != ""){
		$('#edit-event :input:radio[name=groupRadio]:input[value='+$('#groupNo').val()+']').attr("checked", true);
	}
});

//화면 로딩 처리 함수 
$(document).ready(function(){
	userList();
	//삭제 버튼 클릭 이벤트
	$('#userDel').click(function(){
		userDel();
	});
	//Checkbox 클릭 이벤트
	$("#userCheckBox").click(function(){
        if($("#userCheckBox").is(":checked")){
            $("input[type=checkbox]").prop("checked",true);
        }else{
        	$("input[type=checkbox]").prop("checked",false);
        }
    });

});

function newAdd() {
    $('#userId').val('');
    $('#userNm').val('');
    $('#pno').val('');
    groupList('tbodyGroupList',''); //그룹 목록 호출
}

//  목록
function userList(pageNo){
	pageNo = pageNo || 1;
	$('#pageNo').val(pageNo);
	var param = { pageNo : pageNo }; 
	
    var userListBack = function(data) {
		console.log(data);
    	$("#userCheckBox").prop("checked",false);
		var paginationHtml = getPaginationHtml(pageNo, data.totalCnt, '10', '10', 'userList');
		
		$('#tbodyUserList').html('');
		$("#userListRowTmpl").tmpl(data).appendTo("#tbodyUserList");
		$("#totalCnt").html(data.totalCnt);
		$("#userPagination").html( paginationHtml );
	};
	callAjax("/system/userAjax.ps", param, userListBack);
}


// 편집 버튼 클릭 시 이벤트
function btnUserMod(codeId){
	$("#userRow" + codeId).css("display","");
}

// 등록 창에서의 취소버튼 클릭 이벤트
function btnUserAddCancel(){
	$('#userRow').css("display","none");
}

// 신규등록 폼에서의 저장 버튼 클릭 이벤트
function btnSaveUserProc() {
	var param = {
			userId : $.trim($('#userId').val())
	};
	//console.log(data);
	if ( ! validation() ) {
		return;
	}
	
	var serviceBack = function(data) {
		if ( data && data.success ) {
			saveProc();
			$('#newAdd').modal('hide');
		}
		else {
			if ( data && data.msg ) {
				alert(data.msg);
			} 
			else {
				alert('저장이 실패하였습니다.');
			}
		}
    }; 
	
	callAjaxAsync("/system/dupUser.ps", param, serviceBack);
}

function saveProc() {
	var param = {
			userId : $.trim($('#userId').val())
			,userNm : $.trim($('#userNm').val())
			,useYn : $(':input:radio[name=useYn]:checked').val()
			,adminYn : $(':input:radio[name=adminYn]:checked').val()
			,pno : $.trim($('#pno').val())
			,useYn : $.trim($('#useYn_check').val())
			,groupNo : $('#newAdd :input:radio[name=groupRadio]:checked').val() 
	};
	//console.log(data);
	if ( ! validation() ) {
		return;
	}
	var serviceBack = function(data) {
		if (data.success ) {
			userList();
		}else{
			if ( data && data.msg ) {
				alert(data.msg);
			} 
			else {
				alert('저장이 실패하였습니다.');
			}
		}
    }; 
	
    callAjaxAsync("/system/saveUser.ps", param, serviceBack);
}

// 편집 ROW의 수정 버튼 클릭 이벤트 
function btnUserModProc(id,type){
	var param = {
			userId : $.trim($('#userId'+ id).val())
			,userNm : $.trim($('#userNm'+ id).val())		
			,userNo : id //0이면 저장
			,useYn : $(':input:radio[name=useYn'+ id+']:checked').val()
			,adminYn : $(':input:radio[name=adminYn'+ id+']:checked').val()
			,loginCount : $(':input:radio[name=loginCount'+ id+']:checked').val()
			,groupNo : $('#edit-event :input:radio[name=groupRadio]:checked').val() 
			
	};
	if(type == "pno"){
		param = {
			userId : $.trim($('#pnoUserId'+ id).val())
			,userNo : id
			,pno : $.trim($('#pno'+ id).val())
			,nowpno : $.trim($('#nowpno'+ id).val())
		};
	}
	if ( ! validation(id) ) {
		return;
	}
	var serviceBack = function(data) {
		if (data.success ) {
			userList();
			if(type == "pno"){
				Swal.fire({
					type: "info",
					text: "암호가 변경 되었습니다.",
				});
				$('#edit-pno').modal('hide');
			}else{
				Swal.fire({
					type: "warning",
					text: "수정된 사용자 정보를 확인 하려면 재 로그인 해주세요.",
				});
				$('#edit-event').modal('hide');
			}
		}else{
			if ( data && data.errorMsg ) {
				alert(data.errorMsg);
			} 
			else {
				alert('수정이 실패하였습니다.');
			}
		}
    }; 
	
    callAjaxAsync("/system/saveUser.ps", param, serviceBack);
}

// 저장/수정 시 입력값 검증
function validation(id) {
	var result = true;
	 var pattern1 = /[0-9]/;
     var pattern2 = /[a-zA-Z]/;
     var pattern3 = /[~!@\#$%<>^&*]/;     // 원하는 특수문자 추가 제거
     var pw_msg = "";
     var gIdPattern = /^[a-zA-Z0-9]+$|^$/g;

    var ids = '';
    if(id != undefined){
        ids = id
    }

    var userId = $('#userId'+ ids).val();
    var userNm = $('#userNm'+ ids).val();
    var 	pw = $('#pno'+ ids).val();
    if(typeof userId != "undefined" && userId != null && typeof userNm != "undefined" && userNm != null){
    	if ( !patternEngNumNoSpRange.test($('#userId' + ids).val()) ) {
    		Swal.fire({
    			type: "warning",
    			text: "사용자ID는 '알파벳','숫자'를 포함하여 2~12자까지 입력해주세요.\n특수문자/공백은 제외해주세요.",
    		});
    		$('#userId' + ids).focus();
    		return false;
    	}
    	if ( !patternHanEngNumNoSpRange.test($('#userNm' + ids).val()) ) {
    		Swal.fire({
    			type: "warning",
    			text: "사용자명은 '한글','알파벳','숫자'를 포함하여 2~12자까지 입력해주세요.\n특수문자/공백은 제외해주세요.",
    		});
    		$('#userNm' + ids).focus();
    		return false;
    	}
    	result = checkNotEmpty(
				[
					 ["userId" + ids  , "사용자 ID를 입력해주세요."]
					,["userNm" + ids, "사용자명을 입력해주세요."]
				]
		);	
    }
	if(typeof pw != "undefined" && pw != null){
		if(!pattern1.test(pw)||!pattern2.test(pw)||!pattern3.test(pw)||pw.length<8){
	        Swal.fire({
	        	type: "warning",
	        	text: "비밀번호는 영문+숫자+특수기호 혼합한  8~15 자 이하로 구성하여야 합니다.",
	        });
	        return false;
	    }

		if(pw.length > 15){
	        Swal.fire({
	        	type: "warning",
	        	text: "비밀번호는 8자에서 15자 이하로 구성해야 합니다.",
	        });
	        return false;
	    }
		result = checkNotEmpty(
				[
					 ["pno" + ids, "비밀번호을 입력해주세요."]
				]
		);
	}
	return result;

}


//사용자  삭제
function userDel() {
	if ( $('.gCodeChk:checked').length == 0 ) {
		Swal.fire({
			type: "warning",
			text: "삭제하려는 사용자를  체크 하세요.",
		});
		return;
	}
	var param = { userNo : $('.gCodeChk:checked')
			.map(function(){ return $(this).val();}).toArray().join(":")	};
	
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
			callAjaxAsync("/system/delUser.ps", param, serviceBack);
		} else {
			return;
		}
	});
	var serviceBack = function(data) {
		console.log(data);
		if (data.success ) {
			userList();
		}else{
			if ( data && data.msg ) {
				alert(data.msg);
			} 
			else {
				alert('삭제 처리가 실패하였습니다.');
			}
		}
    };
}

//정보변경 모달
function btnViewContent( id ) {
    var param = {
            userNo : id
    };
    $('#editDiv').html('');

    var editBack = function(data) {
        $("#editRowTmpl").tmpl(data).appendTo("#editDiv");
        var grpId = data.userList[0].groupNo;
        groupList('tbodyGroupList2',grpId);
    };
    callAjax("/system/userAjaxEdit.ps", param, editBack);
}

//암호변경 모달
function btnViewPno( id ) {
    var param = {
            userNo : id
    };
    $('#editPnoDiv').html('');

    var editBack = function(data) {
        $("#editPnoRowTmpl").tmpl(data).appendTo("#editPnoDiv");
    };
    callAjax("/system/userAjaxEdit.ps", param, editBack);
}

//그룹 목록
function groupList(id,grpVal){
	var param = { pageNo : "1"
			,lineNo : "100"
			,useYn : "y"
	}; 
	
    var listBack = function(data) {
		$('#'+ id).html('');
		$("#groupListRowTmpl").tmpl(data).appendTo('#'+ id);
		if(grpVal == ''){
			$('#'+ id +' input:radio[name=groupRadio]').eq(0).attr("checked", true);						
		}else{
			$('#'+ id +' input:radio[name=groupRadio][value='+grpVal+']').prop("checked", true);
		}
	};
	callAjax("/system/groupAjax.ps", param, listBack);
}

</script>

<%@include file="/WEB-INF/views/common/end.jsp"%>