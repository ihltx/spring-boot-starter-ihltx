package com.ihltx.utility.weixin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageCommentList {
    private Integer total;

    private List<Comment> comment;
}
