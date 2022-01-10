package com.ihltx.utility.jwt.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "ihltx.jwt")
public class JwtConfig {
	private String secertKey = "9dacba0ee1fa655f317ebf2278f7fdf4";
	
	private int expireSeconds = 7200;
	
	private String secertType = "HMAC256";

}
