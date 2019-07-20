package com.revolut.app.service.impl;

import java.math.BigDecimal;
import java.util.List;

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
import com.revolut.app.model.BankStatement;
import com.revolut.app.model.ErrorDetails;
import com.revolut.app.model.Transaction;
import com.revolut.app.model.Transaction.TRANSACTION_TYPE;
import com.revolut.app.model.User;
import com.revolut.app.service.AccountService;
import com.revolut.app.utils.CurrencyConverter;

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

		if(StringUtils.isNullOrEmpty(account.getEmail())){
			Logger.error("User Email is found null or empty");
			return new AppResponse(false, new ErrorDetails(Constants.ERROR_CODE_VALIDATION, "User Email is found null or empty"));
		}

		//check if the user exists or not
		AppResponse resp = userDao.getUserByEmail(account.getEmail());
		if(!resp.isStatus() || resp.getData() == null){
			Logger.error("Account cannot be created since user is not registered having email [{}]", account.getEmail());
			return new AppResponse(false, "Account cannot be created since user is not registered",new ErrorDetails(Constants.ERROR_CODE_VALIDATION, "User is not registered"));
		}

		User u = (User) resp.getData();
		
		resp = CurrencyConverter.checkValidCurrency(account.getCurrencyCode());
		if(!resp.isStatus()) {
			Logger.error("Currency type not supported");
			return new AppResponse(false, "Account cannot be created", new ErrorDetails(Constants.ERROR_CODE_VALIDATION, "Currency type not supported"));
		}
		
		account.setUserId(u.getId());
		return accountDao.createAccount(account);
	}


	@Override
	public AppResponse getAccountByAccountNumber(String accountNumber){
		Logger.debug("Starting getAccountByAccountNumber() in AccountServiceImpl for [{}]", accountNumber);
		AppResponse resp = null;

		if(StringUtils.isNullOrEmpty(accountNumber)){
			Logger.error("Failed to get the account as the account number is null/empty");
			return new AppResponse(false,"Failed to delete the account as the account number is null/empty", new ErrorDetails(Constants.ERROR_CODE_VALIDATION,"Account Number is found null/empty"));
		}

		// check if account exists or not
		resp = accountDao.getAccountByAccountNumber(accountNumber);
		if(!resp.isStatus() || resp.getData() == null){
			Logger.error("Failed to get as the account is not found");
			return new AppResponse(false,"Failed to get as the account is not found", new ErrorDetails(Constants.ERROR_CODE_VALIDATION,"Account is not found in db for requested account number"));
		}

        Account acc = (Account) resp.getData();
		Logger.info("Account fetched successfully having account number [{}]", acc);
		return resp;
	}

	@Override
	public AppResponse deleteAccountByAccountNumber(String accountNumber){
		Logger.debug("Starting deleteAccountByAccountNumber() in AccountServiceImpl for [{}]", accountNumber);
		AppResponse resp = null;

		if(StringUtils.isNullOrEmpty(accountNumber)){
			Logger.error("Failed to delete the account as the account number is null/empty");
			return new AppResponse(false,"Failed to delete the account as the account number is null/empty", new ErrorDetails(Constants.ERROR_CODE_VALIDATION,"Account Number is found null/empty"));
		}

		// check if account exists or not
		resp = accountDao.getAccountByAccountNumber(accountNumber);
		if(!resp.isStatus() || resp.getData() == null){
			Logger.error("Failed to delete as the account is not found");
			return new AppResponse(false,"Failed to delete as the account is not found", new ErrorDetails(Constants.ERROR_CODE_VALIDATION,"Account is not found in db for requested account number"));
		}

		//if account exists, then delete
		resp = accountDao.deleteAccountByAccountNumber(accountNumber);
		if(!resp.isStatus()){
			return new AppResponse(false, "Account not found in Db", resp.getError());
		}

		Logger.info("Account deleted successfully having account number [{}]", accountNumber);
		return resp;
	}

	@Override
	public AppResponse getTransactionHistoryByAccount(String accountNumber) {
		Logger.debug("Starting getTransactionHistoryByAccount() in AccountServiceImpl for [{}]", accountNumber);
		AppResponse resp = null;

		if(StringUtils.isNullOrEmpty(accountNumber)){
			Logger.error("Failed to get bank statement as the account number is null/empty");
			return new AppResponse(false,"Failed to get bank statement as the account number is null/empty", new ErrorDetails(Constants.ERROR_CODE_VALIDATION,"Account Number is found null/empty"));
		}

		// check if account exists or not
		resp = accountDao.getAccountByAccountNumber(accountNumber);
		if(!resp.isStatus() || resp.getData() == null){
			Logger.error("Account not found in database");
			return new AppResponse(false, "Account not found", new ErrorDetails(Constants.ERROR_CODE_VALIDATION,"Account is not found in db for requested account number"));
		}

		Account account = (Account)resp.getData();
		
		resp = accountDao.getTransactionsByAccount(accountNumber);
		if(!resp.isStatus()){
			Logger.error("Failed to get the transaction history");
			return resp;
		}
		
		List<Transaction> transactionList = (List)resp.getData();
		BigDecimal totalBalance = account.getBalance();
		return new AppResponse(true, new BankStatement(totalBalance, account.getCurrencyCode(), transactionList));
	}

	@Override
	public AppResponse deposit(String accountNumber, BigDecimal amount) {
		Logger.debug("Starting deposit() in AccountServiceImpl for [{}]", accountNumber);
		AppResponse resp = null;
		String errMsg = "Failed to deposit amount to the account as the account number";

		if(StringUtils.isNullOrEmpty(accountNumber)){
			Logger.error(errMsg + "is null/empty");
			return new AppResponse(false, errMsg + "is null/empty", new ErrorDetails(Constants.ERROR_CODE_VALIDATION,"Account Number is found null/empty"));
		}

		// check if account exists or not
		resp = accountDao.getAccountByAccountNumber(accountNumber);
		if(!resp.isStatus() || resp.getData() == null){
			Logger.error(errMsg + "is not found");
			return new AppResponse(false, errMsg + "is not found", new ErrorDetails(Constants.ERROR_CODE_VALIDATION,"Account is not found in db for requested account number"));
		}

		Account account = (Account) resp.getData();
		account.setBalance(account.getBalance().add(amount));
		resp =  accountDao.updateBalance(account, amount, TRANSACTION_TYPE.CREDIT);

		if(!resp.isStatus()){
			Logger.error("Failed to deposit amount to the account {} ", accountNumber);
			return new AppResponse(false, "Failed to deposit amount to the account", resp.getError());
		}

		Logger.info("Amount {} deposited successfully to the account {}", amount, accountNumber);
		return new AppResponse(true, "Amount " + amount + account.getCurrencyCode() +" has been deposited successfully");
	}

	@Override
	public AppResponse withdraw(String accountNumber, BigDecimal amount) {
		Logger.debug("Starting withdraw() in AccountServiceImpl for [{}]", accountNumber);
		AppResponse resp = null;
		String errMsg = "Failed to withdraw amount to the account as the account number";

		if(StringUtils.isNullOrEmpty(accountNumber)){
			Logger.error(errMsg + "is null/empty");
			return new AppResponse(false, errMsg + "is null/empty", new ErrorDetails(Constants.ERROR_CODE_VALIDATION,"Account Number is found null/empty"));
		}

		// check if account exists or not
		resp = accountDao.getAccountByAccountNumber(accountNumber);
		if(!resp.isStatus() || resp.getData() == null){
			Logger.error(errMsg + "account is not found");
			return new AppResponse(false, errMsg + "account is not found" , new ErrorDetails(Constants.ERROR_CODE_VALIDATION,"Account is not found in db for requested account number"));
		}

		Account account = (Account) resp.getData();
		account.setBalance(account.getBalance().subtract(amount));
		resp = accountDao.updateBalance(account, amount, TRANSACTION_TYPE.DEBIT);

		if(!resp.isStatus()){
			Logger.error("Failed to withdraw amount from the account {} ", accountNumber);
			return new AppResponse(false, "Failed to withdraw amount from the account", resp.getError());
		}

		Logger.info("Amount {} withdrawn successfully from the account {}", amount, accountNumber);
		return new AppResponse(true, "Amount " + amount + account.getCurrencyCode() + " has been withdrawn successfully");
	}

}
