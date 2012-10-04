package edu.rpi.tw.escience.waterquality;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.catalina.websocket.WsOutbound;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.rpi.tw.escience.waterquality.impl.ModuleManagerFactory;
import edu.rpi.tw.escience.waterquality.util.SemantAquaConfiguration;

public class ClientRequest extends LoggerWrapper implements Request {
	
	WsOutbound clientLog;
	Logger log;
	Map<String, String[]> params;
	OntModel model = null;
	Model dataModel = null;
	boolean combined = false;
	
	public ClientRequest(String name, Map<String, String[]> params, WsOutbound channel) {
		super(name);
		this.params = params;
		this.clientLog = channel;
		this.log = Logger.getLogger(name);
		setLogger(log);
	}

	@Override
	public String[] getParam(String key) {
		return params.get(key);
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
		String err = (t != null ? t.getLocalizedMessage() : "");
		msg = msg.replaceAll("\n", Matcher.quoteReplacement("\\n"))
				.replaceAll("\"", Matcher.quoteReplacement("\\\""));
		err = err.replaceAll("\n", Matcher.quoteReplacement("\\n"))
				.replaceAll("\"", Matcher.quoteReplacement("\\\""));
		if(SemantAquaConfiguration.get().isDebug()) {
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
			forcedLog(LoggerWrapper.class.getName(), priority, message, null);
		}
	}
	
	@Override
	public void log(String callerFQCN, Priority priority, Object message, Throwable t) {
		if(priority.isGreaterOrEqual(log.getEffectiveLevel())) {
			forcedLog(LoggerWrapper.class.getName(), priority, message, null);
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
