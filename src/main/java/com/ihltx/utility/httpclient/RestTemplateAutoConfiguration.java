package com.ihltx.utility.httpclient;

import com.ihltx.utility.httpclient.config.RestTemplateConfig;
import com.ihltx.utility.httpclient.service.RestTemplateUtil;
import com.ihltx.utility.httpclient.service.impl.RestTemplateUtilImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableConfigurationProperties(RestTemplateConfig.class)
@ConditionalOnClass(RestTemplateUtil.class)
public class RestTemplateAutoConfiguration {


	@Autowired
	private RestTemplateConfig restTemplateConfig;

	@Bean
	@Primary
	@ConditionalOnMissingBean(RestTemplateUtil.class)
	public RestTemplateUtil getRestTemplateUtil() {
		RestTemplateUtil restTemplateUtil =  new RestTemplateUtilImpl(restTemplateConfig.getConnectTimeout(), restTemplateConfig.getReadTimeout());
		return restTemplateUtil;
	}

}
