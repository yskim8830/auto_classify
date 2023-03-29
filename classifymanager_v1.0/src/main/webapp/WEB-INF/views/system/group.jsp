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
				                사용자그룹 관리
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
				                        사용자그룹 관리
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
                <input type="hidden" name="serviceNo" id="serviceNo" value="">
                
                <div class="row">
                    <div class="col-12">
                        <div class="card">
                            <div class="card-body">
                            	<div class="row">
	                                <div class="col-md-6">
	                                    <h5 class="card-title m-b-0"><i class="m-r-10 mdi mdi-code-greater-than"></i> 사용자그룹 관리</h5>
	                                </div>
                                </div>
                                <div class="row mb-3">
                                    <c:if test="${fn:trim(loginInfo.adminYn) == 'y'}">
                                    <div class="col-md-12 text-end mt-4">
								        <a href="#" onclick="javascript:newAdd();" id="userAdd" class="btn btn-success" data-bs-toggle="modal" data-bs-target="#newAdd">신규등록</a>
								        <a href="#" id="groupDel" class="btn btn-danger">삭제</a>
								    </div>
								    </c:if>
                                </div>
                                <div class="row">
	                                 <div class="col-md-12">
	                                    <table id="zero_config" class="table">
	                                    <thead class="thead-light">
	                                    <tr>
	                                        <th>
                                                <input type="checkbox" id="grpCheckBox" />
                                                <span class="checkmark"></span>
	                                        </th>
	                                        <th scope="col">그룹ID</th>
	                                        <th scope="col">그룹명</th>
	                                        <th scope="col">사이트</th>
                                            <th scope="col">등록자</th>
                                            <th scope="col">수정자</th>
                                            <th scope="col">등록/수정일</th>
	                                        <th scope="col">사용여부</th>
	                                        <th scope="col">Action</th>
	                                    </tr>
	                                    </thead>
	
	                                    <tbody id="tbodygroupList">
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
	                                       <ul class="pagination justify-content-end" id="pagination" >
	                                       </ul>
	                                   </div>
	                                </div>
	                            </div>
					        </div>
    					</div>
					</div>
					<c:if test="${fn:trim(loginInfo.adminYn) == 'y'}">
					<div class="col-12">
                        <div class="card">
                            <div class="card-body">
                                 <h5 class="card-title m-b-0"> <i class="m-r-10 mdi mdi-code-greater-than"></i> 사이트권한</h5>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table">
                                        <thead class="thead-light">
                                        <tr>
									    <th scope="col">사이트ID</th>
										<th scope="col">사이트명</th>
										<th scope="col">권한여부</th>
										<th scope="col">권한추가/변경</th>
                                        </tr>
                                        </thead>
                                        <tbody id="tbodySiteAuthList">
                                        </tbody>
                                    </table>
                                </div>
                             </div>
                        </div>
                    </div>
                    </c:if>
                    <c:if test="${fn:trim(loginInfo.adminYn) == 'y'}">
                    <div class="col-12">
                        <div class="card">
                            <div class="card-body">
                                 <h5 class="card-title m-b-0"> <i class="m-r-10 mdi mdi-code-greater-than"></i> 메뉴권한</h5>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table">
                                        <thead class="thead-light">
                                        <tr>
									    <th scope="col">메뉴ID</th>
										<th scope="col">메뉴명</th>
										<th scope="col">메뉴전체 경로</th>
										<th scope="col">메뉴URL</th>
										<th scope="col">사용여부/변경</th>
                                        </tr>
                                        </thead>
                                        <tbody id="tbodyAuthList">
                                        </tbody>
                                    </table>
                                </div>
                             </div>
                        </div>
                    </div>
                    </c:if>
                </div>
                </form>
            </div>
<%@include file="/WEB-INF/views/common/footer.jsp"%>
<!-- 사용자 정보 Template Start	 -->
<!-- 사용자 정보 리스트 출력  -->
<script id="groupListRowTmpl" type="text/x-jquery-tmpl">
	{{each(i, gUList) groupList}}
		<tr id="groupListRow\${i}">
			<td>
				<input type="checkbox" name="user_chk" value="\${gUList.groupNo}" class="ml_5 radio gCodeChk" />
            </td>
			<td>\${gUList.group}</td>
			<td>\${gUList.groupNm}</td>
			<td>{{each(j, sUList) gUList.siteList}}\${sUList.siteNm}{{if gUList.siteList.length != j+1 }},{{/if}}{{/each}}</td>
			<td>\${gUList.createUserNm}</td>
			<td>\${gUList.modifyUserNm}</td>
			<td>\${GetDateMMDDSS(gUList.modifyDate)}</td>
			<td>\${gUList.useYn}</td>
			<td>
			    <a href="#" id="modify\${gUList.groupNo}"  data-bs-toggle="modal" data-bs-target="#edit-event" class="btn btn-danger btn-sm">수정</a>
				<c:if test="${fn:trim(loginInfo.adminYn) == 'y'}">
				&nbsp;
				<a href="#" id="authsiteset\${gUList.groupNo}" class="btn btn-danger btn-sm" >사이트권한설정</a>
				</c:if>
            </td>
		</tr>
		<input type="hidden" id="authSite\${gUList.groupNo}" value="{{each(j, sUList) gUList.siteList}}\${sUList.siteNo}{{if gUList.siteList.length != j+1 }},{{/if}}{{/each}}" />
	{{/each}}
</script>
<!-- 사이트정보 리스트 출력  -->
<script id="authSiteListRowTmpl" type="text/x-jquery-tmpl">
	{{each(i, list) siteList}}
		<tr id="authSiteListRow\${list.siteNo}" >
			<td>\${list.site}</td>
			<td>\${list.siteNm}</td>
			<td>
					{{if auth.indexOf((list.siteNo).toString()) >= 0}}
					<label class="switch">
                    	<input type="checkbox" id="authSiteYn\${list.siteNo}" name="authSiteYn" data-toggle="toggle" checked  value="\${list.siteNo}" >
                    	<span class="slider rround"></span>
                    </label>
                    <p id="off\${list.siteNo}" style="display:none;">OFF</p>
                    <p id="on\${list.siteNo}">ON</p>
                    {{else}}
					<label class="switch">
                    	<input type="checkbox" id="authSiteYn\${list.siteNo}" name="authSiteYn" data-toggle="toggle"  value="\${list.siteNo}" >
                    	<span class="slider rround"></span>
                    </label>
                    <p id="off\${list.siteNo}">OFF</p>
                    <p id="on\${list.siteNo}" style="display:none;">ON</p>
					{{/if}}
		    </td>
			<td style="border-right: none;">
				<c:if test="${fn:trim(loginInfo.adminYn) == 'y'}">
					&nbsp;<a href="#" id="authset\${list.siteNo}" name="authset"   class="btn btn-danger btn-sm">메뉴권한설정</a>
                </c:if>
			</td>
		</tr>
 	{{/each}}
</script>
<!-- 메뉴정보 리스트 출력  -->
<script id="authListRowTmpl" type="text/x-jquery-tmpl">
	{{each(i, list) authList}}
		<tr id="authListRow\${list.menuId}" {{if list.menuId % 100 == 0}} style="background-color:#f4f4f4"{{/if}}>
			<td>\${list.menuId}</td>
			<td id="\${list.menuId}">\${list.menuNm}</td>
			<td class="align_l">\${list.fullNm}</td>
			<td class="align_l">\${list.menuUrl}</td>
			<td title="\${list.menuId}">
                    {{if list.auth != 0}}
                            <label class="switch">
                                <input type="checkbox" id="authYn\${list.menuId}" name="authYn\${list.parentMenuId}" data-toggle="toggle" checked>
                                <span class="slider rround"></span>
                            </label>
                            <p id="off\${list.menuId}" style="display:none;">OFF</p>
                            <p id="on\${list.menuId}">ON</p>

                    {{else}}
                            <label class="switch">
                                <input type="checkbox" id="authYn\${list.menuId}" name="authYn\${list.parentMenuId}" data-toggle="toggle">
                                <span class="slider rround"></span>
                            </label>
                            <p id="off\${list.menuId}">OFF</p>
                            <p id="on\${list.menuId}" style="display:none;">ON</p>
                    {{/if}}

		    </td>
		</tr>
 	{{/each}}
</script>

    <!-- 신규 등록 팝업 Start  -->
    <div class="modal fade" id="newAdd" tabindex="-1" data-bs-backdrop="static" role="dialog" aria-labelledby="addModalLabel">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
	            <div class="modal-header d-flex align-items-center">
	                <h5 class="modal-title" id="addModalLabel">신규 등록</h5>
	                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
	            </div>
                <div class="modal-body">
                	<div class="mb-3">
	                    <label for="group" class="control-label">그룹 ID</label>
	                    <input type="text" class="form-control" id="group" data-toggle="tooltip" title="그룹 ID를 입력하세요"  placeholder="예) group1"/>
	                    <span style="font-size:9px;">2~12자 이하</span>
	                </div>
	            	<div class="mb-3">
	                    <label for="groupNm" class="control-label">그룹 명</label>
	                    <input type="text" class="form-control" id="groupNm" data-toggle="tooltip" title="그룹 명을 입력하세요"  placeholder="예) 그룹1"/>
	                    <span style="font-size:9px;">2~12자 이하</span>
	                </div>
	                <div class="mb-3">
	                    <label for="fname" class="col-sm-3 text-right control-label col-form-label">사용여부</label>
                        <input id="useYn1" name="useYn" type="radio" value="y" checked="checked"/><span> 사용</span>
                        <input id="useYn2" name="useYn" type="radio" value="n"/><span> 사용안함</span>
	                </div>
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
    <div class="modal fade" id="edit-event" tabindex="-1" data-bs-backdrop="static" role="dialog" aria-labelledby="editModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header d-flex align-items-center">
	                <h5 class="modal-title" id="editModalLabel">수정</h5>
	                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
	            </div>
                <div class="modal-body" id="editDiv">
                </div>
                    <script id="editRowTmpl" type="text/x-jquery-tmpl">
                    {{each(i, gUList) groupList}}
					<div class="row">
						<div class="col-md-12 mb-3">
	                    	<label for="group" class="control-label">그룹 ID</label>
	                    	<input type="text" class="form-control" id="group\${gUList.groupNo}" value="\${gUList.group}" data-toggle="tooltip" title="그룹 ID를 입력하세요"  placeholder="예) group1" disabled="disabled"/>
	                    	<span style="font-size:9px;">2~12자 이하</span>
	                	</div>
	            		<div class="col-md-12 mb-3">
	                    	<label for="groupNm" class="control-label">그룹 명</label>
	                    	<input type="text" class="form-control" id="groupNm\${gUList.groupNo}"  value="\${gUList.groupNm}" data-toggle="tooltip" title="그룹 명을 입력하세요"  placeholder="예) 그룹1"/>
	                    	<span style="font-size:9px;">2~12자 이하</span>
	                	</div>
	                	<div class="col-md-12 mb-3">
	                    	<label for="fname" class="col-sm-3 text-right control-label col-form-label">사용여부</label>
							<input id="useYn1_\${gUList.groupNo}" name="useYn\${gUList.groupNo}" type="radio" value="y" {{if gUList.useYn == "y" }}checked="checked"{{/if}}/> 사용
							<input id="useYn2_\${gUList.groupNo}" name="useYn\${gUList.groupNo}" type="radio" value="n" {{if gUList.useYn != "y" }}checked="checked"{{/if}}/> 사용안함
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
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-success" id="edit\${gUList.groupNo}" aria-hidden="false">저장</button>
                		<button type="button" class="btn btn-light-danger text-danger font-weight-medium" data-bs-dismiss="modal">취소</button>
                    </div>
                {{/each}}
            </script>
            </div>
        </div>
    </div>
    <!-- 신규 등록 팝업 End  -->

<script type="text/javascript">
var grpId, siteId;
//정보조회
$(document).on("click","a[id^=modify]",function(e) {
    var id = $(this).attr("id");
    id = id.replace("modify","");
    btnViewContent(id);
});

//신규저장
$(document).on("click","#save",function(e) {
    btnSaveGroupProc();
});

//변경저장
$(document).on("click","button[id^=edit]",function(e) {
    var id = $(this).attr("id");
    id = id.replace("edit","");
    btnGroupModProc(id);
});
//사용자관리>권한설정 버튼 클릭 시(사이트 권한)
$(document).on("click","a[id^=authsiteset]",function(e) {
    var id = $(this).attr("id");
    id = id.replace("authsiteset","");
    grpId = id;
    authSiteList(id);
});

//사용자관리>권한설정 버튼 클릭 시(메뉴)
$(document).on("click","a[id^=authset]",function(e) {
  var id = $(this).attr("id");
  id = id.replace("authset","");
  siteId = id;
  authList(id);
});


//사용자관리>사이트권한 사용여부/변경 클릭 시
$(document).on("click","input[id^=authSiteYn]",function(e) {
  var id = $(this).attr("id");
  id = id.replace("authSiteYn","");
  btnEditAuthSite(id,grpId);
});

//사용자관리>메뉴권한정보 사용여부/변경 클릭 시
$(document).on("click","input[id^=authYn]",function(e) {
    var id = $(this).attr("id");
    id = id.replace("authYn","");
    btnEditAuthMenu(id,siteId);
});


//화면 로딩 처리 함수 
$(document).ready(function(){
	groupList();

	//삭제 버튼 클릭 이벤트
	$('#groupDel').click(function(){
		groupDel();
	});
	//Checkbox 클릭 이벤트
	$("#grpCheckBox").click(function(){
        if($("#grpCheckBox").is(":checked")){
            $("input[type=checkbox]").prop("checked",true);
        }else{
        	$("input[type=checkbox]").prop("checked",false);
        }
    });

});

function newAdd() {
    $('#group').val('');
    $('#groupNm').val('');
}

//  목록
function groupList(pageNo){
	$('#tbodyAuthList').html('');
	pageNo = pageNo || 1;
	$('#pageNo').val(pageNo);
	var param = { pageNo : pageNo
				,lineNo:"10"
				,siteList : "Y"
				}; 
	
    var groupListBack = function(data) {
    	$("#grpCheckBox").prop("checked",false);
		var paginationHtml = getPaginationHtml(pageNo, data.totalCnt, '10', '10', 'groupList');
		$('#tbodygroupList').html('');
		$("#groupListRowTmpl").tmpl(data).appendTo("#tbodygroupList");
		$("#totalCnt").html(data.totalCnt);
		$("#pagination").html( paginationHtml );
	};
	
	callAjax("/system/groupAjax.ps", param, groupListBack);
	
}


// 신규등록 폼에서의 저장 버튼 클릭 이벤트
function btnSaveGroupProc() {
	var param = {
			group : $.trim($('#group').val())
	};
	if ( ! validation() ) {
		return;
	}
	var serviceBack = function(data) {
		if ( data && data.success ) {
			saveProc();
		}
		else {
			if ( data && data.msg ) {
				alert(data.msg);
			}else {
				alert('저장이 실패하였습니다.');
			}
		}
    }; 
	
	callAjaxAsync("/system/dupGroup.ps", param, serviceBack);
}

function saveProc() {
	var param = {
			group : $.trim($('#group').val())
			,groupNm : $.trim($('#groupNm').val())
			,useYn : $(':input:radio[name=useYn]:checked').val()
			,add : "Y"
	};
	var serviceBack = function(data) {
		if (data.success ) {
			location.reload();
		}else{
			if ( data && data.msg ) {
				alert(data.msg);
			} 
			else {
				alert('저장이 실패하였습니다.');
			}
		}
    }; 
	
    callAjaxAsync("/system/saveGroup.ps", param, serviceBack);
}

// 편집 ROW의 수정 버튼 클릭 이벤트
function btnGroupModProc(id){
	var param = {
			groupNo : id
			,group : $.trim($('#group'+ id).val())
			,groupNm : $.trim($('#groupNm'+ id).val())
			,useYn : $(':input:radio[name=useYn'+ id+']:checked').val()
			,add : "N"
	};
	if ( ! validation(id) ) {
		return;
	}
	var serviceBack = function(data) {
		if (data.success ) {
			location.reload();
		}else{
			if ( data && data.msg ) {
				alert(data.msg);
			} 
			else {
				alert('저장이 실패하였습니다.');
			}
		}
    }; 
	
    callAjaxAsync("/system/saveGroup.ps", param, serviceBack);

}

// 그룹 코드 저장/수정 시 입력값 검증
function validation(id) {
	 var pattern1 = /[0-9]/;
     var pattern2 = /[a-zA-Z]/;
     var pattern3 = /[~!@\#$%<>^&*]/;     // 원하는 특수문자 추가 제거
     var pw_msg = "";
     var gIdPattern = /^[a-zA-Z0-9]+$|^$/g;

    var ids = '';
    if(id != undefined){
        ids = id
    }

    var group = $('#group'+ ids).val();
    var groupNm = $('#groupNm'+ ids).val();

	if ( !patternEngNumNoSpRange.test($('#group' + ids).val()) ) {
		Swal.fire({
			type: "warning",
			text: "group ID는 '알파벳','숫자'를 포함하여 2~12자까지 입력해주세요.\n특수문자/공백은 제외해주세요.",
		});
		$('#group' + ids).focus();
		return false;
	}
	if ( !patternHanEngNumNoSpRange.test($('#groupNm' + ids).val()) ) {
		Swal.fire({
			type: "warning",
			text: "그룹명은 '한글','알파벳','숫자'를 포함하여 2~12자까지 입력해주세요.\n특수문자/공백은 제외해주세요.",
		});
		$('#groupNm' + ids).focus();
		return false;
	}
	
	return checkNotEmpty(
			[
				 ["group" + ids  , "그룹 ID를 입력해주세요."]
				,["groupNm" + ids, "그룹 명을 입력해주세요."]
			]
	);

}


//그룹삭제
function groupDel() {
	if ( $('.gCodeChk:checked').length == 0 ) {
		Swal.fire({
			type: "warning",
			text: "삭제하려는 항목을 체크 하세요.",
		});
		return;
	}
	var param = { groupNo : $('.gCodeChk:checked')
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
			callAjaxAsync("/system/delGroup.ps", param, serviceBack);
		} else {
			return;
		}
	});
	var serviceBack = function(data) {
		data = data.delData;
		if (data.success ) {
			location.reload();
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

//사이트 목록 조회
function authSiteList(id) {
	$('#tbodySiteAuthList').html('');
	$('#tbodyAuthList').html('');
	
	var param = { 
			groupNo : id
			,useYn : "y"
			,lineNo : 100
			}; 
	
	var authListBack = function(data) {
		data.auth = $('#authSite'+id).val().split(",");
		$("#authSiteListRowTmpl").tmpl(data).appendTo("#tbodySiteAuthList");		//상세코드 리스트 출력
		
		var offset = $('#tbodySiteAuthList').offset();
		$('html').animate({scrollTop : offset.top}, 1);
	};
	
	callAjax("/system/siteAjax.ps", param, authListBack);

}  

//메뉴 조회
function authList(id) {
	$('#tbodyAuthList').html('');
	var param = { 
				  siteNo : id
				}; 
	var authListBack = function(data) {
																		//상세코드 신규 등록 폼에서  ID 값 적용
		$("#authListRowTmpl").tmpl(data).appendTo("#tbodyAuthList");		//상세코드 리스트 출력

		var offset = $('#tbodyAuthList').offset();
		$('html').animate({scrollTop : offset.top}, 1);
 	};
	callAjax("/system/siteAuthAjax.ps", param, authListBack);

}   

//그룹편집정보 확인
function btnViewContent( id ) {
    var url  = "/system/groupAjaxEdit.ps";
    var param = {
            groupNo : id
    };
    $('#editDiv').html('');

    var editBack = function(data) {
        $("#editRowTmpl").tmpl(data).appendTo("#editDiv");
    };
    callAjax(url, param, editBack);
}

function btnEditAuthSite(siteNo,groupNo) {
    var param = {
    		siteNo : siteNo
    		,groupNo : groupNo
    };
    var listBack = function(data) {
    	if ( data && data.success ) {
    		groupList();
    		Swal.fire({
    			type: "info",
    			text: "현재 세션의 권한은 재 로그인시 적용됩니다.",
    		});
    	} else {
			if ( data && data.msg ) {
				alert(data.msg);
			} 
			else {
				alert('수정이 실패하였습니다.');
			}				
		}
    };

	callAjax("/system/saveAuthGroupSite.ps", param, listBack);
}


function btnEditAuthMenu(menuId, siteNo){
	
    var menuIds = [];
    var sid = "#authYn" + menuId;
    var parentId = menuId-(menuId%100);

   	var isAuth = $(sid).prop('checked');
   	var authYn = isAuth ? "Y" : "N";
   	menuIds.push(menuId + "_" + authYn);
   	
    if ( menuId % 100 == 0 ) {
        var authYn2 = "";
        $("input[name^='authYn" + parentId + "']").each(function() {
            var mId = $(this).attr('id');
            mId = mId.replace('authYn','');
        	if(authYn == 'Y'){
        		$(this).prop('checked',true);
                menuIds.push(mId + "_Y");
        	}else{
        		$(this).prop('checked',false);
                menuIds.push(mId + "_N");
        	}
        });
    }
	saveAuthMenu(menuIds,siteNo);
};


function saveAuthMenu(menuIds,siteNo) {
    var params = {
                  menuIds : menuIds
                  ,siteNo : siteNo
    };
    var listBack = function(data) {
	};

	callAjax("/system/saveAuthMenu.ps", params, listBack);

}

</script>
<%@include file="/WEB-INF/views/common/end.jsp"%>