<%@include file="/WEB-INF/views/common/header.jsp"%>
<%@include file="/WEB-INF/views/common/menu.jsp"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
			                사이트 관리
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
			                        사이트 관리
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
	                                    <h5 class="card-title m-b-0"><i class="m-r-10 mdi mdi-code-greater-than"></i> 사이트 관리</h5>
	                                </div>
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
								            <option value="siteNm">사이트명순</option>
								        </select>
									</div>
									<c:if test="${fn:trim(loginInfo.adminYn) == 'y'}">
                                    <div class="col-md-6 text-end mt-4">
								        <a onclick="javascript:newAdd();" class="btn btn-success" data-bs-toggle="modal" data-bs-target="#newAdd">신규등록</a>
								        <a href="#" id="siteDel" class="btn btn-danger">삭제</a>
								    </div>
									</c:if>
                                </div>
                                <div style="height:5px;"></div>
                                    <div class="row">
                                    	<div class="col-md-12">
                                            <table id="zero_config" class="table">
                                            <thead class="thead-light">
	                                            <tr>
	                                                <th>
		                                               <input type="checkbox" id="siteCheckBox" />
		                                               <span class="checkmark"></span>
	                                                </th>
	                                                <th scope="col">사이트ID</th>
	                                                <th scope="col">사이트명</th>
	                                                <th scope="col">등록자</th>
	                                                <th scope="col">수정자</th>
	                                                <th scope="col">등록/수정일</th>
	                                                <th scope="col">Action</th>
	                                            </tr>
                                            </thead>
                                            <tbody id="tbodysiteList">
                                            </tbody>
                                        	</table>
                                    	</div>
                                	</div>
                                	<div class="row">
                                        <div class="col-md-5">
                                                <strong> 총 <span id="totalCnt">0</span>건</strong>
                                        </div>
                                        <div class="col-md-7">
                                           <div class="dataTables_paginate paging_simple_numbers" id="zero_config_paginate">
                                               <ul class="pagination justify-content-end" id="pagination" >
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
<!-- 리스트 출력  -->
<script id="siteListRowTmpl" type="text/x-jquery-tmpl">
	{{each(i, gUList) siteList}}
		<tr id="siteListRow\${i}">
			<td>
            {{if gUList.site != "default"}}
            	<input type="checkbox" name="site_chk" value="\${gUList.siteNo}" class="ml_5 radio gCodeChk" />
            {{/if}}
            </td>
			<td>\${gUList.site}</td>
			<td>\${gUList.siteNm}</td>
			<td>\${gUList.createUserNm}</td>
			<td>\${gUList.modifyUserNm}</td>
			<td>\${GetDateMMDDSS(gUList.modifyDate)}</td>
			<td><a href="#" id="modify\${gUList.siteNo}"  data-bs-toggle="modal" data-bs-target="#edit-event" class="btn btn-danger btn-sm">수정</a></td>
 		</tr>
	    <input type="hidden" id="siteNo\${gUList.site}" name="siteNo\${gUList.site}" value="\${gUList.siteNo}"/>
	{{/each}}
</script>
<!-- 신규 등록 팝업 Start  -->
<div class="modal fade" id="newAdd" tabindex="-1" data-bs-backdrop="static" aria-labelledby="addModalLabel">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header d-flex align-items-center">
                <h5 class="modal-title" id="addModalLabel">신규 등록</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
				<div class="row">
	            	<div class="col-md-6 mb-3">
	                    <label for="site" class="control-label">사이트 ID</label>
	                    <input type="text" class="form-control" id="site" data-toggle="tooltip" title="사이트 ID를 입력하세요"  placeholder="예) chat1"/>
	                    <span style="font-size:9px;">2~12자 이하</span>
	                </div>
	            	<div class="col-md-6 mb-3">
	                    <label for="siteNm" class="control-label">사이트 명</label>
	                    <input type="text" class="form-control" id="siteNm" data-toggle="tooltip" title="사이트 명을 입력하세요"  placeholder="예) 챗봇1"/>
	                    <span style="font-size:9px;">2~12자 이하</span>
	                </div>
	                <div class="col-md-12 mb-3" style="display:none;">
	                	<label for="useYn" class="control-label">사용여부</label>
	                	<select class="form-select" id="useYn" name="useYn">
	                        <option value="y" selected>사용</option>
	                        <option value="n">미사용</option>
	                    </select>
	                </div>
	                <div class="col-md-12 collapse">
	                	<div class="row">
			                <div class="col-md-12 mb-3">
				                <label for="threshold" class="control-label">임계값</label>
						        <input class="form-control" type="text" id="threshold"value="80">
			                </div>
			        	</div>
		        	</div>
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
<div class="modal fade" id="edit-event" tabindex="-1" data-bs-backdrop="static" aria-labelledby="editModalLabel">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <div class="modal-header d-flex align-items-center">
                <h5 class="modal-title" id="editModalLabel">수정</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body" id="editDiv"></div>
            <script id="editRowTmpl" type="text/x-jquery-tmpl">
                {{each(i, gUList) siteList}}
				<div class="row">
					<div class="col-md-6 mb-3">
                    	<label for="site" class="control-label">사이트 ID</label>
                    	<input type="text" class="form-control" id="site\${gUList.siteNo}" value="\${gUList.site}" disabled="disabled" />
                	</div>
            		<div class="col-md-6 mb-3">
                    	<label for="siteNm" class="control-label">사이트 명</label>
                    	<input type="text" class="form-control" id="siteNm\${gUList.siteNo}" value="\${gUList.siteNm}"/>
						<span style="font-size:9px;">2~12자 이하</span>
                	</div>
                	<div class="col-md-12 mb-3" style="display:none;">
                		<label for="useYn" class="control-label">사용여부</label>
						<select class="form-select" id="useYn\${gUList.siteNo}" name="useYn\${gUList.siteNo}">
                        {{if gUList.useYn == "y"}}
							<option value="y"  selected>사용</option>
							<option value="n">미사용</option>
   						{{else}}
							<option value="y">사용</option>
							<option value="n" selected>미사용</option>
						{{/if}}
                    	</select>
                	</div>
					<div class="col-md-12 collapse">
	                	<div class="row">
							<div class="col-md-12 mb-3">
				                <label for="threshold" class="control-label">임계값</label>
							    <input class="form-control" type="text" id="threshold\${gUList.siteNo}" value="80">
                			</div>
                			
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
				</div>
            	<div class="modal-footer">
                	<button type="button" class="btn btn-success" id="edit\${gUList.siteNo}" aria-hidden="false">저장</button>
                	<button type="button" class="btn btn-light-danger text-danger font-weight-medium" data-bs-dismiss="modal">취소</button>
            	</div>

                {{/each}}
            </script>
        </div>
    </div>
</div>
<script src="${CTX_PATH}/static/package/assets/libs/bootstrap-touchspin/dist/jquery.bootstrap-touchspin.min.js"></script>
<!-- 수정 등록 팝업 End  -->
<script type="text/javascript">
$(document).on("click","a[id^=modify]",function(e) {
    var id = $(this).attr("id");
    id = id.replace("modify","");
    btnViewContent(id);
});

$(document).on("click","#save",function(e) {
    btnSaveSiteProc();
});

$(document).on("click","button[id^=edit]",function(e) {
    var id = $(this).attr("id");
    id = id.replace("edit","");
    btnSiteModProc(id);
});

//화면 로딩 처리 함수 
$(document).ready(function(){
	siteList();

	//삭제 버튼 클릭 이벤트
	$('#siteDel').click(function(){
		siteDel();
	});
	
	//Checkbox 클릭 이벤트
	$("#siteCheckBox").click(function(){
        if($("#siteCheckBox").is(":checked")){
            $("input[type=checkbox]").prop("checked",true);
        }else{
        	$("input[type=checkbox]").prop("checked",false);
        }
    });
});


function newAdd() {
    $('#site').val('');
    $('#siteNm').val('');
    $('#threshold').val("80");
    $("#threshold").TouchSpin({
		  min: 1,
		  max: 100,
		  step: 1,
		  boostat: 5,
		  maxboostedstep: 10,
		  postfix: "%",
		});
}


function getChangeItem(){
	siteList(0);
}

//  목록
function siteList(pageNo){
	pageNo = pageNo || 1;
	$('#pageNo').val(pageNo);

	var lineNo = $('select[name=lineNo]').val();

	var param = { pageNo : pageNo 
			,lineNo : lineNo
            ,sort : $('select[name=sortType]').val()		
	}; 
	
	var siteListBack = function(data) {
		$("#siteCheckBox").prop("checked",false);
		var paginationHtml = getPaginationHtml(pageNo, data.totalCnt, lineNo, '10', 'siteList');
		$('#tbodysiteList').html('');
		$("#siteListRowTmpl").tmpl(data).appendTo("#tbodysiteList");
		$("#totalCnt").html(data.totalCnt);
		$("#pagination").html( paginationHtml );    	
	};
	
	callAjax("/system/siteAjax.ps", param, siteListBack);
	
}


function btnViewContent( id ) {
    var url  = "/system/siteAjaxEdit.ps";
    var param = {
    		siteNo : id
    };
    $('#editDiv').html('');

    var editBack = function(data) {
        //console.log(data.siteList);
        $("#editRowTmpl").tmpl(data).appendTo("#editDiv");
        $('#threshold'+id).val(data.siteList[0].threshold);
        $("#threshold"+id).TouchSpin({
  		  min: 1,
  		  max: 100,
  		  step: 1,
  		  boostat: 5,
  		  maxboostedstep: 10,
  		  postfix: "%",
  		});
    };
    callAjax(url, param, editBack);
}

// 신규등록 폼에서의 저장 버튼 클릭 이벤트
function btnSaveSiteProc() {
	var type = "post";
	var param = {
			site : $.trim($('#site').val())
			,siteNm : $.trim($('#siteNm').val())		
			,useYn : $('select[name=useYn]').val()
			,threshold : $('#threshold').val()
			,add : 'Y'						//0이면 저장
		 	};
	var inputData = param;
	if ( ! validatesite() ) {
		return;
	}
	
	var serviceBack = function(data) {
		if ( data && data.success ) {
			siteInsertProc(inputData);
			$('#newAdd').modal('hide');
		}
		else {
			if ( data && data.msg ) {
				alert(data.msg);
			}
		}
    }; 
	
	callAjaxAsync("/system/dupSite.ps", param, serviceBack);
}

// 편집ROW의 수정 버튼 클릭 이벤트
function siteInsertProc(param){
	var serviceBack = function(data) {
		if (data.success ) {
			Swal.fire({
				type: "warning",
				text: "사이트에 저장된 결과를 확인 하려면 재 로그인 해주세요.",
			});
            $('#edit-event').modal('hide');
			siteList(1);
		}else{
			if ( data && data.msg ) {
				alert(data.msg);
			} 
			else {
				alert('저장이 실패하였습니다.');
			}
		}
    }; 
	
    callAjaxAsync("/system/saveSite.ps", param, serviceBack);

}

// 편집ROW의 수정 버튼 클릭 이벤트
function btnSiteModProc(id){
	
	var param = {
			siteNo : id
			,site : $.trim($('#site'+ id).val())
			,siteNm : $.trim($('#siteNm'+ id).val())		
			,useYn : $('select[name=useYn'+ id+']').val()
			,threshold : $('#threshold'+id).val()
			,add : 'N'		
	};
	
	if ( ! validatesite(id) ) {
		return;
	}
	
	var serviceBack = function(data) {
		if (data.success ) {
			Swal.fire({
				type: "warning",
				text: "사이트에 저장된 결과를 확인 하려면 재 로그인 해주세요.",
			});
            $('#edit-event').modal('hide');
			siteList();
		}else{
			if ( data && data.msg ) {
				alert(data.msg);
			} 
			else {
				alert('수정이 실패하였습니다.');
			}
		}
    }; 
	
    callAjaxAsync("/system/saveSite.ps", param, serviceBack);
}

// 그룹 코드 저장/수정 시 입력값 검증
function validatesite(id) {

	var gIdPattern = /^[a-zA-Z0-9_-]+$|^$/g;
	var pattern_spc = /[-_@]/;

	var ids = '';
	if(id != undefined){
		ids = id
	}

    if ( !patternEngNumRange.test($('#site' + ids).val()) ) {
        Swal.fire({
        	type: "warning",
        	text: "사이트ID는 '영문','숫자','_','-','@' 를 포함하여 2~12자까지 입력해주세요.\n특수문자/공백은 제외해주세요.",
        });
        $('#site' + ids).focus();
        return false;
    }

    if ( !patternHanEngNumRange.test($('#siteNm' + ids).val()) ) {
        Swal.fire({
        	type: "warning",
        	text: "사이트명은 '한글',영문','숫자','_','-','@' 를 포함하여 2~12자까지 입력해주세요.\n특수문자/공백은 제외해주세요.",
        });
        $('#siteNm' + ids).focus();
        return false;
    }

	//console.log(ids);
	return checkNotEmpty(
			[
				 ["site" + ids  , "서비스라벨ID를 입력해주세요."]
				,["siteNm" + ids, "서비스라벨명을 입력해주세요."]
			]
	);
}

// 편집 ROW의 취소버튼 클릭 이벤트
function btnSiteModCancel(id){
	$("#siteRow" + id).css("display","none");
}

//서비스  삭제
function siteDel() {
	if ( $('.gCodeChk:checked').length == 0 ) {
		Swal.fire({
			type: "warning",
			text: "삭제하려는 서비스라벨을 체크 하세요.",
		});
		return;
	}
	var param = { siteNo : $('.gCodeChk:checked')
			.map(function(){ return $(this).val();}).toArray().join(":")	};
	
	Swal.fire({
		title: "삭제",
		text: '이 사이트에 등록된 지식이 모두 삭제됩니다.\n 계속 삭제를 진행 하시겠습니까?',
		type: 'error',
		showCancelButton: true, 
	        confirmButtonColor: 'btn-info',
	        cancelButtonColor: 'btn-danger',
		confirmButtonText: '삭제', 
		cancelButtonText: '취소'
	}).then((result) => { 
		if(result.value){
			callAjaxAsync("/system/delSite.ps", param, serviceBack);
		} else {
			return;
		}
	});
	var serviceBack = function(data) {
		if (data.success ) {
			Swal.fire({
				type: "warning",
				text: "사이트에 저장된 결과를 확인 하려면 재 로그인 해주세요.",
			}).then((result) => { 
				if(result.value){
					location.reload();
				}
			});
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

</script>
<%@include file="/WEB-INF/views/common/end.jsp"%>