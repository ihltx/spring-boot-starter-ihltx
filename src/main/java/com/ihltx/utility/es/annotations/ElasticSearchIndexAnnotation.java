package com.ihltx.utility.es.annotations;


@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.TYPE })
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface ElasticSearchIndexAnnotation {
	/**
	 * 索引名称
	 * @return
	 */
	String name() default "";
	
	/**
	 * 索引分片数
	 */
	int number_of_shards() default 1;
	
	/**
	 * 索引副本数
	 */
	int number_of_replicas() default 1;

}
