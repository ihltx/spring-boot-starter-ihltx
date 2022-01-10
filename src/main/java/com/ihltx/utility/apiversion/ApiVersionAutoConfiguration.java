package com.ihltx.utility.apiversion;

import com.ihltx.utility.apiversion.config.ApiVersionConfig;
import com.ihltx.utility.apiversion.config.CustomRequestMappingHandlerMapping;
import com.ihltx.utility.interceptor.config.InterceptorConfig;
import com.ihltx.utility.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SuppressWarnings("all")
@Configuration
@EnableConfigurationProperties(value = {ApiVersionConfig.class,InterceptorConfig.class})
@ConditionalOnProperty(prefix = "ihltx.apiversion" , name = "enable" , havingValue = "true")
public class ApiVersionAutoConfiguration implements WebMvcRegistrations {

    @Autowired
    private ApiVersionConfig apiVersionConfig;

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        String defaultVersion = apiVersionConfig.getDefaultVersion() ;
        if(StringUtil.isNullOrEmpty(defaultVersion)){
            defaultVersion = "1.0.0.0";
        }

        String versionName = apiVersionConfig.getVersionName();
        if(StringUtil.isNullOrEmpty(versionName)){
            versionName = "version";
        }

        String versionOrder = apiVersionConfig.getVersionOrder();
        if(!StringUtil.isNullOrEmpty(versionOrder)){
            String[] versionOrders = versionOrder.toLowerCase().split(",");
            StringBuffer sb =new StringBuffer();
            for(int i=0; i<versionOrders.length; i++){
                String s = versionOrders[i];
                if(!StringUtil.isNullOrEmpty(s)){
                    s =StringUtil.trim(s ," ");
                    if(s.equals("header") || s.equals("request") || s.equals("cookie")){
                        sb.append(s).append(",");
                    }
                }
            }
            versionOrder = StringUtil.trim(sb.toString() , ",");
        }else{
            versionOrder = "request,header,cookie";
        }

        CustomRequestMappingHandlerMapping handlerMapping = new CustomRequestMappingHandlerMapping();
        handlerMapping.setDefaultVersion(defaultVersion);
        handlerMapping.setVersionName(versionName);
        handlerMapping.setVersionOrder(versionOrder);
        handlerMapping.setOrder(0);
        return handlerMapping;
    }


}

