package com.ihltx.utility.acl.service;

import com.ihltx.utility.acl.entity.SysLoginFailure;
import com.ihltx.utility.acl.entity.SysModule;
import com.ihltx.utility.acl.entity.SysUser;

import java.util.List;

public interface UserService {

    /**
     * 根据用户名及密码登录并获取用户信息
     * 密码使用md5 salt加密
     * @param userName          用户名
     * @param password          密码
     * @return  SysUser  null --  用户名或密码错误
     */
    SysUser login(String userName, String password);

    /**
     * 用户注销
     * @param user       用户
     * @return  Boolean   true -- 注销成功   false --  注销失败
     */
    void logout(SysUser user);

    /**
     * 根据用户ID、语言代码、店铺ID获取用户拥有的功能模块列表
     *
     * @param userId        用户ID
     * @param language      语言代码
     * @param shopId        店铺ID
     * @return List<SysModule> 用户拥有的模块列表
     */
    List<SysModule> getModulesByUserId(Long userId, String language, Long shopId);


    /**
     * 根据用户ID，当前请求url及querystring,language,shopId判断用户是否有权限
     * @param userId        用户ID
     * @param url           url
     * @param querystring   查询串
     * @param language      语言代码
     * @param shopId        店铺ID
     * @return true --有权限   false--没有权限
     */
    Boolean decide(Long userId, String url, String querystring, String language, Long shopId);


    /**
     * 清除基于 userId 保存的 acl 权限缓存
     * @param userId    用户ID
     */
    void clearCache(Long userId);




    /**
     * 获取最近锁定时间范围之内指定ip的登录失败重试用户名总个数
     * @param ip                                    ip
     * @param loginSameUserNameRetryTimes           相同IP相同用户登录失败重试次数
     * @param loginLockMinutes                      锁定分钟数
     * @return  Integer
     */
    Integer getLoginFailureUserNameNumber(String ip, Integer loginSameUserNameRetryTimes, Integer loginLockMinutes);


    /**
     * 获取指定用户及ip的登录失败信息
     * @param userName      用户名
     * @param ip            ip
     * @return  SysLoginFailure   null -- 表示不存在登录失败信息
     */
    SysLoginFailure getLoginFailure(String userName, String ip);


    /**
     * 保存登录失败信息，存在即修改否则新增
     * @param loginFailure     登录失败信息
     * @return  Boolean   true -- 成功   false -- 失败
     */
    Boolean saveLoginFailure(SysLoginFailure loginFailure);

    /**
     * 删除指定用户基于当前ip的登录失败信息
     * @param userName      用户名
     * @param ip            ip
     * @return  Boolean   true -- 成功   false -- 失败
     */
    Boolean deleteLoginFailure(String userName, String ip);



}
