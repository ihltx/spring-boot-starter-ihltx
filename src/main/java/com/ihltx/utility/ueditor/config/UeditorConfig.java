package com.ihltx.utility.ueditor.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ihltx.ueditor")
public class UeditorConfig {
	private String rootPath= "classpath:/templates/view";

}
