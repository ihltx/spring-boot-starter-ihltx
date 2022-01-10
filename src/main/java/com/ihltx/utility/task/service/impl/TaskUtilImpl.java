package com.ihltx.utility.task.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.ihltx.utility.log.service.LoggerBuilder;
import com.ihltx.utility.task.config.TaskConfig;
import com.ihltx.utility.task.entity.Task;
import com.ihltx.utility.task.service.TaskService;
import com.ihltx.utility.task.service.TaskUtil;
import com.ihltx.utility.task.task.BaseTask;
import com.ihltx.utility.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@SuppressWarnings("all")
@Component
public class TaskUtilImpl implements TaskUtil {

	@Autowired
	private LoggerBuilder loggerBuilder;

	@Autowired
	private TaskConfig taskConfig;

	@Autowired(required = false)
	private TaskService taskService;

	@Autowired
	public ApplicationContext applicationContext;

	private static Map<Integer, List<Long>> executeLogs = new HashMap<Integer, List<Long>>();
	private static ConcurrentHashMap<Integer, Long[]> taskStatus =new ConcurrentHashMap<>();
	

	/**
	 * 后台任务是否正在运行
	 */
	private static Boolean isRunning = false;

	public TaskUtilImpl() {

	}

	public void start() {
		if (!this.taskConfig.getEnabled()) {
			loggerBuilder.getLogger().debug("Task service has been disabled");
			return;
		}
		if (isRunning) {
			loggerBuilder.getLogger().debug("Task service is running");
			return;
		}
		isRunning = true;
		Thread th = new Thread(this);
		th.start();
	}

	public List<Task> getTasks() {
		List<Task> list = this.taskConfig.getList();
		List<Task> list1 = null;
		List<Task> newlist = new ArrayList<Task>();
		if (this.taskConfig.getEnableDataSource() && taskService != null) {
			list1 = taskService.getTasks();
		}
		if (list != null) {
			newlist.addAll(list);
		}
		if (list1 != null) {
			newlist.addAll(list1);
		}
		return newlist;
	}

	/**
	 * 根据任务时间以及间隔判断是否应该执行任务
	 * 
	 * @param task    任务对象
	 * @return ture--执行 false--不执行
	 * @throws ParseException
	 */
	private Boolean computeTaskTime(Task task) throws ParseException {
		Long lastTime = task.getTaskStartTime().getTime()/1000;
		Boolean isDid = false;
		List<Long> logs = null;
		if (task.getTaskIsDataBase()!=null && task.getTaskIsDataBase()) {
			if(this.taskConfig.getEnableDataSource() && taskService!=null) {
				Long lastLogTime = taskService.getLastLogTime(task.getTaskId());
				if (lastLogTime > 0) {
					isDid = true;
					lastTime = lastLogTime;
				}
			}
		} else {
			if (executeLogs.containsKey(task.getTaskId())) {
				logs = executeLogs.get(task.getTaskId());
			} else {
				logs = new ArrayList<Long>();
			}
			if (!logs.isEmpty()) {
				lastTime = logs.get(logs.size() - 1);
				isDid = true;
			}
		}
		Long currSeconds = DateUtil.getTime();
		if (currSeconds - lastTime - task.getTaskInterval() * 60 >= 0) {
			if (task.getTaskType() == 0) {
				// 一次性任务
				if (!isDid) {
					// 如果从来没有执行过，则执行
					return true;
				}
			} else if (task.getTaskType() == 1) {
				// 周期性任务
				return true;
			}
		}

		return false;
	}

	/**
	 * 死循环运行task
	 */
	public void run() {
		while (true) {
			try {
				Thread.sleep(this.taskConfig.getCheckInterval() * 1000);
				List<Task> tasks = getTasks();
				if (tasks == null || tasks.isEmpty()) {
					continue;
				}

				for (Task task : tasks) {
					if(task.getTaskIsDataBase()==null){
						task.setTaskIsDataBase(false);
					}
					if (task.getTaskEnabled() && computeTaskTime(task)) {
						long status= 0;
						if (task.getTaskIsDataBase()!=null && task.getTaskIsDataBase()) {
							status = task.getTaskStatus();
						}else {
							if(taskStatus.containsKey(task.getTaskId())) {
								Long[] datas = taskStatus.get(task.getTaskId());
								status = datas[0];
								task.setTaskExecStartTime(datas[1]);
							}
						}
						task.setTaskStatus(Integer.valueOf(String.valueOf(status)));

						
						if(status==1) {
							//任务正在执行, 检查并清理超时任务
							if(DateUtil.getTimeMillis() -task.getTaskExecStartTime()> (this.taskConfig.getTimeout() * 1000)){
								this.finishTask(task);
							}
							continue;
						}

						task.setTaskExecStartTime(DateUtil.getTimeMillis());
						if (task.getTaskIsDataBase()!=null && task.getTaskIsDataBase()) {
							if(this.taskConfig.getEnableDataSource() && taskService!=null) {
								Task updateTask = new Task();
								updateTask.setTaskId(task.getTaskId());
								updateTask.setTaskStatus(1);
								updateTask.setTaskExecStartTime(task.getTaskExecStartTime());
								taskService.updateTask(updateTask);
							}
						} else {
							Long[] datas = new Long[]{1L , task.getTaskExecStartTime()};
							taskStatus.put(task.getTaskId(), datas);
						}
						//任务执行类型，
						// 	0--通用任务(需要继承BaseTask类并通过taskClass指定执行类，实现run方法并在run方法中调用supper.finish()完成任务)
						// 	1--URL GET任务(需要直接在taskUrl中指定要执行的url及参数)
						// 	2--URL POST任务(需要在taskUrl中指定要执行的url，并通过taskParams以json格式指定提交的参数)
						String execTaskClass ="";
						switch (task.getTaskExecType()){
							case 0:
								//通用任务
								execTaskClass =  task.getTaskClass();
								break;
							case 1:
								//	1--URL GET任务
							default:
								//	2--URL POST任务
								execTaskClass = "com.ihltx.utility.task.task.UrlTask";
								break;
						}

						Class clazz = Class.forName(execTaskClass);
						Runnable obj = null;
						try{
							obj = (Runnable) this.applicationContext.getBean(clazz);
						}catch (Exception err){
						}
						if(obj==null){
							obj = (Runnable) clazz.getConstructor().newInstance();
						}

						if(obj instanceof BaseTask){
							((BaseTask) obj).setTask(task);
							((BaseTask) obj).setTaskUtil(this);
							((BaseTask) obj).setApplicationContext(this.applicationContext);
						}
						Thread th1 = new Thread(obj);
						th1.start();
						if(task.getTaskExecType()==0){
							loggerBuilder.getLogger().debug("Task(class=" + task.getTaskClass() + " , ID=" + task.getTaskId() + " , name=" + task.getTaskName() + " , type=" + (task.getTaskIsDataBase()?"Database":"Configuration") + ") started at " + DateUtil.date());
						}else{
							loggerBuilder.getLogger().debug("Task(url=" + task.getTaskUrl() + " , ID=" + task.getTaskId() + " , name=" + task.getTaskName() + " , type=" + (task.getTaskIsDataBase()?"Database":"Configuration") + ") started at " + DateUtil.date());
						}
					}
				}
			} catch (InterruptedException | ParseException | ClassNotFoundException | InstantiationException
					| IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 修改任务状态，如果任务已经开始执行但未结束，则不启动任务执行，直到任务已经执行完成。
	 * 任务状态：  0--未开始    1--进行中     2--已完成
	 * @param task     任务对象
	 * @return  true--成功  false--失败
	 */
	public Boolean finishTask(Task task) {
		synchronized (task) {
			if(task.getTaskIsDataBase()!=null && task.getTaskIsDataBase()) {
				if(this.taskConfig.getEnableDataSource() && taskService!=null) {
					try {
						taskService.finish(task);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}else {
				List<Long> logs = null;
				if (executeLogs.containsKey(task.getTaskId())) {
					logs = executeLogs.get(task.getTaskId());
				} else {
					logs = new ArrayList<Long>();
				}
				logs.add(DateUtil.getTime());
				executeLogs.put(task.getTaskId(), logs);
				Long[] datas = new Long[]{2L , 0L};
				taskStatus.put(task.getTaskId(), datas);

				if(this.taskConfig.getEnableDataSource() && taskService!=null) {
					try {
						taskService.finish(task);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return true;
	}
}
