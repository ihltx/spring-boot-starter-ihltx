package com.ihltx.utility.sessionredis.annotations;

import com.ihltx.utility.sessionredis.SessionRedisAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ImportAutoConfiguration({SessionRedisAutoConfiguration.class})
public @interface EnableSessionRedisAutoConfiguration {
}
