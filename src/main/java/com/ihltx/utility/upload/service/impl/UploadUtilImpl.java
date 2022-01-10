package com.ihltx.utility.upload.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.ihltx.utility.upload.entity.UploadResult;
import com.ihltx.utility.upload.config.UploadConfig;
import com.ihltx.utility.upload.service.UploadUtil;
import com.ihltx.utility.util.DateUtil;
import com.ihltx.utility.util.FileUtil;
import com.ihltx.utility.util.SecurityUtil;
import com.ihltx.utility.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
public class UploadUtilImpl implements UploadUtil {

	@Autowired
	private UploadConfig uploadConfig;



	/**
	 * upload 上传文件到storagePath及指定客户ShopId相应的文件夹中
	 * 
	 * @param shopId   客户shopId
	 * @param tempFile 上传到服务器的临时文件对象
	 * @param filename 上传文件名称
	 * @return UploadResult 
	 *     1、成功： status => 0 msg => 成功消息 realPath =>
	 *         上传成功之后的文件完整物理路径 webPath => 上传成功之后的文件完整Web路径 
	 *     2、失败： status => 非0 msg => 错误消息
	 */
	public UploadResult upload(Long shopId, MultipartFile tempFile, String filename) {
		UploadResult rs = new UploadResult();
		String path = uploadConfig.getUploadPath().replaceAll("%SHOP_ID%", String.valueOf(shopId)) + DateUtil.date("yyyy-MM-dd") + "/";
		String RealPath = StringUtil.trim(uploadConfig.getStoragePath().replaceAll("\\\\", "/"), "/") + "/" + path;
		if(StringUtil.isNullOrEmpty(filename)){
			filename = FileUtil.getFileBaseName(tempFile.getOriginalFilename());
		}
		String newFileName = filename;
		String ext = FileUtil.getFileExtName(filename);
		if (uploadConfig.getUploadFilenameRule().equalsIgnoreCase("md5")) {
			newFileName = SecurityUtil.md5(filename + System.currentTimeMillis());
			if (!StringUtils.isEmpty(ext)) {
				newFileName += "." + ext;
			}
		}

		String WebFilePath =  uploadConfig.getWebPathPrefix() + path + newFileName;
		String RealFilePath = RealPath.replaceAll("//", "/") + newFileName;
		WebFilePath = WebFilePath.replaceAll("//", "/");
		// 创建文件夹失败，上传失败
		if (!FileUtil.exists(RealPath)) {
			if (!FileUtil.makeDirs(RealPath)) {
				rs.setStatus(1);
				rs.setMsg("mkdir " + RealPath + " failed");
				return rs;
			}
		}
		// 不允许上传的文件类型，上传失败
		if (!uploadConfig.getUploadAllowExts().contains(ext.toLowerCase())) {
			rs.setStatus(2);
			rs.setMsg("invalid file type(" + ext + "), upload file failed");
			return rs;
		}
		if (tempFile.getSize() > uploadConfig.getMaxUploadFileSize()) {
			rs.setStatus(3);
			rs.setMsg("Upload file size(" + tempFile.getSize() + " bytes) exceeds limit "
					+ uploadConfig.getMaxUploadFileSize() + " bytes, upload file failed");
			return rs;
		}

		try {
			File newFile = new File(RealFilePath);
			// 通过CommonsMultipartFile的方法直接写文件（注意这个时候）
			tempFile.transferTo(newFile);
			rs.setRealPath(RealFilePath);
			rs.setWebPath(WebFilePath);
			rs.setStatus(0);
			rs.setMsg("success");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			rs.setStatus(4);
			rs.setMsg(e.getMessage());
		} catch (IOException e) {
			rs.setStatus(5);
			e.printStackTrace();
			rs.setMsg(e.getMessage());
		}
		return rs;

	}

	/**
	 * upload 上传文件到storagePath及指定客户ShopId相应的文件夹中
	 *
	 * @param shopId   客户shopId
	 * @param tempFile 上传到服务器的临时文件对象
	 * @return UploadResult
	 *     1、成功： status => 0 msg => 成功消息 realPath =>
	 *         上传成功之后的文件完整物理路径 webPath => 上传成功之后的文件完整Web路径
	 *     2、失败： status => 非0 msg => 错误消息
	 */
	public UploadResult upload(Long shopId, MultipartFile tempFile) {
		return  upload(shopId , tempFile , null);
	}
}
