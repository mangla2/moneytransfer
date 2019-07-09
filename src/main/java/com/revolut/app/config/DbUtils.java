package com.revolut.app.config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.tools.RunScript;

public class DbUtils {

	private static final Logger Logger = LogManager.getLogger(DbUtils.class);
	private static final String DB_DRIVER = "org.h2.Driver";
	private static final String DB_URL = "jdbc:h2:mem:banking;DB_CLOSE_DELAY=-1;";
	private static final String DB_USERNAME = "system";
	private static final String DB_PASSWORD = "abc123";

	public static DbUtils _instance = null;

	private DbUtils() {

	}

	public static DbUtils getInstance() {
		if(_instance == null){
			synchronized(DbUtils.class){
				if(_instance == null){
					_instance = new DbUtils();
				}
			}
		}
		return _instance;
	}

	public Connection getConnection() {
		Logger.debug("Starting getConnection() in DbUtils");
		Connection connection = null;
		try {
			Class.forName(DB_DRIVER);
			connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
			Logger.error("Connection ERROR");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			Logger.error("Connection ERROR");
		}
		return connection;
	}

	public void loadInitData() {
		Logger.info("Initializing data ..... ");
		try(Connection conn = DbUtils.getInstance().getConnection()){
			RunScript.execute(conn, new FileReader("src/main/resources/db.sql"));
		} catch (SQLException e) {
			Logger.error("Exception occured while loading init data ", e);
			throw new RuntimeException(e);
		} catch (FileNotFoundException e) {
			Logger.error("Exception occured while loading file in loadInitData():", e);
			throw new RuntimeException(e);
		}
	}

	public void savePrepareStatement(Connection conn, PreparedStatement statement, LinkedHashMap<String,Object> criteria){
		Logger.info("createPreparedStatement() : Preparing the query for execution");

		int[] pos = new int[]{1};

		criteria.keySet().forEach(key -> {
			Object value = criteria.get(key);
			int i = pos[0]++;
			try{
				if(value != null && value instanceof String) {
					statement.setString(i, value.toString());
				}else if(value != null && value instanceof Integer) {
					statement.setInt(i, Integer.valueOf(value.toString()));
				}else if(value != null && value instanceof Long) {
					statement.setLong(i, Long.valueOf(value.toString()));
				}else if(value != null && value instanceof BigDecimal) {
					statement.setBigDecimal(i, (BigDecimal)value);
				}else {
					statement.setNull(i,java.sql.Types.VARCHAR);
				}
			}catch(SQLException e){
				Logger.error("Exception occured while preparing the query",e.getMessage());
				return;
			}
		});
	}
}
