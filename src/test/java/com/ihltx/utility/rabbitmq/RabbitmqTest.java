package com.ihltx.utility.rabbitmq;

import com.ihltx.store.SpringBootStarterIhltxApplication;
import com.ihltx.utility.rabbitmq.config.RabbitConfig;
import com.ihltx.utility.rabbitmq.service.RabbitUtil;
import com.ihltx.utility.util.entity.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.Assert.assertEquals;


@SuppressWarnings("all")
@SpringBootTest(classes = {SpringBootStarterIhltxApplication.class})
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class RabbitmqTest {
	
	private RabbitUtil rabbitUtil;

	@Autowired
	private ApplicationContext applicationContext;

	@BeforeEach
	public void beforeEach(){
		try{
			rabbitUtil = applicationContext.getBean(RabbitUtil.class);
		}catch (Exception e){
			e.printStackTrace();
		}
	}


	@Test
	public void test_11_send2FanoutTestQueue() {
		if(rabbitUtil==null) return;
		try {
			rabbitUtil.sendFanoutMessage(RabbitConfig.FANOUT_EXCHANGE_NAME, "Fanout Queue Message");
			assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}
	}

	@Test
	public void test_12_send2DirectTestQueue() {
		if(rabbitUtil==null) return;
		try {
			rabbitUtil.sendMessage(RabbitConfig.DIRECT_EXCHANGE_NAME,RabbitConfig.DIRECT_ROUTINGKEY,"Direct Queue Message");
			assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}
	}

	@Test
	public void test_13_send2TopicTestAQueue() {
		if(rabbitUtil==null) return;
		try {
			rabbitUtil.sendMessage(RabbitConfig.TOPIC_EXCHANGE_NAME,RabbitConfig.TOPIC_ROUTINGKEY,"Topic Queue A Message");
			assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}
	}

	
	@Test
	public void test_14_send2TopicTestBQueue() {
		if(rabbitUtil==null) return;
		try {
			rabbitUtil.sendMessage(RabbitConfig.TOPIC_EXCHANGE_NAME,RabbitConfig.TOPIC_ROUTINGKEY,"Topic Queue B Message");
			assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}
	}
	
	@Test
	public void test_15_send2DirectLiulinQueue() {
		if(rabbitUtil==null) return;
		try {
			Result rs =new Result("abcd","中国");
			rabbitUtil.sendMessage("liulinExchange","liulin",rs);
			assertEquals(true, true);
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}
	}

}
