package com.ihltx.utility.upload;

import com.ihltx.utility.upload.config.UploadConfig;
import com.ihltx.utility.upload.service.UploadUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(UploadConfig.class)
@ComponentScan(basePackages = {"com.ihltx.utility.upload"})
@ConditionalOnProperty(prefix = "ihltx.upload", name = "storagePath" , matchIfMissing = false)
@ConditionalOnClass(UploadUtil.class)
public class UploadAutoConfiguration {



}
