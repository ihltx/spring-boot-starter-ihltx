package com.ihltx.utility.weixin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageTagUserList {
    private Integer count;

    private List<String> openids;
    private String next_openid;

}
