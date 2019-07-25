package com.blink.ewb;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class Config {

	// public static long uploadMaxSize = 10;

	public static String filePath;

	/** token相关 */
	public static boolean token_enable;

	public static String token_secret;

	public static String fastdfsUrl;

	public static String officePort;

	public static String officeHome;

	public static void init() {

		File file = new File("./config.properties");
		if (file.exists()) {
			Properties prop = new Properties();
			try {
				prop.load(new FileInputStream(file));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

//			uploadMaxSize = (prop.getProperty("uploadMaxSize") != null && !"".equals(prop.getProperty("uploadMaxSize"))
//					? Integer.parseInt(prop.getProperty("uploadMaxSize").trim()) : uploadMaxSize) * 1024 * 1024;
			filePath = prop.getProperty("filePath");

			token_enable = Boolean.valueOf(prop.getProperty("token_enable"));
			token_secret = prop.getProperty("token_secret");

			fastdfsUrl = prop.getProperty("fastdfsUrl");

			officePort = prop.getProperty("officePort");
			officeHome = prop.getProperty("officeHome");
		}

	}
}
