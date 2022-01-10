package com.ihltx.utility.interceptor.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
public class ShopIdInterceptorConfig {
	private Boolean enable = true;
	private Boolean enableRedis = false;
	private String redisName= "default";
	private Boolean enableReverseProxy = false;
	private Map<String , Long> hosts;

}
