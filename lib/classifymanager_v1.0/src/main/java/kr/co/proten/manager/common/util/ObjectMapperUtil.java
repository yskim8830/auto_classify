package kr.co.proten.manager.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ObjectMapperUtil {
	
	private static final Logger log = LoggerFactory.getLogger(ObjectMapperUtil.class);

    static ObjectMapper om = new ObjectMapper();

    public static List<HashMap<String, Object>> toListHashMap(String jsonAsString){

        List<HashMap<String, Object>> myList = new ArrayList<>();
        try {
            myList = om.readValue(jsonAsString, new TypeReference<List<HashMap<String, Object>>>(){});
        } catch ( Exception e) {
        	log.error("Exception " + e.getMessage(), e);
        }

        return myList;
    }



}
