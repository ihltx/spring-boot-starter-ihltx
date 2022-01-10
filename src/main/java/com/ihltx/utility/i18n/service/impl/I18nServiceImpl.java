package com.ihltx.utility.i18n.service.impl;

import com.ihltx.utility.i18n.config.I18nConfig;
import com.ihltx.utility.i18n.entity.Language;
import com.ihltx.utility.i18n.entity.LanguageResource;
import com.ihltx.utility.i18n.entity.LanguageResourceName;
import com.ihltx.utility.i18n.mapper.LanguageMapper;
import com.ihltx.utility.i18n.mapper.LanguageResourceMapper;
import com.ihltx.utility.i18n.mapper.LanguageResourceNameMapper;
import com.ihltx.utility.i18n.service.I18nService;
import com.ihltx.utility.util.SecurityUtil;
import com.ihltx.utility.util.StringUtil;
import com.ihltx.utility.util.WebUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("all")
@Service
@ConditionalOnProperty(prefix = "ihltx.i18n", name = "enableDataSource" , havingValue = "true")
public class I18nServiceImpl implements I18nService {


    @Autowired
    private I18nConfig i18nConfig;


    @Autowired
    private LanguageMapper languageMapper;

    @Autowired
    private LanguageResourceMapper languageResourceMapper;

    @Autowired
    private LanguageResourceNameMapper languageResourceNameMapper;

    /**
     * 语言缓存
     * 格式：  {"key前缀": {"key":Object,...}}
     */
    private Map<String, Map<String, Object>> cacheLanguages = new ConcurrentHashMap<>();

    private void setSelfCache(String prefix, Object[] keys , Object o){
        String key = SecurityUtil.md5(JSON.toJSONString(keys));
        if(this.cacheLanguages.containsKey(prefix) && this.cacheLanguages.get(prefix)!=null){
            this.cacheLanguages.get(prefix).put(key , o);
        }else{
            Map<String , Object> data = new HashMap<>();
            data.put(key , o);
            this.cacheLanguages.put(prefix , data);
        }
    }

    private <T> T getSelfCache(String prefix, Object[] keys){
        String key = SecurityUtil.md5(JSON.toJSONString(keys));
        T data = null;
        if(this.cacheLanguages.containsKey(prefix) && this.cacheLanguages.get(prefix)!=null && this.cacheLanguages.get(prefix).containsKey(key)  && this.cacheLanguages.get(prefix).get(key)!=null){
            return (T)this.cacheLanguages.get(prefix).get(key);
        }
        return data;
    }

    private void clearSelfCache(Long shopId){
        String prefix = this.i18nConfig.getCachePrefix() + shopId;
        if(this.cacheLanguages.containsKey(prefix)){
            this.cacheLanguages.remove(prefix);
        }
    }

    @Override
    @DS("system")
    @Transactional
    public List<Language> getLanguages(Long shopId) {
        List<Language> langs = null;
        String keyPrefix = this.i18nConfig.getCachePrefix();
        Object[] keys = new Object[] {this.getClass().toString() , "getLanguages" , "List<Language>" , shopId};
        if( this.i18nConfig.getEnableCache()){
            List obj = this.getSelfCache(keyPrefix , keys);
            if(obj!=null) {
                return (List<Language>)obj;
            }
        }

        if(this.i18nConfig.getEnableDataSource()) {
            QueryWrapper<Language> qw = new QueryWrapper<>();
            qw
                    .eq(true,"shopId" , shopId)
                    .eq(true,"langDeleted" , 0)
                    .eq(true,"langEnabled" , 1)
                    .orderByDesc("langOrder");

            langs = languageMapper.selectList(qw);
        }else{
            langs = this.i18nConfig.getLanguages();
        }

        if( this.i18nConfig.getEnableCache()){
            this.setSelfCache(keyPrefix, keys, langs);
        }
        return langs;
    }

    @Override
    @DS("system")
    @Transactional
    public List<Language> getLanguages() {
        return getLanguages(WebUtil.getShopId());
    }

    @Override
    @DS("system")
    @Transactional
    public String getMessage(Long shopId , String localeCode, String key, Object... args) {
        LanguageResource languageResource = null;
        String message = null;
        String keyPrefix = this.i18nConfig.getCachePrefix();
        Object[] keys = new Object[] {this.getClass().toString() , "getMessage" , "String"  , shopId , localeCode, key , args};
        if( this.i18nConfig.getEnableCache()){
            message = this.getSelfCache(keyPrefix , keys);
            if(message!=null) {
                return message;
            }
        }

        if(this.i18nConfig.getEnableDataSource()) {
            QueryWrapper<LanguageResource> qw = new QueryWrapper<>();
            qw
                    .select("langResValue")
                    .eq(true,"shopid" , -1)
                    .eq(true,"langCode" , localeCode)
                    .eq(true, "langResName" , key)
                    .last("LIMIT 0,1");

            languageResource = languageResourceMapper.selectOne(qw);
            if(languageResource!=null){
                message = languageResource.getLangResValue();
            }
            if(shopId!=-1){
                qw = new QueryWrapper<>();
                qw
                        .select("langResValue")
                        .eq(true,"shopid" , shopId)
                        .eq(true,"langCode" , localeCode)
                        .eq(true, "langResName" , key)
                        .last("LIMIT 0,1");

                languageResource = languageResourceMapper.selectOne(qw);
                if(languageResource!=null){
                    message = languageResource.getLangResValue();
                }
            }
        }


        if(!Strings.isEmpty(message)) {
            message = StringUtil.formatString(message, args);
        }
        if( this.i18nConfig.getEnableCache()){
            this.setSelfCache(keyPrefix, keys, message);
        }

        return message;
    }

    @Override
    @DS("system")
    @Transactional
    public String getMessage(String localeCode, String key, Object... args) {
        return getMessage(WebUtil.getShopId() , localeCode , key , args);
    }


    @Override
    @DS("system")
    @Transactional
    public Map<String, String> getMessages(Long shopId, String locale) {
        Map<String, String> messages = null;

        String keyPrefix = this.i18nConfig.getCachePrefix();
        Object[] keys = new Object[] {this.getClass().toString(), "Map<String, String>" ,  "getMessages"  , shopId, locale};
        if( this.i18nConfig.getEnableCache()){
            messages = this.getSelfCache(keyPrefix , keys);
            if(messages!=null) {
                return (Map<String, String>)messages;
            }
        }

        if(this.i18nConfig.getEnableDataSource()) {
            //全局语言资源，shopId=-1
            List<LanguageResource> globalRs = null;
            //当前语言资源
            List<LanguageResource> shopRs = null;

            QueryWrapper<LanguageResource> qw = new QueryWrapper<>();
            qw
                    .select("langResName", "langResValue")
                    .eq(true,"shopId" , -1L)
                    .eq(true,"langCode" , locale);

            globalRs = languageResourceMapper.selectList(qw);
            if(globalRs!=null && !globalRs.isEmpty()) {
                if(messages == null){
                    messages = new HashMap<String, String>();
                }
                for(LanguageResource row : globalRs) {
                    messages.put(row.getLangResName(), row.getLangResValue());
                }
            }

            if(shopId!=-1){
                qw = new QueryWrapper<>();
                qw
                        .select("langResName", "langResValue")
                        .eq(true,"shopId" , shopId)
                        .eq(true,"langCode" , locale);

                shopRs = languageResourceMapper.selectList(qw);
                if(shopRs!=null && !shopRs.isEmpty()) {
                    if(messages == null){
                        messages = new HashMap<String, String>();
                    }
                    for(LanguageResource row : shopRs) {
                        messages.put(row.getLangResName(), row.getLangResValue());
                    }
                }
            }
        }

        if( this.i18nConfig.getEnableCache()){
            this.setSelfCache(keyPrefix, keys, messages);
        }
        return messages;
    }

    @Override
    @DS("system")
    @Transactional
    public Map<String, String> getMessages(String locale) {
        return getMessages(WebUtil.getShopId() , locale);
    }

    public void clearCache(Long shopId){
        if( this.i18nConfig.getEnableCache()){
            this.clearSelfCache(shopId);
            if(shopId != -1L){
                this.clearSelfCache(-1L);
            }
        }

    }

    public void clearCache(){
        this.clearCache(WebUtil.getShopId());
    }

    @Override
    @DS("system")
    @Transactional
    public Boolean addLanguage(Language language) {
        return languageMapper.insert(language)>0;
    }

    @Override
    @DS("system")
    @Transactional
    public Boolean updateLanguage(Language language) {
        return languageMapper.updateById(language)>=0;
    }

    @Override
    @DS("system")
    @Transactional
    public Boolean deleteLanguage(Long langId) {
        QueryWrapper<Language> qw =new QueryWrapper<>();
        qw.eq(true , "langId" , langId);
        return languageMapper.delete(qw)>=0;
    }

    @Override
    @DS("system")
    @Transactional
    public Boolean deleteLanguage(QueryWrapper<Language> qw) {
        return languageMapper.delete(qw)>=0;
    }

    @Override
    @DS("system")
    @Transactional
    public IPage<Language> getLanguageList(IPage<Language> page, QueryWrapper<Language> qw) {
        return languageMapper.selectPage(page , qw);
    }

    @Override
    @DS("system")
    @Transactional
    public Boolean addLanguageResource(LanguageResource languageResource) {
        return languageResourceMapper.insert(languageResource)>0;
    }

    @Override
    @DS("system")
    @Transactional
    public Boolean updateLanguageResource(LanguageResource languageResource) {
        return languageResourceMapper.updateById(languageResource)>=0;
    }

    @Override
    @DS("system")
    @Transactional
    public Boolean deleteLanguageResource(Long langResId) {
        QueryWrapper<LanguageResource> qw =new QueryWrapper<>();
        qw.eq(true , "langResId" , langResId);
        return languageResourceMapper.delete(qw)>=0;
    }

    @Override
    @DS("system")
    @Transactional
    public Boolean deleteLanguageResource(QueryWrapper<LanguageResource> qw) {
        return languageResourceMapper.delete(qw)>=0;
    }

    @Override
    @DS("system")
    @Transactional
    public IPage<LanguageResource> getLanguageResourceList(IPage<LanguageResource> page, QueryWrapper<LanguageResource> qw) {
        return languageResourceMapper.selectPage(page , qw);
    }

    @Override
    @DS("system")
    @Transactional
    public Boolean addLanguageResourceName(LanguageResourceName languageResourceName) {
        return languageResourceNameMapper.insert(languageResourceName)>0;
    }

    @Override
    @DS("system")
    @Transactional
    public Boolean updateLanguageResourceName(LanguageResourceName languageResourceName) {
        return languageResourceNameMapper.updateById(languageResourceName)>=0;
    }

    @Override
    @DS("system")
    @Transactional
    public Boolean deleteLanguageResourceName(Long langResNameId) {
        QueryWrapper<LanguageResourceName> qw =new QueryWrapper<>();
        qw.eq(true , "langResNameId" , langResNameId);
        return languageResourceNameMapper.delete(qw)>=0;
    }

    @Override
    @DS("system")
    @Transactional
    public Boolean deleteLanguageResourceName(QueryWrapper<LanguageResourceName> qw) {
        return languageResourceNameMapper.delete(qw)>=0;
    }

    @Override
    @DS("system")
    @Transactional
    public IPage<LanguageResourceName> getLanguageResourceNameList(IPage<LanguageResourceName> page, QueryWrapper<LanguageResourceName> qw) {
        return languageResourceNameMapper.selectPage(page , qw);
    }

    @Override
    @DS("system")
    @Transactional
    public Boolean install(Long shopId, Object[] installLanguages) throws Exception {
        //1、获取-1店铺的要安装的语言信息并进行安装
        QueryWrapper<Language> qwLanguage =new QueryWrapper<>();
        qwLanguage.eq(true , "shopId" , -1L);
        qwLanguage.in((installLanguages!=null && installLanguages.length>0) , "langCode" , installLanguages);
        qwLanguage.orderByAsc("langId");
        List<Language> languages =  languageMapper.selectList(qwLanguage);

        //清空shopId的语言
        qwLanguage =new QueryWrapper<>();
        qwLanguage.eq(true , "shopId" , shopId);
        languageMapper.delete(qwLanguage);

        //安装
        if(languages!=null && !languages.isEmpty()){
            for (Language  language : languages){
                language.setLangId(0L);
                language.setShopId(shopId);
                if(languageMapper.insert(language)<=0){
                    throw new Exception("insert into Language failure. Language info: " + language.toString());
                }
            }
        }
        //2、获取-1店铺的要安装的语言名称信息并进行安装

        QueryWrapper<LanguageResourceName> qwLanguageResourceName =new QueryWrapper<>();
        qwLanguageResourceName.eq(true , "shopId" , -1L);
        qwLanguageResourceName.orderByAsc("langResNameId");
        List<LanguageResourceName> languageResourceNames =  languageResourceNameMapper.selectList(qwLanguageResourceName);

        //清空shopId的语言名称信息
        qwLanguageResourceName =new QueryWrapper<>();
        qwLanguageResourceName.eq(true , "shopId" , shopId);
        languageResourceNameMapper.delete(qwLanguageResourceName);

        //安装
        if(languageResourceNames!=null && !languageResourceNames.isEmpty()){
            for (LanguageResourceName  languageResourceName : languageResourceNames){
                languageResourceName.setLangResNameId(0L);
                languageResourceName.setShopId(shopId);
                if(languageResourceNameMapper.insert(languageResourceName)<=0){
                    throw new Exception("insert into LanguageResourceName failure. LanguageResourceName info: " + languageResourceName.toString());
                }
            }
        }

        //3、获取-1店铺的要安装的语言资源并进行安装
        QueryWrapper<LanguageResource> qwLanguageResource =new QueryWrapper<>();
        qwLanguageResource.eq(true , "shopId" , -1L);
        qwLanguage.in((installLanguages!=null && installLanguages.length>0) , "langCode" , installLanguages);
        qwLanguageResource.orderByAsc("langResId");
        List<LanguageResource> languageResources =  languageResourceMapper.selectList(qwLanguageResource);

        //清空shopId的语言资源信息
        qwLanguageResource =new QueryWrapper<>();
        qwLanguageResource.eq(true , "shopId" , shopId);
        languageResourceMapper.delete(qwLanguageResource);

        //安装
        if(languageResources!=null && !languageResources.isEmpty()){
            for (LanguageResource languageResource : languageResources){
                languageResource.setLangResId(0L);
                languageResource.setShopId(shopId);
                if(languageResourceMapper.insert(languageResource)<=0){
                    throw new Exception("insert into LanguageResource failure. LanguageResource info: " + languageResource.toString());
                }
            }
        }
        return  true;
    }
    @Override
    @DS("system")
    @Transactional
    public Boolean unInstall(Long shopId) {
        //1、清空shopId的语言资源信息
        QueryWrapper<LanguageResource> qwLanguageResource = new QueryWrapper<>();
        qwLanguageResource.eq(true , "shopId" , shopId);
        languageResourceMapper.delete(qwLanguageResource);

        //2、清空shopId的语言名称信息

        QueryWrapper<LanguageResourceName> qwLanguageResourceName = new QueryWrapper<>();
        qwLanguageResourceName.eq(true , "shopId" , shopId);
        languageResourceNameMapper.delete(qwLanguageResourceName);


        //3、清空shopId的语言
        QueryWrapper<Language> qwLanguage = new QueryWrapper<>();
        qwLanguage.eq(true , "shopId" , shopId);
        languageMapper.delete(qwLanguage);
        return  true;
    }
}
