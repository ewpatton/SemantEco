package edu.rpi.tw.escience.semanteco;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

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
