package com.blink.ewb;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ServletComponentScan
@EnableScheduling // 启用定时任务
public class EwbServer {

	private static Logger logger = LoggerFactory.getLogger(EwbServer.class);

	public static void main(String[] args) {
		Config.init();
		PropertyConfigurator.configure("log4j.properties");

		SpringApplication.run(EwbServer.class, args);

		logger.info("start successful~");
	}
}
