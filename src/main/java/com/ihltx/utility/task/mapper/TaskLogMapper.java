package com.ihltx.utility.task.mapper;

import com.ihltx.utility.task.entity.TaskLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;

public interface TaskLogMapper extends BaseMapper<TaskLog> {


    @Insert("DROP TABLE `tasklogs`")
    public void dropTaskLogsTable();

    @Insert("CREATE TABLE `tasklogs` (`taskLogId`  bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务日志ID' ,`shopId`  bigint(20) NOT NULL DEFAULT 0 COMMENT '所属shopid' ,`taskId`  int(11) NOT NULL COMMENT '任务ID' ,`taskIsDataBase`  tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否数据库任务' ,`taskLogStartTime`  bigint(20) NOT NULL DEFAULT 0 COMMENT '任务开始执行时间，时间戳' ,`taskLogDuration`  int(11) NOT NULL COMMENT '任务持续时间，单位：毫秒' ,`taskLogExecSuccess`  tinyint(4) NOT NULL DEFAULT 0 COMMENT '任务执行结果  0--失败  1--成功' ,`taskLogExecMessage`  text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '任务执行结果信息' ,`taskLogCreated`  datetime NOT NULL COMMENT '任务执行时间' ,PRIMARY KEY (`taskLogId`)) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='任务日志表' AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC")
    public void createTaskLogsTable();

    @Insert("TRUNCATE TABLE `tasklogs`")
    public void truncateTaskLogsTable();

}
