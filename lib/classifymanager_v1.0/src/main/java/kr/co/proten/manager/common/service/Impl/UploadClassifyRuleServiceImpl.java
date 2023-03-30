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
import java.util.stream.Collectors;

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
import kr.co.proten.manager.classify.service.ClassifyRuleService;
import kr.co.proten.manager.common.service.UploadDataService;
import kr.co.proten.manager.common.util.DateUtil;
import kr.co.proten.manager.common.util.StringUtil;
import kr.co.proten.manager.common.vo.UploadStepResultVo;
import kr.co.proten.manager.dic.service.ObjectService;
import kr.co.proten.manager.login.model.LoginModel;
import lombok.RequiredArgsConstructor;


@Service("uploadClassifyRuleService")
@RequiredArgsConstructor
public class UploadClassifyRuleServiceImpl extends UploadDataService {
	
	private static final Logger log = LoggerFactory.getLogger(UploadClassifyRuleServiceImpl.class);
	
	private final ClassifyCategoryService classifyCategoryService;
	
	private final ClassifyRuleService classifyRuleService;
	
	private final ObjectService objectService;
	
	private final String[] HEADER_NAME = {"대분류","중분류","소분류","분류룰"};
	private final String[] FEEDBACK_HEADER_NAME = {"피드백","대분류","중분류","소분류","분류룰"};
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
		
		Set<String> dupSet = new HashSet<String>();
		Map<String, Map<String, Object>> refCategoryMap = new HashMap<String, Map<String, Object>>();
		Map<String, Map<String, Object>> refClassifyRuleMap = new HashMap<String, Map<String, Object>>();
		Set<String> refEntitySet = new HashSet<String>();
		
		Map<String, Object> paramMap = new HashMap<String,Object>();
		paramMap.put("siteNo", login.getSiteNo());
		List<Map<String, Object>> entityList = null;
		try {
			entityList = objectService.selectObjDicListAll(paramMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		// 분류 룰에서 참조되는 엔티티
		String entity = "";
		for(Map<String, Object> _dataMap : entityList){
			entity = StringUtil.nvl(_dataMap.get("entity"));
			if(!entity.equals("")) {
				refEntitySet.add(entity);				
			}
		}
		
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
		
		List<Map<String, Object>> classifyRuleList = null;
		try {
			classifyRuleList = classifyRuleService.selectRuleListAll(paramMap);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		for(Map<String, Object> _classifyRuleMap : classifyRuleList) {
			String rule = String.valueOf(_classifyRuleMap.get("rule"));
			String _rule = convertStandardRuleFromSystem(rule);
			
			String categoryNo = String.valueOf(_classifyRuleMap.get("categoryNo"));
			for(Map<String, Object> categoryMap : categoryList) {
				String _categoryNo = String.valueOf(categoryMap.get("categoryNo"));
				if(categoryNo.equals(_categoryNo)) {
					_classifyRuleMap.put("fullItem", categoryMap.get("fullItem"));
				}
			}
			
			refClassifyRuleMap.put(_rule.replaceAll(" ", ""), _classifyRuleMap);
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
				
				// HEADER_NAME : 룰
				cell = row.getCell(3);
				String rule = getCellData(cell).trim();
				if(checkNotEmptyString(rule)) {					
					// entity 확인
					if(isExistEntity(refEntitySet, rule)) {
						// 룰 중복 체크
						String convertedRule = convertStandardRuleFromUser(rule);
						if(refClassifyRuleMap.containsKey(convertedRule.replaceAll(" ", ""))) {
							String categoryName = String.valueOf(refClassifyRuleMap.get(convertedRule.replaceAll(" ", "")).get("fullItem"));
							// invalid
							invalidDataComment.append("[분류룰] ").append(categoryName).append(" 카테고리에 존재하는 분류 룰과 중복 검출 되었습니다.");
							invalidDataComment.append(NEW_LINE);	
						} else {
							dataMap.put("rule", convertedRule);
							dataMap.put("patternCount", convertedRule.split(" ").length);
						}
					} else {
						// invalid
						invalidDataComment.append("[분류룰] 참조되는 엔티티가 존재하지 않습니다.");
						invalidDataComment.append(NEW_LINE);	
					}
				} else {
					// invalid
					invalidDataComment.append("[분류룰] 분류룰은 필수 값입니다.");
					invalidDataComment.append(NEW_LINE);
				}
				
				// 룰 중복 체크
				String keyStr = "";
				if(invalidDataComment.length() == 0) {
					keyStr = convertStandardRuleFromUser(rule);
					if(dupSet.contains(keyStr)) {
						// invalid
						invalidDataComment.append("[중복] 분류룰은 중복값을 허용하지 않습니다.");
						invalidDataComment.append(NEW_LINE);
					} else {
						dupSet.add(keyStr);
					}
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
					dataMap.put("4", rule);
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
			int key = classifyRuleService.selectMaxId();
			for(Map<String,Object> dataMap : dataList) {
				dataMap.put("_id", key);
				dataMap.put("ruleNo", key);
				insertDataList.add(dataMap);
				key++;
			}
			if(insertDataList.size() > 0) {
				classifyRuleService.insertRuleBulk(insertDataList);				
			}
			
			stepResultVo.setStatus("success");
			stepResultVo.setImportCount(dataList.size());
		} catch(Exception e) {
			stepResultVo.setStatus("fail");
			stepResultVo.setErrorMessage(e.getMessage());
		}
		
		return stepResultVo;
	}
	
	/**
	 * 엔티티 존재 여부 확인
	 * @param refEntitySet
	 * @param rule
	 * @return
	 */
	private boolean isExistEntity(Set<String> refEntitySet, String rule) {
		boolean result = true;
		String _rule = rule.replaceAll("&", ",");
		List<String> list = Arrays.asList(_rule.split(",")).stream().map(s -> s.trim()).filter(s -> !s.equals("")).collect(Collectors.toList());
		
		for(String entity : list) {
			if(entity.indexOf('@') != 0) {
				entity = "@" + entity;
			}
			if(!refEntitySet.contains(entity)) {
				result = false;
			}
		}
		return result;
	}
	
	/**
	 * 
	 * input : @지역권역명,@지하철역 @지하철역,@지역권역명 @가지하철역,@지역권역명
	 * output : @가지하철역,@지역권역명 @지역권역명,@지하철역 @지하철역,@지역권역명
	 * @param rule
	 * @return
	 */
	private String convertStandardRuleFromSystem(String rule) {
		String result = "";
		List<String> list = Arrays.asList(rule.split(" ")).stream().sorted().collect(Collectors.toList());
		result = String.join(" ",list);
		return result;
	}
	
	/**
	 * input : 지역권역명,지하철역 & 지하철역,지역권역명 & 가지하철역,지역권역명
	 * output : @가지하철역,@지역권역명 @지역권역명,@지하철역 @지역권역명,@지하철역
	 * @param rule
	 * @return
	 */
	private String convertStandardRuleFromUser(String rule) {
		String result = "";
		List<String> patternList = Arrays.asList(rule.split("&")).stream().map(s -> {
			String pattern = s.trim();
			List<String> entityList = Arrays.asList(pattern.split(",")).stream().map(p -> {
				if(p.indexOf('@') != 0) {
					p = "@" + p.trim();
				}
				return p;
			}).collect(Collectors.toList());
			return String.join(",",entityList);
		}).sorted().collect(Collectors.toList());
		result = String.join(" ",patternList);
		return result;
	}
}
