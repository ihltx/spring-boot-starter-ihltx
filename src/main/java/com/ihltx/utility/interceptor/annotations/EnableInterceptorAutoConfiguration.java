package com.ihltx.utility.interceptor.annotations;

import com.ihltx.utility.interceptor.InterceptorAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ImportAutoConfiguration({InterceptorAutoConfiguration.class})
public @interface EnableInterceptorAutoConfiguration {
}
