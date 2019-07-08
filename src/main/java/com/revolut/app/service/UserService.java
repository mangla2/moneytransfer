package com.revolut.app.service;

import com.revolut.app.model.AppResponse;
import com.revolut.app.model.User;

public interface UserService {

	public AppResponse createUser(User user);
	public AppResponse getAllUsers();
}
