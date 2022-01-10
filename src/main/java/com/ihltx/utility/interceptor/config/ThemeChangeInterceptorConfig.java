package com.ihltx.utility.interceptor.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
public class ThemeChangeInterceptorConfig {
	private Boolean enable = true;

}
