package com.blink.ewb.entity;

import java.util.List;

public class PageData {

	private Page page;

	private List<Page> pages;

	// private List<Long> pageIds;

	private List<Print> prints;

	private LaserPrint laserPrint;

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public List<Page> getPages() {
		return pages;
	}

	public void setPages(List<Page> pages) {
		this.pages = pages;
	}

	// public List<Long> getPageIds() {
	// return pageIds;
	// }
	//
	// public void setPageIds(List<Long> pageIds) {
	// this.pageIds = pageIds;
	// }

	public List<Print> getPrints() {
		return prints;
	}

	public void setPrints(List<Print> prints) {
		this.prints = prints;
	}

	public LaserPrint getLaserPrint() {
		return laserPrint;
	}

	public void setLaserPrint(LaserPrint laserPrint) {
		this.laserPrint = laserPrint;
	}

}
