package com.ihltx.utility.httpclient.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "ihltx.httpclient")
public class RestTemplateConfig {
	
	private int readTimeout = 30000;
	private int connectTimeout = 10000;
	private Boolean enableRibbon = false;

}
