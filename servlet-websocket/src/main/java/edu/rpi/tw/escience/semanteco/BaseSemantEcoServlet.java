package edu.rpi.tw.escience.semanteco;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import edu.rpi.tw.escience.semanteco.i18n.Messages;
import edu.rpi.tw.escience.semanteco.impl.ModuleManagerFactory;
import edu.rpi.tw.escience.semanteco.request.WsClientRequest;
import edu.rpi.tw.escience.semanteco.util.JavaScriptGenerator;
import edu.rpi.tw.escience.semanteco.util.SemantEcoConfiguration;

/**
 * The SemantEcoServlet class provides the main entry point to SemantEco and is
 * primarily concerned with handling dynamic requests and performing the initial
 * configuration of the portal and its dependencies.
 * @author ewpatton
 *
 */
public class BaseSemantEcoServlet extends WebSocketServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5803626987887478846L;
	private static final String POM_PROPERTIES =
		"/META-INF/maven/edu.rpi.tw.escience/semanteco-webapp/pom.properties";
  private static final String PROPERTIES = "/WEB-INF/classes/semanteco.properties";

	private Properties props = new Properties();
	private Random random = new Random();

	private static final int HTTP = 80;
	private static final int HTTPS = 443;

	/**
	 * Default logger for the SemantEcoServlet. Request objects may be used in
	 * place of loggers to send debugging messages to the client.
	 */
	private static Logger log = null;

	/**
	 * Stores WebSocket connections used for client-side logging.
	 */
	private final transient Map<Integer, ResponseChannel> channels =
			new TreeMap<Integer, ResponseChannel>();

	/**
	 * Stores WebSocket connections used for accumulating provenance on the
	 * client.
	 */
	private final transient Map<Integer, ProvenanceChannel> provenanceChannels =
			new TreeMap<Integer, ProvenanceChannel>();;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		initLogger(config);
		log.info("Initializing SemantEco");
		log.debug("Running on servlet version: "+
				getServletContext().getMajorVersion());
		log.debug("Servlet context path: "+
				getServletContext().getContextPath());
		ServletContext context = getServletContext();
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
	
	private void initLogger(ServletConfig config) {
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

	protected int getIntCookie(HttpServletRequest request, String name) {
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

	protected int extractSocketId(HttpServletRequest request) {
		return getIntCookie(request, "socketId");
	}

	protected int extractProvenanceId(HttpServletRequest request) {
		return getIntCookie(request, "provenanceId");
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
		int socketId = extractSocketId(request);
		WsOutbound clientStream = null;
		if(socketId != -1) {
			ResponseChannel channel = channels.get(socketId);
			if(channel != null) {
				clientStream = channel.getWsOutbound();
			}
		}
		int provId = extractProvenanceId(request);
		WsOutbound provenanceStream = null;
		if(provId != -1) {
			ProvenanceChannel channel = provenanceChannels.get(provId);
			if(channel != null) {
				provenanceStream = channel.getWsOutbound();
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
			invokeRestCall(request, response, clientStream, provenanceStream);
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
		int socketId = extractSocketId(request);
		WsOutbound clientStream = null;
		if(socketId != -1) {
			ResponseChannel channel = channels.get(socketId);
			if(channel != null) {
				clientStream = channel.getWsOutbound();
			}
		}
		int provId = extractProvenanceId(request);
		WsOutbound provenanceStream = null;
		if(provId != -1) {
			ProvenanceChannel channel = provenanceChannels.get(provId);
			if(channel != null) {
				provenanceStream = channel.getWsOutbound();
			}
		}
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
		invokeRestCall(request, response, clientStream, provenanceStream);
	}

	@Override
	public String getServletInfo() {
		getServletContext().getResourceAsStream(POM_PROPERTIES);
		return "SemantEco";
	}
	
	@Override
	public long getLastModified(HttpServletRequest request) {
		return -1;
	}
	
	private void printConfig(HttpServletRequest request,
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
		ps.close();
	}
	
	private void printAjax(HttpServletRequest request,
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

	private String invokeHierarchyMethod(HttpServletResponse response,
			WsClientRequest logger, Module module, Method m)
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

	private void invokeRestCall(HttpServletRequest request,
			HttpServletResponse response, WsOutbound clientStream,
			WsOutbound provenanceStream)
					throws IOException {
		URL original = getURL(request);
		String processed = request.getPathInfo();
		processed = processed.substring(1);
		final String modName = processed.substring(0, processed.indexOf('/'));
		final String methodName = processed.substring(processed.indexOf('/')+1);
		log.debug("module name = "+modName);
		log.debug("method name = "+methodName);
		final Module module = ModuleManagerFactory.getInstance().getManager()
				.getModuleByName(modName);
		final WsClientRequest logger =
				new WsClientRequest(module.getClass().getName(),
						request.getParameterMap(), original,
						ModuleManagerFactory.getInstance().getManager(),
						clientStream, provenanceStream);
		try {
			Method m = getMethod(module, methodName);
			logger.debug("Invoking " + methodName + " of " + modName);
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

	@SuppressWarnings("unchecked")
	private String serializeHierarchyEntries(Collection<HierarchyEntry> entries) {
		JSONArray arr = new JSONArray();
		Iterator<HierarchyEntry> i = entries.iterator();
		while(i.hasNext()) {
			arr.add(i.next().toJSONObject());
		}
		JSONObject result = new JSONObject();
		result.put("success", true);
		result.put("results", arr);
		return result.toString();
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
		int id;
		synchronized(random) {
			id = random.nextInt();
		}
		log.debug("creating response channel");
		return new ResponseChannel(channels, id);
	}
	
	private static class ProvenanceChannel extends MessageInbound {
		private int id = 0;
		private Map<Integer, ProvenanceChannel> channels;

		/**
		 * Constructs a provenance channel identified by the specified id.
		 * @param id A unique identifier for the channel.
		 */
		@SuppressWarnings("unused")
		public ProvenanceChannel(Map<Integer, ProvenanceChannel> channels, int id) {
		  this.id = id;
		  this.channels = channels;
		  channels.put(id, this);
		}

	    @Override
	    protected void onBinaryMessage(ByteBuffer message) throws IOException {
	    }

	    @Override
	    protected void onTextMessage(CharBuffer message) throws IOException {
	    }

	    @Override
	    protected void onClose(int status) {
	      super.onClose(status);
	      channels.remove(id);
	    }
	}

	private static class ResponseChannel extends MessageInbound {
		
		private int id = 0;
		private Map<Integer, ResponseChannel> channels;
		
		/**
		 * Constructs a response channel identified by the specified id.
		 * @param id A unique identifier for the channel.
		 */
		public ResponseChannel(Map<Integer, ResponseChannel> channels, int id) {
			this.id = id;
			this.channels = channels;
			channels.put(id, this);
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
			channels = null;
		}

	}

	private Object readResolve() throws ObjectStreamException {
		channels.clear();
		provenanceChannels.clear();
		return this;
	}
}
