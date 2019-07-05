package com.revolut.app.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DbUtils {

	private static final Logger Logger = LogManager.getLogger(DbUtils.class);
    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:mem:banking;INIT=runscript from 'classpath:/db.sql';DB_CLOSE_DELAY=-1";
    private static final String DB_USERNAME = "system";
    private static final String DB_PASSWORD = "abc123";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.error("Connection ERROR");
        } catch (ClassNotFoundException e) {
        	e.printStackTrace();
        	Logger.error("Connection ERROR");
		}
        return connection;
    }
    
    

}
