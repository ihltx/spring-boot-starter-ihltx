package com.ihltx.utility.es.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "ihltx.elasticsearch")
public class ElasticSearchConfig {
	private Boolean enable = true;
	private String clusterName="elasticsearch";
	private String clusterNodes ="";
	private String clusterNodeSchema = "http";
	private Integer connectTimeout = 60;
	private Integer socketTimeout = 60;
	private Integer connectionRequestTimeout = 60;

}
