package kr.co.proten.manager.common.service;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheException;
import com.github.mustachejava.MustacheFactory;

@Service
public class MustacheFactoryService {

		private MustacheFactory mustacheFactory;
		
		private String STATUS = "status";
		private String ERROR_MESSAGE = "errorMessage";
		private String RESULT_VALUE = "resultValue";
		
		public MustacheFactoryService(MustacheFactory mustacheFactory) {
			this.mustacheFactory = mustacheFactory;
		}
		
		public HashMap<String,Object> binding(String template, HashMap<String, Object> valueMap, String sessionInfo) {
			HashMap<String,Object> returnMap = new HashMap<String,Object>();
			StringWriter sw = new StringWriter();
			
			Mustache compile = null;
			
			StringReader templateReader = new StringReader(template);
			try {
				compile = mustacheFactory.compile(templateReader, sessionInfo);
				compile.execute(sw, valueMap).flush();
			} catch (IOException e) {
				returnMap.put(STATUS, "fail");
				returnMap.put(ERROR_MESSAGE, e.getMessage());
			} catch (MustacheException e) {
				returnMap.put(STATUS, "fail");
				returnMap.put(ERROR_MESSAGE, e.getMessage());
			} 
			
			if(!returnMap.containsKey(STATUS)) {
				returnMap.put(STATUS, "success");
				returnMap.put(RESULT_VALUE, sw.toString());
			}
			
			return returnMap;
		}
		
		public HashMap<String,Object> binding(String template, Object value, String sessionInfo) {
			HashMap<String,Object> returnMap = new HashMap<String,Object>();
			StringWriter sw = new StringWriter();
			
			Mustache compile = null;
			
			StringReader templateReader = new StringReader(template);
			try {
				compile = mustacheFactory.compile(templateReader, sessionInfo);
				compile.execute(sw, value).flush();
			} catch (IOException e) {
				returnMap.put(STATUS, "fail");
				returnMap.put(ERROR_MESSAGE, e.getMessage());
			} catch (MustacheException e) {
				returnMap.put(STATUS, "fail");
				returnMap.put(ERROR_MESSAGE, e.getMessage());
			} 
			
			if(!returnMap.containsKey(STATUS)) {
				returnMap.put(STATUS, "success");
				returnMap.put(RESULT_VALUE, sw.toString());
			}
			
			return returnMap;
		}
}
