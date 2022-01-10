package com.ihltx.utility.urlrewrite.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ihltx.urlrewrite")
public class UrlRewriteConfig {

    private String path = "classpath:/urlrewrite.xml";

}
