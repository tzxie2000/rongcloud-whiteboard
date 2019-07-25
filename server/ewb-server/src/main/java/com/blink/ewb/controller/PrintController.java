package com.blink.ewb.controller;

import java.util.List;

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
import com.blink.ewb.entity.Print;
import com.blink.ewb.response.EwbResponse;
import com.blink.ewb.service.PageService;
import com.blink.ewb.service.PrintService;

@Controller
@RequestMapping("/ewb/print")
public class PrintController {

	private static final Logger logger = LoggerFactory.getLogger(PrintController.class);

	@Autowired
	private PageService pageService;

	@Autowired
	private PrintService printService;

	/**
	 * 查询print
	 * 
	 * @param roomKey
	 * @param pageId
	 * @param printId
	 * @param userId
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/datas", method = RequestMethod.GET)
	public EwbResponse getPrints(@RequestAttribute(name = "roomKey", required = true) String roomKey,
			@RequestParam(name = "pageId", required = true) long pageId,
			@RequestParam(name = "printId", required = true) long printId,
			// @RequestParam(name = "userId", required = true) String userId,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			Page page = this.pageService.getPage(roomKey, pageId);
			if (page == null) {
				return new EwbResponse(404, "Page Not Exists", "");
			}

			long count = this.printService.getPagePrintsCount(roomKey, pageId);
			if (count == 0) {
				// return new EWBResponse(200, "CLEAN", "");
				return new EwbResponse(200, "OK", null);
			}
			// List<Print> prints = this.printService.getPagePrints(roomKey,
			// pageId, printId, userId);
			List<Print> prints = this.printService.getPagePrints(roomKey, pageId, printId);
			return new EwbResponse(200, "OK", prints);
		} catch (Exception e) {
			logger.error("error:", e);
			return new EwbResponse(500, "Server Error", "");
		}
	}

	/**
	 * 创建print
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
	public EwbResponse createPrint(@RequestAttribute(name = "roomKey", required = true) String roomKey,
			@RequestParam(name = "pageId", required = true) long pageId,
			@RequestParam(name = "userId", required = true) String userId,
			@RequestParam(name = "data", required = true) String data,
			@RequestParam(name = "thumbnail", required = false) String thumbnail, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			Page page = this.pageService.getPage(roomKey, pageId);
			if (page == null) {
				return new EwbResponse(404, "Page Not Exists", "");
			}

			// Print print = this.printService.createPrint(roomKey, pageId,
			// userId, data);
			Print print = this.printService.createPrint(roomKey, pageId, userId, data, thumbnail);
			return new EwbResponse(200, "OK", print);
		} catch (Exception e) {
			logger.error("error:", e);
			return new EwbResponse(500, "Server Error", "");
		}
	}

}
