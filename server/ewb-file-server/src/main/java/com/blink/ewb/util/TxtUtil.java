package com.blink.ewb.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TxtUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(TxtUtil.class);

	private static final Font defaultFont = new Font("宋体", Font.PLAIN, 20);

	public static void txt2Image(String txtFullName, String imageFullName) throws Exception {
		String textCharset = getFileCharset(txtFullName);
		BufferedImage img = createImage(txtFullName, textCharset, defaultFont);
		String imagePath = FilenameUtils.getFullPath(imageFullName);
		File folder = new File(imagePath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		File imageFile = new File(imageFullName);
		String imageType = FilenameUtils.getExtension(imageFullName);
		ImageIO.write(img, imageType, imageFile);
	}

	private static BufferedImage createImage(String txtFullName, String txtCharset, Font font) throws Exception {
		int imageWidth = 0;
		int imageHeight = 0;
		int nextLinePosition = font.getSize();
		List<String> contentList = new ArrayList<String>();

		// need to find the actual image width and height first
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		g2d.setFont(font);
		FontMetrics fm = g2d.getFontMetrics();

		// 先计算高度和宽度
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(txtFullName), txtCharset));
			// br = new BufferedReader(new FileReader(txtFullPath));
			String line;
			while ((line = br.readLine()) != null) {
				int lineWidth = fm.stringWidth(line);
				imageWidth = imageWidth < lineWidth ? lineWidth : imageWidth;
				imageHeight += font.getSize();
				contentList.add(line);
			}
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
					logger.error("error:", e);
				}
			}
		}

		g2d.dispose();

		// render with known width and height
		img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		g2d = img.createGraphics();
		g2d.setFont(font);
		g2d.setBackground(Color.WHITE);
		g2d.setColor(Color.BLACK);

		for (String content : contentList) {
			// AttributedString str = createText(content, font);
			// g2d.drawString(str.getIterator(), 0, nextLinePosition);
			g2d.drawString(content, 0, nextLinePosition);
			nextLinePosition += font.getSize();
		}

		g2d.dispose();

		return img;
	}

	private static AttributedString createText(String content, Font font) {
		AttributedString str = new AttributedString(content);
		str.addAttribute(TextAttribute.FONT, font);
		str.addAttribute(TextAttribute.FOREGROUND, Color.BLACK, 0, content.length());
		str.addAttribute(TextAttribute.BACKGROUND, Color.LIGHT_GRAY, 0, content.length());
		return str;
	}

	public static String getFileCharset(String fileFullName) throws Exception {
		BufferedInputStream bin = null;
		try {
			bin = new BufferedInputStream(new FileInputStream(fileFullName));
			int p = (bin.read() << 8) + bin.read();
			String code = null;
			switch (p) {
			case 0xefbb:
				code = "UTF-8";
				break;
			case 0xfffe:
				code = "Unicode";
				break;
			case 0xfeff:
				code = "UTF-16BE";
				break;
			default:
				code = "GBK";
			}
			return code;
		} finally {
			if (bin != null) {
				try {
					bin.close();
				} catch (Exception e) {
					logger.error("error:", e);
				}
			}
		}
	}

	public static void convertFileCharset(String sourceFullName, String sourceCharset, String targetFullName,
			String targetCharset) throws Exception {
		InputStreamReader in = null;
		OutputStreamWriter out = null;
		try {
			in = new InputStreamReader(new FileInputStream(sourceFullName), sourceCharset);
			String targetPath = FilenameUtils.getFullPath(targetFullName);
			File folder = new File(targetPath);
			if (!folder.exists()) {
				folder.mkdirs();
			}
			out = new OutputStreamWriter(new FileOutputStream(targetFullName), targetCharset);
			char[] cbuf = new char[1024];
			int n;
			while ((n = in.read(cbuf)) != -1) {
				out.write(cbuf, 0, n);
			}
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
					logger.error("error:", e);
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					logger.error("error:", e);
				}
			}
		}
	}

	public static void convertFileCharset(String sourceFullName, String targetFullName, String targetCharset)
			throws Exception {
		String sourceCharset = getFileCharset(sourceFullName);
		convertFileCharset(sourceFullName, sourceCharset, targetFullName, targetCharset);
	}

}