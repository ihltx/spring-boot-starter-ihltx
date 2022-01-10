package com.ihltx.utility.task;

import com.ihltx.utility.log.LoggerBuilderAutoConfiguration;
import com.ihltx.utility.task.config.TaskConfig;
import com.ihltx.utility.task.service.TaskUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.io.IOException;

@SuppressWarnings("all")
@Configuration
@EnableConfigurationProperties({TaskConfig.class})
@ConditionalOnProperty(prefix = "ihltx.task", name = "enabled" , matchIfMissing = false)
@ConditionalOnClass(value = {TaskUtil.class , PaginationInnerInterceptor.class})
@MapperScan(basePackages = {"com.ihltx.utility.task.mapper"})
@ComponentScan(basePackages = {"com.ihltx.utility.task"})
@ImportAutoConfiguration(classes = {LoggerBuilderAutoConfiguration.class})
public class TaskAutoConfiguration{

    @Bean
    @ConditionalOnClass(MybatisPlusInterceptor.class)
    @ConditionalOnMissingBean(MybatisPlusInterceptor.class)
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Autowired
    private TaskUtil taskUtil;

    @PostConstruct
    public void taskStart() throws IOException {
        taskUtil.start();
    }

}
