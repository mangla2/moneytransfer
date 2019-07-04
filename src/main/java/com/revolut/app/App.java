
package com.revolut.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.revolut.app.config.AppServer;


public class App {

	private static Logger logger = LogManager.getLogger(App.class);

	public static void main( String[] args) {
		logger.debug("STARTING SERVER");
		AppServer.startServer();
	}
	
}