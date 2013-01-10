package edu.rpi.tw.escience.airquality.dataprovider;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.ProvidesDomain;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.Resource;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.query.GraphComponentCollection;
import edu.rpi.tw.escience.semanteco.query.NamedGraphComponent;
import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.Query.Type;
import edu.rpi.tw.escience.semanteco.query.QueryResource;
import edu.rpi.tw.escience.semanteco.query.Variable;

import static edu.rpi.tw.escience.semanteco.query.Query.RDF_NS;
import static edu.rpi.tw.escience.semanteco.query.Query.VAR_NS;

public class AirDataProviderModule implements Module, ProvidesDomain {

	private static final String SITE_VAR = "site";
	private static final String POL_NS = "http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#";
	private static final String AIR_NS = "http://was.tw.rpi.edu/semanteco/air/air.owl#";
	public static final String QUERY_NS = "http://aquarius.tw.rpi.edu/projects/semantaqua/data-source/query-variable/";
	private static final String UNIT_NS = "http://sweet.jpl.nasa.gov/2.1/reprSciUnits.owl#";
	private static final String PROV_NS = "http://www.w3.org/ns/prov#";
	private static final String LAT = "lat";
	private static final String LONG = "long";
	private static final String WGS_NS = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	private static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
	private static final String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";




	private static final String ISAIR_VAR = "isAir";
	private ModuleConfiguration config = null;
	private static Logger log = Logger.getLogger(AirDataProviderModule.class);
	private String countyCode = null;
	private String stateAbbr = null;
	private String stateCode = null;
	private Request request = null;

	
	@Override
	public void visit(Model model, Request request) {
		this.log = request.getLogger();
		log.debug("Visiting AirDataProviderModule building data model");
		this.config = config;
		this.request = request;
		this.stateAbbr = (String)request.getParam("state");
		if(stateAbbr == null || stateAbbr.isEmpty()) {
			throw new IllegalArgumentException("State parameter not supplied. Expected two digit state abbreviation, e.g. CA.");
		}
		try {
			this.countyCode = (String)request.getParam("county");
		}
		catch(Exception e) {
			throw new IllegalArgumentException("County parameter not supplied.", e);
		}
		
		try {
			this.stateCode = (String)request.getParam("stateCode");
		}
		catch(Exception e) {
			throw new IllegalArgumentException("County parameter not supplied.", e);
		}
		
		// TODO build air model here (cf {@link WaterDataProviderModule#visit(Model, Request)})
		final Query query = config.getQueryFactory().newQuery(Type.CONSTRUCT);
		final Variable measurement = query.getVariable(VAR_NS+"measurement");
		final QueryResource polHasCounty = query.getResource(POL_NS+"hasCounty");
		final QueryResource polHasState = query.getResource(POL_NS+"hasState");
		final QueryResource polHasCharacteristic = query.getResource(POL_NS+"hasCharacteristic");
		final QueryResource polHasValue = query.getResource(POL_NS+"hasValue");
		final Variable unit = query.getVariable(QUERY_NS+"unit");
		final Variable value = query.getVariable(QUERY_NS+"value");
		final Variable site = query.getVariable(QUERY_NS+"site");
		final Variable element = query.getVariable(QUERY_NS+"element");
		final QueryResource unitHasUnit = query.getResource(UNIT_NS+"hasUnit");
		final QueryResource atLocation = query.getResource(PROV_NS+"atLocation");
		final QueryResource hasMeasurement = query.getResource(POL_NS +"hasMeasurement");

		final QueryResource type = query.getResource(RDF_NS+"type");

		final Variable lat = query.getVariable(QUERY_NS+LAT);
		final Variable lng = query.getVariable(QUERY_NS+LONG);
		final QueryResource wgsLat = query.getResource(WGS_NS+LAT);
		final QueryResource wgsLong = query.getResource(WGS_NS+LONG);
		final QueryResource airSite = query.getResource(AIR_NS+"AirSite");


		String countyCode = (String) request.getParam("county");
		String stateCode = (String) request.getParam("stateCode");
		String stateAbbr = (String) request.getParam("state");
		//test values
		countyCode = "001";
		stateCode = "08";
		
		final NamedGraphComponent graph = query.getNamedGraph("http://was.tw.rpi.edu/air-measurement-data");
		graph.addPattern(measurement, polHasCounty, countyCode,null);
		graph.addPattern(measurement, polHasState, stateCode,null);
		//new patterns to test
		graph.addPattern(measurement, polHasCharacteristic, element);
		graph.addPattern(measurement, polHasValue, value);
		graph.addPattern(measurement, unitHasUnit, unit);
		graph.addPattern(measurement, atLocation, site);

		
		final NamedGraphComponent graph2 = query.getNamedGraph("http://was.tw.rpi.edu/air-monitoring-sites");
		graph2.addPattern(site, wgsLat, lat);
		graph2.addPattern(site, wgsLong, lng);

		//new construct patterns to test
		final GraphComponentCollection construct = query.getConstructComponent();
		construct.addPattern(measurement, polHasCounty, countyCode,null);
		construct.addPattern(measurement, polHasState, stateCode,null);
		construct.addPattern(measurement, polHasCharacteristic, element);
		construct.addPattern(measurement, polHasValue, value);
		construct.addPattern(measurement, unitHasUnit, unit);
		construct.addPattern(site, hasMeasurement, measurement);

		construct.addPattern(site, type, airSite);
		construct.addPattern(site, wgsLat, lat);
		construct.addPattern(site, wgsLong, lng);


		//return true;
		config.getQueryExecutor(request).accept("text/turtle").execute(query, model);			
	}

	@Override
	public void visit(OntModel model, Request request) {
		request.getLogger().debug("AirDataProviderModule loading air.owl");
		model.read(AIR_NS, "TTL");
	}

	@Override
	public void visit(Query query, Request request) {
		request.getLogger().debug("AirDataProviderModule updating query");
		if(query.getType() != Type.SELECT) {
			return;
		}
		final Variable site = query.getVariable(VAR_NS+SITE_VAR);
		final QueryResource rdfType = query.getResource(RDF_NS+"type");
		final QueryResource polMeasurementSite = query.getResource(POL_NS+"MeasurementSite");
		List<GraphComponentCollection> graphs = query.findGraphComponentsWithPattern(site, rdfType, polMeasurementSite);
		if(graphs != null && graphs.size() > 0) {
			query.setNamespace("air", AIR_NS);
			final Variable isAir = query.createVariableExpression("EXISTS { ?"+SITE_VAR+" a air:AirSite } as ?"+ISAIR_VAR);
			Set<Variable> vars = new LinkedHashSet<Variable>(query.getVariables());
			vars.add(isAir);
			query.setVariables(vars);
		}
	}

	@Override
	public void visit(SemantEcoUI ui, Request request) {
		Resource res = config.getResource("air-data-provider.js");
		if(res != null) {
			ui.addScript(res);
		}
	}

	@Override
	public List<Domain> getDomains(Request request) {
		log.trace("getDomains");
		List<Domain> domains = new ArrayList<Domain>();
		Domain air = config.getDomain(URI.create("http://was.tw.rpi.edu/semanteco/air/air.owl#"), true);
		air.setLabel("Air");
		addDataSources(air, request);
		addRegulations(air);
		addDataTypes(air);
		domains.add(air);
		return domains;
	}

	@Override
	public String getName() {
		return "Air Data Provider";
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
	public void setModuleConfiguration(ModuleConfiguration config) {
		this.config = config;
	}
	
	protected void addDataSources(final Domain domain, final Request request) {
		// TODO query for data sources and add them here (cf {@link WaterDataProviderModule#addDataSources(Domain, Request)})
		domain.addSource(URI.create("http://sparql.tw.rpi.edu/source/epa-gov"), "epa.gov");
	}
	
	protected void addRegulations(final Domain domain) {
		// TODO query for regulations and add them here
		domain.addRegulation(URI.create("http://was.tw.rpi.edu/semanteco/regulations/EPA-air-regulation.owl"), "EPA Regulation");
	}
	
	protected void addDataTypes(final Domain domain) {
		// change to icon names here should also occur in air-data-provider.js
		Resource res = config.getResource("clean-air.png");
		domain.addDataType("clean-air", "Clean Air", res);
		res = config.getResource("clean-air-facility.png");
		domain.addDataType("clean-air-facility", "Clean Air Facility", res);
		res = config.getResource("polluted-air.png");
		domain.addDataType("polluted-air", "Polluted Air", res);
		res = config.getResource("polluted-air-facility.png");
		domain.addDataType("polluted-air-facility", "Polluted Air Facility", res);
	}

}
