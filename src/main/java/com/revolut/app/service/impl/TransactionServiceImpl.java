package com.revolut.app.service.impl;

import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.util.StringUtils;
import org.json.JSONObject;

import com.revolut.app.constants.Constants;
import com.revolut.app.dao.AccountDao;
import com.revolut.app.dao.AccountDaoImpl;
import com.revolut.app.model.Account;
import com.revolut.app.model.AppResponse;
import com.revolut.app.model.ErrorDetails;
import com.revolut.app.model.Transaction;
import com.revolut.app.service.TransactionService;
import com.revolut.app.utils.CurrencyConverter;

public class TransactionServiceImpl implements TransactionService {

	private static final Logger Logger = LogManager.getLogger(TransactionServiceImpl.class);
	private AccountDao accountDao = null;
	private static TransactionServiceImpl instance = null;

	private TransactionServiceImpl(){
		accountDao = AccountDaoImpl.getInstance();
	}

	public static TransactionServiceImpl getInstance() {
		if(instance == null){
			synchronized(TransactionServiceImpl.class){
				if(instance == null){
					instance = new TransactionServiceImpl();
				}
			}
		}
		return instance;
	} 
	
	private AppResponse validateRequest (String transaction) {
		Logger.debug("Starting transferMoney in TransactionServiceImpl [{}]", transaction);	

		if(StringUtils.isNullOrEmpty(transaction)){
			Logger.error("Request Body found null/empty and hence failed to proceed further");
			return new AppResponse(false, "Failed to initiate the transaction", new ErrorDetails(Constants.ERROR_CODE_VALIDATION,"Request Body found null/empty"));
		}

		JSONObject request = null;

		try{		
			request	= new JSONObject(transaction);
		}catch(Exception e){
          Logger.error("Exception occurred since request body is incorrect", transaction);
          return new AppResponse(false, "Failed to initiate the transaction", new ErrorDetails(Constants.ERROR_CODE_VALIDATION,"Request Body incorrect"));
		}

		String accountFrom = request.getString("accountFrom");
		String accountTo = request.getString("accountTo");
		BigDecimal amount = request.getBigDecimal("amount");

		if(StringUtils.isNullOrEmpty(accountFrom) || StringUtils.isNullOrEmpty(accountTo) || amount == null){
			Logger.error("Either of accounts is found null/empty or amount is null");
			return new AppResponse(false, "Failed to initiate the transaction", new ErrorDetails(Constants.ERROR_CODE_VALIDATION,"Account information or money found null/empty"));
		}
		
		return new AppResponse(true,request);
	}

	@Override
	public AppResponse transferMoney(String transaction) {
		Logger.debug("Starting transferMoney in TransactionServiceImpl [{}]", transaction);	
		AppResponse resp = null;

		resp = validateRequest(transaction);
		if(!resp.isStatus()){
			return resp;
		}

		JSONObject request = (JSONObject) resp.getData();

		String accountFrom = request.getString("accountFrom");
		String accountTo = request.getString("accountTo");
		BigDecimal money = request.getBigDecimal("amount");
		String notes = request.getString("notes");
		
		Logger.info("Starting transferMoney in TransactionServiceImpl from [{}] to [{}]", accountFrom, accountTo);	
		
		//check whether account1 and account2 exists in db
		Account account1 = (Account)accountDao.getAccountByAccountNumber(accountFrom).getData();
		Account account2 = (Account)accountDao.getAccountByAccountNumber(accountTo).getData();
		String errMsg = null;

		if(account1 == null || account2 == null){

			if(account1 == null){
				errMsg = "Transaction cannot be performed since sender account is not found";
			}else{
				errMsg = "Transaction cannot be performed since receiver account is not found";
			}
			Logger.error(errMsg);	
			return new AppResponse(false, errMsg, new ErrorDetails(Constants.ERROR_CODE_VALIDATION,errMsg));
		}

		// if yes, then check whether the amount specified is there in sender's account
		if(account1.getBalance().compareTo(money) < 0){
			errMsg = "Not enough balance to transfer money from the account";
			Logger.error(errMsg);
			return new AppResponse(false, errMsg, new ErrorDetails(Constants.ERROR_CODE_PROCESSING,errMsg));
		}

		BigDecimal conversionRate = new BigDecimal(1);
		if(!account1.getCurrencyCode().equalsIgnoreCase(account2.getCurrencyCode())){
			
			//get the conversion rate
			conversionRate = CurrencyConverter.getConversionRate(account1.getCurrencyCode(), account2.getCurrencyCode());
			if(conversionRate == BigDecimal.ZERO){
				errMsg = "Incorrect Currency Conversion requested";
				Logger.error(errMsg);
				return new AppResponse(false, "Failed to transfer money since conversion cannot happen between two accounts", new ErrorDetails(Constants.ERROR_CODE_PROCESSING,errMsg));
			}
		}

		// calculate the amount to be transferred
		BigDecimal amountToBeAdded = money.multiply(conversionRate);
		
		// transfer the money from one account to another
		resp = accountDao.makeTransaction(new Transaction(money, account1, account2, notes),amountToBeAdded);
		if(!resp.isStatus()){
			errMsg = "Transaction failed to happen";
			Logger.error(errMsg);
			return new AppResponse(false, "Transaction failed", resp.getError());
		}

		Transaction transx = (Transaction) resp.getData();
		
		resp = accountDao.getTransactionByTransactionId(transx.getTransactionId());
		if(!resp.isStatus()){
			return resp;
		}
		
		transx = (Transaction) resp.getData();
		Transaction respObj = new Transaction(transx.getTransactionId(), transx.getAmount(), transx.getCreatedAt());
		return new AppResponse(true, respObj, "Transaction is successful");
	}

}
