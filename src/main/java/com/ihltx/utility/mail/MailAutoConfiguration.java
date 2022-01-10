package com.ihltx.utility.mail;

import com.ihltx.utility.mail.config.MailConfig;
import com.ihltx.utility.mail.service.JavaMailSenderUtil;
import com.ihltx.utility.mail.service.impl.JavaMailSenderUtilImpl;
import com.ihltx.utility.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Properties;

@SuppressWarnings("all")
@Configuration
@EnableConfigurationProperties({MailConfig.class})
// matchIfMissing 缺少该配置属性时是否可以加载。如果为true，没有该配置属性时也会正常加载；反之则不会加载
@ConditionalOnProperty(prefix = "ihltx.mail" , name = "host" , matchIfMissing = false)
public class MailAutoConfiguration implements WebMvcConfigurer {

	@Autowired
	private MailConfig maillConfig;

	@Bean
	public JavaMailSenderUtil getJavaMailSenderUtil(){
		JavaMailSenderUtil javaMailSenderUtil = new JavaMailSenderUtilImpl();

		javaMailSenderUtil.setMailConfig(maillConfig);

		JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
		if(!StringUtil.isNullOrEmpty(maillConfig.getProtocol())){
			javaMailSenderImpl.setProtocol(maillConfig.getProtocol());
		}
		if(!StringUtil.isNullOrEmpty(maillConfig.getHost())){
			javaMailSenderImpl.setHost(maillConfig.getHost());
		}
		if(maillConfig.getPort()!=null && maillConfig.getPort()>0){
			javaMailSenderImpl.setPort(maillConfig.getPort());
		}
		if(!StringUtil.isNullOrEmpty(maillConfig.getUsername())){
			javaMailSenderImpl.setUsername(maillConfig.getUsername());
		}
		if(!StringUtil.isNullOrEmpty(maillConfig.getPassword())){
			javaMailSenderImpl.setPassword(maillConfig.getPassword());
		}
		if(!StringUtil.isNullOrEmpty(maillConfig.getDefaultEncoding())){
			javaMailSenderImpl.setDefaultEncoding(maillConfig.getDefaultEncoding());
		}

		if(maillConfig.getProperties()!=null &&  !maillConfig.getProperties().isEmpty()){
			Properties properties =new Properties();
			for(String key : maillConfig.getProperties().keySet()){
				if(maillConfig.getProperties().get(key).toString().equals("false") || maillConfig.getProperties().get(key).toString().equals("true")){
					properties.put(key , Boolean.valueOf(maillConfig.getProperties().get(key)));
				}else{
					properties.put(key , maillConfig.getProperties().get(key));
				}

			}
			javaMailSenderImpl.setJavaMailProperties(properties);
		}
		javaMailSenderUtil.setJavaMailSenderImpl(javaMailSenderImpl);
		return javaMailSenderUtil;
	}
}