package com.ihltx.utility.jwt.service;

import java.util.Map;

import com.ihltx.utility.jwt.config.JwtConfig;

public interface JwtUtil {


	/**
	 * 获取 JwtConfig 配置
	 * @return
	 */
	JwtConfig getJwtConfig();

	/**
	 * 设置 JwtConfig 配置
	 * @param jwtConfig
	 */
	void setJwtConfig(JwtConfig jwtConfig);

	/**
	 * 基于用户名及info扩展信息生成token
	 * 
	 * @param user 用户信息
	 * @return String null--失败
	 */
	String sign(Object user);

	/**
	 * 基于用户名及info扩展信息生成token
	 * 
	 * @param user 用户信息
	 * @return String null--失败
	 */
	String sign(Map<String, Object> user);

	/**
	 * 校验token
	 * 
	 * @param token
	 * @return Map<String,Object> null--失败
	 */
	Map<String,Object> verify(String token);

	


	/**
	 * 校验token并返回保存在jwt中的实体对象
	 * @param <T> 泛型类型
	 * @param token jwt令牌
	 * @param clazz  实体对象类型
	 * @return T  null--失败
	 */
	<T> T verify(String token, Class<?> clazz);
	
	
}
