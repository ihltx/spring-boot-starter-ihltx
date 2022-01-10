package com.ihltx.utility.weixin.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 微信菜单类
 */
@Data
public class Menu implements Serializable {
    private String menuid;

    private Boolean isMenuOpen = false;
    private List<Button> button;

    /**
     * 个性化菜单集合
     *  menuid => ConditionalMenu
     */
    private Map<String , ConditionalMenu> conditionalmenu;
}
