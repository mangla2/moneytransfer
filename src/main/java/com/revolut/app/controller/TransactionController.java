package com.revolut.app.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revolut.app.model.AppResponse;
import com.revolut.app.service.TransactionService;
import com.revolut.app.service.impl.TransactionServiceImpl;

@Path("/user/account")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionController {

	private static final Logger Logger = LogManager.getLogger(TransactionController.class);
    private TransactionService transactionSvc = null;
	
	public TransactionController(){
		transactionSvc = TransactionServiceImpl.getInstance();
	}
	
	@POST
    @Path("/transaction")
    @Consumes(MediaType.APPLICATION_JSON)
    public AppResponse transfer(String transaction) {
    	Logger.info("Request received for transfering the money {}", transaction);
        return transactionSvc.transferMoney(transaction);
    }
}
