package com.ihltx.utility.sensitive.advice;

import com.ihltx.utility.freemarker.exceptions.ViewNotFoundException;
import com.ihltx.utility.freemarker.service.FreemarkerUtil;
import com.ihltx.utility.sensitive.exceptions.SensitiveException;
import com.ihltx.utility.util.WebUtil;
import com.ihltx.utility.util.entity.Result;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@ConditionalOnProperty(prefix = "ihltx.sensitive", name = "enable" , havingValue = "true")
public class SensitiveExceptionHandler {
    @Autowired
    private FreemarkerUtil freemarkerUtil;


    @ExceptionHandler(SensitiveException.class)
    @ResponseBody
    public String customException(SensitiveException e) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest request = null;
        if(servletRequestAttributes != null){
            request = servletRequestAttributes.getRequest();
        }
        Map<String, Object> data= new HashMap<>();
        if(request!=null){
            data.put("path" , request.getRequestURI());
        }else{
            data.put("path" , "");
        }
        data.put("error", e.getMessage());
        data.put("status" , 500);
        if(request!=null && WebUtil.isAjax(request)){
            Result result =new Result();
            result.setStatus("-1");
            result.setSuccess(false);
            result.setMsg(e.getMessage());
            result.setData(data);
            return JSONObject.toJSONString(result);
        }
        try{
            return freemarkerUtil.renderTemplate(data , "error/500");
        }catch (IOException err){
            err.printStackTrace();
            return err.getMessage();
        }catch (ViewNotFoundException err){
            err.printStackTrace();
            return err.getMessage();
        }
    }
}
