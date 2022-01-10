package com.ihltx.utility.swagger.annotations;

import com.ihltx.utility.swagger.SwaggerAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import springfox.documentation.oas.annotations.EnableOpenApi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ImportAutoConfiguration({SwaggerAutoConfiguration.class})
@EnableOpenApi
public @interface EnableSwaggerAutoConfiguration {
}
