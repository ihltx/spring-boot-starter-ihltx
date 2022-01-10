package com.ihltx.utility.upload.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * 上传结果类
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadResult implements Serializable {
	/**
	 * 0 --上传成功 非0--上传失败
	 */
	private int status;

	/**
	 * 消息
	 */
	private String msg;

	/**
	 * 上传成功之后文件的物理路径
	 */
	private String realPath;

	/**
	 * 上传成功之后文件的Web路径
	 */
	private String webPath;
	
}
