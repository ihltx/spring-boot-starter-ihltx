package com.ihltx.utility.sensitive.aop;

import com.ihltx.utility.sensitive.exceptions.SensitiveException;
import com.ihltx.utility.sensitive.service.impl.DFAUtil;
import com.ihltx.utility.util.StringUtil;
import com.ihltx.utility.util.WebUtil;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Set;

public class CheckInputParameterAspect {
    @Autowired
    private DFAUtil dfaUtil ;

    /**
     * 前置通知
     *
     * @param
     * @throws Throwable
     */

    public void before(JoinPoint point) throws Throwable {
        //获取请求参数
        Object[] args = point.getArgs();
        if(args!=null && args.length>0){
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if(attributes!=null){
                HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
                if(request != null){
                    dfaUtil.init(WebUtil.getShopId(request));
                    // 数组转 String
                    Set<String> sensitiveWordByDFAMap = dfaUtil.getSensitiveWordByDFAMap(WebUtil.getShopId(request) , StringUtil.join(args,","), dfaUtil.getMatchType());
                    if(sensitiveWordByDFAMap.size()>=1){
                        //自定义的异常
                        throw new SensitiveException("Sensitive word capture exception, found sensitive word: " + StringUtils.join(sensitiveWordByDFAMap.iterator(),","));
                    }
                }
            }
        }
    }
}
