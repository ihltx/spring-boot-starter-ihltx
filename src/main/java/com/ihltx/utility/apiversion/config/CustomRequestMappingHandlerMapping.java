package com.ihltx.utility.apiversion.config;

import com.ihltx.utility.apiversion.annotations.ApiVersion;
import com.ihltx.utility.apiversion.conditions.ApiVersionCondition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

public class CustomRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    private String defaultVersion;
    public String getDefaultVersion() {
        return defaultVersion;
    }

    public void setDefaultVersion(String defaultVersion) {
        this.defaultVersion = defaultVersion;
    }

    private String versionName;
    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }


    private String versionOrder;
    public String getVersionOrder() {
        return versionOrder;
    }

    public void setVersionOrder(String versionOrder) {
        this.versionOrder = versionOrder;
    }


    @Override
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        ApiVersion apiVersion = AnnotationUtils.findAnnotation(handlerType, ApiVersion.class);
        return createCondition(apiVersion);
    }

    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        ApiVersion apiVersion = AnnotationUtils.findAnnotation(method, ApiVersion.class);
        return createCondition(apiVersion);
    }

    private RequestCondition<ApiVersionCondition> createCondition(ApiVersion apiVersion) {
        if( apiVersion != null){
            ApiVersionCondition apiVersionCondition =  new ApiVersionCondition(apiVersion.value());
            apiVersionCondition.setDefaultVersion(defaultVersion);
            apiVersionCondition.setVersionOrder(versionOrder);
            return apiVersionCondition;
        }
        return null;
    }

}

