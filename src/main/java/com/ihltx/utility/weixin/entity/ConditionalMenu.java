package com.ihltx.utility.weixin.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 微信个性化菜单类
 */
@Data
public class ConditionalMenu implements Serializable {
    private String menuid;

    /**
     * 菜单
     */
    private List<Button> button;

    /**
     * 条件
     */
    private Matchrule matchrule;
}
