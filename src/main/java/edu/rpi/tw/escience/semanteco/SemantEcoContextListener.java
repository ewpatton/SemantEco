package edu.rpi.tw.escience.semanteco;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * This listener fires when the SemantEco context has initialized and sets
 * up information about the root path for SemantEco.
 *
 * @author ewpatton
 *
 */
@WebListener
public class SemantEcoContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		if(System.getProperty("semantEcoRootPath") == null) {
			System.setProperty("semantEcoRootPath", context.getRealPath("/"));
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
