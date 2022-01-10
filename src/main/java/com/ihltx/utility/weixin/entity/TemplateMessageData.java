package com.ihltx.utility.weixin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 消息模板变量数据类
 */
public class TemplateMessageData {
    /**
     * 模板变量值，将替换例如： {{reason.DATA}} 部分
     */
    private String value;

    /**
     * 模板变量值颜色，默认为黑色，即：{{reason.DATA}} 部份的文字颜色
     */
    private String color;

}
