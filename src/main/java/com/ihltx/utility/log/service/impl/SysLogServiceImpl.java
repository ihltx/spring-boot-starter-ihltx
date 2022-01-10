package com.ihltx.utility.log.service.impl;

import com.ihltx.utility.log.entity.SysLog;
import com.ihltx.utility.log.mapper.SysLogMapper;
import com.ihltx.utility.log.service.SysLogService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("all")
@Service
@ConditionalOnProperty(prefix = "ihltx.log" , name = "enableMybatisDbAppender" , havingValue = "true")
public class SysLogServiceImpl implements SysLogService {

    @Autowired
    private SysLogMapper sysLogMapper;

    @Override
    @DS("system")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Boolean add(SysLog log) {
        return sysLogMapper.insert(log)>0;
    }

    @Override
    @DS("system")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Boolean delete(Long shopId) {
        QueryWrapper<SysLog> qw =new QueryWrapper<>();
        qw.eq(true , "shopId" , shopId);
        return sysLogMapper.delete(qw)>=0;
    }

    @Override
    @DS("system")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Boolean delete(QueryWrapper<SysLog> qw) {
        return sysLogMapper.delete(qw)>=0;
    }


    @Override
    @DS("system")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SysLog getOne(Long logId) {
        QueryWrapper<SysLog> qw =new QueryWrapper<>();
        qw.eq(true , "logId" , logId);
        qw.last("LIMIT 0,1");
        return sysLogMapper.selectOne(qw);
    }
}
