package com.ihltx.utility.freemarker.service;

import com.ihltx.utility.freemarker.entity.Theme;

/**
 * 应用ThemeService服务接口
 * @author liulin
 *
 */
public interface ThemeService {

	/**
	 * 获取主题
	 * @param shopId
	 * @return Theme
	 */
	Theme getTheme(Long shopId);

}
