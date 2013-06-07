package edu.rpi.tw.escience.semanteco.geo;

import static edu.rpi.tw.escience.semanteco.query.Query.VAR_NS;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;


import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.QueryMethod;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.Resource;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.query.NamedGraphComponent;
import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.QueryResource;
import edu.rpi.tw.escience.semanteco.query.Variable;
import edu.rpi.tw.escience.semanteco.query.Query.Type;
import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.ProvidesDomain;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SemantEcoGeoModule implements Module {
	// Define that name spaces!
	private static final String WGS_NS = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	private static final String POLLUTION_NS = "http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#";
	private static final String DEFAULT_NS = "http://purl.org/twc/semantgeo/source/aeap_nys/dataset/dfw_lake_samples/aeap-nyserda-chem-94-12-v9-web/version/2013-April-24/";
	
	// Define some constants
	private static final String FAILURE = "{\"success\":false}";
	private static final Logger log = Logger.getLogger(SemantEcoGeoModule.class);
	
	private ModuleConfiguration config = null;
	
	

	@Override
	public void visit(final Query query, final Request request) {
		// TODO modify queries
	}
	
	@Override
	public void visit(final SemantEcoUI ui, final Request request) {
		// TODO add resources to display
	}

	@Override
	public String getName() {
		return "SemantEcoGeo";
	}

	@Override
	public int getMajorVersion() {
		return 1;
	}

	@Override
	public int getMinorVersion() {
		return 0;
	}

	@Override
	public String getExtraVersion() {
		return null;
	}

	@Override
	public void setModuleConfiguration(final ModuleConfiguration config) {
		this.config = config;
	}
	
	@QueryMethod
	public String testMethodAwesome(final Request request) {
		
		//Build the lake chemical query!
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);	
		final NamedGraphComponent graph = query.getNamedGraph("AEAP_NYSERDA");
		final QueryResource hasCharacteristic = query.getResource(POLLUTION_NS + "hasCharacteristic");
		final QueryResource hasValue = query.getResource(POLLUTION_NS + "hasValue");
		final Variable aWaterMeasurement = query.getVariable(VAR_NS +  "aWaterMeasurement");
		final Variable aValue = query.getVariable(VAR_NS + "aValue");
		final Variable aConductivity = query.getVariable(VAR_NS + "aConductivity");
		graph.addPattern(aWaterMeasurement, hasCharacteristic, aConductivity);
		graph.addPattern(aWaterMeasurement, hasValue, aValue);
		
		// Let's make our ajax query now
		String resultStr = config.getQueryExecutor(request).accept("application/json").execute(query);	
		
		// DEBUGGING
		log.debug("Results: " + resultStr);
		
		if( resultStr == null ) {
			// If there was a failure to query, let the system know
			String responseStr = FAILURE;
			return responseStr;
		} else {
			// response was good, return the data we got back
			return resultStr;
		}
	}
	
	@QueryMethod
	public String testMethod(final Request request) {
		
		//Build the lat long query!
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);	
		final NamedGraphComponent graph = query.getNamedGraph("AEAP_NYSERDA_Locations");
		final QueryResource lat = query.getResource(WGS_NS + "lat");
		final QueryResource _long = query.getResource(WGS_NS + "long");
		final Variable aLake = query.getVariable(VAR_NS +  "lake");
		final Variable aLat = query.getVariable(VAR_NS + "lat");
		final Variable aLong = query.getVariable(VAR_NS + "long");
		graph.addPattern(aLake, lat, aLat);
		graph.addPattern(aLake, _long, aLong);
		
		// Let's make our ajax query now
		String responseStr = FAILURE;
		String resultStr = config.getQueryExecutor(request).accept("application/json").execute(query);	
		
		// DEBUGGING
		log.debug("Results: " + resultStr);
		
		// If there was a failure to query
		if(resultStr == null) {
			return responseStr;
		}

			System.out.println(resultStr);
		 
		return resultStr;	
	}
	
	public List<Domain> getDomains(final Request request) {
		List<Domain> domains = new ArrayList<Domain>();
	    Domain semantGeoDomain = config.getDomain(URI.create("http://purl.org/twc/SemantGeo/"), true);
	    // add data sources, regulations, and data types here
	    domains.add(semantGeoDomain);
	    semantGeoDomain.setLabel("SemantGeo");
		addDataSources(semantGeoDomain, request);
		addRegulations(semantGeoDomain);
		addDataTypes(semantGeoDomain);
	    return domains;
	 }
	
	protected void addDataSources(final Domain domain, final Request request) {
		// TODO query for data sources and add them here (cf {@link WaterDataProviderModule#addDataSources(Domain, Request)})
		domain.addSource(URI.create("http://sparql.tw.rpi.edu/source/darrin-fresh-water"), "Darrin Fresh Water");
	}
	
	protected void addRegulations(final Domain domain) {
		// TODO query for regulations and add them here
		//domain.addRegulation(URI.create("http://was.tw.rpi.edu/semanteco/regulations/EPA-air-regulation.owl"), "Darrin Fresh Water");
	}
	
	protected void addDataTypes(final Domain domain) {
		// change to icon names here should also occur in air-data-provider.js
		Resource res = config.getResource("clean-air.png");
		domain.addDataType("clean-air", "DFW - Temp - 1", res);
		res = config.getResource("polluted-air.png");
		domain.addDataType("polluted-air", "DFW - Temp - 2", res);
	}

	@Override
	public void visit(Model model, Request request, Domain domain) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(OntModel model, Request request, Domain domain) {
		// TODO Auto-generated method stub
		
	}
}
