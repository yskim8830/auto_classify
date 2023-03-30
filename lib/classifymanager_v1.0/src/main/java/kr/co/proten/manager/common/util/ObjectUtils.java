package kr.co.proten.manager.common.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectUtils {

	private static final Logger log = LoggerFactory.getLogger(ObjectUtils.class);
	
	public static Map<String, Object> objectToMap(Object object) {
		ObjectMapper oMapper = new ObjectMapper();
		Map<String, Object> map = oMapper.convertValue(object, Map.class);
		return map;
    }
}
