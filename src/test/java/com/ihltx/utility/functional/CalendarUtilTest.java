package com.ihltx.utility.functional;

import com.ihltx.store.SpringBootStarterIhltxApplication;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;
import java.util.Map;


@SpringBootTest(classes = {SpringBootStarterIhltxApplication.class})
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SuppressWarnings("all")
public class CalendarUtilTest {

	
	@Test
	public void test_10_Get() {

		Map<String, String> list  = CalendarUtil.getDatas(new Date());
		System.out.println(list);
	}


}
