package com.ihltx.utility.weixin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private int user_comment_id;
    private String openid;
    private String content;
    private Integer comment_type ;
    private Long create_time;

    private CommentReply reply;
}
