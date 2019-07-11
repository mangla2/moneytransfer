package com.revolut.app.constants;

public class DbQueries {

	public static final String SAVE_USER = "INSERT INTO users (firstName, lastName, email) VALUES (?, ?, ?)";
	public static final String SELECT_ALL_USERS = "SELECT * FROM users";
	public static final String GET_USER_BY_EMAIL = "SELECT * FROM users WHERE email = ?";
	public static final String CREATE_ACCOUNT = "INSERT INTO account (accountNumber, userId, balance, currencyCode) VALUES (?, ?, ?, ?)";
	public static final String GET_ALL_ACCOUNTS = "SELECT a.accountNumber, u.email, a.balance, a.currencyCode FROM account a JOIN users u ON a.userId = u.id";
	public static final String GET_ACCOUNT_BY_ACCOUNT_NUM = "SELECT * from account WHERE accountNumber = ?";
	public static final String GET_ACCOUNTS_BY_USER = "SELECT * from account where userId IN (SELECT id from users WHERE email = ?)";	
	public static final String DELETE_USER = "DELETE FROM users WHERE email =?";
	public static final String DELETE_ACCOUNT_BY_EMAIL = "DELETE FROM account WHERE userId IN (SELECT userId from users WHERE email = ?)";
	public static final String DELETE_ACCOUNT = "DELETE FROM account WHERE accountNumber =?";
}
