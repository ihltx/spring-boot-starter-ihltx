package com.ihltx.utility.task.annotations;

import com.ihltx.utility.task.TaskAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ImportAutoConfiguration({TaskAutoConfiguration.class})
public @interface EnableTaskAutoConfiguration {
}
