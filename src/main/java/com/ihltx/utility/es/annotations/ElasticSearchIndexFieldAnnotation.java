package com.ihltx.utility.es.annotations;
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.FIELD })
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface ElasticSearchIndexFieldAnnotation {
	/**
	 * 索引字段名称
	 */
	String name() default "";
	
	/**
	 * 索引字段类型
	 * byte|short|integer|long|float|double|boolean|binary|text|date|keyword
	 * text将分词其它不分词
	 */
	String type() default "text";
	
	/**
	 * 设置字段是否存储，false只能搜索不存储值，无法获取值,true可搜索也可获取值，默认为false
	 */
	boolean store() default false;
	
	/**
	 * 是否索引   true--是  false--否
	 */
	boolean index() default true;

	/**
	 * 指定分词器，默认为standard(仅能对英文分词，汉字分为一个个汉字), ik_smart     ik_max_word--最大程度分词
	 */
	String analyzer() default "";
	
}
