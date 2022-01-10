package com.ihltx.utility.weixin.tools;

import com.ihltx.utility.util.StringUtil;
import com.ihltx.utility.weixin.entity.ArticleItem;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.Buffer;
import java.util.*;

/**
 * 微信实用工具类
 */
public class WeChatUtil {

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param byteArray
     * @return
     */
    private static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    /**
     * 将字节转换为十六进制字符串
     *
     * @param mByte
     * @return
     */
    private static String byteToHexStr(byte mByte) {
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];

        String s = new String(tempArr);
        return s;
    }

    private static void sort(String a[]) {
        for (int i = 0; i < a.length - 1; i++) {
            for (int j = i + 1; j < a.length; j++) {
                if (a[j].compareTo(a[i]) < 0) {
                    String temp = a[i];
                    a[i] = a[j];
                    a[j] = temp;
                }
            }
        }
    }

    /**
     * 解析微信发来的请求(xml)
     *
     * @param request           request对象
     * @return Map<String,String>
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked"})
    public static Map<String,String> parseXml(HttpServletRequest request) throws Exception {
        return parseXml(request.getInputStream());
    }


    /**
     * 使用utf-8字符集，解析字符串xml内容，xml内容必须包含<root></root>，其它元素必须在<root></root>内
     *
     * @param xmldata                   字符串xml内容
     * @return Map<String,String>
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked"})
    public static Map<String,String> parseXml(String xmldata) throws Exception {
        return parseXml(xmldata, "UTF-8");
    }


    /**
     * 解析字符串xml内容，xml内容必须包含<root></root>，其它元素必须在<root></root>内
     *
     * @param xmldata                   字符串xml内容
     * @param charset                   字符集
     * @return Map<String,String>
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked"})
    public static Map<String,String> parseXml(String xmldata, String charset) throws Exception {
        return parseXml(new ByteArrayInputStream(xmldata.getBytes(charset)));
    }

    /**
     * 针对输入流解析xml，并返回Map<String,String>
     *
     * @param inputStream       输入流
     * @return Map<String,String>
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked"})
    public static Map<String,String> parseXml(InputStream inputStream) throws Exception {
        // 将解析结果存储在HashMap中
        Map<String,String> map = new HashMap<String,String>();
        // 读取输入流
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        // 得到xml根元素
        Element root = document.getRootElement();
        // 得到根元素的所有子节点
        List<Element> elementList = root.elements();
        // 遍历所有子节点
        for (Element e : elementList){
            if(e.getName().equals("ScanCodeInfo")){
                map.put(e.getName() + "_ScanType", e.element("ScanType").getText());
                map.put(e.getName() + "_ScanResult", e.element("ScanResult").getText());
            }else if(e.getName().equals("SendPicsInfo")){
                map.put(e.getName() + "_Count", e.element("Count").getText());
                List<Element> subElementList = e.elements("PicList");
                StringBuffer sb =new StringBuffer();
                int index = 0;
                for(int i=0;i<subElementList.size();i++){

                    List<Element> subItemList = subElementList.get(i).elements("item");
                    if(subItemList.size()>0){
                        for(Element ee : subItemList){
                            if(index>0){
                                sb.append(",");
                            }
                            sb.append(ee.element("PicMd5Sum").getText());
                            index++;
                        }
                    }
                }
                map.put(e.getName() + "_PicList", sb.toString());
            }else if(e.getName().equals("SendLocationInfo")){
                map.put(e.getName() + "_Location_X", e.element("Location_X").getText());
                map.put(e.getName() + "_Location_Y", e.element("Location_Y").getText());
                map.put(e.getName() + "_Scale", e.element("Scale").getText());
                map.put(e.getName() + "_Label", e.element("Label").getText());
                Element element = e.element("Poiname");
                if(element!=null){
                    map.put(e.getName() + "_Poiname", element.getText());
                }else{
                    map.put(e.getName() + "_Poiname", "");
                }

            }else if(e.getName().equals("SubscribeMsgPopupEvent")){
                map.put("SubscribeMsgPopupEvent", null);
                List<Element> elements = e.elements("List");
                if(elements!=null && elements.size()>0){
                    List<Map<String,Object>> subscribeMsgPopupEvent =new ArrayList<>();
                    for (Element ee : elements){
                        Map<String,Object> row =new HashMap<>();
                        row.put("TemplateId" , ee.element("TemplateId").getText());
                        row.put("SubscribeStatusString" , ee.element("SubscribeStatusString").getText());
                        row.put("PopupScene" , ee.element("PopupScene").getText());
                        subscribeMsgPopupEvent.add(row);
                    }
                    map.put("SubscribeMsgPopupEvent", JSONObject.toJSONString(subscribeMsgPopupEvent));
                }
            }else if(e.getName().equals("SubscribeMsgChangeEvent")){
                map.put("SubscribeMsgChangeEvent", null);
                List<Element> elements = e.elements("List");
                if(elements!=null && elements.size()>0){
                    List<Map<String,Object>> SubscribeMsgChangeEvent =new ArrayList<>();
                    for (Element ee : elements){
                        Map<String,Object> row =new HashMap<>();
                        row.put("TemplateId" , ee.element("TemplateId").getText());
                        row.put("SubscribeStatusString" , ee.element("SubscribeStatusString").getText());
                        SubscribeMsgChangeEvent.add(row);
                    }
                    map.put("SubscribeMsgChangeEvent", JSONObject.toJSONString(SubscribeMsgChangeEvent));
                }
            }else if(e.getName().equals("SubscribeMsgSentEvent")){
                map.put("SubscribeMsgSentEvent", null);
                List<Element> elements = e.elements("List");
                if(elements!=null && elements.size()>0){
                    List<Map<String,Object>> SubscribeMsgSentEvent =new ArrayList<>();
                    for (Element ee : elements){
                        Map<String,Object> row =new HashMap<>();
                        row.put("TemplateId" , ee.element("TemplateId").getText());
                        row.put("MsgID" , ee.element("MsgID").getText());
                        row.put("ErrorCode" , ee.element("ErrorCode").getText());
                        row.put("ErrorStatus" , ee.element("ErrorStatus").getText());
                        SubscribeMsgSentEvent.add(row);
                    }
                    map.put("SubscribeMsgSentEvent", JSONObject.toJSONString(SubscribeMsgSentEvent));
                }
            }else if(e.getName().equals("PublishEventInfo")){
                map.put("PublishEventInfo", null);
                Map<String, Object> publishEventInfo = new HashMap<>();
                publishEventInfo.put("publish_id" , e.element("publish_id").getText());
                publishEventInfo.put("publish_status" , e.element("publish_status").getText());
                if(e.element("article_id")!=null){
                    publishEventInfo.put("article_id" , e.element("article_id").getText());
                }
                if(e.element("article_detail")!=null){
                    Map<String, Object> article_detail = new HashMap<>();
                    article_detail.put("count" , e.element("article_detail").element("count").getText());
                    List<Element> itemElements = e.elements("item");
                    if(itemElements!=null && itemElements.size()>0){
                        List<Map<String, Object>> items = new ArrayList<>();
                        for (Element ee : itemElements){
                            Map<String, Object> item =new HashMap<>();
                            item.put("idx" , ee.element("idx").getText());
                            item.put("article_url" , ee.element("article_url").getText());
                            items.add(item);
                        }
                        article_detail.put("item" ,items);
                    }
                    publishEventInfo.put("article_detail" , article_detail);
                }
                List<Element> fail_idxs = e.elements("fail_idx");
                if(fail_idxs!=null && fail_idxs.size()>0){
                    List<String> fail_idx = new ArrayList<>();
                    for (Element ee : fail_idxs){
                        fail_idx.add(ee.getText());
                    }
                    publishEventInfo.put("fail_idx" , fail_idx);
                }
                map.put("PublishEventInfo", JSONObject.toJSONString(publishEventInfo));
            }else{
                map.put(e.getName(), e.getText());
            }

        }
        // 释放资源
        inputStream.close();
        return map;
    }

    public static String mapToXML(Map map) {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        mapToXML2(map, sb);
        sb.append("</xml>");
        try {
            return sb.toString();
        } catch (Exception e) {
        }
        return null;
    }

    private static void mapToXML2(Map map, StringBuffer sb) {
        Set set = map.keySet();
        for (Iterator it = set.iterator(); it.hasNext();) {
            String key = (String) it.next();
            Object value = map.get(key);
            if (null == value)
                value = "";
            if (value.getClass().getName().equals("java.util.ArrayList")) {
                ArrayList list = (ArrayList) map.get(key);
                sb.append("<" + key + ">");
                for (int i = 0; i < list.size(); i++) {
                    HashMap hm = (HashMap) list.get(i);
                    mapToXML2(hm, sb);
                }
                sb.append("</" + key + ">");

            } else {
                if (value instanceof HashMap) {
                    sb.append("<" + key + ">");
                    mapToXML2((HashMap) value, sb);
                    sb.append("</" + key + ">");
                } else {
                    sb.append("<" + key + "><![CDATA[" + value + "]]></" + key + ">");
                }

            }

        }
    }
    /**
     * 回复文本消息
     * @param requestMap        包含发送用户openid及接收微信公众号信息
     * @param content           文本消息
     * @return String xml
     */
    public static String sendTextMsg(Map<String,String> requestMap,String content){

        Map<String,Object> map=new HashMap<String, Object>();
        map.put("ToUserName", requestMap.get(WeChatContant.FromUserName));
        map.put("FromUserName",  requestMap.get(WeChatContant.ToUserName));
        map.put("MsgType", WeChatContant.RESP_MESSAGE_TYPE_TEXT);
        map.put("CreateTime", new Date().getTime());
        map.put("Content", content);
        return  mapToXML(map);
    }

    /**
     * 回复图片消息
     * @param requestMap        包含发送用户openid及接收微信公众号信息
     * @param media_id          图片素材媒体ID
     * @return String xml
     */
    public static String sendImageMsg(Map<String,String> requestMap,String media_id){

        Map<String,Object> map=new HashMap<String, Object>();
        map.put("ToUserName", requestMap.get(WeChatContant.FromUserName));
        map.put("FromUserName",  requestMap.get(WeChatContant.ToUserName));
        map.put("MsgType", WeChatContant.RESP_MESSAGE_TYPE_IMAGE);
        map.put("CreateTime", new Date().getTime());
        Map<String , Object> content =new HashMap<>();
        content.put("MediaId" , media_id);
        map.put("Image", content);
        return  mapToXML(map);
    }

    /**
     * 回复语音消息
     * @param requestMap        包含发送用户openid及接收微信公众号信息
     * @param media_id          语音素材媒体ID
     * @return  String xml
     */
    public static String sendVoiceMsg(Map<String,String> requestMap,String media_id){

        Map<String,Object> map=new HashMap<String, Object>();
        map.put("ToUserName", requestMap.get(WeChatContant.FromUserName));
        map.put("FromUserName",  requestMap.get(WeChatContant.ToUserName));
        map.put("MsgType", WeChatContant.RESP_MESSAGE_TYPE_VOICE);
        map.put("CreateTime", new Date().getTime());
        Map<String , Object> content =new HashMap<>();
        content.put("MediaId" , media_id);
        map.put("Voice", content);
        return  mapToXML(map);
    }

    /**
     * 回复视频消息
     * @param requestMap        包含发送用户openid及接收微信公众号信息
     * @param media_id          视频素材媒体ID
     * @param title             视频标题
     * @param description       视频描述
     * @return String xml
     */
    public static String sendVideoMsg(Map<String,String> requestMap,String media_id , String title , String description){

        Map<String,Object> map=new HashMap<String, Object>();
        map.put("ToUserName", requestMap.get(WeChatContant.FromUserName));
        map.put("FromUserName",  requestMap.get(WeChatContant.ToUserName));
        map.put("MsgType", WeChatContant.RESP_MESSAGE_TYPE_VIDEO);
        map.put("CreateTime", new Date().getTime());
        Map<String , Object> content =new HashMap<>();
        content.put("MediaId" , media_id);
        content.put("Title" , title);
        content.put("Description" , description);
        map.put("Video", content);
        return  mapToXML(map);
    }

    /**
     * 回复音乐消息
     * @param requestMap            包含发送用户openid及接收微信公众号信息
     * @param title                 音乐标题
     * @param description           音乐描述
     * @param musicUrl              音乐url
     * @param hQMusicUrl            高质量音乐链接，WIFI环境优先使用该链接播放音乐
     * @param thumbMediaId          缩略图的媒体id，通过素材管理中的接口上传多媒体文件，得到的id
     * @return String xml
     */
    public static String sendMusicMsg(Map<String,String> requestMap,String title , String description, String musicUrl, String hQMusicUrl, String thumbMediaId){

        Map<String,Object> map=new HashMap<String, Object>();
        map.put("ToUserName", requestMap.get(WeChatContant.FromUserName));
        map.put("FromUserName",  requestMap.get(WeChatContant.ToUserName));
        map.put("MsgType", WeChatContant.RESP_MESSAGE_TYPE_MUSIC);
        map.put("CreateTime", new Date().getTime());
        Map<String , Object> content =new HashMap<>();
        content.put("Title" , title);
        content.put("Description" , description);
        content.put("MusicUrl" , musicUrl);
        content.put("HQMusicUrl" , hQMusicUrl);
        content.put("ThumbMediaId" , thumbMediaId);
        map.put("Music", content);
        return  mapToXML(map);
    }

    /**
     * 回复图文消息
     * @param requestMap            包含发送用户openid及接收微信公众号信息
     * @param items                 图文列表
     * @return String xml
     */
    public static String sendArticleMsg(Map<String,String> requestMap,List<ArticleItem> items){
        if(items == null || items.size()<1){
            return "";
        }
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("ToUserName", requestMap.get(WeChatContant.FromUserName));
        map.put("FromUserName", requestMap.get(WeChatContant.ToUserName));
        map.put("MsgType", WeChatContant.RESP_MESSAGE_TYPE_NEWS);
        map.put("CreateTime", new Date().getTime());
        List<Map<String,Object>> Articles=new ArrayList<Map<String,Object>>();
        for(ArticleItem itembean : items){
            Map<String,Object> item=new HashMap<String, Object>();
            Map<String,Object> itemContent=new HashMap<String, Object>();
            itemContent.put("Title", itembean.getTitle());
            itemContent.put("Description", itembean.getDigest());
            itemContent.put("PicUrl", itembean.getThumb_url());
            itemContent.put("Url", itembean.getUrl());
            item.put("item",itemContent);
            Articles.add(item);
        }
        map.put("Articles", Articles);
        map.put("ArticleCount", Articles.size());
        return mapToXML(map);
    }

    /**
     * 回复消息转发到微信客服系统
     * @param requestMap        包含发送用户openid及接收微信公众号信息
     * @param KfAccount         指定会话接入的客服账号
     * @return String xml
     */
    public static String sendTransferCustomerService(Map<String,String> requestMap, String KfAccount){

        Map<String,Object> map=new HashMap<String, Object>();
        map.put("ToUserName", requestMap.get(WeChatContant.FromUserName));
        map.put("FromUserName",  requestMap.get(WeChatContant.ToUserName));
        map.put("MsgType", WeChatContant.RESP_MESSAGE_TYPE_TRANSFER_CUSTOMER_SERVICE);
        map.put("CreateTime", new Date().getTime());
        if(!StringUtil.isNullOrEmpty(KfAccount)){
            Map<String , Object> transInfo =new HashMap<>();
            transInfo.put("KfAccount" , KfAccount);
            map.put("TransInfo" ,transInfo);
        }
        return  mapToXML(map);
    }

}
