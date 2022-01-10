package com.ihltx.utility.i18n.annotations;

import com.ihltx.utility.i18n.I18nAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ImportAutoConfiguration({I18nAutoConfiguration.class})
public @interface EnableI18nAutoConfiguration {
}
