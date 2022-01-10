package com.ihltx.utility.acl.service.impl;

import com.ihltx.utility.acl.config.AclConfig;
import com.ihltx.utility.acl.entity.SysLoginFailure;
import com.ihltx.utility.acl.entity.SysModule;
import com.ihltx.utility.acl.entity.SysUser;
import com.ihltx.utility.acl.mapper.SysLoginFailureMapper;
import com.ihltx.utility.acl.mapper.SysUserMapper;
import com.ihltx.utility.acl.service.UserService;
import com.ihltx.utility.util.DateUtil;
import com.ihltx.utility.util.SecurityUtil;
import com.ihltx.utility.util.StringUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@SuppressWarnings("all")
@Service
@ConditionalOnProperty(prefix = "ihltx.acl" , name = "enable" , havingValue = "true")
public class UserServiceImpl implements UserService {

    @Autowired
    private AclConfig aclConfig;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysLoginFailureMapper sysLoginFailureMapper;

    /**
     * 格式：{
     *          userId:{
     *              shopId1: {
     *                  "zh-CN" : List<SysModule>，
     *                  。。。
     *              }，
     *              。。。
     *          }
     *      }
     */

    private static Map<Long ,Map<Long , Map<String, List<SysModule>>>> cacheUserModules = new ConcurrentHashMap<>();

    @Override
    @DS("user")
    @Transactional
    public SysUser login(String userName, String password) {
        QueryWrapper<SysUser> qw = new QueryWrapper<>();
        qw.eq(true, "userName" , userName);
        qw.eq(true, "disabled" , false);
        qw.eq(true, "deleted" , false);
        qw.last("LIMIT 0,1");
        SysUser user = sysUserMapper.selectOne(qw);
        if(user!=null){
            if(SecurityUtil.md5(password, user.getSalt()).equals(user.getPassword())){
                this.clearCache(user.getUserId());
                SysUser u =  new SysUser();
                u.setUserId(user.getUserId());
                u.setLastLoginTime(new Date());
                sysUserMapper.updateById(u);
                return user;
            }
        }
        return null;
    }

    @Override
    @DS("user")
    @Transactional
    public List<SysModule> getModulesByUserId(Long userId, String language, Long shopId) {
        List<SysModule> modules = null;
        if(
                cacheUserModules.containsKey(userId) &&
                cacheUserModules.get(userId)!=null &&
                cacheUserModules.get(userId).containsKey(shopId) &&
                cacheUserModules.get(userId).get(shopId)!=null &&
                cacheUserModules.get(userId).get(shopId).containsKey(language) &&
                cacheUserModules.get(userId).get(shopId).get(language)!=null
            ){
            cacheUserModules.get(userId).get(shopId).get(language);
        }
        modules = sysUserMapper.getModulesByUserId(userId , language , shopId);
        Map<Long , Map<String , List<SysModule>>> maps= new ConcurrentHashMap<>();
        Map<String , List<SysModule>> subMaps= new ConcurrentHashMap<>();
        subMaps.put(language , modules);
        maps.put(shopId ,subMaps);
        cacheUserModules.put(userId , maps);
        return  modules;
    }

    @Override
    @DS("user")
    @Transactional
    public Boolean decide(Long userId, String url, String querystring, String language, Long shopId) {
        if(StringUtil.isNullOrEmpty(querystring)){
            querystring = "";
        }
        List<SysModule> modules = getModulesByUserId(userId , language , shopId);
        if(modules==null || modules.isEmpty()){
            return false;
        }
        SysModule module = null;
        for(SysModule m : modules){
            if(m.getModuleUrl().equalsIgnoreCase(url)){
                module = m;
                break;
            }
        }
        if(module==null){
            return false;
        }
        if(!StringUtil.isNullOrEmpty(module.getModuleQueryString())){
            if(!Pattern.compile("^" + module.getModuleQueryString() + "$").matcher(querystring).find()){
                return false;
            }
        }
        return true;
    }

    @Override
    public void clearCache(Long userId) {
        if(userId!=null){
            cacheUserModules.remove(userId);
        }
    }

    @Override
    @DS("user")
    @Transactional
    public void logout(SysUser user) {


    }

    @Override
    @DS("user")
    @Transactional
    public SysLoginFailure getLoginFailure(String userName , String ip) {
        QueryWrapper<SysLoginFailure> qwLoginFailure = new QueryWrapper<>();
        qwLoginFailure.eq(true , "userName" , userName);
        qwLoginFailure.eq(true , "loginIp" , ip);
        qwLoginFailure.last("LIMIT 0,1");

        return sysLoginFailureMapper.selectOne(qwLoginFailure);
    }

    @Override
    @DS("user")
    @Transactional
    public Boolean saveLoginFailure(SysLoginFailure loginFailure) {
        QueryWrapper<SysLoginFailure> qwLoginFailure = new QueryWrapper<>();
        qwLoginFailure.eq(true , "userName" , loginFailure.getUserName());
        qwLoginFailure.eq(true , "loginIp" , loginFailure.getLoginIp());
        qwLoginFailure.last("LIMIT 0,1");

        SysLoginFailure entity = sysLoginFailureMapper.selectOne(qwLoginFailure);
        Boolean rs = false;
        if(entity==null){
            rs =sysLoginFailureMapper.insert(loginFailure)>0;
        }else{
            rs = sysLoginFailureMapper.updateById(loginFailure)>=0;
        }
        return rs;
    }

    @Override
    @DS("user")
    @Transactional
    public Boolean deleteLoginFailure(String userName ,String ip) {
        QueryWrapper<SysLoginFailure> qwLoginFailure = new QueryWrapper<>();
        qwLoginFailure.eq(true , "userName" , userName);
        qwLoginFailure.eq(true , "loginIp" , ip);

        return sysLoginFailureMapper.delete(qwLoginFailure)>=0;
    }

    @Override
    @DS("user")
    @Transactional
    public Integer getLoginFailureUserNameNumber(String ip, Integer loginSameUserNameRetryTimes, Integer loginLockMinutes) {
        QueryWrapper<SysLoginFailure> qwLoginFailure = new QueryWrapper<>();
        qwLoginFailure.eq(true , "loginIp" , ip);
        qwLoginFailure.ge(true , "failureTimes" , loginSameUserNameRetryTimes);
        qwLoginFailure.ge(true , "loginTime" , new Date(DateUtil.getTimeMillis()-loginLockMinutes * 60 * 1000));
        return sysLoginFailureMapper.selectCount(qwLoginFailure);
    }
}
