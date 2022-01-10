package com.ihltx.utility.i18n;

import com.ihltx.store.SpringBootStarterIhltxApplication;
import com.ihltx.utility.i18n.service.I18nService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("all")
@SpringBootTest(classes = {SpringBootStarterIhltxApplication.class})
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class I18nServiceTest {


    @Autowired(required = false)
    private I18nService i18nService;

    @Test
    public void test_10_install(){
        if(i18nService==null){
            return;
        }
        try {
            Boolean rs = i18nService.install(2L,null);
            assertEquals(rs , true);
        } catch (Exception e) {
            e.printStackTrace();
            assertEquals(false , true);
        }

    }


    @Test
    public void test_11_unInstall(){
        if(i18nService==null){
            return;
        }
        try {
            Boolean rs = i18nService.unInstall(2L);
            assertEquals(rs , true);
        } catch (Exception e) {
            e.printStackTrace();
            assertEquals(false , true);
        }

    }

}
