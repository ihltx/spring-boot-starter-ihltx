package com.ihltx.utility.weixin.event;

import com.ihltx.utility.weixin.entity.ArticleItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 微信消息事件类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageEvent {

    /**
     * 消息ID
     */
    private Long message_id;

    /**
     * 消息是否启用
     */
    private Boolean enabled = false;

    /**
     * 消息类型，取值如： MessageType.MESSAGE_TYPE_KEYWORDS
     *
     */
    private Integer message_type;

    /**
     * 消息关键字
     */
    private String key;

    /**
     * 图片发送消息中发送的图片
     */
    private String image;

    /**
     * 语音发送消息中发送的语音
     */
    private String voice;

    /**
     * 视频发送消息中发送的视频
     */
    private String video;

    /**
     * 地理位置发送消息中发送的地址位置x坐标
     */
    private String location_x;
    /**
     * 地理位置发送消息中发送的地址位置y坐标
     */
    private String location_y;

    /**
     * link发送消息中发送的link
     */
    private String link;


    /**
     * 扫描二维码事件消息中的二维码值
     */
    private String scan;


    /**
     * 上报地理位置事件消息中的地址位置x坐标
     */
    private String event_location_x;

    /**
     * 上报地理位置事件消息中的地址位置y坐标
     */
    private String event_location_y;

    /**
     * 指定bean完全限定名，不为空则使用自定义bean处理该类型消息，bean必须继承MessageHandler并可重写默认的相应消息处理方法使用自己的消息处理方法
     * 将优先于 reply_message_type|reply_media_id|reply_text|reply_news进行处理
     */
    private String eventHandlerClass;

    /**
     * 响应消息类型
     * 取值： MessageType.REPLY_MESSAGE_TYPE_TEXT或MessageType.REPLY_MESSAGE_TYPE_NEWS
     */
    private Integer reply_message_type;


    /**
     * 图文响应内容media_id，将优于reply_text|reply_news进行处理
     */
    private String reply_media_id;

    /**
     * 文本响应内容，其中，可使用 \n进行换行
     */
    private String  reply_text;

    /**
     * 图文响应内容
     */
    private List<ArticleItem> reply_news;



}
