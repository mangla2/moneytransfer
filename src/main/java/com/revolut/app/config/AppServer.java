package com.revolut.app.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

public class AppServer {

    private static final Logger Logger = LogManager.getLogger(AppServer.class);
    private static final int SERVER_PORT = 8081;
    private static final String CONTEXT_PATH = "/*";

    private static Server getServer() {
        AppConfig config = new AppConfig();
        ServletHolder servlet = new ServletHolder(new ServletContainer(config));
        Server server = new Server(SERVER_PORT);
        ServletContextHandler context = new ServletContextHandler(server, "/*");
        context.addServlet(servlet, CONTEXT_PATH);
        return server;
    }

    public static void startServer() {
    	Server server = getServer();
        try {
            server.start();
            server.join();
        } catch (Exception e) {
        	Logger.error("Exception occured while starting server: " + e.getClass() + " " + e.getMessage());
            System.exit(1);
        } finally {
            server.destroy();
        }
    }
}