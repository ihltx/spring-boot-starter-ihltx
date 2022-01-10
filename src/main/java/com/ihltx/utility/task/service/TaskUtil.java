package com.ihltx.utility.task.service;

import com.ihltx.utility.task.entity.Task;

import java.util.List;

public interface TaskUtil extends Runnable {

	/**
	 * 开始 task 任务
	 */
	void start();

	/**
	 * 获取所有Task 任务
	 * @return
	 */
	List<Task> getTasks();

	/**
	 * 修改任务状态，如果任务已经开始执行但未结束，则不启动任务执行，直到任务已经执行完成。
	 * 任务状态：  0--未开始    1--进行中     2--已完成
	 * @param task     任务对象
	 * @return  true--成功  false--失败
	 */
	Boolean finishTask(Task task);
}
