package com.revolut.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revolut.app.config.DbUtils;
import com.revolut.app.constants.Constants;
import com.revolut.app.constants.DbQueries;
import com.revolut.app.model.Account;
import com.revolut.app.model.AppResponse;
import com.revolut.app.model.ErrorDetails;
import com.revolut.app.model.User;

public class AccountDaoImpl implements AccountDao {

	private static final Logger Logger = LogManager.getLogger(AccountDaoImpl.class);
	private static AccountDaoImpl instance = null;
	DbUtils dbConn = null;
	
	private AccountDaoImpl(){
		dbConn = DbUtils.getInstance();
	}

	public static AccountDaoImpl getInstance() {
		if(instance == null){
			synchronized(AccountDaoImpl.class){
				if(instance == null){
					instance = new AccountDaoImpl();
				}
			}
		}
		return instance;
	} 

	
	@Override
	public AppResponse createAccount(Account account) {
		Logger.debug("Starting createAccount in AccountDaoImpl", account);
		String accountNumber = account.generateAccountNumber(account.getEmail());
		LinkedHashMap<String,Object> criteria = new LinkedHashMap<>();
		criteria.put("accountNumber", accountNumber);
		criteria.put("userId", account.getUserId());
		criteria.put("balance", account.getBalance());
		criteria.put("currencyCode", account.getCurrencyCode());

		Logger.info("Creating new account for user id [{}] and email [{}]", account.getUserId(), account.getEmail());
		try (Connection connection = dbConn.getConnection();
				PreparedStatement statement = connection.prepareStatement(DbQueries.CREATE_ACCOUNT)) {
			dbConn.savePrepareStatement(connection, statement, criteria);
			int affectedRows = statement.executeUpdate();
			if (affectedRows == 0) {
				Logger.error("createAccount(): Creating account failed, no rows affected");
				return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_PROCESSING,"Creating account failed, no rows affected."));
			}
			account.setAccountNumber(accountNumber);
		}catch(SQLException e){
			Logger.error("Exception occured while creating a new account - {}", e.getMessage());
			return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_PROCESSING,"Exception occured while creating the account for user "+e.getMessage()));
		}
		Logger.info("New account created successfully with accountNumber: {} for userId: {}", account.getAccountNumber(), account.getUserId());
		return new AppResponse(true, account);
	}
	
	@Override
	public AppResponse getAllAccounts() {
		Logger.debug("Starting getAllAccounts in AccountDaoImpl");
		List<Account> accountlist = new ArrayList<>();

		try (Connection connection = dbConn.getConnection();
				PreparedStatement statement = connection.prepareStatement(DbQueries.GET_ALL_ACCOUNTS)){

			try (ResultSet rs = statement.executeQuery();) {
				while (rs.next()) {
					Account account = new Account();
					account.setAccountNumber(rs.getString(Constants.ACCOUNT_NUMBER));
					account.setEmail(rs.getString(Constants.USER_EMAIL));
					account.setBalance(rs.getBigDecimal(Constants.ACCOUNT_BALANCE));
					account.setCurrencyCode(rs.getString(Constants.ACCOUNT_CURRENCY_CODE));
					accountlist.add(account);
				}
			}catch(SQLException e){
				Logger.error("Exception occured while getting all the accounts", e.getMessage());
				return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_PROCESSING,"Exception occured while getting all the accounts "+e.getMessage()));
			}
		}catch(SQLException e){
			Logger.error("Exception occured while getting all the accounts", e.getMessage());
			return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_PROCESSING,"Exception occured while getting all accounts "+e.getMessage()));
		}
		Logger.info("Accounts returned from db {}", accountlist);
		return new AppResponse(true, accountlist);
	}

}
