package com.revolut.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revolut.app.config.DbUtils;
import com.revolut.app.constants.Constants;
import com.revolut.app.constants.DbQueries;
import com.revolut.app.exception.InternalServerError;
import com.revolut.app.model.Account;
import com.revolut.app.model.AppResponse;
import com.revolut.app.model.ErrorDetails;
import com.revolut.app.model.Transaction;

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
			return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_EXCEPTION,"Exception occured :"+e.getMessage()));
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
				return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_EXCEPTION,"Exception occured :"+e.getMessage()));
			}
		}catch(SQLException e){
			Logger.error("Exception occured while getting all the accounts", e.getMessage());
			return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_EXCEPTION,"Exception occured :"+e.getMessage()));
		}
		Logger.info("Accounts returned from db {}", accountlist);
		return new AppResponse(true, accountlist);
	}

	@Override
	public AppResponse deleteAccountByAccountNumber(String accountNumber) {
		Logger.debug("Starting deleteAccountByAccountNumber in AccountDaoImpl for accountNumber {}", accountNumber);
		LinkedHashMap<String,Object> criteria = new LinkedHashMap<>();
		criteria.put(Constants.ACCOUNT_NUMBER, accountNumber);

		Logger.info("Deleting account for user having accountNumber as [{}]", accountNumber);
		try (Connection connection = dbConn.getConnection();
				PreparedStatement statement = connection.prepareStatement(DbQueries.DELETE_ACCOUNT)) {
			dbConn.savePrepareStatement(connection, statement, criteria);
			statement.executeUpdate();
		}catch(SQLException e){
			Logger.error("Exception occured while deleting an account - {}", e.getMessage());
			return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_EXCEPTION, "Exception occured :"+e.getMessage()));
		}
		Logger.info("Account deleted successfully for user: {}", accountNumber);
		return new AppResponse(true, null);
	}

	@Override
	public AppResponse getAccountByAccountNumber(String accountNumber) {
		Logger.debug("Starting getAccountByAccountNumber in AccountDaoImpl {}", accountNumber);
		LinkedHashMap<String,Object> criteria = new LinkedHashMap<>();
		criteria.put(Constants.ACCOUNT_NUMBER, accountNumber);

		Account account = null;
		try (Connection connection = dbConn.getConnection();
				PreparedStatement statement = connection.prepareStatement(DbQueries.GET_ACCOUNT_BY_ACCOUNT_NUM)){

			statement.setString(1, accountNumber);
			try (ResultSet rs = statement.executeQuery();) {
				if (rs.next()) {
					account = new Account(
							rs.getString(Constants.ACCOUNT_NUMBER),
							rs.getInt(Constants.ACCOUNT_USER),
							rs.getBigDecimal(Constants.ACCOUNT_BALANCE),
							rs.getString(Constants.ACCOUNT_CURRENCY_CODE)
							);
				}
			}catch(SQLException e){
				Logger.error("Exception occured while getting the account", e.getMessage());
				return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_EXCEPTION,"Exception occured :"+e.getMessage()));
			}
		}catch(SQLException e){
			Logger.error("Exception occured while getting the account", e.getMessage());
			return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_EXCEPTION,"Exception occured : "+e.getMessage()));
		}
		return new AppResponse(true, account);
	}

	@Override
	public synchronized AppResponse makeTrasaction(Transaction transaction) {
		Logger.debug("Initiating the transaction {}", transaction);

		Account from = transaction.getFrom();
		Account to = transaction.getTo();

		try{
			while(from.getBalance().compareTo(transaction.getDebitAmount()) < 0){
				wait();
			}

			// update accounts balance
			from.setBalance(from.getBalance().subtract(transaction.getDebitAmount()));
			to.setBalance(to.getBalance().add(transaction.getCreditAmount()));

			try (Connection connection = dbConn.getConnection()){
				connection.setAutoCommit(false);

				try(PreparedStatement statement = connection.prepareStatement(DbQueries.SAVE_TRANSACTION);
						PreparedStatement psUpdate = connection.prepareStatement(DbQueries.UPDATE_ACCOUNT_BALANCE)) {

					LinkedHashMap<String,Object> criteria = new LinkedHashMap<>();
					long transactionId = System.currentTimeMillis();
					criteria.put(Constants.TRANSACTION_ID, transactionId);
					criteria.put(Constants.ACCOUNT_FROM_NUMBER, from.getAccountNumber());
					criteria.put(Constants.ACCOUNT_TO_NUMBER, to.getAccountNumber());
					criteria.put(Constants.AMOUNT, transaction.getDebitAmount());
					criteria.put(Constants.NOTES, transaction.getNotes());
					criteria.put(Constants.CREATED_AT, transaction.getCreatedAt());
					criteria.put(Constants.ACCOUNT_CURRENCY_CODE, from.getCurrencyCode());

					dbConn.savePrepareStatement(connection, statement, criteria);
					int result = statement.executeUpdate();

					if(result == 1){
						Logger.debug("Transaction is successful - {}", result);
					}
					
					//update from account
					criteria.clear();
					criteria.put(Constants.ACCOUNT_BALANCE, transaction.getDebitAmount());
					criteria.put(Constants.ACCOUNT_NUMBER, from.getAccountNumber());
					dbConn.savePrepareStatement(connection, psUpdate, criteria);
					psUpdate.addBatch();

					// update to account
					criteria.clear();
					criteria.put(Constants.ACCOUNT_BALANCE, transaction.getCreditAmount());
					criteria.put(Constants.ACCOUNT_NUMBER, to.getAccountNumber());
					dbConn.savePrepareStatement(connection, psUpdate, criteria);
					psUpdate.addBatch();

					int[] rowsUpdated = psUpdate.executeBatch();
					int affectedRows = result + rowsUpdated[0] + rowsUpdated[1];

					Logger.debug("Number of rows updated for the transfer : " + affectedRows);
					if(affectedRows == 3){
						connection.commit();
						transaction.setTransactionId(String.valueOf(transactionId));
						from.getTransactionsList().add(transaction);
					}
				}catch(InternalServerError e){
					connection.rollback();
					connection.setAutoCommit(true);
					Logger.error("Exception occured - {}", e.getMessage());
					return new AppResponse(false,"Failed to complete the transaction", new ErrorDetails(Constants.ERROR_CODE_EXCEPTION,e.getMessage()));
				}
			}catch(SQLException e){
				Logger.error("Exception occured - {}", e.getMessage());
				return new AppResponse(false,"Failed to complete the transaction", new ErrorDetails(Constants.ERROR_CODE_EXCEPTION,e.getMessage()));
			}
			notifyAll();
		}catch(InterruptedException e){
			Logger.error("Exception occured - {}", e.getMessage());
			return new AppResponse(false,"Failed to complete the transaction", new ErrorDetails(Constants.ERROR_CODE_EXCEPTION,e.getMessage()));
		}

		return new AppResponse(true, transaction);
	}

}
