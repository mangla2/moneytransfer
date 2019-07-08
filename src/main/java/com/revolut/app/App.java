package com.revolut.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.revolut.app.config.AppServer;
import com.revolut.app.config.DbUtils;

public class App {

	private static Logger logger = LogManager.getLogger(App.class);

	public static void main( String[] args) throws Exception {
		logger.info("Populating pre-staging data in database");
		DbUtils.getInstance().loadInitData();
		logger.info("Starting server");
		AppServer.startServer();
	}
	
}