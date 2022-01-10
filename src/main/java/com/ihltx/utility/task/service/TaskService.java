package com.ihltx.utility.task.service;

import com.ihltx.utility.task.entity.Task;
import com.ihltx.utility.task.entity.TaskLog;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 应用TaskService服务接口
 * @author Administrator
 *
 */
public interface TaskService {


	/**
	 * 添加任务
	 * @param task	任务
	 * @return Boolean
	 * 		true 	--	success
	 * 		false	--	failure
	 */
	Boolean  addTask(Task task);

	/**
	 * 基于用户自定义业务逻辑返回所有任务列表
	 * @return List<Task>
	 */
	List<Task> getTasks();
	
	/**
	 * 修改任务状态，如果任务已经开始执行但未结束，则不启动任务执行，直到任务已经执行完成。
	 * @param task     任务
	 * @return  true--成功  false--失败
	 */
	Boolean updateTask(Task task);

	
	/**
	 * 获取指定任务最近一次执行的时间，单位：秒
	 * @param taskId   任务ID
	 * @return Long    0----表示不存在执行日志 
	 */
	Long getLastLogTime(int taskId);
	
	
	/**
	 * 针对任务添加执行日志
	 * @param taskLog          	任务日志
	 * @return true--成功   false--失败
	 */
	Boolean addLogs(TaskLog taskLog);


	/**
	 * 完成任务，将添加日志及修改状态
	 * @param task          	任务
	 * @return true--成功   false--失败
	 */
	Boolean finish(Task task) throws Exception;


	/**
	 * 获取指定shopId的分页任务列表
	 * @param page		分页信息
	 * @param shopId	shopId
	 * @return IPage<Task>
	 */
	IPage<Task> getTasks(IPage<Task> page, Long shopId);



	/**
	 * 根据qw获取分页任务列表
	 * @param page		分页信息
	 * @param qw		查询参数包装器
	 * @return IPage<Task>
	 */
	IPage<Task> getTasks(IPage<Task> page, QueryWrapper<Task> qw);


	/**
	 * 获取指定shopId的分页日志列表
	 * @param page		分页信息
	 * @param shopId	shopId
	 * @return IPage<Task>
	 */
	IPage<TaskLog> getTaskLogs(IPage<TaskLog> page, Long shopId);

	/**
	 * 根据qw获取分页日志列表
	 * @param page		分页信息
	 * @param qw		查询参数包装器
	 * @return IPage<Task>
	 */
	IPage<TaskLog> getTaskLogs(IPage<TaskLog> page, QueryWrapper<TaskLog> qw);

	/**
	 * 删除shopId的所有任务
	 * @param shopId	shopId
	 * @return Boolean
	 * 		true 	--	success
	 * 		false	--	failure
	 */
	Boolean  deleteTask(Long shopId);

	/**
	 * 删除shopId的所有任务
	 * @param qw	条件包装器
	 * @return Boolean
	 * 		true 	--	success
	 * 		false	--	failure
	 */
	Boolean  deleteTask(QueryWrapper<Task> qw);



	/**
	 * 删除shopId的所有任务日志
	 * @param shopId	shopId
	 * @return Boolean
	 * 		true 	--	success
	 * 		false	--	failure
	 */
	Boolean  deleteTaskLogs(Long shopId);

	/**
	 * 删除shopId的所有任务日志
	 * @param qw	条件包装器
	 * @return Boolean
	 * 		true 	--	success
	 * 		false	--	failure
	 */
	Boolean  deleteTaskLogs(QueryWrapper<TaskLog> qw);

	/**
	 * 获取任务
	 * @param taskId
	 * @return Task
	 */
	Task getTask(Integer taskId);

	/**
	 * 获取任务日志
	 * @param taskLogId
	 * @return TaskLog
	 */
	TaskLog getTaskLog(Long taskLogId);
}
