package com.blink.ewb.controller;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.blink.ewb.Config;
import com.blink.ewb.fastdfs.FastdfsClient;
import com.blink.ewb.fastdfs.FastdfsVo;
import com.blink.ewb.jodconverter.JodConverterClient;
import com.blink.ewb.response.FileResponse;
import com.blink.ewb.util.FileUtil;
import com.blink.ewb.util.JsonUtil;
import com.blink.ewb.util.PDFUtil;
import com.blink.ewb.util.PPTUtil;
import com.blink.ewb.util.TxtUtil;
import com.google.gson.reflect.TypeToken;

@Controller
@RequestMapping("/ewbfile")
public class EwbFileController {

	private static final Logger logger = LoggerFactory.getLogger(EwbFileController.class);

	/**
	 * 上传文件
	 * 
	 * @param roomKey
	 * @param file
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public FileResponse upload(@RequestParam(name = "roomKey", required = true) String roomKey,
			@RequestParam(name = "file", required = true) MultipartFile file, HttpServletRequest request,
			HttpServletResponse response) {
		String uploadPath = Config.filePath + File.separator + roomKey + File.separator + "source" + File.separator;
		String tempPdfPath = Config.filePath + File.separator + roomKey + File.separator + "temppdf" + File.separator;
		String tempTxtPath = Config.filePath + File.separator + roomKey + File.separator + "temptxt" + File.separator;
		String imagePath = Config.filePath + File.separator + roomKey + File.separator + "image" + File.separator;
		String uploadFullName = null;
		String tempPdfFullName = null;
		String tempTxtFullName = null;
		List<String> imageList = null;
		try {
			if (file == null || file.isEmpty() || file.getOriginalFilename() == null
					|| "".equals(file.getOriginalFilename())) {
				return new FileResponse(500, "ERROR", "");
			}
			// if (file.getSize() > Config.uploadMaxSize) {
			// return new FileResponse(500, "ERROR", "");
			// }

			String originalFileName = file.getOriginalFilename();
			// 拿到后辍名带点
			String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
			// 文件类型不带点
			String fileType = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
			// 文件名
			// String fileName = originalFileName.substring(1,
			// originalFileName.lastIndexOf("."));

			// 获取当前时间
			Date currDate = new Date();
			String timeToInt = currDate.getTime() + "";
			int randomNumber = (int) Math.ceil(Math.random() * 10);
			String uploadFileName = randomNumber + timeToInt + suffix;
			uploadFullName = uploadPath + uploadFileName;
			// 上传到本地
			FileUtil.upload(file, uploadPath, uploadFileName);

			// 转换成IMAGE
			imageList = new ArrayList<String>();
			if ("jpg".equals(fileType) || "png".equals(fileType) || "jpeg".equals(fileType) || "bmp".equals(fileType)
					|| "gif".equals(fileType)) { // image
				String imageFullName = imagePath + uploadFileName;
				FileUtil.copy(uploadFullName, imageFullName);
				imageList.add(imageFullName);
			} else if ("pdf".equals(fileType)) { // pdf
				imageList = PDFUtil.pdf2Image(uploadFullName, imagePath, "png");
//			} else if ("ppt".equals(fileType) || "pptx".equals(fileType)) {
//				if ("ppt".equals(fileType)) { // PPT/PPTX
//					imageList = PPTUtil.ppt2Image(uploadFullName, imagePath, "png");
//				} else {
//					imageList = PPTUtil.pptx2Image(uploadFullName, imagePath, "png");
//				}
			} else if ("txt".equals(fileType) || "doc".equals(fileType) || "docx".equals(fileType)
					|| "xls".equals(fileType) || "xlsx".equals(fileType) || "ppt".equals(fileType)
					|| "pptx".equals(fileType)) { // JodConverter
				if ("txt".equals(fileType)) { // txt
					String charset = TxtUtil.getFileCharset(uploadFullName);
					if (!"UTF-8".equalsIgnoreCase(charset)) {
						String tempTxtName = uploadFileName + ".txt";
						tempTxtFullName = tempTxtPath + tempTxtName;
						// 转编码
						TxtUtil.convertFileCharset(uploadFullName, charset, tempTxtFullName, "UTF-8");
						tempPdfFullName = tempPdfPath + tempTxtName + ".pdf";
						JodConverterClient.convert(tempTxtFullName, tempPdfFullName);
					} else {
						tempPdfFullName = tempPdfPath + uploadFileName + ".pdf";
						JodConverterClient.convert(uploadFullName, tempPdfFullName);
					}
				} else { // office
					tempPdfFullName = tempPdfPath + uploadFileName + ".pdf";
					JodConverterClient.convert(uploadFullName, tempPdfFullName);
				}
				imageList = PDFUtil.pdf2Image(tempPdfFullName, imagePath, "png");
			} else {
				return new FileResponse(500, "ERROR", "");
			}

			// 上传到文件服务器
			List<FastdfsVo> fileList = new ArrayList<FastdfsVo>();
			for (String imageFullName : imageList) {
				String imageName = imageFullName.substring(imageFullName.lastIndexOf(File.separator) + 1);
				String imageExt = imageName.substring(imageName.lastIndexOf(".") + 1);
				File imageFile = new File(imageFullName);
				if (imageFile.exists()) {
					FastdfsVo fastdfsVo = FastdfsClient.uploadFile(imageFullName, imageExt);
					fastdfsVo.setFileName(imageName);
					fastdfsVo.setFileExt(imageExt);
					fileList.add(fastdfsVo);
				}
			}
			String listJson = JsonUtil.toJson(fileList,
					new String[] { "fileName", "fileExt", "fileLength", "remoteFileExt" });
			return new FileResponse(200, "OK", listJson);
		} catch (Exception e) {
			logger.error("error:", e);
			return new FileResponse(500, "ERROR", "");
		} finally {
			try {
				if (uploadFullName != null && !"".equals(uploadFullName)) {
					FileUtil.delFile(uploadFullName);
				}
			} catch (Exception e) {
				logger.error("error:", e);
			}
			try {
				if (tempPdfFullName != null && !"".equals(tempPdfFullName)) {
					FileUtil.delFile(tempPdfFullName);
				}
			} catch (Exception e) {
				logger.error("error:", e);
			}
			try {
				if (tempTxtFullName != null && !"".equals(tempTxtFullName)) {
					FileUtil.delFile(tempTxtFullName);
				}
			} catch (Exception e) {
				logger.error("error:", e);
			}
			if (imageList != null && imageList.size() > 0) {
				for (String imageFullName : imageList) {
					try {
						FileUtil.delFile(imageFullName);
					} catch (Exception e) {
						logger.error("error:", e);
					}
				}
			}
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public FileResponse delete(@RequestParam(name = "file", required = true) String file, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			Type type = new TypeToken<List<FastdfsVo>>() {
			}.getType();
			List<FastdfsVo> fileList = JsonUtil.fromJson(file, type);
			for (FastdfsVo fastdfsVo : fileList) {
				try {
					FastdfsClient.deleteFile(fastdfsVo.getRemoteFileGroup(), fastdfsVo.getRemoteFileName());
				} catch (Exception e) {
					logger.error("error:", e);
				}
			}
			return new FileResponse(200, "OK", "");
		} catch (Exception e) {
			logger.error("error:", e);
			return new FileResponse(500, "ERROR", "");
		}
	}

}
