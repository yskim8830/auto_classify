package kr.co.proten.manager.common.config;

import java.net.URI;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;

@Configuration
public class RestTemplateConfig {
	
	@Bean
	public RestTemplate httpRestTemplate() {
		HttpComponentsClientHttpRequestFactory requestFactory = new CustomHttpComponentsClientHttpRequestFactory();
		requestFactory.setReadTimeout(10000);		// read timeout
		requestFactory.setConnectTimeout(5000);		// connection timeout
		
		// Apache HttpComponents HttpClient
		HttpClient httpClient = HttpClientBuilder.create()
				.setMaxConnTotal(200) // 최대 커넥션 수
				.setMaxConnPerRoute(20).build();
		
		requestFactory.setHttpClient(httpClient);

		RestTemplate restTemplate = new RestTemplate(requestFactory);
		// RestTemplate에 MessageConverter 세팅
	    List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
	    converters.add(new FormHttpMessageConverter());
	    converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
	    
	    restTemplate.setMessageConverters(converters);
		return restTemplate;
	}
	
	private static final class CustomHttpComponentsClientHttpRequestFactory extends HttpComponentsClientHttpRequestFactory {

		@Override
		protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {

			if (HttpMethod.GET.equals(httpMethod)) {
				return new HttpEntityEnclosingGetRequestBase(uri);
			}
			return super.createHttpUriRequest(httpMethod, uri);
		}
	}
	
	private static final class HttpEntityEnclosingGetRequestBase extends HttpEntityEnclosingRequestBase {

		public HttpEntityEnclosingGetRequestBase(final URI uri) {
			super.setURI(uri);
		}

		@Override
		public String getMethod() {
			return HttpMethod.GET.name();
		}
	}

	@Bean
	public RestTemplate httpsRestTemplate() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setReadTimeout(10000);		// read timeout
		requestFactory.setConnectTimeout(5000);	// connection timeout
		
		TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
	    	public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException{
	    		return true;
	    	}
	    };
	    
	    SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
        		.loadTrustMaterial(null, acceptingTrustStrategy)
        		.build();
        
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
        
        CloseableHttpClient httpClient = HttpClients.custom()
        		.setSSLSocketFactory(csf)
        		.build();
		
		requestFactory.setHttpClient(httpClient);
	    
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		// RestTemplate에 MessageConverter 세팅
	    List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
	    converters.add(new FormHttpMessageConverter());
	    converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
	    
	    restTemplate.setMessageConverters(converters);
		return restTemplate;
	}
}
