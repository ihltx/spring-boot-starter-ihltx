package com.ihltx.utility.log;


import com.ihltx.store.SpringBootStarterIhltxApplication;
import com.ihltx.utility.log.service.LoggerBuilder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("all")
@SpringBootTest(classes = {SpringBootStarterIhltxApplication.class})
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
//@Transactional
public class LoggerBuilderTest {
	
	@Autowired
	private LoggerBuilder loggerBuilder;

	@Test
	public void test_10_Log() {

		loggerBuilder.debug("debug 日志");
		assertEquals(true, true);
	}

	@Test
	public void testLog() {

		loggerBuilder.debug("debug 日志");
		loggerBuilder.info("info 日志");
		loggerBuilder.warn("warn 日志");
		loggerBuilder.error("error 日志");
		loggerBuilder.trace("trace 日志");
		assertEquals(true, true);


		loggerBuilder.getLogger(LoggerBuilderTest.class.getName()).debug("debug 日志");
		loggerBuilder.getLogger(LoggerBuilderTest.class.getName()).info("info 日志");
		loggerBuilder.getLogger(LoggerBuilderTest.class.getName()).warn("warn 日志");
		loggerBuilder.getLogger(LoggerBuilderTest.class.getName()).error("error 日志");
		loggerBuilder.getLogger(LoggerBuilderTest.class.getName()).trace("trace 日志");
		assertEquals(true, true);


		loggerBuilder.getLogger(0 , LoggerBuilderTest.class.getName()).debug("debug 日志");
		loggerBuilder.getLogger(0 , LoggerBuilderTest.class.getName()).info("info 日志");
		loggerBuilder.getLogger(0 , LoggerBuilderTest.class.getName()).warn("warn 日志");
		loggerBuilder.getLogger(0 , LoggerBuilderTest.class.getName()).error("error 日志");
		loggerBuilder.getLogger(0 , LoggerBuilderTest.class.getName()).trace("trace 日志");
		assertEquals(true, true);
		loggerBuilder.getLogger(10000 , LoggerBuilderTest.class.getName()).trace("trace 日志");
		assertEquals(true, true);

		loggerBuilder.getLogger(10001 , LoggerBuilderTest.class.getName()).trace("trace 日志");
		assertEquals(true, true);

	}

}
