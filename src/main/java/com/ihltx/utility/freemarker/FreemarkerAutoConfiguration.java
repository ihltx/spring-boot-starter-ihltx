package com.ihltx.utility.freemarker;

import com.ihltx.utility.i18n.I18nAutoConfiguration;
import com.ihltx.utility.i18n.service.I18nUtil;
import com.ihltx.utility.freemarker.config.FreemarkerConfig;
import com.ihltx.utility.freemarker.service.CdnUrlService;
import com.ihltx.utility.freemarker.service.FreemarkerUtil;
import com.ihltx.utility.freemarker.service.ThemeService;
import com.ihltx.utility.freemarker.service.impl.FreemarkerUtilImpl;
import freemarker.template.TemplateExceptionHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableConfigurationProperties(FreemarkerConfig.class)
@SuppressWarnings("all")
@ComponentScan(basePackages = {"com.ihltx.utility.freemarker"})
@MapperScan(basePackages = {"com.ihltx.utility.freemarker.mapper"})
@ImportAutoConfiguration(classes = {I18nAutoConfiguration.class})
public class FreemarkerAutoConfiguration {

	@Autowired
	private FreemarkerConfig freemarkerConfig;

	@Autowired
	private I18nUtil i18nUtil;

	@Autowired(required = false)
	private ThemeService themeService;

	@Autowired(required = false)
	private CdnUrlService cdnUrlService;

	@Autowired
	private ApplicationContext applicationContext;


	@Bean
	@Primary
	public FreemarkerUtil getFreemarkerUtil() {
		FreemarkerUtil freemarkerUtil= new FreemarkerUtilImpl();
		freemarkerConfig.setTemplateExceptionHandlerObject(getTemplateExceptionHandler(freemarkerConfig.getTemplateExceptionHandler()));
		freemarkerUtil.setApplicationContext(applicationContext);
		freemarkerUtil.setFreemarkerConfig(freemarkerConfig);
		freemarkerUtil.setI18nUtil(i18nUtil);
		if(themeService!=null){
			freemarkerUtil.setThemeService(themeService);
		}
		if(cdnUrlService!=null){
			freemarkerUtil.setCdnUrlService(cdnUrlService);
		}
		return freemarkerUtil;
	}
	
	
	private TemplateExceptionHandler getTemplateExceptionHandler(String tmplExceptionHandler) {
		if(tmplExceptionHandler.equalsIgnoreCase("IGNORE_HANDLER")) {
			return TemplateExceptionHandler.IGNORE_HANDLER;
		}else if(tmplExceptionHandler.equalsIgnoreCase("RETHROW_HANDLER")) {
			return TemplateExceptionHandler.RETHROW_HANDLER;
		}else if(tmplExceptionHandler.equalsIgnoreCase("DEBUG_HANDLER")) {
			return TemplateExceptionHandler.DEBUG_HANDLER;
		}else if(tmplExceptionHandler.equalsIgnoreCase("HTML_DEBUG_HANDLER")) {
			return TemplateExceptionHandler.HTML_DEBUG_HANDLER;
		}else if(tmplExceptionHandler.equalsIgnoreCase("HTML_DEBUG_HANDLER")) {
			return TemplateExceptionHandler.HTML_DEBUG_HANDLER;
		}else{
			return TemplateExceptionHandler.RETHROW_HANDLER;
		}
	}

}
