package com.ihltx.utility.task.task;

import com.ihltx.utility.util.DateUtil;
import com.ihltx.utility.util.FileUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("testTask")
public class TestTask extends BaseTask {

	@Override
	public void run() {
		try {
			FileUtil.appendFile("e:/testTask.txt",
					"任务ID： " + this.task.getTaskId() + "， 任务名称： " + task.getTaskName() + "， 任务参数：" + task.getTaskParams()
							+ "，任务开始时间：" + DateUtil.long2Date(task.getTaskExecStartTime()) + "， 任务结束时间：" + DateUtil.date()
							+ "\r\n");
			this.task.setTaskExecSuccess(true);
		} catch (IOException e) {
			this.task.setTaskExecSuccess(false);
			this.task.setTaskExecMessage(e.getMessage());
			e.printStackTrace();
		}
		super.finish();
	}
}
