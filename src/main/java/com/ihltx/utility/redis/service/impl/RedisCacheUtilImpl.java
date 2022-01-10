package com.ihltx.utility.redis.service.impl;

import java.util.Set;

import com.ihltx.utility.redis.service.RedisCacheUtil;
import com.ihltx.utility.redis.service.RedisFactory;
import com.ihltx.utility.redis.service.RedisUtil;
import com.ihltx.utility.util.SecurityUtil;
import com.ihltx.utility.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;

public class RedisCacheUtilImpl implements RedisCacheUtil {

	private static final String SHOP_TABLENAMESET_SUFFIX = "__all_tables__";

	private String redisCacheKeyPrefixSetKeyPrefix;

	
	public String getRedisCacheKeyPrefixSetKeyPrefix() {
		return redisCacheKeyPrefixSetKeyPrefix;
	}

	public void setRedisCacheKeyPrefixSetKeyPrefix(String redisCacheKeyPrefixSetKeyPrefix) {
		this.redisCacheKeyPrefixSetKeyPrefix = redisCacheKeyPrefixSetKeyPrefix;
	}

	private String redisCacheKeyPrefix;

	public String getRedisCacheKeyPrefix() {
		return redisCacheKeyPrefix;
	}

	public void setRedisCacheKeyPrefix(String redisCacheKeyPrefix) {
		this.redisCacheKeyPrefix = redisCacheKeyPrefix;
	}

	private RedisFactory redisFactory;
	private String redisName;

	public RedisFactory getRedisFactory() {
		return redisFactory;
	}

	public void setRedisFactory(RedisFactory redisFactory) {
		this.redisFactory = redisFactory;
	}

	public String getRedisName() {
		return redisName;
	}

	public void setRedisName(String redisName) {
		this.redisName = redisName;
	}

	/**
	 * 设置缓存(永不过期)
	 *
	 * @param shopId     	当前店铺ID
	 * @param tableName 	表名称
	 * @param cacheKeys		缓存key后缀对象数组
	 * @param value  		缓存值
	 */
	public void setCache(Long shopId , String tableName, Object[] cacheKeys, Object value) {
		setCache(shopId, tableName , cacheKeys, value, 0);
	}

	/**
	 * 设置缓存
	 *
	 * @param shopId     	当前店铺ID
	 * @param tableName 	表名称
	 * @param cacheKeys		缓存key后缀对象数组
	 * @param value  		缓存值
	 * @param time   		过期时间(秒) 如果time小于等于0 将设置为永不过期
	 */
	public void setCache(Long shopId , String tableName, Object[] cacheKeys, Object value, long time) {
		if(cacheKeys==null) return;
		String keyPrefix = null;
		String setKeyPrefix = null;
		if (Strings.isNullOrEmpty(redisCacheKeyPrefix)) {
			keyPrefix = "";
		} else {
			keyPrefix = redisCacheKeyPrefix + ":";
		}
		if (Strings.isNullOrEmpty(redisCacheKeyPrefixSetKeyPrefix)) {
			setKeyPrefix = "";
		} else {
			setKeyPrefix = redisCacheKeyPrefixSetKeyPrefix + ":";
		}

		if (Strings.isNullOrEmpty(redisName))
			return;
		try {
			RedisUtil redisUtil = redisFactory.openSession(redisName);
			if (redisUtil == null)
				return;
			String key = keyPrefix + shopId + ":" + tableName + ":" + SecurityUtil.md5(JSON.toJSONString(cacheKeys));
			String shopSetKey = setKeyPrefix + shopId;
			String shopTableSetKey = setKeyPrefix + shopId + ":" + tableName;
			String shopAllTablesSetKey =  setKeyPrefix + shopId + ":" + SHOP_TABLENAMESET_SUFFIX;
			if(value!=null) {
				redisUtil.set(key, value, time);
				redisUtil.sSet(shopSetKey, key);
				redisUtil.sSet(shopTableSetKey, key);
				redisUtil.sSet(shopAllTablesSetKey , shopTableSetKey);
			}else {
				redisUtil.remove(key);
				redisUtil.sRemove(shopSetKey, key);
				redisUtil.sRemove(shopTableSetKey, key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 获取字符串类型缓存
	 *
	 * @param shopId     	当前店铺ID
	 * @param tableName 	表名称
	 * @param cacheKeys		缓存key后缀对象数组
	 * @return String null--表示不存在
	 */
	public String getCache(Long shopId , String tableName, Object[] cacheKeys) {
		if (Strings.isNullOrEmpty(redisName))
			return null;
		if(cacheKeys ==null) return null;
		String keyPrefix = null;
		if (Strings.isNullOrEmpty(redisCacheKeyPrefix)) {
			keyPrefix = "";
		} else {
			keyPrefix = redisCacheKeyPrefix + ":";
		}
		
		try {
			RedisUtil redisUtil = redisFactory.openSession(redisName);
			if (redisUtil == null)
				return null;
			String key = keyPrefix + shopId +":" + tableName + ":" + SecurityUtil.md5(JSON.toJSONString(cacheKeys));
			return redisUtil.get(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
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
	public <T> T getCache(Long shopId , String tableName, Object[] cacheKeys, Class<T> clazz) {
		if (Strings.isNullOrEmpty(redisName))
			return null;
		
		String keyPrefix = null;
		if (Strings.isNullOrEmpty(redisCacheKeyPrefix)) {
			keyPrefix = "";
		} else {
			keyPrefix = redisCacheKeyPrefix + ":";
		}
		
		try {
			RedisUtil redisUtil = redisFactory.openSession(redisName);
			if (redisUtil == null)
				return null;
			String key = keyPrefix + shopId +":" + tableName + ":" + SecurityUtil.md5(JSON.toJSONString(cacheKeys));
			return redisUtil.get(key, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	/**
	 * 清除db中所有缓存
	 */
	public void clearCache() {
		try {
			RedisUtil redisUtil = redisFactory.openSession(redisName);
			if (redisUtil == null)
				return;
			redisUtil.flushDb();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 清除指定shopId所有缓存
	 * @param shopId    shopId
	 */
	public void clearCache(Long shopId) {
		clearCache(shopId , null , null);
	}
	
	/**
	 * 清除指定shopId下指定tableName的缓存
	 * @param shopId    	shopId
	 * @param tableName 	表名称
	 */
	public void clearCache(Long shopId, String tableName) {
		clearCache(shopId, tableName , null);
	}

	/**
	 * 清除指定shopId下指定tableName下指定keys的缓存
	 * @param shopId    	shopId
	 * @param tableName 	表名称
	 * @param cacheKeys  	缓存key后缀对象数组
	 */
	public void clearCache(Long shopId, String tableName, Object[] cacheKeys) {
		String cacheKey = null;
		if(cacheKeys!=null){
			cacheKey = SecurityUtil.md5(JSON.toJSONString(cacheKeys));
		}
		clearCacheByKey(shopId , tableName , cacheKey);
	}

	/**
	 * 清除指定shopId下指定tableName下的指定key缓存
	 * @param shopId    	shopId
	 * @param tableName 	表名称
	 * @param cacheKey 		缓存key
	 */
	public void clearCacheByKey(Long shopId, String tableName, String cacheKey) {
		if (Strings.isNullOrEmpty(redisName))
			return;
		String keyPrefix = null;
		String setKeyPrefix = null;
		if (Strings.isNullOrEmpty(redisCacheKeyPrefix)) {
			keyPrefix = "";
		} else {
			keyPrefix = redisCacheKeyPrefix + ":";
		}
		if (Strings.isNullOrEmpty(redisCacheKeyPrefixSetKeyPrefix)) {
			setKeyPrefix = "";
		} else {
			setKeyPrefix = redisCacheKeyPrefixSetKeyPrefix + ":";
		}

		try {
			RedisUtil redisUtil = redisFactory.openSession(redisName);
			if (redisUtil == null)
				return;
			if(shopId==null){
				//清除db所有缓存
				clearCache();
				return;
			}
			String shopSetKey = setKeyPrefix + shopId;
			String shopTableSetKey = setKeyPrefix + shopId + ":" + tableName;
			String shopAllTablesSetKey = setKeyPrefix + shopId + ":" + SHOP_TABLENAMESET_SUFFIX;
			if(StringUtil.isNullOrEmpty(cacheKey)) {
				if(StringUtil.isNullOrEmpty(tableName)){
					//清除shopId的所有缓存
					Set<String> allTablesSetKeys = redisUtil.sGet(shopAllTablesSetKey);
					if(allTablesSetKeys!=null && allTablesSetKeys.size()>0){
						for(String key : allTablesSetKeys) {
							redisUtil.remove(key);
						}
					}
					redisUtil.remove(shopAllTablesSetKey);
					Set<String> allSetKeys = redisUtil.sGet(shopSetKey);
					if(allSetKeys!=null && allSetKeys.size()>0){
						for(String key : allSetKeys) {
							redisUtil.remove(key);
						}
					}
					redisUtil.remove(shopSetKey);
				}else{
					//清除shopId下tableName的所有缓存
					Set<String> tableSetKeys = redisUtil.sGet(shopTableSetKey);
					if(tableSetKeys!=null && tableSetKeys.size()>0){
						for(String key : tableSetKeys) {
							redisUtil.remove(key);
							redisUtil.sRemove(shopSetKey , key);
							redisUtil.sRemove(shopTableSetKey , key);
						}
					}
					if(redisUtil.sSize(shopTableSetKey)<=0){
						redisUtil.remove(shopTableSetKey);
						redisUtil.sRemove(shopAllTablesSetKey, shopTableSetKey);
					}
					if(redisUtil.sSize(shopAllTablesSetKey)<=0){
						redisUtil.remove(shopAllTablesSetKey);
					}
					if(redisUtil.sSize(shopSetKey)<=0){
						redisUtil.remove(shopSetKey);
					}
				}
			}else {
				String key = keyPrefix + shopId + ":" + tableName + ":" + cacheKey;
				redisUtil.remove(key);
				redisUtil.sRemove(shopSetKey, key);
				redisUtil.sRemove(shopTableSetKey, key);
				if(redisUtil.sSize(shopTableSetKey)<=0){
					redisUtil.remove(shopTableSetKey);
					redisUtil.sRemove(shopAllTablesSetKey, shopTableSetKey);
				}
				if(redisUtil.sSize(shopAllTablesSetKey)<=0){
					redisUtil.remove(shopAllTablesSetKey);
				}
				if(redisUtil.sSize(shopSetKey)<=0){
					redisUtil.remove(shopSetKey);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


}
