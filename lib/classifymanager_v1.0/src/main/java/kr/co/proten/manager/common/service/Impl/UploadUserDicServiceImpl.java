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
import kr.co.proten.manager.dic.service.DicService;
import kr.co.proten.manager.login.model.LoginModel;
import lombok.RequiredArgsConstructor;

@Service("uploadUserDicService")
@RequiredArgsConstructor
public class UploadUserDicServiceImpl extends UploadDataService {
	
	private static final Logger log = LoggerFactory.getLogger(UploadUserDicServiceImpl.class);
		
	private final DicService dicService;
	
	private String[] HEADER_NAME = {"단어","분석정보","동의어","금칙어"};
	private String[] FEEDBACK_HEADER_NAME = {"피드백","단어","분석정보","동의어","금칙어"};
	
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
		Map<String, String> synonymMap = new HashMap<String, String>();
		Map<String, Object> paramMap = new HashMap<String,Object>();
		paramMap.put("siteNo", login.getSiteNo());
		paramMap.put("lineNo", "10000");
		List<Map<String, Object>> dataList = dicService.selectDicListAll(paramMap);
		for(Map<String, Object> _dataMap : dataList){
			String word = String.valueOf(_dataMap.get("word"));
			String key = word.replaceAll(" ", "").toLowerCase();
			refMap.put(key, _dataMap);
			
			if(_dataMap.get("synonyms") != null && !_dataMap.get("synonyms").equals("")) {
				String synonyms = String.valueOf(_dataMap.get("synonyms"));
				for(String _syn : synonyms.split(",")) {
					synonymMap.put(_syn, key);
				}
			}	
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
				
				// HEADER_NAME : 단어
				Cell cell = row.getCell(0);
				String word = getCellData(cell);
				if(checkNotEmptyString(word)) {
					word = word.trim();
					// valid
					if(wordValidation(word)) {
						dataMap.put("word", word);
					} else {
						// invalid
						invalidDataComment.append("[단어] 단어는 '한글',영문','숫자','_','-' 를 포함하여 2~12자까지 입력해주세요. 또한 특수문자/공백은 제외해주세요.");
						invalidDataComment.append(NEW_LINE);	
					}
				} else {
					// invalid
					invalidDataComment.append("[단어] 단어는 필수 값입니다.");
					invalidDataComment.append(NEW_LINE);
				}
				
				// HEADER_NAME : 분석정보
				cell = row.getCell(1);
				String wordSep = getCellData(cell);
				if(checkNotEmptyString(wordSep)) {
					// valid
					wordSep = wordSep.trim();
					int wordLength = word.length();
					if(wordSeperatorValidation(wordLength, wordSep)) {
						dataMap.put("wordSep", wordSep);						
					} else {
						// invalid
						invalidDataComment.append("[분석정보] 분석정보의 숫자가 맞지 않습니다.");
						invalidDataComment.append(NEW_LINE);
					}
				} else {
					// invalid
					invalidDataComment.append("[분석정보] 분석정보는 필수 값입니다.");
					invalidDataComment.append(NEW_LINE);
				}
				
				// 중복 체크
				String keyStr = "";
				if(invalidDataComment.length() == 0) {
					keyStr = word.replaceAll(" ", "").toLowerCase();
					if(dupSet.contains(keyStr)) {
						// invalid
						invalidDataComment.append("[중복] 단어는 중복값을 허용하지 않습니다.");
						invalidDataComment.append(NEW_LINE);
					} else {
						dupSet.add(keyStr);
					}
				}
					
				// HEADER_NAME : 동의어
				cell = row.getCell(2);
				String synonyms = getCellData(cell);
				if(checkNotEmptyString(synonyms) && invalidDataComment.length() == 0) {
					// valid
					String _synonyms = synonyms.replaceAll("\\|", "");
					if(synonymsValidation(_synonyms)) {
						boolean duplicate = false;
						String dupSyn = "";
						for(String syn : synonyms.split("\\|")) {
							
							if(synonymMap.containsKey(syn)) {
								//keyStr
								String _key = synonymMap.get(syn);
								if(!_key.equals(keyStr)) {
									dupSyn = syn;
									duplicate = true; //중복이 있음
									break;
								} else {
									synonymMap.put(syn, keyStr);
								}
							} else {
								synonymMap.put(syn, keyStr);
							}
						}
						if(!duplicate) {
							_synonyms = synonyms.replaceAll("\\|", ",");
							dataMap.put("synonyms", _synonyms);	
						} else {
							invalidDataComment.append("[동의어] 동의어 [").append(dupSyn).append("] 중복값이 존재 합니다.");
							invalidDataComment.append(NEW_LINE);
						}
					} else {
						// invalid
						invalidDataComment.append("[동의어] 동의어는 '한글',영문','숫자' 를 포함하여 2~50자까지 입력해주세요. 또한 특수문자/공백은 제외해주세요.");
						invalidDataComment.append(NEW_LINE);		
					}
				} else {
					dataMap.put("synonyms", "");
				}
				
				// HEADER_NAME : 금칙어
				cell = row.getCell(3);
				String nosearchYn = getCellData(cell);
				if(checkNotEmptyString(nosearchYn)) {
					// valid
					if(nosearchYn.toLowerCase().equals("y") || nosearchYn.toLowerCase().equals("n")) {
						dataMap.put("nosearchYn", nosearchYn.toLowerCase());						
					} else {
						// invalid
						invalidDataComment.append("[금칙어] 금칙어는 'y'(제외) 또는 'n'(미제외)을 입력해 주세요.");
						invalidDataComment.append(NEW_LINE);
					}
				} else {
					// invalid
					invalidDataComment.append("[금칙어] 금칙어는 필수 값입니다. 'y'(제외) 또는 'n'(미제외)을 입력해 주세요.");
					invalidDataComment.append(NEW_LINE);
				}
				
				if(invalidDataComment.length() == 0) {
					if(refMap.containsKey(word.replaceAll(" ", "").toLowerCase())) {
						dataMap.put("_action", "update");
					} else {
						dataMap.put("_action", "index");
					}
					String wordUnq = StringUtil.removeTrimChar(word);
					dataMap.put("_id", word);
					dataMap.put("dicNo", word);
					dataMap.put("wordUnq",wordUnq);	
					
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
					dataMap.put("1", word);
					dataMap.put("2", wordSep);
					dataMap.put("3", synonyms);
					dataMap.put("4", nosearchYn);
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
				dicService.insertDicBulk(insertDataList);				
			}
			if(updateDataList.size() > 0) {
				dicService.updateDicBulk(updateDataList);	
			}
			
			stepResultVo.setStatus("success");
			stepResultVo.setImportCount(dataList.size());
		} catch(Exception e) {
			stepResultVo.setStatus("fail");
			stepResultVo.setErrorMessage(e.getMessage());
		}
		
		return stepResultVo;
	}
	
	private boolean wordValidation(String word) {
		boolean result = true;
		Pattern pattern = null;
		Matcher matcher = null;
		String regexp = "[A-Za-z가-힣0-9_-]{1,12}";
		
		// check regexp
		pattern = Pattern.compile(regexp);
		matcher = pattern.matcher(word);
		String matchedString = "";
		if(matcher.find()) {
			matchedString = matcher.group();
			if(!matchedString.equals(word)) {
				result = false;				
			}
		} else {
			result = false;	
		}
		
		return result;
	}
	
	private boolean wordSeperatorValidation(int wordLength, String wordSeperator) {
		boolean result = true;
		
		if(wordSeperator.equals("0")) {
			return result;
		}
		
		char charValue = ' ';
		int intValue = 0;
		int length = 0;
		for(int i=0; i<wordSeperator.length(); i++) {
			charValue = wordSeperator.charAt(i);
			intValue = Character.getNumericValue(charValue);
			length = length + intValue;
		}
		
		if(length != wordLength) {
			result = false;
		}
		
		return result;
	}
	
	private boolean synonymsValidation(String entry) {
		boolean result = true;
		Pattern pattern = null;
		Matcher matcher = null;
		String regexp = "[^A-Za-z가-힣0-9_-]";
		
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
