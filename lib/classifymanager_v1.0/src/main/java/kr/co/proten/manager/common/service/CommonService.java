package kr.co.proten.manager.common.service;

import java.util.List;
import java.util.Map;

public interface CommonService {
	
	/**
	 * 업로드 실패 데이터 벌크 저장
	 * @param listMap
	 * @return
	 * @throws Exception
	 */
	boolean insertUploadInvalidDataBulk(List<Map<String, Object>> listMap) throws Exception;
	
}
