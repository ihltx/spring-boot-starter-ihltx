package com.ihltx.utility.weixin;

import com.ihltx.utility.httpclient.service.RestTemplateUtil;
import com.ihltx.utility.redis.service.RedisFactory;
import com.ihltx.utility.util.DateUtil;
import com.ihltx.utility.util.StringUtil;
import com.ihltx.utility.weixin.entity.*;
import com.ihltx.utility.weixin.event.MessageEvent;
import com.ihltx.utility.weixin.pay.WXPayConstants;
import com.ihltx.utility.weixin.pay.WXPayUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WeixinUtil
 * WeixinUtil utility class
 * @author liulin 84611913@qq.com
 *
 */
public interface WeixinPayUtil {
    /**
     * 微信商户API接口URL
     */
    public static final String WEIXIN_PAY_API_URL = "https://api.mch.weixin.qq.com";


    void setApplicationContext(ApplicationContext applicationContext);

    ApplicationContext getApplicationContext();

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
     * 设置 mchId 微信支付分配的商户号
     *
     * @param mchId      微信支付分配的商户号
     */
    void setMchId(String mchId);


    /**
     * 返回mchId 微信支付分配的商户号
     * @return  String
     */
    String getMchId();



    /**
     * 设置微信支付分配的API 密钥 apiKey
     *
     * @param apiKey      微信支付分配的API密钥
     */
    void setApiKey(String apiKey);


    /**
     * 返回微信支付分配的API 密钥 apiKey
     * @return  String
     */
    String getApiKey();


    /**
     * 设置微信商户apiclient_cert.p12证书文件物理全路径名
     *
     * @param certFileName       微信商户apiclient_cert.p12证书文件物理全路径名
     */
    void setCertFileName(String certFileName);


    /**
     * 返回 微信商户apiclient_cert.p12证书文件物理全路径名
     * @return  String
     */
    String getCertFileName();

    /**
     * 设置微信商户签名方式
     *
     * @param signType       微信商户签名方式
     */
    void setSignType(WXPayConstants.SignType signType);


    /**
     * 返回 微信商户签名方式
     * @return  WXPayConstants.SignType
     */
    WXPayConstants.SignType getSignType();

    /**
     * jsApi微信支付统一下单,需要在微信商户开发配置中配置JSAPI支付授权目录(最多5个)，以及在公众号中配置:JS接口安全域名/业务域名/网页授权域名
     * 说明：在微信服务器端，只要out_trade_no与body完成相同，则会认为是同一订单，因此如果订单发生了改价行为，需要修改out_trade_no订单号并重新发起统一下单。
     *      所有参数将自动转ISO-8859-1字符集
     * @param requestData               请求数据
     *     {
     *           openid                    trade_type=JSAPI时（即JSAPI支付），此参数必传，此参数为微信用户在商户对应appid下的唯一标识。
     *                                     openid如何获取，可参考【获取openid】。
     *                                     企业号请使用【企业号OAuth2.0接口】获取企业号内成员userid，再调用【企业号userid转openid接口】进行转换
     *           body               必须    商品简单描述，该字段请按照规范传递，String(128)
     *           out_trade_no       必须    商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|* 且在同一个商户号下唯一,String(32)
     *           total_fee          必须    订单总金额，单位为分,int
     *           spbill_create_ip   必须    支持IPV4和IPV6两种格式的IP地址。用户的客户端IP,String(64)
     *           notify_url         必须    body 异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。 公网域名必须为https，如果是走专线接入，使用专线NAT IP或者私有回调域名可使用http
     *           trade_type         必须    交易类型固定为： JSAPI -JSAPI支付
     *           device_info               设备号,自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB" String(32)
     *           detail                    商品详细描述，对于使用单品优惠的商户，该字段必须按照规范上传，详见“单品优惠参数说明”
     *           attach                    附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用。	String(127)
     *           sign_type                 签名类型，默认为MD5，支持HMAC-SHA256和MD5。String(32)
     *           fee_type                  符合ISO 4217标准的三位字母代码，默认人民币：CNY  String(16)
     *           time_start                交易起始时间, 订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。String(14)
     *           time_expire               交易结束时间, 订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010。订单失效时间是针对订单号而言的，由于在请求支付的时候有一个必传参数prepay_id只有两小时的有效期，所以在重入时间超过2小时的时候需要重新请求下单接口获取新的prepay_id。其他详见时间规则
     *                                    time_expire只能第一次下单传值，不允许二次修改，二次修改系统将报错。如用户支付失败后，需再次支付，需更换原订单号重新下单。String(14)
     *                                    建议：最短失效时间间隔大于1分钟
     *           goods_tag                 订单优惠标记，使用代金券或立减优惠功能时需要的参数，说明详见代金券或立减优惠 String(32)
     *           product_id                商品ID,trade_type=NATIVE时，此参数必传。此参数为二维码中包含的商品ID，商户自行定义。String(32)
     *           limit_pay                 指定支付方式,上传此参数no_credit--可限制用户不能使用信用卡支付  String(32)
     *           receipt                   电子发票入口开放标识,Y，传入Y时，支付成功消息和支付详情页将出现开票入口。需要在微信支付商户平台或微信公众平台开通电子发票功能，传此字段才可生效 String(8)
     *           profit_sharing            是否需要分账, Y-是，需要分账  N-否，不分账, 字母要求大写，不传默认不分账 String(16)
     *           scene_info                场景信息, 该字段常用于线下活动时的场景信息上报，支持上报实际门店信息，商户也可以按需求自己上报相关信息。该字段为JSON对象数据  String(256)
     *                                           对象格式为：{"store_info":{"id": "门店ID","name": "名称","area_code": "编码","address": "地址" }}，说明如下：
     *                                           -门店id	        id	        是	String(32)	SZTX001	门店编号，由商户自定义
     *                                           -门店名称	    name	    否	String(64)	腾讯大厦腾大餐厅	门店名称 ，由商户自定义
     *                                           -门店行政区划码	area_code	否	String(6)	440305	门店所在地行政区划码，详细见《最新县及县以上行政区划代码》
     *                                           -门店详细地址	    address	    否	String(128)	科技园中一路腾讯大厦	门店详细地址 ，由商户自定义
     *           appid             必须    不需要设置，微信支付分配的公众账号ID（企业号corpid即为此appid），自动生成
     *           nonce_str         必须    不需要设置，随机字符串，长度要求在32位以内，自动生成
     *           sign              必须    不需要设置，签名，根据签名规则自动生成
     *     }
     * @return Map<String,Object>  null -- 失败
     * @throws Exception
     * {
     *     "return_code": "SUCCESS",                                            //SUCCESS/FAIL,此字段是通信标识，非交易标识，交易是否成功需要查看result_code来判断
     *     "return_msg": "OK",                                                  //当return_code为FAIL时返回信息为错误原因 ，例如 签名失败 参数格式校验错误
     *     "appid": "wx2421b1c4370ec43b",                                       //调用接口提交的公众账号ID
     *     "mch_id": "10000100",                                                //调用接口提交的商户号
     *     "nonce_str": "IITRi8Iabbblz1Jc",                                     //微信返回的随机字符串
     *     "openid": "oUpF8uMuAJO_M2pxb1Q9zNjWeS6o",                            //微信用户openid
     *     "sign": "7921E432F65EB8ED0CE9755F0E86D72F",                          //微信返回的签名值
     *     "result_code": "SUCCESS",                                            //业务结果 SUCCESS/FAIL
     *     "prepay_id": "wx201411101639507cbf6ffd8b0779950874",                 //预支付交易会话标识 微信生成的预支付会话标识，用于后续接口调用中使用，该值有效期为2小时
     *     "trade_type": "JSAPI",                                               //交易类型 JSAPI -JSAPI支付, NATIVE -Native支付, APP -APP支付
     *     "timestamp": "timestamp",                                            //timestamp
     *     "code_url": "weixin://wxpay/bizpayurl/up?pr=NwY5Mz9&groupid=00"      //二维码链接 trade_type=NATIVE时有返回，此url用于生成支付二维码，然后提供给用户进行扫码支付。
     * }
     * 错误码
     * 名称	                    描述                          原因	                        解决方案
     * INVALID_REQUEST	        参数错误	                    参数格式有误或者未按规则上传	        订单重入时，要求参数值与原请求一致，请确认参数问题
     * NOAUTH	                商户无此接口权限	            商户未开通此接口权限	            请商户前往申请此接口权限
     * NOTENOUGH	            余额不足	                    用户账号余额不足	                用户账号余额不足，请用户充值或更换支付卡后再支付
     * ORDERPAID	            商户订单已支付	                商户订单已支付，无需重复操作	        商户订单已支付，无需更多操作
     * ORDERCLOSED	            订单已关闭	                当前订单已关闭，无法支付	        当前订单已关闭，请重新下单
     * SYSTEMERROR	            系统错误	                    系统超时	                        系统异常，请用相同参数重新调用
     * APPID_NOT_EXIST	        APPID不存在	                参数中缺少APPID	                请检查APPID是否正确
     * MCHID_NOT_EXIST	        MCHID不存在	                参数中缺少MCHID	                请检查MCHID是否正确
     * APPID_MCHID_NOT_MATCH	appid和mch_id不匹配	        appid和mch_id不匹配	            请确认appid和mch_id是否匹配
     * LACK_PARAMS	            缺少参数	                    缺少必要的请求参数	                请检查参数是否齐全
     * OUT_TRADE_NO_USED	    商户订单号重复	                同一笔交易不能多次提交	            请核实商户订单号是否重复提交
     * SIGNERROR	            签名错误	                    参数签名结果不正确	                请检查签名参数和方法是否都符合签名算法要求
     * XML_FORMAT_ERROR	        XML格式错误	                XML格式错误	                    请检查XML参数格式是否正确
     * REQUIRE_POST_METHOD	    请使用post方法	            未使用post传递参数 	            请检查请求参数是否通过post方法提交
     * POST_DATA_EMPTY	        post数据为空	                post数据不能为空	                请检查post数据是否为空
     * NOT_UTF8	                编码格式错误	                未使用指定编码格式	                请使用UTF-8编码格式
     */
    Map<String,String> jsApiPayUnifiedOrder(Map<String, String> requestData) throws Exception;

    /**
     * PC 微信支付统一下单，将直接向当前web上下文response输出二维码图片,需要在微信商户开发配置中配置Native支付回调链接
     * 说明：在微信服务器端，只要out_trade_no与body完成相同，则会认为是同一订单，因此如果订单发生了改价行为，需要修改out_trade_no订单号并重新发起统一下单。
     * @param requestData               请求数据
     *     {
     *           openid                    trade_type=JSAPI时（即JSAPI支付），此参数必传，此参数为微信用户在商户对应appid下的唯一标识。
     *                                     openid如何获取，可参考【获取openid】。
     *                                     企业号请使用【企业号OAuth2.0接口】获取企业号内成员userid，再调用【企业号userid转openid接口】进行转换
     *           body               必须    商品简单描述，该字段请按照规范传递，String(128)
     *           out_trade_no       必须    商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|* 且在同一个商户号下唯一,String(32)
     *           total_fee          必须    订单总金额，单位为分,int
     *           spbill_create_ip   必须    支持IPV4和IPV6两种格式的IP地址。用户的客户端IP,String(64)
     *           notify_url         必须    body 异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。 公网域名必须为https，如果是走专线接入，使用专线NAT IP或者私有回调域名可使用http
     *           trade_type         必须    交易类型固定为： NATIVE -Native支付
     *           device_info               设备号,自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB" String(32)
     *           detail                    商品详细描述，对于使用单品优惠的商户，该字段必须按照规范上传，详见“单品优惠参数说明”
     *           attach                    附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用。	String(127)
     *           sign_type                 签名类型，默认为MD5，支持HMAC-SHA256和MD5。String(32)
     *           fee_type                  符合ISO 4217标准的三位字母代码，默认人民币：CNY  String(16)
     *           time_start                交易起始时间, 订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。String(14)
     *           time_expire               交易结束时间, 订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010。订单失效时间是针对订单号而言的，由于在请求支付的时候有一个必传参数prepay_id只有两小时的有效期，所以在重入时间超过2小时的时候需要重新请求下单接口获取新的prepay_id。其他详见时间规则
     *                                    time_expire只能第一次下单传值，不允许二次修改，二次修改系统将报错。如用户支付失败后，需再次支付，需更换原订单号重新下单。String(14)
     *                                    建议：最短失效时间间隔大于1分钟
     *           goods_tag                 订单优惠标记，使用代金券或立减优惠功能时需要的参数，说明详见代金券或立减优惠 String(32)
     *           product_id                商品ID,trade_type=NATIVE时，此参数必传。此参数为二维码中包含的商品ID，商户自行定义。String(32)
     *           limit_pay                 指定支付方式,上传此参数no_credit--可限制用户不能使用信用卡支付  String(32)
     *           receipt                   电子发票入口开放标识,Y，传入Y时，支付成功消息和支付详情页将出现开票入口。需要在微信支付商户平台或微信公众平台开通电子发票功能，传此字段才可生效 String(8)
     *           profit_sharing            是否需要分账, Y-是，需要分账  N-否，不分账, 字母要求大写，不传默认不分账 String(16)
     *           scene_info                场景信息, 该字段常用于线下活动时的场景信息上报，支持上报实际门店信息，商户也可以按需求自己上报相关信息。该字段为JSON对象数据  String(256)
     *                                           对象格式为：{"store_info":{"id": "门店ID","name": "名称","area_code": "编码","address": "地址" }}，说明如下：
     *                                           -门店id	        id	        是	String(32)	SZTX001	门店编号，由商户自定义
     *                                           -门店名称	    name	    否	String(64)	腾讯大厦腾大餐厅	门店名称 ，由商户自定义
     *                                           -门店行政区划码	area_code	否	String(6)	440305	门店所在地行政区划码，详细见《最新县及县以上行政区划代码》
     *                                           -门店详细地址	    address	    否	String(128)	科技园中一路腾讯大厦	门店详细地址 ，由商户自定义
     *           appid             必须    不需要设置，微信支付分配的公众账号ID（企业号corpid即为此appid），自动生成
     *           nonce_str         必须    不需要设置，随机字符串，长度要求在32位以内，自动生成
     *           sign              必须    不需要设置，签名，根据签名规则自动生成
     *     }

     * @throws Exception
     * {
     *     "return_code": "SUCCESS",                                            //SUCCESS/FAIL,此字段是通信标识，非交易标识，交易是否成功需要查看result_code来判断
     *     "return_msg": "OK",                                                  //当return_code为FAIL时返回信息为错误原因 ，例如 签名失败 参数格式校验错误
     *     "appid": "wx2421b1c4370ec43b",                                       //调用接口提交的公众账号ID
     *     "mch_id": "10000100",                                                //调用接口提交的商户号
     *     "nonce_str": "IITRi8Iabbblz1Jc",                                     //微信返回的随机字符串
     *     "openid": "oUpF8uMuAJO_M2pxb1Q9zNjWeS6o",                            //微信用户openid
     *     "sign": "7921E432F65EB8ED0CE9755F0E86D72F",                          //微信返回的签名值
     *     "result_code": "SUCCESS",                                            //业务结果 SUCCESS/FAIL
     *     "prepay_id": "wx201411101639507cbf6ffd8b0779950874",                 //预支付交易会话标识 微信生成的预支付会话标识，用于后续接口调用中使用，该值有效期为2小时
     *     "trade_type": "JSAPI",                                               //交易类型 JSAPI -JSAPI支付, NATIVE -Native支付, APP -APP支付
     *     "timestamp": "timestamp",                                            //timestamp
     *     "code_url": "weixin://wxpay/bizpayurl/up?pr=NwY5Mz9&groupid=00"      //二维码链接 trade_type=NATIVE时有返回，此url用于生成支付二维码，然后提供给用户进行扫码支付。
     * }
     * 错误码
     * 名称	                    描述                          原因	                        解决方案
     * INVALID_REQUEST	        参数错误	                    参数格式有误或者未按规则上传	        订单重入时，要求参数值与原请求一致，请确认参数问题
     * NOAUTH	                商户无此接口权限	            商户未开通此接口权限	            请商户前往申请此接口权限
     * NOTENOUGH	            余额不足	                    用户账号余额不足	                用户账号余额不足，请用户充值或更换支付卡后再支付
     * ORDERPAID	            商户订单已支付	                商户订单已支付，无需重复操作	        商户订单已支付，无需更多操作
     * ORDERCLOSED	            订单已关闭	                当前订单已关闭，无法支付	        当前订单已关闭，请重新下单
     * SYSTEMERROR	            系统错误	                    系统超时	                        系统异常，请用相同参数重新调用
     * APPID_NOT_EXIST	        APPID不存在	                参数中缺少APPID	                请检查APPID是否正确
     * MCHID_NOT_EXIST	        MCHID不存在	                参数中缺少MCHID	                请检查MCHID是否正确
     * APPID_MCHID_NOT_MATCH	appid和mch_id不匹配	        appid和mch_id不匹配	            请确认appid和mch_id是否匹配
     * LACK_PARAMS	            缺少参数	                    缺少必要的请求参数	                请检查参数是否齐全
     * OUT_TRADE_NO_USED	    商户订单号重复	                同一笔交易不能多次提交	            请核实商户订单号是否重复提交
     * SIGNERROR	            签名错误	                    参数签名结果不正确	                请检查签名参数和方法是否都符合签名算法要求
     * XML_FORMAT_ERROR	        XML格式错误	                XML格式错误	                    请检查XML参数格式是否正确
     * REQUIRE_POST_METHOD	    请使用post方法	            未使用post传递参数 	            请检查请求参数是否通过post方法提交
     * POST_DATA_EMPTY	        post数据为空	                post数据不能为空	                请检查post数据是否为空
     * NOT_UTF8	                编码格式错误	                未使用指定编码格式	                请使用UTF-8编码格式
     */
    void pcPayUnifiedOrder(Map<String, String> requestData) throws Exception;

    /**
     * PC 微信支付统一下单，将直接向指定response输出二维码图片,需要在微信商户开发配置中配置Native支付回调链接
     * 说明：在微信服务器端，只要out_trade_no与body完成相同，则会认为是同一订单，因此如果订单发生了改价行为，需要修改out_trade_no订单号并重新发起统一下单。
     * @param requestData               请求数据
     *     {
     *           openid                    trade_type=JSAPI时（即JSAPI支付），此参数必传，此参数为微信用户在商户对应appid下的唯一标识。
     *                                     openid如何获取，可参考【获取openid】。
     *                                     企业号请使用【企业号OAuth2.0接口】获取企业号内成员userid，再调用【企业号userid转openid接口】进行转换
     *           body               必须    商品简单描述，该字段请按照规范传递，String(128)
     *           out_trade_no       必须    商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|* 且在同一个商户号下唯一,String(32)
     *           total_fee          必须    订单总金额，单位为分,int
     *           spbill_create_ip   必须    支持IPV4和IPV6两种格式的IP地址。用户的客户端IP,String(64)
     *           notify_url         必须    body 异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。 公网域名必须为https，如果是走专线接入，使用专线NAT IP或者私有回调域名可使用http
     *           trade_type         必须    交易类型固定为： NATIVE -Native支付
     *           device_info               设备号,自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB" String(32)
     *           detail                    商品详细描述，对于使用单品优惠的商户，该字段必须按照规范上传，详见“单品优惠参数说明”
     *           attach                    附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用。	String(127)
     *           sign_type                 签名类型，默认为MD5，支持HMAC-SHA256和MD5。String(32)
     *           fee_type                  符合ISO 4217标准的三位字母代码，默认人民币：CNY  String(16)
     *           time_start                交易起始时间, 订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。String(14)
     *           time_expire               交易结束时间, 订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010。订单失效时间是针对订单号而言的，由于在请求支付的时候有一个必传参数prepay_id只有两小时的有效期，所以在重入时间超过2小时的时候需要重新请求下单接口获取新的prepay_id。其他详见时间规则
     *                                    time_expire只能第一次下单传值，不允许二次修改，二次修改系统将报错。如用户支付失败后，需再次支付，需更换原订单号重新下单。String(14)
     *                                    建议：最短失效时间间隔大于1分钟
     *           goods_tag                 订单优惠标记，使用代金券或立减优惠功能时需要的参数，说明详见代金券或立减优惠 String(32)
     *           product_id                商品ID,trade_type=NATIVE时，此参数必传。此参数为二维码中包含的商品ID，商户自行定义。String(32)
     *           limit_pay                 指定支付方式,上传此参数no_credit--可限制用户不能使用信用卡支付  String(32)
     *           receipt                   电子发票入口开放标识,Y，传入Y时，支付成功消息和支付详情页将出现开票入口。需要在微信支付商户平台或微信公众平台开通电子发票功能，传此字段才可生效 String(8)
     *           profit_sharing            是否需要分账, Y-是，需要分账  N-否，不分账, 字母要求大写，不传默认不分账 String(16)
     *           scene_info                场景信息, 该字段常用于线下活动时的场景信息上报，支持上报实际门店信息，商户也可以按需求自己上报相关信息。该字段为JSON对象数据  String(256)
     *                                           对象格式为：{"store_info":{"id": "门店ID","name": "名称","area_code": "编码","address": "地址" }}，说明如下：
     *                                           -门店id	        id	        是	String(32)	SZTX001	门店编号，由商户自定义
     *                                           -门店名称	    name	    否	String(64)	腾讯大厦腾大餐厅	门店名称 ，由商户自定义
     *                                           -门店行政区划码	area_code	否	String(6)	440305	门店所在地行政区划码，详细见《最新县及县以上行政区划代码》
     *                                           -门店详细地址	    address	    否	String(128)	科技园中一路腾讯大厦	门店详细地址 ，由商户自定义
     *           appid             必须    不需要设置，微信支付分配的公众账号ID（企业号corpid即为此appid），自动生成
     *           nonce_str         必须    不需要设置，随机字符串，长度要求在32位以内，自动生成
     *           sign              必须    不需要设置，签名，根据签名规则自动生成
     *     }
     * @param response                  response响应对象

     * @throws Exception
     */
    void pcPayUnifiedOrder(Map<String, String> requestData, HttpServletResponse response) throws Exception;

    /**
     * H5 微信支付统一下单,需要在微信商户开发配置中配置H5支付域名
     * 说明：在微信服务器端，只要out_trade_no与body完成相同，则会认为是同一订单，因此如果订单发生了改价行为，需要修改out_trade_no订单号并重新发起统一下单。
     * @param redirect_url              支付成功的回调地址
     * @param requestData               请求数据
     *     {
     *           openid                    trade_type=JSAPI时（即JSAPI支付），此参数必传，此参数为微信用户在商户对应appid下的唯一标识。
     *                                     openid如何获取，可参考【获取openid】。
     *                                     企业号请使用【企业号OAuth2.0接口】获取企业号内成员userid，再调用【企业号userid转openid接口】进行转换
     *           body               必须    商品简单描述，该字段请按照规范传递，String(128)
     *           out_trade_no       必须    商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|* 且在同一个商户号下唯一,String(32)
     *           total_fee          必须    订单总金额，单位为分,int
     *           spbill_create_ip   必须    支持IPV4和IPV6两种格式的IP地址。用户的客户端IP,String(64)
     *           notify_url         必须    body 异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。 公网域名必须为https，如果是走专线接入，使用专线NAT IP或者私有回调域名可使用http
     *           trade_type         必须    交易类型固定为： NATIVE -Native支付
     *           device_info               设备号,自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB" String(32)
     *           detail                    商品详细描述，对于使用单品优惠的商户，该字段必须按照规范上传，详见“单品优惠参数说明”
     *           attach                    附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用。	String(127)
     *           sign_type                 签名类型，默认为MD5，支持HMAC-SHA256和MD5。String(32)
     *           fee_type                  符合ISO 4217标准的三位字母代码，默认人民币：CNY  String(16)
     *           time_start                交易起始时间, 订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。String(14)
     *           time_expire               交易结束时间, 订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010。订单失效时间是针对订单号而言的，由于在请求支付的时候有一个必传参数prepay_id只有两小时的有效期，所以在重入时间超过2小时的时候需要重新请求下单接口获取新的prepay_id。其他详见时间规则
     *                                    time_expire只能第一次下单传值，不允许二次修改，二次修改系统将报错。如用户支付失败后，需再次支付，需更换原订单号重新下单。String(14)
     *                                    建议：最短失效时间间隔大于1分钟
     *           goods_tag                 订单优惠标记，使用代金券或立减优惠功能时需要的参数，说明详见代金券或立减优惠 String(32)
     *           product_id                商品ID,trade_type=NATIVE时，此参数必传。此参数为二维码中包含的商品ID，商户自行定义。String(32)
     *           limit_pay                 指定支付方式,上传此参数no_credit--可限制用户不能使用信用卡支付  String(32)
     *           receipt                   电子发票入口开放标识,Y，传入Y时，支付成功消息和支付详情页将出现开票入口。需要在微信支付商户平台或微信公众平台开通电子发票功能，传此字段才可生效 String(8)
     *           profit_sharing            是否需要分账, Y-是，需要分账  N-否，不分账, 字母要求大写，不传默认不分账 String(16)
     *           scene_info                场景信息, 该字段常用于线下活动时的场景信息上报，支持上报实际门店信息，商户也可以按需求自己上报相关信息。该字段为JSON对象数据  String(256)
     *                                           对象格式为：{"store_info":{"id": "门店ID","name": "名称","area_code": "编码","address": "地址" }}，说明如下：
     *                                           -门店id	        id	        是	String(32)	SZTX001	门店编号，由商户自定义
     *                                           -门店名称	    name	    否	String(64)	腾讯大厦腾大餐厅	门店名称 ，由商户自定义
     *                                           -门店行政区划码	area_code	否	String(6)	440305	门店所在地行政区划码，详细见《最新县及县以上行政区划代码》
     *                                           -门店详细地址	    address	    否	String(128)	科技园中一路腾讯大厦	门店详细地址 ，由商户自定义
     *           appid             必须    不需要设置，微信支付分配的公众账号ID（企业号corpid即为此appid），自动生成
     *           nonce_str         必须    不需要设置，随机字符串，长度要求在32位以内，自动生成
     *           sign              必须    不需要设置，签名，根据签名规则自动生成
     *     }
     # @return Map<String , String> null  -- 失败
     * @throws Exception
     * {
     *     "return_code": "SUCCESS",                                            //SUCCESS/FAIL,此字段是通信标识，非交易标识，交易是否成功需要查看result_code来判断
     *     "return_msg": "OK",                                                  //当return_code为FAIL时返回信息为错误原因 ，例如 签名失败 参数格式校验错误
     *     "appid": "wx2421b1c4370ec43b",                                       //调用接口提交的公众账号ID
     *     "mch_id": "10000100",                                                //调用接口提交的商户号
     *     "nonce_str": "IITRi8Iabbblz1Jc",                                     //微信返回的随机字符串
     *     "openid": "oUpF8uMuAJO_M2pxb1Q9zNjWeS6o",                            //微信用户openid
     *     "sign": "7921E432F65EB8ED0CE9755F0E86D72F",                          //微信返回的签名值
     *     "result_code": "SUCCESS",                                            //业务结果 SUCCESS/FAIL
     *     "prepay_id": "wx201411101639507cbf6ffd8b0779950874",                 //预支付交易会话标识 微信生成的预支付会话标识，用于后续接口调用中使用，该值有效期为2小时
     *     "trade_type": "JSAPI",                                               //交易类型 JSAPI -JSAPI支付, NATIVE -Native支付, APP -APP支付
     *     "timestamp": "timestamp",                                            //timestamp
     *     "mweb_url": "https://wx.tenpay.com/cgi-bin/mmpayweb-bin/checkmweb?prepay_id=wx22125216628202ad2c20154f7fd9990000&package=1525768300",                  //h5微信支付直接跳转url
     * }
     * 错误码
     * 名称	                    描述                          原因	                        解决方案
     * INVALID_REQUEST	        参数错误	                    参数格式有误或者未按规则上传	        订单重入时，要求参数值与原请求一致，请确认参数问题
     * NOAUTH	                商户无此接口权限	            商户未开通此接口权限	            请商户前往申请此接口权限
     * NOTENOUGH	            余额不足	                    用户账号余额不足	                用户账号余额不足，请用户充值或更换支付卡后再支付
     * ORDERPAID	            商户订单已支付	                商户订单已支付，无需重复操作	        商户订单已支付，无需更多操作
     * ORDERCLOSED	            订单已关闭	                当前订单已关闭，无法支付	        当前订单已关闭，请重新下单
     * SYSTEMERROR	            系统错误	                    系统超时	                        系统异常，请用相同参数重新调用
     * APPID_NOT_EXIST	        APPID不存在	                参数中缺少APPID	                请检查APPID是否正确
     * MCHID_NOT_EXIST	        MCHID不存在	                参数中缺少MCHID	                请检查MCHID是否正确
     * APPID_MCHID_NOT_MATCH	appid和mch_id不匹配	        appid和mch_id不匹配	            请确认appid和mch_id是否匹配
     * LACK_PARAMS	            缺少参数	                    缺少必要的请求参数	                请检查参数是否齐全
     * OUT_TRADE_NO_USED	    商户订单号重复	                同一笔交易不能多次提交	            请核实商户订单号是否重复提交
     * SIGNERROR	            签名错误	                    参数签名结果不正确	                请检查签名参数和方法是否都符合签名算法要求
     * XML_FORMAT_ERROR	        XML格式错误	                XML格式错误	                    请检查XML参数格式是否正确
     * REQUIRE_POST_METHOD	    请使用post方法	            未使用post传递参数 	            请检查请求参数是否通过post方法提交
     * POST_DATA_EMPTY	        post数据为空	                post数据不能为空	                请检查post数据是否为空
     * NOT_UTF8	                编码格式错误	                未使用指定编码格式	                请使用UTF-8编码格式
     */
    Map<String , String> h5PayUnifiedOrder(Map<String, String> requestData, String redirect_url) throws Exception;

    /**
     * 获取指定request上下文请求中异步支付通知数据
     * @param request           request
     * @return  Map<String, String>  null -- 失败
     */
    Map<String, String> getNotifyData(HttpServletRequest request) throws Exception;


    /**
     * 获取当前request上下文请求中异步支付通知数据
     * @return  Map<String, String>  null -- 失败
     */
    Map<String, String> getNotifyData() throws Exception;

    /**
     * 查询商户订单状态
     * 交易成功判断条件： return_code、result_code和trade_state都为SUCCESS
     *
     * @param out_trade_no              外部交易订单号
     * @return Map<String, String>  null -- 失败
     * @throws Exception
     * {
     *     "return_code": "SUCCESS",                                            //SUCCESS/FAIL,此字段是通信标识，非交易标识，交易是否成功需要查看result_code来判断
     *     "return_msg": "OK",                                                  //当return_code为FAIL时返回信息为错误原因 ，例如 签名失败 参数格式校验错误
     *     "appid": "wx2421b1c4370ec43b",                                       //调用接口提交的公众账号ID
     *     "mch_id": "10000100",                                                //调用接口提交的商户号
     *     "nonce_str": "IITRi8Iabbblz1Jc",                                     //微信返回的随机字符串
     *     "sign": "7921E432F65EB8ED0CE9755F0E86D72F",                          //微信返回的签名值
     *     "result_code": "SUCCESS",                                            //业务结果 SUCCESS/FAIL
     *     "err_code": "SYSTEMERROR",                                           //当result_code为FAIL时返回错误代码，详细参见下文错误列表
     *     "err_code_des": "系统错误",                                            //当result_code为FAIL时返回错误描述，详细参见下文错误列表
     *     "device_info": "013467007045764",                                    //微信支付分配的终端设备号
     *     "openid": "oUpF8uMuAJO_M2pxb1Q9zNjWeS6o",                            //用户在商户appid下的唯一标识
     *     "is_subscribe": "Y",                                                 //用户是否关注公众账号，Y-关注，N-未关注
     *     "trade_type": "JSAPI",                                               //调用接口提交的交易类型，取值如下：
     *                                                                              SAPI--JSAPI支付（或小程序支付）、NATIVE--Native支付、APP--app支付，MWEB--H5支付，不同trade_type决定了调起支付的方式，请根据支付产品正确上传
     *                                                                              MICROPAY--付款码支付，付款码支付有单独的支付接口，所以接口不需要上传，该字段在对账单中会出现
     *     "trade_state": "SUCCESS",                                            //交易状态	SUCCESS--支付成功  REFUND--转入退款 NOTPAY--未支付 CLOSED--已关闭 REVOKED--已撤销(刷卡支付)
     *                                                                                      USERPAYING--用户支付中  PAYERROR--支付失败(其他原因，如银行返回失败) ACCEPT--已接收，等待扣款
     *     "bank_type": "CMC",                                                  //银行类型，采用字符串类型的银行标识
     *     "total_fee": "100",                                                  //订单总金额，单位为分
     *     "settlement_total_fee": "100",                                       //应结订单金 当订单使用了免充值型优惠券后返回该参数，应结订单金额=订单金额-免充值优惠券金额。
     *     "fee_type": "CNY",                                                   //货币类型，符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见  https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=4_2
     *     "cash_fee": "100",                                                   //现金支付金额，现金支付金额订单现金支付金额，详见 https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=4_2
     *     "coupon_fee": "100",                                                 //代金券金额	 “代金券”金额<=订单金额，订单金额-“代金券”金额=现金支付金额， https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=4_2
     *     "coupon_count": "1",                                                 //代金券使用数量
     *     "coupon_type_$n": "CASH",                                            //代金券类型  CASH：充值代金券 NO_CASH：非充值优惠券  开通免充值券功能，并且订单使用了优惠券后有返回（取值：CASH、NO_CASH）。$n为下标,从0开始编号，举例：coupon_type_$0
     *     "coupon_id_$n": "10000",                                             //代金券ID, $n为下标，从0开始编号
     *     "coupon_fee_$n": "100",                                              //单个代金券支付金额, $n为下标，从0开始编号
     *     "transaction_id": "1009660380201506130728806387",                    //微信支付订单号
     *     "out_trade_no": "20150806125346",                                    //商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。
     *     "attach": "深圳分店",                                                  //附加数据，原样返回
     *     "time_end": "20141030133525",                                        //订单支付时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。
     *     "trade_state_desc": "支付失败，请重新下单支付"                          //交易状态描述， 对当前查询订单状态的描述和下一步操作的指引
     * }
     *
     * 错误码
     * 名称	                            描述	                        原因	                            解决方案
     * ORDERNOTEXIST	                此交易订单号不存在	            查询系统中不存在此交易订单号	        该API只能查提交支付交易返回成功的订单，请商户检查需要查询的订单号是否正确
     * SYSTEMERROR	                    系统错误	                    后台系统返回错误	                系统异常，请再调用发起查询
     */
    Map<String, String> orderQuery(String out_trade_no) throws Exception;

    /**
     * 关闭商户订单
     * 关闭商户订单成功判断条件： return_code、result_code都为SUCCESS
     *
     * @param out_trade_no              外部交易订单号
     * @return Map<String, String>  null -- 失败
     * @throws Exception
     * {
     *     "return_code": "SUCCESS",                                            //SUCCESS/FAIL,此字段是通信标识，非交易标识，交易是否成功需要查看result_code来判断
     *     "return_msg": "OK",                                                  //当return_code为FAIL时返回信息为错误原因 ，例如 签名失败 参数格式校验错误
     *     "appid": "wx2421b1c4370ec43b",                                       //调用接口提交的公众账号ID
     *     "mch_id": "10000100",                                                //调用接口提交的商户号
     *     "nonce_str": "IITRi8Iabbblz1Jc",                                     //微信返回的随机字符串
     *     "sign": "7921E432F65EB8ED0CE9755F0E86D72F",                          //微信返回的签名值
     *     "result_code": "SUCCESS",                                            //业务结果 SUCCESS/FAIL
     *     "result_msg": "OK",                                                  //对业务结果的补充说明
     *     "err_code": "SYSTEMERROR",                                           //当result_code为FAIL时返回错误代码，详细参见下文错误列表
     *     "err_code_des": "系统错误"                                            //当result_code为FAIL时返回错误描述，详细参见下文错误列表
     * }
     *
     * 错误码
     * 名称	                            描述	                        原因	                            解决方案
     * ORDERPAID	                    订单已支付	                订单已支付，不能发起关单	        订单已支付，不能发起关单，请当作已支付的正常交易
     * SYSTEMERROR	                    系统错误	                    系统错误	                        系统异常，请重新调用该API
     * ORDERCLOSED	                    订单已关闭	                订单已关闭，无法重复关闭	        订单已关闭，无需继续调用
     * SIGNERROR	                    签名错误	                    参数签名结果不正确	                请检查签名参数和方法是否都符合签名算法要求
     * REQUIRE_POST_METHOD	            请使用post方法	            未使用post传递参数 	            请检查请求参数是否通过post方法提交
     * XML_FORMAT_ERROR	                XML格式错误	                XML格式错误	                     请检查XML参数格式是否正确
     */
    Map<String, String> closeOrder(String out_trade_no) throws Exception;

    /**
     * 已支付订单申请退款
     * 关闭商户订单成功判断条件： return_code、result_code都为SUCCESS
     * 请求需要双向证书 详见证书使用: https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=4_3
     * 注意需要将jdk/jre环境中/jre/lib/security相应的：local_policy.jar和US_export_policy.jar 两个文件替换为无政策限制文件，下载路径：
     * jdk8: http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
     *
     * @param requestData               请求参数
     *  {
     *     "transaction_id": "1217752501201407033233368018",                    //二选一String(32)，微信生成的订单号，在支付通知中有返回
     *     "out_trade_no": "1217752501201407033233368018",                      //二选一String(32)，商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。 transaction_id、out_trade_no二选一，如果同时存在优先级：transaction_id> out_trade_no
     *     "out_refund_no": "1217752501201407033233368018",                     //必须String(64)，商户退款单号 ，商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔。
     *     "total_fee": "100",                                                  //必须int订单总金额，单位为分，只能为整数
     *     "refund_fee": "100",                                                 //必须int，退款总金额，订单总金额，单位为分，只能为整数
     *     "refund_fee_type": "CNY",                                            //String(8)，退款货币类型，需与支付一致，或者不填。符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见 https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=4_2
     *     "refund_desc": "商品已售完	",                                          //String(80)，若商户传入，会在下发给用户的退款消息中体现退款原因 注意：若订单退款金额≤1元，且属于部分退款，则不会在退款消息中体现退款原因
     *     "refund_account": "REFUND_SOURCE_RECHARGE_FUNDS",                    //String(30)，退款资金来源   仅针对老资金流商户使用 REFUND_SOURCE_UNSETTLED_FUNDS---未结算资金退款（默认使用未结算资金退款）   REFUND_SOURCE_RECHARGE_FUNDS---可用余额退款
     *     "notify_url": "https://weixin.qq.com/notify/"                        //String(256)，异步接收微信支付退款结果通知的回调地址，通知URL必须为外网可访问的url，不允许带参数
     *                                                                              公网域名必须为https，如果是走专线接入，使用专线NAT IP或者私有回调域名可使用http
     *                                                                              如果参数中传了notify_url，则商户平台上配置的回调地址将不会生效。
     *  }
     * @return Map<String, String>  null -- 失败
     * @throws Exception
     * {
     *     "return_code": "SUCCESS",                                            //SUCCESS/FAIL,此字段是通信标识，非交易标识，交易是否成功需要查看result_code来判断
     *     "return_msg": "OK",                                                  //当return_code为FAIL时返回信息为错误原因 ，例如 签名失败 参数格式校验错误
     *     "appid": "wx2421b1c4370ec43b",                                       //调用接口提交的公众账号ID
     *     "mch_id": "10000100",                                                //调用接口提交的商户号
     *     "nonce_str": "IITRi8Iabbblz1Jc",                                     //微信返回的随机字符串
     *     "sign": "7921E432F65EB8ED0CE9755F0E86D72F",                          //微信返回的签名值
     *     "result_code": "SUCCESS",                                            //业务结果 SUCCESS/FAIL
     *     "err_code": "SYSTEMERROR",                                           //当result_code为FAIL时返回错误代码，详细参见下文错误列表
     *     "err_code_des": "系统错误",                                            //当result_code为FAIL时返回错误描述，详细参见下文错误列表
     *     "transaction_id": "1009660380201506130728806387",                    //微信支付订单号
     *     "out_trade_no": "20150806125346",                                    //商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。
     *     "refund_id": "2007752501201407033233368018",                         //微信退款单号
     *     "refund_fee": "100",                                                 //退款总金额,单位为分,可以做部分退款
     *     "settlement_refund_fee": "100",                                      //去掉非充值代金券退款金额后的退款金额，退款金额=申请退款金额-非充值代金券退款金额，退款金额<=申请退款金额
     *     "total_fee": "100",                                                  //订单总金额，单位为分
     *     "settlement_total_fee": "100",                                       //去掉非充值代金券金额后的订单总金额，应结订单金额=订单金额-非充值代金券金额，应结订单金额<=订单金额。
     *     "fee_type": "CNY",                                                   //货币类型，符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见  https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=4_2
     *     "cash_fee": "100",                                                   //现金支付金额，现金支付金额订单现金支付金额，详见 https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=4_2
     *     "cash_fee_type": "CNY",                                              //现金支付币种，货币类型，符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见
     *     "cash_refund_fee": "100",                                            //现金退款金额，单位为分，只能为整数
     *     "coupon_type_$n": "CASH",                                            //代金券类型  CASH：充值代金券 NO_CASH：非充值优惠券  开通免充值券功能，并且订单使用了优惠券后有返回（取值：CASH、NO_CASH）。$n为下标,从0开始编号，举例：coupon_type_$0
     *     "coupon_refund_fee_$n": "100",                                       //单个代金券退款金额 代金券退款金额<=退款金额，退款金额-代金券或立减优惠退款金额为现金
     *     "coupon_refund_count": "1",                                          //退款代金券使用数量
     *     "coupon_refund_id_$n": "10000"                                       //退款代金券ID, $n为下标，从0开始编号
     * }
     *
     * 错误码
     * 名称	                            描述	                        原因	                            解决方案
    SYSTEMERROR	接口返回错误	系统超时等	请不要更换商户退款单号，请使用相同参数再次调用API。
    BIZERR_NEED_RETRY	退款业务流程错误，需要商户触发重试来解决	并发情况下，业务被拒绝，商户重试即可解决	请不要更换商户退款单号，请使用相同参数再次调用API。
    TRADE_OVERDUE	订单已经超过退款期限	订单已经超过可退款的最大期限(支付后一年内可退款)	请选择其他方式自行退款
    ERROR	业务错误	申请退款业务发生错误	该错误都会返回具体的错误原因，请根据实际返回做相应处理。
    USER_ACCOUNT_ABNORMAL	退款请求失败	用户账号注销	此状态代表退款申请失败，商户可自行处理退款。
    INVALID_REQ_TOO_MUCH	无效请求过多	连续错误请求数过多被系统短暂屏蔽	请检查业务是否正常，确认业务正常后请在1分钟后再来重试
    NOTENOUGH	余额不足	商户可用退款余额不足	此状态代表退款申请失败，商户可根据具体的错误提示做相应的处理。
    INVALID_TRANSACTIONID	无效transaction_id	请求参数未按指引进行填写	请求参数错误，检查原交易号是否存在或发起支付交易接口返回失败
    PARAM_ERROR	参数错误	请求参数未按指引进行填写	请求参数错误，请重新检查再调用退款申请
    APPID_NOT_EXIST	APPID不存在	参数中缺少APPID	请检查APPID是否正确
    MCHID_NOT_EXIST	MCHID不存在	参数中缺少MCHID	请检查MCHID是否正确
    ORDERNOTEXIST	订单号不存在	缺少有效的订单号	请检查你的订单号是否正确且是否已支付，未支付的订单不能发起退款
    REQUIRE_POST_METHOD	请使用post方法	未使用post传递参数 	请检查请求参数是否通过post方法提交
    SIGNERROR	签名错误	参数签名结果不正确	请检查签名参数和方法是否都符合签名算法要求
    XML_FORMAT_ERROR	XML格式错误	XML格式错误	请检查XML参数格式是否正确
    FREQUENCY_LIMITED	频率限制	1个月之前的订单申请退款有频率限制	该笔退款未受理，请降低频率后重试
    NOAUTH	异常IP请求不予受理	请求ip异常	如果是动态ip，请登录商户平台后台关闭ip安全配置；
    如果是静态ip，请确认商户平台配置的请求ip 在不在配的ip列表里
    CERT_ERROR	证书校验错误	请检查证书是否正确，证书是否过期或作废。	请检查证书是否正确，证书是否过期或作废。
    REFUND_FEE_MISMATCH	订单金额或退款金额与之前请求不一致，请核实后再试	订单金额或退款金额与之前请求不一致，请核实后再试	订单金额或退款金额与之前请求不一致，请核实后再试
    INVALID_REQUEST	请求参数符合参数格式，但不符合业务规则	此状态代表退款申请失败，商户可根据具体的错误提示做相应的处理。	此状态代表退款申请失败，商户可根据具体的错误提示做相应的处理。
    ORDER_NOT_READY	订单处理中，暂时无法退款，请稍后再试	订单处理中，暂时无法退款，请稍后再试  	订单处理中，暂时无法退款，请稍后再试       */
    Map<String, String> refund(Map<String, String> requestData) throws Exception;


    /**
     * 获取指定request上下文请求中异步退款通知数据
     * return_code以及refund_status都为SUCCESS,表示成功
     * 注意需要将jdk/jre环境中/jre/lib/security相应的：local_policy.jar和US_export_policy.jar 两个文件替换为无政策限制文件，下载路径：
     * jdk8: http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
     * @param request           request
     * @return  Map<String, String>  null -- 失败
     *   {
     *           "transaction_id" -> "4200001146202110225106697529"
     *           "nonce_str" -> "3737625762e0e2432850fa23c452903e"
     *           "refund_status" -> "SUCCESS"
     *           "out_refund_no" -> "20211022151556_refund"
     *           "settlement_refund_fee" -> "1"
     *           "success_time" -> "2021-10-24 16:36:03"
     *           "mch_id" -> "1515824841"
     *           "refund_recv_accout" -> "支付用户零钱"
     *           "refund_id" -> "50301410162021102413676085668"
     *           "out_trade_no" -> "20211022151556"
     *           "req_info" -> "7aJCvOVGgMeLeGTdPNVE2v+dSUVm4qxZJD9xdAo4Abay5IMcKvoNxpX5JOYn8kYglWGPWBAo8HYU+HkXIsryjQxmxX2a2nYohcnO0Qbb4DQXkmK4OoRaUAUtXm34i4JceUE78jpUcTj+PgDEld3fDEaIs0lEU3hkVybEBTw/i/4Btbtyjbgh8ObH5NZGAbPjvKgFwqm0vLXquVzKJQDO/L4f0rtxNpO4rp/2KnXUOWP6d9JHhs5qNdEhqYUqWKRMgC1nZDzpjT4DcfMQ0JybVPAF1Rsv77yySjSp6sNPOnBTyGWjC9PVGV5oCokeyFirY1q7chKoR815lVY8O7miS4bZQHnStpo2iqnIgM/ONhesbrvrttv/4nFmJU29LvJRYTUyXBDsBKKvn7lAARXd1VdHoqUwg6RCNe8XSJIiTh9r2SMsbPGsF3qcwrezw4Mu9gmO7p+g+dAffQdkm+16Mapfb2bNR2pULmEGdZhENO+2Dc9e5yfVmLZDnMB4Lhnrwx6M1j7cmTK5w5vjflHaxy72ngSgu5GeEyx4MDgL3dClp7GT4lOtHUspHfrO/pTLcsodNlx2dEDnMa+ipgBZ0mYUVNNRSKX2Xsg/raOQXOVwu0EqsU0bXnG4SVumEKopwArYnqM+4c14YJhkWguSG2PyjkMvhDL6SMNXokVt4z6nEttIjpCwTsgP9CcgrDdBtrDT/Vl7yJvCyYeMeOEeU/3HeImsaRM4dvvoLDVzkRqZGXiql0dPKLgDwLPgOc2C8QbgQUtGMrpsZv9YsEoiTmNSdw/3cECUFjdqKL7xFHGjTk+9+SPVBvLFdePD6eGZBcIhF+4+dYyw7FlsU4BeR3Yjn+yusoMK0cdkiQDFqThNoQ/wFTi9a+KXh30OrWNPP81/Sc87/nv9g2pmDDDH+UWjHJmuIXsmT0zIVq6X1LHnZXZhof4xg5XGo+WVTbjZBip5vgerTP2V6+3b5rDoX8aTjC3Ry/AMB5L5CbIb"
     *           "refund_account" -> "REFUND_SOURCE_RECHARGE_FUNDS"
     *           "appid" -> "wxbc462ec740814766"
     *           "refund_fee" -> "1"
     *           "total_fee" -> "1"
     *           "settlement_total_fee" -> "1"
     *           "return_code" -> "SUCCESS"
     *           "refund_request_source" -> "API"
     *    }
     */
    Map<String, String> getRefundNotifyData(HttpServletRequest request) throws Exception;


    /**
     * 获取当前request上下文请求中异步退款通知数据
     * return_code以及refund_status都为SUCCESS,表示成功
     * 注意需要将jdk/jre环境中/jre/lib/security相应的：local_policy.jar和US_export_policy.jar 两个文件替换为无政策限制文件，下载路径：
     * jdk8: http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
     * @return  Map<String, String>  null -- 失败
     *   {
     *           "transaction_id" -> "4200001146202110225106697529"
     *           "nonce_str" -> "3737625762e0e2432850fa23c452903e"
     *           "refund_status" -> "SUCCESS"
     *           "out_refund_no" -> "20211022151556_refund"
     *           "settlement_refund_fee" -> "1"
     *           "success_time" -> "2021-10-24 16:36:03"
     *           "mch_id" -> "1515824841"
     *           "refund_recv_accout" -> "支付用户零钱"
     *           "refund_id" -> "50301410162021102413676085668"
     *           "out_trade_no" -> "20211022151556"
     *           "req_info" -> "7aJCvOVGgMeLeGTdPNVE2v+dSUVm4qxZJD9xdAo4Abay5IMcKvoNxpX5JOYn8kYglWGPWBAo8HYU+HkXIsryjQxmxX2a2nYohcnO0Qbb4DQXkmK4OoRaUAUtXm34i4JceUE78jpUcTj+PgDEld3fDEaIs0lEU3hkVybEBTw/i/4Btbtyjbgh8ObH5NZGAbPjvKgFwqm0vLXquVzKJQDO/L4f0rtxNpO4rp/2KnXUOWP6d9JHhs5qNdEhqYUqWKRMgC1nZDzpjT4DcfMQ0JybVPAF1Rsv77yySjSp6sNPOnBTyGWjC9PVGV5oCokeyFirY1q7chKoR815lVY8O7miS4bZQHnStpo2iqnIgM/ONhesbrvrttv/4nFmJU29LvJRYTUyXBDsBKKvn7lAARXd1VdHoqUwg6RCNe8XSJIiTh9r2SMsbPGsF3qcwrezw4Mu9gmO7p+g+dAffQdkm+16Mapfb2bNR2pULmEGdZhENO+2Dc9e5yfVmLZDnMB4Lhnrwx6M1j7cmTK5w5vjflHaxy72ngSgu5GeEyx4MDgL3dClp7GT4lOtHUspHfrO/pTLcsodNlx2dEDnMa+ipgBZ0mYUVNNRSKX2Xsg/raOQXOVwu0EqsU0bXnG4SVumEKopwArYnqM+4c14YJhkWguSG2PyjkMvhDL6SMNXokVt4z6nEttIjpCwTsgP9CcgrDdBtrDT/Vl7yJvCyYeMeOEeU/3HeImsaRM4dvvoLDVzkRqZGXiql0dPKLgDwLPgOc2C8QbgQUtGMrpsZv9YsEoiTmNSdw/3cECUFjdqKL7xFHGjTk+9+SPVBvLFdePD6eGZBcIhF+4+dYyw7FlsU4BeR3Yjn+yusoMK0cdkiQDFqThNoQ/wFTi9a+KXh30OrWNPP81/Sc87/nv9g2pmDDDH+UWjHJmuIXsmT0zIVq6X1LHnZXZhof4xg5XGo+WVTbjZBip5vgerTP2V6+3b5rDoX8aTjC3Ry/AMB5L5CbIb"
     *           "refund_account" -> "REFUND_SOURCE_RECHARGE_FUNDS"
     *           "appid" -> "wxbc462ec740814766"
     *           "refund_fee" -> "1"
     *           "total_fee" -> "1"
     *           "settlement_total_fee" -> "1"
     *           "return_code" -> "SUCCESS"
     *           "refund_request_source" -> "API"
     *    }
     */
    Map<String, String> getRefundNotifyData() throws Exception;

    /**
     * 对微信退款通知中req_info数据进行解码
     * 注意需要将jdk/jre环境中/jre/lib/security相应的：local_policy.jar和US_export_policy.jar 两个文件替换为无政策限制文件，下载路径：
     * jdk8: http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
     * @param req_info              req_info数据
     * @return Map<String, String>  null -- 失败
     */
    Map<String, String> decryptRefundReqInfo(String req_info) throws Exception;


    /**
     * 商户退款查询
     * return_code/result_code以及refund_status_0都为SUCCESS,表示成功
     * @param requestData               请求参数
     *  {
     *     "transaction_id": "1217752501201407033233368018",                    //四选一String(32)，微信生成的订单号，在支付通知中有返回
     *     "out_trade_no": "1217752501201407033233368018",                      //四选一String(32)，商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。 transaction_id、out_trade_no二选一，如果同时存在优先级：transaction_id> out_trade_no
     *     "out_refund_no": "1217752501201407033233368018",                     //四选一String(64)，商户退款单号 ，商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔。
     *     "refund_id": "1217752501201407033233368018",                         //四选一String(32)，微信生成的退款单号，在申请退款接口有返回
     *     "offset": "15"                                                       //偏移量，当部分退款次数超过10次时可使用，表示返回的查询结果从这个偏移量开始取记录
     *  }
     * @return  Map<String, String>  null -- 失败
     *
     *       {
     *           "transaction_id":"4200001129202110241149712934",
     *           "nonce_str":"50JfBKPiYRbFdKn4",
     *           "out_refund_no_0":"201210240000009_refund",
     *           "refund_status_0":"SUCCESS",
     *           "sign":"4F774BA3CA06EBCEC2C661F67DFBB690",
     *           "refund_fee_0":"1",
     *           "refund_recv_accout_0":"支付用户零钱",
     *           "return_msg":"OK",
     *           "mch_id":"1515824841",
     *           "refund_success_time_0":"2021-10-24 18:30:07",
     *           "cash_fee":"1",
     *           "refund_id_0":"50301610212021102413673348172",
     *           "out_trade_no":"201210240000009",
     *           "appid":"wxbc462ec740814766",
     *           "refund_fee":"1",
     *           "total_fee":"1",
     *           "result_code":"SUCCESS",
     *           "refund_account_0":"REFUND_SOURCE_RECHARGE_FUNDS",
     *           "refund_count":"1",
     *           "return_code":"SUCCESS",
     *           "refund_channel_0":"ORIGINAL"
     *       }
     * @throws Exception
     */
    Map<String, String> refundQuery(Map<String, String> requestData) throws Exception;

    /**
     * 下载交易帐单
     * @param requestData               请求参数
     *  {
     *     "bill_date": "20140603",                                             //必须String(8)，对账单日期
     *     "bill_type": "ALL",                                                  //必须String(8)，账单类型
     *                                                                                  ALL（默认值），返回当日所有订单信息（不含充值退款订单）
     *                                                                                  SUCCESS，返回当日成功支付的订单（不含充值退款订单）
     *                                                                                  REFUND，返回当日退款订单（不含充值退款订单）
     *                                                                                  RECHARGE_REFUND，返回当日充值退款订单
     *     "tar_type": "GZIP"                                                   //String(8)，压缩账单 非必传参数，固定值：GZIP，返回格式为.gzip的压缩包账单。不传则默认为数据流形式。
     *  }
     * @return List<List<String>> null -- 失败
     * 第一行为表头，根据请求下载的对账单类型不同而不同(由bill_type决定)
     * 从第二行起，为数据记录，各参数以逗号分隔，参数前增加`符号，为标准键盘1左边键的字符，字段顺序与表头一致。
     * 倒数第二行为订单统计标题，最后一行为统计数据
     *
     * [
     *     [
     *         "﻿交易时间",
     *         "公众账号ID",
     *         "商户号",
     *         "特约商户号",
     *         "设备号",
     *         "微信订单号",
     *         "商户订单号",
     *         "用户标识",
     *         "交易类型",
     *         "交易状态",
     *         "付款银行",
     *         "货币种类",
     *         "应结订单金额",
     *         "代金券金额",
     *         "微信退款单号",
     *         "商户退款单号",
     *         "退款金额",
     *         "充值券退款金额",
     *         "退款类型",
     *         "退款状态",
     *         "商品名称",
     *         "商户数据包",
     *         "手续费",
     *         "费率",
     *         "订单金额",
     *         "申请退款金额",
     *         "费率备注"
     *     ],
     *     [
     *         "`2021-10-24 21:31:47",
     *         "`wxbc462ec740814766",
     *         "`1515824841",
     *         "`0",
     *         "`",
     *         "`4200001118202110248678272487",
     *         "`201210240000023",
     *         "`oePyf52e7mpMJjSrTpbig7qiy0ZE",
     *         "`MWEB",
     *         "`SUCCESS",
     *         "`OTHERS",
     *         "`CNY",
     *         "`0.01",
     *         "`0.00",
     *         "`0",
     *         "`0",
     *         "`0.00",
     *         "`0.00",
     *         "`",
     *         "`",
     *         "`测试商品",
     *         "`",
     *         "`0.00000",
     *         "`0.60%",
     *         "`0.01",
     *         "`0.00",
     *         "`"
     *     ],
     *     ...
     *     ,
     *     [
     *         "总交易单数",
     *         "应结订单总金额",
     *         "退款总金额",
     *         "充值券退款总金额",
     *         "手续费总金额",
     *         "订单总金额",
     *         "申请退款总金额"
     *     ],
     *     [
     *         "`38",
     *         "`0.30",
     *         "`0.08",
     *         "`0.00",
     *         "`0.00000",
     *         "`0.30",
     *         "`0.08"
     *     ]
     * ]
     *
     * @throws Exception
     */
    List<List<String>> downloadBill(Map<String, String> requestData) throws Exception;



    /**
     * 下载资金账单，请求需要双向证书
     * 注意需要将jdk/jre环境中/jre/lib/security相应的：local_policy.jar和US_export_policy.jar 两个文件替换为无政策限制文件，下载路径：
     * jdk8: http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
     * @param requestData               请求参数
     *  {
     *     "bill_date": "20140603",                                             //必须String(8)，对账单日期
     *     "account_type": "Basic",                                             //必须String(8)，账单的资金来源账户：
     *                                                                              Basic  基本账户
     *                                                                              Operation 运营账户
     *                                                                              Fees 手续费账户
     *     "tar_type": "GZIP"                                                   //String(8)，压缩账单 非必传参数，固定值：GZIP，返回格式为.gzip的压缩包账单。不传则默认为数据流形式。
     *  }
     * @return List<List<String>> null -- 失败
     * 第一行为表头
     * 从第二行起，为资金流水数据，各参数以逗号分隔，参数前增加`符号，为标准键盘1左边键的字符，字段顺序与表头一致
     * 倒数第二行为资金账单统计标题
     * 最后一行为统计数据
     *
     * [
     *     [
     *         "记账时间",
     *         "微信支付业务单号",
     *         "资金流水单号",
     *         "业务名称",
     *         "业务类型",
     *         "收支类型",
     *         "收支金额(元)",
     *         "账户结余(元)",
     *         "资金变更提交申请人",
     *         "备注",
     *         "业务凭证号"
     *     ],
     *     [
     *         "`2021-10-24 14:50:29",
     *         "`2021102414000000044507053",
     *         "`100002670121102410205004632624673253423583674",
     *         "`充值/提现",
     *         "`扫码充值",
     *         "`收入",
     *         "`5.00",
     *         "`5.00",
     *         "`@1515824841",
     *         "`",
     *         "`2021102414000000044507053"
     *     ],
     *     ...
     *     ,
     *     [
     *         "资金流水总笔数",
     *         "收入笔数",
     *         "收入金额",
     *         "支出笔数",
     *         "支出金额"
     *     ],
     *     [
     *         "`39",
     *         "`31",
     *         "`5.30",
     *         "`8",
     *         "`0.08"
     *     ]
     * ]
     *
     *
     * @throws Exception
     */
    List<List<String>> downloadFundflow(Map<String, String> requestData) throws Exception;

    /**
     * 企业用于向微信用户个人付款,目前支持向指定微信用户的openid付款。
     * 请求需要双向证书
     * return_code以及result_code都为SUCCESS,表示成功
     * 注意需要将jdk/jre环境中/jre/lib/security相应的：local_policy.jar和US_export_policy.jar 两个文件替换为无政策限制文件，下载路径：
     * jdk8: http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
     *
     * @param requestData               请求参数
     *   {
     *      "partner_trade_no": "10000098201411111234567890",                    //必须String(32)，商户订单号，需保持唯一性 (只能是字母或者数字，不能包含有其它字符)
     *      "openid": "oxTWIuGaIt6gTKsQRLau2M0yL16E	",                           //必须String(64)，商户appid下，某用户的openid
     *      "check_name": "FORCE_CHECK",                                         //必须String(16)，校验用户姓名选项	NO_CHECK：不校验真实姓名 FORCE_CHECK：强校验真实姓名
     *      "amount": "10099",                                                   //必须int，付款金额，单位为分,款金额限制: 不低于最小金额1.00元且不累计超过5000.00元。
     *      "desc": "理赔",                                                       //必须String(100)，付款备注 付款备注，必填。注意：备注中的敏感词会被转成字符*
     *      "re_user_name": "王小王",                                             //String(64)，收款用户姓名,收款用户真实姓名。 如果check_name设置为FORCE_CHECK，则必填用户真实姓名  如需电子回单，需要传入收款用户姓名
     *      "device_info": "013467007045764",                                    //String(32)，微信支付分配的终端设备号
     *      "spbill_create_ip": "192.168.0.1"                                    //String(32)，该IP同在商户平台设置的IP白名单中的IP没有关联，该IP可传用户端或者服务端的IP
     *   }
     *
     * @return Map<String, String> null --失败
     * @throws Exception
     *
     */
    Map<String, String> transfer(Map<String, String> requestData) throws Exception;


    /**
     * 小程序微信支付统一下单,需要在微信小程序开发设置中设置相应的服务器域名及业务域名
     * 说明：在微信服务器端，只要out_trade_no与body完成相同，则会认为是同一订单，因此如果订单发生了改价行为，需要修改out_trade_no订单号并重新发起统一下单。
     *      所有参数将自动转ISO-8859-1字符集
     * @param requestData               请求数据
     *     {
     *           openid                    trade_type=JSAPI时（即JSAPI支付），此参数必传，此参数为微信用户在商户对应appid下的唯一标识。
     *                                     openid如何获取，可参考【获取openid】。
     *                                     企业号请使用【企业号OAuth2.0接口】获取企业号内成员userid，再调用【企业号userid转openid接口】进行转换
     *           body               必须    商品简单描述，该字段请按照规范传递，String(128)
     *           out_trade_no       必须    商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|* 且在同一个商户号下唯一,String(32)
     *           total_fee          必须    订单总金额，单位为分,int
     *           spbill_create_ip   必须    支持IPV4和IPV6两种格式的IP地址。用户的客户端IP,String(64)
     *           notify_url         必须    body 异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。 公网域名必须为https，如果是走专线接入，使用专线NAT IP或者私有回调域名可使用http
     *           trade_type         必须    交易类型固定为： JSAPI -JSAPI支付
     *           device_info               设备号,自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB" String(32)
     *           detail                    String(6000)商品详细描述，对于使用单品优惠的商户，该字段必须按照规范上传，详见“单品优惠参数说明”
     *           attach                    附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用。	String(127)
     *           sign_type                 签名类型，默认为MD5，支持HMAC-SHA256和MD5。String(32)
     *           fee_type                  符合ISO 4217标准的三位字母代码，默认人民币：CNY  String(16)
     *           time_start                交易起始时间, 订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。String(14)
     *           time_expire               交易结束时间, 订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010。订单失效时间是针对订单号而言的，由于在请求支付的时候有一个必传参数prepay_id只有两小时的有效期，所以在重入时间超过2小时的时候需要重新请求下单接口获取新的prepay_id。其他详见时间规则
     *                                    time_expire只能第一次下单传值，不允许二次修改，二次修改系统将报错。如用户支付失败后，需再次支付，需更换原订单号重新下单。String(14)
     *                                    建议：最短失效时间间隔大于1分钟
     *           goods_tag                 订单优惠标记，使用代金券或立减优惠功能时需要的参数，说明详见代金券或立减优惠 String(32)
     *           product_id                商品ID,trade_type=NATIVE时，此参数必传。此参数为二维码中包含的商品ID，商户自行定义。String(32)
     *           limit_pay                 指定支付方式,上传此参数no_credit--可限制用户不能使用信用卡支付  String(32)
     *           receipt                   电子发票入口开放标识,Y，传入Y时，支付成功消息和支付详情页将出现开票入口。需要在微信支付商户平台或微信公众平台开通电子发票功能，传此字段才可生效 String(8)
     *           profit_sharing            是否需要分账, Y-是，需要分账  N-否，不分账, 字母要求大写，不传默认不分账 String(16)
     *           scene_info                String(256)场景信息, 该字段常用于线下活动时的场景信息上报，支持上报实际门店信息，商户也可以按需求自己上报相关信息。该字段为JSON对象数据  String(256)
     *                                           对象格式为：{"store_info":{"id": "门店ID","name": "名称","area_code": "编码","address": "地址" }}，说明如下：
     *                                           -门店id	        id	        是	String(32)	SZTX001	门店编号，由商户自定义
     *                                           -门店名称	    name	    否	String(64)	腾讯大厦腾大餐厅	门店名称 ，由商户自定义
     *                                           -门店行政区划码	area_code	否	String(6)	440305	门店所在地行政区划码，详细见《最新县及县以上行政区划代码》
     *                                           -门店详细地址	    address	    否	String(128)	科技园中一路腾讯大厦	门店详细地址 ，由商户自定义
     *           appid             必须    不需要设置，微信分配的小程序ID，自动生成
     *           nonce_str         必须    不需要设置，随机字符串，长度要求在32位以内，自动生成
     *           sign              必须    不需要设置，签名，根据签名规则自动生成
     *     }
     * @return Map<String,Object>  null -- 失败
     * @throws Exception
     * {
     *     "return_code": "SUCCESS",                                            //SUCCESS/FAIL,此字段是通信标识，非交易标识，交易是否成功需要查看result_code来判断
     *     "return_msg": "OK",                                                  //当return_code为FAIL时返回信息为错误原因 ，例如 签名失败 参数格式校验错误
     *     "appid": "wx2421b1c4370ec43b",                                       //调用接口提交的公众账号ID
     *     "mch_id": "10000100",                                                //调用接口提交的商户号
     *     "nonce_str": "IITRi8Iabbblz1Jc",                                     //微信返回的随机字符串
     *     "openid": "oUpF8uMuAJO_M2pxb1Q9zNjWeS6o",                            //微信用户openid
     *     "sign": "7921E432F65EB8ED0CE9755F0E86D72F",                          //微信返回的签名值
     *     "result_code": "SUCCESS",                                            //业务结果 SUCCESS/FAIL
     *     "prepay_id": "wx201411101639507cbf6ffd8b0779950874",                 //预支付交易会话标识 微信生成的预支付会话标识，用于后续接口调用中使用，该值有效期为2小时
     *     "trade_type": "JSAPI",                                               //交易类型 JSAPI -JSAPI支付, NATIVE -Native支付, APP -APP支付
     *     "timestamp": "timestamp",                                            //timestamp
     *     "code_url": "weixin://wxpay/bizpayurl/up?pr=NwY5Mz9&groupid=00"      //二维码链接 trade_type=NATIVE时有返回，此url用于生成支付二维码，然后提供给用户进行扫码支付。
     * }
     * 错误码
     * 名称	                    描述                          原因	                        解决方案
     * INVALID_REQUEST	        参数错误	                    参数格式有误或者未按规则上传	        订单重入时，要求参数值与原请求一致，请确认参数问题
     * NOAUTH	                商户无此接口权限	            商户未开通此接口权限	            请商户前往申请此接口权限
     * NOTENOUGH	            余额不足	                    用户账号余额不足	                用户账号余额不足，请用户充值或更换支付卡后再支付
     * ORDERPAID	            商户订单已支付	                商户订单已支付，无需重复操作	        商户订单已支付，无需更多操作
     * ORDERCLOSED	            订单已关闭	                当前订单已关闭，无法支付	        当前订单已关闭，请重新下单
     * SYSTEMERROR	            系统错误	                    系统超时	                        系统异常，请用相同参数重新调用
     * APPID_NOT_EXIST	        APPID不存在	                参数中缺少APPID	                请检查APPID是否正确
     * MCHID_NOT_EXIST	        MCHID不存在	                参数中缺少MCHID	                请检查MCHID是否正确
     * APPID_MCHID_NOT_MATCH	appid和mch_id不匹配	        appid和mch_id不匹配	            请确认appid和mch_id是否匹配
     * LACK_PARAMS	            缺少参数	                    缺少必要的请求参数	                请检查参数是否齐全
     * OUT_TRADE_NO_USED	    商户订单号重复	                同一笔交易不能多次提交	            请核实商户订单号是否重复提交
     * SIGNERROR	            签名错误	                    参数签名结果不正确	                请检查签名参数和方法是否都符合签名算法要求
     * XML_FORMAT_ERROR	        XML格式错误	                XML格式错误	                    请检查XML参数格式是否正确
     * REQUIRE_POST_METHOD	    请使用post方法	            未使用post传递参数 	            请检查请求参数是否通过post方法提交
     * POST_DATA_EMPTY	        post数据为空	                post数据不能为空	                请检查post数据是否为空
     * NOT_UTF8	                编码格式错误	                未使用指定编码格式	                请使用UTF-8编码格式
     */
    Map<String,String> weappPayUnifiedOrder(Map<String, String> requestData) throws Exception;



}

