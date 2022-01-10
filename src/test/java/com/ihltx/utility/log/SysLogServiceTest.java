package com.ihltx.utility.log;


import com.ihltx.store.SpringBootStarterIhltxApplication;
import com.ihltx.utility.log.entity.SysLog;
import com.ihltx.utility.log.service.SysLogService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("all")
@SpringBootTest(classes = {SpringBootStarterIhltxApplication.class})
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
//@Transactional
public class SysLogServiceTest {
	
	@Autowired(required = false)
	private SysLogService sysLogService;

	@Test
	public void test_10_add() {
		if(sysLogService==null){
			return;
		}

		SysLog sysLog =new SysLog(null , 0L , "aaa" ,"bbb","ddd","ddd","ddssds","aaa","asasas","ddd",new Date());
		Boolean rs = sysLogService.add(sysLog);

		assertEquals(rs, true);
	}


}
