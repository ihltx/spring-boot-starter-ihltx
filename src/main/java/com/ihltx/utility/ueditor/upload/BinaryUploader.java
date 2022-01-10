package com.ihltx.utility.ueditor.upload;


import com.ihltx.utility.ueditor.PathFormat;
import com.ihltx.utility.ueditor.define.AppInfo;
import com.ihltx.utility.ueditor.define.BaseState;
import com.ihltx.utility.ueditor.define.FileType;
import com.ihltx.utility.ueditor.define.State;
import com.ihltx.utility.util.FileUtil;
import com.ihltx.utility.util.StringUtil;
import com.ihltx.utility.util.WebUtil;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BinaryUploader {

	public static final State save(HttpServletRequest request,
								   Map<String, Object> conf) {
		if (!(request instanceof MultipartHttpServletRequest)) {
			return new BaseState(false, AppInfo.NOT_MULTIPART_CONTENT);
		}

		try {
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;

			MultipartFile multipartFile = multiRequest.getFile(conf.get("fieldName").toString());
			if (multipartFile == null) {
				return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
			}

			String savePath = (String) conf.get("savePath");
			savePath = savePath.replaceAll("%SHOP_ID%" , String.valueOf(WebUtil.getShopId()));
			String originFileName = multipartFile.getOriginalFilename();
			String suffix = FileType.getSuffixByFilename(originFileName);

			originFileName = originFileName.substring(0,
					originFileName.length() - suffix.length());
			savePath = savePath + suffix;

			long maxSize = ((Long) conf.get("maxSize")).longValue();

			if (!validType(suffix, (String[]) conf.get("allowFiles"))) {
				return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
			}

			savePath = PathFormat.parse(savePath, originFileName);
			String physicalPath = null;
			if(conf.get("physicsPath")==null || StringUtil.isNullOrEmpty(conf.get("physicsPath").toString())){
				physicalPath = (String) conf.get("rootPath") + savePath;
			}else{
				physicalPath = (String) conf.get("physicsPath") + savePath;
			}
			if(multipartFile.getSize() > maxSize){
				return new BaseState(false, AppInfo.MAX_SIZE);
			}
			String parentPath = FileUtil.getFilePath(physicalPath);
			if(!FileUtil.exists(parentPath)){
				FileUtil.makeDirs(parentPath);
			}
			multipartFile.transferTo(new File(physicalPath));

			State storageState = new BaseState(true);
			storageState.putInfo("url", PathFormat.format(savePath));
			storageState.putInfo("type", suffix);
			storageState.putInfo("original", originFileName + suffix);
			return storageState;
		} catch (IOException e) {
			return new BaseState(false, AppInfo.IO_ERROR);
		}
	}

	private static boolean validType(String type, String[] allowTypes) {
		List<String> list = Arrays.asList(allowTypes);

		return list.contains(type);
	}
}
