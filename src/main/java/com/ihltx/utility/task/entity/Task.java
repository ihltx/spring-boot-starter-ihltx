package com.ihltx.utility.task.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tasks")
public class Task implements Serializable {

	/**
	 * 任务ID
	 */
	@TableId(value = "taskId" , type = IdType.AUTO)
	private Integer taskId;

	/**
	 * 任务所属shopId
	 */
	private Long shopId;

	/**
	 * 任务名称
	 */
	private String taskName;

	/**
	 * 任务类型
	 * 		0	--	一次性任务，仅在系统启动时检查条件并执行一次
	 * 		1	--	周期性任务，将循环执行
	 */
	private Integer taskType;

	/**
	 *  任务执行类型
	 *  	0	--	通用任务(taskUrl属性无效)
	 *  			需要继承BaseTask抽像类并实现run方法，并在run方法实现任务业务逻辑，在run方法中可通过this.applicationContext获取spring上下文容器，从而获取容器中的bean，实现task业务
	 *  			也可在run方法中通过this.task获取当前正在执行的任务相关数据。
	 *  			强调：必须在run方法业务逻辑执行结束时调用supper.finish()方法来结束task任务，否则task任务将一直被挂起无法循环执行，直到task任务执行超时
	 *  	1	--	URL GET任务(taskClass属性无效)
	 *  			需要在taskUrl中指定要执行的url并可同时指定get参数，也可通过taskParams以json格式指定GET参数
	 *  			taskParams在该情况的格式如下：
	 *  			{"参数名1":"值","参数名2":值,...,"参数名n":"值"}
	 *  			并且无论成功与否系统将自动完成任务
	 *  	2	--	URL POST任务(taskClass属性无效)
	 *  			需要在taskUrl中指定要执行的url并可同时指定get参数，同时可通过taskParams以json格式指定提交的参数
	 *  			taskParams在该情况的格式如下：
	 *  			{"参数名1":"值","参数名2":值,...,"参数名n":"值"}
	 *  			并且无论成功与否系统将自动完成任务
	 */
	private Integer taskExecType;

	/**
	 * 通用任务执行类完全限定名称
	 *  	需要继承BaseTask抽像类并实现run方法，并在run方法实现任务业务逻辑，在run方法中可通过this.applicationContext获取spring上下文容器，从而获取容器中的bean，实现task业务
	 *  	也可在run方法中通过this.task获取当前正在执行的任务相关数据。
	 *  	强调：必须在run方法业务逻辑执行结束时调用supper.finish()方法来结束task任务，否则task任务将一直被挂起无法循环执行，直到task任务执行超时
	 */
	private String taskClass;
	/**
	 * 任务参数
	 *  	0	--	通用任务
	 *  			自定义格式
	 *  	1	--	URL GET任务
	 *  			taskParams在该情况的格式如下：
	 *  			{"参数名1":"值","参数名2":值,...,"参数名n":"值"}
	 *  	2	--	URL POST任务(taskClass属性无效)
	 *  			taskParams在该情况的格式如下：
	 *  			{"参数名1":"值","参数名2":值,...,"参数名n":"值"}
	 *  	2	--	URL POST任务(taskClass属性无效)
	 */
	private String taskParams;

	/**
	 * URL POST/GET任务时，指定url并可同时指定get参数
	 */
	private String taskUrl;


	/**
	 * 任务开始时间，将从此时间开始一次性或周期性执行任务
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date taskStartTime;

	/**
	 * 任务执行间隔时间，必须为>=0的值，单位：分钟
	 * 		0	--	将由application.yml配置文件中的 ihltx.task.checkInterval(单位：秒) 决定任务执行间隔时间
	 */
	private Integer taskInterval;

	/**
	 *
	 * 任务是否启用，仅启用的任务才会执行  0--禁用  1--启用
	 */
	private Boolean taskEnabled;

	/**
	 *  任务状态：   0--未开始    1--进行中     2--已完成
	 */
	private Integer taskStatus;

	/**
	 * 任务进度，0表示未开始    100表示已完成： 目前未使用
	 */
	private Integer taskProgress;

	/**
	 * 任务是否删除，仅未删除的任务才会执行：0--未删除  1--删除
	 */
	private Integer taskDeleted;

	/**
	 * 任务创建时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date taskCreated;

	/**
	 * 任务修改时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date taskUpdated;
	/**
	 * 是否数据库任务
	 * 		0	--	application.yml配置文件中的配置任务
	 * 		1	--	数据库任务
	 */
	private Boolean taskIsDataBase;

	/**
	 * 任务上次开始执行的时间戳，单位：毫秒
	 */
	private Long taskExecStartTime;

	/**
	 * 任务执行结果  0--失败  1--成功
	 */
	private Boolean taskExecSuccess;

	/**
	 * 任务执行结果信息
	 */
	private String taskExecMessage;

}
