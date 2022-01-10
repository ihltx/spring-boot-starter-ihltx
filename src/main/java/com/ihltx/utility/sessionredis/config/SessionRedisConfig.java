package com.ihltx.utility.sessionredis.config;


import com.ihltx.utility.jwt.config.JwtConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ihltx.sessionredis")
public class SessionRedisConfig {

    /**
     * session redis 使用的 redis服务器配置名称
     */
    private String redisServerName = "default";

    /**
     * session 过期时间，单位：秒
     */
    private int maxInactiveIntervalInSeconds = 1800;

    /**
     * redis 中 session名称空间，即前缀
     */
    private String redisNamespace = "spring:session";

    /**
     * session数据向redis刷新模式
     *  ON_SAVE    --  保存时刷新
     *  IMMEDIATE  --  立即刷新
     */
    private String flushMode = "ON_SAVE";

    /**
     * 定时清除过期session的计划任务设置
     */
    private String cleanupCron = "0 * * * * *";

    /**
     * session向redis保存数据的保存模式
     *    ON_SET_ATTRIBUTE  --
     *    ON_GET_ATTRIBUTE  --
     *    ALWAYS            --
     */
    private String saveMode = "ON_SET_ATTRIBUTE";
}
