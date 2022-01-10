package com.ihltx.utility.es.config;

import lombok.Data;

@Data
public class ElasticSearchIndexSettings {
	/**
	 * 索引分片数
	 */
	private int number_of_shards = 1;
	
	/**
	 * 索引副本数
	 */
	private int number_of_replicas = 1;
	
}
