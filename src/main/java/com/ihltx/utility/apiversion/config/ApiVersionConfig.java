package com.ihltx.utility.apiversion.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ihltx.apiversion")
public class ApiVersionConfig {

	private Boolean enable = true;

	private String defaultVersion="1.0.0";
	private String versionName ="version";
	private String versionOrder = "header,request,cookie";

}
