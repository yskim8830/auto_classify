package kr.co.proten.manager.common.service.Impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kr.co.proten.manager.common.service.UploadDataService;
import kr.co.proten.manager.common.util.DateUtil;
import kr.co.proten.manager.common.util.StringUtil;
import kr.co.proten.manager.common.vo.UploadStepResultVo;
import kr.co.proten.manager.dic.service.ObjectService;
import kr.co.proten.manager.login.model.LoginModel;
import lombok.RequiredArgsConstructor;

@Service("uploadObjectDicService")
@RequiredArgsConstructor
public class UploadObjectDicServiceImpl extends UploadDataService {
	
	private static final Logger log = LoggerFactory.getLogger(UploadObjectDicServiceImpl.class);
		
	private final ObjectService objectService;
	
	private String[] HEADER_NAME = {"엔티티","엔트리","사용여부"};
	private String[] FEEDBACK_HEADER_NAME = {"피드백","엔티티","엔트리","사용여부"};
	
	@Override
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
		
		Map<String, Map<String, Object>> refMap = new HashMap<String, Map<String, Object>>();
		Map<String, Object> paramMap = new HashMap<String,Object>();
		paramMap.put("siteNo", login.getSiteNo());
		paramMap.put("useYn", "y");
		List<Map<String, Object>> dataList = objectService.selectObjDicListAll(paramMap);
		for(Map<String, Object> _dataMap : dataList){
			String entity = String.valueOf(_dataMap.get("entity"));
			String key = entity.replaceAll(" ", "").toLowerCase();
			refMap.put(key, _dataMap);
		}
		
		List<Map<String,Object>> validDataList = new ArrayList<>();
		List<Map<String,Object>> inValidDataList = new ArrayList<>();
		
		Sheet sheet = workbook.getSheetAt(0);
		int rows = sheet.getLastRowNum();
		Map<String,Object> dataMap = null;
		StringBuffer invalidDataComment = new StringBuffer();
		
		Set<String> dupSet = new HashSet<String>();
		for (int rowIndex = (sheet.getFirstRowNum() + 1); rowIndex <= rows; rowIndex++) {
			Row row = sheet.getRow(rowIndex);
			if (row != null) {
				invalidDataComment.setLength(0);
				
				dataMap = new HashMap<String, Object>();
				
				// HEADER_NAME : 엔티티
				Cell cell = row.getCell(0);
				String entity = getCellData(cell);
				if(checkNotEmptyString(entity)) {
					// valid
					entity = entity.replace("@", "");
					if(entityValidation(entity)) {
						entity = "@" + entity;
						dataMap.put("entity", entity);						
					} else {
						// invalid
						invalidDataComment.append("[엔티티] 엔티티명은 '한글',영문','숫자','_','-' 를 포함하여 2~12자까지 입력해주세요. 또한 특수문자/공백은 제외해주세요.");
						invalidDataComment.append(NEW_LINE);						
					}
				} else {
					// invalid
					invalidDataComment.append("[엔티티] 엔티티는 필수 값입니다.");
					invalidDataComment.append(NEW_LINE);
				}
				
				// HEADER_NAME : 엔트리
				cell = row.getCell(1);
				String entry = getCellData(cell);
				if(checkNotEmptyString(entry)) {
					// valid
					String _entry = entry.replaceAll("\\|", "");
					if(entryValidation(_entry)) {
						_entry = entry.replaceAll("\\|", ",");
						dataMap.put("entry", _entry);						
					} else {
						// invalid
						invalidDataComment.append("[엔트리] 엔티티명은 '한글',영문','숫자' 를 포함하여 2~50자까지 입력해주세요. 또한 특수문자/공백은 제외해주세요.");
						invalidDataComment.append(NEW_LINE);		
					}
				} else {
					dataMap.put("entry", "");
				}
				
				// 중복 체크
				if(invalidDataComment.length() == 0) {
					String keyStr = entity.toLowerCase();
					if(dupSet.contains(keyStr)) {
						// invalid
						invalidDataComment.append("[중복] 엔티티명은 중복값을 허용하지 않습니다.");
						invalidDataComment.append(NEW_LINE);
					} else {
						dupSet.add(keyStr);
					}
				}
					
				// HEADER_NAME : 사용여부
				cell = row.getCell(2);
				String useYn = getCellData(cell);
				if(checkNotEmptyString(useYn)) {
					// valid
					if(useYn.toLowerCase().equals("y") || useYn.toLowerCase().equals("n")) {
						dataMap.put("useYn", useYn.toLowerCase());						
					} else {
						// invalid
						invalidDataComment.append("[사용여부] 사용유무는 'y'(사용) 또는 'n'(미사용)을 입력해 주세요.");
						invalidDataComment.append(NEW_LINE);
					}
				} else {
					// invalid
					invalidDataComment.append("[사용여부] 사용유무는 'y'(사용) 또는 'n'(미사용)을 입력해 주세요.");
					invalidDataComment.append(NEW_LINE);
				}
				
				
				if(invalidDataComment.length() == 0) {
					// 데이터 존재 유무 체크
					if(refMap.containsKey(entity.replaceAll(" ", "").toLowerCase())) {
						dataMap.put("_action", "update");
					} else {
						dataMap.put("_action", "index");
					}
					String entityUnq = StringUtil.removeTrimChar(entity);
					dataMap.put("_id", entity);
					dataMap.put("entityNo", entity);
					dataMap.put("entityUnq",entityUnq);
					
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
					dataMap.put("1", entity);
					dataMap.put("2", entry);
					dataMap.put("3", useYn);
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
		List<Map<String,Object>> updateDataList = new ArrayList<>();
		
		try {
			String action = "";
			for(Map<String,Object> dataMap : dataList) {
				action = String.valueOf(dataMap.get("_action"));
				dataMap.remove("_action");
				if(action.equals("index")){
					insertDataList.add(dataMap);
				} else {
					updateDataList.add(dataMap);
				}
			}
			if(insertDataList.size() > 0) {
				objectService.insertObjDicBulk(insertDataList);				
			}
			if(updateDataList.size() > 0) {
				objectService.updateObjDicBulk(updateDataList);	
			}
			
			stepResultVo.setStatus("success");
			stepResultVo.setImportCount(dataList.size());
		} catch(Exception e) {
			stepResultVo.setStatus("fail");
			stepResultVo.setErrorMessage(e.getMessage());
		}
		
		return stepResultVo;
	}
	
	private boolean entityValidation(String entity) {
		boolean result = true;
		Pattern pattern = null;
		Matcher matcher = null;
		String regexp = "[A-Za-z가-힣0-9_-]{1,12}";
		
		// check regexp
		pattern = Pattern.compile(regexp);
		matcher = pattern.matcher(entity);
		String matchedString = "";
		if(matcher.find()) {
			matchedString = matcher.group();
			if(!matchedString.equals(entity)) {
				result = false;				
			}
		} else {
			result = false;	
		}
		
		return result;
	}
	
	private boolean entryValidation(String entry) {
		boolean result = true;
		Pattern pattern = null;
		Matcher matcher = null;
		String regexp = "[^A-Za-z가-힣0-9]";
		
		if(entry.length() > 50) {
			result = false;
			return result;
		}
		
		// check regexp
		pattern = Pattern.compile(regexp);
		matcher = pattern.matcher(entry);
		if(matcher.find()) {
			result = false;
		}
		
		return result;
	}
}
