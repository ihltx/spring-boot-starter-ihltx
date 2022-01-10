package com.ihltx.utility.log.mapper;

import com.ihltx.utility.log.entity.SysLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;

public interface SysLogMapper extends BaseMapper<SysLog> {

    @Insert("DROP TABLE `syslogs`")
    public void dropSyslogsTable();

    @Insert("CREATE TABLE `syslogs` (`logId`  bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID' ,`shopId`  bigint(20) NOT NULL COMMENT '所属ShopId' ,`logName`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '日志名称' ,`logLevel`  varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '日志等级' ,`logThreadName`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '线程名称' ,`logCallClassName`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类名' ,`logCallMethodName`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '方法名称' ,`logFileName`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件名' ,`logCallLine`  varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '行号' ,`logMessage`  text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '日志内容' ,`logCreated`  datetime NOT NULL COMMENT '日志创建时间' ,PRIMARY KEY (`logId`)) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci AUTO_INCREMENT=1 ROW_FORMAT=DYNAMIC")
    public void createSyslogsTable();

    @Insert("TRUNCATE TABLE `syslogs`")
    public void truncateSyslogsTable();

}
