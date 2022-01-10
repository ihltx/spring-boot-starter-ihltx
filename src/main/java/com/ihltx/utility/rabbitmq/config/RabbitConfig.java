package com.ihltx.utility.rabbitmq.config;

public class RabbitConfig {
   
	public static final String FANOUT_QUEUE_NAME  = "fanoutQueueName";
	public static final String FANOUT_QUEUE_NAME1  = "fanoutQueueName1";
	public static final String FANOUT_EXCHANGE_NAME = "fanoutExchangeName";
	public static final String DIRECT_QUEUE_NAME = "directQueueName";
	public static final String DIRECT_EXCHANGE_NAME = "directExchangeName";
	public static final String DIRECT_ROUTINGKEY = "test";
	public static final String TOPIC_QUEUE_NAME = "topicQueueName";
	public static final String TOPIC_EXCHANGE_NAME = "topicExchangeName";
	public static final String TOPIC_ROUTINGKEY  = "test.*";
}
