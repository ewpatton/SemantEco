package edu.rpi.tw.escience.semanteco.request;

import java.io.IOException;
import java.net.URL;
import java.nio.CharBuffer;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.websocket.WsOutbound;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.json.JSONException;
import org.json.JSONObject;

import edu.rpi.tw.escience.semanteco.i18n.Messages;
import edu.rpi.tw.escience.semanteco.util.SemantEcoConfiguration;
import edu.rpi.tw.escience.semanteco.ModuleManager;
import edu.rpi.tw.escience.semanteco.Request;

/**
 * ClientRequest provides the main encapsulation
 * of a RESTful client request to SemantEco. It
 * contains information on request parameters and
 * a WebSocket stream that can be used via the Logger
 * interface to send debugging information back to
 * the client for development purposes.
 * @author ewpatton
 *
 */
public class WsClientRequest extends ClientRequest implements Request {

	private WsOutbound clientLog;
	private WsOutbound provenanceLog;

	/**
	 * Creates a new ClientRequest object with the given parameters and a
	 * WebSocket channel
	 * @param name Class name used for the Logger
	 * @param params Request parameters extracted from the query string
	 * @param original Original URL being processed by server
	 * @param channel optional web socket channel where debugging information
	 * is sent
	 * @param provenance optional web socket channel where provenance
	 * information is sent
	 */
	public WsClientRequest(String name, Map<String, String[]> params,
			URL original, ModuleManager manager, WsOutbound channel,
			WsOutbound provenance) {
		super(name, params, original, manager);
		this.clientLog = channel;
		this.provenanceLog = provenance;
	}

	public WsClientRequest(ClientRequest other, WsOutbound channel,
	        WsOutbound provenance) {
	    super(other);
	    this.clientLog = channel;
	    this.provenanceLog = provenance;
	}

	@Override
	public Logger getLogger() {
		return this;
	}

	protected void sendToClient(String str) {
		CharBuffer cb = CharBuffer.wrap(str);
		try {
			clientLog.writeTextMessage(cb);
			clientLog.flush();
		} catch (IOException e) {
      clientLog = null;
			super.warn("Error communicating with client; assuming closed socket", e);
		}
	}

	protected void logToClient(Priority priority, Object message, Throwable t) {
		if(clientLog == null) {
			return;
		}
		JSONObject clientMsg = new JSONObject();
		try {
			clientMsg.put("level", priority.toString());
			clientMsg.put("message", message.toString());
			if ( t != null ) {
				String err = t.toString();
				if ( t.getCause() != null ) {
					err += " due to "+t.getCause().toString();
				}
				clientMsg.put("error", err);
			}
		} catch(JSONException e) {
			super.warn("Unexpected JSONException", e);
			return;
		}
		if(SemantEcoConfiguration.get().isDebug()) {
			if(priority.isGreaterOrEqual(Level.DEBUG)) {
				sendToClient(clientMsg.toString());
			}
		}
		else {
			if(priority.isGreaterOrEqual(Level.INFO)) {
				sendToClient(clientMsg.toString());
			}
		}
	}

	@Override
	protected void forcedLog(String fqcn, Priority priority, Object message, Throwable t) {
	  super.forcedLog(fqcn, priority, message, t);
		logToClient(priority, message, t);
	}

	@Override
	public boolean canLogProvenance() {
		return provenanceLog != null;
	}

	@Override
	public void logProvenance(String graph, String contents) {
		if(!canLogProvenance()) {
			return;
		}
		try {
			String trigBlock = "<"+graph+"> {\n"+contents+"\n}\n";
			CharBuffer cb = CharBuffer.wrap(trigBlock);
			provenanceLog.writeTextMessage(cb);
			provenanceLog.flush();
		} catch(IOException e) {
			this.warn(Messages.PROVENANCE_CONNECTION_LOST, e);
			try {
				provenanceLog.close(HttpServletResponse.SC_OK, null);
			} catch(IOException e1) {
				this.warn(Messages.PROVENANCE_CONNECTION_NOCLOSE);
			} finally {
				provenanceLog = null;
			}
		}
	}
}
