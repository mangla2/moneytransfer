package com.revolut.app.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Transaction {

	private String transactionId;
	private BigDecimal amount;
	private Account from;
	private Account to;
	private String accountFrom;
	private String accountTo;
	private TRANSACTION_TYPE type;
	private String notes;
	private String createdAt;
	private String currencyCode;
	
	public enum TRANSACTION_TYPE {
		DEBIT, CREDIT
	}
	
	public Transaction(){
	 super();	
	}
	
	/**
	 * @param transactionId
	 * @param debitAmount
	 * @param from
	 * @param to
	 * @param notes
	 * @param createdAt
	 */
	public Transaction(String transactionId, BigDecimal debitAmount, String from, String to, String notes,
			String createdAt) {
		super();
		this.transactionId = transactionId;
		this.amount = debitAmount;
		this.accountFrom = from;
		this.accountTo = to;
		this.notes = notes;
		this.createdAt = createdAt;
	}
	
	/**
	 * @param amount
	 * @param from
	 * @param to
	 * @param notes
	 */
	public Transaction(BigDecimal amount, Account from, Account to, String notes) {
		super();
		this.amount = amount;
		this.from = from;
		this.to = to;
		this.notes = notes;
	}

	/**
	 * @param transactionId
	 * @param debitAmount
	 * @param createdAt
	 */
	public Transaction(String transactionId, BigDecimal debitAmount, String createdAt) {
		super();
		this.transactionId = transactionId;
		this.amount = debitAmount;
		this.createdAt = createdAt;
	}

	/**
	 * @return the transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}
	/**
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
	/**
	 * @return the from
	 */
	public Account getFrom() {
		return from;
	}
	/**
	 * @param from the from to set
	 */
	public void setFrom(Account from) {
		this.from = from;
	}
	/**
	 * @return the to
	 */
	public Account getTo() {
		return to;
	}
	/**
	 * @param to the to to set
	 */
	public void setTo(Account to) {
		this.to = to;
	}
	/**
	 * @return the notes
	 */
	public String getNotes() {
		return notes;
	}
	/**
	 * @param notes the notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * @return the createdAt
	 */
	public String getCreatedAt() {
		return createdAt;
	}
	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * @return the accountFrom
	 */
	public String getAccountFrom() {
		return accountFrom;
	}

	/**
	 * @param accountFrom the accountFrom to set
	 */
	public void setAccountFrom(String accountFrom) {
		this.accountFrom = accountFrom;
	}

	/**
	 * @return the accountTo
	 */
	public String getAccountTo() {
		return accountTo;
	}

	/**
	 * @param accountTo the accountTo to set
	 */
	public void setAccountTo(String accountTo) {
		this.accountTo = accountTo;
	}

	/**
	 * @return the type
	 */
	public TRANSACTION_TYPE getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(TRANSACTION_TYPE type) {
		this.type = type;
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

}
