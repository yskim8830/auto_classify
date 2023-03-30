package kr.co.proten.manager.common.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.co.proten.manager.common.ClassifyManagerConstant;
import kr.co.proten.manager.common.service.ExcelDownloadService;
import kr.co.proten.manager.common.vo.UploadnDownloadServiceField;
import kr.co.proten.manager.login.model.LoginModel;
import lombok.RequiredArgsConstructor;

/**
 * 다운로드
 */
@Controller
@RequestMapping("common")
@RequiredArgsConstructor
public class ExcelDownloadController {
	
	private static final Logger log = LoggerFactory.getLogger(ExcelDownloadController.class);
	
	private final ExcelDownloadService excelDownloadService;

	/**
	 * 다운로드
	 */
	@RequestMapping(value="/excelDownload.ps")
	public void excelDownload(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session, @RequestParam(value = "serviceId", required = true) String serviceId) throws Exception {
		log.debug( "Get {}", request.getRequestURI() );
		
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		boolean ret = true;
		boolean isValidService = false;
				
		if(serviceId != null) {
			for(UploadnDownloadServiceField value : UploadnDownloadServiceField.values()) {
				if(serviceId.equals(value.toString())){
					isValidService = true;
					break;
				}
			}
			
			if(isValidService) {
				ret = excelDownloadService.download(response, login, serviceId);
			} else {
				ret = false;
			}
		} else {
			ret = false;
		}
		
		if(!ret) {
			excelDownloadService.downloadTemplate(response, login, serviceId);
		}
	}
	
	/**
	 * 템플릿 다운로드
	 */
	@RequestMapping(value="/excelTemplateDownload.ps")
	public void excelTemplateDownload(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session, @RequestParam(value = "serviceId", required = true) String serviceId) throws Exception {
		log.debug( "Get {}", request.getRequestURI() );
		
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		boolean ret = true;
		boolean isValidService = false;
				
		if(serviceId != null) {
			for(UploadnDownloadServiceField value : UploadnDownloadServiceField.values()) {
				if(serviceId.equals(value.toString())){
					isValidService = true;
					break;
				}
			}
			
			if(isValidService) {
				excelDownloadService.downloadTemplate(response, login, serviceId);
			} else {
				ret = false;
			}
		} else {
			ret = false;
		}		
	}
	
	/**
	 * 피드백 다운로드
	 */
	@RequestMapping(value="/excelFeedBackDownload.ps")
	public void excelFeedBackDownload(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session, @RequestParam(value = "taskId", required = true) String taskId) throws Exception {
		log.debug( "Get {}", request.getRequestURI() );
		excelDownloadService.downloadFeedback(response, taskId);
	}
}
