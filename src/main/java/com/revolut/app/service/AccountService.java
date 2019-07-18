package com.revolut.app.service;

import java.math.BigDecimal;

import com.revolut.app.model.Account;
import com.revolut.app.model.AppResponse;

public interface AccountService {
	public AppResponse getAllAccounts();
	public AppResponse createAccount(Account account);
	public AppResponse getAccountByAccountNumber(String accountNumber);
	public AppResponse deleteAccountByAccountNumber(String accountNumber);
	public AppResponse deposit(String accountNumber,BigDecimal amount);
	public AppResponse withdraw(String accountNumber,BigDecimal amount);
	public AppResponse getTransactionHistoryByAccount(String accountNumber);
}
