package com.ihltx.utility.functional;

import com.ihltx.store.SpringBootStarterIhltxApplication;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.Set;


@SpringBootTest(classes = {SpringBootStarterIhltxApplication.class})
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SuppressWarnings("all")
public class WeatherUtilTest {

	
	@Test
	public void test_10_Get() {

		JSONObject list  = WeatherUtil.getDatas("成都");
		System.out.println(list);

	}


}
