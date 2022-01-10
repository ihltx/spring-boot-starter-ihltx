package com.ihltx.utility.upload.service;

import com.ihltx.utility.upload.entity.UploadResult;
import org.springframework.web.multipart.MultipartFile;

public interface UploadUtil {

	/**
	 * upload 上传文件到storagePath及指定客户ShopId相应的文件夹中
	 * 
	 * @param ShopId   客户ShopId
	 * @param tempFile 上传到服务器的临时文件对象
	 * @param filename 上传文件名称
	 * @return UploadResult 
	 *     1、成功： status => 0 msg => 成功消息 realPath =>
	 *         上传成功之后的文件完整物理路径 webPath => 上传成功之后的文件完整Web路径 
	 *     2、失败： status => 非0 msg => 错误消息
	 */
	UploadResult upload(Long ShopId, MultipartFile tempFile, String filename);

	/**
	 * upload 上传文件到storagePath及指定客户ShopId相应的文件夹中
	 *
	 * @param ShopId   客户ShopId
	 * @param tempFile 上传到服务器的临时文件对象
	 * @return UploadResult
	 *     1、成功： status => 0 msg => 成功消息 realPath =>
	 *         上传成功之后的文件完整物理路径 webPath => 上传成功之后的文件完整Web路径
	 *     2、失败： status => 非0 msg => 错误消息
	 */
	UploadResult upload(Long ShopId, MultipartFile tempFile);

}
