package edu.rpi.tw.escience.characteristics;

import static edu.rpi.tw.escience.semanteco.query.Query.VAR_NS;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.Domain;
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
import edu.rpi.tw.escience.semanteco.query.OptionalComponent;
import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.Query.SortType;
import edu.rpi.tw.escience.semanteco.query.Query.Type;
import edu.rpi.tw.escience.semanteco.query.QueryResource;
import edu.rpi.tw.escience.semanteco.query.UnionComponent;
import edu.rpi.tw.escience.semanteco.query.Variable;
import edu.rpi.tw.escience.semanteco.util.QueryVariableUtils;

/**
 * The characteristics module allows users to limit data access to subsets of
 * measurements based on a set of characteristics. It provides a hierarchical
 * search facet from which users select characteristics.
 * @author ewpatton
 * @author apseyed
 *
 */
public class CharacteristicsModule implements Module {
	private static final String POL_NS = "http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#";
	private static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
	private static final String FAILURE = "{\"success\":false}";
	private static final String BINDINGS = "bindings";
	private static final String XSD_NS = "http://www.w3.org/2001/XMLSchema#";
	private static final String UNIT_NS = "http://sweet.jpl.nasa.gov/2.1/reprSciUnits.owl#";
	private static final String TIME_NS = "http://www.w3.org/2006/time#";
	private static final String OWL_NS = "http://www.w3.org/2002/07/owl#";
	private static final String CUAHSI_NS = "http://was.tw.rpi.edu/all-characteristics-cuahsi-ontology2";
	private static final String PROP_VAR = "p";

	private ModuleConfiguration config = null;
	private static final Logger LOG = Logger
			.getLogger(CharacteristicsModule.class);

	@Override
	public void visit(final Model model, final Request request,
			final Domain domain) {
	}

	@Override
	public void visit(final SemantEcoUI ui, final Request request) {
		Resource res = config.getResource("characteristicHierarchy.jsp");
		ui.addFacet(res);
	}

	@Override
	public void visit(final OntModel model, final Request request,
			final Domain domain) {
		// model.read(CHARACTERISTIC_NS);
	}

	/**
	 * Provides server-side implementation of the module's hierarchical facet.
	 * @param request Client request object
	 * @param action Action the hierarchical facet is attempting
	 * @return
	 */
	@HierarchicalMethod(parameter="characteristic")
	public Collection<HierarchyEntry> queryCharacteristicsTaxonomyHM(final Request request, final HierarchyVerb action) {
		switch(action) {
		case ROOTS:
			return getTaxonomySubclass(request, OWL_NS+"Thing");
		case CHILDREN:
			return getTaxonomySubclass(request, (String)request.getParam("characteristic"));
		case SEARCH:
			return searchCharacteristics(request);
		case PATH_TO_NODE:
			return getPathInTaxonomy(request, (String)request.getParam("uri"));
		default:
			return Collections.emptySet();
		}
	}

	protected Collection<HierarchyEntry> getTaxonomySubclass(final Request request, final String parentCls) {
		final Logger log = request.getLogger();
		final Collection<HierarchyEntry> entries = new LinkedList<HierarchyEntry>();
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		// Variables
		final Variable uri = query.getVariable(VAR_NS + "uri");
		final Variable label = query.getVariable(VAR_NS + "label");
		// URIs
		final QueryResource rdfsSubClassOf = query.getResource(RDFS_NS
				+ "subClassOf");
		final QueryResource rdfsLabel = query.getResource(RDFS_NS + "label");
		final QueryResource parent = query.getResource(parentCls);

		// build query
		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(uri);
		vars.add(label);
		query.setVariables(vars);

		final NamedGraphComponent graph = query.getNamedGraph(CUAHSI_NS);
		graph.addPattern(uri, rdfsSubClassOf, parent);
		graph.addPattern(uri, rdfsLabel, label);

		String resultStr = config.getQueryExecutor(request)
				.accept("application/json").execute(query);
		if (resultStr == null) {
			return entries;
		}
		JSONObject results = null;
		try {
			results = new JSONObject(resultStr).getJSONObject("results");
		} catch(JSONException e) {
			log.error("Unable to parse json results from endpoint.");
			return entries;
		}
		JSONArray bindings = results.optJSONArray(BINDINGS);
		URI parentUri = URI.create(parentCls);
		for (int i = 0; i < bindings.length(); i++) {
			JSONObject binding = bindings.optJSONObject(i);
			URI rootUri = URI.create(binding.optJSONObject("uri").optString("value"));
			String rootLabel = binding.optJSONObject("label").optString("value");
			entries.add(new HierarchyEntry(rootUri, parentUri, rootLabel));
		}
		return entries;
	}

	protected Collection<HierarchyEntry> searchCharacteristics(final Request request) {
		final String str = (String)request.getParam("string");
		final Collection<HierarchyEntry> entries = new LinkedList<HierarchyEntry>();
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		final Variable uri = query.createVariable(VAR_NS + "uri");
		final Variable label = query.createVariable(VAR_NS + "label");
		final QueryResource rdfsLabel = query.getResource(RDFS_NS + "label");
		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(uri);
		vars.add(label);
		query.setVariables(vars);
		final NamedGraphComponent graph = query.getNamedGraph(CUAHSI_NS);
		graph.addPattern(uri, rdfsLabel, label);
		graph.addFilter("bif:contains(?label,\"'" + str + "*'\")");
		query.addOrderBy(label, SortType.ASC);
		JSONObject results = null;
		try {
			results = new JSONObject(config.getQueryExecutor(request)
					.accept("application/json").execute(query))
				.getJSONObject("results");
		} catch(JSONException e) {
			LOG.error("Unable to parse json results from endpoint.");
			return entries;
		}
		JSONArray bindings = results.optJSONArray("bindings");
		for(int i = 0; i < bindings.length(); i++) {
			final JSONObject binding = bindings.optJSONObject(i);
			final HierarchyEntry entry = new HierarchyEntry();
			entry.setUri(binding.optJSONObject("uri").optString("value"));
			entry.setLabel(binding.optJSONObject("label").optString("value"));
			entries.add(entry);
		}
		return entries;
	}

	protected Collection<HierarchyEntry> getPathInTaxonomy(final Request request,
			final String node) {
		final Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		final Variable uri = query.createVariable(VAR_NS + "uri");
		final Variable label = query.createVariable(VAR_NS + "label");
		final Variable parent = query.createVariable(VAR_NS + "parent");
		final QueryResource nodeRes = query.getResource(node);
		final QueryResource rdfsSubClassOf = query.getResource(RDFS_NS
				+ "subClassOf");
		final QueryResource rdfsLabel = query.getResource(RDFS_NS + "label");
		final NamedGraphComponent graph = query.getNamedGraph(CUAHSI_NS);

		graph.addPattern(nodeRes, rdfsSubClassOf, parent, true);
		graph.addPattern(uri, rdfsSubClassOf, parent);
		graph.addPattern(uri, rdfsLabel, label);
		query.addOrderBy(label, SortType.ASC);

		try {
			final JSONObject results = new JSONObject(config
					.getQueryExecutor(request).accept("application/json")
					.execute(query));
			final JSONArray bindings = results.getJSONObject("results")
					.getJSONArray("bindings");
			for (int i = 0; i < bindings.length(); i++) {
				final JSONObject binding = bindings.getJSONObject(i);
				final HierarchyEntry entry = new HierarchyEntry();
				entry.setUri(binding.getJSONObject("uri").getString("value"));
				entry.setLabel(binding.getJSONObject("label")
						.getString("value"));
				if (binding.has("altLabel")) {
					entry.setAltLabel(binding.getJSONObject("altLabel")
							.getString("value"));
				}
				entry.setParent(URI.create(binding.getJSONObject("parent")
						.getString("value")));
				entries.add(entry);
			}
		} catch (JSONException e) {
			request.getLogger()
					.warn("Unable to parse data from remote server.");
		}
		return entries;
	}

	/**
	 * Queries for whether a characteristic URI is within the CUAHSI taxonomy.
	 * @param request Client request object used for executing queries
	 * @param characteristicInArray URI to test for membership in the taxonomy
	 * @return
	 */
	// first check ifg bbbq state is null for chemical
	// new Query().findGraphComponentsWithPattern(?measurement,
	// pol:hasCharacteristic, null)
	// returns a list of graphComponent. should be a singleon list, check on
	// what it returns when empty
	// if not empty, graph.addpattern();
	protected List<String> queryIfTaxonomicCharacteristicCategory(Request request,
			String characteristicInArray) {
		final List<String> uris = new LinkedList<String>();
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		final NamedGraphComponent graph = query
				.getNamedGraph("http://was.tw.rpi.edu/all-characteristics-cuahsi-ontology2");
		// final NamedGraphComponent graph =
		// query.getNamedGraph("http://was.tw.rpi.edu/ebird-data");
		final QueryResource subClassOf = query.getResource(RDFS_NS
				+ "subClassOf");
		final Variable speciesVariable = query.getVariable(VAR_NS
				+ "characteristic");
		final QueryResource addedCharacteristic = query
				.getResource(characteristicInArray);
		graph.addPattern(speciesVariable, subClassOf, addedCharacteristic);
		String resultStr = config.getQueryExecutor(request)
				.accept("application/json").execute(query);
		LOG.debug("Results: " + resultStr);
		if (resultStr == null) {
			return Collections.emptyList();
		}
		try {
			JSONObject results = new JSONObject(resultStr);
			results = results.getJSONObject("results");
			JSONArray bindings = results.getJSONArray(BINDINGS);
			for (int j = 0; j < bindings.length(); j++) {
				JSONObject binding = bindings.getJSONObject(j);
				String speciesId = binding.getJSONObject("characteristic")
						.getString("value");
				uris.add(speciesId);
			}
		} catch (JSONException e) {
			LOG.error("Unable to parse JSON results", e);
		}
		return uris;

	}

	/*
	 * adds a constraint on the characteristics based on jstree selection
	 */
	@Override
	public void visit(final Query query, final Request request) {
		if (!query.hasVariable(VAR_NS + "element")) {
			return;
		}
		JSONArray characteristics = (JSONArray)request.getParam("characteristic");
		if (characteristics == null) {
			return;
		}
		final Variable element = query.getVariable(QUERY_NS + "element");
		UnionComponent union = query.createUnion();
		final QueryResource rdfsSubClassOf =
				query.getResource(RDFS_NS + "subClassOf");
		for(int i=0; i<characteristics.length(); i++) {
			String characteristic = characteristics.optString(i);
			List<String> subclasses = 
					queryIfTaxonomicCharacteristicCategory(request, characteristic);
			if(subclasses.size() == 0) {
				continue;
			}
			for(String uri : subclasses) {
				union.getUnionComponent(union.size())
					.addPattern(element, rdfsSubClassOf, query.getResource(uri));
			}
		}
		if(union.size() > 0) {
			NamedGraphComponent graph = query.getNamedGraph(CUAHSI_NS);
			graph.addGraphComponent(union);
		}
	}

	@Override
	public String getName() {
		return "Characteristics";
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

	/**
	 * Queries for measurements for a given site, characteristic, and optional test.
	 * @param request Client request object
	 * @return
	 */
	@QueryMethod
	public String queryForSiteMeasurements(Request request) {
		final String siteUri = (String) request.getParam("uri");
		if (siteUri == null) {
			return "{\"error\":\"No uri parameter supplied\"}";
		}

		final String chemicalString = (String) request
				.getParam("characteristic");
		if (chemicalString == null) {
			return "{\"error\":\"No chemical parameter supplied\"}";
		}
		final String test = (String) request.getParam("TestsForCharacteristic");

		final Query query = config.getQueryFactory().newQuery(Type.SELECT);

		query.setNamespace("pol", POL_NS);
		query.setNamespace("xsd", XSD_NS);

		// Variables
		final Variable element = query.getVariable(VAR_NS + "element");
		final Variable permit = query.getVariable(VAR_NS + "permit");
		final Variable value = query.getVariable(VAR_NS + "value");
		final Variable unit = query.getVariable(VAR_NS + "unit");
		final Variable time = query.getVariable(VAR_NS + "time");
		final Variable measurement = query.getVariable(VAR_NS + "measurement");
		final Variable supers = query.getVariable(VAR_NS + "supers");
		final Variable supers2 = query.getVariable(VAR_NS + "supers2");
		final Variable op = query.getVariable(VAR_NS + "op");
		final Variable dt = query.getVariable(VAR_NS + "dt");
		final Variable bn = query.createBlankNode();
		final Variable res = query.getVariable(VAR_NS + "res");
		final Variable cls = query.getVariable(VAR_NS + "cls");
		final Variable p = query.getVariable(VAR_NS + PROP_VAR);
		final Variable limit = query.getVariable(VAR_NS + "limit");
		final Variable supers3 = query.getVariable(VAR_NS + "supers3");
		final Variable list = query.getVariable(VAR_NS + "list");

		final Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(element);
		vars.add(permit);
		vars.add(value);
		vars.add(unit);
		vars.add(time);
		vars.add(measurement);
		vars.add(op);
		vars.add(limit);
		query.setVariables(vars);

		// Resources
		final QueryResource site = query.getResource(siteUri);
		final QueryResource chemical = query.getResource(chemicalString);
		final QueryResource polHasMeasurement = query.getResource(POL_NS
				+ "hasMeasurement");
		final QueryResource polHasPermit = query.getResource(POL_NS
				+ "hasPermit");
		final QueryResource polHasCharacteristic = query.getResource(POL_NS
				+ "hasCharacteristic");
		final QueryResource polHasValue = query
				.getResource(POL_NS + "hasValue");
		final QueryResource unitHasUnit = query
				.getResource(UNIT_NS + "hasUnit");
		final QueryResource timeInXSDDateTime = query.getResource(TIME_NS
				+ "inXSDDateTime");
		final QueryResource propPath = query
				.createPropertyPath("rdf:rest*/rdf:first");
		final QueryResource owlOnProperty = query.getResource(OWL_NS
				+ "onProperty");
		final QueryResource owlHasValue = query
				.getResource(OWL_NS + "hasValue");
		final QueryResource owlSomeValuesFrom = query.getResource(OWL_NS
				+ "someValuesFrom");
		final QueryResource owlWithRestrictions = query.getResource(OWL_NS
				+ "withRestrictions");
		// OptionalComponent optional = query.createOptional();
		final QueryResource polHasLimitValue = query.getResource(POL_NS
				+ "hasLimitValue");
		final QueryResource propPathReverseList = query
				.createPropertyPath("^rdf:first/(^rdf:rest)*");
		final QueryResource propPathReverseIntersection = query
				.createPropertyPath("^owl:intersectionOf");
		final QueryResource polTestType = query.getResource(POL_NS + "test_type");
		final Variable charTest = query.createVariable(QUERY_NS + "test");

		query.addPattern(site, polHasMeasurement, measurement);
		query.addPattern(measurement, polHasCharacteristic, chemical);
		if(test != null && !test.isEmpty()) {
			query.addPattern(measurement, polTestType, charTest);
			query.addFilter("regex(str(?test), \""+test+"\")");
		}
		query.addPattern(measurement, polHasValue, value);
		query.addPattern(measurement, unitHasUnit, unit);
		query.addPattern(measurement, timeInXSDDateTime, time);

		// limits based on ontology
		OptionalComponent optional = query.createOptional();
		query.addGraphComponent(optional);
		optional.addPattern(supers, owlOnProperty, polHasCharacteristic);
		optional.addPattern(supers, owlHasValue, chemical);
		optional.addPattern(supers, propPathReverseList, list);
		optional.addPattern(list, propPathReverseIntersection, cls);
		optional.addPattern(list, propPath, supers2);
		optional.addPattern(list, propPath, supers3);
		optional.addPattern(supers2, owlOnProperty, polHasValue);
		optional.addPattern(supers2, owlSomeValuesFrom, dt);
		optional.addPattern(supers3, owlOnProperty, unitHasUnit);
		optional.addPattern(supers3, owlHasValue, unit);
		optional.addPattern(dt, owlWithRestrictions, res);
		optional.addPattern(res, propPath, bn);
		optional.addPattern(bn, p, limit);
		optional.addFilter("datatype(?limit) = xsd:decimal");

		addOpMatch(query, optional, "xsd:minInclusive", "<=", op);
		addOpMatch(query, optional, "xsd:maxInclusive", ">=", op);
		addOpMatch(query, optional, "xsd:minExclusive", "<", op);
		addOpMatch(query, optional, "xsd:maxExclusive", ">", op);

		// limits based on epa vocab (i.e. pol:hasLimitValue)
		optional = query.createOptional();
		query.addGraphComponent(optional);
		optional.addPattern(measurement, polHasPermit, permit);
		optional.addPattern(measurement, polHasLimitValue, limit);

		optional = query.createOptional();
		query.addGraphComponent(optional);

		query.addOrderBy(time, SortType.ASC);

		return config.getQueryExecutor(request).accept("application/json")
				.executeLocalQuery(query);
	}

	protected void addOpMatch(final Query query,
			final GraphComponentCollection graph, final String xsdOp,
			final String mathOp, final Variable mathVar) {
		OptionalComponent optional = query.createOptional();
		graph.addGraphComponent(optional);
		optional.addFilter("?" + PROP_VAR + " = " + xsdOp);
		optional.addBind("\"" + mathOp + "\"", mathVar);
	}

	/**
	 * Retrieves a list of tests from the model for a given site and characteristic.
	 * @param request Client request object
	 * @return
	 */
	@QueryMethod
	public String getTestsForCharacteristic(final Request request) {
		final String siteUri = (String) request.getParam("uri");
		final String characteristicUri = (String) request
				.getParam("visualizedCharacteristic");

		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		final QueryVariableUtils var = new QueryVariableUtils(query);

		// prevents the time module from modifying the query...
		var.time();

		final QueryResource hasCharacteristic = query.getResource(POL_NS
				+ "hasCharacteristic");
		final QueryResource characteristic = query
				.getResource(characteristicUri);
		final QueryResource site = query.getResource(siteUri);
		final QueryResource hasPermit = query.getResource(POL_NS + "hasPermit");

		final Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(var.test());
		query.setVariables(vars);
		query.setDistinct(true);

		String stateUri = getStateURI(request,
				(String) request.getParam("state"));
		List<String> graphs = retrieveStateGraphsForSource(request, stateUri,
				"http://sparql.tw.rpi.edu/source/epa-gov");
		if (graphs.size() == 2) {
			String measuresUri = null;
			String sitesUri = null;
			for (int i = 0; i < graphs.size(); i++) {
				if (graphs.get(i).contains("measurement")) {
					measuresUri = graphs.get(i);
				} else if (graphs.get(i).contains("facilities")
						|| graphs.get(i).contains("foia-")) {
					sitesUri = graphs.get(i);
				}
			}
			if (measuresUri == null || sitesUri == null) {
				return "{\"error\": \"Unable to find a measurements graph for the selected region.\"}";
			}

			final Pattern converterPattern = Pattern
					.compile("(.*)/source/([^/]*)/dataset/([^/]*)/.*");
			final Matcher matcher = converterPattern.matcher(measuresUri);
			matcher.find();
			String propUri = matcher.group(1);
			propUri += "/source/";
			propUri += matcher.group(2);
			propUri += "/dataset/";
			propUri += matcher.group(3);
			propUri += "/vocab/enhancement/1/test_type";

			final QueryResource test_typeLocal = query.getResource(propUri);
			final QueryResource test_type = query.getResource(POL_NS
					+ "test_type");

			NamedGraphComponent sites = query.getNamedGraph(sitesUri);
			sites.addPattern(site, hasPermit, var.permit());
			NamedGraphComponent named = query.getNamedGraph(measuresUri);
			named.addPattern(var.measurement(), hasCharacteristic, characteristic);
			named.addPattern(var.measurement(), hasPermit, var.permit());
			UnionComponent union = query.createUnion();
			named.addGraphComponent(union);
			union.getUnionComponent(0).addPattern(var.measurement(), test_type,
					var.test());
			union.getUnionComponent(1).addPattern(var.measurement(), test_typeLocal,
					var.test());
		} else {
			return "{\"error\": \"Unable to find a measurements graph for the selected region.\"}";
		}

		String results = config.getQueryExecutor(request)
				.accept("application/json").execute(query);
		List<String> testUris = processUriList(results);
		JSONArray response = new JSONArray();
		for (String i : testUris) {
			if (i.contains("#")) {
				String[] parts = i.split("#");
				response.put(parts[1]);
			} else if (i.contains("http://")) {
				String[] parts = i.split("/");
				response.put(parts[parts.length - 1]);
			} else {
				response.put(i);
			}
		}

		return response.toString();
	}

	// move QueryUtils to common package and remove this block later...
	public static final String INSTANCE_HUB_STATES = "http://logd.tw.rpi.edu/source/twc-rpi-edu/dataset/instance-hub-us-states-and-territories/version/2011-Apr-09";
	public static final String QUERY_NS = "http://aquarius.tw.rpi.edu/projects/semantaqua/data-source/query-variable/";
	public static final String RESULTS_BLOCK = "results";
	public static final String STATE_VAR = "state";
	public static final String DC_NS = "http://purl.org/dc/terms/";
	public static final String LOGD_ENDPOINT = "http://logd.tw.rpi.edu/sparql?output=sparqljson";
	public static final String VALUE = "value";
	public static final String GRAPH_VAR = "graph";
	public static final String SEMANTECO_METADATA = "http://sparql.tw.rpi.edu/semanteco/data-source";
	public static final String SIOC_NS = "http://rdfs.org/sioc/ns#";
	public static final String SOURCE_VAR = "source";

	protected final String getStateURI(final Request request, final String state) {
		LOG.trace("retrieveStateGraphsForSource");
		String stateUri = null;

		Query query = config.getQueryFactory().newQuery(Type.SELECT);
		query.setVariables(null);
		NamedGraphComponent graph = query.getNamedGraph(INSTANCE_HUB_STATES);
		QueryResource stateVar = query.getVariable(QUERY_NS + STATE_VAR);
		QueryResource identifier = query.getResource(DC_NS + "identifier");
		graph.addPattern(stateVar, identifier, state, null);

		// execute query
		String results = config.getQueryExecutor(request).execute(
				LOGD_ENDPOINT, query);
		if (results != null) {
			try {
				JSONObject response = new JSONObject(results);
				if (response.getJSONObject(RESULTS_BLOCK) != null
						&& response.getJSONObject(RESULTS_BLOCK).getJSONArray(
								BINDINGS) != null
						&& response.getJSONObject(RESULTS_BLOCK)
								.getJSONArray(BINDINGS).length() > 0) {
					JSONArray bindings = response.getJSONObject(RESULTS_BLOCK)
							.getJSONArray(BINDINGS);
					for (int i = 0; i < bindings.length(); i++) {
						JSONObject binding = bindings.getJSONObject(i);
						if (binding.getJSONObject(STATE_VAR) != null
								&& binding.getJSONObject(STATE_VAR).getString(
										VALUE) != null) {
							stateUri = binding.getJSONObject(STATE_VAR)
									.getString(VALUE);
							break;
						}
					}
				}
			} catch (JSONException e) {
				LOG.error("Could not parse JSON results", e);
			}
		}
		return stateUri;
	}

	/**
	 * Retrieves a list of graphs from {@link #SEMANTECO_METADATA} related to
	 * the specified state and source.
	 * 
	 * @param state
	 *            State uri in instance hub
	 * @param source
	 *            Source entity, e.g. http://sparql.tw.rpi.edu/source/epa-gov
	 * @return List of URIs representing graphs in the SPARQL endpoint
	 */
	protected List<String> retrieveStateGraphsForSource(final Request request,
			final String state, final String source) {
		LOG.trace("retrieveStateGraphsForSource");
		final List<String> graphs = new ArrayList<String>();

		// get graphs from sparql.tw.rpi.edu related to (stateUri, source)
		Query query = config.getQueryFactory().newQuery(Type.SELECT);
		query.setVariables(null);
		NamedGraphComponent graph = query.getNamedGraph(SEMANTECO_METADATA);
		QueryResource graphVar = query.getVariable(QUERY_NS + GRAPH_VAR);
		QueryResource topicProp = query.getResource(SIOC_NS + "topic");
		QueryResource sourceProp = query.getResource(DC_NS + SOURCE_VAR);
		graph.addPattern(graphVar, topicProp, query.getResource(state));
		graph.addPattern(graphVar, sourceProp, query.getResource(source));

		// execute query
		String results = config.getQueryExecutor(request)
				.accept("application/json").execute(query);
		graphs.addAll(processUriList(results));
		return graphs;
	}

	/**
	 * Processes the results of a SPARQL query into a list of entries of the
	 * first variable in the SELECT statement
	 * 
	 * @param sparqlJson
	 *            JSON results for a SPARQL query
	 * @return
	 */
	protected final List<String> processUriList(final String sparqlJson) {
		LOG.trace("processUriList");

		List<String> uris = new ArrayList<String>();
		try {
			JSONObject results = new JSONObject(sparqlJson);
			JSONArray vars = results.getJSONObject("head").getJSONArray("vars");
			String var = vars.getString(0);
			results = results.getJSONObject("results");
			JSONArray bindings = results.getJSONArray("bindings");
			for (int i = 0; i < bindings.length(); i++) {
				try {
					JSONObject binding = bindings.getJSONObject(i);
					uris.add(binding.getJSONObject(var).getString("value"));
				} catch (Exception e) {
					LOG.warn("Unable to process binding in result", e);
				}
			}
		} catch (Exception e) {
			LOG.warn("Unable to retrieve URI list from SPARQL", e);
		}
		return uris;
	}

	/**
	 * Queries the graph for all of the characteristics present. Expects the
	 * zip, state, county, and site parameters.
	 * 
	 * @param request
	 *            Object encapsulating a RESTful request
	 * @return A JSON-encoded object containing a success code or a SPARQL/JSON
	 *         result.
	 */
	@QueryMethod
	public String getCharacteristicsForSite(final Request request) {
		if (request.getParam("uri") == null) {
			LOG.error("Expected parameter site missing in REST call");
			return FAILURE;
		}
		if (!(request.getParam("uri") instanceof String)) {
			LOG.error("Expected a single site as a string");
			return FAILURE;
		}

		final Query query = config.getQueryFactory().newQuery(Type.SELECT);

		final QueryResource polHasMeasurement = query.getResource(POL_NS
				+ "hasMeasurement");
		final QueryResource polHasCharacteristic = query.getResource(POL_NS
				+ "hasCharacteristic");
		final QueryResource rdfsLabel = query.getResource(RDFS_NS + "label");
		final QueryResource site = query.getResource((String) request
				.getParam("uri"));

		final Variable measure = query.getVariable(VAR_NS + "measure");
		final Variable element = query.getVariable(VAR_NS + "element");
		final Variable label = query.getVariable(VAR_NS + "label");

		query.addPattern(site, polHasMeasurement, measure);
		query.addPattern(measure, polHasCharacteristic, element);
		OptionalComponent optional = query.createOptional();
		optional.addPattern(element, rdfsLabel, label);

		return config.getQueryExecutor(request).executeLocalQuery(query);
	}

}
