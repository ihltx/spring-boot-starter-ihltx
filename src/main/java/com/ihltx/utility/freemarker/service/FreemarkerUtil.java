package com.ihltx.utility.freemarker.service;

import com.ihltx.utility.i18n.service.I18nUtil;
import com.ihltx.utility.freemarker.config.FreemarkerConfig;
import com.ihltx.utility.freemarker.exceptions.ViewNotFoundException;
import freemarker.template.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

public interface FreemarkerUtil {


	/**
	 * 获取 ApplicationContext 对象
	 * @return
	 */
	ApplicationContext getApplicationContext();

	/**
	 * 设置 ApplicationContext 对象
	 * @param applicationContext
	 */
	void setApplicationContext(ApplicationContext applicationContext);


	/**
	 * 获取 I18nUtil 对象
	 * @return
	 */
	I18nUtil getI18nUtil();

	/**
	 * 设置 I18nUtil 对象
	 * @param i18nUtil
	 */
	void setI18nUtil(I18nUtil i18nUtil);

	/**
	 * 获取 FreemarkerConfig 配置
 	 * @return
	 */
	FreemarkerConfig getFreemarkerConfig();

	/**
	 * 设置 FreemarkerConfig 配置
	 * @param freemarkerConfig
	 */
	void setFreemarkerConfig(FreemarkerConfig freemarkerConfig);


	/**
	 * 获取基于数据库的ThemeService对象
	 * @return
	 */
	ThemeService getThemeService();
	/**
	 * 设置基于数据库的ThemeService对象
	 * @return
	 */
	void setThemeService(ThemeService themeService);



	/**
	 * 获取基于数据库的CdnUrlService对象
	 * @return
	 */
	CdnUrlService getCdnUrlService();
	/**
	 * 设置基于数据库的CdnUrlService对象
	 * @return
	 */
	void setCdnUrlService(CdnUrlService cdnUrlService);


	/**
	 * 基于shopId从获取shopId对应的当前默认主题
	 * 如果启用了数据库，则从数据库中获取shopId对应的当前默认主题
	 * 如果未启用数据库，则从配置中获取shopId对应的当前默认主题
	 * 如果都未获取到则使用全局默认主题
	 * @param shopId
	 * @return String
	 * 		null -- 表示shopId没有当前默认主题
	 */
	String getTheme(Long shopId);


	/**
	 * 获取当前主题
	 * @return String 
	 */
	String getTheme();


	/**
	 * 基于shopId从获取对应的当前默认cdn url
	 * 如果启用了数据库，则从数据库中获取shopId对应的当前默认cdn url
	 * 如果未启用数据库，则从配置中获取shopId对应的当前默认cdn url
	 * 如果都未获取到则使用全局默认cdn url
	 * @param shopId
	 * @return String
	 * 		null -- 表示shopId没有默认cdn url
	 */
	String getCdnUrl(Long shopId);



	/**
	 * 获取 Freemarker 模板引挚配置
	 * @return Configuration
	 * @throws IOException
	 */
	public Configuration getConfiguration() throws IOException;


	/**
	 * 基于指定shopId以及theme 获取 Freemarker 模板引挚配置
	 * @param shopId   shopId
	 * @param theme     theme主题名称
	 * @return Configuration
	 * @throws IOException
	 */
	public Configuration getConfiguration(Long shopId, String theme) throws IOException;


	/**
	 * 清除当前shopId及theme对应的 Freemarker 模板引挚视图缓存，以及cdnUrl缓存
	 * @throws IOException
	 */
	void clearTemplateCache() throws IOException;


	/**
	 * 清除指定shopId及theme对应的 Freemarker 模板引挚视图缓存，以及cdnUrl缓存
	 * @param shopId   shopId
	 * @param theme     theme主题名称
	 * @throws IOException
	 */
	void clearTemplateCache(Long shopId, String theme) throws IOException;

	/**
	 * 基于当前ShopId的当前theme以及data中保存的渲染视图名称，使用Freemarker 模板引挚渲染视图，并返回渲染之后的静态页面内容
	 * @param data  ModelAndView 模型视图及视图，必须指定视图名称（指定需要渲染的视图名称，使用/间隔的视图路径，可以/或不以/开始，均表示在当前theme主题文件夹下搜索视力视图，不需要扩展名）
	 * @return 渲染之后的字符串
	 * @throws IOException
	 */
	String renderTemplate(ModelAndView data) throws IOException, ViewNotFoundException;
	
	/**
	 * 基于当前ShopId的当前theme以及data及tplName指定的视图名称，使用Freemarker 模板引挚渲染视图，并返回渲染之后的静态页面内容
	 * @param data  ModelAndView 模型视图及视图，不需要设置视图名称
	 * @param tplName  指定需要渲染的视图名称，使用/间隔的视图路径，可以/或不以/开始，均表示在当前theme主题文件夹下搜索视力视图，不需要扩展名
	 * @return 渲染之后的字符串
	 * @throws IOException
	 */
	String renderTemplate(ModelAndView data, String tplName) throws IOException, ViewNotFoundException;

	/**
	 * 基于当前ShopId的当前theme以及data及tplName指定的视图名称，使用Freemarker 模板引挚渲染视图，并返回渲染之后的静态页面内容
	 * @param data  Map<String, Object> Map类型模型数据
	 * @param tplName  指定需要渲染的视图名称，使用/间隔的视图路径，可以/或不以/开始，均表示在当前theme主题文件夹下搜索视力视图，不需要扩展名
	 * @return 渲染之后的字符串
	 * @throws IOException
	 */
	String renderTemplate(Map<String, Object> data, String tplName) throws IOException, ViewNotFoundException;


	/**
	 * 基于指定ShopId、指定theme、data、tplName视图名称及指定的request请求对象，使用Freemarker 模板引挚渲染视图，并返回渲染之后的静态页面内容
	 * @param data  Map<String, Object> Map类型模型数据
	 * @param tplName  指定需要渲染的视图名称，使用/间隔的视图路径，可以/或不以/开始，均表示在当前theme主题文件夹下搜索视力视图，不需要扩展名
	 * @param shopId   shopId
	 * @param theme     theme主题名称
	 * @param language   语言代码，如: ch-CN
	 * @param request  request
	 * @return 渲染之后的字符串
	 * @throws IOException
	 */
	String renderTemplate(Map<String, Object> data, String tplName, Long shopId, String theme, String language, HttpServletRequest request) throws IOException, ViewNotFoundException;


	/**
	 * 基于指定ShopId、指定theme、data、tplName视图名称及指定的request请求对象，使用Freemarker 模板引挚渲染视图，并返回渲染之后的静态页面内容
	 * @param data     ModelAndView 模型视图及视图，不需要设置视图名称
	 * @param tplName  指定需要渲染的视图名称，使用/间隔的视图路径，可以/或不以/开始，均表示在当前theme主题文件夹下搜索视力视图，不需要扩展名
	 * @param shopId   shopId
	 * @param theme     theme主题名称
	 * @param language   语言代码，如: ch-CN
	 * @param request  request
	 * @return 渲染之后的字符串
	 * @throws IOException
	 */
	String renderTemplate(ModelAndView data, String tplName, Long shopId, String theme, String language, HttpServletRequest request) throws IOException, ViewNotFoundException;


	/**
	 * 基于指定ShopId、指定theme、data、tplName视图名称及指定的request请求对象，使用Freemarker 模板引挚渲染视图，并返回渲染之后的静态页面内容
	 * @param data  Map<String, Object> Map类型模型数据
	 * @param tplName  指定需要渲染的视图名称，使用/间隔的视图路径，可以/或不以/开始，均表示在当前theme主题文件夹下搜索视力视图，不需要扩展名
	 * @param shopId   shopId
	 * @param theme     theme主题名称
	 * @param language   语言代码，如: ch-CN
	 * @param request  request
	 * @param storagePath  静态页面文件存储基础路径，该路径应该配置到SpringBoot的staticLocations中
	 * @param filename     静态页面文件名，指定存储到storagePath指定的路径下面什么位置及文件名，文件名中允许使用%SHOP_ID%代表shopId，允许使用%THEME%代表主题名称，允许使用%LANGUAGE%代表语言代码
	 * @return 返回除storagePath之外的静态页面文件路径名，返回null表示生成失败
	 */
	String makeHtml(Map<String, Object> data, String tplName, Long shopId, String theme, String language, HttpServletRequest request, String storagePath, String filename);



	/**
	 * 基于指定ShopId、指定theme、data、tplName视图名称及指定的request请求对象，使用Freemarker 模板引挚渲染视图，并返回渲染之后的静态页面内容
	 * @param data  Map<String, Object> Map类型模型数据
	 * @param tplName  指定需要渲染的视图名称，使用/间隔的视图路径，可以/或不以/开始，均表示在当前theme主题文件夹下搜索视力视图，不需要扩展名
	 * @param shopId   shopId
	 * @param theme     theme主题名称
	 * @param language   语言代码，如: ch-CN
	 * @param request  request
	 * @param storagePath  静态页面文件存储基础路径，该路径应该配置到SpringBoot的staticLocations中
	 * @param filename     静态页面文件名，指定存储到storagePath指定的路径下面什么位置及文件名，文件名中允许使用%SHOP_ID%代表shopId，允许使用%THEME%代表主题名称，允许使用%LANGUAGE%代表语言代码
	 * @return 返回除storagePath之外的静态页面文件路径名，返回null表示生成失败
	 */
	String makeHtml(ModelAndView data, String tplName, Long shopId, String theme, String language, HttpServletRequest request, String storagePath, String filename);


}
