<%@include file="/WEB-INF/views/common/header.jsp"%>
<%@include file="/WEB-INF/views/common/menu.jsp"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link href="${CTX_PATH}/static/css/json-path-picker/json-path-picker-styles.css" rel="stylesheet" />
<link href="${CTX_PATH}/static/css/json-path-picker/json-path-picker.css" rel="stylesheet" />

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
				                자동분류이력
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
				                        자동분류이력
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
			    <input type="hidden" name="question" id="question" value="">
                <div class="row">
					<div class="col-12">
                        <div class="card">
                            <div class="card-body">
                        		<div class="row">
                                	<div class="col-md-6">
                                    	<h5 class="card-title m-b-0"><i class="m-r-10 mdi mdi-code-greater-than"></i> 자동분류이력</h5>
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
									<!-- <div class="col-md-3">
								        <label class="control-label">검색어</label>
								        <div class="input-group">
									        <input type="text" class="form-control" id="searchKeyword" name="searchKeyword" placeholder="검색 할 단어를 입력하세요." onkeypress="if(event.keyCode==13) {dicList(); return false;}" >
									        <button class="btn btn-light-secondary text-secondary font-weight-medium" id="searchBtn" type="button"><i data-feather="search" class="feather-sm fill-white"></i></button>
									    </div>
								    </div> -->
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
                                                <tbody id="tbodyList">
                                                </tbody>
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

<%@include file="/WEB-INF/views/common/footer.jsp"%>
<script src="${CTX_PATH}/static/js/json-path-picker/json-path-picker.js"></script>
<!-- 정보 Template Start   -->
<!-- 정보 리스트 출력  -->
<script id="listRowTmpl" type="text/x-jquery-tmpl">
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
	historyList();
});

function getChangeItem(){
	historyList(1);
}

//목록
function historyList(pageNo){
	pageNo = pageNo || 1;
	$('#pageNo').val(pageNo);

	var lineNo = $('select[name=lineNo]').val();

	var param = { pageNo : pageNo 
			,lineNo : lineNo
			,resultType : $('#resultType').val()
			,matchedType :  $('#matchedType').val()
	}; 
	
	var listBack = function(data) {
		var paginationHtml = getPaginationHtml(pageNo, data.totalCnt, lineNo, '10', 'historyList');
		$('#tbodyList').html('');
		$("#listRowTmpl").tmpl(data).appendTo("#tbodyList");
		$("#totalCnt").html(data.totalCnt);
		$("#pagination").html( paginationHtml );    	
	};
	
	callAjax("/learning/historyAjax.ps", param, listBack);
	
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

function filteringXSS(origin) {
	return origin.replace(/\<|\>|\"|\'|\%|\;|\(|\)|\&|\+|\-/g, "");
}
</script>

<%@include file="/WEB-INF/views/common/end.jsp"%>