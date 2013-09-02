package com.ItemMapper.model;

import java.sql.Date;
import java.util.ArrayList;

public class User {
	protected Integer id;
	protected ArrayList<Item> items;
	protected ArrayList<SecurityQuestion> securityQuestions;
	protected String firstName;
	protected String lastName;
	protected String email;
	protected String password;
	protected Date created;
	protected Integer status;
	protected Integer type;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public ArrayList<Item> getItems() {
		return items;
	}
	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}
	public ArrayList<SecurityQuestion> getSecurityQuestions() {
		return securityQuestions;
	}
	public void setSecurityQuestions(ArrayList<SecurityQuestion> securityQuestions) {
		this.securityQuestions = securityQuestions;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
}
