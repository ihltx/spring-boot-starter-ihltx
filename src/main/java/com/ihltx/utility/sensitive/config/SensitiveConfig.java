package com.ihltx.utility.sensitive.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Set;

@Data
@ConfigurationProperties(prefix = "ihltx.sensitive")
public class SensitiveConfig {

	/**
	 * 是否启用 Sensitive
	 */
	private Boolean enable = true;

	/**
	 * 敏感词匹配模式
	 * 如: 大中华帝国牛逼 , 对应: 大中华和大中华帝国 , 两个关键字，1为最小检查，会检查出大中华，2位最大，会检查出大中华帝国
	 * 	1	--	最小匹配		例如，对于要检查的字符串"意大利黑手党"，假如敏感词库有三个敏感词如： 意大利、黑手党、意大利黑手党，  则最小匹配将匹配检查到：   意大利  和  黑手党 两个敏感词
	 * 	2	--	最大匹配		例如，对于要检查的字符串"意大利黑手党"，假如敏感词库有三个敏感词如： 意大利、黑手党、意大利黑手党，  则最大匹配将匹配检查到：  意大利黑手党 一个个敏感词
	 */
	private Integer matchType = 1;

	/**
	 * 是否每shopId使用独立的敏感词库
	 * 	true	-- 使用独立的敏感词库，仅shopId没有词库时使用全局词库
	 * 	false 	-- 使用全局敏感词库
	 *
	 */
	private Boolean isStandalonePerShop = false;

	/**
	 * Sensitive 是否使用数据库
	 */
	private Boolean enableDataSource = false;

	/**
	 * Sensitive 是否使用数据库缓存
	 */
	private Boolean enableCache = true;

	/**
	 * Sensitive 使用数据库的MyBatis Plus数据源名称
	 */
	private String mybatisDynamicDataSourceName = "system";

	/**
	 * 配置中定义的全局敏感词列表
	 */
	private Set<String> words;

	/**
	 * 配置中定义的shop独立敏感词列表
	 */
	private Map<Long , Set<String>> shopWords;
	
}
