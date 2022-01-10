package com.ihltx.utility.freemarker.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.ihltx.utility.i18n.service.I18nUtil;
import com.ihltx.utility.util.FileUtil;
import com.ihltx.utility.util.StringUtil;
import com.ihltx.utility.util.WebUtil;
import com.ihltx.utility.freemarker.config.CustomizeTag;
import com.ihltx.utility.freemarker.config.FreemarkerConfig;
import com.ihltx.utility.freemarker.entity.CdnUrl;
import com.ihltx.utility.freemarker.entity.Theme;
import com.ihltx.utility.freemarker.exceptions.ViewNotFoundException;
import com.ihltx.utility.freemarker.service.CdnUrlService;
import com.ihltx.utility.freemarker.service.FreemarkerUtil;
import com.ihltx.utility.freemarker.service.ThemeService;
import freemarker.template.TemplateModelException;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;


import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreemarkerUtilImpl implements FreemarkerUtil {

	/**
	 * 格式：  {shopId: {"default": Configuration, "blue": Configuration, ...,},...,}
	 */
	private static Map<Long, Map<String,Configuration>> container = new ConcurrentHashMap<>();


	private ApplicationContext applicationContext;
	public ApplicationContext getApplicationContext(){
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext){
		this.applicationContext = applicationContext;
	}


	private I18nUtil i18nUtil;
	public I18nUtil getI18nUtil(){
		return i18nUtil;
	}
	public void setI18nUtil(I18nUtil i18nUtil) {
		this.i18nUtil = i18nUtil;
	}

	private FreemarkerConfig freemarkerConfig;
	public FreemarkerConfig getFreemarkerConfig() {
		return freemarkerConfig;
	}
	public void setFreemarkerConfig(FreemarkerConfig freemarkerConfig) {
		this.freemarkerConfig = freemarkerConfig;
	}


	private ThemeService themeService;
	public ThemeService getThemeService() {
		return themeService;
	}
	public void setThemeService(ThemeService themeService) {
		this.themeService = themeService;
	}


	private CdnUrlService cdnUrlService;
	public CdnUrlService getCdnUrlService() {
		return cdnUrlService;
	}
	public void setCdnUrlService(CdnUrlService cdnUrlService) {
		this.cdnUrlService = cdnUrlService;
	}

	private static Map<Long, String> themeCaches = new ConcurrentHashMap<>();
	private static Map<Long, String> cdnUrlCaches = new ConcurrentHashMap<>();


	public String getTheme(Long shopId){
		if(this.getFreemarkerConfig().getEnableDataSource() && this.themeService!=null){
			if(this.getFreemarkerConfig().getEnableCache()){
				if(themeCaches.containsKey(shopId) && themeCaches.get(shopId)!=null){
					return themeCaches.get(shopId);
				}
			}
			Theme theme = themeService.getTheme(shopId);
			if(theme!=null){
				if(this.getFreemarkerConfig().getEnableCache()){
					themeCaches.put(shopId , theme.getThemeName());
				}
				return theme.getThemeName();
			}
		}else{
			if(freemarkerConfig.getShopThemes()!=null && freemarkerConfig.getShopThemes().containsKey(shopId) && freemarkerConfig.getShopThemes().get(shopId) != null){
				return  freemarkerConfig.getShopThemes().get(shopId);
			}
		}
		return freemarkerConfig.getDefaultTheme();
	}

	public String getCdnUrl(Long shopId){
		String stringCdnUrl = null;
		if(this.getFreemarkerConfig().getEnableDataSource() && this.cdnUrlService!=null){
			if(this.getFreemarkerConfig().getEnableCache()){
				if(cdnUrlCaches.containsKey(shopId) && cdnUrlCaches.get(shopId)!=null){
					return cdnUrlCaches.get(shopId);
				}
			}
			CdnUrl cdnUrl = cdnUrlService.getCdnUrl(shopId);
			if(cdnUrl!=null){
				stringCdnUrl = StringUtil.isNullOrEmpty(cdnUrl.getCdnUrl())?"":cdnUrl.getCdnUrl().trim();
				if(this.getFreemarkerConfig().getEnableCache()){
					cdnUrlCaches.put(shopId , stringCdnUrl);
				}
				return stringCdnUrl;
			}
		}else{
			if(freemarkerConfig.getShopCdnUrls()!=null && freemarkerConfig.getShopCdnUrls().containsKey(shopId) && freemarkerConfig.getShopCdnUrls().get(shopId) != null){
				return  freemarkerConfig.getShopCdnUrls().get(shopId).trim();
			}
		}
		return StringUtil.isNullOrEmpty(freemarkerConfig.getDefaultCdnUrl())?"":freemarkerConfig.getDefaultCdnUrl().trim();
	}

	public static Map<Long, Map<String,Configuration>> getContainer() {
		return container;
	}

	/**
	 * 获取主题
	 * @return String 
	 */
	public String getTheme() {
		String theme = WebUtil.getTheme();
		if(StringUtils.isEmpty(theme)) {
			theme = freemarkerConfig.getDefaultTheme();
		}
		return theme;
	}

	

	/**
	 * 获取 Freemarker 模板引挚配置
	 * @return Configuration
	 * @throws IOException
	 */
	public Configuration getConfiguration() throws IOException {
		return  getConfiguration(WebUtil.getShopId() , WebUtil.getTheme());
	}

	public Configuration getConfiguration(Long shopId, String theme) throws IOException {
		if(StringUtils.isEmpty(theme)) {
			theme = freemarkerConfig.getDefaultTheme();
		}
		if(container.containsKey(shopId) && container.get(shopId)!=null && container.get(shopId).containsKey(theme) && container.get(shopId).get(theme)!=null){
			return container.get(shopId).get(theme);
		}
		Configuration cfg = build(shopId, theme);
		if(container.containsKey(shopId) && container.get(shopId)!=null){
			container.get(shopId).put(theme , cfg);
		}else{
			Map<String , Configuration> map =new ConcurrentHashMap<>();
			map.put(theme, cfg);
			container.put(shopId , map);
		}
		return cfg;
	}

	/**
	 * 初始化视图中自定义标签
	 * @param configuration
	 * @param shopId
	 * @param theme
	 */
	private void initCustomizeTags(Configuration configuration, Long shopId, String theme){
		Boolean isAutowired = false;
		try {
			Map<String, CustomizeTag> customizeTags = this.freemarkerConfig.getCustomizeTags();
			if(customizeTags!=null && !customizeTags.isEmpty()){
				for(String name : customizeTags.keySet()){
					CustomizeTag customizeTag = customizeTags.get(name);
					if(customizeTag.getClazz()!=null){
						isAutowired = false;
						Object tag = null;
						if(applicationContext!=null){
							try{
								tag = applicationContext.getBean(Class.forName(customizeTag.getClazz()));
								if(tag!=null){
									isAutowired =true;
								}
							}catch (Exception e){
							}
						}
						if(!isAutowired){
							try {
								tag = Class.forName(customizeTag.getClazz()).getConstructor(null).newInstance(null);
							} catch (ClassNotFoundException classNotFoundException) {
								classNotFoundException.printStackTrace();
							} catch (NoSuchMethodException noSuchMethodException) {
								noSuchMethodException.printStackTrace();
							} catch (IllegalAccessException illegalAccessException) {
								illegalAccessException.printStackTrace();
							} catch (InstantiationException instantiationException) {
								instantiationException.printStackTrace();
							} catch (InvocationTargetException invocationTargetException) {
								invocationTargetException.printStackTrace();
							}
							if(customizeTag.getAutowireds()!=null && !customizeTag.getAutowireds().isEmpty()){
								for(String key : customizeTag.getAutowireds().keySet()){
									String methodName = "set" + StringUtil.captureFirstName(key);
									try {
										Method method = tag.getClass().getMethod(methodName , Class.forName(customizeTag.getAutowireds().get(key)));
										if(method!=null){
											Object value = null;
											if(applicationContext!=null){
												try{
													value = applicationContext.getBean(Class.forName(customizeTag.getAutowireds().get(key)));
												}catch (Exception e){
												}
											}
											if(value!=null){
												method.invoke(tag , value);
											}
										}
									} catch (ClassNotFoundException e) {
										e.printStackTrace();
									} catch (NoSuchMethodException e) {
										e.printStackTrace();
									} catch (IllegalAccessException e) {
										e.printStackTrace();
									} catch (InvocationTargetException e) {
										e.printStackTrace();
									}
								}
							}
						}
						if(tag!=null){
							configuration.setSharedVariable(name , tag);
						}
					}
				}
			}
		} catch (TemplateModelException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据theme主题 构造  Freemarker 模板引挚配置
	 * @param theme  主题
	 * @return  Configuration
	 * @throws IOException
	 */
	private Configuration build(String theme) throws IOException {
		return build( WebUtil.getShopId() , theme);
	}

	/**
	 * 根据指定shopId及theme主题 构造  Freemarker 模板引挚配置
	 * @param shopId  shopId
	 * @param theme  主题
	 * @return  Configuration
	 * @throws IOException
	 */
	private Configuration build(Long shopId, String theme) throws IOException {
		Configuration freemarkerCfg = new Configuration(Configuration.VERSION_2_3_30);
		freemarkerCfg.setDefaultEncoding(freemarkerConfig.getCharset());
		freemarkerCfg.setTagSyntax(freemarkerConfig.getTagsynta());
		String templatePaths = freemarkerConfig.getTemplatePaths().replaceAll(" ", "").replaceAll("\\\\" , "/");
		Boolean isFindClassPathTemplates = false;
		String[] paths = templatePaths.split(",");
		for (int i=0;i< paths.length ; i++){
			paths[i] = StringUtil.rtrim(paths[i].replaceAll("\\\\" , "/") , "/") + "/";
			if(paths[i].equalsIgnoreCase("classpath:/templates/")) {
				isFindClassPathTemplates = true;
			}
		}

		List<TemplateLoader> loaders = new ArrayList<>();

		if(shopId==-1) { //暂未使用如下代码，因此设置为条件为-1
			//0店铺
			if(isFindClassPathTemplates) {
				//如果在视图模板路径配置中有包含类路径配置，则0店铺使用类路径下全局视图
				TemplateLoader defaultftl=new ClassTemplateLoader(FreemarkerUtilImpl.class,"/templates/" + freemarkerConfig.getViewName() + "/" + theme + "/");
				loaders.add(defaultftl);
			}else {
				//否则使用0店铺私有路径下视图
				for(int i=0;i<paths.length;i++) {
					String path = paths[i];
					if(!path.equalsIgnoreCase("classpath:/templates/")) {
						if(!StringUtils.isEmpty(path)) {
							File file =new File(path + shopId + "/" + freemarkerConfig.getViewName() + "/" + theme + "/");
							if(file.isDirectory() && file.exists()) {
								FileTemplateLoader ftl = new FileTemplateLoader(file);
								loaders.add(ftl);
							}
						}
					}
				}
			}
		}else {
			//0店铺及非0店铺均使用如下业务逻辑
			//优先使用私有路径下视图
			for(int i=0;i<paths.length;i++) {
				String path = paths[i];
				if(!path.equalsIgnoreCase("classpath:/templates/")) {
					if(!StringUtils.isEmpty(path)) {
						File file =new File(path + shopId + "/" + freemarkerConfig.getViewName() + "/" + theme + "/");
						if(file.isDirectory() && file.exists()) {
							FileTemplateLoader ftl = new FileTemplateLoader(file);
							loaders.add(ftl);
						}
					}
				}
			}

			//如果在视图模板路径配置中有包含类路径配置，则最后使用类路径下全局视图
			if(isFindClassPathTemplates) {
				String themePath  = "templates/" + freemarkerConfig.getViewName() + "/" + theme + "/";
				URL url = this.getClass().getResource("/" + themePath);
				if(url==null && loaders.size()<=0){
					throw new IOException(" Theme " + theme + " [classpath:/" + themePath + "] not found.");
				}
				TemplateLoader defaultftl=new ClassTemplateLoader(FreemarkerUtilImpl.class,"/" + themePath);
				loaders.add(defaultftl);
			}
		}

		TemplateLoader[] tls= new TemplateLoader[loaders.size()];
		loaders.toArray(tls);
		MultiTemplateLoader mtl = new MultiTemplateLoader(tls);
		freemarkerCfg.setTemplateLoader(mtl);
		freemarkerCfg.setTemplateExceptionHandler(freemarkerConfig.getTemplateExceptionHandlerObject());
		initCustomizeTags(freemarkerCfg , shopId , theme);
		return freemarkerCfg;
	}


	public void clearTemplateCache() throws IOException {
		clearTemplateCache(WebUtil.getShopId() , WebUtil.getTheme());
	}

	/**
	 * 清除 Freemarker 模板引挚视图缓存
	 * @throws IOException
	 */
	public void clearTemplateCache(Long shopId, String theme) throws IOException {
		getConfiguration(shopId , theme).clearTemplateCache();
		container.remove(shopId);
		themeCaches.remove(shopId);
		cdnUrlCaches.remove(shopId);
		WebUtil.removeTheme();
	}

	/**
	 * 基于指定shopId、theme及language语言代码 初始化全局视图变量
	 * @param shopId		shopId
	 * @param theme			theme主题
	 * @param language		language语言代码以-间隔，如: zh-CN
	 * @param request		request请求对象
	 * @return
	 */
	private Map<String, Object> getGlobalViewVars(Long shopId, String theme, String language, HttpServletRequest request) {
		if(StringUtils.isEmpty(theme)) {
			theme = freemarkerConfig.getDefaultTheme();
		}
		Map<String, Object> vars = new HashMap<String, Object>();
		if(freemarkerConfig.getGlobalVariables()!=null && !freemarkerConfig.getGlobalVariables().isEmpty()){
			for(String key :  freemarkerConfig.getGlobalVariables().keySet()){
				vars.put(key ,  freemarkerConfig.getGlobalVariables().get(key));
			}
		}


		vars.put(WebUtil.APP_SHOP_ID_NAME, shopId);
		vars.put(WebUtil.APP_THEME_NAME, theme);
		vars.put(WebUtil.APP_PATH, freemarkerConfig.getAppPath());
		vars.put(WebUtil.APP_STATIC_CDN_URL_PREFIX , getCdnUrl(shopId));

		vars.put(WebUtil.APP_GLOBAL_THEME_PATH, "/" + freemarkerConfig.getViewName() + "/" + theme + "/");

		if(i18nUtil!=null){
			vars.put(WebUtil.APP_LANGUAGES, i18nUtil.getLanguages(shopId));
			vars.put(i18nUtil.getRequestAndSessionAndViewLanguageName(), language);
			String allLangs = i18nUtil.getMessages(shopId , language);
			vars.put(WebUtil.APP_All_Langs, StringUtil.isNullOrEmpty(allLangs)?"{}":allLangs);
		}else{
			vars.put(WebUtil.APP_LANGUAGES, null);
			vars.put(i18nUtil.getRequestAndSessionAndViewLanguageName(), null);
			vars.put(WebUtil.APP_All_Langs, "{}");
		}
		vars.put(WebUtil.APP_THEME_PATH, "/" + shopId + "/" + freemarkerConfig.getViewName() + "/" + theme + "/");

		if(request!=null){

			vars.put("request",request);
			vars.put("session",request.getSession());
			vars.put("requestContextPath",request.getContextPath());
			vars.put("requestUri",request.getRequestURI()+(StringUtil.isNullOrEmpty(request.getQueryString())?"":("?"+request.getQueryString())));
			vars.put("queryString",request.getQueryString());

			Map<String , Object> requestParams = new HashMap<>();
			Enumeration<String> eParameters = request.getParameterNames();
			while (eParameters.hasMoreElements()){
				String name = eParameters.nextElement();
				requestParams.put(name,request.getParameter(name));
			}
			vars.put("requestParams",requestParams);

			Map<String , Object> requestScope = new HashMap<>();
			Enumeration<String> eAttributes = request.getAttributeNames();
			while (eAttributes.hasMoreElements()){
				String name = eAttributes.nextElement();
				requestScope.put(name,request.getAttribute(name));
			}
			vars.put("requestScope",requestScope);

			Map<String , Object> sessionScope = new HashMap<>();
			if(request.getSession()!= null){
				eAttributes = request.getSession().getAttributeNames();
				while (eAttributes.hasMoreElements()){
					String name = eAttributes.nextElement();
					sessionScope.put(name,request.getSession().getAttribute(name));
				}
			}
			vars.put("sessionScope",sessionScope);
			Map<String , String> cookies = new HashMap<>();
			if(request.getCookies()!= null){
				for(Cookie cookie : request.getCookies()){
					cookies.put(cookie.getName(),cookie.getValue());
				}
			}
			vars.put("cookie",cookies);

		}
		return vars;
	}

	private void initGlobalViewVars(ModelAndView data , Long shopId, String theme, String language, HttpServletRequest request) {
		if(data==null){
			data = new ModelAndView();
		}
		Map<String, Object> vars = getGlobalViewVars(shopId , theme , language , request);
		for(Map.Entry<String, Object> var : vars.entrySet()) {
			data.addObject(var.getKey(), var.getValue());		
		}
		
		
	}

	private void initGlobalViewVars(Map<String, Object> data , Long shopId, String theme, String language, HttpServletRequest request) {
		if(data==null){
			data = new HashMap<>();
		}
		Map<String, Object> vars = getGlobalViewVars(shopId , theme , language , request);
		for(Map.Entry<String, Object> var : vars.entrySet()) {
			data.put(var.getKey(), var.getValue());	
		}	
	}

	/**
	 * 渲染  Freemarker 模板引挚视图
	 * @param data  ModelAndView 模型视图及视图，必须指定视图名称
	 * @return 渲染之后的字符串
	 * @throws IOException
	 */
	public String renderTemplate(ModelAndView data) throws IOException, ViewNotFoundException {
		return renderTemplate(data,data.getViewName());
	}


	/**
	 * 渲染  Freemarker 模板引挚视图
	 * @param data  ModelAndView 模型视图及视图，不需要设置视图名称
	 * @param tplName  指定需要渲染的视图名称
	 * @return 渲染之后的字符串
	 * @throws IOException
	 */
	public String renderTemplate(ModelAndView data, String tplName) throws IOException, ViewNotFoundException {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpServletRequest request = servletRequestAttributes.getRequest();
		String language = null ;
		if(i18nUtil!=null){
			language = i18nUtil.getLanguage();
		}
		return renderTemplate(data , tplName, WebUtil.getShopId(), WebUtil.getTheme() , language , request);
	}

	/**
	 * 渲染  Freemarker 模板引挚视图
	 * @param data  Map<String, Object> Map类型模型数据
	 * @param tplName  指定需要渲染的视图名称
	 * @return 渲染之后的字符串
	 * @throws IOException
	 */
	public String renderTemplate(Map<String, Object> data, String tplName) throws IOException, ViewNotFoundException {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpServletRequest request = servletRequestAttributes.getRequest();
		String language = null ;
		if(i18nUtil!=null){
			language = i18nUtil.getLanguage();
		}
		return renderTemplate(data , tplName, WebUtil.getShopId(), WebUtil.getTheme() , language , request);
	}


	public String renderTemplate(ModelAndView data, String tplName, Long shopId , String theme,String language, HttpServletRequest request) throws IOException, ViewNotFoundException {
		String result = null;
		Long oldShopId = 0L;
		String oldLanguage = null;
		String oldtheme = null;
		if(request!=null){
			//保存旧的shopId，Language, Theme
			oldShopId = WebUtil.getShopId(request);
			if(i18nUtil!=null){
				oldLanguage = i18nUtil.getLanguage(request);
			}
			oldtheme = WebUtil.getTheme(request);
			//设置新的shopId，Language, Theme
			WebUtil.setShopId(request , shopId);
			if(i18nUtil!=null){
				i18nUtil.setLanguage(request, language);
			}
			WebUtil.setTheme(request , theme);
		}
		Configuration freemarkerCfg = getConfiguration(shopId, theme);
		if(freemarkerCfg!=null) {
			initGlobalViewVars(data, shopId , theme , language , request);
			try {
				Template template = freemarkerCfg.getTemplate(tplName + freemarkerConfig.getTemplateExt());
				StringWriter out = new StringWriter();
				template.process(data, out);
				out.flush();
				result = out.toString();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(request!=null){
			//恢复旧的shopId，Language, Theme
			WebUtil.setShopId(request , oldShopId);
			if(i18nUtil!=null){
				i18nUtil.setLanguage(request , oldLanguage);
			}
			WebUtil.setTheme(request , oldtheme);
		}
		return result;
	}




	public String renderTemplate(Map<String, Object> data, String tplName, Long shopId , String theme,String language, HttpServletRequest request) throws IOException, ViewNotFoundException {
		String result = null;
		Long oldShopId = 0L;
		String oldLanguage = null;
		String oldtheme = null;
		if(request!=null){
			//保存旧的shopId，Language, Theme
			oldShopId = WebUtil.getShopId(request);
			if(i18nUtil!=null){
				oldLanguage = i18nUtil.getLanguage(request);
			}
			oldtheme = WebUtil.getTheme(request);
			//设置新的shopId，Language, Theme
			WebUtil.setShopId(request , shopId);
			if(i18nUtil!=null){
				i18nUtil.setLanguage(request, language);
			}
			WebUtil.setTheme(request , theme);
		}
		Configuration freemarkerCfg = getConfiguration(shopId, theme);
		if(freemarkerCfg!=null) {
			initGlobalViewVars(data, shopId , theme , language , request);
			try {
				Template template = freemarkerCfg.getTemplate(tplName + freemarkerConfig.getTemplateExt());
				StringWriter out = new StringWriter();
				template.process(data, out);
				out.flush();
				result = out.toString();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(request!=null){
			//恢复旧的shopId，Language, Theme
			WebUtil.setShopId(request , oldShopId);
			if(i18nUtil!=null){
				i18nUtil.setLanguage(request , oldLanguage);
			}
			WebUtil.setTheme(request , oldtheme);
		}
		return result;
	}

	public String makeHtml(Map<String, Object> data, String tplName, Long shopId , String theme ,String language, HttpServletRequest request, String storagePath, String filename)
	{
		String webPath = null;
		try{
			this.clearTemplateCache(shopId , theme);
			String content = this.renderTemplate(data , tplName , shopId , theme , language , request);
			if(!StringUtil.isNullOrEmpty(content)){
				webPath = "/" + StringUtil.ltrim(filename.replaceAll("%SHOP_ID%" , String.valueOf(shopId)).replaceAll("%THEME%" , theme).replaceAll("%LANGUAGE%" , language).replaceAll("\\\\","/") ,"/");
				String path  = StringUtil.rtrim(storagePath.replaceAll("\\\\" , "/") , "/") + webPath;
				String parentPath = FileUtil.getFilePath(path);
				FileUtil.makeDirs(parentPath);
				FileUtil.writeFile(path , content);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return webPath;
	}

	public String makeHtml(ModelAndView data, String tplName, Long shopId , String theme ,String language, HttpServletRequest request, String storagePath, String filename)
	{
		String webPath = null;
		try{
			this.clearTemplateCache(shopId , theme);
			String content = this.renderTemplate(data , tplName , shopId , theme , language , request);
			if(!StringUtil.isNullOrEmpty(content)){
				webPath = "/" + StringUtil.ltrim(filename.replaceAll("%SHOP_ID%" , String.valueOf(shopId)).replaceAll("%THEME%" , theme).replaceAll("%LANGUAGE%" , language).replaceAll("\\\\","/") ,"/");
				String path  = StringUtil.rtrim(storagePath.replaceAll("\\\\" , "/") , "/") + webPath;
				String parentPath = FileUtil.getFilePath(path);
				FileUtil.makeDirs(parentPath);
				FileUtil.writeFile(path , content);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return webPath;
	}

}
