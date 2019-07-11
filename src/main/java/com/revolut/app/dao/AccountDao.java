package com.revolut.app.dao;

import com.revolut.app.model.Account;
import com.revolut.app.model.AppResponse;

public interface AccountDao {

	public AppResponse createAccount(Account account);
	public AppResponse getAllAccounts();
	public AppResponse getAccountByAccountNumber(String accountNumber);
	public AppResponse deleteAccountByAccountNumber(String accountNumber);
}
