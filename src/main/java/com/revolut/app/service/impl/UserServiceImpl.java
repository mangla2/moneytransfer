package com.revolut.app.service.impl;

import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revolut.app.constants.Constants;
import com.revolut.app.dao.AccountDao;
import com.revolut.app.dao.AccountDaoImpl;
import com.revolut.app.dao.UserDao;
import com.revolut.app.dao.UserDaoImpl;
import com.revolut.app.model.Account;
import com.revolut.app.model.AppResponse;
import com.revolut.app.model.ErrorDetails;
import com.revolut.app.model.User;
import com.revolut.app.service.UserService;

public class UserServiceImpl implements UserService {

	private static final Logger Logger = LogManager.getLogger(UserServiceImpl.class);
	private UserDao userDao = null;
	private AccountDao accountDao = null;
    private static UserServiceImpl instance = null;
    
	private UserServiceImpl(){
		userDao = UserDaoImpl.getInstance();
		accountDao = AccountDaoImpl.getInstance();
	}

	public static UserServiceImpl getInstance() {
		if(instance == null){
			synchronized(UserServiceImpl.class){
				if(instance == null){
					instance = new UserServiceImpl();
				}
			}
		}
		return instance;
	} 
	
	@Override
	public AppResponse createUser(User user) {
		Logger.debug("Starting createUser()", user);
		AppResponse resp = null;
		
		if(user == null){
			return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_VALIDATION, "User found is null"));
		}

		Logger.info("Checking whether user exists with requested email {} or not", user.getEmail());
		resp = userDao.getUserByEmail(user.getEmail());
		if(resp.getData() != null) {
			Logger.info("User already exists");
			return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_NONE, "User already exists"));
		}

		resp = userDao.saveUser(user);
		if(!resp.isStatus()) {
			return resp;
		}
		
		Logger.info("Creating an account for user having email {}", user.getEmail());
		long userId = user.getId();
		resp = accountDao.createAccount(new Account(user.getEmail(), userId, new BigDecimal(5000), "INR"));
		if(!resp.isStatus()){
			userDao.deleteUser(user);
		}
		user.getAccounts().add((Account)resp.getData());
		return new AppResponse(true,user);
	}
    
	@Override
	public AppResponse getAllUsers() {
		Logger.debug("Starting getAllUsers()");
		return userDao.getAllUsers();
	}
}
