package com.ihltx.utility.weixin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleItem {
    private String title;
    /**
     * 图文消息的封面图片素材id（必须是永久mediaID）
     */
    private String thumb_media_id;
    /**
     * 图文消息的封面图片url
     */
    private String thumb_url;
    private String author;
    /**
     * 图文消息的摘要
     */
    private String digest;
    private String content;
    /**
     * 图文消息的原文地址，即点击“阅读原文”后的URL
     */
    private String content_source_url;
    /**
     * 图文素材详情url
     */
    private String url;
    /**
     * 是否显示封面，0为false，即不显示，1为true，即显示
     */
    private Integer show_cover_pic;
    /**
     * 是否打开评论，0不打开，1打开
     */
    private Integer need_open_comment;
    /**
     * 是否粉丝才可评论，0所有人可评论，1粉丝才可评论
     */
    private Integer only_fans_can_comment;

    private Boolean is_deleted;
}
