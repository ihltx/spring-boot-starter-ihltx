package com.ihltx.utility.sensitive.service;

import java.util.Set;

public interface SensitiveWordService {

    /**
     * 获取指定shopId的敏感词列表
     * 如果没有启用独立敏感词，则获取全局敏感词列表(shopId=-1)，如果启用了独立敏感词，则获取指定shopId的敏感词列表，如果该shopId没有独立敏感词列表则返回全局敏感词列表
     * 如果没有启用数据库则将返回配置中的敏感词列表
     * @param shopId        shopId
     * @return Set<String>
     */
    Set<String> getSensitiveWords(Long shopId);

    /**
     * 清除指定shopId的敏感词缓存列表
     * @param shopId        shopId
     */
    void clearCache(Long shopId);
}
