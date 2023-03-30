	var target; 																	// 호출한 Object의 저장
	var stime;
	$(function(){
		$('body').append("<div id='minical' oncontextmenu='return false' ondragstart='return false' onselectstart='return false' style=\"background:DDE4FF; margin:5; padding:5;margin-top:2;border-top:1 solid buttonshadow;border-left: 1 solid buttonshadow;border-right: 1 solid buttonshadow;border-bottom:1 solid buttonshadow;width:160px;display:none;position: absolute; z-index: 999999\"></div>");
		
//		$(document).mouseup(function (e) {
//			var minical = $("#minical");
//			if( !minical.is(e.target) && minical.has(e.target) ){
//				minical.hide();
//			}
//		});
	});
	

// dayToBlur (yyyymmdd) : 특정일 이전 일자는 흐릿하게 표시하고 선택하지 못하도록 하는 경우 지정
function MiniCalendar(obj, dayToBlur) {														// jucke
	var now = obj.value.split("-");
	var x, y;
	
	target = obj;																// Object 저장;

	if( event )
	{
		//x = (document.layers) ? loc.pageX : event.pageX;
		//y = (document.layers) ? loc.pageY : event.pageY;
		x = (document.all) ? loc.pageX : event.pageX;
		y = (document.all) ? loc.pageY : event.pageY;
		$('#minical').css('top', y+5);
		$('#minical').css('left', x-50);
		$('#minical').toggle();
	
		if (now.length == 3) {														// 정확한지 검사
			Show_cal(now[0],now[1],now[2], dayToBlur);											// 넘어온 값을 년월일로 분리
		} else {
			now = new Date();
			Show_cal(now.getFullYear(), now.getMonth()+1, now.getDate(), dayToBlur);			// 현재 년/월/일을 설정하여 넘김.
		}
	}
	
}

function doOver() {																// 마우스가 칼렌다위에 있으면
	var el = window.event.srcElement;
	cal_Day = el.title;

	if (cal_Day.length > 7) {													// 날자 값이 있으면.
		el.style.borderTopColor = el.style.borderLeftColor = "buttonhighlight";
		el.style.borderRightColor = el.style.borderBottomColor = "buttonshadow";
	}
	window.clearTimeout(stime);													// Clear
}

function doClick() {															// 날자를 선택하였을 경우
	cal_Day = window.event.srcElement.title;
	window.event.srcElement.style.borderColor = "red";							// 테두리 색을 빨간색으로
	if (cal_Day.length > 7) {													// 날자 값이있으면
		target.value=addDashYmd(cal_Day);													// 값 설정
	}
	minical.style.display='none';												// 화면에서 지움
}

function doOut() {
	var el = window.event.fromElement;
	cal_Day = el.title;

	if (cal_Day.length > 7) {
		el.style.borderColor = "white";
	}
	//stime=window.setTimeout("minical.style.display='none';", 200);
}

function day2(d) {																// 2자리 숫자로 변경
	var str = new String();
	
	if (parseInt(d) < 10) {
		str = "0" + parseInt(d);
	} else {
		str = "" + parseInt(d);
	}
	return str;
}

function Show_cal(sYear, sMonth, sDay, dayToBlur) {	// dayToBlur (yyyymmdd) : 특정일 이전 일자는 흐릿하게 표시하고 선택하지 못하도록 하는 경우 지정
	var Months_day = new Array(0,31,28,31,30,31,30,31,31,30,31,30,31)
	var Weekday_name = new Array("일", "월", "화", "수", "목", "금", "토");
	var intThisYear = new Number(), intThisMonth = new Number(), intThisDay = new Number();
	document.all.minical.innerHTML = "";
	datToday = new Date();													// 현재 날자 설정
	
	intThisYear = parseInt(sYear);
	intThisMonth = parseInt(sMonth);
	intThisDay = parseInt(sDay);
	
	if (intThisYear == 0) intThisYear = datToday.getFullYear();				// 값이 없을 경우
	if (intThisMonth == 0) intThisMonth = parseInt(datToday.getMonth())+1;	// 월 값은 실제값 보다 -1 한 값이 돼돌려 진다.
	if (intThisDay == 0) intThisDay = datToday.getDate();
	
	switch(intThisMonth) {
		case 1:
				intPrevYear = intThisYear -1;
				intPrevMonth = 12;
				intNextYear = intThisYear;
				intNextMonth = 2;
				break;
		case 12:
				intPrevYear = intThisYear;
				intPrevMonth = 11;
				intNextYear = intThisYear + 1;
				intNextMonth = 1;
				break;
		default:
				intPrevYear = intThisYear;
				intPrevMonth = parseInt(intThisMonth) - 1;
				intNextYear = intThisYear;
				intNextMonth = parseInt(intThisMonth) + 1;
				break;
	}

	NowThisYear = datToday.getFullYear();										// 현재 년
	NowThisMonth = datToday.getMonth()+1;										// 현재 월
	NowThisDay = datToday.getDate();											// 현재 일
	
	datFirstDay = new Date(intThisYear, intThisMonth-1, 1);						// 현재 달의 1일로 날자 객체 생성(월은 0부터 11까지의 정수(1월부터 12월))
	intFirstWeekday = datFirstDay.getDay();										// 현재 달 1일의 요일을 구함 (0:일요일, 1:월요일)
	
	intSecondWeekday = intFirstWeekday;
	intThirdWeekday = intFirstWeekday;
	
	datThisDay = new Date(intThisYear, intThisMonth, intThisDay);				// 넘어온 값의 날자 생성
	intThisWeekday = datThisDay.getDay();										// 넘어온 날자의 주 요일

	varThisWeekday = Weekday_name[intThisWeekday];								// 현재 요일 저장
	
	intPrintDay = 1																// 달의 시작 일자
	secondPrintDay = 1
	thirdPrintDay = 1
	
	Stop_Flag = 0
	
	if ((intThisYear % 4)==0) {													// 4년마다 1번이면 (사로나누어 떨어지면)
		if ((intThisYear % 100) == 0) {
			if ((intThisYear % 400) == 0) {
				Months_day[2] = 29;
			}
		} else {
			Months_day[2] = 29;
		}
	}
	intLastDay = Months_day[intThisMonth];										// 마지막 일자 구함
	Stop_flag = 0
	
	Cal_HTML = "<div class=\"cal-wrap\" style=\"position:absolute;top:5px;left:55px;\">";
	Cal_HTML +="<div class=\"cal-typeA\"><div class=\"cal-header\" style=\"margin-left :35px; margin-top: 10px;\">";
	Cal_HTML +="<ul> <li><a href=\"javascript:;\"><img src=\"/images/btn_cal_prev.gif\" style=\"height: 25px; margin-top: 3px; margin-right: 3px; border: solid 0.3px;\" alt=\"이전달\" onclick='Show_cal("+intPrevYear+","+intPrevMonth+",1,"+dayToBlur+");' /></a></li>";
	Cal_HTML +="<li class=\"cal-date mt_5\" ><span>"+get_Yearinfo(intThisYear,intThisMonth,intThisDay)+"년</span><span>"+get_Monthinfo(intThisYear,intThisMonth,intThisDay)+"월</span></li>";
	Cal_HTML +="<li><a href=\"javascript:;\"><img src=\"/images/btn_cal_next.gif\" style=\"height: 25px; margin-top: 3px; margin-left: 3px; border: solid 0.3px;\"  alt=\"다음달\" onclick='Show_cal("+intNextYear+","+intNextMonth+",1,"+dayToBlur+");'/></a></li><ul>";
	Cal_HTML +="</div>";
	Cal_HTML +="<div class=\"cal-cont\" style=\"margin-left : 20px;\">";
	Cal_HTML +="<table summary=\"날짜를 선택합니다\" class=\"calendar\">";
	Cal_HTML +="<colgroup>";
	Cal_HTML +="	<col width=\"10px\" /><col width=\"10px\" /><col width=\"10px\" /><col width=\"10px\" /><col width=\"10px\" /><col width=\"10px\" /><col width=\"10px\" />";
	Cal_HTML +="</colgroup>";
	Cal_HTML +="<thead>";
	Cal_HTML +="<tr><th scope=\"col\" class=\"sun\">일</th><th scope=\"col\">월</th><th scope=\"col\">화</th><th scope=\"col\">수</th><th scope=\"col\">목</th><th scope=\"col\">금</th><th scope=\"col\" class=\"sat\">토</th></tr>";
	Cal_HTML +="</thead>";
	Cal_HTML +="<tbody>";
	
	// 해당 일자를 선택하지 못하도록 해야 하는지 체크 ( dayToBlur 이전 일자는 흐릿하게 표시 )
	var needBlur = function(thirdPrintDay, dayToBlur) {
		var isBlur = !!dayToBlur;
		var padZero = function(v) {
		    return Number(v) < 10 ? ('0' + v) : v;
        } 
		var thisYmd = intThisYear + "" + padZero(intThisMonth) + "" + padZero(thirdPrintDay);
		return isBlur && thisYmd <= dayToBlur;
	}
	
	for (intLoopWeek=1; intLoopWeek < 7; intLoopWeek++) {						// 주단위 루프 시작, 최대 6주
		Cal_HTML += "<tr>"
		for (intLoopDay=1; intLoopDay <= 7; intLoopDay++) {						// 요일단위 루프 시작, 일요일 부터
			var blur = needBlur(thirdPrintDay, dayToBlur);
			if (intThirdWeekday > 0) {											// 첫주 시작일이 1보다 크면
				Cal_HTML += "<td class=\"sun\" onclick=doClick();>";
				intThirdWeekday--;
			} else {
				if (thirdPrintDay > intLastDay) {								// 입력 날짝 월말보다 크다면
					Cal_HTML += "<td onclick=doClick();>";
				} else {														// 입력날짜가 현재월에 해당 되면
					if ( blur ) { // dayToBlur 이전 일자는 클릭 이벤트 없음
						Cal_HTML += "<a href=\"javascript:;\"><td title=\""+intThisYear+day2(intThisMonth).toString()+day2(thirdPrintDay).toString()+"\" onmouseover=\"this.style.background='#838383'\" onmouseout=\"this.style.background='#FFFFFF'\" style=\"cursor: pointer;";
					}
					else {
						Cal_HTML += "<a href=\"javascript:;\"><td onclick=doClick(); title=\""+intThisYear+day2(intThisMonth).toString()+day2(thirdPrintDay).toString()+"\" onmouseover=\"this.style.background='#838383'\" onmouseout=\"this.style.background='#FFFFFF'\" style=\"cursor: pointer;";
					}
					if (intThisYear == NowThisYear && intThisMonth==NowThisMonth && thirdPrintDay==intThisDay) {
						Cal_HTML += "background-color:DDE4FF;";
					}
					
					switch(intLoopDay) {
						case 1:													// 일요일이면 빨간 색으로
							Cal_HTML += "color:red;"
							break;
						case 7:
							Cal_HTML += "color:blue;"
							break;
						default:
							Cal_HTML += "color:black;"
							break;
					}
					
					if ( blur ) { // dayToBlur 이전 일자는 흐릿하게 표시
						Cal_HTML += "opacity:0.3;";
					}
					
					
					Cal_HTML += "\">"+thirdPrintDay;
					
				}
				thirdPrintDay++;
				
				if (thirdPrintDay > intLastDay) {								// 만약 날짜 값이 월말 값보다 크면 루프문 탈출
					Stop_Flag = 1;
				}
			}
			Cal_HTML += "</td></a>";
		}
		Cal_HTML += "</tr>";
		if (Stop_Flag==1) break;
	}
	Cal_HTML += "</table>";
	document.all.minical.innerHTML = Cal_HTML;
}


function get_Yearinfo(year,month,day) {											// 년 정보를 콤보 박스로 표시
	var min = parseInt(year) - 100;
	var max = parseInt(year) + 10;
	var i = new Number();
	var str = new String();
 
	str = "<select onchange='Show_cal(this.value,"+month+","+day+");' onmouseover=doOver(); style='width:85px'>";
	for (i=min; i<=max; i++) {
		if (i == parseInt(year)) {
			str += "<option value="+i+" selected onmouseover=doOver();>"+i+"</option>";
		} else {
			str += "<option value="+i+" onmouseover=doOver();>"+i+"</option>";
		}
	}
	str += "</select>";
	return str;
}


function get_Monthinfo(year,month,day) {										// 월 정보를 콤보 박스로 표시
	var i = new Number();
	var str = new String();
	
	str = "<select onchange='Show_cal("+year+",this.value,"+day+");' onmouseover=doOver(); style='width:60px; margin-left:10px;' >";
	for (i=1; i<=12; i++) {
		if (i == parseInt(month)) {
			str += "<option value="+i+" selected onmouseover=doOver();>"+i+"</option>";
		} else {
			str += "<option value="+i+" onmouseover=doOver();>"+i+"</option>";
		}
	}
	str += "</select>";
	return str;
}
