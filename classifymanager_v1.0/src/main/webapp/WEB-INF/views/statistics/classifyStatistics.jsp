<%@include file="/WEB-INF/views/common/header.jsp"%>
<%@include file="/WEB-INF/views/common/menu.jsp"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script src="${CTX_PATH}/static/package/assets/libs/echarts/dist/echarts-en.min.js"></script>

<!-- ============================================================== -->
<!-- Bread crumb and right sidebar toggle -->
<!-- ============================================================== -->
<div class="page-breadcrumb border-bottom">
	<div class="row">
		<div
			class="
				                col-lg-3 col-md-4 col-xs-12
				                justify-content-start
				                d-flex
				                align-items-center
				              ">
			<h5 class="font-weight-medium text-uppercase mb-0">분류 통계</h5>
		</div>
		<div
			class="
				                col-lg-9 col-md-8 col-xs-12
				                d-flex
				                justify-content-start justify-content-md-end
				                align-self-center
				              ">
			<nav aria-label="breadcrumb" class="mt-2">
				<ol class="breadcrumb mb-0 p-0">
					<li class="breadcrumb-item"><a href="#">통계관리</a></li>
					<li class="breadcrumb-item active" aria-current="page">분류 통계</li>
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
					<div class="border-bottom title-part-padding">
						<div class="row">
							<div class="col-md-12" style="display:flex" >
									<label class="col-sm-1 text-left control-label col-form-label">통계기준&nbsp;</label>
									<div class="col-sm-2 " style="display:flex;margin-right: 20px" >
										<select class="form-select mr-sm-2" id="stdType" onchange="getChangeItem();">
								            <option value="hour">시간별</option>
								            <option value="day" selected>일별</option>
								            <option value="month">월별</option>
								            <option value="category">카테고리별</option>
								        </select>
									</div>
									<label class="col-sm-1 text-left control-label col-form-label">기간설정&nbsp;</label>
									<div class="col-sm-4 shawCalRanges" style="display:flex">
										 <input type="text"  class="form-control " id="dateRange" placeholder="기간" name="dateRange" readonly/>
										<span class="input-group-text" >
				                     	 	<i data-feather="calendar" class="feather-sm"></i>
				                   		</span>
				                    </div>
				                    <label class="col-sm-1 text-left control-label col-form-label">&nbsp;</label>
				                    <div class="col-sm-1" >
										<button type="button" onclick="doSearch();" id="searchBtn" class="btn waves-effect waves-light btn-outline-success">통계 조회</button>
				                    </div>
							</div> 
						</div>
					</div>
				</div>
			</div>
		</div>
		
		<div class="row" id="trendTimeSeriesArea">
            <div class="col-md-12">
              <div class="card">
                <div class="border-bottom title-part-padding">
                  <h4 class="card-title mb-0">분류 요청 현황</h4>
                </div>
                <div class="card-body analytics-info">
                  <div id="trendTimeSeries" style="height: 300px"></div>
                </div>
              </div>
            </div>
		</div>
		
		<div class="row" id="matchedCategoryBarArea" style="display:none;">
            <div class="col-md-12">
              <div class="card">
                <div class="border-bottom title-part-padding">
                  <h4 class="card-title mb-0">매칭 카테고리 현황</h4>
                </div>
                <div class="card-body analytics-info">
                  <div id="matchedCategoryBar" style="max-height: 300px"></div>
                </div>
              </div>
            </div>
		</div>
		
		<div class="row">
			<div class="col-md-6">
              <div class="card">
                <div class="border-bottom title-part-padding">
                  <h4 class="card-title mb-0">분류현황</h4>
                </div>
                <div class="card-body analytics-info">
                  <div id="resultTypePie" style="max-height: 200px"></div>
                </div>
              </div>
            </div>
            
 			<div class="col-md-6">
              <div class="card">
                <div class="border-bottom title-part-padding">
                  <h4 class="card-title mb-0">매칭현황</h4>
                </div>
                <div class="card-body analytics-info">
                  <div id="matchedTypePie" style="max-height: 200px"></div>
                </div>
              </div>
            </div>
		</div>
	
		<div class="row">
			<div class="col-12">
				<div class="card">
					<div class="card-body">
						<div class="row">
							<div class="col-md-6">
								<h5 class="card-title m-b-0">
									<i class="m-r-10 mdi mdi-code-greater-than"></i> 통계 데이터
								</h5>
							</div>
                            <div class="col-md-6 text-end mt-4" >
		                    	<a href="#none" id="download" class="btn btn-primary"><i data-feather="download" class="feather-sm fill-white me-1"></i>일괄 다운로드</a>
							</div>
						</div>

						<div style="height: 10px;"></div>
						<div class="row" id="trendRawDataArea">
							<div class="col-sm-12 col-md-12">
								<table class="table">
									<thead class="thead-light">
										<tr>
		                          			<th>통계기준</th>
		                          			<th>통계라벨</th>
		                          			<th>문서 건수</th>
		                          			<th>분류 건수</th>
		                          			<th>매칭률</th>
		                          			<th>룰 매칭 건수</th>
		                          			<th>분류 매칭 건수</th>
		                        		</tr>
									</thead>
									<tbody id="tTrendBodyList">
									</tbody>
								</table>
							</div>
						</div>
						<div class="row" id="categoryRawDataArea" style="display:none;">
							<div id="categoryRawDataTable" class="col-sm-12 col-md-12">
								<table class="table">
									<thead class="thead-light">
										<tr>
		                          			<th>통계기준</th>
		                          			<th>매칭 건수</th>
		                        		</tr>
									</thead>
									<tbody id="tCategoryBodyList">
									</tbody>
								</table>
							</div>
							<div id="categoryNoData" class='alert alert-secondary m-n' style="display:none;"> 데이터가 존재하지 않습니다. </div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</form>
</div>

<%@include file="/WEB-INF/views/common/footer.jsp"%>
<form id="frmDownload" name="frmDownload" action="" method="post">
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
   	<input type="hidden" id="dwn_startDate" name="startDate" value="">
   	<input type="hidden" id="dwn_endDate" name="endDate" value="">
   	<input type="hidden" id="dwn_stdType" name="stdType" value="">
</form>
<!-- 정보 Template Start   -->
<!-- 정보 리스트 출력  -->
<script id="trendListRowTmpl" type="text/x-jquery-tmpl">
    {{each(i, gUList) trendList}}
        <tr id="trendListRow\${gUList.stdValue}">
            <td>\${gUList.stdValue}</td>
			<td>\${gUList.stdViewValue}</td>
            <td>\${gUList.trend} 건</td>
            <td>\${gUList.matched} 건</td>
			<td>\${gUList.matched_rate} %</td>
			<td>\${gUList.rule} 건</td>
			<td>\${gUList.classify} 건</td>
        </tr>
    {{/each}}
</script>
<!-- 정보 리스트 출력  -->
<script id="categoryListRowTmpl" type="text/x-jquery-tmpl">
    {{each(i, gUList) categoryMapList}}
        <tr id="categoryListRow\${i}">
            <td>\${gUList.categoryName}</td>
            <td>\${gUList.count} 건</td>
        </tr>
    {{/each}}
</script>

<script type="text/javascript">
	var trendTimeSeriesGraph;
	var matchedCategoryBarGraph;
	var resultTypePieGraph;
	var matchedTypePieGraph;
	
	//화면 로딩 처리 함수 
	$(document).ready(function() {
		setDateRangepicker('day');
		doSearch();
	});
	
	function doSearch(){
		var dateRange = $("#dateRange").val().split(' - ');
		var startDate = dateRange[0];
		var endDate = dateRange[1];
		
		var param = { 
				startDate : startDate,
				endDate : endDate,
				stdType : $('#stdType').val()
		}; 
		var serviceBack = function(data) {
			console.log(data);
			if($('#stdType').val() == 'category'){
				matchedCategoryBar(data.categoryMapList);
				$('#tCategoryBodyList').html('');
		        $('#categoryRawDataArea').show();
				if(data.categoryMapList > 0) {
					$('#download').show();
					$("#categoryRawDataTable").show();
					$("#categoryNoData").hide();
					$("#categoryListRowTmpl").tmpl(data).appendTo("#tCategoryBodyList");
				} else {
					$('#download').hide();
					$("#categoryRawDataTable").hide();
					$('#categoryNoData').show();
				}
				$('#trendRawDataArea').hide();
				$("#trendTimeSeriesArea").hide();
				$("#matchedCategoryBarArea").show();
			} else {
				$('#download').show();
				trendTimeSeries(data.trendList);
				$('#tTrendBodyList').html('');
		        $("#trendListRowTmpl").tmpl(data).appendTo("#tTrendBodyList");
		        $("#trendTimeSeriesArea").show();
				$("#matchedCategoryBarArea").hide();
		        $('#trendRawDataArea').show();
		        $('#categoryRawDataArea').hide();
			}
			drawResultTypePie(data.resultResultTypeMap);
			drawMatchedTypePie(data.resultMatchedTypeMap);
			
	    }; 
		
	    callAjaxAsync("/statistics/classifyStatisticsAjax.ps", param, serviceBack);
	}
	
	// 통계 기준 세팅
	function getChangeItem(){
		var stdType = $('#stdType').val();
		setDateRangepicker(stdType);
		if(stdType == 'hour'){
			setDateRangepicker(stdType);
		} else if(stdType == 'day'){
			setDateRangepicker(90);
		} else if(stdType == 'month'){
			setDateRangepicker(365);
		} else if(stdType == 'category'){
			setDateRangepicker(90);
		}
	}

	function drawResultTypePie(data){
		if($.isEmptyObject(data) != true){
			$('#resultTypePie').css('height','200px');
			$(function () {
				resultTypePieGraph = echarts.init(document.getElementById("resultTypePie"));
				  var option = {
				    // Add legend
				    legend: {
				      orient: "vertical",
				      x: "left",
				      y: "center",
				      data: ["분류", "미분류"],
				    },

				    // Add custom colors
				    color: ["#1e88e5","#f62d51"],

				    // Display toolbox
				    toolbox: {
				      show: true,
				      orient: "horizontal",
				      feature: {
				        restore: {
				          show: true,
				          title: "새로고침",
				        },
				        saveAsImage: {
				          show: true,
				          title: "저장",
				          lang: ["Save"],
				        },
				      },
				    },

				    // Enable drag recalculate
				    calculable: true,

				    // Add series
				    series: [
				      {
				        name: "Browsers",
				        type: "pie",
				        radius: ["50%", "70%"],
				        center: ["50%", "50%"],
				        itemStyle: {
				          normal: {
				            label: {
				              show: true,
				              formatter: "{b}" + " \n " + "{c} ({d}%)",
				              textStyle: {
					                fontSize: "15",
					                fontWeight: "500",
					              },
				            },
				            labelLine: {
				              show: true
				            },
				          },
				          emphasis: {
				            label: {
				              show: true,
				              formatter: "{b}" + " \n " + "{c} ({d}%)",
				              position: "center",
				              textStyle: {
				                fontSize: "20",
				                fontWeight: "500",
				              },
				            },
				          },
				        },

				        data: [
				          { value: data.matched, name: "분류" },
				          { value: data.not_matched, name: "미분류" }
				        ],
				      },
				    ],
				  };

				  resultTypePieGraph.setOption(option);
				  setTimeout(function () {
					  resultTypePieGraph.resize();
				  }, 100);	 
			});	
		} else {
			$('#resultTypePie').css('height','70px');
			$('#resultTypePie').html("<div class='alert alert-secondary m-n'> 데이터가 존재하지 않습니다. </div>");
		}
	}
	
	function drawMatchedTypePie(data){
		if($.isEmptyObject(data) != true){
			$(function () {
				$('#matchedTypePie').css('height','200px');
				matchedTypePieGraph = echarts.init(document.getElementById("matchedTypePie"));
				  var option = {
				    // Add legend
				    legend: {
				      orient: "vertical",
				      x: "left",
				      y: "center",
				      data: ["룰매칭", "분류매칭"],
				    },

				    // Add custom colors
				    color: ["#1e88e5","#f62d51"],

				    // Display toolbox
				    toolbox: {
				      show: true,
				      orient: "horizontal",
				      feature: {
				        restore: {
				          show: true,
				          title: "새로고침",
				        },
				        saveAsImage: {
				          show: true,
				          title: "저장",
				          lang: ["Save"],
				        },
				      },
				    },

				    // Enable drag recalculate
				    calculable: true,

				    // Add series
				    series: [
				      {
				        name: "Browsers",
				        type: "pie",
				        radius: ["50%", "70%"],
				        center: ["50%", "50%"],
				        itemStyle: {
				          normal: {
				            label: {
				              show: true,
				              formatter: "{b}" + " \n " + "{c} ({d}%)",
				              textStyle: {
					                fontSize: "15",
					                fontWeight: "500",
					              },
				            },
				            labelLine: {
				              show: true
				            },
				          },
				          emphasis: {
				            label: {
				              show: true,
				              formatter: "{b}" + " \n " + "{c} ({d}%)",
				              position: "center",
				              textStyle: {
				                fontSize: "20",
				                fontWeight: "500",
				              },
				            },
				          },
				        },

				        data: [
				          { value: data.rule, name: "룰매칭" },
				          { value: data.classify, name: "분류매칭" }
				        ],
				      },
				    ],
				  };

				  matchedTypePieGraph.setOption(option);
				  setTimeout(function () {
					  matchedTypePieGraph.resize();
				  }, 100);	 
			});
		} else {
			$('#matchedTypePie').css('height','70px');
			$('#matchedTypePie').html("<div class='alert alert-secondary m-n'> 데이터가 존재하지 않습니다. </div>");
		}		
	}
	
	function trendTimeSeries(data){
		var interval = 0;
		var stdViewCnt = 31;
		if($('#stdType').val() == 'hour') {
			stdViewCnt = 24;
		} else if($('#stdType').val() == 'day') {
			stdViewCnt = 31;
		} else if($('#stdType').val() == 'month') {
			stdViewCnt = 12;
		}
		
		var stdViewValueArr = new Array();
		var trendArr = new Array();
		var matchedArr = new Array();
		var notMatchedArr = new Array();
		
		data.forEach(function(element){
			stdViewValueArr.push(element.stdViewValue);
			trendArr.push(element.trend);
			matchedArr.push(element.matched);
			notMatchedArr.push(element.not_matched);
		});
		
		var endRate = data.length / 30;
		if(data.length < stdViewCnt) {
			endRate = 100;
		} else {
			interval = 1;
			endRate = stdViewCnt / data.length * 100;
		}
		console.log('endRate : ' + endRate + ', data.length : ' + data.length);
		
		$(function(){			
			trendTimeSeriesGraph = echarts.init(document.getElementById("trendTimeSeries"));
			  var option = {
			    // Setup grid
			    grid: {
			      left: "1%",
			      right: "2%",
			      bottom: "3%",
			      containLabel: true,
			    },

			    // Add Tooltip
			    tooltip: {
			      trigger: "axis",
			    },

			    legend: {
			      data: ["전체","분류","미분류"],
			    },
			    dataZoom: [
			    	{
			            id: 'dataZoomX',
			            type: 'slider',
			            xAxisIndex: [0],
			            filterMode: 'filter',
			            start: 0,
			            end: endRate
			        }
		        ],
			    toolbox: {
			      show: true,
			      feature: {
				       // magicType: { show: true,   title: { line : "꺾은선그래프로 변환", bar: "막대그래프로 변환",},type: ["line", "bar"] },
				        restore: { show: true, title: "새로고침", },
					    saveAsImage: { show: true, title: "저장", lang: ["Save"], },
				  },
			    },
			    color: ["#212529","#1e88e5","#ffbc34"],
			    calculable: true,
			    xAxis: [
			      {
			    	  type: "category",
				        data: stdViewValueArr,
				        borderColor: "rgba(0,0,0,.1)",
				        offset:0,
				        axisTick:{
				        	alignWithLabel:true,
				        },
				        axisLabel: {
				            interval: interval,
				            formatter: function (value, index) {
				            	return value;
				            }
				        },
			      },
			    ],
			    yAxis: [
			      {
			        type: "value",
			        borderColor: "rgba(0,0,0,.1)",
			      },
			    ],
			    series: [
			     {
				    name: "전체",
				    type: "line",
				    data: trendArr,
			      },
			      {
			        name: "분류",
			        type: "bar",
			        data: matchedArr,
			      },
			      {
			        name: "미분류",
			        type: "bar",
			        data: notMatchedArr,
			      }
			    ],
			  };
			  // use configuration item and data specified to show chart
			  trendTimeSeriesGraph.setOption(option);
			  setTimeout(function () {
				  trendTimeSeriesGraph.resize();
			  }, 100);	
		});
	}
	
	function matchedCategoryBar(data){
		var interval = 0;
		var stdViewCnt = 31;
		
		var categoryArr = new Array();
		var countArr = new Array();
		
		data.forEach(function(element){
			categoryArr.push(element.categoryName);
			countArr.push(element.count);
		});
		categoryArr.reverse();
		countArr.reverse();
		
		var endRate = data.length / 30;
		if(data.length < stdViewCnt) {
			endRate = 100;
		} else {
			endRate = stdViewCnt / data.length * 100;
		}
		console.log('endRate : ' + endRate + ', data.length : ' + data.length);
		
		if(categoryArr.length > 0){
			$('#matchedCategoryBar').css('height','300px');
			$(function(){			
				matchedCategoryBarGraph = echarts.init(document.getElementById("matchedCategoryBar"));
				var option = {
					    // Setup grid
					    grid: {
					      x: 160,
					      x2: 140,
					      y: 45,
					      y2: 25,
					    },

					    // Add tooltip
					    tooltip: {
					      trigger: "axis",
					    },

					    // Add legend
					    legend: {
					      data: ["카테고리"],
					    },

					    // Add custom colors
					    color: ["#1e88e5"],

					    // Horizontal axis
					    xAxis: [
					      {
					        type: "value",
					        boundaryGap: [0, 10],
					      },
					    ],

					    // Vertical axis
					    yAxis: [
					      {
					        type: "category",
					        data: categoryArr,
					      },
					    ],

					    // Add series
					    series: [
					      {
					        name: "카테고리",
					        type: "bar",
					        data: countArr,
					      },
					    ],
					  };
				  // use configuration item and data specified to show chart
				  matchedCategoryBarGraph.setOption(option);
				  setTimeout(function () {
					  matchedCategoryBarGraph.resize();
				  }, 100);	
			});	
		} else {
			$('#matchedCategoryBar').css('height','70px');
			$('#matchedCategoryBar').html("<div class='alert alert-secondary m-n'> 데이터가 존재하지 않습니다. </div>");
		}
	}
</script>
<script>
	function setDateRangepicker(stdType){
		if(stdType == 'hour'){
			$("#dateRange").daterangepicker({
			    ranges: {
			      "오늘": [moment(), moment()],
			      "어제": [
			        moment().subtract(1, "days"),
			        moment().subtract(1, "days"),
			      ],
			      "지난 7일간": [moment().subtract(6, "days"), moment()],
			    },
			    "maxSpan": {
			        "days": 7
			    },
			    autoApply:true,
			    alwaysShowCalendars: true,
			    startDate: moment().subtract(7, "days"), // after open picker you'll see this dates as picked
			    endDate: moment(),
			    maxDate: moment()
			  });
		} else if(stdType == 'day' || stdType == 'category'){
			$("#dateRange").daterangepicker({
			    ranges: {
			      "오늘": [moment(), moment()],
			      "어제": [
			        moment().subtract(1, "days"),
			        moment().subtract(1, "days"),
			      ],
			      "지난 7일간": [moment().subtract(6, "days"), moment()],
			      "지난 30일간": [moment().subtract(29, "days"), moment()],
			      "이번달": [moment().startOf("month"), moment().endOf("month")],
			      "지난달": [
			        moment().subtract(1, "month").startOf("month"),
			        moment().subtract(1, "month").endOf("month"),
			      ],
			    },
			    "maxSpan": {
			        "days": 90
			    },
			    autoApply:true,
			    alwaysShowCalendars: true,
			    startDate: moment().subtract(7, "days"), // after open picker you'll see this dates as picked
			    endDate: moment(),
			    maxDate: moment()
			  });
		} else if(stdType == 'month'){
			$("#dateRange").daterangepicker({
			    ranges: {
			      "오늘": [moment(), moment()],
			      "어제": [
			        moment().subtract(1, "days"),
			        moment().subtract(1, "days"),
			      ],
			      "지난 7일간": [moment().subtract(6, "days"), moment()],
			      "지난 30일간": [moment().subtract(29, "days"), moment()],
			      "이번달": [moment().startOf("month"), moment().endOf("month")],
			      "지난달": [
			        moment().subtract(1, "month").startOf("month"),
			        moment().subtract(1, "month").endOf("month"),
			      ],
			    },
			    "maxSpan": {
			        "days": 365
			    },
			    autoApply:true,
			    alwaysShowCalendars: true,
			    startDate: moment().subtract(7, "days"), // after open picker you'll see this dates as picked
			    endDate: moment(),
			    maxDate: moment()
			  });
		} else {
			$("#dateRange").daterangepicker({
			    ranges: {
			      "오늘": [moment(), moment()],
			      "어제": [
			        moment().subtract(1, "days"),
			        moment().subtract(1, "days"),
			      ],
			      "지난 7일간": [moment().subtract(6, "days"), moment()],
			      "지난 30일간": [moment().subtract(29, "days"), moment()],
			      "이번달": [moment().startOf("month"), moment().endOf("month")],
			      "지난달": [
			        moment().subtract(1, "month").startOf("month"),
			        moment().subtract(1, "month").endOf("month"),
			      ],
			    },
			    "maxSpan": {
			        "days": 90
			    },
			    autoApply:true,
			    alwaysShowCalendars: true,
			    startDate: moment().subtract(7, "days"), // after open picker you'll see this dates as picked
			    endDate: moment(),
			    maxDate: moment()
			  });
		}
	}
	
	// 일괄 다운로드
	$('#download').click(function(){
	    excelDownload();
	});

	// 다운로드 실행
	function excelDownload() {
		var dateRange = $("#dateRange").val().split(' - ');
		var startDate = dateRange[0];
		var endDate = dateRange[1];
		$('#dwn_startDate').val(startDate);
		$('#dwn_endDate').val(endDate);
		$('#dwn_stdType').val($('#stdType').val());
		$('#frmDownload').attr('action','${CTX_PATH}/statistics/excelDownload.ps').submit();
	}
</script>
<%@include file="/WEB-INF/views/common/end.jsp"%>