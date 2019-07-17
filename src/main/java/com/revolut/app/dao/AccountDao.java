package com.revolut.app.dao;

import com.revolut.app.model.Account;
import com.revolut.app.model.AppResponse;
import com.revolut.app.model.Transaction;

public interface AccountDao {

	public AppResponse createAccount(Account account);
	public AppResponse getAllAccounts();
	public AppResponse getAccountByAccountNumber(String accountNumber);
	public AppResponse deleteAccountByAccountNumber(String accountNumber);
	public AppResponse makeTrasaction(Transaction transaction);
	public AppResponse getTransactionsByAccount(String accountNumber);
	public AppResponse getTransactionByTransactionId(String transactionId);
}
