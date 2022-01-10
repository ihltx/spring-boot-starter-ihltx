package com.ihltx.utility.swagger;

import com.ihltx.utility.swagger.config.SwaggerConfig;
import com.ihltx.utility.util.StringUtil;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SuppressWarnings("all")
@Configuration
@EnableConfigurationProperties({SwaggerConfig.class})
@EnableSwagger2
public class SwaggerAutoConfiguration{

	@Autowired
	private SwaggerConfig swaggerConfig;

	private static final String SPLITOR = ";";

	@Bean
	public Docket buildDocket() {
		Docket docket = new Docket(DocumentationType.SWAGGER_2);
		docket.apiInfo(buildApiInfo());
		if(!this.swaggerConfig.getEnabled()){
			docket.enable(false);
		}
		ApiSelectorBuilder apiSelectorBuilder = docket.select();
		if(this.swaggerConfig.getWithApiAnnotation()){
			apiSelectorBuilder.apis(RequestHandlerSelectors.withClassAnnotation(Api.class));
		}else{
			apiSelectorBuilder.apis(basePackage(this.swaggerConfig.getBasePackages()));
		}
		apiSelectorBuilder.paths(PathSelectors.any());
		return apiSelectorBuilder.build();
	}

	/**
	 * @param
	 * @return springfox.documentation.service.ApiInfo
	 * @Title: 构建API基本信息
	 * @methodName: buildApiInfo
	 */
	private ApiInfo buildApiInfo() {
		ApiInfoBuilder apiInfoBuilder = new ApiInfoBuilder();
		apiInfoBuilder.title(this.swaggerConfig.getTitle());
		apiInfoBuilder.description(this.swaggerConfig.getDescription());
		if(!StringUtil.isNullOrEmpty(this.swaggerConfig.getContactName())){
			Contact contact = new Contact(this.swaggerConfig.getContactName(),this.swaggerConfig.getContactUrl(),this.swaggerConfig.getContactEmail());
			apiInfoBuilder.contact(contact);
		}
		String version ="1.0.0";
		if(!StringUtil.isNullOrEmpty(this.swaggerConfig.getVersion())){
			version = this.swaggerConfig.getVersion();
		}
		return apiInfoBuilder.version(version).build();
	}



	public static Predicate<RequestHandler> basePackage(final String basePackage) {
		return input -> declaringClass(input).transform(handlerPackage(basePackage)).or(true);
	}

	private static Function<Class<?>, Boolean> handlerPackage(final String basePackage) {
		return input -> {
			for (String strPackage : basePackage.split(";")) {
				boolean isMatch = input.getPackage().getName().startsWith(strPackage);
				if (isMatch) {
					return true;
				}
			}
			return false;
		};
	}

	private static Optional<? extends Class<?>> declaringClass(RequestHandler input) {
		return Optional.fromNullable(input.declaringClass());
	}


}