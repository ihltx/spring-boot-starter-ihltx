package com.ihltx.utility.mail;

import com.ihltx.store.SpringBootStarterIhltxApplication;
import com.ihltx.utility.mail.service.JavaMailSenderUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("all")
@SpringBootTest(classes = {SpringBootStarterIhltxApplication.class})
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@Transactional
public class JavaMailSenderUtilTest {

    @Autowired
    private JavaMailSenderUtil javaMailSenderUtil;



    @Test
    public void test_20_send() {
        Boolean rs = javaMailSenderUtil.send("星期五下午会议1","mingnuo@163.com","84611913@qq.com,刘林","星期五下午14：00会议，希望大家准备参加。");
        assertEquals(rs, true);
    }

    @Test
    public void test_21_send() {
        Boolean rs = javaMailSenderUtil.send("星期五下午会议2","mingnuo@163.com","84611913@qq.com,刘林","星期五下午14：00会议，<span style='color:red;font-size:16px'>希望大</span>家准备参加。",true);
        assertEquals(rs, true);
    }

    @Test
    public void test_22_send() {
        File[] attachments = new File[2];
        attachments[0] = new File("C:\\tmp\\test\\1.jpg");
        attachments[1] = new File("C:\\tmp\\test\\elasticsearch-head-master.zip");


        Boolean rs = javaMailSenderUtil.send("星期五下午会议3","mingnuo@163.com","84611913@qq.com,刘林","星期五下午14：00会议，<span style='color:red;font-size:16px'>希望大</span>家准备参加。",true , attachments);
        assertEquals(rs, true);
    }

    @Test
    public void test_23_send() {
        File[] attachments = new File[2];
        attachments[0] = new File("C:\\tmp\\test\\1.jpg");
        attachments[1] = new File("C:\\tmp\\test\\elasticsearch-head-master.zip");

        Map<String , File> bodyImages =new HashMap<>();
        bodyImages.put("img1" , new File("C:\\tmp\\test\\1.jpg"));

        Boolean rs = javaMailSenderUtil.send("星期五下午会议4","mingnuo@163.com","84611913@qq.com,刘林","星期五下午14：00会议，<span style='color:red;font-size:16px'>希望大</span>家准备参加<img src=\"cid:img1\" />。",true , attachments , bodyImages);
        assertEquals(rs, true);
    }

    @Test
    public void test_30_send() {
        Boolean rs = javaMailSenderUtil.send("星期五下午会议1","mingnuo@163.com","星期五下午14：00会议，希望大家准备参加。");
        assertEquals(rs, true);
    }

    @Test
    public void test_31_send() {
        Boolean rs = javaMailSenderUtil.send("星期五下午会议2","mingnuo@163.com","星期五下午14：00会议，<span style='color:red;font-size:16px'>希望大</span>家准备参加。",true);
        assertEquals(rs, true);
    }

    @Test
    public void test_32_send() {
        File[] attachments = new File[2];
        attachments[0] = new File("C:\\tmp\\test\\1.jpg");
        attachments[1] = new File("C:\\tmp\\test\\elasticsearch-head-master.zip");


        Boolean rs = javaMailSenderUtil.send("星期五下午会议3","mingnuo@163.com","星期五下午14：00会议，<span style='color:red;font-size:16px'>希望大</span>家准备参加。",true , attachments);
        assertEquals(rs, true);
    }

    @Test
    public void test_33_send() {
        File[] attachments = new File[2];
        attachments[0] = new File("C:\\tmp\\test\\1.jpg");
        attachments[1] = new File("C:\\tmp\\test\\elasticsearch-head-master.zip");

        Map<String , File> bodyImages =new HashMap<>();
        bodyImages.put("img1" , new File("C:\\tmp\\test\\1.jpg"));

        Boolean rs = javaMailSenderUtil.send("星期五下午会议4","mingnuo@163.com","星期五下午14：00会议，<span style='color:red;font-size:16px'>希望大</span>家准备参加<img src=\"cid:img1\" />。",true , attachments , bodyImages);
        assertEquals(rs, true);
    }
}
