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
import com.revolut.app.model.ErrorDetails;
import com.revolut.app.model.Account;
import com.revolut.app.model.AppResponse;
import com.revolut.app.model.User;

public class UserDaoImpl implements UserDao {

	private static final Logger Logger = LogManager.getLogger(UserDaoImpl.class);
	private static UserDaoImpl instance = null;
	DbUtils dbConn = null;

	private UserDaoImpl(){
		dbConn = DbUtils.getInstance();
	}

	public static UserDaoImpl getInstance() {
		if(instance == null){
			synchronized(UserDaoImpl.class){
				if(instance == null){
					instance = new UserDaoImpl();
				}
			}
		}
		return instance;
	} 

	@Override
	public AppResponse saveUser(User user) {
		Logger.debug("Starting saveUser in UserDaoImpl", user);
		ResultSet generatedKeys = null;
		LinkedHashMap<String,Object> criteria = new LinkedHashMap<>();
		criteria.put("firstName", user.getFirstName());
		criteria.put("lastName", user.getLastName());
		criteria.put("email", user.getEmail());

		Logger.info("Creating new user");
		try (Connection connection = dbConn.getConnection();
				PreparedStatement statement = connection.prepareStatement(DbQueries.SAVE_USER,Statement.RETURN_GENERATED_KEYS)) {
			dbConn.savePrepareStatement(connection, statement, criteria);
			int affectedRows = statement.executeUpdate();
			if (affectedRows == 0) {
				Logger.error("saveUser(): Creating user failed, no rows affected." + user);
				return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_PROCESSING,"Creating user failed, no rows affected."));
			}

			generatedKeys = statement.getGeneratedKeys();
			if (generatedKeys.next()) {
				int user_id = generatedKeys.getInt(1);
				user.setId(user_id);
			} else {
				Logger.error("saveUser():  Creating new user failed", user);
				return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_PROCESSING,"Users cannot be created"));
			}
		}catch(SQLException e){
			Logger.error("Exception occured while saving the new user", e.getMessage());
			return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_EXCEPTION,"Exception occured :"+e.getMessage()));
		}
		Logger.info("New user created successfully with user id : {}", user.getId());
		return new AppResponse(true,user);
	}

	@Override
	public AppResponse getAllUsers() {
		Logger.debug("Starting getAllUsers in UserDaoImpl");
		List<User> userslist = new ArrayList<>();

		try (Connection connection = dbConn.getConnection();
				PreparedStatement statement = connection.prepareStatement(DbQueries.SELECT_ALL_USERS)){

			try (ResultSet rs = statement.executeQuery();) {
				while (rs.next()) {
					User user = new User(
							rs.getString(Constants.USER_FIRST_NAME),
							rs.getString(Constants.USER_LAST_NAME),
							rs.getString(Constants.USER_EMAIL)
							);
					user.getAccounts().addAll((List<Account>)getAllAccountByUser(user.getEmail()).getData());
					userslist.add(user);
				}
			}catch(SQLException e){
				Logger.error("Exception occured while getting all the users", e.getMessage());
				return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_EXCEPTION,"Exception occured :"+e.getMessage()));
			}
		}catch(SQLException e){
			Logger.error("Exception occured while getting all the users", e.getMessage());
			return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_EXCEPTION,"Exception occured :"+e.getMessage()));
		}
		Logger.info("Users returned from db {}", userslist);
		return new AppResponse(true, userslist);
	}

	@Override
	public AppResponse getUserByEmail(String email) {
		Logger.debug("Starting getUserByEmail in UserDaoImpl", email);
		User user = null;
		try (Connection connection = dbConn.getConnection();
				PreparedStatement statement = connection.prepareStatement(DbQueries.GET_USER_BY_EMAIL)){

			statement.setString(1, email);
			try (ResultSet rs = statement.executeQuery();) {
				if (rs.next()) {
					user = new User(
							rs.getLong(Constants.USER_ID),
							rs.getString(Constants.USER_FIRST_NAME),
							rs.getString(Constants.USER_LAST_NAME),
							rs.getString(Constants.USER_EMAIL)
							);
					AppResponse allAccounts = getAllAccountByUser(user.getEmail());
					List<Account> userAccounts = null;
					if(allAccounts.getData() != null){
						userAccounts = (List<Account>)allAccounts.getData();
					}
					user.getAccounts().addAll(userAccounts);
				}
			}catch(SQLException e){
				Logger.error("Exception occured while getting the user", e.getMessage());
				return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_EXCEPTION,"Exception occured :"+e.getMessage()));
			}
		}catch(SQLException e){
			Logger.error("Exception occured while getting the user", e.getMessage());
			return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_EXCEPTION,"Exception occured : "+e.getMessage()));
		}
		return new AppResponse(true, user);
	}

	@Override
	public AppResponse getAllAccountByUser(String email) {
		Logger.debug("Starting getAllAccountByUser in AccountDaoImpl for user with email {}", email);
		LinkedHashMap<String,Object> criteria = new LinkedHashMap<>();
		criteria.put(Constants.USER_EMAIL, email);

		List<Account> accountlist = new ArrayList<>();

		try (Connection connection = dbConn.getConnection();
				PreparedStatement statement = connection.prepareStatement(DbQueries.GET_ACCOUNTS_BY_USER)){
			dbConn.savePrepareStatement(connection, statement, criteria);
			try (ResultSet rs = statement.executeQuery();) {
				while (rs.next()) {
					Account account = new Account();
					account.setAccountNumber(rs.getString(Constants.ACCOUNT_NUMBER));
					account.setBalance(rs.getBigDecimal(Constants.ACCOUNT_BALANCE));
					account.setCurrencyCode(rs.getString(Constants.ACCOUNT_CURRENCY_CODE));
					accountlist.add(account);
				}
			}catch(SQLException e){
				Logger.error("Exception occured while getting all the accounts for user {} - {}", email, e.getMessage());
				return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_EXCEPTION,"Exception occured :"+e.getMessage()));
			}
		}catch(SQLException e){
			Logger.error("Exception occured while getting all the accounts for user {} - {}", email, e.getMessage());
			return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_EXCEPTION,"Exception occured :"+e.getMessage()));
		}
		Logger.info("Accounts returned from db {}", accountlist);
		return new AppResponse(true, accountlist);
	}

	@Override
	public AppResponse deleteUser(String email) {
		Logger.debug("Starting deleteUser in UserDaoImpl having email", email);
		LinkedHashMap<String,Object> criteria = new LinkedHashMap<>();
		criteria.put("email", email);

		Logger.info("Deleting the user");
		try (Connection connection = dbConn.getConnection();
				PreparedStatement statement = connection.prepareStatement(DbQueries.DELETE_USER,Statement.RETURN_GENERATED_KEYS)) {
			dbConn.savePrepareStatement(connection, statement, criteria);
			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				Logger.error("deleteUser(): Delete user failed, no rows affected");
				return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_PROCESSING,"Delete user failed, no rows affected."));
			}
			return new AppResponse(true,null);
		}catch(SQLException e){
			Logger.error("Exception occured while deleting the user", e.getMessage());
			return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_EXCEPTION,"Exception occured :"+e.getMessage()));
		}
	}

	@Override
	public AppResponse deleteAccountsByUser (String email) {
		Logger.debug("Starting deleteAccount in AccountDaoImpl for email {}",email);
		LinkedHashMap<String,Object> criteria = new LinkedHashMap<>();
		criteria.put("email", email);

		Logger.info("Deleting all accounts for user having email [{}]", email);
		try (Connection connection = dbConn.getConnection();
				PreparedStatement statement = connection.prepareStatement(DbQueries.DELETE_ACCOUNT_BY_EMAIL)) {
			dbConn.savePrepareStatement(connection, statement, criteria);
			statement.executeUpdate();
		}catch(SQLException e){
			Logger.error("Exception occured while creating a new account - {}", e.getMessage());
			return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_EXCEPTION, "Exception occured :"+e.getMessage()));
		}
		Logger.info("Account deleted successfully for user: {}", email);
		return new AppResponse(true, null);
	}
}
