package com.ihltx.utility.freemarker.annotations;

import com.ihltx.utility.i18n.I18nAutoConfiguration;
import com.ihltx.utility.freemarker.FreemarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ImportAutoConfiguration({I18nAutoConfiguration.class, FreemarkerAutoConfiguration.class})
public @interface EnableFreemarkerAutoConfiguration {
}
