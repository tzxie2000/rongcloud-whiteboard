package com.blink.ewb.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "page")
public class Page {

	private long pageId;

	private String roomKey;

	private Date createTime;

	private String fileGroup;

	private String fileName;

	private String fileUrl;

	private String thumbnail;

	@Id
	@GeneratedValue
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

	@Column(name = "createtime")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "filegroup")
	public String getFileGroup() {
		return fileGroup;
	}

	public void setFileGroup(String fileGroup) {
		this.fileGroup = fileGroup;
	}

	@Column(name = "filename")
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Column(name = "fileurl")
	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	@Column(name = "thumbnail")
	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

}
