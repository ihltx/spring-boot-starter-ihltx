package com.ihltx.utility.log;

import com.ihltx.utility.log.config.LogConfig;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LogConfig.class)
@MapperScan(basePackages = {"com.ihltx.utility.log.mapper"})
@ComponentScan(basePackages = {"com.ihltx.utility.log"})
@AutoConfigureAfter(value = DataSourceAutoConfiguration.class, name = "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration")
public class LoggerBuilderAutoConfiguration {

	@Bean
	@ConditionalOnClass(MybatisPlusInterceptor.class)
	@ConditionalOnMissingBean(MybatisPlusInterceptor.class)
	public MybatisPlusInterceptor mybatisPlusInterceptor() {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
		return interceptor;
	}
}