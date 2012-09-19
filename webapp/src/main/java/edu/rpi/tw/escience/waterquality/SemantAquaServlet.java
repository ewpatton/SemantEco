package edu.rpi.tw.escience.waterquality;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name="SemantAqua",
			urlPatterns={"/rest/*","/js/modules/*","/js/config.js"},
			description="SemantAqua Portal",
			displayName="SemantAqua")
public class SemantAquaServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5803626987887478846L;
	
	private static final String PROPERTIES = "/WEB-INF/classes/semantaqua.properties";
	
	private Properties props = new Properties();
	
	private static boolean debugging = false;
	
	private static final int HTTP_PORT = 80;
	private static final int HTTPS_PORT = 443;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		getServletContext().log("Initializing SemantAqua");
		getServletContext().log("Running on servlet version: "+getServletContext().getMajorVersion());
		getServletContext().log("Servlet context path: "+getServletContext().getContextPath());
		try {
			InputStream is = getServletContext().getResourceAsStream(PROPERTIES);
			if(is != null) {
				props.load(is);
				if(props.getProperty("debug", "false").equals("true")) {
					debugging = true;
				}
			}
			else {
				throw new IOException("Unable to get resource "+PROPERTIES);
			}
		} catch (IOException e) {
			getServletContext().log("Unable to load "+PROPERTIES, e);
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		getServletContext().log(request.getScheme());
		getServletContext().log(request.getServerName());
		getServletContext().log(""+request.getServerPort());
		getServletContext().log(request.getContextPath());
		getServletContext().log(request.getServletPath());
		getServletContext().log(request.getPathInfo());
		getServletContext().log(request.getRequestURI());
		PrintStream ps = null;
		if(request.getServletPath().equals("/js/config.js")) {
			printConfig(request, response);
		}
		else {
			ps = new PrintStream(response.getOutputStream());
			ps.println("<h1>It works!</h1>");
			ps.close();
		}
	}
	
	@Override
	public String getServletInfo() {
		getServletConfig().getServletContext().getResourceAsStream("/META-INF/maven/edu.rpi.tw.escience/semantauqa/pom.properties");
		return "SemantAqua";
	}
	
	@Override
	public long getLastModified(HttpServletRequest request) {
		return -1;
	}
	
	private void printConfig(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setHeader("Content-Type", "text/javascript");
		PrintStream ps = new PrintStream(response.getOutputStream());
		String baseUrl = computeBaseUrl(request);
		if(isDebug()) {
			ps.println("// file autogenerated by "+getClass().getName()+"#printConfig");
		}
		ps.println("var SemantAqua = { \"baseUrl\": \""+baseUrl+"\", \"restBaseUrl\": \""+baseUrl+"rest/\" }");
		ps.close();
	}
	
	private String computeBaseUrl(HttpServletRequest request) {
		if(props.contains("baseUrl") && !props.get("baseUrl").equals("")) {
			return props.getProperty("baseUrl");
		}
		else {
			String path = "";
			path += request.getScheme();
			path += "://";
			path += request.getServerName();
			if(request.getScheme().equals("http") && request.getServerPort() != HTTP_PORT) {
				path += ":"+request.getServerPort();
			}
			else if(request.getScheme().equals("https") && request.getServerPort() != HTTPS_PORT) {
				path += ":"+request.getServerPort();
			}
			path += request.getContextPath();
			path += "/";
			return path;
		}
	}
	
	/**
	 * Returns whether the debug property is set to true for this deployment or not
	 * @return
	 */
	public static boolean isDebug() {
		return debugging;
	}

}
