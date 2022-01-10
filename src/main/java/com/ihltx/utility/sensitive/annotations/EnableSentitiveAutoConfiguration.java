package com.ihltx.utility.sensitive.annotations;

import com.ihltx.utility.sensitive.SensitiveAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ImportAutoConfiguration({SensitiveAutoConfiguration.class})
public @interface EnableSentitiveAutoConfiguration {
}
