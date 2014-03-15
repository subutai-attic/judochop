package org.safehaus.chop.webapp;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;


public class Launcher {

    private static String contextPath = "/";
    private static String resourceBase = ".";
    private static int httpPort = 8888;

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        ServletContextHandler handler = new ServletContextHandler();
        server.setHandler(handler);
        handler.addServlet(UIServlet.class, "/*");
        handler.addServlet(UIServlet.class, "/VAADIN/*");
        handler.setSessionHandler(new SessionHandler());
        server.start();
        server.join();
    }
}
