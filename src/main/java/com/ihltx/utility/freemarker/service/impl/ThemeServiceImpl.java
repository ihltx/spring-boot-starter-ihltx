package com.ihltx.utility.freemarker.service.impl;

import com.ihltx.utility.freemarker.config.FreemarkerConfig;
import com.ihltx.utility.freemarker.entity.Theme;
import com.ihltx.utility.freemarker.mapper.ThemeMapper;
import com.ihltx.utility.freemarker.service.ThemeService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("all")
@Service
@ConditionalOnProperty(prefix = "ihltx.freemarker", name = "enableDataSource" , havingValue = "true")
public class ThemeServiceImpl implements ThemeService {

	@Autowired
	private FreemarkerConfig freemarkerConfig;

	@Autowired
	private ThemeMapper themeMapper;

	@Override
	@DS("system")
	@Transactional
	public Theme getTheme(Long shopId) {
		QueryWrapper<Theme> qw =new QueryWrapper<>();
		qw.eq(true, "shopId" , shopId);
		qw.last("LIMIT 0,1");
		return themeMapper.selectOne(qw);
	}
}
