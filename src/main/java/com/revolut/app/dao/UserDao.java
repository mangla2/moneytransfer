package com.revolut.app.dao;

import com.revolut.app.model.AppResponse;
import com.revolut.app.model.User;

public interface UserDao {

	public AppResponse saveUser(User user);
	public AppResponse getAllUsers();
	public AppResponse getUserByEmail(String email);
}
