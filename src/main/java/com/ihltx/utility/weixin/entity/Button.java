package com.ihltx.utility.weixin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 一级菜单最多3个
 */
public class Button implements Serializable {

    /**
     * 必须，如果为空表示为顶级菜单
     * 菜单的响应动作类型，view表示网页类型，click表示点击类型，miniprogram表示小程序类型
     * scancode_waitmsg     --  扫码带提示
     * scancode_push        --  扫码推事件
     * pic_sysphoto         --  系统拍照发图
     * pic_photo_or_album   --  拍照或者相册发图
     * pic_weixin           --  微信相册发图
     * location_select      --  发送位置
     * media_id             --  图片
     * view_limited         --  图文消息
     */
    private String type;


    /**
     * 必须
     * 菜单标题，不超过16个字节，子菜单不超过60个字节
     */
    private String name;

    /**
     * click等点击类型必须
     * 菜单KEY值，用于消息接口推送，不超过128字节
     */
    private String key;


    /**
     * view、miniprogram类型必须
     * 网页 链接，用户点击菜单可打开链接，不超过1024字节。 type为miniprogram时，不支持小程序的老版本客户端将打开本url。
     */
    private String url;


    /**
     * media_id类型和view_limited类型必须
     * 调用新增永久图片、音频、视频素材接口返回的合法media_id
     */
    private String media_id;

    /**
     * article_id类型和article_view_limited类型必须
     * 调用新增永久图文素材接口返回的合法media_id
     */
    private String article_id;

    /**
     * miniprogram类型必须
     * 小程序的appid（仅认证公众号可配置）
     */
    private String appid;

    /**
     * miniprogram类型必须
     * 小程序的页面路径
     */
    private String pagepath;


    /**
     * 二级菜单数组，个数应为1~5个
     */
    private List<Button> sub_button;

}
