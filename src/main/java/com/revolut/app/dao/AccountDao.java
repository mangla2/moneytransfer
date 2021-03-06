package com.revolut.app.dao;

import java.math.BigDecimal;

import com.revolut.app.model.Account;
import com.revolut.app.model.AppResponse;
import com.revolut.app.model.Transaction;
import com.revolut.app.model.Transaction.TRANSACTION_TYPE;

public interface AccountDao {

	public AppResponse createAccount(Account account);
	public AppResponse getAllAccounts();
	public AppResponse getAccountByAccountNumber(String accountNumber);
	public AppResponse deleteAccountByAccountNumber(String accountNumber);
	public AppResponse makeTransaction(Transaction transaction,BigDecimal amountConverted);
	public AppResponse updateBalance(Account account, BigDecimal amount, TRANSACTION_TYPE type);
	public AppResponse getTransactionsByAccount(String accountNumber);
	public AppResponse getTransactionByTransactionId(String transactionId);
}
