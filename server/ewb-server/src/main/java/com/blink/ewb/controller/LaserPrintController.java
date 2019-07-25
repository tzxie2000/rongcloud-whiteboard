package com.blink.ewb.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.blink.ewb.entity.Page;
import com.blink.ewb.entity.LaserPrint;
import com.blink.ewb.response.EwbResponse;
import com.blink.ewb.service.PageService;
import com.blink.ewb.service.LaserPrintService;

@Controller
@RequestMapping("/ewb/laserPrint")
public class LaserPrintController {

	private static final Logger logger = LoggerFactory.getLogger(LaserPrintController.class);

	@Autowired
	private PageService pageService;

	@Autowired
	private LaserPrintService laserPrintService;

	/**
	 * 查询laserPrint
	 * 
	 * @param roomKey
	 * @param pageId
	 * @param laserPrintId
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/data", method = RequestMethod.GET)
	public EwbResponse getLaserPrint(@RequestAttribute(name = "roomKey", required = true) String roomKey,
			@RequestParam(name = "pageId", required = true) long pageId,
			@RequestParam(name = "laserPrintId", required = true) long laserPrintId, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			Page page = this.pageService.getPage(roomKey, pageId);
			if (page == null) {
				return new EwbResponse(404, "Page Not Exists", "");
			}
			LaserPrint laserPrint = this.laserPrintService.getLaserPrint(roomKey, pageId);
			return new EwbResponse(200, "OK", laserPrint);
		} catch (Exception e) {
			logger.error("error:", e);
			return new EwbResponse(500, "Server Error", "");
		}
	}

	/**
	 * 创建laserPrint
	 * 
	 * @param roomKey
	 * @param pageId
	 * @param data
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public EwbResponse createLaserPrint(@RequestAttribute(name = "roomKey", required = true) String roomKey,
			@RequestParam(name = "pageId", required = true) long pageId,
			@RequestParam(name = "data", required = true) String data, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			Page page = this.pageService.getPage(roomKey, pageId);
			if (page == null) {
				return new EwbResponse(404, "Page Not Exists", "");
			}
			this.laserPrintService.createLaserPrint(roomKey, pageId, data);
			return new EwbResponse(200, "OK", "");
		} catch (Exception e) {
			logger.error("error:", e);
			return new EwbResponse(500, "Server Error", "");
		}
	}

	/**
	 * 清除laserPrint
	 * 
	 * @param roomKey
	 * @param pageId
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/clean", method = RequestMethod.POST)
	public EwbResponse cleanPageLaserPrints(@RequestAttribute(name = "roomKey", required = true) String roomKey,
			@RequestParam(name = "pageId", required = true) long pageId, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			Page page = this.pageService.getPage(roomKey, pageId);
			if (page == null) {
				return new EwbResponse(404, "Page Not Exists", "");
			}
			this.laserPrintService.cleanPageLaserPrints(roomKey, pageId);
			return new EwbResponse(200, "OK", "");
		} catch (Exception e) {
			logger.error("error:", e);
			return new EwbResponse(500, "Server Error", "");
		}
	}

}
