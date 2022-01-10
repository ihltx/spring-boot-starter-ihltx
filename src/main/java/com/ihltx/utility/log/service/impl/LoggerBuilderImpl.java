package com.ihltx.utility.log.service.impl;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.OptionHelper;
import com.ihltx.utility.log.config.LogConfig;
import com.ihltx.utility.log.service.LoggerBuilder;

import com.ihltx.utility.util.FileUtil;
import com.ihltx.utility.util.StringUtil;
import com.ihltx.utility.util.WebUtil;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@SuppressWarnings("all")
@Service
public class LoggerBuilderImpl implements LoggerBuilder {

	@Autowired
	private LogConfig logConfig;

	@Autowired(required = false)
	private MybatisDbAppender mybatisDbAppender;



	private static final Map<String, Logger> container = new ConcurrentHashMap<>();

	public Map<String, Logger> getContainer() {
		return container;
	}

	public Logger getLogger() {
		String classname = null;
		String method_name = null;
		Exception exception = new Exception();
		if(exception.getStackTrace().length>1){
			classname = new Exception().getStackTrace()[1].getClassName();
//		 method_name = new Exception().getStackTrace()[1].getMethodName();
		}else if(exception.getStackTrace().length>0) {
			classname = new Exception().getStackTrace()[0].getClassName();
//		 method_name = new Exception().getStackTrace()[0].getMethodName();
		}else{
			classname = this.getClass().getName();
		}
		return getLogger(WebUtil.getShopId() , classname);
	}

	public Logger getParentLogger() {
		String classname = null;
		String method_name = null;
		if(new Exception().getStackTrace().length>2){
			classname = new Exception().getStackTrace()[2].getClassName();
//		 method_name = new Exception().getStackTrace()[2].getMethodName();
			return getLogger(WebUtil.getShopId() , classname);
		}else{
			return getLogger();
		}
	}


	public Logger getLogger(Class<?> clazz) {
		return getLogger(WebUtil.getShopId() , clazz.getName());
	}

	public Logger getLogger(String classname) {
		return getLogger(WebUtil.getShopId() , classname);
	}

	
	public Logger getLogger(long ShopId) {
		String classname = new Exception().getStackTrace()[1].getClassName();
		return getLogger(WebUtil.getShopId() , classname);
	}

	public Logger getLogger(long ShopId , String name) {

		String key = name + ShopId;

		Logger logger = container.get(key);
		if (logger != null) {
			return logger;
		}
		logger = build(ShopId , name);
		container.put(key, logger);
		return logger;
	}

	private Logger build(long ShopId , String name) {

		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		String loggerName = name + "." + ShopId;
		Logger logger = context.getLogger(loggerName);

		//current logconfig
		LogConfig currLogConfig = logConfig.clone();
		if(logConfig.getLoggers()!=null && !logConfig.getLoggers().isEmpty()){
			String[] loggerNames = loggerName.split("\\.");
			for (String key : logConfig.getLoggers().keySet()){
				for(int i=loggerNames.length-1;i>=0;i--)
				{
					StringBuffer tempLoggerName = new StringBuffer();
					for(int j=0;j<=i;j++){
						tempLoggerName.append(loggerNames[j]).append(".");
					}
					String tmpLoggerName = StringUtil.trim(tempLoggerName.toString(),".");
					String keyPattern = key.replaceAll("\\.","\\.");
					keyPattern = keyPattern.replaceAll("\\*\\*","[a-zA-Z0-9_]+(\\.[a-zA-Z0-9_]+)~@~@~@");
					keyPattern = keyPattern.replaceAll("\\*","[a-zA-Z0-9_]+");
					keyPattern = keyPattern.replaceAll("~@~@~@","*");
					if(Pattern.compile(keyPattern).matcher(tmpLoggerName).find()){
						if(logConfig.getLoggers().get(key)!=null && logConfig.getLoggers().get(key).getLevel()!=null){
							currLogConfig.setLevel(logConfig.getLoggers().get(key).getLevel());
						}

						if(logConfig.getLoggers().get(key)!=null && logConfig.getLoggers().get(key).getAdditive()!=null){
							currLogConfig.setAdditive(logConfig.getLoggers().get(key).getAdditive());
						}

						if(logConfig.getLoggers().get(key)!=null && logConfig.getLoggers().get(key).getPrudent()!=null){
							currLogConfig.setPrudent(logConfig.getLoggers().get(key).getPrudent());
						}


						if(logConfig.getLoggers().get(key)!=null && logConfig.getLoggers().get(key).getEnableConsoleAppender()!=null){
							currLogConfig.setEnableConsoleAppender(logConfig.getLoggers().get(key).getEnableConsoleAppender());
						}

						if(logConfig.getLoggers().get(key)!=null && logConfig.getLoggers().get(key).getConsolePattern()!=null){
							currLogConfig.setConsolePattern(logConfig.getLoggers().get(key).getConsolePattern());
						}

						if(logConfig.getLoggers().get(key)!=null && logConfig.getLoggers().get(key).getEnableMybatisDbAppender()!=null){
							currLogConfig.setEnableMybatisDbAppender(logConfig.getLoggers().get(key).getEnableMybatisDbAppender());
						}

						if(logConfig.getLoggers().get(key)!=null && logConfig.getLoggers().get(key).getMybatisDynamicDataSourceName()!=null){
							currLogConfig.setMybatisDynamicDataSourceName(logConfig.getLoggers().get(key).getMybatisDynamicDataSourceName());
						}

						if(logConfig.getLoggers().get(key)!=null && logConfig.getLoggers().get(key).getMybatisDbPattern()!=null){
							currLogConfig.setMybatisDbPattern(logConfig.getLoggers().get(key).getMybatisDbPattern());
						}

						if(logConfig.getLoggers().get(key)!=null && logConfig.getLoggers().get(key).getEnableRollingFileAppender()!=null){
							currLogConfig.setEnableRollingFileAppender(logConfig.getLoggers().get(key).getEnableRollingFileAppender());
						}

						if(logConfig.getLoggers().get(key)!=null && logConfig.getLoggers().get(key).getRollingFilePattern()!=null){
							currLogConfig.setRollingFilePattern(logConfig.getLoggers().get(key).getRollingFilePattern());
						}

						if(logConfig.getLoggers().get(key)!=null && logConfig.getLoggers().get(key).getStoragePath()!=null){
							currLogConfig.setStoragePath(logConfig.getLoggers().get(key).getStoragePath());
						}

						if(logConfig.getLoggers().get(key)!=null && logConfig.getLoggers().get(key).getRollingName()!=null){
							currLogConfig.setRollingName(logConfig.getLoggers().get(key).getRollingName());
						}

						if(logConfig.getLoggers().get(key)!=null && logConfig.getLoggers().get(key).getRollingName()!=null){
							currLogConfig.setRollingName(logConfig.getLoggers().get(key).getRollingName());
						}

						if(logConfig.getLoggers().get(key)!=null && logConfig.getLoggers().get(key).getMaxFileSize()!=null){
							currLogConfig.setMaxFileSize(logConfig.getLoggers().get(key).getMaxFileSize());
						}

						if(logConfig.getLoggers().get(key)!=null && logConfig.getLoggers().get(key).getMaxHistory()!=null){
							currLogConfig.setMaxHistory(logConfig.getLoggers().get(key).getMaxHistory());
						}

						if(logConfig.getLoggers().get(key)!=null && logConfig.getLoggers().get(key).getTotalFileSize()!=null){
							currLogConfig.setTotalFileSize(logConfig.getLoggers().get(key).getTotalFileSize());
						}
					}
				}
			}
		}
		if(currLogConfig.getLevel()==null){
			currLogConfig.setLevel(Level.ALL);
		}
		if(currLogConfig.getAdditive()==null){
			currLogConfig.setAdditive(false);
		}
		if(currLogConfig.getPrudent()==null){
			currLogConfig.setPrudent(false);
		}
		if(currLogConfig.getEnableConsoleAppender()==null){
			currLogConfig.setEnableConsoleAppender(true);
		}
		if(currLogConfig.getConsolePattern()==null){
			currLogConfig.setConsolePattern("%d{yyyy-MM-dd HH:mm:ss.SSS}  %highlight(%-5level) %magenta(%4line) --- [%15t] %cyan(%-40logger{40}) : %m%n");
		}
		if(currLogConfig.getEnableMybatisDbAppender()==null){
			currLogConfig.setEnableMybatisDbAppender(false);
		}
		if(currLogConfig.getMybatisDynamicDataSourceName()==null){
			currLogConfig.setMybatisDynamicDataSourceName("default");
		}

		if(currLogConfig.getMybatisDbPattern()==null){
			currLogConfig.setMybatisDbPattern("%d{yyyy-MM-dd HH:mm:ss.SSS}  %-5level %4line  --- [%15t] %-40logger{40} : %m%n");
		}

		if(currLogConfig.getEnableRollingFileAppender()==null){
			currLogConfig.setEnableRollingFileAppender(false);
		}

		if(currLogConfig.getRollingFilePattern()==null){
			currLogConfig.setRollingFilePattern("%d{yyyy-MM-dd HH:mm:ss.SSS}  %-5level %4line  --- [%15t] %-40logger{40} : %m%n");
		}

		if(StringUtil.isNullOrEmpty(currLogConfig.getStoragePath())){
			URL url = this.getClass().getResource("/");
			if(url!=null){
				if(url.getPath().contains(".jar!")){
					currLogConfig.setStoragePath(StringUtil.rtrim(FileUtil.getJarDir().replaceAll("\\\\","/"),"/") + "/logs/%SHOP_ID%/");
				}else{
					currLogConfig.setStoragePath(StringUtil.rtrim(url.getPath().replaceAll("\\\\","/"),"/") + "/logs/%SHOP_ID%/");
				}
			}else{
				currLogConfig.setStoragePath(StringUtil.rtrim(FileUtil.getJarDir().replaceAll("\\\\","/"),"/") + "/logs/%SHOP_ID%/");
			}
		}
		String logPath = currLogConfig.getStoragePath().replaceAll("%SHOP_ID%", String.valueOf(ShopId));
		FileUtil.makeDirs(logPath);

		if(currLogConfig.getRollingName()==null){
			currLogConfig.setRollingName(".%d{yyyy-MM-dd}.%i");
		}

		if(currLogConfig.getMaxFileSize()==null){
			currLogConfig.setMaxFileSize("2048KB");
		}

		if(currLogConfig.getMaxHistory()==null){
			currLogConfig.setMaxHistory(15);
		}

		if(currLogConfig.getTotalFileSize()==null){
			currLogConfig.setTotalFileSize("32GB");
		}

		logger.setLevel(currLogConfig.getLevel());
		logger.setAdditive(currLogConfig.getAdditive());


		if(currLogConfig.getEnableRollingFileAppender()){
			RollingFileAppender appender = new RollingFileAppender();
			appender.setContext(context);
			appender.setName(name + "-" + ShopId);
			appender.setFile(logPath + "/" + name + ".log");
			appender.setAppend(true);
			appender.setPrudent(currLogConfig.getPrudent());
			SizeAndTimeBasedRollingPolicy policy = new SizeAndTimeBasedRollingPolicy();
			policy.setContext(context);
			String fp = OptionHelper.substVars(logPath + "/" + name +  currLogConfig.getRollingName() + ".log", context);

			policy.setMaxFileSize(FileSize.valueOf(currLogConfig.getMaxFileSize()));
			policy.setFileNamePattern(fp);
			policy.setMaxHistory(currLogConfig.getMaxHistory());
			policy.setParent(appender);
			policy.setTotalSizeCap(FileSize.valueOf(currLogConfig.getTotalFileSize()));
			policy.start();
			appender.setRollingPolicy(policy);

			PatternLayoutEncoder rollingFileEncoder = new PatternLayoutEncoder();
			rollingFileEncoder.setContext(context);
			if(!Strings.isEmpty(currLogConfig.getRollingFilePattern())){
				rollingFileEncoder.setPattern(currLogConfig.getRollingFilePattern());
			}
			rollingFileEncoder.start();


			appender.setEncoder(rollingFileEncoder);
			appender.start();
			logger.addAppender(appender);
		}


		if(currLogConfig.getEnableConsoleAppender()){
			ConsoleAppender consoleAppender = new ConsoleAppender();
			consoleAppender.setContext(context);
			PatternLayoutEncoder consoleEncoder = new PatternLayoutEncoder();
			consoleEncoder.setContext(context);
			if(!Strings.isEmpty(currLogConfig.getConsolePattern())){
				consoleEncoder.setPattern(currLogConfig.getConsolePattern());
			}
			consoleEncoder.start();

			consoleAppender.setEncoder(consoleEncoder);
			consoleAppender.start();
			logger.addAppender(consoleAppender);
		}

		if(currLogConfig.getEnableMybatisDbAppender() && mybatisDbAppender!=null){
			mybatisDbAppender.setContext(context);
			PatternLayout patternLayout =new PatternLayout();
			patternLayout.setContext(context);
			if(!Strings.isEmpty(currLogConfig.getMybatisDbPattern())){
				patternLayout.setPattern(currLogConfig.getMybatisDbPattern());
			}
			patternLayout.start();

			mybatisDbAppender.setLayout(patternLayout);
			mybatisDbAppender.start();
			logger.addAppender(mybatisDbAppender);
		}

		return logger;
	}
	
	public void debug(String message) {
		this.getParentLogger().debug(message);
	}

	public void info(String message) {
		this.getParentLogger().info(message);
	}
	

	public void warn(String message) {
		this.getParentLogger().warn(message);
	}
	
	public void error(String message) {
		this.getParentLogger().error(message);
	}
	
	public void trace(String message) {
		this.getParentLogger().trace(message);

	}
}
