package com.blink.ewb.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blink.ewb.dao.LaserPrintDao;
import com.blink.ewb.entity.LaserPrint;

@Service
public class LaserPrintService {

	@Autowired
	private LaserPrintDao laserPrintDao;

	/**
	 * 获取laserPrint
	 * 
	 * @param pageId
	 * @param printId
	 * @return
	 */
	public LaserPrint getLaserPrint(String roomKey, long pageId) {
		return this.laserPrintDao.findFirstByRoomKeyAndPageIdOrderByPrintIdDesc(roomKey, pageId);
	}

	/**
	 * 保存print
	 * 
	 * @param print
	 * @return
	 */
	public LaserPrint saveLaserPrint(LaserPrint laserPrint) {
		return this.laserPrintDao.save(laserPrint);
	}

	/**
	 * 创建print
	 * 
	 * @param roomKey
	 * @param pageId
	 * @param data
	 * @return
	 */
	public LaserPrint createLaserPrint(String roomKey, long pageId, String data) {
		LaserPrint laserPrint = new LaserPrint();
		laserPrint.setPageId(pageId);
		laserPrint.setRoomKey(roomKey);
		laserPrint.setData(data);
		laserPrint.setCreateTime(new Date());
		return this.saveLaserPrint(laserPrint);
	}

	/**
	 * 清理page的laserPrint
	 * 
	 * @param roomKey
	 * @param pageId
	 */
	@Transactional
	public void cleanPageLaserPrints(String roomKey, long pageId) {
		this.laserPrintDao.deleteByRoomKeyAndPageId(roomKey, pageId);
	}

	/**
	 * 清理room的laserPrint
	 * 
	 * @param roomKey
	 */
	@Transactional
	public void cleanRoomLaserPrints(String roomKey) {
		this.laserPrintDao.deleteByRoomKey(roomKey);
	}

}
