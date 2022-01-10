package com.ihltx.utility.task.service.impl;

import com.ihltx.utility.task.config.TaskConfig;
import com.ihltx.utility.task.entity.Task;
import com.ihltx.utility.task.entity.TaskLog;
import com.ihltx.utility.task.mapper.TaskLogMapper;
import com.ihltx.utility.task.mapper.TaskMapper;
import com.ihltx.utility.task.service.TaskService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@SuppressWarnings("all")
@Service
@ConditionalOnProperty(prefix = "ihltx.task", name = "enableDataSource" , havingValue = "true")
public class TaskServiceImpl implements TaskService {

	@Autowired
	private TaskConfig taskConfig;

	@Autowired
	private TaskMapper taskMapper;

	@Autowired
	private TaskLogMapper taskLogMapper;


	@Override
	@DS("system")
	@Transactional
	public Boolean  addTask(Task task){
		return taskMapper.insert(task)>0;
	}


	@Override
	@DS("system")
	@Transactional
	public List<Task> getTasks() {
		List<Task> list = null;
		if(!this.taskConfig.getEnableDataSource()){
			return list;
		}
		QueryWrapper<Task> qw = new QueryWrapper<>();
		qw
				.eq(true , "taskDeleted" , 0)
				.eq(true , "taskEnabled" ,1)
				.orderByAsc("taskCreated");
		return taskMapper.selectList(qw);
	}

	@Override
	@DS("system")
	@Transactional
	public Boolean updateTask(Task task) {
		return taskMapper.updateById(task) >=0;
	}

	@Override
	@DS("system")
	@Transactional
	public Long getLastLogTime(int taskId) {
		TaskLog taskLog = null;
		Long lastLogTime = 0L;
		if(!this.taskConfig.getEnableDataSource()){
			return lastLogTime;
		}

		QueryWrapper<TaskLog> qw = new QueryWrapper<>();
		qw
				.select("taskLogCreated")
				.eq(true , "taskId" , taskId)
				.orderByDesc("taskLogCreated")
				.last("LIMIT 0,1");
		taskLog = taskLogMapper.selectOne(qw);
		if(taskLog!=null){
			lastLogTime = taskLog.getTaskLogCreated().getTime()/1000;
		}
		return lastLogTime;
	}

	@Override
	@DS("system")
	@Transactional
	public Boolean addLogs(TaskLog taskLog){
		return taskLogMapper.insert(taskLog)>0;
	}

	@Override
	@DS("system")
	@Transactional
	public Boolean finish(Task task) throws Exception {
		TaskLog taskLog =new TaskLog();
		taskLog.setShopId(task.getShopId());
		taskLog.setTaskId(task.getTaskId());
		taskLog.setTaskIsDataBase(task.getTaskIsDataBase());
		taskLog.setTaskLogStartTime(task.getTaskExecStartTime());
		Long taskLogDuration = (new Date()).getTime() - task.getTaskExecStartTime();
		taskLog.setTaskLogDuration(taskLogDuration.intValue());
		taskLog.setTaskLogCreated(new Date());
		taskLog.setTaskLogExecSuccess(task.getTaskExecSuccess());
		taskLog.setTaskLogExecMessage(task.getTaskExecMessage());
		Boolean rs = this.addLogs(taskLog);
		if(!rs){
			throw  new Exception("Task finish failure, add task log failure.");
		}
		Task updateTask = new Task();
		updateTask.setTaskId(task.getTaskId());
		updateTask.setTaskExecStartTime(0L);
		updateTask.setTaskStatus(2);
		updateTask.setTaskExecSuccess(task.getTaskExecSuccess());
		updateTask.setTaskExecMessage(task.getTaskExecMessage());
		rs =this.updateTask(updateTask);
		if(!rs){
			throw  new Exception("Task finish failure, update task status failure.");
		}
		return true;
	}


	@Override
	@DS("system")
	@Transactional
	public IPage<Task> getTasks(IPage<Task> page, Long shopId){
		QueryWrapper<Task> qw =new QueryWrapper<>();
		qw.eq(true, "shopId" , shopId);
		return taskMapper.selectPage(page , qw);
	}


	@Override
	@DS("system")
	@Transactional
	public IPage<Task> getTasks(IPage<Task> page, QueryWrapper<Task> qw){
		return taskMapper.selectPage(page , qw);
	}

	@Override
	@DS("system")
	@Transactional
	public IPage<TaskLog> getTaskLogs(IPage<TaskLog> page, Long shopId){
		QueryWrapper<TaskLog> qw =new QueryWrapper<>();
		qw.eq(true, "shopId" , shopId);
		return taskLogMapper.selectPage(page , qw);
	}

	@Override
	@DS("system")
	@Transactional
	public IPage<TaskLog> getTaskLogs(IPage<TaskLog> page, QueryWrapper<TaskLog> qw){
		return taskLogMapper.selectPage(page , qw);
	}


	@Override
	@DS("system")
	@Transactional
	public Boolean  deleteTask(Long shopId){
		QueryWrapper<Task> qw =new QueryWrapper<>();
		qw.eq(true, "shopId" , shopId);
		return taskMapper.delete(qw)>=0;
	}

	@Override
	@DS("system")
	@Transactional
	public Boolean  deleteTask(QueryWrapper<Task> qw){
		return taskMapper.delete(qw)>=0;
	}


	@Override
	@DS("system")
	@Transactional
	public Boolean  deleteTaskLogs(Long shopId){
		QueryWrapper<TaskLog> qw =new QueryWrapper<>();
		qw.eq(true, "shopId" , shopId);
		return taskLogMapper.delete(qw)>=0;
	}

	@Override
	@DS("system")
	@Transactional
	public Boolean  deleteTaskLogs(QueryWrapper<TaskLog> qw){
		return taskLogMapper.delete(qw)>=0;
	}

	@Override
	@DS("system")
	@Transactional
	public Task getTask(Integer taskId) {
		QueryWrapper<Task> qw =new QueryWrapper<>();
		qw.eq(true, "taskId" , taskId);
		qw.last("LIMIT 0,1");
		return taskMapper.selectOne(qw);
	}

	@Override
	@DS("system")
	@Transactional
	public TaskLog getTaskLog(Long taskLogId) {
		QueryWrapper<TaskLog> qw =new QueryWrapper<>();
		qw.eq(true, "taskLogId" , taskLogId);
		qw.last("LIMIT 0,1");
		return taskLogMapper.selectOne(qw);
	}


}
