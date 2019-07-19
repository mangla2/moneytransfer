package com.revolut.app.controller;

import java.math.BigDecimal;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revolut.app.model.Account;
import com.revolut.app.model.AppResponse;
import com.revolut.app.service.AccountService;
import com.revolut.app.service.impl.AccountServiceImpl;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountController {

	private static final Logger Logger = LogManager.getLogger(AccountController.class);
	private AccountService acctSvc = null;
	
	public AccountController(){
		acctSvc = AccountServiceImpl.getInstance();
	}
	
    @GET
    @Path("/all")
    public AppResponse getAllAccounts() {
    	Logger.info("Request received for getting all the accounts");
    	return acctSvc.getAllAccounts();
    }

    
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public AppResponse createAccount(Account account) {
    	Logger.info("Request received for creating the account {}", account);
        return acctSvc.createAccount(account);
    }

    @DELETE
    @Path("/delete/{accountNumber}")
    public AppResponse deleteAccount(@PathParam("accountNumber") String accountNumber) {
    	Logger.info("Request received for deleting the account {}", accountNumber);
        return acctSvc.deleteAccountByAccountNumber(accountNumber);
    }
    
    @GET
    @Path("/transactions/{accountNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    public AppResponse getAllTransationsByAccountNumber(@PathParam("accountNumber") String accountNumber) {
    	Logger.info("Request received for getting the statement for account {}", accountNumber);
    	return acctSvc.getTransactionHistoryByAccount(accountNumber);
    }
    
    @PUT
    @Path("/deposit/{accountNumber}")
    @Consumes(MediaType.APPLICATION_JSON)
    public AppResponse deposit(@PathParam("accountNumber") String accountNumber, @QueryParam("amount") BigDecimal amount) {
    	Logger.info("Request received for depositing amount [{}] to the account {}", amount, accountNumber);
        return acctSvc.deposit(accountNumber, amount);
    }
    
    @PUT
    @Path("/withdraw/{accountNumber}")
    @Consumes(MediaType.APPLICATION_JSON)
    public AppResponse withdraw(@PathParam("accountNumber") String accountNumber, @QueryParam("amount") BigDecimal amount) {
    	Logger.info("Request received for depositing amount [{}] to the account {}", amount, accountNumber);
        return acctSvc.withdraw(accountNumber, amount);
    }
}
