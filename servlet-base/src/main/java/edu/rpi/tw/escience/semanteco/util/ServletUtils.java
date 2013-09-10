package edu.rpi.tw.escience.semanteco.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.simple.JSONObject;

import edu.rpi.tw.escience.semanteco.HierarchicalMethod;
import edu.rpi.tw.escience.semanteco.HierarchyEntry;
import edu.rpi.tw.escience.semanteco.HierarchyVerb;
import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.ModuleManager;
import edu.rpi.tw.escience.semanteco.QueryMethod;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.i18n.Messages;
import edu.rpi.tw.escience.semanteco.impl.ModuleManagerFactory;
import edu.rpi.tw.escience.semanteco.request.ClientRequest;

public class ServletUtils {

	private static final String PROPERTIES = "/WEB-INF/classes/semanteco.properties";
	private static final int HTTP = 80;
	private static final int HTTPS = 443;

	private Properties props = new Properties();
	private Logger log = null;

	public ServletUtils(ServletConfig config, ServletContext context) {
		initLogger(config);
		log.info("Initializing SemantEco");
		log.debug("Running on servlet version: "+
				context.getMajorVersion());
		log.debug("Servlet context path: "+
				context.getContextPath());
	    String basePath = context.getRealPath("/");
	    InputStream properties = context.getResourceAsStream(PROPERTIES);
	    SemantEcoConfiguration.configure(basePath, properties);
	    try {
	      properties.close();
	    } catch (IOException e) {
	      log.warn("Unable to close property input stream.", e);
	    }
        final String webinf = config.getServletContext().getRealPath("WEB-INF");
	    log.debug("WEB-INF: "+webinf);
		InputStream is = null;
		try {
			is = new FileInputStream(webinf+"/classes/semanteco.properties");
			props.load(is);
			log.info("Successfully read properties from semanteco.properties");
		}
		catch(IOException e) {
			log.warn("Unable to read semanteco.properties", e);
		}
		finally {
			if(is != null) {
				try {
					is.close();
				}
				catch(IOException e) {
					// assume success even with an exception
				}
			}
		}
		File modules = new File(webinf+"/modules");
		if(!modules.exists()) {
			log.info("Creating modules directory");
			if(!modules.mkdir()) {
				log.error("Unable to make module directory. Running modules " +
						"will be restricted to those packaged in the " +
						"web archive.");
			}
		}
		ModuleManagerFactory.getInstance().
			setModulePath(modules.getAbsolutePath());
		log.info("Finished initializing SemantEco");
	}

	public void printConfig(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setHeader("Content-Type", "text/javascript");
		PrintStream ps =
				new PrintStream(response.getOutputStream(), true,
						SemantEcoConfiguration.get().getEncoding());
		String baseUrl = computeBaseUrl(request);
		if(SemantEcoConfiguration.get().isDebug()) {
			ps.println(Messages.AUTOGEN+getClass().getName()+
					"#printConfig");
		}
		ps.println("SemantEco.baseUrl=\""+baseUrl+"\";\n" +
				"SemantEco.restBaseUrl=\""+baseUrl+"rest/\";");
		ps.println("if(SemantEco.baseUrl!==location.href){"
				+ "SemantEco.baseUrl=URI(location.href).fragment('').query('').toString();"
				+ "SemantEco.restBaseUrl=SemantEco.baseUrl+'rest/';"
				+ "}");
		ps.close();
	}

	private String computeBaseUrl(HttpServletRequest request) {
		if(props.containsKey("baseUrl") && !props.get("baseUrl").equals("")) {
			return props.getProperty("baseUrl");
		}
		else {
			String path = "";
			path += request.getScheme();
			path += "://";
			path += request.getServerName();
			if(request.getScheme().equals("http") &&
					request.getServerPort() != HTTP) {
				path += ":"+request.getServerPort();
			}
			else if(request.getScheme().equals("https") &&
					request.getServerPort() != HTTPS) {
				path += ":"+request.getServerPort();
			}
			path += request.getContextPath();
			path += "/";
			return path;
		}
	}

	@SuppressWarnings("unchecked")
	private String serializeHierarchyEntries(Collection<HierarchyEntry> entries) {
		JSONArray arr = new JSONArray();
		Iterator<HierarchyEntry> i = entries.iterator();
		while(i.hasNext()) {
			arr.put(i.next().toJSONObject());
		}
		JSONObject result = new JSONObject();
		result.put("success", true);
		result.put("results", arr);
		return result.toString();
	}

	private String invokeHierarchyMethod(HttpServletResponse response,
			ClientRequest logger, Module module, Method m)
			throws IOException, IllegalAccessException, InvocationTargetException {
		Object mode = logger.getParam("mode");
		if(!(mode instanceof String)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					Messages.MODE_NOTVALID);
			return null;
		}
		HierarchyVerb verb = null;
		try {
			verb = HierarchyVerb.valueOf((String)mode);
		} catch(IllegalArgumentException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					Messages.MODE_NOTVALID);
			return null;
		}
		@SuppressWarnings("unchecked")
		Collection<HierarchyEntry> entries =
				(Collection<HierarchyEntry>) m.invoke(module, logger,
													  verb);
		return serializeHierarchyEntries(entries);
	}

	public void invokeRestCall(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		invokeRestCall(buildRequest(request), request, response);
	}

	public void invokeRestCall(ClientRequest logger, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Module module = getModule(request);
		String processed = request.getPathInfo();
		processed = processed.substring(1);
		final String methodName = processed.substring(processed.indexOf('/')+1);
		log.debug("method name = "+methodName);
		try {
			Method m = getMethod(module, methodName);
			logger.debug("Invoking " + methodName + " of " + module.getName());
			final long start = System.currentTimeMillis();
			String result = null;
			if(m.isAnnotationPresent(QueryMethod.class)) {
				result = (String) m.invoke(module, logger);
			} else if(m.isAnnotationPresent(HierarchicalMethod.class)) {
				result = invokeHierarchyMethod(response, logger, module, m);
				if(result != null) {
					response.setHeader("Content-Type", "application/json");
				}
			} else {
				response.sendError(HttpServletResponse.SC_FORBIDDEN,
						Messages.MODULE_INVALID);
				return;
			}
			if( result == null ) {
				return;
			}
			log.debug("Response time: "+(System.currentTimeMillis()-start)+
					" ms");
			logger.debug("Returning response to client");
			PrintStream ps =
					new PrintStream(response.getOutputStream(), true,
							SemantEcoConfiguration.get().getEncoding());
			ps.print(result);
			ps.close();
		} catch (SecurityException e) {
			logger.error("Unable to execute specified method", e);
		} catch (NoSuchMethodException e) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN,
					Messages.MODULE_INVALID);
			logger.error("Invalid method", e);
		} catch (IllegalArgumentException e) {
			logger.error("Illegal argument", e);
		} catch (IllegalAccessException e) {
			logger.error("Illegal access", e);
		} catch (InvocationTargetException e) {
			logger.error("Invalid target for invocation", e);
		}
	}

	private URL getURL(final HttpServletRequest request) {
		final StringBuffer temp = request.getRequestURL();
		final String params = request.getQueryString();
		if(params != null) {
			temp.append("?");
			temp.append(params);
		}
		try {
			return new URL(temp.toString());
		} catch(MalformedURLException e) {
			return null;
		}
	}

	private Method getMethod(Module module, String name)
			throws NoSuchMethodException {
		Method m = null;
		try {
			m = module.getClass().getMethod(name, Request.class);
		} catch(NoSuchMethodException e) {
			try {
				m = module.getClass().getMethod(name, Request.class,
						HierarchyVerb.class);
			} catch(NoSuchMethodException e2) {
				throw e;
			}
		}
		return m;
	}

	public void printAjax(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setHeader("Content-Type", "text/javascript");
		PrintStream ps =
				new PrintStream(response.getOutputStream(), true,
						SemantEcoConfiguration.get().getEncoding());
		String module =
				request.getPathInfo().replace("/", "").replace(".js", "");
		ModuleManager mgr = ModuleManagerFactory.getInstance().getManager();
		Module mod = mgr.getModuleByName(module);
		if(mod != null) {
			ps.println(JavaScriptGenerator.ajaxForModule(mod));
		}
		ps.close();
	}

	public int getIntCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			for(int i=0;i<cookies.length;i++) {
				Cookie cookie = cookies[i];
				if(cookie.getName().equals(name)) {
					try {
						return Integer.parseInt(cookie.getValue());
					} catch(NumberFormatException e) {
						return -1;
					}
				}
			}
		}
		return -1;
	}

	public int extractSocketId(HttpServletRequest request) {
		return getIntCookie(request, "socketId");
	}

	public int extractProvenanceId(HttpServletRequest request) {
		return getIntCookie(request, "provenanceId");
	}

	private final void initLogger(ServletConfig config) {
		final String log4jconfig =
				config.getInitParameter("log4j-properties-location");
		ServletContext context = config.getServletContext();
		if(log4jconfig == null) {
			BasicConfigurator.configure();
		}
		else {
			String appPath = context.getRealPath("/");
			String log4jpath = appPath + log4jconfig;
			if(new File(log4jpath).exists()) {
				PropertyConfigurator.configure(log4jpath);
				log = Logger.getLogger(this.getClass().getName());
			}
			else {
				BasicConfigurator.configure();
			}
		}
	}

	protected Module getModule(HttpServletRequest request) {
		String processed = request.getPathInfo();
		processed = processed.substring(1);
		final String modName = processed.substring(0, processed.indexOf('/'));
		log.debug("module name = "+modName);
		return ModuleManagerFactory.getInstance().getManager()
				.getModuleByName(modName);
	}

	public ClientRequest buildRequest(HttpServletRequest request) {
		URL original = getURL(request);
		Module module = getModule(request);
		return new ClientRequest(module.getClass().getName(),
				request.getParameterMap(), original,
				ModuleManagerFactory.getInstance().getManager());
	}
}
