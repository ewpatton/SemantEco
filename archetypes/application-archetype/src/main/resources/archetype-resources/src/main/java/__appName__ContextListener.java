#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ${appName}ContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        if(System.getProperty("${appName}RootPath") == null) {
            System.setProperty("${appName}RootPath", context.getRealPath("/"));
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
