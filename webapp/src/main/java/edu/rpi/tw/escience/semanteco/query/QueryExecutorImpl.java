package edu.rpi.tw.escience.semanteco.query;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.impl.ModuleManagerFactory;
import edu.rpi.tw.escience.semanteco.util.SemantEcoConfiguration;
import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.ModuleManager;
import edu.rpi.tw.escience.semanteco.QueryExecutor;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.query.Query;

import static edu.rpi.tw.escience.semanteco.util.DomainQueryUtils.executeAsk;
import static edu.rpi.tw.escience.semanteco.util.DomainQueryUtils.executeDescribe;
import static edu.rpi.tw.escience.semanteco.util.DomainQueryUtils.executeConstruct;
import static edu.rpi.tw.escience.semanteco.util.DomainQueryUtils.executeSelect;

/**
 * QueryExecutorImpl provides the default implementation used by
 * modules to execute queries on external SPARQL data sources.
 * 
 * NB: This class holds a weak reference to the module it is meant to
 * track.
 * 
 * @author ewpatton
 *
 */
public class QueryExecutorImpl implements QueryExecutor, Cloneable {
	
	protected String endpoint = null;
	protected WeakReference<Module> owner = null;
	protected Logger log = Logger.getLogger(QueryExecutorImpl.class);
	protected List<String> acceptTypes = new LinkedList<String>();
	private static final int BUFSIZE = 1024;
	protected Request request = null;
	
	/**
	 * Creates a new QueryExecutorImpl for the specified module that
	 * will execute any queries against the specified triple store.
	 * @param owner
	 * @param tripleStore
	 */
	public QueryExecutorImpl(Module owner, String tripleStore) {
		if(owner != null) {
			this.owner = new WeakReference<Module>(owner);
		} else {
			throw new IllegalArgumentException("Attempting to create QueryExecutorImpl without an owner.");
		}
		endpoint = tripleStore;
	}
	
	/**
	 * This protected method is used to support the {@link Object#clone()}
	 * method.
	 * @param other The QueryExecutorImpl to clone.
	 */
	protected QueryExecutorImpl(final QueryExecutorImpl other) {
		this.endpoint = other.endpoint;
		this.owner = other.owner;
		this.log = other.log;
		this.acceptTypes = new LinkedList<String>(other.acceptTypes);
		this.request = other.request;
	}

	@Override
	public String execute(Query query) {
		return execute(endpoint, query);
	}

	@Override
	public QueryExecutor execute(Query query, Model model) {
		return execute(endpoint, query, model);
	}
	
	protected String buildQueryString(String endpoint, Query query) {
		String queryString = endpoint;
		if(queryString.contains("?")) {
			queryString += "&";
		}
		else {
			queryString += "?";
		}
		try {
			queryString += "query="+URLEncoder.encode(query.toString(), "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			log.error("Unable to construct query URI", e);
			return null;
		}
		return queryString;
	}
	
	protected String listToHeader(List<String> items) {
		if(items.size()==1) {
			return items.get(0);
		}
		String res = "";
		for(String i : items) {
			if(res.equals("")) {
				res = i;
			}
			else {
				res += ", "+i;
			}
		}
		return res;
	}

	@Override
	public String execute(String endpoint, Query query) {
		assert(owner!=null);
		final Module mod = owner.get();
		assert(mod!=null);
		final String modName = mod.getName();
		assert(modName != null);
		log.trace("execute");
		log.info("Module '"+modName+"' executing query");
		log.debug("Letting modules visit query before execution");
		long start = System.currentTimeMillis();
		ModuleManagerFactory.getInstance().getManager().augmentQuery(query, request, mod);
		log.debug("Time to augment: "+(System.currentTimeMillis()-start)+" ms");
		log.debug("Endpoint: "+endpoint);
		log.debug("Query: "+query.toString().replaceAll("\n", "\n    "));
		List<String> mimeTypes = new ArrayList<String>(acceptTypes);
		if(mimeTypes.size()==0) {
			mimeTypes.add("application/rdf+xml");
		}
		String acceptedStr = listToHeader(mimeTypes);
		String queryStr = buildQueryString(endpoint, query);
		if(queryStr == null) {
			return null;
		}
		java.net.URI queryUrl = java.net.URI.create(queryStr);
		try {
			start = System.currentTimeMillis();
			HttpURLConnection conn = (HttpURLConnection)queryUrl.toURL().openConnection();
			conn.setRequestProperty("Accept", acceptedStr);
			conn.connect();
			InputStream is = conn.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			final byte[] buffer = new byte[BUFSIZE];
			int read = 0;
			while((read = is.read(buffer))>0) {
				baos.write(buffer, 0, read);
			}
			is.close();
			conn.disconnect();
			log.info("Query execution took "+(System.currentTimeMillis()-start)+" ms");
			return baos.toString();
		} catch (MalformedURLException e) {
			log.error("Invalid query URL", e);
		} catch (IOException e) {
			log.error("Error attempting to reach remote server", e);
		}
		return null;
	}

	@Override
	public QueryExecutor execute(String endpoint, Query query, Model model) {
		assert(owner!=null);
		final Module mod = owner.get();
		assert(mod!=null);
		final String modName = mod.getName();
		assert(modName != null);
		log.trace("execute");
		log.debug("Module '"+modName+"' executing remote query");
		long start = System.currentTimeMillis();
		ModuleManagerFactory.getInstance().getManager().augmentQuery(query, request, mod);
		log.debug("Augmenting query took "+(System.currentTimeMillis()-start)+" ms");
		log.debug("Endpoint: "+endpoint);
		log.debug("Query: "+query);
		String queryUri = endpoint;
		if(endpoint.contains("?")) {
			queryUri += "&query=";
		}
		else {
			queryUri += "?query=";
		}
		try {
			queryUri += URLEncoder.encode(query.toString(), "UTF-8");
			URL url = java.net.URI.create(queryUri).toURL();
			URLConnection conn = url.openConnection();
			List<String> mimeTypes = new ArrayList<String>(acceptTypes);
			if(mimeTypes.size()==0) {
				mimeTypes.add("application/rdf+xml");
			}
			String acceptedStr = listToHeader(mimeTypes);
			conn.addRequestProperty("Accept", acceptedStr);
			start = System.currentTimeMillis();
			conn.connect();
			String responseType = conn.getContentType();
			String type = "RDF/XML";
			if(responseType.startsWith("text/turtle")) {
				type = "TTL";
			}
			else if(responseType.startsWith("text/n3")) {
				type = "N3";
			}
			InputStream is = conn.getInputStream();
			model.read(is, endpoint, type);
		} catch (UnsupportedEncodingException e) {
			log.warn("Unable to construct query URI", e);
		} catch (MalformedURLException e) {
			log.warn("Invalid URI generated by query", e);
		} catch (IOException e) {
			log.warn("Unable to communicate with server", e);
		}
		log.debug("Query completed in "+(System.currentTimeMillis()-start)+" ms");
		return this;
	}
	
	/**
	 * Generates the default executor for a particular module
	 * @param module
	 * @return
	 */
	public static QueryExecutorImpl getExecutorForModule(Module module) {
		return new QueryExecutorImpl(module, SemantEcoConfiguration.get().getTripleStore());
	}

	@Override
	public String getDefaultSparqlEndpoint() {
		return SemantEcoConfiguration.get().getTripleStore();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		QueryExecutorImpl copy = new QueryExecutorImpl(owner.get(), endpoint);
		copy.acceptTypes = new ArrayList<String>(acceptTypes);
		copy.log = this.log;
		return copy;
	}

	@Override
	public QueryExecutor accept(String mimeType) {
		try {
			QueryExecutorImpl clone = (QueryExecutorImpl)clone();
			clone.endpoint = endpoint;
			clone.owner = owner;
			clone.request = request;
			clone.acceptTypes.add(mimeType);
			return clone;
		}
		catch(CloneNotSupportedException e) {
			return this;
		}
	}

	protected boolean shouldSaveModel() {
		return System.getProperty("edu.rpi.tw.escience.writemodel", "false")
				.equals("true");
	}

	protected String doLocalQuery(final Query query, final List<Model> models) {
		if(shouldSaveModel()) {
			String tmpDir = System.getProperty("java.io.tmpdir");
			int i=0;
			for(Model m : models) {
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(tmpDir+"/model"+i+".rdf");
					m.write(fos);
				} catch(Exception e) {
					// do nothing
				} finally {
					try {
						fos.close();
					} catch(IOException e) { }
				}
				i++;
			}
		}
		assert(owner!=null);
		final Module mod = owner.get();
		assert(mod!=null);
		final String modName = mod.getName();
		assert(modName != null);
		log.trace("executeLocalQuery");
		
		log.debug("Module '"+modName+"' executing local query");
		ModuleManager mgr = ModuleManagerFactory.getInstance().getManager();
		mgr.augmentQuery(query, request, mod);
		log.debug("Query: "+query.toString());
		Model resultModel = null;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		long start = System.currentTimeMillis();
		//QueryExecution qe = QueryExecutionFactory.create(query.toString(), model);
		boolean parallel = SemantEcoConfiguration.get().isParallel();
		try {
			switch(query.getType()) {
			case SELECT:
				ResultSet results = executeSelect(query, models, parallel);
				ResultSetFormatter.outputAsJSON(baos, results);
				log.debug("Local query took "+(System.currentTimeMillis()-start)+" ms");
				return baos.toString("UTF-8");
			case DESCRIBE:
				resultModel = executeDescribe(query, models, parallel);
				resultModel.write(baos);
				log.debug("Local query took "+(System.currentTimeMillis()-start)+" ms");
				return baos.toString("UTF-8");
			case CONSTRUCT:
				resultModel = executeConstruct(query, models, parallel);
				resultModel.write(baos);
				log.debug("Local query took "+(System.currentTimeMillis()-start)+" ms");
				return baos.toString("UTF-8");
			case ASK:
				if(executeAsk(query, models, parallel)) {
					log.debug("Local query took "+(System.currentTimeMillis()-start)+" ms");
					return "{\"result\":true}";
				}
				else {
				  log.debug("Local query took "+(System.currentTimeMillis()-start)+" ms");
					return "{\"result\":false}";
				}
			}
		}
		catch(Exception e) {
			log.warn("Unable to execute query due to exception", e);
		}
		return null;
	}

	/**
	 * Executes a local query on a model other than the default.
	 * @param query Query to execute on the model
	 * @param model Jena Model containing content to query
	 */
	public String executeLocalQuery(Query query, Model model) {
		return doLocalQuery(query, Arrays.asList(model));
	}

	@Override
	public String executeLocalQuery(Query query) {
		final List<Model> models = new ArrayList<Model>();
		final List<Domain> activeDomains = request.listActiveDomains();
		for(Domain i : activeDomains) {
			models.add(request.getCombinedModel(i));
		}
		return doLocalQuery(query, models);
	}
	
	/**
	 * Sets the request that will be used for augmenting
	 * queries during the evaluation of this QueryExecutorImpl.
	 * @param request A valid client-side request being processed.
	 */
	public void setRequest(Request request) {
		this.request = request;
		this.log = request.getLogger();
	}

}
