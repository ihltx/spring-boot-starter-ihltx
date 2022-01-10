package com.ihltx.utility.task.config;


import java.util.ArrayList;
import java.util.List;

import com.ihltx.utility.task.entity.Task;
import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "ihltx.task")
public class TaskConfig {
	/**
	 * 是否启用task
	 */
	private Boolean enabled = false;

	/**
	 * 任务检查间隔时间，单位：秒
	 */
	private int checkInterval = 1;

	/**
	 * 指定任务完成超时时间，单位：秒，如果在该时间内未完成任务，将自动修改为已完成
	 */
	private  int timeout = 60;

	/**
	 * task是否使用数据库数据源
	 */
	private Boolean enableDataSource = false;

	/**
	 * task使用数据库数据源时使用的mybatis动态数据源名称
	 */
	private String mybatisDynamicDataSourceName = "system";

	/**
	 * 配置中定义的Task
	 */
	private List<Task> list =new ArrayList<Task>();

}
