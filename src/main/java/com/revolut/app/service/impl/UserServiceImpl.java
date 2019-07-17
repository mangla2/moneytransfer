package com.revolut.app.service.impl;

import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.util.StringUtils;

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
		String currencyCode = (StringUtils.isNullOrEmpty(user.getCurrencyCode()))  ? "INR" : user.getCurrencyCode();
		resp = accountDao.createAccount(new Account(user.getEmail(), userId, new BigDecimal(5000), currencyCode));
		if(!resp.isStatus()){
			userDao.deleteUser(user.getEmail());
			return new AppResponse(false, "User cannot be created", new ErrorDetails(Constants.ERROR_CODE_PROCESSING, "Account failed to create hence deleting the user"));
		}
		Account acc = (Account)resp.getData();
		acc.setEmail(null);
		user.getAccounts().add(acc);
		return new AppResponse(true,user);
	}

	@Override
	public AppResponse getAllUsers() {
		Logger.debug("Starting getAllUsers()");
		return userDao.getAllUsers();
	}

	@Override
	public AppResponse deleteUser(String email) {
		Logger.debug("Starting deleteUser() in UserServiceImpl");
		AppResponse resp = null;

		if(StringUtils.isNullOrEmpty(email)){
			Logger.error("Failed to delete as the email is null/empty");
			return new AppResponse(false,"Email cannot be null/empty", new ErrorDetails(Constants.ERROR_CODE_VALIDATION,"Email is found null/empty"));
		}

		// check if user exists or not
		resp = userDao.getUserByEmail(email);
		if(!resp.isStatus() || resp.getData() == null){
			Logger.error("Failed to delete as the user is not found");
			return new AppResponse(false,"Failed to delete as the user is not found", new ErrorDetails(Constants.ERROR_CODE_VALIDATION,"User is not found in db with requested email address"));
		}

		// first delete all accounts of user
		resp = userDao.deleteAccountsByUser(email);
		if(!resp.isStatus()){
			Logger.error("Failed to delete user accounts having email [{}]", email);
			return new AppResponse(false, "Failed to delete the user", resp.getError());
		}

		// if success, now deleting the user
		return userDao.deleteUser(email);
	}

	@Override
	public AppResponse getUser(String email) {
		Logger.debug("Starting getUser() in UserServiceImpl for email {}", email);
		AppResponse resp = null;

		if(StringUtils.isNullOrEmpty(email)){
			Logger.error("Failed to get user as the email is null/empty");
			return new AppResponse(false,"Email cannot be null/empty", new ErrorDetails(Constants.ERROR_CODE_VALIDATION,"Email is found null/empty"));
		}

		// check if user exists or not
		resp = userDao.getUserByEmail(email);
		if(!resp.isStatus() || resp.getData() == null){
			Logger.error("User is not found");
			return new AppResponse(false,"User Not found", new ErrorDetails(Constants.ERROR_CODE_VALIDATION,"User is not found in db for requested email address"));
		}

		Logger.debug("Returning found user successfully for email {}", email);
		return resp;
	}

	@Override
	public AppResponse getAccountsByUser(String email) {
		Logger.debug("Starting getAccountsByUser() in UserServiceImpl for email {}", email);
		AppResponse resp = null;

		if(StringUtils.isNullOrEmpty(email)){
			Logger.error("Failed to get accounts as the email is null/empty");
			return new AppResponse(false,"Email cannot be null/empty", new ErrorDetails(Constants.ERROR_CODE_VALIDATION,"Email is found null/empty"));
		}

		// check if user exists or not
		resp = userDao.getUserByEmail(email);
		if(!resp.isStatus() || resp.getData() == null){
			Logger.error("User is not found");
			return new AppResponse(false,"User Not found", new ErrorDetails(Constants.ERROR_CODE_VALIDATION,"User is not found in db for requested email address"));
		}
		
		return userDao.getAllAccountByUser(email);
	}
}
