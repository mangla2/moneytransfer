package com.revolut.app.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppResponse {

	private boolean status;
	private Object data;
	private String message;
	private ErrorDetails error;
	
	
	/**
	 * @param status
	 * @param data
	 */
	public AppResponse(boolean status, Object data) {
		super();
		this.status = status;
		this.data = data;
	}

	/**
	 * @param status
	 * @param data
	 * @param message
	 */
	public AppResponse(boolean status, Object data, String message) {
		super();
		this.status = status;
		this.data = data;
		this.message = message;
	}
	
	/**
	 * @param status
	 * @param error
	 */
	public AppResponse(boolean status, ErrorDetails error) {
		super();
		this.status = status;
		this.error = error;
	}
	
	/**
	 * @param status
	 * @param message
	 * @param error
	 */
	public AppResponse(boolean status, String message, ErrorDetails error) {
		super();
		this.status = status;
		this.message = message;
		this.error = error;
	}

	/**
	 * @return the status
	 */
	public boolean isStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}
	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(Object data) {
		this.data = data;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the error
	 */
	public ErrorDetails getError() {
		return error;
	}
	/**
	 * @param error the error to set
	 */
	public void setError(ErrorDetails error) {
		this.error = error;
	}
	
}
