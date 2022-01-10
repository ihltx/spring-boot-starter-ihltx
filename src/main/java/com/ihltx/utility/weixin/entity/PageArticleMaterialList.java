package com.ihltx.utility.weixin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageArticleMaterialList {
    private int total_count;
    private int item_count;
    private List<ArticleMaterial> item;
}
