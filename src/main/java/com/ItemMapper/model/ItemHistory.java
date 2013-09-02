package com.ItemMapper.model;

import java.sql.Date;

public class ItemHistory {
	protected Integer id;
	protected Integer what;
	protected Date when;
	protected String details;
	protected Integer itemId;
	protected Integer userId;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getWhat() {
		return what;
	}
	public void setWhat(Integer what) {
		this.what = what;
	}
	public Date getWhen() {
		return when;
	}
	public void setWhen(Date when) {
		this.when = when;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public Integer getItemId() {
		return itemId;
	}
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
}
