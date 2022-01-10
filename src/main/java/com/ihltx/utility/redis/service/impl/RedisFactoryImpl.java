package com.ihltx.utility.redis.service.impl;

import com.ihltx.utility.log.service.LoggerBuilder;
import com.ihltx.utility.redis.config.RedisConfig;
import com.ihltx.utility.redis.config.RedisServerConfig;
import com.ihltx.utility.redis.exception.RedisException;
import com.ihltx.utility.redis.service.RedisFactory;
import com.ihltx.utility.redis.service.RedisUtil;
import com.ihltx.utility.redis.service.StringRedisUtil;
import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RedisFactoryImpl implements RedisFactory {
	private LoggerBuilder loggerBuilder;
	

	/**
	 * RedisUtil列表
	 */
	private static Map<String, RedisUtil> redisUtils = new HashMap<>();

	/**
	 * StringRedisUtil列表
	 */
	private static Map<String, StringRedisUtil> stringRedisUtils = new HashMap<>();
	
	private RedisConfig redisConfig;
	

	public RedisConfig getRedisConfig() {
		return redisConfig;
	}


	public void setRedisConfig(RedisConfig redisConfig) {
		this.redisConfig = redisConfig;
	}



	private static RedisFactoryImpl redisFactory;
	
	/**
	 * 单例模式保存Redis连接工厂，格式： { "redisName" :RedisConnectionFactory, ... }
	 */
	private Map<String, RedisConnectionFactory> redisConnectionFactoryContainer = new HashMap<String, RedisConnectionFactory>();
	

	public static RedisFactory instance(RedisConfig redisConfig, LoggerBuilder loggerBuilder) {
		if(RedisFactoryImpl.redisFactory==null) {
			RedisFactoryImpl.redisFactory = new RedisFactoryImpl();
			RedisFactoryImpl.redisFactory.setRedisConfig(redisConfig);
			RedisFactoryImpl.redisFactory.loggerBuilder = loggerBuilder;
			try {
				RedisFactoryImpl.redisFactory.init();
			} catch (RedisException e) {
				e.printStackTrace();
				RedisFactoryImpl.redisFactory = null;
			}
		}
		return RedisFactoryImpl.redisFactory;
	}
	
	/**
	 * 提前初始化当前需要用到的所有Redis连接工厂
	 * @throws RedisException 
	 */
	private void init() throws RedisException {
		for(String redisName : this.redisConfig.getServer().keySet()) {
			if(this.redisConfig.getServer().get(redisName)!=null) {
				if (!redisConnectionFactoryContainer.containsKey(redisName) || redisConnectionFactoryContainer.get(redisName) == null) {
					redisConnectionFactoryContainer.put(redisName, buildConnectionFactory(redisName));
					loggerBuilder.getLogger(this.getClass().getName()).debug("Init redisConnectionFactory :" + redisName);
				}
			}
		}
	}

	private GenericObjectPoolConfig getGenericObjectPoolConfig(RedisServerConfig redisServerConfig){
		if(redisServerConfig==null) {
			return null;
		}
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		poolConfig.setMaxTotal(redisServerConfig.getMaxActive());
		poolConfig.setMaxIdle(redisServerConfig.getMaxIdle());
		poolConfig.setMinIdle(redisServerConfig.getMinIdle());
		poolConfig.setMaxWaitMillis(redisServerConfig.getMaxWait());
		poolConfig.setTestOnBorrow(redisServerConfig.getTestOnBorrow());
		poolConfig.setMinEvictableIdleTimeMillis(redisServerConfig.getMinEvictableIdleTimeMillis());
		poolConfig.setTimeBetweenEvictionRunsMillis(redisServerConfig.getTimeBetweenEvictionRunsMillis());
		poolConfig.setNumTestsPerEvictionRun(redisServerConfig.getNumTestsPerEvictionRun());
		poolConfig.setLifo(redisServerConfig.getLifo());
		poolConfig.setFairness(redisServerConfig.getFairness());
		poolConfig.setSoftMinEvictableIdleTimeMillis(redisServerConfig.getSoftMinEvictableIdleTimeMillis());
		poolConfig.setEvictorShutdownTimeoutMillis(redisServerConfig.getEvictorShutdownTimeoutMillis());
		poolConfig.setTestOnCreate(redisServerConfig.getTestOnCreate());
		poolConfig.setTestOnReturn(redisServerConfig.getTestOnReturn());
		poolConfig.setTestWhileIdle(redisServerConfig.getTestWhileIdle());
		poolConfig.setBlockWhenExhausted(redisServerConfig.getBlockWhenExhausted());
		poolConfig.setJmxEnabled(redisServerConfig.getJmxEnabled());
		poolConfig.setJmxNameBase(redisServerConfig.getJmxNameBase());
		poolConfig.setJmxNamePrefix(redisServerConfig.getJmxNamePrefix());
		return poolConfig;
	}

	@Override
	public RedisTemplate<String, Object> redisTemplate(String redisName) throws RedisException{
		return buildRedisTemplate(redisConnectionFactory(redisName));
	}


	@Override
	public StringRedisTemplate stringRedisTemplate(String redisName) throws RedisException{
		return buildStringRedisTemplate(redisConnectionFactory(redisName));
	}

	@Override
	public RedisConnectionFactory redisConnectionFactory(String redisName) throws RedisException{
		if(this.redisConfig.getServer().isEmpty()) return null;
		RedisConnectionFactory redisConnectionFactory = null;
		if(this.redisConnectionFactoryContainer.containsKey(redisName) && this.redisConnectionFactoryContainer.get(redisName)!=null) {
			redisConnectionFactory=  this.redisConnectionFactoryContainer.get(redisName);
		}else {
			redisConnectionFactory= buildConnectionFactory(redisName);
			this.redisConnectionFactoryContainer.put(redisName, redisConnectionFactory);
		}
		return redisConnectionFactory;
	}

	private RedisConnectionFactory buildConnectionFactory(String redisName) throws RedisException {
		if(Strings.isEmpty(redisName) || !redisConfig.getServer().containsKey(redisName)){
			redisName = redisConfig.getPrimary();
		}
		RedisServerConfig redisServerConfig = redisConfig.getServer().get(redisName);
		
		if(redisServerConfig==null) {
			throw new RedisException("[" + redisName + "] Redis config not found."); 
		}
		GenericObjectPoolConfig poolConfig = getGenericObjectPoolConfig(redisServerConfig);
		JedisClientConfiguration clientConfig = JedisClientConfiguration.builder().usePooling().poolConfig(poolConfig)
				.and().readTimeout(Duration.ofMillis(redisServerConfig.getTimeout())).build();
//		LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
//				.commandTimeout(Duration.ofMillis(redisServerConfig.getTimeout()))
//				.shutdownTimeout(Duration.ofMillis(redisServerConfig.getShutDownTimeout()))
//				.poolConfig(poolConfig)
//				.build();
		Set<RedisNode> redisNodeSet = null;
		switch (redisServerConfig.getRedisServerMode()){
			case 1:
				//集群模式 cluster
				RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration();
				if(redisServerConfig.getCluster().getMaxRedirects()<=0){
					redisServerConfig.getCluster().setMaxRedirects(1);
				}
				clusterConfig.setMaxRedirects(redisServerConfig.getCluster().getMaxRedirects());
				redisNodeSet = new HashSet<>();
				for(String node : redisServerConfig.getCluster().getNodes()){
					String[] nodes = node.split(":");
					redisNodeSet.add(new RedisNode(nodes[0] , Integer.valueOf(nodes[1])));
				}
				clusterConfig.setClusterNodes(redisNodeSet);
				clusterConfig.setPassword(RedisPassword.of(redisServerConfig.getPassword()));
				//lettuce
//				return new LettuceConnectionFactory(clusterConfig , clientConfig);
				return new JedisConnectionFactory(clusterConfig, clientConfig);
			case 2:
				//哨兵模式 sentinel
				RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration();
				sentinelConfig.master(redisServerConfig.getSentinel().getMaster());
				redisNodeSet = new HashSet<>();
				for(String node : redisServerConfig.getSentinel().getNodes()){
					String[] nodes = node.split(":");
					redisNodeSet.add(new RedisNode(nodes[0] , Integer.valueOf(nodes[1])));
				}
				sentinelConfig.setSentinels(redisNodeSet);
				sentinelConfig.setPassword(RedisPassword.of(redisServerConfig.getPassword()));
				sentinelConfig.setDatabase(redisServerConfig.getDatabase());
//				return new LettuceConnectionFactory(sentinelConfig , clientConfig);
				return new JedisConnectionFactory(sentinelConfig, clientConfig);
			default:
				//单点或主从模式
				RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration();
				standaloneConfig.setHostName(redisServerConfig.getHost());
				standaloneConfig.setPassword(RedisPassword.of(redisServerConfig.getPassword()));
				standaloneConfig.setPort(redisServerConfig.getPort());
				standaloneConfig.setDatabase(redisServerConfig.getDatabase());
//				return new LettuceConnectionFactory(standaloneConfig , clientConfig);
				return new JedisConnectionFactory(standaloneConfig, clientConfig);
		}
	}
	

	protected RedisTemplate<String, Object> buildRedisTemplate(RedisConnectionFactory connectionFactory) {
//		GenericJackson2JsonRedisSerializer serializationRedisSerializer = new GenericJackson2JsonRedisSerializer();
//		JdkSerializationRedisSerializer serializationRedisSerializer = new JdkSerializationRedisSerializer();
		GenericFastJsonRedisSerializer serializationRedisSerializer = new GenericFastJsonRedisSerializer();
		StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
		RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
		template.setConnectionFactory(connectionFactory);
		template.afterPropertiesSet();
		template.setKeySerializer(stringRedisSerializer);
		template.setValueSerializer(serializationRedisSerializer);
		template.setHashKeySerializer(stringRedisSerializer);
		template.setHashValueSerializer(serializationRedisSerializer);
		template.setDefaultSerializer(serializationRedisSerializer);
		template.setEnableDefaultSerializer(true);
		template.afterPropertiesSet();
		return template;
	}


	protected StringRedisTemplate buildStringRedisTemplate(RedisConnectionFactory connectionFactory) {
		StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

		StringRedisTemplate template = new StringRedisTemplate();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(stringRedisSerializer);
		template.setValueSerializer(stringRedisSerializer);
		template.setHashKeySerializer(stringRedisSerializer);
		template.setHashValueSerializer(stringRedisSerializer);
		template.setDefaultSerializer(stringRedisSerializer);
		template.setEnableDefaultSerializer(true);
		template.afterPropertiesSet();
		return template;
	}
	
	
	/**
	 * 使用默认primary及默认数据库编号打开RedisSession会话
	 * 

	 * @throws RedisException 
	 * @throws Exception 
	 */
	public RedisUtil openSession() throws RedisException {
		return openSession(redisConfig.getPrimary());
	}
	

	
	/**
	 * 打开新的RedisSession会话
	 * 
	 * @param redisName
	 * @return RedisUtil  null--失败
	 * @throws RedisException 
	 * @throws Exception 
	 */
	public RedisUtil openSession(String redisName) throws RedisException {
		if(this.redisConfig.getServer().isEmpty()) return null;
		if(redisUtils.containsKey(redisName) && redisUtils.get(redisName)!=null) {
			return redisUtils.get(redisName);
		}
		RedisUtilImpl stringRedisUtil = new RedisUtilImpl();
		RedisConnectionFactory redisConnectionFactory = null;
		if(this.redisConnectionFactoryContainer.containsKey(redisName) && this.redisConnectionFactoryContainer.get(redisName)!=null) {
			redisConnectionFactory=  this.redisConnectionFactoryContainer.get(redisName);
		}else {
			redisConnectionFactory= buildConnectionFactory(redisName);
			this.redisConnectionFactoryContainer.put(redisName, redisConnectionFactory);
			loggerBuilder.getLogger(this.getClass().getName()).debug("openSession redisConnectionFactory :" + redisName);
		}
		RedisTemplate stringRedisTemplate =  buildRedisTemplate(redisConnectionFactory);
		stringRedisUtil.setCurrRedisTemplate(stringRedisTemplate);
		redisUtils.put(redisName, stringRedisUtil);
		return stringRedisUtil;
	}
	
	

	/**
	 * 关闭RedisSession会话
	 * 
	 * @param redisName
	 * @return true--成功 false--失败
	 * @throws Exception 
	 */
	public Boolean closeSession(String redisName) {
		if(redisUtils.containsKey(redisName)) {
			redisUtils.remove(redisName);
		}
		return true;
	}






	/**
	 * 使用默认default及默认数据库编号打开RedisSession会话
	 *

	 * @throws RedisException
	 * @throws Exception
	 */
	public StringRedisUtil openSessionWithString() throws RedisException {
		return openSessionWithString(redisConfig.getPrimary());
	}



	/**
	 * 打开新的RedisSession会话
	 *
	 * @param redisName
	 * @return RedisUtil  null--失败
	 * @throws RedisException
	 * @throws Exception
	 */
	public StringRedisUtil openSessionWithString(String redisName) throws RedisException {
		if(this.redisConfig.getServer().isEmpty()) return null;
		if(stringRedisUtils.containsKey(redisName) && stringRedisUtils.get(redisName)!=null) {
			return stringRedisUtils.get(redisName);
		}
		StringRedisUtil stringRedisUtil = new StringRedisUtilImpl();
		RedisConnectionFactory redisConnectionFactory = null;
		if(this.redisConnectionFactoryContainer.containsKey(redisName) && this.redisConnectionFactoryContainer.get(redisName)!=null) {
			redisConnectionFactory=  this.redisConnectionFactoryContainer.get(redisName);
		}else {
			redisConnectionFactory= buildConnectionFactory(redisName);
			this.redisConnectionFactoryContainer.put(redisName, redisConnectionFactory);
			loggerBuilder.getLogger(this.getClass().getName()).debug("openSession redisConnectionFactory :" + redisName);
		}
		StringRedisTemplate stringRedisTemplate = buildStringRedisTemplate(redisConnectionFactory);
		stringRedisUtil.setCurrStringRedisTemplate(stringRedisTemplate);
		stringRedisUtils.put(redisName, stringRedisUtil);
		return stringRedisUtil;
	}



	/**
	 * 关闭RedisSession会话
	 *
	 * @param redisName
	 * @return true--成功 false--失败
	 * @throws Exception
	 */
	public Boolean closeSessionWithString(String redisName) {
		if(stringRedisUtils.containsKey(redisName)) {
			stringRedisUtils.remove(redisName);
		}
		return true;
	}
}
