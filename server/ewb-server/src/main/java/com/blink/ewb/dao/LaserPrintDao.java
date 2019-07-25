package com.blink.ewb.dao;

import org.springframework.stereotype.Repository;

import com.blink.ewb.entity.LaserPrint;

@Repository
public interface LaserPrintDao extends BaseDao<LaserPrint, Long> {

	/**
	 * 获取laserPrint
	 * 
	 * @param roomKey
	 * @param pageId
	 * @return
	 */
	LaserPrint findFirstByRoomKeyAndPageIdOrderByPrintIdDesc(String roomKey, Long pageId);

	/**
	 * 删除page的laserPrint
	 * 
	 * @param roomKey
	 * @param pageId
	 */
	void deleteByRoomKeyAndPageId(String roomKey, Long pageId);

	/**
	 * 删除room的laserPrint
	 * 
	 * @param roomKey
	 */
	void deleteByRoomKey(String roomKey);

}
