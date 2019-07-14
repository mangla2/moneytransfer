package com.revolut.app.model;

public class FixerError {

	private String code;
	private String type;
	
	/**
	 * @param code
	 * @param type
	 */
	public FixerError(String code, String type) {
		super();
		this.code = code;
		this.type = type;
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
}
