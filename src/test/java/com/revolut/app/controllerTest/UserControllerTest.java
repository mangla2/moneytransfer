package com.revolut.app.controllerTest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.revolut.app.controller.UserController;
import com.revolut.app.dao.UserDaoImpl;
import com.revolut.app.model.AppResponse;
import com.revolut.app.model.User;
import com.revolut.app.service.impl.UserServiceImpl;

public class UserControllerTest extends JerseyTest {

	@Mock
	UserServiceImpl userSvc;
	
	@Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
	
	@Override
    protected Application configure() {
        return new ResourceConfig(UserController.class);
    }
	
	@Test
	public void createUser(){
		User user = new User();
		user.setFirstName("Mark");
		user.setLastName("Henry");
		user.setEmail("mark.henry@mail.xyz");
		user.setCurrencyCode("EUR");
		
		when(userSvc.createUser(any(User.class))).thenReturn(new AppResponse(true,null));
		
		Response resp = target("user/create").request()
        .post(Entity.json(user));
		
		assertEquals("Http Response should be 200 ", 200, resp.getStatus());
	}
	
	@Test
	public void getAllUsers(){
		User user = new User();
		user.setFirstName("Mark");
		user.setLastName("Henry");
		user.setEmail("mark.henry@mail.xyz");
		user.setCurrencyCode("EUR");
		
		List<User> userList = new ArrayList<>();
		userList.add(user);
		
		AppResponse resp = new AppResponse(true,userList);
		
		when(userSvc.getAllUsers()).thenReturn(resp);
		
		Response response = target("user/all").request()
        .get(Response.class);
		
		assertEquals("Http Response should be 200 ", 200, response.getStatus());
	}
	
	@Test
	public void getUserByEmail() {
		when(userSvc.getUser(any(String.class))).thenReturn(new AppResponse(true,null));
		Response resp = target("user").path("abc.xyz@mail.com").request().get(Response.class);
		
		assertEquals("Http Response should be 200 ", 200, resp.getStatus());
	}
	
	@Test
	public void deleteUserByEmail() {
		when(userSvc.deleteUser(any(String.class))).thenReturn(new AppResponse(true,null));
		Response resp = target("user/delete").path("abc.xyz@mail.com").request().delete(Response.class);
		
		assertEquals("Http Response should be 200 ", 200, resp.getStatus());
	}
	
	@Test
	public void getAccountsByUser() {
		when(userSvc.getAccountsByUser(any(String.class))).thenReturn(new AppResponse(true,null));
		Response resp = target("user").path("abc.xyz@mail.com").request().get(Response.class);
		
		assertEquals("Http Response should be 200 ", 200, resp.getStatus());
	}
}
