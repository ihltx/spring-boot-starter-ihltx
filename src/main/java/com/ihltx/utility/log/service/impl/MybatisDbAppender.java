package com.ihltx.utility.log.service.impl;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.ihltx.utility.log.entity.SysLog;
import com.ihltx.utility.log.service.SysLogService;
import com.ihltx.utility.util.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Date;

@SuppressWarnings("all")
@Component
@ConditionalOnProperty(prefix = "ihltx.log" , name = "enableMybatisDbAppender" , havingValue = "true")
public class MybatisDbAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    @Autowired(required = false)
    private SysLogService  sysLogService;

    Layout<ILoggingEvent> layout;
    public Layout<ILoggingEvent> getLayout() {
        return layout;
    }

    public void setLayout(Layout<ILoggingEvent> layout) {
        this.layout = layout;
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (eventObject == null || !isStarted()){
            return;
        }
        String formatMessage = layout.doLayout(eventObject);
        SysLog log = new SysLog();
        log.setShopId(WebUtil.getShopId());
        log.setLogName(eventObject.getLoggerName());
        log.setLogLevel(eventObject.getLevel().levelStr);
        log.setLogThreadName(eventObject.getThreadName());
        if(eventObject.getCallerData()!=null && eventObject.getCallerData().length>0){
            log.setLogCallClassName(eventObject.getCallerData()[0].getClassName());
            log.setLogCallMethodName(eventObject.getCallerData()[0].getMethodName());
            log.setLogFileName(eventObject.getCallerData()[0].getFileName());
            log.setLogCallLine(String.valueOf(eventObject.getCallerData()[0].getLineNumber()));
        }
        log.setLogMessage(formatMessage);
        log.setLogCreated(new Date());
        if(sysLogService!=null){
            sysLogService.add(log);
        }
    }
}
