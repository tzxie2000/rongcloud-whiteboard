package com.blink.ewb.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blink.ewb.dao.PrintDao;
import com.blink.ewb.entity.Page;
import com.blink.ewb.entity.Print;
import com.blink.ewb.service.PrintService;

@Service
public class PrintService {

	@Autowired
	private PrintDao printDao;

	@Autowired
	private PageService pageService;

	/**
	 * 获取page的print数
	 * 
	 * @param roomKey
	 * @param pageId
	 * @return
	 */
	public long getPagePrintsCount(String roomKey, Long pageId) {
		return this.printDao.countByRoomKeyAndPageId(roomKey, pageId);
	}

	/**
	 * 获取page的某个print之后的print集合
	 * 
	 * @param roomKey
	 * @param pageId
	 * @param printId
	 * @return
	 */
	public List<Print> getPagePrints(String roomKey, long pageId, long printId) {
		return this.printDao.findByRoomKeyAndPageIdAndPrintIdGreaterThan(roomKey, pageId, printId);
	}

	/**
	 * 获取page的某个print之后且不属于某个user的print集合
	 * 
	 * @param roomKey
	 * @param pageId
	 * @param printId
	 * @param userId
	 * @return
	 */
	public List<Print> getPagePrints(String roomKey, long pageId, long printId, String userId) {
		return this.printDao.findByRoomKeyAndPageIdAndPrintIdGreaterThanAndUserIdNot(roomKey, pageId, printId, userId);
	}

	/**
	 * 保存print
	 * 
	 * @param print
	 * @return
	 */
	public Print savePrint(Print print) {
		return this.printDao.save(print);
	}

	/**
	 * 创建print
	 * 
	 * @param roomKey
	 * @param pageId
	 * @param userId
	 * @param data
	 * @return
	 */
	public Print createPrint(String roomKey, long pageId, String userId, String data) {
		Print print = new Print();
		print.setRoomKey(roomKey);
		print.setPageId(pageId);
		print.setUserId(userId);
		print.setData(data);
		print.setCreateTime(new Date());
		return this.savePrint(print);
	}

	/**
	 * 创建print, 同时创建缩略图
	 * 
	 * @param roomKey
	 * @param pageId
	 * @param userId
	 * @param data
	 * @param thumbnail
	 * @return
	 */
	@Transactional
	public Print createPrint(String roomKey, long pageId, String userId, String data, String thumbnail) {
		if (thumbnail != null && !"".equals(thumbnail)) {
			// 保存缩略图
			Page page = this.pageService.getPage(roomKey, pageId);
			page.setThumbnail(thumbnail);
			this.pageService.savePage(page);
		}
		// 保存print
		return this.createPrint(roomKey, pageId, userId, data);
	}

	/**
	 * 清理page的print
	 * 
	 * @param roomKey
	 * @param pageId
	 */
	@Transactional
	public void cleanPagePrints(String roomKey, long pageId) {
		this.printDao.deleteByRoomKeyAndPageId(roomKey, pageId);
	}

	/**
	 * 清理room的print
	 * 
	 * @param roomKey
	 */
	@Transactional
	public void cleanRoomPrints(String roomKey) {
		this.printDao.deleteByRoomKey(roomKey);
	}

}
