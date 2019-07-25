package com.blink.ewb.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFSlideShowImpl;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hslf.usermodel.HSLFTextRun;
import org.apache.poi.hslf.usermodel.HSLFTextShape;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PPTUtil {

	private static final Logger logger = LoggerFactory.getLogger(PPTUtil.class);

	public static List<String> pptx2Image(String pptFullName, String imagePath, String imageType) throws Exception {
		FileInputStream fis = null;
		XMLSlideShow ppt = null;
		try {
			File folder = new File(imagePath);
			if (!folder.exists()) {
				folder.mkdirs();
			}

			fis = new FileInputStream(pptFullName);
			ppt = new XMLSlideShow(fis);
			Dimension pageSize = ppt.getPageSize();

			List<String> imageList = new ArrayList<String>();
			for (int i = 0; i < ppt.getSlides().size(); i++) {
				// 防止中文乱码
				for (XSLFShape shape : ppt.getSlides().get(i).getShapes()) {
					if (shape instanceof XSLFTextShape) {
						XSLFTextShape tsh = (XSLFTextShape) shape;
						for (XSLFTextParagraph paragraph : tsh) {
							for (XSLFTextRun run : paragraph) {
								run.setFontFamily("宋体");
							}
						}
					}
				}

				BufferedImage image = new BufferedImage(pageSize.width, pageSize.height, BufferedImage.TYPE_INT_RGB);
				Graphics2D graphics = image.createGraphics();
				// clear drawing area
				graphics.setPaint(Color.WHITE);
				graphics.fill(new Rectangle2D.Float(0, 0, pageSize.width, pageSize.height));

				// render
				ppt.getSlides().get(i).draw(graphics);

				// save output
				String pptName = FilenameUtils.getBaseName(pptFullName);
				String imageName = pptName + "_" + (i + 1) + "." + imageType;
				String imageFullPath = imagePath + imageName;
				File file = new File(imageFullPath);
				ImageIO.write(image, imageType, file);
				image.flush();

				imageList.add(imageFullPath);
			}
			return imageList;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
					logger.error("error:", e);
				}
			}
			if (ppt != null) {
				try {
					ppt.close();
				} catch (Exception e) {
					logger.error("error:", e);
				}
			}
		}
	}

	public static List<String> ppt2Image(String pptFullName, String imagePath, String imageType) throws Exception {
		HSLFSlideShow ppt = null;
		try {
			File folder = new File(imagePath);
			if (!folder.exists()) {
				folder.mkdirs();
			}

			ppt = new HSLFSlideShow(new HSLFSlideShowImpl(pptFullName));
			Dimension pageSize = ppt.getPageSize();

			List<String> imageList = new ArrayList<String>();
			for (int i = 0; i < ppt.getSlides().size(); i++) {
				// 防止中文乱码
				for (HSLFShape shape : ppt.getSlides().get(i).getShapes()) {
					if (shape instanceof HSLFTextShape) {
						HSLFTextShape tsh = (HSLFTextShape) shape;
						for (HSLFTextParagraph paragraph : tsh) {
							for (HSLFTextRun run : paragraph) {
								run.setFontFamily("宋体");
							}
						}
					}
				}

				// clear drawing area
				BufferedImage image = new BufferedImage(pageSize.width, pageSize.height, BufferedImage.TYPE_INT_RGB);
				Graphics2D graphics = image.createGraphics();
				graphics.setPaint(Color.WHITE);
				graphics.fill(new Rectangle2D.Float(0, 0, pageSize.width, pageSize.height));

				// render
				ppt.getSlides().get(i).draw(graphics);

				// save output
				String pptName = FilenameUtils.getBaseName(pptFullName);
				String imageName = pptName + "_" + (i + 1) + "." + imageType;
				String imageFullPath = imagePath + imageName;
				File file = new File(imageFullPath);
				ImageIO.write(image, imageType, file);
				image.flush();

				imageList.add(imageFullPath);
			}
			return imageList;
		} finally {
			if (ppt != null) {
				try {
					ppt.close();
				} catch (Exception e) {
					logger.error("error:", e);
				}
			}
		}
	}
}
