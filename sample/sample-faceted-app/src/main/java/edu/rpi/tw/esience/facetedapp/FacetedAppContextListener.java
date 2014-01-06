package edu.rpi.tw.esience.facetedapp;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class FacetedAppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        if(System.getProperty("FacetedAppRootPath") == null) {
            System.setProperty("FacetedAppRootPath", context.getRealPath("/"));
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
