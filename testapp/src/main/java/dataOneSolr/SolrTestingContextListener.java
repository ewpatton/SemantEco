package dataOneSolr;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class SolrTestingContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        if(System.getProperty("SolrTestingRootPath") == null) {
            System.setProperty("SolrTestingRootPath", context.getRealPath("/"));
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
