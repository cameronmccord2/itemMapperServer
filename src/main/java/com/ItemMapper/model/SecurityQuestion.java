package com.ItemMapper.model;

import java.sql.Date;

public class SecurityQuestion {
	protected Integer id;
	protected String question;
	protected String answer;
	protected Date created;
	protected Integer userId;
	protected Integer timesCorrect;
	protected Integer timesIncorrect;
	protected Integer status;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getTimesCorrect() {
		return timesCorrect;
	}
	public void setTimesCorrect(Integer timesCorrect) {
		this.timesCorrect = timesCorrect;
	}
	public Integer getTimesIncorrect() {
		return timesIncorrect;
	}
	public void setTimesIncorrect(Integer timesIncorrect) {
		this.timesIncorrect = timesIncorrect;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
}
