package com.ihltx.utility.task.task;

import com.ihltx.utility.httpclient.service.RestTemplateUtil;
import com.ihltx.utility.log.service.LoggerBuilder;
import com.ihltx.utility.util.StringUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UrlTask extends BaseTask {

    @Override
    public void run() {
        RestTemplateUtil restTemplateUtil = this.applicationContext.getBean(RestTemplateUtil.class);
        LoggerBuilder loggerBuilder = applicationContext.getBean(LoggerBuilder.class);
        String msg = "Task(url=" + this.task.getTaskUrl() + " , ID=" + this.task.getTaskId() + " , name=" + this.task.getTaskName() + " , type=" + (task.getTaskIsDataBase()?"Database":"Configuration") + ") execute failed. Reason: ";
        try{
            if(restTemplateUtil == null){
                msg += "Spring application context did not find " + RestTemplateUtil.class + ".";
                throw new Exception(msg);
            }
            if(this.task.getTaskExecType()!=1 && this.task.getTaskExecType()!=2){
                msg += "TaskExecType error, It should be 1 or 2 .";
                throw new Exception(msg);
            }
            //URL任务
            String url = this.task.getTaskUrl();
            Map<String,Object> result = null;
            if(StringUtil.isNullOrEmpty(url)){
                msg += "The taskUrl of task is empty.";
                throw new Exception(msg);
            }
            String params = this.task.getTaskParams();
            Map<String,Object> mapParams = new HashMap<>();
            if(!StringUtil.isNullOrEmpty(params)){
                mapParams = (Map<String,Object>)JSON.parse(params);
            }
            if(this.task.getTaskExecType()==1){
                //URL GET任务
                StringBuffer sbParams =new StringBuffer();
                if(mapParams!=null && !mapParams.isEmpty()){
                    for (String k : mapParams.keySet()){
                        sbParams.append("&" + k + "=" + mapParams.get(k).toString());
                    }
                }
                StringBuffer sbUrl =new StringBuffer();
                sbUrl.append(url);
                if(!StringUtil.isNullOrEmpty(sbParams.toString())){
                    if(url.contains("?")){
                        sbUrl.append(sbParams);
                    }else{
                        sbUrl.append("?").append(StringUtil.ltrim(sbParams.toString(),"&"));
                    }
                }
                result = (Map<String,Object>)restTemplateUtil.getForObject(sbUrl.toString(),Map.class);
            }else{
                //URL POST任务
                result = (Map<String,Object>)restTemplateUtil.postFormForObject(url,mapParams,Map.class);
            }
            if(result==null || !result.containsKey("success") || result.get("success")==null || (!result.get("success").toString().equalsIgnoreCase("true") && !result.get("success").toString().equalsIgnoreCase("false"))){
                msg += "The JSON returned by the URL request should contain the success and message fields. For example: {\"success\":true|false,\"message\":\"failure|success\",...} or {\"success\":true|false,\"msg\":\"failure|success\",...}";
                throw new Exception(msg);
            }
            this.task.setTaskExecSuccess(Boolean.valueOf(result.get("success").toString()));
            if(result.containsKey("message") && result.get("message")!=null){
                this.task.setTaskExecMessage(result.get("message").toString());
            }else if(result.containsKey("msg") && result.get("msg")!=null){
                this.task.setTaskExecMessage(result.get("msg").toString());
            }else{
                this.task.setTaskExecMessage(this.task.getTaskExecSuccess()?"success":"failure");
            }
        }catch (Exception e){
            this.task.setTaskExecSuccess(false);
            this.task.setTaskExecMessage(e.getMessage());
            msg += e.getMessage();
            if(loggerBuilder!=null){
                loggerBuilder.getLogger().error(msg);
            }

        }
        super.finish();
    }

}
