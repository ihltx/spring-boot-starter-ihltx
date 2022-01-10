package com.ihltx.utility.sessionredis.config;

import com.ihltx.utility.redis.exception.RedisException;
import com.ihltx.utility.redis.service.RedisFactory;
import com.ihltx.utility.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.session.FlushMode;
import org.springframework.session.SaveMode;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;

@SuppressWarnings("all")
@Configuration
public class SessionRedisConfiguration extends RedisHttpSessionConfiguration {

    @Autowired
    private SessionRedisConfig sessionRedisConfig;

    public SessionRedisConfiguration(){
        super();
        if(!StringUtil.isNullOrEmpty(sessionRedisConfig.getRedisNamespace())){
            super.setRedisNamespace(sessionRedisConfig.getRedisNamespace());
        }
        super.setMaxInactiveIntervalInSeconds(sessionRedisConfig.getMaxInactiveIntervalInSeconds());
        if(sessionRedisConfig.getFlushMode().equalsIgnoreCase("ON_SAVE")){
            super.setFlushMode(FlushMode.ON_SAVE);
        }else{
            super.setFlushMode(FlushMode.IMMEDIATE);
        }
        if(!StringUtil.isNullOrEmpty(sessionRedisConfig.getCleanupCron())){
            super.setCleanupCron(sessionRedisConfig.getCleanupCron());
        }

        if(sessionRedisConfig.getSaveMode().equalsIgnoreCase("ON_SET_ATTRIBUTE")){
            super.setSaveMode(SaveMode.ON_SET_ATTRIBUTE);
        }else if(sessionRedisConfig.getSaveMode().equalsIgnoreCase("ON_GET_ATTRIBUTE")){
            super.setSaveMode(SaveMode.ON_GET_ATTRIBUTE);
        }else{
            super.setSaveMode(SaveMode.ALWAYS);
        }
    }

}
