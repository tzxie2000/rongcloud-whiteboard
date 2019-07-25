package com.blink.ewb.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blink.ewb.Config;
import com.blink.ewb.entity.Room;

@Service
public class DataService {

	@Autowired
	private RoomService roomService;

	/**
	 * 清理数据
	 * 
	 * @param roomKey
	 * @return
	 */
	public void cleanData() {
		long nowTime = new Date().getTime();
		long cleanTime = nowTime - Config.clean_expiration * 24 * 60 * 60 * 1000;
		Date cleanDate = new Date(cleanTime);
		List<Room> roomList = this.roomService.getCleanRooms(cleanDate);
		if (roomList != null && !roomList.isEmpty()) {
			for (Room room : roomList) {
				this.roomService.destroyRoom(room.getRoomKey());
			}
		}
	}

}
