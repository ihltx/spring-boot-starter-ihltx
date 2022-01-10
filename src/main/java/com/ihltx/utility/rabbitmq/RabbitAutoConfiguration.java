package com.ihltx.utility.rabbitmq;

import com.ihltx.utility.rabbitmq.service.RabbitUtil;
import com.ihltx.utility.rabbitmq.service.impl.RabbitUtilImpl;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ComponentScan(basePackages = {"com.ihltx.utility.rabbitmq"})
public class RabbitAutoConfiguration {

    @Autowired
    ApplicationContext applicationContext;

    @Bean(name = "rabbitUtil")
    @Primary
    @ConditionalOnMissingBean(RabbitUtil.class)
    public RabbitUtil getRabbitUtil() {
        RabbitUtil rabbitUtil = null;
        try{
            RabbitTemplate rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
            if(rabbitTemplate!=null){
                rabbitUtil = new RabbitUtilImpl();
                rabbitUtil.setRabbitTemplate(rabbitTemplate);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return rabbitUtil;
    }

}
