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
 * WeappUtil
 * WeappUtil utility class
 * @author liulin 84611913@qq.com
 *
 */
public interface WeappUtil {
    /**
     * 微信小程序API接口URL
     */
    public static final String WEAPP_API_URL = "https://api.weixin.qq.com";


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
     * 获取当前微信小程序 AppID
     * @return AppID
     */
    String getAppID();

    /**
     * 设置当前微信小程序 AppID
     *
     * @param appID     当前微信小程序 AppID
     */
    void setAppID(String appID);


    /**
     * 获取当前微信小程序 appSecret
     * @return appSecret
     */
    String getAppSecret();

    /**
     * 设置当前微信小程序 appSecret
     *
     * @param appSecret     当前微信小程序 appSecret
     */
    void setAppSecret(String appSecret);

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
     * 微信小程序登录
     * 登录凭证校验。通过 wx.login 接口获得临时登录凭证 code 后传到开发者服务器调用此接口完成登录流程。更多使用方法详见 小程序登录
     * @param code       小程序端登录时获取的 code
     * @return Map<String,Object>   null -- 失败
     * 返回正确
     * {
     *     "openid": "" ,                   //用户唯一标识
     *     "session_key": "" ,              //会话密钥
     *     "unionid": "" ,                  //用户在开放平台的唯一标识符，若当前小程序已绑定到微信开放平台帐号下会返回，详见 UnionID 机制说明。
     * }
     */
    Map<String,Object> login(String code);

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
     * 检查加密信息是否由微信生成（当前只支持手机号加密数据），只能检测最近3天生成的加密数据
     * @param encrypted_msg_hash       加密数据的sha256，通过Hex（Base16）编码后的字符串
     * @return Map<String,Object>   null -- 失败
     *
     * {
     *     "vaild": "true|false" ,          //是否是合法的数据
     *     "create_time": "" ,              //加密数据生成的时间戳
     * }
     */
    Map<String,Object> checkEncryptedMsg(String encrypted_msg_hash);


    /**
     * 用户支付完成后，获取该用户的 UnionId，无需用户授权。本接口支持第三方平台代理查询
     * 调用前需要用户完成支付，且在支付后的五分钟内有效
     * @param openid                支付用户唯一标识
     * @param mch_id                微信商户ID
     * @param out_trade_no          外部订单
     * @return String   null -- 失败  返回 unionid
     */
    String getPaidUnionid(String openid, String mch_id, String out_trade_no);


    /**
     * 获取客服消息内的临时素材。即下载临时的多媒体文件。目前小程序仅支持下载图片文件
     *
     * @param media_id                  媒体文件 ID
     * @return Map<String,Object>  null -- 失败
     * {
     *    "errCode": 0,
     *    "errMsg": "openapi.customerServiceMessage.getTempMedia:ok",
     *    "contentType": "image/jpeg",
     *    "buffer": Buffer
     * }
     */
    Map<String,Object> getTempMedia(String media_id);


    /**
     * 发送文本客服消息给用户
     * @param touser                    用户的 OpenID
     * @param content                   文本消息内容
     * @return Boolean  true  -- 成功   false -- 失败
     */
    Boolean sendText(String touser, String content);

    /**
     * 发送图片客服消息给用户
     * @param touser                    用户的 OpenID
     * @param media_id                  发送的图片的媒体ID，通过 新增素材接口 上传图片文件获得
     * @return Boolean  true  -- 成功   false -- 失败
     */
    Boolean sendImage(String touser, String media_id);

    /**
     * 发送图文链接客服消息给用户
     * @param touser                    用户的 OpenID
     * @param title                     消息标题
     * @param description               图文链接消息
     * @param url                       图文链接消息被点击后跳转的链接
     * @param thumb_url                 图文链接消息的图片链接，支持 JPG、PNG 格式，较好的效果为大图 640 X 320，小图 80 X 80
     * @return Boolean  true  -- 成功   false -- 失败
     */
    Boolean sendLink(String touser, String title, String description, String url, String thumb_url);

    /**
     * 发送小程序卡片客服消息给用户
     * @param touser                    用户的 OpenID
     * @param title                     消息标题
     * @param pagepath                  小程序的页面路径，跟app.json对齐，支持参数，比如pages/index/index?foo=bar
     * @param thumb_media_id            小程序消息卡片的封面， image 类型的 media_id，通过 新增素材接口 上传图片文件获得，建议大小为 520*416
     * @return Boolean  true  -- 成功   false -- 失败
     */
    Boolean sendMiniProgramPage(String touser, String title, String pagepath, String thumb_media_id);

    /**
     * 下发客服当前输入状态给用户
     * @param touser                    用户的 OpenID
     * @param command                   命令
     *                                  Typing          --      对用户下发"正在输入"状态
     *                                  CancelTyping    --      取消对用户的"正在输入"状态
     * @return Boolean  true  -- 成功   false -- 失败
     */
    Boolean setTyping(String touser, String command);

    /**
     * 把媒体文件上传到微信服务器。目前仅支持图片。用于发送客服消息或被动回复用户消息
     * 媒体文件上传后，获取标识，3天内有效
     * @param type                      文件类型，image -- 图片
     * @param mediaFileName             服务器端本地媒体文件完全路径
     * @return String  null -- 失败  媒体文件media_id
     */
    String uploadTempMedia(String type, String mediaFileName);

    /**
     * 下发小程序和公众号统一的服务消息
     * @param touser                    用户的 OpenID
     * @param mp_template_msg           公众号模板消息相关的信息，可以参考公众号模板消息接口；有此节点并且没有weapp_template_msg节点时，发送公众号模板消息
     *                                  公众号，要求与小程序有绑定且同主体
     *    {
     *         "appid":"APPID ",                                //公众号appid，要求与小程序有绑定且同主体
     *         "template_id":"TEMPLATE_ID",                     //公众号模板id
     *         "url":"http://weixin.qq.com/download",           //公众号模板消息所要跳转的url
     *         "miniprogram":{                                  //公众号模板消息所要跳转的小程序，小程序的必须与公众号具有绑定关系
     *             "appid":"xiaochengxuappid12345",
     *             "pagepath":"index?foo=bar"
     *         },
     *         "data":{                                         //公众号模板消息的数据
     *             "first":{
     *                 "value":"恭喜你购买成功！",
     *                 "color":"#173177"
     *             },
     *             "keyword1":{
     *                 "value":"巧克力",
     *                 "color":"#173177"
     *             },
     *             "keyword2":{
     *                 "value":"39.8元",
     *                 "color":"#173177"
     *             },
     *             "keyword3":{
     *                 "value":"2014年9月22日",
     *                 "color":"#173177"
     *             },
     *             "remark":{
     *                 "value":"欢迎再次购买！",
     *                 "color":"#173177"
     *             }
     *         }
     *     }
     * @return Boolean  true  -- 成功   false -- 失败
     */
    Boolean sendTemplateMessage(String touser, JSONObject mp_template_msg);



    /**
     * 创建小程序二维码，适用于需要的码数量较少的业务场景。通过该接口生成的小程序码，永久有效，有数量限制
     * 只能生成已发布的小程序的二维码,总共生成的码数量限制为 100,000
     *
     * @param path                  扫码进入的小程序页面路径，最大长度 128 字节，不能为空；
     *                              对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"，即可在 wx.getLaunchOptionsSync 接口中的 query 参数获取到 {foo:"bar"}
     * @param width                二维码的宽度，单位 px。最小 280px，最大 1280px
     * @return Map<String,Object>  null -- 失败
     * {
     *    "errCode": 0,
     *    "errMsg": "openapi.customerServiceMessage.getTempMedia:ok",
     *    "contentType": "image/jpeg",
     *    "buffer": Buffer
     * }
     */
    Map<String,Object> createQRCode(String path, int width);

    /**
     * 获取小程序码，适用于需要的码数量较少的业务场景。通过该接口生成的小程序码，永久有效，有数量限制
     * 只能生成已发布的小程序的二维码,总共生成的码数量限制为 100,000
     *
     * @param path                  扫码进入的小程序页面路径，最大长度 128 字节，不能为空；
     *                              对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"，即可在 wx.getLaunchOptionsSync 接口中的 query 参数获取到 {foo:"bar"}
     * @param width                二维码的宽度，单位 px。最小 280px，最大 1280px
     * @param auto_color           是否自动配置线条颜色，如果颜色依然是黑色，则说明不建议配置主色调
     * @param line_color           {"r":0,"g":0,"b":0}, auto_color 为 false 时生效，使用 rgb 设置颜色 例如 {"r":"xxx","g":"xxx","b":"xxx"} 十进制表示
     * @param is_hyaline           是否需要透明底色，为 true 时，生成透明底色的小程序码
     * @return Map<String,Object>  null -- 失败
     * {
     *    "errCode": 0,
     *    "errMsg": "openapi.customerServiceMessage.getTempMedia:ok",
     *    "contentType": "image/jpeg",
     *    "buffer": Buffer
     * }
     */
    Map<String,Object> getQRCode(String path, int width, Boolean auto_color, String line_color, Boolean is_hyaline);

    /**
     * 获取小程序码，适用于需要的码数量极多的业务场景。通过该接口生成的小程序码，永久有效，数量暂无限制
     *
     * @param scene                 最大32个可见字符，只支持数字，大小写英文以及部分特殊字符：!#$&'()*+,/:;=?@-._~，其它字符请自行编码为合法字符（因不支持%，中文无法使用 urlencode 处理，请使用其他编码方式）
     * @param path                  扫码进入的小程序页面路径，最大长度 128 字节，不能为空；
     *                              对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"，即可在 wx.getLaunchOptionsSync 接口中的 query 参数获取到 {foo:"bar"}
     * @param width                二维码的宽度，单位 px。最小 280px，最大 1280px
     * @param auto_color           是否自动配置线条颜色，如果颜色依然是黑色，则说明不建议配置主色调
     * @param line_color           {"r":0,"g":0,"b":0}, auto_color 为 false 时生效，使用 rgb 设置颜色 例如 {"r":"xxx","g":"xxx","b":"xxx"} 十进制表示
     * @param is_hyaline           是否需要透明底色，为 true 时，生成透明底色的小程序码
     * @return
     * {
     *    "errCode": 0,
     *    "errMsg": "openapi.customerServiceMessage.getTempMedia:ok",
     *    "contentType": "image/jpeg",
     *    "buffer": Buffer
     * }
     */
    Map<String,Object> getUnlimitedQRCode(String scene, String path, int width, Boolean auto_color, String line_color, Boolean is_hyaline);


    /**
     * 获取小程序 scheme 码，适用于短信、邮件、外部网页、微信内等拉起小程序的业务场景。通过该接口，可以选择生成到期失效和永久有效的小程序码，有数量限制，目前仅针对国内非个人主体的小程序开放
     *
     * @param jump_wxa                      跳转到的目标小程序信息
     *        {
     *            "path": "",               //通过 scheme 码进入的小程序页面路径，必须是已经发布的小程序存在的页面，不可携带 query。path 为空时会跳转小程序主页
     *            "query": "",              //通过 scheme 码进入小程序时的 query，最大1024个字符，只支持数字，大小写英文以及部分特殊字符：!#$&'()*+,/:;=?@-._~%
     *            "env_version": "release"  //要打开的小程序版本。正式版为"release"，体验版为"trial"，开发版为"develop"，仅在微信外打开时生效
     *        }
     * @param is_expire                     生成的 scheme 码类型，到期失效：true，永久有效：false。注意，永久有效 scheme 和有效时间超过31天的到期失效 scheme 的总数上限为10万个，详见获取 URL scheme，生成 scheme 码前请仔细确认
     * @param expire_type                   到期失效的 scheme 码失效类型，失效时间：0，失效间隔天数：1
     * @param expire_time                   到期失效的 scheme 码的失效时间，为 Unix 时间戳。生成的到期失效 scheme 码在该时间前有效。最长有效期为1年。is_expire 为 true 且 expire_type 为 0 时必填
     * @param expire_interval               到期失效的 scheme 码的失效间隔天数。生成的到期失效 scheme 码在该间隔时间到达前有效。最长间隔天数为365天。is_expire 为 true 且 expire_type 为 1 时必填
     * @return Map<String,Object>  null -- 失败
     * {
     *  "errcode": 0,
     *  "errmsg": "ok",
     *  "openlink": Scheme,
     * }
     *
     */
    Map<String,Object> generateScheme(Map<String, String> jump_wxa, Boolean is_expire, int expire_type, int expire_time, int expire_interval);


    /**
     * 获取小程序 URL Link，适用于短信、邮件、网页、微信内等拉起小程序的业务场景。通过该接口，可以选择生成到期失效和永久有效的小程序链接，有数量限制，目前仅针对国内非个人主体的小程序开放
     *
     * @param path                          通过 URL Link 进入的小程序页面路径，必须是已经发布的小程序存在的页面，不可携带 query 。path 为空时会跳转小程序主页
     * @param query                         通过 URL Link 进入小程序时的query，最大1024个字符，只支持数字，大小写英文以及部分特殊字符：!#$&'()*+,/:;=?@-._~%
     * @param env_version                   要打开的小程序版本。正式版为"release"，体验版为"trial"，开发版为"develop"，仅在微信外打开时生效
     * @param is_expire                     生成的 URL Link 类型，到期失效：true，永久有效：false。注意，永久有效 Link 和有效时间超过31天的到期失效 Link 的总数上限为10万个，详见获取 URL Link，生成 Link 前请仔细确认
     * @param expire_type                   小程序 URL Link 失效类型，失效时间：0，失效间隔天数：1
     * @param expire_time                   到期失效的 URL Link 的失效时间，为 Unix 时间戳。生成的到期失效 URL Link 在该时间前有效。最长有效期为1年。expire_type 为 0 必填
     * @param expire_interval               到期失效的URL Link的失效间隔天数。生成的到期失效URL Link在该间隔时间到达前有效。最长间隔天数为365天。expire_type 为 1 必填
     * @param cloud_base                    云开发静态网站自定义 H5 配置参数，可配置中转的云开发 H5 页面。不填默认用官方 H5 页面，可为null
     *    {
     *         "env":"",                    云开发环境
     *         "domain":"",                 静态网站自定义域名，不填则使用默认域名
     *         "path":"",                   云开发静态网站 H5 页面路径，不可携带 query
     *         "query":"",                  云开发静态网站 H5 页面 query 参数，最大 1024 个字符，只支持数字，大小写英文以及部分特殊字符：!#$&'()*+,/:;=?@-._~%
     *         "resource_appid":""          第三方批量代云开发时必填，表示创建该 env 的 appid （小程序/第三方平台）
     *    }
     * @return  Map<String,Object>  null -- 失败
     *
     * {
     *  "errcode": 0,
     *  "errmsg": "ok",
     *  "url_link": "URL Link"
     * }
     */
    Map<String,Object> generateUrlLink(String path, String query, String env_version, Boolean is_expire, int expire_type, int expire_time, int expire_interval, Map<String, String> cloud_base);


    /**
     * 校验一张图片是否含有违法违规内容
     * 应用场景举例：
     *
     * 图片智能鉴黄：涉及拍照的工具类应用(如美拍，识图类应用)用户拍照上传检测；电商类商品上架图片检测；媒体类用户文章里的图片检测等；
     * 敏感人脸识别：用户头像；媒体类用户文章里的图片检测；社交类用户上传的图片检测等。 ** 频率限制：单个 appId 调用上限为 2000 次/分钟，200,000 次/天 （ 图片大小限制：1M **）
     *
     * @param mediaFileName                 本地图片完全路径
     * @return  Boolean  true  -- 内容正常   false -- 内容可能潜在风险
     */
    Boolean imgSecCheck(String mediaFileName);

    /**
     * 异步校验图片/音频是否含有违法违规内容。
     * 1.0 版本接口文档【点击查看】，1.0版本在2021年9月1日停止更新，请尽快更新至2.0
     *
     * 应用场景举例：
     * 语音风险识别：社交类用户发表的语音内容检测；
     * 图片智能鉴黄：涉及拍照的工具类应用(如美拍，识图类应用)用户拍照上传检测；电商类商品上架图片检测；媒体类用户文章里的图片检测等；
     * 敏感人脸识别：用户头像；媒体类用户文章里的图片检测；社交类用户上传的图片检测等。 频率限制：单个 appId 调用上限为 2000 次/分钟，200,000 次/天；文件大小限制：单个文件大小不超过10M
     *
     * @param media_url                 要检测的图片或音频的url，支持图片格式包括jpg, jepg, png, bmp, gif（取首帧），支持的音频格式包括mp3, aac, ac3, wma, flac, vorbis, opus, wav
     * @param media_type                1:音频;2:图片
     * @param version                   接口版本号，2.0版本为固定值2
     * @param openid                    用户的openid（用户需在近两小时访问过小程序）
     * @param scene                     场景枚举值（1 资料；2 评论；3 论坛；4 社交日志）
     * @return  String  null  -- 失败   trace_id
     */
    String mediaCheckAsync(String media_url, int media_type, int version, String openid, int scene);

    /**
     * 检查一段文本是否含有违法违规内容。
     *
     * 1.0 版本接口文档【点击查看】，1.0版本在2021年9月1日停止更新，请尽快更新至2.0
     * 应用场景举例：
     * 用户个人资料违规文字检测；
     * 媒体新闻类用户发表文章，评论内容检测；
     * 游戏类用户编辑上传的素材(如答题类小游戏用户上传的问题及答案)检测等。 频率限制：单个 appId 调用上限为 4000 次/分钟，2,000,000 次/天*
     *
     * @param version                   接口版本号，2.0版本为固定值2
     * @param openid                    用户的openid（用户需在近两小时访问过小程序）
     * @param scene                     场景枚举值（1 资料；2 评论；3 论坛；4 社交日志）
     * @param content                   需检测的文本内容，文本字数的上限为2500字，需使用UTF-8编码
     * @param nickname                  用户昵称，需使用UTF-8编码
     * @param title                     文本标题，需使用UTF-8编码
     * @param signature                 个性签名，该参数仅在资料类场景有效(scene=1)，需使用UTF-8编码
     * @return  JSONObject  null -- 失败
     *
     * {
     *    "errcode": 0,
     *    "errmsg": "ok",
     *    "result": {
     *        "suggest": "risky",               //建议，有risky、pass、review三种值
     *        "label": 20001                    //命中标签枚举值，100 正常；10001 广告；20001 时政；20002 色情；20003 辱骂；20006 违法犯罪；20008 欺诈；20012 低俗；20013 版权；21000 其他
     *    },
     *    "detail": [
     *        {
     *            "strategy": "content_model",      //策略类型
     *            "errcode": 0,                     //错误码，仅当该值为0时，该项结果有效
     *            "suggest": "risky",               //建议，有risky、pass、review三种值
     *            "label": 20006,                   //命中标签枚举值，100 正常；10001 广告；20001 时政；20002 色情；20003 辱骂；20006 违法犯罪；20008 欺诈；20012 低俗；20013 版权；21000 其他
     *            "prob": 90                        //0-100，代表置信度，越高代表越有可能属于当前返回的标签（label）
     *        },
     *        {
     *            "strategy": "keyword",
     *            "errcode": 0,
     *            "suggest": "pass",
     *            "label": 20006,
     *            "level": 20,
     *            "keyword": "命中的关键词1"
     *        },
     *        {
     *            "strategy": "keyword",
     *            "errcode": 0,
     *            "suggest": "risky",
     *            "label": 20006,
     *            "level": 90,
     *            "keyword": "命中的关键词2"
     *        }
     *    ],
     *    "trace_id": "60ae120f-371d5872-7941a05b"
     * }
     *
     */
    JSONObject msgSecCheck(int version, String openid, int scene, String content, String nickname, String title, String signature);


    /**
     * 本接口用于获得指定用户可以领取的红包封面链接
     *
     * @param openid                可领取用户的openid
     * @param ctoken                在红包封面平台获取发放ctoken（需要指定可以发放的appid）
     * @return  String null -- 失败   红包封面链接url
     */
    String getAuthenticationUrl(String openid, String ctoken);

    /**
     * 本接口提供基于小程序的条码/二维码识别的API
     * @param imgFileName               本地二维码图片地址
     * @return  JSONObject  null -- 失败
     *
     * {
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "code_results": [
     *         {
     *             "type_name": "QR_CODE",
     *             "data": "http://www.qq.com",
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
     */
    JSONObject scanQRCode(String imgFileName);


    /**
     * 本接口提供基于小程序的条码/二维码识别的API
     * @param imgUrl               要检测的图片 url，传这个则不用传 img 参数
     * @return  JSONObject  null -- 失败
     *
     * {
     *     "errcode": 0,
     *     "errmsg": "ok",
     *     "code_results": [
     *         {
     *             "type_name": "QR_CODE",
     *             "data": "http://www.qq.com",
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
     */
    JSONObject scanQRCodeUrl(String imgUrl) throws UnsupportedEncodingException;




    /**
     * 订阅通知: 从公共模板库中选用模板，添到私有模板库中
     * @param tid               模板标题 id，可通过getPubTemplateTitleList接口获取，也可登录公众号后台查看获取
     * @param kidList           开发者自行组合好的模板关键词列表，关键词顺序可以自由搭配（例如 [3,5,4] 或 [4,5,3]），最多支持5个，最少2个关键词组合
     * @param sceneDesc         服务场景描述，15个字以内
     * @return String  返回添加至帐号下的模板id，发送订阅通知时所需  null --失败
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
     * @param page                      点击模板卡片后的跳转页面，仅限本小程序内的页面。支持带参数,（示例index?foo=bar）。该字段不填则模板无跳转。
     * @param data                      模板内容，格式形如
     *                                      {
     *                                          "key1": {"value": any },
     *                                          "key2": { "value": any }
     *                                          ,...
     *                                       }
     * @param miniprogram_state         跳转小程序类型：developer为开发版；trial为体验版；formal为正式版；默认为正式版
     * @param lang                      进入小程序查看”的语言类型，支持zh_CN(简体中文)、en_US(英文)、zh_HK(繁体中文)、zh_TW(繁体中文)，默认为zh_CN
     * @return  true -- 成功  false --  失败
     */
    Boolean sendNewtmplSubscribe(String touser, String template_id, String page, Map<String, Map<String, Object>> data, String miniprogram_state, String lang);


}


