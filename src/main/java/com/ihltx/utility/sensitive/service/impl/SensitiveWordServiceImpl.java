package com.ihltx.utility.sensitive.service.impl;

import com.ihltx.utility.redis.service.RedisCacheUtil;
import com.ihltx.utility.sensitive.config.SensitiveConfig;
import com.ihltx.utility.sensitive.entity.SensitiveWord;
import com.ihltx.utility.sensitive.mapper.SensitiveWordMapper;
import com.ihltx.utility.sensitive.service.SensitiveWordService;
import com.ihltx.utility.util.StringUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("all")
@Service
@ConditionalOnProperty(prefix = "ihltx.sensitive", name = "enable" , havingValue = "true")
public class SensitiveWordServiceImpl implements SensitiveWordService {

    @Autowired
    private SensitiveWordMapper sensitiveWordMapper;

    @Autowired
    private SensitiveConfig sensitiveConfig;

    @Autowired(required = false)
    private RedisCacheUtil redisCacheUtil;

    private final String tableName = "sensitive_words";

    @Override
    @DS("system")
    @Transactional
    public Set<String> getSensitiveWords(Long shopId) {
        Set<String> words = new HashSet<>();
        Object[] keys = null;
        if(sensitiveConfig.getIsStandalonePerShop()){
            //启用独立敏感词库
            keys = new Object[]{SensitiveWordServiceImpl.class , "Set<String>" , "getSensitiveWords" , shopId };
        }else{
            //使用全局敏感词库
            keys = new Object[]{SensitiveWordServiceImpl.class , "Set<String>" , "getSensitiveWords" , -1L };
        }
        if(sensitiveConfig.getEnableDataSource()){
            if(sensitiveConfig.getEnableCache() && redisCacheUtil!=null){
                Object cache = redisCacheUtil.getCache(shopId , tableName , keys , Set.class);
                if(cache!=null){
                    return (Set<String>)cache;
                }
            }
            QueryWrapper<SensitiveWord> qw = new QueryWrapper<>();
            SensitiveWord sensitiveWord = null;
            if(sensitiveConfig.getIsStandalonePerShop()){
                //启用独立敏感词库
                qw.eq(true , "shopId" , shopId);
                qw.last("LIMIT 0,1");
                sensitiveWord = sensitiveWordMapper.selectOne(qw);
                if(sensitiveWord==null || sensitiveWord.getWords()==null || StringUtil.isNullOrEmpty(sensitiveWord.getWords().trim())){
                    //没有独立敏感词库，查询全局敏感词库
                    qw = new QueryWrapper<>();
                    qw.eq(true , "shopId" , -1L);
                    sensitiveWord = sensitiveWordMapper.selectOne(qw);
                }
            }else{
                //使用全局敏感词库
                qw.eq(true , "shopId" , -1L);
                qw.last("LIMIT 0,1");
                sensitiveWord = sensitiveWordMapper.selectOne(qw);
            }
            if(sensitiveWord!=null && sensitiveWord.getWords()!=null && !StringUtil.isNullOrEmpty(sensitiveWord.getWords().trim())){
                String[] arrWords = sensitiveWord.getWords().trim().split(",");
                for(String word : arrWords){
                    if(word!=null && !StringUtil.isNullOrEmpty(word.trim())){
                        words.add(word.trim());
                    }
                }
            }
            if(sensitiveConfig.getEnableCache() && redisCacheUtil!=null && !words.isEmpty()){
                redisCacheUtil.setCache(shopId , tableName , keys , words);
            }
            return  words;
        }else{
            if(sensitiveConfig.getIsStandalonePerShop()){
                //启用独立敏感词库
                if(sensitiveConfig.getShopWords()!=null && sensitiveConfig.getShopWords().containsKey(shopId) && sensitiveConfig.getShopWords().get(shopId)!=null && !sensitiveConfig.getShopWords().get(shopId).isEmpty()){
                    words = sensitiveConfig.getShopWords().get(shopId);
                }else{
                    words = sensitiveConfig.getWords();
                }
            }else{
                //使用全局敏感词库
                words = sensitiveConfig.getWords();
            }
        }
        return words;
    }

    @Override
    public void clearCache(Long shopId) {
        if(sensitiveConfig.getEnableDataSource()) {
            if (sensitiveConfig.getEnableCache() && redisCacheUtil!=null) {
                redisCacheUtil.clearCache(shopId , tableName);
            }
        }
    }
}
