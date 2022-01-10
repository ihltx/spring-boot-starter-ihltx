package com.ihltx.utility.acl.config;


import com.ihltx.utility.util.ImageVerifyCodeUtil;
import lombok.Data;

import java.awt.*;

@Data
public class VerificationCodeConfig {

    /**
     * 是否启用验证码
     */
    private Boolean enable = true;

    /**
     * 验证码key
     */
    private String key = "verificationCode";

    /**
     * 验证码类型
     * LOWERLETTER - 0, UPPERLETTER - 1, NUMBER - 2, SYMBOLS - 3, LOWERLETTER_UPPERLETTER - 4, LOWERLETTER_NUMBER - 5, LOWERLETTER_SYMBOLS - 6, UPPERLETTER_NUMBER - 7, UPPERLETTER_SYMBOLS - 8, NUMBER_SYMBOLS - 9, LOWERLETTER_UPPERLETTER_NUMBER - 10 , LOWERLETTER_UPPERLETTER_SYMBOLS - 11 , LOWERLETTER_NUMBER_SYMBOLS - 12 ,UPPERLETTER_NUMBER_SYMBOLS - 13 ,LOWERLETTER_UPPERLETTER_NUMBER_SYMBOLS - 14
     */
    private Integer type = ImageVerifyCodeUtil.NUMBER;

    /**
     * 验证码长度
     */
    private Integer length = 4;

    /**
     * 验证码图片宽度, px
     */
    private Integer width = 95;

    /**
     * 验证码图片高度, px
     */
    private Integer height = 25;

    /**
     * 验证码杂线数
     */
    private Integer disturbLineNumber = 40;

    /**
     * font size
     */
    private Integer fontSize = 18;

    /**
     * font name
     */
    private String fontName = "Fixedsys";

    /**
     * font style
     */
    private Integer fontStyle = Font.CENTER_BASELINE;

    /**
     * 验证码参数名
     */
    private String parameterName = "verificationCode";

    /**
     * 验证码过期时间，单位：秒
     */
    private Integer expires = 10;


    /**
     * 验证码图片url
     */
    private String url;



}
