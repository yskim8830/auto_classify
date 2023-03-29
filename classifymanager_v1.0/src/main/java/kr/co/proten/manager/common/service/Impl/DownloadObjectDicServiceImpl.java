package kr.co.proten.manager.common.service.Impl;

import java.util.HashMap;
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
import kr.co.proten.manager.dic.service.ObjectService;
import kr.co.proten.manager.login.model.LoginModel;
import lombok.RequiredArgsConstructor;

@Service("downloadObjectDicService")
@RequiredArgsConstructor
public class DownloadObjectDicServiceImpl extends DownloadDataService {
	
	private static final Logger log = LoggerFactory.getLogger(DownloadObjectDicServiceImpl.class);
		
	private final ObjectService objectService;
	
	private final String[] HEADER_NAME = {"엔티티","엔트리","사용여부"};
	private String downloadFileName = "엔티티사전";
	private String templateFileName = "엔티티사전_템플릿";
	
	@Override
	public List<Map<String, Object>> getDataList(LoginModel login) {
		Map<String, Object> paramMap = new HashMap<String,Object>();
		paramMap.put("siteNo", login.getSiteNo());
		paramMap.put("lineNo", "100000");
		List<Map<String, Object>> dataList = null;
		try {
			dataList = objectService.selectObjDicListAll(paramMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return dataList;
	}

	@Override
	public void writeExcelFile(String type, List<Map<String, Object>> dataList, LoginModel login, HttpServletResponse response) throws Exception {
		
		String fileName = "";
		if(type.equals(DOWNLOAD_DATA_TASK)) {
			fileName = new String((downloadFileName).getBytes("euc-kr"), "8859_1") + "_" + DateUtil.getCurrentDateTimeMille() + EXCEL_EXTENSION;
		} else if(type.equals(DOWNLOAD_TEMPLATE_TASK)) {
			fileName = new String((templateFileName).getBytes("euc-kr"), "8859_1") + EXCEL_EXTENSION;
		}
		
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
		for(int idx=0; idx<HEADER_NAME.length; idx++) {
			XSSFCell cell = headerRow.createCell(idx);
			cell.setCellStyle(styleHeader);
			cell.setCellValue(HEADER_NAME[idx]);
		}
		
		// 데이터 쓰기
		XSSFRow dataRow = null;
		XSSFCell dataCell = null;
		if(dataList != null && dataList.size() > 0 && type.equals(DOWNLOAD_DATA_TASK)) {
			
			for(Map<String, Object> dataMap : dataList){
				dataRow = sheet.createRow(rowNum++);
				
				// 엔티티
				dataCell = dataRow.createCell(0);
				dataCell.setCellStyle(styleLeftStr);
				String entity = String.valueOf(dataMap.get("entity")).replace("@", "");
				dataCell.setCellValue(entity);
				
				// 엔트리
				dataCell = dataRow.createCell(1);
				dataCell.setCellStyle(styleLeftStr);
				if(dataMap.get("entry") != null && !dataMap.get("entry").equals("")) {
					String _entry = String.valueOf(dataMap.get("entry")).replaceAll(",", "|");
					dataCell.setCellValue(_entry);					
				} else {
					dataCell.setCellValue("");	
				}
				
				// 사용여부
				dataCell = dataRow.createCell(2);
				dataCell.setCellStyle(styleCenterStr);
				dataCell.setCellValue(String.valueOf(dataMap.get("useYn")));
			}
		}
		
		for(int idx=0; idx<HEADER_NAME.length; idx++) {
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
	}
}
