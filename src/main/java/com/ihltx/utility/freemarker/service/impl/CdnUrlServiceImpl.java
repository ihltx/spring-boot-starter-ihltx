package com.ihltx.utility.freemarker.service.impl;

import com.ihltx.utility.freemarker.config.FreemarkerConfig;
import com.ihltx.utility.freemarker.entity.CdnUrl;
import com.ihltx.utility.freemarker.mapper.CdnUrlMapper;
import com.ihltx.utility.freemarker.service.CdnUrlService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("all")
@Service
@ConditionalOnProperty(prefix = "ihltx.freemarker", name = "enableDataSource" , havingValue = "true")
public class CdnUrlServiceImpl implements CdnUrlService {

	@Autowired
	private FreemarkerConfig freemarkerConfig;

	@Autowired
	private CdnUrlMapper cdnUrlMapper;


	@Override
	@DS("system")
	@Transactional
	public CdnUrl getCdnUrl(Long shopId) {
		QueryWrapper<CdnUrl> qw =new QueryWrapper<>();
		qw.eq(true, "shopId" , shopId);
		qw.last("LIMIT 0,1");
		return cdnUrlMapper.selectOne(qw);
	}
}
