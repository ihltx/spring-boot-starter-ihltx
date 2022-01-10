package com.ihltx.utility.i18n.config;


import java.util.List;
import java.util.Map;

import com.ihltx.utility.i18n.entity.Language;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "ihltx.i18n")
public class I18nConfig {

	private Boolean enableStrictMode = false;

	private String basePath = "classpath:/i18n";
	private String baseName = "messages";

	private String requestAndSessionAndViewLanguageName ="APP_CURRENT_LANGUAGE";

	private List<Language> languages;
	
	private Boolean enableDataSource = false;

	private Boolean enableCache = true;

	private String cachePrefix = "languages";

	private String mybatisDynamicDataSourceName = "system";

	private Map<Long , List<Language>> shopLanguages;
	
}
