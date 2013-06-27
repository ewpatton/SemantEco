package edu.rpi.tw.escience.characteristics;

import static edu.rpi.tw.escience.semanteco.query.Query.VAR_NS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
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

public class CharacteristicsModule implements Module {
	private static final String POL_NS = "http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#";
	private static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
	private static final String FAILURE = "{\"success\":false}";
	private static final String BINDINGS = "bindings";
	private static final String XSD_NS = "http://www.w3.org/2001/XMLSchema#";
	private static final String UNIT_NS = "http://sweet.jpl.nasa.gov/2.1/reprSciUnits.owl#";
	private static final String TIME_NS = "http://www.w3.org/2006/time#";
	private static final String OWL_NS = "http://www.w3.org/2002/07/owl#";
	private static final String PROP_VAR = "p";

	private ModuleConfiguration config = null;
	private static final Logger log = Logger
			.getLogger(CharacteristicsModule.class);

	@Override
	public void visit(final Model model, final Request request,
			final Domain domain) {
	}

	@Override
	public void visit(final SemantEcoUI ui, final Request request) {
		// TODO add resources to display
		Resource res = null;
		res = config.getResource("characteristicHierarchy.js");
		Resource res2 = config.getResource("characteristicHierarchy.jsp");
		ui.addScript(res);
		ui.addFacet(res2);
	}

	@Override
	public void visit(final OntModel model, final Request request,
			final Domain domain) {
		// model.read(CHARACTERISTIC_NS);
	}

	@QueryMethod
	public String queryIfTaxonomicCategoryForJstree(Request request)
			throws JSONException {

		String candidateTaxonomicCategory = (String) request
				.getParam("queryIfCharacteristicTaxonomicCategoryForJstree");
		request.getLogger().error(
				"json object is: " + candidateTaxonomicCategory);
		// String candidateTaxonomicCategoryString =
		// candidateTaxonomicCategory.toString();
		if (candidateTaxonomicCategory == null) {
			return null;
		} else {
			final Query query = config.getQueryFactory().newQuery(Type.SELECT);
			final NamedGraphComponent graph = query
					.getNamedGraph("http://was.tw.rpi.edu/all-characteristics-cuahsi-ontology2");
			// final NamedGraphComponent graph =
			// query.getNamedGraph("http://was.tw.rpi.edu/ebird-data");
			final QueryResource subClassOf = query.getResource(RDFS_NS
					+ "subClassOf");
			final Variable characteristicVariable = query.getVariable(VAR_NS
					+ "characteristic");
			final QueryResource addedSpecies = query
					.getResource(candidateTaxonomicCategory);
			graph.addPattern(characteristicVariable, subClassOf, addedSpecies);
			String responseStr = FAILURE;
			String resultStr = config.getQueryExecutor(request)
					.accept("application/json").execute(query);
			log.debug("Results: " + resultStr);
			if (resultStr == null) {
				return responseStr;
			}
			try {
				JSONObject results = new JSONObject(resultStr);
				JSONObject response = new JSONObject();
				JSONArray data = new JSONArray();
				response.put("success", true);
				response.put("data", data);
				results = results.getJSONObject("results");
				JSONArray bindings = results.getJSONArray(BINDINGS);
				for (int j = 0; j < bindings.length(); j++) {
					JSONObject binding = bindings.getJSONObject(j);
					String speciesId = binding.getJSONObject("characteristic")
							.getString("value");
					JSONObject mapping = new JSONObject();
					mapping.put("characteristic", speciesId);
					data.put(mapping);
				}
				responseStr = response.toString();
			} catch (JSONException e) {
				log.error("Unable to parse JSON results", e);
			}
			return responseStr;
		}
	}

	@QueryMethod
	public String queryCharacteristicsTaxonomyRoots(Request request)
			throws IOException, JSONException {
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		// Variables
		final Variable id = query.getVariable(VAR_NS + "child");
		final Variable label = query.getVariable(VAR_NS + "label");
		final Variable parent = query.getVariable(VAR_NS + "parent");
		// URIs
		final QueryResource subClassOf = query.getResource(RDFS_NS
				+ "subClassOf");
		final QueryResource hasLabel = query.getResource(RDFS_NS + "label");
		final QueryResource owlTaxonomy = query
				.getResource("http://www.w3.org/2002/07/owl#Thing");

		// build query
		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(id);
		vars.add(label);
		vars.add(parent);
		query.setVariables(vars);
		// query.addPattern(site, inDataSet, dataSet);
		final NamedGraphComponent graph = query
				.getNamedGraph("http://was.tw.rpi.edu/all-characteristics-cuahsi-ontology2");
		graph.addPattern(id, subClassOf, parent);
		graph.addPattern(id, subClassOf, owlTaxonomy);

		graph.addPattern(id, hasLabel, label);
		// get only the subclasses of the subclasses of OWL thing
		String responseStr = FAILURE;

		String resultStr = config.getQueryExecutor(request)
				.accept("application/json").execute(query);
		// //return
		// config.getQueryExecutor(request).accept("application/json").execute(query);
		log.debug("Results: " + resultStr);
		if (resultStr == null) {
			return responseStr;
		}
		try {
			JSONObject results = new JSONObject(resultStr);
			JSONObject response = new JSONObject();
			JSONArray data = new JSONArray();
			response.put("success", true);
			response.put("data", data);
			String superclassId = null;
			results = results.getJSONObject("results");
			JSONArray bindings = results.getJSONArray(BINDINGS);
			for (int i = 0; i < bindings.length(); i++) {
				JSONObject binding = bindings.getJSONObject(i);
				String subclassId = binding.getJSONObject("child").getString(
						"value");
				String subclassLabel = binding.getJSONObject("label")
						.getString("value");

				try {
					superclassId = binding.getJSONObject("parent").getString(
							"value");
				} catch (Exception e) {
				}
				// if(labelStr == null) {
				// labelStr =
				// sourceUri.substring(sourceUri.lastIndexOf('/')+1).replace('-',
				// '.');
				// }
				JSONObject mapping = new JSONObject();
				mapping.put("id", subclassId);
				mapping.put("label", subclassLabel);
				mapping.put("parent", superclassId);
				data.put(mapping);
			}
			responseStr = response.toString();
		} catch (JSONException e) {
			log.error("Unable to parse JSON results", e);
		}
		return responseStr;
	}

	@QueryMethod
	public String queryCharacteristicsTaxonomySubClasses(Request request)
			throws IOException, JSONException {

		String classRequiresSubclassesString = (String) request
				.getParam("queryCharacteristicsTaxonomySubClasses");
		if (classRequiresSubclassesString == null) {
			return null;
		}

		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		// Variables
		final Variable id = query.getVariable(VAR_NS + "child");
		final Variable label = query.getVariable(VAR_NS + "label");
		final Variable parent = query.getVariable(VAR_NS + "parent");
		// URIs
		final QueryResource subClassOf = query.getResource(RDFS_NS
				+ "subClassOf");
		final QueryResource hasLabel = query.getResource(RDFS_NS + "label");

		// final QueryResource birdTaxonomy =
		// query.getResource("http://ebird#birdTaxonomy");
		final QueryResource classRequiresSubclasses = query
				.getResource(classRequiresSubclassesString);

		// build query
		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(id);
		vars.add(label);
		vars.add(parent);
		query.setVariables(vars);
		// query.addPattern(site, inDataSet, dataSet);
		final NamedGraphComponent graph = query
				.getNamedGraph("http://was.tw.rpi.edu/all-characteristics-cuahsi-ontology2");
		graph.addPattern(id, subClassOf, parent);
		graph.addPattern(id, subClassOf, classRequiresSubclasses);

		graph.addPattern(id, hasLabel, label);
		// get only the subclasses of the subclasses of OWL thing
		String responseStr = FAILURE;

		String resultStr = config.getQueryExecutor(request)
				.accept("application/json").execute(query);
		// //return
		// config.getQueryExecutor(request).accept("application/json").execute(query);
		log.debug("Results: " + resultStr);
		if (resultStr == null) {
			return responseStr;
		}
		try {
			JSONObject results = new JSONObject(resultStr);
			JSONObject response = new JSONObject();
			JSONArray data = new JSONArray();
			response.put("success", true);
			response.put("data", data);
			String superclassId = null;
			results = results.getJSONObject("results");
			JSONArray bindings = results.getJSONArray(BINDINGS);
			for (int i = 0; i < bindings.length(); i++) {
				JSONObject binding = bindings.getJSONObject(i);
				String subclassId = binding.getJSONObject("child").getString(
						"value");
				String subclassLabel = binding.getJSONObject("label")
						.getString("value");

				try {
					superclassId = binding.getJSONObject("parent").getString(
							"value");
				} catch (Exception e) {
				}
				// if(labelStr == null) {
				// labelStr =
				// sourceUri.substring(sourceUri.lastIndexOf('/')+1).replace('-',
				// '.');
				// }
				JSONObject mapping = new JSONObject();
				mapping.put("id", subclassId);
				mapping.put("label", subclassLabel);
				mapping.put("parent", superclassId);
				data.put(mapping);
			}
			responseStr = response.toString();
		} catch (JSONException e) {
			log.error("Unable to parse JSON results", e);
		}
		return responseStr;
	}

	// first check ifg bbbq state is null for chemical
	// new Query().findGraphComponentsWithPattern(?measurement,
	// pol:hasCharacteristic, null)
	// returns a list of graphComponent. should be a singleon list, check on
	// what it returns when empty
	// if not empty, graph.addpattern();
	public String queryIfTaxonomicCharacteristicCategory(Request request,
			String characteristicInArray) {
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
		String responseStr = FAILURE;
		String resultStr = config.getQueryExecutor(request)
				.accept("application/json").execute(query);
		log.debug("Results: " + resultStr);
		if (resultStr == null) {
			return responseStr;
		}
		try {
			JSONObject results = new JSONObject(resultStr);
			JSONObject response = new JSONObject();
			JSONArray data = new JSONArray();
			response.put("success", true);
			response.put("data", data);
			results = results.getJSONObject("results");
			JSONArray bindings = results.getJSONArray(BINDINGS);
			for (int j = 0; j < bindings.length(); j++) {
				JSONObject binding = bindings.getJSONObject(j);
				String speciesId = binding.getJSONObject("characteristic")
						.getString("value");
				JSONObject mapping = new JSONObject();
				mapping.put("characteristic", speciesId);
				data.put(mapping);
			}
			responseStr = response.toString();
		} catch (JSONException e) {
			log.error("Unable to parse JSON results", e);
		}
		return responseStr;

	}

	public void visit(final Query query, final Request request) {

		/*
		 * adds a constraint on the characteristics based on jstree selection
		 */

		if (!query.hasVariable(VAR_NS + "element")) {

			return;
		}

		final Variable element = query.getVariable(QUERY_NS + "element");
		if (request.getParam("characteristic") == null) {
			return;
		}

		if (((JSONArray) request.getParam("characteristic")).length() > 1) {

			JSONArray characteristicParams = (JSONArray) request
					.getParam("characteristic");
			final UnionComponent union = query.createUnion();
			final NamedGraphComponent graph2 = query
					.getNamedGraph("http://was.tw.rpi.edu/all-characteristics-cuahsi-ontology2");
			graph2.addGraphComponent(union);
			GraphComponentCollection coll;

			for (int i = 0; i < characteristicParams.length(); i++) {
				String characteristicInArray = null;
				try {
					characteristicInArray = characteristicParams.getString(i);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String resultStr = queryIfTaxonomicCharacteristicCategory(
						request, characteristicInArray);
				if (resultStr != "FAILURE") {
					request.getLogger().error(resultStr);
					JSONObject results = null;
					try {
						results = new JSONObject(resultStr);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					JSONArray data = null;
					try {
						data = (JSONArray) results.get("data");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (data.length() > 0) {
						final QueryResource addedCharacteristic = query
								.getResource(characteristicInArray);
						final QueryResource subClassOf = query
								.getResource(RDFS_NS + "subClassOf");
						coll = union.getUnionComponent(i);
						coll.addPattern(element, subClassOf,
								addedCharacteristic);
					} else {
						request.getLogger()
								.error("(no results for queryIfTaxonomicCharacteristicCategory)");
						// final QueryResource addedSpecies =
						// query.getResource(characteristicInArray);
						// coll = union.getUnionComponent(i);
						// coll.addPattern(addedSpecies, hasLabel,
						// scientificName);
						// graph.addPattern(measurement, hasCharacteristic,
						// characteristicResource);

					}
				} else {
					request.getLogger()
							.error("(failure to queryIfTaxonomicCharacteristicCategory)");
				}

			}
		} else if (((JSONArray) request.getParam("characteristic")).length() == 1) {
			request.getLogger().error("(got to else if where species == 1)");

			JSONArray characteristicParams = (JSONArray) request
					.getParam("characteristic");
			String characteristicInArray = null;
			try {
				characteristicInArray = characteristicParams.getString(0);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String resultStr = queryIfTaxonomicCharacteristicCategory(request,
					characteristicInArray);
			if (resultStr != "FAILURE") {
				request.getLogger().error("subclassOf results: " + resultStr);
				JSONObject results = null;
				try {
					results = new JSONObject(resultStr);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				JSONArray data = null;
				try {
					data = (JSONArray) results.get("data");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// data.length is > 0 then there were positive results, so now
				// we can ask for subclasses of selection
				request.getLogger().error("data.length : " + data.length());
				final NamedGraphComponent graph2 = query
						.getNamedGraph("http://was.tw.rpi.edu/all-characteristics-cuahsi-ontology2");
				final QueryResource addedCharacteristic = query
						.getResource(characteristicInArray);
				if (data.length() > 0) {
					// just use the pattern species subClassOf speciesSelection
					final QueryResource subClassOf = query.getResource(RDFS_NS
							+ "subClassOf");

					request.getLogger().error(
							"addedCharacteristic: "
									+ addedCharacteristic.toString());
					request.getLogger().error("element: " + element.toString());
					request.getLogger().error(
							"subclassof: " + subClassOf.toString());

					graph2.addPattern(element, subClassOf, addedCharacteristic);
				} else {
					request.getLogger()
							.error("(no results for queryIfTaxonomicCharacteristicCategory)");
					// graph2.addPattern(addedSpecies, hasLabel,
					// scientificName);
				}
			}
		}
	}

	@QueryMethod
	public String queryCharacteristicTaxonomy(Request request)
			throws IOException, JSONException {
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		// Variables
		final Variable id = query.getVariable(VAR_NS + "child");
		final Variable label = query.getVariable(VAR_NS + "label");
		final Variable parent = query.getVariable(VAR_NS + "parent");
		// URIs
		final QueryResource subClassOf = query.getResource(RDFS_NS
				+ "subClassOf");
		final QueryResource hasLabel = query.getResource(RDFS_NS + "label");
		// build query
		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(id);
		vars.add(label);
		vars.add(parent);
		query.setVariables(vars);
		final NamedGraphComponent graph = query
				.getNamedGraph("http://was.tw.rpi.edu/all-characteristics-cuahsi-ontology2");
		graph.addPattern(id, subClassOf, parent);
		graph.addPattern(id, hasLabel, label);
		String responseStr = FAILURE;

		String resultStr = config.getQueryExecutor(request)
				.accept("application/json").execute(query);
		// //return
		// config.getQueryExecutor(request).accept("application/json").execute(query);
		log.debug("Results: " + resultStr);
		if (resultStr == null) {
			return responseStr;
		}
		try {
			JSONObject results = new JSONObject(resultStr);
			JSONObject response = new JSONObject();
			JSONArray data = new JSONArray();
			response.put("success", true);
			response.put("data", data);
			String superclassId = null;
			results = results.getJSONObject("results");
			JSONArray bindings = results.getJSONArray(BINDINGS);
			for (int i = 0; i < bindings.length(); i++) {
				JSONObject binding = bindings.getJSONObject(i);
				String subclassId = binding.getJSONObject("child").getString(
						"value");
				String subclassLabel = binding.getJSONObject("label")
						.getString("value");

				try {
					superclassId = binding.getJSONObject("parent").getString(
							"value");
				} catch (Exception e) {
				}
				// if(labelStr == null) {
				// labelStr =
				// sourceUri.substring(sourceUri.lastIndexOf('/')+1).replace('-',
				// '.');
				// }
				JSONObject mapping = new JSONObject();
				mapping.put("id", subclassId);
				mapping.put("label", subclassLabel);
				mapping.put("parent", superclassId);
				data.put(mapping);
			}
			responseStr = response.toString();
		} catch (JSONException e) {
			log.error("Unable to parse JSON results", e);
		}
		return responseStr;

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
		log.trace("retrieveStateGraphsForSource");
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
				log.error("Could not parse JSON results", e);
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
		log.trace("retrieveStateGraphsForSource");
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
		log.trace("processUriList");

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
					log.warn("Unable to process binding in result", e);
				}
			}
		} catch (Exception e) {
			log.warn("Unable to retrieve URI list from SPARQL", e);
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
			log.error("Expected parameter site missing in REST call");
			return FAILURE;
		}
		if (!(request.getParam("uri") instanceof String)) {
			log.error("Expected a single site as a string");
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
