package com.ihltx.utility.functional;

import com.ihltx.utility.httpclient.service.RestTemplateUtil;
import com.ihltx.utility.httpclient.service.impl.RestTemplateUtilImpl;
import com.ihltx.utility.util.SecurityUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 快递鸟免费物流查询实用工具，每帐号每天最多支持500次查询
 */
public class ExpressUtil {
    private static String API_HOST = "https://api.kdniao.com";
    private static String API_URL = API_HOST + "/Ebusiness/EbusinessOrderHandle.aspx";

    private static RestTemplateUtil restTemplateUtil =  new RestTemplateUtilImpl(3000, 10000);

    private static Map<String,String> SHIPPERCODES = new HashMap<String, String>(){
        {
            put("百世","HTKY");
            put("申通","STO");
            put("圆通","YTO");
            put("天天","HHTT");
        }
    };



    /**
     * 根据物流公司编码以及运单号查询物流信息
     * 仅支持 申通、圆通、百世、天天
     * @param EBusinessID       快递鸟注册账号ID（1256310）
     * @param apiKey            快递鸟注册账号apiKey（e77ceced-e98b-460a-a26c-6731a6cd0ccf）
     * @param ShipperCode       物流公司编码
     * @param LogisticCode      运单号
     * @return String
     * 百世快递	HTKY
     * 申通快递	STO
     * 圆通速递	YTO
     * 天天快递	HHTT
     */
    public static String getDatas(String EBusinessID ,String apiKey , String ShipperCode, String LogisticCode) throws Exception {
        String RequestData= "{"+
                "'CustomerName': '',"+
                "'OrderCode': '',"+
                "'ShipperCode': '" + ShipperCode + "',"+
                "'LogisticCode': '" + LogisticCode + "',"+
                "}";

        // 组装系统级参数
        Map<String,String> params = new HashMap<String,String>();
        params.put("RequestData", urlEncoder(RequestData, "UTF-8"));
        params.put("EBusinessID", EBusinessID);
        params.put("RequestType", "1002");//免费即时查询接口指令1002/在途监控即时查询接口指令8001/地图版即时查询接口指令8003
        String dataSign=encrypt(RequestData, apiKey, "UTF-8");
        params.put("DataSign", urlEncoder(dataSign, "UTF-8"));
        params.put("DataType", "2");
        // 以form表单形式提交post请求，post请求体中包含了应用级参数和系统级参数
        String result=sendPost(API_URL, params);

        //根据公司业务处理返回的信息......
        return result;
    }


    /**
     *  根据物流公司编码获取物流公司名称
     * @param ShipperCode       物流公司编码
     * @return  String
     */
    public static String getShipperNameByCode(String ShipperCode){
        for(String name : SHIPPERCODES.keySet()){
            if(SHIPPERCODES.get(name).equals(ShipperCode)){
                return name;
            }
        }
        return null;
    }

    /**
     *  根据物流公司名称获取物流公司编码
     * @param ShipperName       物流公司名称
     * @return  String
     */
    public static String getShipperCodeByName(String ShipperName){
        return SHIPPERCODES.containsKey(ShipperName)?SHIPPERCODES.get(ShipperName):null;
    }

    /**
     * 根据物流公司编码以及运单号查询物流微信文本信息
     * 仅支持 申通、圆通、百世、天天
     * @param EBusinessID       快递鸟注册账号ID（1256310）
     * @param apiKey            快递鸟注册账号apiKey（e77ceced-e98b-460a-a26c-6731a6cd0ccf）
     * @param ShipperCode       物流公司编码
     * @param LogisticCode      运单号
     * @return String
     */

    public static String getWeiXinDatas(String EBusinessID ,String apiKey , String ShipperCode, String LogisticCode) throws Exception {
        String split = "==============";
        String rs = null;
        String result = getDatas(EBusinessID , apiKey, ShipperCode , LogisticCode);
        JSONObject jsonObject = JSONObject.parseObject(result);
        StringBuffer sb =new StringBuffer();
        if(jsonObject!=null){
            String  shipperName = getShipperNameByCode(ShipperCode);
            sb.append(shipperName).append(" ").append(LogisticCode).append("\n").append(split).append("\n");
            JSONArray jsonArray = jsonObject.getJSONArray("Traces");
            if(jsonArray!=null && !jsonArray.isEmpty()){
                for (int i=0;i< jsonArray.size();i++){
                    sb.append(jsonArray.getJSONObject(i).getString("AcceptTime")).append(" ").append(jsonArray.getJSONObject(i).getString("AcceptStation")).append("\n");
                }
            }
            rs = sb.toString();
        }
        return  rs;
    }




    /**
     * MD5加密
     * str 内容
     * charset 编码方式
     * @throws Exception
     */
    @SuppressWarnings("unused")
    private static String MD5(String str,String charset) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes(charset));
        byte[] result = md.digest();
        StringBuffer sb = new StringBuffer(32);
        for (int i = 0; i < result.length; i++) {
            int val = result[i] & 0xff;
            if (val <= 0xf) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(val));
        }
        return sb.toString().toLowerCase();
    }

    /**
     * base64编码
     * str 内容
     * charset 编码方式
     * @throws UnsupportedEncodingException
     */
    private static String base64(String str, String charset) throws UnsupportedEncodingException{
        String encoded = SecurityUtil.base64Encode(str, charset);
        return encoded;
    }

    @SuppressWarnings("unused")
    private static String urlEncoder(String str, String charset) throws UnsupportedEncodingException{
        String result = URLEncoder.encode(str, charset);
        return result;
    }

    /**
     * 电商Sign签名生成
     * content 内容
     * keyValue ApiKey
     * charset 编码方式
     * @throws UnsupportedEncodingException ,Exception
     * @return DataSign签名
     */
    @SuppressWarnings("unused")
    private static String encrypt (String content,String keyValue,String charset) throws UnsupportedEncodingException, Exception
    {
        if (keyValue != null)
        {
            return base64(MD5(content + keyValue, charset), charset);
        }
        return base64(MD5(content, charset), charset);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * url 发送请求的 URL
     * params 请求的参数集合
     * @return 远程资源的响应结果
     */
    @SuppressWarnings("unused")
    private static String sendPost(String url, Map<String,String> params) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new    StringBuilder();
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn =(HttpURLConnection) realUrl.openConnection();
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // POST方法
            conn.setRequestMethod("POST");
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();
            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            // 发送请求参数
            if (params != null) {
                StringBuilder param = new    StringBuilder();
                for (Map.Entry<   String,    String> entry : params.entrySet()) {
                    if(param.length()>0){
                        param.append("&");
                    }
                    param.append(entry.getKey());
                    param.append("=");
                    param.append(entry.getValue());
                }
                out.write(param.toString());
            }
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result.toString();
    }
}
