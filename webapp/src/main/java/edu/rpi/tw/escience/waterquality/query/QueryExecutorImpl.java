package edu.rpi.tw.escience.waterquality.query;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.Module;
import edu.rpi.tw.escience.waterquality.QueryExecutor;
import edu.rpi.tw.escience.waterquality.util.SemantAquaConfiguration;

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
	
	private String endpoint = null;
	private WeakReference<Module> owner = null;
	private Logger log = Logger.getLogger(QueryExecutorImpl.class);
	private List<String> acceptTypes = new LinkedList<String>();
	private static final int BUFSIZE = 1024;
	
	/**
	 * Creates a new QueryExecutorImpl for the specified module that
	 * will execute any queries against the specified triple store.
	 * @param owner
	 * @param tripleStore
	 */
	public QueryExecutorImpl(Module owner, String tripleStore) {
		if(owner != null) {
			this.owner = new WeakReference<Module>(owner);
		}
		endpoint = tripleStore;
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
		log.trace("execute");
		log.info("Module '"+owner.get().getName()+"' executing query");
		log.debug(query.toString().replaceAll("\n", "\n    "));
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
			long start = System.currentTimeMillis();
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
		log.trace("execute");
		log.debug("Module '"+owner.get().getName()+"' executing query: "+query.toString());
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Generates the default executor for a particular module
	 * @param module
	 * @return
	 */
	public static QueryExecutor getExecutorForModule(Module module) {
		return new QueryExecutorImpl(module, SemantAquaConfiguration.get().getTripleStore());
	}

	@Override
	public String getDefaultSparqlEndpoint() {
		return SemantAquaConfiguration.get().getTripleStore();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		QueryExecutorImpl copy = new QueryExecutorImpl(owner.get(), endpoint);
		copy.acceptTypes = new ArrayList<String>(acceptTypes);
		return copy;
	}

	@Override
	public QueryExecutor accept(String mimeType) {
		try {
			QueryExecutorImpl clone = (QueryExecutorImpl)clone();
			clone.acceptTypes.add(mimeType);
			return clone;
		}
		catch(CloneNotSupportedException e) {
			return this;
		}
	}

}
