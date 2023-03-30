package kr.co.proten.manager.common.service.Impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kr.co.proten.manager.classify.service.ClassifyCategoryService;
import kr.co.proten.manager.classify.service.ClassifyDataService;
import kr.co.proten.manager.common.service.UploadDataService;
import kr.co.proten.manager.common.util.DateUtil;
import kr.co.proten.manager.common.vo.UploadStepResultVo;
import kr.co.proten.manager.login.model.LoginModel;
import lombok.RequiredArgsConstructor;


@Service("uploadClassifyDataService")
@RequiredArgsConstructor
public class UploadClassifyDataServiceImpl extends UploadDataService {
	
	private static final Logger log = LoggerFactory.getLogger(UploadClassifyDataServiceImpl.class);
	
	private final ClassifyCategoryService classifyCategoryService;
	
	private final ClassifyDataService classifyDataService;
	
	private final String[] HEADER_NAME = {"대분류","중분류","소분류","문서"};
	private final String[] FEEDBACK_HEADER_NAME = {"피드백","대분류","중분류","소분류","문서"};
	private String DEPTH_SEPERATOR = ">";
	
	public String[] getFeedbackHeader() {
		String[] _headerName = Arrays.copyOf(FEEDBACK_HEADER_NAME, FEEDBACK_HEADER_NAME.length);
		return _headerName;
	}
	
	public UploadStepResultVo doFileValidation(File file) throws Exception {
		return super.doFileValidation(HEADER_NAME, file);		
	}

	@Override
	public UploadStepResultVo doDataValidation(LoginModel login, File file) throws Exception {
		UploadStepResultVo stepResultVo = new UploadStepResultVo();
		
		Workbook workbook = null;
		String fileExt = "";
		FileInputStream fis = new FileInputStream(file);
		
		fileExt = file.getName().substring(file.getName().lastIndexOf('.') + 1);
		if(MSOFFICE_EXCEL_EXT_XLSX.equalsIgnoreCase(fileExt)) {
			workbook = new XSSFWorkbook(fis);
		}
		
		if(MSOFFICE_EXCEL_EXT_XLS.equalsIgnoreCase(fileExt)) {
			workbook = new HSSFWorkbook(fis);
		}
		
		List<Map<String,Object>> validDataList = new ArrayList<>();
		List<Map<String,Object>> inValidDataList = new ArrayList<>();
		
		Sheet sheet = workbook.getSheetAt(0);
		int rows = sheet.getLastRowNum();
		Map<String,Object> dataMap = null;
		StringBuffer invalidDataComment = new StringBuffer();
		
		Map<String, Map<String, Object>> refCategoryMap = new HashMap<String, Map<String, Object>>();
		
		Map<String, Object> paramMap = new HashMap<String,Object>();
		paramMap.put("siteNo", login.getSiteNo());		
		// 분류 카테고리 조회
		List<Map<String, Object>> categoryList = null;
		try {
			categoryList = classifyCategoryService.selectCategoryList(paramMap, false);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		for(Map<String, Object> _categoryMap : categoryList){
			String fullItem = String.valueOf(_categoryMap.get("fullItem"));
			String categoryName[] = fullItem.split(DEPTH_SEPERATOR);
			if(categoryName.length > 4) {
				continue;
			}
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
			String key = cate1Depth + DEPTH_SEPERATOR + cate2Depth + DEPTH_SEPERATOR + cate3Depth;
			key = key.replaceAll(" ", "").toLowerCase();
			refCategoryMap.put(key, _categoryMap);
		}
		
		for (int rowIndex = (sheet.getFirstRowNum() + 1); rowIndex <= rows; rowIndex++) {
			Row row = sheet.getRow(rowIndex);
			if (row != null) {
				invalidDataComment.setLength(0);
				int depth = 0;
				int categoryNo = 0;
				dataMap = new HashMap<String, Object>();
								
				// HEADER_NAME : 대분류
				Cell cell = row.getCell(0);
				String cate1Depth = getCellData(cell).trim();
				String depth1Key = "";
				if(checkNotEmptyString(cate1Depth)) {
					depth = 1;
					depth1Key = cate1Depth + DEPTH_SEPERATOR + "" + DEPTH_SEPERATOR + "";
					depth1Key = depth1Key.replaceAll(" ", "").toLowerCase();
				} 
				
				// HEADER_NAME : 중분류
				cell = row.getCell(1);
				String cate2Depth = getCellData(cell).trim();
				String depth2Key = "";
				if(checkNotEmptyString(cate1Depth) && checkNotEmptyString(cate2Depth)) {
					depth = 2;
					depth2Key = cate1Depth + DEPTH_SEPERATOR + cate2Depth + DEPTH_SEPERATOR + "";
					depth2Key = depth2Key.replaceAll(" ", "").toLowerCase();
				}
				
				// HEADER_NAME : 소분류
				cell = row.getCell(2);
				String cate3Depth = getCellData(cell).trim();
				String depth3Key = "";
				if(checkNotEmptyString(cate1Depth) && checkNotEmptyString(cate2Depth) && checkNotEmptyString(cate3Depth)) {
					depth = 3;
					depth3Key = cate1Depth + DEPTH_SEPERATOR + cate2Depth + DEPTH_SEPERATOR + cate3Depth;
					depth3Key = depth3Key.replaceAll(" ", "").toLowerCase();
				} 
				
				// 카테고리 참조 체크
				if(depth == 1) {
					if(refCategoryMap.containsKey(depth1Key)) {
						if(refCategoryMap.get(depth1Key).get("categoryNo") != null) {
							categoryNo = (int) refCategoryMap.get(depth1Key).get("categoryNo");
						}
					} else {
						invalidDataComment.append("[카테고리] 참조되는 카테고리명이 존재하지 않습니다.");
						invalidDataComment.append(NEW_LINE);
					}
				} else if(depth == 2) {
					if(refCategoryMap.containsKey(depth2Key)) {
						if(refCategoryMap.get(depth2Key).get("categoryNo") != null) {
							categoryNo = (int) refCategoryMap.get(depth2Key).get("categoryNo");
						}
					} else {
						invalidDataComment.append("[카테고리] 참조되는 카테고리명이 존재하지 않습니다.");
						invalidDataComment.append(NEW_LINE);
					}
				} else if(depth == 3) {
					if(refCategoryMap.containsKey(depth3Key)) {
						if(refCategoryMap.get(depth3Key).get("categoryNo") != null) {
							categoryNo = (int) refCategoryMap.get(depth3Key).get("categoryNo");
						}
					} else {
						invalidDataComment.append("[카테고리] 참조되는 카테고리명이 존재하지 않습니다.");
						invalidDataComment.append(NEW_LINE);
					}
				} else {
					// 카테고리를 입력하지 않았을 경우 0으로 세팅됨
				}
				
				// HEADER_NAME : 문서
				cell = row.getCell(3);
				String document = getCellData(cell).trim();
				if(checkNotEmptyString(document)) {	
					// XSS 필터 처리
					dataMap.put("document", document);
				} else {
					// invalid
					invalidDataComment.append("[문서] 문서 내용이 없습니다.");
					invalidDataComment.append(NEW_LINE);
				}
				
				if(invalidDataComment.length() == 0) {					
					dataMap.put("_action", "index");
					dataMap.put("siteNo", login.getSiteNo());
					dataMap.put("categoryNo", categoryNo);
					dataMap.put("createUser", login.getUserId());
					dataMap.put("modifyUser", login.getUserId());
					dataMap.put("createUserNm", login.getUserNm());
					dataMap.put("modifyUserNm", login.getUserNm());
					dataMap.put("createDate", DateUtil.getCurrentDateTimeMille());
					dataMap.put("modifyDate", DateUtil.getCurrentDateTimeMille());
					validDataList.add(dataMap);	
				} else {
					dataMap.put("0", invalidDataComment.toString());
					dataMap.put("1", cate1Depth);
					dataMap.put("2", cate2Depth);
					dataMap.put("3", cate3Depth);
					dataMap.put("4", document);
					inValidDataList.add(dataMap);
				}
			}
		}
		
		stepResultVo.setStatus("success");
		stepResultVo.setValidDataList(validDataList);
		stepResultVo.setInValidDataList(inValidDataList);
		
		return stepResultVo;
	}

	@Override
	public UploadStepResultVo doImportData(List<Map<String,Object>> dataList) {
		UploadStepResultVo stepResultVo = new UploadStepResultVo();
		
		List<Map<String,Object>> insertDataList = new ArrayList<>();
				
		try {
			int key = classifyDataService.selectMaxId();
			for(Map<String,Object> dataMap : dataList) {
				dataMap.put("_id", key);
				dataMap.put("dataNo", key);
				insertDataList.add(dataMap);
				key++;
			}
			if(insertDataList.size() > 0) {
				classifyDataService.insertDataBulk(insertDataList);				
			}
			
			stepResultVo.setStatus("success");
			stepResultVo.setImportCount(dataList.size());
		} catch(Exception e) {
			stepResultVo.setStatus("fail");
			stepResultVo.setErrorMessage(e.getMessage());
		}
		
		return stepResultVo;
	}
}
