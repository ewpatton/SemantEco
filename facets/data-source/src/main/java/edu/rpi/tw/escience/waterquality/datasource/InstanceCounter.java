package edu.rpi.tw.escience.waterquality.datasource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

import edu.rpi.tw.escience.waterquality.ModuleConfiguration;
import edu.rpi.tw.escience.waterquality.Request;
import edu.rpi.tw.escience.waterquality.query.NamedGraphComponent;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.QueryResource;
import edu.rpi.tw.escience.waterquality.query.Variable;

import static edu.rpi.tw.escience.waterquality.query.Query.VAR_NS;

public class InstanceCounter extends QueryUtils {

	private final Logger log;
	private final ModuleConfiguration config;
	private final List<String> sources = new ArrayList<String>();
	private final String stateUri;
	private final String countyCode;
	private final Request request;
	
	public InstanceCounter(final Request request, final ModuleConfiguration config) {
		super(request, config);
		this.request = request;
		this.log = request.getLogger();
		this.config = config;
		String[] sources = request.getParam("source[]");
		if(sources == null || sources.length == 0) {
			throw new IllegalArgumentException("The source parameter must be supplied.");
		}
		for(int i=0;i<sources.length;i++) {
			this.sources.add(sources[i]);
		}
		String[] state = request.getParam("state");
		if(state == null || state.length == 0) {
			throw new IllegalArgumentException("State parameter not supplied. Expected two digit state abbreviation, e.g. CA.");
		}
		this.stateUri = getStateURI(state[0]);
		try {
			this.countyCode = request.getParam("county")[0];
		}
		catch(Exception e) {
			throw new IllegalArgumentException("County parameter not supplied.", e);
		}
	}
	
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
		log.trace("buildUSGSCounter");
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
		sites.addPattern(s, polHasCountyCode, countyCode, XSDDatatype.XSDint);
		sites.addPattern(s, polHasPermit, permit);
		sites.addPattern(s, wgsLat, lat);
		sites.addPattern(s, wgsLong, lng);
		
		measures.addPattern(measurement, polHasPermit, s);
	}
	
	protected final void buildUSGSCounter(final Query query, final List<String> graphs) {
		log.trace("buildUSGSCounter");
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
