package com.ihltx.utility.weixin.impl;

import com.ihltx.utility.httpclient.service.RestTemplateUtil;
import com.ihltx.utility.httpclient.service.impl.RestTemplateUtilImpl;
import com.ihltx.utility.redis.exception.RedisException;
import com.ihltx.utility.redis.service.RedisFactory;
import com.ihltx.utility.redis.service.RedisUtil;
import com.ihltx.utility.util.*;
import com.ihltx.utility.weixin.WeixinPayUtil;
import com.ihltx.utility.weixin.WeixinUtil;
import com.ihltx.utility.weixin.entity.Button;
import com.ihltx.utility.weixin.entity.Menu;
import com.ihltx.utility.weixin.entity.*;
import com.ihltx.utility.weixin.event.MessageEvent;
import com.ihltx.utility.weixin.event.MessageEventHandler;
import com.ihltx.utility.weixin.event.MessageEventType;
import com.ihltx.utility.weixin.pay.WXPayConstants;
import com.ihltx.utility.weixin.pay.WXPayUtil;
import com.ihltx.utility.weixin.tools.WeChatContant;
import com.ihltx.utility.weixin.tools.WeChatUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ihltx.utility.weixin.pay.WXPayConstants.USER_AGENT;

/**
 * WeixinUtil
 * WeixinUtil utility class
 * @author liulin 84611913@qq.com
 *
 */
public class WeixinPayUtilImpl implements WeixinPayUtil {

    private static final String WEIXIN_CHARSET = "ISO-8859-1";


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



    private String mchId;
    @Override
    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    @Override
    public String getMchId() {
        return this.mchId;
    }

    private String apiKey;
    @Override
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public String getApiKey() {
        return this.apiKey;
    }

    private String certFileName;
    @Override
    public void setCertFileName(String certFileName) {
        this.certFileName = certFileName;
    }

    @Override
    public String getCertFileName() {
        return this.certFileName;
    }

    private WXPayConstants.SignType signType;
    @Override
    public void setSignType(WXPayConstants.SignType signType) {
        this.signType = signType;
    }

    @Override
    public WXPayConstants.SignType getSignType() {
        return this.signType;
    }



    public WeixinPayUtilImpl(){

    }

    public WeixinPayUtilImpl(String appID , String mchId , String apiKey , String certFileName , WXPayConstants.SignType signType){
        this.appID = appID;
        this.mchId = mchId;
        this.apiKey = apiKey;
        this.certFileName = certFileName;
        this.signType = signType;

    }

    @Override
    public Map<String, String> jsApiPayUnifiedOrder(Map<String,String> requestData) throws Exception {
        Map<String, String> payMap = null;
        String api_url = WEIXIN_PAY_API_URL + "/pay/unifiedorder";
        requestData.put("appid" , this.getAppID());
        requestData.put("mch_id" , this.getMchId());
        Long timestamp= DateUtil.getTime();
        String nonce_str = StringUtil.getRandomString(32);
        requestData.put("nonce_str" , nonce_str);
        requestData.put("trade_type" , "JSAPI");
        if(this.getSignType() == WXPayConstants.SignType.HMACSHA256){
            requestData.put("sign_type" ,WXPayConstants.HMACSHA256);
        }else{
            requestData.put("sign_type" , this.getSignType().toString());
        }
        requestData.put("sign" ,  WXPayUtil.generateSignature(requestData, this.getApiKey(), this.getSignType()));
        String reqBody = WXPayUtil.mapToXml(requestData);
        String rs = request(api_url,this.getMchId(),reqBody);
        if(!StringUtil.isNullOrEmpty(rs)){
            payMap = processResponseXml(rs);
            if(payMap.get("return_code").equals("SUCCESS") && payMap.get("result_code").equals("SUCCESS")){
                String prepay_id = payMap.get("prepay_id");
                payMap = new HashMap<>();
                payMap.put("appId" , this.getAppID());
                payMap.put("timeStamp" , timestamp.toString());
                payMap.put("nonceStr" , nonce_str);
                payMap.put("signType" , "MD5");
                payMap.put("package" , "prepay_id=" + prepay_id);
                String paySign = WXPayUtil.generateSignature(payMap , this.getApiKey());
                payMap.put("paySign" , paySign);
            }
        }
        return payMap;
    }

    @Override
    public void pcPayUnifiedOrder(Map<String,String> requestData) throws Exception {
        pcPayUnifiedOrder(requestData, null);
    }

    @Override
    public void pcPayUnifiedOrder(Map<String, String> requestData, HttpServletResponse response) throws Exception {
        if(response==null){
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if(servletRequestAttributes!=null) {
                response = servletRequestAttributes.getResponse();
            }
        }
        String errorMessage = "-1 unkown error";
        if(response!=null){
            String api_url =WEIXIN_PAY_API_URL + "/pay/unifiedorder";
            requestData.put("appid" , this.getAppID());
            requestData.put("mch_id" , this.getMchId());
            String nonce_str = StringUtil.getRandomString(32);
            requestData.put("nonce_str" , nonce_str);
            requestData.put("trade_type" , "NATIVE");
            if(this.getSignType() == WXPayConstants.SignType.HMACSHA256){
                requestData.put("sign_type" ,WXPayConstants.HMACSHA256);
            }else{
                requestData.put("sign_type" , this.getSignType().toString());
            }

            requestData.put("sign" ,  WXPayUtil.generateSignature(requestData, this.getApiKey(), this.getSignType()));
            String reqBody = WXPayUtil.mapToXml(requestData);
            String rs = request(api_url,this.getMchId(),reqBody);
            if(!StringUtil.isNullOrEmpty(rs)){
                Map<String, String> responseMap = processResponseXml(rs);
                if(responseMap.get("return_code").equals("SUCCESS") && responseMap.get("result_code").equals("SUCCESS")){
                    QrCodeUtil.generate(responseMap.get("code_url"),400,400, response.getOutputStream());
                    return;
                }else{
                    if(responseMap.containsKey("err_code_des")){
                        errorMessage = responseMap.get("err_code_des");
                    }
                }
            }else{
                errorMessage = "-1 no response data";
            }
            GraphicsUtil.generateImage(400,400, Color.BLACK,null,new Font("宋体", Font.CENTER_BASELINE,12),Color.white,errorMessage,20,50,response.getOutputStream());
        }
    }


    @Override
    public Map<String, String> h5PayUnifiedOrder(Map<String, String> requestData, String redirect_url) throws Exception {
        Map<String, String> payMap = null;
        String api_url = WEIXIN_PAY_API_URL + "/pay/unifiedorder";
        requestData.put("appid" , this.getAppID());
        requestData.put("mch_id" , this.getMchId());
        String nonce_str = StringUtil.getRandomString(32);
        requestData.put("nonce_str" , nonce_str);
        requestData.put("trade_type" , "MWEB");
        if(this.getSignType() == WXPayConstants.SignType.HMACSHA256){
            requestData.put("sign_type" ,WXPayConstants.HMACSHA256);
        }else{
            requestData.put("sign_type" , this.getSignType().toString());
        }
        requestData.put("sign" ,  WXPayUtil.generateSignature(requestData, this.getApiKey(), this.getSignType()));
        String reqBody = WXPayUtil.mapToXml(requestData);
        String rs = request(api_url,this.getMchId(),reqBody);
        if(!StringUtil.isNullOrEmpty(rs)){
            payMap = processResponseXml(rs);
            if(payMap!=null && payMap.containsKey("mweb_url")){
                String mweb_url = payMap.get("mweb_url");
                if(mweb_url.contains("?")){
                    mweb_url += "&redirect_url=" + SecurityUtil.urlEncode(redirect_url);
                }else{
                    mweb_url += "?redirect_url=" + SecurityUtil.urlEncode(redirect_url);
                }
                payMap.put("mweb_url" , mweb_url);
            }
        }
        return payMap;
    }

    @Override
    public Map<String, String> getNotifyData(HttpServletRequest request) throws Exception {
        Map<String ,String > requestData = null;
        if(request==null){
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if(servletRequestAttributes!=null){
                request = servletRequestAttributes.getRequest();
            }
        }
        if(request!=null){
            requestData = WeChatUtil.parseXml(request);
            if(!isResponseSignatureValid(requestData)){
                requestData = null;
            }
        }
        return requestData;
    }

    @Override
    public Map<String, String> getNotifyData() throws Exception {
        return getNotifyData(null);
    }


    @Override
    public Map<String, String> orderQuery(String out_trade_no) throws Exception {
        Map<String, String> requestData = new HashMap<>();
        Map<String, String> resultMap = null;
        String api_url =WEIXIN_PAY_API_URL + "/pay/orderquery";
        requestData.put("appid" , this.getAppID());
        requestData.put("mch_id" , this.getMchId());
        requestData.put("out_trade_no" , out_trade_no);
        String nonce_str = StringUtil.getRandomString(32);
        requestData.put("nonce_str" , nonce_str);
        if(this.getSignType() == WXPayConstants.SignType.HMACSHA256){
            requestData.put("sign_type" ,WXPayConstants.HMACSHA256);
        }else{
            requestData.put("sign_type" , this.getSignType().toString());
        }
        requestData.put("sign" ,  WXPayUtil.generateSignature(requestData, this.getApiKey(), this.getSignType()));
        String reqBody = WXPayUtil.mapToXml(requestData);
        String rs = request(api_url,this.getMchId(),reqBody);
        if(!StringUtil.isNullOrEmpty(rs)){
            resultMap = processResponseXml(rs);
        }
        return resultMap;
    }

    @Override
    public Map<String, String> closeOrder(String out_trade_no) throws Exception {
        Map<String, String> requestData = new HashMap<>();
        Map<String, String> resultMap = null;
        String api_url =WEIXIN_PAY_API_URL + "/pay/closeorder";
        requestData.put("appid" , this.getAppID());
        requestData.put("mch_id" , this.getMchId());
        requestData.put("out_trade_no" , out_trade_no);
        String nonce_str = StringUtil.getRandomString(32);
        requestData.put("nonce_str" , nonce_str);
        if(this.getSignType() == WXPayConstants.SignType.HMACSHA256){
            requestData.put("sign_type" ,WXPayConstants.HMACSHA256);
        }else{
            requestData.put("sign_type" , this.getSignType().toString());
        }
        requestData.put("sign" ,  WXPayUtil.generateSignature(requestData, this.getApiKey(), this.getSignType()));
        String reqBody = WXPayUtil.mapToXml(requestData);
        String rs = request(api_url,this.getMchId(),reqBody);
        if(!StringUtil.isNullOrEmpty(rs)){
            resultMap = processResponseXml(rs);
        }
        return resultMap;
    }

    @Override
    public Map<String, String> refund(Map<String, String> requestData) throws Exception {
        Map<String, String> resultMap = null;
        String api_url =WEIXIN_PAY_API_URL + "/secapi/pay/refund";
        requestData.put("appid" , this.getAppID());
        requestData.put("mch_id" , this.getMchId());
        String nonce_str = StringUtil.getRandomString(32);
        requestData.put("nonce_str" , nonce_str);
        if(this.getSignType() == WXPayConstants.SignType.HMACSHA256){
            requestData.put("sign_type" ,WXPayConstants.HMACSHA256);
        }else{
            requestData.put("sign_type" , this.getSignType().toString());
        }
        requestData.put("sign" ,  WXPayUtil.generateSignature(requestData, this.getApiKey(), this.getSignType()));
        String reqBody = WXPayUtil.mapToXml(requestData);
        String rs = request(api_url,this.getMchId(),reqBody , true);
        if(!StringUtil.isNullOrEmpty(rs)){
            resultMap = processResponseXml(rs);
        }
        return resultMap;
    }


    @Override
    public Map<String, String> getRefundNotifyData(HttpServletRequest request) throws Exception {
        Map<String ,String > requestData = null;
        if(request==null){
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if(servletRequestAttributes!=null){
                request = servletRequestAttributes.getRequest();
            }
        }
        if(request!=null){
            requestData = WeChatUtil.parseXml(request);
            if(requestData!=null && requestData.containsKey("req_info")){
                Map<String ,String > refundData = decryptRefundReqInfo(requestData.get("req_info"));
                if(refundData!=null){
                    requestData.putAll(refundData);
                }
            }
        }
        return requestData;
    }

    @Override
    public Map<String, String> getRefundNotifyData() throws Exception {
        return getRefundNotifyData(null);
    }

    @Override
    public Map<String, String> decryptRefundReqInfo(String req_info) throws Exception {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] base64ByteArr = decoder.decode(req_info);
        String key = SecurityUtil.md5(this.getApiKey()).toLowerCase();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return WeChatUtil.parseXml(new String(cipher.doFinal(base64ByteArr)));
    }

    @Override
    public Map<String, String> refundQuery(Map<String, String> requestData) throws Exception {
        Map<String, String> resultMap = null;
        String api_url =WEIXIN_PAY_API_URL + "/pay/refundquery";
        requestData.put("appid" , this.getAppID());
        requestData.put("mch_id" , this.getMchId());
        String nonce_str = StringUtil.getRandomString(32);
        requestData.put("nonce_str" , nonce_str);
        if(this.getSignType() == WXPayConstants.SignType.HMACSHA256){
            requestData.put("sign_type" ,WXPayConstants.HMACSHA256);
        }else{
            requestData.put("sign_type" , this.getSignType().toString());
        }
        requestData.put("sign" ,  WXPayUtil.generateSignature(requestData, this.getApiKey(), this.getSignType()));
        String reqBody = WXPayUtil.mapToXml(requestData);
        String rs = request(api_url,this.getMchId(),reqBody );
        if(!StringUtil.isNullOrEmpty(rs)){
            resultMap = processResponseXml(rs);
        }
        return resultMap;
    }

    @Override
    public List<List<String>> downloadBill(Map<String, String> requestData) throws Exception {
        String api_url =WEIXIN_PAY_API_URL + "/pay/downloadbill";
        requestData.put("appid" , this.getAppID());
        requestData.put("mch_id" , this.getMchId());
        String nonce_str = StringUtil.getRandomString(32);
        requestData.put("nonce_str" , nonce_str);
        if(this.getSignType() == WXPayConstants.SignType.HMACSHA256){
            requestData.put("sign_type" ,WXPayConstants.HMACSHA256);
        }else{
            requestData.put("sign_type" , this.getSignType().toString());
        }
        requestData.put("sign" ,  WXPayUtil.generateSignature(requestData, this.getApiKey(), this.getSignType()));
        String reqBody = WXPayUtil.mapToXml(requestData);
        String rs = request(api_url,this.getMchId(),reqBody );
        List<List<String>> listData = null;
        if(!StringUtil.isNullOrEmpty(rs)){
            StringReader stringReader =new StringReader(rs);
            listData = CsvUtil.readCSVByReader(stringReader,true , ',');
        }
        return listData;
    }


    @Override
    public List<List<String>> downloadFundflow(Map<String, String> requestData) throws Exception {
        String api_url =WEIXIN_PAY_API_URL + "/pay/downloadfundflow";
        requestData.put("appid" , this.getAppID());
        requestData.put("mch_id" , this.getMchId());
        String nonce_str = StringUtil.getRandomString(32);
        requestData.put("nonce_str" , nonce_str);
        if(signType!= WXPayConstants.SignType.HMACSHA256){
            signType = WXPayConstants.SignType.HMACSHA256;
        }
        if(this.getSignType() == WXPayConstants.SignType.HMACSHA256){
            requestData.put("sign_type" ,WXPayConstants.HMACSHA256);
        }else{
            requestData.put("sign_type" , this.getSignType().toString());
        }
        requestData.put("sign" ,  WXPayUtil.generateSignature(requestData, this.getApiKey(), this.getSignType()));
        String reqBody = WXPayUtil.mapToXml(requestData);
        String rs = request(api_url,this.getMchId(),reqBody , true);
        List<List<String>> listData = null;
        if(!StringUtil.isNullOrEmpty(rs)){
            StringReader stringReader =new StringReader(rs);
            listData = CsvUtil.readCSVByReader(stringReader,true , ',');
        }
        return listData;
    }


    @Override
    public Map<String, String> transfer(Map<String, String> requestData) throws Exception {
        Map<String, String> resultMap = null;
        String api_url =WEIXIN_PAY_API_URL + "/mmpaymkttransfers/promotion/transfers";
        requestData.put("mch_appid" , this.getAppID());
        requestData.put("mchid" , this.getMchId());
        String nonce_str = StringUtil.getRandomString(32);
        requestData.put("nonce_str" , nonce_str);
        requestData.put("sign" ,  WXPayUtil.generateSignature(requestData, this.getApiKey(), this.getSignType()));
        String reqBody = WXPayUtil.mapToXml(requestData);
        String rs = request(api_url,this.getMchId(),reqBody , true);
        if(!StringUtil.isNullOrEmpty(rs)){
            resultMap = WXPayUtil.xmlToMap(rs);
        }
        return resultMap;
    }


    /**
     * 请求，只请求一次，不做重试,不使用双向证书
     *
     * @param url                       请求完整url
     * @param mch_id                    商户号
     * @param data                      请求数据
     * @return String
     * @throws Exception
     */
    private String request(String url , String mch_id, String data) throws Exception {
        return request(url , mch_id, data, false);
    }


    /**
     * 请求，只请求一次，不做重试
     * 如果需要使用双向证书[apiclient_cert.p12]
     * 注意需要将jdk/jre环境中/jre/lib/security相应的：local_policy.jar和US_export_policy.jar 两个文件替换为无政策限制文件，下载路径：
     * jdk8: http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
     *
     * @param url                       请求完整url
     * @param mch_id                    商户号
     * @param data                      请求数据
     * @param useCert                   是否使用双向证书[apiclient_cert.p12]
     * @return String
     * @throws Exception
     */
    private String request(String url , String mch_id, String data, Boolean useCert) throws Exception {
        int connectTimeoutMs = 10000;
        int readTimeoutMs = 30000;
        BasicHttpClientConnectionManager connManager;
        if (useCert && !StringUtil.isNullOrEmpty(this.getCertFileName()) && FileUtil.isFile(this.getCertFileName())) {
            // 证书
            char[] password = mch_id.toCharArray();
            InputStream certStream = new FileInputStream(this.getCertFileName());
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(certStream, password);

            // 实例化密钥库 & 初始化密钥工厂
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, password);

            // 创建 SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), null, new SecureRandom());

            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                    sslContext,
                    new String[]{"TLSv1"},
                    null,
                    new DefaultHostnameVerifier());

            connManager = new BasicHttpClientConnectionManager(
                    RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("http", PlainConnectionSocketFactory.getSocketFactory())
                            .register("https", sslConnectionSocketFactory)
                            .build(),
                    null,
                    null,
                    null
            );
        }
        else {
            connManager = new BasicHttpClientConnectionManager(
                    RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("http", PlainConnectionSocketFactory.getSocketFactory())
                            .register("https", SSLConnectionSocketFactory.getSocketFactory())
                            .build(),
                    null,
                    null,
                    null
            );
        }

        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(connManager)
                .build();

        HttpPost httpPost = new HttpPost(url);

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeoutMs).setConnectTimeout(connectTimeoutMs).build();
        httpPost.setConfig(requestConfig);

        StringEntity postEntity = new StringEntity(data, "UTF-8");
        httpPost.addHeader("Content-Type", "text/xml");
        httpPost.addHeader("User-Agent", USER_AGENT + " " +mch_id);
        httpPost.setEntity(postEntity);

        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity httpEntity = httpResponse.getEntity();
        return EntityUtils.toString(httpEntity, "UTF-8");

    }


    /**
     * 处理 HTTPS API返回数据，转换成Map对象。return_code为SUCCESS时，验证签名。
     * @param xmlStr API返回的XML格式数据
     * @return Map类型数据
     * @throws Exception
     */
    private Map<String, String> processResponseXml(String xmlStr) throws Exception {
        String RETURN_CODE = "return_code";
        String return_code;
        Map<String, String> respData = WXPayUtil.xmlToMap(xmlStr);
        if (respData.containsKey(RETURN_CODE)) {
            return_code = respData.get(RETURN_CODE);
        }
        else {
            throw new Exception(String.format("No `return_code` in XML: %s", xmlStr));
        }

        if (return_code.equals(WXPayConstants.FAIL)) {
            return respData;
        }
        else if (return_code.equals(WXPayConstants.SUCCESS)) {
            if (this.isResponseSignatureValid(respData)) {
                return respData;
            }
            else {
                throw new Exception(String.format("Invalid sign value in XML: %s", xmlStr));
            }
        }
        else {
            throw new Exception(String.format("return_code value %s is invalid in XML: %s", return_code, xmlStr));
        }
    }

    /**
     * 判断xml数据的sign是否有效，必须包含sign字段，否则返回false。
     *
     * @param reqData 向wxpay post的请求数据
     * @return 签名是否有效
     * @throws Exception
     */
    public boolean isResponseSignatureValid(Map<String, String> reqData) throws Exception {
        // 返回数据的签名方式和请求中给定的签名方式是一致的
        return WXPayUtil.isSignatureValid(reqData, this.getApiKey(), this.getSignType());
    }

    @Override
    public Map<String, String> weappPayUnifiedOrder(Map<String,String> requestData) throws Exception {
        Map<String, String> payMap = null;
        String api_url = WEIXIN_PAY_API_URL + "/pay/unifiedorder";
        requestData.put("appid" , this.getAppID());
        requestData.put("mch_id" , this.getMchId());
        Long timestamp= DateUtil.getTime();
        String nonce_str = StringUtil.getRandomString(32);
        requestData.put("nonce_str" , nonce_str);
        requestData.put("trade_type" , "JSAPI");
        if(this.getSignType() == WXPayConstants.SignType.HMACSHA256){
            requestData.put("sign_type" ,WXPayConstants.HMACSHA256);
        }else{
            requestData.put("sign_type" , this.getSignType().toString());
        }
        requestData.put("sign" ,  WXPayUtil.generateSignature(requestData, this.getApiKey(), this.getSignType()));
        String reqBody = WXPayUtil.mapToXml(requestData);
        String rs = request(api_url,this.getMchId(),reqBody);
        if(!StringUtil.isNullOrEmpty(rs)){
            payMap = processResponseXml(rs);
            if(payMap.get("return_code").equals("SUCCESS") && payMap.get("result_code").equals("SUCCESS")){
                String prepay_id = payMap.get("prepay_id");
                payMap = new HashMap<>();
                payMap.put("appId" , this.getAppID());
                payMap.put("timeStamp" , timestamp.toString());
                payMap.put("nonceStr" , nonce_str);
                payMap.put("signType" , "MD5");
                payMap.put("package" , "prepay_id=" + prepay_id);
                String paySign = WXPayUtil.generateSignature(payMap , this.getApiKey());
                payMap.put("paySign" , paySign);
            }
        }
        return payMap;
    }
}
