package com.ihltx.utility.redis.service;

public interface RedisCacheUtil {

	/**
	 * 获取 RedisCacheUtil所使用的缓存Key前缀Set数据key前缀
	 * @return
	 */
	String getRedisCacheKeyPrefixSetKeyPrefix();

	/**
	 * 设置 RedisCacheUtil所使用的缓存Key前缀Set数据key前缀
	 * @param redisCacheKeyPrefixSetKeyPrefix
	 */
	void setRedisCacheKeyPrefixSetKeyPrefix(String redisCacheKeyPrefixSetKeyPrefix);

	/**
	 * 获取 RedisCacheUtil所使用的缓存Key前缀
	 * @return
	 */
	String getRedisCacheKeyPrefix();

	/**
	 * 设置 RedisCacheUtil所使用的缓存Key前缀
	 * @param redisCacheKeyPrefix
	 */
	void setRedisCacheKeyPrefix(String redisCacheKeyPrefix);


	/**
	 * 获取 RedisCacheUtil所使用的RedisFactory对象
	 * @return
	 */
	RedisFactory getRedisFactory();

	/**
	 * 设置 RedisCacheUtil所使用的RedisFactory对象
	 * @param redisFactory
	 */
	void setRedisFactory(RedisFactory redisFactory);

	/**
	 * 获取 RedisCacheUtil所使用的Redis配置名称
	 * @return
	 */
	String getRedisName();

	/**
	 * 设置 RedisCacheUtil所使用的Redis配置名称
	 * @param redisName
	 */
	void setRedisName(String redisName);

	/**
	 * 设置缓存(永不过期)
	 *
	 * @param shopId     	当前店铺ID
	 * @param tableName 	表名称
	 * @param cacheKeys		缓存key后缀对象数组
	 * @param value  		缓存值
	 */
	void setCache(Long shopId, String tableName, Object[] cacheKeys, Object value);

	/**
	 * 设置缓存
	 *
	 * @param shopId     	当前店铺ID
	 * @param tableName 	表名称
	 * @param cacheKeys		缓存key后缀对象数组
	 * @param value  		缓存值
	 * @param time   		过期时间(秒) 如果time小于等于0 将设置为永不过期
	 */
	void setCache(Long shopId, String tableName, Object[] cacheKeys, Object value, long time) ;


	/**
	 * 获取字符串类型缓存
	 *
	 * @param shopId     	当前店铺ID
	 * @param tableName 	表名称
	 * @param cacheKeys		缓存key后缀对象数组
	 * @return String null--表示不存在
	 */
	String getCache(Long shopId, String tableName, Object[] cacheKeys);

	/**
	 * 获取泛型类型缓存
	 *
	 * @param <T>    泛型类型
	 * @param shopId     	当前店铺ID
	 * @param tableName 	表名称
	 * @param cacheKeys		缓存key后缀对象数组
	 * @param clazz  		类型，从 redis 获取后的对象直接转换为 T 类型
	 * @return
	 */
	<T> T getCache(Long shopId, String tableName, Object[] cacheKeys, Class<T> clazz);
	
	
	
	/**
	 * 清除所有缓存
	 */
	void clearCache();

	/**
	 * 清除指定shopId所有缓存
	 * @param shopId    shopId
	 */
	void clearCache(Long shopId);

	/**
	 * 清除指定shopId下指定tableName的缓存
	 * @param shopId    	shopId
	 * @param tableName 	表名称
	 */
	void clearCache(Long shopId, String tableName);

	/**
	 * 清除指定shopId下指定tableName下指定keys的缓存
	 * @param shopId    	shopId
	 * @param tableName 	表名称
	 * @param cacheKeys  	缓存key后缀对象数组
	 */
	void clearCache(Long shopId, String tableName, Object[] cacheKeys);

	/**
	 * 清除指定shopId下指定tableName下的指定key缓存
	 * @param shopId    	shopId
	 * @param tableName 	表名称
	 * @param cacheKey 		缓存key
	 */
	void clearCacheByKey(Long shopId, String tableName, String cacheKey) ;


}
