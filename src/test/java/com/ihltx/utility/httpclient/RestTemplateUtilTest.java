package com.ihltx.utility.httpclient;

import static org.junit.Assert.assertEquals;

import com.ihltx.store.SpringBootStarterIhltxApplication;
import com.ihltx.utility.httpclient.service.RestTemplateUtil;
import com.ihltx.utility.util.StringUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(classes = {SpringBootStarterIhltxApplication.class})
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SuppressWarnings("all")
public class RestTemplateUtilTest {
	
	@Autowired
	private RestTemplateUtil restTemplateUtil;
	
	@Test
	public void test_10_Get() {
		String data = restTemplateUtil.get("https://www.baidu.com/");
		assertEquals(StringUtil.isNullOrEmpty(data), false);
		System.out.println(data);
		assertEquals(data.contains("www.baidu.com"), true);
	}

//	@Test
//	public void testPostObject() {
//		User user = new User(3, "张三");
//		String data = RestTemplateUtil.postObject("http://127.0.0.1:8080/post_object", user);
//		System.out.println(data);
//		assertEquals(data.length() > 0, true);
//	}
//	

//	@Test
//	public void testPostMap() {
//		Map<String, Object> user = new HashMap<>();
//		user.put("userId", "1000");
//		user.put("userName", "李四");
//		String data = RestTemplateUtil.postObject("http://127.0.0.1:8080/post_map", user);
//		System.out.println(data);
//		assertEquals(data.length() > 0, true);
//	}
//
//	@Test
//	public void testPostJson() {
//		JSONObject user = new JSONObject();
//		user.put("userId", "1000");
//		user.put("userName", "李四");
//		String data = RestTemplateUtil.postObject("http://127.0.0.1:8080/post_json", user);
//		System.out.println(data);
//		assertEquals(data.length() > 0, true);
//	}
//
//	@Test
//	public void testPostForm() {
//		Map<String, Object> user = new HashMap<>();
//		user.put("userId", "1000");
//		user.put("userName", "李四123");
//		String data = RestTemplateUtil.postForm("http://127.0.0.1:8080/post_form", user);
//		System.out.println(data);
//		assertEquals(data.length() > 0, true);
//	}
//
//	@Test
//	public void testPostFormFile() {
//		Map<String, Object> user = new HashMap<>();
//		user.put("userId", "1000");
//		user.put("userName", "李四123");
//		Map<String, String> files = new HashMap<String, String>();
//		files.put("file", "e:/1.jpg");
//
//		String data = RestTemplateUtil.postForm("http://127.0.0.1:8080/post_formfile", user, files);
//		System.out.println(data);
//		assertEquals(data.length() > 0, true);
//	}


}
