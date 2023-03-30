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

import kr.co.proten.manager.classify.service.ClassifyCategoryService;
import kr.co.proten.manager.classify.service.ClassifyRuleService;
import kr.co.proten.manager.common.service.DownloadDataService;
import kr.co.proten.manager.common.util.DateUtil;
import kr.co.proten.manager.login.model.LoginModel;
import lombok.RequiredArgsConstructor;

@Service("downloadClassifyRuleService")
@RequiredArgsConstructor
public class DownloadClassifyRuleServiceImpl extends DownloadDataService {
	
	private static final Logger log = LoggerFactory.getLogger(DownloadClassifyRuleServiceImpl.class);
	
	private final ClassifyCategoryService classifyCategoryService;
	
	private final ClassifyRuleService classifyRuleService;
	
	private final String[] HEADER_NAME = {"대분류","중분류","소분류","분류룰"};
	private String downloadFileName = "분류룰";
	private String templateFileName = "분류룰_템플릿";
	private String DEPTH_SEPERATOR = ">";
	
	@Override
	public List<Map<String, Object>> getDataList(LoginModel login) {
		
		Map<String, Object> paramMap = new HashMap<String,Object>();
		paramMap.put("siteNo", login.getSiteNo());
		List<Map<String, Object>> categoryList = null;
		try {
			categoryList = classifyCategoryService.selectCategoryList(paramMap, false);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		List<Map<String, Object>> dataList = null;
		try {
			dataList = classifyRuleService.selectRuleListAll(paramMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		for(Map<String, Object> dataMap : dataList) {
			String categoryNo = String.valueOf(dataMap.get("categoryNo"));
			for(Map<String, Object> categoryMap : categoryList) {
				String _categoryNo = String.valueOf(categoryMap.get("categoryNo"));
				if(categoryNo.equals(_categoryNo)) {
					dataMap.put("fullItem", categoryMap.get("fullItem"));
				}
			}
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
				
				// 카테고리 대분류
				String fullItem = String.valueOf(dataMap.get("fullItem"));
				String categoryName[] = fullItem.split(DEPTH_SEPERATOR);
				
				String cate1Depth = "";
				String cate2Depth = "";
				String cate3Depth = "";
				if(categoryName.length == 2) {
					cate1Depth = categoryName[1];
				} else if(categoryName.length == 3) {
					cate1Depth = categoryName[1];
					cate2Depth = categoryName[2];
				} else if(categoryName.length == 4) {
					cate1Depth = categoryName[1];
					cate2Depth = categoryName[2];
					cate3Depth = categoryName[3];
				} 
				
				// 대분류
				dataCell = dataRow.createCell(0);
				dataCell.setCellStyle(styleCenterStr);
				dataCell.setCellValue(cate1Depth);
				
				// 중분류
				dataCell = dataRow.createCell(1);
				dataCell.setCellStyle(styleCenterStr);
				dataCell.setCellValue(cate2Depth);
				
				// 소분류
				dataCell = dataRow.createCell(2);
				dataCell.setCellStyle(styleCenterStr);
				dataCell.setCellValue(cate3Depth);
								
				// 분류 룰
				dataCell = dataRow.createCell(3);
				dataCell.setCellStyle(styleLeftStr);
				String rule = String.valueOf(dataMap.get("rule")).replaceAll("@", "").replaceAll(" ", " & ");
				dataCell.setCellValue(rule);
				
			}
		}
		
		for(int idx=0; idx<HEADER_NAME.length; idx++) {
			sheet.autoSizeColumn(idx);	
			sheet.setColumnWidth(idx, Math.min(128 * 128, sheet.getColumnWidth(idx) + 1200));
			//sheet.setColumnWidth(idx, (sheet.getColumnWidth(idx))+2500);
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