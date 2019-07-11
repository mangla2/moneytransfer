package com.revolut.app.service;

import com.revolut.app.model.Account;
import com.revolut.app.model.AppResponse;

public interface AccountService {
	public AppResponse getAllAccounts();
	public AppResponse createAccount(Account account);
	public AppResponse getAccountByAccountNumber(String accountNumber);
	public AppResponse deleteAccountByAccountNumber(String accountNumber);
}
