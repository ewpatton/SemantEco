package edu.rpi.tw.escience.semanteco;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
import org.apache.log4j.Logger;

import edu.rpi.tw.escience.semanteco.i18n.Messages;
import edu.rpi.tw.escience.semanteco.impl.ModuleManagerFactory;
import edu.rpi.tw.escience.semanteco.request.WsClientRequest;
import edu.rpi.tw.escience.semanteco.util.SemantEcoConfiguration;
import edu.rpi.tw.escience.semanteco.util.ServletUtils;

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

	private ServletUtils utils = null;
	private Random random = null;

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
			new TreeMap<Integer, ProvenanceChannel>();

	public final ServletUtils getUtils() {
	    return utils;
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		utils = new ServletUtils(config, getServletContext());
		log = Logger.getLogger(BaseSemantEcoServlet.class);
		random = new Random();
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
		int socketId = utils.extractSocketId(request);
		WsOutbound clientStream = null;
		if(socketId != -1) {
			ResponseChannel channel = channels.get(socketId);
			if(channel != null) {
				clientStream = channel.getWsOutbound();
			}
		}
		int provId = utils.extractProvenanceId(request);
		WsOutbound provenanceStream = null;
		if(provId != -1) {
			ProvenanceChannel channel = provenanceChannels.get(provId);
			if(channel != null) {
				provenanceStream = channel.getWsOutbound();
			}
		}
		PrintStream ps = null;
		if(request.getServletPath().equals("/js/config.js")) {
			utils.printConfig(request, response);
		}
		else if(request.getServletPath().equals("/js/modules")) {
			utils.printAjax(request, response);
		}
		else if(request.getServletPath().startsWith("/rest")) {
	        WsClientRequest logger;
	        logger = new WsClientRequest(utils.buildRequest(request),
	                clientStream, provenanceStream);
	        utils.invokeRestCall(logger, request, response);
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
		int socketId = utils.extractSocketId(request);
		WsOutbound clientStream = null;
		if(socketId != -1) {
			ResponseChannel channel = channels.get(socketId);
			if(channel != null) {
				clientStream = channel.getWsOutbound();
			}
		}
		int provId = utils.extractProvenanceId(request);
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
        WsClientRequest logger;
        logger = new WsClientRequest(utils.buildRequest(request),
                clientStream, provenanceStream);
        utils.invokeRestCall(logger, request, response);
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
