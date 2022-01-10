package com.ihltx.utility.upload.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "ihltx.upload")
public class UploadConfig {
	
	private String uploadPath ="%SHOP_ID%/runtime/upload";

	private String webPathPrefix = "/static/";

	private String uploadFilenameRule ="MD5";

	private List<String> uploadAllowExts;

	private int maxUploadFileSize = 5 * 1024 * 1024;

	private String storagePath;

}
