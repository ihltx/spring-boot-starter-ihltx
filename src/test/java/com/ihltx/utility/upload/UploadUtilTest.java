package com.ihltx.utility.upload;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;

import com.ihltx.store.SpringBootStarterIhltxApplication;
import com.ihltx.utility.upload.entity.UploadResult;
import com.ihltx.utility.upload.service.impl.UploadUtilImpl;
import com.ihltx.utility.util.FileUtil;
import com.ihltx.utility.util.StringUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;



@SuppressWarnings("all")
@SpringBootTest(classes = {SpringBootStarterIhltxApplication.class})
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class UploadUtilTest {
	
	@Autowired
	private UploadUtilImpl uploadUtil;



	private MockMultipartHttpServletRequest request;
    private MockHttpServletResponse response;

	private String testBaseDirPath = "/tmp/test/";
	private String currTestBaseDirPath = "/tmp/test/";

	@BeforeEach
	void setUp() {
		if(FileUtil.isWindows()){
			testBaseDirPath = "C:" + testBaseDirPath;
			currTestBaseDirPath = "C:" + currTestBaseDirPath;
		}
		currTestBaseDirPath += this.getClass().getSimpleName() + "/";
		if(!FileUtil.exists(currTestBaseDirPath)){
			FileUtil.makeDirs(currTestBaseDirPath);
		}

		request = new MockMultipartHttpServletRequest();
		request.setCharacterEncoding(StringUtil.UTF_8);
		response = new MockHttpServletResponse();
		response.setCharacterEncoding(StringUtil.UTF_8);

	}

	@Test
	public void test_10_upload() {
		byte[] content;
		try {
			String filename = "test.txt";
			content = "Hallo Word".getBytes(StringUtil.UTF_8);
			MockMultipartFile multipartFile  = new MockMultipartFile("file", filename, "text/plain", content);
			request.addFile(multipartFile);
			
			UploadResult rs = uploadUtil.upload(0L, multipartFile, filename);
			System.out.println(rs);
			assertEquals(rs.getStatus()==0, true);
			
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}
	}
	
	@Test
	public void test_11_upload() {
		try {
			String filename = testBaseDirPath + "1.jpg";
			File file =new File(filename);
			FileInputStream in_file = new FileInputStream(file);
			
			MockMultipartFile multipartFile  = new MockMultipartFile("file" , in_file);
			request.addFile(multipartFile);
			
			UploadResult rs = uploadUtil.upload(0L, multipartFile, filename);
			System.out.println(rs);
			assertEquals(rs.getStatus()==0, true);
			
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(false, true);
		}
	}

}
