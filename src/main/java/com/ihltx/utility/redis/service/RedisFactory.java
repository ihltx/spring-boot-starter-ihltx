package com.ihltx.utility.redis.service;

import com.ihltx.utility.redis.config.RedisConfig;
import com.ihltx.utility.redis.exception.RedisException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

public interface RedisFactory{

	/**
	 * 获取 RedisConfig 配置
	 * @return
	 */
	RedisConfig getRedisConfig();

	/**
	 * 设置 RedisConfig 配置
	 * @param redisConfig
	 */
	void setRedisConfig(RedisConfig redisConfig);


	/**
	 * 使用默认default及默认数据库编号打开RedisSession会话
	 * 

	 * @throws RedisException 
	 */
	RedisUtil openSession() throws RedisException;
	

	
	/**
	 * 打开新的RedisSession会话
	 * 
	 * @param redisName
	 * @return RedisUtil  null--失败
	 * @throws RedisException 
	 * @throws Exception 
	 */
	RedisUtil openSession(String redisName) throws RedisException;
	
	

	/**
	 * 关闭RedisSession会话
	 * 
	 * @param redisName
	 * @return true--成功 false--失败
	 */
	Boolean closeSession(String redisName);


	/**
	 * 使用默认default及默认数据库编号打开RedisSession会话
	 *

	 * @throws RedisException
	 * @throws RedisException
	 */
	StringRedisUtil openSessionWithString() throws RedisException;



	/**
	 * 打开新的RedisSession会话
	 *
	 * @param redisName
	 * @return RedisUtil  null--失败
	 * @throws RedisException
	 */
	StringRedisUtil openSessionWithString(String redisName) throws RedisException;



	/**
	 * 关闭RedisSession会话
	 *
	 * @param redisName
	 * @return true--成功 false--失败
	 * @throws RedisException
	 */
	Boolean closeSessionWithString(String redisName) throws RedisException;


	/**
	 * 基于指定 redisName 		获取 RedisTemplate
	 * @param redisName			redisName
	 * @return RedisTemplate<String, Object>
	 * @throws RedisException
	 */
	RedisTemplate<String, Object> redisTemplate(String redisName) throws RedisException;


	/**
	 * 基于指定 redisName 		获取 RedisTemplate
	 * @param redisName			redisName
	 * @return RedisTemplate<String, String>
	 * @throws RedisException
	 */
	StringRedisTemplate stringRedisTemplate(String redisName) throws RedisException;

	/**
	 * 基于指定 redisName 		获取 RedisConnectionFactory
	 * @param redisName			redisName
	 * @return RedisConnectionFactory
	 * @throws RedisException
	 */
	RedisConnectionFactory redisConnectionFactory(String redisName) throws RedisException;


}
