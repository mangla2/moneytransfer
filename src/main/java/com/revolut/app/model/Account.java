package com.revolut.app.model;

import java.math.BigDecimal;
import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
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

	/**
	 * @return the transactionsList
	 */
	public List<Transaction> getTransactionsList() {
		return transactionsList;
	}

	private List<Transaction> transactionsList;

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
		this.transactionsList = new ArrayList<>();
	}

	public Account(long userId, String accountNumber, BigDecimal balance, String currencyCode) {
		super();
		this.accountNumber = accountNumber;
		this.userId = userId;
		this.balance = balance;
		this.currencyCode = currencyCode;
		this.transactionsList = new ArrayList<>();
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
		this.transactionsList = new ArrayList<>();
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

	public String generateAccountNumber(String email) {
		int TOTAL_BITS = 64;
		int EPOCH_BITS = 42;
		int NODE_ID_BITS = 10;

		int maxNodeId = (int)(Math.pow(2, NODE_ID_BITS) - 1);
		long CUSTOM_EPOCH = 1420070400000L;

		long sequence = 0L;

		int nodeId = createNodeId(maxNodeId);
		long currentTimestamp = Instant.now().toEpochMilli() - CUSTOM_EPOCH;
		long id = currentTimestamp << (TOTAL_BITS - EPOCH_BITS);
		id |= (nodeId << (TOTAL_BITS - EPOCH_BITS - NODE_ID_BITS));
		id |= sequence;

		if(id <= 0){
			id = Long.valueOf(generateAccountNumber(email));
		}
		return String.valueOf(id);
	}

	private int createNodeId(int maxNodeId) {
		int nodeId;
		try {
			StringBuilder sb = new StringBuilder();
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = networkInterfaces.nextElement();
				byte[] mac = networkInterface.getHardwareAddress();
				if (mac != null) {
					for(int i = 0; i < mac.length; i++) {
						sb.append(String.format("%02X", mac[i]));
					}
				}
			}
			nodeId = sb.toString().hashCode();
		} catch (Exception ex) {
			nodeId = (new SecureRandom().nextInt());
		}
		nodeId = nodeId & maxNodeId;
		return nodeId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Account [accountNumber=" + accountNumber + ", email=" + email + ", balance="
				+ balance + ", currencyCode=" + currencyCode + "]";
	}


}
