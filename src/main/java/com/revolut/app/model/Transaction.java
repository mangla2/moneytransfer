package com.revolut.app.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class Transaction {

	private String transactionId;
	private BigDecimal debitAmount;
	private BigDecimal creditAmount;
	private Account from;
	private Account to;
	private String notes;
	private ZonedDateTime createdAt;
	
	public Transaction(Account accountFrom, Account accountTo, BigDecimal debitAmt, BigDecimal creditAmt, String notes) {
        this.from = accountFrom;
        this.to = accountTo;
        this.debitAmount = debitAmt;
        this.creditAmount= creditAmt;
        this.notes = notes;
        this.createdAt = ZonedDateTime.now();
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
	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}
	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(ZonedDateTime createdAt) {
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
