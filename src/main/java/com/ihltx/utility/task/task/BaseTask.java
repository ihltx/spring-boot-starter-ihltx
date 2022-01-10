package com.ihltx.utility.task.task;

import com.ihltx.utility.log.service.LoggerBuilder;
import com.ihltx.utility.task.entity.Task;
import com.ihltx.utility.task.service.impl.TaskUtilImpl;
import com.ihltx.utility.util.DateUtil;
import org.springframework.context.ApplicationContext;


public abstract class BaseTask  implements Runnable{
	protected ApplicationContext applicationContext;

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}


	protected Task task = null;
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	protected TaskUtilImpl taskUtil = null;
	public TaskUtilImpl getTaskUtil() {
		return taskUtil;
	}

	public void setTaskUtil(TaskUtilImpl taskUtil) {
		this.taskUtil = taskUtil;
	}



	public abstract void run();
	
	public synchronized void finish() {
		LoggerBuilder loggerBuilder = applicationContext.getBean(LoggerBuilder.class);
		if(this.taskUtil!=null && this.task!=null) {
			String msg = " , ID=" + this.task.getTaskId() + " , name=" + this.task.getTaskName() + " , type=" + (task.getTaskIsDataBase()?"Database":"Configuration") + ") was completed at " + DateUtil.date();
			if(this.task.getTaskExecType()!=0){
				msg = "Task(url=" + this.task.getTaskUrl() + msg;
			}else{
				msg = "Task(calss=" + this.task.getTaskClass() + msg;
			}
			if(loggerBuilder!=null){
				loggerBuilder.getParentLogger().debug(msg);
			}
			this.taskUtil.finishTask(this.task);
		}
	}
}
