package com.ihltx.utility.weixin.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信消息事件类型类
 */

public class MessageEventType {


    /**
     * 默认消息类型，当没有任何一个消息处理时，将使用默认消息类型进行处理
     */
    public static final Integer MESSAGE_TYPE_DEFAULT=-1;
    /**
     * 关键字消息类型,取值范围为：0-99(总共100个)，即最多可定义内置关键字99个，基于取值为0表示自定义关键字，将联合key进行匹配
     */
    public static final Integer MESSAGE_TYPE_KEYWORDS=0;

    /**
     * 百科关键字消息类型
     */
    public static final Integer MESSAGE_TYPE_KEYWORDS_BAIKE=1;

    /**
     * 翻译关键字消息类型
     */
    public static final Integer MESSAGE_TYPE_KEYWORDS_TRANSLAE=2;

    /**
     * 天气关键字消息类型
     */
    public static final Integer MESSAGE_TYPE_KEYWORDS_WEATHER=3;


    /**
     * 快递关键字消息类型
     */
    public static final Integer MESSAGE_TYPE_KEYWORDS_EXPRESS=4;

    /**
     * 日历关键字消息类型
     */
    public static final Integer MESSAGE_TYPE_KEYWORDS_CALENDAR=5;

    /**
     * 头条新闻关键字消息类型
     */
    public static final Integer MESSAGE_TYPE_KEYWORDS_NEWS=6;

    /**
     * 可供微信用户点击选择的菜单单击消息类型
     */
    public static final Integer MESSAGE_TYPE_KEYWORDS_MSGMENU=7;

    /**
     * 图片消息类型
     */
    public static final Integer MESSAGE_TYPE_IMAGE=110;

    /**
     * 语音消息类型
     */
    public static final Integer MESSAGE_TYPE_VOICE=120;

    /**
     * 视频消息类型
     */
    public static final Integer MESSAGE_TYPE_VIDEO=130;
    /**
     * 小视频消息类型
     */
    public static final Integer MESSAGE_TYPE_SHORT_VIDEO=135;

    /**
     * 发送location位置信息消息类型
     */
    public static final Integer MESSAGE_TYPE_LOCATION=140;

    /**
     * 发送链接消息类型
     */
    public static final Integer MESSAGE_TYPE_LINK=150;

    /**
     * 事件推送消息类型,取值范围为：500-999(总共500个)，即最多可定义内置关键字499个，基于取值为500表示自定义事件消息，将联合key进行匹配
     */
    public static final Integer MESSAGE_TYPE_EVENT=500;

    /**
     * 关注事件消息类型
     */
    public static final Integer MESSAGE_TYPE_EVENT_SUBSCRIBE=501;

    /**
     * 取消关注事件消息类型
     */
    public static final Integer MESSAGE_TYPE_EVENT_UNSUBSCRIBE=502;

    /**
     * 扫描二维码事件消息类型
     */
    public static final Integer MESSAGE_TYPE_EVENT_SCAN=503;

    /**
     * 扫码推事件消息类型
     */
    public static final Integer MESSAGE_TYPE_EVENT_SCANCODE_PUSH=504;

    /**
     * 扫码带提示事件消息类型
     */
    public static final Integer MESSAGE_TYPE_EVENT_SCANCODE_WAITMSG=505;



    /**
     * 上报地理位置事件消息类型
     */
    public static final Integer MESSAGE_TYPE_EVENT_LOCATION=506;

    /**
     * 菜单click事件消息类型
     */
    public static final Integer MESSAGE_TYPE_EVENT_CLICK=507;

    /**
     * 菜单系统拍照发图pic_sysphoto事件消息类型
     */
    public static final Integer MESSAGE_TYPE_EVENT_PIC_SYSPHOTO=508;

    /**
     * 菜单拍照或者相册发图pic_photo_or_album事件消息类型
     */
    public static final Integer MESSAGE_TYPE_EVENT_PIC_PHOTO_OR_ALBUM=509;

    /**
     * 菜单微信相册发图pic_weixin事件消息类型
     */
    public static final Integer MESSAGE_TYPE_EVENT_PIC_WEIXIN=510;

    /**
     * 菜单发送位置location_select事件消息类型
     */
    public static final Integer MESSAGE_TYPE_EVENT_LOCATION_SELECT=511;



    /**
     * 菜单view事件消息类型
     */
    public static final Integer MESSAGE_TYPE_EVENT_VIEW=512;


    /**
     * 点击菜单跳转小程序的事件推送
     */
    public static final Integer MESSAGE_TYPE_EVENT_VIEW_MINIPROGRAM=513;


    /**
     * 模版消息发送任务完成后，微信服务器会将是否送达成功作为通知，发送到开发者中心中填写的服务器配置地址中。
     * 模版消息送达时的事件推送
     */
    public static final Integer MESSAGE_TYPE_EVENT_TEMPLATESENDJOBFINISH=514;


    /**
     * 事件推送群发结果
     * 由于群发任务提交后，群发任务可能在一定时间后才完成，因此，群发接口调用时，仅会给出群发任务是否提交成功的提示，若群发任务提交成功，则在群发任务结束时，会向开发者在公众平台填写的开发者URL（callback URL）推送事件。
     * 需要注意，由于群发任务彻底完成需要较长时间，将会在群发任务即将完成的时候，就推送群发结果，此时的推送人数数据将会与实际情形存在一定误差
     */
    public static final Integer MESSAGE_TYPE_EVENT_MASSSENDJOBFINISH=515;

    /**
     * 事件推送 用户操作订阅通知弹窗
     * 场景：用户在图文等场景内订阅通知的操作
     */
    public static final Integer MESSAGE_TYPE_EVENT_SUBSCRIBE_MSG_POPUP=516;

    /**
     * 事件推送 用户管理订阅通知
     * 场景：用户在服务通知管理页面做通知管理时的操作
     */
    public static final Integer MESSAGE_TYPE_EVENT_SUBSCRIBE_MSG_CHANGE=517;

    /**
     * 事件推送 发送订阅通知
     * 场景：调用 bizsend 接口发送通知
     */
    public static final Integer MESSAGE_TYPE_EVENT_SUBSCRIBE_MSG_SENT=518;

    /**
     * 事件推送 草稿发布事件推送发布结果
     * 场景：由于发布任务提交后，发布任务可能在一定时间后才完成，因此，发布接口调用时，仅会给出发布任务是否提交成功的提示，若发布任务提交成功，则在发布任务结束时，会向开发者在公众平台填写的开发者URL（callback URL）推送事件。
     */
    public static final Integer MESSAGE_TYPE_EVENT_FREEPUBLISHJOBFINISH=519;

    /**
     * 资质认证成功 事件推送
     * 场景：为了帮助公众号开发者获取公众号的认证状态，也为了第三方平台开发者获知旗下公众号的认证状态，微信公众平台提供了公众号认证过程中各个阶段的事件推送
     */
    public static final Integer MESSAGE_TYPE_EVENT_QUALIFICATION_VERIFY_SUCCESS=520;

    /**
     * 资质认证失败 事件推送
     * 场景：为了帮助公众号开发者获取公众号的认证状态，也为了第三方平台开发者获知旗下公众号的认证状态，微信公众平台提供了公众号认证过程中各个阶段的事件推送
     */
    public static final Integer MESSAGE_TYPE_EVENT_QUALIFICATION_VERIFY_FAIL=521;

    /**
     * 名称认证成功（即命名成功）
     */
    public static final Integer MESSAGE_TYPE_EVENT_NAMING_VERIFY_SUCCESS=522;

    /**
     * 名称认证失败（这时虽然客户端不打勾，但仍有接口权限）
     */
    public static final Integer MESSAGE_TYPE_EVENT_NAMING_VERIFY_FAIL=523;


    /**
     * 年审通知
     */
    public static final Integer MESSAGE_TYPE_EVENT_ANNUAL_RENEW=524;


    /**
     * 认证过期失效通知
     */
    public static final Integer MESSAGE_TYPE_EVENT_VERIFY_EXPIRED=525;

    /**
     * 响应消息类型，文本响应
     */
    public static final Integer REPLY_MESSAGE_TYPE_TEXT=0;

    /**
     * 响应消息类型，图文响应
     */
    public static final Integer REPLY_MESSAGE_TYPE_NEWS=1;

}
