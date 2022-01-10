package com.ihltx.utility.jwt;

import com.ihltx.utility.jwt.config.JwtConfig;
import com.ihltx.utility.jwt.service.impl.JwtUtilImpl;
import com.ihltx.utility.jwt.service.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableConfigurationProperties(JwtConfig.class)
public class JwtAutoConfiguration {

	@Autowired
	private JwtConfig jwtConfig;

	@Bean
	@Primary
	@ConditionalOnMissingBean(JwtUtil.class)
	public JwtUtil getJwtUtil() {
		JwtUtil jwtUtil = new JwtUtilImpl();
		jwtUtil.setJwtConfig(jwtConfig);
		return jwtUtil; 
	}


}