package com.ihltx.utility.freemarker.controller;

import com.ihltx.utility.util.WebUtil;
import com.ihltx.utility.util.entity.Result;
import com.ihltx.utility.freemarker.exceptions.ViewNotFoundException;
import com.ihltx.utility.freemarker.service.FreemarkerUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
public class GlobalErrorController extends AbstractErrorController {

    private final ErrorProperties errorProperties;

    private final String ERROR_PATH ="/error";

    @Autowired
    private FreemarkerUtil freemarkerUtil;

    @Autowired
    public GlobalErrorController(ErrorAttributes errorAttributes, ServerProperties serverProperties) {
        super(errorAttributes);
        this.errorProperties=serverProperties.getError();
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    /**
     * Web页面错误处理
     */
    @RequestMapping(value = ERROR_PATH, produces = {"text/html"})
    @ResponseBody
    public String errorHtml(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> data=getErrorAttributes(request, isIncludeStackTrace(request, MediaType.ALL));
        if(data == null){
            data.put("error","");
            data.put("path" , "");
        }
        int code = response.getStatus();
        data.put("status" , code);
        if(request != null && WebUtil.isAjax(request)){
            Result result =new Result();
            result.setStatus("-1");
            result.setSuccess(false);
            result.setMsg(data.get("error").toString());
            result.setData(data);
            return JSONObject.toJSONString(result);
        }
        String url = null;
        if (404 == code) {
            url = "error/404";
        } else if (403 == code) {
            url = "error/403";
        } else {
            url = "error/500";
        }

        try{
            return freemarkerUtil.renderTemplate(data , url);
        }catch (IOException err){
            err.printStackTrace();
            return err.getMessage();
        }catch (ViewNotFoundException err){
            err.printStackTrace();
            return err.getMessage();
        }
    }


    @RequestMapping
    @ResponseBody
    public String error(HttpServletRequest request, HttpServletResponse response){
        return errorHtml(request, response);
    }


    protected boolean isIncludeStackTrace(HttpServletRequest request,
                                          MediaType produces) {
        ErrorProperties.IncludeStacktrace include = getErrorProperties().getIncludeStacktrace();
        if (include == ErrorProperties.IncludeStacktrace.ALWAYS) {
            return true;
        }
        if (include == ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM) {
            return getTraceParameter(request);
        }
        return false;
    }

    private ErrorProperties getErrorProperties() {
        return this.errorProperties;
    }

}
