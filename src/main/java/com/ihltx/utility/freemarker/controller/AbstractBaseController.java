package com.ihltx.utility.freemarker.controller;

import com.ihltx.utility.i18n.service.I18nUtil;
import com.ihltx.utility.util.WebUtil;
import com.ihltx.utility.freemarker.exceptions.ViewNotFoundException;
import com.ihltx.utility.freemarker.service.FreemarkerUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public abstract class AbstractBaseController {


	@Autowired
	protected FreemarkerUtil freemarkerUtil;
	public FreemarkerUtil getFreemarkerUtil() {
		return freemarkerUtil;
	}

	public void setFreemarkerUtil(FreemarkerUtil freemarkerUtil) {
		this.freemarkerUtil = freemarkerUtil;
	}

	@Autowired
	protected I18nUtil i18nUtil;
	public I18nUtil getI18nUtil() {
		return i18nUtil;
	}

	public void setI18nUtil(I18nUtil i18nUtil) {
		this.i18nUtil = i18nUtil;
	}

	public Map<String,Object> ViewData=new HashMap<String, Object>();

	public void setData(String key , Object data){
		this.ViewData.put(key , data);
	}

	public Object getData(String key){
		return this.ViewData.get(key);
	}

	public <T> T getData(String key , Class<T> clazz){
		return (T)this.ViewData.get(key);
	}

	/**
	 * 基于当前ViewData的数据、当前视图名称、当前shopId、当前theme主题、当前language语言及当前request渲染视图
	 * 当前视图名称规则，从controller包开始，不包括该包到当前控制器类名不包括类名中的Controller，转为全小写构成视图基于当前主题文件夹下的视图搜寻路径，并以当前action小写方法做为视图名称，形成当前视图
	 * 		如：
	 * 		控制器:    cn.ihltx.shop.hshopsingleapp.controller.web.HomeController
	 * 	    Action方法：      index
	 * 	    则视图名为：     web/home/index
	 * @return	渲染之后的静态视图字符串
	 */
	public String display(){
		try {
			String className = Thread.currentThread().getStackTrace()[2].getClassName();//调用的类名
			String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();//调用的方法名
			String viewName = WebUtil.getViewPath(className, methodName);
			ViewData.put("APP_VIEWNAME", viewName);
			return freemarkerUtil.renderTemplate(ViewData, viewName);
		}catch (IOException err){
			err.printStackTrace();
			return err.getMessage();
		}catch (ViewNotFoundException err){
			err.printStackTrace();
			return err.getMessage();
		}
	}

	/**
	 * 基于当前ViewData的数据、指定ViewName视图名称、当前shopId、当前theme主题、当前language语言及当前request渲染视图
	 * @param ViewName		ViewName视图名称，指定需要渲染的视图名称，使用/间隔的视图路径，可以/或不以/开始，均表示在当前theme主题文件夹下搜索视力视图，不需要扩展名
	 * @return	渲染之后的静态视图字符串
	 */
	public String display(String ViewName) {
		try {
			ViewData.put("APP_VIEWNAME", ViewName);
			return freemarkerUtil.renderTemplate(ViewData, ViewName);
		}catch (IOException err){
			err.printStackTrace();
			return err.getMessage();
		}catch (ViewNotFoundException err){
			err.printStackTrace();
			return err.getMessage();
		}
	}

	/**
	 * 基于当前ViewData的数据、指定ViewName视图名称、指定shopId、指定theme主题、指定language语言及指定request渲染视图
	 * @param ViewName			ViewName视图名称
	 * @param shopId			shopId
	 * @param theme				theme主题
	 * @param language			language语言，例如: zh-CN
	 * @param request			请求对象，可基于MockHttpServletResponse或MultipartHttpServletRequest创建
	 * @return  渲染之后的静态视图字符串
	 */
	public String display(String ViewName, Long shopId , String theme, String language, HttpServletRequest request) {
		try {
			ViewData.put("APP_VIEWNAME", ViewName);
			return freemarkerUtil.renderTemplate(ViewData, ViewName, shopId, theme , language , request);
		}catch (IOException err){
			err.printStackTrace();
			return err.getMessage();
		}catch (ViewNotFoundException err){
			err.printStackTrace();
			return err.getMessage();
		}
	}

	/**
	 * 清除当前shopId语言缓存及当前theme的视图缓存
	 */
	public void clearTemplateCache() {
		try{
			i18nUtil.clearCache();
			freemarkerUtil.clearTemplateCache();
		}catch (IOException err){
			err.printStackTrace();
		}
	}

	/**
	 * 清除指定shopId语言缓存及theme的视图缓存
	 * @param shopId			shopId
	 * @param theme				theme主题
	 */
	public void clearTemplateCache(Long shopId , String theme) {
		try{
			i18nUtil.clearCache(shopId);
			freemarkerUtil.clearTemplateCache(shopId,theme);
		}catch (IOException err){
			err.printStackTrace();
		}
	}


}
