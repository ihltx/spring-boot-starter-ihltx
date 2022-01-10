package com.ihltx.utility.redis.annotations;

import com.ihltx.utility.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ImportAutoConfiguration({RedisAutoConfiguration.class})
public @interface EnableRedisAutoConfiguration {
}
