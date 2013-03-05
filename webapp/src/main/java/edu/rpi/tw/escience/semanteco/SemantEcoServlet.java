package edu.rpi.tw.escience.semanteco;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;

import edu.rpi.tw.escience.semanteco.impl.ModuleManagerFactory;
import edu.rpi.tw.escience.semanteco.util.JavaScriptGenerator;
import edu.rpi.tw.escience.semanteco.util.SemantEcoConfiguration;

/**
 * The SemantEcoServlet class provides the main entry point to SemantEco and is
 * primarily concerned with handling dynamic requests and performing the initial
 * configuration of the portal and its dependencies.
 * @author ewpatton
 *
 */
@WebServlet(name="SemantEco",
			urlPatterns={"/rest/*","/js/modules/*","/js/config.js","/log"},
			description="SemantEco Portal",
			displayName="SemantEco")
public class SemantEcoServlet extends WebSocketServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5803626987887478846L;
	
	private Properties props = new Properties();
	
	private static final int HTTP = 80;
	private static final int HTTPS = 443;
	
	private static Logger log = null;
	
	private static Map<Integer, ResponseChannel> channels = new TreeMap<Integer, ResponseChannel>();
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		initLogger(config);
		log.info("Initializing SemantEco");
		log.debug("Running on servlet version: "+getServletContext().getMajorVersion());
		log.debug("Servlet context path: "+getServletContext().getContextPath());
		SemantEcoConfiguration.configure(getServletContext());
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
			modules.mkdir();
		}
		ModuleManagerFactory.getInstance().setModulePath(modules.getAbsolutePath());
		log.info("Finished initializing SemantEco");
	}
	
	private void initLogger(ServletConfig config) {
		String log4jconfig = config.getInitParameter("log4j-properties-location");
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
	
	protected int extractSocketId(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			for(int i=0;i<cookies.length;i++) {
				Cookie cookie = cookies[i];
				if(cookie.getName().equals("socketId")) {
					return Integer.parseInt(cookie.getValue());
				}
			}
		}
		return -1;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		if(request.getServletPath().equals("/log")) {
			super.doGet(request, response);
			return;
		}
		int socketId = extractSocketId(request);
		WsOutbound clientStream = null;
		if(socketId != -1) {
			ResponseChannel channel = channels.get(socketId);
			if(channel != null) {
				clientStream = channel.getWsOutbound();
			}
		}
		PrintStream ps = null;
		if(request.getServletPath().equals("/js/config.js")) {
			printConfig(request, response);
		}
		else if(request.getServletPath().equals("/js/modules")) {
			printAjax(request, response);
		}
		else if(request.getServletPath().startsWith("/rest")) {
			invokeRestCall(request, response, clientStream);
		}
		else {
			ps = new PrintStream(response.getOutputStream(), true, "UTF-8");
			ps.println("<h1>It works!</h1>");
			ps.close();
		}
	}
	
	@Override
	public String getServletInfo() {
		getServletContext().getResourceAsStream("/META-INF/maven/edu.rpi.tw.escience/semanteco-webapp/pom.properties");
		return "SemantEco";
	}
	
	@Override
	public long getLastModified(HttpServletRequest request) {
		return -1;
	}
	
	private void printConfig(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setHeader("Content-Type", "text/javascript");
		PrintStream ps = new PrintStream(response.getOutputStream(), true, "UTF-8");
		String baseUrl = computeBaseUrl(request);
		if(SemantEcoConfiguration.get().isDebug()) {
			ps.println("// file autogenerated by "+getClass().getName()+"#printConfig");
		}
		ps.println("SemantEco.baseUrl=\""+baseUrl+"\";\n" +
				"SemantEco.restBaseUrl=\""+baseUrl+"rest/\";");
		ps.close();
	}
	
	private void printAjax(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setHeader("Content-Type", "text/javascript");
		PrintStream ps = new PrintStream(response.getOutputStream(), true, "UTF-8");
		String module = request.getPathInfo().replace("/", "").replace(".js", "");
		ModuleManager mgr = ModuleManagerFactory.getInstance().getManager();
		Module mod = mgr.getModuleByName(module);
		if(mod != null) {
			ps.println(JavaScriptGenerator.ajaxForModule(mod));
		}
		ps.close();
	}
	
	private void invokeRestCall(HttpServletRequest request, HttpServletResponse response, 
			WsOutbound clientStream) throws IOException {
		String processed = request.getPathInfo();
		processed = processed.substring(1);
		final String modName = processed.substring(0, processed.indexOf('/'));
		final String methodName = processed.substring(processed.indexOf('/')+1);
		log.debug("module name = "+modName);
		log.debug("method name = "+methodName);
		final Module module = ModuleManagerFactory.getInstance().getManager().getModuleByName(modName);
		final ClientRequest logger = new ClientRequest(module.getClass().getName(), request.getParameterMap(), clientStream);
		Method m;
		try {
			try {
				m = module.getClass().getMethod(methodName, Request.class);
			} catch(NoSuchMethodException e) {
				try {
					m = module.getClass().getMethod(methodName, Request.class, HierarchyVerb.class);
				} catch(NoSuchMethodException e2) {
					throw e;
				}
			}
			if(m == null || !(m.getAnnotation(QueryMethod.class) != null ||
					m.getAnnotation(HierarchicalMethod.class) != null)) {
				response.sendError(403, "Invalid module or method specified in REST call");
				return;
			}
			logger.debug("Invoking "+methodName+" of "+modName);
			final long start = System.currentTimeMillis();
			String result = null;
			if(m.isAnnotationPresent(QueryMethod.class)) {
				result = (String) m.invoke(module, logger);
			} else if(m.isAnnotationPresent(HierarchicalMethod.class)) {
				Object mode = logger.getParam("mode");
				if(mode == null || !(mode instanceof String)) {
					response.sendError(400, "Mode parameter was not valid");
					return;
				}
				HierarchyVerb verb = null;
				try {
					verb = HierarchyVerb.valueOf((String)mode);
				} catch(IllegalArgumentException e) {
					response.sendError(400, "Mode parameter was not valid");
					return;
				}
				@SuppressWarnings("unchecked")
				Collection<HierarchyEntry> entries =
						(Collection<HierarchyEntry>) m.invoke(module, logger, verb);
				result = serializeHierarchyEntries(entries);
			}
			log.debug("Response time: "+(System.currentTimeMillis()-start)+" ms");
			logger.debug("Returning response to client");
			PrintStream ps = new PrintStream(response.getOutputStream(), true, "UTF-8");
			ps.print(result);
			ps.close();
		} catch (SecurityException e) {
			logger.error("Unable to execute specified method", e);
		} catch (NoSuchMethodException e) {
			logger.error("Invalid method", e);
		} catch (IllegalArgumentException e) {
			logger.error("Illegal argument", e);
		} catch (IllegalAccessException e) {
			logger.error("Illegal access", e);
		} catch (InvocationTargetException e) {
			logger.error("Invalid target for invocation", e);
		}
	}

	private String serializeHierarchyEntries(Collection<HierarchyEntry> entries) {
		JSONArray arr = new JSONArray();
		Iterator<HierarchyEntry> i = entries.iterator();
		while(i.hasNext()) {
			arr.put(i.next().toJSONObject());
		}
		return arr.toString();
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
			if(request.getScheme().equals("http") && request.getServerPort() != HTTP) {
				path += ":"+request.getServerPort();
			}
			else if(request.getScheme().equals("https") && request.getServerPort() != HTTPS) {
				path += ":"+request.getServerPort();
			}
			path += request.getContextPath();
			path += "/";
			return path;
		}
	}
	
	@Override
	public void destroy() {
		ModuleManagerFactory.destroy();
	}

	@Override
	protected StreamInbound createWebSocketInbound(String subProtocol,
			HttpServletRequest request) {
		log.debug("createWebSocketInbound");
		log.debug("subProtocol: "+subProtocol);
		log.debug("request: "+request);
		int id = (int)(Math.random()*Integer.MAX_VALUE);
		log.debug("creating response channel");
		ResponseChannel rc = new ResponseChannel(id);
		channels.put(id, rc);
		return rc;
	}
	
	private static class ResponseChannel extends MessageInbound {
		
		private int id = 0;
		
		/**
		 * Constructs a response channel identified by the specified id.
		 * @param id A unique identifier for the channel.
		 */
		public ResponseChannel(int id) {
			this.id = id;
		}

		@Override
		protected void onBinaryMessage(ByteBuffer message) throws IOException {
			
		}

		@Override
		protected void onTextMessage(CharBuffer message) throws IOException {
			if(message.toString().equals("getId")) {
				getWsOutbound().writeTextMessage(CharBuffer.wrap("{\"socketId\":"+id+"}"));
			}
		}
		
		@Override
		protected void onClose(int status) {
			super.onClose(status);
			channels.remove(id);
		}

	}

}
