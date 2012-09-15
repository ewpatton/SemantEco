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

public class JspResource extends OwnedResource {

	private String path = null;
	private Logger log = Logger.getLogger(JspResource.class);
	
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
