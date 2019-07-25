package com.blink.ewb.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.blink.ewb.entity.Print;

@Repository
public interface PrintDao extends BaseDao<Print, Long> {

	/**
	 * page的print数
	 * 
	 * @param roomKey
	 * @param pageId
	 * @return
	 */
	long countByRoomKeyAndPageId(String roomKey, Long pageId);

	/**
	 * page的某个print之后的print集合
	 * 
	 * @param roomKey
	 * @param pageId
	 * @param printId
	 * @return
	 */
	List<Print> findByRoomKeyAndPageIdAndPrintIdGreaterThan(String roomKey, Long pageId, Long printId);

	/**
	 * page的某个print之后且不属于某个user的print集合
	 * 
	 * @param roomKey
	 * @param pageId
	 * @param printId
	 * @param userId
	 * @return
	 */
	List<Print> findByRoomKeyAndPageIdAndPrintIdGreaterThanAndUserIdNot(String roomKey, Long pageId, Long printId,
			String userId);

	/**
	 * 删除page的print
	 * 
	 * @param roomKey
	 * @param pageId
	 */
	void deleteByRoomKeyAndPageId(String roomKey, Long pageId);

	/**
	 * 删除room的print
	 * 
	 * @param roomKey
	 */
	void deleteByRoomKey(String roomKey);

}
