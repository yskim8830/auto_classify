package kr.co.proten.manager.common.service.Impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kr.co.proten.manager.common.elastic.ElasticSearchIndex;
import kr.co.proten.manager.common.repository.ElasticSearchInsertRepository;
import kr.co.proten.manager.common.service.CommonService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

	private static final Logger log = LoggerFactory.getLogger(CommonServiceImpl.class);
	
	private final ElasticSearchInsertRepository elasticSearchInsertRepository;
	
	@Override
	public boolean insertUploadInvalidDataBulk(List<Map<String, Object>> listMap) throws Exception {
		boolean result = true;
		result = elasticSearchInsertRepository.insertBulkData(ElasticSearchIndex.INDEX_NAME_UPLOAD_INVALID_DATA, listMap);
		return result;
	}
	
}
