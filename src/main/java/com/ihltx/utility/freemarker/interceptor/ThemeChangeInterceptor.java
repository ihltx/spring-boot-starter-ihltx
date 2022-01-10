package com.ihltx.utility.freemarker.interceptor;

import com.ihltx.utility.util.StringUtil;
import com.ihltx.utility.util.WebUtil;
import com.ihltx.utility.freemarker.service.FreemarkerUtil;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ThemeChangeInterceptor implements HandlerInterceptor {

	private FreemarkerUtil freemarkerUtil;

	public FreemarkerUtil getFreemarkerUtil() {
		return freemarkerUtil;
	}

	public void setFreemarkerUtil(FreemarkerUtil freemarkerUtil) {
		this.freemarkerUtil = freemarkerUtil;
	}


	/**
     * 在请求处理之前进行调用（Controller方法调用之前）
     */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if(freemarkerUtil!=null){
			String value = request.getParameter(freemarkerUtil.getFreemarkerConfig().getRequestAndSessionAndViewThemeName());
			if(Strings.isEmpty(value)) {
				value = request.getHeader(freemarkerUtil.getFreemarkerConfig().getRequestAndSessionAndViewThemeName());
			}
			if(!StringUtil.isNullOrEmpty(value)){
				WebUtil.setTheme(request , value);
			}else{
				value = WebUtil.getThemeOrNull(request);
				if(StringUtil.isNullOrEmpty(value)){
					Long shopId = WebUtil.getShopId(request);
					String theme = freemarkerUtil.getTheme(shopId);
					if(theme!=null){
						value = theme;
					}else{
						value = freemarkerUtil.getFreemarkerConfig().getDefaultTheme();
					}
					WebUtil.setTheme(request , value);
				}
			}
		}
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}

	/**
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}

	/**
     * 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
     */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}


	

	
}
