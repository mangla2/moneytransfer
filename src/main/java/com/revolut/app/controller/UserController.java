package com.revolut.app.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.ws.rs.core.MediaType;
import com.revolut.app.model.AppResponse;
import com.revolut.app.model.User;
import com.revolut.app.service.UserService;
import com.revolut.app.service.impl.UserServiceImpl;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserController {

	private static final Logger Logger = LogManager.getLogger(UserController.class);
	private UserService userSvc = null;
	
	public UserController(){
		userSvc = UserServiceImpl.getInstance();
	}
	
    @GET
    @Path("/all")
    public AppResponse getAllUsers() {
    	Logger.info("Request received for getting all the users");
    	return userSvc.getAllUsers();
    }
    
    
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public AppResponse createUser(User user) {
    	Logger.info("Request received for creating a user {}", user);
        return userSvc.createUser(user);
    }
}