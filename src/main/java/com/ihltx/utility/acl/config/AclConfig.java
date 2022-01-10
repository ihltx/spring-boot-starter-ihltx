package com.ihltx.utility.acl.config;


import com.ihltx.utility.jwt.config.JwtConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@Data
@ConfigurationProperties(prefix = "ihltx.acl")
public class AclConfig {

    /**
     * 是否启用acl
     */
    private Boolean enable = true;

    /**
     * 指定acl 组件权限处理类，必须实现 UserService 接口，且必须注册为Bean
     */
    private String handleClass = "com.ihltx.utility.acl.service.impl.UserServiceImpl";

    /**
     * 通过header/request/cookie传递的jwt令牌名称
     */
    private String tokenName = "token";

    /**
     * jwt令牌获取顺序，最前面的优先级最高
     */
    private String tokenOrder = "header,request,cookie";

    /**
     * 登录页面url
     */
    private String loginPageUrl;


    /**
     * 基于Ajax请求的登录页面url
     */
    private String loginPageUrlForAjax;

    /**
     * 登录处理url
     */
    private String loginProcessingUrl;

    /**
     * 基于Ajax请求的登录处理url
     */
    private String loginProcessingUrlForAjax;

    /**
     * 登录失败rl
     */
    private String loginFailureUrl;

    /**
     * 返回页面的登录成功url
     */
    private String loginSuccessUrl;

    /**
     * 注销登录rl
     */
    private String logoutUrl;

    /**
     * 注销成功url
     */
    private String logoutSuccessUrl;

    /**
     * 相同IP登录失败重试用户名个数,超过指定个数，当前IP将被锁定指定分钟
     */
    private Integer loginSameIpRetryUserNameNumber = 100;

    /**
     * 相同IP相同用户名登录失败重试次数，超过指定次数，当前IP及用户名将被锁定指定分钟
     */
    private Integer loginSameUserNameRetryTimes = 6;

    /**
     * 登录超出次数，将被锁定指定分钟
     */
    private Integer loginLockMinutes = 20;


    /**
     * 非法会话url
     */
    private String invalidSessionUrl;

    /**
     * 用户名参数名
     */
    private String userNameParameter= "userName";

    /**
     * 用户名参数名
     */
    private String passwordParameter= "password";


    /**
     * 图片验证码配置
     */
    private VerificationCodeConfig verificationCode = new VerificationCodeConfig();


    /**
     * 是否记住我
     */
    private Boolean rememberMe = true;

    /**
     * 记住我参数名,1|true|yes --- 记住我   0|false|no -- 不记住我
     */
    private String rememberMeParameter = "rememberMe";

    /**
     * csrf名称
     */
    private String csrfParameterName = "_csrf";

    /**
     * 允许直接访问的url，必须以/开始，允许以*代表除/之外的字符，允许以**代表多级文件夹及文件
     */
    private Set<String> permitUrls;

    /**
     * jwt 配置
     */
    private JwtConfig jwt = new JwtConfig();
}
