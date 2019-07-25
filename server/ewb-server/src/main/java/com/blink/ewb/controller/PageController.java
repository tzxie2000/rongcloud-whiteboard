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
import com.blink.ewb.entity.PageData;
import com.blink.ewb.entity.Room;
import com.blink.ewb.response.EwbResponse;
import com.blink.ewb.service.PageService;
import com.blink.ewb.service.RoomService;

@Controller
@RequestMapping("/ewb/page")
public class PageController {

	private static final Logger logger = LoggerFactory.getLogger(PageController.class);

	@Autowired
	private RoomService roomService;

	@Autowired
	private PageService pageService;

	/**
	 * 获取room的入口page信息
	 * 
	 * @param roomKey
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/entry", method = RequestMethod.GET)
	public EwbResponse getEntryPage(@RequestAttribute(name = "roomKey", required = true) String roomKey,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			Room room = this.roomService.getRoom(roomKey);
			if (room == null) {
				return new EwbResponse(404, "Room Not Exists", "");
			}
			Page entryPage = this.pageService.getEntryPage(roomKey);
			return new EwbResponse(200, "OK", entryPage);
		} catch (Exception e) {
			logger.error("error:", e);
			return new EwbResponse(500, "Server Error", "");
		}
	}

	/**
	 * 获取room的pageid集合
	 * 
	 * @param roomKey
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/datas", method = RequestMethod.GET)
	public EwbResponse getRoomPages(@RequestAttribute(name = "roomKey", required = true) String roomKey,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			Room room = this.roomService.getRoom(roomKey);
			if (room == null) {
				return new EwbResponse(404, "Room Not Exists", "");
			}

			// List<Long> pageIds = this.pageService.getRoomPageIds(roomKey);
			// return new EwbResponse(200, "OK", pageIds);
			List<Page> pages = this.pageService.getRoomPages(roomKey);
			return new EwbResponse(200, "OK", pages);
		} catch (Exception e) {
			logger.error("error:", e);
			return new EwbResponse(500, "Server Error", "");
		}
	}

	/**
	 * 加载数据
	 * 
	 * @param roomKey
	 * @param pageId
	 * @param printId
	 * @param laserPrintId
	 * @param userId
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/load", method = RequestMethod.GET)
	public EwbResponse loadData(@RequestAttribute(name = "roomKey", required = true) String roomKey,
			@RequestParam(name = "pageId", required = true) long pageId,
			@RequestParam(name = "printId", required = true) long printId,
			@RequestParam(name = "laserPrintId", required = true) long laserPrintId,
			// @RequestParam(name = "userId", required = true) String userId,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			Page page = this.pageService.getPage(roomKey, pageId);
			if (page == null) {
				return new EwbResponse(404, "Page Not Exists", "");
			}
			// PageData pageData = this.pageService.loadData(roomKey, pageId,
			// printId, laserPrintId, userId);
			PageData pageData = this.pageService.loadData(roomKey, pageId, printId, laserPrintId);
			return new EwbResponse(200, "OK", pageData);
		} catch (Exception e) {
			logger.error("error:", e);
			return new EwbResponse(500, "Server Error", "");
		}
	}

	/**
	 * 获取page信息
	 * 
	 * @param roomKey
	 * @param pageId
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/data", method = RequestMethod.GET)
	public EwbResponse getPage(@RequestAttribute(name = "roomKey", required = true) String roomKey,
			@RequestParam(name = "pageId", required = true) long pageId, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			Page page = this.pageService.getPage(roomKey, pageId);
			if (page == null) {
				return new EwbResponse(404, "Page Not Exists", "");
			}
			return new EwbResponse(200, "OK", page);
		} catch (Exception e) {
			logger.error("error:", e);
			return new EwbResponse(500, "Server Error", "");
		}
	}

	/**
	 * 创建page
	 * 
	 * @param roomKey
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public EwbResponse createPage(@RequestAttribute(name = "roomKey", required = true) String roomKey,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			Room room = this.roomService.getRoom(roomKey);
			if (room == null) {
				return new EwbResponse(404, "Room Not Exists", "");
			}

			Page newPage = this.pageService.createPage(roomKey);
			return new EwbResponse(200, "OK", newPage);
		} catch (Exception e) {
			logger.error("error:", e);
			return new EwbResponse(500, "Server Error", "");
		}
	}

	/**
	 * 上传图片
	 * 
	 * @param roomKey
	 * @param file
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/file", method = RequestMethod.POST)
	public EwbResponse uploadFile(@RequestAttribute(name = "roomKey", required = true) String roomKey,
			@RequestParam(name = "file", required = true) String file, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			Room room = this.roomService.getRoom(roomKey);
			if (room == null) {
				return new EwbResponse(404, "Room Not Exists", "");
			}

			this.pageService.addFile(roomKey, file);
			return new EwbResponse(200, "OK", "");
		} catch (Exception e) {
			logger.error("error:", e);
			return new EwbResponse(500, "Server Error", "");
		}
	}

	/**
	 * 上传缩略图
	 * 
	 * @param roomKey
	 * @param pageId
	 * @param thumbnail
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/thumbnail", method = RequestMethod.POST)
	public EwbResponse uploadThumbnai(@RequestAttribute(name = "roomKey", required = true) String roomKey,
			@RequestParam(name = "pageId", required = true) long pageId,
			@RequestParam(name = "thumbnail", required = true) String thumbnail, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			Page page = this.pageService.getPage(roomKey, pageId);
			if (page == null) {
				return new EwbResponse(404, "Page Not Exists", "");
			}

			this.pageService.addThumbnail(roomKey, pageId, thumbnail);
			return new EwbResponse(200, "OK", "");
		} catch (Exception e) {
			logger.error("error:", e);
			return new EwbResponse(500, "Server Error", "");
		}
	}

	/**
	 * 清理page的print
	 * 
	 * @param roomKey
	 * @param pageId
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/clean", method = RequestMethod.POST)
	public EwbResponse cleanPagePrints(@RequestAttribute(name = "roomKey", required = true) String roomKey,
			@RequestParam(name = "pageId", required = true) long pageId, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			Page page = this.pageService.getPage(roomKey, pageId);
			if (page == null) {
				return new EwbResponse(404, "Page Not Exists", "");
			}

			this.pageService.cleanPagePrints(roomKey, pageId);
			return new EwbResponse(200, "OK", "");
		} catch (Exception e) {
			logger.error("error:", e);
			return new EwbResponse(500, "Server Error", "");
		}
	}

	/**
	 * 删除page
	 * 
	 * @param roomKey
	 * @param pageId
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public EwbResponse deletePage(@RequestAttribute(name = "roomKey", required = true) String roomKey,
			@RequestParam(name = "pageId", required = true) long pageId, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			Page page = this.pageService.getPage(roomKey, pageId);
			if (page == null) {
				return new EwbResponse(404, "Page Not Exists", "");
			}

			long count = this.pageService.getRoomPagesCount(roomKey);
			if (count == 1) {
				return new EwbResponse(500, "Can Not Delete The Only Page", "");
			}

			this.pageService.deletePage(roomKey, pageId);
			return new EwbResponse(200, "OK", "");
		} catch (Exception e) {
			logger.error("error:", e);
			return new EwbResponse(500, "Server Error", "");
		}
	}

}
