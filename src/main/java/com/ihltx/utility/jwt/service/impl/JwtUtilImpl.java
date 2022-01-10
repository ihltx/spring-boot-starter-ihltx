package com.ihltx.utility.jwt.service.impl;

import com.ihltx.utility.jwt.config.JwtConfig;
import com.ihltx.utility.jwt.service.JwtUtil;
import com.ihltx.utility.util.ObjectUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtilImpl implements JwtUtil {

	private JwtConfig jwtConfig;

	public JwtConfig getJwtConfig() {
		return jwtConfig;
	}

	public void setJwtConfig(JwtConfig jwtConfig) {
		this.jwtConfig = jwtConfig;
	}

	/**
	 * 基于用户名及info扩展信息生成token
	 * 
	 * @param user 用户信息
	 * @return String null--失败
	 */
	public String sign(Object user) {
		if (user != null) {
			try {
				Map<String, Object> body = ObjectUtil.object2Map(user);
				return sign(body);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 基于用户名及info扩展信息生成token
	 * 
	 * @param user 用户信息
	 * @return String null--失败
	 */
	public String sign(Map<String, Object> user) {
		// 过期时间
		Date expiresAt = new Date(System.currentTimeMillis() + jwtConfig.getExpireSeconds() * 1000);
		// 私钥及加密算法
		Algorithm algorithm = null;
		// 设置头信息
		Map<String, Object> header = new HashMap<String, Object>();
		header.put("typ", "JWT");
		com.auth0.jwt.JWTCreator.Builder builder = JWT.create();

		if (jwtConfig.getSecertType().toUpperCase().equals("HMAC256")) {
			header.put("alg", "HS256");
			builder.withHeader(header);
			algorithm = Algorithm.HMAC256(jwtConfig.getSecertKey());
		} else if (jwtConfig.getSecertType().toUpperCase().equals("HMAC384")) {
			header.put("alg", "HS384");
			builder.withHeader(header);
			algorithm = Algorithm.HMAC384(jwtConfig.getSecertKey());
		} else if (jwtConfig.getSecertType().toUpperCase().equals("HMAC512")) {
			header.put("alg", "HS512");
			builder.withHeader(header);
			algorithm = Algorithm.HMAC512(jwtConfig.getSecertKey());
		}
		if (algorithm != null) {
			if (user != null) {
				for (String key : user.keySet()) {
					if(user.get(key)!=null) {
						builder.withClaim(key, user.get(key).toString());	
					}
				}
			}
			builder.withExpiresAt(expiresAt);
			return builder.sign(algorithm);
		}
		return null;
	}

	/**
	 * 校验token
	 * 
	 * @param token
	 * @return Map<String,Object> null--失败
	 */
	public Map<String,Object> verify(String token) {
		Map<String,Object> result = null;
		try {
			Algorithm algorithm = null;
			if (jwtConfig.getSecertType().toUpperCase().equals("HMAC256")) {
				algorithm = Algorithm.HMAC256(jwtConfig.getSecertKey());
			} else if (jwtConfig.getSecertType().toUpperCase().equals("HMAC384")) {
				algorithm = Algorithm.HMAC384(jwtConfig.getSecertKey());
			} else if (jwtConfig.getSecertType().toUpperCase().equals("HMAC512")) {
				algorithm = Algorithm.HMAC512(jwtConfig.getSecertKey());
			}
			if (algorithm != null) {
				
				JWTVerifier verifier = JWT.require(algorithm).build();
				DecodedJWT jwt = verifier.verify(token);
				result = new HashMap<String, Object>();
				for(String k : jwt.getClaims().keySet()) {
					Claim v = jwt.getClaims().get(k);
					result.put(k, v.asString());
				}
				return result;
			}
		} catch (IllegalArgumentException e) {
		} catch (JWTVerificationException e) {
		}
		return result;
	}

	


	/**
	 * 校验token并返回保存在jwt中的实体对象
	 * @param <T> 泛型类型
	 * @param token jwt令牌
	 * @param clazz  实体对象类型
	 * @return T  null--失败
	 */
	public <T> T verify(String token, Class<?> clazz) {
		Map<String,Object> result = verify(token);
		if(result==null) return null;
		return ObjectUtil.Map2Object(result, clazz);
	}
	
	
}
