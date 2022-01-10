package com.ihltx.utility.alipay.impl;

import com.ihltx.utility.alipay.AlipayUtil;
import com.ihltx.utility.httpclient.service.RestTemplateUtil;
import com.ihltx.utility.httpclient.service.impl.RestTemplateUtilImpl;
import com.ihltx.utility.util.ObjectUtil;
import com.ihltx.utility.util.StringUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.AlipayTradeQueryResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AlipayUtilImpl  implements AlipayUtil {

    private String appID;
    @Override
    public String getAppID() {
        return this.appID;
    }

    @Override
    public void setAppID(String appID) {
        this.appID = appID;
    }


    private String appPrivateKey;
    @Override
    public String getAppPrivateKey() {
        return this.appPrivateKey;
    }

    @Override
    public void setAppPrivateKey(String appPrivateKey) {
        this.appPrivateKey = appPrivateKey;
    }

    private String appCertPath;
    @Override
    public String getAppCertPath() {
        return this.appCertPath;
    }

    @Override
    public void setAppCertPath(String appCertPath) {
        this.appCertPath = appCertPath;
    }

    private String alipayPublicCertPath;
    @Override
    public String getAlipayPublicCertPath() {
        return this.alipayPublicCertPath;
    }

    @Override
    public void setAlipayPublicCertPath(String alipayPublicCertPath) {
        this.alipayPublicCertPath = alipayPublicCertPath;
    }

    private String alipayRootCertPath;
    @Override
    public String getAlipayRootCertPath() {
        return this.alipayRootCertPath;
    }

    @Override
    public void setAlipayRootCertPath(String alipayRootCertPath) {
        this.alipayRootCertPath = alipayRootCertPath;
    }

    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }


    private String notifyUrl;
    @Override
    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    @Override
    public String getNotifyUrl() {
        return this.notifyUrl;
    }

    private String returnUrl;
    @Override
    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    @Override
    public String getReturnUrl() {
        return this.returnUrl;
    }

    public AlipayUtilImpl(){

    }

    public AlipayUtilImpl(String appID , String appPrivateKey, String appCertPath, String alipayPublicCertPath, String alipayRootCertPath){
        this.appID = appID;
        this.appPrivateKey = appPrivateKey;
        this.appCertPath = appCertPath;
        this.alipayPublicCertPath = alipayPublicCertPath;
        this.alipayRootCertPath = alipayRootCertPath;

    }

    private AlipayClient alipayClient;

    private AlipayClient getAlipayClient() throws AlipayApiException {
        if(this.alipayClient==null){
            CertAlipayRequest certAlipayRequest = new CertAlipayRequest();
            certAlipayRequest.setServerUrl(API_URL);  //gateway:支付宝网关（固定）https://openapi.alipay.com/gateway.do
            certAlipayRequest.setAppId(getAppID());  //APPID 即创建应用后生成,详情见创建应用并获取 APPID
            certAlipayRequest.setPrivateKey(this.appPrivateKey);  //开发者应用私钥，由开发者自己生成
            certAlipayRequest.setFormat(DATA_FORMART);  //参数返回格式，只支持 json 格式
            certAlipayRequest.setCharset(CHARSET);  //请求和签名使用的字符编码格式，支持 GBK和 UTF-8
            certAlipayRequest.setSignType(SIGN_TYPE);  //商户生成签名字符串所使用的签名算法类型，目前支持 RSA2 和 RSA，推荐商家使用 RSA2。
            certAlipayRequest.setCertPath(this.appCertPath); //应用公钥证书路径（app_cert_path 文件绝对路径）
            certAlipayRequest.setAlipayPublicCertPath(this.alipayPublicCertPath); //支付宝公钥证书文件路径（alipay_cert_path 文件绝对路径）
            certAlipayRequest.setRootCertPath(this.alipayRootCertPath);  //支付宝CA根证书文件路径（alipay_root_cert_path 文件绝对路径）
            this.alipayClient = new DefaultAlipayClient(certAlipayRequest);
        }
        return this.alipayClient;
    }

    @Override
    public String wapPay(Map<String, Object> requestData) {
        return wapPay(requestData , null , null);
    }

    @Override
    public String wapPay(Map<String, Object> requestData,String notify_url , String return_url) {
        AlipayTradeWapPayRequest alipay_request=new AlipayTradeWapPayRequest();
        // 设置异步通知地址
        if(StringUtil.isNullOrEmpty(notify_url)){
            alipay_request.setNotifyUrl(this.notifyUrl);
        }else{
            alipay_request.setNotifyUrl(notify_url);
        }

        // 设置同步地址
        if(StringUtil.isNullOrEmpty(return_url)){
            alipay_request.setReturnUrl(this.returnUrl);
        }else{
            alipay_request.setReturnUrl(return_url);
        }

        // 封装请求支付信息
        AlipayTradeWapPayModel model=new AlipayTradeWapPayModel();
        model.setOutTradeNo(requestData.get("out_trade_no").toString());
        model.setSubject(requestData.get("subject").toString());
        model.setTotalAmount(requestData.get("total_amount").toString());
        model.setProductCode("QUICK_WAP_WAY");
        if(requestData.containsKey("quit_url")){
            model.setQuitUrl(requestData.get("quit_url").toString());
        }
        if(requestData.containsKey("body")){
            model.setBody(requestData.get("body").toString());
        }
        if(requestData.containsKey("auth_token")){
            model.setAuthToken(requestData.get("auth_token").toString());
        }


        if(requestData.containsKey("time_expire")){
            model.setTimeExpire(requestData.get("time_expire").toString());
        }
        if(requestData.containsKey("timeout_express")){
            model.setTimeoutExpress(requestData.get("timeout_express").toString());
        }

        if(requestData.containsKey("extend_params")){
            model.setExtendParams((ExtendParams)requestData.get("extend_params"));
        }
        if(requestData.containsKey("business_params")){
            model.setBusinessParams(requestData.get("business_params").toString());
        }
        if(requestData.containsKey("promo_params")){
            model.setPromoParams(requestData.get("promo_params").toString());
        }
        if(requestData.containsKey("passback_params")){
            model.setPassbackParams(requestData.get("passback_params").toString());
        }
        if(requestData.containsKey("store_id")){
            model.setStoreId(requestData.get("store_id").toString());
        }

        if(requestData.containsKey("enable_pay_channels")){
            model.setEnablePayChannels(requestData.get("enable_pay_channels").toString());
        }
        if(requestData.containsKey("disable_pay_channels")){
            model.setDisablePayChannels(requestData.get("disable_pay_channels").toString());
        }
        if(requestData.containsKey("specified_channel")){
            model.setSpecifiedChannel(requestData.get("specified_channel").toString());
        }
        if(requestData.containsKey("merchant_order_no")){
            model.setMerchantOrderNo(requestData.get("merchant_order_no").toString());
        }
        if(requestData.containsKey("ext_user_info")){
            model.setExtUserInfo((ExtUserInfo)requestData.get("ext_user_info"));
        }


        alipay_request.setBizModel(model);


        // form表单生产
        String form = null;
        try {
            // 调用SDK生成表单
            form = getAlipayClient().pageExecute(alipay_request).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return form;
    }


    @Override
    public Map<String, String> getNotifyData(HttpServletRequest request) {
        Map<String ,String > requestData = null;
        if(request==null){
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if(servletRequestAttributes!=null){
                request = servletRequestAttributes.getRequest();
            }
        }
        if(request!=null){
            requestData = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            Iterator<String> iterator = requestParams.keySet().iterator();
            while (iterator.hasNext()){
                String name = iterator.next();
                String[] values = requestParams.get(name);
                StringBuffer sb =new StringBuffer();
                for(int i=0;i< values.length;i++){
                    if(i>0){
                        sb.append(",").append(values[i]);
                    }else{
                        sb.append(values[i]);
                    }
                }
                requestData.put(name , sb.toString());
            }
            try {
                Boolean verify_result = AlipaySignature.certVerifyV1(requestData,this.getAlipayPublicCertPath(), CHARSET, "RSA2");
                if(!verify_result){
                    //验证失败
                    requestData = null;
                }
            } catch (AlipayApiException e) {
                e.printStackTrace();
            }
        }

        return requestData;
    }

    @Override
    public Map<String, String> getNotifyData() {
        return getNotifyData(null);
    }


    @Override
    public Map<String, Object> orderQuery(String out_trade_no) {
        AlipayTradeQueryRequest alipay_request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model=new AlipayTradeQueryModel();
        model.setOutTradeNo(out_trade_no);
        alipay_request.setBizModel(model);

        try {
            String rs = getAlipayClient().certificateExecute(alipay_request).getBody();
            if(!StringUtil.isNullOrEmpty(rs)){
                JSONObject jsonObject = JSONObject.parseObject(rs);
                return ObjectUtil.JsonObject2Map(jsonObject.getJSONObject("alipay_trade_query_response"));
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, Object> closeOrder(String out_trade_no) {
        AlipayTradeCloseRequest alipay_request=new AlipayTradeCloseRequest();
        AlipayTradeCloseModel model =new AlipayTradeCloseModel();
        model.setOutTradeNo(out_trade_no);
        alipay_request.setBizModel(model);
        try {
            String rs = getAlipayClient().certificateExecute(alipay_request).getBody();
            if(!StringUtil.isNullOrEmpty(rs)){
                JSONObject jsonObject = JSONObject.parseObject(rs);
                return ObjectUtil.JsonObject2Map(jsonObject.getJSONObject("alipay_trade_close_response"));
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, Object> refund(Map<String, String> requestData) {
        AlipayTradeRefundRequest alipay_request = new AlipayTradeRefundRequest();

        AlipayTradeRefundModel model=new AlipayTradeRefundModel();
        model.setOutTradeNo(requestData.get("out_trade_no"));
        model.setRefundAmount(requestData.get("refund_amount"));
        model.setRefundReason(requestData.get("refund_reason"));
        model.setOutRequestNo(requestData.get("out_request_no"));
        alipay_request.setBizModel(model);
        try {
            String rs = getAlipayClient().certificateExecute(alipay_request).getBody();
            if(!StringUtil.isNullOrEmpty(rs)){
                JSONObject jsonObject = JSONObject.parseObject(rs);
                return ObjectUtil.JsonObject2Map(jsonObject.getJSONObject("alipay_trade_refund_response"));
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, Object> refundQuery(Map<String, String> requestData) {
        AlipayTradeFastpayRefundQueryRequest alipay_request = new AlipayTradeFastpayRefundQueryRequest();

        AlipayTradeFastpayRefundQueryModel model=new AlipayTradeFastpayRefundQueryModel();
        model.setOutTradeNo(requestData.get("out_trade_no"));
        model.setOutRequestNo(requestData.get("out_request_no"));
        alipay_request.setBizModel(model);
        try {
            String rs = getAlipayClient().certificateExecute(alipay_request).getBody();
            if(!StringUtil.isNullOrEmpty(rs)){
                JSONObject jsonObject = JSONObject.parseObject(rs);
                return ObjectUtil.JsonObject2Map(jsonObject.getJSONObject("alipay_trade_fastpay_refund_query_response"));
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String downloadBill(Map<String, String> requestData) {
        AlipayDataDataserviceBillDownloadurlQueryRequest  alipay_request = new AlipayDataDataserviceBillDownloadurlQueryRequest ();
        AlipayDataDataserviceBillDownloadurlQueryModel model =new AlipayDataDataserviceBillDownloadurlQueryModel();
        model.setBillType(requestData.get("bill_type"));
        model.setBillDate(requestData.get("bill_date"));
        alipay_request.setBizModel(model);
        try {
            String rs = getAlipayClient().certificateExecute(alipay_request).getBody();
            if(!StringUtil.isNullOrEmpty(rs)){
                JSONObject jsonObject = JSONObject.parseObject(rs);
                if(jsonObject!=null && jsonObject.containsKey("alipay_data_dataservice_bill_downloadurl_query_response") && jsonObject.getJSONObject("alipay_data_dataservice_bill_downloadurl_query_response").containsKey("bill_download_url"))
                return jsonObject.getJSONObject("alipay_data_dataservice_bill_downloadurl_query_response").getString("bill_download_url");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String pcPay(Map<String, Object> requestData) {
        return pcPay(requestData, null , null);
    }

    @Override
    public String pcPay(Map<String, Object> requestData,String notify_url , String return_url) {
        AlipayTradePagePayRequest alipay_request=new AlipayTradePagePayRequest();
        // 设置异步通知地址
        if(StringUtil.isNullOrEmpty(notify_url)){
            alipay_request.setNotifyUrl(this.notifyUrl);
        }else{
            alipay_request.setNotifyUrl(notify_url);
        }

        // 设置同步地址
        if(StringUtil.isNullOrEmpty(return_url)){
            alipay_request.setReturnUrl(this.returnUrl);
        }else{
            alipay_request.setReturnUrl(return_url);
        }

        // 封装请求支付信息
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo(requestData.get("out_trade_no").toString());
        model.setSubject(requestData.get("subject").toString());
        model.setTotalAmount(requestData.get("total_amount").toString());
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        if(requestData.containsKey("body")){
            model.setBody(requestData.get("body").toString());
        }
        if(requestData.containsKey("qr_pay_mode")){
            model.setQrPayMode(requestData.get("qr_pay_mode").toString());
        }
        if(requestData.containsKey("qrcode_width")){
            model.setQrcodeWidth(Long.valueOf(requestData.get("qrcode_width").toString()));
        }


        if(requestData.containsKey("goods_detail")){
            model.setGoodsDetail((List<GoodsDetail>)requestData.get("goods_detail"));
        }

        if(requestData.containsKey("time_expire")){
            model.setTimeExpire(requestData.get("time_expire").toString());
        }
        if(requestData.containsKey("timeout_express")){
            model.setTimeoutExpress(requestData.get("timeout_express").toString());
        }

        if(requestData.containsKey("extend_params")){
            model.setExtendParams((ExtendParams)requestData.get("extend_params"));
        }
        if(requestData.containsKey("business_params")){
            model.setBusinessParams(requestData.get("business_params").toString());
        }
        if(requestData.containsKey("promo_params")){
            model.setPromoParams(requestData.get("promo_params").toString());
        }
        if(requestData.containsKey("passback_params")){
            model.setPassbackParams(requestData.get("passback_params").toString());
        }

        if(requestData.containsKey("integration_type")){
            model.setIntegrationType(requestData.get("integration_type").toString());
        }
        if(requestData.containsKey("request_from_url")){
            model.setRequestFromUrl(requestData.get("request_from_url").toString());
        }

        if(requestData.containsKey("store_id")){
            model.setStoreId(requestData.get("store_id").toString());
        }

        if(requestData.containsKey("enable_pay_channels")){
            model.setEnablePayChannels(requestData.get("enable_pay_channels").toString());
        }
        if(requestData.containsKey("disable_pay_channels")){
            model.setDisablePayChannels(requestData.get("disable_pay_channels").toString());
        }
        if(requestData.containsKey("merchant_order_no")){
            model.setMerchantOrderNo(requestData.get("merchant_order_no").toString());
        }
        if(requestData.containsKey("invoice_info")){
            model.setInvoiceInfo((InvoiceInfo)requestData.get("invoice_info"));
        }

        if(requestData.containsKey("ext_user_info")){
            model.setExtUserInfo((ExtUserInfo)requestData.get("ext_user_info"));
        }


        alipay_request.setBizModel(model);


        // form表单生产
        String form = null;
        try {
            // 调用SDK生成表单
            form = getAlipayClient().pageExecute(alipay_request).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return form;
    }


    @Override
    public Map<String, Object> transfer(Map<String, Object> requestData, Participant payee_info) {
        AlipayFundTransUniTransferRequest alipay_request = new AlipayFundTransUniTransferRequest();
        AlipayFundTransUniTransferModel model =new AlipayFundTransUniTransferModel();
        model.setPayeeInfo(payee_info);
        model.setOutBizNo(requestData.get("out_biz_no").toString());
        model.setTransAmount(requestData.get("trans_amount").toString());
        model.setProductCode(requestData.get("product_code").toString());
        if(requestData.containsKey("biz_scene")){
            model.setBizScene(requestData.get("biz_scene").toString());
        }
        if(requestData.containsKey("order_title")){
            model.setOrderTitle(requestData.get("order_title").toString());
        }
        if(requestData.containsKey("original_order_id")){
            model.setOriginalOrderId(requestData.get("original_order_id").toString());
        }
        if(requestData.containsKey("remark")){
            model.setRemark(requestData.get("remark").toString());
        }
        if(requestData.containsKey("business_params")){
            model.setBusinessParams(requestData.get("business_params").toString());
        }

        alipay_request.setBizModel(model);
        try {
            String rs = getAlipayClient().certificateExecute(alipay_request).getBody();
            if(!StringUtil.isNullOrEmpty(rs)){
                JSONObject jsonObject = JSONObject.parseObject(rs);
                return ObjectUtil.JsonObject2Map(jsonObject.getJSONObject("alipay_fund_trans_uni_transfer_response"));
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return null;
    }
}
