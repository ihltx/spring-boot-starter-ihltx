package com.ihltx.utility.log.config;


import ch.qos.logback.classic.Level;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;

@Data
@ConfigurationProperties(prefix = "ihltx.log")
public class LogConfig implements Cloneable {
	private Level level;
	private Boolean additive;
	private Boolean prudent;
	private Boolean enableConsoleAppender;
	private String consolePattern;
	private Boolean enableMybatisDbAppender;
	private String mybatisDynamicDataSourceName;
	private String mybatisDbPattern;
	private Boolean enableRollingFileAppender;
	private String rollingFilePattern;
	private String storagePath;
	private String rollingName;
	private String maxFileSize;
	private Integer maxHistory;
	private String totalFileSize;

	private LinkedHashMap<String,LogConfig> loggers;


	@Override
	public LogConfig clone(){
		LogConfig logConfig = new LogConfig();
		logConfig.level = this.level;
		logConfig.additive = this.additive;
		logConfig.prudent = this.prudent;
		logConfig.enableConsoleAppender = this.enableConsoleAppender;
		logConfig.consolePattern = this.consolePattern;
		logConfig.enableMybatisDbAppender = this.enableMybatisDbAppender;
		logConfig.mybatisDynamicDataSourceName = this.mybatisDynamicDataSourceName;
		logConfig.mybatisDbPattern = this.mybatisDbPattern;
		logConfig.enableRollingFileAppender = this.enableRollingFileAppender;
		logConfig.rollingFilePattern = this.rollingFilePattern;
		logConfig.storagePath = this.storagePath;
		logConfig.rollingName = this.rollingName;
		logConfig.maxFileSize = this.maxFileSize;
		logConfig.maxHistory = this.maxHistory;
		logConfig.totalFileSize = this.totalFileSize;
		return logConfig;
	}

}
