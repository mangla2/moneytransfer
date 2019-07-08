package com.revolut.app.constants;

public class DbQueries {

	public static final String SAVE_USER = "INSERT INTO users (firstName, lastName, email) VALUES (?, ?, ?)";
	public static final String SELECT_ALL_USERS = "SELECT * FROM users";
	public static final String GET_USER_BY_EMAIL = "SELECT * FROM users WHERE email = ?";
  
}
