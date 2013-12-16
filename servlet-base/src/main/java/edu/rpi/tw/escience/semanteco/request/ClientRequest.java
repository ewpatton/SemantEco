package edu.rpi.tw.escience.semanteco.request;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import edu.rpi.tw.escience.semanteco.util.JSONUtils;
import edu.rpi.tw.escience.semanteco.wrapper.LoggerWrapper;
import edu.rpi.tw.escience.semanteco.Domain;
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

	private Logger log;
	private Map<String, String> params;
	private Map<Domain, ModelCache> models = new HashMap<Domain, ModelCache>();
	private URL original = null;
	private List<Domain> activeDomains = new ArrayList<Domain>();
	private ModuleManager manager = null;

	private static final class ModelCache {
		private OntModel model = null;
		private Model dataModel = null;
		private boolean combined = false;
	}

	protected final String arrayToString(String[] arr) {
		StringBuilder res = new StringBuilder("[");
		for(int i=0;i<arr.length;i++) {
			if(i>0) {
				res.append(",");
			}
			res.append("\""+arr[i]+"\"");
		}
		res.append("]");
		return res.toString();
	}

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
	public ClientRequest(String name, Map<String, String[]> params,
			URL original, ModuleManager manager) {
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
		this.log = Logger.getLogger(name);
		this.original = original;
		this.manager = manager;
		setLogger(log);
		List<String> domainUris = JSONUtils.toList((JSONArray) getParam("domain"));
		List<Domain> allDomains = this.manager.listDomains();
		for(Domain d : allDomains) {
			if(domainUris.contains(d.getUri().toString())) {
				activeDomains.add(d);
			}
		}
	}

	protected ClientRequest(ClientRequest other) {
		super(other.name);
		log = other.log;
		params = other.params;
		models = other.models;
		original = other.original;
		activeDomains = other.activeDomains;
		manager = other.manager;
	}

	@Override
	public final Object getParam(String key) {
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

	@Override
	protected void forcedLog(String fqcn, Priority priority, Object message, Throwable t) {
		log.callAppenders(new LoggingEvent(LoggerWrapper.class.getName(), log, priority, message, t));
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

	private void ensureModelCacheForDomain(Domain domain) {
		if(!models.containsKey(domain)) {
			models.put(domain, new ModelCache());
		}
	}

	@Override
	public OntModel getModel(Domain domain) {
		ensureModelCacheForDomain(domain);
		if(models.get(domain).model == null) {
			models.get(domain).model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
			manager.buildOntologyModel(models.get(domain).model, this, domain);
		}
		return models.get(domain).model;
	}

	@Override
	public Model getDataModel(Domain domain) {
		ensureModelCacheForDomain(domain);
		if(models.get(domain).dataModel == null) {
			models.get(domain).dataModel = ModelFactory.createDefaultModel();
			manager.buildDataModel(models.get(domain).dataModel, this, domain);
		}
		return models.get(domain).dataModel;
	}

	@Override
	public Model getCombinedModel(Domain domain) {
		ensureModelCacheForDomain(domain);
		if(!models.get(domain).combined) {
			getModel(domain);
			manager.buildDataModel(models.get(domain).model, this, domain);
			models.get(domain).combined = true;
		}
		return models.get(domain).model;
	}

	@Override
	public URL getOriginalURL() {
		return original;
	}

	@Override
	public boolean canLogProvenance() {
		return false;
	}

	@Override
	public void logProvenance(String graph, String contents) {

	}

	@Override
	public List<Domain> listActiveDomains() {
		return activeDomains;
	}
}
