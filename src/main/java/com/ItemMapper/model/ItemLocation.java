package com.ItemMapper.model;

import java.sql.Date;

public class ItemLocation {
	protected Integer id;
	protected String longitude;
	protected String latitude;
	protected Date whenSet;
	protected Integer userId;
	protected Integer itemId;
	protected Integer howSet;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public Date getWhenSet() {
		return whenSet;
	}
	public void setWhenSet(Date whenSet) {
		this.whenSet = whenSet;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getItemId() {
		return itemId;
	}
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	public Integer getHowSet() {
		return howSet;
	}
	public void setHowSet(Integer howSet) {
		this.howSet = howSet;
	}
}
