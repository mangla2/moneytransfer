package com.revolut.app.service.impl;

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
import com.revolut.app.service.AccountService;

public class AccountServiceImpl implements AccountService {

	private static final Logger Logger = LogManager.getLogger(AccountServiceImpl.class);
	private UserDao userDao = null;
	private AccountDao accountDao = null;
	private static AccountServiceImpl instance = null;

	private AccountServiceImpl(){
		userDao = UserDaoImpl.getInstance();
		accountDao = AccountDaoImpl.getInstance();
	}

	public static AccountServiceImpl getInstance() {
		if(instance == null){
			synchronized(UserServiceImpl.class){
				if(instance == null){
					instance = new AccountServiceImpl();
				}
			}
		}
		return instance;
	}

	@Override
	public AppResponse getAllAccounts() {
		Logger.debug("Starting getAllAccounts in AccountServiceImpl");
		return accountDao.getAllAccounts();
	}

	@Override
	public AppResponse createAccount(Account account) {
		Logger.debug("Starting createAccount in AccountServiceImpl");

		if(account == null) {
			Logger.error("Requested payload for creating account is null");
			return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_VALIDATION, "Request Payload found is null"));
		}

		//check if the user exists or not
		if(StringUtils.isNullOrEmpty(account.getEmail())){
			Logger.error("User Email is found null or empty");
			return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_VALIDATION, "User Email is found null or empty"));
		}
		
		AppResponse resp = userDao.getUserByEmail(account.getEmail());
		if(!resp.isStatus() || resp.getData() == null){
			Logger.error("Account cannot be created since user is not registered having email [{}]", account.getEmail());
			return new AppResponse(false, "Account cannot be created since user is not registered",new ErrorDetails(Constants.ERROR_CODE_VALIDATION, "User is not registered"));
		}
		
		User u = (User) resp.getData();
		account.setUserId(u.getId());
		return accountDao.createAccount(account);
	}


}
