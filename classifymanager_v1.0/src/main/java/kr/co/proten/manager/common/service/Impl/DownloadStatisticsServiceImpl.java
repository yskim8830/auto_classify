package kr.co.proten.manager.common.service.Impl;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kr.co.proten.manager.common.service.DownloadDataService;
import kr.co.proten.manager.common.util.DateUtil;
import kr.co.proten.manager.login.model.LoginModel;
import lombok.RequiredArgsConstructor;

@Service("downloadStatisticsService")
@RequiredArgsConstructor
public class DownloadStatisticsServiceImpl extends DownloadDataService {
	
	private static final Logger log = LoggerFactory.getLogger(DownloadStatisticsServiceImpl.class);
	
	private final String[] TREND_HEADER_NAME = {"통계기준","통계라벨","문서 건수","분류 건수","매칭률","룰 매칭 건수","분류 매칭 건수"};
	private final String[] CATEGORY_HEADER_NAME = {"통계기준","매칭 건수"};
	private String downloadFileName = "통계데이터";

	@Override
	public void writeExcelFile(String type, List<Map<String, Object>> dataList, LoginModel login, HttpServletResponse response) throws Exception {
		
		String fileName = new String((downloadFileName).getBytes("euc-kr"), "8859_1") + "_" + DateUtil.getCurrentDateTimeMille() + EXCEL_EXTENSION;
		
				
		// 파일 생성
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("download data");
		int rowNum = 0;
		
		// 셀 스타일
		XSSFCellStyle styleTitle = getTitleStyle(workbook);
		XSSFCellStyle styleHeader = getHeaderStyle(workbook);
		XSSFCellStyle styleCenterStr = getStrCenterStyle(workbook);
		XSSFCellStyle styleLeftStr = getStrLeftStyle(workbook);
		XSSFCellStyle styleNum = getNumberStyle(workbook);
		
		// 헤더 생성
		XSSFRow headerRow = sheet.createRow(rowNum++);
		if(type.equals("trend")) {
			for(int idx=0; idx<TREND_HEADER_NAME.length; idx++) {
				XSSFCell cell = headerRow.createCell(idx);
				cell.setCellStyle(styleHeader);
				cell.setCellValue(TREND_HEADER_NAME[idx]);
			}			
		} else if(type.equals("category")) {
			for(int idx=0; idx<CATEGORY_HEADER_NAME.length; idx++) {
				XSSFCell cell = headerRow.createCell(idx);
				cell.setCellStyle(styleHeader);
				cell.setCellValue(CATEGORY_HEADER_NAME[idx]);
			}	
		}
		
		// 데이터 쓰기
		XSSFRow dataRow = null;
		XSSFCell dataCell = null;
		if(dataList != null && dataList.size() > 0) {
			
			for(Map<String, Object> dataMap : dataList){
				
				dataRow = sheet.createRow(rowNum++);
				
				if(type.equals("trend")) {
					// 통계기준
					dataCell = dataRow.createCell(0);
					dataCell.setCellStyle(styleCenterStr);
					dataCell.setCellValue(String.valueOf(dataMap.get("stdValue")));
					
					// 통계라벨
					dataCell = dataRow.createCell(1);
					dataCell.setCellStyle(styleCenterStr);
					dataCell.setCellValue(String.valueOf(dataMap.get("stdViewValue")));
					
					// 문서 건수
					dataCell = dataRow.createCell(2);
					dataCell.setCellStyle(styleLeftStr);
					dataCell.setCellValue(String.valueOf(dataMap.get("trend")));
					
					// 분류 건수
					dataCell = dataRow.createCell(3);
					dataCell.setCellStyle(styleLeftStr);
					dataCell.setCellValue(String.valueOf(dataMap.get("matched")));
					
					// 매칭률
					dataCell = dataRow.createCell(4);
					dataCell.setCellStyle(styleCenterStr);
					dataCell.setCellValue(String.valueOf(dataMap.get("matched_rate")));
					
					// 룰 매칭 건수
					dataCell = dataRow.createCell(5);
					dataCell.setCellStyle(styleLeftStr);
					dataCell.setCellValue(String.valueOf(dataMap.get("rule")));
					
					// 분류 매칭 건수
					dataCell = dataRow.createCell(6);
					dataCell.setCellStyle(styleLeftStr);
					dataCell.setCellValue(String.valueOf(dataMap.get("classify")));
				} else if(type.equals("category")) {
					// 통계기준
					dataCell = dataRow.createCell(0);
					dataCell.setCellStyle(styleCenterStr);
					dataCell.setCellValue(String.valueOf(dataMap.get("categoryName")));
					
					// 통계라벨
					dataCell = dataRow.createCell(1);
					dataCell.setCellStyle(styleCenterStr);
					dataCell.setCellValue(String.valueOf(dataMap.get("count")));
				}
			}
		}
		
		if(type.equals("trend")) {
			for(int idx=0; idx<TREND_HEADER_NAME.length; idx++) {
				sheet.autoSizeColumn(idx);	
				//sheet.setColumnWidth(idx, (sheet.getColumnWidth(idx))+2500);
				sheet.setColumnWidth(idx, Math.min(128 * 128, sheet.getColumnWidth(idx) + 1200));
			}		
		} else if(type.equals("category")) {
			for(int idx=0; idx<CATEGORY_HEADER_NAME.length; idx++) {
				sheet.autoSizeColumn(idx);	
				//sheet.setColumnWidth(idx, (sheet.getColumnWidth(idx))+2500);
				sheet.setColumnWidth(idx, Math.min(128 * 128, sheet.getColumnWidth(idx) + 1200));
			}	
		}
		
		
		// 컨텐츠 타입과 파일명 지정
		response.setContentType("ms-vnd/excel");
		response.setHeader("Pragma", "no-cache;");
	    response.setHeader("Expires", "-1;");
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

		// 엑셀 출력
		workbook.write(response.getOutputStream());
		workbook.close();
	}
}
