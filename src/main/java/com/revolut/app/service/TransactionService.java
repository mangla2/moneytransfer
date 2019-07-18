package com.revolut.app.service;

import com.revolut.app.model.AppResponse;

public interface TransactionService {

	public AppResponse transferMoney(String transaction);
}
