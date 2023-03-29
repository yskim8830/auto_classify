package kr.co.proten.manager.common.service;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import kr.co.proten.manager.common.vo.BulkSimulationResultRootVo;
import kr.co.proten.manager.common.vo.UploadStepResultVo;
import kr.co.proten.manager.login.model.LoginModel;

public abstract class UploadDataService {
	
	public final String MSOFFICE_EXCEL_EXT_XLSX = "xlsx";
	public final String MSOFFICE_EXCEL_EXT_XLS = "xls";
	public final String NEW_LINE = "\n";
	
	public String[] getFeedbackHeader() {
		return null;
	}
	
	/**
	 * 파일 검증
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public UploadStepResultVo doFileValidation(File file) throws Exception {
		return null;
	};
		
	/**
	 * 파일 검증
	 * @param HEADER_NAME
	 * @param file
	 * @return
	 * @throws Exception
	 */
	protected UploadStepResultVo doFileValidation(String[] HEADER_NAME, File file) throws Exception {
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
		
		Sheet sheet = workbook.getSheetAt(0);
		/* START: 엑셀 헤더 양식 체크 */
		Row headerRow = sheet.getRow(0);
		int lastHeaderCellNum = headerRow.getLastCellNum();
		if(lastHeaderCellNum != HEADER_NAME.length) {
			stepResultVo.setStatus("fail");
			stepResultVo.setErrorMessage("업로드 파일 양식이 잘못되었습니다.");
			if(workbook != null) {
				workbook.close();
			}
			if(fis != null) {
				fis.close();
			}
			return stepResultVo;
		}
		
		Cell headerCell = null;
		for(int idx=0; idx<HEADER_NAME.length; idx++) {
			headerCell = headerRow.getCell(idx);
			if(headerCell.getRichStringCellValue() == null) {
				stepResultVo.setStatus("fail");
				stepResultVo.setErrorMessage("[" + HEADER_NAME[idx] + "] 헤더가 일치 하지 않습니다.");
				if(workbook != null) {
					workbook.close();
				}
				if(fis != null) {
					fis.close();
				}
				return stepResultVo;
			}
			
			if(!headerCell.getRichStringCellValue().getString().trim().equals(HEADER_NAME[idx])) {
				stepResultVo.setStatus("fail");
				stepResultVo.setErrorMessage("[" + HEADER_NAME[idx] + "] 헤더가 일치 하지 않습니다.");
				if(workbook != null) {
					workbook.close();
				}
				if(fis != null) {
					fis.close();
				}
				return stepResultVo;
			}
		}
		
		stepResultVo.setStatus("success");
		if(workbook != null) {
			workbook.close();
		}
		if(fis != null) {
			fis.close();
		}
		return stepResultVo;	
	}
		
	/**
	 * 데이터 파싱 & 검증
	 * @param login
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public UploadStepResultVo doDataValidation(LoginModel login, File file) throws Exception {
		return null;
	};
	
	/**
	 * 데이터 적재
	 * @param dataList
	 * @return
	 */
	public UploadStepResultVo doImportData(List<Map<String,Object>> dataList) {
		return null;
		
	}
	
	/**
	 * 엑셀 셀데이터 처리
	 * @param cell
	 * @return
	 */
	public String getCellData(Cell cell) {
		
		String value = "";
		if (cell == null)
			value = "";
		else {
			switch (cell.getCellType()) { // cell 타입에 따른 데이타 저장
				case FORMULA:
					value = cell.getCellFormula();
					break;
				case NUMERIC:
					if (DateUtil.isCellDateFormatted(cell)) {
						SimpleDateFormat objSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
						value = objSimpleDateFormat.format(cell.getDateCellValue());
					} else {
						value = String.format("%.0f", new Double(cell.getNumericCellValue()));
					}
					break;
				case STRING:
					value = cell.getStringCellValue();
					break;
				case BLANK:
					value = "";
					break;
				case ERROR:
					value = String.valueOf(cell.getErrorCellValue());
					break;
				default:
					break;
			}
		}
		
		return value.trim();
	}
	
	/**
	 * 엑셀 숫자형 데이터 확인
	 * @param value
	 * @return
	 */
	public boolean checkStringToInteger(String value) {
		boolean result = true;
		
		if(value == null || value.equals("")) {
			result = false;
			return result;
		}
		
		try {
			Integer.parseInt(value);
		} catch(NumberFormatException e) {
			result = false;
			return result;
		}
		
		return result;
	}
	
	/**
	 * 엑셀 빈값 확인
	 * @param value
	 * @return
	 */
	public boolean checkNotEmptyString(String value) {
		boolean result = true;
		
		if(value == null || value.equals("null") || value.replaceAll(" ", "").equals("")) {
			result = false;
			return result;
		}
		
		return result;
	}
	
	/**
	 * 전화번호 체크
	 * @param value
	 * @return
	 */
	public boolean phoneNumberChk(String value) {
		boolean flag = true;
		String _regexp = "^[0-9]{9,20}";
		if(Pattern.matches(_regexp, value)) {
			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}
}
