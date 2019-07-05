package com.revolut.app.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import com.revolut.app.dao.UserDao;
import com.revolut.app.dao.UserDaoImpl;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class BaseController {

	private UserDao userDao = new UserDaoImpl();
	
    @GET
    public String index() {
        return "Now transfer money with ease and security. Checkout /api/transfer";
    }

    @GET
    @Path("/users")
    public Response getAll() {
        try {
            return Response.ok(userDao.getAll()).build();
        } catch (Exception e) {
            return Response.noContent().build();
        }
    }
}