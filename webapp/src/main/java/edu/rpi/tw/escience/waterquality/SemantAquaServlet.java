package edu.rpi.tw.escience.waterquality;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
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

import edu.rpi.tw.escience.waterquality.impl.ModuleManagerFactory;
import edu.rpi.tw.escience.waterquality.util.JavaScriptGenerator;
import edu.rpi.tw.escience.waterquality.util.SemantAquaConfiguration;

/**
 * The SemantAquaServlet class provides the main entry point to SemantAqua and is
 * primarily concerned with handling dynamic requests and performing the initial
 * configuration of the portal and its dependencies.
 * 
 * @author ewpatton
 *
 */
@WebServlet(name="SemantAqua",
			urlPatterns={"/rest/*","/js/modules/*","/js/config.js","/log"},
			description="SemantAqua Portal",
			displayName="SemantAqua")
public class SemantAquaServlet extends WebSocketServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5803626987887478846L;
	
	private Properties props = new Properties();
	
	private static final int HTTP = 80;
	private static final int HTTPS = 443;
	
	private Logger log = null;
	
	private static Map<Integer, ResponseChannel> channels = new TreeMap<Integer, ResponseChannel>();
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		initLogger(config);
		log.info("Initializing SemantAqua");
		log.debug("Running on servlet version: "+getServletContext().getMajorVersion());
		log.debug("Servlet context path: "+getServletContext().getContextPath());
		SemantAquaConfiguration.configure(getServletContext());
		final String webinf = config.getServletContext().getRealPath("WEB-INF");
		log.debug("WEB-INF: "+webinf);
		File modules = new File(webinf+"/modules");
		if(!modules.exists()) {
			log.info("Creating modules directory");
			modules.mkdir();
		}
		ModuleManagerFactory.getInstance().setModulePath(modules.getAbsolutePath());
		log.info("Finished initializing SemantAqua");
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
				log = Logger.getLogger(SemantAquaServlet.class);
			}
			else {
				BasicConfigurator.configure();
			}
		}
	}
	
	protected int extractSocketId(HttpServletRequest request) {
		for(int i=0;i<request.getCookies().length;i++) {
			Cookie cookie = request.getCookies()[i];
			if(cookie.getName().equals("socketId")) {
				return Integer.parseInt(cookie.getValue());
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
		log.debug("socketId = "+socketId);
		WsOutbound clientStream = null;
		if(socketId != -1) {
			ResponseChannel channel = channels.get(socketId);
			if(channel != null) {
				clientStream = channel.getWsOutbound();
			}
		}
		log.debug("clientStream = "+clientStream);
		log.debug(request.getScheme());
		log.debug(request.getServerName());
		log.debug(""+request.getServerPort());
		log.debug(request.getContextPath());
		log.debug(request.getServletPath());
		log.debug(request.getPathInfo());
		log.debug(request.getRequestURI());
		PrintStream ps = null;
		if(request.getServletPath().equals("/js/config.js")) {
			printConfig(request, response);
		}
		else if(request.getServletPath().equals("/js/modules")) {
			printAjax(request, response);
		}
		else if(request.getServletPath().startsWith("/rest")) {
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
				m = module.getClass().getMethod(methodName, Request.class);
				if(m == null || m.getAnnotation(QueryMethod.class)==null) {
					response.sendError(403, "Invalid module or method specified in REST call");
					return;
				}
				logger.debug("Invoking "+methodName+" of "+modName);
				String result = (String) m.invoke(module, logger);
				logger.debug("Returning response to client");
				ps = new PrintStream(response.getOutputStream());
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
		else {
			ps = new PrintStream(response.getOutputStream());
			ps.println("<h1>It works!</h1>");
			ps.close();
		}
	}
	
	@Override
	public String getServletInfo() {
		getServletContext().getResourceAsStream("/META-INF/maven/edu.rpi.tw.escience/semantauqa/pom.properties");
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
		if(SemantAquaConfiguration.get().isDebug()) {
			ps.println("// file autogenerated by "+getClass().getName()+"#printConfig");
		}
		ps.println("SemantAqua.baseUrl=\""+baseUrl+"\";\n" +
				"SemantAqua.restBaseUrl=\""+baseUrl+"rest/\";");
		ps.close();
	}
	
	private void printAjax(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setHeader("Content-Type", "text/javascript");
		PrintStream ps = new PrintStream(response.getOutputStream());
		String module = request.getPathInfo().replace("/", "").replace(".js", "");
		ModuleManager mgr = ModuleManagerFactory.getInstance().getManager();
		Module mod = mgr.getModuleByName(module);
		if(mod != null) {
			ps.println(JavaScriptGenerator.ajaxForModule(mod));
		}
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
