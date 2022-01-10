package com.ihltx.utility.i18n.interceptor;

import com.ihltx.utility.i18n.service.I18nUtil;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LanguageChangeInterceptor implements HandlerInterceptor {

	private I18nUtil i18nUtil;

	public I18nUtil getI18nUtil() {
		return i18nUtil;
	}

	public void setI18nUtil(I18nUtil i18nUtil) {
		this.i18nUtil = i18nUtil;
	}


	
	/**
     * 在请求处理之前进行调用（Controller方法调用之前）
     */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if(i18nUtil!=null){
			String value = request.getParameter(i18nUtil.getRequestAndSessionAndViewLanguageName());
			if(Strings.isEmpty(value)) {
				value = request.getHeader(i18nUtil.getRequestAndSessionAndViewLanguageName());
			}
			if(!Strings.isEmpty(value)) {
				i18nUtil.setLanguage(value.toString());
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
