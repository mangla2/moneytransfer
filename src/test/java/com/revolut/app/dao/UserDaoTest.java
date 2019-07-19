package com.revolut.app.dao;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.revolut.app.config.DbUtils;
import com.revolut.app.constants.DbQueries;
import com.revolut.app.model.Account;
import com.revolut.app.model.AppResponse;
import com.revolut.app.model.User;

public class UserDaoTest extends JerseyTest {

	@Mock
	DbUtils dbconn;
	
	@Mock
	Connection c;

	@Mock
	PreparedStatement ps;
	
	@Mock
	ResultSet rs;
	
	@Mock
	private ResultSetMetaData resultSetMetaData;

	@Spy
	@InjectMocks
	UserDaoImpl userDao;
	
	private User u;
	
	@Override
    protected Application configure() {
        return new ResourceConfig(UserDaoImpl.class);
    }
	
	@Before
    public void setup() throws SQLException {
        MockitoAnnotations.initMocks(this);
        when(dbconn.getConnection()).thenReturn(c);
        when(c.prepareStatement(DbQueries.SELECT_ALL_USERS)).thenReturn(ps);
    }
	
	@After
    public void after() throws SQLException {
	   rs.close();
       ps.close();
       c.close();
    }
	
	@Test
	public void getAllUsersTest() throws Exception {
		u = new User(1, "mark","henry","mark.henry@mail.xyz");
        when(rs.next()).thenReturn(true, false);
        when(rs.getString(1)).thenReturn(u.getFirstName());
        when(rs.getString(2)).thenReturn(u.getLastName());
        when(rs.getString(3)).thenReturn(u.getEmail());
        
        List<Account> accountList = new ArrayList<>();
        Account acc = new Account(u.getEmail(), new BigDecimal(5000),"INR");
        accountList.add(acc);
        
        AppResponse getAllAccounts = new AppResponse(true,accountList);
        doReturn(getAllAccounts).when(userDao).getAllAccountByUser(any(String.class));
        
        u.getAccounts().addAll(accountList);
        when(ps.executeQuery()).thenReturn(rs);
        
		AppResponse resp = userDao.getAllUsers();
		assertEquals(1, ((List)resp.getData()).size());
	}
		
}
