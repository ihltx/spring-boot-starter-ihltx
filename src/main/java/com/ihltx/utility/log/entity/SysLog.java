package com.ihltx.utility.log.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@TableName("syslogs")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SysLog {
    @TableId(value = "logId", type = IdType.AUTO)
    private Long logId;
    private Long shopId;
    private String logName;
    private String logLevel;
    private String logThreadName;
    private String logCallClassName;
    private String logCallMethodName;
    private String logFileName;
    private String logCallLine;
    private String logMessage;
    private Date logCreated;
}
