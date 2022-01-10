package com.ihltx.utility.es.config;

import lombok.Data;

@Data
public class ElasticSearchIndexProperty {
	/**
	 * 索引字段名称
	 */
	private String name;
	
	/**
	 * 索引字段类型
	 * byte|short|integer|long|float|double|boolean|binary|text|date|keyword
	 * text将分词其它不分词
	 */
	private String type = "text";
	
	/**
	 * 设置字段是否存储，false只能搜索不存储值，无法获取值,true可搜索也可获取值，默认为false
	 */
	private Boolean store = false;
	
	/**
	 * 是否索引   true--是  false--否
	 */
	private Boolean index = true;

	/**
	 * 指定分词器，默认为standard(仅能对英文分词，汉字分为一个个汉字), ik_smart     ik_max_word--最大程度分词
	 */
	private String analyzer = "standard";

	
}
