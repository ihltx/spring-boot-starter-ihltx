package com.ihltx.utility.rabbitmq.service;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public interface RabbitUtil {

    RabbitTemplate getRabbitTemplate();

    void setRabbitTemplate(RabbitTemplate rabbitTemplate);

    /**
     * 创建广播(扇形)队列，扇形队列通过扇形交换机投递时不需要路由key，一个扇形交换机可绑定多个扇形队列
     * 当向一个扇形交换机投递消息时，将同时投递到与该扇形交换机绑定的所有扇形队列
     * @param queueName   队列名称
     * @return
     */
    Queue createFanoutQueue(String queueName);

    /**
     * 创建直接队列，直接队列通过通过直接交换机投递时需要路由key，且必须相等
     * 当向直接交换机投递消息时，必须指定明确的路由key，通过此路由key匹配绑定到直接交换机的路由key，如果相等则投递到绑定的直接队列
     * @param queueName   对列名称
     * @return
     */
    Queue createDirectQueue(String queueName);

    /**
     * 创建主题队列，主题队列绑定到主题交换机时需要指定路由key，路由key中允许使用两个通配符，*表示仅匹配一个单词，#表示可匹配多个单词，单词间以.间隔，
     * 当向主题交换机投递消息时，必须指定路由key，通过此路由key检查是否匹配绑定到主题交换机的路由key，如果匹配则投递到绑定的主题队列
     * @param queueName   对列名称
     * @return
     */
    Queue createTopicQueue(String queueName);

    /**
     * 创建队列
     * @param queueName   对列名称
     * @return
     */
    Queue createQueue(String queueName);

    /**
     * 创建扇形交换机
     * @param exchangeName  交换机名称
     * @return
     */
    FanoutExchange createFanoutExchange(String exchangeName);


    /**
     * 创建直接交换机
     * @param exchangeName  交换机名称
     * @return
     */
    DirectExchange createDirectExchange(String exchangeName);


    /**
     * 创建主题交换机
     * @param exchangeName  交换机名称
     * @return
     */
    TopicExchange createTopicExchange(String exchangeName);


    /**
     * 创建扇形交换机及多个扇形队列并完成绑定
     * @param exchangeName  交换机名称
     * @param queueNames    队列名称
     * @return
     */
    void createFanoutExchangeAndFanoutQueues(String exchangeName, String... queueNames);

    /**
     * 创建直接交换机及直接队列并指定路由key完成绑定
     * @param exchangeName   交换机名称
     * @param queueName      队列名称
     * @param routingKey     明确的路由key
     * @return
     */
    void createDirectExchangeAndDirectQueue(String exchangeName, String queueName, String routingKey);

    /**
     * 创建直接交换机及直接队列并指定路由key完成绑定
     * @param exchangeName   交换机名称
     * @param queueName      队列名称
     * @param routingKey     可带*或#通配符的路由key
     * @return
     */
    void createTopicExchangeAndTopicQueue(String exchangeName, String queueName, String routingKey);

    /**
     * 向扇形交换机发送消息
     * @param exchangeName    交换机名称
     * @param massage          消息对象，必须实现Serializable
     * @return
     */
    <T> void sendFanoutMessage(String exchangeName, T massage);

    /**
     * 向直接或主题交换机发送消息
     * @param exchangeName      交换机名称
     * @param routingKey        路由key
     * @param massage           消息，必须实现Serializable
     * @return
     */
    <T> void sendMessage(String exchangeName, String routingKey, T massage);

}
