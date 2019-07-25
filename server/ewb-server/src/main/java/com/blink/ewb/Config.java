package com.blink.ewb;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class Config {

	public static String web_url;

	public static String file_server_path;

	/** 数据清理任务相关 */
	public static boolean clean_enable;

	public static String clean_cron;
	/** 单位是天 */
	public static long clean_expiration;

	/** token相关 */
	public static boolean token_enable;
	
	public static String token_secret;

	/** 单位是分钟 */
	public static long token_expiration;

	/** HttpAsyncClient相关 */
	public static int connection_request_timeout = 3000;

	public static int connect_timeout = 3000;

	public static int socket_timeout = 3000;

	// public static int pool_max_total = 20;
	//
	// public static int pool_default_max_per_route = 2;

	public static void init() {

		File file = new File("./config.properties");
		if (file.exists()) {
			Properties prop = new Properties();
			try {
				prop.load(new FileInputStream(file));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			web_url = prop.getProperty("web_url");
			file_server_path = prop.getProperty("file_server_path");

			clean_enable = Boolean.valueOf(prop.getProperty("clean_enable"));
			clean_cron = prop.getProperty("clean_cron");
			clean_expiration = Long.valueOf(prop.getProperty("clean_expiration"));

			token_enable = Boolean.valueOf(prop.getProperty("token_enable"));
			token_secret = prop.getProperty("token_secret");
			token_expiration = Long.valueOf(prop.getProperty("token_expiration"));

			connection_request_timeout = prop.getProperty("connection_request_timeout") != null
					&& !"".equals(prop.getProperty("connection_request_timeout"))
							? Integer.valueOf(prop.getProperty("connection_request_timeout"))
							: connection_request_timeout;
			connect_timeout = prop.getProperty("connect_timeout") != null
					&& !"".equals(prop.getProperty("connect_timeout"))
							? Integer.valueOf(prop.getProperty("connect_timeout")) : connect_timeout;
			socket_timeout = prop.getProperty("socket_timeout") != null
					&& !"".equals(prop.getProperty("socket_timeout"))
							? Integer.valueOf(prop.getProperty("socket_timeout")) : socket_timeout;
//			pool_max_total = prop.getProperty("pool_max_total") != null
//					&& !"".equals(prop.getProperty("pool_max_total"))
//							? Integer.valueOf(prop.getProperty("pool_max_total")) : pool_max_total;
//			pool_default_max_per_route = prop.getProperty("pool_default_max_per_route") != null
//					&& !"".equals(prop.getProperty("pool_default_max_per_route"))
//							? Integer.valueOf(prop.getProperty("pool_default_max_per_route"))
//							: pool_default_max_per_route;
		}

	}
}
