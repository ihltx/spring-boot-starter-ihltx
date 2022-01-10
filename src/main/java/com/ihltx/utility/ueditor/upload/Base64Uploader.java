package com.ihltx.utility.ueditor.upload;


import com.ihltx.utility.ueditor.PathFormat;
import com.ihltx.utility.ueditor.define.AppInfo;
import com.ihltx.utility.ueditor.define.BaseState;
import com.ihltx.utility.ueditor.define.FileType;
import com.ihltx.utility.ueditor.define.State;
import com.ihltx.utility.util.FileUtil;
import com.ihltx.utility.util.StringUtil;
import com.ihltx.utility.util.WebUtil;
import org.apache.commons.codec.binary.Base64;

import java.util.Map;

public final class Base64Uploader {

	public static State save(String content, Map<String, Object> conf) {
		
		byte[] data = decode(content);

		long maxSize = ((Long) conf.get("maxSize")).longValue();

		if (!validSize(data, maxSize)) {
			return new BaseState(false, AppInfo.MAX_SIZE);
		}

		String suffix = FileType.getSuffix("JPG");

		String savePath = PathFormat.parse((String) conf.get("savePath"),
				(String) conf.get("filename"));
		savePath = savePath.replaceAll("%SHOP_ID%" , String.valueOf(WebUtil.getShopId()));

		savePath = savePath + suffix;
		String physicalPath = null;
		if(conf.get("physicsPath")==null || StringUtil.isNullOrEmpty(conf.get("physicsPath").toString())){
			physicalPath = (String) conf.get("rootPath") + savePath;
		}else{
			physicalPath = (String) conf.get("physicsPath") + savePath;
		}
		String parentPath = FileUtil.getFilePath(physicalPath);
		if(!FileUtil.exists(parentPath)){
			FileUtil.makeDirs(parentPath);
		}

		State storageState = StorageManager.saveBinaryFile(data, physicalPath);

		if (storageState.isSuccess()) {
			storageState.putInfo("url", PathFormat.format(savePath));
			storageState.putInfo("type", suffix);
			storageState.putInfo("original", "");
		}

		return storageState;
	}

	private static byte[] decode(String content) {
		return Base64.decodeBase64(content);
	}

	private static boolean validSize(byte[] data, long length) {
		return data.length <= length;
	}
	
}