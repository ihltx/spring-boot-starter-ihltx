package com.ihltx.utility.task.mapper;

import com.ihltx.utility.task.entity.Task;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;

public interface TaskMapper extends BaseMapper<Task> {

    @Insert("DROP TABLE `tasks`")
    public void dropTasksTable();

    @Insert("CREATE TABLE `tasks` (`taskId`  int(11) NOT NULL AUTO_INCREMENT ,`shopId`  bigint(20) NOT NULL DEFAULT 0 COMMENT '所属ShopId' ,`taskName`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务名称' ,`taskType`  tinyint(4) NOT NULL DEFAULT 0 COMMENT '任务类型   0--一次性任务    1--周期任务' ,`taskExecType`  tinyint(4) NOT NULL DEFAULT 0 COMMENT '任务执行类型，0--通用任务(需要继承BaseTask类并通过taskClass指定执行类，实现run方法并在run方法中调用supper.finish()完成任务)  1--URL GET任务(需要直接在taskUrl中指定要执行的url及参数)  2--URL POST任务(需要在taskUrl中指定要执行的url，并通过taskParams以json格式指定提交的参数)' ,`taskClass`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务执行类完全限定名称，需要继承BaseTask抽像类并实现run方法' ,`taskParams`  text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '任务参数，如果为通用任务自定义格式，如果为URL POST/GET任务则以json格式指定参数' ,`taskUrl`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'URL任务时，指定url' ,`taskStartTime`  datetime NOT NULL COMMENT '任务开始时间，从此任务开始执行任务' ,`taskInterval`  int(11) NOT NULL DEFAULT 0 COMMENT '任务间隔时间，单位：分钟   0--仅一次性任务可用' ,`taskStatus`  tinyint(4) NOT NULL DEFAULT 0 COMMENT '任务状态   0--未开始    1--进行中     2--已完成' ,`taskProgress`  tinyint(4) NOT NULL DEFAULT 0 COMMENT '任务进度，0表示未开始    100表示已完成： 目前未使用' ,`taskEnabled`  tinyint(4) NOT NULL DEFAULT 1 COMMENT '任务是否启用，仅启用的任务才会执行  0--禁用  1--启用' ,`taskDeleted`  tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否删除  0--否  1--是' ,`taskIsDataBase`  tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否数据库任务' ,`taskExecStartTime`  bigint(20) NOT NULL DEFAULT 0 COMMENT '任务执行开始时间' ,`taskExecSuccess`  tinyint(4) NOT NULL DEFAULT 0 COMMENT '任务执行结果  0--失败  1--成功' ,`taskExecMessage`  text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '任务执行结果信息' ,`taskCreated`  datetime NOT NULL COMMENT '任务创建时间' ,`taskUpdated`  datetime NOT NULL ,PRIMARY KEY (`taskId`))ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='后台系统任务表' AUTO_INCREMENT=10000 ROW_FORMAT=DYNAMIC")
    public void createTasksTable();

    @Insert("TRUNCATE TABLE `tasks`")
    public void truncateTasksTable();
}
