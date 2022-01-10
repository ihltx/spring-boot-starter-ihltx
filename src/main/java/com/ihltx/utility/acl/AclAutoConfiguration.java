package com.ihltx.utility.acl;

import com.ihltx.utility.acl.config.AclConfig;
import com.ihltx.utility.acl.service.impl.UserAuthorityInterceptor;
import com.ihltx.utility.freemarker.FreemarkerAutoConfiguration;
import com.ihltx.utility.util.ImageVerifyCodeUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SuppressWarnings("all")
@Configuration
@EnableConfigurationProperties({AclConfig.class})
// matchIfMissing 缺少该配置属性时是否可以加载。如果为true，没有该配置属性时也会正常加载；反之则不会加载
@ConditionalOnProperty(prefix = "ihltx.acl" , name = "enable" , havingValue = "true")
@ComponentScan(basePackages = {"com.ihltx.utility.acl"})
@MapperScan(basePackages = {"com.ihltx.utility.acl.mapper"})
@ImportAutoConfiguration(classes = {FreemarkerAutoConfiguration.class})
public class AclAutoConfiguration implements WebMvcConfigurer {

	@Autowired
	private AclConfig aclConfig;

	@Autowired
	private UserAuthorityInterceptor userAuthorityInterceptor;

	@Bean("aclImageVerifyCodeUtil")
	public ImageVerifyCodeUtil getImageVerifyCodeUtil(){
		ImageVerifyCodeUtil imageVerifyCodeUtil = new ImageVerifyCodeUtil();
		imageVerifyCodeUtil.setVerifyCodeLength(aclConfig.getVerificationCode().getLength());
		imageVerifyCodeUtil.setVerifyCodeKey(aclConfig.getVerificationCode().getKey());
		imageVerifyCodeUtil.setVerifyCodeType(aclConfig.getVerificationCode().getType());
		imageVerifyCodeUtil.setVerifyCodeExpires(aclConfig.getVerificationCode().getExpires());
		imageVerifyCodeUtil.setWidth(aclConfig.getVerificationCode().getWidth());
		imageVerifyCodeUtil.setHeight(aclConfig.getVerificationCode().getHeight());
		imageVerifyCodeUtil.setFontName(aclConfig.getVerificationCode().getFontName());
		imageVerifyCodeUtil.setFontSize(aclConfig.getVerificationCode().getFontSize());
		imageVerifyCodeUtil.setDisturbLineNumber(aclConfig.getVerificationCode().getDisturbLineNumber());
		return imageVerifyCodeUtil;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		if(aclConfig.getEnable()) {
			registry.addInterceptor(userAuthorityInterceptor).order(Ordered.LOWEST_PRECEDENCE);
		}
	}



}