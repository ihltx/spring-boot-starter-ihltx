package com.ihltx.utility.freemarker;

import com.ihltx.store.SpringBootStarterIhltxApplication;
import com.ihltx.utility.freemarker.exceptions.ViewNotFoundException;
import com.ihltx.utility.freemarker.service.FreemarkerUtil;
import com.ihltx.utility.util.StringUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;


@SuppressWarnings("all")
@SpringBootTest(classes = {SpringBootStarterIhltxApplication.class})
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class FreemarkerUtilTest {
	
	@Autowired
	private FreemarkerUtil freemarkerUtil;



	private MockMultipartHttpServletRequest request;
    private MockHttpServletResponse response;


	@BeforeEach
	void setUp() {
		request = new MockMultipartHttpServletRequest();
		request.setCharacterEncoding(StringUtil.UTF_8);
		response = new MockHttpServletResponse();
		response.setCharacterEncoding(StringUtil.UTF_8);

	}

	@Test
	public void test_10_renderTemplate() {
		Map<String, Object> map =new HashMap<>();
		try {
			String content= freemarkerUtil.renderTemplate(map,"test" , 0L,"default" , "zh-CN" , request);
			System.out.println(content);
			assertEquals(content.contains("default"), true);
		} catch (IOException e) {
			e.printStackTrace();
			assertEquals(false, true);
		} catch (ViewNotFoundException e) {
			e.printStackTrace();
			assertEquals(false, true);
		}
	}

	@Test
	public void test_11_renderTemplate() {
		Map<String, Object> map =new HashMap<>();
		try {
			String content= freemarkerUtil.renderTemplate(map,"test" , 0L,"blue" , "zh-CN" , request);
			System.out.println(content);
			assertEquals(content.contains("blue"), true);
		} catch (IOException e) {
			e.printStackTrace();
			assertEquals(false, true);
		} catch (ViewNotFoundException e) {
			e.printStackTrace();
			assertEquals(false, true);
		}
	}

}
