package com.ihltx.utility.interceptor.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "ihltx.interceptor")
public class InterceptorConfig {
	private ShopIdInterceptorConfig shopId;
	private ThemeChangeInterceptorConfig theme;
	private LanguageChangeInterceptorConfig language;

}
