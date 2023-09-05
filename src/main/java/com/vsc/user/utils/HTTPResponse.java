package com.vsc.user.utils;

public class HTTPResponse {
	private int statusCode;
	private String message;
	private String errorMsg;
	
	public HTTPResponse(int statusCode, String message, String errorMsg) {
		super();
		this.statusCode = statusCode;
		this.message = message;
		this.errorMsg = errorMsg;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
