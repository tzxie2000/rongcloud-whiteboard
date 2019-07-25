package com.blink.ewb.util;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PDFUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(PDFUtil.class);

	public static List<String> pdf2Image(String pdfFullName, String imagePath, String imageType) throws Exception {
		Document document = new Document();
		try {
			File folder = new File(imagePath);
			if (!folder.exists()) {
				folder.mkdirs();
			}

			document.setFile(pdfFullName);
			float scale = 2.5f;
			float rotation = 0f;

			List<String> imageList = new ArrayList<String>();
			for (int i = 0; i < document.getNumberOfPages(); i++) {
				BufferedImage image = (BufferedImage) document.getPageImage(i, GraphicsRenderingHints.SCREEN,
						Page.BOUNDARY_CROPBOX, rotation, scale);
				RenderedImage rendImage = image;

				String pdfName = FilenameUtils.getBaseName(pdfFullName);
				String imageName = pdfName + "_" + (i + 1) + "." + imageType;
				String imageFullPath = imagePath + imageName;
				File file = new File(imageFullPath);
				ImageIO.write(rendImage, imageType, file);
				image.flush();

				imageList.add(imageFullPath);
			}
			return imageList;
		} finally {
			if (document != null) {
				try {
					document.dispose();
				} catch (Exception e) {
					logger.error("error:", e);
				}
			}
		}
	}

}