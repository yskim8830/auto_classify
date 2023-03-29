package kr.co.proten.manager.common.service.Impl;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import kr.co.proten.manager.common.elastic.ElasticSearchConstant;
import kr.co.proten.manager.common.elastic.ElasticSearchIndex;
import kr.co.proten.manager.common.repository.ElasticSearchDeleteRepository;
import kr.co.proten.manager.common.repository.ElasticSearchSelectRepository;
import kr.co.proten.manager.common.service.DownloadDataService;
import kr.co.proten.manager.common.service.FeedbackService;
import lombok.RequiredArgsConstructor;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl extends DownloadDataService implements FeedbackService {
	
	private static final Logger log = LoggerFactory.getLogger(FeedbackServiceImpl.class);
	
	private final ElasticSearchSelectRepository elasticSearchSelectRepository;
	
	private final ElasticSearchDeleteRepository elasticSearchDeleteRepository;

	@Override
	public List<Map<String, Object>> selectFeedbackData(String taskId) throws Exception {
		List<Map<String, Object>> resultList = null;
		int from = 0;
		int rownum = 100000;
		String sortType = "id";
		String sortOrder = ElasticSearchConstant.ASC;
		
		StringBuilder queryString = new StringBuilder();
		queryString.append("taskId:"+taskId);
		resultList = elasticSearchSelectRepository.selectDataListByQueryString(ElasticSearchIndex.INDEX_NAME_UPLOAD_INVALID_DATA, from, rownum, sortType, sortOrder, queryString.toString());
		return resultList;
	}

	@Override
	public boolean deleteFeedbackData(String taskId) throws Exception {
		boolean result = true;
		StringBuilder queryString = new StringBuilder();
		queryString.append("taskId:"+taskId);
		result = elasticSearchDeleteRepository.deleteDataByQuery(ElasticSearchIndex.INDEX_NAME_UPLOAD_INVALID_DATA, queryString.toString());
		return result;
	}

	@Override
	public void writeFeedbackReport(String[] header, List<Map<String,Object>> dataList, HttpServletResponse response) throws Exception {
		String fileName = new String(("피드백").getBytes("euc-kr"), "8859_1") + EXCEL_EXTENSION;
		
		// 파일 생성
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("feedback data");
		int rowNum = 0;
		
		// 셀 스타일
		XSSFCellStyle styleTitle = getTitleStyle(workbook);
		XSSFCellStyle styleHeader = getHeaderStyle(workbook);
		XSSFCellStyle styleCenterStr = getStrCenterStyle(workbook);
		XSSFCellStyle styleLeftStr = getStrLeftStyle(workbook);
		XSSFCellStyle styleNum = getNumberStyle(workbook);
		
		// 헤더 생성
		XSSFRow headerRow = sheet.createRow(rowNum++);
		for(int idx=0; idx<header.length; idx++) {
			XSSFCell cell = headerRow.createCell(idx);
			cell.setCellStyle(styleHeader);
			cell.setCellValue(header[idx]);
		}
		
		// 데이터 쓰기
		XSSFRow dataRow = null;
		XSSFCell dataCell = null;
		if(dataList != null && dataList.size() > 0) {
			for(Map<String, Object> dataMap : dataList){
				dataRow = sheet.createRow(rowNum++);
				
				for(int idx=0; idx<header.length; idx++) {
					dataCell = dataRow.createCell(idx);
					dataCell.setCellStyle(styleLeftStr);
					String value="";
					if(dataMap.get(String.valueOf(idx)) != null) {
						value = String.valueOf(dataMap.get(String.valueOf(idx)));
					}
					dataCell.setCellValue(value);	
				} 
			}
		}
		
		for(int idx=0; idx<header.length; idx++) {
			sheet.autoSizeColumn(idx);	
			if(idx == 0) {
				sheet.setColumnWidth(idx, 20000);
			} else {
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
