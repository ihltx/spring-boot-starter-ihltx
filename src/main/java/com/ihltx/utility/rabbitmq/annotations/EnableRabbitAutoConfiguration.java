package com.ihltx.utility.rabbitmq.annotations;

import com.ihltx.utility.rabbitmq.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ImportAutoConfiguration({RabbitAutoConfiguration.class})
public @interface EnableRabbitAutoConfiguration {
}
