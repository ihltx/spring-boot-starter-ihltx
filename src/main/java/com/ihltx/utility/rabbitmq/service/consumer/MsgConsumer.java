package com.ihltx.utility.rabbitmq.service.consumer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.ihltx.utility.log.service.LoggerBuilder;
import com.ihltx.utility.rabbitmq.config.RabbitConfig;
import com.ihltx.utility.util.entity.Result;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@SuppressWarnings("all")
@Component
public class MsgConsumer {

	@Autowired
	private LoggerBuilder loggerBuilder;

	//自动创建队列及交换机并建立绑定关系，同时指定ack响应模式为MANUAL手动响应
	@RabbitListener(bindings = {
			@QueueBinding(
					value = @Queue(value = RabbitConfig.FANOUT_QUEUE_NAME, durable = "true"),
					exchange = @Exchange(name = RabbitConfig.FANOUT_EXCHANGE_NAME, type = "fanout")
			) }
			,ackMode = "MANUAL")
	@RabbitHandler
	public void processFanoutMsg(String massage, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
		loggerBuilder.getLogger().debug("received Fanout message : " + massage);
		//如果对消息处理成功则调用channel的basucAck进行基本Ack回应，同时指定第二个参数为false表示消息已经被当前消费者成功处理不需要投递给其它消费者进行处理
		//如果第二个参数为true，表示将把当前消息继续投递给其它消费者进行处理，即可以处理多次
		channel.basicAck(deliveryTag , false);
	}

	//自动创建队列及交换机并建立绑定关系
	@RabbitListener(bindings = {
			@QueueBinding(value = @Queue(value = RabbitConfig.FANOUT_QUEUE_NAME1, durable = "true"), exchange = @Exchange(name = RabbitConfig.FANOUT_EXCHANGE_NAME, type = "fanout")) })
	@RabbitHandler
	public void processFanout1Msg(String massage) {

		loggerBuilder.getLogger().debug("received Fanout1 message : " + massage);
	}

	@RabbitListener(bindings = {
			@QueueBinding(value = @Queue(value = RabbitConfig.DIRECT_QUEUE_NAME, durable = "true"), exchange = @Exchange(value = RabbitConfig.DIRECT_EXCHANGE_NAME), key = RabbitConfig.DIRECT_ROUTINGKEY) })
	@RabbitHandler
	public void processDirectMsg(Message massage) {
		String msg = new String(massage.getBody(), StandardCharsets.UTF_8);
		loggerBuilder.getLogger().debug("received Direct message : " + msg);
	}

	@RabbitListener(bindings = {
			@QueueBinding(value = @Queue(value = RabbitConfig.TOPIC_QUEUE_NAME, durable = "true"), exchange = @Exchange(value = RabbitConfig.TOPIC_EXCHANGE_NAME, type = "topic"), key = RabbitConfig.TOPIC_ROUTINGKEY) })
	@RabbitHandler
	public void processTopicMsg(Message massage) {
		String msg = new String(massage.getBody(), StandardCharsets.UTF_8);
		loggerBuilder.getLogger().debug("received Topic message : " + msg);
	}

	//自动创建队列及交换机并建立绑定关系，同时指定ack响应模式为MANUAL手动响应
	@RabbitListener(bindings = {
			@QueueBinding(value = @Queue(value = "liulinQueue", durable = "true"), exchange = @Exchange(value = "liulinExchange"), key = "liulin") }
			,ackMode = "MANUAL")
	@RabbitHandler
	public void processDirectLiulinMsg(Result rs, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
		loggerBuilder.getLogger().debug(rs.toString());
		loggerBuilder.getLogger().debug("received Direct Liulin message : " + rs.toString());

//		//处理不成功，不响应，不投递给其它消费者进行处理，并且重新放入队列
//		channel.basicNack(deliveryTag , false,true);
		channel.basicAck(deliveryTag,false);
	}

}
