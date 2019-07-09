package com.revolut.app.model;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Account {

	private String accountNumber;
	
	@JsonProperty(required=true)
	private String email;
	
	@JsonIgnore
	private long userId;
	
	@JsonProperty(required=true)
	private BigDecimal balance;
	
	@JsonProperty(required=true)
	private String currencyCode;

	public Account(){
		
	}
	
	/**
	 * @param email
	 * @param userId
	 * @param balance
	 * @param currencyCode
	 */
	public Account(String email, long userId, BigDecimal balance, String currencyCode) {
		super();
		this.accountNumber = generateAccountNumber(email);
		this.email = email;
		this.userId = userId;
		this.balance = balance;
		this.currencyCode = currencyCode;
	}

	/**
	 * @param email
	 * @param balance
	 * @param currencyCode
	 */
	public Account(String email, BigDecimal balance, String currencyCode) {
		super();
		this.accountNumber = generateAccountNumber(email);
		this.email = email;
		this.balance = balance;
		this.currencyCode = currencyCode;
	}

	/**
	 * @param accountNumber
	 * @param email
	 * @param balance
	 * @param currencyCode
	 */
	public Account(String accountNumber, String email, BigDecimal balance, String currencyCode) {
		super();
		this.accountNumber = accountNumber;
		this.email = email;
		this.balance = balance;
		this.currencyCode = currencyCode;
	}


	/**
	 * @return the accountNumber
	 */
	public String getAccountNumber() {
		return accountNumber;
	}

	/**
	 * @param accountNumber the accountNumber to set
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the balance
	 */
	public BigDecimal getBalance() {
		return balance;
	}

	/**
	 * @param balance the balance to set
	 */
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	/**
	 * @return the currencyCode
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * @param currencyCode the currencyCode to set
	 */
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	
	
	/**
	 * @return the userId
	 */
	public long getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}

	private String generateAccountNumber(String email) {
		long now = Instant.now().toEpochMilli();
		int emailHash = email.hashCode();
		int rand = (int) ((Math.random() * ((now-emailHash)*10)));
		rand = rand + emailHash*10;
		return String.valueOf(rand);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Account [accountNumber=" + accountNumber + ", email=" + email + ", userId=" + userId + ", balance="
				+ balance + ", currencyCode=" + currencyCode + "]";
	}
	
	
}
