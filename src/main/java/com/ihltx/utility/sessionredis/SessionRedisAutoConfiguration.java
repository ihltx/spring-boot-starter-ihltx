package com.ihltx.utility.sessionredis;

import com.ihltx.utility.redis.RedisAutoConfiguration;
import com.ihltx.utility.redis.exception.RedisException;
import com.ihltx.utility.redis.service.RedisFactory;
import com.ihltx.utility.sessionredis.config.SessionRedisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SuppressWarnings("all")
@Configuration
@EnableConfigurationProperties({SessionRedisConfig.class})
@ComponentScan(basePackages = {"com.ihltx.utility.sessionredis"})
@ImportAutoConfiguration(classes = {RedisAutoConfiguration.class})
public class SessionRedisAutoConfiguration {

    @Autowired
    private SessionRedisConfig sessionRedisConfig;

    @Autowired
    private RedisFactory redisFactory;

    @Bean
    @Primary
    public RedisConnectionFactory sessionRedisConnectionFactory () throws RedisException {
        return redisFactory.redisConnectionFactory(sessionRedisConfig.getRedisServerName());
    }

}
