package com.ihltx.utility.jwt;

import com.ihltx.store.SpringBootStarterIhltxApplication;
import com.ihltx.utility.jwt.entity.User;
import com.ihltx.utility.jwt.service.JwtUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("all")
@SpringBootTest(classes = {SpringBootStarterIhltxApplication.class})
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class JwtUtilTest {
	
	@Autowired
	private JwtUtil jwtUtil;

	
	@Test
	public void test_01_sign() {
		Map<String,Object> user=new HashMap<String, Object>();
		user.put("loginId", 123454);
		user.put("loginName", "admin");
		String token = jwtUtil.sign(user);
		System.out.println(token);
		//eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbklkIjoiMTIzNDU0IiwibG9naW5OYW1lIjoiYWRtaW4iLCJleHAiOjE2MDg5NTk0NjF9.sMjYOJdCqWyZaoTMnzWcW5uqDGNjOEAoSfmvAJPKJlU
		assertEquals(token!=null , true);
		
		Map<String,Object> result = jwtUtil.verify(token);
		System.out.println(result);
		assertEquals(result.get("loginName").toString().equals("admin") , true);
		
	}
	
	
	@Test
	public void test_02_sign() {
		User user = new User();
		user.setUser_name("admin");
		user.setUser_password("123456");
		String token = jwtUtil.sign(user);
		System.out.println(token);
		//eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX3VwZGF0ZWQiOiIwIiwidXNlcl9wYXNzd29yZCI6IiIsInVzZXJfaWQiOiIwIiwidXNlcl9uYW1lIjoiIiwidXNlcl9jcmVhdGVkIjoiMCIsInVzZXJfZGVsZXRlZCI6IjAiLCJleHAiOjE2MDg5NTk0NjF9.C8rmgMbSQ0b4NujTnWuCPWsXSzXNdAC1OrATeERSWzs
		assertEquals(token!=null , true);
		
		User user1 = jwtUtil.verify(token,  User.class);
		System.out.println(user1);
		assertEquals(user1.getUser_password().equals("123456") , true);
		
	}



}
