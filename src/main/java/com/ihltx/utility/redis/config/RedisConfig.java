package com.ihltx.utility.redis.config;


import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "ihltx.redis")
public class RedisConfig {

    /**
     * 指定是否使用默认的{primary}redis服务器配置名称对应的redis服务器配置替代springboot默认RedisConnectionFactory。
     * 如果设置为false则不替代，替代之后RedisTemplate及StringRedisTemplate将使用替代之后的RedisConnectionFactory来创建
     */
    private Boolean isSubstituteSpringBootRedisConnectionFactory = false;
    private String primary = "default";
    private String redisCacheKeyPrefix = "rediscache";
    private String redisCacheKeyPrefixSetKeyPrefix = "rediscacheprefix";
    private String redisCacheUtilRedisName = "default";


    private Map<String, RedisServerConfig> server = new HashMap<String, RedisServerConfig>();

}
