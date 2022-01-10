package com.ihltx.utility.i18n;

import com.ihltx.store.SpringBootStarterIhltxApplication;
import com.ihltx.utility.i18n.entity.Language;
import com.ihltx.utility.i18n.mapper.LanguageMapper;
import com.ihltx.utility.i18n.service.I18nUtil;
import com.ihltx.utility.util.StringUtil;
import com.ihltx.utility.util.WebUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("all")
@SpringBootTest(classes = {SpringBootStarterIhltxApplication.class})

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class I18nUtilTest {

    @Autowired
    private I18nUtil i18nUtil;


    private MockMultipartHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockMultipartHttpServletRequest();
        request.setCharacterEncoding(StringUtil.UTF_8);
        response = new MockHttpServletResponse();
        response.setCharacterEncoding(StringUtil.UTF_8);


    }

    //无数据库
    @Test
    public void test_10_getConfigurationMessageProperties(){
        i18nUtil.getI18nConfig().setEnableDataSource(false);
        i18nUtil.clearCache(0L);

        //shopId=0的店铺在配置文件中有特殊语言配置，仅配置了en-US语言，将直接做默认语言，因此虽然获取的是zh-CN语言资源，但因为shopid=0没有zh-CN语言
        //所以获取的是默认语言en-US的语言资源
        Properties messages =  i18nUtil.getConfigurationMessageProperties(0L,"zh-CN");
        System.out.println(messages.getProperty("hshop_web_application_name"));
        assertEquals("HShop Application(test_0)-{0}-{1}".equals(messages.getProperty("hshop_web_application_name")), true);

        messages =  i18nUtil.getConfigurationMessageProperties(0L,"zh-CN");
        System.out.println(messages.getProperty("hshop_web_application_name"));
        assertEquals("HShop Application(test_0)-{0}-{1}".equals(messages.getProperty("hshop_web_application_name")), true);

        //shopId=null表示获取的是全局语言配置资源，全局语言配置中包括zh-CN语言，因此OK
        messages =  i18nUtil.getConfigurationMessageProperties(null,"zh-CN");
        System.out.println(messages.getProperty("hshop_web_application_name"));
        assertEquals("HShop应用程序(test)-{0}-{1}".equals(messages.getProperty("hshop_web_application_name")), true);

        messages =  i18nUtil.getConfigurationMessageProperties(null,"zh-CN");
        System.out.println(messages.getProperty("hshop_web_application_name"));
        assertEquals("HShop应用程序(test)-{0}-{1}".equals(messages.getProperty("hshop_web_application_name")), true);


        i18nUtil.clearCache();

        messages =  i18nUtil.getConfigurationMessageProperties(0L,"zh-CN");
        System.out.println(messages.getProperty("hshop_web_application_name"));
        assertEquals("HShop Application(test_0)-{0}-{1}".equals(messages.getProperty("hshop_web_application_name")), true);

        messages =  i18nUtil.getConfigurationMessageProperties(null,"zh-CN");
        System.out.println(messages.getProperty("hshop_web_application_name"));
        assertEquals("HShop应用程序(test)-{0}-{1}".equals(messages.getProperty("hshop_web_application_name")), true);

    }

    @Test
    public void test_11_getConfigurationMessage(){
        i18nUtil.getI18nConfig().setEnableDataSource(false);
        i18nUtil.clearCache(0L);

        //shopId=0的店铺在配置文件中有特殊语言配置，仅配置了en-US语言，将直接做默认语言，因此虽然获取的是zh-CN语言资源，但因为shopid=0没有zh-CN语言
        //所以获取的是默认语言en-US的语言资源
        String message = i18nUtil.getConfigurationMessage(0L , "zh-CN" , "hshop_web_application_name" , new String[]{"a","b"});
        System.out.println(message);
        assertEquals("HShop Application(test_0)-a-b".equals(message) , true);

        message = i18nUtil.getConfigurationMessage("hshop_web_application_name" , new String[]{"a","b"});
        System.out.println(message);
        assertEquals("HShop Application(test_0)-a-b".equals(message) , true);

        message = i18nUtil.getConfigurationMessage(null , "zh-CN" , "hshop_web_application_name" , new String[]{"a","b"});
        System.out.println(message);
        assertEquals("HShop应用程序(test)-a-b".equals(message) , true);

        message = i18nUtil.getConfigurationMessage(null , "zh-CN" , "hshop_web_application_name" , new String[]{"a","b"});
        System.out.println(message);
        assertEquals("HShop应用程序(test)-a-b".equals(message) , true);

    }

    @Test
    public void test_12_getMessageProperties(){
        i18nUtil.getI18nConfig().setEnableDataSource(false);
        i18nUtil.clearCache(0L);

        //shopId=0的店铺在配置文件中有特殊语言配置，仅配置了en-US语言，将直接做默认语言，因此虽然获取的是zh-CN语言资源，但因为shopid=0没有zh-CN语言
        //所以获取的是默认语言en-US的语言资源
        Properties props = i18nUtil.getMessageProperties(0L,"zh-CN");
        System.out.println(props.get("hshop_web_application_name").equals("HShop Application(test_0)-{0}-{1}"));

        //shopId=-1 or null表示获取的是全局语言配置资源，全局语言配置中包括zh-CN语言，因此OK
        props = i18nUtil.getMessageProperties(-1L,"zh-CN");
        System.out.println(props.get("hshop_web_application_name").equals("HShop应用程序(test)-{0}-{1}"));



    }

    @Test
    public void test_13_getMessages(){
        i18nUtil.getI18nConfig().setEnableDataSource(false);
        i18nUtil.clearCache(0L);

        //shopId=0的店铺在配置文件中有特殊语言配置，仅配置了en-US语言，将直接做默认语言，因此虽然获取的是zh-CN语言资源，但因为shopid=0没有zh-CN语言
        //所以获取的是默认语言en-US的语言资源
        String messages = i18nUtil.getMessages(0L,"zh-CN");
        System.out.println(messages);

        //shopId=-1 or null表示获取的是全局语言配置资源，全局语言配置中包括zh-CN语言，因此OK
        messages = i18nUtil.getMessages(-1L,"zh-CN");
        System.out.println(messages);

    }

    @Test
    public void test_14_getMessage(){
        i18nUtil.getI18nConfig().setEnableDataSource(false);
        i18nUtil.clearCache(0L);

        //shopId=0的店铺在配置文件中有特殊语言配置，仅配置了en-US语言，将直接做默认语言，因此虽然获取的是zh-CN语言资源，但因为shopid=0没有zh-CN语言
        //所以获取的是默认语言en-US的语言资源
        String message = i18nUtil.getMessage(0L,"zh-CN" , "hshop_web_application_name" , new String[]{"a","b"});
        System.out.println(message);
        assertEquals("HShop Application(test_0)-a-b".equals(message) ,true);

        //shopId=-1 or null表示获取的是全局语言配置资源，全局语言配置中包括zh-CN语言，因此OK
        message = i18nUtil.getMessage(-1L,"zh-CN" , "hshop_web_application_name" , new String[]{"a","b"});
        System.out.println(message);
        assertEquals("HShop应用程序(test)-a-b".equals(message) ,true);

    }


    @Test
    public void test_15_existLanguage(){
        i18nUtil.getI18nConfig().setEnableDataSource(false);
        i18nUtil.clearCache(0L);

        //shopId=0的店铺在配置文件中有特殊语言配置，仅配置了en-US语言，将直接做默认语言，因此虽然获取的是zh-CN语言资源，但因为shopid=0没有zh-CN语言
        //所以获取的是默认语言en-US的语言资源
        Boolean rs = i18nUtil.existLanguage(0L,"zh-CN");
        System.out.println(rs);
        assertEquals(!rs ,true);

        rs = i18nUtil.existLanguage(-1L,"zh-CN");
        System.out.println(rs);
        assertEquals(rs ,true);

    }

    @Test
    public void test_16_getLanguages(){
        i18nUtil.getI18nConfig().setEnableDataSource(false);
        i18nUtil.clearCache(0L);

        //shopId=0的店铺在配置文件中有特殊语言配置，仅配置了en-US语言，将直接做默认语言，因此虽然获取的是zh-CN语言资源，但因为shopid=0没有zh-CN语言
        List<Language> langs = i18nUtil.getLanguages(0L);
        System.out.println(langs);
        assertEquals(langs.size()==1 ,true);

        langs = i18nUtil.getLanguages(-1L);
        System.out.println(langs);
        assertEquals(langs.size()==2 ,true);

    }

    @Test
    public void test_17_getCurrLanguage(){
        i18nUtil.getI18nConfig().setEnableDataSource(false);
        i18nUtil.clearCache(0L);

        //shopId=0的店铺在配置文件中有特殊语言配置，仅配置了en-US语言，将直接做默认语言，因此虽然获取的是zh-CN语言资源，但因为shopid=0没有zh-CN语言
        request.addHeader("Accept-Language" , "zh-CN");
        Language lang = i18nUtil.getCurrLanguage(0L , request);
        System.out.println(lang);
        assertEquals(lang.getLangCode().equals("en-US") ,true);

        request.getSession().setAttribute(i18nUtil.getRequestAndSessionAndViewLanguageName(),"zh-CN");
        lang = i18nUtil.getCurrLanguage( -1L , request);
        System.out.println(lang);
        assertEquals(lang.getLangCode().equals("zh-CN") ,true);


    }

    @Test
    public void test_18_setLanguage(){
        i18nUtil.getI18nConfig().setEnableDataSource(false);
        i18nUtil.clearCache(0L);

        //shopId=0的店铺在配置文件中有特殊语言配置，仅配置了en-US语言，将直接做默认语言，因此虽然获取的是zh-CN语言资源，但因为shopid=0没有zh-CN语言
        Boolean rs = i18nUtil.setLanguage(request , "zh-CN");
        System.out.println(rs);
        assertEquals(rs ,true);

        String code = i18nUtil.getLanguage(request);
        System.out.println(code);
        assertEquals(code.equals("zh-CN") ,true);


        rs = i18nUtil.setLanguage(request , "en-US");
        System.out.println(rs);
        assertEquals(rs ,true);

        code = i18nUtil.getLanguage(request);
        System.out.println(code);
        assertEquals(code.equals("en-US") ,true);
    }


    @Test
    public void test_19_getDefaultLanguage(){
        i18nUtil.getI18nConfig().setEnableDataSource(false);
        i18nUtil.clearCache(0L);

        //1、使用配置，获取 shopId=0的默认语言(shopId=1的有自定义语言配置因此不会全局语言配置)
        String defaultLanguage = i18nUtil.getDefaultLanguage(0L);
        assertEquals(defaultLanguage.equals("en-US") , true);

        //2、使用配置，获取shopId=2的默认语言(shopId=2的没有自定义语言配置因此使用全局语言配置)
        defaultLanguage = i18nUtil.getDefaultLanguage(2L);
        assertEquals(defaultLanguage.equals("zh-CN") , true);


        //3、使用配置，修改默认shopId，获取当前默认shopId的自定义语言
        request.setAttribute(WebUtil.APP_SHOP_ID_NAME , 0L);
        defaultLanguage = i18nUtil.getDefaultLanguage(request);
        assertEquals(defaultLanguage.equals("en-US") , true);


        //4、使用配置，修改默认shopId，获取当前默认shopId的自定义语言
        WebUtil.setShopId(request , 2L);
        defaultLanguage = i18nUtil.getDefaultLanguage(request);
        assertEquals(defaultLanguage.equals("zh-CN") , true);


    }



    //使用数据库
    @Test
    public void test_30_getConfigurationMessageProperties(){
        if(!i18nUtil.getI18nConfig().getEnableDataSource()){
            return;
        }
        i18nUtil.clearCache(0L);

        //shopId=0的店铺在数据库存在zh-CN语言
        Properties messages =  i18nUtil.getConfigurationMessageProperties(0L,"zh-CN");
        System.out.println(messages.getProperty("hshop_web_application_name"));
        assertEquals("HShop应用程序(test_0)-{0}-{1}".equals(messages.getProperty("hshop_web_application_name")), true);

        messages =  i18nUtil.getConfigurationMessageProperties(0L,"zh-CN");
        System.out.println(messages.getProperty("hshop_web_application_name"));
        assertEquals("HShop应用程序(test_0)-{0}-{1}".equals(messages.getProperty("hshop_web_application_name")), true);

        //shopId=null -1的店铺在数据库存在zh-CN语言
        messages =  i18nUtil.getConfigurationMessageProperties(null,"zh-CN");
        System.out.println(messages.getProperty("hshop_web_application_name"));
        assertEquals("HShop应用程序(test)-{0}-{1}".equals(messages.getProperty("hshop_web_application_name")), true);

        messages =  i18nUtil.getConfigurationMessageProperties(null,"zh-CN");
        System.out.println(messages.getProperty("hshop_web_application_name"));
        assertEquals("HShop应用程序(test)-{0}-{1}".equals(messages.getProperty("hshop_web_application_name")), true);


        i18nUtil.clearCache();

        messages =  i18nUtil.getConfigurationMessageProperties(0L,"zh-CN");
        System.out.println(messages.getProperty("hshop_web_application_name"));
        assertEquals("HShop应用程序(test_0)-{0}-{1}".equals(messages.getProperty("hshop_web_application_name")), true);

        messages =  i18nUtil.getConfigurationMessageProperties(null,"zh-CN");
        System.out.println(messages.getProperty("hshop_web_application_name"));
        assertEquals("HShop应用程序(test)-{0}-{1}".equals(messages.getProperty("hshop_web_application_name")), true);

    }

    @Test
    public void test_31_getConfigurationMessage(){
        if(!i18nUtil.getI18nConfig().getEnableDataSource()){
            return;
        }
        i18nUtil.clearCache(0L);

        //shopId=0的店铺在数据库存在zh-CN语言
        String message = i18nUtil.getConfigurationMessage(0L , "zh-CN" , "hshop_web_application_name" , new String[]{"a","b"});
        System.out.println(message);
        assertEquals("HShop应用程序(test_0)-a-b".equals(message) , true);

        message = i18nUtil.getConfigurationMessage("hshop_web_application_name" , new String[]{"a","b"});
        System.out.println(message);
        assertEquals("HShop应用程序(test_0)-a-b".equals(message) , true);

        //shopId=null -1的店铺在数据库存在zh-CN语言
        message = i18nUtil.getConfigurationMessage(null , "zh-CN" , "hshop_web_application_name" , new String[]{"a","b"});
        System.out.println(message);
        assertEquals("HShop应用程序(test)-a-b".equals(message) , true);

        message = i18nUtil.getConfigurationMessage(null , "zh-CN" , "hshop_web_application_name" , new String[]{"a","b"});
        System.out.println(message);
        assertEquals("HShop应用程序(test)-a-b".equals(message) , true);

    }

    @Test
    public void test_32_getMessageProperties(){
        if(!i18nUtil.getI18nConfig().getEnableDataSource()){
            return;
        }
        i18nUtil.clearCache(0L);

        //shopId=0的店铺在数据库存在zh-CN语言
        Properties props = i18nUtil.getMessageProperties(0L,"zh-CN");
        System.out.println(props.get("hshop_web_application_name").equals("HShop应用程序(db4)"));

        //shopId=-1 or null
        props = i18nUtil.getMessageProperties(-1L,"zh-CN");
        System.out.println(props.get("hshop_web_application_name").equals("HShop应用程序(global)(db4)"));

    }

    @Test
    public void test_33_getMessages(){
        if(!i18nUtil.getI18nConfig().getEnableDataSource()){
            return;
        }
        i18nUtil.clearCache(0L);

        //shopId=0的店铺在数据库存在zh-CN语言
        String messages = i18nUtil.getMessages(0L,"zh-CN");
        System.out.println(messages);

        //shopId=null -1的店铺在数据库存在zh-CN语言
        messages = i18nUtil.getMessages(-1L,"zh-CN");
        System.out.println(messages);

    }

    @Test
    public void test_34_getMessage(){
        if(!i18nUtil.getI18nConfig().getEnableDataSource()){
            return;
        }
        i18nUtil.clearCache(0L);

        //shopId=0的店铺在数据库存在zh-CN语言
        String message = i18nUtil.getMessage(0L,"zh-CN" , "hshop_web_application_name" , new String[]{"a","b"});
        System.out.println(message);
        assertEquals("HShop应用程序(db4)a-b".equals(message) ,true);

        //shopId=null -1的店铺在数据库存在zh-CN语言
        message = i18nUtil.getMessage(-1L,"zh-CN" , "hshop_web_application_name" , new String[]{"a","b"});
        System.out.println(message);
        assertEquals("HShop应用程序(global)(db4)".equals(message) ,true);

    }


    @Test
    public void test_35_existLanguage(){
        if(!i18nUtil.getI18nConfig().getEnableDataSource()){
            return;
        }
        i18nUtil.clearCache(0L);

        //shopId=0的店铺在数据库存在zh-CN语言
        Boolean rs = i18nUtil.existLanguage(0L,"zh-CN");
        System.out.println(rs);
        assertEquals(rs ,true);

        //shopId=null -1的店铺在数据库存在zh-CN语言
        rs = i18nUtil.existLanguage(-1L,"zh-CN");
        System.out.println(rs);
        assertEquals(rs ,true);

    }

    @Test
    public void test_36_getLanguages(){
        if(!i18nUtil.getI18nConfig().getEnableDataSource()){
            return;
        }
        i18nUtil.clearCache(0L);

        //shopId=0的店铺在数据库语言
        List<Language> langs = i18nUtil.getLanguages(0L);
        System.out.println(langs);
        assertEquals(langs.size()==4 ,true);

        //shopId=-1L的店铺在数据库语言
        langs = i18nUtil.getLanguages(-1L);
        System.out.println(langs);
        assertEquals(langs.size()==4 ,true);

    }

    @Test
    public void test_37_getCurrLanguage(){
        if(!i18nUtil.getI18nConfig().getEnableDataSource()){
            return;
        }
        i18nUtil.clearCache(0L);

        //shopId=0的店铺在数据库语言zh-CN语言
        request.addHeader("Accept-Language" , "zh-CN");
        Language lang = i18nUtil.getCurrLanguage(0L , request);
        System.out.println(lang);
        assertEquals(lang.getLangCode().equals("zh-CN") ,true);

        request.getSession().setAttribute(i18nUtil.getRequestAndSessionAndViewLanguageName(),"zh-CN");
        lang = i18nUtil.getCurrLanguage( -1L , request);
        System.out.println(lang);
        assertEquals(lang.getLangCode().equals("zh-CN") ,true);


    }

    @Test
    public void test_38_setLanguage(){
        if(!i18nUtil.getI18nConfig().getEnableDataSource()){
            return;
        }
        i18nUtil.clearCache(0L);

        //shopId=0的店铺在数据库语言zh-CN语言
        Boolean rs = i18nUtil.setLanguage(request , "zh-CN");
        System.out.println(rs);
        assertEquals(rs ,true);

        String code = i18nUtil.getLanguage(request);
        System.out.println(code);
        assertEquals(code.equals("zh-CN") ,true);


        rs = i18nUtil.setLanguage(request , "en-US");
        System.out.println(rs);
        assertEquals(rs ,true);

        code = i18nUtil.getLanguage(request);
        System.out.println(code);
        assertEquals(code.equals("en-US") ,true);
    }


    @Test
    public void test_39_getDefaultLanguage(){
        if(!i18nUtil.getI18nConfig().getEnableDataSource()){
            return;
        }
        i18nUtil.clearCache(0L);

        //shopId=0的店铺在数据库语言zh-CN语言
        String defaultLanguage = i18nUtil.getDefaultLanguage(0L);
        assertEquals(defaultLanguage.equals("zh-CN") , true);

        //2、shopId=2在数据库中不存在语言配置
        defaultLanguage = i18nUtil.getDefaultLanguage(2L);
        assertEquals(defaultLanguage == null , true);


        //3、修改默认shopId，获取当前默认shopId的自定义语言
        request.setAttribute(WebUtil.APP_SHOP_ID_NAME , 0L);
        defaultLanguage = i18nUtil.getDefaultLanguage(request);
        assertEquals(defaultLanguage.equals("zh-CN") , true);


        //4、修改默认shopId，获取当前默认shopId的自定义语言
        WebUtil.setShopId(request , 2L);
        defaultLanguage = i18nUtil.getDefaultLanguage(request);
        assertEquals(defaultLanguage==null , true);


    }

}
