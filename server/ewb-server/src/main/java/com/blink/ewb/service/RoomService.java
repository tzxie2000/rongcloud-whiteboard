package com.blink.ewb.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blink.ewb.dao.RoomDao;
import com.blink.ewb.entity.Room;

@Service
public class RoomService {

	@Autowired
	private RoomDao roomDao;
	@Autowired
	private PageService pageService;

	/**
	 * 获取room
	 * 
	 * @param roomKey
	 * @return
	 */
	public Room getRoom(String roomKey) {
		return this.roomDao.findByRoomKey(roomKey);
	}

	/**
	 * 获取room
	 * 
	 * @param appId
	 * @param roomNr
	 * @return
	 */
	public Room getRoom(String appId, String roomNr) {
		String roomKey = this.getRoomKey(appId, roomNr);
		return this.getRoom(roomKey);
	}

	/**
	 * 获取需要清理的room
	 * 
	 * @return
	 */
	public List<Room> getCleanRooms(Date cleanDate) {
		return this.roomDao.findByCreateTimeLessThan(cleanDate);
	}

	/**
	 * 创建room
	 * 
	 * @param appId
	 * @param roomNr
	 * @return
	 */
	@Transactional
	public Room createRoom(String appId, String roomNr) {
		String roomKey = this.getRoomKey(appId, roomNr);
		// 创建room
		Room room = new Room();
		room.setRoomKey(roomKey);
		room.setAppId(appId);
		room.setRoomNr(roomNr);
		room.setCreateTime(new Date());
		room = this.roomDao.save(room);
		// 创建page
		this.pageService.createPage(roomKey);
		return room;
	}

	/**
	 * 销毁room
	 * 
	 * @param appId
	 * @param roomNr
	 */
	@Transactional
	public void destroyRoom(String appId, String roomNr) {
		String roomKey = this.getRoomKey(appId, roomNr);
		this.destroyRoom(roomKey);
	}

	/**
	 * 销毁room
	 * 
	 * @param roomKey
	 */
	@Transactional
	public void destroyRoom(String roomKey) {
		// 删除page，包括print
		this.pageService.cleanRoomPages(roomKey);
		// 删除room
		this.roomDao.deleteByRoomKey(roomKey);
	}

	/**
	 * 获取roomKey
	 * 
	 * @param appId
	 * @param roomNr
	 * @return
	 */
	private String getRoomKey(String appId, String roomNr) {
		return appId + roomNr;
	}

}
