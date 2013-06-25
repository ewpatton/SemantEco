package edu.rpi.tw.escience.semanteco.geo;

import static edu.rpi.tw.escience.semanteco.query.Query.VAR_NS;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;


import edu.rpi.tw.escience.semanteco.HierarchicalMethod;
import edu.rpi.tw.escience.semanteco.HierarchyEntry;
import edu.rpi.tw.escience.semanteco.HierarchyVerb;
import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.QueryMethod;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.Resource;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.query.GraphComponentCollection;

import edu.rpi.tw.escience.semanteco.query.NamedGraphComponent;
import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.QueryResource;
import edu.rpi.tw.escience.semanteco.query.UnionComponent;
import edu.rpi.tw.escience.semanteco.query.Variable;
import edu.rpi.tw.escience.semanteco.query.Query.SortType;
import edu.rpi.tw.escience.semanteco.query.Query.Type;
import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.ProvidesDomain;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SemantEcoGeoModule implements Module, ProvidesDomain {
	// Define that name spaces!
	private static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
	private static final String WGS_NS = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	private static final String POLLUTION_NS = "http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#";
	private static final String DEFAULT_NS = "http://purl.org/twc/semantgeo/source/aeap_nys/dataset/dfw_lake_samples/aeap-nyserda-chem-94-12-v9-web/version/2013-April-24/";
    private static final String WATER_NS = "http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#";
	//public static final String FISH_NS = "http://escience.rpi.edu/ontology/semanteco/2/0/fish.owl#";
	public static final String QUERY_NS = "http://aquarius.tw.rpi.edu/projects/semantaqua/data-source/query-variable/";
	private static final String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private static final String LAT = "lat";
	private static final String LONG = "long";
	private static final String POL_NS = "http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#";
    private static final String OBOE_NS = "http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#";
	public static final String UNIT_NS = "http://sweet.jpl.nasa.gov/2.1/reprSciUnits.owl#";
	public static final String REPR_NS = "http://sweet.jpl.nasa.gov/2.1/repr.owl#";
	private static final String BINDINGS = "bindings";


	
	// Define some constants
	private static final String FAILURE = "{\"success\":false}";
	private static final Logger log = Logger.getLogger(SemantEcoGeoModule.class);
	
	private ModuleConfiguration config = null;
	

	@Override
	public void visit(Model model, Request request, Domain domain) {
		// TODO Auto-generated method stub
		
		String domainUri = domain.getUri().toString();
		String countyCode = (String) request.getParam("county");
		String stateAbbr = (String) request.getParam("state");

		if (domainUri.equals(WATER_NS)) {
			
			final Query query = config.getQueryFactory().newQuery(Type.CONSTRUCT);
			final QueryResource WaterSite = query.getResource(WATER_NS
					+ "WaterSite");
			final QueryResource waterSite = query.getResource(WATER_NS
					+ "waterSite");
			final Variable s = query.getVariable(QUERY_NS + "s");
			final GraphComponentCollection construct = query
					.getConstructComponent();
			final QueryResource wgsLat = query.getResource(WGS_NS + LAT);
			final QueryResource wgsLong = query.getResource(WGS_NS + LONG);
			final QueryResource ofEntity = query.getResource(OBOE_NS + "ofEntity");
			final QueryResource hasContext = query.getResource(OBOE_NS + "hasContext");


			final Variable measurement = query.getVariable(QUERY_NS
					+ "measurement");
			final Variable lat = query.getVariable(QUERY_NS + LAT);
			final Variable lng = query.getVariable(QUERY_NS + LONG);
			final Variable label = query.getVariable(QUERY_NS + "label");
			final Variable entity = query.getVariable(QUERY_NS + "entity");
			
			final QueryResource rdfsLabel = query
					.getResource(RDFS_NS + "label");
			final QueryResource rdfType = query.getResource(RDF_NS + "type");
			final QueryResource site = query
					.getResource(DEFAULT_NS + "site");
			final QueryResource polHasCharacteristic = query.getResource(POL_NS+"hasCharacteristic");
			final QueryResource polHasMeasurement = query.getResource(POL_NS+"hasMeasurement");

			final QueryResource polHasValue = query.getResource(POL_NS+"hasValue");
			final Variable element = query.getVariable(QUERY_NS+"element");
			final Variable value = query.getVariable(QUERY_NS+"value");
			final Variable unit = query.getVariable(QUERY_NS+"unit");
			final QueryResource reprHasUnit = query.getResource(UNIT_NS+"hasUnit");
			
			final QueryResource countyCoded = query
					.getResource(POL_NS + "hasCountyCode");
			final QueryResource stateAbbrev = query
					.getResource(POL_NS + "hasStateCode");

			//        <http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#ofEntity> <http://purl.org/twc/semantgeo/source/aeap_nys/dataset/dfw_lake_samples/aeap-nyserda-chem-94-12-v9-web/typed/watersample/9446846> ;
			//<http://purl.org/twc/semantgeo/source/aeap_nys/dataset/dfw_lake_samples/aeap-nyserda-chem-94-12-v9-web/typed/watersample/9446846> <http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#hasContext> <http://purl.org/twc/semantgeo/source/aeap_nys/dataset/dfw_lake_samples/aeap-nyserda-chem-94-12-v9-web/typed/lake/040752> .
			final QueryResource unitHasUnit = query.getResource(POL_NS+"hasUnit");
			String geofeatures = (String) request.getParam("geofeatures");

			if(geofeatures !=null  && geofeatures.length() == 1){
				request.getLogger().debug(
						"species length: "
								+ ((JSONArray) request.getParam("geofeatures"))
										.length());
				request.getLogger().debug("(got to if where geofeatures == 1)");
				JSONArray geoFeaturesParams = (JSONArray) request
						.getParam("geofeatures");
				String geoFeaturesInArray = geoFeaturesParams.optString(0);
				
				
				//the standard query pre-lake constraint
				construct.addPattern(s, rdfType, WaterSite);
				construct.addPattern(s, rdfsLabel, label);
				construct.addPattern(s, wgsLat, lat);
				construct.addPattern(s, wgsLong, lng);
				construct.addPattern(s, polHasMeasurement, measurement);
				construct.addPattern(measurement, polHasCharacteristic, element);
				construct.addPattern(measurement, polHasValue, value);
				construct.addPattern(measurement, reprHasUnit, unit);
				final QueryResource waterWaterMeasurement = query.getResource(WATER_NS+"WaterMeasurement");
				construct.addPattern(measurement, rdfType, waterWaterMeasurement);
				

				
				final GraphComponentCollection graph = query
						.getNamedGraph("AEAP_NYSERDA_ph2");
				final GraphComponentCollection graph2 = query
						.getNamedGraph("AEAP_NYSERDA_Locations");
				
				
				graph.addPattern(measurement, ofEntity, entity); //so entity is the sample
				
				//in this block, we are binding to selected lakes, no commenting out below.
				final QueryResource geoFeature = query.getResource(geoFeaturesInArray);

				//graph.addPattern(entity, hasContext, s);
				graph.addPattern(entity, hasContext, geoFeature);

				
				graph.addPattern(measurement, polHasCharacteristic, element);
				graph.addPattern(measurement, polHasValue, value);
				graph.addPattern(measurement, unitHasUnit, unit);

				graph2.addPattern(s, rdfType, waterSite);
				graph2.addPattern(s, rdfsLabel, label);
				//graph.addPattern(measurement, site, s);
				//graph2.addPattern(s, countyCoded, countyCode, null);
				//graph2.addPattern(s, stateAbbrev, stateAbbr, null);
				graph2.addPattern(s, wgsLat, lat);
				graph2.addPattern(s, wgsLong, lng);
				
				config.getQueryExecutor(request).accept("text/turtle")
				.execute(query, model);
				
				
				
				
				
				
			//	final UnionComponent union = query.createUnion();
			//	final NamedGraphComponent graph2 = query
			//			.getNamedGraph("http://darrin-lakes");
				
			}
			
			
			
			else if(countyCode !=null && stateAbbr !=null ){

			assert (countyCode != null);
			assert (stateAbbr != null);
			
			construct.addPattern(s, rdfType, WaterSite);
			construct.addPattern(s, rdfsLabel, label);
			construct.addPattern(s, wgsLat, lat);
			construct.addPattern(s, wgsLong, lng);
			construct.addPattern(s, polHasMeasurement, measurement);
			construct.addPattern(measurement, polHasCharacteristic, element);
			construct.addPattern(measurement, polHasValue, value);
			construct.addPattern(measurement, reprHasUnit, unit);
			final QueryResource waterWaterMeasurement = query.getResource(WATER_NS+"WaterMeasurement");
			construct.addPattern(measurement, rdfType, waterWaterMeasurement);
			

			
			final GraphComponentCollection graph = query
					.getNamedGraph("AEAP_NYSERDA_ph2");
			final GraphComponentCollection graph2 = query
					.getNamedGraph("AEAP_NYSERDA_Locations");
			
			
			graph.addPattern(measurement, ofEntity, entity);
			graph.addPattern(entity, hasContext, s);
			graph.addPattern(measurement, polHasCharacteristic, element);
			graph.addPattern(measurement, polHasValue, value);
			graph.addPattern(measurement, unitHasUnit, unit);

			graph2.addPattern(s, rdfType, waterSite);
			graph2.addPattern(s, rdfsLabel, label);
			//graph.addPattern(measurement, site, s);
			graph2.addPattern(s, countyCoded, countyCode, null);
			graph2.addPattern(s, stateAbbrev, stateAbbr, null);
			graph2.addPattern(s, wgsLat, lat);
			graph2.addPattern(s, wgsLong, lng);
			
			config.getQueryExecutor(request).accept("text/turtle")
			.execute(query, model);
			
			}
			
		}

		
	}
	
	/**
	 * Provides the hierarchical behavior for the darrin species module's facet.
	 * @param request
	 * @param action
	 * @return
	 */
	@HierarchicalMethod(parameter = "geofeatures")
	public Collection<HierarchyEntry> querydarrinTaxonomyHM(
			final Request request, final HierarchyVerb action) {
		List<HierarchyEntry> items = new ArrayList<HierarchyEntry>();
		if (action == HierarchyVerb.ROOTS) {
			// HierarchyEntry entry = new HierarchyEntry();
			// entry.setUri(URI.create("http://example.com/bird1"));
			// entry.setLabel("bird1");
			// entry.setAltLabel("birdicus uno");
			// items.add(entry);
			// entry = new HierarchyEntry();
			// entry.setUri(URI.create("http://example.com/bird2"));
			// entry.setLabel("bird2");
			// entry.setAltLabel("birdicus dos");
			// items.add(entry);
			return queryedarrinTaxonomyHMRoots(request);
		} else if (action == HierarchyVerb.CHILDREN) {
			// if (
			// request.getParam("species").equals("http://example.com/bird1") )
			// {
			// HierarchyEntry entry = new HierarchyEntry();
			// entry.setUri(URI.create("http://example.com/bird3"));
			// entry.setLabel("bird3");
			// entry.setAltLabel("birdicus tres");
			// items.add(entry);
			// entry = new HierarchyEntry();
			// entry.setUri(URI.create("http://example.com/bird4"));
			// entry.setLabel("bird4");
			// entry.setAltLabel("birdicus quatro");
			// items.add(entry);
			// }
			return querydarrinTaxonomyHMChildren(request,
					(String) request.getParam("geofeatures"));
		} else if (action == HierarchyVerb.SEARCH) {
			return searchDarrinSpecies(request, (String) request.getParam("string"));
		} else if (action == HierarchyVerb.PATH_TO_NODE) {
			return darrinSpeciesPathToNode(request, (String) request.getParam("uri"));
		}
		return items;
	}
	
	protected Collection<HierarchyEntry> queryedarrinTaxonomyHMRoots(final Request request) {
		Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();
		// Variables
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);

				final Variable id = query.getVariable(VAR_NS + "child");
				final Variable label = query.getVariable(VAR_NS + "label");
				final Variable parent = query.getVariable(VAR_NS + "parent");
				
				final QueryResource subClassOf = query.getResource(RDFS_NS
						+ "subClassOf");
				final QueryResource hasLabel = query.getResource(RDFS_NS + "label");

				final QueryResource darrinTaxonomy = query
						.getResource("http://darrin-taxonomy");
				request.getLogger().info("reached darrin-taxonomy \n");
				
				// build query
				Set<Variable> vars = new LinkedHashSet<Variable>();
				vars.add(id);
				vars.add(label);
				vars.add(parent);
				query.setVariables(vars);
				query.addOrderBy(label, SortType.ASC);
				final NamedGraphComponent graph = query
						.getNamedGraph("http://darrin-taxonomy");
				graph.addPattern(id, subClassOf, parent);
				graph.addPattern(id, subClassOf, darrinTaxonomy);
				graph.addPattern(id, hasLabel, label);
				
				String resultStr = config.getQueryExecutor(request)
						.accept("application/json").execute(query);
				if (resultStr == null) {
					return entries;
				}
				
				try {
					JSONObject results = new JSONObject(resultStr);
					results = results.getJSONObject("results");
					JSONArray bindings = results.getJSONArray(BINDINGS);
					for (int i = 0; i < bindings.length(); i++) {
						JSONObject binding = bindings.getJSONObject(i);
						String subclassId = binding.getJSONObject("child").getString(
								"value");
						String subclassLabel = binding.getJSONObject("label")
								.getString("value");
						HierarchyEntry entry = new HierarchyEntry();
						entry.setUri(subclassId);
						entry.setLabel(subclassLabel);
						entries.add(entry);
					}
				} catch (JSONException e) {
					log.error("Unable to parse JSON results", e);
				}
				

		
		return entries;

	}
	protected Collection<HierarchyEntry> querydarrinTaxonomyHMChildren(
			final Request request, final String species) {
			Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();
			
				final Query query = config.getQueryFactory().newQuery(Type.SELECT);
				// Variables
				final Variable id = query.getVariable(VAR_NS + "child");
				final Variable label = query.getVariable(VAR_NS + "label");
				final Variable parent = query.getVariable(VAR_NS + "parent");
				// URIs
				final QueryResource subClassOf = query.getResource(RDFS_NS
						+ "subClassOf");
				final QueryResource classPredicate = query
						.getResource("http://purl.org/twc/semantgeo/source/aeap_nys/dataset/dfw_lake_samples/vocab/enhancement/1/class");
			
				final QueryResource classRequiresSubclasses = query
						.getResource(species);
				final QueryResource rdfsLabel = query
						.getResource(RDFS_NS + "label");

				// build query
				Set<Variable> vars = new LinkedHashSet<Variable>();
				vars.add(id);
				vars.add(label);
				vars.add(parent);
				query.setVariables(vars);
				query.addOrderBy(label, SortType.ASC);
				final NamedGraphComponent graph = query
						.getNamedGraph("http://darrin-taxonomy");
				graph.addPattern(id, classPredicate, parent);
				graph.addPattern(id, classPredicate, classRequiresSubclasses);

				graph.addPattern(id, rdfsLabel, label);
				String resultStr = config.getQueryExecutor(request)
						.accept("application/json").execute(query);
				if (resultStr == null) {
					return entries;
				}
				try {
					JSONObject results = new JSONObject(resultStr);
					results = results.getJSONObject("results");
					JSONArray bindings = results.getJSONArray(BINDINGS);
					for (int i = 0; i < bindings.length(); i++) {
						JSONObject binding = bindings.getJSONObject(i);
						HierarchyEntry entry = new HierarchyEntry();
						entry.setUri(binding.getJSONObject("child").getString("value"));
						entry.setLabel(binding.getJSONObject("label")
								.getString("value"));

						try {
							entry.setParent(URI.create(binding.getJSONObject("parent")
									.getString("value")));
						} catch (Exception e) {
						}

						entries.add(entry);
					}
				} catch (JSONException e) {
					log.error("Unable to parse JSON results", e);
				}
				
			return entries;
	}
	protected Collection<HierarchyEntry> searchDarrinSpecies(final Request request,
			final String str) {
		final Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();
		return entries;
	}
	protected Collection<HierarchyEntry> darrinSpeciesPathToNode(final Request request,
			final String node) {
		final Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();
		return entries;

	}
	
	
	/*...MY HIERARCHY...*/
	@HierarchicalMethod(parameter = "lakes")
		public Collection<HierarchyEntry> queryeLakeTypesHM(
				final Request request, final HierarchyVerb action) {
			List<HierarchyEntry> items = new ArrayList<HierarchyEntry>();
			if (action == HierarchyVerb.ROOTS) {
				
			return queryeLakeTypesHMRoots(request);
			} else if (action == HierarchyVerb.CHILDREN) { 
				return queryeLakeTypesHMChildren(request,
					(String) request.getParam("lakes"));
			} else if (action == HierarchyVerb.SEARCH) {
				return searcheLake(request, (String) request.getParam("string"));
			} else if (action == HierarchyVerb.PATH_TO_NODE) {
				return eLakePathToNode(request, (String) request.getParam("uri"));
			}
			return items;
	    }
	
	
	protected Collection<HierarchyEntry> queryeLakeTypesHMChildren(
			final Request request, String lakes) {
				return null;		
	}
	
	protected Collection<HierarchyEntry> searcheLake(
			final Request request, String lakes) {
				return null;		
	}
	
	protected Collection<HierarchyEntry> eLakePathToNode(
			final Request request, String lakes) {
				return null;		
	}
	
	protected Collection<HierarchyEntry> queryeLakeTypesHMRoots(
			final Request request) {
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		// Variables
		final Variable id = query.getVariable(VAR_NS + "child");
		final Variable label = query.getVariable(VAR_NS + "label");
		final Variable parent = query.getVariable(VAR_NS + "parent");
		// URIs
		final QueryResource typeOf = query.getResource(RDF_NS
				+ "type");
		final QueryResource hasLabel = query.getResource(RDFS_NS + "label");

		final QueryResource lakeTypes = query
				.getResource("http://purl.org/twc/semantgeo/source/aeap_nys/dataset/dfw_lake_samples/vocab/Lake");
		request.getLogger().info("reached queryeLakesRoots\n");

		// build query
		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(id);
		vars.add(label);
		vars.add(parent);
		query.setVariables(vars);
		query.addOrderBy(label, SortType.ASC);
		// query.addPattern(site, inDataSet, dataSet);
		/* this is what our query looks like
		 * PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
		 * PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
		   PREFIX owl: <http://www.w3.org/2002/07/owl#>
		   PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
		   SELECT ?child ?label ?parent 
		   WHERE 
		  {
		  graph <http://purl.org/twc/semantgeo/source/aeap_nys/dataset/dfw_lake_samples/vocab/lake> {
		  ?child <http://www.w3.org/2000/01/rdf-schema#type> ?parent . 
		  ?child <http://www.w3.org/2000/01/rdf-schema#type> <http://purl.org/twc/semantgeo/source/aeap_nys/dataset/dfw_lake_samples/vocab/lake> . 
		  ?child <http://www.w3.org/2000/01/rdf-schema#label> ?label . 
		  }
		  }
		 */
		String LAKE_NS = "http://darrin-lakes";
		final NamedGraphComponent graph = query
				.getNamedGraph(LAKE_NS);
		graph.addPattern(id, typeOf, parent);
		graph.addPattern(id, typeOf, lakeTypes);

		graph.addPattern(id, hasLabel, label);
		// for testing common names
		// graph.addPattern(id, hasCommonName, label);

		// get only the subclasses of the subclasses of OWL thing
		Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();

		String resultStr = config.getQueryExecutor(request)
				.accept("application/json").execute(query);
		if (resultStr == null) {
			return entries;
		}
		try {
			JSONObject results = new JSONObject(resultStr);
			results = results.getJSONObject("results");
			JSONArray bindings = results.getJSONArray(BINDINGS);
			for (int i = 0; i < bindings.length(); i++) {
				JSONObject binding = bindings.getJSONObject(i);
				String subclassId = binding.getJSONObject("child").getString(
						"value");
				String subclassLabel = binding.getJSONObject("label")
						.getString("value");
				HierarchyEntry entry = new HierarchyEntry();
				entry.setUri(subclassId);
				entry.setLabel(subclassLabel);
				entries.add(entry);
			}
		} catch (JSONException e) {
			log.error("Unable to parse JSON results", e);
		}
		return entries;}

	
	

	@Override
	public void visit(OntModel model, Request request, Domain domain) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return "NYState Lakes";
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
		final NamedGraphComponent graph = query.getNamedGraph("AEAP_NYSERDA_ph2");
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
	    //Domain semantGeoDomain = config.getDomain(URI.create("http://purl.org/twc/SemantGeo/"), true);
		Domain water = config.getDomain(URI.create("http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#"), true);
	    // add data sources, regulations, and data types here
	   // domains.add(semantGeoDomain);
	   // semantGeoDomain.setLabel("SemantGeo");
		//addDataSources(semantGeoDomain, request);
		//addRegulations(water);
		//addDataTypes(semantGeoDomain);
	    return domains;
	 }
	
	protected void addDataSources(final Domain domain, final Request request) {
		// TODO query for data sources and add them here (cf {@link WaterDataProviderModule#addDataSources(Domain, Request)})
		domain.addSource(URI.create("http://sparql.tw.rpi.edu/source/darrin-fresh-water"), "Darrin Fresh Water");
	}
	
	protected void addRegulations(final Domain domain) {
		// TODO query for regulations and add them here
		//domain.addRegulation(URI.create("http://was.tw.rpi.edu/semanteco/regulations/EPA-air-regulation.owl"), "Darrin Fresh Water");
		//domain.addRegulation(URI.create("http://dataone.tw.rpi.edu/darrin-fresh-water.owl"), "Darrin Fresh Water");

	}
	

	
	protected void addDataTypes(final Domain domain) {
		// change to icon names here should also occur in air-data-provider.js
		//Resource res = config.getResource("clean-air.png");
		//domain.addDataType("clean-air", "DFW - Temp - 1", res);
		//res = config.getResource("polluted-air.png");
		//domain.addDataType("polluted-air", "DFW - Temp - 2", res);
	}

	@Override
	public void visit(Query query, Request request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(SemantEcoUI ui, Request request) {
		Resource res = null;
		Resource res2 = null;
		/*
		res = config.getResource("darrinSpeciesHierarchy.js");
		res2 = config.getResource("darrinSpeciesHierarchy.jsp");
		
		ui.addScript(res);
		ui.addFacet(res2);
		*/
		
		res = config.getResource("sampleHierarchy.js");
		res2 = config.getResource("sampleHierarchy.jsp");
		ui.addScript(res);
		ui.addFacet(res2);
		
	}

}
