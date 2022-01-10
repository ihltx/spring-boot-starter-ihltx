package com.ihltx.utility.log.service;


import com.ihltx.utility.log.config.LogConfig;

import ch.qos.logback.classic.Logger;
import org.springframework.context.ApplicationContext;

public interface LoggerBuilder {


	/**
	 * 基于调用上下文当前类父类，获取 LoggerBuilder 日志组件
	 * @return
	 */
	Logger getLogger();

	/**
	 * 基于调用上下文当前类祖先类，获取 LoggerBuilder 日志组件
	 * @return
	 */
	Logger getParentLogger();


	/**
	 * 基于指定类型，获取 LoggerBuilder 日志组件
	 * @param clazz
	 * @return
	 */
	Logger getLogger(Class<?> clazz);

	/**
	 * 基于指定完全限定类名，获取 LoggerBuilder 日志组件
	 * @param classname  完全限定类名
	 * @return
	 */
	Logger getLogger(String classname);

	/**
	 * 基于指定ShopId，获取 LoggerBuilder 日志组件
	 * @param ShopId  指定ShopId
	 * @return
	 */
	Logger getLogger(long ShopId);

	/**
	 * 基于指定ShopId及名称，获取 LoggerBuilder 日志组件
	 * @param ShopId  指定ShopId
	 * @param name    指定名称
	 * @return
	 */
	Logger getLogger(long ShopId, String name);

	/**
	 * 基于当前获取的 LoggerBuilder 日志组件输出 DEBUG 信息
	 * @param message
	 */
	void debug(String message);

	/**
	 * 基于当前获取的 LoggerBuilder 日志组件输出 INFO 信息
	 * @param message
	 */
	void info(String message);

	/**
	 * 基于当前获取的 LoggerBuilder 日志组件输出 WARN 信息
	 * @param message
	 */
	void warn(String message);

	/**
	 * 基于当前获取的 LoggerBuilder 日志组件输出 ERROR 信息
	 * @param message
	 */
	void error(String message);

	/**
	 * 基于当前获取的 LoggerBuilder 日志组件输出 TRACE 信息
	 * @param message
	 */
	void trace(String message);

}
