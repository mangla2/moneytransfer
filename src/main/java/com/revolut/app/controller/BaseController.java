package com.revolut.app.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class BaseController {

    @GET
    public String index() {
        return "Now transfer money with ease and security. Checkout /api/transfer";
    }

}