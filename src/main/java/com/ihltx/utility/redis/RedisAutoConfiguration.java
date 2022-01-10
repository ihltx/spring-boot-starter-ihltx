package com.ihltx.utility.redis;

import com.ihltx.utility.log.LoggerBuilderAutoConfiguration;
import com.ihltx.utility.log.service.LoggerBuilder;
import com.ihltx.utility.redis.config.RedisConfig;
import com.ihltx.utility.redis.exception.RedisException;
import com.ihltx.utility.redis.service.RedisCacheUtil;
import com.ihltx.utility.redis.service.RedisFactory;
import com.ihltx.utility.redis.service.impl.RedisCacheUtilImpl;
import com.ihltx.utility.redis.service.impl.RedisFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@SuppressWarnings("all")
@Configuration
@EnableConfigurationProperties(RedisConfig.class)
@ConditionalOnProperty(prefix = "ihltx.redis.server.default", name = "host" , matchIfMissing = false)
@ConditionalOnClass(RedisFactory.class)
@ImportAutoConfiguration(classes = {LoggerBuilderAutoConfiguration.class})
public class RedisAutoConfiguration {

	@Autowired
	private RedisConfig redisConfig;
	
	@Autowired
	private LoggerBuilder loggerBuilder;

	@Bean(name = "redisFactory")
	@Primary
	@ConditionalOnMissingBean(RedisFactory.class)
	public RedisFactory getRedisFactory() {
		RedisFactory redisFactory = RedisFactoryImpl.instance(redisConfig,loggerBuilder);
		return redisFactory;
	}
	
	
	@Bean(name = "redisCacheUtil")
	@Primary
	@ConditionalOnMissingBean(RedisCacheUtil.class)
	@ConditionalOnProperty(prefix = "ihltx.redis", name = "redisCacheUtilRedisName" , matchIfMissing = false)
	public RedisCacheUtil RedisCacheUtil() throws RedisException {
		if(redisConfig.getServer()==null || redisConfig.getServer().isEmpty()){
			throw new RedisException("Configuration node {ihltx.redis.service} cannot be empty.");
		}
		RedisFactory redisFactory = RedisFactoryImpl.instance(redisConfig,loggerBuilder);
		RedisCacheUtil redisCacheUtil = new RedisCacheUtilImpl();
		redisCacheUtil.setRedisCacheKeyPrefixSetKeyPrefix(redisConfig.getRedisCacheKeyPrefixSetKeyPrefix());
		redisCacheUtil.setRedisCacheKeyPrefix(redisConfig.getRedisCacheKeyPrefix());
		redisCacheUtil.setRedisFactory(redisFactory);
		redisCacheUtil.setRedisName(redisConfig.getRedisCacheUtilRedisName());
		return redisCacheUtil;
	}

	@Bean
	@ConditionalOnProperty(prefix = "ihltx.redis", name = "isSubstituteSpringBootRedisConnectionFactory" , havingValue = "true")
	public RedisConnectionFactory redisConnectionFactory () throws RedisException {
		return getRedisFactory().redisConnectionFactory(redisConfig.getPrimary());
	}


}