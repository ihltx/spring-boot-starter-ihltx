package com.ihltx.utility.weixin;

import com.ihltx.utility.httpclient.service.RestTemplateUtil;
import com.ihltx.utility.redis.service.RedisFactory;
import com.ihltx.utility.weixin.entity.*;
import com.ihltx.utility.weixin.event.MessageEvent;
import com.alibaba.fastjson.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * WeixinUtil
 * WeixinUtil utility class
 * @author liulin 84611913@qq.com
 *
 */
public interface WeixinUtil {
    /**
     * 微信API接口URL
     */
    public static final String WEIXIN_API_URL = "https://api.weixin.qq.com";

    /**
     * 微信 MP API接口URL
     */
    public static final String WEIXIN_MP_API_URL = "https://mp.weixin.qq.com";

    void setApplicationContext(ApplicationContext applicationContext);

    ApplicationContext getApplicationContext();

    RestTemplateUtil getRestTemplateUtil();

    void setRestTemplateUtil(RestTemplateUtil restTemplateUtil);


    /**
     * 设置RedisFactory工厂
     * @param redisFactory      RedisFactory工厂
     */
    void setRedisFactory(RedisFactory redisFactory);


    /**
     * 返回RedisFactory工厂
     * @return  RedisFactory
     */
    RedisFactory getRedisFactory();


    /**
     * 设置AccessToken令牌Redis存储名称，此名称必须要配置文件中的 ihltx.redis.server中进行配置
     * @param redisName      Redis存储名称
     */
    void setAccessTokenRedisStorageName(String redisName);


    /**
     * 返回AccessToken令牌Redis存储名称
     * @return  String
     */
    String getAccessTokenRedisStorageName();


    /**
     * 设置是否启用AccessToken令牌Redis存储
     * @param enable      true|false
     */
    void setEnableAccessTokenRedisStorage(Boolean enable);

    /**
     * 返回AccessToken令牌Redis存储启用状态
     * @return  Boolean
     */
    Boolean getEnableAccessTokenRedisStorage();


    /**
     * 获取当前微信公众号 AppID
     * @return AppID
     */
    String getAppID();

    /**
     * 设置当前微信公众号 AppID
     *
     * @param appID     微信公众号 AppID
     */
    void setAppID(String appID);


    /**
     * 获取当前微信公众号 appSecret
     * @return appSecret
     */
    String getAppSecret();

    /**
     * 设置当前微信公众号 appSecret
     *
     * @param appSecret     微信公众号 appSecret
     */
    void setAppSecret(String appSecret);

    /**
     * 获取当前微信公众号 token
     * @return token
     */
    String getToken();

    /**
     * 设置当前微信公众号 token
     *
     * @param token     微信公众号 token
     */
    void setToken(String token);

    /**
     * 获取当前微信公众号 消息加解密密钥 encodingAESKey
     * @return encodingAESKey
     */
    String getEncodingAESKey();

    /**
     * 设置当前微信公众号 消息加解密密钥 encodingAESKey
     *
     * @param encodingAESKey     微信公众号 消息加解密密钥 encodingAESKey
     */
    void setEncodingAESKey(String encodingAESKey);

    /**
     * 获取当前微信公众号 消息加解密方式 encodingAESType
     * 消息加解密方式：
     *       0--明文模式           明文模式下，不使用消息体加解密功能，安全系数较低
     *       1--兼容模式           兼容模式下，明文、密文将共存，方便开发者调试和维护
     *       2--安全模式（推荐）     安全模式下，消息包为纯密文，需要开发者加密和解密，安全系数高     * @return encodingAESType
     */
    Integer getEcodingAESType();


    /**
     * 设置当前微信公众号 消息加解密方式 encodingAESType
     * 消息加解密方式：
     *       0--明文模式           明文模式下，不使用消息体加解密功能，安全系数较低
     *       1--兼容模式           兼容模式下，明文、密文将共存，方便开发者调试和维护
     *       2--安全模式（推荐）     安全模式下，消息包为纯密文，需要开发者加密和解密，安全系数高     * @return encodingAESType
     *
     * @param encodingAESType     微信公众号 消息加解密方式 encodingAESType
     */
    void setEcodingAESType(Integer encodingAESType);

    /**
     * 设置AccessToken获取代理ProxyUrl
     * proxyUrl只能接收一个名为url的参数，并通过http get请求将url结果直接返回
     * proxyUrl接收到的url参数必须通过urlDecode(utf-8)进行解码
     *
     * @param proxyUrl      代理URL
     */
    void setAccessTokenProxyUrl(String proxyUrl);


    /**
     * 返回AccessToken获取代理ProxyUrl
     * @return  String
     */
    String getAccessTokenProxyUrl();


    /**
     * 设置消息事件列表
     * @param messageEvents  消息事件列表
     */
    void setMessageEvents(List<MessageEvent> messageEvents);

    /**
     * 获取消息事件列表
     * @return  List<MessageEvent>
     */
    List<MessageEvent> getMessageEvents();

    /**
     * 向用户发送文本消息
     * @param requestBody       包含用户信息的请求体
     * @param message           文本消息
     * @return String xml
     */
    String sendTextMsg(Map<String, String> requestBody, String message);

    /**
     * 向用户发送图片消息
     * @param requestBody        包含发送用户openid及接收微信公众号信息
     * @param media_id          图片素材媒体ID
     * @return String xml
     */
    String sendImageMsg(Map<String, String> requestBody, String media_id);

    /**
     * 向用户发送语音消息
     * @param requestBody        包含发送用户openid及接收微信公众号信息
     * @param media_id          语音素材媒体ID
     * @return  String xml
     */
    String sendVoiceMsg(Map<String, String> requestBody, String media_id);


    /**
     * 向用户发送视频消息
     * @param requestBody        包含发送用户openid及接收微信公众号信息
     * @param media_id          视频素材媒体ID
     * @param title             视频标题
     * @param description       视频描述
     * @return String xml
     */
    String sendVideoMsg(Map<String, String> requestBody, String media_id, String title, String description);

    /**
     * 向用户发送音乐消息
     * @param requestBody            包含发送用户openid及接收微信公众号信息
     * @param title                 音乐标题
     * @param description           音乐描述
     * @param musicUrl              音乐url
     * @param hQMusicUrl            高质量音乐链接，WIFI环境优先使用该链接播放音乐
     * @param thumbMediaId          缩略图的媒体id，通过素材管理中的接口上传多媒体文件，得到的id
     * @return String xml
     */
    String sendMusicMsg(Map<String, String> requestBody, String title, String description, String musicUrl, String hQMusicUrl, String thumbMediaId);


    /**
     * 向用户发送图文消息
     * @param requestBody       包含用户信息的请求体
     * @param items             图文列表
     * @return String xml
     */
    String sendArticleMsg(Map<String, String> requestBody, List<ArticleItem> items);

    /**
     * 回复消息转发到微信客服系统
     * @param requestBody       包含用户信息的请求体
     * @param KfAccount         指定会话接入的客服账号
     * @return String xml
     */
    String sendTransferCustomerService(Map<String, String> requestBody, String KfAccount);



    /**
     * 获取AccessToken，如果通过setRedisFactory传递了RedisFactory，并且通过setAccessTokenRedisStorageName设置了redisName名称，
     * 且通过setEnableAccessTokenRedisStorage启用了AccessTokenRedisStorage则在AccessToken有效期内将通过Redis保存相应的AccessToken
     * @return JSONObject
     *      {"access_token":"ACCESS_TOKEN","expires_in":7200}   --  true
     *      {"errcode":40013,"errmsg":"invalid appid"}          --  false
     */
    JSONObject getAccessToken();

    /**
     * 基于代理url(proxy_url)获取AccessToken,proxy_url只能接收一个名为url的参数，并通过http get请求将结果直接返回给getAccessToken
     * proxy_url接收到的url参数必须通过urlDecode(utf-8)进行解码
     * 如果通过setRedisFactory传递了RedisFactory，并且通过setAccessTokenRedisStorageName设置了redisName名称，
     * 且通过setEnableAccessTokenRedisStorage启用了AccessTokenRedisStorage则在AccessToken有效期内将通过Redis保存相应的AccessToken
     * @param proxy_url         proxy_url
     * @return JSONObject
     *      {"access_token":"ACCESS_TOKEN","expires_in":7200}   --  true
     *      {"errcode":40013,"errmsg":"invalid appid"}          --  false
     */

    JSONObject getAccessToken(String proxy_url);


    /**
     * 获取微信服务器ip地址列表
     * @return List<String>
     */
    List<String> getApiDomainIp();



    /**
     * 创建微信菜单
     * @param menu                menu
     * @return Boolean
     */
    Boolean createMenu(Menu menu);


    /**
     * 删除微信菜单
     * @return Boolean
     */
    Boolean deleteMenu();

    /**
     * 查询微信菜单
     * @return Menu
     */
    Menu getMenu();


    /**
     * 创建微信个性化菜单
     * @param menu                menu
     * @return String   menuid -- 成功   null  -- 失败
     */
    String createConditionalMenu(ConditionalMenu menu);

    /**
     * 删除微信个性化菜单
     * @param menuid                menuid
     * @return Boolean
     */
    Boolean deleteConditionalMenu(String menuid);

    /**
     * 测试个性化菜单匹配结果
     * @param user_id               user_id可以是粉丝的OpenID，也可以是粉丝的微信号。
     * @return List<Button>  返回某用户的个性化菜单
     */
    List<Button> getConditionalMenu(String user_id);


    /**
     * 返回通用菜单及个性化菜单
     * @return  Menu  返回通用菜单及个性化菜单   null -- 失败
     */
    Menu getConditionalMenu();

    /**
     * 获取微信用户信息
     * @param openid              微信用户openid
     * @return  Map<String, Object>
     *     {
     *     "subscribe": 1,
     *     "openid": "o6_bmjrPTlm6_2sgVt7hMZOPfL2M",
     *     "nickname": "Band",
     *     "sex": 1,
     *     "language": "zh_CN",
     *     "city": "广州",
     *     "province": "广东",
     *     "country": "中国",
     *     "headimgurl":"http://thirdwx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0",
     *     "subscribe_time": 1382694957,
     *     "unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL",
     *     "remark": "",
     *     "groupid": 0,
     *     "tagid_list":[128,2],
     *     "subscribe_scene": "ADD_SCENE_QR_CODE",
     *     "qr_scene": 98765,
     *     "qr_scene_str": ""
     * }
     */
    Map<String, Object> getUserInfo(String openid);


    /**
     * 微信公众号消息服务中心，主要用于处理微信公众号推送的消息，并回送相关消息
     * @param request           request
     * @return String  application/json
     */
    String messageServiceCenter(HttpServletRequest request);

    /**
     * 验证签名
     * @param request
     * @return  true  -- 验签成功  false   -- 验签失败
     */
    Boolean checkSign(HttpServletRequest request);

    /**
     * 验证签名
     * @param signature
     * @param timestamp
     * @param nonce
     * @return true  -- 验签成功  false   -- 验签失败
     */

    Boolean checkSign(String signature, String timestamp, String nonce);


    /**
     * 获取素材总数
     *
     * @return  Map<String , Integer>
     *     {
     *          "voice_count":COUNT,
     *          "video_count":COUNT,
     *          "image_count":COUNT,
     *          "news_count":COUNT
     *      }
     */
    Map<String , Integer> getMaterialCount();

    /**
     * 获取图文素材列表，仅返回单图文，不返回多图文
     *
     * @param offset        偏移，从0开始
     * @param count         每次获取的素材总数，最多不能超过20
     *
     * @return List<NewsMaterial>
     *      [
     *        {
     *             "media_id": "media_id",
     *             "create_time": "create_time",
     *             "update_time": "update_time",
     *             "news_item" :
     *             [
     *               {
     *                  "title": "标题",
     *                  "thumb_media_id": "图文消息的封面图片素材id（必须是永久mediaID）",
     *                  "thumb_url": "图文消息的封面图片url",
     *                  "author": "作者",
     *                  "digest": "图文消息的摘要，仅有单图文消息才有摘要，多图文此处为空。如果本字段为没有填写，则默认抓取正文前54个字。",
     *                  "show_cover_pic": 0|1,  //是否显示封面，0为false，即不显示，1为true，即显示
     *                  "content": "图文消息的具体内容，支持HTML标签，必须少于2万字符，小于1M，且此处会去除JS,涉及图片url必须来源 "上传图文消息内的图片获取URL"接口获取。外部图片url将被过滤。",
     *                  "content_source_url": "图文消息的原文地址，即点击“阅读原文”后的URL",
     *                  "need_open_comment": 0|1,  //Uint32 是否打开评论，0不打开，1打开
     *                  "only_fans_can_comment": 0|1,  //Uint32 是否粉丝才可评论，0所有人可评论，1粉丝才可评论
     *                  "url": "图文素材详情url"
     *               },...
     *            ]
     *         },
     *         ...
     *      ]
     */
    List<NewsMaterial> getArticleItems(int offset, int count);


    /**
     * 获取图片素材列表
     *
     * @param offset        偏移，从0开始
     * @param count         每次获取的素材总数，最多不能超过20
     *
     * @return List<Map<String , Object>>
     *      [
     *        {
     *             "media_id": "media_id",
     *             "update_time": "update_time",
     *             "name": "名称",
     *             "url": "图片url",
     *             "tags" "[]"
     *         },
     *         ...
     *      ]
     */
    List<Map<String , Object>> getImageItems(int offset, int count);


    /**
     * 获取视频素材列表
     *
     * @param offset        偏移，从0开始
     * @param count         每次获取的素材总数，最多不能超过20
     *
     * @return List<Map<String , Object>>
     *      [
     *        {
     *             "media_id": "media_id",
     *             "update_time": "update_time",
     *             "name": "名称",
     *             "description": "视频描述",
     *             "tags" "[]"
     *         },
     *         ...
     *      ]
     */
    List<Map<String , Object>> getVideoItems(int offset, int count);


    /**
     * 获取语音素材列表
     *
     * @param offset        偏移，从0开始
     * @param count         每次获取的素材总数，最多不能超过20
     *
     * @return List<Map<String , Object>>
     *      [
     *        {
     *             "media_id": "media_id",
     *             "update_time": "update_time",
     *             "name": "名称",
     *             "tags" "[]"
     *         },
     *         ...
     *      ]
     */
    List<Map<String , Object>> getVoiceItems(int offset, int count);


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
     *             "content": "图文消息的具体内容，支持HTML标签，必须少于2万字符，小于1M，且此处会去除JS,涉及图片url必须来源 "上传图文消息内的图片获取URL"接口获取。外部图片url将被过滤。",
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
    String addArticleItem(List<ArticleItem> articles, String contentImageDownLoadPath, String contentImageUrlPrefix);

    /**
     * 上传图文素材
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
     *             "content": "图文消息的具体内容，支持HTML标签，必须少于2万字符，小于1M，且此处会去除JS,涉及图片url必须来源 "上传图文消息内的图片获取URL"接口获取。外部图片url将被过滤。",
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
    String uploadNews(List<ArticleItem> articles, String contentImageDownLoadPath, String contentImageUrlPrefix);


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
     *             "content": "图文消息的具体内容，支持HTML标签，必须少于2万字符，小于1M，且此处会去除JS,涉及图片url必须来源 "上传图文消息内的图片获取URL"接口获取。外部图片url将被过滤。",
     *             "content_source_url": "图文消息的原文地址，即点击“阅读原文”后的URL",
     *             "need_open_comment": 0|1,  //Uint32 是否打开评论，0不打开，1打开
     *             "only_fans_can_comment": 0|1  //Uint32 是否粉丝才可评论，0所有人可评论，1粉丝才可评论
     *         }
     * @param contentImageDownLoadPath        图文内容中图片(仅支持.png或.jpg)下载目标文件夹完整物理路径名，
     *                                        将针对内容中src="*.jpg|*.png"及url("*.jpg|*.png")中图片下载到contentImageDownLoadPath服务器本地文件夹，
     *                                        然后再上传到微信服务器获取微信服务器图片url之后完成替换，完成处理之后，下载到contentImageDownLoadPath本地文件夹中的相应图片文件将自动删除
     * @param contentImageUrlPrefix           内容中.png或.jpg如果未使用http://或https://开头的url时，图片的完整前缀url，格式如： https://www.abcd.com/abcd
     *
     * @return String  false -- 失败  true -- 成功
     */
    Boolean updateArticleItem(String media_id, int index, ArticleItem article, String contentImageDownLoadPath, String contentImageUrlPrefix);

    /**
     * 获取 永久图文素材
     * @param media_id          永久图文素材的 media_id
     * @return NewsMaterial  null  -- 失败
     *        {
     *             "media_id": "media_id",
     *             "create_time": "create_time",
     *             "update_time": "update_time",
     *             "news_item" :
     *             [
     *               {
     *                  "title": "标题",
     *                  "thumb_media_id": "图文消息的封面图片素材id（必须是永久mediaID）",
     *                  "thumb_url": "图文消息的封面图片url",
     *                  "author": "作者",
     *                  "digest": "图文消息的摘要，仅有单图文消息才有摘要，多图文此处为空。如果本字段为没有填写，则默认抓取正文前54个字。",
     *                  "show_cover_pic": 0|1,  //是否显示封面，0为false，即不显示，1为true，即显示
     *                  "content": "图文消息的具体内容，支持HTML标签，必须少于2万字符，小于1M，且此处会去除JS,涉及图片url必须来源 "上传图文消息内的图片获取URL"接口获取。外部图片url将被过滤。",
     *                  "content_source_url": "图文消息的原文地址，即点击“阅读原文”后的URL",
     *                  "need_open_comment": 0|1,  //Uint32 是否打开评论，0不打开，1打开
     *                  "only_fans_can_comment": 0|1,  //Uint32 是否粉丝才可评论，0所有人可评论，1粉丝才可评论
     *                  "url": "图文素材详情url"
     *               },...
     *            ]
     *         }
     */
    NewsMaterial getArticleItem(String media_id);

    /**
     * 新增永久图片素材
     * @param fileName       服务器端图片文件名
     * @return  String  null -- 添加失败  否则返回新增永久图片的 media_id
     */
    String addImage(String fileName);

    /**
     * 获取永久图片素材
     * @param media_id       永久图片的 media_id
     * @return  byte[]  null -- 失败  返回图片素材二进制内容
     */
    byte[] getImage(String media_id);

    /**
     * 新增永久语音素材
     * @param fileName       服务器端语音文件名
     * @return  String  null -- 添加失败  否则返回新增永久语音的 media_id
     */
    String addVoice(String fileName);

    /**
     * 获取永久语音素材
     * @param media_id       永久语音的 media_id
     * @return  byte[]  null -- 失败  返回语音素材二进制内容
     */
    byte[] getVoice(String media_id);

    /**
     * 新增永久视频素材
     * @param fileName       服务器端视频文件名
     * @param title          视频标题
     * @param introduction   视频简介
     * @return  String  null -- 添加失败  否则返回新增永久视频的 media_id
     */
    String addVideo(String fileName, String title, String introduction);

    /**
     * 获取永久视频素材
     * @param media_id       永久视频的 media_id
     * @return  Map<String,String>  null -- 失败
     * {
     *      "vid":"vid",
     *      "cover_url":"封面url",
     *      "down_url":"视频下载url",
     *      "newsubcat":"",
     *      "description":"描述",
     *      "title":"标题",
     *      "newcat":"",
     *      "tags":"[]"
     * }
     *
     */
    Map<String,Object> getVideo(String media_id);

    /**
     * 上传图文消息内的图片获取URL
     *
     * @param mediaFileName        服务器端图片文件名，仅支持jpg/png格式，大小必须在1MB以下
     * @return String  true  --  URL   null  --  失败
     */
    String uploadImg(String mediaFileName);

    /**
     * 处理图文素材中图片资源内容，图片资源仅处理 src="*.jpg"  src="*.png"      url("bgimage.jpg")  url("bgimage.png")格式的图片
     * 下载到本地之后再做为微信图片资源上传到微信服务并获取url进行替换
     * @param content               需要处理的图文素材内容
     * @param downLoadPath          图片下载位置，指定文件夹名称，下载的文件上传之后将自动删除
     * @param urlPrefix             图片使用相对或/开始或//开始时的绝对路径时的前缀URL地址
     * @return  String 处理之后的内容
     */
    String handleArticleContent(String content, String downLoadPath, String urlPrefix);

    /**
     * 删除永久素材
     * @param media_id          永久素材ID
     * @return   true  -- 成功  false -- 失败(不存在或可能被使用)
     */
    Boolean deleteMaterial(String media_id);

    /**
     * 新增临时素材
     * @param type          素材类型，图片（image）、语音（voice）、视频（video）和缩略图（thumb）
     * @param fileName      素材完整路径名
     * @return
     */
    String addTempMaterial(String type, String fileName);


    /**
     * 获取临时视频素材的二进制内容
     * @param media_id       临时视频素材的 media_id
     * @return  byte[]  null -- 失败  二进制内容

     *
     */
    byte[] getTempMaterial(String media_id);


    /**
     * 创建临时带整型参数二维码
     * @param expire_seconds        该二维码有效时间，以秒为单位。 最大不超过2592000（即30天），此字段如果不填，则默认有效期为60秒。
     * @param scene_id              场景值ID，临时二维码时为32位非0整型
     * @return  Map<String,Object>
     *     ticket           =>  获取的二维码ticket，凭借此ticket可以在有效时间内换取二维码。
     *     expire_seconds   =>  该二维码有效时间，以秒为单位。 最大不超过2592000（即30天）。0--表示不限制时间
     *     url              =>  二维码图片解析后的地址，开发者可根据该地址自行生成需要的二维码图片
     */
    Map<String,Object> createTempQrcode(int expire_seconds, int scene_id);

    /**
     * 创建临时带字符串型参数二维码
     * @param expire_seconds        该二维码有效时间，以秒为单位。 最大不超过2592000（即30天），此字段如果不填，则默认有效期为60秒。
     * @param scene_str              场景值ID（字符串形式的ID），字符串类型，长度限制为1到64
     * @return  Map<String,Object>
     *     ticket           =>  获取的二维码ticket，凭借此ticket可以在有效时间内换取二维码。
     *     expire_seconds   =>  该二维码有效时间，以秒为单位。 最大不超过2592000（即30天）。0--表示不限制时间
     *     url              =>  二维码图片解析后的地址，开发者可根据该地址自行生成需要的二维码图片
     */
    Map<String,Object> createTempQrcode(int expire_seconds, String scene_str);

    /**
     * 创建永久带整型参数二维码
     * @param scene_id              场景值ID，永久二维码时最大值为100000（目前参数只支持1--100000）
     * @return  Map<String,Object>
     *     ticket           =>  获取的二维码ticket，凭借此ticket可以在有效时间内换取二维码。
     *     expire_seconds   =>  该二维码有效时间，以秒为单位。 最大不超过2592000（即30天）。0--表示不限制时间
     *     url              =>  二维码图片解析后的地址，开发者可根据该地址自行生成需要的二维码图片
     */
    Map<String,Object> createQrcode(int scene_id);

    /**
     * 创建永久带字符串型参数二维码
     * @param scene_str              场景值ID（字符串形式的ID），字符串类型，长度限制为1到64
     * @return  Map<String,Object>
     *     ticket           =>  获取的二维码ticket，凭借此ticket可以在有效时间内换取二维码。
     *     expire_seconds   =>  该二维码有效时间，以秒为单位。 最大不超过2592000（即30天）。 0--表示不限制时间
     *     url              =>  二维码图片解析后的地址，开发者可根据该地址自行生成需要的二维码图片
     */
    Map<String,Object> createQrcode(String scene_str);

    /**
     * 根据 二维码ticket 获取二维码图片二进制内容
     * @param ticket             获取的二维码ticket
     * @return  byte[]
     */
    byte[] showQrcode(String ticket) throws UnsupportedEncodingException;

    /**
     * 重定向到微信二维码ticket接口页面用于呈现二维码图片
     * @param ticket             获取的二维码ticket
     * @param response           HttpServletResponse, 如果为null表示使用当前上下文中HttpServletResponse
     * @return  byte[]
     * @throws IOException
     */
   void redirectQrcode(String ticket, HttpServletResponse response) throws IOException;

    /**
     * 重定向到微信二维码ticket接口页面用于呈现二维码图片
     * @param ticket             获取的二维码ticket
     * @return  byte[]
     * @throws IOException
     */
    void redirectQrcode(String ticket) throws IOException;


    /**
     * 设置模板消息所属行业  每月可修改行业1次，帐号仅可使用所属行业中相关的模板
     * @param industry_id1      主行业ID，来自于com.ihltx.utility.weixin.tools.TemplateMessageIndustry中的行业ID
     * @param industry_id2      副行业ID，来自于com.ihltx.utility.weixin.tools.TemplateMessageIndustry中的行业ID
     * @return  true -- 成功   false -- 失败
     */
    Boolean setIndustry(Integer industry_id1, Integer industry_id2);

    /**
     * 获取模板消息所属行业
     * @return  Map<String , Map<String,String>>
     *     {
     *     "primary_industry":{"first_class":"运输与仓储","second_class":"快递"},
     *     "secondary_industry":{"first_class":"IT科技","second_class":"互联网|电子商务"}
     *    }
     */
    Map<String , Map<String,String>> getIndustry();

    /**
     * 根据模板编号添加私有消息模板并返回添加的模板ID
     * @param templateNo          模板编号，模板库中模板的编号，有“TM**”和“OPENTMTM**”等形式
     * @return  String 模板ID
     */
    String addPrivateTemplate(String templateNo);

    /**
     * 获取私有消息模板列表
     * @return List<Map<String , Object>>
     *     [{
     *       "template_id": "iPk5sOIt5X_flOVKn5GrTFpncEYTojx6ddbt8WYoV5s",
     *       "title": "领取奖金提醒",
     *       "primary_industry": "IT科技",
     *       "deputy_industry": "互联网|电子商务",
     *       "content": "{ {result.DATA} }\n\n领奖金额:{ {withdrawMoney.DATA} }\n领奖  时间:    { {withdrawTime.DATA} }\n银行信息:{ {cardInfo.DATA} }\n到账时间:  { {arrivedTime.DATA} }\n{ {remark.DATA} }",
     *       "example": "您已提交领奖申请\n\n领奖金额：xxxx元\n领奖时间：2013-10-10 12:22:22\n银行信息：xx银行(尾号xxxx)\n到账时间：预计xxxxxxx\n\n预计将于xxxx到达您的银行卡"
     *    }]
     */
    List<Map<String , Object>> getAllPrivateTemplate();


    /**
     * 删除私有消息模板
     * @param template_id           模板ID
     * @return Boolean  true -- 成功   false -- 失败
     */
    Boolean deletePrivateTemplate(String template_id);


    /**
     * 发送模板消息
     * @param template_id           模板ID
     * @return Boolean  true -- 成功   false -- 失败
     */
    /**
     * 发送模板消息
     * @param template_id           模板ID
     * @param touser                接收者openid
     * @param url                   模板跳转链接（海外帐号没有跳转能力）
     * @param miniprogram_appid     所需跳转到的小程序appid（该小程序appid必须与发模板消息的公众号是绑定关联关系，暂不支持小游戏）,不需跳小程序可不用传该数据
     * @param miniprogram_pagepath  所需跳转到小程序的具体页面路径，支持带参数,（示例index?foo=bar），要求该小程序已发布，暂不支持小游戏,不需跳小程序可不用传该数据
     * @param data                  模板数据
     *       {
     *          “模板变量名” :  TemplateMessageData,
     *          ...
     *        }
     * @return Boolean true -- 成功   false -- 失败
     */
    Boolean sendTemplateMessage(String template_id, String touser, String url, String miniprogram_appid, String miniprogram_pagepath, Map<String, TemplateMessageData> data);

    /**
     * 公众号一次性订阅消息，必须在微信端调用此方法，同时该方法将重定向到微信端订阅消息授权页面
     * @param scene                 重定向后会带上scene参数，开发者可以填0-10000的整型值，用来标识订阅场景值
     * @param template_id           订阅消息模板ID，登录公众平台后台，在接口权限列表处可查看订阅模板ID
     * @param redirect_url          授权后重定向的回调地址，请使用UrlEncode对链接进行处理。 注：要求redirect_url的域名要跟登记的业务域名一致，且业务域名不能带路径。 业务域名需登录公众号，在设置-公众号设置-功能设置里面对业务域名设置。
     * @param reserved              用于保持请求和回调的状态，授权请后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验，开发者可以填写a-zA-Z0-9的参数值，最多128字节，要求做urlencode
     * @param response              HttpServletResponse
     * @throws IOException
     */
    void subscribeMessage(int scene, String template_id, String redirect_url, String reserved, HttpServletResponse response) throws IOException;

    /**
     * 公众号一次性订阅消息，必须在微信端调用此方法，同时该方法将重定向到微信端订阅消息授权页面
     * @param scene                 重定向后会带上scene参数，开发者可以填0-10000的整型值，用来标识订阅场景值
     * @param template_id           订阅消息模板ID，登录公众平台后台，在接口权限列表处可查看订阅模板ID
     * @param redirect_url          授权后重定向的回调地址，请使用UrlEncode对链接进行处理。 注：要求redirect_url的域名要跟登记的业务域名一致，且业务域名不能带路径。 业务域名需登录公众号，在设置-公众号设置-功能设置里面对业务域名设置。
     * @param reserved              用于保持请求和回调的状态，授权请后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验，开发者可以填写a-zA-Z0-9的参数值，最多128字节，要求做urlencode
     * @throws IOException
     */
    void subscribeMessage(int scene, String template_id, String redirect_url, String reserved) throws IOException;

    /**
     * 发送公众号一次性订阅消息
     * @param openid                用户唯一标识，只在用户确认授权时才会带上
     * @param scene                 订阅场景值
     * @param template_id           订阅消息模板ID
     * @param action                用户点击动作，"confirm"代表用户确认授权，"cancel"代表用户取消授权
     * @param reserved              请求带入原样返回
     * @param title                 消息标题
     * @param url                   模板跳转链接（海外帐号没有跳转能力）
     * @param miniprogram_appid     所需跳转到的小程序appid（该小程序appid必须与发模板消息的公众号是绑定关联关系，暂不支持小游戏）,不需跳小程序可不用传该数据
     * @param miniprogram_pagepath  所需跳转到小程序的具体页面路径，支持带参数,（示例index?foo=bar），要求该小程序已发布，暂不支持小游戏,不需跳小程序可不用传该数据
     * @param data                  模板数据
     *       {
     *          “模板变量名” :  TemplateMessageData,
     *          ...
     *        }
     * @return Boolean true -- 成功   false -- 失败
     */
    Boolean sendSubscribeMessage(String openid, int scene, String template_id, String action, String reserved, String title, String url, String miniprogram_appid, String miniprogram_pagepath, Map<String, TemplateMessageData> data);


    /**
     * 群发图文消息
     * @param is_to_all             用于设定是否向全部用户发送，值为true或false，选择true该消息群发给所有用户，选择false可根据tag_id发送给指定群组的用户
     * @param tag_id                群发到的标签的tag_id，参见用户管理中用户分组接口，若is_to_all值为true，可不填写tag_id
     * @param media_id              用于群发的图文消息的media_id
     * @param send_ignore_reprint   图文消息被判定为转载时，是否继续群发。 1为继续群发（转载），0为停止群发。 该参数默认为0。
     *                              当 send_ignore_reprint 参数设置为1时，文章被判定为转载时，且原创文允许转载时，将继续进行群发操作。
     *                              当 send_ignore_reprint 参数设置为0时，文章被判定为转载时，将停止群发操作。
     *                                 send_ignore_reprint 默认为0。
     * @param clientmsgid            开发者侧群发msgid，长度限制64字节，如不填，则后台默认以群发范围和群发内容的摘要值做为clientmsgid
     *                               开发者调用群发接口时可以主动设置 clientmsgid 参数，避免重复推送
     *                               群发时，微信后台将对 24 小时内的群发记录进行检查，如果该 clientmsgid 已经存在一条群发记录，则会拒绝本次群发请求，返回已存在的群发msgid，开发者可以调用“查询群发消息发送状态”接口查看该条群发的状态。
     * @return  Map<String,String>   null --失败
     *      {
     *          "msg_id":34182,                 //消息发送任务的ID
     *          "msg_data_id": 206227730        //消息的数据ID，该字段只有在群发图文消息时，才会出现。可以用于在图文分析数据接口中，获取到对应的图文消息的数据，是图文分析数据接口中的msgid字段中的前半部分，详见图文分析数据接口中的msgid字段的介绍。
     *      }
     */
    Map<String,String> sendAllNews(Boolean is_to_all, String tag_id, String media_id, int send_ignore_reprint, String clientmsgid);


    /**
     * 群发文本消息
     * @param is_to_all             用于设定是否向全部用户发送，值为true或false，选择true该消息群发给所有用户，选择false可根据tag_id发送给指定群组的用户
     * @param tag_id                群发到的标签的tag_id，参见用户管理中用户分组接口，若is_to_all值为true，可不填写tag_id
     * @param content               群发文本
     * @param clientmsgid            开发者侧群发msgid，长度限制64字节，如不填，则后台默认以群发范围和群发内容的摘要值做为clientmsgid
     *                               开发者调用群发接口时可以主动设置 clientmsgid 参数，避免重复推送
     *                               群发时，微信后台将对 24 小时内的群发记录进行检查，如果该 clientmsgid 已经存在一条群发记录，则会拒绝本次群发请求，返回已存在的群发msgid，开发者可以调用“查询群发消息发送状态”接口查看该条群发的状态。
     * @return  Map<String,String>   null --失败
     *      {
     *          "msg_id":34182,                 //消息发送任务的ID
     *          "msg_data_id": 206227730        //消息的数据ID，该字段只有在群发图文消息时，才会出现。可以用于在图文分析数据接口中，获取到对应的图文消息的数据，是图文分析数据接口中的msgid字段中的前半部分，详见图文分析数据接口中的msgid字段的介绍。
     *      }
     */
    Map<String,String> sendAllText(Boolean is_to_all, String tag_id, String content, String clientmsgid);

    /**
     * 群发语音/音频消息
     * @param is_to_all             用于设定是否向全部用户发送，值为true或false，选择true该消息群发给所有用户，选择false可根据tag_id发送给指定群组的用户
     * @param tag_id                群发到的标签的tag_id，参见用户管理中用户分组接口，若is_to_all值为true，可不填写tag_id
     * @param media_id              语音/音频media_id
     * @param clientmsgid            开发者侧群发msgid，长度限制64字节，如不填，则后台默认以群发范围和群发内容的摘要值做为clientmsgid
     *                               开发者调用群发接口时可以主动设置 clientmsgid 参数，避免重复推送
     *                               群发时，微信后台将对 24 小时内的群发记录进行检查，如果该 clientmsgid 已经存在一条群发记录，则会拒绝本次群发请求，返回已存在的群发msgid，开发者可以调用“查询群发消息发送状态”接口查看该条群发的状态。
     * @return  Map<String,String>   null --失败
     *      {
     *          "msg_id":34182,                 //消息发送任务的ID
     *          "msg_data_id": 206227730        //消息的数据ID，该字段只有在群发图文消息时，才会出现。可以用于在图文分析数据接口中，获取到对应的图文消息的数据，是图文分析数据接口中的msgid字段中的前半部分，详见图文分析数据接口中的msgid字段的介绍。
     *      }
     */
    Map<String,String> sendAllVoice(Boolean is_to_all, String tag_id, String media_id, String clientmsgid);


    /**
     * 群发图片消息
     * @param is_to_all             用于设定是否向全部用户发送，值为true或false，选择true该消息群发给所有用户，选择false可根据tag_id发送给指定群组的用户
     * @param tag_id                群发到的标签的tag_id，参见用户管理中用户分组接口，若is_to_all值为true，可不填写tag_id
     * @param media_ids             图片media_id数组
     * @param recommend             推荐语，不填则默认为“分享图片”
     * @param need_open_comment     是否打开评论，0不打开，1打开
     * @param only_fans_can_comment 是否粉丝才可评论，0所有人可评论，1粉丝才可评论
     * @param clientmsgid            开发者侧群发msgid，长度限制64字节，如不填，则后台默认以群发范围和群发内容的摘要值做为clientmsgid
     *                               开发者调用群发接口时可以主动设置 clientmsgid 参数，避免重复推送
     *                               群发时，微信后台将对 24 小时内的群发记录进行检查，如果该 clientmsgid 已经存在一条群发记录，则会拒绝本次群发请求，返回已存在的群发msgid，开发者可以调用“查询群发消息发送状态”接口查看该条群发的状态。
     * @return  Map<String,String>   null --失败
     *      {
     *          "msg_id":34182,                 //消息发送任务的ID
     *          "msg_data_id": 206227730        //消息的数据ID，该字段只有在群发图文消息时，才会出现。可以用于在图文分析数据接口中，获取到对应的图文消息的数据，是图文分析数据接口中的msgid字段中的前半部分，详见图文分析数据接口中的msgid字段的介绍。
     *      }
     */
    Map<String,String> sendAllImages(Boolean is_to_all, String tag_id, String[] media_ids, String recommend, int need_open_comment, int only_fans_can_comment, String clientmsgid);

    /**
     * 群发视频消息
     * @param is_to_all             用于设定是否向全部用户发送，值为true或false，选择true该消息群发给所有用户，选择false可根据tag_id发送给指定群组的用户
     * @param tag_id                群发到的标签的tag_id，参见用户管理中用户分组接口，若is_to_all值为true，可不填写tag_id
     * @param media_id              视频media_id
     * @param clientmsgid            开发者侧群发msgid，长度限制64字节，如不填，则后台默认以群发范围和群发内容的摘要值做为clientmsgid
     *                               开发者调用群发接口时可以主动设置 clientmsgid 参数，避免重复推送
     *                               群发时，微信后台将对 24 小时内的群发记录进行检查，如果该 clientmsgid 已经存在一条群发记录，则会拒绝本次群发请求，返回已存在的群发msgid，开发者可以调用“查询群发消息发送状态”接口查看该条群发的状态。
     * @return  Map<String,String>   null --失败
     *      {
     *          "msg_id":34182,                 //消息发送任务的ID
     *          "msg_data_id": 206227730        //消息的数据ID，该字段只有在群发图文消息时，才会出现。可以用于在图文分析数据接口中，获取到对应的图文消息的数据，是图文分析数据接口中的msgid字段中的前半部分，详见图文分析数据接口中的msgid字段的介绍。
     *      }
     */
    Map<String,String> sendAllVideo(Boolean is_to_all, String tag_id, String media_id, String clientmsgid);

    /**
     * 群发卡券消息
     * @param is_to_all             用于设定是否向全部用户发送，值为true或false，选择true该消息群发给所有用户，选择false可根据tag_id发送给指定群组的用户
     * @param tag_id                群发到的标签的tag_id，参见用户管理中用户分组接口，若is_to_all值为true，可不填写tag_id
     * @param card_id               卡券id
     * @param clientmsgid            开发者侧群发msgid，长度限制64字节，如不填，则后台默认以群发范围和群发内容的摘要值做为clientmsgid
     *                               开发者调用群发接口时可以主动设置 clientmsgid 参数，避免重复推送
     *                               群发时，微信后台将对 24 小时内的群发记录进行检查，如果该 clientmsgid 已经存在一条群发记录，则会拒绝本次群发请求，返回已存在的群发msgid，开发者可以调用“查询群发消息发送状态”接口查看该条群发的状态。
     * @return  Map<String,String>   null --失败
     *      {
     *          "msg_id":34182,                 //消息发送任务的ID
     *          "msg_data_id": 206227730        //消息的数据ID，该字段只有在群发图文消息时，才会出现。可以用于在图文分析数据接口中，获取到对应的图文消息的数据，是图文分析数据接口中的msgid字段中的前半部分，详见图文分析数据接口中的msgid字段的介绍。
     *      }
     */
    Map<String,String> sendAllCard(Boolean is_to_all, String tag_id, String card_id, String clientmsgid);

    /**
     * 群发图文消息
     * @param tousers               填写图文消息的接收者，一串OpenID列表，OpenID最少2个，最多10000个
     * @param media_id              用于群发的图文消息的media_id
     * @param send_ignore_reprint   图文消息被判定为转载时，是否继续群发。 1为继续群发（转载），0为停止群发。 该参数默认为0。
     *                              当 send_ignore_reprint 参数设置为1时，文章被判定为转载时，且原创文允许转载时，将继续进行群发操作。
     *                              当 send_ignore_reprint 参数设置为0时，文章被判定为转载时，将停止群发操作。
     *                                 send_ignore_reprint 默认为0。
     * @param clientmsgid            开发者侧群发msgid，长度限制64字节，如不填，则后台默认以群发范围和群发内容的摘要值做为clientmsgid
     *                               开发者调用群发接口时可以主动设置 clientmsgid 参数，避免重复推送
     *                               群发时，微信后台将对 24 小时内的群发记录进行检查，如果该 clientmsgid 已经存在一条群发记录，则会拒绝本次群发请求，返回已存在的群发msgid，开发者可以调用“查询群发消息发送状态”接口查看该条群发的状态。
     * @return  Map<String,String>   null --失败
     *      {
     *          "msg_id":34182,                 //消息发送任务的ID
     *          "msg_data_id": 206227730        //消息的数据ID，该字段只有在群发图文消息时，才会出现。可以用于在图文分析数据接口中，获取到对应的图文消息的数据，是图文分析数据接口中的msgid字段中的前半部分，详见图文分析数据接口中的msgid字段的介绍。
     *      }
     */
    Map<String,String> sendMassNews(String[] tousers, String media_id, int send_ignore_reprint, String clientmsgid);


    /**
     * 群发文本消息
     * @param tousers               填写图文消息的接收者，一串OpenID列表，OpenID最少2个，最多10000个
     * @param content               群发文本
     * @param clientmsgid            开发者侧群发msgid，长度限制64字节，如不填，则后台默认以群发范围和群发内容的摘要值做为clientmsgid
     *                               开发者调用群发接口时可以主动设置 clientmsgid 参数，避免重复推送
     *                               群发时，微信后台将对 24 小时内的群发记录进行检查，如果该 clientmsgid 已经存在一条群发记录，则会拒绝本次群发请求，返回已存在的群发msgid，开发者可以调用“查询群发消息发送状态”接口查看该条群发的状态。
     * @return  Map<String,String>   null --失败
     *      {
     *          "msg_id":34182,                 //消息发送任务的ID
     *          "msg_data_id": 206227730        //消息的数据ID，该字段只有在群发图文消息时，才会出现。可以用于在图文分析数据接口中，获取到对应的图文消息的数据，是图文分析数据接口中的msgid字段中的前半部分，详见图文分析数据接口中的msgid字段的介绍。
     *      }
     */
    Map<String,String> sendMassText(String[] tousers, String content, String clientmsgid);

    /**
     * 群发语音/音频消息
     * @param tousers               填写图文消息的接收者，一串OpenID列表，OpenID最少2个，最多10000个
     * @param media_id              语音/音频media_id
     * @param clientmsgid            开发者侧群发msgid，长度限制64字节，如不填，则后台默认以群发范围和群发内容的摘要值做为clientmsgid
     *                               开发者调用群发接口时可以主动设置 clientmsgid 参数，避免重复推送
     *                               群发时，微信后台将对 24 小时内的群发记录进行检查，如果该 clientmsgid 已经存在一条群发记录，则会拒绝本次群发请求，返回已存在的群发msgid，开发者可以调用“查询群发消息发送状态”接口查看该条群发的状态。
     * @return  Map<String,String>   null --失败
     *      {
     *          "msg_id":34182,                 //消息发送任务的ID
     *          "msg_data_id": 206227730        //消息的数据ID，该字段只有在群发图文消息时，才会出现。可以用于在图文分析数据接口中，获取到对应的图文消息的数据，是图文分析数据接口中的msgid字段中的前半部分，详见图文分析数据接口中的msgid字段的介绍。
     *      }
     */
    Map<String,String> sendMassVoice(String[] tousers, String media_id, String clientmsgid);


    /**
     * 群发图片消息
     * @param tousers               填写图文消息的接收者，一串OpenID列表，OpenID最少2个，最多10000个
     * @param media_ids             图片media_id数组
     * @param recommend             推荐语，不填则默认为“分享图片”
     * @param need_open_comment     是否打开评论，0不打开，1打开
     * @param only_fans_can_comment 是否粉丝才可评论，0所有人可评论，1粉丝才可评论
     * @param clientmsgid            开发者侧群发msgid，长度限制64字节，如不填，则后台默认以群发范围和群发内容的摘要值做为clientmsgid
     *                               开发者调用群发接口时可以主动设置 clientmsgid 参数，避免重复推送
     *                               群发时，微信后台将对 24 小时内的群发记录进行检查，如果该 clientmsgid 已经存在一条群发记录，则会拒绝本次群发请求，返回已存在的群发msgid，开发者可以调用“查询群发消息发送状态”接口查看该条群发的状态。
     * @return  Map<String,String>   null --失败
     *      {
     *          "msg_id":34182,                 //消息发送任务的ID
     *          "msg_data_id": 206227730        //消息的数据ID，该字段只有在群发图文消息时，才会出现。可以用于在图文分析数据接口中，获取到对应的图文消息的数据，是图文分析数据接口中的msgid字段中的前半部分，详见图文分析数据接口中的msgid字段的介绍。
     *      }
     */
    Map<String,String> sendMassImages(String[] tousers, String[] media_ids, String recommend, int need_open_comment, int only_fans_can_comment, String clientmsgid);

    /**
     * 群发视频消息
     * @param tousers               填写图文消息的接收者，一串OpenID列表，OpenID最少2个，最多10000个
     * @param media_id              视频media_id
     * @param clientmsgid            开发者侧群发msgid，长度限制64字节，如不填，则后台默认以群发范围和群发内容的摘要值做为clientmsgid
     *                               开发者调用群发接口时可以主动设置 clientmsgid 参数，避免重复推送
     *                               群发时，微信后台将对 24 小时内的群发记录进行检查，如果该 clientmsgid 已经存在一条群发记录，则会拒绝本次群发请求，返回已存在的群发msgid，开发者可以调用“查询群发消息发送状态”接口查看该条群发的状态。
     * @return  Map<String,String>   null --失败
     *      {
     *          "msg_id":34182,                 //消息发送任务的ID
     *          "msg_data_id": 206227730        //消息的数据ID，该字段只有在群发图文消息时，才会出现。可以用于在图文分析数据接口中，获取到对应的图文消息的数据，是图文分析数据接口中的msgid字段中的前半部分，详见图文分析数据接口中的msgid字段的介绍。
     *      }
     */
    Map<String,String> sendMassVideo(String[] tousers, String media_id, String clientmsgid);

    /**
     * 群发卡券消息
     * @param tousers               填写图文消息的接收者，一串OpenID列表，OpenID最少2个，最多10000个
     * @param card_id               卡券id
     * @param clientmsgid            开发者侧群发msgid，长度限制64字节，如不填，则后台默认以群发范围和群发内容的摘要值做为clientmsgid
     *                               开发者调用群发接口时可以主动设置 clientmsgid 参数，避免重复推送
     *                               群发时，微信后台将对 24 小时内的群发记录进行检查，如果该 clientmsgid 已经存在一条群发记录，则会拒绝本次群发请求，返回已存在的群发msgid，开发者可以调用“查询群发消息发送状态”接口查看该条群发的状态。
     * @return  Map<String,String>   null --失败
     *      {
     *          "msg_id":34182,                 //消息发送任务的ID
     *          "msg_data_id": 206227730        //消息的数据ID，该字段只有在群发图文消息时，才会出现。可以用于在图文分析数据接口中，获取到对应的图文消息的数据，是图文分析数据接口中的msgid字段中的前半部分，详见图文分析数据接口中的msgid字段的介绍。
     *      }
     */
    Map<String,String> sendMassCard(String[] tousers, String card_id, String clientmsgid);

    /**
     * 删除消息群发任务
     * @param msg_id        消息发送任务的ID
     * @param article_idx   要删除的文章在图文消息中的位置，第一篇编号为1，该字段不填或填0会删除全部文章
     * @return  true  -- 成功   false -- 失败
     */
    Boolean deleteMass(String msg_id, int article_idx);

    /**
     * 群发图文消息预览
     * @param towxname              针对微信号进行预览（而非openID），towxname和touser同时赋值时，以towxname优先
     * @param touser                针对微信openid进行预览
     * @param media_id              用于群发的图文消息的media_id
     * @param send_ignore_reprint   图文消息被判定为转载时，是否继续群发。 1为继续群发（转载），0为停止群发。 该参数默认为0。
     *                              当 send_ignore_reprint 参数设置为1时，文章被判定为转载时，且原创文允许转载时，将继续进行群发操作。
     *                              当 send_ignore_reprint 参数设置为0时，文章被判定为转载时，将停止群发操作。
     *                                 send_ignore_reprint 默认为0。
     * @return  true  -- 成功   false -- 失败
     */
    Boolean sendPreviewNews(String towxname, String touser, String media_id, int send_ignore_reprint);


    /**
     * 群发文本消息预览
     * @param towxname              针对微信号进行预览（而非openID），towxname和touser同时赋值时，以towxname优先
     * @param touser                针对微信openid进行预览
     * @param content               群发文本
     * @return  true  -- 成功   false -- 失败
     */
    Boolean sendPreviewText(String towxname, String touser, String content);

    /**
     * 群发语音/音频消息预览
     * @param towxname              针对微信号进行预览（而非openID），towxname和touser同时赋值时，以towxname优先
     * @param touser                针对微信openid进行预览
     * @param media_id              语音/音频media_id
     * @return  Map<String,String>   null --失败
     *      {
     *          "msg_id":34182,                 //消息发送任务的ID
     *          "msg_data_id": 206227730        //消息的数据ID，该字段只有在群发图文消息时，才会出现。可以用于在图文分析数据接口中，获取到对应的图文消息的数据，是图文分析数据接口中的msgid字段中的前半部分，详见图文分析数据接口中的msgid字段的介绍。
     *      }
     */
    Boolean sendPreviewVoice(String towxname, String touser, String media_id);


    /**
     * 群发图片消息预览
     * @param towxname              针对微信号进行预览（而非openID），towxname和touser同时赋值时，以towxname优先
     * @param touser                针对微信openid进行预览
     * @param media_ids             图片media_id数组
     * @param recommend             推荐语，不填则默认为“分享图片”
     * @param need_open_comment     是否打开评论，0不打开，1打开
     * @param only_fans_can_comment 是否粉丝才可评论，0所有人可评论，1粉丝才可评论
     * @return  Map<String,String>   null --失败
     *      {
     *          "msg_id":34182,                 //消息发送任务的ID
     *          "msg_data_id": 206227730        //消息的数据ID，该字段只有在群发图文消息时，才会出现。可以用于在图文分析数据接口中，获取到对应的图文消息的数据，是图文分析数据接口中的msgid字段中的前半部分，详见图文分析数据接口中的msgid字段的介绍。
     *      }
     */
    Boolean sendPreviewImages(String towxname, String touser, String[] media_ids, String recommend, int need_open_comment, int only_fans_can_comment);

    /**
     * 群发视频消息预览
     * @param towxname              针对微信号进行预览（而非openID），towxname和touser同时赋值时，以towxname优先
     * @param touser                针对微信openid进行预览
     * @param media_id              视频media_id
     * @return  Map<String,String>   null --失败
     *      {
     *          "msg_id":34182,                 //消息发送任务的ID
     *          "msg_data_id": 206227730        //消息的数据ID，该字段只有在群发图文消息时，才会出现。可以用于在图文分析数据接口中，获取到对应的图文消息的数据，是图文分析数据接口中的msgid字段中的前半部分，详见图文分析数据接口中的msgid字段的介绍。
     *      }
     */
    Boolean sendPreviewVideo(String towxname, String touser, String media_id);

    /**
     * 群发卡券消息预览
     * @param towxname              针对微信号进行预览（而非openID），towxname和touser同时赋值时，以towxname优先
     * @param touser                针对微信openid进行预览
     * @param card_id               卡券id
     * @return  Map<String,String>   null --失败
     *      {
     *          "msg_id":34182,                 //消息发送任务的ID
     *          "msg_data_id": 206227730        //消息的数据ID，该字段只有在群发图文消息时，才会出现。可以用于在图文分析数据接口中，获取到对应的图文消息的数据，是图文分析数据接口中的msgid字段中的前半部分，详见图文分析数据接口中的msgid字段的介绍。
     *      }
     */
    Boolean sendPreviewCard(String towxname, String touser, String card_id);

    /**
     * 查询群发任务状态
     * @param msg_id        群发消息后返回的消息id
     * @return  String
     *      消息发送后的状态，SEND_SUCCESS表示发送成功，SENDING表示发送中，SEND_FAIL表示发送失败，DELETE表示已删除
     */
    String queryMassStatus(String msg_id);

    /**
     * 设置群发速度
     * @param speed     群发速度的级别
     *                  0	80w/分钟
     *                  1	60w/分钟
     *                  2	45w/分钟
     *                  3	30w/分钟
     *                  4	10w/分钟
     * @return true  --  成功  false -- 失败
     */
    Boolean setMassSpeed(int speed);



    /**
     * 获取群发速度
     * @return Map<String, Object>
     *     {
     *          "speed":3,              //群发速度的级别
     *          "realspeed":15          //群发速度的真实值 单位：万/分钟
     *      }
     */
    Map<String, Object> getMassSpeed();



    /**
     * 公众号调用或第三方平台帮公众号调用对公众号的所有api调用（包括第三方帮其调用）次数进行清零
     * @return true  --  成功  false -- 失败
     */
    Boolean clearQuota();

    /**
     * 返回的JSON格式字符串
     * {
     *    "is_add_friend_reply_open": 1,
     *    "is_autoreply_open": 1,
     *    "add_friend_autoreply_info": {
     *        "type": "text",
     *        "content": "Thanks for your attention!"
     *    },
     *    "message_default_autoreply_info": {
     *        "type": "text",
     *        "content": "Hello, this is autoreply!"
     *    },
     *    "keyword_autoreply_info": {
     *        "list": [
     *            {
     *                "rule_name": "autoreply-news",
     *                "create_time": 1423028166,
     *                "reply_mode": "reply_all",
     *                "keyword_list_info": [
     *                    {
     *                        "type": "text",
     *                        "match_mode": "contain",
     *                        "content": "news测试"//此处content即为关键词内容
     *                    }
     *                ],
     *                "reply_list_info": [
     *                    {
     *                        "type": "news",
     *                        "news_info": {
     *                            "list": [
     *                                {
     *                                    "title": "it's news",
     *                                    "author": "jim",
     *                                    "digest": "it's digest",
     *                                    "show_cover": 1,  "cover_url": "http://mmbiz.qpic.cn/mmbiz/GE7et87vE9vicuCibqXsX9GPPLuEtBfXfKbE8sWdt2DDcL0dMfQWJWTVn1N8DxI0gcRmrtqBOuwQH
     *
     * euPKmFLK0ZQ/0",
     *                                    "content_url": "http://mp.weixin.qq.com/s?__biz=MjM5ODUwNTM3Ng==&mid=203929886&idx=1&sn=628f964cf0c6d84c026881b6959aea8b#rd",
     *                                    "source_url": "http://www.url.com"
     *                                }
     *                            ]
     *                        }
     *                    },
     *                    {
     *                        "type": "news",
     *                        "content":"KQb_w_Tiz-nSdVLoTV35Psmty8hGBulGhEdbb9SKs-o",
     *                        "news_info": {
     *                            "list": [
     *                                {
     *                                    "title": "MULTI_NEWS",
     *                                    "author": "JIMZHENG",
     *                                    "digest": "text",
     *                                    "show_cover": 0,
     *                                    "cover_url": "http://mmbiz.qpic.cn/mmbiz/GE7et87vE9vicuCibqXsX9GPPLuEtBfXfK0HKuBIa1A1cypS0uY1wickv70iaY1gf3I1DTszuJoS3lAVLv
     *
     * hTcm9sDA/0",
     *                                    "content_url": "http://mp.weixin.qq.com/s?__biz=MjM5ODUwNTM3Ng==&mid=204013432&idx=1&sn=80ce6d9abcb832237bf86c87e50fda15#rd",
     *                                    "source_url": ""
     *                                },
     *                                {
     *                                    "title": "MULTI_NEWS4",
     *                                    "author": "JIMZHENG",
     *                                    "digest": "MULTI_NEWSMULTI_NEWSMULTI_NEWSMULTI_NEWSMULTI_NEWSMULT",
     *                                    "show_cover": 1,
     * "cover_url": "http://mmbiz.qpic.cn/mmbiz/GE7et87vE9vicuCibqXsX9GPPLuEtBfXfKbE8sWdt2DDcL0dMfQWJWTVn1N8DxI0gcRmrtqBOuwQ
     *
     * HeuPKmFLK0ZQ/0",
     *                                    "content_url": "http://mp.weixin.qq.com/s?__biz=MjM5ODUwNTM3Ng==&mid=204013432&idx=5&sn=b4ef73a915e7c2265e437096582774af#rd",
     *                                    "source_url": ""
     *                                }
     *                            ]
     *                        }
     *                    }
     *                ]
     *            },
     *            {
     *                "rule_name": "autoreply-voice",
     *                "create_time": 1423027971,
     *                "reply_mode": "random_one",
     *                "keyword_list_info": [
     *                    {
     *                        "type": "text",
     *                        "match_mode": "contain",
     *                        "content": "voice测试"
     *                    }
     *                ],
     *                "reply_list_info": [
     *                    {
     *                        "type": "voice",
     *                        "content": "NESsxgHEvAcg3egJTtYj4uG1PTL6iPhratdWKDLAXYErhN6oEEfMdVyblWtBY5vp"
     *                    }
     *                ]
     *            },
     *            {
     *                "rule_name": "autoreply-text",
     *                "create_time": 1423027926,
     *                "reply_mode": "random_one",
     *                "keyword_list_info": [
     *                    {
     *                        "type": "text",
     *                        "match_mode": "contain",
     *                        "content": "text测试"
     *                    }
     *                ],
     *                "reply_list_info": [
     *                    {
     *                        "type": "text",
     *                        "content": "hello!text!"
     *                    }
     *                ]
     *            },
     *            {
     *                "rule_name": "autoreply-video",
     *                "create_time": 1423027801,
     *                "reply_mode": "random_one",
     *                "keyword_list_info": [
     *                    {
     *                        "type": "text",
     *                        "match_mode": "equal",
     *                        "content": "video测试"
     *                    }
     *                ],
     *                "reply_list_info": [
     *                    {
     *                  "type": "video",
     * "content": "http://61.182.133.153/vweixinp.tc.qq.com/1007_114bcede9a2244eeb5ab7f76d951df5f.f10.mp4?vkey=7183E5C952B16C3AB1991BA8138673DE1037CB82A29801A504B64A77F691BF9DF7AD054A9B7FE683&sha=0&save=1"
     *                    }
     *                ]
     *            }
     *        ]
     *    }
     * }
     * @return String
     */
    String getCurrentAutoreplyInfo();


    /**
     * 订阅通知: 从公共模板库中选用模板，添到私有模板库中
     * @param tid               模板标题 id，可通过getPubTemplateTitleList接口获取，也可登录公众号后台查看获取
     * @param kidList           开发者自行组合好的模板关键词列表，关键词顺序可以自由搭配（例如 [3,5,4] 或 [4,5,3]），最多支持5个，最少2个关键词组合
     * @param sceneDesc         服务场景描述，15个字以内
     * @return String  添加至帐号下的模板id，发送订阅通知时所需  null --失败
     */
    String addNewtmplTemplate(String tid, String[] kidList, String sceneDesc);


    /**
     * 订阅通知: 删除私有模板库中的模板
     * @param priTmplId         模板id
     * @return true  --  成功  false -- 失败
     */
    Boolean deleteNewtmplTemplate(String priTmplId);

    /**
     * 订阅通知:获取公众号类目
     * @return Map<String , Object>  null--失败
     *      [{
     *            "id": 616,
     *            "name": "公交"
     *        },...
     *      ]
     */
    List<Map<String, Object>> getNewtmplCategory();

    /**
     * 订阅通知: 获取tid模板中的关键词
     * @param tid           模板标题 id，可通过接口获取
     * @return   List<Map<String, Object>>  null--失败
     *   [
     *        {
     *            "kid": 1,
     *            "name": "物品名称",
     *            "example": "名称",
     *            "rule": "thing"
     *        },...
     *    ]
     */
    List<Map<String, Object>> getNewtmplPubTemplateKeywords(String tid);

    /**
     * 订阅通知: 获取类目下的公共模板
     * @param ids           类目 id，多个用逗号隔开
     * @param start         用于分页，表示从 start 开始，从 0 开始计数
     * @param limit         用于分页，表示拉取 limit 条记录，最大为 30
     * @return   PageDataList  null--失败
     * {
     *    "count": 55,
     *    "data": [
     *        {
     *            "tid": 99,
     *            "title": "付款成功通知",
     *            "type": 2,
     *            "categoryId": "616"
     *        },...
     *    ]
     *  }
     */
    PageDataList getNewtmplPubTemplateTitles(String ids, int start, int limit);

    /**
     * 订阅通知: 获取私有模板列表
     * @return   List<Map<String, Object>>  null--失败
     *   [
     *    {
     *           "priTmplId": "9Aw5ZV1j9xdWTFEkqCpZ7mIBbSC34khK55OtzUPl0rU",
     *           "title": "报名结果通知",
     *           "content": "会议时间:{{date2.DATA}}\n会议地点:{{thing1.DATA}}\n",
     *           "example": "会议时间:2016年8月8日\n会议地点:TIT会议室\n",
     *           "type": 2
     *    },...
     *   ]
     */
    List<Map<String, Object>> getNewtmplTemplate();

    /**
     * 订阅通知: 发送订阅通知
     * @param touser                    接收者（用户）的 openid
     * @param template_id               所需下发的订阅模板id
     * @param page                      跳转网页时填写
     * @param miniprogram_appid         跳转小程序时填写appid
     * @param miniprogram_pagepath      跳转小程序时填写pagepath
     * @param data                      模板内容，格式形如
     *                                      {
     *                                          "key1": {"value": any },
     *                                          "key2": { "value": any }
     *                                          ,...
     *                                       }
     * @return  true -- 成功  false --  失败
     */
    Boolean sendNewtmplSubscribe(String touser, String template_id, String page, String miniprogram_appid, String miniprogram_pagepath, Map<String, Map<String, Object>> data);


    /**
     * 添加客服帐号
     * @param kf_account        完整客服账号，格式为：账号前缀@公众号微信号
     * @param nickname          客服昵称
     * @param password          客服账号登录密码，明文密码
     *                          内部将自动转为32位加密MD5值。该密码仅用于在公众平台官网的多客服功能中使用，若不使用多客服功能，则不必设置密码
     * @return true -- 成功   false  --  失败
     */
    Boolean addCustomServiceKfAccount(String kf_account, String nickname, String password);

    /**
     * 修改客服帐号
     * @param kf_account        完整客服账号，格式为：账号前缀@公众号微信号
     * @param nickname          客服昵称
     * @param password          客服账号登录密码，明文密码
     *                          内部将自动转为32位加密MD5值。该密码仅用于在公众平台官网的多客服功能中使用，若不使用多客服功能，则不必设置密码
     * @return true -- 成功   false  --  失败
     */
    Boolean updateCustomServiceKfAccount(String kf_account, String nickname, String password);

    /**
     * 删除客服帐号
     * @param kf_account        完整客服账号，格式为：账号前缀@公众号微信号
     * @return true -- 成功   false  --  失败
     */
    Boolean deleteCustomServiceKfAccount(String kf_account);

    /**
     * 设置客服帐号的头像
     * @param kf_account        完整客服账号，格式为：账号前缀@公众号微信号
     * @param fileName          服务器端头像文件名
     * @return true -- 成功   false  --  失败
     */
    Boolean uploadheadimgCustomServiceKfAccount(String kf_account, String fileName);


    /**
     * 获取所有客服列表
     * @return List<Map<String,Object>>  null -- 失败
     *
     *  [{
     *             "kf_account": "test1@test",          //完整客服账号，格式为：账号前缀@公众号微信号
     *             "kf_nick": "ntest1",                 //客服昵称
     *             "kf_id": "1001",                     //客服工号
     *             "kf_wx": "kfwx1",                    //如果客服帐号已绑定了客服人员微信号， 则此处显示微信号
     *             "kf_headimgurl": " http://mmbiz.qpic.cn/mmbiz/4whpV1VZl2iccsvYbHvnphkyGtnvjfUS8Ym0GSaLic0FD3vN0V8PILcibEGb2fPfEOmw/0",
     *             "invite_wx" : "kfwx3",               //如果客服帐号尚未绑定微信号，但是已经发起了一个绑定邀请， 则此处显示绑定邀请的微信号
     *             "invite_expire_time" : 123456789,    //如果客服帐号尚未绑定微信号，但是已经发起过一个绑定邀请， 邀请的过期时间，为unix 时间戳
     *             "invite_status" : "waiting"          //邀请的状态，有等待确认“waiting”，被拒绝“rejected”， 过期“expired”
     *   },...
     *   ]
     */
    List<Map<String,Object>> getkflistCustomServiceKfAccount();

    /**
     * 客服: 发送文本
     * @param touser                接收方帐号（OpenID）
     * @param content               文本内容
     *                              发送文本消息时，支持插入跳小程序的文字链
     *                              文本内容<a href="http://www.qq.com" data-miniprogram-appid="appid" data-miniprogram-path="pages/index/index">点击跳小程序</a>
     * @param kf_account            发送方客服帐号，可为空
     * @return true -- 成功   false  --  失败
     */
    Boolean sendtextCustomService(String touser, String content, String kf_account);

    /**
     * 客服: 发送图片
     * @param touser                接收方帐号（OpenID）
     * @param media_id              图片media_id
     * @param kf_account            发送方客服帐号，可为空
     * @return true -- 成功   false  --  失败
     */
    Boolean sendimageCustomService(String touser, String media_id, String kf_account);

    /**
     * 客服: 发送语音
     * @param touser                接收方帐号（OpenID）
     * @param media_id              语音media_id
     * @param kf_account            发送方客服帐号，可为空
     * @return true -- 成功   false  --  失败
     */
    Boolean sendvoiceCustomService(String touser, String media_id, String kf_account);

    /**
     * 客服: 发送视频
     * @param touser                接收方帐号（OpenID）
     * @param media_id              视频media_id
     * @param thumb_media_id        视频封面图片media_id
     * @param title                 视频标题
     * @param description           视频简介
     * @param kf_account            发送方客服帐号，可为空
     * @return true -- 成功   false  --  失败
     */
    Boolean sendvideoCustomService(String touser, String media_id, String thumb_media_id, String title, String description, String kf_account);

    /**
     * 客服: 发送音乐
     * @param touser                接收方帐号（OpenID）
     * @param title                 音乐标题
     * @param description           音乐描述
     * @param musicurl              音乐url
     * @param hqmusicurl            高质量音乐链接，WIFI环境优先使用该链接播放音乐
     * @param thumb_media_id        缩略图的媒体id，通过素材管理中的接口上传多媒体文件，得到的id
     * @param kf_account            发送方客服帐号，可为空
     * @return true -- 成功   false  --  失败
     */
    Boolean sendmusicCustomService(String touser, String title, String description, String musicurl, String hqmusicurl, String thumb_media_id, String kf_account);

    /**
     * 客服: 发送图文消息（点击跳转到外链）,图文消息条数限制在1条以内，注意，如果图文数超过1，则将会返回错误码45008。
     * @param touser                接收方帐号（OpenID）
     * @param articles              图文列表，最多只能有1条图文
     *       [
     *          {
     *              "title":"Happy Day",    //图文消息/视频消息/音乐消息/小程序卡片的标题
     *              "description":"Is Really A Happy Day",  //图文消息/视频消息/音乐消息的描述
     *              "url":"URL",         //图文消息被点击后跳转的链接
     *              "picurl":"PIC_URL"   //图文消息的图片链接，支持JPG、PNG格式，较好的效果为大图640*320，小图80*80
     *          }
     *      ]
     * @param kf_account            发送方客服帐号，可为空
     * @return true -- 成功   false  --  失败
     */
    Boolean sendnewsCustomService(String touser, List<Map<String, Object>> articles, String kf_account);

    /**
     * 客服: 发送图文消息（点击跳转到图文消息页面）,图文消息条数限制在1条以内，注意，如果图文数超过1，则将会返回错误码45008。
     * @param touser                接收方帐号（OpenID）
     * @param media_id              图文media_id
     * @param kf_account            发送方客服帐号，可为空
     * @return true -- 成功   false  --  失败
     */
    Boolean sendnewsCustomService(String touser, String media_id, String kf_account);

    /**
     * 客服: 发送图文消息（点击跳转到图文消息页面）使用通过 “发布” 系列接口得到的 article_id
     * 注意: 草稿接口灰度完成后，将不再支持此前客服接口中带 media_id 的 mpnews 类型的图文消息
     * @param touser                接收方帐号（OpenID）
     * @param article_id            图文media_id
     * @param kf_account            发送方客服帐号，可为空
     * @return true -- 成功   false  --  失败
     */
    Boolean sendarticleCustomService(String touser, String article_id, String kf_account);

    /**
     * 客服: 发送菜单消息
     * @param touser                接收方帐号（OpenID）
     * @param head_content          头部内容
     * @param list                  菜单列表
     *    [
     *       {
     *         "id": "101",
     *         "content": "满意"
     *       },
     *       {
     *         "id": "102",
     *         "content": "不满意"
     *       }
     *    ]
     *   其中，“满意”和“不满意”是可点击的，当用户点击后，微信会发送一条XML消息到开发者服务器，格式如下：
     *
     *      <xml>
     *          <ToUserName><![CDATA[ToUser]]></ToUserName>
     *          <FromUserName><![CDATA[FromUser]]></FromUserName>
     *          <CreateTime>1500000000</CreateTime>
     *          <MsgType><![CDATA[text]]></MsgType>
     *          <Content><![CDATA[满意]]></Content>
     *          <MsgId>1234567890123456</MsgId>
     *          <bizmsgmenuid>101</bizmsgmenuid>
     *      </xml>
     * @param tail_content          尾部内容
     * @param kf_account            发送方客服帐号，可为空
     * @return true -- 成功   false  --  失败
     */
    Boolean sendmenuCustomService(String touser, String head_content, List<Map<String, Object>> list, String tail_content, String kf_account);

    /**
     * 客服: 发送卡券
     * @param touser                接收方帐号（OpenID）
     * @param card_id               卡券card_id
     * @param kf_account            发送方客服帐号，可为空
     * @return true -- 成功   false  --  失败
     */
    Boolean sendcardCustomService(String touser, String card_id, String kf_account);

    /**
     * 客服: 发送小程序卡片（要求小程序与公众号已关联）
     * @param touser                接收方帐号（OpenID）
     * @param title                 小程序卡片标题
     * @param appid                 小程序appid
     * @param pagepath              小程序pagepath
     * @param thumb_media_id        缩略图/小程序卡片图片的媒体ID，小程序卡片图片建议大小为520*416
     * @param kf_account            发送方客服帐号，可为空
     * @return true -- 成功   false  --  失败
     */
    Boolean sendminiprogrampageCustomService(String touser, String title, String appid, String pagepath, String thumb_media_id, String kf_account);



    /**
     * 客服: 客服输入状态，开发者可通过调用“客服输入状态”接口，返回客服当前输入状态给用户
     * 此接口需要客服消息接口权限。
     *     如果不满足发送客服消息的触发条件，则无法下发输入状态。
     *     下发输入状态，需要客服之前30秒内跟用户有过消息交互。
     *     在输入状态中（持续15s），不可重复下发输入态。
     *
     * 在输入状态中，如果向用户下发消息，会同时取消输入状态。
     * @param touser                接收方帐号（OpenID）
     * @param command               "Typing"：对用户下发“正在输入"状态 "CancelTyping"：取消对用户的”正在输入"状态
     * @return true -- 成功   false  --  失败
     */
    Boolean typingCustomService(String touser, String command);

    /**
     * 客服: 获取客服在线状态列表

     * @return List<Map<String,Object>>  null -- 失败
     * [
     *          {
     *               "kf_account" :  "test1@test" ,     //完整客服帐号，格式为：帐号前缀@公众号微信号
     *               "status" : 1,                      //客服在线状态，目前为：1、web 在线
     *               "kf_id" : "1001" ,                 //客服编号
     *               "accepted_case" : 1                //客服当前正在接待的会话数
     *          },
     *          {
     *               "kf_account" : "test2@test" ,
     *               "status" : 1,
     *               "kf_id" : "1002" ,
     *               "accepted_case" : 2
     *          }
     *      ]
     */
    List<Map<String,Object>> getonlineCustomService();

    /**
     * 客服: 邀请绑定客服帐号
     * 新添加的客服帐号是不能直接使用的，只有客服人员用微信号绑定了客服账号后，方可登录Web客服进行操作。
     * 此接口发起一个绑定邀请到客服人员微信号，客服人员需要在微信客户端上用该微信号确认后帐号才可用。
     * 尚未绑定微信号的帐号可以进行绑定邀请操作，邀请未失效时不能对该帐号进行再次绑定微信号邀请。
     *
     * @param kf_account                完整客服帐号，格式为：帐号前缀@公众号微信号
     * @param invite_wx                 接收绑定邀请的客服微信号
     * @return true -- 成功   false  --  失败
     */
    Boolean inviteworkerCustomService(String kf_account, String invite_wx);



    /**
     * 客服: 创建会话
     * 此接口在客服和用户之间创建一个会话，如果该客服和用户会话已存在，则直接返回0。指定的客服帐号必须已经绑定微信号且在线。
     *
     * @param kf_account                完整客服帐号，格式为：帐号前缀@公众号微信号
     * @param openid                    粉丝的openid
     * @return true -- 成功   false  --  失败
     */
    Boolean createsessionCustomService(String kf_account, String openid);

    /**
     * 客服: 关闭会话
     *
     * @param kf_account                完整客服帐号，格式为：帐号前缀@公众号微信号
     * @param openid                    粉丝的openid
     * @return true -- 成功   false  --  失败
     */
    Boolean closesessionCustomService(String kf_account, String openid);

    /**
     * 客服: 获取客户会话状态
     * 此接口获取一个客户的会话，如果不存在，则kf_account为空。
     *
     * @param openid                    粉丝的openid
     * @return String  null -- 不存在会话，客服为空   否则返回 kf_account 客服帐号
     */
    String getsessionCustomService(String openid);

    /**
     * 客服: 获取客服会话列表
     *
     * @param kf_account                完整客服帐号，格式为：帐号前缀@公众号微信号
     * @return List<Map<String,Object>>  null -- 失败
     *
     * [
     *          {
     *             "createtime"   : 123456789,
     *             "openid"   :  "OPENID"
     *          },
     *          {
     *             "createtime"   : 123456789,
     *             "openid"   :  "OPENID"
     *          }
     * ]
     */
    List<Map<String,Object>> getsessionlistCustomService(String kf_account);

    /**
     * 客服: 获取未接入会话列表
     *
     * @return PageDataList  null -- 失败
     *  {
     *      count: 111,             //未接入会话数量
     *      data:                   //未接入会话列表，最多返回100条数据，按照来访顺序
     *      [
     *          {
     *             "latest_time"   : 123456789, //粉丝的最后一条消息的时间
     *             "openid"   :  "OPENID"       //粉丝的openid
     *          },
     *          {
     *             "latest_time"   : 123456789,
     *             "openid"   :  "OPENID"
     *          }
     *      ]
     * }
     */
    PageDataList getwaitcaseCustomService();


    /**
     * 客服: 获取聊天记录
     * 此接口返回的聊天记录中，对于图片、语音、视频，分别展示成文本格式的[image]、[voice]、[video]。对于较可能包含重要信息的图片消息，后续将提供图片拉取URL，近期将上线。
     * @param starttime         起始时间，unix时间戳
     * @param endtime           结束时间，unix时间戳，每次查询时段不能超过24小时
     * @param msgid             消息id顺序从小到大，从1开始，后一次查询请设置为上一次查询出来的msgid
     * @param number            每次获取条数，最多10000条
     * @return MsgDataList  null -- 失败
     *  {
     *      number : 122,         //实际获取条数
     *      msgid : 20165267,     //本次查询的最后一条消息msgid
     *      "recordlist"   : [
     *          {
     *             "openid"   :  "oDF3iY9WMaswOPWjCIp_f3Bnpljk" ,       //用户标识
     *             "opercode"   : 2002,                                 //操作码，2002（客服发送信息），2003（客服接收消息）
     *             "text"   :  " 您好，客服test1为您服务。" ,               //聊天记录
     *             "time"   : 1400563710,                               //操作时间，unix时间戳
     *             "worker"   :  "test1@test"                           //完整客服帐号，格式为：帐号前缀@公众号微信号
     *          },
     *          {
     *             "openid"   :  "oDF3iY9WMaswOPWjCIp_f3Bnpljk" ,
     *             "opercode"   : 2003,
     *             "text"   :  "你好，有什么事情？" ,
     *             "time"   : 1400563731,
     *             "worker"   :  "test1@test"
     *          }
     *       ]
     *
     *  }
     */
    MsgDataList getmsglistCustomService(int starttime, int endtime, long msgid, int number);

    /**
     * 发起微信网页授权，将直接跳转到微信网页授权引导页面，可使用静默授权(不弹出授权页面，直接跳转，只能获取用户openid)或
     * 网页授权(弹出授权页面，可通过openid拿到昵称、性别、所在地。并且， 即使在未关注的情况下，只要用户授权，也能获取其信息)
     * 尤其注意：跳转回调redirect_uri，应当使用https链接来确保授权code的安全性。
     * 如果用户同意授权，页面将跳转至 redirect_uri/?code=CODE&state=STATE
     * code说明 ： code作为换取网页授权access_token令牌的票据，每次用户授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期
     *            网页授权access_token令牌与接口调用的access_token令牌不一样
     * @param redirect_uri          授权后重定向的回调链接地址， 请使用 urlEncode 对链接进行处理，url中域名必须配置到网页授权域名列表中
     * @param scope                 应用授权作用域
     *                              snsapi_base         --    静默授权（不弹出授权页面，直接跳转，只能获取用户openid）
     *                              snsapi_userinfo     --    网页授权（弹出授权页面，可通过openid拿到昵称、性别、所在地。并且， 即使在未关注的情况下，只要用户授权，也能获取其信息 ）
     * @param state                 重定向后会带上state参数，开发者可以填写a-zA-Z0-9的参数值，最多128字节，可用于传递参数
     * @throws IOException
     */
    void oauth2Authorize(String redirect_uri, String scope, String state)  throws IOException;

    /**
     * 发起微信网页授权，将直接跳转到微信网页授权引导页面，可使用静默授权(不弹出授权页面，直接跳转，只能获取用户openid)或
     * 网页授权(弹出授权页面，可通过openid拿到昵称、性别、所在地。并且， 即使在未关注的情况下，只要用户授权，也能获取其信息)
     * 尤其注意：跳转回调redirect_uri，应当使用https链接来确保授权code的安全性。
     * 如果用户同意授权，页面将跳转至 redirect_uri/?code=CODE&state=STATE
     * code说明 ： code作为换取网页授权access_token令牌的票据，每次用户授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期
     *            网页授权access_token令牌与接口调用的access_token令牌不一样
     * @param redirect_uri          授权后重定向的回调链接地址， 请使用 urlEncode 对链接进行处理，url中域名必须配置到网页授权域名列表中
     * @param scope                 应用授权作用域
     *                              snsapi_base         --    静默授权（不弹出授权页面，直接跳转，只能获取用户openid）
     *                              snsapi_userinfo     --    网页授权（弹出授权页面，可通过openid拿到昵称、性别、所在地。并且， 即使在未关注的情况下，只要用户授权，也能获取其信息 ）
     * @param state                 重定向后会带上state参数，开发者可以填写a-zA-Z0-9的参数值，最多128字节，可用于传递参数
     * @param response              response
     * @throws IOException
     */
    void oauth2Authorize(String redirect_uri, String scope, String state, HttpServletResponse response) throws IOException;



    /**
     * 使用 oauth2Authorize方法 重定向获取的微信网页授权 code 换取 网页授权访问令牌 access_token
     * @param code                  填写第一步获取的code参数
     * @return JSONObject  null -- 失败  或  不包含access_token的key时也失败
     *   {
     *      "access_token":"ACCESS_TOKEN",          //网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
     *      "expires_in":7200,                      //access_token接口调用凭证超时时间，单位（秒）
     *      "refresh_token":"REFRESH_TOKEN",        //用户刷新access_token
     *      "openid":"OPENID",                      //用户唯一标识，请注意，在未关注公众号时，用户访问公众号的网页，也会产生一个用户和公众号唯一的OpenID
     *      "scope":"SCOPE"                         //用户授权的作用域，使用逗号（,）分隔
     * }
     */
    JSONObject getOauth2AccessToken(String code);


    /**
     * 用户网页授权成功之后直接获取网页授权令牌，必须开启令牌redis存储否则直接返回null
     * 如果redis存储中存在有效的网页授权令牌，直接返回
     * 如果redis存储中不存在有效的网页授权令牌，则检查是否存在有效的网页授权刷新令牌，如果存在则使用网页授权刷新令牌获取最新的网页授权令牌并返回，同时存储最新的网页授权令牌及刷新令牌
     * 如果不存在有效的网页授权刷新令牌，则直接返回null
     * @return JSONObject  null -- 失败  或  不包含access_token的key时也失败
     *   {
     *      "access_token":"ACCESS_TOKEN",                  //网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
     *      "expires_in":7200,                              //access_token接口调用凭证超时时间，单位（秒）
     *      "refresh_token":"REFRESH_TOKEN",                //用户刷新access_token
     *      "unionid": "oOQJy1qOPD9wcPiAzl_uw2oX4LV0",      //仅采用snsapi_userinfo授权，且绑定微信开放平台，才返回微信用户的unionid
     *      "openid":"OPENID",                              //用户唯一标识，请注意，在未关注公众号时，用户访问公众号的网页，也会产生一个用户和公众号唯一的OpenID
     *      "scope":"SCOPE"                                 //用户授权的作用域，使用逗号（,）分隔
     * }
     */
    JSONObject getOauth2AccessToken();


    /**
     * 刷新网页授权access_token令牌，并返回网页授权access_token令牌
     * 由于access_token拥有较短的有效期，当access_token超时后，可以使用refresh_token进行刷新，refresh_token有效期为30天，当refresh_token失效之后，需要用户重新授权。
     * @return JSONObject  null -- 失败  或  不包含access_token的key时也失败
     *   {
     *      "access_token":"ACCESS_TOKEN",          //网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
     *      "expires_in":7200,                      //access_token接口调用凭证超时时间，单位（秒）
     *      "refresh_token":"REFRESH_TOKEN",        //用户刷新access_token
     *      "openid":"OPENID",                      //用户唯一标识，请注意，在未关注公众号时，用户访问公众号的网页，也会产生一个用户和公众号唯一的OpenID
     *      "scope":"SCOPE"                         //用户授权的作用域，使用逗号（,）分隔
     * }
     */
    JSONObject refreshOauth2AccessToken(String refresh_token);

    /**
     * 拉取用户信息(需scope为 snsapi_userinfo)
     * 如果网页授权作用域为snsapi_userinfo，则此时开发者可以通过access_token和openid拉取用户信息了。
     * @param access_token          //网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
     * @param openid                //用户的唯一标识
     * @param lang                  //返回国家地区语言版本，zh_CN 简体，zh_TW 繁体，en 英语
     * @return  JSONObject  null -- 失败
     * 成功
     * {
     *   "openid": "OPENID",                //用户的唯一标识
     *   "nickname": NICKNAME,              //用户昵称
     *   "sex": 1,                          //用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
     *   "province":"PROVINCE",             //用户个人资料填写的省份
     *   "city":"CITY",                     //普通用户个人资料填写的城市
     *   "country":"COUNTRY",               //国家，如中国为CN
     *   "headimgurl":"https://thirdwx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/46",
     *                                      //用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。
     *   "privilege":[ "PRIVILEGE1" "PRIVILEGE2"     ],
     *                                      //用户特权信息，json 数组，如微信沃卡用户为（chinaunicom）
     *   "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
     *                                      //只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。
     * }
     * 错误
     * {
     *      "errcode":40003,
     *      "errmsg":" invalid openid "
     * }
     */
    JSONObject getSnsUserInfo(String access_token, String openid, String lang);

    /**
     * 检验 网页授权令牌  access_token 是否有效
     * @param access_token                      网页授权令牌  access_token
     * @param openid                            用户的唯一标识
     * @return      true  --  有效    false -- 无效
     */
    Boolean checkOauth2AccessToken(String access_token, String openid);

    /**
     * 获取 JsApi Ticket
     * @return String  Ticket   null -- 失败
     */
    String getJsApiTicket();

    /**
     * 针对指定 url 进行 jsApi签名，并返回可供wx.config使用的Map集合
     * @param url           jsApi签名相关url
     * @return  Map<String,Object>   null --失败
     *
     */
    Map<String,Object> jsApiSignature(String url) throws Exception;


    /**
     * 新建草稿
     * 开发者可新增常用的素材到草稿箱中进行使用。上传到草稿箱中的素材被群发或发布后，该素材将从草稿箱中移除。新增草稿可在公众平台官网-草稿箱中查看和管理
     * @param articleItems              图文列表
     * @return  String  null -- 失败   media_id
     *
     */
    String addDraft(List<ArticleItem> articleItems);


    /**
     * 获取草稿
     * 新增草稿后，开发者可以根据草稿指定的字段来下载草稿
     * @param media_id              草稿 media_id
     * @return  List<ArticleItem>  null -- 失败
     *
     */
    List<ArticleItem> getDraft(String media_id);

    /**
     * 删除草稿
     * 新增草稿后，开发者可以根据本接口来删除不再需要的草稿，节省空间。此操作无法撤销，请谨慎操作
     * @param media_id              草稿 media_id
     * @return  Boolean  false -- 失败
     *
     */
    Boolean deleteDraft(String media_id);

    /**
     * 修改草稿
     * 开发者可通过本接口对草稿进行修改
     * @param media_id                  草稿 media_id
     * @param index                     索引号，要更新的文章在图文消息中的位置（多图文消息时，此字段才有意义），第一篇为0
     * @param articleItem               图文
     * @return   Boolean  false -- 失败
     *
     */
    Boolean updateDraft(String media_id, int index, ArticleItem articleItem);

    /**
     * 获取草稿总数
     * 开发者可以根据本接口来获取草稿的总数。此接口只统计数量，不返回草稿的具体内容
     * @return   int
     *
     */
    int getDraftcount();

    /**
     * 获取草稿列表
     * 新增草稿之后，开发者可以获取草稿的列表
     *
     * @param offset                    从全部草稿素材的该偏移位置开始返回，0表示从第一个素材返回
     * @param count                     返回草稿素材的数量，取值在1到20之间
     * @param no_content                1 表示不返回 content 字段，0 表示正常返回，默认为 0
     * @return PageNewsMaterialList  null -- 失败
     */
    PageNewsMaterialList getDraftList(int offset, int count, int no_content);

    /**
     * 发布草稿,并返回 publish_id
     * 开发者需要先将图文素材以草稿的形式保存（见“草稿箱/新建草稿”，如需从已保存的草稿中选择，见“草稿箱/获取草稿列表”），选择要发布的草稿 media_id 进行发布
     *
     * @param media_id              草稿 media_id
     * @return String  null -- 失败   publish_id
     *
     */
     String freePublishDraft(String media_id);

    /**
     * 发布状态轮询接口
     * 开发者可以尝试通过下面的发布状态轮询接口获知发布情况
     *
     * @param publish_id              发布任务id  publish_id
     * @return JSONObject  null -- 失败
     *
     * 返回示例1（成功）
     * {
     *     "publish_id":"100000001",
     *     "publish_status":0,
     *     "article_id":ARTICLE_ID,
     *     "article_detail":{
     *         "count":1,
     *         "item":[
     *             {
     *                 "idx":1,
     *                 "article_url": ARTICLE_URL
     *             }
     *             //如果 count 大于 1，此处会有多篇文章
     *         ]
     *     },
     *     "fail_idx": []
     * }
     * 返回示例2（发布中）
     * {
     *     "publish_id":"100000001",
     *     "publish_status":1,
     *     "fail_idx": []
     * }
     * 返回示例3（原创审核不通过时）
     * {
     *     "publish_id":"100000001",
     *     "publish_status":2,
     *     "fail_idx":[1,2]
     * }
     *
     * 返回参数说明
     * 参数	                说明
     * publish_id	        发布任务id
     * publish_status	    发布状态，0:成功, 1:发布中，2:原创失败, 3: 常规失败, 4:平台审核不通过, 5:成功后用户删除所有文章, 6: 成功后系统封禁所有文章
     * article_id	        当发布状态为0时（即成功）时，返回图文的 article_id，可用于“客服消息”场景
     * count	            当发布状态为0时（即成功）时，返回文章数量
     * idx	                当发布状态为0时（即成功）时，返回文章对应的编号
     * article_url	        当发布状态为0时（即成功）时，返回图文的永久链接
     * fail_idx	            当发布状态为2或4时，返回不通过的文章编号，第一篇为 1；其他发布状态则为空
     *
     */
    JSONObject getFreePublish(String publish_id);

    /**
     * 删除发布
     * 发布成功之后，随时可以通过该接口删除。此操作不可逆，请谨慎操作
     * @param article_id            成功发布时返回的 article_id
     * @param index                 要删除的文章在图文消息中的位置，第一篇编号为1，该字段不填或填0会删除全部文章
     * @return Boolean  false -- 失败
     */
    Boolean deleteFreePublish(String article_id, int index);

    /**
     * 通过 article_id 获取已发布文章
     * 开发者可以通过 article_id 获取已发布的图文信息
     * @param article_id            文章article_id
     * @return   List<ArticleItem>   null -- 失败
     */
    List<ArticleItem> getFreePublishArticle(String article_id);

    /**
     * 获取成功发布列表
     * 开发者可以获取已成功发布的图文列表
     * @param offset                    从全部草稿素材的该偏移位置开始返回，0表示从第一个素材返回
     * @param count                     返回草稿素材的数量，取值在1到20之间
     * @param no_content                1 表示不返回 content 字段，0 表示正常返回，默认为 0
     * @return PageArticleMaterialList   null -- 失败
     */
    PageArticleMaterialList getFreePublishArticles(int offset, int count, int no_content);


    /**
     * 打开已群发文章评论
     *
     * @param msg_data_id               群发返回的msg_data_id
     * @param index                     多图文时，用来指定第几篇图文，从0开始，不带默认操作该msg_data_id的第一篇图文
     * @return  Boolean  false  -- 失败
     */
    Boolean openComment(int msg_data_id, int index);


    /**
     * 关闭已群发文章评论
     *
     * @param msg_data_id               群发返回的msg_data_id
     * @param index                     多图文时，用来指定第几篇图文，从0开始，不带默认操作该msg_data_id的第一篇图文
     * @return  Boolean  false  -- 失败
     */
    Boolean closeComment(int msg_data_id, int index);


    /**
     *  查看指定文章的评论数据
     * @param msg_data_id               群发返回的msg_data_id
     * @param index                     多图文时，用来指定第几篇图文，从0开始，不带默认返回该msg_data_id的第一篇图文
     * @param begin                     起始位置，从0开始
     * @param count                     获取数目（>=50会被拒绝）
     * @param type                      type=0 普通评论&精选评论 type=1 普通评论 type=2 精选评论
     * @return PageCommentList  null -- 失败
     */
    PageCommentList getCommentList(int msg_data_id, int index, int begin, int count, int type);

    /**
     * 将评论标记精选
     *
     * @param msg_data_id                       群发返回的msg_data_id
     * @param index                             多图文时，用来指定第几篇图文，从0开始，不带默认操作该msg_data_id的第一篇图文
     * @param user_comment_id                   用户评论id
     * @return Boolean  false  -- 失败
     */
    Boolean markelectComment(int msg_data_id, int index, int user_comment_id);

    /**
     * 将评论取消精选
     *
     * @param msg_data_id                       群发返回的msg_data_id
     * @param index                             多图文时，用来指定第几篇图文，从0开始，不带默认操作该msg_data_id的第一篇图文
     * @param user_comment_id                   用户评论id
     * @return Boolean  false  -- 失败
     */
    Boolean unmarkelectComment(int msg_data_id, int index, int user_comment_id);

    /**
     * 删除评论
     *
     * @param msg_data_id                       群发返回的msg_data_id
     * @param index                             多图文时，用来指定第几篇图文，从0开始，不带默认操作该msg_data_id的第一篇图文
     * @param user_comment_id                   用户评论id
     * @return Boolean  false  -- 失败
     */
    Boolean deleteComment(int msg_data_id, int index, int user_comment_id);

    /**
     * 回复评论
     *
     * @param msg_data_id                       群发返回的msg_data_id
     * @param index                             多图文时，用来指定第几篇图文，从0开始，不带默认操作该msg_data_id的第一篇图文
     * @param user_comment_id                   用户评论id
     * @param content                           回复内容
     * @return Boolean  false  -- 失败
     */
    Boolean replyComment(int msg_data_id, int index, int user_comment_id, String content);

    /**
     * 删除回复
     *
     * @param msg_data_id                       群发返回的msg_data_id
     * @param index                             多图文时，用来指定第几篇图文，从0开始，不带默认操作该msg_data_id的第一篇图文
     * @param user_comment_id                   用户评论id
     * @return Boolean  false  -- 失败
     */
    Boolean deleteReplyComment(int msg_data_id, int index, int user_comment_id);

    /**
     * 用户标签管理: 创建标签
     * 开发者可以使用用户标签管理的相关接口，实现对公众号的标签进行创建、查询、修改、删除等操作，也可以对用户进行打标签、取消标签等操作。使用接口过程中有任何问题，可以前往微信开放社区 #公众号 专区发帖交流。
     * 一个公众号，最多可以创建100个标签。
     * @param name              标签名（30个字符以内）
     * @return String  null  -- 失败  返回标签id
     *
     */
    String createTag(String name);

    /**
     * 获取公众号已创建的标签
     * @return  List<Tag> null  -- 失败
     */
    List<Tag> getTags();


    /**
     * 编辑标签
     * @param id            tag id
     * @param name          标签名（30个字符以内）
     * @return Boolean  false  -- 失败
     */
    Boolean updateTag(String id, String name);

    /**
     * 删除标签
     * @param id            tag id
     * @return Boolean  false  -- 失败
     */
    Boolean deleteTag(String id);


    /**
     * 获取标签下粉丝列表
     * @param tagid                     tag id
     * @param next_openid               第一个拉取的OPENID，null--从头开始拉取
     * @return  PageTagUserList  null -- 失败
     */
    PageTagUserList getTagUsers(String tagid, String next_openid);


    /**
     * 批量为用户打标签
     * @param tagid                     tag id
     * @param openids                   粉丝列表
     * @return Boolean  false  -- 失败
     */
    Boolean addTagUsers(String tagid, List<String> openids);


    /**
     * 批量为用户取消标签
     * @param tagid                     tag id
     * @param openids                   粉丝列表
     * @return Boolean  false  -- 失败
     */
    Boolean deleteTagUsers(String tagid, List<String> openids);

    /**
     * 获取用户身上的标签列表
     * @param openid                粉丝openid
     * @return List<String> null -- 失败
     */
    List<String> getTagsByUser(String openid);

    /**
     * 设置用户备注名
     * 开发者可以通过该接口对指定用户设置备注名，该接口暂时开放给微信认证的服务号。 接口调用请求说明
     * @param openid            粉丝openid
     * @param remark            新的备注名，长度必须小于30字节
     * @return Boolean  false  -- 失败
     */
    Boolean updateUserRemark(String openid, String remark);


    /**
     * 获取用户列表
     * 公众号可通过本接口来获取帐号的关注者列表，关注者列表由一串OpenID（加密后的微信号，每个用户对每个公众号的OpenID是唯一的）组成。一次拉取调用最多拉取10000个关注者的OpenID，可以通过多次拉取的方式来满足需求。
     * @param next_openid           第一个拉取的OPENID，null--从头开始拉取
     * @return PageUserList  null -- 失败
     */
    PageUserList getUserList(String next_openid);


    /**
     * 获取公众号的黑名单列表
     * @param next_openid           当 next_openid 为空时，默认从开头拉取
     * @return PageUserList  null -- 失败
     */
    PageUserList getBlackUserList(String next_openid);


    /**
     * 拉黑用户
     * 公众号可通过该接口来拉黑一批用户，黑名单列表由一串 OpenID （加密后的微信号，每个用户对每个公众号的OpenID是唯一的）组成。
     * @param openid_list           粉丝openid列表
     * @return Boolean  false  -- 失败
     */
    Boolean addBlackUser(List<String> openid_list);

    /**
     * 取消拉黑用户
     * 公众号可通过该接口来取消拉黑一批用户，黑名单列表由一串OpenID（加密后的微信号，每个用户对每个公众号的OpenID是唯一的）组成
     * @param openid_list           粉丝openid列表
     * @return Boolean  false  -- 失败
     */
    Boolean deleteBlackUser(List<String> openid_list);

    /**
     * 长链接转短链接  2021年03月15日后将停止该接口生成短链能力
     * 将一条长链接转成短链接。
     * 主要使用场景： 开发者用于生成二维码的原链接（商品、支付二维码等）太长导致扫码速度和成功率下降，将原长链接通过此接口转成短链接再生成二维码将大大提升扫码速度和成功率。
     * @param long_url               需要转换的长链接，支持http://、https://、weixin://wxpay 格式的url
     * @return String  null  -- 失败  返回短链接
     */
    String shorturl(String long_url);

    /**
     * 短key托管
     * 短key托管类似于短链API，开发者可以通过GenShorten将不超过4KB的长信息转成短key，再通过FetchShorten将短key还原为长信息。
     * @param long_data                 需要转换的长信息，不超过4KB
     * @param expire_seconds            过期秒数，最大值为2592000（即30天），默认为2592000
     * @return String  null  -- 失败  返回短key
     */
    String getShorten(String long_data, int expire_seconds);

    /**
     * 根据 短key 返回托管的 长信息
     * @param short_key             短key
     * @return Map<String,Object>  null  -- 失败
     * {
     *   "errcode": 0,
     *   "errmsg": "ok",
     *   "long_data": "loooooong data",
     *   "create_time": 1611047541,
     *   "expire_seconds": 86300
     * }
     */
    Map<String,Object> fetchShorten(String short_key);


    /**
     * 身份证OCR识别 文件大小限制：小于2M 拍摄图片样例
     * @param img_url           身份证图片url，支持正面及反面
     * @return  Map<String,Object>  null -- 失败
     * 正面：
     * {
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "type": "Front", //正面
     *     "name": "张三", //姓名
     *     "id": "123456789012345678", //身份证号码
     *     "addr": "广东省广州市XXX", //住址
     *     "gender": "男", //性别
     *     "nationality": "汉" //民族
     * }
     * 反面：
     * {
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "type": "Back", //背面
     *     "valid_date": "20171025-20271025", //有效期
     * }
     */
    Map<String,Object> ocrIdcardImgUrl(String img_url);

    /**
     * 身份证OCR识别 文件大小限制：小于2M 拍摄图片样例
     * @param imgFileName           本地身份证图片全路径名，支持正面及反面
     * @return  Map<String,Object>  null -- 失败
     * 正面：
     * {
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "type": "Front", //正面
     *     "name": "张三", //姓名
     *     "id": "123456789012345678", //身份证号码
     *     "addr": "广东省广州市XXX", //住址
     *     "gender": "男", //性别
     *     "nationality": "汉" //民族
     * }
     * 反面：
     * {
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "type": "Back", //背面
     *     "valid_date": "20171025-20271025", //有效期
     * }
     */
    Map<String,Object> ocrIdcardImg(String imgFileName);


    /**
     * 银行卡OCR识别 文件大小限制：小于2M 拍摄图片样例
     * @param img_url           银行卡图片url
     * @return  Map<String,Object>  null -- 失败
     * {
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "number": "银行卡号"
     * }
     */
    Map<String,Object> ocrBankcardImgUrl(String img_url);

    /**
     * 银行卡OCR识别 文件大小限制：小于2M 拍摄图片样例
     * @param imgFileName           本地银行卡图片全路径名
     * @return  Map<String,Object>  null -- 失败
     * {
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "number": "银行卡号"
     * }
     */
    Map<String,Object> ocrBankcardImg(String imgFileName);


    /**
     * 行驶证OCR识别 文件大小限制：小于2M 拍摄图片样例
     * @param img_url           行驶证图片url
     * @return  JSONObject  null -- 失败
     *
     * {
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "plate_num": "粤xxxxx", //车牌号码
     *     "vehicle_type": "小型普通客车", //车辆类型
     *     "owner": "东莞市xxxxx机械厂", //所有人
     *     "addr": "广东省东莞市xxxxx号", //住址
     *     "use_character": "非营运", //使用性质
     *     "model": "江淮牌HFCxxxxxxx", //品牌型号
     *     "vin": "LJ166xxxxxxxx51", //车辆识别代号
     *     "engine_num": "J3xxxxx3", //发动机号码
     *     "register_date": "2018-07-06", //注册日期
     *     "issue_date": "2018-07-01", //发证日期
     *     "plate_num_b": "粤xxxxx", //车牌号码
     *     "record": "441xxxxxx3", //号牌
     *     "passengers_num": "7人", //核定载人数
     *     "total_quality": "2700kg", //总质量
     *     "prepare_quality": "1995kg" //整备质量,
     *     "overall_size": "4582x1795x1458mm" //外廓尺寸,
     *     "card_position_front": {//卡片正面位置（检测到卡片正面才会返回）
     *         "pos": {
     *             "left_top": {
     *                 "x": 119,
     *                 "y": 2925
     *             },
     *             "right_top": {
     *                 "x": 1435,
     *                 "y": 2887
     *             },
     *             "right_bottom": {
     *                 "x": 1435,
     *                 "y": 3793
     *             },
     *             "left_bottom": {
     *                 "x": 119,
     *                 "y": 3831
     *             }
     *         }
     *     },
     *     "card_position_back": {//卡片反面位置（检测到卡片反面才会返回）
     *         "pos": {
     *             "left_top": {
     *                 "x": 1523,
     *                 "y": 2849
     *             },
     *             "right_top": {
     *                 "x": 2898,
     *                 "y": 2887
     *             },
     *             "right_bottom": {
     *                 "x": 2927,
     *                 "y": 3831
     *             },
     *             "left_bottom": {
     *                 "x": 1523,
     *                 "y": 3831
     *             }
     *         }
     *     },
     *     "img_size": {//图片大小
     *         "w": 3120,
     *         "h": 4208
     *     }
     * }
     *
     */
    JSONObject ocrDrivingImgUrl(String img_url);

    /**
     * 行驶证OCR识别 文件大小限制：小于2M 拍摄图片样例
     * @param imgFileName           本地行驶证图片全路径名
     * @return  JSONObject  null -- 失败
     *
     * {
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "plate_num": "粤xxxxx", //车牌号码
     *     "vehicle_type": "小型普通客车", //车辆类型
     *     "owner": "东莞市xxxxx机械厂", //所有人
     *     "addr": "广东省东莞市xxxxx号", //住址
     *     "use_character": "非营运", //使用性质
     *     "model": "江淮牌HFCxxxxxxx", //品牌型号
     *     "vin": "LJ166xxxxxxxx51", //车辆识别代号
     *     "engine_num": "J3xxxxx3", //发动机号码
     *     "register_date": "2018-07-06", //注册日期
     *     "issue_date": "2018-07-01", //发证日期
     *     "plate_num_b": "粤xxxxx", //车牌号码
     *     "record": "441xxxxxx3", //号牌
     *     "passengers_num": "7人", //核定载人数
     *     "total_quality": "2700kg", //总质量
     *     "prepare_quality": "1995kg" //整备质量,
     *     "overall_size": "4582x1795x1458mm" //外廓尺寸,
     *     "card_position_front": {//卡片正面位置（检测到卡片正面才会返回）
     *         "pos": {
     *             "left_top": {
     *                 "x": 119,
     *                 "y": 2925
     *             },
     *             "right_top": {
     *                 "x": 1435,
     *                 "y": 2887
     *             },
     *             "right_bottom": {
     *                 "x": 1435,
     *                 "y": 3793
     *             },
     *             "left_bottom": {
     *                 "x": 119,
     *                 "y": 3831
     *             }
     *         }
     *     },
     *     "card_position_back": {//卡片反面位置（检测到卡片反面才会返回）
     *         "pos": {
     *             "left_top": {
     *                 "x": 1523,
     *                 "y": 2849
     *             },
     *             "right_top": {
     *                 "x": 2898,
     *                 "y": 2887
     *             },
     *             "right_bottom": {
     *                 "x": 2927,
     *                 "y": 3831
     *             },
     *             "left_bottom": {
     *                 "x": 1523,
     *                 "y": 3831
     *             }
     *         }
     *     },
     *     "img_size": {//图片大小
     *         "w": 3120,
     *         "h": 4208
     *     }
     * }
     */
    JSONObject ocrDrivingImg(String imgFileName);


    /**
     * 驾驶证OCR识别 文件大小限制：小于2M 拍摄图片样例
     * @param img_url           驾驶证图片url
     * @return  JSONObject  null -- 失败
     *
     * {
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "id_num": "660601xxxxxxxx1234", //证号
     *     "name": "张三", //姓名
     *     "sex": "男", //性别
     *     "nationality": "中国", //国籍
     *     "address": "广东省东莞市xxxxx号", //住址
     *     "birth_date": "1990-12-21", //出生日期
     *     "issue_date": "2012-12-21", //初次领证日期
     *     "car_class": "C1", //准驾车型
     *     "valid_from": "2018-07-06", //有效期限起始日
     *     "valid_to": "2020-07-01", //有效期限终止日
     *     "official_seal": "xx市公安局公安交通管理局" //印章文字
     * }
     *
     */
    JSONObject ocrDrivinglicenseImgUrl(String img_url);

    /**
     * 驾驶证OCR识别 文件大小限制：小于2M 拍摄图片样例
     * @param imgFileName           本地驾驶证图片全路径名
     * @return  JSONObject  null -- 失败
     *
     * {
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "id_num": "660601xxxxxxxx1234", //证号
     *     "name": "张三", //姓名
     *     "sex": "男", //性别
     *     "nationality": "中国", //国籍
     *     "address": "广东省东莞市xxxxx号", //住址
     *     "birth_date": "1990-12-21", //出生日期
     *     "issue_date": "2012-12-21", //初次领证日期
     *     "car_class": "C1", //准驾车型
     *     "valid_from": "2018-07-06", //有效期限起始日
     *     "valid_to": "2020-07-01", //有效期限终止日
     *     "official_seal": "xx市公安局公安交通管理局" //印章文字
     * }
     *
     */
    JSONObject ocrDrivinglicenseImg(String imgFileName);

    /**
     * 营业执照OCR识别 文件大小限制：小于2M 拍摄图片样例
     * @param img_url           营业执照图片url
     * @return  JSONObject  null -- 失败
     *
     *
     * {
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "reg_num": "123123",//注册号
     *     "serial": "123123",//编号
     *     "legal_representative": "张三", //法定代表人姓名
     *     "enterprise_name": "XX饮食店", //企业名称
     *     "type_of_organization": "个人经营", //组成形式
     *     "address": "XX市XX区XX路XX号", //经营场所/企业住所
     *     "type_of_enterprise": "xxx", //公司类型
     *     "business_scope": "中型餐馆(不含凉菜、不含裱花蛋糕，不含生食海产品)。", //经营范围
     *     "registered_capital": "200万", //注册资本
     *     "paid_in_capital": "200万", //实收资本
     *     "valid_period": "2019年1月1日", //营业期限
     *     "registered_date": "2018年1月1日", //注册日期/成立日期
     *     "cert_position": { //营业执照位置
     *         "pos": {
     *             "left_top": {
     *                 "x": 155,
     *                 "y": 191
     *             },
     *             "right_top": {
     *                 "x": 725,
     *                 "y": 157
     *             },
     *             "right_bottom": {
     *                 "x": 743,
     *                 "y": 512
     *             },
     *             "left_bottom": {
     *                 "x": 164,
     *                 "y": 525
     *             }
     *         }
     *     },
     *     "img_size": { //图片大小
     *         "w": 966,
     *         "h": 728
     *     }
     * }
     *
     */
    JSONObject ocrBizlicenseImgUrl(String img_url);

    /**
     * 营业执照OCR识别 文件大小限制：小于2M 拍摄图片样例
     * @param imgFileName           本地营业执照图片全路径名
     * @return  JSONObject  null -- 失败
     *
     *
     * {
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "reg_num": "123123",//注册号
     *     "serial": "123123",//编号
     *     "legal_representative": "张三", //法定代表人姓名
     *     "enterprise_name": "XX饮食店", //企业名称
     *     "type_of_organization": "个人经营", //组成形式
     *     "address": "XX市XX区XX路XX号", //经营场所/企业住所
     *     "type_of_enterprise": "xxx", //公司类型
     *     "business_scope": "中型餐馆(不含凉菜、不含裱花蛋糕，不含生食海产品)。", //经营范围
     *     "registered_capital": "200万", //注册资本
     *     "paid_in_capital": "200万", //实收资本
     *     "valid_period": "2019年1月1日", //营业期限
     *     "registered_date": "2018年1月1日", //注册日期/成立日期
     *     "cert_position": { //营业执照位置
     *         "pos": {
     *             "left_top": {
     *                 "x": 155,
     *                 "y": 191
     *             },
     *             "right_top": {
     *                 "x": 725,
     *                 "y": 157
     *             },
     *             "right_bottom": {
     *                 "x": 743,
     *                 "y": 512
     *             },
     *             "left_bottom": {
     *                 "x": 164,
     *                 "y": 525
     *             }
     *         }
     *     },
     *     "img_size": { //图片大小
     *         "w": 966,
     *         "h": 728
     *     }
     * }
     *
     */
    JSONObject ocrBizlicenseImg(String imgFileName);


    /**
     * 通用印刷体OCR识别 文件大小限制：小于2M 拍摄图片样例
     * @param img_url           通用印刷体图片url
     * @return  JSONObject  null -- 失败
     *
     *
     * {
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "items": [ //识别结果
     *         {
     *             "text": "腾讯",
     *             "pos": {
     *                 "left_top": {
     *                     "x": 575,
     *                     "y": 519
     *                 },
     *                 "right_top": {
     *                     "x": 744,
     *                     "y": 519
     *                 },
     *                 "right_bottom": {
     *                     "x": 744,
     *                     "y": 532
     *                 },
     *                 "left_bottom": {
     *                     "x": 573,
     *                     "y": 532
     *                 }
     *             }
     *         },
     *         {
     *             "text": "微信团队",
     *             "pos": {
     *                 "left_top": {
     *                     "x": 670,
     *                     "y": 516
     *                 },
     *                 "right_top": {
     *                     "x": 762,
     *                     "y": 517
     *                 },
     *                 "right_bottom": {
     *                     "x": 762,
     *                     "y": 532
     *                 },
     *                 "left_bottom": {
     *                     "x": 670,
     *                     "y": 531
     *                 }
     *             }
     *         }
     *     ],
     *     "img_size": { //图片大小
     *         "w": 1280,
     *         "h": 720
     *     }
     * }
     *
     */
    JSONObject ocrCommImgUrl(String img_url);

    /**
     * 通用印刷体OCR识别 文件大小限制：小于2M 拍摄图片样例
     * @param imgFileName           本地通用印刷体图片全路径名
     * @return  JSONObject  null -- 失败
     *
     *
     *{
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "items": [ //识别结果
     *         {
     *             "text": "腾讯",
     *             "pos": {
     *                 "left_top": {
     *                     "x": 575,
     *                     "y": 519
     *                 },
     *                 "right_top": {
     *                     "x": 744,
     *                     "y": 519
     *                 },
     *                 "right_bottom": {
     *                     "x": 744,
     *                     "y": 532
     *                 },
     *                 "left_bottom": {
     *                     "x": 573,
     *                     "y": 532
     *                 }
     *             }
     *         },
     *         {
     *             "text": "微信团队",
     *             "pos": {
     *                 "left_top": {
     *                     "x": 670,
     *                     "y": 516
     *                 },
     *                 "right_top": {
     *                     "x": 762,
     *                     "y": 517
     *                 },
     *                 "right_bottom": {
     *                     "x": 762,
     *                     "y": 532
     *                 },
     *                 "left_bottom": {
     *                     "x": 670,
     *                     "y": 531
     *                 }
     *             }
     *         }
     *     ],
     *     "img_size": { //图片大小
     *         "w": 1280,
     *         "h": 720
     *     }
     * }
     *
     */
    JSONObject ocrCommImg(String imgFileName);


    /**
     * 车牌识别OCR识别 文件大小限制：小于2M 拍摄图片样例
     * @param img_url           车牌识别图片url
     * @return  Map<String,Object>  null -- 失败
     *
     *
     *{
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "number":"粤A123456" //车牌号
     * }
     *
     */
    Map<String,Object> ocrPlatenumImgUrl(String img_url);

    /**
     * 车牌识别OCR识别 文件大小限制：小于2M 拍摄图片样例
     * @param imgFileName           本地车牌识别图片全路径名
     * @return  Map<String,Object>  null -- 失败
     *
     *
     *{
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "number":"粤A123456" //车牌号
     * }
     *
     */
    Map<String,Object> ocrPlatenumImg(String imgFileName);

    /**
     * 菜单OCR识别 文件大小限制：小于2M 拍摄图片样例
     * @param img_url           菜单图片url
     * @return  JSONObject  null -- 失败
     *
     *
     *{
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "content": "{\"menu_items\": [{\"name\": \"酱鸭\", \"price\": 48.0}, {\"name\": \"龙虾\", \"price\": 80.0}, {\"name\": \"芝麻鸭块\", \"price\": 48.0}, {\"name\": \"皮皮虾\", \"price\": 70.0}, {\"name\": \"特色辣鸭脚\", \"price\": 46.0}, {\"name\": \"花甲\", \"price\": 30.0}, {\"name\": \"小鸭翅\", \"price\": 46.0}, {\"name\": \"鸭肠\", \"price\": 40.0}, {\"name\": \"微辣大鸭脚\", \"price\": 43.0}, {\"name\": \"八宝\", \"price\": 10.0}, {\"name\": \"大鸭翅\", \"price\": 43.0}, {\"name\": \"千叶豆腐\", \"price\": 10.0}, {\"name\": \"鸭脖\", \"price\": 35.0}, {\"name\": \"毛豆\", \"price\": 15.0}, {\"name\": \"鸭锁骨\", \"price\": 35.0}, {\"name\": \"螺丝\", \"price\": 15.0}, {\"name\": \"特色蟹脚\", \"price\": 55.0}, {\"name\": \"藕片\", \"price\": 15.0}, {\"name\": \"鱿鱼须\", \"price\": 70.0}, {\"name\": \"腐竹\", \"price\": 15.0}, {\"name\": \"鸭下巴\", \"price\": 70.0}, {\"name\": \"鸭头\", \"price\": 5.0}, {\"name\": \"无骨鸭脚\", \"price\": 70.0}, {\"name\": \"辣椒酱\", \"price\": 10.0}, {\"name\": \"鸭舌\", \"price\": 138.0}, {\"name\": \"花生米\", \"price\": 5.0}, {\"name\": \"鸭胗\", \"price\": 55.0}, {\"name\": \"萝卜干\", \"price\": 12.0}, {\"name\": \"牛肉串\", \"price\": 65.0}, {\"name\": \"盐菜\", \"price\": 12.0}]}"
     * }
     *
     */
    JSONObject ocrMenuImgUrl(String img_url);

    /**
     * 菜单OCR识别 文件大小限制：小于2M 拍摄图片样例
     * @param imgFileName           本地菜单识别图片全路径名
     * @return  JSONObject  null -- 失败
     *
     *
     *{
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "content": "{\"menu_items\": [{\"name\": \"酱鸭\", \"price\": 48.0}, {\"name\": \"龙虾\", \"price\": 80.0}, {\"name\": \"芝麻鸭块\", \"price\": 48.0}, {\"name\": \"皮皮虾\", \"price\": 70.0}, {\"name\": \"特色辣鸭脚\", \"price\": 46.0}, {\"name\": \"花甲\", \"price\": 30.0}, {\"name\": \"小鸭翅\", \"price\": 46.0}, {\"name\": \"鸭肠\", \"price\": 40.0}, {\"name\": \"微辣大鸭脚\", \"price\": 43.0}, {\"name\": \"八宝\", \"price\": 10.0}, {\"name\": \"大鸭翅\", \"price\": 43.0}, {\"name\": \"千叶豆腐\", \"price\": 10.0}, {\"name\": \"鸭脖\", \"price\": 35.0}, {\"name\": \"毛豆\", \"price\": 15.0}, {\"name\": \"鸭锁骨\", \"price\": 35.0}, {\"name\": \"螺丝\", \"price\": 15.0}, {\"name\": \"特色蟹脚\", \"price\": 55.0}, {\"name\": \"藕片\", \"price\": 15.0}, {\"name\": \"鱿鱼须\", \"price\": 70.0}, {\"name\": \"腐竹\", \"price\": 15.0}, {\"name\": \"鸭下巴\", \"price\": 70.0}, {\"name\": \"鸭头\", \"price\": 5.0}, {\"name\": \"无骨鸭脚\", \"price\": 70.0}, {\"name\": \"辣椒酱\", \"price\": 10.0}, {\"name\": \"鸭舌\", \"price\": 138.0}, {\"name\": \"花生米\", \"price\": 5.0}, {\"name\": \"鸭胗\", \"price\": 55.0}, {\"name\": \"萝卜干\", \"price\": 12.0}, {\"name\": \"牛肉串\", \"price\": 65.0}, {\"name\": \"盐菜\", \"price\": 12.0}]}"
     * }
     *
     */
    JSONObject ocrMenuImg(String imgFileName);

    /**
     * 二维码/条码识别 文件大小限制：小于2M 拍摄图片样例
     * @param img_url           二维码/条码图片url
     * @return  JSONObject  null -- 失败
     *
     *{
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "code_results": [
     *         {
     *             "type_name": "QR_CODE",
     *             "data": "https://www.qq.com",
     *             "pos": {
     *                 "left_top": {
     *                     "x": 585,
     *                     "y": 378
     *                 },
     *                 "right_top": {
     *                     "x": 828,
     *                     "y": 378
     *                 },
     *                 "right_bottom": {
     *                     "x": 828,
     *                     "y": 618
     *                 },
     *                 "left_bottom": {
     *                     "x": 585,
     *                     "y": 618
     *                 }
     *             }
     *         },
     *         {
     *             "type_name": "QR_CODE",
     *             "data": "https://mp.weixin.qq.com",
     *             "pos": {
     *                 "left_top": {
     *                     "x": 185,
     *                     "y": 142
     *                 },
     *                 "right_top": {
     *                     "x": 396,
     *                     "y": 142
     *                 },
     *                 "right_bottom": {
     *                     "x": 396,
     *                     "y": 353
     *                 },
     *                 "left_bottom": {
     *                     "x": 185,
     *                     "y": 353
     *                 }
     *             }
     *         },
     *         {
     *             "type_name": "EAN_13",
     *             "data": "5906789678957"
     *         },
     *         {
     *             "type_name": "CODE_128",
     *             "data": "50090500019191"
     *         }
     *     ],
     *     "img_size": {
     *         "w": 1000,
     *         "h": 900
     *     }
     * }
     *
     */
    JSONObject cvImageQrcodeImgUrl(String img_url);

    /**
     * 二维码/条码识别 文件大小限制：小于2M 拍摄图片样例
     * @param imgFileName           本地二维码/条码识别图片全路径名
     * @return  JSONObject  null -- 失败
     *
     *
     *{
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "code_results": [
     *         {
     *             "type_name": "QR_CODE",
     *             "data": "https://www.qq.com",
     *             "pos": {
     *                 "left_top": {
     *                     "x": 585,
     *                     "y": 378
     *                 },
     *                 "right_top": {
     *                     "x": 828,
     *                     "y": 378
     *                 },
     *                 "right_bottom": {
     *                     "x": 828,
     *                     "y": 618
     *                 },
     *                 "left_bottom": {
     *                     "x": 585,
     *                     "y": 618
     *                 }
     *             }
     *         },
     *         {
     *             "type_name": "QR_CODE",
     *             "data": "https://mp.weixin.qq.com",
     *             "pos": {
     *                 "left_top": {
     *                     "x": 185,
     *                     "y": 142
     *                 },
     *                 "right_top": {
     *                     "x": 396,
     *                     "y": 142
     *                 },
     *                 "right_bottom": {
     *                     "x": 396,
     *                     "y": 353
     *                 },
     *                 "left_bottom": {
     *                     "x": 185,
     *                     "y": 353
     *                 }
     *             }
     *         },
     *         {
     *             "type_name": "EAN_13",
     *             "data": "5906789678957"
     *         },
     *         {
     *             "type_name": "CODE_128",
     *             "data": "50090500019191"
     *         }
     *     ],
     *     "img_size": {
     *         "w": 1000,
     *         "h": 900
     *     }
     * }
     *
     */
    JSONObject cvImageQrcodeImg(String imgFileName);


    /**
     * 图片高清化 文件大小限制：小于2M 拍摄图片样例
     * @param img_url               图片url
     * @return  Map<String,Object>  null -- 失败
     *
     *
     *{
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "media_id": "6WXsIXkG7lXuDLspD9xfm5dsvHzb0EFl0li6ySxi92ap8Vl3zZoD9DpOyNudeJGB"
     * }
     *
     * 返回的media_id有效期为3天，期间可以通过“获取临时素材”接口获取图片二进制
     * curl "https://api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID" -o "output.jpg"
     *
     */
    Map<String,Object> cvImageSuperresolutionImgUrl(String img_url);

    /**
     * 图片高清化 文件大小限制：小于2M 拍摄图片样例
     * @param imgFileName           本地图片全路径名
     * @return  Map<String,Object>  null -- 失败
     *
     *
     *{
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "media_id": "6WXsIXkG7lXuDLspD9xfm5dsvHzb0EFl0li6ySxi92ap8Vl3zZoD9DpOyNudeJGB"
     * }
     *
     * 返回的media_id有效期为3天，期间可以通过“获取临时素材”接口获取图片二进制
     * curl "https://api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID" -o "output.jpg"
     *
     */
    Map<String,Object> cvImageSuperresolutionImg(String imgFileName);



    /**
     * 图片智能裁剪 文件大小限制：小于2M 拍摄图片样例
     * @param img_url           图片智能裁剪图片url
     * @param ratios                1,2.35
     * @return  JSONObject  null -- 失败
     *
     *{
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "results": [ //智能裁剪结果
     *         {
     *             "crop_left": 112,
     *             "crop_top": 0,
     *             "crop_right": 839,
     *             "crop_bottom": 727
     *         },
     *         {
     *             "crop_left": 0,
     *             "crop_top": 205,
     *             "crop_right": 965,
     *             "crop_bottom": 615
     *         }
     *     ],
     *     "img_size": { //图片大小
     *         "w": 966,
     *         "h": 728
     *     }
     * }
     *
     */
    JSONObject cvImageAicropImgUrl(String img_url, String ratios);

    /**
     * 图片智能裁剪 文件大小限制：小于2M 拍摄图片样例
     * @param imgFileName           本地图片智能裁剪图片全路径名
     * @param ratios                1,2.35
     * @return  JSONObject  null -- 失败
     *
     *
     *{
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "results": [ //智能裁剪结果
     *         {
     *             "crop_left": 112,
     *             "crop_top": 0,
     *             "crop_right": 839,
     *             "crop_bottom": 727
     *         },
     *         {
     *             "crop_left": 0,
     *             "crop_top": 205,
     *             "crop_right": 965,
     *             "crop_bottom": 615
     *         }
     *     ],
     *     "img_size": { //图片大小
     *         "w": 966,
     *         "h": 728
     *     }
     * }
     *
     */
    JSONObject cvImageAicropImg(String imgFileName, String ratios);
}


