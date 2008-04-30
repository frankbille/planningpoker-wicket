package org.planningpoker.test;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

public class JettyStarter {

	public static void main(String[] args) throws Exception {
		System.setProperty("wicket.configuration", "DEVELOPMENT");

		Server server = new Server(8080);
		server.addHandler(new WebAppContext("src/main/webapp", "/"));
		server.start();
	}

}
