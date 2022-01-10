package com.ihltx.utility.functional;

import com.ihltx.store.SpringBootStarterIhltxApplication;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Set;


@SpringBootTest(classes = {SpringBootStarterIhltxApplication.class})
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SuppressWarnings("all")
public class TranslateUtilTest {

	
	@Test
	public void test_10_Get() {

		Map<String, Set<String>>  list  = TranslateUtil.getDatas("英语");
		System.out.println(list);

		list  = TranslateUtil.getDatas("english");
		System.out.println(list);
	}


}
