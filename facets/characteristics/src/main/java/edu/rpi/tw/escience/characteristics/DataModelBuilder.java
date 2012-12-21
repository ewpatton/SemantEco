package edu.rpi.tw.escience.characteristics;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.query.GraphComponentCollection;

/**
 * DataModelBuilder is a utility class used by the DataSourceModule to build an appropriate
 * data model for a given request. It is responsible for making all of the necessary external 
 * SPARQL requests to where the data are stored.
 * 
 * @author ewpatton
 *
 */
public class DataModelBuilder {//extends QueryUtils {
	
	private static final String SITE = "s";
	private static final String LAT = "lat";
	private static final String LONG = "long";
	
	private final Logger log = null;
	private final String stateUri = null;
	private final List<String> sources = new ArrayList<String>();
	private ModuleConfiguration config = null;
	private final String countyCode = null;
	private final Request request = null;
	
	/**
	 * Constructs a DataModelBuilder for the specified request
	 * @param request Request object encapsulating the client's request
	 * @param config Data source module's configuration
	 */
	public DataModelBuilder(final Request request, final ModuleConfiguration config) {
		this.config = config;
		//super(request, config);
		/*
		this.log = request.getLogger();
		
		this.request = request;
		JSONArray sources = (JSONArray)request.getParam("source");
		if(sources == null || sources.length() == 0) {
			throw new IllegalArgumentException("The source parameter must be supplied");
		}
		try {
			for(int i=0;i<sources.length();i++) {
				this.sources.add(sources.getString(i));
			}
		} catch (JSONException e) {
			throw new IllegalArgumentException("Unable to parse input 'source'", e);
		}
		String state = (String)request.getParam("state");
		if(state == null || state.isEmpty()) {
			throw new IllegalArgumentException("State parameter not supplied. Expected two digit state abbreviation, e.g. CA.");
		}
		this.stateUri = getStateURI(state);
		try {
			this.countyCode = (String)request.getParam("county");
		}
		catch(Exception e) {
			throw new IllegalArgumentException("County parameter not supplied.", e);
		}
		*/
	}
	
	/**
	 * Builds the model from the current state of the DataModelBuilder
	 * @param model A Jena model to populate with triples
	 * @return
	 */
	public boolean build(final Model model) {
		
		return false;
	}


	

	/**
	 * Adds the site filter to the specified graph component collection using the list of
	 * sites specified.
	 * @param query A graph, usually the one containing sites/facilities, to add a FILTER to
	 * @param sites List of sites to include in the filter
	 */
	protected final void addSiteFilter(final GraphComponentCollection query, final List<String> sites) {
		String filter = "?"+SITE+" IN (<";
		boolean first = true;
		for(String i : sites) {
			if(!first) {
				filter += ">,<";
			}
			else {
				first = false;
			}
			filter += i;
		}
		filter += ">)";
		query.addFilter(filter);
	}

}
