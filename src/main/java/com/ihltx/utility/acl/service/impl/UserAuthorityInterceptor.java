package com.ihltx.utility.acl.service.impl;

import com.ihltx.utility.acl.config.AclConfig;
import com.ihltx.utility.acl.entity.SysLoginFailure;
import com.ihltx.utility.acl.entity.SysUser;
import com.ihltx.utility.acl.service.UserService;
import com.ihltx.utility.freemarker.exceptions.ViewNotFoundException;
import com.ihltx.utility.freemarker.service.FreemarkerUtil;
import com.ihltx.utility.i18n.service.I18nUtil;
import com.ihltx.utility.jwt.service.JwtUtil;
import com.ihltx.utility.jwt.service.impl.JwtUtilImpl;
import com.ihltx.utility.util.*;
import com.ihltx.utility.util.entity.Result;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;

@Component
@ConditionalOnProperty(prefix = "ihltx.acl" , name = "enable" , havingValue = "true")

public class UserAuthorityInterceptor implements HandlerInterceptor {
    @Autowired
    private AclConfig aclConfig;

    @Autowired
    @Qualifier("aclImageVerifyCodeUtil")
    private ImageVerifyCodeUtil imageVerifyCodeUtil;

    @Autowired
    private FreemarkerUtil freemarkerUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private I18nUtil i18nUtil;

    @PostConstruct
    public void init(){
        if(aclConfig.getPermitUrls()==null){
            aclConfig.setPermitUrls(new HashSet<>());
        }
        aclConfig.getPermitUrls().add(aclConfig.getLoginPageUrl());
        aclConfig.getPermitUrls().add(aclConfig.getLoginPageUrlForAjax());
        aclConfig.getPermitUrls().add(aclConfig.getLoginProcessingUrl());
        aclConfig.getPermitUrls().add(aclConfig.getLoginProcessingUrlForAjax());
        aclConfig.getPermitUrls().add(aclConfig.getLoginFailureUrl());
        aclConfig.getPermitUrls().add(aclConfig.getLogoutUrl());
        if(aclConfig.getVerificationCode()!=null && aclConfig.getVerificationCode().getEnable() && !StringUtil.isNullOrEmpty(aclConfig.getVerificationCode().getUrl())){
            aclConfig.getPermitUrls().add(aclConfig.getVerificationCode().getUrl());
        }
    }

    /**
     * 检查是否放行
     * @param url    url
     * @return  true -- 放行    false  -- 禁止访问
     */
    private Boolean checkPermit(String url){
        if(aclConfig.getPermitUrls()==null || aclConfig.getPermitUrls().size()<=0){
            return false;
        }
        Boolean rs = false;
        for(String u : aclConfig.getPermitUrls()){
            u = "^" + StringUtil.trim(u , "/") + "$";
            u = u.replaceAll("\\*\\*" , "([^/]+(/[^/]+)~@~@~@)");
            u = u.replaceAll("\\*" , "([^/]+)");
            u = u.replaceAll("~@~@~@" , "*");
            if(Pattern.compile(u, Pattern.CASE_INSENSITIVE).matcher(url).find()){
                return true;
            }
        }
        return rs;
    }

    private void send403(String url , HttpServletRequest request, HttpServletResponse response){
        String msg = "No permission, access denied.";
        int code = 403;
        Map<String, Object> data= new HashMap<>();
        data.put("error",msg);
        data.put("path" , url);
        data.put("status" , code);
        if(!WebUtil.isAjax(request)){
            response.setContentType("text/html;charset=UTF-8");
            String html = null;
            try {
                html = freemarkerUtil.renderTemplate(data , "error/" + code);
            } catch (IOException | ViewNotFoundException e) {
                e.printStackTrace();
                html = e.getMessage();
            }
            response.setStatus(code);
            try {
                response.getWriter().println(html);
            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
            response.setContentType("application/json");
            Result result =new Result();
            result.setStatus(String.valueOf(code));
            result.setMsg(msg);
            result.setSuccess(false);
            result.setData(data);
            try {
                response.getWriter().println(JSONObject.toJSONString(result));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 返回 json
     * @param result        result
     * @param response
     */
    private void sendJson(Result result , HttpServletResponse response){
        response.setContentType("application/json");
        try {
            response.getWriter().println(JSONObject.toJSONString(result));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建jwt令牌
     * @param user  用户令牌
     * @return String
     */
    private String makeJwt(SysUser user){
        JwtUtil jwtUtil = new JwtUtilImpl();
        jwtUtil.setJwtConfig(aclConfig.getJwt());
        user.setPassword(null);
        return jwtUtil.sign(user);
    }

    /**
     * 通过jwt令牌获取用户令牌
     * @param token  用户令牌
     * @return SysUser
     */
    private SysUser getUserByJwt(String token){
        JwtUtil jwtUtil = new JwtUtilImpl();
        jwtUtil.setJwtConfig(aclConfig.getJwt());
        return jwtUtil.verify(token , SysUser.class);
    }

    /**
     * 通过request获取jwt令牌
     * @param request   request
     * @return String
     */
    private String getJwtByRequest(HttpServletRequest request){
        String tokenOrder = aclConfig.getTokenOrder();
        String[] tokenOrders = null;
        if(!StringUtil.isNullOrEmpty(tokenOrder)){
            tokenOrders = tokenOrder.toLowerCase().split(",");
            StringBuffer sb =new StringBuffer();
            for(int i=0; i<tokenOrders.length; i++){
                String s = tokenOrders[i];
                if(!StringUtil.isNullOrEmpty(s)){
                    s =StringUtil.trim(s ," ");
                    if(s.equals("header") || s.equals("request") || s.equals("cookie")){
                        sb.append(s).append(",");
                    }
                }
            }
            tokenOrder = StringUtil.trim(sb.toString() , ",");
        }else{
            tokenOrder = "request,header,cookie";
        }
        tokenOrders =tokenOrder.split(",");
        String token = null;
        for(int i=0;i< tokenOrders.length;i++){
            if(tokenOrders[i].equals("header")){
                token = request.getHeader(aclConfig.getTokenName());
                if(!StringUtil.isNullOrEmpty(token)){
                    break;
                }
            }else if(tokenOrders[i].equals("request")){
                token = request.getParameter(aclConfig.getTokenName());
                if(!StringUtil.isNullOrEmpty(token)){
                    break;
                }
            }else if(tokenOrders[i].equals("cookie")){
                token = WebUtil.getCookie(request , aclConfig.getTokenName());
                if(!StringUtil.isNullOrEmpty(token)){
                    break;
                }
            }
        }
        return token;
    }

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if(!aclConfig.getEnable()){
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }
        Result result = new Result();
        String msg = null;
        SysLoginFailure sysLoginFailure = null;

        String url = StringUtil.trim(request.getRequestURI() , "/").replaceAll("//","/");
        String token = getJwtByRequest(request);
        SysUser user = null;
        if(checkPermit(url)){
            if(!(handler instanceof HandlerMethod)){
                if((url.equals(StringUtil.trim(aclConfig.getLoginPageUrl(),"/")) || url.equals(StringUtil.trim(aclConfig.getLoginPageUrlForAjax(),"/"))) && request.getMethod().toLowerCase().equals("get")){
                    if(!StringUtil.isNullOrEmpty(token)){
                        try{
                            user = getUserByJwt(token);
                            if(user!=null){
                                WebUtil.redirect(aclConfig.getLoginSuccessUrl() , request, response);
                                return false;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    //login page
                    response.setCharacterEncoding("UTF-8");
                    response.setContentType("text/html; charset=utf-8");
                    String uuid = SecurityUtil.uuid();
                    request.getSession().setAttribute(aclConfig.getCsrfParameterName() , uuid);
                    String html="\n" +
                            "<!DOCTYPE html>\n" +
                            "<html lang=\"en\">\n" +
                            "  <head>\n" +
                            "    <meta charset=\"utf-8\">\n" +
                            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n" +
                            "    <meta name=\"description\" content=\"\">\n" +
                            "    <meta name=\"author\" content=\"\">\n" +
                            "    <title>Please sign in</title>\n" +
                            "    <link href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-/Y6pD6FV/Vv2HJnA6t+vslU6fwYXjCFtcEpHbNJ0lyAFsXTsjBbfaDjzALeQsN6M\" crossorigin=\"anonymous\">\n" +
                            "    <link href=\"https://getbootstrap.com/docs/4.0/examples/signin/signin.css\" rel=\"stylesheet\" crossorigin=\"anonymous\"/>\n" +
                            "    <script src=\"https://code.jquery.com/jquery-2.1.1.min.js\"></script>\n" +
                            " </head>\n" +
                            "  <body>\n" +
                            "     <div class=\"container\">\n";
                    if(url.equals(StringUtil.trim(aclConfig.getLoginPageUrl(),"/"))){
                        html += "      <form id=\"frmLogin\" name=\"frmLogin\" class=\"form-signin\" method=\"post\" action=\"" + aclConfig.getLoginProcessingUrl() + "\">\n";
                    }else{
                        html += "      <form id=\"frmLogin\" name=\"frmLogin\" class=\"form-signin\" method=\"post\" onsubmit=\"return ajaxSubmit()\">\n";
                    }
                    html += "        <h2 class=\"form-signin-heading\">Please sign in</h2>\n" +
                            "        <p>\n" +
                            "          <label for=\"" + aclConfig.getUserNameParameter() +"\" class=\"sr-only\">" + aclConfig.getUserNameParameter() +"</label>\n" +
                            "          <input type=\"text\" id=\"" + aclConfig.getUserNameParameter() +"\" name=\"" + aclConfig.getUserNameParameter() + "\" class=\"form-control\" placeholder=\"" + aclConfig.getUserNameParameter() +"\" required autofocus>\n" +
                            "        </p>\n" +
                            "        <p>\n" +
                            "          <label for=\"" + aclConfig.getPasswordParameter() +"\" class=\"sr-only\">" + aclConfig.getPasswordParameter() + "</label>\n" +
                            "          <input type=\"password\" id=\"" + aclConfig.getPasswordParameter() +"\" name=\"" + aclConfig.getPasswordParameter() + "\" class=\"form-control\" placeholder=\"" +  aclConfig.getPasswordParameter() +"\" required>\n" +
                            "        </p>\n";
                    if(aclConfig.getVerificationCode().getEnable()){
                        html +="        <p>\n" +
                                "          <label for=\"" + aclConfig.getVerificationCode().getParameterName() +"\" class=\"sr-only\">" + aclConfig.getVerificationCode().getParameterName() + "</label>\n" +
                                "          <input type=\"text\" id=\"" + aclConfig.getVerificationCode().getParameterName() +"\" name=\"" + aclConfig.getVerificationCode().getParameterName() + "\" class=\"form-control\" placeholder=\"" +  aclConfig.getVerificationCode().getParameterName() +"\" required style=\"float:left;width:180px;\">\n" +
                                "          <img src=\"" + aclConfig.getVerificationCode().getUrl() + "?"+ DateUtil.getTimeMillis() +"\" onclick=\"this.src='" +  aclConfig.getVerificationCode().getUrl() + "?t='+ (new Date()).getTime();\" style=\"cursor:pointer;float:left;position: relative; top: 4px;margin-left:10px;\"/>" +
                                "        </p>\n";
                    }
                    if(aclConfig.getRememberMe()){
                        html +="        <p style='clear:both'>\n" +
                                "          <input type=\"checkbox\" id=\"" + aclConfig.getRememberMeParameter() +"\" name=\"" + aclConfig.getRememberMeParameter() + "\" value=\"1\" style='margin-top:20px'/>\n" +
                                "          <label for=\"" + aclConfig.getRememberMeParameter() +"\">" + aclConfig.getRememberMeParameter() + "</label>\n" +
                                "        </p>\n";
                    }
                    html +=
                            "<input name=\"" + aclConfig.getCsrfParameterName() +"\" type=\"hidden\" value=\"" + uuid + "\" />\n" +
                                    "        <button class=\"btn btn-lg btn-primary btn-block\" type=\"submit\">Sign in</button>\n" +
                                    "      </form>\n" +
                                    "</div>\n" +
                                    "</body>\n";
                    if(url.equals(StringUtil.trim(aclConfig.getLoginPageUrlForAjax(),"/"))){
                        html +=
                                "<script type=\"text/javascript\">\n" +
                                        "   function ajaxSubmit(){\n" +
                                        "       $.ajax({\n" +
                                        "           type: \"POST\",\n" +
                                        "           dataType: \"JSON\",\n" +
                                        "           url: \"" + aclConfig.getLoginPageUrlForAjax() + "\",\n" +
                                        "           data: $(\"#frmLogin\").serialize(),\n" +
                                        "           success: function (result) {\n" +
                                        "               if(result.success){\n" +
                                        "                   alert(result.data.token);\n" +
                                        "                   location = \"" + aclConfig.getLoginSuccessUrl() + "\";\n" +
                                        "               }else{\n" +
                                        "                   alert(result.msg);\n" +
                                        "               }\n" +
                                        "           }\n" +
                                        "       });\n" +
                                        "       return false;\n" +
                                        "   }\n" +
                                        "</script>\n";
                    }
                    html +=
                                    "</html>";
                    response.getWriter().println(html);
                    return false;
                }else if(url.equals(StringUtil.trim(aclConfig.getLoginProcessingUrl(),"/")) && request.getMethod().toLowerCase().equals("post")){
                    //login post for page
                    String _csrf= request.getParameter(aclConfig.getCsrfParameterName());
                    String userName = request.getParameter(aclConfig.getUserNameParameter());
                    if(aclConfig.getLoginSameIpRetryUserNameNumber()>0 && aclConfig.getLoginSameUserNameRetryTimes()>0){
                        int loginFailureUserNameNumber = userService.getLoginFailureUserNameNumber(WebUtil.getIpAddress(request) , aclConfig.getLoginSameUserNameRetryTimes() , aclConfig.getLoginLockMinutes());
                        if(loginFailureUserNameNumber >= aclConfig.getLoginSameIpRetryUserNameNumber()){
                            WebUtil.redirect(aclConfig.getLoginFailureUrl() + "=IpLoginlockedError&loginLockMinutes=" + aclConfig.getLoginLockMinutes() , request, response);
                            return false;
                        }

                        sysLoginFailure = userService.getLoginFailure(userName , WebUtil.getIpAddress(request));
                        if(sysLoginFailure!=null && sysLoginFailure.getFailureTimes()>= aclConfig.getLoginSameUserNameRetryTimes()){
                            if((DateUtil.getTime()-DateUtil.getTime(sysLoginFailure.getLoginTime()))/60 <= aclConfig.getLoginLockMinutes()){
                                WebUtil.redirect(aclConfig.getLoginFailureUrl() + "=LoginlockedError&loginLockMinutes=" + aclConfig.getLoginLockMinutes() , request, response);
                                return false;
                            }else{
                                sysLoginFailure.setFailureTimes(0);
                            }
                        }
                    }


                    String password = request.getParameter(aclConfig.getPasswordParameter());
                    String verificationCode = null;
                    if(aclConfig.getVerificationCode().getEnable()){
                        verificationCode = request.getParameter(aclConfig.getVerificationCode().getParameterName());
                        if(!verificationCode.equalsIgnoreCase(imageVerifyCodeUtil.getSaveVerifyCode(request))){
                            WebUtil.redirect(aclConfig.getLoginFailureUrl() + "=VerifyCodeError" , request, response);
                            return false;
                        }
                    }
                    Boolean rememberMe = false;
                    if(aclConfig.getRememberMe()){
                        if(request.getParameter(aclConfig.getRememberMeParameter())!=null){
                            if(StringUtil.isInteger(request.getParameter(aclConfig.getRememberMeParameter()))){
                                rememberMe = Integer.valueOf(request.getParameter(aclConfig.getRememberMeParameter()))==0?false:true;
                            }else{
                                rememberMe = Boolean.valueOf(request.getParameter(aclConfig.getRememberMeParameter()));
                            }
                        }
                    }
                    if(request.getSession().getAttribute(aclConfig.getCsrfParameterName())==null || !_csrf.equals(request.getSession().getAttribute(aclConfig.getCsrfParameterName()).toString())){
                        WebUtil.redirect(aclConfig.getLoginFailureUrl() + "=csrfError" , request, response);
                        return false;
                    }

                    user = userService.login(userName,password);
                    if(user==null){
                        msg = aclConfig.getLoginFailureUrl() + "=usernameErrorOrpasswordError";
                        if(aclConfig.getLoginSameIpRetryUserNameNumber()>0 && aclConfig.getLoginSameUserNameRetryTimes()>0){
                            if(sysLoginFailure == null){
                                sysLoginFailure =new SysLoginFailure();
                                sysLoginFailure.setUserName(userName);
                                sysLoginFailure.setFailureTimes(1);
                                sysLoginFailure.setLoginTime(new Date());
                                sysLoginFailure.setLoginIp(WebUtil.getIpAddress(request));
                            }else{
                                sysLoginFailure.setFailureTimes(sysLoginFailure.getFailureTimes()+1);
                                sysLoginFailure.setLoginTime(new Date());
                            }
                            userService.saveLoginFailure(sysLoginFailure);
                            msg += "&leftRetryTimes=" + (aclConfig.getLoginSameUserNameRetryTimes() - sysLoginFailure.getFailureTimes());
                            if(sysLoginFailure.getFailureTimes()>= aclConfig.getLoginSameUserNameRetryTimes()){
                                msg =aclConfig.getLoginFailureUrl() + "=IpLoginlockedError&loginLockMinutes=" + aclConfig.getLoginLockMinutes();
                            }
                        }
                        WebUtil.redirect(msg , request, response);
                        return false;
                    }else{
                        if(aclConfig.getLoginSameIpRetryUserNameNumber()>0 && aclConfig.getLoginSameUserNameRetryTimes()>0){
                            userService.deleteLoginFailure(userName , WebUtil.getIpAddress(request));
                        }
                    }
                    token = makeJwt(user);
                    if(rememberMe){
                        WebUtil.setCookie(aclConfig.getTokenName() , token , "/" ,null , true , aclConfig.getJwt().getExpireSeconds(),response);
                    }else{
                        WebUtil.setCookie(aclConfig.getTokenName() , token , "/" ,null , true , 0 ,response);
                    }
                    request.getSession().removeAttribute(aclConfig.getCsrfParameterName());
                    WebUtil.redirect(aclConfig.getLoginSuccessUrl() , request, response);
                    return false;
                }else if(url.equals(StringUtil.trim(aclConfig.getLoginProcessingUrlForAjax(),"/")) && request.getMethod().toLowerCase().equals("post")){
                    //login post for ajax
                    String _csrf= request.getParameter(aclConfig.getCsrfParameterName());
                    String userName = request.getParameter(aclConfig.getUserNameParameter());
                    if(aclConfig.getLoginSameIpRetryUserNameNumber()>0 && aclConfig.getLoginSameUserNameRetryTimes()>0){
                        int loginFailureUserNameNumber = userService.getLoginFailureUserNameNumber(WebUtil.getIpAddress(request) , aclConfig.getLoginSameUserNameRetryTimes() , aclConfig.getLoginLockMinutes());
                        if(loginFailureUserNameNumber >= aclConfig.getLoginSameIpRetryUserNameNumber()){
                            msg = aclConfig.getLoginFailureUrl() + "=IpLoginlockedError&loginLockMinutes=" + aclConfig.getLoginLockMinutes();
                            result.setStatus("-1");
                            result.setSuccess(false);
                            result.setMsg(msg);
                            sendJson(result , response);
                            return false;
                        }
                        sysLoginFailure = userService.getLoginFailure(userName , WebUtil.getIpAddress(request));
                        if(sysLoginFailure!=null && sysLoginFailure.getFailureTimes()>= aclConfig.getLoginSameUserNameRetryTimes()){
                            if((DateUtil.getTime()-DateUtil.getTime(sysLoginFailure.getLoginTime()))/60 <= aclConfig.getLoginLockMinutes()){
                                msg = aclConfig.getLoginFailureUrl() + "=LoginlockedError&loginLockMinutes=" + aclConfig.getLoginLockMinutes();
                                result.setStatus("-1");
                                result.setSuccess(false);
                                result.setMsg(msg);
                                sendJson(result , response);
                                return false;
                            }else{
                                sysLoginFailure.setFailureTimes(0);
                            }
                        }
                    }

                    String password = request.getParameter(aclConfig.getPasswordParameter());
                    String verificationCode = null;
                    if(aclConfig.getVerificationCode().getEnable()){
                        verificationCode = request.getParameter(aclConfig.getVerificationCode().getParameterName());
                        if(!verificationCode.equalsIgnoreCase(imageVerifyCodeUtil.getSaveVerifyCode(request))){
                            msg = aclConfig.getLoginFailureUrl() + "=VerifyCodeError";
                            result.setStatus("-1");
                            result.setSuccess(false);
                            result.setMsg(msg);
                            sendJson(result , response);
                            return false;
                        }
                    }
                    Boolean rememberMe = false;
                    if(aclConfig.getRememberMe()){
                        if(request.getParameter(aclConfig.getRememberMeParameter())!=null){
                            if(StringUtil.isInteger(request.getParameter(aclConfig.getRememberMeParameter()))){
                                rememberMe = Integer.valueOf(request.getParameter(aclConfig.getRememberMeParameter()))==0?false:true;
                            }else{
                                rememberMe = Boolean.valueOf(request.getParameter(aclConfig.getRememberMeParameter()));
                            }
                        }
                    }
                    if(request.getSession().getAttribute(aclConfig.getCsrfParameterName())==null || !_csrf.equals(request.getSession().getAttribute(aclConfig.getCsrfParameterName()).toString())){
                        msg = aclConfig.getLoginFailureUrl() + "=csrfError";
                        result.setStatus("-1");
                        result.setSuccess(false);
                        result.setMsg(msg);
                        sendJson(result , response);
                        return false;
                    }

                    user = userService.login(userName,password);
                    if(user==null){
                        msg = aclConfig.getLoginFailureUrl() + "=usernameErrorOrpasswordError";
                        if(aclConfig.getLoginSameIpRetryUserNameNumber()>0 && aclConfig.getLoginSameUserNameRetryTimes()>0){
                            if(sysLoginFailure == null){
                                sysLoginFailure =new SysLoginFailure();
                                sysLoginFailure.setUserName(userName);
                                sysLoginFailure.setFailureTimes(1);
                                sysLoginFailure.setLoginTime(new Date());
                                sysLoginFailure.setLoginIp(WebUtil.getIpAddress(request));
                            }else{
                                sysLoginFailure.setFailureTimes(sysLoginFailure.getFailureTimes()+1);
                                sysLoginFailure.setLoginTime(new Date());
                            }
                            userService.saveLoginFailure(sysLoginFailure);
                            msg += "&leftRetryTimes=" + (aclConfig.getLoginSameUserNameRetryTimes() - sysLoginFailure.getFailureTimes());
                            if(sysLoginFailure.getFailureTimes()>= aclConfig.getLoginSameUserNameRetryTimes()){
                                msg =aclConfig.getLoginFailureUrl() + "=IpLoginlockedError&loginLockMinutes=" + aclConfig.getLoginLockMinutes();
                            }
                        }
                        result.setStatus("-1");
                        result.setSuccess(false);
                        result.setMsg(msg);
                        sendJson(result , response);
                        return false;
                    }else{
                        if(aclConfig.getLoginSameIpRetryUserNameNumber()>0 && aclConfig.getLoginSameUserNameRetryTimes()>0){
                            userService.deleteLoginFailure(userName , WebUtil.getIpAddress(request));
                        }
                    }
                    token = makeJwt(user);
                    if(rememberMe){
                        WebUtil.setCookie(aclConfig.getTokenName() , token , "/" ,null , true , aclConfig.getJwt().getExpireSeconds(),response);
                    }else{
                        WebUtil.setCookie(aclConfig.getTokenName() , token , "/" ,null , true , 0 ,response);
                    }
                    request.getSession().removeAttribute(aclConfig.getCsrfParameterName());
                    result.setStatus("0");
                    result.setSuccess(true);
                    Map<String , Object> data = new HashMap<>();
                    data.put("token" , token);
                    result.setData(data);
                    result.setMsg(aclConfig.getLoginSuccessUrl());
                    sendJson(result , response);
                    return false;
                }else if(url.equals(StringUtil.trim(aclConfig.getLogoutUrl(),"/"))){
                    //logout
                    if(!StringUtil.isNullOrEmpty(token)){
                        user = null;
                        try{
                            user = getUserByJwt(token);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    if(user!=null){
                        userService.logout(user);
                        userService.clearCache(user.getUserId());
                    }
                    WebUtil.setCookie(aclConfig.getTokenName() , null);
                    WebUtil.redirect(aclConfig.getLogoutSuccessUrl() , request, response);
                    return false;
                }else if(url.equals(StringUtil.trim(aclConfig.getVerificationCode().getUrl(),"/"))){
                    //VerificationCode
                    imageVerifyCodeUtil.outputImage2Response(request,response);
                    return false;
                }
            }

            return HandlerInterceptor.super.preHandle(request, response, handler);
        }

        if(StringUtil.isNullOrEmpty(token)){
            send403(request.getRequestURI(), request , response);
            return false;
        }
        user = null;
        try{
            user = getUserByJwt(token);

        }catch (Exception e){
            e.printStackTrace();
        }
        if(user==null){
            send403(request.getRequestURI(),request , response);
            return false;
        }

        if(!userService.decide(user.getUserId() , request.getRequestURI() , request.getQueryString() , i18nUtil.getLanguage(request) , WebUtil.getShopId(request))){
            send403(request.getRequestURI(), request ,response);
            return false;
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }


}
