package com.virginholidays;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.net.URI;

/**
 * Main class.
 *
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:5050/properties/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.virginholidays package
        final ResourceConfig rc = new ResourceConfig().packages("com.virginholidays");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();
    }

    public void copypaste() {
//        ServletHandler sa = new ServletHandler();
//        sa.setContextPath("/");
//        sa.setServletInstance(new SpringServlet());
//        sa.addContextParameter("contextConfigLocation", "classpath:spring-context.xml");
//        sa.addServletListener("org.springframework.web.context.ContextLoaderListener");
//        sa.addServletListener("org.springframework.web.context.request.RequestContextListener");
//
//        ServerConfiguration config = server.getServerConfiguration();
//        config.addHttpHandler(sa, new String[] {"/"});
    }

    public void test() {
        ConfigurableApplicationContext cac = new ClassPathXmlApplicationContext("classpath*:/applicationContext-*.xml");
//        IoCComponentProviderFactory factory = new SpringComponentProviderFactory(rc, cac);
//        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc, factory);
    }
}

