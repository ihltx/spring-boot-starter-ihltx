package com.ihltx.utility.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tasklogs")
public class TaskLog implements Serializable {

    /**
     * ID
     */
    @TableId(value = "taskLogId" , type = IdType.AUTO)
    private Long taskLogId;

    /**
     * 所属shopId
     */
    private Long shopId;

    /**
     * 任务ID
     */
    private Integer taskId;


    /**
     * 是否数据库任务
     * 		0	--	application.yml配置文件中的配置任务
     * 		1	--	数据库任务
     */
    private Boolean taskIsDataBase;


    /**
     * 任务开始执行时间，时间戳，单位：毫秒
     */
    private Long taskLogStartTime;

    /**
     * 任务执行持续时间，单位：毫秒
     */
    private Integer taskLogDuration;

    /**
     * 任务执行结果  0--失败  1--成功
     */
    private Boolean taskLogExecSuccess;

    /**
     * 任务执行结果信息
     */
    private String taskLogExecMessage;

    /**
     * 日志创建时间
     */
    private Date taskLogCreated;

}
