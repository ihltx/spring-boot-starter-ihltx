package com.ihltx.utility.urlrewrite.annotations;

import com.ihltx.utility.urlrewrite.UrlRewriteAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ImportAutoConfiguration({UrlRewriteAutoConfiguration.class})
public @interface EnableUrlRewriteAutoConfiguration {
}
