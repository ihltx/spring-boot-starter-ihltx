package com.ihltx.utility.freemarker.service;

import com.ihltx.utility.freemarker.entity.CdnUrl;

/**
 * 应用CdnUrlService服务接口
 * @author liulin
 *
 */
public interface CdnUrlService {


	/**
	 * 获取CdnUrl
	 * @param shopId
	 * @return CdnUrl
	 */
	CdnUrl getCdnUrl(Long shopId);

}
