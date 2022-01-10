package com.ihltx.utility.alipay;

import com.ihltx.utility.httpclient.service.RestTemplateUtil;
import com.ihltx.utility.redis.service.RedisFactory;
import com.alipay.api.domain.Participant;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface AlipayUtil {

    /**
     * 交易完成状态
     */
    public static final String TRADE_FINISHED = "TRADE_FINISHED";

    /**
     * 交易成功状态
     */
    public static final String TRADE_SUCCESS = "TRADE_SUCCESS";

    //支付宝网关（注意沙箱alipaydev，正式则为 alipay）不需要修改
    public static final String API_URL = "https://openapi.alipay.com/gateway.do";

    //编码类型
    public static final String CHARSET = "UTF-8";

    //数据类型
    public static final String DATA_FORMART = "json";

    //签名类型
    public static final String SIGN_TYPE = "RSA2";

    /**
     * 获取支付宝应用 appID
     * 我的应用---自研服务--网页&移动应用--找到自己的应用--点击打开获取APPID
     * https://open.alipay.com/platform/developerIndex.htm
     * @return String
     */
    String getAppID();

    /**
     * 设置支付宝应用 appID
     * @param appID
     */
    void setAppID(String appID);

    /**
     * 获取应用私钥
     * 账户中心---密钥管理--开放平台密钥--相应APP应用--接口加签方式--设置/查看 选择证加签模式为【公钥证书】模式，并通过工具生成相关证书，参考：https://opendocs.alipay.com/open/291/105971#LDsXr
     * @return String
     */
    String getAppPrivateKey();

    /**
     * 设置应用私钥
     * @param appPrivateKey               应用私钥
     */
    void setAppPrivateKey(String appPrivateKey);



    /**
     * 获取应用公钥证书路径（app_cert_path 文件绝对路径）
     * 账户中心---密钥管理--开放平台密钥--相应APP应用--接口加签方式--设置/查看 选择证加签模式为【公钥证书】模式，并通过工具生成相关证书，参考：https://opendocs.alipay.com/open/291/105971#LDsXr
     * @return String
     */
    String getAppCertPath();

    /**
     * 设置应用公钥证书路径（app_cert_path 文件绝对路径）
     * @param appCertPath               应用公钥证书路径（app_cert_path 文件绝对路径）
     */
    void setAppCertPath(String appCertPath);


    /**
     * 获取支付宝公钥证书文件路径（alipay_cert_path 文件绝对路径）
     * 账户中心---密钥管理--开放平台密钥--相应APP应用--接口加签方式--设置/查看 选择证加签模式为【公钥证书】模式，并通过工具生成相关证书，参考：https://opendocs.alipay.com/open/291/105971#LDsXr
     *
     * @return String
     */
    String getAlipayPublicCertPath();

    /**
     * 设置支付宝公钥证书文件路径（alipay_cert_path 文件绝对路径）
     * @param alipayPublicCertPath              支付宝公钥证书文件路径（alipay_cert_path 文件绝对路径）
     */
    void setAlipayPublicCertPath(String alipayPublicCertPath);


    /**
     * 获取支付宝CA根证书文件路径（alipay_root_cert_path 文件绝对路径）
     * 账户中心---密钥管理--开放平台密钥--相应APP应用--接口加签方式--设置/查看 选择证加签模式为【公钥证书】模式，并通过工具生成相关证书，参考：https://opendocs.alipay.com/open/291/105971#LDsXr
     *
     * @return String
     */
    String getAlipayRootCertPath();

    /**
     * 设置支付宝CA根证书文件路径（alipay_root_cert_path 文件绝对路径）
     * @param alipayRootCertPath              支付宝CA根证书文件路径（alipay_root_cert_path 文件绝对路径）
     */
    void setAlipayRootCertPath(String alipayRootCertPath);



    void setApplicationContext(ApplicationContext applicationContext);

    ApplicationContext getApplicationContext();


    /**
     * 设置支付宝异步通知URL
     * @param notifyUrl             异步通知URL
     */
    void setNotifyUrl(String notifyUrl);

    /**
     * 返回支付宝异步通知URL
     */
    String getNotifyUrl();


    /**
     * 设置支付宝同步通知URL
     * @param returnUrl             同步通知URL
     */
    void setReturnUrl(String returnUrl);

    /**
     * 返回支付宝同步通知URL
     */
    String getReturnUrl();


    /**
     * 使用默认异步及同步通知url，手机网站2.0支付(接口名：alipay.trade.wap.pay)
     * @param requestData                   请求数据
     *       {
     *              "out_trade_no":"70501111111S001111119",                     //必填，商户订单号。String(64) 由商家自定义，64个字符以内，仅支持字母、数字、下划线且需保证在商户端不重复。
     *              "total_amount":0.01,                                        //必填，订单总金额。Double 单位为元，精确到小数点后两位，取值范围：[0.01,100000000] 。
     *              "subject":"70501111111S001111119",                          //必填，订单标题。String(256) 注意：不可使用特殊字符，如 /，=，& 等。
     *              "product_code":"QUICK_WAP_WAY",                             //必填，固定为QUICK_WAP_WAY
     *              "quit_url":"url",                                           //必填，String(400) 用户付款中途退出返回商户网站的地址
     *              "body":"Iphone6 16G",                                       //String(128) 订单附加信息。 如果请求时传递了该参数，将在异步通知、对账单中原样返回，同时会在商户和用户的pc账单详情中作为交易描述展示
     *              "auth_token":"appopenBb64d181d0146481ab6a762c00714cC27",    //String(40) 针对用户授权接口，获取用户相关数据时，用于标识用户授权关系
     *              "time_expire":"2016-12-31 10:05:00",                        //订单绝对超时时间。 格式为yyyy-MM-dd HH:mm:ss。 注：time_express和timeout_express两者只需传入一个或者都不传，如果两者都传，优先使用time_expire。
     *              "timeout_express":"90m",                                    //订单相对超时时间。 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：5m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。
     *                                                                              该参数数值不接受小数点， 如 1.5h，可转换为 90m。    注：无线支付场景最小值为5m，低于5m支付超时时间按5m计算。
     *                                                                               注：time_express和timeout_express两者只需传入一个或者都不传，如果两者都传，优先使用time_expire。
     *              "extend_params":"ExtendParams json数据",                     //业务扩展参数
     *                              {
     *                                      "sys_service_provider_id": "2088511833207846",      //系统商编号 该参数作为系统商返佣数据提取的依据，请填写系统商签约协议的PID
     *                                      "hb_fq_num": 3,                                     //使用花呗分期要进行的分期数
     *                                      "hb_fq_seller_percent": 100,                        //使用花呗分期需要卖家承担的手续费比例的百分值，传入100代表100%
     *                                      "industry_reflux_info": "{\"scene_code\":\"metro_tradeorder\",\"channel\":\"xxxx\",\"scene_data\":{\"asset_name\":\"ALIPAY\"}}",
     *                                                                                          //行业数据回流信息, 详见：地铁支付接口参数补充说明
     *                                      "card_type": "S0JP0000",                            //卡类型
     *                                      "specified_seller_name": "XXX的跨境小铺"             //特殊场景下，允许商户指定交易展示的卖家名称
     *
     *                              }
     *              "business_params":"{\"data":\"123\"}",                      //商户传入业务信息，具体值要和支付宝约定，应用于安全，营销等参数直传场景，格式为json格式
     *              "promo_params":"{\"storeIdType\":\"1\"}",                   //优惠参数 注：仅与支付宝协商后可用
     *              "passback_params":"merchantBizType%3d3C%26merchantBizNo%3d2016010101111",
     *                                                                          //公用回传参数。 如果请求时传递了该参数，支付宝会在异步通知时将该参数原样返回。 本参数必须进行UrlEncode之后才可以发送给支付宝。
     *              "store_id":"NJ_001",                                        //商户门店编号。 指商户创建门店时输入的门店编号。
     *              "enable_pay_channels":"pcredit,moneyFund,debitCardExpress", //指定支付渠道。 用户只能使用指定的渠道进行支付，多个渠道以逗号分割。
     *                                                                           与disable_pay_channels互斥，支持传入的值：渠道列表。https://docs.open.alipay.com/common/wifww7
     *                                                                            注：如果传入了指定支付渠道，则用户只能用指定内的渠道支付，包括营销渠道也要指定才能使用。该参数可能导致用户支付受限，慎用。
     *              "disable_pay_channels":"pcredit,moneyFund,debitCardExpress", //禁用渠道,用户不可用指定渠道支付，多个渠道以逗号分割
     *                                                                            注，与enable_pay_channels互斥
     *              "specified_channel":"pcredit",                               //指定单通道。 目前仅支持传入pcredit，若由于用户原因渠道不可用，用户可选择是否用其他渠道支付。 注：该参数不可与花呗分期参数同时传入
     *              "merchant_order_no":"20161008001",                           //商户的原始订单号
     *              "ext_user_info":"ExtUserInfo的json数据",                      //外部指定买家
     *                            {
     *                                     "name":"李明",                           //指定买家姓名。 注： need_check_info=T或fix_buyer=T时该参数才有效
     *                                     "mobile":"16587658765",                 //指定买家手机号。 注：该参数暂不校验
     *                                     "cert_type":"IDENTITY_CARD",            //指定买家证件类型。 枚举值： IDENTITY_CARD：身份证； PASSPORT：护照； OFFICER_CARD：军官证； SOLDIER_CARD：士兵证； HOKOU：户口本。如有其它类型需要支持，请与蚂蚁金服工作人员联系。
     *                                                                               注： need_check_info=T或fix_buyer=T时该参数才有效，支付宝会比较买家在支付宝留存的证件类型与该参数传入的值是否匹配。
     *                                     "cert_no":"362334768769238881",         //买家证件号。 注：need_check_info=T或fix_buyer=T时该参数才有效，支付宝会比较买家在支付宝留存的证件号码与该参数传入的值是否匹配。
     *                                     "min_age":"18",                         //允许的最小买家年龄。 买家年龄必须大于等于所传数值  注： 1. need_check_info=T时该参数才有效   2. min_age为整数，必须大于等于0
     *                                     "fix_buyer":"F",                        //是否强制校验买家身份。 需要强制校验传：T; 不需要强制校验传：F或者不传； 当传T时，接口上必须指定cert_type、cert_no和name信息且支付宝会校验传入的信息跟支付买家的信息都匹配，否则报错。  默认为不校验。
     *                                     "need_check_info":"F"                   //是否强制校验买家信息； 需要强制校验传：T; 不需要强制校验传：F或者不传； 当传T时，支付宝会校验支付买家的信息与接口上传递的cert_type、cert_no、name或age是否匹配，只有接口传递了信息才会进行对应项的校验；只要有任何一项信息校验不匹配交易都会失败。如果传递了need_check_info，但是没有传任何校验项，则不进行任何校验。  默认为不校验。
     *                            }
     *
     *       }
     * @return String  null -- 失败
     *       否则返回 支付宝生产的表单FORM的完整html，表单在浏览器中呈现将自动提交
     */
    String wapPay(Map<String, Object> requestData);

    /**
     * 手机网站2.0支付(接口名：alipay.trade.wap.pay)
     * @param requestData                   请求数据
     *       {
     *              "out_trade_no":"70501111111S001111119",                     //必填，商户订单号。String(64) 由商家自定义，64个字符以内，仅支持字母、数字、下划线且需保证在商户端不重复。
     *              "total_amount":0.01,                                        //必填，订单总金额。Double 单位为元，精确到小数点后两位，取值范围：[0.01,100000000] 。
     *              "subject":"70501111111S001111119",                          //必填，订单标题。String(256) 注意：不可使用特殊字符，如 /，=，& 等。
     *              "product_code":"QUICK_WAP_WAY",                             //必填，固定为QUICK_WAP_WAY
     *              "quit_url":"url",                                           //必填，String(400) 用户付款中途退出返回商户网站的地址
     *              "body":"Iphone6 16G",                                       //String(128) 订单附加信息。 如果请求时传递了该参数，将在异步通知、对账单中原样返回，同时会在商户和用户的pc账单详情中作为交易描述展示
     *              "auth_token":"appopenBb64d181d0146481ab6a762c00714cC27",    //String(40) 针对用户授权接口，获取用户相关数据时，用于标识用户授权关系
     *              "goods_detail":"json数组格式",                                //订单包含的商品列表信息，json格式，其它说明详见商品明细说明
     *                             [
     *                                      {
     *                                          "goods_id":"",                  //必填,商品的编号
     *                                          "alipay_goods_id":"",           //支付宝定义的统一商品编号
     *                                          "goods_name":"",                //必填,商品名称
     *                                          "quantity":1,                   //必填,商品数量
     *                                          "price":0.01,                   //必填,商品单价，单位为元
     *                                          "goods_category":"",            //商品类目
     *                                          "categories_tree":"",           //商品类目树，从商品类目根节点到叶子节点的类目id组成，类目id值使用|分割
     *                                          "body":"",                      //商品描述信息
     *                                          "show_url":""                   //商品的展示地址
     *                                      },...
     *                             ]
     *              "time_expire":"2016-12-31 10:05:00",                        //订单绝对超时时间。 格式为yyyy-MM-dd HH:mm:ss。 注：time_express和timeout_express两者只需传入一个或者都不传，如果两者都传，优先使用time_expire。
     *              "timeout_express":"90m",                                    //订单相对超时时间。 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：5m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。
     *                                                                              该参数数值不接受小数点， 如 1.5h，可转换为 90m。    注：无线支付场景最小值为5m，低于5m支付超时时间按5m计算。
     *                                                                               注：time_express和timeout_express两者只需传入一个或者都不传，如果两者都传，优先使用time_expire。
     *              "extend_params":ExtendParams,                               //业务扩展参数，ExtendParams对象
     *                              {
     *                                      "sys_service_provider_id": "2088511833207846",      //系统商编号 该参数作为系统商返佣数据提取的依据，请填写系统商签约协议的PID
     *                                      "hb_fq_num": 3,                                     //使用花呗分期要进行的分期数
     *                                      "hb_fq_seller_percent": 100,                        //使用花呗分期需要卖家承担的手续费比例的百分值，传入100代表100%
     *                                      "industry_reflux_info": "{\"scene_code\":\"metro_tradeorder\",\"channel\":\"xxxx\",\"scene_data\":{\"asset_name\":\"ALIPAY\"}}",
     *                                                                                          //行业数据回流信息, 详见：地铁支付接口参数补充说明
     *                                      "card_type": "S0JP0000",                            //卡类型
     *                                      "specified_seller_name": "XXX的跨境小铺"             //特殊场景下，允许商户指定交易展示的卖家名称
     *
     *                              }
     *              "business_params":"{\"data":\"123\"}",                      //商户传入业务信息，具体值要和支付宝约定，应用于安全，营销等参数直传场景，格式为json格式
     *              "promo_params":"{\"storeIdType\":\"1\"}",                   //优惠参数 注：仅与支付宝协商后可用
     *              "passback_params":"merchantBizType%3d3C%26merchantBizNo%3d2016010101111",
     *                                                                          //公用回传参数。 如果请求时传递了该参数，支付宝会在异步通知时将该参数原样返回。 本参数必须进行UrlEncode之后才可以发送给支付宝。
     *              "store_id":"NJ_001",                                        //商户门店编号。 指商户创建门店时输入的门店编号。
     *              "enable_pay_channels":"pcredit,moneyFund,debitCardExpress", //指定支付渠道。 用户只能使用指定的渠道进行支付，多个渠道以逗号分割。
     *                                                                           与disable_pay_channels互斥，支持传入的值：渠道列表。https://docs.open.alipay.com/common/wifww7
     *                                                                            注：如果传入了指定支付渠道，则用户只能用指定内的渠道支付，包括营销渠道也要指定才能使用。该参数可能导致用户支付受限，慎用。
     *              "disable_pay_channels":"pcredit,moneyFund,debitCardExpress", //禁用渠道,用户不可用指定渠道支付，多个渠道以逗号分割
     *                                                                            注，与enable_pay_channels互斥
     *              "specified_channel":"pcredit",                               //指定单通道。 目前仅支持传入pcredit，若由于用户原因渠道不可用，用户可选择是否用其他渠道支付。 注：该参数不可与花呗分期参数同时传入
     *              "merchant_order_no":"20161008001",                           //商户的原始订单号
     *              "ext_user_info":ExtUserInfo,                                //外部指定买家, ExtUserInfo对象
     *                            {
     *                                     "name":"李明",                           //指定买家姓名。 注： need_check_info=T或fix_buyer=T时该参数才有效
     *                                     "mobile":"16587658765",                 //指定买家手机号。 注：该参数暂不校验
     *                                     "cert_type":"IDENTITY_CARD",            //指定买家证件类型。 枚举值： IDENTITY_CARD：身份证； PASSPORT：护照； OFFICER_CARD：军官证； SOLDIER_CARD：士兵证； HOKOU：户口本。如有其它类型需要支持，请与蚂蚁金服工作人员联系。
     *                                                                               注： need_check_info=T或fix_buyer=T时该参数才有效，支付宝会比较买家在支付宝留存的证件类型与该参数传入的值是否匹配。
     *                                     "cert_no":"362334768769238881",         //买家证件号。 注：need_check_info=T或fix_buyer=T时该参数才有效，支付宝会比较买家在支付宝留存的证件号码与该参数传入的值是否匹配。
     *                                     "min_age":"18",                         //允许的最小买家年龄。 买家年龄必须大于等于所传数值  注： 1. need_check_info=T时该参数才有效   2. min_age为整数，必须大于等于0
     *                                     "fix_buyer":"F",                        //是否强制校验买家身份。 需要强制校验传：T; 不需要强制校验传：F或者不传； 当传T时，接口上必须指定cert_type、cert_no和name信息且支付宝会校验传入的信息跟支付买家的信息都匹配，否则报错。  默认为不校验。
     *                                     "need_check_info":"F"                   //是否强制校验买家信息； 需要强制校验传：T; 不需要强制校验传：F或者不传； 当传T时，支付宝会校验支付买家的信息与接口上传递的cert_type、cert_no、name或age是否匹配，只有接口传递了信息才会进行对应项的校验；只要有任何一项信息校验不匹配交易都会失败。如果传递了need_check_info，但是没有传任何校验项，则不进行任何校验。  默认为不校验。
     *                            }
     *
     *       }
     * @param notify_url                    异步通知url，如果为空，使用this.notifyUrl
     * @param return_url                    同步通知url,如果为空，使用this.returnUrl
     * @return String  null -- 失败
     *       否则返回 支付宝生产的表单FORM的完整html，表单在浏览器中呈现将自动提交
     */
    String wapPay(Map<String, Object> requestData, String notify_url, String return_url);

    /**
     * 获取指定request上下文请求中同步或异步支付宝通知数据
     * @param request           request
     * @return  Map<String, String>  null -- 验签失败
     */
    Map<String, String> getNotifyData(HttpServletRequest request);


    /**
     * 获取当前request上下文请求中同步或异步支付宝通知数据
     * @return  Map<String, String>  null -- 验签失败
     */
    Map<String, String> getNotifyData();


    /**
     * 订单查询
     * @param out_trade_no              外部订单号
     * @return  Map<String, Object>  null -- 失败
     *
     * {
     *         "code":"10000",                                      //网关返回码, 10000 接口调用成功
     *         "msg":"Success",                                     //网关返回码描述
     *         "sub_code": "ACQ.TRADE_HAS_SUCCESS",                 //业务返回码，参见具体的API接口文档
     *         "sub_msg": "交易已被支付",                             //业务返回码描述，参见具体的API接口文档
     *         "buyer_logon_id":"186******28",                      //买家支付宝账号
     *         "buyer_pay_amount":"0.00",                           //买家实付金额，单位为元，两位小数。该金额代表该笔交易买家实际支付的金额，不包含商户折扣等金额
     *         "buyer_user_id":"2088422621978401",                  //买家在支付宝的用户id
     *         "invoice_amount":"0.00",                             //交易中用户支付的可开具发票的金额，单位为元，两位小数。该金额代表该笔交易中可以给用户开具发票的金额
     *         "out_trade_no":"20211027153333tz1BCl",               //商家订单号
     *         "point_amount":"0.00",                               //积分支付的金额，单位为元，两位小数。该金额代表该笔交易中用户使用积分支付的金额，比如集分宝或者支付宝实时优惠等
     *         "receipt_amount":"0.00",                             //实收金额，单位为元，两位小数。该金额为本笔交易，商户账户能够实际收到的金额
     *         "send_pay_date":"2021-10-27 15:33:42",               //本次交易打款给卖家的时间
     *         "total_amount":"0.01",                               //交易的订单金额，单位为元，两位小数。该参数的值为支付时传入的total_amount
     *         "trade_no":"2021102722001478401405326469",           //支付宝交易号
     *         "trade_status":"TRADE_SUCCESS"                       //交易状态：WAIT_BUYER_PAY（交易创建，等待买家付款）、TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、TRADE_SUCCESS（交易支付成功）、TRADE_FINISHED（交易结束，不可退款）
     *     }
     *
     *
     *
     */

    Map<String, Object> orderQuery(String out_trade_no);

    /**
     * 关闭订单
     * 用于交易创建后，用户在一定时间内未进行支付，可调用该接口直接将未付款的交易进行关闭
     * @param out_trade_no                      外部订单号
     * @return Map<String, Object>  null -- 失败
     *
     * {
     *         "code": "10000",                                     //网关返回码, 10000 接口调用成功
     *         "msg": "Success",                                    //网关返回码描述
     *         "sub_code": "ACQ.TRADE_HAS_SUCCESS",                 //业务返回码，参见具体的API接口文档
     *         "sub_msg": "交易已被支付",                             //业务返回码描述，参见具体的API接口文档
     *         "trade_no": "2013112111001004500000675971",          //支付宝交易号
     *         "out_trade_no": "YX_001"                             //商家订单号
     *     }
     */
    Map<String, Object> closeOrder(String out_trade_no);

    /**
     * 统一收单交易退款接口，退款通知自动通知到 notify_url
     * 当交易发生之后一段时间内，由于买家或者卖家的原因需要退款时，卖家可以通过退款接口将支付款退还给买家，支付宝将在收到退款请求并且验证成功之后，按照退款规则将支付款按原路退到买家帐号上。
     * 交易超过约定时间（签约时设置的可退款时间）的订单无法进行退款。
     * 支付宝退款支持单笔交易分多次退款，多次退款需要提交原支付订单的订单号和设置不同的退款请求号。一笔退款失败后重新提交，要保证重试时退款请求号不能变更，防止该笔交易重复退款。
     * 同一笔交易累计提交的退款金额不能超过原始交易总金额。
     *
     * @param requestData               请求参数
     *  {
     *     "out_trade_no": "1217752501201407033233368018",                      //必须String(64),商户订单号
     *     "out_request_no": "1217752501201407033233368018",                    //必须String(64)，退款请求号。
     *                                                                              标识一次退款请求，需要保证在交易号下唯一，如需部分退款，则此参数必传。
     *                                                                               注：针对同一次退款请求，如果调用接口失败或异常了，重试时需要保证退款请求号不能变更，防止该笔交易重复退款。支付宝会保证同样的退款请求号多次请求只会退一次。
     *     "refund_amount": "200.12",                                           //必须,退款金额。
     *                                                                              需要退款的金额，该金额不能大于订单金额，单位为元，支持两位小数。
     *                                                                              注：如果正向交易使用了营销，该退款金额包含营销金额，支付宝会按业务规则分配营销和买家自有资金分别退多少，默认优先退买家的自有资金。如交易总金额100元，用户使用了80元自有资金和20元营销券，则全额退款时应该传入的退款金额是100元。
     *     "refund_reason": "正常退款"                                           //String(256)，退款原因说明
     *  }
     * @return Map<String, Object>  null -- 失败
     *
     * {
     *     "code":"10000",                                              //网关返回码, 10000 接口调用成功
     *     "msg":"Success",                                             //网关返回码描述
     *     "out_trade_no":"2021102700000001",                           //商户订单号
     *     "refund_fee":"0.01",                                         //退款金额
     *     "gmt_refund_pay":"2021-10-27 16:26:44",                      //退款时间
     *     "send_back_fee":"0.00",                                      //本次商户实际退回金额
     *     "trade_no":"2021102722001478401406617362",                   //支付宝交易号
     *     "buyer_logon_id":"186******28",                              //用户的登录id
     *     "buyer_user_id":"2088422621978401",                          //买家在支付宝的用户id
     *     "fund_change":"Y"                                            //本次退款是否发生了资金变化 Y--是
     * }
     */
    Map<String, Object> refund(Map<String, String> requestData);


    /**
     * 统一收单交易退款接口，退款通知自动通知到 notify_url
     * 当交易发生之后一段时间内，由于买家或者卖家的原因需要退款时，卖家可以通过退款接口将支付款退还给买家，支付宝将在收到退款请求并且验证成功之后，按照退款规则将支付款按原路退到买家帐号上。
     * 交易超过约定时间（签约时设置的可退款时间）的订单无法进行退款。
     * 支付宝退款支持单笔交易分多次退款，多次退款需要提交原支付订单的订单号和设置不同的退款请求号。一笔退款失败后重新提交，要保证重试时退款请求号不能变更，防止该笔交易重复退款。
     * 同一笔交易累计提交的退款金额不能超过原始交易总金额。
     *
     * @param requestData               请求参数
     *  {
     *     "out_trade_no": "1217752501201407033233368018",                      //必须String(64),商户订单号
     *     "out_request_no": "1217752501201407033233368018"                     //必须String(64)，退款请求号。
     *  }
     * @return Map<String, Object>  null -- 失败
     *
     * {
     *     "code":"10000",                                              //网关返回码, 10000 接口调用成功
     *     "msg":"Success",                                             //网关返回码描述
     *     "out_trade_no":"2021102700000001",                           //商户订单号
     *     "refund_status":"REFUND_SUCCESS",                            //退款状态。枚举值：
     *                                                                      REFUND_SUCCESS 退款处理成功；
     *                                                                       未返回该字段表示退款请求未收到或者退款失败；
     *                                                                       注：如果退款查询发起时间早于退款时间，或者间隔退款发起时间太短，可能出现退款查询时还没处理成功，后面又处理成功的情况，建议商户在退款发起后间隔10秒以上再发起退款查询请求。
     *     "total_amount":"0.01",                                       //该笔退款所对应的交易的订单金额
     *     "refund_amount":"0.01",                                      //本次退款请求，对应的退款金额
     *     "trade_no":"2021102722001478401406617362",                   //支付宝交易号
     *     "out_request_no":"2021102700000001_refund"                   //本笔退款对应的退款请求号
     * }
     */
    Map<String, Object> refundQuery(Map<String, String> requestData);

    /**
     * 获取对账单下载地址
     * 为方便商户快速查账，支持商户通过本接口获取商户离线账单下载地址
     *
     * @param requestData               请求参数
     *  {
     *     "bill_type": "trade",                                                //账单类型，商户通过接口或商户经开放平台授权后其所属服务商通过接口可以获取以下账单类型，支持：
     *                                                                              trade：商户基于支付宝交易收单的业务账单；
     *                                                                              signcustomer：基于商户支付宝余额收入及支出等资金变动的账务账单。
     *     "bill_date": "2021-04-05"                                            //账单时间：日账单格式为yyyy-MM-dd，最早可下载2016年1月1日开始的日账单；月账单格式为yyyy-MM，最早可下载2016年1月开始的月账单
     *  }
     * @return String  null -- 失败
     *          账单下载地址链接，获取连接后30秒后未下载，链接地址失效。
     * @throws Exception
     */
    String downloadBill(Map<String, String> requestData);


    /**
     * 使用默认异步及同步通知url，电脑网站支付(接口名：alipay.trade.page.pay)
     * @param requestData                   请求数据
     *       {
     *              "out_trade_no":"70501111111S001111119",                     //必填，商户订单号。String(64) 由商家自定义，64个字符以内，仅支持字母、数字、下划线且需保证在商户端不重复。
     *              "total_amount":0.01,                                        //必填，订单总金额。Double 单位为元，精确到小数点后两位，取值范围：[0.01,100000000] 。
     *              "subject":"70501111111S001111119",                          //必填，订单标题。String(256) 注意：不可使用特殊字符，如 /，=，& 等。
     *              "product_code":"FAST_INSTANT_TRADE_PAY",                    //必填，固定为FAST_INSTANT_TRADE_PAY
     *              "body":"Iphone6 16G",                                       //String(128) 订单附加信息。 如果请求时传递了该参数，将在异步通知、对账单中原样返回，同时会在商户和用户的pc账单详情中作为交易描述展示
     *              "qr_pay_mode":"1",                                          //PC扫码支付的方式。
     *                                                                              支持前置模式和跳转模式。
     *                                                                              前置模式是将二维码前置到商户的订单确认页的模式。需要商户在自己的页面中以 iframe 方式请求支付宝页面。具体支持的枚举值有以下几种：
     *                                                                                  0：订单码-简约前置模式，对应 iframe 宽度不能小于600px，高度不能小于300px；
     *                                                                                  1：订单码-前置模式，对应iframe 宽度不能小于 300px，高度不能小于600px；
     *                                                                                  3：订单码-迷你前置模式，对应 iframe 宽度不能小于 75px，高度不能小于75px；
     *                                                                                  4：订单码-可定义宽度的嵌入式二维码，商户可根据需要设定二维码的大小。
     *                                                                              跳转模式下，用户的扫码界面是由支付宝生成的，不在商户的域名下。支持传入的枚举值有：
     *                                                                                  2：订单码-跳转模式
     *
     *              "qrcode_width":"100",                                       //商户自定义二维码宽度。 注：qr_pay_mode=4时该参数有效
     *              "goods_detail":List<GoodsDetail>,                           //订单包含的商品列表信息，List<GoodsDetail>对象
     *                             [
     *                                      {
     *                                          "goods_id":"",                  //必填,商品的编号
     *                                          "alipay_goods_id":"",           //支付宝定义的统一商品编号
     *                                          "goods_name":"",                //必填,商品名称
     *                                          "quantity":1,                   //必填,商品数量
     *                                          "price":0.01,                   //必填,商品单价，单位为元
     *                                          "goods_category":"",            //商品类目
     *                                          "categories_tree":"",           //商品类目树，从商品类目根节点到叶子节点的类目id组成，类目id值使用|分割
     *                                          "body":"",                      //商品描述信息
     *                                          "show_url":""                   //商品的展示地址
     *                                      },...
     *                             ]
     *              "time_expire":"2016-12-31 10:05:00",                        //订单绝对超时时间。 格式为yyyy-MM-dd HH:mm:ss。 注：time_express和timeout_express两者只需传入一个或者都不传，如果两者都传，优先使用time_expire。
     *              "timeout_express":"90m",                                    //订单相对超时时间。 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：5m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。
     *                                                                              该参数数值不接受小数点， 如 1.5h，可转换为 90m。    注：无线支付场景最小值为5m，低于5m支付超时时间按5m计算。
     *                                                                               注：time_express和timeout_express两者只需传入一个或者都不传，如果两者都传，优先使用time_expire。
     *              "extend_params":"ExtendParams json数据",                     //业务扩展参数
     *                              {
     *                                      "sys_service_provider_id": "2088511833207846",      //系统商编号 该参数作为系统商返佣数据提取的依据，请填写系统商签约协议的PID
     *                                      "hb_fq_num": 3,                                     //使用花呗分期要进行的分期数
     *                                      "hb_fq_seller_percent": 100,                        //使用花呗分期需要卖家承担的手续费比例的百分值，传入100代表100%
     *                                      "industry_reflux_info": "{\"scene_code\":\"metro_tradeorder\",\"channel\":\"xxxx\",\"scene_data\":{\"asset_name\":\"ALIPAY\"}}",
     *                                                                                          //行业数据回流信息, 详见：地铁支付接口参数补充说明
     *                                      "card_type": "S0JP0000",                            //卡类型
     *                                      "specified_seller_name": "XXX的跨境小铺"             //特殊场景下，允许商户指定交易展示的卖家名称
     *
     *                              }
     *              "business_params":"{\"data":\"123\"}",                      //商户传入业务信息，具体值要和支付宝约定，应用于安全，营销等参数直传场景，格式为json格式
     *              "promo_params":"{\"storeIdType\":\"1\"}",                   //优惠参数 注：仅与支付宝协商后可用
     *              "passback_params":"merchantBizType%3d3C%26merchantBizNo%3d2016010101111",
     *                                                                          //公用回传参数。 如果请求时传递了该参数，支付宝会在异步通知时将该参数原样返回。 本参数必须进行UrlEncode之后才可以发送给支付宝。
     *              "integration_type":"PCWEB",                                 //请求后页面的集成方式。 枚举值： ALIAPP：支付宝钱包内 PCWEB：PC端访问 默认值为PCWEB。
     *              "request_from_url":"url",                                   //请求来源地址。如果使用ALIAPP的集成方式，用户中途取消支付会返回该地址。
     *              "store_id":"NJ_001",                                        //商户门店编号
     *              "enable_pay_channels":"pcredit,moneyFund,debitCardExpress", //指定支付渠道。 用户只能使用指定的渠道进行支付，多个渠道以逗号分割。
     *                                                                           与disable_pay_channels互斥，支持传入的值：渠道列表。https://docs.open.alipay.com/common/wifww7
     *                                                                            注：如果传入了指定支付渠道，则用户只能用指定内的渠道支付，包括营销渠道也要指定才能使用。该参数可能导致用户支付受限，慎用。
     *              "disable_pay_channels":"pcredit,moneyFund,debitCardExpress", //禁用渠道,用户不可用指定渠道支付，多个渠道以逗号分割
     *                                                                            注，与enable_pay_channels互斥
     *              "merchant_order_no":"20161008001",                          //商户的原始订单号
     *              "invoice_info":InvoiceInfo,                                 //开票信息 InvoiceInfo对象
     *                 {
     *                      "key_info":{                                        //开票关键信息
     *                           "is_support_invoice": "true",                  //该交易是否支持开票
     *                           "invoice_merchant_name": "ABC|003",            //开票商户名称：商户品牌简称|商户门店简称
     *                           "tax_num": "1464888883494"                     //税号
     *
     *                      },
     *                      "details": "[{"code":"100294400","name":"服饰","num":"2","sumPrice":"200.00","taxRate":"6%"}]"
     *                                                                          //开票内容 注：json数组格式
     *                 }
     *              "ext_user_info":ExtUserInfo,                                //外部指定买家, ExtUserInfo对象
     *                            {
     *                                     "name":"李明",                           //指定买家姓名。 注： need_check_info=T或fix_buyer=T时该参数才有效
     *                                     "mobile":"16587658765",                 //指定买家手机号。 注：该参数暂不校验
     *                                     "cert_type":"IDENTITY_CARD",            //指定买家证件类型。 枚举值： IDENTITY_CARD：身份证； PASSPORT：护照； OFFICER_CARD：军官证； SOLDIER_CARD：士兵证； HOKOU：户口本。如有其它类型需要支持，请与蚂蚁金服工作人员联系。
     *                                                                               注： need_check_info=T或fix_buyer=T时该参数才有效，支付宝会比较买家在支付宝留存的证件类型与该参数传入的值是否匹配。
     *                                     "cert_no":"362334768769238881",         //买家证件号。 注：need_check_info=T或fix_buyer=T时该参数才有效，支付宝会比较买家在支付宝留存的证件号码与该参数传入的值是否匹配。
     *                                     "min_age":"18",                         //允许的最小买家年龄。 买家年龄必须大于等于所传数值  注： 1. need_check_info=T时该参数才有效   2. min_age为整数，必须大于等于0
     *                                     "fix_buyer":"F",                        //是否强制校验买家身份。 需要强制校验传：T; 不需要强制校验传：F或者不传； 当传T时，接口上必须指定cert_type、cert_no和name信息且支付宝会校验传入的信息跟支付买家的信息都匹配，否则报错。  默认为不校验。
     *                                     "need_check_info":"F"                   //是否强制校验买家信息； 需要强制校验传：T; 不需要强制校验传：F或者不传； 当传T时，支付宝会校验支付买家的信息与接口上传递的cert_type、cert_no、name或age是否匹配，只有接口传递了信息才会进行对应项的校验；只要有任何一项信息校验不匹配交易都会失败。如果传递了need_check_info，但是没有传任何校验项，则不进行任何校验。  默认为不校验。
     *                            }
     *       }
     * @return String  null -- 失败
     *       否则返回 支付宝生产的表单FORM的完整html，表单在浏览器中呈现将自动提交
     */
    String pcPay(Map<String, Object> requestData);


    /**
     * 使用指定异步及同步通知url，电脑网站支付(接口名：alipay.trade.page.pay)
     * @param requestData                   请求数据
     *       {
     *              "out_trade_no":"70501111111S001111119",                     //必填，商户订单号。String(64) 由商家自定义，64个字符以内，仅支持字母、数字、下划线且需保证在商户端不重复。
     *              "total_amount":0.01,                                        //必填，订单总金额。Double 单位为元，精确到小数点后两位，取值范围：[0.01,100000000] 。
     *              "subject":"70501111111S001111119",                          //必填，订单标题。String(256) 注意：不可使用特殊字符，如 /，=，& 等。
     *              "product_code":"FAST_INSTANT_TRADE_PAY",                    //必填，固定为FAST_INSTANT_TRADE_PAY
     *              "body":"Iphone6 16G",                                       //String(128) 订单附加信息。 如果请求时传递了该参数，将在异步通知、对账单中原样返回，同时会在商户和用户的pc账单详情中作为交易描述展示
     *              "qr_pay_mode":"1",                                          //PC扫码支付的方式。
     *                                                                              支持前置模式和跳转模式。
     *                                                                              前置模式是将二维码前置到商户的订单确认页的模式。需要商户在自己的页面中以 iframe 方式请求支付宝页面。具体支持的枚举值有以下几种：
     *                                                                                  0：订单码-简约前置模式，对应 iframe 宽度不能小于600px，高度不能小于300px；
     *                                                                                  1：订单码-前置模式，对应iframe 宽度不能小于 300px，高度不能小于600px；
     *                                                                                  3：订单码-迷你前置模式，对应 iframe 宽度不能小于 75px，高度不能小于75px；
     *                                                                                  4：订单码-可定义宽度的嵌入式二维码，商户可根据需要设定二维码的大小。
     *                                                                              跳转模式下，用户的扫码界面是由支付宝生成的，不在商户的域名下。支持传入的枚举值有：
     *                                                                                  2：订单码-跳转模式
     *
     *              "qrcode_width":"100",                                       //商户自定义二维码宽度。 注：qr_pay_mode=4时该参数有效
     *              "goods_detail":List<GoodsDetail>,                           //订单包含的商品列表信息，List<GoodsDetail>对象
     *                             [
     *                                      {
     *                                          "goods_id":"",                  //必填,商品的编号
     *                                          "alipay_goods_id":"",           //支付宝定义的统一商品编号
     *                                          "goods_name":"",                //必填,商品名称
     *                                          "quantity":1,                   //必填,商品数量
     *                                          "price":0.01,                   //必填,商品单价，单位为元
     *                                          "goods_category":"",            //商品类目
     *                                          "categories_tree":"",           //商品类目树，从商品类目根节点到叶子节点的类目id组成，类目id值使用|分割
     *                                          "body":"",                      //商品描述信息
     *                                          "show_url":""                   //商品的展示地址
     *                                      },...
     *                             ]
     *              "time_expire":"2016-12-31 10:05:00",                        //订单绝对超时时间。 格式为yyyy-MM-dd HH:mm:ss。 注：time_express和timeout_express两者只需传入一个或者都不传，如果两者都传，优先使用time_expire。
     *              "timeout_express":"90m",                                    //订单相对超时时间。 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：5m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。
     *                                                                              该参数数值不接受小数点， 如 1.5h，可转换为 90m。    注：无线支付场景最小值为5m，低于5m支付超时时间按5m计算。
     *                                                                               注：time_express和timeout_express两者只需传入一个或者都不传，如果两者都传，优先使用time_expire。
     *              "extend_params":"ExtendParams json数据",                     //业务扩展参数
     *                              {
     *                                      "sys_service_provider_id": "2088511833207846",      //系统商编号 该参数作为系统商返佣数据提取的依据，请填写系统商签约协议的PID
     *                                      "hb_fq_num": 3,                                     //使用花呗分期要进行的分期数
     *                                      "hb_fq_seller_percent": 100,                        //使用花呗分期需要卖家承担的手续费比例的百分值，传入100代表100%
     *                                      "industry_reflux_info": "{\"scene_code\":\"metro_tradeorder\",\"channel\":\"xxxx\",\"scene_data\":{\"asset_name\":\"ALIPAY\"}}",
     *                                                                                          //行业数据回流信息, 详见：地铁支付接口参数补充说明
     *                                      "card_type": "S0JP0000",                            //卡类型
     *                                      "specified_seller_name": "XXX的跨境小铺"             //特殊场景下，允许商户指定交易展示的卖家名称
     *
     *                              }
     *              "business_params":"{\"data":\"123\"}",                      //商户传入业务信息，具体值要和支付宝约定，应用于安全，营销等参数直传场景，格式为json格式
     *              "promo_params":"{\"storeIdType\":\"1\"}",                   //优惠参数 注：仅与支付宝协商后可用
     *              "passback_params":"merchantBizType%3d3C%26merchantBizNo%3d2016010101111",
     *                                                                          //公用回传参数。 如果请求时传递了该参数，支付宝会在异步通知时将该参数原样返回。 本参数必须进行UrlEncode之后才可以发送给支付宝。
     *              "integration_type":"PCWEB",                                 //请求后页面的集成方式。 枚举值： ALIAPP：支付宝钱包内 PCWEB：PC端访问 默认值为PCWEB。
     *              "request_from_url":"url",                                   //请求来源地址。如果使用ALIAPP的集成方式，用户中途取消支付会返回该地址。
     *              "store_id":"NJ_001",                                        //商户门店编号
     *              "enable_pay_channels":"pcredit,moneyFund,debitCardExpress", //指定支付渠道。 用户只能使用指定的渠道进行支付，多个渠道以逗号分割。
     *                                                                           与disable_pay_channels互斥，支持传入的值：渠道列表。https://docs.open.alipay.com/common/wifww7
     *                                                                            注：如果传入了指定支付渠道，则用户只能用指定内的渠道支付，包括营销渠道也要指定才能使用。该参数可能导致用户支付受限，慎用。
     *              "disable_pay_channels":"pcredit,moneyFund,debitCardExpress", //禁用渠道,用户不可用指定渠道支付，多个渠道以逗号分割
     *                                                                            注，与enable_pay_channels互斥
     *              "merchant_order_no":"20161008001",                          //商户的原始订单号
     *              "invoice_info":InvoiceInfo,                                 //开票信息 InvoiceInfo对象
     *                 {
     *                      "key_info":{                                        //开票关键信息
     *                           "is_support_invoice": "true",                  //该交易是否支持开票
     *                           "invoice_merchant_name": "ABC|003",            //开票商户名称：商户品牌简称|商户门店简称
     *                           "tax_num": "1464888883494"                     //税号
     *
     *                      },
     *                      "details": "[{"code":"100294400","name":"服饰","num":"2","sumPrice":"200.00","taxRate":"6%"}]"
     *                                                                          //开票内容 注：json数组格式
     *                 }
     *              "ext_user_info":ExtUserInfo,                                //外部指定买家, ExtUserInfo对象
     *                            {
     *                                     "name":"李明",                           //指定买家姓名。 注： need_check_info=T或fix_buyer=T时该参数才有效
     *                                     "mobile":"16587658765",                 //指定买家手机号。 注：该参数暂不校验
     *                                     "cert_type":"IDENTITY_CARD",            //指定买家证件类型。 枚举值： IDENTITY_CARD：身份证； PASSPORT：护照； OFFICER_CARD：军官证； SOLDIER_CARD：士兵证； HOKOU：户口本。如有其它类型需要支持，请与蚂蚁金服工作人员联系。
     *                                                                               注： need_check_info=T或fix_buyer=T时该参数才有效，支付宝会比较买家在支付宝留存的证件类型与该参数传入的值是否匹配。
     *                                     "cert_no":"362334768769238881",         //买家证件号。 注：need_check_info=T或fix_buyer=T时该参数才有效，支付宝会比较买家在支付宝留存的证件号码与该参数传入的值是否匹配。
     *                                     "min_age":"18",                         //允许的最小买家年龄。 买家年龄必须大于等于所传数值  注： 1. need_check_info=T时该参数才有效   2. min_age为整数，必须大于等于0
     *                                     "fix_buyer":"F",                        //是否强制校验买家身份。 需要强制校验传：T; 不需要强制校验传：F或者不传； 当传T时，接口上必须指定cert_type、cert_no和name信息且支付宝会校验传入的信息跟支付买家的信息都匹配，否则报错。  默认为不校验。
     *                                     "need_check_info":"F"                   //是否强制校验买家信息； 需要强制校验传：T; 不需要强制校验传：F或者不传； 当传T时，支付宝会校验支付买家的信息与接口上传递的cert_type、cert_no、name或age是否匹配，只有接口传递了信息才会进行对应项的校验；只要有任何一项信息校验不匹配交易都会失败。如果传递了need_check_info，但是没有传任何校验项，则不进行任何校验。  默认为不校验。
     *                            }
     *       }
     * @param notify_url                    异步通知url，如果为空，使用this.notifyUrl
     * @param return_url                    同步通知url,如果为空，使用this.returnUrl
     * @return String  null -- 失败
     *       否则返回 支付宝生产的表单FORM的完整html，表单在浏览器中呈现将自动提交
     */
    String pcPay(Map<String, Object> requestData, String notify_url, String return_url);

    /**
     * 单笔转账接口，应用需要使用公钥证书方式，否则无法转帐，请参考： https://opendocs.alipay.com/open/291/105971#LDsXr
     *
     * @param requestData               请求参数
     *   {
     *      "out_biz_no": "201806300001",                                       //必须String(64)，商家侧唯一转账订单号，由商家自定义。对于不同转账请求，商家需保证该订单号在自身系统唯一
     *      "trans_amount": "1.01",                                             //必须Price，订单总金额，单位为元，精确到小数点后两位，STD_RED_PACKET产品取值范围[0.01,100000000]； TRANS_ACCOUNT_NO_PWD产品取值范围[0.1,100000000]
     *      "product_code": "TRANS_ACCOUNT_NO_PWD",                             //必须String(64)，业务产品码， 单笔无密转账到支付宝账户固定为: TRANS_ACCOUNT_NO_PWD； 收发现金红包固定为: STD_RED_PACKET；
     *      "biz_scene": "DIRECT_TRANSFER",                                     //描述特定的业务场景，可传的参数如下： DIRECT_TRANSFER：单笔无密转账到支付宝，B2C现金红包; PERSONAL_COLLECTION：C2C现金红包-领红包
     *      "order_title": "转账标题",                                            //必须String(64)，转账标题
     *      "original_order_id": "20190620110075000006640000063056",             //String(64)，原支付宝业务单号。C2C现金红包-红包领取时，传红包支付时返回的支付宝单号；B2C现金红包、单笔无密转账到支付宝不需要该参数
     *      "remark": "单笔转账",                                                 //String(200)，业务备注
     *      "business_params": "{"sub_biz_scene":"REDPACKET"}"                   //String(2048)，转账业务请求的扩展参数，支持传入的扩展参数如下： sub_biz_scene 子业务场景，红包业务必传，取值REDPACKET，C2C现金红包、B2C现金红包均需传入
     *   }
     * @param payee_info               收款方信息
     *      {
     *          "identity":"2088123412341234",                                  //必须String(64),参与方的唯一标识
     *          "identity_type":"ALIPAY_USER_ID",                               //必须String(64),参与方的标识类型，目前支持如下类型： 1、ALIPAY_USER_ID 支付宝的会员ID 2、ALIPAY_LOGON_ID：支付宝登录号，支持邮箱和手机号格式
     *          "name":"黄龙国际有限公司",                                         //必须String(256),参与方真实姓名，如果非空，将校验收款支付宝账号姓名一致性。当identity_type=ALIPAY_LOGON_ID时，本字段必填。
     *      }
     *
     * @return Map<String, Object> null --失败
     * {
     *         "code":"10000",                                              //网关返回码, 10000 接口调用成功
     *         "msg":"Success",                                             //网关返回码描述
     *         "out_biz_no": "201808080001",                                //商家侧唯一转账订单号
     *         "order_id": "20190801110070000006380000250621",              //支付宝转账订单号
     *         "pay_fund_order_id": "20190801110070001506380000251556",     //支付宝支付资金流水号
     *         "status": "SUCCESS",                                         //转账单据状态。 SUCCESS：成功； FAIL：失败（具体失败原因请参见error_code以及fail_reason返回值）； DEALING：处理中； REFUND：退票；
     *         "trans_date": "2019-08-21 00:00:00"                          //转账时间
     * }
     *
     */
    Map<String, Object> transfer(Map<String, Object> requestData, Participant payee_info);
}
