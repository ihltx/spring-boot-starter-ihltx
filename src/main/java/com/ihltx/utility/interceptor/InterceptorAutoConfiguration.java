package com.ihltx.utility.interceptor;

import com.ihltx.utility.freemarker.FreemarkerAutoConfiguration;
import com.ihltx.utility.freemarker.interceptor.ThemeChangeInterceptor;
import com.ihltx.utility.freemarker.service.FreemarkerUtil;
import com.ihltx.utility.i18n.I18nAutoConfiguration;
import com.ihltx.utility.i18n.interceptor.LanguageChangeInterceptor;
import com.ihltx.utility.i18n.service.I18nUtil;
import com.ihltx.utility.interceptor.config.InterceptorConfig;
import com.ihltx.utility.interceptor.interceptor.ShopIdInterceptor;
import com.ihltx.utility.redis.RedisAutoConfiguration;
import com.ihltx.utility.redis.service.RedisFactory;
import com.ihltx.utility.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SuppressWarnings("all")
@Configuration
@EnableConfigurationProperties({InterceptorConfig.class})
@ImportAutoConfiguration(classes = {I18nAutoConfiguration.class , FreemarkerAutoConfiguration.class , RedisAutoConfiguration.class})
public class InterceptorAutoConfiguration implements WebMvcConfigurer {

	@Autowired
	private InterceptorConfig interceptorConfig;

	@Autowired
	private I18nUtil i18nUtil;

	@Autowired
	private FreemarkerUtil freemarkerUtil;

	@Autowired(required = false)
	private RedisFactory redisFactory;

	@Value("${spring.web.resources.static-locations}")
	private String staticLocations;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		if(interceptorConfig.getShopId().getEnable()){
			ShopIdInterceptor shopIdInterceptor =new ShopIdInterceptor();
			shopIdInterceptor.setInterceptorConfig(this.interceptorConfig);
			if(redisFactory!=null){
				shopIdInterceptor.setRedisFactory(redisFactory);
			}
			registry.addInterceptor(shopIdInterceptor).order(Ordered.HIGHEST_PRECEDENCE);
		}

		if(interceptorConfig.getTheme().getEnable()){
			ThemeChangeInterceptor themeChangeInterceptor = new ThemeChangeInterceptor();
			themeChangeInterceptor.setFreemarkerUtil(freemarkerUtil);
			registry.addInterceptor(themeChangeInterceptor).order(Ordered.HIGHEST_PRECEDENCE);

		}

		if(interceptorConfig.getLanguage().getEnable()){
			LanguageChangeInterceptor languageChangeInterceptor =new LanguageChangeInterceptor();
			languageChangeInterceptor.setI18nUtil(i18nUtil);
			registry.addInterceptor(languageChangeInterceptor).order(Ordered.HIGHEST_PRECEDENCE);
		}
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		if(!StringUtil.isNullOrEmpty(staticLocations)){
			registry.addResourceHandler("/**").addResourceLocations(staticLocations.split(","));
		}
	}
}