package com.ihltx.utility.weixin.impl;

import com.ihltx.utility.httpclient.service.RestTemplateUtil;
import com.ihltx.utility.httpclient.service.impl.RestTemplateUtilImpl;
import com.ihltx.utility.redis.exception.RedisException;
import com.ihltx.utility.redis.service.RedisFactory;
import com.ihltx.utility.redis.service.RedisUtil;
import com.ihltx.utility.util.*;
import com.ihltx.utility.weixin.WeixinUtil;
import com.ihltx.utility.weixin.entity.*;
import com.ihltx.utility.weixin.event.MessageEvent;
import com.ihltx.utility.weixin.event.MessageEventHandler;
import com.ihltx.utility.weixin.event.MessageEventType;
import com.ihltx.utility.weixin.tools.WeChatContant;
import com.ihltx.utility.weixin.tools.WeChatUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * WeixinUtil
 * WeixinUtil utility class
 * @author liulin 84611913@qq.com
 *
 */
public class WeixinUtilImpl implements WeixinUtil {
    private static final String WEIXIN_CHARSET = "ISO-8859-1";
    private static final String ACCESSTOKEN_PREFIX = "weixin:accesstoken:";
    private static final String JSAPI_TICKET_PREFIX = "weixin:jsapiticket:";
    private static final String OAUTH2ACCESSTOKEN_PREFIX = "weixin:oauth2accesstoken:";
    private static final String OAUTH2REFRESHACCESSTOKEN_PREFIX = "weixin:oauth2refreshaccesstoken:";
    private static final long OAUTH2REFRESHACCESSTOKEN_EXPIRES = 30 * 24 * 3600;


    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    private String appID;
    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    private String appSecret;
    @Override
    public String getAppSecret() {
        return appSecret;
    }

    @Override
    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    private String token;
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String encodingAESKey;
    public String getEncodingAESKey() {
        return encodingAESKey;
    }

    public void setEncodingAESKey(String encodingAESKey) {
        this.encodingAESKey = encodingAESKey;
    }


    private RedisFactory redisFactory;
    @Override
    public void setRedisFactory(RedisFactory redisFactory) {
        this.redisFactory = redisFactory;
    }

    @Override
    public RedisFactory getRedisFactory() {
        return this.redisFactory;
    }

    private String redisName;
    @Override
    public void setAccessTokenRedisStorageName(String redisName) {
        this.redisName = redisName;
    }

    @Override
    public String getAccessTokenRedisStorageName() {
        return this.redisName;
    }

    private Boolean enableAccessTokenRedisStorage;
    @Override
    public void setEnableAccessTokenRedisStorage(Boolean enable) {
        this.enableAccessTokenRedisStorage = enable;
    }

    @Override
    public Boolean getEnableAccessTokenRedisStorage() {
        return this.enableAccessTokenRedisStorage;
    }

    /**
     * @param encodingAESType   消息加解密方式：
     *                          0--明文模式           明文模式下，不使用消息体加解密功能，安全系数较低
     *                          1--兼容模式           兼容模式下，明文、密文将共存，方便开发者调试和维护
     *                          2--安全模式（推荐）     安全模式下，消息包为纯密文，需要开发者加密和解密，安全系数高
     */
    private Integer encodingAESType = 1;
    public Integer getEcodingAESType() {
        return encodingAESType;
    }
    public void setEcodingAESType(Integer encodingAESType) {
        this.encodingAESType = encodingAESType;
    }

    private List<MessageEvent> messageEvents;
    @Override
    public void setMessageEvents(List<MessageEvent> messageEvents) {
        this.messageEvents = messageEvents;
    }

    @Override
    public List<MessageEvent> getMessageEvents() {
        return this.messageEvents;
    }

    public WeixinUtilImpl(){
        init();
    }

    public WeixinUtilImpl(String appID , String appSecret , String token , String encodingAESKey , Integer encodingAESType){
        this.appID = appID;
        this.appSecret = appSecret;
        this.token = token;
        this.encodingAESKey = encodingAESKey;
        this.encodingAESType = encodingAESType;
        init();
    }

    public WeixinUtilImpl(String appID  ,String appSecret , String token , String encodingAESKey){
        this.appID = appID;
        this.appSecret = appSecret;
        this.token = token;
        this.encodingAESKey = encodingAESKey;
        init();
    }



    private RestTemplateUtil restTemplateUtil;
    public RestTemplateUtil getRestTemplateUtil() {
        return restTemplateUtil;
    }

    public void setRestTemplateUtil(RestTemplateUtil restTemplateUtil) {
        this.restTemplateUtil = restTemplateUtil;
    }


    private void init(){
        if(this.restTemplateUtil==null){
            restTemplateUtil =  new RestTemplateUtilImpl(10000 , 30000);
        }
    }

    private String accessTokenProxyUrl;
    @Override
    public void setAccessTokenProxyUrl(String proxyUrl) {
        this.accessTokenProxyUrl = proxyUrl;
    }

    @Override
    public String getAccessTokenProxyUrl() {
        return this.accessTokenProxyUrl;
    }

    @Override
    public String sendTextMsg(Map<String, String> requestBody, String message) {
        return  WeChatUtil.sendTextMsg(requestBody , message);
    }

    @Override
    public String sendImageMsg(Map<String, String> requestBody, String media_id) {
        return  WeChatUtil.sendImageMsg(requestBody , media_id);
    }

    @Override
    public String sendVoiceMsg(Map<String, String> requestBody, String media_id) {
        return  WeChatUtil.sendVoiceMsg(requestBody , media_id);
    }

    @Override
    public String sendVideoMsg(Map<String, String> requestBody, String media_id, String title, String description) {
        return  WeChatUtil.sendVideoMsg(requestBody , media_id , title , description);
    }

    @Override
    public String sendMusicMsg(Map<String, String> requestBody, String title, String description, String musicUrl, String hQMusicUrl, String thumbMediaId) {
        return  WeChatUtil.sendMusicMsg(requestBody , title , description , musicUrl, hQMusicUrl , thumbMediaId);
    }

    @Override
    public String sendArticleMsg(Map<String, String> requestBody, List<ArticleItem> items) {
        return WeChatUtil.sendArticleMsg(requestBody , items);
    }

    @Override
    public String sendTransferCustomerService(Map<String, String> requestBody, String KfAccount) {
        return WeChatUtil.sendTransferCustomerService(requestBody , KfAccount);
    }

    @Override
    public JSONObject getAccessToken() {
        return getAccessToken(null);
    }

    @Override
    public JSONObject getAccessToken(String proxy_api_url) {
        String key = ACCESSTOKEN_PREFIX + appID;
        String api_url = WEIXIN_API_URL + "/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
        api_url = String.format(api_url , appID , appSecret);
        if(!StringUtil.isNullOrEmpty(proxy_api_url)){
            try {
                api_url = proxy_api_url + "?url=" + SecurityUtil.urlEncode(api_url);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        }
        JSONObject jsonObject = null;
        if(this.getEnableAccessTokenRedisStorage() && !StringUtil.isNullOrEmpty(this.getAccessTokenRedisStorageName()) && this.redisFactory!=null){
            try {
                RedisUtil redisUtil = this.redisFactory.openSession(this.getAccessTokenRedisStorageName());
                jsonObject = redisUtil.get(key , JSONObject.class);
                if(jsonObject !=null && jsonObject.containsKey("access_token")){
                    return jsonObject;
                }
            } catch (RedisException e) {
                e.printStackTrace();
            }
        }
        String rs = restTemplateUtil.get(api_url);
        if(!StringUtil.isNullOrEmpty(rs)){
            jsonObject = JSONObject.parseObject(rs);
            if(this.getEnableAccessTokenRedisStorage() && !StringUtil.isNullOrEmpty(this.getAccessTokenRedisStorageName()) && this.redisFactory!=null){
                try {
                    RedisUtil redisUtil = this.redisFactory.openSession(this.getAccessTokenRedisStorageName());
                    if(jsonObject.containsKey("access_token")){
                        redisUtil.set(key , jsonObject , jsonObject.getLong("expires_in"));
                    }
                } catch (RedisException e) {
                    e.printStackTrace();
                }
            }
        }

        return jsonObject;
    }

    @Override
    public List<String> getApiDomainIp() {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }

        String api_url = WEIXIN_API_URL + "/cgi-bin/get_api_domain_ip?access_token=%s";
        api_url = String.format(api_url , access_token);
        jsonObject = JSONObject.parseObject(restTemplateUtil.get(api_url));
        if(jsonObject.containsKey("ip_list")){
            return JSONArray.parseArray(jsonObject.getString("ip_list") , String.class);
        }
        return null;
    }

    @Override
    public Boolean createMenu(Menu menu) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }

        String api_url = WEIXIN_API_URL + "/cgi-bin/menu/create?access_token=%s";
        api_url = String.format(api_url , access_token);

        jsonObject = JSONObject.parseObject(restTemplateUtil.post(api_url , menu));
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getInteger("errcode")==0){
            return  true;
        }
        return false;
    }

    @Override
    public Boolean deleteMenu() {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/menu/delete?access_token=%s";
        api_url = String.format(api_url , access_token);

        jsonObject = JSONObject.parseObject(restTemplateUtil.get(api_url));
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getInteger("errcode")==0){
            return  true;
        }
        return false;
    }

    @Override
    public String createConditionalMenu(ConditionalMenu menu) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/menu/addconditional?access_token=%s";
        api_url = String.format(api_url , access_token);

        jsonObject = JSONObject.parseObject(restTemplateUtil.postForObject(api_url , menu , String.class));
        if(jsonObject!=null && jsonObject.containsKey("menuid")){
            return  jsonObject.getString("menuid");
        }
        return null;
    }

    @Override
    public Boolean deleteConditionalMenu(String menuid) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/menu/delconditional?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData =new HashMap<>();
        postData.put("menuid" , menuid);
        jsonObject = JSONObject.parseObject(restTemplateUtil.postForObject(api_url , postData , String.class));
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getInteger("errcode")==0){
            return  true;
        }
        return null;
    }

    @Override
    public List<Button> getConditionalMenu(String user_id) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        Menu menu =new Menu();
        List<Button> buttons = null;
        String api_url = WEIXIN_API_URL + "/cgi-bin/menu/trymatch?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData =new HashMap<>();
        postData.put("user_id" , user_id);

        jsonObject = JSONObject.parseObject(restTemplateUtil.postForObject(api_url , postData , String.class));
        if(jsonObject!=null && jsonObject.containsKey("button")){
            JSONArray jsonArray = jsonObject.getJSONArray("button");
            if(jsonArray!=null &&  jsonArray.size()>0){
                buttons = new ArrayList<>();
                for(int i=0; i<jsonArray.size();i++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    Button button =new Button();
                    if(jsonObject1.containsKey("type")){
                        button.setType(jsonObject1.getString("type"));
                    }
                    if(jsonObject1.containsKey("name")){
                        button.setName(jsonObject1.getString("name"));
                    }
                    if(jsonObject1.containsKey("key")){
                        button.setKey(jsonObject1.getString("key"));
                    }
                    if(jsonObject1.containsKey("url")){
                        button.setUrl(jsonObject1.getString("url"));
                    }
                    if(jsonObject1.containsKey("media_id")){
                        button.setMedia_id(jsonObject1.getString("media_id"));
                    }
                    if(jsonObject1.containsKey("appid")){
                        button.setAppid(jsonObject1.getString("appid"));
                    }
                    if(jsonObject1.containsKey("pagepath")){
                        button.setPagepath(jsonObject1.getString("pagepath"));
                    }
                    if(jsonObject1.containsKey("sub_button") && jsonObject1.getJSONObject("sub_button").containsKey("list")  && jsonObject1.getJSONObject("sub_button").getJSONArray("list").size()>0){
                        List<Button> subButtons = new ArrayList<>();
                        JSONArray jsonArray1 = jsonObject1.getJSONObject("sub_button").getJSONArray("list");
                        for (int j=0;j< jsonArray1.size();j++){
                            JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                            Button button1 =new Button();
                            if(jsonObject2.containsKey("type")){
                                button1.setType(jsonObject2.getString("type"));
                            }
                            if(jsonObject2.containsKey("name")){
                                button1.setName(jsonObject2.getString("name"));
                            }
                            if(jsonObject2.containsKey("key")){
                                button1.setKey(jsonObject2.getString("key"));
                            }
                            if(jsonObject2.containsKey("api")){
                                button1.setUrl(jsonObject2.getString("api"));
                            }
                            if(jsonObject2.containsKey("media_id")){
                                button1.setMedia_id(jsonObject2.getString("media_id"));
                            }
                            if(jsonObject2.containsKey("appid")){
                                button1.setAppid(jsonObject2.getString("appid"));
                            }
                            if(jsonObject2.containsKey("pagepath")){
                                button1.setPagepath(jsonObject2.getString("pagepath"));
                            }
                            subButtons.add(button1);
                        }
                        button.setSub_button(subButtons);
                    }
                    buttons.add(button);
                }
            }
        }
        return buttons;
    }

    @Override
    public Menu getMenu(){
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        Menu menu =new Menu();
        List<Button> buttons = null;
        String api_url = WEIXIN_API_URL + "/cgi-bin/get_current_selfmenu_info?access_token=%s";
        api_url = String.format(api_url , access_token);
        jsonObject = JSONObject.parseObject(restTemplateUtil.get(api_url));
        if(jsonObject!=null && jsonObject.containsKey("is_menu_open") && jsonObject.containsKey("selfmenu_info")){
            if(jsonObject.getInteger("is_menu_open")==1){
                menu.setIsMenuOpen(true);
            }else{
                menu.setIsMenuOpen(false);
            }
            if(jsonObject.containsKey("selfmenu_info") && jsonObject.getJSONObject("selfmenu_info").containsKey("button")){
                JSONArray jsonArray = jsonObject.getJSONObject("selfmenu_info").getJSONArray("button");
                if(jsonArray!=null &&  jsonArray.size()>0){
                    buttons = new ArrayList<>();
                    for(int i=0; i<jsonArray.size();i++){
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        Button button =new Button();
                        if(jsonObject1.containsKey("type")){
                            button.setType(jsonObject1.getString("type"));
                        }
                        if(jsonObject1.containsKey("name")){
                            button.setName(jsonObject1.getString("name"));
                        }
                        if(jsonObject1.containsKey("key")){
                            button.setKey(jsonObject1.getString("key"));
                        }
                        if(jsonObject1.containsKey("api")){
                            button.setUrl(jsonObject1.getString("api"));
                        }
                        if(jsonObject1.containsKey("media_id")){
                            button.setMedia_id(jsonObject1.getString("media_id"));
                        }
                        if(jsonObject1.containsKey("appid")){
                            button.setAppid(jsonObject1.getString("appid"));
                        }
                        if(jsonObject1.containsKey("pagepath")){
                            button.setPagepath(jsonObject1.getString("pagepath"));
                        }
                        if(jsonObject1.containsKey("sub_button") && jsonObject1.getJSONObject("sub_button").containsKey("list")  && jsonObject1.getJSONObject("sub_button").getJSONArray("list").size()>0){
                            List<Button> subButtons = new ArrayList<>();
                            JSONArray jsonArray1 = jsonObject1.getJSONObject("sub_button").getJSONArray("list");
                            for (int j=0;j< jsonArray1.size();j++){
                                JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                                Button button1 =new Button();
                                if(jsonObject2.containsKey("type")){
                                    button1.setType(jsonObject2.getString("type"));
                                }
                                if(jsonObject2.containsKey("name")){
                                    button1.setName(jsonObject2.getString("name"));
                                }
                                if(jsonObject2.containsKey("key")){
                                    button1.setKey(jsonObject2.getString("key"));
                                }
                                if(jsonObject2.containsKey("api")){
                                    button1.setUrl(jsonObject2.getString("api"));
                                }
                                if(jsonObject2.containsKey("media_id")){
                                    button1.setMedia_id(jsonObject2.getString("media_id"));
                                }
                                if(jsonObject2.containsKey("appid")){
                                    button1.setAppid(jsonObject2.getString("appid"));
                                }
                                if(jsonObject2.containsKey("pagepath")){
                                    button1.setPagepath(jsonObject2.getString("pagepath"));
                                }
                                subButtons.add(button1);
                            }
                            button.setSub_button(subButtons);
                        }
                        buttons.add(button);
                    }
                    menu.setButton(buttons);
                }
            }
        }
        return menu;
    }

    @Override
    public Menu getConditionalMenu(){
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        Menu menu =new Menu();
        List<Button> buttons = null;
        String api_url = WEIXIN_API_URL + "/cgi-bin/menu/get?access_token=%s";
        api_url = String.format(api_url , access_token);
        jsonObject = JSONObject.parseObject(restTemplateUtil.get(api_url));
        if(jsonObject!=null && jsonObject.containsKey("menu")){
            if(jsonObject.getJSONObject("menu").containsKey("menuid")){
                menu.setMenuid(jsonObject.getJSONObject("menu").getString("menuid"));
            }

            if(jsonObject.getJSONObject("menu").containsKey("button")){
                JSONArray jsonArray = jsonObject.getJSONObject("menu").getJSONArray("button");
                if(jsonArray!=null &&  jsonArray.size()>0){
                    buttons = new ArrayList<>();
                    for(int i=0; i<jsonArray.size();i++){
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        Button button =new Button();
                        if(jsonObject1.containsKey("type")){
                            button.setType(jsonObject1.getString("type"));
                        }
                        if(jsonObject1.containsKey("name")){
                            button.setName(jsonObject1.getString("name"));
                        }
                        if(jsonObject1.containsKey("key")){
                            button.setKey(jsonObject1.getString("key"));
                        }
                        if(jsonObject1.containsKey("api")){
                            button.setUrl(jsonObject1.getString("api"));
                        }
                        if(jsonObject1.containsKey("media_id")){
                            button.setMedia_id(jsonObject1.getString("media_id"));
                        }
                        if(jsonObject1.containsKey("appid")){
                            button.setAppid(jsonObject1.getString("appid"));
                        }
                        if(jsonObject1.containsKey("pagepath")){
                            button.setPagepath(jsonObject1.getString("pagepath"));
                        }
                        if(jsonObject1.containsKey("sub_button") && jsonObject1.getJSONObject("sub_button").containsKey("list")  && jsonObject1.getJSONObject("sub_button").getJSONArray("list").size()>0){
                            List<Button> subButtons = new ArrayList<>();
                            JSONArray jsonArray1 = jsonObject1.getJSONObject("sub_button").getJSONArray("list");
                            for (int j=0;j< jsonArray1.size();j++){
                                JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                                Button button1 =new Button();
                                if(jsonObject2.containsKey("type")){
                                    button1.setType(jsonObject2.getString("type"));
                                }
                                if(jsonObject2.containsKey("name")){
                                    button1.setName(jsonObject2.getString("name"));
                                }
                                if(jsonObject2.containsKey("key")){
                                    button1.setKey(jsonObject2.getString("key"));
                                }
                                if(jsonObject2.containsKey("api")){
                                    button1.setUrl(jsonObject2.getString("api"));
                                }
                                if(jsonObject2.containsKey("media_id")){
                                    button1.setMedia_id(jsonObject2.getString("media_id"));
                                }
                                if(jsonObject2.containsKey("appid")){
                                    button1.setAppid(jsonObject2.getString("appid"));
                                }
                                if(jsonObject2.containsKey("pagepath")){
                                    button1.setPagepath(jsonObject2.getString("pagepath"));
                                }
                                subButtons.add(button1);
                            }
                            button.setSub_button(subButtons);
                        }
                        buttons.add(button);
                    }
                    menu.setButton(buttons);
                }
            }
        }

        if(jsonObject!=null && jsonObject.containsKey("conditionalmenu")){
            Map<String , ConditionalMenu> conditionalMenuMap = null;
            JSONArray jsonArray = jsonObject.getJSONArray("conditionalmenu");
            if(jsonArray!=null && jsonArray.size()>0){
                conditionalMenuMap = new HashMap<>();
                for(int i=0;i< jsonArray.size(); i++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String menuid= null;
                    if(jsonObject1.containsKey("menuid")){
                        menuid = jsonObject1.getString("menuid");
                    }
                    ConditionalMenu conditionalMenu =new ConditionalMenu();

                    if(jsonObject1.containsKey("matchrule")){
                        JSONObject matchruleJSONObject = jsonObject1.getJSONObject("matchrule");
                        Matchrule matchrule = new Matchrule();
                        if(matchruleJSONObject.containsKey("tag_id")){
                            matchrule.setTag_id(matchruleJSONObject.getString("tag_id"));
                        }
                        if(matchruleJSONObject.containsKey("sex")){
                            matchrule.setSex(matchruleJSONObject.getString("sex"));
                        }
                        if(matchruleJSONObject.containsKey("country")){
                            matchrule.setCountry(matchruleJSONObject.getString("country"));
                        }
                        if(matchruleJSONObject.containsKey("province")){
                            matchrule.setProvince(matchruleJSONObject.getString("province"));
                        }
                        if(matchruleJSONObject.containsKey("city")){
                            matchrule.setCity(matchruleJSONObject.getString("city"));
                        }
                        if(matchruleJSONObject.containsKey("client_platform_type")){
                            matchrule.setClient_platform_type(matchruleJSONObject.getString("client_platform_type"));
                        }
                        if(matchruleJSONObject.containsKey("language")){
                            matchrule.setLanguage(matchruleJSONObject.getString("language"));
                        }
                        conditionalMenu.setMatchrule(matchrule);
                    }
                    if(jsonObject1.containsKey("button")){
                        JSONArray jsonArray1 = jsonObject1.getJSONArray("button");
                        if(jsonArray1!=null &&  jsonArray1.size()>0){
                            buttons = new ArrayList<>();
                            for(int j=0; j<jsonArray1.size();j++){
                                JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                                Button button =new Button();
                                if(jsonObject2.containsKey("type")){
                                    button.setType(jsonObject2.getString("type"));
                                }
                                if(jsonObject2.containsKey("name")){
                                    button.setName(jsonObject2.getString("name"));
                                }
                                if(jsonObject2.containsKey("key")){
                                    button.setKey(jsonObject2.getString("key"));
                                }
                                if(jsonObject2.containsKey("api")){
                                    button.setUrl(jsonObject2.getString("api"));
                                }
                                if(jsonObject2.containsKey("media_id")){
                                    button.setMedia_id(jsonObject2.getString("media_id"));
                                }
                                if(jsonObject2.containsKey("appid")){
                                    button.setAppid(jsonObject2.getString("appid"));
                                }
                                if(jsonObject2.containsKey("pagepath")){
                                    button.setPagepath(jsonObject2.getString("pagepath"));
                                }
                                if(jsonObject2.containsKey("sub_button") && jsonObject2.getJSONArray("sub_button").size()>0){
                                    List<Button> subButtons = new ArrayList<>();
                                    JSONArray jsonArray2 = jsonObject2.getJSONArray("sub_button");
                                    for (int k=0;k< jsonArray2.size();k++){
                                        JSONObject jsonObject3 = jsonArray2.getJSONObject(k);
                                        Button button1 =new Button();
                                        if(jsonObject3.containsKey("type")){
                                            button1.setType(jsonObject3.getString("type"));
                                        }
                                        if(jsonObject3.containsKey("name")){
                                            button1.setName(jsonObject3.getString("name"));
                                        }
                                        if(jsonObject3.containsKey("key")){
                                            button1.setKey(jsonObject3.getString("key"));
                                        }
                                        if(jsonObject3.containsKey("api")){
                                            button1.setUrl(jsonObject3.getString("api"));
                                        }
                                        if(jsonObject3.containsKey("media_id")){
                                            button1.setMedia_id(jsonObject3.getString("media_id"));
                                        }
                                        if(jsonObject3.containsKey("appid")){
                                            button1.setAppid(jsonObject3.getString("appid"));
                                        }
                                        if(jsonObject3.containsKey("pagepath")){
                                            button1.setPagepath(jsonObject3.getString("pagepath"));
                                        }
                                        subButtons.add(button1);
                                    }
                                    button.setSub_button(subButtons);
                                }
                                buttons.add(button);
                            }
                            conditionalMenu.setButton(buttons);
                        }
                    }

                    conditionalMenuMap.put(menuid , conditionalMenu);
                }
            }
            menu.setConditionalmenu(conditionalMenuMap);
        }

        return menu;
    }

    @Override
    public Map<String, Object> getUserInfo(String openid) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";
        api_url = String.format(api_url , access_token , openid);
        jsonObject = JSONObject.parseObject(restTemplateUtil.get(api_url));
        return ObjectUtil.JsonObject2Map(jsonObject);
    }


    @Override
    public String messageServiceCenter(HttpServletRequest request) {
        // xml格式的消息数据
        String respXml = null;
        // 默认返回的文本消息内容
        String respContent;
        NewsMaterial newsMaterial = null;
        MessageEventHandler messageEventHandler = new MessageEventHandler();
        //微信服务器消息推送
        try {
            Map<String,String> requestMap = WeChatUtil.parseXml(request);
            // 消息类型
            String msgType = requestMap.get(WeChatContant.MsgType).toString();
            Integer CreateTime= Integer.valueOf(requestMap.get("CreateTime").toString());
            String toUserName= requestMap.get("ToUserName").toString();
            String fromOpenid= requestMap.get("FromUserName").toString();
            Long msgId = 0L;
            if(requestMap.containsKey("MsgId")){
                msgId = Long.valueOf(requestMap.get("MsgId").toString());
            }
            String keywords = null;
            Integer messageType = MessageEventType.MESSAGE_TYPE_DEFAULT;
            // 文本消息
            if (msgType.equalsIgnoreCase(WeChatContant.REQ_MESSAGE_TYPE_TEXT)) {
                messageType = MessageEventType.MESSAGE_TYPE_KEYWORDS;
                keywords =requestMap.get(WeChatContant.Content).toString();
                if(requestMap.containsKey("bizmsgmenuid")){
                    messageType = MessageEventType.MESSAGE_TYPE_KEYWORDS_MSGMENU;
                }else{
                    if(keywords.startsWith("百科")){
                        keywords = keywords.replaceAll("百科\\+?", "");
                        messageType = MessageEventType.MESSAGE_TYPE_KEYWORDS_BAIKE;
                    }else if(keywords.startsWith("@")){
                        keywords = keywords.replaceAll("@\\+?", "");
                        messageType = MessageEventType.MESSAGE_TYPE_KEYWORDS_TRANSLAE;
                    }else if(keywords.startsWith("天气")){
                        keywords = keywords.replaceAll("天气\\+?", "");
                        messageType = MessageEventType.MESSAGE_TYPE_KEYWORDS_WEATHER;
                    }else if(keywords.startsWith("快递")){
                        keywords = keywords.replaceAll("快递\\+?", "");
                        messageType = MessageEventType.MESSAGE_TYPE_KEYWORDS_EXPRESS;
                    }else if("日历".equals(keywords)){
                        messageType = MessageEventType.MESSAGE_TYPE_KEYWORDS_CALENDAR;
                    }else if("新闻".equals(keywords)){
                        messageType = MessageEventType.MESSAGE_TYPE_KEYWORDS_NEWS;
                    }
                }
            }
            // 图片消息
            else if (msgType.equalsIgnoreCase(WeChatContant.REQ_MESSAGE_TYPE_IMAGE)) {
                messageType = MessageEventType.MESSAGE_TYPE_IMAGE;
            }
            // 语音消息
            else if (msgType.equalsIgnoreCase(WeChatContant.REQ_MESSAGE_TYPE_VOICE)) {
                messageType = MessageEventType.MESSAGE_TYPE_VOICE;
            }
            // 视频消息
            else if (msgType.equalsIgnoreCase(WeChatContant.REQ_MESSAGE_TYPE_VIDEO)) {
                messageType = MessageEventType.MESSAGE_TYPE_VIDEO;
            } // 小视频消息
            else if (msgType.equalsIgnoreCase(WeChatContant.REQ_MESSAGE_TYPE_SHORT_VIDEO)) {
                messageType = MessageEventType.MESSAGE_TYPE_SHORT_VIDEO;
            }
            // 地理位置消息
            else if (msgType.equalsIgnoreCase(WeChatContant.REQ_MESSAGE_TYPE_LOCATION)) {
                messageType = MessageEventType.MESSAGE_TYPE_LOCATION;
            }
            // 链接消息
            else if (msgType.equalsIgnoreCase(WeChatContant.REQ_MESSAGE_TYPE_LINK)) {
                messageType = MessageEventType.MESSAGE_TYPE_LINK;
            }
            // 事件推送
            else if (msgType.equalsIgnoreCase(WeChatContant.REQ_MESSAGE_TYPE_EVENT)) {
                messageType = MessageEventType.MESSAGE_TYPE_EVENT;
                // 事件类型
                String eventType = (String) requestMap.get(WeChatContant.Event);
                // 关注
                if (eventType.equalsIgnoreCase(WeChatContant.EVENT_TYPE_SUBSCRIBE)) {
                    messageType = MessageEventType.MESSAGE_TYPE_EVENT_SUBSCRIBE;
                }
                // 取消关注
                else if (eventType.equalsIgnoreCase(WeChatContant.EVENT_TYPE_UNSUBSCRIBE)) {
                    // 取消订阅后用户不会再收到公众账号发送的消息，因此不需要回复
                    messageType = MessageEventType.MESSAGE_TYPE_EVENT_UNSUBSCRIBE;
                }
                // 扫描带提示
                else if (eventType.equalsIgnoreCase(WeChatContant.EVENT_TYPE_SCANCODE_WAITMSG)) {
                    // 处理扫描带提示
                    messageType = MessageEventType.MESSAGE_TYPE_EVENT_SCANCODE_WAITMSG;
                }
                // 扫描推事件
                else if (eventType.equalsIgnoreCase(WeChatContant.EVENT_TYPE_SCANCODE_PUSH)) {
                    // 扫描推事件
                    messageType = MessageEventType.MESSAGE_TYPE_EVENT_SCANCODE_PUSH;
                }
                // 扫描带参数二维码
                else if (eventType.equalsIgnoreCase(WeChatContant.EVENT_TYPE_SCAN)) {
                    // 处理扫描带参数二维码事件
                    messageType = MessageEventType.MESSAGE_TYPE_EVENT_SCAN;
                }
                // 上报地理位置
                else if (eventType.equalsIgnoreCase(WeChatContant.EVENT_TYPE_LOCATION)) {
                    // 处理上报地理位置事件
                    messageType = MessageEventType.MESSAGE_TYPE_EVENT_LOCATION;
                }
                // 自定义菜单
                else if (eventType.equalsIgnoreCase(WeChatContant.EVENT_TYPE_CLICK)) {
                    // 处理菜单点击事件
                    messageType = MessageEventType.MESSAGE_TYPE_EVENT_CLICK;
                }// VIEW
                else if (eventType.equalsIgnoreCase(WeChatContant.EVENT_TYPE_VIEW)) {
                    // 处理菜单点击事件
                    messageType = MessageEventType.MESSAGE_TYPE_EVENT_VIEW;
                }// view_miniprogram
                else if (eventType.equalsIgnoreCase(WeChatContant.EVENT_TYPE_VIEW_MINIPROGRAM)) {
                    // 点击菜单跳转小程序的事件
                    messageType = MessageEventType.MESSAGE_TYPE_EVENT_VIEW_MINIPROGRAM;
                }// pic_sysphoto
                else if (eventType.equalsIgnoreCase(WeChatContant.EVENT_TYPE_PIC_SYSPHOTO)) {
                    // 系统拍照发图事件
                    messageType = MessageEventType.MESSAGE_TYPE_EVENT_PIC_SYSPHOTO;
                }// pic_photo_or_album
                else if (eventType.equalsIgnoreCase(WeChatContant.EVENT_TYPE_PIC_PHOTO_OR_ALBUM)) {
                    // 拍照或者相册发图
                    messageType = MessageEventType.MESSAGE_TYPE_EVENT_PIC_PHOTO_OR_ALBUM;
                }// pic_weixin
                else if (eventType.equalsIgnoreCase(WeChatContant.EVENT_TYPE_PIC_WEIXIN)) {
                    // 微信相册发图
                    messageType = MessageEventType.MESSAGE_TYPE_EVENT_PIC_WEIXIN;
                }// location_select
                else if (eventType.equalsIgnoreCase(WeChatContant.EVENT_TYPE_LOCATION_SELECT)) {
                    // 发送位置
                    messageType = MessageEventType.MESSAGE_TYPE_EVENT_LOCATION_SELECT;
                }// TEMPLATESENDJOBFINISH
                else if (eventType.equalsIgnoreCase(WeChatContant.EVENT_TYPE_TEMPLATESENDJOBFINISH)) {
                    // 模版消息送达时的事件推送
                    messageType = MessageEventType.MESSAGE_TYPE_EVENT_TEMPLATESENDJOBFINISH;
                }// TEMPLATESENDJOBFINISH
                else if (eventType.equalsIgnoreCase(WeChatContant.EVENT_TYPE_MASSSENDJOBFINISH)) {
                    // 事件推送群发结果
                    messageType = MessageEventType.MESSAGE_TYPE_EVENT_MASSSENDJOBFINISH;
                }// subscribe_msg_popup_event
                else if (eventType.equalsIgnoreCase(WeChatContant.EVENT_TYPE_SUBSCRIBE_MSG_POPUP)) {
                    // 用户操作订阅通知弹窗
                    messageType = MessageEventType.MESSAGE_TYPE_EVENT_SUBSCRIBE_MSG_POPUP;
                }// subscribe_msg_change_event
                else if (eventType.equalsIgnoreCase(WeChatContant.EVENT_TYPE_SUBSCRIBE_MSG_CHANGE)) {
                    // 用户管理订阅通知
                    messageType = MessageEventType.MESSAGE_TYPE_EVENT_SUBSCRIBE_MSG_CHANGE;
                }// subscribe_msg_sent_event
                else if (eventType.equalsIgnoreCase(WeChatContant.EVENT_TYPE_SUBSCRIBE_MSG_SENT)) {
                    // 发送订阅通知
                    messageType = MessageEventType.MESSAGE_TYPE_EVENT_SUBSCRIBE_MSG_SENT;
                }// FREEPUBLISHJOBFINISH
                else if (eventType.equalsIgnoreCase(WeChatContant.EVENT_TYPE_FREEPUBLISHJOBFINISH)) {
                    // 草稿发布事件推送通知
                    messageType = MessageEventType.MESSAGE_TYPE_EVENT_FREEPUBLISHJOBFINISH;
                }// qualification_verify_success
                else if (eventType.equalsIgnoreCase(WeChatContant.EVENT_TYPE_QUALIFICATION_VERIFY_SUCCESS)) {
                    // 资质认证成功（此时立即获得接口权限）
                    messageType = MessageEventType.MESSAGE_TYPE_EVENT_QUALIFICATION_VERIFY_SUCCESS;
                }// qualification_verify_fail
                else if (eventType.equalsIgnoreCase(WeChatContant.EVENT_TYPE_QUALIFICATION_VERIFY_FAIL)) {
                    // 资质认证失败
                    messageType = MessageEventType.MESSAGE_TYPE_EVENT_QUALIFICATION_VERIFY_FAIL;
                }// naming_verify_success
                else if (eventType.equalsIgnoreCase(WeChatContant.EVENT_TYPE_NAMING_VERIFY_SUCCESS)) {
                    // 名称认证成功（即命名成功）
                    messageType = MessageEventType.MESSAGE_TYPE_EVENT_NAMING_VERIFY_SUCCESS;
                }// naming_verify_fail
                else if (eventType.equalsIgnoreCase(WeChatContant.EVENT_TYPE_NAMING_VERIFY_FAIL)) {
                    // 名称认证失败（这时虽然客户端不打勾，但仍有接口权限）
                    messageType = MessageEventType.MESSAGE_TYPE_EVENT_NAMING_VERIFY_FAIL;
                }// annual_renew
                else if (eventType.equalsIgnoreCase(WeChatContant.EVENT_TYPE_ANNUAL_RENEW)) {
                    // 年审通知
                    messageType = MessageEventType.MESSAGE_TYPE_EVENT_ANNUAL_RENEW;
                }// verify_expired
                else if (eventType.equalsIgnoreCase(WeChatContant.EVENT_TYPE_VERIFY_EXPIRED)) {
                    // 认证过期失效通知
                    messageType = MessageEventType.MESSAGE_TYPE_EVENT_VERIFY_EXPIRED;
                }
            }
            if(messageType>=0 && messageType<=99){
                //用户发送关键字消息
                if(this.getMessageEvents()!=null){
                    for(MessageEvent messageEvent: this.getMessageEvents()){
                        if(messageEvent.getEnabled() && messageEvent.getMessage_type()>=0 && messageEvent.getMessage_type()<=99){
                            if(messageEvent.getMessage_type()== MessageEventType.MESSAGE_TYPE_KEYWORDS_BAIKE && messageType== MessageEventType.MESSAGE_TYPE_KEYWORDS_BAIKE){
                                if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass()) && this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                                respXml = messageEventHandler.callBackBaike(this , messageEvent , requestMap , keywords);
                                break;
                            }else if(messageEvent.getMessage_type()== MessageEventType.MESSAGE_TYPE_KEYWORDS_TRANSLAE && messageType== MessageEventType.MESSAGE_TYPE_KEYWORDS_TRANSLAE){
                                if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass()) && this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                                respXml = messageEventHandler.callBackTranslate(this ,messageEvent , requestMap , keywords);
                                break;
                            }else if(messageEvent.getMessage_type()== MessageEventType.MESSAGE_TYPE_KEYWORDS_WEATHER && messageType== MessageEventType.MESSAGE_TYPE_KEYWORDS_WEATHER){
                                if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass()) && this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                                respXml = messageEventHandler.callBackWeather(this ,messageEvent , requestMap , keywords);
                                break;
                            }else if(messageEvent.getMessage_type()== MessageEventType.MESSAGE_TYPE_KEYWORDS_EXPRESS && messageType== MessageEventType.MESSAGE_TYPE_KEYWORDS_EXPRESS){
                                if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass()) && this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                                respXml = messageEventHandler.callBackExpress(this ,messageEvent , requestMap , keywords);
                                break;
                            }else if(messageEvent.getMessage_type()== MessageEventType.MESSAGE_TYPE_KEYWORDS_CALENDAR && messageType== MessageEventType.MESSAGE_TYPE_KEYWORDS_CALENDAR){
                                if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass()) && this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                                respXml = messageEventHandler.callBackCalendar(this ,messageEvent , requestMap , keywords);
                                break;
                            }else if(messageEvent.getMessage_type()== MessageEventType.MESSAGE_TYPE_KEYWORDS_NEWS && messageType== MessageEventType.MESSAGE_TYPE_KEYWORDS_NEWS){
                                if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass()) && this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                                respXml = messageEventHandler.callBackNews(this ,messageEvent , requestMap , keywords);
                                break;
                            }else if(messageEvent.getMessage_type()== MessageEventType.MESSAGE_TYPE_KEYWORDS_MSGMENU && messageType== MessageEventType.MESSAGE_TYPE_KEYWORDS_MSGMENU){
                                if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass()) && this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                                respXml = messageEventHandler.callBackMsgMenu(this ,messageEvent , requestMap , keywords, requestMap.get("bizmsgmenuid").toString());
                                break;
                            }else{
                                if(keywords.equals(messageEvent.getKey())){
                                    if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                        if(this.getApplicationContext()!=null){
                                            try{
                                                Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                                messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                            }catch (Exception e){
                                            }
                                        }
                                    }
                                    respXml = messageEventHandler.callBackKeyWords(this ,messageEvent , requestMap, keywords);
                                    break;
                                }
                            }
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_LINK){
                //用户发送链接消息
                String title = requestMap.get("Title").toString();//消息标题
                String description = requestMap.get("Description").toString();//消息描述
                String url = requestMap.get("Url").toString();//消息链接
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_LINK) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackLink(this ,messageEvent , requestMap, title , description, url);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_IMAGE){
                //用户发送临时图片消息，最多保存3天
                String mediaId = requestMap.get("MediaId").toString();//图片消息媒体id，可以调用获取临时素材接口拉取数据。
                String picUrl = requestMap.get("PicUrl").toString();//图片链接（由系统生成）
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_IMAGE) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackImage(this ,messageEvent , requestMap, mediaId , picUrl);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_VOICE){
                //用户发送临时语音消息，最多保存3天
                String mediaId = requestMap.get("MediaId").toString();//语音消息媒体id，可以调用获取临时素材接口拉取数据。
                String format = requestMap.get("Format").toString();//语音格式，如amr，speex等
                String recognition = requestMap.get("Recognition").toString();//语音识别结果，UTF8编码
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_VOICE) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackVoice(this ,messageEvent , requestMap, mediaId , format , recognition);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_VIDEO){
                //用户发送临时视频消息，最多保存3天
                String mediaId = requestMap.get("MediaId").toString();//视频消息媒体id，可以调用获取临时素材接口拉取数据。
                String thumbMediaId = requestMap.get("ThumbMediaId").toString();//视频消息缩略图的媒体id，可以调用多媒体文件下载接口拉取数据。
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_VIDEO) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackVideo(this ,messageEvent , requestMap, mediaId , thumbMediaId);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_SHORT_VIDEO){
                //用户发送临时短视频消息，最多保存3天
                String mediaId = requestMap.get("MediaId").toString();//视频消息媒体id，可以调用获取临时素材接口拉取数据。
                String thumbMediaId = requestMap.get("ThumbMediaId").toString();//视频消息缩略图的媒体id，可以调用多媒体文件下载接口拉取数据。
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_SHORT_VIDEO) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackShortVideo(this ,messageEvent , requestMap, mediaId , thumbMediaId);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_LOCATION){
                //用户发送地理位置消息
                String location_X = requestMap.get("Location_X").toString();//地理位置纬度
                String location_Y = requestMap.get("Location_Y").toString();//地理位置经度
                String label = requestMap.get("Label").toString();//地理位置信息
                String scale = requestMap.get("Scale").toString();//地图缩放大小
                String precision = requestMap.get("Precision").toString();//地理位置精度
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_LOCATION) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackLocation(this ,messageEvent , requestMap, Double.valueOf(location_X) ,  Double.valueOf(location_Y), label ,  Double.valueOf(scale) ,  Double.valueOf(precision));
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_EVENT_SUBSCRIBE){
                //用户关注公众号
                String eventKey = null;
                if(requestMap.containsKey("EventKey")){
                    eventKey = requestMap.get("EventKey").toString();  //扫描带参数二维码的值,qrscene_为前缀，后面为二维码的参数值
                }
                String ticket = null;
                if(requestMap.containsKey("Ticket")){
                    ticket = requestMap.get("Ticket").toString();      //带参数二维码的ticket,可用来换取二维码图片
                }

                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_EVENT_SUBSCRIBE) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackSubscribe(this ,messageEvent , requestMap , eventKey , ticket);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_EVENT_UNSUBSCRIBE){
                //用户取消关注公众号
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_EVENT_UNSUBSCRIBE) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackUnSubscribe(this ,messageEvent , requestMap);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_EVENT_SCAN){
                //用户关注公众号之后。在扫描带参数二维码时触发的事件
                String eventKey = requestMap.get("EventKey").toString();  //扫描带参数二维码的值
                String ticket = requestMap.get("Ticket").toString();      //带参数二维码的ticket
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_EVENT_SCAN) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackScan(this ,messageEvent , requestMap , eventKey , ticket);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_EVENT_CLICK){
                //用户点击click类型菜单推送的消息
                String eventKey = requestMap.get("EventKey").toString();
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_EVENT_CLICK && messageEvent.getKey().equals(eventKey)) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackEventClick(this ,messageEvent , requestMap, eventKey);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_EVENT_SCANCODE_PUSH){
                // 扫码推事件的事件推送
                // 用户点击扫码推事件类型菜单推送的消息,主要用于扫描公众号带参数的二维码或条形码
                String scanCodeInfo_ScanType = requestMap.get("ScanCodeInfo_ScanType").toString(); //qrcode--二维码  barcode--条形码
                String scanCodeInfo_ScanResult = requestMap.get("ScanCodeInfo_ScanResult").toString();  //值
                String eventKey = requestMap.get("EventKey").toString();

                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_EVENT_SCANCODE_PUSH && messageEvent.getKey().equals(eventKey)) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackEventScanCodePush(this ,messageEvent , requestMap, eventKey , scanCodeInfo_ScanType , scanCodeInfo_ScanResult);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_EVENT_SCANCODE_WAITMSG){
                // 扫码推事件且弹出“消息接收中”提示框的事件推送
                // 用户点击扫码带提示事件类型菜单推送的消息,主要用于扫描公众号带参数的二维码或条形码
                String scanCodeInfo_ScanType = requestMap.get("ScanCodeInfo_ScanType").toString(); //qrcode--二维码  barcode--条形码
                String scanCodeInfo_ScanResult = requestMap.get("ScanCodeInfo_ScanResult").toString();  //值
                String eventKey = requestMap.get("EventKey").toString();

                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_EVENT_SCANCODE_WAITMSG && messageEvent.getKey().equals(eventKey)) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackEventScanCodeWaitMsg(this ,messageEvent , requestMap, eventKey , scanCodeInfo_ScanType , scanCodeInfo_ScanResult);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_EVENT_VIEW){
                //用户点击view类型菜单推送的消息
                String eventKey = requestMap.get("EventKey").toString();
                String menuId = requestMap.get("MenuId").toString(); //如果是个性化菜单，则表示个性化菜单ID
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_EVENT_VIEW && messageEvent.getKey().equals(eventKey)) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackEventView(this ,messageEvent , requestMap, eventKey, menuId);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_EVENT_VIEW_MINIPROGRAM){
                //用户点击菜单跳转小程序的事件
                String eventKey = requestMap.get("EventKey").toString();
                String menuId = requestMap.get("MenuId").toString(); //如果是个性化菜单，则表示个性化菜单ID
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_EVENT_VIEW && messageEvent.getKey().equals(eventKey)) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackEventViewMiniprogram(this ,messageEvent , requestMap, eventKey, menuId);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_EVENT_PIC_SYSPHOTO){
                //用户点击系统拍照发图类型菜单推送的消息
                String eventKey = requestMap.get("EventKey").toString();
                String sendPicsInfo_Count = requestMap.get("SendPicsInfo_Count").toString();        //发送的图片总数
                String sendPicsInfo_PicList = requestMap.get("SendPicsInfo_PicList").toString();    //发送的图片PicMd5Sum值列表，以英文,间隔
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_EVENT_PIC_SYSPHOTO && messageEvent.getKey().equals(eventKey)) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackEventPicSysPhoto(this ,messageEvent , requestMap, eventKey, Integer.valueOf(sendPicsInfo_Count) , sendPicsInfo_PicList);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_EVENT_PIC_PHOTO_OR_ALBUM){
                //用户点击拍照或者相册发图类型菜单推送的消息
                String eventKey = requestMap.get("EventKey").toString();
                String sendPicsInfo_Count = requestMap.get("SendPicsInfo_Count").toString();        //发送的图片总数
                String sendPicsInfo_PicList = requestMap.get("SendPicsInfo_PicList").toString();    //发送的图片PicMd5Sum值列表，以英文,间隔
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_EVENT_PIC_PHOTO_OR_ALBUM && messageEvent.getKey().equals(eventKey)) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackEventPicPhotoOrAlbum(this ,messageEvent , requestMap, eventKey, Integer.valueOf(sendPicsInfo_Count) , sendPicsInfo_PicList);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_EVENT_PIC_WEIXIN){
                //用户点击微信相册发图类型菜单推送的消息
                String eventKey = requestMap.get("EventKey").toString();
                String sendPicsInfo_Count = requestMap.get("SendPicsInfo_Count").toString();        //发送的图片总数
                String sendPicsInfo_PicList = requestMap.get("SendPicsInfo_PicList").toString();    //发送的图片PicMd5Sum值列表，以英文,间隔
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_EVENT_PIC_WEIXIN && messageEvent.getKey().equals(eventKey)) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackEventPicWeixin(this ,messageEvent , requestMap, eventKey, Integer.valueOf(sendPicsInfo_Count) , sendPicsInfo_PicList);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_EVENT_LOCATION_SELECT){
                //用户点击发送位置菜单推送的消息
                String eventKey = requestMap.get("EventKey").toString();
                String location_X = requestMap.get("SendLocationInfo_Location_X").toString();//地理位置纬度
                String location_Y = requestMap.get("SendLocationInfo_Location_Y").toString();//地理位置经度
                String label = requestMap.get("SendLocationInfo_Label").toString();//地理位置信息
                String scale = requestMap.get("SendLocationInfo_Scale").toString();//地图缩放大小
                String poiname = requestMap.get("SendLocationInfo_Poiname").toString();//朋友圈POI的名字，可能为空
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_EVENT_LOCATION_SELECT && messageEvent.getKey().equals(eventKey)) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackEventLocationSelect(this ,messageEvent , requestMap, eventKey, Double.valueOf(location_X) ,  Double.valueOf(location_Y), label ,  Double.valueOf(scale) , poiname);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_EVENT_TEMPLATESENDJOBFINISH){
                //模版消息送达时的事件推送
                String status = requestMap.get("Status").toString();//送达状态  success--成功  failed:user block  -- 失败，用户拒绝  failed: system failed--失败，其它原因
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_EVENT_TEMPLATESENDJOBFINISH) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackEventTemplateSendJobFinish(this ,messageEvent , requestMap, status);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_EVENT_MASSSENDJOBFINISH){
                //事件推送群发结果
                //群发的结果，为“send success”或“send fail”或“err(num)”。但send success时，也有可能因用户拒收公众号的消息、系统错误等原因造成少量用户接收失败。err(num)是审核失败的具体原因，可能的情况如下：err(10001):涉嫌广告, err(20001):涉嫌政治, err(20004):涉嫌社会, err(20002):涉嫌色情, err(20006):涉嫌违法犯罪, err(20008):涉嫌欺诈, err(20013):涉嫌版权, err(22000):涉嫌互推(互相宣传), err(21000):涉嫌其他, err(30001):原创校验出现系统错误且用户选择了被判为转载就不群发, err(30002): 原创校验被判定为不能群发, err(30003): 原创校验被判定为转载文且用户选择了被判为转载就不群发, err(40001)：管理员拒绝, err(40002)：管理员30分钟内无响应，超时
                String status = requestMap.get("Status").toString();
                //tag_id下粉丝数；或者openid_list中的粉丝数
                String totalCount = requestMap.get("TotalCount").toString();
                //过滤（过滤是指特定地区、性别的过滤、用户设置拒收的过滤，用户接收已超4条的过滤）后，准备发送的粉丝数，原则上，FilterCount 约等于 SentCount + ErrorCount
                String filterCount = requestMap.get("FilterCount").toString();
                //发送成功的粉丝数
                String sentCount = requestMap.get("SentCount").toString();
                //发送失败的粉丝数
                String errorCount = requestMap.get("ErrorCount").toString();

                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_EVENT_MASSSENDJOBFINISH) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackEventMassSendJobFinish(this ,messageEvent , requestMap, status , Integer.valueOf(totalCount), Integer.valueOf(filterCount), Integer.valueOf(sentCount), Integer.valueOf(errorCount));
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_EVENT_SUBSCRIBE_MSG_POPUP){
                //用户操作订阅通知弹窗事件推送
                List<Map<String,Object>> SubscribeMsgPopupEvent = null;
                if(requestMap.get("SubscribeMsgPopupEvent")!=null){
                    SubscribeMsgPopupEvent = JSONObject.parseObject(requestMap.get("SubscribeMsgPopupEvent").toString(),List.class);
                }
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_EVENT_SUBSCRIBE_MSG_POPUP) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackEventSubscribeMsgPopup(this ,messageEvent , requestMap, SubscribeMsgPopupEvent);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_EVENT_SUBSCRIBE_MSG_CHANGE){
                //用户管理订阅通知事件推送
                List<Map<String,Object>> SubscribeMsgChangeEvent = null;
                if(requestMap.get("SubscribeMsgChangeEvent")!=null){
                    SubscribeMsgChangeEvent = JSONObject.parseObject(requestMap.get("SubscribeMsgChangeEvent").toString(),List.class);
                }
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_EVENT_SUBSCRIBE_MSG_CHANGE) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackEventSubscribeMsgChange(this ,messageEvent , requestMap, SubscribeMsgChangeEvent);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_EVENT_SUBSCRIBE_MSG_SENT){
                //用户管理订阅通知事件推送
                List<Map<String,Object>> SubscribeMsgSentEvent = null;
                if(requestMap.get("SubscribeMsgSentEvent")!=null){
                    SubscribeMsgSentEvent = JSONObject.parseObject(requestMap.get("SubscribeMsgSentEvent").toString(),List.class);
                }
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_EVENT_SUBSCRIBE_MSG_SENT) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackEventSubscribeMsgSent(this ,messageEvent , requestMap, SubscribeMsgSentEvent);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_EVENT_FREEPUBLISHJOBFINISH){
                //草稿发布事件推送发布结果推送
                Map<String,Object> publishEventInfo = null;
                if(requestMap.get("PublishEventInfo")!=null){
                    publishEventInfo = JSONObject.parseObject(requestMap.get("PublishEventInfo").toString(),Map.class);
                }
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_EVENT_FREEPUBLISHJOBFINISH) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackEventFreePublishJobFinish(this ,messageEvent , requestMap, publishEventInfo);
                            break;
                        }
                    }
                }
            }
            else if(messageType == MessageEventType.MESSAGE_TYPE_EVENT_LOCATION){
                //当公众号在接口权限--对话服务--用户管理--获取用户地理位置 权限打开之后，用户将上报当前地理位置并推送事件
                Double latitude = Double.valueOf(requestMap.get("Latitude"));  //地理位置纬度
                Double longitude = Double.valueOf(requestMap.get("Longitude"));  //地理位置经度
                Double precision = Double.valueOf(requestMap.get("Precision"));  //地理位置精度

                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_EVENT_LOCATION) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackEventLocation(this ,messageEvent , requestMap, latitude, longitude, precision);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_EVENT_QUALIFICATION_VERIFY_SUCCESS){
                //资质认证成功（此时立即获得接口权限）推送事件
                Long expiredTime = Long.valueOf(requestMap.get("expiredTime"));  //有效期 (整型)，指的是时间戳，将于该时间戳认证过期
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_EVENT_QUALIFICATION_VERIFY_SUCCESS) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackEventQualificationVerifySuccess(this ,messageEvent , requestMap, expiredTime);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_EVENT_QUALIFICATION_VERIFY_FAIL){
                //资质认证失败
                Long failTime = Long.valueOf(requestMap.get("FailTime"));  //失败发生时间 (整型)，时间戳
                String failReason =requestMap.get("FailReason");  //认证失败的原因
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_EVENT_QUALIFICATION_VERIFY_FAIL) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackEventQualificationVerifyFail(this ,messageEvent , requestMap, failTime, failReason);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_EVENT_NAMING_VERIFY_SUCCESS){
                //名称认证成功（即命名成功）
                Long expiredTime = Long.valueOf(requestMap.get("ExpiredTime"));  //失败发生时间 (整型)，时间戳
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_EVENT_NAMING_VERIFY_SUCCESS) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackEventNamingVerifySuccess(this ,messageEvent , requestMap, expiredTime);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_EVENT_NAMING_VERIFY_FAIL){
                //名称认证失败（这时虽然客户端不打勾，但仍有接口权限）
                Long failTime = Long.valueOf(requestMap.get("FailTime"));  //失败发生时间 (整型)，时间戳
                String failReason =requestMap.get("FailReason");  //认证失败的原因
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_EVENT_NAMING_VERIFY_FAIL) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackEventNamingVerifyFail(this ,messageEvent , requestMap, failTime, failReason);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_EVENT_ANNUAL_RENEW){
                //年审通知
                Long expiredTime = Long.valueOf(requestMap.get("ExpiredTime"));  //失败发生时间 (整型)，时间戳
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_EVENT_ANNUAL_RENEW) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackEventAnnualRenew(this ,messageEvent , requestMap, expiredTime);
                            break;
                        }
                    }
                }
            }else if(messageType == MessageEventType.MESSAGE_TYPE_EVENT_VERIFY_EXPIRED){
                //年审通知
                Long expiredTime = Long.valueOf(requestMap.get("ExpiredTime"));  //失败发生时间 (整型)，时间戳
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_EVENT_VERIFY_EXPIRED) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackEventVerifyExpired(this ,messageEvent , requestMap, expiredTime);
                            break;
                        }
                    }
                }
            }
            //没有任何消息处理过
            if(respXml == null){
                if(this.getMessageEvents()!=null) {
                    for (MessageEvent messageEvent : this.getMessageEvents()) {
                        if (messageEvent.getEnabled() && messageEvent.getMessage_type() == MessageEventType.MESSAGE_TYPE_DEFAULT) {
                            if(!StringUtil.isNullOrEmpty(messageEvent.getEventHandlerClass())){
                                if(this.getApplicationContext()!=null){
                                    try{
                                        Class clazz =Class.forName(messageEvent.getEventHandlerClass());
                                        messageEventHandler = (MessageEventHandler) this.getApplicationContext().getBean(clazz);
                                    }catch (Exception e){
                                    }
                                }
                            }
                            respXml = messageEventHandler.callBackDefault(this ,messageEvent , requestMap);
                            break;
                        }
                    }
                }
            }
            return respXml;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Boolean checkSign(HttpServletRequest request) {
        return  checkSign(request.getParameter("signature"), request.getParameter("timestamp") , request.getParameter("nonce"));
    }

    @Override
    public Boolean checkSign(String signature, String timestamp, String nonce) {
        List<String> keys = new ArrayList<>();
        keys.add(this.token);
        keys.add(timestamp);
        keys.add(nonce);
        Collections.sort(keys);
        StringBuffer sb = new StringBuffer();
        for (String key : keys){
            sb.append(key);
        }
        try {
            return SecurityUtil.sha(sb.toString()).equals(signature);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Map<String, Integer> getMaterialCount() {
        Map<String, Integer> result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/material/get_materialcount?access_token=%s";
        api_url = String.format(api_url , access_token);
        String jsonObjectArticleItem =  restTemplateUtil.get(api_url);
        jsonObject = JSONObject.parseObject(jsonObjectArticleItem);
        if(jsonObject.containsKey("voice_count")){
            result = new HashMap<>();
            result.put("voice_count" , jsonObject.getIntValue("voice_count"));
            result.put("video_count" , jsonObject.getIntValue("video_count"));
            result.put("image_count" , jsonObject.getIntValue("image_count"));
            result.put("news_count" , jsonObject.getIntValue("news_count"));
        }
        return result;
    }

    @Override
    public List<NewsMaterial> getArticleItems(int offset , int count) {
        List<NewsMaterial> result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/material/batchget_material?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String,Object> postData = new HashMap<>();
        postData.put("type" , "news");
        postData.put("offset" , offset);
        postData.put("count" , count);

        String jsonObjectArticleItem =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(jsonObjectArticleItem);
        if(jsonObject!=null && jsonObject.containsKey("item")){
            JSONArray jsonArray = jsonObject.getJSONArray("item");
            if(jsonArray.size() > 0 ){
                result =new ArrayList<>();
                for(int i=0; i< jsonArray.size() ;i++){
                    if(jsonArray.getJSONObject(i).containsKey("media_id") && jsonArray.getJSONObject(i).containsKey("content")){
                        NewsMaterial newsMaterial = new NewsMaterial();
                        newsMaterial.setMedia_id(jsonArray.getJSONObject(i).getString("media_id"));
                        newsMaterial.setUpdate_time(jsonArray.getJSONObject(i).getJSONObject("content").getLong("update_time"));
                        newsMaterial.setCreate_time(jsonArray.getJSONObject(i).getJSONObject("content").getLong("create_time"));

                        JSONArray jsonArray1 = jsonArray.getJSONObject(i).getJSONObject("content").getJSONArray("news_item");
                        if(jsonArray1!=null && jsonArray1.size()>0){
                            List<ArticleItem> news_item = new ArrayList<>();
                            for (int j=0;j<jsonArray1.size();j++){
                                ArticleItem article =new ArticleItem();
                                JSONObject jsonObject2 = jsonArray1.getJSONObject(j);
                                try {
                                    if(jsonObject2.containsKey("title")){
                                        article.setTitle(new String(jsonObject2.getString("title").getBytes(WEIXIN_CHARSET)));
                                    }
                                    if(jsonObject2.containsKey("thumb_media_id")){
                                        article.setThumb_media_id(new String(jsonObject2.getString("thumb_media_id").getBytes(WEIXIN_CHARSET)));
                                    }
                                    if(jsonObject2.containsKey("thumb_url")){
                                        article.setThumb_url(new String(jsonObject2.getString("thumb_url").getBytes(WEIXIN_CHARSET)));
                                    }
                                    if(jsonObject2.containsKey("author")){
                                        article.setAuthor(new String(jsonObject2.getString("author").getBytes(WEIXIN_CHARSET)));
                                    }
                                    if(jsonObject2.containsKey("digest")){
                                        article.setDigest(new String(jsonObject2.getString("digest").getBytes(WEIXIN_CHARSET)));
                                    }
                                    if(jsonObject2.containsKey("content")){
                                        article.setContent(new String(jsonObject2.getString("content").getBytes(WEIXIN_CHARSET)));
                                    }
                                    if(jsonObject2.containsKey("content_source_url")){
                                        article.setContent_source_url(new String(jsonObject2.getString("content_source_url").getBytes(WEIXIN_CHARSET)));
                                    }
                                    if(jsonObject2.containsKey("api")){
                                        article.setUrl(new String(jsonObject2.getString("api").getBytes(WEIXIN_CHARSET)));
                                    }
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                if(jsonObject2.containsKey("show_cover_pic")){
                                    article.setShow_cover_pic(jsonObject2.getInteger("show_cover_pic"));
                                }
                                if(jsonObject2.containsKey("need_open_comment")){
                                    article.setNeed_open_comment(jsonObject2.getInteger("need_open_comment"));
                                }
                                if(jsonObject2.containsKey("only_fans_can_comment")){
                                    article.setOnly_fans_can_comment(jsonObject2.getInteger("only_fans_can_comment"));
                                }
                                news_item.add(article);
                            }
                            newsMaterial.setNews_item(news_item);
                        }
                        result.add(newsMaterial);
                    }
                }
            }
        }

        return result;
    }


    @Override
    public List<Map<String, Object>> getImageItems(int offset, int count) {
        return getImageItems("image" , offset , count);
    }

    @Override
    public List<Map<String, Object>> getVideoItems(int offset, int count) {
        return getImageItems("video" , offset , count);
    }

    @Override
    public List<Map<String, Object>> getVoiceItems(int offset, int count) {
        return getImageItems("voice" , offset , count);
    }

    private List<Map<String, Object>> getImageItems(String type, int offset, int count) {
        List<Map<String , Object>> result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/material/batchget_material?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String,Object> postData = new HashMap<>();
        postData.put("type" , type);
        postData.put("offset" , offset);
        postData.put("count" , count);

        String jsonObjectArticleItem =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(jsonObjectArticleItem);
        if(jsonObject!=null && jsonObject.containsKey("item")){
            JSONArray jsonArray = jsonObject.getJSONArray("item");
            if(jsonArray.size() > 0 ){
                result =new ArrayList<>();
                for(int i=0; i< jsonArray.size() ;i++){
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    Map<String , Object> map =new HashMap<>();
                    for(Map.Entry<String,Object> entry : jsonObject2.entrySet()){
                        try {
                            map.put(entry.getKey() , new String(entry.getValue().toString().getBytes(WEIXIN_CHARSET)));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    result.add(map);
                }
            }
        }

        return result;
    }

    /**
     * 新增永久图文素材
     *
     * @param articles                      图文列表
     *      [
     *        {
     *             "title": "标题",
     *             "thumb_media_id": "图文消息的封面图片素材id（必须是永久mediaID）",
     *             "thumb_url": "图文消息的封面图片本地全路径图片文件名，用于上传永久图片素材并获取media_id",
     *             "author": "作者",
     *             "digest": "图文消息的摘要，仅有单图文消息才有摘要，多图文此处为空。如果本字段为没有填写，则默认抓取正文前54个字。",
     *             "show_cover_pic": 0|1,  //是否显示封面，0为false，即不显示，1为true，即显示
     *             "content": "图文消息的具体内容，支持HTML标签，必须少于2万字符，小于1M，且此处会去除JS,涉及图片url必须来源 "上传图文消息内的图片获取URL"接口获取。外部图片api_url将被过滤。",
     *                                   小程序卡片跳转小程序: <mp-miniprogram data-miniprogram-appid="wx123123123" data-miniprogram-path="pages/index/index" data-miniprogram-title="小程序示例" data-miniprogram-imageurl="http://example.com/demo.jpg"></mp-miniprogram>
     *                                   文字跳转小程序:<p><a data-miniprogram-appid="wx123123123" data-miniprogram-path="pages/index" href="">点击文字跳转小程序</a></p>
     *                                   图片跳转小程序:<p><a data-miniprogram-appid="wx123123123" data-miniprogram-path="pages/index" href=""><img src="https://mmbiz.qpic.cn/mmbiz_jpg/demo/0?wx_fmt=jpg" alt="" data-width="null" data-ratio="NaN"></a></p>
     *                                   说明：   data-miniprogram-appid	    是	小程序的AppID
     *                                           data-miniprogram-path	    是	小程序要打开的路径
     *                                           data-miniprogram-title	    是	小程序卡片的标题，不超过35个字
     *                                           data-miniprogram-imageurl	是	小程序卡片的封面图链接，图片必须为1080*864像素
     *             "content_source_url": "图文消息的原文地址，即点击“阅读原文”后的URL",
     *             "need_open_comment": 0|1,  //Uint32 是否打开评论，0不打开，1打开
     *             "only_fans_can_comment": 0|1  //Uint32 是否粉丝才可评论，0所有人可评论，1粉丝才可评论
     *         },
     *         ...
     *      ]
     * @param contentImageDownLoadPath        图文内容中图片(仅支持.png或.jpg)下载目标文件夹完整物理路径名，
     *                                        将针对内容中src="*.jpg|*.png"及url("*.jpg|*.png")中图片下载到contentImageDownLoadPath服务器本地文件夹，
     *                                        然后再上传到微信服务器获取微信服务器图片url之后完成替换，完成处理之后，下载到contentImageDownLoadPath本地文件夹中的相应图片文件将自动删除
     * @param contentImageUrlPrefix           内容中.png或.jpg如果未使用http://或https://开头的url时，图片的完整前缀url，格式如： https://www.abcd.com/abcd
     *
     * @return String  null -- 添加失败  否则返回新增图文的 media_id
     */
    public String addArticleItem(List<ArticleItem> articles, String contentImageDownLoadPath, String contentImageUrlPrefix) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/material/add_news?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String,List<ArticleItem>> postData = new HashMap<>();
        for(ArticleItem articleItem : articles){
               if(!StringUtil.isNullOrEmpty(articleItem.getThumb_url())){
                  String media_id = addImage(articleItem.getThumb_url());
                  if(media_id!=null){
                      articleItem.setThumb_media_id(media_id);
                  }
                   articleItem.setThumb_url(null);
               }
               if(!StringUtil.isNullOrEmpty(articleItem.getContent())){
                   articleItem.setContent(handleArticleContent(articleItem.getContent() , contentImageDownLoadPath , contentImageUrlPrefix));
               }
        }
        postData.put("articles" , articles);

        String jsonObjectArticleItem =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(jsonObjectArticleItem);
        if(jsonObject.containsKey("media_id")){
            return jsonObject.getString("media_id");
        }
        return null;
    }

    /**
     * 新增永久图文素材
     *
     * @param articles                      图文列表
     *      [
     *        {
     *             "title": "标题",
     *             "thumb_media_id": "图文消息的封面图片素材id（必须是永久mediaID）",
     *             "thumb_url": "图文消息的封面图片本地全路径图片文件名，用于上传永久图片素材并获取media_id",
     *             "author": "作者",
     *             "digest": "图文消息的摘要，仅有单图文消息才有摘要，多图文此处为空。如果本字段为没有填写，则默认抓取正文前54个字。",
     *             "show_cover_pic": 0|1,  //是否显示封面，0为false，即不显示，1为true，即显示
     *             "content": "图文消息的具体内容，支持HTML标签，必须少于2万字符，小于1M，且此处会去除JS,涉及图片url必须来源 "上传图文消息内的图片获取URL"接口获取。外部图片api_url将被过滤。",
     *                                   小程序卡片跳转小程序: <mp-miniprogram data-miniprogram-appid="wx123123123" data-miniprogram-path="pages/index/index" data-miniprogram-title="小程序示例" data-miniprogram-imageurl="http://example.com/demo.jpg"></mp-miniprogram>
     *                                   文字跳转小程序:<p><a data-miniprogram-appid="wx123123123" data-miniprogram-path="pages/index" href="">点击文字跳转小程序</a></p>
     *                                   图片跳转小程序:<p><a data-miniprogram-appid="wx123123123" data-miniprogram-path="pages/index" href=""><img src="https://mmbiz.qpic.cn/mmbiz_jpg/demo/0?wx_fmt=jpg" alt="" data-width="null" data-ratio="NaN"></a></p>
     *                                   说明：   data-miniprogram-appid	    是	小程序的AppID
     *                                           data-miniprogram-path	    是	小程序要打开的路径
     *                                           data-miniprogram-title	    是	小程序卡片的标题，不超过35个字
     *                                           data-miniprogram-imageurl	是	小程序卡片的封面图链接，图片必须为1080*864像素
     *             "content_source_url": "图文消息的原文地址，即点击“阅读原文”后的URL",
     *             "need_open_comment": 0|1,  //Uint32 是否打开评论，0不打开，1打开
     *             "only_fans_can_comment": 0|1  //Uint32 是否粉丝才可评论，0所有人可评论，1粉丝才可评论
     *         },
     *         ...
     *      ]
     * @param contentImageDownLoadPath        图文内容中图片(仅支持.png或.jpg)下载目标文件夹完整物理路径名，
     *                                        将针对内容中src="*.jpg|*.png"及url("*.jpg|*.png")中图片下载到contentImageDownLoadPath服务器本地文件夹，
     *                                        然后再上传到微信服务器获取微信服务器图片url之后完成替换，完成处理之后，下载到contentImageDownLoadPath本地文件夹中的相应图片文件将自动删除
     * @param contentImageUrlPrefix           内容中.png或.jpg如果未使用http://或https://开头的url时，图片的完整前缀url，格式如： https://www.abcd.com/abcd
     *
     * @return String  null -- 添加失败  否则返回新增图文的 media_id
     */
    public String uploadNews(List<ArticleItem> articles, String contentImageDownLoadPath, String contentImageUrlPrefix) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/media/uploadnews?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String,List<ArticleItem>> postData = new HashMap<>();
        for(ArticleItem articleItem : articles){
            if(!StringUtil.isNullOrEmpty(articleItem.getThumb_url())){
                String media_id = addImage(articleItem.getThumb_url());
                if(media_id!=null){
                    articleItem.setThumb_media_id(media_id);
                }
                articleItem.setThumb_url(null);
            }
            if(!StringUtil.isNullOrEmpty(articleItem.getContent())){
                articleItem.setContent(handleArticleContent(articleItem.getContent() , contentImageDownLoadPath , contentImageUrlPrefix));
            }
        }
        postData.put("articles" , articles);

        String jsonObjectArticleItem =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(jsonObjectArticleItem);
        if(jsonObject.containsKey("media_id")){
            return jsonObject.getString("media_id");
        }
        return null;
    }

    @Override
    /**
     * 修改永久图文素材中指定位置图文
     *
     * @param media_id                      永久图文素材media_id
     * @param index                         永久图文素材中的指定索引号，从0开始
     * @param article                       图文信息
     *        {
     *             "title": "标题",
     *             "thumb_media_id": "图文消息的封面图片素材id（必须是永久mediaID）",
     *             "thumb_url": "图文消息的封面图片本地全路径图片文件名，用于上传永久图片素材并获取media_id",
     *             "author": "作者",
     *             "digest": "图文消息的摘要，仅有单图文消息才有摘要，多图文此处为空。如果本字段为没有填写，则默认抓取正文前54个字。",
     *             "show_cover_pic": 0|1,  //是否显示封面，0为false，即不显示，1为true，即显示
     *             "content": "图文消息的具体内容，支持HTML标签，必须少于2万字符，小于1M，且此处会去除JS,涉及图片url必须来源 "上传图文消息内的图片获取URL"接口获取。外部图片api_url将被过滤。",
     *             "content_source_url": "图文消息的原文地址，即点击“阅读原文”后的URL",
     *             "need_open_comment": 0|1,  //Uint32 是否打开评论，0不打开，1打开
     *             "only_fans_can_comment": 0|1  //Uint32 是否粉丝才可评论，0所有人可评论，1粉丝才可评论
     *         }
     * @param contentImageDownLoadPath        图文内容中图片(仅支持.png或.jpg)下载目标文件夹完整物理路径名，
     *                                        将针对内容中src="*.jpg|*.png"及url("*.jpg|*.png")中图片下载到contentImageDownLoadPath服务器本地文件夹，
     *                                        然后再上传到微信服务器获取微信服务器图片url之后完成替换，完成处理之后，下载到contentImageDownLoadPath本地文件夹中的相应图片文件将自动删除
     * @param contentImageUrlPrefix           内容中.png或.jpg如果未使用http://或https://开头的url时，图片的完整前缀url，格式如： https://www.abcd.com/abcd
     *
     * @return String  null -- 添加失败  否则返回新增图文的 media_id
     */
    public Boolean updateArticleItem(String media_id, int index, ArticleItem article, String contentImageDownLoadPath, String contentImageUrlPrefix) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/material/update_news?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String,Object> postData = new HashMap<>();
        postData.put("media_id" , media_id);
        postData.put("index" , index);

        if(!StringUtil.isNullOrEmpty(article.getThumb_url())){
            String thumb_media_id = addImage(article.getThumb_url());
            if(thumb_media_id!=null){
                article.setThumb_media_id(thumb_media_id);
            }
            article.setThumb_url(null);
        }
        if(!StringUtil.isNullOrEmpty(article.getContent())){
            article.setContent(handleArticleContent(article.getContent() , contentImageDownLoadPath , contentImageUrlPrefix));
        }
        postData.put("articles" , article);

        String jsonObjectArticleItem =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(jsonObjectArticleItem);
        if(jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            return true;
        }
        return false;
    }

    @Override
    public NewsMaterial getArticleItem(String media_id) {
        NewsMaterial newsMaterial = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/material/get_material?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String,String> postData = new HashMap<>();
        postData.put("media_id" , media_id);

        String jsonObjectArticleItem =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(jsonObjectArticleItem);
        if(jsonObject!=null && jsonObject.containsKey("news_item")){
            JSONArray jsonArray = jsonObject.getJSONArray("news_item");
            if(jsonArray.size()>0){
                newsMaterial =new NewsMaterial();
                newsMaterial.setMedia_id(media_id);
                newsMaterial.setUpdate_time(jsonObject.getLong("update_time"));
                newsMaterial.setCreate_time(jsonObject.getLong("create_time"));

                List<ArticleItem> news_item = new ArrayList<>();

                for (int i=0;i< jsonArray.size();i++){
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    ArticleItem article =new ArticleItem();
                    try {
                        if(jsonObject2.containsKey("title")){
                            article.setTitle(new String(jsonObject2.getString("title").getBytes(WEIXIN_CHARSET)));
                        }
                        if(jsonObject2.containsKey("thumb_media_id")){
                            article.setThumb_media_id(new String(jsonObject2.getString("thumb_media_id").getBytes(WEIXIN_CHARSET)));
                        }
                        if(jsonObject2.containsKey("thumb_url")){
                            article.setThumb_url(new String(jsonObject2.getString("thumb_url").getBytes(WEIXIN_CHARSET)));
                        }
                        if(jsonObject2.containsKey("author")){
                            article.setAuthor(new String(jsonObject2.getString("author").getBytes(WEIXIN_CHARSET)));
                        }
                        if(jsonObject2.containsKey("digest")){
                            article.setDigest(new String(jsonObject2.getString("digest").getBytes(WEIXIN_CHARSET)));
                        }
                        if(jsonObject2.containsKey("content")){
                            article.setContent(new String(jsonObject2.getString("content").getBytes(WEIXIN_CHARSET)));
                        }
                        if(jsonObject2.containsKey("content_source_url")){
                            article.setContent_source_url(new String(jsonObject2.getString("content_source_url").getBytes(WEIXIN_CHARSET)));
                        }
                        if(jsonObject2.containsKey("api")){
                            article.setUrl(new String(jsonObject2.getString("api").getBytes(WEIXIN_CHARSET)));
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if(jsonObject2.containsKey("show_cover_pic")){
                        article.setShow_cover_pic(jsonObject2.getInteger("show_cover_pic"));
                    }
                    if(jsonObject2.containsKey("need_open_comment")){
                        article.setNeed_open_comment(jsonObject2.getInteger("need_open_comment"));
                    }
                    if(jsonObject2.containsKey("only_fans_can_comment")){
                        article.setOnly_fans_can_comment(jsonObject2.getInteger("only_fans_can_comment"));
                    }
                    news_item.add(article);
                }
                newsMaterial.setNews_item(news_item);
            }
        }

        return newsMaterial;
    }

    @Override
    public String addImage(String fileName) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/material/add_material?access_token=%s&type=image";
        api_url = String.format(api_url , access_token);
        Map<String , String> files = new HashMap<>();
        files.put("media" , fileName);

        String result  = restTemplateUtil.postForm(api_url , null , null , files);
        jsonObject = JSONObject.parseObject(result);
        if(jsonObject.containsKey("media_id")){
            return  jsonObject.getString("media_id");
        }
        return null;
    }

    private byte[] getMaterial(String media_id) {
        List<Map<String, Object>> result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/material/get_material?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String,String> postData = new HashMap<>();
        postData.put("media_id" , media_id);

        return restTemplateUtil.postForObject(api_url, postData, byte[].class);
    }

    @Override
    public byte[] getImage(String media_id) {
        return getMaterial(media_id);
    }

    @Override
    public String addVoice(String fileName) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/material/add_material?access_token=%s&type=voice";
        api_url = String.format(api_url , access_token);
        Map<String , String> files = new HashMap<>();
        files.put("media" , fileName);

        String result  = restTemplateUtil.postForm(api_url , null , null , files);
        jsonObject = JSONObject.parseObject(result);
        if(jsonObject.containsKey("media_id")){
            return  jsonObject.getString("media_id");
        }
        return null;
    }

    @Override
    public byte[] getVoice(String media_id) {
        return getMaterial(media_id);
    }

    @Override
    public String addVideo(String fileName, String title, String introduction) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/material/add_material?access_token=%s&type=video";
        api_url = String.format(api_url , access_token);
        Map<String , String> files = new HashMap<>();
        files.put("media" , fileName);

        Map<String , String> description = new HashMap<>();
        description.put("title" , title);
        description.put("introduction" , introduction);

        Map<String , Object> postForm = new HashMap<>();
        postForm.put("description" , JSONObject.toJSONString(description));

        String result  = restTemplateUtil.postForm(api_url , postForm , null , files);
        jsonObject = JSONObject.parseObject(result);
        if(jsonObject.containsKey("media_id")){
            return  jsonObject.getString("media_id");
        }
        return null;
    }

    @Override
    public Map<String,Object> getVideo(String media_id) {
        Map<String, Object> result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/material/get_material?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String,String> postData = new HashMap<>();
        postData.put("media_id" , media_id);

        String data = restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(data);
        if(jsonObject!=null){
            result = new HashMap<>();
            for(Map.Entry<String,Object> entry : jsonObject.entrySet()){
                try {
                    result.put(entry.getKey() , new String(entry.getValue().toString().getBytes(WEIXIN_CHARSET)));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @Override
    public String uploadImg(String mediaFileName) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/media/uploadimg?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , String> files = new HashMap<>();
        files.put("media" , mediaFileName);

        String result  = restTemplateUtil.postForm(api_url , null , null , files);
        jsonObject = JSONObject.parseObject(result);
        if(jsonObject.containsKey("url")){
            return  jsonObject.getString("url");
        }
        return null;
    }


    @Override
    public String handleArticleContent(String content, String downLoadPath, String urlPrefix) {
        String protocol ="";
        String orgSrc = null;
        String src = null;
        String tempFileName = null;
        String mediaUrl = null;
        if(!StringUtil.isNullOrEmpty(urlPrefix)){
            try {
                URL url =new URL(urlPrefix);
                protocol = url.getProtocol();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        Pattern pattern = Pattern.compile("src=(\\\"|')?(.+?\\.(jpg|png))(\\\"|')?" , Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()){
            orgSrc = src = matcher.group(2);
            if(src.toLowerCase().startsWith("http://") || src.toLowerCase().startsWith("https://")){

            }else if(src.startsWith("//")){
                src = protocol + ":" + src;
            }else if(src.startsWith("/")) {
                src = StringUtil.rtrim(urlPrefix , "/") + src;
            }else{
                src =  urlPrefix + src;
            }
            tempFileName =  this.restTemplateUtil.download(src , downLoadPath);
            if(tempFileName!=null){
                mediaUrl = this.uploadImg(tempFileName);
                if(mediaUrl!=null){
                    content = content.replace(orgSrc , mediaUrl);
                }
                FileUtil.deleteFile(tempFileName);
            }
        }

        Pattern pattern1 = Pattern.compile("url\\((\\\"|')?(.+?\\.(jpg|png))(\\\"|')?\\)" , Pattern.CASE_INSENSITIVE);
        Matcher matcher1 = pattern1.matcher(content);
        while (matcher1.find()){
            orgSrc = src = matcher1.group(2);
            if(src.toLowerCase().startsWith("http://") || src.toLowerCase().startsWith("https://")){

            }else if(src.startsWith("//")){
                src = protocol + ":" + src;
            }else if(src.startsWith("/")) {
                src = StringUtil.rtrim(urlPrefix , "/") + src;
            }else{
                src =  urlPrefix + src;
            }
            tempFileName =  this.restTemplateUtil.download(src , downLoadPath);
            if(tempFileName!=null){
                mediaUrl = this.uploadImg(tempFileName);
                if(mediaUrl!=null){
                    content = content.replace(orgSrc , mediaUrl);
                }
                FileUtil.deleteFile(tempFileName);
            }
        }

        return content;
    }

    @Override
    public Boolean deleteMaterial(String media_id) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/material/del_material?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , String> postData = new HashMap<>();
        postData.put("media_id" , media_id);

        String result =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(result);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            return true;
        }
        return false;

    }

    @Override
    public String addTempMaterial(String type, String fileName) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/media/upload?access_token=%s&type="+type;
        api_url = String.format(api_url , access_token);
        Map<String , String> files = new HashMap<>();
        files.put("media" , fileName);

        String result  = restTemplateUtil.postForm(api_url , null , null , files);
        jsonObject = JSONObject.parseObject(result);
        if(jsonObject.containsKey("media_id")){
            return  jsonObject.getString("media_id");
        }
        return null;
    }

    @Override
    public byte[] getTempMaterial(String media_id) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/media/get?access_token=%s&media_id=%s";
        api_url = String.format(api_url , access_token , media_id);
        return restTemplateUtil.getForObject(api_url , byte[].class);
    }

    @Override
    public Map<String, Object> createTempQrcode(int expire_seconds, int scene_id) {
        Map<String, Object> result =null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/qrcode/create?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();
        postData.put("expire_seconds" , expire_seconds);
        postData.put("action_name" , "QR_SCENE");  //二维码类型，QR_SCENE为临时的整型参数值，QR_STR_SCENE为临时的字符串参数值，QR_LIMIT_SCENE为永久的整型参数值，QR_LIMIT_STR_SCENE为永久的字符串参数值
        Map<String , Object> action_info = new HashMap<>();
        Map<String , Object> scene = new HashMap<>();
        scene.put("scene_id" , scene_id);
        action_info.put("scene" ,scene);
        postData.put("action_info" ,action_info);

        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("ticket")){
            result = new HashMap<>();
            result.put("ticket" , jsonObject.getString("ticket"));
            result.put("expire_seconds" , jsonObject.getIntValue("expire_seconds"));
            result.put("url" , jsonObject.getString("url"));
        }
        return result;
    }

    @Override
    public Map<String, Object> createTempQrcode(int expire_seconds, String scene_str) {
        Map<String, Object> result =null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/qrcode/create?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();
        postData.put("expire_seconds" , expire_seconds);
        postData.put("action_name" , "QR_STR_SCENE");  //二维码类型，QR_SCENE为临时的整型参数值，QR_STR_SCENE为临时的字符串参数值，QR_LIMIT_SCENE为永久的整型参数值，QR_LIMIT_STR_SCENE为永久的字符串参数值
        Map<String , Object> action_info = new HashMap<>();
        Map<String , Object> scene = new HashMap<>();
        scene.put("scene_str" , scene_str);
        action_info.put("scene" ,scene);
        postData.put("action_info" ,action_info);

        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("ticket")){
            result = new HashMap<>();
            result.put("ticket" , jsonObject.getString("ticket"));
            result.put("expire_seconds" , jsonObject.getIntValue("expire_seconds"));
            result.put("url" , jsonObject.getString("url"));
        }
        return result;
    }

    @Override
    public Map<String, Object> createQrcode(int scene_id) {
        Map<String, Object> result =null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/qrcode/create?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();
        postData.put("action_name" , "QR_LIMIT_SCENE");  //二维码类型，QR_SCENE为临时的整型参数值，QR_STR_SCENE为临时的字符串参数值，QR_LIMIT_SCENE为永久的整型参数值，QR_LIMIT_STR_SCENE为永久的字符串参数值
        Map<String , Object> action_info = new HashMap<>();
        Map<String , Object> scene = new HashMap<>();
        scene.put("scene_id" , scene_id);
        action_info.put("scene" ,scene);
        postData.put("action_info" ,action_info);

        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("ticket")){
            result = new HashMap<>();
            result.put("ticket" , jsonObject.getString("ticket"));
            result.put("expire_seconds" , jsonObject.getIntValue("expire_seconds"));
            result.put("url" , jsonObject.getString("url"));
        }
        return result;
    }

    @Override
    public Map<String, Object> createQrcode(String scene_str) {
        Map<String, Object> result =null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/qrcode/create?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();
        postData.put("action_name" , "QR_LIMIT_STR_SCENE");  //二维码类型，QR_SCENE为临时的整型参数值，QR_STR_SCENE为临时的字符串参数值，QR_LIMIT_SCENE为永久的整型参数值，QR_LIMIT_STR_SCENE为永久的字符串参数值
        Map<String , Object> action_info = new HashMap<>();
        Map<String , Object> scene = new HashMap<>();
        scene.put("scene_str" , scene_str);
        action_info.put("scene" ,scene);
        postData.put("action_info" ,action_info);

        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("ticket")){
            result = new HashMap<>();
            result.put("ticket" , jsonObject.getString("ticket"));
            result.put("expire_seconds" , jsonObject.getIntValue("expire_seconds"));
            result.put("url" , jsonObject.getString("url"));
        }
        return result;
    }


    @Override
    public byte[] showQrcode(String ticket) throws UnsupportedEncodingException {
        String api_url = WEIXIN_MP_API_URL + "/cgi-bin/showqrcode?ticket=%s";
        api_url = String.format(api_url , SecurityUtil.urlEncode(ticket));
        return restTemplateUtil.getForObject(api_url , byte[].class);
    }


    @Override
    public void redirectQrcode(String ticket, HttpServletResponse response) throws IOException {
        String api_url = WEIXIN_MP_API_URL + "/cgi-bin/showqrcode?ticket=%s";
        api_url = String.format(api_url , SecurityUtil.urlEncode(ticket));
        if(response==null){
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if(servletRequestAttributes!=null){
                response = servletRequestAttributes.getResponse();
            }
        }
        if(response!=null){
            response.sendRedirect(api_url);
        }
    }

    @Override
    public void redirectQrcode(String ticket) throws IOException {
        redirectQrcode( ticket, null);
    }


    @Override
    public Boolean setIndustry(Integer industry_id1, Integer industry_id2) {
        Map<String, Object> result =null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/template/api_set_industry?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();
        postData.put("industry_id1" , industry_id1);
        postData.put("industry_id2" , industry_id2);

        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            return true;
        }
        return false;
    }

    @Override
    public Map<String, Map<String, String>> getIndustry() {
        Map<String, Map<String, String>> result =null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/template/get_industry?access_token=%s";
        api_url = String.format(api_url , access_token);

        String rs =  restTemplateUtil.get(api_url);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("primary_industry") && jsonObject.containsKey("secondary_industry")){
            result = new HashMap<>();
            Map<String, String> map =new HashMap<>();
            map.put("first_class" , jsonObject.getJSONObject("primary_industry").getString("first_class"));
            map.put("second_class" , jsonObject.getJSONObject("primary_industry").getString("second_class"));
            result.put("primary_industry" , map);

            map =new HashMap<>();
            map.put("first_class" , jsonObject.getJSONObject("secondary_industry").getString("first_class"));
            map.put("second_class" , jsonObject.getJSONObject("secondary_industry").getString("second_class"));
            result.put("secondary_industry" , map);
        }
        return result;
    }


    @Override
    public String addPrivateTemplate(String templateNo) {
        String templateId =null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/template/api_add_template?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();
        postData.put("template_id_short" , templateNo);
        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            templateId = jsonObject.getString("template_id");
        }
        return templateId;

    }

    @Override
    public List<Map<String, Object>> getAllPrivateTemplate() {
        List<Map<String, Object>> result =null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/template/get_all_private_template?access_token=%s";
        api_url = String.format(api_url , access_token);

        String rs =  restTemplateUtil.get(api_url);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("template_list")){
           JSONArray jsonArray = jsonObject.getJSONArray("template_list");
           if(jsonArray!=null && jsonArray.size()>0){
               result = new ArrayList<>();
               for (int i=0;i < jsonArray.size();i++){
                   result.add(ObjectUtil.JsonObject2Map(jsonArray.getJSONObject(i)));
               }
           }
        }
        return result;
    }

    @Override
    public Boolean deletePrivateTemplate(String template_id) {
        Boolean result =false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/template/del_private_template?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();
        postData.put("template_id" , template_id);
        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;

    }

    @Override
    public Boolean sendTemplateMessage(String template_id, String touser, String url, String miniprogram_appid, String miniprogram_pagepath, Map<String, TemplateMessageData> data) {
        Boolean result =false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/template/send?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();
        postData.put("touser" , touser);
        postData.put("template_id" , template_id);
        if(!StringUtil.isNullOrEmpty(url)){
            postData.put("url" , url);
        }
        if(!StringUtil.isNullOrEmpty(miniprogram_appid) && !StringUtil.isNullOrEmpty(miniprogram_pagepath)){
            Map<String , Object> miniprogram = new HashMap<>();
            miniprogram.put("appid" , miniprogram_appid);
            miniprogram.put("pagepath" , miniprogram_pagepath);
            postData.put("miniprogram" , miniprogram);
        }
        postData.put("data" , data);


        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }


    @Override
    public void subscribeMessage(int scene, String template_id, String redirect_url, String reserved, HttpServletResponse response) throws IOException {
        if(response==null){
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if(servletRequestAttributes!=null){
                response = servletRequestAttributes.getResponse();
            }
        }
        if(response!=null){
            String api_url = WEIXIN_MP_API_URL + "/mp/subscribemsg?action=get_confirm&appid=%s&scene=%d&template_id=%s&redirect_url=%s&reserved=%s#wechat_redirect";
            api_url = String.format(api_url , this.getAppID() , scene , template_id , SecurityUtil.urlEncode(redirect_url) , SecurityUtil.urlEncode(reserved));
            response.sendRedirect(api_url);
        }
    }

    @Override
    public void subscribeMessage(int scene, String template_id, String redirect_url, String reserved) throws IOException{
        subscribeMessage(scene, template_id, redirect_url, reserved , null);
    }


    @Override
    public Boolean sendSubscribeMessage(String openid,int scene,String template_id,String action, String reserved, String title,String url , String miniprogram_appid, String miniprogram_pagepath , Map<String , TemplateMessageData> data){
        Boolean result =false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/template/subscribe?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();
        postData.put("touser" , openid);
        postData.put("template_id" , template_id);
        if(!StringUtil.isNullOrEmpty(url)){
            postData.put("url" , url);
        }
        if(!StringUtil.isNullOrEmpty(miniprogram_appid) && !StringUtil.isNullOrEmpty(miniprogram_pagepath)){
            Map<String , Object> miniprogram = new HashMap<>();
            miniprogram.put("appid" , miniprogram_appid);
            miniprogram.put("pagepath" , miniprogram_pagepath);
            postData.put("miniprogram" , miniprogram);
        }
        postData.put("scene" , scene);
        postData.put("title" , title);
        postData.put("data" , data);

        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }


    @Override
    public Map<String,String> sendAllNews(Boolean is_to_all, String tag_id, String media_id, int send_ignore_reprint,String clientmsgid) {
        Map<String,String> result =null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/mass/sendall?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();

        Map<String , Object> filter = new HashMap<>();
        filter.put("is_to_all" , is_to_all);
        filter.put("tag_id" , tag_id);
        postData.put("filter" , filter);

        Map<String , Object> mpnews = new HashMap<>();
        mpnews.put("media_id" , media_id);
        postData.put("mpnews" , mpnews);

        postData.put("msgtype" , "mpnews");
        postData.put("send_ignore_reprint" , send_ignore_reprint);
        if(!StringUtil.isNullOrEmpty(clientmsgid)){
            postData.put("clientmsgid" , clientmsgid);
        }

        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =new HashMap<>();
            result.put("msg_id" , jsonObject.getString("msg_id"));
            result.put("msg_data_id" , jsonObject.getString("msg_data_id"));
        }
        return result;
    }

    @Override
    public Map<String,String> sendAllText(Boolean is_to_all, String tag_id, String content,String clientmsgid) {
        Map<String,String> result =null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/mass/sendall?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();

        Map<String , Object> filter = new HashMap<>();
        filter.put("is_to_all" , is_to_all);
        filter.put("tag_id" , tag_id);
        postData.put("filter" , filter);

        Map<String , Object> text = new HashMap<>();
        text.put("content" , content);
        postData.put("text" , text);

        postData.put("msgtype" , "text");
        if(!StringUtil.isNullOrEmpty(clientmsgid)){
            postData.put("clientmsgid" , clientmsgid);
        }
        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =new HashMap<>();
            result.put("msg_id" , jsonObject.getString("msg_id"));
            result.put("msg_data_id" , jsonObject.getString("msg_data_id"));
        }
        return result;
    }

    @Override
    public Map<String,String> sendAllVoice(Boolean is_to_all, String tag_id, String media_id,String clientmsgid) {
        Map<String,String> result =null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/mass/sendall?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();

        Map<String , Object> filter = new HashMap<>();
        filter.put("is_to_all" , is_to_all);
        filter.put("tag_id" , tag_id);
        postData.put("filter" , filter);

        Map<String , Object> voice = new HashMap<>();
        voice.put("media_id" , media_id);
        postData.put("voice" , voice);

        postData.put("msgtype" , "voice");
        if(!StringUtil.isNullOrEmpty(clientmsgid)){
            postData.put("clientmsgid" , clientmsgid);
        }
        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =new HashMap<>();
            result.put("msg_id" , jsonObject.getString("msg_id"));
            result.put("msg_data_id" , jsonObject.getString("msg_data_id"));
        }
        return result;
    }

    @Override
    public Map<String,String> sendAllImages(Boolean is_to_all, String tag_id, String[] media_ids, String recommend, int need_open_comment, int only_fans_can_comment,String clientmsgid) {
        Map<String,String> result =null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/mass/sendall?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();

        Map<String , Object> filter = new HashMap<>();
        filter.put("is_to_all" , is_to_all);
        filter.put("tag_id" , tag_id);
        postData.put("filter" , filter);

        Map<String , Object> images = new HashMap<>();
        images.put("media_ids" , media_ids);
        images.put("recommend" , recommend);
        images.put("need_open_comment" , need_open_comment);
        images.put("only_fans_can_comment" , only_fans_can_comment);
        postData.put("images" , images);

        postData.put("msgtype" , "image");
        if(!StringUtil.isNullOrEmpty(clientmsgid)){
            postData.put("clientmsgid" , clientmsgid);
        }
        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =new HashMap<>();
            result.put("msg_id" , jsonObject.getString("msg_id"));
            result.put("msg_data_id" , jsonObject.getString("msg_data_id"));
        }
        return result;
    }

    @Override
    public Map<String,String> sendAllVideo(Boolean is_to_all, String tag_id, String media_id,String clientmsgid) {
        Map<String,String> result =null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/mass/sendall?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();

        Map<String , Object> filter = new HashMap<>();
        filter.put("is_to_all" , is_to_all);
        filter.put("tag_id" , tag_id);
        postData.put("filter" , filter);

        Map<String , Object> mpvideo = new HashMap<>();
        mpvideo.put("media_id" , media_id);
        postData.put("mpvideo" , mpvideo);

        postData.put("msgtype" , "mpvideo");
        if(!StringUtil.isNullOrEmpty(clientmsgid)){
            postData.put("clientmsgid" , clientmsgid);
        }
        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =new HashMap<>();
            result.put("msg_id" , jsonObject.getString("msg_id"));
            result.put("msg_data_id" , jsonObject.getString("msg_data_id"));
        }
        return result;
    }

    @Override
    public Map<String,String> sendAllCard(Boolean is_to_all, String tag_id, String card_id,String clientmsgid) {
        Map<String,String> result =null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/mass/sendall?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();

        Map<String , Object> filter = new HashMap<>();
        filter.put("is_to_all" , is_to_all);
        filter.put("tag_id" , tag_id);
        postData.put("filter" , filter);

        Map<String , Object> wxcard = new HashMap<>();
        wxcard.put("card_id" , card_id);
        postData.put("wxcard" , wxcard);

        postData.put("msgtype" , "wxcard");
        if(!StringUtil.isNullOrEmpty(clientmsgid)){
            postData.put("clientmsgid" , clientmsgid);
        }
        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =new HashMap<>();
            result.put("msg_id" , jsonObject.getString("msg_id"));
            result.put("msg_data_id" , jsonObject.getString("msg_data_id"));
        }
        return result;
    }


    @Override
    public Map<String,String> sendMassNews(String[] tousers, String media_id, int send_ignore_reprint,String clientmsgid) {
        Map<String,String> result =null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/mass/sendall?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();

        postData.put("touser" , tousers);

        Map<String , Object> mpnews = new HashMap<>();
        mpnews.put("media_id" , media_id);
        postData.put("mpnews" , mpnews);

        postData.put("msgtype" , "mpnews");
        postData.put("send_ignore_reprint" , send_ignore_reprint);
        if(!StringUtil.isNullOrEmpty(clientmsgid)){
            postData.put("clientmsgid" , clientmsgid);
        }
        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =new HashMap<>();
            result.put("msg_id" , jsonObject.getString("msg_id"));
            result.put("msg_data_id" , jsonObject.getString("msg_data_id"));
        }
        return result;
    }

    @Override
    public Map<String,String> sendMassText(String[] tousers, String content,String clientmsgid) {
        Map<String,String> result =null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/mass/sendall?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();

        postData.put("touser" , tousers);

        Map<String , Object> text = new HashMap<>();
        text.put("content" , content);
        postData.put("text" , text);

        postData.put("msgtype" , "text");
        if(!StringUtil.isNullOrEmpty(clientmsgid)){
            postData.put("clientmsgid" , clientmsgid);
        }
        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =new HashMap<>();
            result.put("msg_id" , jsonObject.getString("msg_id"));
            result.put("msg_data_id" , jsonObject.getString("msg_data_id"));
        }
        return result;
    }

    @Override
    public Map<String,String> sendMassVoice(String[] tousers, String media_id,String clientmsgid) {
        Map<String,String> result =null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/mass/sendall?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();

        postData.put("tousers" , tousers);

        Map<String , Object> voice = new HashMap<>();
        voice.put("media_id" , media_id);
        postData.put("voice" , voice);

        postData.put("msgtype" , "voice");
        if(!StringUtil.isNullOrEmpty(clientmsgid)){
            postData.put("clientmsgid" , clientmsgid);
        }
        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =new HashMap<>();
            result.put("msg_id" , jsonObject.getString("msg_id"));
            result.put("msg_data_id" , jsonObject.getString("msg_data_id"));
        }
        return result;
    }

    @Override
    public Map<String,String> sendMassImages(String[] tousers, String[] media_ids, String recommend, int need_open_comment, int only_fans_can_comment,String clientmsgid) {
        Map<String,String> result =null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/mass/sendall?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();

        postData.put("tousers" , tousers);

        Map<String , Object> images = new HashMap<>();
        images.put("media_ids" , media_ids);
        images.put("recommend" , recommend);
        images.put("need_open_comment" , need_open_comment);
        images.put("only_fans_can_comment" , only_fans_can_comment);
        postData.put("images" , images);

        postData.put("msgtype" , "image");
        if(!StringUtil.isNullOrEmpty(clientmsgid)){
            postData.put("clientmsgid" , clientmsgid);
        }
        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =new HashMap<>();
            result.put("msg_id" , jsonObject.getString("msg_id"));
            result.put("msg_data_id" , jsonObject.getString("msg_data_id"));
        }
        return result;
    }

    @Override
    public Map<String,String> sendMassVideo(String[] tousers, String media_id,String clientmsgid) {
        Map<String,String> result =null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/mass/sendall?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();

        postData.put("tousers" , tousers);

        Map<String , Object> mpvideo = new HashMap<>();
        mpvideo.put("media_id" , media_id);
        postData.put("mpvideo" , mpvideo);

        postData.put("msgtype" , "mpvideo");
        if(!StringUtil.isNullOrEmpty(clientmsgid)){
            postData.put("clientmsgid" , clientmsgid);
        }
        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =new HashMap<>();
            result.put("msg_id" , jsonObject.getString("msg_id"));
            result.put("msg_data_id" , jsonObject.getString("msg_data_id"));
        }
        return result;
    }

    @Override
    public Map<String,String> sendMassCard(String[] tousers, String card_id,String clientmsgid) {
        Map<String,String> result =null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/mass/sendall?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();

        postData.put("tousers" , tousers);

        Map<String , Object> wxcard = new HashMap<>();
        wxcard.put("card_id" , card_id);
        postData.put("wxcard" , wxcard);

        postData.put("msgtype" , "wxcard");
        if(!StringUtil.isNullOrEmpty(clientmsgid)){
            postData.put("clientmsgid" , clientmsgid);
        }
        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =new HashMap<>();
            result.put("msg_id" , jsonObject.getString("msg_id"));
            result.put("msg_data_id" , jsonObject.getString("msg_data_id"));
        }
        return result;
    }

    @Override
    public Boolean deleteMass(String msg_id, int article_idx) {
        Boolean result =false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/mass/delete?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();

        postData.put("msg_id" , msg_id);
        postData.put("article_idx" , article_idx);

        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }


    @Override
    public Boolean sendPreviewNews(String towxname,String touser, String media_id, int send_ignore_reprint) {
        Boolean result =false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/mass/sendall?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();

        postData.put("towxname" , towxname);
        postData.put("touser" , touser);

        Map<String , Object> mpnews = new HashMap<>();
        mpnews.put("media_id" , media_id);
        postData.put("mpnews" , mpnews);

        postData.put("msgtype" , "mpnews");
        postData.put("send_ignore_reprint" , send_ignore_reprint);

        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public Boolean sendPreviewText(String towxname, String touser, String content) {
        Boolean result =false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/mass/sendall?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();

        postData.put("towxname" , towxname);
        postData.put("touser" , touser);

        Map<String , Object> text = new HashMap<>();
        text.put("content" , content);
        postData.put("text" , text);

        postData.put("msgtype" , "text");

        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public Boolean sendPreviewVoice(String towxname, String touser, String media_id) {
        Boolean result =false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/mass/sendall?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();

        postData.put("towxname" , towxname);
        postData.put("touser" , touser);


        Map<String , Object> voice = new HashMap<>();
        voice.put("media_id" , media_id);
        postData.put("voice" , voice);

        postData.put("msgtype" , "voice");

        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public Boolean sendPreviewImages(String towxname, String touser, String[] media_ids, String recommend, int need_open_comment, int only_fans_can_comment) {
        Boolean result =false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/mass/sendall?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();

        postData.put("towxname" , towxname);
        postData.put("touser" , touser);


        Map<String , Object> images = new HashMap<>();
        images.put("media_ids" , media_ids);
        images.put("recommend" , recommend);
        images.put("need_open_comment" , need_open_comment);
        images.put("only_fans_can_comment" , only_fans_can_comment);
        postData.put("images" , images);

        postData.put("msgtype" , "image");

        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public Boolean sendPreviewVideo(String towxname, String touser, String media_id) {
        Boolean result =false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/mass/sendall?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();

        postData.put("towxname" , towxname);
        postData.put("touser" , touser);


        Map<String , Object> mpvideo = new HashMap<>();
        mpvideo.put("media_id" , media_id);
        postData.put("mpvideo" , mpvideo);

        postData.put("msgtype" , "mpvideo");

        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public Boolean sendPreviewCard(String towxname, String touser, String card_id) {
        Boolean result =false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/mass/sendall?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();

        postData.put("towxname" , towxname);
        postData.put("touser" , touser);


        Map<String , Object> wxcard = new HashMap<>();
        wxcard.put("card_id" , card_id);
        postData.put("wxcard" , wxcard);

        postData.put("msgtype" , "wxcard");

        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public String queryMassStatus(String msg_id) {
        String result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/mass/get?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();
        postData.put("msg_id" , msg_id);

        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("msg_status")){
            result =jsonObject.getString("msg_status");
        }
        return result;
    }

    @Override
    public Boolean setMassSpeed(int speed) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/mass/speed/set?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();
        postData.put("speed" , speed);

        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public Map<String, Object> getMassSpeed() {
        Map<String, Object> result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/mass/speed/get?access_token=%s";
        api_url = String.format(api_url , access_token);


        String rs =  restTemplateUtil.get(api_url);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = ObjectUtil.JsonObject2Map(jsonObject);
        }
        return result;
    }

    @Override
    public Boolean clearQuota() {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/clear_quota?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();
        postData.put("appid" , this.getAppID());

        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public String getCurrentAutoreplyInfo() {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/get_current_autoreply_info?access_token=%s";
        api_url = String.format(api_url , access_token);
        return restTemplateUtil.get(api_url);
    }

    @Override
    public String addNewtmplTemplate(String tid, String[] kidList, String sceneDesc) {
        String result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/wxaapi/newtmpl/addtemplate?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();
        postData.put("tid" , tid);
        postData.put("kidList" , kidList);
        postData.put("sceneDesc" , sceneDesc);

        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =jsonObject.getString("priTmplId");
        }
        return result;
    }

    @Override
    public Boolean deleteNewtmplTemplate(String priTmplId) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/wxaapi/newtmpl/deltemplate?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();
        postData.put("priTmplId" , priTmplId);

        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getNewtmplCategory() {
        List<Map<String, Object>> result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/wxaapi/newtmpl/getcategory?access_token=%s";
        api_url = String.format(api_url , access_token);


        String rs =  restTemplateUtil.get(api_url);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
           JSONArray jsonArray = jsonObject.getJSONArray("data");
           if(jsonArray!=null && jsonArray.size()>0){
               result = new ArrayList<>();
               for(int i=0 ;i< jsonArray.size();i++){
                   result.add(ObjectUtil.JsonObject2Map(jsonArray.getJSONObject(i)));
               }
           }
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getNewtmplPubTemplateKeywords(String tid) {
        List<Map<String, Object>> result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/wxaapi/newtmpl/getcategory?access_token=%s&tid=%s";
        api_url = String.format(api_url , access_token, tid);

        String rs =  restTemplateUtil.get(api_url);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            if(jsonArray!=null && jsonArray.size()>0){
                result = new ArrayList<>();
                for(int i=0 ;i< jsonArray.size();i++){
                    result.add(ObjectUtil.JsonObject2Map(jsonArray.getJSONObject(i)));
                }
            }
        }
        return result;
    }

    @Override
    public PageDataList getNewtmplPubTemplateTitles(String ids, int start, int limit) {
        PageDataList result = null;
        List<Map<String, Object>> data = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/wxaapi/newtmpl/getpubtemplatetitles?access_token=%s&ids=%s&start=%d&limit=%d";
        api_url = String.format(api_url , access_token, ids , start , limit);

        String rs =  restTemplateUtil.get(api_url);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =new PageDataList();
            result.setCount(jsonObject.getInteger("count"));
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            if(jsonArray!=null && jsonArray.size()>0){
                data = new ArrayList<>();
                for(int i=0 ;i< jsonArray.size();i++){
                    data.add(ObjectUtil.JsonObject2Map(jsonArray.getJSONObject(i)));
                }
                result.setData(data);
            }
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getNewtmplTemplate() {
        List<Map<String, Object>> result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/wxaapi/newtmpl/gettemplate?access_token=%s";
        api_url = String.format(api_url , access_token);

        String rs =  restTemplateUtil.get(api_url);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            if(jsonArray!=null && jsonArray.size()>0){
                result = new ArrayList<>();
                for(int i=0 ;i< jsonArray.size();i++){
                    result.add(ObjectUtil.JsonObject2Map(jsonArray.getJSONObject(i)));
                }
            }
        }
        return result;
    }

    @Override
    public Boolean sendNewtmplSubscribe(String touser, String template_id, String page, String miniprogram_appid, String miniprogram_pagepath, Map<String, Map<String, Object>> data) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/subscribe/bizsend?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();
        postData.put("touser" , touser);
        postData.put("template_id" , template_id);
        if(!StringUtil.isNullOrEmpty(page)){
            postData.put("page" , page);
        }
        if(!StringUtil.isNullOrEmpty(miniprogram_appid) && !StringUtil.isNullOrEmpty(miniprogram_pagepath)){
            Map<String , Object> miniprogram = new HashMap<>();
            miniprogram.put("appid" , miniprogram_appid);
            miniprogram.put("pagepath" , miniprogram_pagepath);
            postData.put("miniprogram" , miniprogram);
        }
        postData.put("data" , data);

        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }


    @Override
    public Boolean addCustomServiceKfAccount(String kf_account, String nickname, String password) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/customservice/kfaccount/add?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();
        postData.put("kf_account" , kf_account);
        postData.put("nickname" , nickname);
        postData.put("password" , SecurityUtil.md5(password));

        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public Boolean updateCustomServiceKfAccount(String kf_account, String nickname, String password) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/customservice/kfaccount/update?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();
        postData.put("kf_account" , kf_account);
        postData.put("nickname" , nickname);
        postData.put("password" , SecurityUtil.md5(password));

        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public Boolean deleteCustomServiceKfAccount(String kf_account) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/customservice/kfaccount/del?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();
        postData.put("kf_account" , kf_account);

        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public Boolean uploadheadimgCustomServiceKfAccount(String kf_account, String fileName) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/customservice/kfaccount/del?access_token=%s&kf_account=%s";
        api_url = String.format(api_url , access_token , kf_account);
        Map<String , String > files =new HashMap<>();
        files.put("media" , fileName);

        String rs =  restTemplateUtil.postForm(api_url, null,null , files);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getkflistCustomServiceKfAccount() {
        List<Map<String, Object>> result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/customservice/getkflist?access_token=%s";
        api_url = String.format(api_url , access_token);

        String rs =  restTemplateUtil.get(api_url);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("kf_list")){
            JSONArray jsonArray = jsonObject.getJSONArray("kf_list");
            if(jsonArray!=null && jsonArray.size()>0){
                result = new ArrayList<>();
                for(int i=0 ;i< jsonArray.size();i++){
                    result.add(ObjectUtil.JsonObject2Map(jsonArray.getJSONObject(i)));
                }
            }
        }
        return result;
    }

    @Override
    public Boolean sendtextCustomService(String touser, String content,String kf_account) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/custom/send?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("touser" , touser);

        postData.put("msgtype" , "text");
        Map<String , Object > text =new HashMap<>();
        text.put("content" , content);
        postData.put("text" , text);

        if(!StringUtil.isNullOrEmpty(kf_account)){
            Map<String , Object > customservice =new HashMap<>();
            customservice.put("kf_account" , kf_account);
            postData.put("customservice" , customservice);
        }

        String rs =  restTemplateUtil.postForObject(api_url , postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public Boolean sendimageCustomService(String touser, String media_id,String kf_account) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/custom/send?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("touser" , touser);

        postData.put("msgtype" , "image");
        Map<String , Object > image =new HashMap<>();
        image.put("media_id" , media_id);
        postData.put("image" , image);

        if(!StringUtil.isNullOrEmpty(kf_account)){
            Map<String , Object > customservice =new HashMap<>();
            customservice.put("kf_account" , kf_account);
            postData.put("customservice" , customservice);
        }

        String rs =  restTemplateUtil.postForObject(api_url , postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public Boolean sendvoiceCustomService(String touser, String media_id,String kf_account) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/custom/send?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("touser" , touser);

        postData.put("msgtype" , "voice");
        Map<String , Object > voice =new HashMap<>();
        voice.put("media_id" , media_id);
        postData.put("voice" , voice);

        if(!StringUtil.isNullOrEmpty(kf_account)){
            Map<String , Object > customservice =new HashMap<>();
            customservice.put("kf_account" , kf_account);
            postData.put("customservice" , customservice);
        }

        String rs =  restTemplateUtil.postForObject(api_url , postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public Boolean sendvideoCustomService(String touser, String media_id, String thumb_media_id, String title, String description,String kf_account) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/custom/send?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("touser" , touser);

        postData.put("msgtype" , "video");
        Map<String , Object > video =new HashMap<>();
        video.put("media_id" , media_id);
        video.put("thumb_media_id" , thumb_media_id);
        video.put("title" , title);
        video.put("description" , description);
        postData.put("video" , video);

        if(!StringUtil.isNullOrEmpty(kf_account)){
            Map<String , Object > customservice =new HashMap<>();
            customservice.put("kf_account" , kf_account);
            postData.put("customservice" , customservice);
        }

        String rs =  restTemplateUtil.postForObject(api_url , postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public Boolean sendmusicCustomService(String touser, String title, String description, String musicurl, String hqmusicurl, String thumb_media_id,String kf_account) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/custom/send?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("touser" , touser);

        postData.put("msgtype" , "music");
        Map<String , Object > music =new HashMap<>();
        music.put("title" , title);
        music.put("description" , description);
        music.put("musicurl" , musicurl);
        music.put("hqmusicurl" , hqmusicurl);
        music.put("thumb_media_id" , thumb_media_id);
        postData.put("music" , music);

        if(!StringUtil.isNullOrEmpty(kf_account)){
            Map<String , Object > customservice =new HashMap<>();
            customservice.put("kf_account" , kf_account);
            postData.put("customservice" , customservice);
        }

        String rs =  restTemplateUtil.postForObject(api_url , postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public Boolean sendnewsCustomService(String touser, List<Map<String,Object>> articles,String kf_account) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/custom/send?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("touser" , touser);

        postData.put("msgtype" , "news");
        Map<String , Object > news =new HashMap<>();
        news.put("articles" , articles);
        postData.put("news" , news);

        if(!StringUtil.isNullOrEmpty(kf_account)){
            Map<String , Object > customservice =new HashMap<>();
            customservice.put("kf_account" , kf_account);
            postData.put("customservice" , customservice);
        }

        String rs =  restTemplateUtil.postForObject(api_url , postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public Boolean sendnewsCustomService(String touser, String media_id,String kf_account) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/custom/send?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("touser" , touser);

        postData.put("msgtype" , "mpnews");
        Map<String , Object > mpnews =new HashMap<>();
        mpnews.put("media_id" , media_id);
        postData.put("mpnews" , mpnews);

        if(!StringUtil.isNullOrEmpty(kf_account)){
            Map<String , Object > customservice =new HashMap<>();
            customservice.put("kf_account" , kf_account);
            postData.put("customservice" , customservice);
        }

        String rs =  restTemplateUtil.postForObject(api_url , postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public Boolean sendarticleCustomService(String touser, String article_id,String kf_account) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/custom/send?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("touser" , touser);

        postData.put("msgtype" , "mpnewsarticle");
        Map<String , Object > mpnewsarticle =new HashMap<>();
        mpnewsarticle.put("article_id" , article_id);
        postData.put("mpnewsarticle" , mpnewsarticle);

        if(!StringUtil.isNullOrEmpty(kf_account)){
            Map<String , Object > customservice =new HashMap<>();
            customservice.put("kf_account" , kf_account);
            postData.put("customservice" , customservice);
        }

        String rs =  restTemplateUtil.postForObject(api_url , postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public Boolean sendmenuCustomService(String touser, String head_content, List<Map<String,Object>> list, String tail_content,String kf_account) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/custom/send?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("touser" , touser);

        postData.put("msgtype" , "msgmenu");
        Map<String , Object > msgmenu =new HashMap<>();
        msgmenu.put("head_content" , head_content);
        msgmenu.put("list" , list);
        msgmenu.put("tail_content" , tail_content);
        postData.put("msgmenu" , msgmenu);

        if(!StringUtil.isNullOrEmpty(kf_account)){
            Map<String , Object > customservice =new HashMap<>();
            customservice.put("kf_account" , kf_account);
            postData.put("customservice" , customservice);
        }

        String rs =  restTemplateUtil.postForObject(api_url , postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public Boolean sendcardCustomService(String touser, String card_id,String kf_account) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/custom/send?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("touser" , touser);

        postData.put("msgtype" , "wxcard");
        Map<String , Object > wxcard =new HashMap<>();
        wxcard.put("card_id" , card_id);
        postData.put("wxcard" , wxcard);

        if(!StringUtil.isNullOrEmpty(kf_account)){
            Map<String , Object > customservice =new HashMap<>();
            customservice.put("kf_account" , kf_account);
            postData.put("customservice" , customservice);
        }

        String rs =  restTemplateUtil.postForObject(api_url , postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public Boolean sendminiprogrampageCustomService(String touser, String title, String appid, String pagepath, String thumb_media_id,String kf_account){
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/custom/send?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("touser" , touser);

        postData.put("msgtype" , "wxcard");
        Map<String , Object > miniprogrampage =new HashMap<>();
        miniprogrampage.put("title" , title);
        miniprogrampage.put("appid" , appid);
        miniprogrampage.put("pagepath" , pagepath);
        miniprogrampage.put("thumb_media_id" , thumb_media_id);
        postData.put("miniprogrampage" , miniprogrampage);

        if(!StringUtil.isNullOrEmpty(kf_account)){
            Map<String , Object > customservice =new HashMap<>();
            customservice.put("kf_account" , kf_account);
            postData.put("customservice" , customservice);
        }

        String rs =  restTemplateUtil.postForObject(api_url , postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public Boolean typingCustomService(String touser, String command){
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/message/custom/typing?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("touser" , touser);

        postData.put("command" , command);

        String rs =  restTemplateUtil.postForObject(api_url , postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getonlineCustomService() {
        List<Map<String, Object>> result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/customservice/getonlinekflist?access_token=%s";
        api_url = String.format(api_url , access_token);

        String rs =  restTemplateUtil.get(api_url);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("kf_online_list")){
            JSONArray jsonArray = jsonObject.getJSONArray("kf_online_list");
            if(jsonArray!=null && jsonArray.size()>0){
                result = new ArrayList<>();
                for(int i=0 ;i< jsonArray.size();i++){
                    result.add(ObjectUtil.JsonObject2Map(jsonArray.getJSONObject(i)));
                }
            }
        }
        return result;
    }

    @Override
    public Boolean inviteworkerCustomService(String kf_account, String invite_wx) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/customservice/kfaccount/inviteworker?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("kf_account" , kf_account);
        postData.put("invite_wx" , invite_wx);

        String rs =  restTemplateUtil.postForObject(api_url , postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public Boolean createsessionCustomService(String kf_account, String openid) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/customservice/kfsession/create?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("kf_account" , kf_account);
        postData.put("openid" , openid);

        String rs =  restTemplateUtil.postForObject(api_url , postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public Boolean closesessionCustomService(String kf_account, String openid) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/customservice/kfsession/close?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("kf_account" , kf_account);
        postData.put("openid" , openid);

        String rs =  restTemplateUtil.postForObject(api_url , postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public String getsessionCustomService(String openid) {
        String result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/customservice/kfsession/getsession?access_token=%s&openid=%s";
        api_url = String.format(api_url , access_token , openid);


        String rs =  restTemplateUtil.get(api_url);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("kf_account")){
            result = jsonObject.getString("kf_account");
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getsessionlistCustomService(String kf_account) {
        List<Map<String, Object>> result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/customservice/kfsession/getsessionlist?access_token=%s&kf_account=%s";
        api_url = String.format(api_url , access_token , kf_account);

        String rs =  restTemplateUtil.get(api_url);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("sessionlist")){
            JSONArray jsonArray = jsonObject.getJSONArray("sessionlist");
            if(jsonArray!=null && jsonArray.size()>0){
                result = new ArrayList<>();
                for(int i=0 ;i< jsonArray.size();i++){
                    result.add(ObjectUtil.JsonObject2Map(jsonArray.getJSONObject(i)));
                }
            }
        }
        return result;
    }

    @Override
    public PageDataList getwaitcaseCustomService() {
        PageDataList result = null;
        List<Map<String, Object>> data = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/customservice/kfsession/getwaitcase?access_token=%s";
        api_url = String.format(api_url , access_token);

        String rs =  restTemplateUtil.get(api_url);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("waitcaselist")){
            result = new PageDataList();
            result.setCount(jsonObject.getInteger("count"));

            JSONArray jsonArray = jsonObject.getJSONArray("waitcaselist");
            if(jsonArray!=null && jsonArray.size()>0){
                data = new ArrayList<>();
                for(int i=0 ;i< jsonArray.size();i++){
                    data.add(ObjectUtil.JsonObject2Map(jsonArray.getJSONObject(i)));
                }
                result.setData(data);
            }
        }
        return result;
    }

    @Override
    public MsgDataList getmsglistCustomService(int starttime, int endtime, long msgid, int number) {
        MsgDataList result = null;
        List<Map<String, Object>> recordlist = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/customservice/msgrecord/getmsglist?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();
        postData.put("starttime" , starttime);
        postData.put("endtime" , endtime);
        postData.put("msgid" , msgid);
        postData.put("number" , number);

        String rs =  restTemplateUtil.postForObject(api_url , postData ,String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("recordlist")){
            result = new MsgDataList();
            result.setNumber(jsonObject.getInteger("number"));
            result.setMsgid(jsonObject.getLong("msgid"));


            JSONArray jsonArray = jsonObject.getJSONArray("recordlist");
            if(jsonArray!=null && jsonArray.size()>0){
                recordlist = new ArrayList<>();
                for(int i=0 ;i< jsonArray.size();i++){
                    recordlist.add(ObjectUtil.JsonObject2Map(jsonArray.getJSONObject(i)));
                }
                result.setRecordlist(recordlist);
            }
        }
        return result;
    }

    @Override
    public void oauth2Authorize(String redirect_uri, String scope, String state) throws IOException {
        oauth2Authorize(redirect_uri , scope , state , null);
    }

    @Override
    public void oauth2Authorize(String redirect_uri, String scope, String state, HttpServletResponse response) throws IOException {
        String api_url ="https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect";
        api_url = String.format(api_url , this.getAppID() , SecurityUtil.urlEncode(redirect_uri) , scope , state);
        if(response==null){
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if(servletRequestAttributes==null) return ;
            response = servletRequestAttributes.getResponse();
        }
        if(response!=null){
            response.sendRedirect(api_url);
        }
    }

    @Override
    public JSONObject getOauth2AccessToken(String code) {
        String key =null;
        String refresh_key = null;
        HttpServletRequest request =null;
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        if(servletRequestAttributes!=null) {
            request = servletRequestAttributes.getRequest();
        }
        if(request!=null){
            key = OAUTH2ACCESSTOKEN_PREFIX + appID + ":" + request.getSession().getId();
            refresh_key = OAUTH2REFRESHACCESSTOKEN_PREFIX + appID + ":" + request.getSession().getId();
        }
        JSONObject jsonObject = null;
        if(request!=null && this.getEnableAccessTokenRedisStorage() && !StringUtil.isNullOrEmpty(this.getAccessTokenRedisStorageName()) && this.redisFactory!=null){
            try {
                RedisUtil redisUtil = this.redisFactory.openSession(this.getAccessTokenRedisStorageName());
                jsonObject = redisUtil.get(key , JSONObject.class);
                if(jsonObject !=null && jsonObject.containsKey("access_token")){
                    return jsonObject;
                }
                jsonObject = redisUtil.get(refresh_key , JSONObject.class);
                if(jsonObject !=null && jsonObject.containsKey("refresh_token")){
                    jsonObject = refreshOauth2AccessToken(jsonObject.getString("refresh_token"));
                    if(jsonObject !=null && jsonObject.containsKey("access_token")){
                        return jsonObject;
                    }
                }
            } catch (RedisException e) {
                e.printStackTrace();
            }
        }

        String api_url = WEIXIN_API_URL + "/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
        api_url = String.format(api_url , appID , appSecret , code);
        jsonObject = JSONObject.parseObject(restTemplateUtil.get(api_url));
        if(request!=null && this.getEnableAccessTokenRedisStorage() && !StringUtil.isNullOrEmpty(this.getAccessTokenRedisStorageName()) && this.redisFactory!=null){
            try {
                RedisUtil redisUtil = this.redisFactory.openSession(this.getAccessTokenRedisStorageName());
                if(jsonObject.containsKey("access_token")){
                    redisUtil.set(key , jsonObject , jsonObject.getLong("expires_in"));
                    redisUtil.set(refresh_key , jsonObject , OAUTH2REFRESHACCESSTOKEN_EXPIRES);
                }
            } catch (RedisException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    @Override
    public JSONObject getOauth2AccessToken() {
        String key =null;
        String refresh_key = null;
        HttpServletRequest request =null;
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        if(servletRequestAttributes!=null) {
            request = servletRequestAttributes.getRequest();
        }
        if(request!=null){
            key = OAUTH2ACCESSTOKEN_PREFIX + appID + ":" + request.getSession().getId();
            refresh_key = OAUTH2REFRESHACCESSTOKEN_PREFIX + appID + ":" + request.getSession().getId();
        }
        JSONObject jsonObject = null;
        if(request!=null && this.getEnableAccessTokenRedisStorage() && !StringUtil.isNullOrEmpty(this.getAccessTokenRedisStorageName()) && this.redisFactory!=null){
            try {
                RedisUtil redisUtil = this.redisFactory.openSession(this.getAccessTokenRedisStorageName());
                jsonObject = redisUtil.get(key , JSONObject.class);
                if(jsonObject !=null && jsonObject.containsKey("access_token")){
                    return jsonObject;
                }
                jsonObject = redisUtil.get(refresh_key , JSONObject.class);
                if(jsonObject !=null && jsonObject.containsKey("refresh_token")){
                    jsonObject = refreshOauth2AccessToken(jsonObject.getString("refresh_token"));
                    if(jsonObject !=null && jsonObject.containsKey("access_token")){
                        return jsonObject;
                    }
                }
            } catch (RedisException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public JSONObject refreshOauth2AccessToken(String refresh_token) {
        String key =null;
        String refresh_key = null;
        HttpServletRequest request =null;
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        if(servletRequestAttributes!=null) {
            request = servletRequestAttributes.getRequest();
        }
        if(request!=null){
            key = OAUTH2ACCESSTOKEN_PREFIX + appID + ":" + request.getSession().getId();
            refresh_key = OAUTH2REFRESHACCESSTOKEN_PREFIX + appID + ":" + request.getSession().getId();
        }

        String api_url = WEIXIN_API_URL + "/sns/oauth2/refresh_token?appid=%s&grant_type=refresh_token&refresh_token=%s";
        api_url = String.format(api_url , appID , refresh_token);
        JSONObject jsonObject = JSONObject.parseObject(restTemplateUtil.get(api_url));
        if(request!=null && this.getEnableAccessTokenRedisStorage() && !StringUtil.isNullOrEmpty(this.getAccessTokenRedisStorageName()) && this.redisFactory!=null){
            try {
                RedisUtil redisUtil = this.redisFactory.openSession(this.getAccessTokenRedisStorageName());
                if(jsonObject.containsKey("access_token")){
                    redisUtil.set(key , jsonObject , jsonObject.getLong("expires_in"));
                    redisUtil.set(refresh_key , jsonObject , OAUTH2REFRESHACCESSTOKEN_EXPIRES);
                }
            } catch (RedisException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }


    @Override
    public JSONObject getSnsUserInfo(String access_token, String openid, String lang) {
        String api_url = WEIXIN_API_URL + "/sns/auth?access_token=%s&openid=%s";
        api_url = String.format(api_url , access_token , openid , lang);
        return JSONObject.parseObject(restTemplateUtil.get(api_url));
    }

    @Override
    public Boolean checkOauth2AccessToken(String access_token, String openid) {
        Boolean result = false;
        String api_url = WEIXIN_API_URL + "/sns/auth?access_token=%s&openid=%s";
        api_url = String.format(api_url , access_token , openid);
        JSONObject jsonObject = JSONObject.parseObject(restTemplateUtil.get(api_url));
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }

    @Override
    public String getJsApiTicket() {
        String jsapi_ticket = null;
        String key = JSAPI_TICKET_PREFIX + this.getAppID();

        if(this.getEnableAccessTokenRedisStorage() && !StringUtil.isNullOrEmpty(this.getAccessTokenRedisStorageName()) && this.redisFactory!=null){
            try {
                RedisUtil redisUtil = this.redisFactory.openSession(this.getAccessTokenRedisStorageName());
                jsapi_ticket =  redisUtil.get(key);
                if(jsapi_ticket!=null){
                    return jsapi_ticket;
                }
            } catch (RedisException e) {
                e.printStackTrace();
            }
        }

        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/ticket/getticket?access_token=%s&type=jsapi";
        api_url = String.format(api_url , access_token);


        String rs =  restTemplateUtil.get(api_url);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("ticket")){
           jsapi_ticket = jsonObject.getString("ticket");
           if(this.getEnableAccessTokenRedisStorage() && !StringUtil.isNullOrEmpty(this.getAccessTokenRedisStorageName()) && this.redisFactory!=null){
               try {
                   RedisUtil redisUtil = this.redisFactory.openSession(this.getAccessTokenRedisStorageName());
                   redisUtil.set(key , jsapi_ticket , jsonObject.getLong("expires_in"));
               } catch (RedisException e) {
                   e.printStackTrace();
               }
           }
        }
        return jsapi_ticket;
    }

    @Override
    public Map<String, Object> jsApiSignature(String url) throws Exception {
        String nonce_str = StringUtil.getRandomString(32);
        Long timestamp= DateUtil.getTime();
        String jsapi_ticket= getJsApiTicket();
        // 注意这里参数名必须全部小写，且必须有序
        String  string1 = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonce_str
                + "&timestamp=" + timestamp  + "&url=" + url;

        String signature = SecurityUtil.sha(string1);
        HashMap<String, Object> signatureMap=new HashMap<>();
        signatureMap.put("appId", this.getAppID());
        signatureMap.put("timestamp", timestamp);
        signatureMap.put("nonceStr", nonce_str);
        signatureMap.put("signature", signature);
        return signatureMap;
    }


    @Override
    public String addDraft(List<ArticleItem> articleItems) {
        String result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/draft/add?access_token=%s";
        api_url = String.format(api_url , access_token);

        String rs =  restTemplateUtil.postForObject(api_url, articleItems , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("media_id")){
            result = jsonObject.getString("media_id");
        }
        return result;
    }

    @Override
    public List<ArticleItem> getDraft(String media_id) {
        List<ArticleItem> result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/draft/get?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("media_id" , media_id);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("news_item")){
            JSONArray jsonArray = jsonObject.getJSONArray("news_item");
            if(jsonArray.size()>0){
                result = new ArrayList<>();
                for(int i=0;i < jsonArray.size();i++){
                    ArticleItem article =new ArticleItem();
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    try {
                        if(jsonObject1.containsKey("title")){
                            article.setTitle(new String(jsonObject1.getString("title").getBytes(WEIXIN_CHARSET)));
                        }
                        if(jsonObject1.containsKey("thumb_media_id")){
                            article.setThumb_media_id(new String(jsonObject1.getString("thumb_media_id").getBytes(WEIXIN_CHARSET)));
                        }
                        if(jsonObject1.containsKey("thumb_url")){
                            article.setThumb_url(new String(jsonObject1.getString("thumb_url").getBytes(WEIXIN_CHARSET)));
                        }
                        if(jsonObject1.containsKey("author")){
                            article.setAuthor(new String(jsonObject1.getString("author").getBytes(WEIXIN_CHARSET)));
                        }
                        if(jsonObject1.containsKey("digest")){
                            article.setDigest(new String(jsonObject1.getString("digest").getBytes(WEIXIN_CHARSET)));
                        }
                        if(jsonObject1.containsKey("content")){
                            article.setContent(new String(jsonObject1.getString("content").getBytes(WEIXIN_CHARSET)));
                        }
                        if(jsonObject1.containsKey("content_source_url")){
                            article.setContent_source_url(new String(jsonObject1.getString("content_source_url").getBytes(WEIXIN_CHARSET)));
                        }
                        if(jsonObject1.containsKey("url")){
                            article.setUrl(new String(jsonObject1.getString("url").getBytes(WEIXIN_CHARSET)));
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if(jsonObject1.containsKey("show_cover_pic")){
                        article.setShow_cover_pic(jsonObject1.getInteger("show_cover_pic"));
                    }
                    if(jsonObject1.containsKey("need_open_comment")){
                        article.setNeed_open_comment(jsonObject1.getInteger("need_open_comment"));
                    }
                    if(jsonObject1.containsKey("only_fans_can_comment")){
                        article.setOnly_fans_can_comment(jsonObject1.getInteger("only_fans_can_comment"));
                    }
                    result.add(article);
                }
            }
        }
        return result;
    }

    @Override
    public Boolean deleteDraft(String media_id) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/draft/delete?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("media_id" , media_id);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }

    @Override
    public Boolean updateDraft(String media_id, int index, ArticleItem articleItem) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/draft/update?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("media_id" , media_id);
        postData.put("index" , index);
        postData.put("articles" , articleItem);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }

    @Override
    public int getDraftcount() {
        int result = 0;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/draft/count?access_token=%s";
        api_url = String.format(api_url , access_token);

        String rs =  restTemplateUtil.get(api_url);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("total_count")){
            result = jsonObject.getInteger("total_count");
        }
        return result;
    }

    @Override
    public PageNewsMaterialList getDraftList(int offset, int count, int no_content) {
        PageNewsMaterialList result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/draft/batchget?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("offset" , offset);
        postData.put("count" , count);
        postData.put("no_content" , no_content);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("total_count")){
            result = new PageNewsMaterialList();
            result.setTotal_count(jsonObject.getInteger("total_count"));
            result.setItem_count(jsonObject.getInteger("item_count"));
            List<ArticleItem> items = new ArrayList<>();
            JSONArray itemJsonArray = jsonObject.getJSONArray("item");
            for(int i=0; i < itemJsonArray.size(); i++){
                JSONObject jsonObject1 = itemJsonArray.getJSONObject(i);
                NewsMaterial newsMaterial = new NewsMaterial();
                newsMaterial.setMedia_id(jsonObject1.getString("media_id"));
                newsMaterial.setUpdate_time(jsonObject1.getLong("update_time"));
                if(jsonObject1.containsKey("content") && jsonObject1.getJSONObject("content").containsKey("news_item")){
                    JSONArray news_items = jsonObject1.getJSONObject("content").getJSONArray("news_item");
                    if(news_items.size()>0){
                        List<ArticleItem> articleItems = new ArrayList<>();
                        for(int j=0;j < news_items.size() ; j++){
                            ArticleItem article =new ArticleItem();
                            JSONObject jsonObject2 = news_items.getJSONObject(j);
                            try {
                                if(jsonObject2.containsKey("title")){
                                    article.setTitle(new String(jsonObject2.getString("title").getBytes(WEIXIN_CHARSET)));
                                }
                                if(jsonObject2.containsKey("thumb_media_id")){
                                    article.setThumb_media_id(new String(jsonObject2.getString("thumb_media_id").getBytes(WEIXIN_CHARSET)));
                                }
                                if(jsonObject2.containsKey("thumb_url")){
                                    article.setThumb_url(new String(jsonObject2.getString("thumb_url").getBytes(WEIXIN_CHARSET)));
                                }
                                if(jsonObject2.containsKey("author")){
                                    article.setAuthor(new String(jsonObject2.getString("author").getBytes(WEIXIN_CHARSET)));
                                }
                                if(jsonObject2.containsKey("digest")){
                                    article.setDigest(new String(jsonObject2.getString("digest").getBytes(WEIXIN_CHARSET)));
                                }
                                if(jsonObject2.containsKey("content")){
                                    article.setContent(new String(jsonObject2.getString("content").getBytes(WEIXIN_CHARSET)));
                                }
                                if(jsonObject2.containsKey("content_source_url")){
                                    article.setContent_source_url(new String(jsonObject2.getString("content_source_url").getBytes(WEIXIN_CHARSET)));
                                }
                                if(jsonObject2.containsKey("url")){
                                    article.setUrl(new String(jsonObject2.getString("url").getBytes(WEIXIN_CHARSET)));
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            if(jsonObject2.containsKey("show_cover_pic")){
                                article.setShow_cover_pic(jsonObject2.getInteger("show_cover_pic"));
                            }
                            if(jsonObject2.containsKey("need_open_comment")){
                                article.setNeed_open_comment(jsonObject2.getInteger("need_open_comment"));
                            }
                            if(jsonObject2.containsKey("only_fans_can_comment")){
                                article.setOnly_fans_can_comment(jsonObject2.getInteger("only_fans_can_comment"));
                            }
                            articleItems.add(article);
                        }
                        newsMaterial.setNews_item(articleItems);
                    }
                }
            }
        }
        return result;
    }


    @Override
    public String freePublishDraft(String media_id) {
        String result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/freepublish/submit?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("media_id" , media_id);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = jsonObject.getString("publish_id");
        }
        return result;
    }


    @Override
    public JSONObject getFreePublish(String publish_id) {
        JSONObject result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/freepublish/get?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("publish_id" , publish_id);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        result = JSONObject.parseObject(rs);
        return result;
    }

    @Override
    public Boolean deleteFreePublish(String article_id, int index) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/freepublish/delete?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("article_id" , article_id);
        postData.put("index" , index);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }


    @Override
    public List<ArticleItem> getFreePublishArticle(String article_id) {
        List<ArticleItem> result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/freepublish/getarticle?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("article_id" , article_id);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("news_item")){
            JSONArray jsonArray = jsonObject.getJSONArray("news_item");
            if(jsonArray.size()>0){
                result = new ArrayList<>();
                for(int i=0;i < jsonArray.size();i++){
                    ArticleItem article =new ArticleItem();
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    try {
                        if(jsonObject1.containsKey("title")){
                            article.setTitle(new String(jsonObject1.getString("title").getBytes(WEIXIN_CHARSET)));
                        }
                        if(jsonObject1.containsKey("thumb_media_id")){
                            article.setThumb_media_id(new String(jsonObject1.getString("thumb_media_id").getBytes(WEIXIN_CHARSET)));
                        }
                        if(jsonObject1.containsKey("thumb_url")){
                            article.setThumb_url(new String(jsonObject1.getString("thumb_url").getBytes(WEIXIN_CHARSET)));
                        }
                        if(jsonObject1.containsKey("author")){
                            article.setAuthor(new String(jsonObject1.getString("author").getBytes(WEIXIN_CHARSET)));
                        }
                        if(jsonObject1.containsKey("digest")){
                            article.setDigest(new String(jsonObject1.getString("digest").getBytes(WEIXIN_CHARSET)));
                        }
                        if(jsonObject1.containsKey("content")){
                            article.setContent(new String(jsonObject1.getString("content").getBytes(WEIXIN_CHARSET)));
                        }
                        if(jsonObject1.containsKey("content_source_url")){
                            article.setContent_source_url(new String(jsonObject1.getString("content_source_url").getBytes(WEIXIN_CHARSET)));
                        }
                        if(jsonObject1.containsKey("url")){
                            article.setUrl(new String(jsonObject1.getString("url").getBytes(WEIXIN_CHARSET)));
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if(jsonObject1.containsKey("show_cover_pic")){
                        article.setShow_cover_pic(jsonObject1.getInteger("show_cover_pic"));
                    }
                    if(jsonObject1.containsKey("need_open_comment")){
                        article.setNeed_open_comment(jsonObject1.getInteger("need_open_comment"));
                    }
                    if(jsonObject1.containsKey("only_fans_can_comment")){
                        article.setOnly_fans_can_comment(jsonObject1.getInteger("only_fans_can_comment"));
                    }
                    if(jsonObject1.containsKey("is_deleted")){
                        article.setIs_deleted(jsonObject1.getBoolean("is_deleted"));
                    }
                    result.add(article);
                }
            }
        }
        return result;
    }


    @Override
    public PageArticleMaterialList getFreePublishArticles(int offset, int count, int no_content) {
        PageArticleMaterialList result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/freepublish/batchget?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("offset" , offset);
        postData.put("count" , count);
        postData.put("no_content" , no_content);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("total_count")){
            result = new PageArticleMaterialList();
            result.setTotal_count(jsonObject.getInteger("total_count"));
            result.setItem_count(jsonObject.getInteger("item_count"));
            List<ArticleMaterial> items = new ArrayList<>();
            JSONArray itemJsonArray = jsonObject.getJSONArray("item");
            for(int i=0; i < itemJsonArray.size(); i++){
                JSONObject jsonObject1 = itemJsonArray.getJSONObject(i);
                ArticleMaterial articleMaterial = new ArticleMaterial();
                articleMaterial.setArticle_id(jsonObject1.getString("article_id"));
                articleMaterial.setUpdate_time(jsonObject1.getLong("update_time"));
                if(jsonObject1.containsKey("content") && jsonObject1.getJSONObject("content").containsKey("news_item")){
                    JSONArray news_items = jsonObject1.getJSONObject("content").getJSONArray("news_item");
                    if(news_items.size()>0){
                        List<ArticleItem> articleItems = new ArrayList<>();
                        for(int j=0;j < news_items.size() ; j++){
                            ArticleItem article =new ArticleItem();
                            JSONObject jsonObject2 = news_items.getJSONObject(j);
                            try {
                                if(jsonObject2.containsKey("title")){
                                    article.setTitle(new String(jsonObject2.getString("title").getBytes(WEIXIN_CHARSET)));
                                }
                                if(jsonObject2.containsKey("thumb_media_id")){
                                    article.setThumb_media_id(new String(jsonObject2.getString("thumb_media_id").getBytes(WEIXIN_CHARSET)));
                                }
                                if(jsonObject2.containsKey("thumb_url")){
                                    article.setThumb_url(new String(jsonObject2.getString("thumb_url").getBytes(WEIXIN_CHARSET)));
                                }
                                if(jsonObject2.containsKey("author")){
                                    article.setAuthor(new String(jsonObject2.getString("author").getBytes(WEIXIN_CHARSET)));
                                }
                                if(jsonObject2.containsKey("digest")){
                                    article.setDigest(new String(jsonObject2.getString("digest").getBytes(WEIXIN_CHARSET)));
                                }
                                if(jsonObject2.containsKey("content")){
                                    article.setContent(new String(jsonObject2.getString("content").getBytes(WEIXIN_CHARSET)));
                                }
                                if(jsonObject2.containsKey("content_source_url")){
                                    article.setContent_source_url(new String(jsonObject2.getString("content_source_url").getBytes(WEIXIN_CHARSET)));
                                }
                                if(jsonObject2.containsKey("url")){
                                    article.setUrl(new String(jsonObject2.getString("url").getBytes(WEIXIN_CHARSET)));
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            if(jsonObject2.containsKey("show_cover_pic")){
                                article.setShow_cover_pic(jsonObject2.getInteger("show_cover_pic"));
                            }
                            if(jsonObject2.containsKey("need_open_comment")){
                                article.setNeed_open_comment(jsonObject2.getInteger("need_open_comment"));
                            }
                            if(jsonObject2.containsKey("only_fans_can_comment")){
                                article.setOnly_fans_can_comment(jsonObject2.getInteger("only_fans_can_comment"));
                            }
                            if(jsonObject2.containsKey("is_deleted")){
                                article.setIs_deleted(jsonObject2.getBoolean("is_deleted"));
                            }
                            articleItems.add(article);
                        }
                        articleMaterial.setNews_item(articleItems);
                    }
                }
            }
        }
        return result;
    }


    @Override
    public Boolean openComment(int msg_data_id, int index) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/comment/open?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("msg_data_id" , msg_data_id);
        postData.put("index" , index);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }

    @Override
    public Boolean closeComment(int msg_data_id, int index) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/comment/close?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("msg_data_id" , msg_data_id);
        postData.put("index" , index);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }


    @Override
    public PageCommentList getCommentList(int msg_data_id, int index, int begin, int count, int type) {
        PageCommentList result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/comment/list?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("msg_data_id" , msg_data_id);
        postData.put("index" , index);
        postData.put("begin" , begin);
        postData.put("count" , count);
        postData.put("type" , type);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("total")){
            result = new PageCommentList();
            result.setTotal(jsonObject.getInteger("total"));
            List<Comment> items = new ArrayList<>();
            JSONArray itemJsonArray = jsonObject.getJSONArray("comment");
            for(int i=0; i < itemJsonArray.size(); i++){
                JSONObject jsonObject1 = itemJsonArray.getJSONObject(i);
                Comment comment = new Comment();
                comment.setUser_comment_id(jsonObject1.getInteger("user_comment_id"));
                comment.setOpenid(jsonObject1.getString("openid"));
                comment.setContent(jsonObject1.getString("content"));
                comment.setComment_type(jsonObject1.getInteger("comment_type"));
                comment.setCreate_time(jsonObject1.getLong("create_time"));

                if(jsonObject1.containsKey("reply")){
                    CommentReply reply = new CommentReply();
                    reply.setContent(jsonObject1.getJSONObject("reply").getString("content"));
                    reply.setCreate_time(jsonObject1.getJSONObject("reply").getLong("create_time"));
                    comment.setReply(reply);
                }
                items.add(comment);
            }
            result.setComment(items);
        }
        return result;
    }

    @Override
    public Boolean markelectComment(int msg_data_id, int index, int user_comment_id) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/comment/markelect?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("msg_data_id" , msg_data_id);
        postData.put("index" , index);
        postData.put("user_comment_id" , user_comment_id);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }

    @Override
    public Boolean unmarkelectComment(int msg_data_id, int index, int user_comment_id) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/comment/unmarkelect?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("msg_data_id" , msg_data_id);
        postData.put("index" , index);
        postData.put("user_comment_id" , user_comment_id);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }

    @Override
    public Boolean deleteComment(int msg_data_id, int index, int user_comment_id) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/comment/delete?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("msg_data_id" , msg_data_id);
        postData.put("index" , index);
        postData.put("user_comment_id" , user_comment_id);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }

    @Override
    public Boolean replyComment(int msg_data_id, int index, int user_comment_id, String content) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/comment/reply/add?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("msg_data_id" , msg_data_id);
        postData.put("index" , index);
        postData.put("user_comment_id" , user_comment_id);
        postData.put("content" , content);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }


    @Override
    public Boolean deleteReplyComment(int msg_data_id, int index, int user_comment_id) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/comment/reply/delete?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("msg_data_id" , msg_data_id);
        postData.put("index" , index);
        postData.put("user_comment_id" , user_comment_id);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }


    @Override
    public String createTag(String name) {
        String result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/tags/create?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        Map<String , Object > tag =new HashMap<>();
        tag.put("name" , name);
        postData.put("tag" , tag);

        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("tag")){
            result = jsonObject.getJSONObject("tag").getString("id");
        }
        return result;
    }


    @Override
    public List<Tag> getTags() {
        List<Tag> result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/tags/get?access_token=%s";
        api_url = String.format(api_url , access_token);


        String rs =  restTemplateUtil.get(api_url);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("tags")){
            JSONArray jsonArray = jsonObject.getJSONArray("tags");
            if(jsonArray.size()>0){
                result =new ArrayList<>();
                for(int i =0 ;i< jsonArray.size(); i++){
                    Tag tag =new Tag();
                    tag.setId(jsonArray.getJSONObject(i).getString("id"));
                    tag.setName(jsonArray.getJSONObject(i).getString("name"));
                    tag.setCount(jsonArray.getJSONObject(i).getInteger("count"));
                    result.add(tag);
                }
            }
        }
        return result;
    }

    @Override
    public Boolean updateTag(String id, String name) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/tags/update?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        Map<String , Object > tag =new HashMap<>();
        tag.put("id" , id);
        tag.put("name" , name);
        postData.put("tag" , tag);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }

    @Override
    public Boolean deleteTag(String id) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/tags/delete?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        Map<String , Object > tag =new HashMap<>();
        tag.put("id" , id);
        postData.put("tag" , tag);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }


    @Override
    public PageTagUserList getTagUsers(String tagid, String next_openid) {
        PageTagUserList result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/user/tag/get?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        Map<String , Object > tag =new HashMap<>();
        tag.put("tagid" , tagid);
        if(StringUtil.isNullOrEmpty(next_openid)){
            next_openid = "";
        }
        postData.put("next_openid" , next_openid);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("data")){
            result =new PageTagUserList();
            result.setCount(jsonObject.getInteger("count"));
            result.setNext_openid(jsonObject.getString("next_openid"));
            JSONArray jsonArray = jsonObject.getJSONArray("openid");
            if(jsonArray.size()>0){
                List<String> openids = new ArrayList<>();
                for(int i=0; i< jsonArray.size();i++){
                    openids.add(jsonArray.getString(i));
                }
                result.setOpenids(openids);
            }
        }
        return result;
    }

    @Override
    public Boolean addTagUsers(String tagid, List<String> openids) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/tags/members/batchtagging?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("tagid" , tagid);
        postData.put("openid_list" , openids);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }

    @Override
    public Boolean deleteTagUsers(String tagid, List<String> openids) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/tags/members/batchuntagging?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("tagid" , tagid);
        postData.put("openid_list" , openids);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }


    @Override
    public List<String> getTagsByUser(String openid) {
        List<String> result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/tags/getidlist?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("openid" , openid);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("tagid_list")){
            result = new ArrayList<>();
            JSONArray jsonArray = jsonObject.getJSONArray("tagid_list");
            if(jsonArray.size()>0){
                for(int i=0; i< jsonArray.size();i++){
                    result.add(jsonArray.getString(i));
                }
            }
        }
        return result;
    }

    @Override
    public Boolean updateUserRemark(String openid, String remark) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/user/info/updateremark?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("openid" , openid);
        postData.put("remark" , remark);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }

    @Override
    public PageUserList getUserList(String next_openid) {
        PageUserList result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        if(StringUtil.isNullOrEmpty(next_openid)){
            next_openid = "";
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/user/get?access_token=%s&next_openid=%s";
        api_url = String.format(api_url , access_token , next_openid);
        String rs =  restTemplateUtil.get(api_url);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("data")){
            result =new PageUserList();
            result.setTotal(jsonObject.getInteger("total"));
            result.setCount(jsonObject.getInteger("count"));
            result.setNext_openid(jsonObject.getString("next_openid"));
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("openid");
            if(jsonArray.size()>0){
                List<String> openids = new ArrayList<>();
                for(int i=0; i< jsonArray.size();i++){
                    openids.add(jsonArray.getString(i));
                }
                result.setOpenids(openids);
            }
        }
        return result;
    }

    @Override
    public PageUserList getBlackUserList(String next_openid) {
        PageUserList result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        if(StringUtil.isNullOrEmpty(next_openid)){
            next_openid = "";
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/tags/members/getblacklist?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("begin_openid" , next_openid);

        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("data")){
            result =new PageUserList();
            result.setTotal(jsonObject.getInteger("total"));
            result.setCount(jsonObject.getInteger("count"));
            result.setNext_openid(jsonObject.getString("next_openid"));
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("openid");
            if(jsonArray.size()>0){
                List<String> openids = new ArrayList<>();
                for(int i=0; i< jsonArray.size();i++){
                    openids.add(jsonArray.getString(i));
                }
                result.setOpenids(openids);
            }
        }
        return result;
    }


    @Override
    public Boolean addBlackUser(List<String> openid_list) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/tags/members/batchblacklist?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("openid_list" , openid_list);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }

    @Override
    public Boolean deleteBlackUser(List<String> openid_list) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/tags/members/batchunblacklist?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("openid_list" , openid_list);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }

    @Override
    public String shorturl(String long_url) {
        String result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/shorturl?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("action" , "long2short");
        postData.put("long_url" , long_url);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("short_url")){
            result = jsonObject.getString("short_url");
        }
        return result;
    }

    @Override
    public String getShorten(String long_data, int expire_seconds) {
        String result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/shorten/gen?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("long_data" , long_data);
        postData.put("expire_seconds" , expire_seconds);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("short_key")){
            result = jsonObject.getString("short_key");
        }
        return result;
    }

    @Override
    public Map<String,Object> fetchShorten(String short_key) {
        Map<String,Object> result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cgi-bin/shorten/fetch?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object > postData =new HashMap<>();
        postData.put("short_key" , short_key);
        String rs =  restTemplateUtil.postForObject(api_url, postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("long_data")){
            result = ObjectUtil.JsonObject2Map(jsonObject);
        }
        return result;
    }


    @Override
    public Map<String, Object> ocrIdcardImgUrl(String img_url) {
        Map<String,Object> result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cv/ocr/idcard?img_url=%s&access_token=%s";
        api_url = String.format(api_url ,img_url, access_token);

        String rs =  restTemplateUtil.get(api_url);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null){
            result = ObjectUtil.JsonObject2Map(jsonObject);
        }
        return result;
    }

    @Override
    public Map<String, Object> ocrIdcardImg(String imgFileName) {
        Map<String,Object> result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cv/ocr/idcard?access_token=%s";
        api_url = String.format(api_url ,access_token);
        Map<String , String > files =new HashMap<>();
        files.put("img" , imgFileName);

        String rs =  restTemplateUtil.postForm(api_url, null, null ,files);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null){
            result = ObjectUtil.JsonObject2Map(jsonObject);
        }
        return result;
    }

    @Override
    public Map<String, Object> ocrBankcardImgUrl(String img_url) {
        Map<String,Object> result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cv/ocr/bankcard?img_url=%s&access_token=%s";
        api_url = String.format(api_url ,img_url, access_token);

        String rs =  restTemplateUtil.get(api_url);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null){
            result = ObjectUtil.JsonObject2Map(jsonObject);
        }
        return result;
    }

    @Override
    public Map<String, Object> ocrBankcardImg(String imgFileName) {
        Map<String,Object> result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cv/ocr/bankcard?access_token=%s";
        api_url = String.format(api_url ,access_token);
        Map<String , String > files =new HashMap<>();
        files.put("img" , imgFileName);

        String rs =  restTemplateUtil.postForm(api_url, null, null ,files);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null){
            result = ObjectUtil.JsonObject2Map(jsonObject);
        }
        return result;
    }

    @Override
    public JSONObject ocrDrivingImgUrl(String img_url) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cv/ocr/driving?img_url=%s&access_token=%s";
        api_url = String.format(api_url ,img_url, access_token);

        String rs =  restTemplateUtil.get(api_url);
        return JSONObject.parseObject(rs);
    }

    @Override
    public JSONObject ocrDrivingImg(String imgFileName) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cv/ocr/driving?access_token=%s";
        api_url = String.format(api_url ,access_token);
        Map<String , String > files =new HashMap<>();
        files.put("img" , imgFileName);

        String rs =  restTemplateUtil.postForm(api_url, null, null ,files);
        return JSONObject.parseObject(rs);
    }

    @Override
    public JSONObject ocrDrivinglicenseImgUrl(String img_url) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cv/ocr/drivinglicense?img_url=%s&access_token=%s";
        api_url = String.format(api_url ,img_url, access_token);

        String rs =  restTemplateUtil.get(api_url);
        return JSONObject.parseObject(rs);
    }

    @Override
    public JSONObject ocrDrivinglicenseImg(String imgFileName) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cv/ocr/drivinglicense?access_token=%s";
        api_url = String.format(api_url ,access_token);
        Map<String , String > files =new HashMap<>();
        files.put("img" , imgFileName);

        String rs =  restTemplateUtil.postForm(api_url, null, null ,files);
        return JSONObject.parseObject(rs);
    }

    @Override
    public JSONObject ocrBizlicenseImgUrl(String img_url) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cv/ocr/bizlicense?img_url=%s&access_token=%s";
        api_url = String.format(api_url ,img_url, access_token);

        String rs =  restTemplateUtil.get(api_url);
        return JSONObject.parseObject(rs);
    }

    @Override
    public JSONObject ocrBizlicenseImg(String imgFileName) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cv/ocr/bizlicense?access_token=%s";
        api_url = String.format(api_url ,access_token);
        Map<String , String > files =new HashMap<>();
        files.put("img" , imgFileName);

        String rs =  restTemplateUtil.postForm(api_url, null, null ,files);
        return JSONObject.parseObject(rs);
    }

    @Override
    public JSONObject ocrCommImgUrl(String img_url) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cv/ocr/comm?img_url=%s&access_token=%s";
        api_url = String.format(api_url ,img_url, access_token);

        String rs =  restTemplateUtil.get(api_url);
        return JSONObject.parseObject(rs);
    }

    @Override
    public JSONObject ocrCommImg(String imgFileName) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cv/ocr/comm?access_token=%s";
        api_url = String.format(api_url ,access_token);
        Map<String , String > files =new HashMap<>();
        files.put("img" , imgFileName);

        String rs =  restTemplateUtil.postForm(api_url, null, null ,files);
        return JSONObject.parseObject(rs);
    }

    @Override
    public Map<String,Object> ocrPlatenumImgUrl(String img_url) {
        Map<String,Object> result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cv/ocr/platenum?img_url=%s&access_token=%s";
        api_url = String.format(api_url ,img_url, access_token);

        String rs =  restTemplateUtil.get(api_url);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null){
            result = ObjectUtil.JsonObject2Map(jsonObject);
        }
        return result;
    }

    @Override
    public Map<String,Object> ocrPlatenumImg(String imgFileName) {
        Map<String,Object> result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cv/ocr/platenum?access_token=%s";
        api_url = String.format(api_url ,access_token);
        Map<String , String > files =new HashMap<>();
        files.put("img" , imgFileName);

        String rs =  restTemplateUtil.postForm(api_url, null, null ,files);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null){
            result = ObjectUtil.JsonObject2Map(jsonObject);
        }
        return result;
    }


    @Override
    public JSONObject ocrMenuImgUrl(String img_url) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cv/ocr/menu?img_url=%s&access_token=%s";
        api_url = String.format(api_url ,img_url, access_token);

        String rs =  restTemplateUtil.get(api_url);
        return JSONObject.parseObject(rs);
    }

    @Override
    public JSONObject ocrMenuImg(String imgFileName) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cv/ocr/menu?access_token=%s";
        api_url = String.format(api_url ,access_token);
        Map<String , String > files =new HashMap<>();
        files.put("img" , imgFileName);

        String rs =  restTemplateUtil.postForm(api_url, null, null ,files);
        return JSONObject.parseObject(rs);
    }

    @Override
    public JSONObject cvImageQrcodeImgUrl(String img_url) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cv/img/qrcode?img_url=%s&access_token=%s";
        api_url = String.format(api_url ,img_url, access_token);

        String rs =  restTemplateUtil.get(api_url);
        return JSONObject.parseObject(rs);
    }

    @Override
    public JSONObject cvImageQrcodeImg(String imgFileName) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cv/img/qrcode?access_token=%s";
        api_url = String.format(api_url ,access_token);
        Map<String , String > files =new HashMap<>();
        files.put("img" , imgFileName);

        String rs =  restTemplateUtil.postForm(api_url, null, null ,files);
        return JSONObject.parseObject(rs);
    }

    @Override
    public Map<String,Object> cvImageSuperresolutionImgUrl(String img_url) {
        Map<String,Object> result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cv/img/superresolution?img_url=%s&access_token=%s";
        api_url = String.format(api_url ,img_url, access_token);

        String rs =  restTemplateUtil.get(api_url);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null){
            result = ObjectUtil.JsonObject2Map(jsonObject);
        }
        return result;
    }

    @Override
    public Map<String,Object> cvImageSuperresolutionImg(String imgFileName) {
        Map<String,Object> result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cv/img/superresolution?access_token=%s";
        api_url = String.format(api_url ,access_token);
        Map<String , String > files =new HashMap<>();
        files.put("img" , imgFileName);

        String rs =  restTemplateUtil.postForm(api_url, null, null ,files);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null){
            result = ObjectUtil.JsonObject2Map(jsonObject);
        }
        return result;
    }


    @Override
    public JSONObject cvImageAicropImgUrl(String img_url, String ratios) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cv/img/aicrop?img_url=%s&access_token=%s";
        api_url = String.format(api_url ,img_url, access_token);
        Map<String , Object > postForm =new HashMap<>();
        postForm.put("ratios" , ratios);

        String rs =  restTemplateUtil.postForm(api_url,postForm);
        return JSONObject.parseObject(rs);
    }

    @Override
    public JSONObject cvImageAicropImg(String imgFileName, String ratios) {
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEIXIN_API_URL + "/cv/img/aicrop?access_token=%s";
        api_url = String.format(api_url ,access_token);
        Map<String , String > postForm =new HashMap<>();
        postForm.put("ratios" , ratios);

        Map<String , String > files =new HashMap<>();
        files.put("img" , imgFileName);

        String rs =  restTemplateUtil.postForm(api_url, null, null ,files);
        return JSONObject.parseObject(rs);
    }
}
