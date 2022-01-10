package com.ihltx.utility.freemarker.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

import freemarker.template.TemplateExceptionHandler;
import lombok.Data;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "ihltx.freemarker")
public class FreemarkerConfig {

	private String requestAndSessionAndViewThemeName = "APP_CURRENT_THEME";
	private String themeName = "APP_THEME";
	private String defaultTheme ="default";
	private String defaultCdnUrl = "";

	private Boolean enableDataSource = false;
	private Boolean enableCache = false;

	private Map<String , Object> globalVariables;

	private String mybatisDynamicDataSourceName = "system";

	private String charset = "UTF-8";
	private int tagsynta = 0;

	private String templateExt = ".ftl";

	private String templatePaths ="classpath:/templates/";

	private String templateExceptionHandler = "RETHROW_HANDLER";


	private String viewName = "view";

	private String appPath = "/";

	private TemplateExceptionHandler templateExceptionHandlerObject = TemplateExceptionHandler.RETHROW_HANDLER;

	private Map<String ,CustomizeTag> customizeTags;

	/**
	 * 未启用数据时指定各店铺自己的默认主题,如果仍然未找到使用 defaultTheme 主题
	 */
	private Map<Long , String> shopThemes;

	/**
	 * 未启用数据时指定各店铺自己的默认CDN url,如果仍然未找到使用 defaultCdnUrl
	 */
	private Map<Long , String> shopCdnUrls;
}
