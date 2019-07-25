package com.blink.ewb.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blink.ewb.Config;
import com.blink.ewb.dao.PageDao;
import com.blink.ewb.entity.FastdfsVo;
import com.blink.ewb.entity.Page;
import com.blink.ewb.entity.PageData;
import com.blink.ewb.entity.Print;
import com.blink.ewb.entity.LaserPrint;
import com.blink.ewb.util.HttpAsyncClientUtil;
import com.blink.ewb.util.JsonUtil;
import com.google.gson.reflect.TypeToken;

@Service
public class PageService {

	private static final Logger logger = LoggerFactory.getLogger(PageService.class);

	@Autowired
	private PageDao pageDao;

	@Autowired
	private PrintService printService;

	@Autowired
	private LaserPrintService laserPrintService;

	/**
	 * 获取入口page
	 * 
	 * @param roomKey
	 * @return
	 */
	public Page getEntryPage(String roomKey) {
		Page page = this.pageDao.findFirstByRoomKeyOrderByPageIdDesc(roomKey);
		if (page == null) {
			page = this.createPage(roomKey);
		}
		return page;
	}

	/**
	 * 获取room的page总数
	 * 
	 * @param roomKey
	 * @return
	 */
	public long getRoomPagesCount(String roomKey) {
		return this.pageDao.countByRoomKey(roomKey);
	}

	/**
	 * 获取room的page集合
	 * 
	 * @param roomKey
	 * @return
	 */
	public List<Page> getRoomPages(String roomKey) {
		return this.pageDao.findByRoomKey(roomKey);
	}

	/**
	 * 获取room的pageId集合
	 * 
	 * @param roomKey
	 * @return
	 */
	public List<Long> getRoomPageIds(String roomKey) {
		List<Long> pageIdList = new ArrayList<Long>();
		List<Page> pageList = this.getRoomPages(roomKey);
		if (pageList != null && pageList.size() > 0) {
			for (Page page : pageList) {
				pageIdList.add(page.getPageId());
			}
		}
		return pageIdList;
	}

	/**
	 * 加载数据
	 * 
	 * @param roomKey
	 * @param pageId
	 * @param printId
	 * @param laserPrintId
	 * @return
	 */
	public PageData loadData(String roomKey, long pageId, long printId, long laserPrintId) {
		Page page = this.getPage(roomKey, pageId);
		List<Page> pages = this.getRoomPages(roomKey);
		// List<Long> pageIds = this.getRoomPageIds(roomKey);
		List<Print> prints = null;
		long count = this.printService.getPagePrintsCount(roomKey, pageId);
		if (count > 0) { // 等于0表示clean
			// prints = this.printService.getPagePrints(roomKey, pageId,
			// printId, userId);
			prints = this.printService.getPagePrints(roomKey, pageId, printId);
		}
		LaserPrint laserPrint = this.laserPrintService.getLaserPrint(roomKey, pageId);

		PageData pageData = new PageData();
		pageData.setPage(page);
		pageData.setPages(pages);
		// pageData.setPageIds(pageIds);
		pageData.setPrints(prints);
		pageData.setLaserPrint(laserPrint);

		return pageData;
	}

	/**
	 * 获取page
	 * 
	 * @param pageId
	 * @return
	 */
	public Page getPage(String roomKey, long pageId) {
		return this.pageDao.findByRoomKeyAndPageId(roomKey, pageId);
	}

	/**
	 * 创建page
	 * 
	 * @param roomKey
	 * @return
	 */
	public Page createPage(String roomKey) {
		Page page = new Page();
		page.setRoomKey(roomKey);
		page.setCreateTime(new Date());
		return this.savePage(page);
	}

	/**
	 * 保存page
	 * 
	 * @param page
	 * @return
	 */
	public Page savePage(Page page) {
		return this.pageDao.save(page);
	}

	/**
	 * 清理page
	 * 
	 * @param roomKey
	 * @param pageId
	 */
	@Transactional
	public void cleanPage(String roomKey, long pageId) {
		Page page = this.getPage(roomKey, pageId);
		boolean isUpdatePage = false;
		// 清空缩略图
		String thumbnail = page.getThumbnail();
		if (thumbnail != null && !"".equals(thumbnail)) {
			page.setThumbnail(null);
			isUpdatePage = true;
		}
		// 清空背景图片
		String fileUrl = page.getFileUrl();
		String fileGroup = page.getFileGroup();
		String fileName = page.getFileName();
		if (fileUrl != null && !"".equals(fileUrl)) {
			page.setFileUrl(null);
			page.setFileGroup(null);
			page.setFileName(null);
			isUpdatePage = true;
		}
		if (isUpdatePage) { // 原来有缩略图或者背景图片，需要清空字段
			this.savePage(page);
		}
		// 删除print by page
		this.printService.cleanPagePrints(roomKey, pageId);
		// 删除laserPrint
		this.laserPrintService.cleanPageLaserPrints(roomKey, pageId);
		// 删除背景图片
		if (fileGroup != null && !"".equals(fileGroup) && fileName != null && !"".equals(fileName)) {
			page.setFileGroup(fileGroup);
			page.setFileName(fileName);
			this.deleteFile(page);
		}
	}

	/**
	 * 清理page的print
	 * 
	 * @param roomKey
	 * @param pageId
	 */
	@Transactional
	public void cleanPagePrints(String roomKey, long pageId) {
		// 清空缩略图
		Page page = this.getPage(roomKey, pageId);
		String thumbnail = page.getThumbnail();
		if (thumbnail != null && !"".equals(thumbnail)) {
			page.setThumbnail(null);
			this.savePage(page);
		}
		// 删除print by page
		this.printService.cleanPagePrints(roomKey, pageId);
		// 删除laserPrint
		this.laserPrintService.cleanPageLaserPrints(roomKey, pageId);
	}

	/**
	 * 删除page
	 * 
	 * @param roomKey
	 * @param pageId
	 */
	@Transactional
	public void deletePage(String roomKey, long pageId) {
		Page page = this.getPage(roomKey, pageId);
		// 删除page by id
		this.pageDao.deleteByRoomKeyAndPageId(roomKey, pageId);
		// 删除print by page
		this.printService.cleanPagePrints(roomKey, pageId);
		// 删除laserPrint
		this.laserPrintService.cleanPageLaserPrints(roomKey, pageId);
		// 删除背景图片
		this.deleteFile(page);
	}

	/**
	 * 清理room的page
	 * 
	 * @param roomKey
	 */
	@Transactional
	public void cleanRoomPages(String roomKey) {
		List<Page> pageList = this.getRoomPages(roomKey);
		// 删除print by room
		this.printService.cleanRoomPrints(roomKey);
		// 删除page by room
		this.pageDao.deleteByRoomKey(roomKey);
		// 删除laserPrint
		this.laserPrintService.cleanRoomLaserPrints(roomKey);
		// 删除背景图片
		this.deleteFile(pageList);
	}

	/**
	 * 添加背景图片
	 * 
	 * @param roomKey
	 * @param file
	 */
	@Transactional
	public void addFile(String roomKey, String file) {
		Type type = new TypeToken<List<FastdfsVo>>() {
		}.getType();
		List<FastdfsVo> resultList = JsonUtil.fromJson(file, type);
		if (resultList != null && resultList.size() > 0) {
			for (FastdfsVo result : resultList) {
				Page page = new Page();
				page.setRoomKey(roomKey);
				page.setCreateTime(new Date());
				page.setFileGroup(result.getRemoteFileGroup());
				page.setFileName(result.getRemoteFileName());
				page.setFileUrl(result.getRemoteFileUrl());
				this.savePage(page);
			}
		}
	}

	/**
	 * 添加缩略图
	 * 
	 * @param roomKey
	 * @param pageId
	 * @param thumbnail
	 */
	public void addThumbnail(String roomKey, long pageId, String thumbnail) {
		Page page = this.getPage(roomKey, pageId);
		page.setThumbnail(thumbnail);
		this.savePage(page);
	}

	/**
	 * 删除背景图片
	 * 
	 * @param page
	 */
	private void deleteFile(Page page) {
		List<Page> pageList = new ArrayList<Page>();
		pageList.add(page);
		this.deleteFile(pageList);
	}

	/**
	 * 删除背景图片
	 * 
	 * @param pageList
	 */
	private void deleteFile(List<Page> pageList) {
		List<FastdfsVo> fileList = new ArrayList<FastdfsVo>();
		for (Page page : pageList) {
			String fileGroup = page.getFileGroup();
			String fileName = page.getFileName();
			if (fileGroup != null && !"".equals(fileGroup) && fileName != null && !"".equals(fileName)) {
				FastdfsVo fastdfsVo = new FastdfsVo();
				fastdfsVo.setRemoteFileGroup(fileGroup);
				fastdfsVo.setRemoteFileName(fileName);
				fileList.add(fastdfsVo);
			}
		}
		this.deleteRemoteFile(fileList);
	}

	/**
	 * 删除远程文件
	 * 
	 * @param fileList
	 */
	private void deleteRemoteFile(List<FastdfsVo> fileList) {
		try {
			if (fileList != null && !fileList.isEmpty()) {
				Map<String, String> map = new HashMap<String, String>();
				String file = JsonUtil.toJson(fileList,
						new String[] { "fileName", "fileExt", "fileLength", "remoteFileExt", "remoteFileUrl" });
				map.put("file", file);
				if (!map.isEmpty()) {
					HttpAsyncClientUtil.httpPost(Config.file_server_path + "/ewbfile/delete", map);
				}
			}
		} catch (Exception e) {
			logger.error("error:", e);
		}
	}

}
