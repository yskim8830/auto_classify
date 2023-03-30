function getContextPath(){
	var offset=location.href.indexOf(location.host)+location.host.length;
	var ctxPath=location.href.substring(offset,location.href.indexOf('/',offset+1));
	ctxPath = "";
	return ctxPath;
}

/**
 * 문자 치환 홤수
 * @param 원래 문자
 * @param  바뀌는 문자
 * @param 바꿀 문자 
 * @return 치환 결과 문자열
 */
function replaceAll(str, searchStr, replaceStr) {

    while (str.indexOf(searchStr) != -1) {
        str = str.replace(searchStr, replaceStr);
    }

    return str;

}

/**
 * Bootstrap 링크를 적용한 페이징 태그 생성
 * @param  currentPage 현재 페이지
 * @param  totalCount 총 건수
 * @param  pageRow 페이지당 보여주는 목록 갯수
 * @param  blockPage 페이지 번호 갯수 
 * @param  pageFunc  페이지 번호를 클릭하면 호출할 함수 객체
 * @param  exParams  pageFunc에 넘겨줄 추가적인 파라미터 ( optional / 가능한 파라미터 형식: 문자열 )
 * @return html 문자열
 */
function getPaginationHtml(currentPage, totalCount, pageRow, blockPage, pageFunc, exParams)
{	
	totalCount = parseInt(totalCount);
	pageRow = parseInt(pageRow);
	blockPage = parseInt(blockPage);
	
	var totalPage = Math.ceil(totalCount / pageRow);
	if (totalPage == 0) {
		totalPage = 1;
	}

	// 현재 페이지가 전체 페이지 수보다 크면 전체 페이지 수로 설정
	if (currentPage > totalPage) {
		currentPage = totalPage;
	}

	// 현재 페이지의 처음과 마지막 글의 번호 가져오기.
	var startCount = (currentPage - 1) * pageRow;
	var endCount = startCount + pageRow;

	// 시작 페이지와 마지막 페이지 값 구하기.
	startPage = Math.floor((currentPage - 1) / blockPage) * blockPage + 1;
	endPage = startPage + blockPage - 1;

	// 마지막 페이지가 전체 페이지 수보다 크면 전체 페이지 수로 설정
	if (endPage > totalPage) {
		endPage = totalPage;
	}
	
	// 추가 파라미터가 있는 경우 함수 호출 파라미터로 적용
	var sExParam = exParams==undefined ? "" : ",\"" + exParams.join("\",\"") + "\"";
    var pagingHtml = "";
	pagingHtml += "<li class='paginate_button page-item'><a href='javascript:void(0);' class='page-link' onclick="+pageFunc+"(1"+sExParam+");return false; '>&lt&lt</a></li>&nbsp;";
	pagingHtml += "<li class='paginate_button page-item'><a href='javascript:void(0);' class='page-link' onclick="+pageFunc+"(" + (startPage - 1 == 0 ? 1 : (startPage -1)) + sExParam + ");return false; '>&lt</a></li>&nbsp;";

	for (var i = startPage; i <= endPage; i++) {
		if (i > totalPage) {
			break;
		}
		
		if(i > startPage) {
			firstPage = "";
		}
		
		if (i == currentPage) {
//			pagingHtml += "<li class='active'><a href='javascript:;'>" + i + "</a></li>";
			pagingHtml += "<li class='paginate_button page-item active'><a class='page-link'>" + i + "</a></li>&nbsp";
		} else {
//			pagingHtml += "<li><a href=javascript:"+pageFunc+"(" + i + sExParam +")>" + i + "</a></li>";
			pagingHtml += "<li class='paginate_button page-item'><a href='javascript:void(0);' class='page-link' onclick="+pageFunc+"(" + i +sExParam + ");return false; '>" + i + "</a></li>&nbsp";
		}
	}

//	pagingHtml += "<li><a title='다음' href='javascript:"+pageFunc+"(" + (endPage + 1 > totalPage ? totalPage : (endPage + 1))+ sExParam + ")'><span arial-hidden='true'><span class='glyphicon glyphicon-chevron-right' aria-hidden='true'></span></span></a></li>";
//	pagingHtml += "<li><a title='마지막' href='javascript:"+pageFunc+"(" + totalPage + sExParam + ")'><span arial-hidden='true'><span class='glyphicon glyphicon-forward' aria-hidden='true'></span></span></a></li>";
	pagingHtml += "<li class='page-item'><a href='javascript:void(0);' class='page-link' onclick="+pageFunc+"(" + (endPage + 1 > totalPage ? totalPage : (endPage + 1)) + sExParam + ");return false; '>&gt</a></li>&nbsp";
	pagingHtml += "<li class='page-item'><a href='javascript:void(0);' class='page-link' onclick="+pageFunc+"(" + totalPage +sExParam + ");return false; '>&gt&gt</a></li>";
	
//	pagingHtml += "</ul>";
	//console.log(pagingHtml);
	return pagingHtml;
};

function getPaginationHtml2(currentPage, totalCount, pageRow, blockPage, pageFunc, exParams)
{	
	totalCount = parseInt(totalCount);
	pageRow = parseInt(pageRow);
	blockPage = parseInt(blockPage);
	
	var totalPage = Math.ceil(totalCount / pageRow);
	if (totalPage == 0) {
		totalPage = 1;
	}

	// 현재 페이지가 전체 페이지 수보다 크면 전체 페이지 수로 설정
	if (currentPage > totalPage) {
		currentPage = totalPage;
	}

	// 현재 페이지의 처음과 마지막 글의 번호 가져오기.
	var startCount = (currentPage - 1) * pageRow;
	var endCount = startCount + pageRow;

	// 시작 페이지와 마지막 페이지 값 구하기.
	startPage = Math.floor((currentPage - 1) / blockPage) * blockPage + 1;
	endPage = startPage + blockPage - 1;

	// 마지막 페이지가 전체 페이지 수보다 크면 전체 페이지 수로 설정
	if (endPage > totalPage) {
		endPage = totalPage;
	}
	
	// 추가 파라미터가 있는 경우 함수 호출 파라미터로 적용
	var sExParam = exParams==undefined ? "" : ",\"" + exParams.join("\",\"") + "\"";
	
//	var pagingHtml = "<ul class='pagination'>";
	var pagingHtml ="";
//	pagingHtml += "<li><a title='처음' href='javascript:"+pageFunc+"(1"+sExParam+")'><span arial-hidden='true'><span class='glyphicon glyphicon-backward' aria-hidden='true'></span></span></a></li>";
//	pagingHtml += "<li><a title='이전' href='javascript:"+pageFunc+"(" + (startPage - 1 == 0 ? 1 : (startPage -1)) +sExParam+")'><span arial-hidden='true'><span class='glyphicon glyphicon-chevron-left' aria-hidden='true'></span></span></a></li>";

	pagingHtml += "<li class='page-item'><a href='#' class='page-link' onclick="+pageFunc+"(1"+sExParam+");return false; '>&lt&lt</a></li>";
	pagingHtml += "<li class='page-item'><a href='#' class='page-link' onclick="+pageFunc+"(" + (startPage - 1 == 0 ? 1 : (startPage -1)) + sExParam + ");return false; ' style='margin:0'>&lt</a></li>";

	for (var i = startPage; i <= endPage; i++) {
		if (i > totalPage) {
			break;
		}
		
		if(i > startPage) {
			firstPage = "";
		}
		
		if (i == currentPage) {
//			pagingHtml += "<li class='active'><a href='javascript:;'>" + i + "</a></li>";
			pagingHtml += "<li class='page-item'><a class='page-link'>" + i + "</a></li>";
		} else {
//			pagingHtml += "<li><a href=javascript:"+pageFunc+"(" + i + sExParam +")>" + i + "</a></li>";
			pagingHtml += "<li class='page-item'><a href='#' class='page-link' onclick="+pageFunc+"(" + i +sExParam + ");return false; '>" + i + "</a></li>";
		}
	}

//	pagingHtml += "<li><a title='다음' href='javascript:"+pageFunc+"(" + (endPage + 1 > totalPage ? totalPage : (endPage + 1))+ sExParam + ")'><span arial-hidden='true'><span class='glyphicon glyphicon-chevron-right' aria-hidden='true'></span></span></a></li>";
//	pagingHtml += "<li><a title='마지막' href='javascript:"+pageFunc+"(" + totalPage + sExParam + ")'><span arial-hidden='true'><span class='glyphicon glyphicon-forward' aria-hidden='true'></span></span></a></li>";
	pagingHtml += "<li class='page-item'><a href='#' class='page-link' onclick="+pageFunc+"(" + (endPage + 1 > totalPage ? totalPage : (endPage + 1)) + sExParam + ");return false; ' style='margin:0'>&gt</a></li>";
	pagingHtml += "<li class='page-item'><a href='#' class='page-link' onclick="+pageFunc+"(" + totalPage +sExParam + ");return false; '>&gt&gt</a></li>";
	
//	pagingHtml += "</ul>";
	console.log(pagingHtml);
	return pagingHtml;
};





/**
 * 폼 요소 값이 비어있는지 체크하고 비어있으면 메시지를 표시한다.
 *
 * @param
 *   arr : 비어있는지 체크할 폼 요소 id, 얼럿 메시지 쌍의 배열
 *     ex) checkNotEmpty[ ["elem1","메시지1"], ["elem2","메시지2"] ] 
 *
 * @return : 값이 비어있는 폼 요소가 하나라도 있으면 false 값이 전부 입력된 경우 true
 */
function checkNotEmpty(arr) {
    
	for(var i=0, len=arr.length; i<len; i++) {
		
		var $elem = $('#' + arr[i][0]);
		var elemValue = $.trim( $elem.val() );
		var alertMsg = arr[i][1];

		if ( elemValue == "" ) {
			alert(alertMsg);
			$elem.focus();
			return false;
		}
	} 
	 
    return true;	 
} 

/**
 * 폼 요소 값이 비어있는지 체크하고 비어있으면 메시지를 표시한다.
 *
 * @param
 *   arr : 비어있는지 체크할 폼 요소, 얼럿 메시지 쌍의 배열
 *     ex) checkNotEmpty[ [$("#elem1"),"메시지1"], [$("#elem2"),"메시지2"] ] 
 *
 * @return : 값이 비어있는 폼 요소가 하나라도 있으면 false 값이 전부 입력된 경우 true
 */
function checkNotEmptyElement(arr) {
    
	for(var i=0, len=arr.length; i<len; i++) {
		
		var $elem = arr[i][0];
		var elemValue = $.trim( $elem.val() );
		var alertMsg = arr[i][1];

		if ( elemValue == "" ) {
			alert(alertMsg);
			$elem.focus();
			return false;
		}
	} 
	 
    return true;	 
} 

/**
 * 폼 요소 값이 전부 비어있는지 체크하고 전부 비어있으면 메시지를 표시한다.
 *
 * @param
 *   arr : 비어있는지 체크할 폼 요소, 얼럿 메시지 쌍의 배열
 *     ex) checkAllEmpty( ['id1','id2',... ], '메시지' ) 
 *
 * @return : 값이 입력된 폼 요소가 하나라도 있으면 false 값이 전부 비어있는 경우 true
 */
function checkAllEmpty(arr, msg) {
	
	for(var i=0, len=arr.length; i<len; i++) {
		
		var $elem = $('#' + arr[i]);
		var elemValue = $.trim( $elem.val() );

		if ( elemValue != "" ) {
			return false;
		}
	} 
	 
	alert(msg);
	
    return true;
}

/**
 * 폼 요소 값이 전부 비어있는지 체크하고 전부 비어있으면 메시지를 표시한다.
 *
 */
function checkAllEmptyElement(arr, msg) {
	
	for(var i=0, len=arr.length; i<len; i++) {
		
		var $elem = arr[i];
		var elemValue = $.trim( $elem.val() );

		if ( elemValue != "" ) {
			return false;
		}
	} 
	 
	alert(msg);
	
    return true;
}

/**
 * 폼이 요소 값들이 특정 패턴을 만족하는지 검사
 * 
 * @param arr - [아이디, 패턴, 메시지] 쌍의 배열 
 * @returns 요소들이 패턴을 만족하면 true 아니면 false
 *  사용예 ) 
 *	var gIdPattern = /^[A-Z0-9_]+$|^$/g;
 *	var numberPattern = /^[1-9]+[0-9]*$|^$/g;
 *	var patternMatch = checkPatternMatch(
 *		[
 *			 ['cId' , gIdPattern,    "상세코드 ID는 '_', '알파벳 대문자', '숫자'만 입력 가능합니다."]
 *			,['cSeq', numberPattern, "순서는 '숫자'만 입력 가능합니다."]
 *		]
 *	); 
 * 
 */
function checkPatternMatch(arr) {

	for(var i=0, len=arr.length; i<len; i++) {
		
		var $elem = $('#' + arr[i][0]);
		var elemValue = $.trim( $elem.val() );
		var pattern = arr[i][1];
		var alertMsg = arr[i][2];

		if ( elemValue.search(pattern) == -1 ) {
			alert(alertMsg);
			$elem.focus();
			return false;
		}
	} 
	
	return true;
}


/**
Calendar :  년, 월을 입력받아 해당 년 월의 달력 정보를 생성한다.

  @param:
  	year  ( deafult: current year )
    month ( default: current month )

  @method:
    2016년 9월
    주차  월 화 수 목 금 토 일
       1  28 29 30 31 01 02 03
       2  04 05 06 07 08 09 10
       3  11 12 13 14 15 16 17
       4  18 19 20 21 22 23 24
       5  25 26 27 28 29 30 01

    year         - 2016
    month        - 9
    dayNames     - [ 일, 월, 화, 수, 목, 금, 토 ]
    days         - 30  [ 2016년 9월의 총 일수  ]
    weeks        - 5  [ 2016년 9월이 총 몇주인지 ]
    day(1)       - 4, day(2) - 5, day(3) - 6, day(4) - 0, day(5) - 1  [ 해당일의 요일 번호 반환(0:일 ~ 6:토) ]
    dayName(1)   - 목, dayName(2) - 금, dayName(5) - 월
    week(1)      - 1, week(3) - 1, week(4) - 2, week(25) - 5  [ 해당일이 몇주차인지 반환 ]
    firstDay     - 4  [ 2016년 9월 1일의 요일 번호 ]
    lastDay      - 5  [ 2016년 9월 마지막일의 요일 번호 ]
    prevYear     - 2015
    nextYear     - 2017
    prevMonth    - 8
    nextMonth    - 10
    prev         - Calendar(2016, 8)   [ 전달의 달력 정보 반환 ]
    next         - Calendar(2016, 10)  [ 다음달의 달력 정보 반환 ]
    list         - 2016년 9월의 주차별/일자별 달력 정보를 리스트 형식으로 반환  [ 인덱스, 년, 월, 주차, 일, 요일번호, 요일명, 주말여부, 현재월여부 반환 ]
*/
function Calendar (year, month) {
  
  var today = new Date();

  var leapYear = function(y) {
    return (y % 4 == 0 && y % 100 != 0) || (y % 400 == 0);
  }
  
  var padZero = function(v) {
	 return Number(v) < 10 ? ('0' + v) : v;
  } 
  
  var toObj = function(o, i, d, c) {
    return {
       index    : i
      ,year     : o.year
      ,month    : o.month
      ,week     : o.week(d)
      ,date     : d
      ,ymd      : o.year + '' + padZero(o.month) + '' + padZero(d)
      ,day      : o.day(d)
      ,dayName  : o.dayName(d)
      ,holiday  : o.day(d) == 0 || o.day(d) == 6
      ,current  : c
    };
  };

  year  = Number(year)  || today.getFullYear();
  month = Number(month) || ( today.getMonth() + 1 );
  
  var daysOfFebruary = leapYear(year) ? 29 : 28;
  var daysOfMonth = [31,daysOfFebruary,31,30,31,30,31,31,30,31,30,31];
  var dayNames = ['일','월','화','수','목','금','토'];
  var dateObj = new Date(year, month - 1);
  
  return {
     year: year
    ,month: month
    ,ym : function() {
    	return year + padZero(month);
    }
    ,dayNames: dayNames
    ,days: function(){
    	return daysOfMonth[month - 1];
    }
    ,weeks: function() {
    	return Math.ceil( (this.firstDay() + this.days()) / 7 );
    }
    ,day: function(date) {
    	return (this.firstDay() + date - 1) % 7;
    }
    ,dayName: function(date) {
    	return dayNames[this.day(date)];
    }
    ,week: function(date) {
    	return Math.ceil( (this.firstDay() + date) / 7 );
    }
    ,firstDay: function() {
    	return dateObj.getDay();
    }
    ,lastDay: function() {
    	return this.day( this.days() );
    }
    ,prevYear: function() {
    	return month == 1 ? (year - 1) : year;
    }
    ,nextYear: function() {
    	return month == 12 ? (year + 1) : year;
    }
    ,prevMonth: function() {
    	return month == 1 ? 12 : (month - 1);
    }
    ,nextMonth: function() {
    	return month == 12 ? 1 : (month + 1);
    }
    ,prev: function() {
    	return Calendar( this.prevYear(), this.prevMonth() );
    }
    ,next: function() {
    	return Calendar( this.nextYear(), this.nextMonth() );
    }
    ,weekList: function() {
    	var index = 0;
    	var list = [];
    	var week = undefined;
    	var prev = this.prev();
    	
    	addDateInfo = function(obj, from, to, current) {
    		for(var d=from; d <= to; d++) {
    			if ( index % 7 == 0 ) {
    				week = [];
    				list.push( week );
    			}
    			week.push( toObj(obj, ++index, d, current) );
    		}
    	}
    	
    	addDateInfo( prev,        prev.days() - this.firstDay() + 1, prev.days(),        false );
    	addDateInfo( this,        1,                                 this.days(),        true );
    	addDateInfo( this.next(), 1,                                 6 - this.lastDay(), false );

    	return list;
    }
  };
}

/**
 * 0보다 작은 값은 0을 붙여준다
 * @param val
 * @returns
 */
function addZero(val) {
	var temp = parseInt(val);
	var result = "" + temp;
	if(result < 10)
		result = "0" + temp;
	return result;
}


/**
 * 이전 날짜 구하여 Element에 셋
 * @param id
 * @param num
 */
function setBeforeDate(id, num){
	var date = new Date();
	var yyyy = date.getFullYear();
	var mm   = date.getMonth();
	var dd   = date.getDate();
	
	var changeDate = new Date();
	changeDate.setFullYear(yyyy, mm, dd - num);
	
	var y = changeDate.getFullYear();
	var m = changeDate.getMonth() + 1;
	var d = changeDate.getDate();
	
	m = addZero(m);
	d = addZero(d);
	
	var resultDate = "" + y +"-"+ m +"-"+ d;
	$("#" + id).val(resultDate);
}


/**
 * 이전 날짜 구하여 Element에 셋
 * @param id
 * @param num
 */
function setBeforeMonth(id, num){
	var date = new Date();
	var yyyy = date.getFullYear();
	var mm   = date.getMonth();
	var dd   = date.getDate();
	
	var changeDate = new Date();
	changeDate.setFullYear(yyyy, mm - num, dd);
	
	var y = changeDate.getFullYear();
	var m = changeDate.getMonth() + 1;
	var d = changeDate.getDate();
	
	m = addZero(m);
	d = addZero(d);
	
	var resultDate = "" + y +"-"+ m +"-"+ d;
	$("#" + id).val(resultDate);
}

/**
 * AJAX 호출 공통
 * 
 * @param url
 * @param type
 * @param data
 * @param onSuccess
 * @returns
 */
function commonAjax(url, type, dataType, data, onSuccess,noLoadingBar) {
	$.ajax({	          
		url : url,
		type : type,
		dataType: "json",
		data : data,
	    beforeSend: function(xhr){
	        xhr.setRequestHeader(header,token);
	    },
		success : function(data) {
			onSuccess(data);
//			if (noLoadingBar) hideLoadingBar();			//bootstrap.js 제거로 인한 주석처리 20170911
		},
		error : function(xhr, exMessage) {
			if(xhr.status == 500){
				alert(xhr + ' : ' + exMessage);
				//document.location.href = '/manager/login.ps';
			}else{
				alert('[commonAjax] 시스템 오류가 발생하였습니다.');
				//document.location.href = '/manager/login.ps';
			}
//		    hideLoadingBar();								//bootstrap.js 제거로 인한 주석처리 20170911
		},
		beforeSend : function() {
//			if (noLoadingBar)  showLoadingBar();		//bootstrap.js 제거로 인한 주석처리 20170911
		}
	});
}

function getJson(url, data, onSuccess, noLoadingBar) {
	commonAjax(url, "get", "json", data, onSuccess, noLoadingBar);
}

function postJson(url, data, onSuccess, noLoadingBar) {
	commonAjax(url, "post", "json", data, onSuccess, noLoadingBar);
}

function replaceAll(str, searchStr, replaceStr) {
  return str.split(searchStr).join(replaceStr);
}


/**
 * 현재 일자를 기준으로 +- 한 날짜를 yyyymmdd 형식으로 구한다.
 * 
 * 사용예) 현재일이 20170220 이면 addDays(5) => '20170225' / addDays(-1) => '20170224'
 * 
 * @param days
 * @param baseYmd [기준일자: 'yyyymmdd' 형식으로 생략 가능]
 * @returns
 */
function addDays(days, baseYmd) {
	
	function dateAdd(days) {
		var addedDate = baseYmd ? ymdToDate(baseYmd) : new Date();
		addedDate.setDate(addedDate.getDate() + days);
		
		return addedDate;
	}
	function ymdToDate(ymd) {
		return new Date( Number(ymd.substr(0,4)), Number(ymd.substr(4,2))-1, Number(ymd.substr(6,2)) );
	}
	function ymd(date) {
		return date.getFullYear() + addZero(date.getMonth()+1) + addZero(date.getDate());
	}
	
	return ymd(dateAdd(days));
}
function addZero(d) {
	return d<10 ? "0"+d : ""+d;
}

/**
 * 오늘 일자 yyyymmdd 반환
 */
function today() {
	return addDays(0);
}

/**
 * 현재 일자를 기준으로 +- 한 날짜를 yyyymmdd 형식으로 구한다.
 * 
 * 사용예) 현재일이 20170220 이면 addMonths(5) => '20170720' / addMonths(-1) => '20170120'
 * 
 * @param months
 * @param baseYmd [기준일자: 'yyyymmdd' 형식으로 생략 가능]
 * @returns
 */
function addMonths(months, baseYmd) {
	var today = baseYmd || addDays(0);
	var year = today.substr(0,4);
	var month = today.substr(4,2);
	var day = today.substr(6,2);
	var diff = Number(month)+months;
	if ( diff <= 0 ) {
		year = (Number(year) - 1) + "";
		month = addZero(12 + diff) + "";
	}
	else if ( diff > 12 ) {
		year =(Number(year) + 1) + "";
		month = addZero(diff - 12) + "";
	}
	else {
		month = addZero(diff) + "";
	}
	
    var lastDayOfMonth = dayCount(year + month);
	if ( Number(day) > lastDayOfMonth  ) {
		day = lastDayOfMonth+"";
	}
 	
	return year + month + day;
}

/**
 * 현재 일자를 기준으로 +- 한 날짜를 yyyymmdd 형식으로 구한다.
 * 
 * 사용예) 현재일이 20170220 이면 addYears(5) => '20220220' / addYears(-1) => '20160220'
 * 
 * @param years
 * @param baseYmd [기준일자: 'yyyymmdd' 형식으로 생략 가능]
 * @returns
 */
function addYears(years, baseYmd) {
	
	function yearAdd(years) {
		var addedYear = baseYmd ? ymdToDate(baseYmd) : new Date();
		addedYear.setFullYear((years > 0) ? addedYear.getFullYear() + Math.abs(years) : addedYear.getFullYear() - Math.abs(years));

		return addedYear;
	}
	function ymdToDate(ymd) {
		return new Date(Number(ymd.substr(0, 4)), Number(ymd.substr(4, 2)) - 1,
				Number(ymd.substr(6, 2)));
	}
	function ymd(date) {
		return date.getFullYear() + addZero(date.getMonth() + 1)
				+ addZero(date.getDate());
	}

	return ymd(yearAdd(years));
}

/**
 * 입력한 년월일의 첫번째 일자를 반환 
 * firstDate('20170205') => '20170201'
 */
function firstDate(ymd) {
	return ymd.substr(0,6)+"01";
}

/**
 * 사용법 $("#TableID").rowspan(0);
 * @param colIdx	merge 할 row의 column 인덱스값 
 * @returns
 */
$.fn.rowspan = function (colIdx) {
	return this.each(function(){
		var that;
		$('tr', this).each(function(row) {
			$('td:eq(' + colIdx + ')', this).filter(':visible').each(function(col) {
				
				if($(this).html() == $(that).html()) {
					
					rowspan = $(that).attr("rowspan") || 1;
					rowspan = Number(rowspan) + 1;
					
					$(that).attr("rowspan", rowspan);
					$(this).attr("rowspan", rowspan);
					
					$(this).hide();
					
				}else {
					that = this;
				}
				
				that = (that == null) ? this : that;
			})
		})
	});
}

/**
 * 년월이 총 몇일까지 있는지 계산
 * @param ym [ 'yyyymm' or 'yyyymmdd', 생략가능 : 생략할 경우 현재 년월 기준]
 * @returns
 */
function dayCount(ym) {
	var dt = new Date(Number(ym.substr(0,4)), Number(ym.substr(4,2)), 0) || new Date();
	return dt.getDate();
}

/**
 *  입력한 년월일에 대해 해당 년월의 마지막일자를 반환
 *  dayCountYmd('20170205') => '20170228'
 */
function lastDate(ymd) {
	return ymd.substr(0,6)+dayCount(ymd);
}

/**
 * 두 날짜를 받아서 차이가 몇일인지 계산
 * 사용 예 ) daydiff('20170215','20170213') => -2
 */
function daydiff(first, second) {
	
	function parseDate(ymd) {
		return new Date(ymd.substr(0,4), Number(ymd.substr(4,2))+1, ymd.substr(6,2)).getTime();
	}
	
	return Math.round(( parseDate(second) - parseDate(first) )/(1000*60*60*24));
}

/**
 * 날짜 포맷 변경 : formatDt('20170223', '/')  =>  2017/02/23  
 */
function formatDt(ymd, delim) {
	return ymd.substr(0,4) + delim + ymd.substr(4,2) + delim + ymd.substr(6,2);
}

/**
 * 입력한 일짜가 yyyymmdd 형식에 맞는지 확인 
 * 
 *     checkDateFormat('20170308') => true
 *     checkDateFormat('20171313') => false
 *     checkDateFormat('20170013') => false
 *     checkDateFormat('20170132') => false
 */
function checkDateFormat(ymd) {
	var datePattern = /^([1-2][0-9]{3})(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])$/g;
	return ymd.search(datePattern) >= 0;
}

/**
 * 조회 일자 유효성 검증
 * 
 * @param fromDt  - 시작일 yyyymmdd
 * @param toDt    - 종료일 yyyymmdd
 * @param $fromDt - 시작일 입력 폼 jQuery 객체 [생략가능 / 기본값:$('#fromDate')]
 * @param $toDt   - 종요일 입력 폼 jQuery 객체 [생략가능 / 기본값:$('#toDate')]
 * @returns
 */
function validateDt(fromDt, toDt, $fromDt, $toDt) {
	
	$fromDt = $fromDt || $('#fromDate');
	$toDt = $toDt || $('#toDate');
	
	if ( !checkDateFormat(fromDt) ) {
		alert('시작 일자 형식이 맞지 않습니다.');
		$fromDt.focus();
		return false;
	}

	if ( !checkDateFormat(toDt) ) {
		alert('종료 일자 형식이 맞지 않습니다.');
		$toDt.focus();
		return false;
	}

	if ( fromDt > toDt) {
		alert('시작 일자를 종료 일자 이전으로 입력해주세요.');
		$fromDt.focus();
		return false;
	}
	
	var yesterday = addDays(-1);
	if ( fromDt > yesterday) {
		alert('시작 일자를 어제 일자 이전으로 입력해주세요');
		$fromDt.focus();
		return false;
	}
	
	if ( toDt > yesterday) {
		alert('종료 일자를 어제 일자 이전으로 입력해주세요');
		$toDt.focus();
		return false;
	}
	
	if ( addYears(1, fromDt) < toDt ) {
		alert('조회 기간은 최대 1년까지 가능합니다.');
		$fromDt.focus();
		return;
	}
	
	return true;
}

/**
 * 요일 반환
 * 
 */
function getDayOfWeek(date){
	var week = ['일', '월', '화', '수', '목', '금', '토']; 
	var dayOfWeek = week[new Date(date).getDay()]; 
	return dayOfWeek;
}

/**
 * 콤마 추가
 * numberWithComma(12345) -> 12,345
 */
function numberWithComma(x) {
	return Number(x).toString().replace(/\B(?=(\d{3})+(?!\d))/g,",");
}

/**
 * 대쉬 제거
 * removeDash('2017-03-16') -> '20170316'
 */
function removeDash(s) {
	return s.replace(/-/g, "");
}

/**
 * 년도에 대쉬 구분자 추가
 * addDashYmd('20170316') -> '2017-03-16'
 */
function addDashYmd(s) {
	return s.replace(/(\d{4})(\d{2})(\d{2})/g,"$1-$2-$3");
}

function getDayName(ymd) {
	var date = new Date( Number(ymd.substr(0,4)), Number(ymd.substr(4,2))-1, Number(ymd.substr(6,2)) );
	var dayNames = ['일','월','화','수','목','금','토'];
	return dayNames[date.getDay()];
}

/**
 * 문자열 생략 처리 ( 인자로 지정한 길이보다 문자열이 더 긴경우 ... 으로 표시 ) 
 * truncStr("가나다라마바사", 5)  =>  가나다라마 ...
 * truncStr("가나다라마바사", 10)  =>  가나다라마바사
 */
function truncStr(str, len) {
	if(typeof str == 'undefined'){
		return '';
	}
	if (str.length > len)
		return str.substr(0,len) + " ...";
	return str;
}

(function(){
	
	// IE에서 콘솔 객체 없는 경우 에러 방지용
	if ( !window.console ) {
		window.console = { log: function(){ return "noop"; } }
	}
	
	// underscore 템플릿 사용시  변수 제어에 {{}} 로 사용하도록 설정 ( default 설정( ${} )은 JSP EL 과 충돌 )
	if ( window._ ) {
		window._.templateSettings = { interpolate : /\{\{(.+?)\}\}/g };
	}
})();

function longToDate(str){
	var date = new Date(str);
	return date.format("yyyy-MM-dd"); 
}

function intToDate(str){
    str = String(str);
    return str.substring(0,4) + "-" + str.substring(4,6) + "-" + str.substring(6,8);
}
function intToDateMin(str){
    str = String(str);
    return str.substring(0,4) + "-" + str.substring(4,6) + "-" + str.substring(6,8) + " " + str.substring(8,10) + ":" + str.substring(10,12) ;
}


Date.prototype.format = function(f) {
    if (!this.valueOf()) return " ";
 
    var weekName = ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"];
    var d = this;
     
    return f.replace(/(yyyy|yy|MM|dd|E|hh|mm|ss|a\/p)/gi, function($1) {
        switch ($1) {
            case "yyyy": return d.getFullYear();
            case "yy": return (d.getFullYear() % 1000).zf(2);
            case "MM": return (d.getMonth() + 1).zf(2);
            case "dd": return d.getDate().zf(2);
            case "E": return weekName[d.getDay()];
            case "HH": return d.getHours().zf(2);
            case "hh": return ((h = d.getHours() % 12) ? h : 12).zf(2);
            case "mm": return d.getMinutes().zf(2);
            case "ss": return d.getSeconds().zf(2);
            case "a/p": return d.getHours() < 12 ? "오전" : "오후";
            default: return $1;
        }
    });
};


 function numberWithCommas(x) {
    if(x.toString() == null) x=0;
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
 }
 
 function isNumeric(num, opt){

  // 좌우 trim(공백제거)을 해준다.
  num = String(num).replace(/^\s+|\s+$/g, "");

  if(typeof opt == "undefined" || opt == "1"){
    // 모든 10진수 (부호 선택, 자릿수구분기호 선택, 소수점 선택)
    var regex = /^[+\-]?(([1-9][0-9]{0,2}(,[0-9]{3})*)|[0-9]+){1}(\.[0-9]+)?$/g;
  }else if(opt == "2"){
    // 부호 미사용, 자릿수구분기호 선택, 소수점 선택
    var regex = /^(([1-9][0-9]{0,2}(,[0-9]{3})*)|[0-9]+){1}(\.[0-9]+)?$/g;
  }else if(opt == "3"){
    // 부호 미사용, 자릿수구분기호 미사용, 소수점 선택
    var regex = /^[0-9]+(\.[0-9]+)?$/g;
  }else{
    var regex = /^[0-9]$/g;
  }

  if( regex.test(num) ){
    num = num.replace(/,/g, "");
    return isNaN(num) ? false : true;
  }else{ return false;  }

}

function pad2(n) { return n < 10 ? '0' + n : n }

function GetDateMMDDSS(jsonDate) {
  var ret = "";
  jsonDate = jsonDate + "";
  if ( jsonDate.length >= 14 ) {
    ret = jsonDate.substring(0,4) + "-" + jsonDate.substring(4,6) + "-" + jsonDate.substring(6,8) + " " + jsonDate.substring(8,10) + ":" + jsonDate.substring(10,12) + ":" + jsonDate.substring(12,14);
  }
  return ret;
}

function GetDateMMDDSSsss(jsonDate) {
  var ret = "";
  jsonDate = jsonDate + "";
  if ( jsonDate.length >= 14 ) {
    ret = jsonDate.substring(0,4) + "-" + jsonDate.substring(4,6) + "-" + jsonDate.substring(6,8) + " " + jsonDate.substring(8,10) + ":" + jsonDate.substring(10,12) + ":" + jsonDate.substring(12,14) + "." + jsonDate.substring(14);
  }else{
	ret = jsonDate;
 }
  return ret;
}


function GetDate(jsonDate) {
  var ret = "";
  if ( jsonDate != null && jsonDate != "undefined"  ) {
    jsonDate = jsonDate + "";
    if ( jsonDate.length >= 8 )  {
        ret = jsonDate.substring(0,4) + "-" + jsonDate.substring(4,6) + "-" + jsonDate.substring(6,8);
    }
  }
  return ret;
}

function GetTime(jsonDate) {
  var ret = "";
  if ( jsonDate != null && jsonDate != "undefined"  ) {
    jsonDate = jsonDate + "";
    if ( jsonDate.length >= 4 )  {
        ret = jsonDate.substring(0,2) + ":" + jsonDate.substring(2,4);
    }
  }
  return ret;
}

function GetTimeAmPm(jsonDate) {
  var ret = "";
  if ( jsonDate != null && jsonDate != "undefined"  ) {
    jsonDate = jsonDate + "";
    
    var yyyymmdd = jsonDate.substring(0,8);
	var hh = jsonDate.substring(8,10), min = jsonDate.substring(10,12);
	var ampm = hh >= 12 ? "pm" : "am";
    hh = hh % 12;
    hh = hh ? hh : 12;
    //hh = hh < 10 ? "0" + hh : hh;
    //min = min < 10 ? "0" + min : min;

    ret = hh + ":" + min + " " + ampm;
  }
  return ret;
}

function GetTaggingData(sentence) {
  var ret = '<font>'+sentence+'</font>';
  ret = replaceAll(ret,'<!TS>','<mark>');
  ret = replaceAll(ret,'<!TE>','</mark>');
  return ret;
}

function GetInterval(milliSec) {
	milliSec = parseInt(String(milliSec).replace('.',''));
	const days = Math.floor(milliSec / (1000 * 60 * 60 * 24)); // 일
	const hour = String(Math.floor((milliSec/ (1000 * 60 *60 )) % 24 )).padStart(2, "0"); // 시
	const minutes = String(Math.floor((milliSec  / (1000 * 60 )) % 60 )).padStart(2, "0"); // 분
	const second = String(Math.floor((milliSec / 1000 ) % 60)).padStart(2, "0"); // 초
 	
 	var ret = (days != '0' ? `${days}일 ` : "");
 	ret += (hour != '00' ? `${hour}시 ` : "");
 	ret += (minutes != '00' ? `${minutes}분 ` : "");
 	ret += `${second}초`;
	return ret;
}

function GetDateHHMISS(jsonDate) {
  var ret = "";
  if ( jsonDate != null && jsonDate != "undefined"  ) {
    jsonDate = jsonDate + "";
    if ( jsonDate.length >= 8 )  {
        ret = jsonDate.substring(8,10) + ":" + jsonDate.substring(10,12) + ":" + jsonDate.substring(12,14);
    }
  }
  return ret;
}

var patternSpecial = /[\{\}\[\]\/?.,;:|\)*~`!^\-_+<>@\#$%&\\\=\(\'\"\/]/gi
var patternSpecialComma = /[\{\}\[\]\/?.;:|\)*~`!^\-_+<>@\#$%&\\\=\(\'\"\/]/gi
var patternHanEng = /^[가-힣a-zA-Z]+$/
var patternHanEngNum = /^[가-힣a-zA-Z0-9_-]+$/
var patternNumSlash= /[^0-9\/]/g;
var patternNum = /^[0-9]+$|^$/;
var patternSpace = /[\s]/g;

var patternEngNum = /[^a-zA-Z0-9]+$/;
var patternSpecialGolbang = /[ \{\}\[\]\/?.,;:|\)*~`!^\+┼<>\#$%&\'\"\\\(\=]/g;

var patternEngNumNoSpRange = /^[\w@]{2,12}$/;
var patternEngNumRange = /^[\w@_-]{2,12}$/;
var patternOnlyEngNumRange = /^[\w]{2,12}$/;
var patternHanEngNumNoSpRange = /^[\w가-힣]{2,12}$/;
var patternHanEngNumRange = /^[\w가-힣@_-]{2,12}$/;
var patternHanEngNumRange2 = /^[\s\w가-힣@_\.()+/-]{2,50}$/;
var patternHanEngNumComma = /^[a-zA-Z0-9가-힣,]{1,100}$/;

var patternHanEngNumSpaceRange = /^[\w가-힣\s#+@&^%]{2,12}$/;
var patternHanEngNumSpaceRange2 = /^[\w가-힣\s]{2,50}$/;
var patternNumPlusMinus = /^[+-]?[\d*]{1,4}$/;

String.prototype.unescapeHtml = function(){
  return this.replace(/&amp;/g, "&").replace(/&lt;/g, "<").replace(/&gt;/g, ">").replace(/&quot;/g, "\"").replace(/&#39;/g, "'");
};
String.prototype.string = function(len){var s = '', i = 0; while (i++ < len) { s += this; } return s;};
String.prototype.zf = function(len){return "0".string(len - this.length) + this;};
Number.prototype.zf = function(len){return this.toString().zf(len);};
