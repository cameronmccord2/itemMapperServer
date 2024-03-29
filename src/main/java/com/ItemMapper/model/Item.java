package com.ItemMapper.model;

import java.util.List;

public class Item {
	protected Integer id;
	protected List<ItemHistory> historyList;
	protected List<ItemLocation> locationList;
	protected List<ItemCode> codeList;
	protected String name;
	protected String userComment;
	protected Integer status;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public List<ItemHistory> getHistoryList() {
		return historyList;
	}
	public void setHistoryList(List<ItemHistory> historyList) {
		this.historyList = historyList;
	}
	public List<ItemLocation> getLocationList() {
		return locationList;
	}
	public void setLocationList(List<ItemLocation> locationList) {
		this.locationList = locationList;
	}
	public List<ItemCode> getCodeList() {
		return codeList;
	}
	public void setCodeList(List<ItemCode> codeList) {
		this.codeList = codeList;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserComment() {
		return userComment;
	}
	public void setUserComment(String userComment) {
		this.userComment = userComment;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
}
