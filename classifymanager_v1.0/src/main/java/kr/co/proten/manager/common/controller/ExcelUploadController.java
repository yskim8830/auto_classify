package kr.co.proten.manager.common.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import kr.co.proten.manager.common.ClassifyManagerConstant;
import kr.co.proten.manager.common.config.AsyncConfig;
import kr.co.proten.manager.common.service.ExcelUploadService;
import kr.co.proten.manager.common.util.DateUtil;
import kr.co.proten.manager.common.util.StringUtil;
import kr.co.proten.manager.common.vo.BulkSimulationResultRootVo;
import kr.co.proten.manager.common.vo.UploadStepResultVo;
import kr.co.proten.manager.common.vo.UploadnDownloadServiceField;
import kr.co.proten.manager.login.model.LoginModel;
import lombok.RequiredArgsConstructor;

/**
 * 엑셀 업로드
 */
@Controller
@RequestMapping("common")
@RequiredArgsConstructor
public class ExcelUploadController {
	
	private static final Logger log = LoggerFactory.getLogger(ExcelUploadController.class);
	
	private String STATUS = "status";
	private String ERROR_MESSAGE = "errorMessage";
	private String TASK_ID = "taskId";
	
	private final ExcelUploadService excelUploadService;
	
	private final AsyncConfig asyncConfig;

	/**
	 * 엑셀 업로드
	 * @param result
	 * @param request
	 * @param session
	 * @param serviceId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="excelUpload.ps", method = RequestMethod.POST)
	public String excelUpload(Model result, MultipartHttpServletRequest request, HttpSession session, @RequestParam(value = "serviceId", required = true) String serviceId) throws Exception {
		log.debug( "Get {}", request.getRequestURI() );
		
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		HashMap<String,Object> returnMap = new HashMap<String,Object>();
		String filePath = "/WEB-INF/upload/";
		boolean isValidService = false;
		
		UUID uuId = UUID.randomUUID();
		String taskId = uuId.toString();
		
		if(asyncConfig.checkTaskExecute()) {
			if(serviceId != null) {
				for(UploadnDownloadServiceField value : UploadnDownloadServiceField.values()) {
					if(serviceId.equals(value.toString())){
						isValidService = true;
						break;
					}
				}
				
				if(isValidService) {
					filePath = filePath + serviceId;
					
					Iterator<String> iter = request.getFileNames();
					
					if(iter.hasNext()) {
						List<MultipartFile> mpf = request.getFiles((String) iter.next());
						File excelFile = new File(request.getSession().getServletContext().getRealPath(filePath) + File.separator + DateUtil.getCurrentDateTimeMille() + "_" +mpf.get(0).getOriginalFilename());
						if(!excelFile.mkdirs()) {
							excelFile.mkdirs();
						}
						mpf.get(0).transferTo(excelFile);
						excelUploadService.upload(taskId, login, serviceId, excelFile);
						returnMap.put(STATUS, "success");
						returnMap.put(TASK_ID, taskId);
					}
				} else {
					returnMap.put(STATUS, "fail");
					returnMap.put(ERROR_MESSAGE, "지원하지 않는 serviceId 입니다.");
				}
			} else {
				returnMap.put(STATUS, "fail");
				returnMap.put(ERROR_MESSAGE, "serviceId는 필수 파라미터 입니다.");
			}
		} else {
			returnMap.put(STATUS, "fail");
			returnMap.put(ERROR_MESSAGE, "현재 작업중인 프로세스가 많아 요청하신 작업을 진행 할 수 없습니다. 잠시 후에 다시 시도해 주세요.");
		}
		
		result.addAttribute("result", returnMap);
		
		return "jsonView";
	}
	
	/**
	 * 엑셀 업로드 시뮬레이션
	 * @param result
	 * @param request
	 * @param session
	 * @param serviceId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="bulkSimulation.ps", method = RequestMethod.POST)
	public String bulkSimulation(Model result, MultipartHttpServletRequest request, HttpSession session, @RequestParam(value = "serviceId", required = true) String serviceId) throws Exception {
		log.debug( "Get {}", request.getRequestURI() );
		String SERVICE_ID = "bulkSimulation";
		
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		HashMap<String,Object> returnMap = new HashMap<String,Object>();
		String filePath = "/WEB-INF/upload/";
		
		UUID uuId = UUID.randomUUID();
		String taskId = uuId.toString();
		int version = Integer.parseInt(request.getParameter("bulkModelVersion"));
		int threshold = Integer.parseInt(request.getParameter("mThreshold"));
		log.debug("[bulkSimulation] taskId :: {}", taskId);
		log.debug("[bulkSimulation] version :: {}", version);
		log.debug("[bulkSimulation] threshold :: {}", threshold);
		
		List<String> taskIdList = excelUploadService.getBulkSimulationTaskIdList(login);
		if(taskIdList.size() > 0) {
			returnMap.put(STATUS, "fail");
			returnMap.put(ERROR_MESSAGE, "접속하신 ID [" +login.getUserId()+" ] 사용자가 진행중인 시뮬레이션이 있습니다. 잠시 후에 다시 시도해 주세요.");
			result.addAttribute("result", returnMap);
			return "jsonView";
		}
		
		if(asyncConfig.checkTaskExecute()) {
			if(serviceId != null && serviceId.equals(SERVICE_ID)) {			
				filePath = filePath + serviceId;
				
				Iterator<String> iter = request.getFileNames();
				
				if(iter.hasNext()) {
					List<MultipartFile> mpf = request.getFiles((String) iter.next());
					File excelFile = new File(request.getSession().getServletContext().getRealPath(filePath) + File.separator + DateUtil.getCurrentDateTimeMille() + "_" +mpf.get(0).getOriginalFilename());
					if(!excelFile.mkdirs()) {
						excelFile.mkdirs();
					}
					mpf.get(0).transferTo(excelFile);
					excelUploadService.bulkSimulation(login, taskId, version, threshold, excelFile);
					returnMap.put(STATUS, "success");
					returnMap.put(TASK_ID, taskId);
				}
			} else {
				returnMap.put(STATUS, "fail");
				returnMap.put(ERROR_MESSAGE, "지원하지 않는 serviceId 입니다");
			}
		} else {
			returnMap.put(STATUS, "fail");
			returnMap.put(ERROR_MESSAGE, "현재 작업중인 프로세스가 많아 요청하신 작업을 진행 할 수 없습니다. 잠시 후에 다시 시도해 주세요.");
		}
		result.addAttribute("result", returnMap);
		
		return "jsonView";
	}
	
	/**
	 * 업로드 상태 조회
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "uploadStepAjax.ps")
	public String uploadStepAjax(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session){

		String taskId = StringUtil.nvl(paramMap.get("taskId"), "");
		UploadStepResultVo stepResultVo = null;
		if(!taskId.equals("")) {
			stepResultVo = excelUploadService.getStep(taskId);
		} else {
			stepResultVo = new UploadStepResultVo();
			stepResultVo.setStatus("fail");
			stepResultVo.setErrorMessage("taskId는 필수 파라미터 입니다.");
		}
		
		result.addAttribute("result", stepResultVo);

		return "jsonView";
	}
	
	/**
	 * 시뮬레이션 상태 조회
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "simulationProgressAjax.ps")
	public String simulationProgressAjax(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session){

		String taskId = StringUtil.nvl(paramMap.get("taskId"), "");
		BulkSimulationResultRootVo returnResultVo = null;
		if(!taskId.equals("")) {
			returnResultVo = excelUploadService.getBulkSimulationProgressInfo(taskId);
		} else {
			returnResultVo = new BulkSimulationResultRootVo();
			returnResultVo.setStatus("fail");
			returnResultVo.setErrorMessage("taskId는 필수 파라미터 입니다.");
		}
		
		result.addAttribute("result", returnResultVo);

		return "jsonView";
	}
	
	/**
	 * 시뮬레이션 동작 리스트 조회
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "simulationTaskIdListAjax.ps")
	public String simulationTaskIdListAjax(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session){

		log.debug( "Get {}", request.getRequestURI() );
		LoginModel login = (LoginModel)session.getAttribute(ClassifyManagerConstant.LOGIN_SESSEION_NAME);
		List<String> taskIdList = excelUploadService.getBulkSimulationTaskIdList(login);
		
		result.addAttribute("result", taskIdList);

		return "jsonView";
	}
	
	/**
	 * 시뮬레이션 중지
	 * @param result
	 * @param paramMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "terminateBulkSimulationAjax.ps")
	public String terminateBulkSimulationAjax(Model result, @RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, HttpSession session){

		log.debug( "Get {}", request.getRequestURI() );
		String taskId = StringUtil.nvl(paramMap.get("taskId"), "");
		boolean ret = true;
		if(!taskId.equals("")) {
			ret = excelUploadService.terminateBulkSimulation(taskId);
		} else {
			ret = false;
		}
		
		result.addAttribute("result", ret);

		return "jsonView";
	}
}
