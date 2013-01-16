package edu.rpi.tw.escience.waterquality.dataprovider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.query.NamedGraphComponent;
import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.QueryResource;
import edu.rpi.tw.escience.semanteco.query.Variable;

import static edu.rpi.tw.escience.semanteco.query.Query.VAR_NS;

/**
 * Counts instances of facilities and water sites available in the different
 * data sources.
 * @author ewpatton
 *
 */
public class InstanceCounter extends QueryUtils {

	private final Logger log;
	private final ModuleConfiguration config;
	private final List<String> sources = new ArrayList<String>();
	private final String stateUri;
	private final String countyCode;
	private final Request request;
	
	/**
	 * Creates an instance counter for the given request and module configuration.
	 * @param request Client request describing RESTful call
	 * @param config Module configuration for the water data module
	 */
	public InstanceCounter(final Request request, final ModuleConfiguration config) {
		super(request, config);
		this.request = request;
		this.log = request.getLogger();
		this.config = config;
		JSONArray sources = (JSONArray)request.getParam("source");
		if(sources == null || sources.length() == 0) {
			throw new IllegalArgumentException("The source parameter must be supplied.");
		}
		try {
			for(int i=0;i<sources.length();i++) {
				this.sources.add(sources.getString(i));
			}
		}
		catch(JSONException e) {
			log.error("Unable to retrieve sources", e);
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
	}
	
	/**
	 * Builds a results object that can be serialized back to the client.
	 * @return A JSONObject containing the count information in the form
	 * of: {"sites": #, "facilities": #}
	 */
	public final JSONObject build() {
		JSONObject response = new JSONObject();
		for(String source : sources) {
			final List<String> graphs = retrieveStateGraphsForSource(stateUri, source);
			final Query query = config.getQueryFactory().newQuery();
			if(source.contains("usgs-gov")) {
				buildUSGSCounter(query, graphs);
				String resultStr = config.getQueryExecutor(request).accept("application/json").execute(query);
				int number = process(resultStr);
				try {
					response.put("site", number);
				}
				catch(JSONException e) {
					log.warn("Unable to retrieve site counts", e);
				}
			}
			else if(source.contains("epa-gov")) {
				buildEPACounter(query, graphs);
				String resultStr = config.getQueryExecutor(request).accept("application/json").execute(query);
				int number = process(resultStr);
				try {
					response.put("facility", number);
				}
				catch(JSONException e) {
					log.warn("Unable to retrieve facility count", e);
				}
			}
		}
		return response;
	}
	
	protected final void buildEPACounter(final Query query, final List<String> graphs) {
		log.trace("buildEPACounter");
		if(graphs == null || graphs.size() == 0) {
			log.warn("Unable to find any EPA graphs for "+stateUri);
			return;
		}
		String siteGraph, measuresGraph;
		if(graphs.get(0).contains("measurement")) {
			measuresGraph = graphs.get(0);
			siteGraph = graphs.get(1);
		}
		else {
			siteGraph = graphs.get(0);
			measuresGraph = graphs.get(1);
		}
		
		final Variable s = query.getVariable(VAR_NS+"s");
		final Variable permit = query.getVariable(VAR_NS+"permit");
		final Variable measurement = query.getVariable(VAR_NS+"measurement");
		final Variable lat = query.getVariable(VAR_NS+"lat");
		final Variable lng = query.getVariable(VAR_NS+"long");
		final Variable counter = query.createVariableExpression("count(distinct ?s) as ?cnt");
		final Set<Variable> vars = new HashSet<Variable>();
		vars.add(counter);
		query.setVariables(vars);
		
		final QueryResource rdfType = query.getResource(RDF_NS+"type");
		final QueryResource waterWaterFacility = query.getResource(WATER_NS+"WaterFacility");
		final QueryResource polHasCountyCode = query.getResource(POL_NS+"hasCountyCode");
		final QueryResource polHasPermit = query.getResource(POL_NS+"hasPermit");
		final QueryResource wgsLat = query.getResource(WGS_NS+"lat");
		final QueryResource wgsLong = query.getResource(WGS_NS+"long");
		
		final NamedGraphComponent sites = query.getNamedGraph(siteGraph);
		final NamedGraphComponent measures = query.getNamedGraph(measuresGraph);
		
		sites.addPattern(s, rdfType, waterWaterFacility);
		sites.addPattern(s, polHasCountyCode, request.getParam("state")+countyCode, null);
		sites.addPattern(s, polHasPermit, permit);
		sites.addPattern(s, wgsLat, lat);
		sites.addPattern(s, wgsLong, lng);
		
		measures.addPattern(measurement, polHasPermit, permit);
	}
	
	protected final void buildUSGSCounter(final Query query, final List<String> graphs) {
		log.trace("buildUSGSCounter");
		if(graphs == null || graphs.size() == 0) {
			log.warn("Unable to find any USGS graphs for "+stateUri);
			return;
		}
		String siteGraph, measuresGraph;
		if(graphs.get(0).contains("measurement")) {
			measuresGraph = graphs.get(0);
			siteGraph = graphs.get(1);
		}
		else {
			siteGraph = graphs.get(0);
			measuresGraph = graphs.get(1);
		}
		
		final Variable s = query.getVariable(VAR_NS+"s");
		final Variable measurement = query.getVariable(VAR_NS+"measurement");
		final Variable counter = query.createVariableExpression("count(distinct ?s) as ?cnt");
		final Set<Variable> vars = new HashSet<Variable>();
		vars.add(counter);
		query.setVariables(vars);
		
		final QueryResource rdfType = query.getResource(RDF_NS+"type");
		final QueryResource waterWaterSite = query.getResource(WATER_NS+"WaterSite");
		final QueryResource polHasCountyCode = query.getResource(POL_NS+"hasCountyCode");
		final QueryResource polHasSite = query.getResource(POL_NS+"hasSite");
		
		final NamedGraphComponent sites = query.getNamedGraph(siteGraph);
		final NamedGraphComponent measures = query.getNamedGraph(measuresGraph);
		
		sites.addPattern(s, rdfType, waterWaterSite);
		sites.addPattern(s, polHasCountyCode, countyCode, XSDDatatype.XSDint);
		
		measures.addPattern(measurement, polHasSite, s);
	}
	
	/**
	 * Extracts the integer value from the SPARQL results for the queries
	 * in {@link #buildEPACounter(Query, List)} and 
	 * {@link #buildUSGSCounter(Query, List)}
	 * @param sparqlResults
	 * @return
	 */
	protected final int process(final String sparqlResults) {
		int count = 0;
		try {
			JSONObject results = new JSONObject(sparqlResults);
			count = results.getJSONObject("results").getJSONArray("bindings")
					.getJSONObject(0).getJSONObject("cnt").getInt("value");
		}
		catch(JSONException e) {
			log.warn("Unable to parse sparql results", e);
		}
		return count;
	}
	
}
