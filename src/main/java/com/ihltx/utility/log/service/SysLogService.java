package com.ihltx.utility.log.service;

import com.ihltx.utility.log.entity.SysLog;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
public interface SysLogService {

    /**
     * 添加系统日志
     * @param log   日志
     * @return Boolean
     * 		true 	--	success
     * 		false	--	failure
     */
    Boolean add(SysLog log);

    /**
     * 删除shopId的系统日志
     * @param shopId	shopId
     * @return Boolean
     * 		true 	--	success
     * 		false	--	failure
     */
    Boolean  delete(Long shopId);

    /**
     * 根据qw删除系统日志
     * @param qw	条件包装器
     * @return Boolean
     * 		true 	--	success
     * 		false	--	failure
     */
    Boolean  delete(QueryWrapper<SysLog> qw);

    /**
     * 获取系统日志
     * @param logId
     * @return
     */
    SysLog getOne(Long logId);

}
