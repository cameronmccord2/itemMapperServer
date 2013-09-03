package com.ItemMapper.model;

import java.util.List;

public class ItemListElement {
	protected Integer id;
	protected List<ItemLocation> locationList;
	protected String code;
	protected String name;
	protected String userComment;
	protected Integer status;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public List<ItemLocation> getLocationList() {
		return locationList;
	}
	public void setLocationList(List<ItemLocation> locationList) {
		this.locationList = locationList;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
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
