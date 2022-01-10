package com.ihltx.utility.weixin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsMaterial {
    private String media_id;
    private Long create_time;
    private Long update_time;
    private List<ArticleItem> news_item;
}
