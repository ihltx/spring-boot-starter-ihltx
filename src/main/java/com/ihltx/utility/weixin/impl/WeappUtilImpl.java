package com.ihltx.utility.weixin.impl;

import com.ihltx.utility.httpclient.service.RestTemplateUtil;
import com.ihltx.utility.httpclient.service.impl.RestTemplateUtilImpl;
import com.ihltx.utility.redis.exception.RedisException;
import com.ihltx.utility.redis.service.RedisFactory;
import com.ihltx.utility.redis.service.RedisUtil;
import com.ihltx.utility.util.ObjectUtil;
import com.ihltx.utility.util.SecurityUtil;
import com.ihltx.utility.util.StringUtil;
import com.ihltx.utility.weixin.WeappUtil;
import com.ihltx.utility.weixin.entity.PageDataList;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.context.ApplicationContext;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WeixinUtil
 * WeixinUtil utility class
 * @author liulin 84611913@qq.com
 *
 */
public class WeappUtilImpl implements WeappUtil {
    private static final String ACCESSTOKEN_PREFIX = "weixin:accesstoken:";
    private static final String WEAPP_CHARSET = "ISO-8859-1";


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

    private String accessTokenProxyUrl;
    @Override
    public void setAccessTokenProxyUrl(String proxyUrl) {
        this.accessTokenProxyUrl = proxyUrl;
    }

    @Override
    public String getAccessTokenProxyUrl() {
        return this.accessTokenProxyUrl;
    }

    public WeappUtilImpl(){
        init();
    }

    public WeappUtilImpl(String appID , String appSecret){
        this.appID = appID;
        this.appSecret = appSecret;
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


    @Override
    public Map<String, Object> login(String code) {
        String api_url = WEAPP_API_URL + "/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";
        api_url = String.format(api_url ,this.getAppID(), this.getAppSecret(),code);

        String rs =  restTemplateUtil.get(api_url);
        JSONObject jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null){
            return ObjectUtil.JsonObject2Map(jsonObject);
        }
        return null;
    }



    @Override
    public JSONObject getAccessToken() {
        return getAccessToken(null);
    }

    @Override
    public JSONObject getAccessToken(String proxy_api_url) {
        String key = ACCESSTOKEN_PREFIX + appID;
        String api_url = WEAPP_API_URL + "/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
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
    public Map<String, Object> checkEncryptedMsg(String encrypted_msg_hash) {
        String api_url = WEAPP_API_URL + "/wxa/business/checkencryptedmsg?access_token=%s";

        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        api_url = String.format(api_url ,access_token);
        Map<String , String > postForm =new HashMap<>();
        postForm.put("encrypted_msg_hash" , encrypted_msg_hash);

        String rs =  restTemplateUtil.postForObject(api_url, postForm, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null){
            return ObjectUtil.JsonObject2Map(jsonObject);
        }
        return null;
    }

    @Override
    public String getPaidUnionid(String openid, String mch_id, String out_trade_no) {
        String result =null;
        String api_url = WEAPP_API_URL + "/wxa/getpaidunionid?access_token=%s&openid=%s&mch_id=%s&out_trade_no=%s";

        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        api_url = String.format(api_url ,access_token, openid, mch_id, out_trade_no);


        String rs =  restTemplateUtil.get(api_url);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("unionid")){
            result = jsonObject.getString("unionid");
        }
        return result;
    }


    @Override
    public Map<String, Object> getTempMedia(String media_id) {
        String api_url = WEAPP_API_URL + "/cgi-bin/media/get?access_token=%s&media_id=%s";

        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        api_url = String.format(api_url ,access_token, media_id);


        String rs =  restTemplateUtil.get(api_url);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null){
            return ObjectUtil.JsonObject2Map(jsonObject);
        }
        return null;
    }


    @Override
    public Boolean sendText(String touser, String content) {
        Boolean result = false;
        String api_url = WEAPP_API_URL + "/cgi-bin/message/custom/send?access_token=%s";

        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        api_url = String.format(api_url ,access_token);

        Map<String , Object > postForm =new HashMap<>();
        postForm.put("touser" , touser);
        postForm.put("msgtype" , "text");
        Map<String , Object > text =new HashMap<>();
        text.put("content" , content);
        postForm.put("text" , text);

        String rs =  restTemplateUtil.postForObject(api_url, postForm, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }

    @Override
    public Boolean sendImage(String touser, String media_id) {
        Boolean result = false;
        String api_url = WEAPP_API_URL + "/cgi-bin/message/custom/send?access_token=%s";

        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        api_url = String.format(api_url ,access_token);

        Map<String , Object > postForm =new HashMap<>();
        postForm.put("touser" , touser);
        postForm.put("msgtype" , "image");
        Map<String , Object > image =new HashMap<>();
        image.put("media_id" , media_id);
        postForm.put("image" , image);

        String rs =  restTemplateUtil.postForObject(api_url, postForm, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }

    @Override
    public Boolean sendLink(String touser, String title, String description, String url, String thumb_url) {
        Boolean result = false;
        String api_url = WEAPP_API_URL + "/cgi-bin/message/custom/send?access_token=%s";

        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        api_url = String.format(api_url ,access_token);

        Map<String , Object > postForm =new HashMap<>();
        postForm.put("touser" , touser);
        postForm.put("msgtype" , "link");
        Map<String , Object > link =new HashMap<>();
        link.put("title" , title);
        link.put("description" , description);
        link.put("url" , url);
        link.put("thumb_url" , thumb_url);
        postForm.put("link" , link);

        String rs =  restTemplateUtil.postForObject(api_url, postForm, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }

    @Override
    public Boolean sendMiniProgramPage(String touser, String title, String pagepath, String thumb_media_id) {
        Boolean result = false;
        String api_url = WEAPP_API_URL + "/cgi-bin/message/custom/send?access_token=%s";

        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        api_url = String.format(api_url ,access_token);

        Map<String , Object > postForm =new HashMap<>();
        postForm.put("touser" , touser);
        postForm.put("msgtype" , "miniprogrampage");
        Map<String , Object > miniprogrampage =new HashMap<>();
        miniprogrampage.put("title" , title);
        miniprogrampage.put("pagepath" , pagepath);
        miniprogrampage.put("thumb_media_id" , thumb_media_id);
        postForm.put("miniprogrampage" , miniprogrampage);

        String rs =  restTemplateUtil.postForObject(api_url, postForm, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }

    @Override
    public Boolean setTyping(String touser, String command) {
        Boolean result = false;
        String api_url = WEAPP_API_URL + "/cgi-bin/message/custom/typing?access_token=%s";

        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        api_url = String.format(api_url ,access_token);

        Map<String , Object > postForm =new HashMap<>();
        postForm.put("touser" , touser);
        postForm.put("command" , "command");

        String rs =  restTemplateUtil.postForObject(api_url, postForm, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }


    @Override
    public String uploadTempMedia(String type, String mediaFileName) {
        String result = null;
        String api_url = WEAPP_API_URL + "/cgi-bin/media/upload?access_token=%s&type=%s";

        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        api_url = String.format(api_url ,access_token , type);

        Map<String , String > files =new HashMap<>();
        files.put("media" , mediaFileName);

        String rs =  restTemplateUtil.postForm(api_url,null,null,files);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("media_id")){
            result = jsonObject.getString("media_id");
        }
        return result;
    }


    @Override
    public Boolean sendTemplateMessage(String touser, JSONObject mp_template_msg) {
        Boolean result = false;
        String api_url = WEAPP_API_URL + "/cgi-bin/message/wxopen/template/uniform_send?access_token=%s";

        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        api_url = String.format(api_url ,access_token);

        Map<String , Object > postForm =new HashMap<>();
        postForm.put("touser" , touser);
        postForm.put("mp_template_msg" , mp_template_msg);

        String rs =  restTemplateUtil.postForObject(api_url, postForm, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }


    @Override
    public Map<String, Object> createQRCode(String path, int width) {
        String api_url = WEAPP_API_URL + "/cgi-bin/wxaapp/createwxaqrcode?access_token=%s";

        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        api_url = String.format(api_url ,access_token);
        Map<String , Object > postForm =new HashMap<>();
        postForm.put("path" , path);
        postForm.put("width" , width);

        String rs =  restTemplateUtil.postForObject(api_url, postForm, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null){
            return ObjectUtil.JsonObject2Map(jsonObject);
        }
        return null;
    }

    @Override
    public Map<String,Object> getQRCode(String path, int width, Boolean auto_color, String line_color, Boolean is_hyaline) {
        String api_url = WEAPP_API_URL + "/wxa/getwxacode?access_token=%s";

        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        api_url = String.format(api_url ,access_token);
        Map<String , Object > postForm =new HashMap<>();
        postForm.put("path" , path);
        postForm.put("width" , width);
        postForm.put("auto_color" , auto_color);
        postForm.put("line_color" , line_color);
        postForm.put("is_hyaline" , is_hyaline);

        String rs =  restTemplateUtil.postForObject(api_url, postForm, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null){
            return ObjectUtil.JsonObject2Map(jsonObject);
        }
        return null;
    }

    @Override
    public Map<String, Object> getUnlimitedQRCode(String scene, String path, int width, Boolean auto_color, String line_color, Boolean is_hyaline) {
        String api_url = WEAPP_API_URL + "/wxa/getwxacodeunlimit?access_token=%s";

        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        api_url = String.format(api_url ,access_token);
        Map<String , Object > postForm =new HashMap<>();
        postForm.put("scene" , scene);
        postForm.put("path" , path);
        postForm.put("width" , width);
        postForm.put("auto_color" , auto_color);
        postForm.put("line_color" , line_color);
        postForm.put("is_hyaline" , is_hyaline);

        String rs =  restTemplateUtil.postForObject(api_url, postForm, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null){
            return ObjectUtil.JsonObject2Map(jsonObject);
        }
        return null;
    }

    @Override
    public Map<String, Object> generateScheme(Map<String,String> jump_wxa, Boolean is_expire, int expire_type, int expire_time, int expire_interval) {
        String api_url = WEAPP_API_URL + "/wxa/generatescheme?access_token=%s";

        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        api_url = String.format(api_url ,access_token);
        Map<String , Object > postForm =new HashMap<>();
        postForm.put("jump_wxa" , jump_wxa);
        postForm.put("is_expire" , is_expire);
        postForm.put("expire_type" , expire_type);
        postForm.put("expire_time" , expire_time);
        postForm.put("expire_interval" , expire_interval);

        String rs =  restTemplateUtil.postForObject(api_url, postForm, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null){
            return ObjectUtil.JsonObject2Map(jsonObject);
        }
        return null;
    }


    @Override
    public Map<String, Object> generateUrlLink(String path, String query, String env_version, Boolean is_expire, int expire_type, int expire_time, int expire_interval, Map<String, String> cloud_base) {
        String api_url = WEAPP_API_URL + "/wxa/generate_urllink?access_token=%s";

        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        api_url = String.format(api_url ,access_token);
        Map<String , Object > postForm =new HashMap<>();
        postForm.put("path" , path);
        postForm.put("query" , query);
        postForm.put("env_version" , env_version);
        postForm.put("is_expire" , is_expire);
        postForm.put("expire_type" , expire_type);
        postForm.put("expire_time" , expire_time);
        postForm.put("expire_interval" , expire_interval);
        if(cloud_base!=null){
            postForm.put("cloud_base" , cloud_base);
        }

        String rs =  restTemplateUtil.postForObject(api_url, postForm, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null){
            return ObjectUtil.JsonObject2Map(jsonObject);
        }
        return null;
    }


    @Override
    public Boolean imgSecCheck(String mediaFileName) {
        Boolean result = false;
        String api_url = WEAPP_API_URL + "/wxa/img_sec_check?access_token=%s";

        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        api_url = String.format(api_url ,access_token);

        Map<String , String > files =new HashMap<>();
        files.put("media" , mediaFileName);

        String rs =  restTemplateUtil.postForm(api_url,null,null,files);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result = true;
        }
        return result;
    }

    @Override
    public String mediaCheckAsync(String media_url, int media_type, int version, String openid, int scene) {
        String result = null;
        String api_url = WEAPP_API_URL + "/wxa/media_check_async?access_token=%s";

        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        api_url = String.format(api_url ,access_token);

        Map<String , Object > postData =new HashMap<>();
        postData.put("media_url" , media_url);
        postData.put("media_type" , media_type);
        postData.put("version" , version);
        postData.put("openid" , openid);
        postData.put("scene" , scene);

        String rs =  restTemplateUtil.postForObject(api_url , postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("trace_id")){
            result = jsonObject.getString("trace_id");
        }
        return result;
    }


    @Override
    public JSONObject msgSecCheck(int version, String openid, int scene, String content, String nickname, String title, String signature) {
        String api_url = WEAPP_API_URL + "/wxa/msg_sec_check?access_token=%s";

        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        api_url = String.format(api_url ,access_token);

        Map<String , Object > postData =new HashMap<>();
        postData.put("version" , version);
        postData.put("openid" , openid);
        postData.put("scene" , scene);
        postData.put("content" , content);
        postData.put("nickname" , nickname);
        postData.put("title" , title);
        postData.put("signature" , signature);

        String rs =  restTemplateUtil.postForObject(api_url,postData,String.class);
        return JSONObject.parseObject(rs);
    }


    @Override
    public String getAuthenticationUrl(String openid, String ctoken) {
        String result = null;
        String api_url = WEAPP_API_URL + "/redpacketcover/wxapp/cover_url/get_by_token?access_token=%s";

        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        api_url = String.format(api_url ,access_token);

        Map<String , Object > postData =new HashMap<>();
        postData.put("openid" , openid);
        postData.put("ctoken" , ctoken);

        String rs =  restTemplateUtil.postForObject(api_url , postData , String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("data") && jsonObject.getJSONObject("data").containsKey("url")){
            result = jsonObject.getJSONObject("data").getString("url");
        }
        return result;
    }


    @Override
    public JSONObject scanQRCode(String imgFileName) {
        String api_url = WEAPP_API_URL + "/cv/img/qrcode?access_token=%s";

        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        api_url = String.format(api_url ,access_token);

        Map<String , String > files =new HashMap<>();
        files.put("img" , imgFileName);

        String rs =  restTemplateUtil.postForm(api_url,null,null,files);
        return JSONObject.parseObject(rs);
    }

    @Override
    public JSONObject scanQRCodeUrl(String imgUrl) throws UnsupportedEncodingException {
        String api_url = WEAPP_API_URL + "/cv/img/qrcode?access_token=%s&img_url=%s";

        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        api_url = String.format(api_url ,access_token , SecurityUtil.urlEncode(imgUrl));

        String rs =  restTemplateUtil.postForObject(api_url,null,String.class);
        return JSONObject.parseObject(rs);
    }


    @Override
    public String addNewtmplTemplate(String tid, String[] kidList, String sceneDesc) {
        String result = null;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEAPP_API_URL + "/wxaapi/newtmpl/addtemplate?access_token=%s";
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
        String api_url = WEAPP_API_URL + "/wxaapi/newtmpl/deltemplate?access_token=%s";
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
        String api_url = WEAPP_API_URL + "/wxaapi/newtmpl/getcategory?access_token=%s";
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
        String api_url = WEAPP_API_URL + "/wxaapi/newtmpl/getcategory?access_token=%s&tid=%s";
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
        String api_url = WEAPP_API_URL + "/wxaapi/newtmpl/getpubtemplatetitles?access_token=%s&ids=%s&start=%d&limit=%d";
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
        String api_url = WEAPP_API_URL + "/wxaapi/newtmpl/gettemplate?access_token=%s";
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
    public Boolean sendNewtmplSubscribe(String touser, String template_id, String page ,Map<String , Map<String,Object>> data, String miniprogram_state, String lang) {
        Boolean result = false;
        JSONObject jsonObject = getAccessToken(this.getAccessTokenProxyUrl());
        String access_token = "";
        if(jsonObject!=null && jsonObject.containsKey("access_token")){
            access_token = jsonObject.getString("access_token");
        }
        String api_url = WEAPP_API_URL + "/cgi-bin/message/subscribe/send?access_token=%s";
        api_url = String.format(api_url , access_token);
        Map<String , Object> postData = new HashMap<>();
        postData.put("touser" , touser);
        postData.put("template_id" , template_id);
        postData.put("page" , page);
        postData.put("data" , data);
        postData.put("miniprogram_state" , miniprogram_state);
        postData.put("lang" , lang);

        String rs =  restTemplateUtil.postForObject(api_url, postData, String.class);
        jsonObject = JSONObject.parseObject(rs);
        if(jsonObject!=null && jsonObject.containsKey("errcode") && jsonObject.getString("errcode").equals("0")){
            result =true;
        }
        return result;
    }


}
