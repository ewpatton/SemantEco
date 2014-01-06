package edu.rpi.tw.escience.semanteco;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.PrintStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import edu.rpi.tw.escience.semanteco.i18n.Messages;
import edu.rpi.tw.escience.semanteco.impl.ModuleManagerFactory;
import edu.rpi.tw.escience.semanteco.util.SemantEcoConfiguration;
import edu.rpi.tw.escience.semanteco.util.ServletUtils;

/**
 * The SemantEcoServlet class provides the main entry point to SemantEco and is
 * primarily concerned with handling dynamic requests and performing the initial
 * configuration of the portal and its dependencies.
 * @author ewpatton
 *
 */
public class BaseSemantEcoServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5803626987887478846L;

    private ServletUtils utils = null;

    /**
	 * Default logger for the SemantEcoServlet. Request objects may be used in
	 * place of loggers to send debugging messages to the client.
	 */
	private static Logger log = null;

	protected final ServletUtils getUtils() {
	    return utils;
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		utils = new ServletUtils(config, getServletContext());
		log = Logger.getLogger(BaseSemantEcoServlet.class);
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		if(request.getServletPath().equals("/log")) {
			super.doGet(request, response);
			return;
		}
		if(request.getServletPath().equals("/provenance")) {
		  super.doGet(request, response);
		  return;
		}
		PrintStream ps = null;
		if(request.getServletPath().equals("/js/config.js")) {
			utils.printConfig(request, response);
		}
		else if(request.getServletPath().equals("/js/modules")) {
			utils.printAjax(request, response);
		}
		else if(request.getServletPath().startsWith("/rest")) {
			utils.invokeRestCall(request, response);
		}
		else {
			ps = new PrintStream(response.getOutputStream(), true,
					SemantEcoConfiguration.get().getEncoding());
			ps.println("<h1>It works!</h1>");
			ps.close();
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		if(!request.getServletPath().startsWith("/rest")) {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			response.setHeader("Accept", "HEAD GET");
			PrintStream ps =
					new PrintStream(response.getOutputStream(), true,
							SemantEcoConfiguration.get().getEncoding());
			ps.println(Messages.METHOD_ONLYGET);
			ps.close();
			return;
		}
		log.debug("Handling POST call");
		utils.invokeRestCall(request, response);
	}

	@Override
	public String getServletInfo() {
		return "SemantEco";
	}
	
	@Override
	public long getLastModified(HttpServletRequest request) {
		return -1;
	}
	
	@Override
	public void destroy() {
		ModuleManagerFactory.destroy();
	}

	private Object readResolve() throws ObjectStreamException {
		return this;
	}
}
