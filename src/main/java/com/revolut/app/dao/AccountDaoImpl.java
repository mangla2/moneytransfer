package com.revolut.app.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.revolut.app.model.Transaction.TRANSACTION_TYPE;

public class AccountDaoImpl implements AccountDao {

	private static final Logger Logger = LogManager.getLogger(AccountDaoImpl.class);
	private static AccountDaoImpl instance = null;
	DbUtils dbConn = null;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
							rs.getInt(Constants.ACCOUNT_USER),
							rs.getString(Constants.ACCOUNT_NUMBER),
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
	public synchronized AppResponse makeTransaction(Transaction transaction, BigDecimal amountConverted) {
		Logger.debug("Initiating the transaction {}", transaction);

		Account from = transaction.getFrom();
		Account to = transaction.getTo();

		try{
			while(from.getBalance().compareTo(transaction.getAmount()) < 0){
				wait();
			}

			// update accounts balance
			from.setBalance(from.getBalance().subtract(transaction.getAmount()));
			to.setBalance(to.getBalance().add(amountConverted));

			try (Connection connection = dbConn.getConnection()){
				connection.setAutoCommit(false);

				try(PreparedStatement statement = connection.prepareStatement(DbQueries.SAVE_TRANSACTION, Statement.RETURN_GENERATED_KEYS);
						PreparedStatement psUpdate = connection.prepareStatement(DbQueries.UPDATE_ACCOUNT_BALANCE)) {

					LinkedHashMap<String,Object> criteria = new LinkedHashMap<>();
					long transactionId = System.currentTimeMillis();
					transaction.setTransactionId(String.valueOf(transactionId));
					criteria.put(Constants.TRANSACTION_ID, transactionId);
					criteria.put(Constants.ACCOUNT_FROM_NUMBER, from.getAccountNumber());
					criteria.put(Constants.ACCOUNT_TO_NUMBER, to.getAccountNumber());
					criteria.put(Constants.AMOUNT, transaction.getAmount());
					criteria.put(Constants.NOTES, transaction.getNotes());
					criteria.put(Constants.ACCOUNT_CURRENCY_CODE, from.getCurrencyCode());

					dbConn.savePrepareStatement(connection, statement, criteria);
					int result = statement.executeUpdate();

					if(result == 1){
						Logger.debug("Transaction is successful - {}", result);
					}

					//update from account
					criteria.clear();
					criteria.put(Constants.ACCOUNT_BALANCE, from.getBalance());
					criteria.put(Constants.ACCOUNT_NUMBER, from.getAccountNumber());
					dbConn.savePrepareStatement(connection, psUpdate, criteria);
					psUpdate.addBatch();

					// update to account
					criteria.clear();
					criteria.put(Constants.ACCOUNT_BALANCE, to.getBalance());
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

	@Override
	public AppResponse getTransactionsByAccount(String accountNumber) {
		Logger.debug("Starting getTransactionsByAccount in AccountDaoImpl for account : {}", accountNumber);
		List<Transaction> transactionList = new ArrayList<>();

		LinkedHashMap<String,Object> criteria = new LinkedHashMap<>();
		criteria.put(Constants.ACCOUNT_FROM_NUMBER, accountNumber);
		criteria.put(Constants.ACCOUNT_TO_NUMBER, accountNumber);
		
		try (Connection connection = dbConn.getConnection();
				PreparedStatement statement = connection.prepareStatement(DbQueries.GET_TRANSACTIONS_BY_ACCOUNT_NUM)){
			dbConn.savePrepareStatement(connection, statement, criteria);
			try (ResultSet rs = statement.executeQuery();) {
				while (rs.next()) {
					Transaction transaction = new Transaction();
					transaction.setTransactionId(String.valueOf(rs.getLong(Constants.TRANSACTION_ID)));
					
					String from = rs.getString(Constants.ACCOUNT_FROM_NUMBER);
					String to = rs.getString(Constants.ACCOUNT_TO_NUMBER);
					
					TRANSACTION_TYPE type = (accountNumber.equalsIgnoreCase(from)) ? TRANSACTION_TYPE.DEBIT : TRANSACTION_TYPE.CREDIT;
					transaction.setType(type);
					
					if(TRANSACTION_TYPE.DEBIT.equals(type)){
						transaction.setAccountTo(from);
					}else{
						transaction.setAccountFrom(to);
					}
					
					transaction.setAmount(rs.getBigDecimal(Constants.AMOUNT));
					transaction.setCurrencyCode(rs.getString(Constants.ACCOUNT_CURRENCY_CODE));
					transaction.setNotes(rs.getString(Constants.NOTES));
					transaction.setCreatedAt(sdf.format(new Date(rs.getTimestamp(Constants.CREATED_AT).getTime())));
					transactionList.add(transaction);
				}
			}catch(SQLException e){
				Logger.error("Exception occured while getting all the transactions for accountnumber {} - {}", accountNumber, e.getMessage());
				return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_EXCEPTION,"Exception occured :"+e.getMessage()));
			}
		}catch(SQLException e){
			Logger.error("Exception occured while getting all the transactions for accountnumber {} - {}", accountNumber, e.getMessage());
			return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_EXCEPTION,"Exception occured :"+e.getMessage()));
		}
		Logger.info("Transaction History returned from db {}", transactionList);
		return new AppResponse(true, transactionList);
	}
	
	@Override
	public AppResponse getTransactionByTransactionId(String transactionId) {
		Logger.debug("Starting getTransactionByTransactionId in AccountDaoImpl {}", transactionId);
		
		LinkedHashMap<String,Object> criteria = new LinkedHashMap<>();
		criteria.put(Constants.TRANSACTION_ID, transactionId);
		
		Transaction transaction = null;
		
		try (Connection connection = dbConn.getConnection();
				PreparedStatement statement = connection.prepareStatement(DbQueries.GET_TRANSACTION_BY_ID)){
			dbConn.savePrepareStatement(connection, statement, criteria);
			try (ResultSet rs = statement.executeQuery();) {
				while (rs.next()) {
					transaction = new Transaction(
							String.valueOf(rs.getLong(Constants.TRANSACTION_ID)),
							rs.getBigDecimal(Constants.AMOUNT),
							rs.getString(Constants.ACCOUNT_FROM_NUMBER),
							rs.getString(Constants.ACCOUNT_TO_NUMBER),
							rs.getString(Constants.NOTES),
							sdf.format(new Date(rs.getTimestamp(Constants.CREATED_AT).getTime()))
							);
				}
			}catch(SQLException e){
				Logger.error("Exception occured while getting the transaction", e.getMessage());
				return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_EXCEPTION,"Exception occured :"+e.getMessage()));
			}
		}catch(SQLException e){
			Logger.error("Exception occured while getting the transaction", e.getMessage());
			return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_EXCEPTION,"Exception occured : "+e.getMessage()));
		}
		
		return new AppResponse(true, transaction);
	}

	@Override
	public AppResponse updateBalance(Account account, BigDecimal amount) {
		Logger.debug("Starting updateBalance in AccountDaoImpl for accountNumber {}", account.getAccountNumber());
		LinkedHashMap<String,Object> criteria = new LinkedHashMap<>();
		criteria.put(Constants.AMOUNT, account.getBalance());
		criteria.put(Constants.ACCOUNT_NUMBER, account.getAccountNumber());
		
		Logger.info("Updating amount [{}] to account for user having accountNumber as [{}]", amount, account.getAccountNumber());
		try (Connection connection = dbConn.getConnection();
				PreparedStatement statement = connection.prepareStatement(DbQueries.UPDATE_ACCOUNT_BALANCE)) {
			dbConn.savePrepareStatement(connection, statement, criteria);
			int affectedRows = statement.executeUpdate();
			
			if(affectedRows == 1){
				Logger.info("Amount {} updated successfully to the account {}", amount, account.getAccountNumber());
			}
		}catch(SQLException e){
			Logger.error("Exception occured while updating money to an account - {}", e.getMessage());
			return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_EXCEPTION, "Exception occured :"+e.getMessage()));
		}
		return new AppResponse(true, null);
	}

}
