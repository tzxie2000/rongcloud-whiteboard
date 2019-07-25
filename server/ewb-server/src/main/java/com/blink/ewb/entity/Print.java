package com.blink.ewb.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "print")
public class Print {

	private long printId;

	private long pageId;

	private String roomKey;

	private String data;

	private Date createTime;

	private String userId;

	@Id
	@GeneratedValue
	@Column(name = "printid")
	public long getPrintId() {
		return printId;
	}

	public void setPrintId(long printId) {
		this.printId = printId;
	}

	@Column(name = "pageid")
	public long getPageId() {
		return pageId;
	}

	public void setPageId(long pageId) {
		this.pageId = pageId;
	}

	@Column(name = "roomkey")
	public String getRoomKey() {
		return roomKey;
	}

	public void setRoomKey(String roomKey) {
		this.roomKey = roomKey;
	}

	@Column(name = "data")
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Column(name = "createtime")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "userid")
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
