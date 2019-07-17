package com.revolut.app.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Transaction {

	private String transactionId;
	private BigDecimal debitAmount;
	private BigDecimal creditAmount;
	private Account from;
	private Account to;
	private String accountFrom;
	private String accountTo;
	private String notes;
	private String createdAt;
	
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
		this.debitAmount = debitAmount;
		this.accountFrom = from;
		this.accountTo = to;
		this.notes = notes;
		this.createdAt = createdAt;
	}
	
	/**
	 * @param transactionId
	 * @param debitAmount
	 * @param createdAt
	 */
	public Transaction(String transactionId, BigDecimal debitAmount, String createdAt) {
		super();
		this.transactionId = transactionId;
		this.debitAmount = debitAmount;
		this.createdAt = createdAt;
	}

	public Transaction(Account accountFrom, Account accountTo, BigDecimal debitAmt, BigDecimal creditAmt, String notes) {
        this.from = accountFrom;
        this.to = accountTo;
        this.debitAmount = debitAmt;
        this.creditAmount= creditAmt;
        this.notes = notes;
        this.createdAt = String.valueOf(ZonedDateTime.now().toInstant().getEpochSecond() * 1000L);
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
	 * @return the debitAmount
	 */
	public BigDecimal getDebitAmount() {
		return debitAmount;
	}

	/**
	 * @param debitAmount the debitAmount to set
	 */
	public void setDebitAmount(BigDecimal debitAmount) {
		this.debitAmount = debitAmount;
	}

	/**
	 * @return the creditAmount
	 */
	public BigDecimal getCreditAmount() {
		return creditAmount;
	}

	/**
	 * @param creditAmount the creditAmount to set
	 */
	public void setCreditAmount(BigDecimal creditAmount) {
		this.creditAmount = creditAmount;
	}
	
}
