package com.ihltx.utility.i18n.service.impl;

import com.ihltx.utility.i18n.config.I18nConfig;
import com.ihltx.utility.i18n.entity.Language;
import com.ihltx.utility.i18n.service.I18nService;
import com.ihltx.utility.i18n.service.I18nUtil;
import com.ihltx.utility.util.FileUtil;
import com.ihltx.utility.util.StringUtil;
import com.ihltx.utility.util.WebUtil;
import com.alibaba.fastjson.JSON;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("all")
@Service
public class I18nUtilImpl implements I18nUtil {
	@Autowired
	private I18nConfig i18nConfig;

	@Autowired(required = false)
	private I18nService i18nService;

	public I18nConfig getI18nConfig() {
		return i18nConfig;
	}

	public void setI18nConfig(I18nConfig i18nConfig) {
		this.i18nConfig = i18nConfig;
	}


	/**
	 * 基于配置文件获取的语言资源缓存
	 * 全局语言shopId为-1
	 * 格式：  {shopId: {"language":Properties,...}}
	 */
	private Map<Long, Map<String,Properties>> cacheConfigurationMessageProperties = new ConcurrentHashMap<>();

	public String getRequestAndSessionAndViewLanguageName() {
		return this.i18nConfig.getRequestAndSessionAndViewLanguageName();
	}

	public String getDefaultLanguage(Long shopId){
		HttpServletRequest request = null;
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		if(servletRequestAttributes!=null){
			request = servletRequestAttributes.getRequest();
		}
		return getDefaultLanguage(shopId , request);
	}

	public String getDefaultLanguage(Long shopId , HttpServletRequest request){
		if(!this.i18nConfig.getEnableStrictMode()){
			//不启用严格模式，则当前shopId的默认语言为当前浏览器首选语言
			return WebUtil.getLanguageByAcceptLanguage(request);
		}
		List<Language> langs = this.getLanguages(shopId);
		if(langs==null || langs.isEmpty()) {
			return  null;
		}
		for(Language lang : langs){
			if(lang.getLangIsDefault()){
				return lang.getLangCode();
			}
		}
		return  null;
	}

	public String getDefaultLanguage(HttpServletRequest request){
		return  getDefaultLanguage(WebUtil.getShopId(request) , request);
	}


	public String getDefaultLanguage(){
		HttpServletRequest request = null;
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		if(servletRequestAttributes!=null){
			request = servletRequestAttributes.getRequest();
		}
		return  getDefaultLanguage(WebUtil.getShopId(request) , request);
	}


	public String getLanguage(HttpServletRequest request){
		String language = null;
		if(request!=null){
			Object obj = request.getSession().getAttribute(getRequestAndSessionAndViewLanguageName());
			//Object obj = request.getAttribute(getRequestAndSessionAndViewLanguageName());
			if(obj==null) {
				language = WebUtil.getLanguageByAcceptLanguage(request);
				if(!Strings.isEmpty(language)) {
					request.getSession().setAttribute(getRequestAndSessionAndViewLanguageName(), language);
					//request.setAttribute(getRequestAndSessionAndViewLanguageName(), language);
				}
			}else {
				language = obj.toString();
			}
			if(!Strings.isEmpty(language)) {
				return language;
			}
		}else {
			Locale locale  = LocaleContextHolder.getLocale();
			if(locale!=null) {
				if(Strings.isEmpty(locale.getCountry())) {
					return locale.getLanguage();
				}else {
					return locale.getLanguage() + "-" + locale.getCountry().toUpperCase();
				}
			}
		}
		return getDefaultLanguage(request);
	}


	public String getLanguage(){
		HttpServletRequest request = null;
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		if(servletRequestAttributes!=null){
			request = servletRequestAttributes.getRequest();
		}
		return getLanguage(request);
	}

	public Boolean setLanguage(HttpServletRequest request , String language){
		if(request!=null)
		{
			request.getSession().setAttribute(this.getRequestAndSessionAndViewLanguageName(), language);
			//request.setAttribute(this.getRequestAndSessionAndViewLanguageName(), language);
		}
		String[] languages = language.split("-");
		Locale locale = null;
		if(languages.length > 1) {
			locale = new Locale(languages[0],languages[1].toUpperCase());
		}else {
			locale = new Locale(language);
		}
		LocaleContextHolder.setLocale(locale);
		return true;
	}

	public Boolean setLanguage(String language){
		HttpServletRequest request = null;
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		if(servletRequestAttributes!=null) {
			request = servletRequestAttributes.getRequest();
		}
		return setLanguage(request , language);
	}

	public Language getCurrLanguage(Long shopId , HttpServletRequest request){
		String language = getLanguage(request);
		Language currLanguageModel = null;
		if(!this.i18nConfig.getEnableStrictMode()){
			//不启用严格模式，则直接封装当前语言代码Language对象并返回
			if(!StringUtil.isNullOrEmpty(language)){
				currLanguageModel = new Language();
				currLanguageModel.setShopId(shopId);
				currLanguageModel.setLangCode(language);
				currLanguageModel.setLangCssClass(language);
				currLanguageModel.setLangName(language);
				currLanguageModel.setLangRemark(language);
			}
			return currLanguageModel;
		}
		if(shopId==null){
			shopId = -1L;
		}
		List<Language> langs = this.getLanguages(shopId);
		if(langs==null) {
			return null;
		}
		for(Language lang : langs) {
			if(lang.getLangCode().equalsIgnoreCase(language)) {
				currLanguageModel = lang;
				break;
			}
		}
		if(currLanguageModel==null) {
			for(Language lang : langs) {
				if(lang.getLangCode().equalsIgnoreCase(this.getDefaultLanguage())) {
					language = this.getDefaultLanguage();
					currLanguageModel =  lang;
					break;
				}
			}
		}
		setLanguage(request, language);
		return currLanguageModel;
	}

	public Language getCurrLanguage(HttpServletRequest request){
		return getCurrLanguage(WebUtil.getShopId(request) , request);
	}

	public Language getCurrLanguage(Long shopId){
		HttpServletRequest request = null;
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		if(servletRequestAttributes!=null) {
			request = servletRequestAttributes.getRequest();
		}
		return getCurrLanguage(shopId , request);
	}

	public Language getCurrLanguage(){
		HttpServletRequest request = null;
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		if(servletRequestAttributes!=null) {
			request = servletRequestAttributes.getRequest();
		}
		return getCurrLanguage(WebUtil.getShopId(request) , request);
	}

	public void clearCache(){
		clearCache(WebUtil.getShopId());
	}

	public void clearCache(Long shopId){
		if(this.i18nConfig.getEnableCache()){
			if(this.i18nConfig.getEnableDataSource() && this.i18nService!=null){
				this.i18nService.clearCache(shopId);
			}
			this.cacheConfigurationMessageProperties.remove(-1L);
			if(shopId!=-1L){
				this.cacheConfigurationMessageProperties.remove(shopId);
			}
		}
	}



	public List<Language> getLanguages(Long shopId){
		List<Language> rs =null;
		if(shopId==null){
			shopId = -1L;
		}
		if(!this.i18nConfig.getEnableStrictMode()){
			//不启用严格模式，则当前shopId对应的语言列表为null
			return null;
		}
		if(this.i18nConfig.getEnableDataSource() && this.i18nService!=null) {
			rs = this.i18nService.getLanguages(shopId);
		}else{
			if(shopId==-1L){
				//全局语言配置
				rs = this.i18nConfig.getLanguages();
			}else{
				if(this.i18nConfig.getShopLanguages()!=null && !this.i18nConfig.getShopLanguages().isEmpty() && this.i18nConfig.getShopLanguages().containsKey(shopId) && this.i18nConfig.getShopLanguages().get(shopId)!=null){
					rs = this.i18nConfig.getShopLanguages().get(shopId);
				}else{
					rs = this.i18nConfig.getLanguages();
				}
			}
		}
		return rs;
	}

	public List<Language> getLanguages(){
		return getLanguages(WebUtil.getShopId());
	}


	public Boolean existLanguage(Long shopId, String language){
		if(!this.i18nConfig.getEnableStrictMode()){
			//不启用严格模式，当前shopId一定存在language语言
			if(!StringUtil.isNullOrEmpty(language)){
				return true;
			}else{
				return  false;
			}
		}
		if(language==null) return false;
		if(language.equals(getDefaultLanguage(shopId))){
			return  true;
		}
		List<Language> langs =  getLanguages(shopId);
		if(langs!=null){
			for(Language lang : langs){
				if(lang.getLangCode().equals(language)){
					return  true;
				}
			}
		}
		return false;
	}

	public Boolean existLanguage(String language){
		return existLanguage(WebUtil.getShopId() , language);
	}


	public String getMessage(Long shopId , String language , String code, Object[] args){
		String msg = null;
		if(shopId == null){
			shopId = -1L;
		}
		if(!this.existLanguage(shopId , language)){
			language = getDefaultLanguage(shopId);
		}
		if(this.i18nConfig.getEnableDataSource() && i18nService!=null) {
			msg= i18nService.getMessage(shopId, language, code, args);
			if(!Strings.isEmpty(msg)) {
				return msg;
			}
		}
		msg = this.getConfigurationMessage(shopId , language , code, args);
		if(!Strings.isEmpty(msg)) {
			return msg;
		}
		return code;
	}

	public String getMessage(String language , String code, Object[] args){
		return getMessage(WebUtil.getShopId() , language , code, args);
	}

	public String getMessage(Long shopId , String code, Object[] args){
		return getMessage(shopId , getLanguage() , code, args);
	}

	public String getMessage(String code, Object[] args){
		return getMessage(WebUtil.getShopId() , getLanguage() , code, args);
	}

	public String getMessage(Long shopId , String language , String code){
		return getMessage(shopId , language , code, null);
	}

	public String getMessage(String language , String code){
		return getMessage(WebUtil.getShopId() , language , code, null);
	}

	public String getMessage(Long shopId , String code){
		return getMessage(shopId , getLanguage() , code, null);
	}

	public String getMessage(String code){
		return getMessage(WebUtil.getShopId() , getLanguage() , code, null);
	}


	public String getMessages(Long shopId , String language){
		if(shopId==null){
			shopId = -1L;
		}
		if(!this.existLanguage(shopId , language)){
			language = getDefaultLanguage();
		}
		Properties properties = getConfigurationMessageProperties(shopId , language);
		if(this.i18nConfig.getEnableDataSource() && this.i18nService!=null) {
			Map<String,String> applicationMessages = this.i18nService.getMessages(shopId , language);
			if(applicationMessages!=null) {
				for(String key : applicationMessages.keySet()) {
					properties.put(key, applicationMessages.get(key));
				}
			}
		}
		return JSON.toJSONString(properties);
	}

	public String getMessages(String language){
		return getMessages(WebUtil.getShopId() , language);
	}

	public String getMessages(Long shopId){
		return getMessages(shopId , getLanguage());
	}


	public String getMessages(){
		return getMessages(WebUtil.getShopId() , getLanguage());
	}

	public Properties getMessageProperties(Long shopId , String language){
		if(shopId==null) {
			shopId=-1L;
		}
		if(!this.existLanguage(shopId , language)){
			language = getDefaultLanguage(shopId);
		}
		if(language == null) return  null;
		Properties properties = getConfigurationMessageProperties(shopId , language);
		if(this.i18nConfig.getEnableDataSource() && this.i18nService!=null) {

			Map<String,String> applicationMessages = this.i18nService.getMessages(shopId , language);
			if(applicationMessages!=null) {
				for(String key : applicationMessages.keySet()) {
					properties.put(key, applicationMessages.get(key));
				}
			}
		}
		return properties;
	}

	public Properties getMessageProperties(String language){
		return getMessageProperties(WebUtil.getShopId() , language);
	}

	public Properties getMessageProperties(Long shopId){
		return getMessageProperties(shopId , getLanguage());
	}

	public Properties getMessageProperties(){
		return getMessageProperties(WebUtil.getShopId() , getLanguage());
	}


	/**
	 * 根据指定shopId及语言代码从语言资源文件中获取所有语言资源到Properties并返回
	 * 		1、如果shopId为null或不存在将使用全局语言资源文件(例：0/messages_zh_CN.properties不存在，将使用messages_zh_CN.properties)
	 * 		2、如果language为null或不存在将使用全局默认资源文件(如：messages.properties)
	 *
	 * @param shopId		shopId，允许为null--全局语言配置  -1--全局语言配置
	 * @param language		语言代码，以-间隔，允许为null
	 * @return	Properties
	 * 		null --  failure
	 */
	public Properties getConfigurationMessageProperties(Long shopId, String language) {
		String globalBasePath = null;
		String shopBasePath = null;
		String basePath = null;
		if(shopId ==null){
			shopId = -1L;
		}
		if(!this.existLanguage(shopId , language)){
			language = getDefaultLanguage(shopId);
		}
		String keyLanguage = (language==null?"":language);
		if(this.i18nConfig.getEnableCache()){
			if(this.cacheConfigurationMessageProperties.containsKey(shopId)
					&& this.cacheConfigurationMessageProperties.get(shopId)!=null
					&& this.cacheConfigurationMessageProperties.get(shopId).containsKey(keyLanguage)
					&& this.cacheConfigurationMessageProperties.get(shopId).get(keyLanguage)!=null){
				return this.cacheConfigurationMessageProperties.get(shopId).get(keyLanguage);
			}
		}
		basePath = i18nConfig.getBasePath();

		if(basePath.contains("%SHOP_ID%")){
			//basePath中包含%SHOP_ID%
			globalBasePath = basePath.replaceAll("%SHOP_ID%","");
			if(shopId==-1L){
				shopBasePath = globalBasePath;
			}else{
				shopBasePath  = basePath.replaceAll("%SHOP_ID%",shopId.toString()).replaceAll("\\/\\/" , "/");
			}
		}else{
			//basePath中不包含%SHOP_ID%
			shopBasePath = globalBasePath = basePath;
		}
		globalBasePath = StringUtil.rtrim(globalBasePath.replaceAll("\\/\\/","/"),"/");
		shopBasePath = StringUtil.rtrim(shopBasePath.replaceAll("\\/\\/","/"),"/");

		if(StringUtil.isNullOrEmpty(i18nConfig.getBaseName())){
			return null;
		}

		String languageFileName = null;
		if(StringUtil.isNullOrEmpty(language)){
			languageFileName = i18nConfig.getBaseName() + ".properties";
		}else{
			languageFileName = i18nConfig.getBaseName() + "_" + language.replaceAll("-", "_") + ".properties";
		}

		String fileName = globalBasePath + "/" + languageFileName;
		InputStream in = null;
		//1、首先获取全局资源
		Properties globalProperties = new Properties();
		try{
			if(fileName.contains("classpath:")) {
				//全局资源文件名中包含classpath:，则在类路径下
				fileName =  fileName.replaceAll("classpath:[ ]*" , "");
				in = this.getClass().getResourceAsStream(fileName);
			}else{
				//全局资源文件名中不包含classpath:，则为确定路径
				if(FileUtil.exists(fileName)){
					in = new FileInputStream(fileName);
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(in!=null){
					globalProperties.load(in);
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		//2、获取shopId店铺资源
		Properties shopProperties = new Properties();
		if(!shopBasePath.equals(globalBasePath)){
			//如果店铺语言资源路径与全局语言资源路径不等，则获取店铺语言资源
			fileName = shopBasePath + "/" + languageFileName;
			try{
				if(fileName.contains("classpath:")) {
					//店铺资源文件名中包含classpath:，则在类路径下
					fileName =  fileName.replaceAll("classpath:[ ]*" , "");
					in = this.getClass().getResourceAsStream(fileName);
				}else{
					//店铺资源文件名中不包含classpath:，则为确定路径
					if(FileUtil.exists(fileName)){
						in = new FileInputStream(fileName);
					}
				}
			}catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					if(in!=null){
						shopProperties.load(in);
						in.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		Properties properties = new Properties();
		if(globalProperties!=null && !globalProperties.isEmpty()){
			properties.putAll(globalProperties);
		}
		if(shopProperties!=null && !shopProperties.isEmpty()){
			properties.putAll(shopProperties);
		}
		Map<String , Properties> langaugeMap = null;
		if(this.i18nConfig.getEnableCache() && properties!=null && !properties.isEmpty()){
			if(!this.cacheConfigurationMessageProperties.containsKey(shopId) || this.cacheConfigurationMessageProperties.get(shopId)==null){
				langaugeMap = new ConcurrentHashMap<>();
				langaugeMap.put(keyLanguage , properties);
				this.cacheConfigurationMessageProperties.put(shopId,langaugeMap);
			}else{
				langaugeMap = this.cacheConfigurationMessageProperties.get(shopId);
				langaugeMap.put(keyLanguage , properties);
				this.cacheConfigurationMessageProperties.put(shopId,langaugeMap);
			}
		}

		return properties.isEmpty()?null:properties;
	}


	/**
	 * 基于当前shopId及指定语言获取指定语言资源文件中所有语言资源到Properties中
	 * @param language   指定语言代码，以-间隔
	 * @return Properties
	 */
	public Properties getConfigurationMessageProperties(String language) {
		return getConfigurationMessageProperties(WebUtil.getShopId(),language);
	}


	/**
	 * 基于指定shopId及指定语言，获取语言包中资源
	 *
	 * @param shopId		shopId，允许为null
	 * @param language  指定语言代码，以-间隔
	 * @param code      语言资源名称
	 * @param args      传递给语言资源的参数数组，0--{0} 1--{1}
	 * @return String
	 */
	public String getConfigurationMessage(Long shopId , String language, String code, Object[] args) {
		Properties properties = getConfigurationMessageProperties(shopId , language);
		if(properties==null) return null;
		String message = properties.getProperty(code);
		if(!Strings.isEmpty(message)) {
			return StringUtil.formatString(message, args);
		}
		return null;
	}

	/**
	 * 基于当前shopId获取语言包中资源
	 *
	 * @param code 语言资源名称
	 * @param args 传递给语言资源的参数数组，0--{0} 1--{1}
	 * @return String
	 */
	public String getConfigurationMessage(String code, Object[] args) {
		return getConfigurationMessage(WebUtil.getShopId() , getLanguage() , code, args);
	}


}
