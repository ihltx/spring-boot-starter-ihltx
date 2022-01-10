package com.ihltx.utility.i18n.service;

import com.ihltx.utility.i18n.config.I18nConfig;
import com.ihltx.utility.i18n.entity.Language;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Properties;

public interface I18nUtil {


	/**
	 * 获取 I18nConfig 配置
	 * @return
	 */
	I18nConfig getI18nConfig();

	/**
	 * 设置 I18nConfig 配置
	 * @param i18nConfig
	 */
	void setI18nConfig(I18nConfig i18nConfig);


	/**
	 * 获取在request或header或session或View中保存的当前语言的名称
	 * @return String
	 */
	String getRequestAndSessionAndViewLanguageName();

	/**
	 * 获取指定shopId的默认语言
	 * 		1、如果未启用数据库，shopId没有特殊配置语言，则返回全局配置默认语言
	 * 		2、如果启用数据库，则返回数据库中shopId的默认语言
	 *
	 * @param shopId          shopId
	 * @return String
	 * 		Returns default language
	 * 		null -- not found default language
	 */
	String getDefaultLanguage(Long shopId);


	/**
	 * 基于指定shopId及指定request获取默认语言
	 * 		1、如果未启用数据库，shopId没有特殊配置语言，则返回全局配置默认语言
	 * 		2、如果启用数据库，则返回数据库中shopId的默认语言getLanguages
	 *
	 * @param shopId          shopId
	 * @param request     指定request
	 * @return String
	 * 		Returns default language
	 * 		null -- not found default language
	 */
	String getDefaultLanguage(Long shopId, HttpServletRequest request);


	/**
	 * 从指定请求中获取当前shopId的默认语言
	 * 		1、如果未启用数据库，shopId没有特殊配置语言，则返回全局配置默认语言
	 * 		2、如果启用数据库，则返回数据库中shopId的默认语言
	 *
	 * @param request     指定request
	 * @return String
	 * 		Returns default language
	 * 		null -- not found default language
	 */
	String getDefaultLanguage(HttpServletRequest request);

	/**
	 * 从当前请求中获取当前shopId的默认语言
	 * 		1、如果未启用数据库，shopId没有特殊配置语言，则返回全局配置默认语言
	 * 		2、如果启用数据库，则返回数据库中shopId的默认语言
	 *
	 * @return String
	 * 		Returns default language
	 * 		null -- not found default language
	 */
	String getDefaultLanguage();



	/**
	 * 基于指定请求获取当前语言
	 * 	如果request为空，则基于
	 * @param request     指定request
	 * @return String
	 */
	String getLanguage(HttpServletRequest request);


	/**
	 * 基于当前请求获取当前语言
	 * @return String
	 */
	String getLanguage();



	/**
	 * 基于指定请求设置当前语言
	 * @param request     指定request
	 * @param language 语言代码
	 * @return true--成功   false--失败
	 */
	Boolean setLanguage(HttpServletRequest request, String language);


	/**
	 * 基于当前请求设置当前语言
	 * @param language 语言代码
	 * @return true--成功   false--失败
	 */
	Boolean setLanguage(String language);


	/**
	 * 基于指定请求及shopid获取当前语言对象LanguageModel
	 * @param shopId          shopId
	 * @param request         request,null表示非web环境
	 * @return LanguageModel
	 */
	Language getCurrLanguage(Long shopId, HttpServletRequest request);

	/**
	 * 基于指定请求及当前shopId获取当前语言对象LanguageModel
	 * @param request         request,null表示非web环境
	 * @return LanguageModel
	 */
	Language getCurrLanguage(HttpServletRequest request);

	/**
	 * 基于当前请求及指定shopId获取当前语言对象LanguageModel
	 * @param shopId         	shopId
	 * @return LanguageModel
	 */
	Language getCurrLanguage(Long shopId);

	/**
	 * 基于当前请求及当前shopId获取当前语言对象LanguageModel
	 * @return LanguageModel
	 */
	Language getCurrLanguage();


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
	 * 基于指定shopId及获取当前支持所有语言列表，优先从果数据库中获取所有语言列表，不存在则从配置文件中获取语言列表
	 * @param shopId	shopId
	 * @return List<LanguageModel>
	 */
	List<Language> getLanguages(Long shopId);


	/**
	 * 基于当前shopId及获取当前支持所有语言列表，优先从果数据库中获取所有语言列表，不存在则从配置文件中获取语言列表
	 * @return List<LanguageModel>
	 */
	List<Language> getLanguages();


	/**
	 * 判断指定language语言(-连接)是否在shopId存在
	 * @param shopId		shopId
	 * @param language		语言代码(-连接)
	 * @return Boolean
	 * 		true	--	exists
	 * 		false	--	not exists
	 */
	Boolean existLanguage(Long shopId, String language);

	/**
	 * 判断指定language语言(-连接)是否在当前shopId存在
	 * @param language		语言代码(-连接)
	 * @return Boolean
	 * 		true	--	exists
	 * 		false	--	not exists
	 */
	Boolean existLanguage(String language);


	/**
	 * 基于指定shopId及指定语言，获取语言包中资源，优先使用数据库中的语言资源，如果数据库中不存在语言资源则使用配置文件中语言资源
	 *
	 * @param shopId 	shopId
	 * @param language 语言代码，以-间隔
	 * @param code 语言资源名称
	 * @param args 传递给语言资源的参数数组，0--{0} 1--{1}
	 * @return String
	 */
	String getMessage(Long shopId, String language, String code, Object[] args);

	/**
	 * 基于当前shopId及指定语言，获取语言包中资源，优先使用数据库中的语言资源，如果数据库中不存在语言资源则使用配置文件中语言资源
	 *
	 * @param language 语言代码，以-间隔
	 * @param code 语言资源名称
	 * @param args 传递给语言资源的参数数组，0--{0} 1--{1}
	 * @return String
	 */
	String getMessage(String language, String code, Object[] args);


	/**
	 * 基于指定shopId及基于当前语言，获取语言包中资源，优先使用数据库中的语言资源，如果数据库中不存在语言资源则使用配置文件中语言资源
	 *
	 * @param shopId	shopId
	 * @param code 语言资源名称
	 * @param args 传递给语言资源的参数数组，0--{0} 1--{1}
	 * @return String
	 */
	String getMessage(Long shopId, String code, Object[] args);


	/**
	 * 基于当前shopId及基于当前语言，获取语言包中资源，优先使用数据库中的语言资源，如果数据库中不存在语言资源则使用配置文件中语言资源
	 *
	 * @param code 语言资源名称
	 * @param args 传递给语言资源的参数数组，0--{0} 1--{1}
	 * @return String
	 */
	String getMessage(String code, Object[] args);



	/**
	 * 基于指定shopId及指定语言，获取语言包中资源，优先使用数据库中的语言资源，如果数据库中不存在语言资源则使用配置文件中语言资源
	 *
	 * @param shopId 	shopId
	 * @param language 语言代码，以-间隔
	 * @param code 语言资源名称
	 * @return String
	 */
	String getMessage(Long shopId, String language, String code);

	/**
	 * 基于当前shopId及指定语言，获取语言包中资源，优先使用数据库中的语言资源，如果数据库中不存在语言资源则使用配置文件中语言资源
	 *
	 * @param language 语言代码，以-间隔
	 * @param code 语言资源名称
	 * @return String
	 */
	String getMessage(String language, String code);


	/**
	 * 基于指定shopId及基于当前语言，获取语言包中资源，优先使用数据库中的语言资源，如果数据库中不存在语言资源则使用配置文件中语言资源
	 *
	 * @param shopId	shopId
	 * @param code 语言资源名称
	 * @return String
	 */
	String getMessage(Long shopId, String code);

	/**
	 * 基于当前shopId及基于当前语言，获取语言包中资源，优先使用数据库中的语言资源，如果数据库中不存在语言资源则使用配置文件中语言资源
	 *
	 * @param code 语言资源名称
	 * @return String
	 */
	String getMessage(String code);



	/**
	 * 基于指定shopId及基于指定语言获取所有语言资源，并以json字符串返回用于js中语言资源
	 * 将优先获取基于i18nService所获得的语言资源并覆盖配置文件中语言资源
	 * @param shopId	shopId
	 * @return String
	 */
	String getMessages(Long shopId, String language);

	/**
	 * 基于当前shopId及基于指定语言获取所有语言资源，并以json字符串返回用于js中语言资源
	 * 将优先获取基于i18nService所获得的语言资源并覆盖配置文件中语言资源
	 * @return String
	 */
	String getMessages(String language);



	/**
	 * 基于指定shopId及基于当前语言获取所有语言资源，并以json字符串返回用于js中语言资源
	 * 将优先获取基于i18nService所获得的语言资源并覆盖配置文件中语言资源
	 * @param shopId	shopId
	 * @return String
	 */
	String getMessages(Long shopId);


	/**
	 * 基于当前shopId及基于当前语言获取所有语言资源，并以json字符串返回用于js中语言资源
	 * 将优先获取基于i18nService所获得的语言资源并覆盖配置文件中语言资源
	 * @return String
	 */
	String getMessages();


	/**
	 * 基于指定shopId及基于指定语言获取所有语言资源，并以Properties返回
	 * 将优先获取基于i18nService所获得的语言资源并覆盖配置文件中语言资源
	 * @param shopId	shopId
	 * @return String
	 */
	Properties getMessageProperties(Long shopId, String language);


	/**
	 * 基于当前shopId及基于指定语言获取所有语言资源，并以Properties返回
	 * 将优先获取基于i18nService所获得的语言资源并覆盖配置文件中语言资源
	 * @return String
	 */
	Properties getMessageProperties(String language);


	/**
	 * 基于指定shopId及基于当前语言获取所有语言资源，并以Properties返回
	 * 将优先获取基于i18nService所获得的语言资源并覆盖配置文件中语言资源
	 * @param shopId	shopId
	 * @return String
	 */
	Properties getMessageProperties(Long shopId);

	/**
	 * 基于当前shopId及基于当前语言获取所有语言资源，并以Properties返回
	 * 将优先获取基于i18nService所获得的语言资源并覆盖配置文件中语言资源
	 * @return String
	 */
	Properties getMessageProperties();




	/**
	 * 根据指定shopId及语言代码从语言资源文件中获取所有语言资源到Properties并返回
	 * 		1、如果shopId为null或不存在将使用全局语言资源文件(例：0/messages_zh_CN.properties不存在，将使用messages_zh_CN.properties)
	 * 		2、如果language为null或不存在将使用全局默认资源文件(如：messages.properties)
	 *
	 * @param shopId		shopId，允许为null
	 * @param language		语言代码，以-间隔，允许为null
	 * @return	Properties
	 * 		null --  failure
	 */
	Properties getConfigurationMessageProperties(Long shopId, String language);


	/**
	 * 基于当前shopId及指定语言获取指定语言资源文件中所有语言资源到Properties中
	 * @param language   指定语言代码，以-间隔
	 * @return Properties
	 */
	Properties getConfigurationMessageProperties(String language);


	/**
	 * 基于指定shopId及指定语言，获取语言包中资源
	 *
	 * @param shopId		shopId，允许为null
	 * @param language  指定语言代码，以-间隔
	 * @param code      语言资源名称
	 * @param args      传递给语言资源的参数数组，0--{0} 1--{1}
	 * @return String
	 */
	String getConfigurationMessage(Long shopId, String language, String code, Object[] args);

	/**
	 * 基于当前shopId获取语言包中资源
	 *
	 * @param code 语言资源名称
	 * @param args 传递给语言资源的参数数组，0--{0} 1--{1}
	 * @return String
	 */
	String getConfigurationMessage(String code, Object[] args);


}
