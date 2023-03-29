package kr.co.proten.manager.common.service.Impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kr.co.proten.manager.common.elastic.ElasticSearchIndex;
import kr.co.proten.manager.common.repository.ElasticSearchInsertRepository;
import kr.co.proten.manager.common.service.BulkSimulationService;
import kr.co.proten.manager.common.util.DateUtil;
import kr.co.proten.manager.common.util.ObjectUtils;
import kr.co.proten.manager.common.vo.BulkSimulationResultRootVo;
import kr.co.proten.manager.common.vo.BulkSimulationResultSummaryVo;
import kr.co.proten.manager.common.vo.BulkSimulationResultVo;
import kr.co.proten.manager.learning.service.SimulationService;
import kr.co.proten.manager.learning.vo.ResponseClassifyRootVo;
import lombok.RequiredArgsConstructor;


@Service("bulkSimulationService")
@RequiredArgsConstructor
public class BulkSimulationServiceImpl extends BulkSimulationService {
	
	private static final Logger log = LoggerFactory.getLogger(BulkSimulationServiceImpl.class);
	
	private final ElasticSearchInsertRepository elasticSearchInsertRepository;
	
	private final SimulationService simulationService;
	
	private final String[] HEADER_NAME = {"문서"};
	
	public void doFileValidation(File file, BulkSimulationResultRootVo resultVo) throws Exception {
		super.doFileValidation(HEADER_NAME, file, resultVo);		
	}
	
	@Override
	public void readFile(File file, BulkSimulationResultRootVo resultVo) throws Exception {
		
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
		
		Sheet sheet = workbook.getSheetAt(0);
		int rows = sheet.getLastRowNum();
		
		BulkSimulationResultVo dataResultVo = null;
		
		// 헤더 제외
		resultVo.getSummaryVo().setDataCnt(sheet.getLastRowNum());
		
		for (int rowIndex = (sheet.getFirstRowNum() + 1); rowIndex <= rows; rowIndex++) {
			Row row = sheet.getRow(rowIndex);
			if (row != null) {
				dataResultVo = new BulkSimulationResultVo(); 
								
				// HEADER_NAME : 문서
				Cell cell = row.getCell(0);
				String document = getCellData(cell).trim();
				String id = resultVo.getSummaryVo().getTaskId() + "_" + rowIndex;
				dataResultVo.set_id(id);
				dataResultVo.setId(id);
				dataResultVo.setSiteNo(resultVo.getSummaryVo().getSiteNo());
				dataResultVo.setTaskId(resultVo.getSummaryVo().getTaskId());
				dataResultVo.setOrgSentence(document);
				resultVo.addDataQueue(dataResultVo);
			}
		}
		
		int dataCount = resultVo.getSummaryVo().getDataCnt();
		if(dataCount > 0) {
			resultVo.setStatus("success");			
		} else {
			throw new Exception("There is no Data!!");
		}
	}

	@Override
	@Async("proworker")
	public Future<Void> doSimulation(String siteCode, BulkSimulationResultRootVo resultVo) throws Exception {
		log.debug("[BulkSimulationServiceImpl] [requestSimulation] Thread Name : {}", Thread.currentThread().getName());
		GsonBuilder gsonBuilder = new GsonBuilder();  
		gsonBuilder.serializeNulls();  
		Gson gson = gsonBuilder.create();
		
		BulkSimulationResultVo dataResultVo = null;
		ResponseClassifyRootVo apiResultVo = null;
		String result = "";
		boolean failed = false;
		boolean matched = false; 
		boolean ruleMatched = false;
		boolean classifyMatched = false;
		List<String> categoryList = null;
		while(!resultVo.getDataQueue().isEmpty() && resultVo.isRunning()) {
			dataResultVo = resultVo.getDataQueue().poll();
			if(dataResultVo != null) {
				failed = false;
				matched = false; 
				ruleMatched = false;
				classifyMatched = false;
				try {
					result = simulationService.requestSimulationApi(siteCode, dataResultVo.getOrgSentence(), String.valueOf(resultVo.getSummaryVo().getVersion()));
					apiResultVo = gson.fromJson(result, ResponseClassifyRootVo.class);
					if(apiResultVo.getStatus().getCode().equals("200")) {
						if(apiResultVo.getAnalysisResult().getResultType().equals("matched")) {
							matched = true;
							if(apiResultVo.getAnalysisResult().getMatchedType().equals("rule")) {
								ruleMatched = true;
								categoryList = apiResultVo.getAnalysisResult().getRuleResult().stream().map(vo -> {
									String categoryName = vo.getFullItem();
									return categoryName;
								}).collect(Collectors.toList());
								dataResultVo.setMatchedCategory(String.join(" ", categoryList));
							} else if(apiResultVo.getAnalysisResult().getMatchedType().equals("classify")) {
								classifyMatched = true;
								categoryList = apiResultVo.getAnalysisResult().getClassifyResult().stream().map(vo -> {
									String categoryName = vo.getFullItem();
									return categoryName;
								}).collect(Collectors.toList());
								dataResultVo.setMatchedCategory(String.join(" ", categoryList));
							}
						}
						dataResultVo.setResultType(apiResultVo.getAnalysisResult().getResultType());
						dataResultVo.setTagSentence(apiResultVo.getAnalysisResult().getTagSentence());
						dataResultVo.setMatchedType(apiResultVo.getAnalysisResult().getMatchedType());
						dataResultVo.setRuleResult(gson.toJson(apiResultVo.getAnalysisResult().getRuleResult()));
						dataResultVo.setClassifyResult(gson.toJson(apiResultVo.getAnalysisResult().getClassifyResult()));
					} else {
						dataResultVo.setResultType(apiResultVo.getAnalysisResult().getResultType());
						dataResultVo.setFailedMessage(apiResultVo.getStatus().getMessage());
						failed = true;
					}
					resultVo.getSummaryVo().addResultCnt(failed, matched, ruleMatched, classifyMatched);
				} catch (Exception e) {
					dataResultVo.setResultType("error");
					dataResultVo.setFailedMessage("API 요청이 정상적으로 이루어지지 않았습니다. API 서버의 상태를 확인해 주세요.");
					log.error(e.getMessage());
				}
				resultVo.addResultData(dataResultVo);
			}
		}
		log.debug("[BulkSimulationServiceImpl] [requestSimulation] {} finished!!", Thread.currentThread().getName());	
		return new AsyncResult<>(null);
	}
	
	@Override
	public void doImportData(BulkSimulationResultRootVo resultVo) throws IOException {
		
		BulkSimulationResultSummaryVo summaryVo = resultVo.getSummaryVo();
		
		List<BulkSimulationResultVo> resultDataList = resultVo.getResultDataList();
		if(resultDataList.size() > 0) {
			List<Map<String, Object>> listMap = resultDataList.stream().map(vo -> {
				vo.setCreateDate(DateUtil.getCurrentDateTimeMille());
				return ObjectUtils.objectToMap(vo);
			}).collect(Collectors.toList());
			elasticSearchInsertRepository.insertBulkData(ElasticSearchIndex.INDEX_NAME_SIMULATION_HISTORY, listMap);
		}
		
		summaryVo.setCreateDate(DateUtil.getCurrentDateTimeMille());
		Map<String, Object> summaryMap = ObjectUtils.objectToMap(summaryVo);
		elasticSearchInsertRepository.insertData(ElasticSearchIndex.INDEX_NAME_SIMULATION_SUMMARY, summaryVo.getTaskId(), summaryMap);	
	}
}
