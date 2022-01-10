package com.ihltx.utility.es.config;

import java.util.List;

import lombok.Data;

@Data
public class ElasticSearchIndexMappings {

	private List<ElasticSearchIndexProperty> properties;
	
	
}
