package com.blink.ewb.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;

import com.blink.ewb.Config;
import com.blink.ewb.service.DataService;

@Configuration
@PropertySource("file:config.properties")
public class SchedulingConfig {

	private static Logger logger = LoggerFactory.getLogger(SchedulingConfig.class);

	@Autowired
	private DataService dataService;

	@Scheduled(cron = "${clean_cron}")
	public void cleanTask() {
		logger.info("cleanTask enable=" + Config.clean_enable);
		if (Config.clean_enable) {
			logger.info("cleanTask start");
			this.dataService.cleanData();
			logger.info("cleanTask end");
		}
	}

}
