package com.revolut.app.model;

import java.math.BigDecimal;
import java.util.List;

public class BankStatement {

	private BigDecimal balance;
	private String currencyCode;
	private List<Transaction> transactionList;
	
	public BankStatement() {
		super();
	}
	
	/**
	 * @param balance
	 * @param transactionList
	 */
	public BankStatement(BigDecimal balance, String currencyCode, List<Transaction> transactionList) {
		super();
		this.balance = balance;
		this.currencyCode = currencyCode;
		this.transactionList = transactionList;
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
	 * @return the transactionList
	 */
	public List<Transaction> getTransactionList() {
		return transactionList;
	}
	/**
	 * @param transactionList the transactionList to set
	 */
	public void setTransactionList(List<Transaction> transactionList) {
		this.transactionList = transactionList;
	}
	
}
