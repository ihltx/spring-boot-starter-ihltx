package com.ihltx.utility.es.annotations;

import com.ihltx.utility.es.ElasticSearchAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ImportAutoConfiguration({ElasticSearchAutoConfiguration.class})
public @interface EnableElasticSearchAutoConfiguration {
}
