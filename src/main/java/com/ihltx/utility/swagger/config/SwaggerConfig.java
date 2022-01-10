package com.ihltx.utility.swagger.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ihltx.swagger")
public class SwaggerConfig {
	private Boolean enabled = true;
	private Boolean withApiAnnotation = true;
	private String basePackages;
	private String contactName;
	private String contactUrl;
	private String contactEmail;
	private String title;
	private String description;
	private String version;

}
