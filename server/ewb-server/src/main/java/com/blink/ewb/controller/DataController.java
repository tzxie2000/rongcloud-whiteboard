package com.blink.ewb.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.blink.ewb.response.EwbResponse;
import com.blink.ewb.service.DataService;

@Controller
@RequestMapping("/ewb/data")
public class DataController {

	private static final Logger logger = LoggerFactory.getLogger(DataController.class);

	@Autowired
	private DataService dataService;

	/**
	 * 清理数据
	 * 
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@RequestMapping(value = "/clean", method = RequestMethod.POST)
	public EwbResponse clean(HttpServletRequest request, HttpServletResponse response) {
		try {
			this.dataService.cleanData();
			return new EwbResponse(200, "OK", "");
		} catch (Exception e) {
			logger.error("error:", e);
			return new EwbResponse(500, "Server Error", "");
		}
	}

}
