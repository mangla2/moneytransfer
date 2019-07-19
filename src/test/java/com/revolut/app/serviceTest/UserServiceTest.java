package com.revolut.app.serviceTest;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.revolut.app.controller.UserController;
import com.revolut.app.dao.UserDaoImpl;
import com.revolut.app.service.impl.UserServiceImpl;

public class UserServiceTest extends JerseyTest {

	@Mock
	UserDaoImpl userDao;

	@Override
    protected Application configure() {
        return new ResourceConfig(UserServiceImpl.class);
    }
	
	@Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
	
	@Test
	public void getAllUsers(){
		
	}
	
}
