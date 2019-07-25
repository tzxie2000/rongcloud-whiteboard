package com.blink.ewb.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "room")
public class Room {

	private String roomKey;

	private String appId;

	private String roomNr;

	private Date createTime;

	@Id
	@Column(name = "roomkey")
	public String getRoomKey() {
		return roomKey;
	}

	public void setRoomKey(String roomKey) {
		this.roomKey = roomKey;
	}

	@Column(name = "appid")
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	@Column(name = "roomnr")
	public String getRoomNr() {
		return roomNr;
	}

	public void setRoomNr(String roomNr) {
		this.roomNr = roomNr;
	}

	@Column(name = "createtime")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
