package com.ihltx.utility.interceptor.interceptor;

import com.ihltx.utility.interceptor.config.InterceptorConfig;
import com.ihltx.utility.redis.service.RedisFactory;
import com.ihltx.utility.redis.service.StringRedisUtil;
import com.ihltx.utility.util.WebUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ShopIdInterceptor implements  HandlerInterceptor {
	private InterceptorConfig interceptorConfig;
	
	private RedisFactory redisFactory;
	
	

	public RedisFactory getRedisFactory() {
		return redisFactory;
	}

	public void setRedisFactory(RedisFactory redisFactory) {
		this.redisFactory = redisFactory;
	}

	
	
	/**
     * 在请求处理之前进行调用（Controller方法调用之前）
     */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		Long shopId = WebUtil.DEFAULT_SHOP_ID;
		String host = "host:" + request.getServerName() + "_" + request.getServerPort();
		if(this.getInterceptorConfig().getShopId().getEnableReverseProxy()){
			if(request.getHeader(WebUtil.X_PROXY_HOST)!=null && request.getHeader(WebUtil.X_PROXY_SERVER_PORT)!=null){
				host = "host:" + request.getHeader(WebUtil.X_PROXY_HOST) + "_" + request.getHeader(WebUtil.X_PROXY_SERVER_PORT);
			}
		}

		if(this.interceptorConfig.getShopId().getEnableRedis() && redisFactory!=null){
			StringRedisUtil stringRedisUtil = redisFactory.openSessionWithString(interceptorConfig.getShopId().getRedisName());
			if(stringRedisUtil!=null) {
				shopId = stringRedisUtil.get(host,Long.class);
			}
		}else{
			//基于配置
			if(this.interceptorConfig.getShopId().getHosts()!=null && this.interceptorConfig.getShopId().getHosts().containsKey(host) && this.interceptorConfig.getShopId().getHosts().get(host)!=null){
				shopId = this.interceptorConfig.getShopId().getHosts().get(host);
			}else{
				shopId = WebUtil.DEFAULT_SHOP_ID;
			}
		}
		if(shopId==null) {
			shopId = WebUtil.DEFAULT_SHOP_ID;
		}
		WebUtil.setShopId(request , shopId);
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

	public InterceptorConfig getInterceptorConfig() {
		return interceptorConfig;
	}

	public void setInterceptorConfig(InterceptorConfig interceptorConfig) {
		this.interceptorConfig = interceptorConfig;
	}
	
	

	
}
