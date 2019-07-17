package com.revolut.app.model;

import org.json.JSONObject;

public class FixerResponse {

	private boolean success;
	private String timestamp;
	private String base;
	private String date;
	private Object rates;
	private FixerError error;
	
	/**
	 * @param success
	 * @param error
	 */
	public FixerResponse(boolean success, FixerError error) {
		super();
		this.success = success;
		this.error = error;
	}
	/**
	 * 
	 */
	public FixerResponse() {
		super();
	}
	/**
	 * @return the error
	 */
	public FixerError getError() {
		return error;
	}
	/**
	 * @param error the error to set
	 */
	public void setError(FixerError error) {
		this.error = error;
	}
	/**
	 * @return the success
	 */
	public boolean getSuccess() {
		return success;
	}
	/**
	 * @param success the success to set
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}
	/**
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	/**
	 * @return the base
	 */
	public String getBase() {
		return base;
	}
	/**
	 * @param base the base to set
	 */
	public void setBase(String base) {
		this.base = base;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * @return the rates
	 */
	public Object getRates() {
		return rates;
	}
	/**
	 * @param rates the rates to set
	 */
	public void setRates(Object rates) {
		this.rates = rates;
	}
	
}
