package kr.co.proten.manager.common.service;

import java.net.URI;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

@Service
public class RestTemplateService<T> {
	
	private static final Logger log = LoggerFactory.getLogger(RestTemplateService.class);
	
	private RestTemplate httpRestTemplate;
	private RestTemplate httpsRestTemplate;
		
	public RestTemplateService(RestTemplate httpRestTemplate, RestTemplate httpsRestTemplate) {
		this.httpRestTemplate = httpRestTemplate;
		this.httpsRestTemplate = httpsRestTemplate;
	}
	
	public ResponseEntity<T> httpGet(String url, HttpHeaders httpHeaders) {
		return callApiEndpoint(url, HttpMethod.GET, httpHeaders, null, (Class<T>)Object.class);
	}

	public ResponseEntity<T> httpGet(String url, HttpHeaders httpHeaders, String body, Class<T> clazz) {
	    return callApiEndpoint(url, HttpMethod.GET, httpHeaders, body, clazz);
	}
	
	public ResponseEntity<T> httpGet(String url, HttpHeaders httpHeaders, Class<T> clazz) {
	    return callApiEndpoint(url, HttpMethod.GET, httpHeaders, null, clazz);
	}
	
	public ResponseEntity<T> httpGet(URI uri, HttpHeaders httpHeaders, String body, Class<T> clazz) {
	    return callApiEndpointByUri(uri, HttpMethod.GET, httpHeaders, body, clazz);
	}
	
	public ResponseEntity<T> httpGet(URI uri, HttpHeaders httpHeaders, Class<T> clazz) {
	    return callApiEndpointByUri(uri, HttpMethod.GET, httpHeaders, null, clazz);
	}
	
	public ResponseEntity<T> httpPost(String url, HttpHeaders httpHeaders, String body) {
	    return callApiEndpoint(url, HttpMethod.POST, httpHeaders, body, (Class<T>)Object.class);
	}
	
	public ResponseEntity<T> httpPost(String url, HttpHeaders httpHeaders, String body, Class<T> clazz) {
	    return callApiEndpoint(url, HttpMethod.POST, httpHeaders, body, clazz);
	}
	
	public ResponseEntity<T> httpPut(String url, HttpHeaders httpHeaders, String body, Class<T> clazz) {
	    return callApiEndpoint(url, HttpMethod.PUT, httpHeaders, body, clazz);
	}
	
	public ResponseEntity<T> httpDelete(String url, HttpHeaders httpHeaders, String body, Class<T> clazz) {
	    return callApiEndpoint(url, HttpMethod.DELETE, httpHeaders, body, clazz);
	}
	
	public ResponseEntity<T> httpsGet(String url, HttpHeaders httpHeaders) {
		return callHttpsApiEndpoint(url, HttpMethod.GET, httpHeaders, null, (Class<T>)Object.class);
	}

	public ResponseEntity<T> httpsGet(String url, HttpHeaders httpHeaders, String body, Class<T> clazz) {
	    return callHttpsApiEndpoint(url, HttpMethod.GET, httpHeaders, body, clazz);
	}
	
	public ResponseEntity<T> httpsGet(String url, HttpHeaders httpHeaders, Class<T> clazz) {
	    return callHttpsApiEndpoint(url, HttpMethod.GET, httpHeaders, null, clazz);
	}
	
	public ResponseEntity<T> httpsGet(URI uri, HttpHeaders httpHeaders, String body, Class<T> clazz) {
	    return callHttpsApiEndpointByUri(uri, HttpMethod.GET, httpHeaders, body, clazz);
	}
	
	public ResponseEntity<T> httpsGet(URI uri, HttpHeaders httpHeaders, Class<T> clazz) {
	    return callHttpsApiEndpointByUri(uri, HttpMethod.GET, httpHeaders, null, clazz);
	}
	
	public ResponseEntity<T> httpsPost(String url, HttpHeaders httpHeaders, String body) {
	    return callHttpsApiEndpoint(url, HttpMethod.POST, httpHeaders, body, (Class<T>)Object.class);
	}
	
	public ResponseEntity<T> httpsPost(String url, HttpHeaders httpHeaders, String body, Class<T> clazz) {
	    return callHttpsApiEndpoint(url, HttpMethod.POST, httpHeaders, body, clazz);
	}
	
	public ResponseEntity<T> httpsPut(String url, HttpHeaders httpHeaders, String body, Class<T> clazz) {
	    return callHttpsApiEndpoint(url, HttpMethod.PUT, httpHeaders, body, clazz);
	}
	
	public ResponseEntity<T> httpsDelete(String url, HttpHeaders httpHeaders, String body, Class<T> clazz) {
	    return callHttpsApiEndpoint(url, HttpMethod.DELETE, httpHeaders, body, clazz);
	}
	
	private ResponseEntity<T> callApiEndpoint(String url, HttpMethod httpMethod, HttpHeaders httpHeaders, String body, Class<T> clazz) {
	    return httpRestTemplate.exchange(url, httpMethod, new HttpEntity<String>(body, httpHeaders), clazz);
	}
	
	private ResponseEntity<T> callApiEndpointByUri(URI uri, HttpMethod httpMethod, HttpHeaders httpHeaders, String body, Class<T> clazz) {
	    return httpRestTemplate.exchange(uri, httpMethod, new HttpEntity<String>(body, httpHeaders), clazz);
	} 
	
	private ResponseEntity<T> callHttpsApiEndpoint(String url, HttpMethod httpMethod, HttpHeaders httpHeaders, String body, Class<T> clazz) {
	    return httpsRestTemplate.exchange(url, httpMethod, new HttpEntity<String>(body, httpHeaders), clazz);
	}
	
	private ResponseEntity<T> callHttpsApiEndpointByUri(URI uri, HttpMethod httpMethod, HttpHeaders httpHeaders, String body, Class<T> clazz) {
	    return httpsRestTemplate.exchange(uri, httpMethod, new HttpEntity<String>(body, httpHeaders), clazz);
	} 
	
	public HttpHeaders getHeader(String contentType, String apiHeader, String authName, String authPassword) {
		HttpHeaders headers = new HttpHeaders();
		if(contentType.equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)) {
			headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
		} else if(contentType.equalsIgnoreCase(MediaType.APPLICATION_XML_VALUE)) {
			headers.setContentType(new MediaType("application", "xml", Charset.forName("UTF-8")));
		} else if(contentType.equalsIgnoreCase(MediaType.TEXT_PLAIN_VALUE)) {
			headers.setContentType(new MediaType("text", "plain", Charset.forName("UTF-8")));
		} else if(contentType.equalsIgnoreCase(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
			headers.setContentType(new MediaType("application", "x-www-form-urlencoded", Charset.forName("UTF-8")));
		}
		
		// 헤더 세팅
		JsonArray headerArray = new Gson().fromJson(apiHeader, JsonArray.class);
		for(int i=0; i<headerArray.size(); i++) {
			if(headers.containsKey(headerArray.get(i).getAsJsonObject().get("key").getAsString())) {
				headers.set(headerArray.get(i).getAsJsonObject().get("key").getAsString(), headerArray.get(i).getAsJsonObject().get("value").getAsString());
			} else {
				headers.add(headerArray.get(i).getAsJsonObject().get("key").getAsString(), headerArray.get(i).getAsJsonObject().get("value").getAsString());
			}
		}
		
		// authorization set(Basic Auth)
		if(authName!= null && !authName.equals("") && authPassword!= null && !authPassword.equals("")) {
			headers.setBasicAuth(authName, authPassword);
		}
		
		return headers;	    	
	}
	
	public HttpHeaders getTrainingApiHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
		
		return headers;	    	
	}
}
