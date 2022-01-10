package com.ihltx.utility.weixin.event;

import com.ihltx.utility.functional.*;
import com.ihltx.utility.util.StringUtil;
import com.ihltx.utility.weixin.WeixinUtil;
import com.ihltx.utility.weixin.entity.ArticleItem;
import com.ihltx.utility.weixin.entity.NewsMaterial;
import com.ihltx.utility.weixin.tools.WeChatUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 消息默认响应接口
 */
public class MessageEventHandler {

    /**
     * 针对百科类关键词回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param keywords          百科关键字
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackBaike(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String, String> requestBody , String keywords){
        String respXml = null;
        if(!StringUtil.isNullOrEmpty(keywords)){
            List<ArticleItem> items = BaikeUtil.getWeiXinDatas(keywords);
            if(items!=null && items.size()>0){
                respXml = WeChatUtil.sendArticleMsg(requestBody, items);
            }else{
                respXml = WeChatUtil.sendTextMsg(requestBody, "没有[" + keywords +"]的百科信息！");
            }
        }else{
            respXml = WeChatUtil.sendTextMsg(requestBody, "百科关键字不能为空，格式为：百科+关键字");
        }
        return respXml;
    }

    /**
     * 针对翻译类关键词回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param keywords          翻译关键字
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackTranslate(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody , String keywords){
        String respXml = null;
        if(!StringUtil.isNullOrEmpty(keywords)){
            String result = TranslateUtil.getWeiXinDatas(keywords);
            if(!StringUtil.isNullOrEmpty(result)){
                respXml = WeChatUtil.sendTextMsg(requestBody, result);
            }else{
                respXml = WeChatUtil.sendTextMsg(requestBody, "没有找到[" + keywords +"]的翻译信息！");
            }
        }else{
            respXml = WeChatUtil.sendTextMsg(requestBody, "翻译关键字不能为空，格式为：@关键字");
        }
        return respXml;
    }

    /**
     * 针对天气类关键词回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param keywords          天气关键字
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackWeather(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody , String keywords){
        String respXml = null;
        if(!StringUtil.isNullOrEmpty(keywords)){
            String result = WeatherUtil.getWeiXinDatas(keywords);
            if(!StringUtil.isNullOrEmpty(result)){
                respXml = WeChatUtil.sendTextMsg(requestBody, result);
            }else{
                respXml = WeChatUtil.sendTextMsg(requestBody, "没有找到[" + keywords +"]的天气信息！");
            }
        }else{
            respXml = WeChatUtil.sendTextMsg(requestBody, "天气城市不能为空，格式为：天气+城市");
        }
        return respXml;
    }

    /**
     * 针对快递类关键词回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param keywords          快递关键字， 百世 552052643733750

     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackExpress(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody , String keywords){
        String respXml = null;
        if(!StringUtil.isNullOrEmpty(keywords)){
            String[] temps = keywords.split(" +");
            String result = null;
            try {
                result = ExpressUtil.getWeiXinDatas("1256310" , "e77ceced-e98b-460a-a26c-6731a6cd0ccf", ExpressUtil.getShipperCodeByName(temps[0].trim()) , temps[1].trim());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(!StringUtil.isNullOrEmpty(result)){
                respXml = WeChatUtil.sendTextMsg(requestBody, result);
            }else{
                respXml = WeChatUtil.sendTextMsg(requestBody, "没有找到[" + keywords +"]的快递信息！");
            }
        }else{
            respXml = WeChatUtil.sendTextMsg(requestBody, "快递信息不能为空，格式为：快递+百世 552052643733750");
        }
        return respXml;
    }

    /**
     * 日历关键词回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param keywords          日历关键字， 日历
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackCalendar(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody , String keywords){
        String respXml = null;
        String result = CalendarUtil.getWeiXinDatas(new Date());
        if(!StringUtil.isNullOrEmpty(result)){
            respXml = WeChatUtil.sendTextMsg(requestBody, result);
        }else{
            respXml = WeChatUtil.sendTextMsg(requestBody, "没有找到今天的日历信息！");
        }
        return respXml;
    }


    /**
     * 新闻关键词回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param keywords          新闻关键字， 新闻
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackNews(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody , String keywords){
        List<ArticleItem> items = NewsUtil.getWeiXinDatas();
        String respXml = null;
        if(items!=null && items.size()>0){
            respXml = WeChatUtil.sendArticleMsg(requestBody, items);
        }else{
            respXml = WeChatUtil.sendTextMsg(requestBody, "没有找到头条新闻！");
        }
        return respXml;
    }

    /**
     * 其它关键词回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param keywords          其它关键字， 除：百科、天气、@、快递、日历、新闻之外的其它关键字
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackKeyWords(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody , String keywords){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 用户发送链接消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param title             消息标题
     * @param description       消息描述
     * @param url               消息链接
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackLink(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody , String title, String description, String url){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 用户发送图片消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param mediaId           临时图片素材媒体ID，三天内有效
     * @param picUrl            临时图片素材访问url，三天内有效
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackImage(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody , String mediaId, String picUrl){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 用户发送语音消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param mediaId           临时语音素材媒体ID，三天内有效
     * @param format            语音格式，如amr，speex等
     * @param recognition       语音识别结果，UTF8编码
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackVoice(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody , String mediaId, String format, String recognition){
        return callBack(weixinUtil, messageEvent , requestBody);
    }


    /**
     * 用户发送视频消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param mediaId           视频消息媒体id，可以调用获取临时素材接口拉取数据。
     * @param thumbMediaId      视频消息缩略图的媒体id，可以调用多媒体文件下载接口拉取数据。
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackVideo(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody , String mediaId, String thumbMediaId){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 用户发送短视频消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param mediaId           短视频消息媒体id，可以调用获取临时素材接口拉取数据。
     * @param thumbMediaId      短视频消息缩略图的媒体id，可以调用多媒体文件下载接口拉取数据。
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackShortVideo(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody , String mediaId, String thumbMediaId){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 用户发送地理位置消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param location_X        地理位置纬度
     * @param location_Y        地理位置经度
     * @param label             地理位置信息
     * @param scale             地图缩放大小
     * @param precision         地理位置精度
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackLocation(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody , Double location_X, Double location_Y, String label, Double scale , Double precision){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 微信用户点击选择的菜单消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param keywords          选择的菜单内容
     * @param bizmsgmenuid      选择的菜单ID
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackMsgMenu(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody , String keywords, String bizmsgmenuid){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 用户关注公众号消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param eventKey          扫描带参数二维码的值，如果为空表示不是通过扫描带参数二维码完成的关注
     * @param ticket            参数二维码的ticket，如果为空表示不是通过扫描带参数二维码完成的关注
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackSubscribe(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody, String eventKey , String ticket){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 用户取消关注公众号消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackUnSubscribe(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 用户关注公众号之后扫描带参数二维码消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param eventKey          扫描带参数二维码的值
     * @param ticket            参数二维码的ticket
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackScan(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody , String eventKey , String ticket){
        return callBack(weixinUtil, messageEvent , requestBody);
    }



    /**
     * 用户点击扫码推事件类型菜单推送的消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param eventKey                   事件key
     * @param scanCodeInfo_ScanType      扫码类型
     * @param scanCodeInfo_ScanResult    扫码结果
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackEventScanCodePush(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody, String eventKey, String scanCodeInfo_ScanType, String scanCodeInfo_ScanResult){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 用户点击扫码带提示事件类型菜单推送的消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param eventKey          事件key
     * @param scanCodeInfo_ScanType      扫码类型
     * @param scanCodeInfo_ScanResult    扫码结果
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackEventScanCodeWaitMsg(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody, String eventKey, String scanCodeInfo_ScanType, String scanCodeInfo_ScanResult){
        return callBack(weixinUtil, messageEvent , requestBody);
    }


    /**
     * 用户点击view类型菜单推送的消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param eventKey          事件key
     * @param menuId            如果是个性化菜单，则表示个性化菜单ID
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackEventView(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody, String eventKey , String menuId){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 用户点击菜单跳转小程序的事件回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param eventKey          事件key,跳转的小程序路径
     * @param menuId            如果是个性化菜单，则表示个性化菜单ID
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackEventViewMiniprogram(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody, String eventKey , String menuId){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 用户点击系统拍照发图类型菜单推送的消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param eventKey                事件key
     * @param sendPicsInfo_Count      发送的图片总数
     * @param sendPicsInfo_PicList    发送的图片PicMd5Sum值列表，以英文,间隔
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackEventPicSysPhoto(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody, String eventKey , Integer sendPicsInfo_Count, String sendPicsInfo_PicList){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 用户点击拍照或者相册发图类型菜单推送的消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param eventKey          事件key
     * @param sendPicsInfo_Count      发送的图片总数
     * @param sendPicsInfo_PicList    发送的图片PicMd5Sum值列表，以英文,间隔
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackEventPicPhotoOrAlbum(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody, String eventKey , Integer sendPicsInfo_Count, String sendPicsInfo_PicList){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 用户点击微信相册发图类型菜单推送的消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param eventKey          事件key
     * @param sendPicsInfo_Count      发送的图片总数
     * @param sendPicsInfo_PicList    发送的图片PicMd5Sum值列表，以英文,间隔
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackEventPicWeixin(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody, String eventKey , Integer sendPicsInfo_Count, String sendPicsInfo_PicList){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 用户点击发送位置菜单推送的消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param eventKey          事件key
     * @param location_X        地理位置纬度
     * @param location_Y        地理位置经度
     * @param label             地理位置信息
     * @param scale             地图缩放大小
     * @param poiname           朋友圈POI的名字，可能为空
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackEventLocationSelect(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody , String eventKey , Double location_X, Double location_Y, String label, Double scale , String poiname){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 用户点击click菜单推送的消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param eventKey          事件key
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackEventClick(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody , String eventKey){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 用户点击click菜单推送的消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param status            送达状态  success--成功  failed:user block  -- 失败，用户拒绝  failed: system failed--失败，其它原因
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackEventTemplateSendJobFinish(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody , String status){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 用户点击click菜单推送的消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param status            群发状态
     *                          群发的结果，为“send success”或“send fail”或“err(num)”。但send success时，也有可能因用户拒收公众号的消息、系统错误等原因造成少量用户接收失败。err(num)是审核失败的具体原因，可能的情况如下：err(10001):涉嫌广告, err(20001):涉嫌政治, err(20004):涉嫌社会, err(20002):涉嫌色情, err(20006):涉嫌违法犯罪, err(20008):涉嫌欺诈, err(20013):涉嫌版权, err(22000):涉嫌互推(互相宣传), err(21000):涉嫌其他, err(30001):原创校验出现系统错误且用户选择了被判为转载就不群发, err(30002): 原创校验被判定为不能群发, err(30003): 原创校验被判定为转载文且用户选择了被判为转载就不群发, err(40001)：管理员拒绝, err(40002)：管理员30分钟内无响应，超时
     * @param totalCount        tag_id下粉丝数；或者openid_list中的粉丝数
     * @param filterCount       过滤（过滤是指特定地区、性别的过滤、用户设置拒收的过滤，用户接收已超4条的过滤）后，准备发送的粉丝数，原则上，FilterCount 约等于 SentCount + ErrorCount
     * @param sentCount         发送成功的粉丝数
     * @param errorCount        发送失败的粉丝数
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackEventMassSendJobFinish(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody , String status, Integer totalCount, Integer filterCount, Integer sentCount, Integer errorCount){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 订阅通知： 用户操作订阅通知弹窗
     * 场景：用户在图文等场景内订阅通知的操作
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param subscribeMsgPopupEvent            消息内容
     *           [
     *                {
     *                     "TemplateId": "模板 id",
     *                     "SubscribeStatusString": "用户点击行为（同意、取消发送通知）",  //SubscribeStatusString 的合法值  : reject -- 用户点击“取消”  accept -- 用用户点击“同意”
     *                     "PopupScene": "场景",      //1 -- 弹窗来自 H5 页面      2 -- 弹窗来自图文消息
     *                }
     *            ]
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackEventSubscribeMsgPopup(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody , List<Map<String,Object>> subscribeMsgPopupEvent){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 订阅通知： 用户管理订阅通知
     * 场景：用户在服务通知管理页面做通知管理时的操作
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param subscribeMsgChangeEvent            消息内容
     *           [
     *                {
     *                     "TemplateId": "模板 id",
     *                     "SubscribeStatusString": "用户点击行为（同意、取消发送通知）",  //SubscribeStatusString 的合法值  : reject -- 用户点击“取消”
     *                }
     *            ]
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackEventSubscribeMsgChange(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody , List<Map<String,Object>> subscribeMsgChangeEvent){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 订阅通知： 发送订阅通知
     * 场景：调用 bizsend 接口发送通知
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param subscribeMsgSentEvent            消息内容
     *           [
     *                {
     *                     "TemplateId": "模板 id",
     *                     "MsgID": "消息 id",
     *                     "ErrorCode": "推送结果状态码（0表示成功）",
     *                     "ErrorStatus": "推送结果状态码文字含义"
     *                }
     *            ]
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackEventSubscribeMsgSent(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody , List<Map<String,Object>> subscribeMsgSentEvent){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 草稿发布： 草稿发布事件推送发布结果通知
     * 场景：草稿发布事件推送发布结果通知
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param publishEventInfo  草稿发布结果
     * 发布成功时
     *   {
     *       "publish_id": "publish_id",
     *       "publish_status": "0",
     *       "article_id": "article_id",
     *       "article_detail": {
     *            "count" : "1",
     *            "item" : [
     *                  {
     *                      "idx": "1",
     *                      "article_url": "article_url"
     *                  },
     *                  ...
     *            ]
     *       }
     *   }
     * 原创审核不通过时
     *   {
     *       "publish_id": "publish_id",
     *       "publish_status": "2",
     *       "article_id": "article_id",
     *       "fail_idx": [
     *              1,2
     *       ]
     *   }
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackEventFreePublishJobFinish(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody , Map<String,Object> publishEventInfo){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 微信用户上报地理位置的消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param latitude          地理位置纬度
     * @param longitude         地理位置经度
     * @param precision         地理位置精度
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackEventLocation(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody, Double latitude,Double longitude, Double precision){
        return callBack(weixinUtil, messageEvent , requestBody);
    }


    /**
     * 资质认证成功（此时立即获得接口权限）的消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param expiredTime       有效期 (整型)，指的是时间戳，将于该时间戳认证过期
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackEventQualificationVerifySuccess(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody, Long expiredTime){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 资质认证失败的消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param failTime          失败发生时间 (整型)，时间戳
     * @param failReason        认证失败的原因
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackEventQualificationVerifyFail(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody, Long failTime, String failReason){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 名称认证成功（即命名成功）的消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param expiredTime       有效期 (整型)，指的是时间戳，将于该时间戳认证过期
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackEventNamingVerifySuccess(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody, Long expiredTime){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 名称认证失败（这时虽然客户端不打勾，但仍有接口权限）的消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param failTime          失败发生时间 (整型)，时间戳
     * @param failReason        认证失败的原因
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackEventNamingVerifyFail(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody, Long failTime, String failReason){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 年审通知消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param expiredTime       有效期 (整型)，指的是时间戳，将于该时间戳认证过期，需尽快年审
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackEventAnnualRenew(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody, Long expiredTime){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 认证过期失效通知审通知
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @param expiredTime       有效期 (整型)，指的是时间戳，表示已于该时间戳认证过期，需要重新发起微信认证
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackEventVerifyExpired(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody, Long expiredTime){
        return callBack(weixinUtil, messageEvent , requestBody);
    }

    /**
     * 没有任何消息响应时的消息回调方法
     * @param weixinUtil        当前微信实用工具类
     * @param messageEvent      消息事件
     * @param requestBody       请求体
     * @return  返回可直接响应微信公众号的xml数据，
     *    使用 WeChatUtil.sendArticleMsg() 返回响应图文消息
     *    使用 WeChatUtil.sendTextMsg()    返回响应文本消息
     */
    public String callBackDefault(WeixinUtil weixinUtil, MessageEvent messageEvent , Map<String,String> requestBody){
        return callBack(weixinUtil, messageEvent , requestBody);
    }



    protected String callBack(WeixinUtil weixinUtil, MessageEvent messageEvent, Map<String, String> requestBody){
        String respXml = null;
        if(messageEvent.getReply_message_type() == MessageEventType.REPLY_MESSAGE_TYPE_TEXT){
            respXml = WeChatUtil.sendTextMsg(requestBody, messageEvent.getReply_text());
        }else{
            if(messageEvent.getReply_news()!=null && messageEvent.getReply_news().size()>0){
                respXml = WeChatUtil.sendArticleMsg(requestBody, messageEvent.getReply_news());
            }else if(!StringUtil.isNullOrEmpty(messageEvent.getReply_media_id())) {
                NewsMaterial newsMaterial = weixinUtil.getArticleItem(messageEvent.getReply_media_id());
                if(newsMaterial!=null){
                    respXml = weixinUtil.sendArticleMsg(requestBody, newsMaterial.getNews_item());
                }else{
                    respXml = weixinUtil.sendTextMsg(requestBody, "没有回应图文信息！");
                }
            }else{
                respXml = weixinUtil.sendTextMsg(requestBody, "没有回应图文信息！");
            }
        }
        return respXml;
    }



}
