package com.ihltx.utility.apiversion.annotations;

import com.ihltx.utility.apiversion.ApiVersionAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ImportAutoConfiguration({ApiVersionAutoConfiguration.class})
public @interface EnableApiVersionAutoConfiguration {
}
