package edu.rpi.tw.escience.waterquality.res;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import edu.rpi.tw.escience.waterquality.Module;

/**
 * Reference to a JSP file provided in a module's jar file.
 * 
 * @author ewpatton
 *
 */
public class JspResource extends OwnedResource {

	private String path = null;
	private Logger log = Logger.getLogger(JspResource.class);
	
	/**
	 * Creates a JspResource reference for the given module
	 * named by path
	 * 
	 * @param owner
	 * @param path
	 */
	public JspResource(Module owner, String path) {
		super(owner);
		this.path = path;
	}
	
	@Override
	public String getPath() {
		return path;
	}

	@Override
	public InputStream open() throws IOException {
		return (new URL(path)).openStream();
	}

	@Override
	public boolean isJspResource() {
		return true;
	}
	
	/**
	 * Dispatches a request to the JSP named by the resource
	 * so that it can provide output to the main SemantAqua interface.
	 * @param context
	 * @param request
	 * @param response
	 */
	public void dispatch(ServletContext context, HttpServletRequest request, HttpServletResponse response) {
		try {
			context.getRequestDispatcher(path).forward(request, response);
		} catch (ServletException e) {
			log.warn("Unable to dispatch request to JSP file", e);
		} catch (IOException e) {
			log.warn("Unable to write response to client", e);
		}
	}

}
