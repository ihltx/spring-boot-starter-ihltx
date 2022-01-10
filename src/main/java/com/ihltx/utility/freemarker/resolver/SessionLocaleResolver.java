package com.ihltx.utility.freemarker.resolver;

import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.AbstractLocaleContextResolver;
import org.springframework.web.util.WebUtils;

@Component
public class SessionLocaleResolver extends AbstractLocaleContextResolver {
	 
	//语言信息放到session中的名称
	public static final String LOCALE_SESSION_ATTRIBUTE_NAME = SessionLocaleResolver.class.getName() + ".LOCALE";
 
	public static final String TIME_ZONE_SESSION_ATTRIBUTE_NAME = SessionLocaleResolver.class.getName() + ".TIME_ZONE";
 
 
	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, LOCALE_SESSION_ATTRIBUTE_NAME);
		if (locale == null) {
			locale = determineDefaultLocale(request);
		}
		return locale;
	}
 
	@Override
	public LocaleContext resolveLocaleContext(final HttpServletRequest request) {
		return new TimeZoneAwareLocaleContext() {
			@Override
			public Locale getLocale() {
				Locale locale = (Locale) WebUtils.getSessionAttribute(request, LOCALE_SESSION_ATTRIBUTE_NAME);
				if (locale == null) {
					locale = determineDefaultLocale(request);
				}
				return locale;
			}
			@Override
			public TimeZone getTimeZone() {
				TimeZone timeZone = (TimeZone) WebUtils.getSessionAttribute(request, TIME_ZONE_SESSION_ATTRIBUTE_NAME);
				if (timeZone == null) {
					timeZone = determineDefaultTimeZone(request);
				}
				return timeZone;
			}
		};
	}
 
	@Override
	public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext) {
		Locale locale = null;
		TimeZone timeZone = null;
		if (localeContext != null) {
			locale = localeContext.getLocale();
			if (localeContext instanceof TimeZoneAwareLocaleContext) {
				timeZone = ((TimeZoneAwareLocaleContext) localeContext).getTimeZone();
			}
		}
		WebUtils.setSessionAttribute(request, LOCALE_SESSION_ATTRIBUTE_NAME, locale);
		WebUtils.setSessionAttribute(request, TIME_ZONE_SESSION_ATTRIBUTE_NAME, timeZone);
	}
 
	protected Locale determineDefaultLocale(HttpServletRequest request) {
		Locale defaultLocale = getDefaultLocale();
		if (defaultLocale == null) {
			defaultLocale = request.getLocale();
		}
		return defaultLocale;
	}
 
	protected TimeZone determineDefaultTimeZone(HttpServletRequest request) {
		return getDefaultTimeZone();
	}
 
}
