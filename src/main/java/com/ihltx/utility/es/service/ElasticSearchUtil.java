package com.ihltx.utility.es.service;

import java.util.List;
import java.util.Map;

import com.ihltx.utility.es.config.ElasticSearchConfig;
import com.ihltx.utility.es.config.ElasticSearchIndexMappings;
import com.ihltx.utility.es.config.ElasticSearchIndexSettings;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public interface ElasticSearchUtil {

	/**
	 * 获取 ElasticSearchConfig 配置
	 * @return
	 */
	ElasticSearchConfig getElasticSearchConfig();

	/**
	 * 设置 ElasticSearchConfig 配置
	 * @param elasticSearchConfig
	 */
	void setElasticSearchConfig(ElasticSearchConfig elasticSearchConfig);

	/**
	 * 获取 ElasticSearch RestHighLevelClient操作客户端
	 * @return
	 */
	RestHighLevelClient getRestHighLevelClient();

	/**
	 * 设置 ElasticSearch RestHighLevelClient操作客户端
	 * @return
	 */
	void setRestHighLevelClient(RestHighLevelClient restHighLevelClient);


	/**
	 * 使用默认选项创建索引，分片为1，副本为1
	 * 
	 * @param indexName 索引名称
	 * @return true--成功 false--失败
	 */
	Boolean createIndex(String indexName) ;

	/**
	 * 创建索引，并指定settings以及mappings
	 * 
	 * 
	 * @param indexName 索引名称
	 * @param settings  索引配置json { "index": {
	 *                  "number_of_shards":1,"number_of_replicas":1} }
	 * @param mappings  索引映射json { "properties": { "字段名": { "type":
	 *                  "byte|short|integer|long|float|double|boolean|binary|text|date|keyword",
	 *                  --text将分词其它不分词 "store": "true|false",
	 *                  --设置字段是否存储，false只能搜索不存储值，无法获取值,true可搜索也可获取值，默认为false
	 *                  "index": "true|false", --是否索引 true--是 false--否 "analyzer":
	 *                  "ik", --指定分词器，默认为standard
	 *                  analyzer(仅能对英文分词，汉字分为一个个汉字),ik_max_word--最大程度分词 } } }
	 * @return true--成功 false--失败
	 */

	Boolean createIndex(String indexName, String settings, String mappings);

	/**
	 * 创建索引，并指定settings以及mappings
	 * 
	 * 
	 * @param indexName 索引名称
	 * @param settings  索引配置
	 * @param mappings  索引映射
	 * @return true--成功 false--失败
	 */

	Boolean createIndex(String indexName, ElasticSearchIndexSettings settings,
                        ElasticSearchIndexMappings mappings);

	/**
	 * 根据实体类类型创建索引，需要在实体类上使用ElasticSearchIndexAnnotation注解指定settings，在实体类索引字段上使用ElasticSearchIndexFieldAnnotation指定索引字段
	 * 
	 * 
	 * @param clazz 实体类型
	 * @return true--成功 false--失败
	 */

	Boolean createIndex(Class<?> clazz);
	

	/**
	 * 根据实体类型获取索引名称
	 * 
	 * @param clazz 实体类型
	 * @return String 索引名称
	 */
	String getIndexNameByClass(Class<?> clazz);

	/**
	 * 检查索引是否存在
	 * 
	 * @param indexName 索引名称
	 * @return true--成功 false--失败
	 */
	Boolean existsIndex(String indexName);

	/**
	 * 删除索引
	 * 
	 * @param indexName 索引名称
	 * @return true--成功 false--失败
	 */
	Boolean deleteIndex(String indexName);
	
	/**
	 * 基于索引名称及实体对象添加索引文档
	 * 
	 * @param indexName 索引名称
	 * @param model 实体对象
	 * @return true--成功 false--失败
	 */
	Boolean insertIndex(String indexName, Object model);

	/**
	 * 基于 实体对象添加索引文档
	 * 
	 * @param model 实体对象
	 * @return true--成功 false--失败
	 */
	Boolean insertIndex(Object model);

	
	/**
	 * 基于索引名称及json文档添加索引文档
	 * 
	 * @param indexName 索引名称
	 * @param json 要添加的文档 json
	 * @return true--成功 false--失败
	 */
	Boolean insertIndex(String indexName, String json);
	

	/**
	 * 基于索引名称以及条件进行查询
	 * 
	 * @param index 索引名称
	 * @param where  条件  Map<String, Object>
	 *   Object Map<String, Object>
	 * 
	 * @return List<Map<String,Object>>
	 */
	List<Map<String,Object>> query(String index, Map<String, Object> where);
	
	/**
	 * 基于索引名称以及sourceBuilder进行查询
	 * 
	 * @param index 索引名称
	 * @param sourceBuilder  sourceBuilder
	 * 
	 * @return List<Map<String,Object>>
	 */
	List<Map<String,Object>> query(String index, SearchSourceBuilder sourceBuilder);
	
	
	/**
	 * 批量添加索引文档
	 * @param indexName  索引名称
	 * @param datas  批量数据 List<T> 可以为：List<Map<String,Object>>
	 * @return  true--成功  false--失败
	 */
	<T> Boolean insertBulk(String indexName, List<T> datas);
	
	
	/**
	 * 批量修改索引文档
	 * @param indexName  索引名称
	 * @param datas  批量数据 List<T> 可以为：List<Map<String,Object>>
	 * @return  true--成功  false--失败
	 */
	<T> Boolean updateBulk(String indexName, List<T> datas);
	
	
	/**
	 * 批量删除文档
	 * @param indexName  索引名称
	 * @param datas  批量数据 List<T> 可以为：List<Map<String,Object>>
	 * @return  true--成功  false--失败
	 */
	<T> Boolean deleteBulk(String indexName, List<T> datas);
	
	
	/**
	 * 批量操作索引文档(可同时包含添加、删除、修改)
	 * @param indexName  索引名称
	 * @param insertDatas  批量添加索引文档数据 List<T> 可以为：List<Map<String,Object>>
	 * @param updateDatas  批量修改索引文档数据 List<T> 可以为：List<Map<String,Object>>
	 * @param deleteDatas  批量删除索引文档数据 List<T> 可以为：List<Map<String,Object>>
	 * @return  true--成功  false--失败
	 */
	<T> Boolean bulk(String indexName, List<T> insertDatas, List<T> updateDatas, List<T> deleteDatas);
	
	/**
	 * 根据id查询索引文档
	 * @param indexName  索引名称
	 * @param id         id值
	 * @return null--不存在
	 */
	Map<String,Object> getById(String indexName, String id);
	
	
	/**
	 * 根据ids数组查询索引文档
	 * @param indexName  索引名称
	 * @param ids        id数组
	 * @return null--不存在
	 */
	public List<Map<String,Object>> getByIds(String indexName, String... ids);
	

}
