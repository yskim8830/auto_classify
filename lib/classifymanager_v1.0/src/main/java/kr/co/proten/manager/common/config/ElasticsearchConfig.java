package kr.co.proten.manager.common.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {
	
	@Value("${elasticsearch.ip}")
    private String hostname;
    @Value("${elasticsearch.port}")
    private int port;
    @Value("${elasticsearch.scheme}")
    private String scheme;
    
    @Bean
    public RestHighLevelClient restHighLevelClient(){

    	if(scheme == null || scheme.equals("")) {
    		scheme = "http";
    	}
    	
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost(hostname,port,scheme)));
        return client;
    }
}
