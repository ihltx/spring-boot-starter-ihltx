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
public class ExpressUtilTest {

	
	@Test
	public void test_10_Get() throws Exception {

		String list = ExpressUtil.getDatas("1256310" , "e77ceced-e98b-460a-a26c-6731a6cd0ccf" , "HTKY" , "552052643733750");
		System.out.println(list);

	}


}
