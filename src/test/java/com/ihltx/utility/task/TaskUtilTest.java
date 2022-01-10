package com.ihltx.utility.task;

import com.ihltx.store.SpringBootStarterIhltxApplication;
import com.ihltx.utility.task.entity.Task;
import com.ihltx.utility.task.entity.TaskLog;
import com.ihltx.utility.task.service.TaskService;
import com.ihltx.utility.task.service.impl.TaskUtilImpl;
import com.ihltx.utility.util.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SuppressWarnings("all")
@SpringBootTest(classes = {SpringBootStarterIhltxApplication.class})
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class TaskUtilTest {

	@Autowired
	private TaskUtilImpl taskUtil;


	@Autowired
	private TaskService taskService;

	@Test
	public void test_10_getTasks() {
		System.out.println(taskUtil.getTasks());
		
	}


	@Test
	public void test_20_start() {
		taskUtil.start();

	}

	@Test
	public void test_30_finish() {
		List<Task> tasks =  taskUtil.getTasks();
		for (Task task : tasks){
			task.setTaskExecStartTime(DateUtil.getTimeMillis());
			taskUtil.finishTask(task);
		}
	}


	@Test
	public void test_40_getTasksPage() {
		int pageSize  =2;
		Page<Task> page = new Page<>(1,pageSize);
		IPage<Task> tasks =  taskService.getTasks(page , 0L);
		System.out.println("current pageIndex:" + tasks.getCurrent());
		System.out.println("total pages:" + tasks.getPages());
		System.out.println("page size:" + tasks.getSize());
		System.out.println(tasks.getRecords());

		page.setCurrent(2);
		tasks =  taskService.getTasks(page , 0L);
		System.out.println("current pageIndex:" + tasks.getCurrent());
		System.out.println("total pages:" + tasks.getPages());
		System.out.println("page size:" + tasks.getSize());
		System.out.println(tasks.getRecords());

		page.setCurrent(3);
		tasks =  taskService.getTasks(page , 0L);
		System.out.println("current pageIndex:" + tasks.getCurrent());
		System.out.println("total pages:" + tasks.getPages());
		System.out.println("page size:" + tasks.getSize());
		System.out.println(tasks.getRecords());
	}

	@Test
	public void test_40_getTasksPageQw() {
		int pageSize = 2;
		Page<Task> page = new Page<>(1,pageSize);
		QueryWrapper<Task> qw =new QueryWrapper<>();
		qw.select("taskId" , "shopId" , "taskName" , "taskType");
		qw.eq(true, "shopId" , 0L);
		IPage<Task> tasks =  taskService.getTasks(page , qw);
		System.out.println("current pageIndex:" + tasks.getCurrent());
		System.out.println("total pages:" + tasks.getPages());
		System.out.println("page size:" + tasks.getSize());
		System.out.println(tasks.getRecords());

		page.setCurrent(2);
		tasks =  taskService.getTasks(page , qw);
		System.out.println("current pageIndex:" + tasks.getCurrent());
		System.out.println("total pages:" + tasks.getPages());
		System.out.println("page size:" + tasks.getSize());
		System.out.println(tasks.getRecords());

		page.setCurrent(3);
		tasks =  taskService.getTasks(page , qw);
		System.out.println("current pageIndex:" + tasks.getCurrent());
		System.out.println("total pages:" + tasks.getPages());
		System.out.println("page size:" + tasks.getSize());
		System.out.println(tasks.getRecords());
	}


	@Test
	public void test_40_getTaskLogsPage() {
		int pageSize  =10;
		Page<TaskLog> page = new Page<>(1,pageSize);
		IPage<TaskLog> tasks =  taskService.getTaskLogs(page,0L);
		System.out.println("current pageIndex:" + tasks.getCurrent());
		System.out.println("total pages:" + tasks.getPages());
		System.out.println("page size:" + tasks.getSize());
		System.out.println(tasks.getRecords());

		page = new Page<>(2,pageSize);
		tasks =  taskService.getTaskLogs(page , 0L);
		System.out.println("current pageIndex:" + tasks.getCurrent());
		System.out.println("total pages:" + tasks.getPages());
		System.out.println("page size:" + tasks.getSize());
		System.out.println(tasks.getRecords());

		page = new Page<>(3,pageSize);
		tasks =  taskService.getTaskLogs(page , 0L);
		System.out.println("current pageIndex:" + tasks.getCurrent());
		System.out.println("total pages:" + tasks.getPages());
		System.out.println("page size:" + tasks.getSize());
		System.out.println(tasks.getRecords());
	}

	@Test
	public void test_40_getTaskLogsPageQw() {
		int pageSize = 10;
		Page<TaskLog> page = new Page<>(1,pageSize);
		QueryWrapper<TaskLog> qw =new QueryWrapper<>();
		qw.select("taskLogId" , "shopId" , "taskId" , "taskIsDataBase");
		qw.eq(true, "shopId" , 0L);
		IPage<TaskLog> tasks =  taskService.getTaskLogs(page , qw);
		System.out.println("current pageIndex:" + tasks.getCurrent());
		System.out.println("total pages:" + tasks.getPages());
		System.out.println("page size:" + tasks.getSize());
		System.out.println(tasks.getRecords());

		page.setCurrent(2);
		tasks =  taskService.getTaskLogs(page , qw);
		System.out.println("current pageIndex:" + tasks.getCurrent());
		System.out.println("total pages:" + tasks.getPages());
		System.out.println("page size:" + tasks.getSize());
		System.out.println(tasks.getRecords());

		page.setCurrent(3);
		tasks =  taskService.getTaskLogs(page , qw);
		System.out.println("current pageIndex:" + tasks.getCurrent());
		System.out.println("total pages:" + tasks.getPages());
		System.out.println("page size:" + tasks.getSize());
		System.out.println(tasks.getRecords());
	}
}
