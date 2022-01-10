package com.ihltx.utility.ueditor;

import com.ihltx.utility.ueditor.config.UeditorConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("all")
@Configuration
@EnableConfigurationProperties({UeditorConfig.class})
@ComponentScan(basePackages = {"com.ihltx.utility.ueditor"})
public class UeditorAutoConfiguration{

	@Autowired
	private UeditorConfig ueditorConfig;
}