package edu.rpi.tw.escience.characteristics;

import static edu.rpi.tw.escience.waterquality.query.Query.VAR_NS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.Module;
import edu.rpi.tw.escience.waterquality.ModuleConfiguration;
import edu.rpi.tw.escience.waterquality.QueryMethod;
import edu.rpi.tw.escience.waterquality.Request;
import edu.rpi.tw.escience.waterquality.Resource;
import edu.rpi.tw.escience.waterquality.SemantAquaUI;
import edu.rpi.tw.escience.waterquality.query.GraphComponentCollection;
import edu.rpi.tw.escience.waterquality.query.OptionalComponent;
import edu.rpi.tw.escience.waterquality.query.NamedGraphComponent;
import edu.rpi.tw.escience.waterquality.query.UnionComponent;
//import edu.rpi.tw.escience.waterquality.dataprovider.WaterDataProviderModule;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.QueryResource;
import edu.rpi.tw.escience.waterquality.query.Variable;
import edu.rpi.tw.escience.waterquality.query.Query.SortType;
import edu.rpi.tw.escience.waterquality.query.Query.Type;

public class CharacteristicsModule implements Module {
	private static final String CHARACTERISTIC_NS = "http://was.tw.rpi.edu/semanteco/water/cuashiCharacteristics.owl#";
	private static final String POL_NS = "http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#";
	private static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
	private static final String FAILURE = "{\"success\":false}";
	private static final String BINDINGS = "bindings";
	private static final String XSD_NS = "http://www.w3.org/2001/XMLSchema#";
	private static final String UNIT_NS = "http://sweet.jpl.nasa.gov/2.1/reprSciUnits.owl#";
	private static final String TIME_NS = "http://www.w3.org/2006/time#";
	private static final String ENHANCE_NS = "http://sparql.tw.rpi.edu/source/epa-gov/dataset/echo-measurements-ri/vocab/enhancement/1/";
	private ModuleConfiguration config = null;
	private static final Logger log = Logger.getLogger(CharacteristicsModule.class);

	//private ModuleConfiguration config = null;
	
	public void visit(Model model, Request request) {
		//DataModelBuilder builder = new DataModelBuilder(request, config);
		//builder.build(model);
	}
	
	@Override
    public void visit(final SemantAquaUI ui, final Request request) {
            // TODO add resources to display
            Resource res = null;
            res = config.getResource("characteristicHierarchy.js");
            Resource res2 = config.getResource("characteristicHierarchy.jsp");
            ui.addScript(res);
            ui.addFacet(res2);
    }
	
	
	@Override
	public void visit(final OntModel model, final Request request) {
		//model.read(CHARACTERISTIC_NS);
	}
	
	//first check ifg bbbq state is null for chemical
	//new Query().findGraphComponentsWithPattern(?measurement, pol:hasCharacteristic, null)
	//returns a list of graphComponent. should be a singleon list, check on what it returns when empty
    //if not empty, graph.addpattern();
	
	
	
	
	public String queryIfTaxonomicCharacteristicCategory(Request request, String characteristicInArray){
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);	
		final NamedGraphComponent graph = query.getNamedGraph("http://was.tw.rpi.edu/characteristics-cuahsi-ontology");
		//final NamedGraphComponent graph = query.getNamedGraph("http://was.tw.rpi.edu/ebird-data");
		final QueryResource subClassOf = query.getResource(RDFS_NS + "subClassOf");
		final Variable speciesVariable = query.getVariable(VAR_NS + "characteristic");	
		final QueryResource addedCharacteristic = query.getResource(characteristicInArray);
		graph.addPattern(speciesVariable, subClassOf , addedCharacteristic);				
		String responseStr = FAILURE;
		String resultStr = config.getQueryExecutor(request).accept("application/json").execute(query);	
			log.debug("Results: "+resultStr);
			if(resultStr == null) {
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
				for(int j=0;j <bindings.length();j++) {
					JSONObject binding = bindings.getJSONObject(j);
					String speciesId = binding.getJSONObject("characteristic").getString("value");
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
		
		final Variable element = query.getVariable(QUERY_NS+"element");
		String singletonCharacteristic ="";
		
		if(request.getParam("characteristic") != null && ((JSONArray) request.getParam("characteristic")).length() > 1  ){
			
		JSONArray characteristicParams = (JSONArray)request.getParam("characteristic");			
		final UnionComponent union = query.createUnion();
		final NamedGraphComponent graph2 = query.getNamedGraph("http://was.tw.rpi.edu/characteristics-cuahsi-ontology");
		graph2.addGraphComponent(union);
		GraphComponentCollection coll;
		
		for(int i = 0; i < characteristicParams.length(); i++)
		{
			String characteristicInArray = null;
			try {
				characteristicInArray = characteristicParams.getString(i);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}				
			String resultStr = queryIfTaxonomicCharacteristicCategory(request, characteristicInArray);
			if(resultStr != "FAILURE"){
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
			    	if(data.length() > 0){
			    		final QueryResource addedCharacteristic = query.getResource(characteristicInArray);
		    			final QueryResource subClassOf = query.getResource(RDFS_NS+"subClassOf");
						coll = union.getUnionComponent(i);
				        coll.addPattern(element, subClassOf, addedCharacteristic);	
			    	}
			    	else{
					    request.getLogger().error("(no results for queryIfTaxonomicCharacteristicCategory)");
					 // final QueryResource addedSpecies = query.getResource(characteristicInArray);
					//	coll = union.getUnionComponent(i);
				   //     coll.addPattern(addedSpecies, hasLabel, scientificName);	
					//	graph.addPattern(measurement, hasCharacteristic, characteristicResource);			

			    	}
			}
			else{
			    request.getLogger().error("(failure to queryIfTaxonomicCharacteristicCategory)");
			}	
			
		}
	}
		else if (((JSONArray) request.getParam("characteristic")).length() == 1){
		    request.getLogger().error("(got to else if where species == 1)");

			JSONArray characteristicParams = (JSONArray)request.getParam("characteristic");	
			String characteristicInArray = null;
			try {
				characteristicInArray = characteristicParams.getString(0);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			String resultStr = queryIfTaxonomicCharacteristicCategory(request, characteristicInArray);
			if(resultStr != "FAILURE"){
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
			    	//data.length is  > 0 then there were positive results, so now we can ask for subclasses of selection
			    request.getLogger().error("data.length : " + data.length());
			    final NamedGraphComponent graph2 = query.getNamedGraph("http://was.tw.rpi.edu/characteristics-cuahsi-ontology");
				final QueryResource addedCharacteristic = query.getResource(characteristicInArray);
			    	if(data.length() > 0){			    		
			    		//just use the pattern species subClassOf speciesSelection
		    			final QueryResource subClassOf = query.getResource(RDFS_NS+"subClassOf");

					    request.getLogger().error("addedCharacteristic: " + addedCharacteristic.toString());
					    request.getLogger().error("element: " + element.toString());
					    request.getLogger().error("subclassof: " + subClassOf.toString());

				        graph2.addPattern(element, subClassOf, addedCharacteristic);	
			    	}
			    	else{
					    request.getLogger().error("(no results for queryIfTaxonomicCharacteristicCategory)");
					//	graph2.addPattern(addedSpecies, hasLabel, scientificName);		
			    	}
				}		
		}
		else{
			//final NamedGraphComponent graph2 = query.getNamedGraph("http://was.tw.rpi.edu/ebird-taxonomy");
	//		graph2.addPattern(species, hasLabel, scientificName);		
		}		
	}
	
	
	@QueryMethod
	public String queryCharacteristicTaxonomy(Request request) throws IOException, JSONException{
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);	
		//Variables
		final Variable id = query.getVariable(VAR_NS+ "child");
		final Variable label = query.getVariable(VAR_NS+ "label");
		final Variable parent = query.getVariable(VAR_NS+ "parent");		
		//URIs
		final QueryResource subClassOf = query.getResource(RDFS_NS + "subClassOf");
		final QueryResource hasLabel = query.getResource(RDFS_NS + "label");
		//build query
		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(id);
		vars.add(label);
		vars.add(parent);
		query.setVariables(vars);
		final NamedGraphComponent graph = query.getNamedGraph("http://was.tw.rpi.edu/characteristics-cuahsi-ontology");
		graph.addPattern(id, subClassOf, parent);
		graph.addPattern(id, hasLabel, label);
		String responseStr = FAILURE;	
		
		String resultStr = config.getQueryExecutor(request).accept("application/json").execute(query);
		////return config.getQueryExecutor(request).accept("application/json").execute(query);
		log.debug("Results: "+resultStr);
		if(resultStr == null) {
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
			for(int i=0;i<bindings.length();i++) {
				JSONObject binding = bindings.getJSONObject(i);
				String subclassId = binding.getJSONObject("child").getString("value");
				String subclassLabel = binding.getJSONObject("label").getString("value");

				try {
					superclassId = binding.getJSONObject("parent").getString("value");
				}
				catch(Exception e) { }
				//if(labelStr == null) {
				//	labelStr = sourceUri.substring(sourceUri.lastIndexOf('/')+1).replace('-', '.');
				//}
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
		final String siteUri = (String)request.getParam("uri");
		if(siteUri == null) {
			return "{\"error\":\"No uri parameter supplied\"}";
		}
		
		final String chemicalString = (String)request.getParam("characteristic");
		if(chemicalString == null) {
			return "{\"error\":\"No chemical parameter supplied\"}";
		}
		
		
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		
		query.setNamespace("pol", POL_NS);
		query.setNamespace("xsd", XSD_NS);
		
		// Variables
		final Variable element = query.getVariable(VAR_NS+"element");
		final Variable permit = query.getVariable(VAR_NS+"permit");
		final Variable value = query.getVariable(VAR_NS+"value");
		final Variable unit = query.getVariable(VAR_NS+"unit");
		final Variable time = query.getVariable(VAR_NS+"time");
		final Variable measurement = query.getVariable(VAR_NS+"measurement");
		
		final Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(element);
		vars.add(permit);
		vars.add(value);
		vars.add(unit);
		vars.add(time);
		vars.add(measurement);
		
		// Resources
		final QueryResource site = query.getResource(siteUri);
		final QueryResource chemical = query.getResource(chemicalString);
		final QueryResource polHasMeasurement = query.getResource(POL_NS+"hasMeasurement");
		final QueryResource polHasPermit = query.getResource(POL_NS+"hasPermit");
		final QueryResource polHasCharacteristic = query.getResource(POL_NS+"hasCharacteristic");
		final QueryResource polHasValue = query.getResource(POL_NS+"hasValue");
		final QueryResource unitHasUnit = query.getResource(UNIT_NS+"hasUnit");
		final QueryResource timeInXSDDateTime = query.getResource(TIME_NS+"inXSDDateTime");
		
		//OptionalComponent optional = query.createOptional();
		final Variable limit = query.getVariable(VAR_NS+"limit");
		final QueryResource polHasLimitValue = query.getResource(POL_NS+"hasLimitValue");


		
		query.addPattern(site, polHasMeasurement, measurement);
		query.addPattern(measurement, polHasCharacteristic, chemical);
		query.addPattern(measurement, polHasValue, value);
		query.addPattern(measurement, unitHasUnit, unit);
		query.addPattern(measurement, timeInXSDDateTime, time);
		OptionalComponent optional = query.createOptional();
		query.addGraphComponent(optional);
		optional.addPattern(measurement, polHasPermit, permit);
		optional.addPattern(measurement, polHasLimitValue, limit);

		optional = query.createOptional();
		query.addGraphComponent(optional);

		query.addOrderBy(time, SortType.ASC);

		return config.getQueryExecutor(request).accept("application/json").executeLocalQuery(query);
	}

	@QueryMethod
	public String getTestsForCharacteristic(final Request request) {
		final String siteUri = (String)request.getParam("uri");
		final String characteristicUri = (String)request.getParam("visualizedCharacteristic");
		
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		
		// prevents the time module from modifying the query...
		query.getVariable(VAR_NS+"time");
		
		final Variable measurement = query.getVariable(VAR_NS+"measurement");
		final Variable testVar = query.getVariable(VAR_NS+"test");
		final Variable permit = query.getVariable(VAR_NS+"permit");
		final QueryResource test_type = query.getResource(ENHANCE_NS+"test_type");
		final QueryResource hasCharacteristic = query.getResource(POL_NS+"hasCharacteristic");
		final QueryResource characteristic = query.getResource(characteristicUri);
		final QueryResource site = query.getResource(siteUri);
		final QueryResource hasPermit = query.getResource(POL_NS+"hasPermit");
		
		final Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(testVar);
		query.setVariables(vars);
		query.setDistinct(true);
		
		String stateUri = getStateURI(request, (String)request.getParam("state"));
		List<String> graphs = retrieveStateGraphsForSource(request, stateUri, "http://sparql.tw.rpi.edu/source/epa-gov");
		if(graphs.size()==2) {
			String measuresUri = null;
			String sitesUri = null;
			for(int i=0;i<graphs.size();i++) {
				if(graphs.get(i).contains("measurement")) {
					measuresUri = graphs.get(i);
				}
				else if(graphs.get(i).contains("facilities")||graphs.get(i).contains("foia-")) {
					sitesUri = graphs.get(i);
				}
			}
			if(measuresUri == null || sitesUri == null) {
				return "{\"error\": \"Unable to find a measurements graph for the selected region.\"}";
			}
			NamedGraphComponent sites = query.getNamedGraph(sitesUri);
			sites.addPattern(site, hasPermit, permit);
			NamedGraphComponent named = query.getNamedGraph(measuresUri);
			named.addPattern(measurement, hasCharacteristic, characteristic);
			named.addPattern(measurement, hasPermit, permit);
			named.addPattern(measurement, test_type, testVar);
		}
		else {
			return "{\"error\": \"Unable to find a measurements graph for the selected region.\"}";
		}
		
		String results = config.getQueryExecutor(request).accept("application/json").execute(query);
		List<String> testUris = processUriList(results);
		JSONArray response = new JSONArray();
		for(String i : testUris) {
			if(i.contains("#")) {
				String[] parts = i.split("#");
				response.put(parts[1]);
			}
			else if(i.contains("http://")) {
				String[] parts = i.split("/");
				response.put(parts[parts.length-1]);
			}
			else {
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
	public static final String SEMANTAQUA_METADATA = "http://sparql.tw.rpi.edu/semanteco/data-source";
	public static final String SIOC_NS = "http://rdfs.org/sioc/ns#";
	public static final String SOURCE_VAR = "source";
	
	protected final String getStateURI(final Request request, final String state) {
		log.trace("retrieveStateGraphsForSource");
		String stateUri = null;
		
		Query query = config.getQueryFactory().newQuery(Type.SELECT);
		query.setVariables(null);
		NamedGraphComponent graph = query.getNamedGraph(INSTANCE_HUB_STATES);
		QueryResource stateVar = query.getVariable(QUERY_NS+STATE_VAR);
		QueryResource identifier = query.getResource(DC_NS+"identifier");
		graph.addPattern(stateVar, identifier, state, null);
		
		// execute query
		String results = config.getQueryExecutor(request).execute(LOGD_ENDPOINT, query);
		if(results != null) {
			try {
				JSONObject response = new JSONObject(results);
				if(response.getJSONObject(RESULTS_BLOCK) != null && 
					response.getJSONObject(RESULTS_BLOCK).getJSONArray(BINDINGS) != null &&
					response.getJSONObject(RESULTS_BLOCK).getJSONArray(BINDINGS).length() > 0) {
					JSONArray bindings = response.getJSONObject(RESULTS_BLOCK).getJSONArray(BINDINGS);
					for(int i=0;i<bindings.length();i++) {
						JSONObject binding = bindings.getJSONObject(i);
						if(binding.getJSONObject(STATE_VAR) != null && binding.getJSONObject(STATE_VAR).getString(VALUE) != null) {
							stateUri = binding.getJSONObject(STATE_VAR).getString(VALUE);
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
	 * Retrieves a list of graphs from {@link #SEMANTAQUA_METADATA} related to
	 * the specified state and source.
	 * 
	 * @param state State uri in instance hub
	 * @param source Source entity, e.g. http://sparql.tw.rpi.edu/source/epa-gov
	 * @return List of URIs representing graphs in the SPARQL endpoint
	 */
	protected List<String> retrieveStateGraphsForSource(final Request request, final String state, final String source) {
		log.trace("retrieveStateGraphsForSource");
		final List<String> graphs = new ArrayList<String>();
		
		// get graphs from sparql.tw.rpi.edu related to (stateUri, source)
		Query query = config.getQueryFactory().newQuery(Type.SELECT);
		query.setVariables(null);
		NamedGraphComponent graph = query.getNamedGraph(SEMANTAQUA_METADATA);
		QueryResource graphVar = query.getVariable(QUERY_NS+GRAPH_VAR);
		QueryResource topicProp = query.getResource(SIOC_NS+"topic");
		QueryResource sourceProp = query.getResource(DC_NS+SOURCE_VAR);
		graph.addPattern(graphVar, topicProp, query.getResource(state));
		graph.addPattern(graphVar, sourceProp, query.getResource(source));
		
		// execute query
		String results = config.getQueryExecutor(request).accept("application/json").execute(query);
		graphs.addAll(processUriList(results));
		return graphs;
	}
	
	/**
	 * Processes the results of a SPARQL query into a list of
	 * entries of the first variable in the SELECT statement
	 * @param sparqlJson JSON results for a SPARQL query
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
			for(int i=0;i<bindings.length();i++) {
				try {
					JSONObject binding = bindings.getJSONObject(i);
					uris.add(binding.getJSONObject(var).getString("value"));
				}
				catch(Exception e) {
					log.warn("Unable to process binding in result", e);
				}
			}
		}
		catch(Exception e) {
			log.warn("Unable to retrieve URI list from SPARQL", e);
		}
		return uris;
	}

}
