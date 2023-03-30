<%@include file="/WEB-INF/views/common/header.jsp"%>
<%@include file="/WEB-INF/views/common/menu.jsp"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<style>
#canvas-container
{ 
	background-color: #F6F6F6; 
	height: 160px;
	width: 100%;
}
</style>

<!-- ============================================================== -->
<!-- Container fluid  -->
<!-- ============================================================== -->
<input type="hidden" id="dateRange" value=""/>
<div class="page-content container-fluid">
	
	<div class="row">
		<div class="col-md-12">
			<div class="card">
		    	<div class="border-bottom title-part-padding">
					<h4 class="card-title mb-0">트래픽현황</h4>
				</div>
				<div class="card-body analytics-info">
					<div id="canvas-container"></div>
		    	</div>
		  	</div>
		</div>
	</div>
	
	<div class="row" id="trendTimeSeriesArea">
    	<div class="col-md-12">
        	<div class="card">
            	<div class="border-bottom title-part-padding">
                	<h4 class="card-title mb-0">분류 요청 일별 현황</h4>
                </div>
                <div class="card-body analytics-info">
                	<div id="trendTimeSeries" style="height: 300px"></div>
                </div>
			</div>
		</div>
	</div>
		
	<div class="row">
		<div class="col-md-6">
			<div class="card">
				<div class="border-bottom title-part-padding">
					<h4 class="card-title mb-0">오늘 분류현황</h4>
                </div>
                <div class="card-body analytics-info">
					<div id="resultTypePie" style="max-height: 200px"></div>
                </div>
			</div>
		</div>
            
 		<div class="col-md-6">
			<div class="card">
                <div class="border-bottom title-part-padding">
					<h4 class="card-title mb-0">오늘 매칭현황</h4>
                </div>
                <div class="card-body analytics-info">
					<div id="matchedTypePie" style="max-height: 200px"></div>
                </div>
			</div>
		</div>
	</div>
	
	<div class="row" id="matchedCategoryBarArea">
		<div class="col-md-12">
			<div class="card">
				<div class="border-bottom title-part-padding">
					<h4 class="card-title mb-0">오늘 매칭 카테고리 Top 10</h4>
				</div>
				<div class="card-body analytics-info">
					<div id="matchedCategoryBar" style="max-height: 300px"></div>
                </div>
			</div>
		</div>
	</div>
	
	<div class="row">
	       <div class="col-md-12">
	         <div class="card">
                 <div class="border-bottom title-part-padding">
	             	 <h4 class="card-title mb-0"> 시스템 사용량 정보 (단일서버/SWAP 메모리 제외)</h4>
	             </div>
                 <div class="row" >
                     <div class="col-sm-4 col-md-4 text-center">
                             <div id="cpuUsage"></div>CPU
                     </div>
                     <div class="col-sm-4 col-md-4 text-center">
                             <div id="memoryUsage"></div>Memory
                     </div>
                     <div class="col-sm-4 col-md-4 text-center">
                             <div id="diskUsage"></div>Disk
                     </div>
                 </div>
	         </div>
	     </div>
	</div>
	
</div>
<!-- ============================================================== -->
<!-- End Container fluid  -->
<!-- ============================================================== -->

<%@include file="/WEB-INF/views/common/footer.jsp"%>
<!-- This Page JS -->

<script type="text/javascript">
var diskUsageChart;
var cpuUsageChart;
var memoryUsageChart;
var trendTimeSeriesGraph;
var matchedCategoryBarGraph;
var resultTypePieGraph;
var matchedTypePieGraph;

$(document).ready(function() {
	var siteNo = '${siteNo}';
	doSearch();
	getSystemInfo();
	scriptInit();
	connectStomp(siteNo);
});

var socket = null;

function connectStomp(siteNo) {
	var sock = new SockJS("${CTX_PATH}/stomp/traffic"); // endpoint
    var client = Stomp.over(sock);
	socket = client;
    
	
    client.connect({}, function () {
        console.log("Connected /stomp/traffic!");
        
        // 해당 토픽을 구독한다!
        var body = "";
        client.subscribe('/topic/' + siteNo, function (event) {
        	body = event.body;
        	// 브라우저가 활성화되어있는 상태에서 시각화 진행
        	if(!document.hidden){
	        	requestQueryVisualization();
        	}
        });
    });
}

function requestQueryVisualization(){
	var key = generateUniqueKey();
	requestQuery(key, requestQueryCallback);
}

function requestQueryCallback(key){
	responseQuery(key);
}

function doSearch(){
	var param = { 
	}; 
	var serviceBack = function(data) {
		console.log(data);
		matchedCategoryBar(data.categoryMapList);
		trendTimeSeries(data.trendList);
		drawResultTypePie(data.resultResultTypeMap);
		drawMatchedTypePie(data.resultMatchedTypeMap);
    }; 
    callAjaxAsync("/main/getStatisticsInfo.ps", param, serviceBack);
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
	var interval = 1;
	var stdViewCnt = 31;
	
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
		interval = 2;
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


function getSystemInfo(){
	var url = "${CTX_PATH}/main/getSystemInfo.ps";
	var data = {};

    var html = "";
	getJson(url, data, function(resp){
		var allocationResult = resp.allocation.result;
		var allocationSuccess = resp.allocation.success;
		var nodesResult = resp.nodes.result;
		var nodesSuccess = resp.nodes.success;
		
		if(allocationSuccess){
			$.each(allocationResult, function(k,v) {
				if ( v["disk.percent"] != null ) {
					diskChart(v["disk.percent"],"Y");
				}
			});
		} else {
			console.log(resp.allocation.msg);
		}
		if(nodesSuccess){
			$.each(nodesResult, function(k,v) {
				if ( k == 0 ) {
					memoryChart(v["ram.percent"],"Y");
                    cpuChart(v["cpu"],"Y");
				}
			});
		} else {
			console.log(resp.nodes.msg);
		}
	});
}

/* Memory 사용 게이지 차트  */
function memoryChart(used,  initChart){
    var total = 100;
	used = used == "" ? 0 : used;
	if(initChart == 'Y'){
		memoryUsageChart = c3.generate({
			bindto: "#memoryUsage",
			data: {
		        columns: [
		            ['사용', used]
		        ],
		        type: 'gauge',
		        onclick: function (d, i) { console.log("onclick", d, i); },
		        onmouseover: function (d, i) { console.log("onmouseover", d, i); },
		        onmouseout: function (d, i) { console.log("onmouseout", d, i); }
		    },
		    gauge: {
		    max: total, // 100 is default
		    },
		    color: {
                pattern: ['#60B044','#F6C600','#F97600','#FF0000'], // the three color levels for the percentage values.
		        threshold: {
		            values: [30, 60, 90, 100]
		        }
		    },
		    size: {
		        height: 130
		    }
		});
	}

	memoryUsageChart.load({
        columns: [['사용', used]]
    });
}

// CPU 사용량 게이지 차트 
function cpuChart(cpuUsage, initChart){
	if(initChart == 'Y'){
		cpuUsageChart = c3.generate({
			 bindto: "#cpuUsage",
				data: {
			        columns: [
			            ['사용', cpuUsage]
			        ],
			        type: 'gauge',
			        onclick: function (d, i) { },
			        onmouseover: function (d, i) { },
			        onmouseout: function (d, i) { }
			    },
			    gauge: {
			    },
			    color: {
                    pattern: ['#60B044','#F6C600','#F97600','#FF0000'], // the three color levels for the percentage values.
			        threshold: {
			            values: [30, 60, 90, 100]
			        }
			    },
                size: {
                    height: 130
                }
			});
		}
	cpuUsageChart.load({
        columns: [['사용', cpuUsage]]
    });
}

// 디스크 사용량 차트
function diskChart(used,  initChart){
    var total = 100;
	used = used == "" ? 0 : used;
	if(initChart == 'Y'){
		diskUsageChart = c3.generate({
			bindto: "#diskUsage",
			data: {
		        columns: [
		            ['사용', used]
		        ],
		        type: 'gauge',
		        onclick: function (d, i) { console.log("onclick", d, i); },
		        onmouseover: function (d, i) { console.log("onmouseover", d, i); },
		        onmouseout: function (d, i) { console.log("onmouseout", d, i); }
		    },
		    gauge: {
		    max: total, // 100 is default
		    },
		    color: {
                pattern: ['#60B044','#F6C600','#F97600','#FF0000'], // the three color levels for the percentage values.
		        threshold: {
		            values: [30, 60, 90, 100]
		        }
		    },
		    size: {
		        height: 130
		    }
		});
	}
	diskUsageChart.load({
        columns: [['사용', used]]
    });
}
  
</script>

<script src="${CTX_PATH}/static/js/websocket/sockjs.min.js"></script>
<script src="${CTX_PATH}/static/js/websocket/stomp.min.js"></script>
<script src="${CTX_PATH}/static/js/traffic/dat.gui.min.js"></script>
<script src="${CTX_PATH}/static/js/traffic/script.js"></script>

<script src="${CTX_PATH}/static/package/assets/libs/flot/jquery.flot.js"></script>
<script src="${CTX_PATH}/static/package/assets/libs/jquery.flot.tooltip/js/jquery.flot.tooltip.min.js"></script>
<script src="${CTX_PATH}/static/package/assets/libs/apexcharts/dist/apexcharts.min.js"></script>
<script src="${CTX_PATH}/static/package/assets/libs/echarts/dist/echarts-en.min.js"></script>
<script type="text/javascript" src="/manager/static/js/chart/c3.min.js"></script>

<%@include file="/WEB-INF/views/common/end.jsp"%>