package edu.rpi.tw.escience.waterquality.datasource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.ModuleConfiguration;
import edu.rpi.tw.escience.waterquality.Request;
import edu.rpi.tw.escience.waterquality.query.GraphComponentCollection;
import edu.rpi.tw.escience.waterquality.query.NamedGraphComponent;
import edu.rpi.tw.escience.waterquality.query.OptionalComponent;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.QueryResource;
import edu.rpi.tw.escience.waterquality.query.Query.SortType;
import edu.rpi.tw.escience.waterquality.query.Query.Type;
import edu.rpi.tw.escience.waterquality.query.Variable;
import edu.rpi.tw.escience.waterquality.util.LimitUtils;

/**
 * DataModelBuilder is a utility class used by the DataSourceModule to build an appropriate
 * data model for a given request. It is responsible for making all of the necessary external 
 * SPARQL requests to where the data are stored.
 * 
 * @author ewpatton
 *
 */
public class DataModelBuilder extends QueryUtils {
	
	private static final String SITE = "s";
	private static final String LAT = "lat";
	private static final String LONG = "long";
	
	private final Logger log;
	private final String stateUri;
	private final List<String> sources = new ArrayList<String>();
	private final ModuleConfiguration config;
	private final String countyCode;
	private final Request request;
	
	/**
	 * Constructs a DataModelBuilder for the specified request
	 * @param request Request object encapsulating the client's request
	 * @param config Data source module's configuration
	 */
	public DataModelBuilder(final Request request, final ModuleConfiguration config) {
		super(request, config);
		this.log = request.getLogger();
		this.config = config;
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
	}
	
	/**
	 * Builds the model from the current state of the DataModelBuilder
	 * @param model A Jena model to populate with triples
	 * @return
	 */
	public boolean build(final Model model) {
		log.trace("build");
		for(String source : sources) {
			loadDataForSource(source, model);
		}
		return false;
	}

	/**
	 * Loads triples for the specified source into the model
	 * @param source URI representing a source in the triple store, e.g. http://sparql.tw.rpi.edu/source/epa-gov
	 * @param model A Jena model to populate with triples
	 */
	protected void loadDataForSource(final String source, final Model model) {
		log.trace("loadDataForSource");
		final Query query = config.getQueryFactory().newQuery(Type.CONSTRUCT);
		if(buildQueryForSource(query, source)) {
			config.getQueryExecutor(request).accept("text/turtle").execute(query, model);
		}
	}

	/**
	 * Builds the SPARQL query for a given source. Each source can potentially have a different structure.
	 * This method is inefficient due to differences in how the data were mapped during the data 
	 * conversion process
	 * @param query Query object to manipulate
	 * @param source URI representing the source to process, e.g. http://sparql.tw.rpi.edu/source/epa-gov
	 * @return
	 */
	protected boolean buildQueryForSource(final Query query, final String source) {
		log.trace("buildQueryForSource");
		final List<String> graphs = retrieveStateGraphsForSource(stateUri, source);
		List<String> sites = null;
		String measurementGraph = null;
		for(int i=0;i<graphs.size();i++) {
			String graph = graphs.get(i);
			if(graph.contains("measurement")) {
				measurementGraph = graph;
			}
			else if(graph.contains("echo") || graph.contains("foia")) {
				double clat = Double.parseDouble((String)request.getParam("lat"));
				double clng = Double.parseDouble((String)request.getParam("lng"));
				int limit = LimitUtils.getLimit(request, "facility");
				int offset = LimitUtils.getOffset(request, "facility");
				sites = listEPASitesInBounds(graphs, clat, clng, offset, limit);
				extendQueryForEPAFacilities(query, graph, sites);
			}
			else if(graph.contains("nwis")) {
				double clat = Double.parseDouble((String)request.getParam("lat"));
				double clng = Double.parseDouble((String)request.getParam("lng"));
				int limit = LimitUtils.getLimit(request, "site");
				int offset = LimitUtils.getOffset(request, "site");
				sites = listUSGSSitesInBounds(graphs, clat, clng, offset, limit);
				extendQueryForUSGSSites(query, graph, sites);
			}
			else {
				log.warn("Unable to process graph '"+graph+"'");
			}
		}
		if(measurementGraph != null) {
			extendQueryForMeasurements(query, measurementGraph);
		}
		return true;
	}
	
	/**
	 * Extends the query under construction with a graph component that encodes the structure of
	 * measurements in the SPARQL endpoint.
	 * @param query Query object to be extended
	 * @param graphUri URI of the graph containing triples
	 * @return
	 */
	protected boolean extendQueryForMeasurements(final Query query, final String graphUri) {
		log.trace("extendQueryForMeasurements");
		
		// named graphs
		final GraphComponentCollection construct = query.getConstructComponent();
		final NamedGraphComponent graph = query.getNamedGraph(graphUri);

		// variables
		final Variable s = query.getVariable(QUERY_NS+"s");
		final Variable measurement = query.getVariable(QUERY_NS+"measurement");
		final Variable element = query.getVariable(QUERY_NS+"element");
		final Variable value = query.getVariable(QUERY_NS+"value");
		final Variable unit = query.getVariable(QUERY_NS+"unit");
		
		// known uris
		final QueryResource rdfType = query.getResource(RDF_NS+"type");
		final QueryResource waterWaterMeasurement = query.getResource(WATER_NS+"WaterMeasurement");
		final QueryResource polHasCharacteristic = query.getResource(POL_NS+"hasCharacteristic");
		final QueryResource polHasValue = query.getResource(POL_NS+"hasValue");
		final QueryResource unitHasUnit = query.getResource(UNIT_NS+"hasUnit");
		final QueryResource reprHasUnit = query.getResource(REPR_NS+"hasUnit");
		
		// build construct query
		construct.addPattern(measurement, rdfType, waterWaterMeasurement);
		construct.addPattern(measurement, polHasCharacteristic, element);
		construct.addPattern(measurement, polHasValue, value);
		construct.addPattern(measurement, unitHasUnit, unit);
		
		// build where clause
		if(graphUri.contains("nwis")) {
			final QueryResource polHasSite = query.getResource(POL_NS+"hasSite");
			graph.addPattern(measurement, polHasSite, s);
		}
		else {
			final Variable permit = query.getVariable(QUERY_NS+"permit");
			final QueryResource polHasPermit = query.getResource(POL_NS+"hasPermit");
			graph.addPattern(measurement, polHasPermit, permit);
		}
		graph.addPattern(measurement, polHasCharacteristic, element);
		if(graphUri.contains("echo")) {
			final QueryResource rdfValue = query.getResource(RDF_NS+"value");
			graph.addPattern(measurement, rdfValue, value);
		}
		else {
			graph.addPattern(measurement, polHasValue, value);
		}
		graph.addPattern(measurement, reprHasUnit, unit);
		
		return true;
	}
	
	/**
	 * Extends the query under construction with a graph component that encodes the structure of
	 * EPA facilities in the SPARQL endpoint.
	 * @param query Query object to be extended
	 * @param graphUri URI of the graph containing triples
	 * @return
	 */
	protected boolean extendQueryForEPAFacilities(final Query query, final String graphUri,
			final List<String> facUris) {
		log.trace("extendQueryForEPAFacilities");
		
		// variables
		final Variable s = query.getVariable(QUERY_NS+"s");
		final Variable label = query.getVariable(QUERY_NS+"label");
		final Variable measurement = query.getVariable(QUERY_NS+"measurement");
		final Variable lat = query.getVariable(QUERY_NS+LAT);
		final Variable lng = query.getVariable(QUERY_NS+LONG);
		
		// known uris
		final QueryResource rdfType = query.getResource(RDF_NS+"type");
		final QueryResource waterWaterFacility = query.getResource(WATER_NS+"WaterFacility");
		final QueryResource rdfsLabel = query.getResource(RDFS_NS+"label");
		final QueryResource polHasMeasurement = query.getResource(POL_NS+"hasMeasurement");
		final QueryResource wgsLat = query.getResource(WGS_NS+LAT);
		final QueryResource wgsLong = query.getResource(WGS_NS+LONG);
		
		// build construct clause
		final GraphComponentCollection construct = query.getConstructComponent();
		construct.addPattern(s, rdfType, waterWaterFacility);
		construct.addPattern(s, rdfsLabel, label);
		construct.addPattern(s, wgsLat, lat);
		construct.addPattern(s, wgsLong, lng);
		construct.addPattern(s, polHasMeasurement, measurement);
		
		// build where clause
		final GraphComponentCollection facilities = query.getNamedGraph(graphUri);
		facilities.addPattern(s, rdfType, waterWaterFacility);
		addSiteFilter(facilities, facUris);
		facilities.addPattern(s, wgsLat, lat);
		facilities.addPattern(s, wgsLong, lng);
		final OptionalComponent optional = query.createOptional();
		facilities.addGraphComponent(optional);
		optional.addPattern(s, rdfsLabel, label);
		
		return false;
	}
	
	/**
	 * Extends the query under construction with a graph component that encodes the structure of
	 * USGS measurement sites in the SPARQL endpoint.
	 * @param query Query object to be extended
	 * @param graphUri URI of the graph containing triples
	 * @param siteUris List of sites used to limit the size of the result set
	 * @return
	 */
	protected boolean extendQueryForUSGSSites(final Query query, final String graphUri,
			final List<String> siteUris) {
		log.trace("extendQueryForUSGSSites");
		
		// variables
		final Variable s = query.getVariable(QUERY_NS+"s");
		final Variable label = query.getVariable(QUERY_NS+"label");
		final Variable measurement = query.getVariable(QUERY_NS+"measurement");
		final Variable state = query.getVariable(QUERY_NS+"state");
		final Variable lat = query.getVariable(QUERY_NS+LAT);
		final Variable lng = query.getVariable(QUERY_NS+LONG);
		
		// known uris
		final QueryResource rdfType = query.getResource(RDF_NS+"type");
		final QueryResource waterWaterSite = query.getResource(WATER_NS+"WaterSite");
		final QueryResource rdfsLabel = query.getResource(RDFS_NS+"label");
		final QueryResource polHasMeasurement = query.getResource(POL_NS+"hasMeasurement");
		final QueryResource polHasCountyCode = query.getResource(POL_NS+"hasCountyCode");
		final QueryResource polHasStateCode = query.getResource(POL_NS+"hasStateCode");
		final QueryResource wgsLat = query.getResource(WGS_NS+LAT);
		final QueryResource wgsLong = query.getResource(WGS_NS+LONG);
		
		// build construct clause
		final GraphComponentCollection construct = query.getConstructComponent();
		construct.addPattern(s, rdfType, waterWaterSite);
		construct.addPattern(s, rdfsLabel, label);
		construct.addPattern(s, polHasMeasurement, measurement);
		construct.addPattern(s, polHasCountyCode, countyCode, XSDDatatype.XSDint);
		construct.addPattern(s, polHasStateCode, state);
		construct.addPattern(s, wgsLat, lat);
		construct.addPattern(s, wgsLong, lng);
		
		// build where clause
		final GraphComponentCollection sites = query.getNamedGraph(graphUri);
		sites.addPattern(s, rdfType, waterWaterSite);
		addSiteFilter(sites, siteUris);
		sites.addPattern(s, polHasCountyCode, countyCode, XSDDatatype.XSDint);
		sites.addPattern(s, polHasStateCode, state);
		sites.addPattern(s, wgsLat, lat);
		sites.addPattern(s, wgsLong, lng);
		final OptionalComponent optional = query.createOptional();
		sites.addGraphComponent(optional);
		optional.addPattern(s, rdfsLabel, label);
		
		return true;
	}
	
	/**
	 * Uses a SPARQL query to generate a list of USGS sites sorted by their
	 * distance to the supplied latitude, longitude coordinate.
	 * @param graphs Set of graphs containing USGS data for a particular state (should be length 2)
	 * @param clat Center latitude of the viewport
	 * @param clng Center longitude of the viewport
	 * @param offset Offset into the ordered set to return
	 * @param limit Limit on the number to return
	 * @return
	 */
	protected final List<String> listUSGSSitesInBounds(final List<String> graphs, 
			final double clat, final double clng,
			final int offset, final int limit) {
		log.trace("listUSGSSitesInBounds");
		
		// figure out which graph is which
		String sitesUri = null;
		String measuresUri = null;
		if(graphs.get(0).contains("measurement")) {
			measuresUri = graphs.get(0);
			sitesUri = graphs.get(1);
		}
		else {
			measuresUri = graphs.get(1);
			sitesUri = graphs.get(0);
		}

		// generate a query
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		query.setDistinct(true);
		
		// named graphs
		final NamedGraphComponent sites = query.getNamedGraph(sitesUri);
		final NamedGraphComponent measures = query.getNamedGraph(measuresUri);
		
		// variables
		final Variable s = query.getVariable(QUERY_NS+"s");
		final Variable lat = query.getVariable(QUERY_NS+"lat");
		final Variable lng = query.getVariable(QUERY_NS+"long");
		final Variable measurement = query.getVariable(QUERY_NS+"measurement");
		
		// known uris
		final QueryResource rdfType = query.getResource(RDF_NS+"type");
		final QueryResource waterWaterSite = query.getResource(WATER_NS+"WaterSite");
		final QueryResource polHasCountyCode = query.getResource(POL_NS+"hasCountyCode");
		final QueryResource wgsLat = query.getResource(WGS_NS+LAT);
		final QueryResource wgsLong = query.getResource(WGS_NS+LONG);
		final QueryResource polHasSite = query.getResource(POL_NS+"hasSite");
		
		// build query
		sites.addPattern(s, rdfType, waterWaterSite);
		sites.addPattern(s, polHasCountyCode, countyCode, XSDDatatype.XSDint);
		sites.addPattern(s, wgsLat, lat);
		sites.addPattern(s, wgsLong, lng);
		measures.addPattern(measurement, polHasSite, s);
		query.addOrderBy("((?"+LAT+" - "+clat+")*(?"+LAT+" - "+clat+")+" +
				"(?"+LONG+" - "+clng+")*(?"+LONG+" - "+clng+"))", SortType.ASC);
		query.setOffset(offset);
		query.setLimit(limit);
		
		// set select only for s variable
		Set<Variable> vars = new HashSet<Variable>();
		vars.add(s);
		query.setVariables(vars);
		
		// execute and return results
		String results = config.getQueryExecutor(request).accept("application/json").execute(query);
		return processUriList(results);
	}
	
	protected final List<String> listEPASitesInBounds(final List<String> graphs,
			final double clat, final double clng, final int offset, final int limit) {
		log.trace("listEPASitesInBounds");

		// figure out which graph is which
		String sitesUri = null;
		String measuresUri = null;
		if(graphs.get(0).contains("measurement")) {
			measuresUri = graphs.get(0);
			sitesUri = graphs.get(1);
		}
		else {
			measuresUri = graphs.get(1);
			sitesUri = graphs.get(0);
		}

		// generate a query
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		query.setDistinct(true);
		
		// named graphs
		final NamedGraphComponent sites = query.getNamedGraph(sitesUri);
		final NamedGraphComponent measures = query.getNamedGraph(measuresUri);
		
		// variables
		final Variable s = query.getVariable(QUERY_NS+"s");
		final Variable lat = query.getVariable(QUERY_NS+"lat");
		final Variable lng = query.getVariable(QUERY_NS+"long");
		final Variable measurement = query.getVariable(QUERY_NS+"measurement");
		final Variable permit = query.getVariable(QUERY_NS+"permit");
		
		// known uris
		final QueryResource rdfType = query.getResource(RDF_NS+"type");
		final QueryResource waterWaterFacility = query.getResource(WATER_NS+"WaterFacility");
		final QueryResource polHasCountyCode = query.getResource(POL_NS+"hasCountyCode");
		final QueryResource wgsLat = query.getResource(WGS_NS+LAT);
		final QueryResource wgsLong = query.getResource(WGS_NS+LONG);
		final QueryResource polHasPermit = query.getResource(POL_NS+"hasPermit");
		
		// build query
		sites.addPattern(s, rdfType, waterWaterFacility);
		sites.addPattern(s, polHasCountyCode, request.getParam("state")+countyCode, null);
		sites.addPattern(s, wgsLat, lat);
		sites.addPattern(s, wgsLong, lng);
		sites.addPattern(s, polHasPermit, permit);
		measures.addPattern(measurement, polHasPermit, permit);
		query.addOrderBy("((?"+LAT+" - "+clat+")*(?"+LAT+" - "+clat+")+" +
				"(?"+LONG+" - "+clng+")*(?"+LONG+" - "+clng+"))", SortType.ASC);
		query.setOffset(offset);
		query.setLimit(limit);
		
		// set select only for s variable
		Set<Variable> vars = new HashSet<Variable>();
		vars.add(s);
		query.setVariables(vars);
		
		// execute and return results
		String results = config.getQueryExecutor(request).accept("application/json").execute(query);
		return processUriList(results);
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
