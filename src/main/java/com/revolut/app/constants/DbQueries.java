package com.revolut.app.constants;

public class DbQueries {

	public static final String SAVE_USER = "INSERT INTO users (firstName, lastName, email) VALUES (?, ?, ?)";
	public static final String SELECT_ALL_USERS = "SELECT * FROM users";
	public static final String GET_USER_BY_EMAIL = "SELECT * FROM users WHERE email = ?";
	public static final String CREATE_ACCOUNT = "INSERT INTO account (accountNumber, userId, balance, currencyCode) VALUES (?, ?, ?, ?)";
	public static final String GET_ALL_ACCOUNTS = "SELECT Acc.accountNumber, U.email,Acc.balance,Acc.currencyCode FROM account Acc JOIN users U ON Acc.userId = U.id";
	public static final String DELETE_USER = "DELETE FROM users WHERE email =?";
}
