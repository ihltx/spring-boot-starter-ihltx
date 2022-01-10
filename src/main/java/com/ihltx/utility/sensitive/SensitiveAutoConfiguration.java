package com.ihltx.utility.sensitive;

import com.ihltx.utility.freemarker.FreemarkerAutoConfiguration;
import com.ihltx.utility.redis.RedisAutoConfiguration;
import com.ihltx.utility.sensitive.config.SensitiveConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@SuppressWarnings("all")
@Configuration
@EnableConfigurationProperties(SensitiveConfig.class)
@ComponentScan(basePackages = {"com.ihltx.utility.sensitive"})
@MapperScan(basePackages = {"com.ihltx.utility.sensitive.mapper"})
@ConditionalOnProperty(prefix = "ihltx.sensitive", name = "enable" , havingValue = "true")
@ImportAutoConfiguration(classes = {RedisAutoConfiguration.class, FreemarkerAutoConfiguration.class})
@ImportResource("classpath:spring-aop.xml")
public class SensitiveAutoConfiguration{


}