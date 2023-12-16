package com.watermelon.model.response;

public class ResponseObject {
	private Object data;
	private int status;
	private String title;
	private String message;
	public ResponseObject(Object data, int status, String title,String message) {
		super();
		this.data = data;
		this.status = status;
		this.title = title;
		this.message=message;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
