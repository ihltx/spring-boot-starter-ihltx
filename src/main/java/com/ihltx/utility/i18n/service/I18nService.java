package com.ihltx.utility.i18n.service;

import com.ihltx.utility.i18n.config.I18nConfig;
import com.ihltx.utility.i18n.entity.Language;
import com.ihltx.utility.i18n.entity.LanguageResource;
import com.ihltx.utility.i18n.entity.LanguageResourceName;
import com.ihltx.utility.i18n.mapper.LanguageMapper;
import com.ihltx.utility.i18n.mapper.LanguageResourceMapper;
import com.ihltx.utility.i18n.mapper.LanguageResourceNameMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Map;

/**
 * 应用i18n服务接口
 * @author Administrator
 *
 */
public interface I18nService {
	/**
	 * 基于指定shopId返回所支持的语言列表
	 * @param shopId   shopId
	 * @return List<Language>
	 */
	List<Language> getLanguages(Long shopId);


	/**
	 * 基于当前shopId返回所支持的语言列表
	 * @return
	 */
	List<Language> getLanguages();


	/**
	 * 基于指定shopId、localeCode、key及args返回给定语言资源字符串
	 * @param shopId   		shopId
	 * @param localeCode   当前语言以-连接的语言code
	 * @param key      语言资源key
	 * @param args     参数数组可不传递或为数组
	 * @return String  null--表示不存在
	 */
	String getMessage(Long shopId, String localeCode, String key, Object... args);

	/**
	 * 基于用户自定义业务逻辑返回给定语言资源key以及参数数组对应的资源字符串
	 * @param localeCode   当前语言以-连接的语言code
	 * @param key      语言资源key
	 * @param args     参数数组可不传递或为数组
	 * @return String  null--表示不存在
	 */
	String getMessage(String localeCode, String key, Object... args);


	/**
	 * 基于用户自定义业务逻辑返回给定语言所有资源Map<String,String>集合
	 * @param shopId   		shopId
	 * @param locale  当前语言以-连接的语言code
	 * @return Map<String,String>
	 */
	Map<String,String> getMessages(Long shopId, String locale);

	/**
	 * 基于用户自定义业务逻辑返回给定语言所有资源Map<String,String>集合
	 * @param locale  当前语言以-连接的语言code
	 * @return Map<String,String>
	 */
	Map<String,String> getMessages(String locale);

	/**
	 * 清除指定shopId的语言缓存及全局语言缓存
	 * @param shopId
	 */
	void clearCache(Long shopId);

	/**
	 * 清除当前shopId的语言缓存及全局语言缓存
	 */
	void clearCache();


	/**
	 * 添加语言
	 * @param language
	 * @return Boolean
	 * 		true 	--	success
	 * 		false	--	failure
	 */
	Boolean addLanguage(Language language);


	/**
	 * 修改语言
	 * @param language
	 * @return Boolean
	 * 		true 	--	success
	 * 		false	--	failure
	 */
	Boolean updateLanguage(Language language);

	/**
	 * 删除语言
	 * @param langId
	 * @return Boolean
	 * 		true 	--	success
	 * 		false	--	failure
	 */
	Boolean deleteLanguage(Long langId);

	/**
	 * 根据qw删除语言
	 * @param qw	条件包装器
	 * @return Boolean
	 * 		true 	--	success
	 * 		false	--	failure
	 */
	Boolean  deleteLanguage(QueryWrapper<Language> qw);


	/**
	 * 根据qw获取分页系统日志列表
	 * @param page		分页信息
	 * @param qw		查询参数包装器
	 * @return IPage<Language>
	 */
	IPage<Language> getLanguageList(IPage<Language> page, QueryWrapper<Language> qw);


	/**
	 * 添加语言资源
	 * @param languageResource
	 * @return Boolean
	 * 		true 	--	success
	 * 		false	--	failure
	 */
	Boolean addLanguageResource(LanguageResource languageResource);


	/**
	 * 修改语言资源
	 * @param languageResource
	 * @return Boolean
	 * 		true 	--	success
	 * 		false	--	failure
	 */
	Boolean updateLanguageResource(LanguageResource languageResource);

	/**
	 * 删除语言资源
	 * @param langResId
	 * @return Boolean
	 * 		true 	--	success
	 * 		false	--	failure
	 */
	Boolean deleteLanguageResource(Long langResId);

	/**
	 * 根据qw删除语言资源
	 * @param qw	条件包装器
	 * @return Boolean
	 * 		true 	--	success
	 * 		false	--	failure
	 */
	Boolean  deleteLanguageResource(QueryWrapper<LanguageResource> qw);


	/**
	 * 根据qw获取分页语言资源列表
	 * @param page		分页信息
	 * @param qw		查询参数包装器
	 * @return IPage<LanguageResource>
	 */
	IPage<LanguageResource> getLanguageResourceList(IPage<LanguageResource> page, QueryWrapper<LanguageResource> qw);












	/**
	 * 添加语言资源名称
	 * @param languageResourceName
	 * @return Boolean
	 * 		true 	--	success
	 * 		false	--	failure
	 */
	Boolean addLanguageResourceName(LanguageResourceName languageResourceName);


	/**
	 * 添加语言资源名称
	 * @param languageResourceName
	 * @return Boolean
	 * 		true 	--	success
	 * 		false	--	failure
	 */
	Boolean updateLanguageResourceName(LanguageResourceName languageResourceName);

	/**
	 * 添加语言资源名称
	 * @param langResNameId
	 * @return Boolean
	 * 		true 	--	success
	 * 		false	--	failure
	 */
	Boolean deleteLanguageResourceName(Long langResNameId);

	/**
	 * 根据qw删除语言资源名称
	 * @param qw	条件包装器
	 * @return Boolean
	 * 		true 	--	success
	 * 		false	--	failure
	 */
	Boolean  deleteLanguageResourceName(QueryWrapper<LanguageResourceName> qw);


	/**
	 * 根据qw获取分页语言资源名称列表
	 * @param page		分页信息
	 * @param qw		查询参数包装器
	 * @return IPage<LanguageResourceName>
	 */
	IPage<LanguageResourceName> getLanguageResourceNameList(IPage<LanguageResourceName> page, QueryWrapper<LanguageResourceName> qw);




	/**
	 * 基于 -1的全局资源向指定shopId安装语言资源
	 * @param shopId
	 * @param installLanguages		要安装的语言列表，如果为空或空数组表示安装所有语言
	 * @return Boolean
	 * 		true 	--	success
	 * 		false	--	failure
	 */
	Boolean install(Long shopId, Object[] installLanguages) throws Exception;


	/**
	 * 卸载语言资源
	 * @param shopId
	 * @return Boolean
	 * 		true 	--	success
	 * 		false	--	failure
	 */
	Boolean unInstall(Long shopId);

}
