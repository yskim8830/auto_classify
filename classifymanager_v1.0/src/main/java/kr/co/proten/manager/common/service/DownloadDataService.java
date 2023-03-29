package kr.co.proten.manager.common.service;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import kr.co.proten.manager.login.model.LoginModel;

public abstract class DownloadDataService {
	
	public final String MSOFFICE_EXCEL_EXT_XLSX = "xlsx";
	public final String MSOFFICE_EXCEL_EXT_XLS = "xls";
	public final String EXCEL_EXTENSION = ".xlsx";
	public final String DOWNLOAD_DATA_TASK = "data";
	public final String DOWNLOAD_TEMPLATE_TASK = "template";
	
	/**
	 * 다운로드 대상 데이터
	 * @param login
	 * @return
	 */
	public List<Map<String,Object>> getDataList(LoginModel login) {
		return null;
	}
	
	/**
	 * 다운로드 파일 생성
	 * @param type
	 * @param dataList
	 * @param login
	 * @param response
	 * @throws Exception
	 */
	public void writeExcelFile(String type, List<Map<String,Object>> dataList, LoginModel login, HttpServletResponse response) throws Exception {
	};
	
	/**
	 * 템플릿 파일 생성
	 * @throws Exception
	 */
	public void writeTemplateFile(String templateFileName, String[] headerNames, HttpServletResponse response) throws Exception {
		String fileName = "";
		fileName = new String((templateFileName).getBytes("euc-kr"), "8859_1") + EXCEL_EXTENSION;
		
		// 파일 생성
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("download data");
		int rowNum = 0;
				
		// 셀 스타일
		XSSFCellStyle styleHeader = getHeaderStyle(workbook);
		
		// 헤더 생성
		XSSFRow headerRow = sheet.createRow(rowNum++);
		for(int idx=0; idx<headerNames.length; idx++) {
			XSSFCell cell = headerRow.createCell(idx);
			cell.setCellStyle(styleHeader);
			cell.setCellValue(headerNames[idx]);
		}
		
		for(int idx=0; idx<headerNames.length; idx++) {
			sheet.autoSizeColumn(idx);	
			//sheet.setColumnWidth(idx, (sheet.getColumnWidth(idx))+2500);
			sheet.setColumnWidth(idx, Math.min(128 * 128, sheet.getColumnWidth(idx) + 1200));
		}
		
		// 컨텐츠 타입과 파일명 지정
		response.setContentType("ms-vnd/excel");
		response.setHeader("Pragma", "no-cache;");
	    response.setHeader("Expires", "-1;");
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

		// 엑셀 출력
		workbook.write(response.getOutputStream());
		workbook.close();
	};
	
	/**
	 * 제목 스타일
	 * @param workbook
	 * @return
	 */
	protected XSSFCellStyle getTitleStyle(XSSFWorkbook workbook) {
		XSSFCellStyle styleTitle = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontHeight(20);
		font.setBold(true);
		styleTitle.setFont(font);
		styleTitle.setAlignment(HorizontalAlignment.CENTER);
		styleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
		return styleTitle;
	}
	
	/**
	 * 헤더 스타일
	 * @param workbook
	 * @return
	 */
	protected XSSFCellStyle getHeaderStyle(XSSFWorkbook workbook) {
		XSSFCellStyle styleHeader = workbook.createCellStyle();
		styleHeader.setBorderBottom(BorderStyle.THIN);
		styleHeader.setBottomBorderColor(new XSSFColor(Color.BLACK));
		styleHeader.setBorderLeft(BorderStyle.THIN);
		styleHeader.setLeftBorderColor(new XSSFColor(Color.GREEN));
		styleHeader.setBorderRight(BorderStyle.THIN);
		styleHeader.setRightBorderColor(new XSSFColor(Color.BLUE));
		styleHeader.setBorderTop(BorderStyle.THIN);
		styleHeader.setTopBorderColor(new XSSFColor(Color.BLACK));
		styleHeader.setFillBackgroundColor(new XSSFColor(Color.WHITE));
		styleHeader.setFillForegroundColor(new XSSFColor(new Color(Integer.parseInt("93ccea", 16))));
		styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleHeader.setAlignment(HorizontalAlignment.CENTER);
		styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
		return styleHeader;
	}
	
	/**
	 * 텍스트 중앙정렬 스타일
	 * @param workbook
	 * @return
	 */
	protected XSSFCellStyle getStrCenterStyle(XSSFWorkbook workbook) {
		XSSFCellStyle styleStr = workbook.createCellStyle();
		styleStr.setBorderBottom(BorderStyle.THIN);
		styleStr.setBottomBorderColor(new XSSFColor(Color.BLACK));
		styleStr.setBorderLeft(BorderStyle.THIN);
		styleStr.setLeftBorderColor(new XSSFColor(Color.GREEN));
		styleStr.setRightBorderColor(new XSSFColor(Color.BLUE));
		styleStr.setBorderTop(BorderStyle.THIN);
		styleStr.setTopBorderColor(new XSSFColor(Color.BLACK));
		styleStr.setAlignment(HorizontalAlignment.CENTER);
		styleStr.setVerticalAlignment(VerticalAlignment.CENTER);
		return styleStr;
	}
	
	/**
	 * 텍스트 왼쪽정렬 스타일
	 * @param workbook
	 * @return
	 */
	protected XSSFCellStyle getStrLeftStyle(XSSFWorkbook workbook) {
		XSSFCellStyle styleStr = workbook.createCellStyle();
		styleStr.setBorderBottom(BorderStyle.THIN);
		styleStr.setBottomBorderColor(new XSSFColor(Color.BLACK));
		styleStr.setBorderLeft(BorderStyle.THIN);
		styleStr.setLeftBorderColor(new XSSFColor(Color.GREEN));
		styleStr.setBorderRight(BorderStyle.THIN);
		styleStr.setRightBorderColor(new XSSFColor(Color.BLUE));
		styleStr.setBorderTop(BorderStyle.THIN);
		styleStr.setTopBorderColor(new XSSFColor(Color.BLACK));
		styleStr.setAlignment(HorizontalAlignment.LEFT);
		styleStr.setVerticalAlignment(VerticalAlignment.CENTER);
		return styleStr;
	}
	
	/**
	 * 숫자 스타일
	 * @param workbook
	 * @return
	 */
	protected XSSFCellStyle getNumberStyle(XSSFWorkbook workbook) {
		XSSFCellStyle styleNum = workbook.createCellStyle();
		XSSFDataFormat df = workbook.createDataFormat();
		styleNum.setDataFormat(df.getFormat("General"));
		styleNum.setBorderBottom(BorderStyle.THIN);
		styleNum.setBottomBorderColor(new XSSFColor(Color.BLACK));
		styleNum.setBorderLeft(BorderStyle.THIN);
		styleNum.setLeftBorderColor(new XSSFColor(Color.GREEN));
		styleNum.setBorderRight(BorderStyle.THIN);
		styleNum.setRightBorderColor(new XSSFColor(Color.BLUE));
		styleNum.setBorderTop(BorderStyle.THIN);
		styleNum.setTopBorderColor(new XSSFColor(Color.BLACK));
		styleNum.setAlignment(HorizontalAlignment.RIGHT);
		return styleNum;
	}
}
