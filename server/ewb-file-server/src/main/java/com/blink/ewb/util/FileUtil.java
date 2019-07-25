package com.blink.ewb.util;

import java.io.File;
import java.nio.file.Files;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileUtil {

	public static void upload(MultipartFile file, String filePath, String fileName) throws Exception {
		File path = new File(filePath);
		if (!path.exists()) {// 判读存储文件路是否存在，不存在则创建
			path.mkdirs();
		}
		String fileFullPath = filePath + File.separator + fileName;
		// 文件开始上传到服务器上
		file.transferTo(new File(fileFullPath));
	}

	public static void copy(String sourceFullName, String targetFullName) throws Exception {
		File sourceFile = new File(sourceFullName);
		if (!sourceFile.exists()) {
			return;
		}

		String targetPath = FilenameUtils.getFullPath(targetFullName);
		File folder = new File(targetPath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		File targetFile = new File(targetFullName);
		Files.copy(sourceFile.toPath(), targetFile.toPath());
	}

	public static void delFile(String filePath) {
		// 删除文件
		File file = new File(filePath);
		if (file.exists() && file.isFile()) {
			file.delete();
		}
	}

	public static void delFolder(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				delFolder(files[i]);
			}
		}
		file.delete();
	}
}
