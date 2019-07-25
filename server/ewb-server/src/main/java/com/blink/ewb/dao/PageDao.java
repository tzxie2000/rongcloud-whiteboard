package com.blink.ewb.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.blink.ewb.entity.Page;

@Repository
public interface PageDao extends BaseDao<Page, Long> {

	/**
	 * 查询page
	 * 
	 * @param roomKey
	 * @param pageId
	 * @return
	 */
	Page findByRoomKeyAndPageId(String roomKey, Long pageId);

	/**
	 * 查询最后一个page
	 * 
	 * @param roomKey
	 * @return
	 */
	Page findFirstByRoomKeyOrderByPageIdDesc(String roomKey);

	/**
	 * room的page数
	 * 
	 * @param roomKey
	 * @return
	 */
	long countByRoomKey(String roomKey);

	/**
	 * room的page集合
	 * 
	 * @param roomKey
	 * @return
	 */
	List<Page> findByRoomKey(String roomKey);

	/**
	 * 删除page
	 * 
	 * @param roomKey
	 * @param pageId
	 */
	void deleteByRoomKeyAndPageId(String roomKey, long pageId);

	/**
	 * 删除room的page
	 * 
	 * @param roomKey
	 */
	void deleteByRoomKey(String roomKey);

}
