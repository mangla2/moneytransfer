package com.revolut.app.dao;

import java.sql.SQLException;
import java.util.List;

import com.revolut.app.model.User;

public interface UserDao {

	int create(final User user) throws SQLException;
	public List<User> getAll() throws SQLException;
}
