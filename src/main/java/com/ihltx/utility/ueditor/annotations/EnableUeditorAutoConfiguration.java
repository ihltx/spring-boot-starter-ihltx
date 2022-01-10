package com.ihltx.utility.ueditor.annotations;

import com.ihltx.utility.ueditor.UeditorAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ImportAutoConfiguration({UeditorAutoConfiguration.class})
public @interface EnableUeditorAutoConfiguration {
}
