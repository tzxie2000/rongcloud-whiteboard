package com.blink.ewb;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import com.blink.ewb.fastdfs.FastdfsClient;
import com.blink.ewb.jodconverter.JodConverterClient;

@SpringBootApplication
@ServletComponentScan
public class EwbFileServer {

	private static Logger logger = LoggerFactory.getLogger(EwbFileServer.class);

	public static void main(String[] args) {
		Config.init();
		FastdfsClient.init();
		JodConverterClient.init();
		PropertyConfigurator.configure("log4j.properties");

		SpringApplication.run(EwbFileServer.class, args);

		logger.info("start successful~");
	}
}
