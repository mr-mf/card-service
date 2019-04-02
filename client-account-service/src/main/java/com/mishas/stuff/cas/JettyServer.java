package com.mishas.stuff.cas;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

public class JettyServer {

    private static final Logger logger = Logger.getLogger(JettyServer.class);
    private static final String CONTEXT_PATH = "/";
    private static final String APPLICATION_PATH = "/api/v1";

    public static void main(String ... args) throws Exception {

        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        final ServletHolder servlet = new ServletHolder(new HttpServletDispatcher());
        final Server server = new Server(8080);
        context.setContextPath(CONTEXT_PATH);

        servlet.setInitParameter("resteasy.servlet.mapping.prefix", APPLICATION_PATH);
        servlet.setInitParameter("javax.ws.rs.Application", ClientAccountApplication.class.getCanonicalName());
        context.addServlet(servlet,  APPLICATION_PATH + "/*");
        server.setHandler(context);

        try {
            server.start();
            server.join();
        } catch (Exception e){
            logger.error("error during server starting: {}",e);
            server.stop();
            server.destroy();
        }

    }
}
