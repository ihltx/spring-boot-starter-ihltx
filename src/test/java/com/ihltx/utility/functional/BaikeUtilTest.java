package com.ihltx.utility.functional;

import com.ihltx.store.SpringBootStarterIhltxApplication;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;


@SpringBootTest(classes = {SpringBootStarterIhltxApplication.class})
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SuppressWarnings("all")
public class BaikeUtilTest {

	
	@Test
	public void test_10_Get() {

		List<Map<String, String>> list  = BaikeUtil.getDatas("大汉");
		System.out.println(list);
	}


}
