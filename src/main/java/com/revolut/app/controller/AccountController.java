package com.revolut.app.controller;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revolut.app.model.Account;
import com.revolut.app.model.AppResponse;
import com.revolut.app.service.AccountService;
import com.revolut.app.service.UserService;
import com.revolut.app.service.impl.AccountServiceImpl;
import com.revolut.app.service.impl.UserServiceImpl;

@Path("/user/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountController {

	private static final Logger Logger = LogManager.getLogger(UserController.class);
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
    public AppResponse createAccount(Account account) {
    	Logger.info("Request received for creating the account {}", account);
        return acctSvc.createAccount(account);
    }

}
