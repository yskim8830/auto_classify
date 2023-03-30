package kr.co.proten.manager.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;

@Configuration
public class MustacheFactoryConfig {
	
	@Bean
	public MustacheFactory defaultMustacheFactory() {
		MustacheFactory mustacheFactory = new DefaultMustacheFactory();
		return mustacheFactory;
	}
}
