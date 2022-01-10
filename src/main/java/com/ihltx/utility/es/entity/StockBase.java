package com.ihltx.utility.es.entity;


import com.ihltx.utility.es.annotations.ElasticSearchIndexAnnotation;
import com.ihltx.utility.es.annotations.ElasticSearchIndexFieldAnnotation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ElasticSearchIndexAnnotation(number_of_replicas = 1,number_of_shards = 1)
public class StockBase implements Serializable {

	@ElasticSearchIndexFieldAnnotation(name = "stock_id",type = "long")
	long stock_id;
	
	/**
	 * 股票代码
	 */
	@ElasticSearchIndexFieldAnnotation(name = "stock_code",type = "text",analyzer = "standard")
	String stock_code;
	
	/**
	 * 股票名称
	 */
	@ElasticSearchIndexFieldAnnotation(name = "stock_name",type = "text",analyzer = "ik_max_word",index = true)
	String stock_name;
	
	/**
	 * 股票类型 sh--上市  sz--深市
	 */
	@ElasticSearchIndexFieldAnnotation(name = "stock_type",type = "text",analyzer = "ik_max_word",index = true)
	String stock_type;
	
	/**
	 * 总股本，单位：亿股
	 */
	@ElasticSearchIndexFieldAnnotation(name = "stock_total_number",type = "double")
	double stock_total_number;
	
	/**
	 * 流通股，单位：亿股
	 */
	@ElasticSearchIndexFieldAnnotation(name = "stock_floating_number",type = "double")
	double stock_floating_number;
	
	/**
	 * 排序，越大越靠前
	 */
	@ElasticSearchIndexFieldAnnotation(name = "stock_order",type = "integer")
	int stock_order;
	
	/**
	 * 所属行业
	 */
	@ElasticSearchIndexFieldAnnotation(name = "stock_trade",type = "text",analyzer = "ik_max_word",index = true)
	String stock_trade;
	
	/**
	 * 细分行业
	 */
	@ElasticSearchIndexFieldAnnotation(name = "stock_sub_trade",type = "text",analyzer = "ik_max_word",index = true)
	String stock_sub_trade;
	
	/**
	 * 创建时间
	 */
	@ElasticSearchIndexFieldAnnotation(name = "stock_created",type = "long")
	long stock_created;
	
	/**
	 * 修改时间
	 */
	@ElasticSearchIndexFieldAnnotation(name = "stock_updated",type = "long")
	long stock_updated;
	
	/**
	 * 是否删除  0--否  1--是
	 */
	@ElasticSearchIndexFieldAnnotation(name = "stock_deleted",type = "integer")
	int stock_deleted;
	
}

	