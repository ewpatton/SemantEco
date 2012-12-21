package edu.rpi.tw.escience.semanteco;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.catalina.websocket.WsOutbound;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.rpi.tw.escience.semanteco.impl.ModuleManagerFactory;
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
public class ClientRequest extends LoggerWrapper implements Request {
	
	WsOutbound clientLog;
	Logger log;
	Map<String, String> params;
	OntModel model = null;
	Model dataModel = null;
	boolean combined = false;
	
	protected final String arrayToString(String[] arr) {
		String res = "[";
		for(int i=0;i<arr.length;i++) {
			if(i>0) {
				res += ",";
			}
			res += "\""+arr[i]+"\"";
		}
		res += "]";
		return res;
	}
	
	/**
	 * Creates a new ClientRequest object with the given parameters and a WebSocket channel
	 * @param name Class name used for the Logger
	 * @param params Request parameters extracted from the query string
	 * @param channel optional web socket channel where debugging information is sent
	 */
	public ClientRequest(String name, Map<String, String[]> params, WsOutbound channel) {
		super(name);
		this.params = new HashMap<String, String>();
		if(params != null) {
			for(Map.Entry<String, String[]> i : params.entrySet()) {
				String key = i.getKey();
				String[] value = i.getValue();
				if(key.contains(".")) {
					try {
						String newPart = key.substring(key.indexOf('.')+1);
						key = key.substring(0, key.indexOf('.'));
						JSONObject obj = null;
						if(null != this.params.get(key)) {
							obj = new JSONObject(this.params.get(key));
						}
						else {
							obj = new JSONObject();
						}
						obj.put(newPart, value[0]);
						this.params.put(key, obj.toString());
					}
					catch (JSONException e) {
					}
				}
				else {
					if(value.length>1) {
						this.params.put(i.getKey(), arrayToString(value));
					}
					else if(!value[0].equals("null")) {
						this.params.put(i.getKey(), value[0]);
					}
				}
			}
		}
		this.clientLog = channel;
		this.log = Logger.getLogger(name);
		setLogger(log);
	}

	@Override
	public Object getParam(String key) {
		String value = params.get(key);
		Object result = null;
		if(value != null) {
			if(value.startsWith("{")) {
				try {
					result = new JSONObject(value);
				} catch (JSONException e) {
					log.warn("Unable to parse JSON object", e);
				}
			}
			else if(value.startsWith("[")) {
				try {
					result = new JSONArray(value);
				} catch (JSONException e) {
					log.warn("Unable to parse JSON array", e);
				}
			}
			else if(value.equals("true")) {
				result = Boolean.TRUE;
			}
			else if(value.equals("false")) {
				result = Boolean.FALSE;
			}
			else {
				result = value;
			}
		}
		return result;
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
			log.warn("Error communicating with client; assuming closed socket", e);
			clientLog = null;
		}
	}
	
	protected void logToClient(Priority priority, Object message, Throwable t) {
		if(clientLog == null) {
			return;
		}
		String msg = message.toString();
		String err = (t != null ? t.toString() : "");
		if(t != null) {
			if(t.getCause() != null) {
				err += " due to "+t.getCause().toString();
			}
		}
		msg = msg.replaceAll("\n", Matcher.quoteReplacement("\\n"))
				.replaceAll("\"", Matcher.quoteReplacement("\\\""));
		err = err.replaceAll("\n", Matcher.quoteReplacement("\\n"))
				.replaceAll("\"", Matcher.quoteReplacement("\\\""));
		if(SemantEcoConfiguration.get().isDebug()) {
			if(priority.isGreaterOrEqual(Level.DEBUG)) {
				String response = "{\"level\":\""+priority+"\"," +
						"\"message\":\""+msg+"\"";
				if(t != null) {
					response += ",\"error\":\""+err+"\"";
				}
				response += "}";
				sendToClient(response);
			}
		}
		else {
			if(priority.isGreaterOrEqual(Level.INFO)) {
				String response = "{\"level\":\""+priority+"\"," +
						"\"message\":\""+msg+"\"";
				if(t != null) {
					response += ",\"error\":\""+err+"\"";
				}
				response += "}";
				sendToClient(response);
			}
		}
	}
	
	@Override
	protected void forcedLog(String fqcn, Priority priority, Object message, Throwable t) {
		log.callAppenders(new LoggingEvent(LoggerWrapper.class.getName(), log, priority, message, t));
		logToClient(priority, message, t);
	}
	
	@Override
	public void log(Priority priority, Object message) {
		if(priority.isGreaterOrEqual(log.getEffectiveLevel())) {
			forcedLog(LoggerWrapper.class.getName(), priority, message, null);
		}
	}
	
	@Override
	public void log(Priority priority, Object message, Throwable t) {
		if(priority.isGreaterOrEqual(log.getEffectiveLevel())) {
			forcedLog(LoggerWrapper.class.getName(), priority, message, t);
		}
	}
	
	@Override
	public void log(String callerFQCN, Priority priority, Object message, Throwable t) {
		if(priority.isGreaterOrEqual(log.getEffectiveLevel())) {
			forcedLog(LoggerWrapper.class.getName(), priority, message, t);
		}
	}

	@Override
	public OntModel getModel() {
		if(model == null) {
			final ModuleManager mgr = ModuleManagerFactory.getInstance().getManager();
			model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
			mgr.buildOntologyModel(model, this);
		}
		return model;
	}

	@Override
	public Model getDataModel() {
		if(dataModel == null) {
			final ModuleManager mgr = ModuleManagerFactory.getInstance().getManager();
			dataModel = ModelFactory.createDefaultModel();
			mgr.buildDataModel(dataModel, this);
		}
		return dataModel;
	}

	@Override
	public Model getCombinedModel() {
		if(combined == false) {
			final ModuleManager mgr = ModuleManagerFactory.getInstance().getManager();
			getModel();
			mgr.buildDataModel(model, this);
			combined = true;
		}
		return model;
	}

}
