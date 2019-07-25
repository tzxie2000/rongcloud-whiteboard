package com.blink.ewb.dao;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.blink.ewb.entity.Room;

@Repository
public interface RoomDao extends BaseDao<Room, Long> {

	/**
	 * 查询某个时间点之前创建的room
	 * 
	 * @param date
	 * @return
	 */
	List<Room> findByCreateTimeLessThan(Date date);

	/**
	 * 查询room
	 * 
	 * @param roomKey
	 * @return
	 */
	Room findByRoomKey(String roomKey);

	/**
	 * 删除room
	 * 
	 * @param roomKey
	 */
	void deleteByRoomKey(String roomKey);
}
