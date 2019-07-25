package com.blink.ewb.jodconverter;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blink.ewb.Config;

public class JodConverterClient {
	
	private static final Logger logger = LoggerFactory.getLogger(JodConverterClient.class);

	private static OfficeManager officeManager;

	public static void init() {
		DefaultOfficeManagerConfiguration configuration = new DefaultOfficeManagerConfiguration();
		String officePortParam = Config.officePort;
		if (officePortParam != null && !"".equals(officePortParam)) {
			configuration.setPortNumber(Integer.parseInt(officePortParam));
		}
		String officeHomeParam = Config.officeHome;
		if (officeHomeParam != null && !"".equals(officeHomeParam)) {
			configuration.setOfficeHome(new File(officeHomeParam));
		}
		officeManager = configuration.buildOfficeManager();
		start();
	}

	public static void start() {
		officeManager.start();
	}

	public static void stop() {
		if (officeManager != null) {
			try {
				officeManager.stop();
			} catch (Exception e) {
				logger.error("error:", e);
			}
		}
	}

	public static void convert(String inputFullPath, String outputFullPath) {
		OfficeDocumentConverter documentConverter = new OfficeDocumentConverter(officeManager);
		String imagePath = FilenameUtils.getFullPath(outputFullPath);
		File folder = new File(imagePath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		File inputFile = new File(inputFullPath);
		File outputFile = new File(outputFullPath);

		documentConverter.convert(inputFile, outputFile);
	}

}
