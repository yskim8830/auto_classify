package kr.co.proten.manager.common.service.Impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import kr.co.proten.manager.common.service.UploadDataService;
import kr.co.proten.manager.common.util.DateUtil;
import kr.co.proten.manager.common.vo.UploadStepResultVo;
import kr.co.proten.manager.login.model.LoginModel;
import lombok.RequiredArgsConstructor;


@Service("uploadCategoryService")
@RequiredArgsConstructor
public class UploadCategoryServiceImpl extends UploadDataService {
	
	private static final Logger log = LoggerFactory.getLogger(UploadCategoryServiceImpl.class);
	
	private final ClassifyCategoryService classifyCategoryService;
	
	private final String[] HEADER_NAME = {"대분류","중분류","소분류","설명"};
	private final String[] FEEDBACK_HEADER_NAME = {"피드백","대분류","중분류","소분류","설명"};
	private String DEPTH_SEPERATOR = ">";
	
	public String[] getFeedbackHeader() {
		String[] _headerName = Arrays.copyOf(FEEDBACK_HEADER_NAME, FEEDBACK_HEADER_NAME.length);
		return _headerName;
	}
	
	@Override
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
		Map<String,Object> virtualDataMap = null;
		StringBuffer invalidDataComment = new StringBuffer();
		
		Map<String, Object> paramMap = new HashMap<String,Object>();
		paramMap.put("siteNo", login.getSiteNo());
		List<Map<String, Object>> dataList = classifyCategoryService.selectCategoryList(paramMap, false);
		Map<String, Map<String, Object>> refMap = new HashMap<String, Map<String, Object>>();
		Map<String, Map<String, Object>> virtualMap = new HashMap<String, Map<String, Object>>();
		Set<String> dupSet = new HashSet<String>();
		for(Map<String, Object> _dataMap : dataList){
			String fullItem = String.valueOf(_dataMap.get("fullItem"));
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
			refMap.put(key, _dataMap);
		}
		
		StringBuffer fullItem = new StringBuffer();
		for (int rowIndex = (sheet.getFirstRowNum() + 1); rowIndex <= rows; rowIndex++) {
			Row row = sheet.getRow(rowIndex);
			if (row != null) {
				invalidDataComment.setLength(0);
				int depth = 0;
				String categoryNm = "";
				fullItem.setLength(0);
				dataMap = new HashMap<String, Object>();
								
				// HEADER_NAME : 대분류
				Cell cell = row.getCell(0);
				String cate1Depth = getCellData(cell).trim();
				String depth1Key = "";
				if(checkNotEmptyString(cate1Depth)) {
					depth = 1;
					categoryNm = cate1Depth;
					fullItem.append("전체").append(DEPTH_SEPERATOR).append(cate1Depth);
					depth1Key = cate1Depth + DEPTH_SEPERATOR + "" + DEPTH_SEPERATOR + "";
					depth1Key = depth1Key.replaceAll(" ", "").toLowerCase();
					if(!refMap.containsKey(depth1Key) && !virtualMap.containsKey(depth1Key)) {
						virtualDataMap = new HashMap<String, Object>();
						virtualDataMap.put("pCategoryNo", 0);
						virtualDataMap.put("categoryNm", categoryNm);
						virtualDataMap.put("item", cate1Depth);
						virtualDataMap.put("fullItem", fullItem.toString());
						virtualDataMap.put("depth", depth);
						virtualDataMap.put("useYn", "y");
						virtualDataMap.put("desc", "");
						
						virtualMap.put(depth1Key, virtualDataMap);
					}
				} else {
					// invalid
					invalidDataComment.append("[대분류] 대분류명은 필수 값입니다.");
					invalidDataComment.append(NEW_LINE);
				}
				
				// HEADER_NAME : 중분류
				cell = row.getCell(1);
				String cate2Depth = getCellData(cell).trim();
				String depth2Key = "";
				if(checkNotEmptyString(cate1Depth) && checkNotEmptyString(cate2Depth)) {
					depth = 2;
					categoryNm = cate2Depth;
					fullItem.append(DEPTH_SEPERATOR).append(cate2Depth);
					depth2Key = cate1Depth + DEPTH_SEPERATOR + cate2Depth + DEPTH_SEPERATOR + "";
					depth2Key = depth2Key.replaceAll(" ", "").toLowerCase();
					if(!refMap.containsKey(depth2Key) && !virtualMap.containsKey(depth2Key)) {
						virtualDataMap = new HashMap<String, Object>();
						if(refMap.containsKey(depth1Key)) {
							virtualDataMap.put("pCategoryNo", (int) refMap.get(depth1Key).get("categoryNo"));
						}
						virtualDataMap.put("categoryNm", categoryNm);
						virtualDataMap.put("item", cate1Depth);
						virtualDataMap.put("fullItem", fullItem.toString());
						virtualDataMap.put("depth", depth);
						virtualDataMap.put("useYn", "y");
						virtualDataMap.put("desc", "");
						
						virtualMap.put(depth2Key, virtualDataMap);
					}
				}
				
				// HEADER_NAME : 소분류
				cell = row.getCell(2);
				String cate3Depth = getCellData(cell).trim();
				if(checkNotEmptyString(cate1Depth) && checkNotEmptyString(cate2Depth) && checkNotEmptyString(cate3Depth)) {
					depth = 3;
					categoryNm = cate3Depth;
					fullItem.append(DEPTH_SEPERATOR).append(cate3Depth);
				} else if(!checkNotEmptyString(cate2Depth) && checkNotEmptyString(cate3Depth)) {
					invalidDataComment.append("[소분류] 중분류값이 없을 경우 작성된 소분류 카테고리는 적용되지 않습니다.");
					invalidDataComment.append(NEW_LINE);
				}
				
				// 중복 체크
				String key = "";
				if(invalidDataComment.length() == 0) {
					key = cate1Depth + DEPTH_SEPERATOR + cate2Depth + DEPTH_SEPERATOR + cate3Depth;
					key = key.replaceAll(" ", "").toLowerCase();
					if(dupSet.contains(key)) {
						// invalid
						invalidDataComment.append("[중복] 카테고리명은 중복값을 허용하지 않습니다.");
						invalidDataComment.append(NEW_LINE);
					} else {
						dupSet.add(key);
					}
				}
					
				// HEADER_NAME : 설명
				cell = row.getCell(3);
				String desc = getCellData(cell);
				if(checkNotEmptyString(desc)) {
					// valid
					dataMap.put("desc", desc);		
				} else {
					dataMap.put("desc", "");		
				}
				
				dataMap.put("item", cate1Depth);
				dataMap.put("categoryNm", categoryNm);
				dataMap.put("depth", depth);
				dataMap.put("fullItem", fullItem.toString());
				dataMap.put("useYn", "y");
				
				if(invalidDataComment.length() == 0) {
					// 데이터 존재 유무 체크
					if(refMap.containsKey(key)) {
						// update
						dataMap.put("_action", "update");
						dataMap.put("_id", (int) refMap.get(key).get("categoryNo"));
						dataMap.put("categoryNo", (int) refMap.get(key).get("categoryNo"));
						dataMap.put("pCategoryNo", (int) refMap.get(key).get("pCategoryNo"));
					} else {
						// insert
						if(depth == 1) {
							dataMap.put("pCategoryNo", 0);
						} else if(depth == 2) {
							if(refMap.containsKey(depth1Key)) {
								dataMap.put("pCategoryNo", (int) refMap.get(depth1Key).get("categoryNo"));
							}
						} else if(depth == 3) {
							if(refMap.containsKey(depth2Key)) {
								dataMap.put("pCategoryNo", (int) refMap.get(depth2Key).get("categoryNo"));
							}
						}
						dataMap.put("_action", "index");
					}
					
					dataMap.put("siteNo", login.getSiteNo());
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
					dataMap.put("4", desc);
					inValidDataList.add(dataMap);
				}
			}
		}
				
		Set<String> virtualKeySet = virtualMap.keySet();
		Iterator<String> it = virtualKeySet.iterator();
		String virtualKey = "";
		while(it.hasNext()) {
			virtualKey = it.next();
			if(!dupSet.contains(virtualKey)) {
				virtualDataMap = virtualMap.get(virtualKey);
				virtualDataMap.put("_action", "index");
				virtualDataMap.put("siteNo", login.getSiteNo());
				virtualDataMap.put("createUser", login.getUserId());
				virtualDataMap.put("modifyUser", login.getUserId());
				virtualDataMap.put("createUserNm", login.getUserNm());
				virtualDataMap.put("modifyUserNm", login.getUserNm());
				virtualDataMap.put("createDate", DateUtil.getCurrentDateTimeMille());
				virtualDataMap.put("modifyDate", DateUtil.getCurrentDateTimeMille());
				validDataList.add(virtualDataMap);
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
		
		List<Map<String,Object>> insertDataDepth1List = new ArrayList<>();
		List<Map<String,Object>> insertDataDepth2List = new ArrayList<>();
		List<Map<String,Object>> insertDataDepth3List = new ArrayList<>();
		List<Map<String,Object>> updateDataList = new ArrayList<>();
		Map<String, Integer> keyMap = new HashMap<String, Integer>();
		
		try {
			int depth = 0;
			for(Map<String,Object> dataMap : dataList) {
				if(String.valueOf(dataMap.get("_action")).equals("index")){
					depth = (int) dataMap.get("depth");
					if(depth == 1) {
						insertDataDepth1List.add(dataMap);
					} else if(depth == 2) {
						insertDataDepth2List.add(dataMap);
					} else if(depth == 3) {
						insertDataDepth3List.add(dataMap);
					}
				} else {
					updateDataList.add(dataMap);
				}
			}
			String curKey = "";
			String parentKey = "";
			int key = classifyCategoryService.selectMaxId();
			if(insertDataDepth1List.size() > 0) {
				for(Map<String,Object> dataMap : insertDataDepth1List) {
					String fullItem = String.valueOf(dataMap.get("fullItem"));
					String categoryName[] = fullItem.split(DEPTH_SEPERATOR);
					curKey = categoryName[1] + DEPTH_SEPERATOR + "" + DEPTH_SEPERATOR + "";
					curKey = curKey.replaceAll(" ", "").toLowerCase();
					
					dataMap.put("_id", key);
					dataMap.put("categoryNo", key);
					dataMap.put("pCategoryNo", 0);
					keyMap.put(curKey, key);
					key++;
				}		
				log.debug("[UploadCategoryService] [doImportData] depth1 data size is {}", insertDataDepth1List.size());
				classifyCategoryService.insertCategoryBulk(insertDataDepth1List);
			}
			
			if(insertDataDepth2List.size() > 0) {
				for(Map<String,Object> dataMap : insertDataDepth2List) {
					String fullItem = String.valueOf(dataMap.get("fullItem"));
					String categoryName[] = fullItem.split(DEPTH_SEPERATOR);
					parentKey = categoryName[1] + DEPTH_SEPERATOR + "" + DEPTH_SEPERATOR + "";
					parentKey = parentKey.replaceAll(" ", "").toLowerCase();
					curKey = categoryName[1] + DEPTH_SEPERATOR + categoryName[2] + DEPTH_SEPERATOR + "";
					curKey = curKey.replaceAll(" ", "").toLowerCase();
					
					dataMap.put("_id", key);
					dataMap.put("categoryNo", key);
					if(!dataMap.containsKey("pCategoryNo")) {
						dataMap.put("pCategoryNo", keyMap.get(parentKey));						
					}
					keyMap.put(curKey, key);	
					key++;
				}			
				log.debug("[UploadCategoryService] [doImportData] depth2 data size is {}", insertDataDepth2List.size());
				classifyCategoryService.insertCategoryBulk(insertDataDepth2List);
			}
			
			if(insertDataDepth3List.size() > 0) {
				for(Map<String,Object> dataMap : insertDataDepth3List) {
					String fullItem = String.valueOf(dataMap.get("fullItem"));
					String categoryName[] = fullItem.split(DEPTH_SEPERATOR);
					parentKey = categoryName[1] + DEPTH_SEPERATOR + categoryName[2] + DEPTH_SEPERATOR + "";
					parentKey = parentKey.replaceAll(" ", "").toLowerCase();
					
					dataMap.put("_id", key);
					dataMap.put("categoryNo", key);
					if(!dataMap.containsKey("pCategoryNo")) {
						dataMap.put("pCategoryNo", keyMap.get(parentKey));						
					}
					key++;
				}			
				log.debug("[UploadCategoryService] [doImportData] depth3 data size is {}", insertDataDepth3List.size());
				classifyCategoryService.insertCategoryBulk(insertDataDepth3List);
			}
			
			if(updateDataList.size() > 0) {
				classifyCategoryService.updateCategoryBulk(updateDataList);
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
