package edu.rpi.tw.escience.waterquality.datasource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.Module;
import edu.rpi.tw.escience.waterquality.ModuleConfiguration;
import edu.rpi.tw.escience.waterquality.QueryMethod;
import edu.rpi.tw.escience.waterquality.Resource;
import edu.rpi.tw.escience.waterquality.SemantAquaUI;
import edu.rpi.tw.escience.waterquality.query.BlankNode;
import edu.rpi.tw.escience.waterquality.query.NamedGraphComponent;
import edu.rpi.tw.escience.waterquality.query.OptionalComponent;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.QueryResource;
import edu.rpi.tw.escience.waterquality.query.Query.Type;
import edu.rpi.tw.escience.waterquality.query.Variable;
import edu.rpi.tw.escience.waterquality.util.JSONUtils;

/**
 * The Data Source Module provides the mechanisms for constructing the primary
 * graph of data used for populating the data model before reasoning occurs.
 * 
 * @author ewpatton
 *
 */
public class DataSourceModule implements Module {

	private ModuleConfiguration config = null;
	private static final String LOGD_ENDPOINT = "http://logd.tw.rpi.edu/sparql?output=sparqljson";
	private static final String SEMANTAQUA_METADATA = "http://sparql.tw.rpi.edu/semanteco/data-source";
	private static final String INSTANCE_HUB_STATES = "http://logd.tw.rpi.edu/source/twc-rpi-edu/dataset/instance-hub-us-states-and-territories/version/2011-Jun-02";
	private static final String QUERY_NS = "http://aquarius.tw.rpi.edu/projects/semantaqua/data-source/query-variable/";
	private static final String POL_NS = "http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#";
	private static final String DC_NS = "http://purl.org/dc/terms/";
	private static final String SIOC_NS = "http://rdfs.org/sioc/ns#";
	private static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
	private static final String STATE_VAR = "state";
	private static final String SOURCE_VAR = "source";
	private static final String LABEL_VAR = "label";
	private static final String RESULTS_BLOCK = "results";
	private static final String FAILURE = "{\"success\":false}";
	private Logger log = Logger.getLogger(DataSourceModule.class);
	
	/**
	 * Queries for data from the SPARQL endpoint and uses that data to
	 * populate the data model.
	 */
	@Override
	public void visit(Model model, Map<String, String> params) {
		Query query = config.getQueryFactory().newQuery(Type.CONSTRUCT);
		buildQueryObject(query, params);
		config.getQueryExecutor().execute(query, model);
	}

	@Override
	public void visit(OntModel model, Map<String, String> params) {
		// do nothing as we are simply a data provider, not an ontology provider
	}

	@Override
	public void visit(Query query, Map<String, String> params) {
		// the data source module handles all of its customization when
		// constructing the data model see visit(Model, Map)
	}

	@Override
	public void visit(SemantAquaUI ui, Map<String, String> params) {
		log.trace("visit(ui)");
		Resource res = null;
		res = config.getResource("test.js");
		ui.addScript(res);
		try {
			String responseText = "<div id=\"DataSourceFacet\" class=\"facet\">";
			String response = queryForDataSources(null);
			log.debug("Response: "+response);
			JSONObject data = new JSONObject(response);
			if(data.getBoolean("success")) {
				JSONArray sources = (JSONArray)data.get("data");
				for(int i=0;i<sources.length();i++) {
					JSONObject mapping = sources.getJSONObject(i);
					responseText += "<input name=\"source\" type=\"checkbox\" checked=\"checked\" value=\""+mapping.getString("uri")+"\" />";
					responseText += "<label for=\"source\">"+mapping.getString("label")+"</label>";
					responseText += "<br />";
				}
			}
			else {
				responseText += "<i>No data sources available</i>";
			}
			responseText += "</div>";
			res = config.generateStringResource(responseText);
			ui.addFacet(res);
		}
		catch(Exception e) {
			log.error("Unable to generate UI component", e);
		}
	}

	@Override
	public String getName() {
		return "Data Source";
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
		log.trace("setModuleConfiguration");
		this.config = config;
	}

	/**
	 * Provides an interface for other modules to query for the available
	 * data sources in the triple store.
	 * 
	 * @param params Parameters from the RESTful call
	 * @return
	 */
	@QueryMethod
	public String queryForDataSources(Map<String, String> params) {
		log.trace("queryForDataSources");
		Query query = config.getQueryFactory().newQuery();
		
		// generate variables and resources for query
		Variable source = query.createVariable(Query.VAR_NS+SOURCE_VAR);
		Variable label = query.createVariable(Query.VAR_NS+LABEL_VAR);
		QueryResource dcSource = query.getResource(DC_NS+"source");
		QueryResource rdfsLabel = query.getResource(RDFS_NS+LABEL_VAR);
		BlankNode graph = query.createBlankNode();
		
		// build query
		Set<Variable> vars = new HashSet<Variable>();
		vars.add(source);
		vars.add(label);
		query.setVariables(vars);
		query.setDistinct(true);
		NamedGraphComponent metadata = query.getNamedGraph(SEMANTAQUA_METADATA);
		metadata.addPattern(graph, dcSource, source);
		OptionalComponent optional = query.createOptional();
		metadata.addGraphComponent(optional);
		optional.addPattern(source, rdfsLabel, label);
		
		// execute query
		String responseStr = FAILURE;
		String resultStr = config.getQueryExecutor().accept("application/json").execute(query);
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
			JSONArray bindings = results.getJSONArray("bindings");
			for(int i=0;i<bindings.length();i++) {
				JSONObject binding = bindings.getJSONObject(i);
				String sourceUri = binding.getJSONObject(SOURCE_VAR).getString("value");
				String labelStr = null;
				try {
					labelStr = binding.getJSONObject(LABEL_VAR).getString("value");
				}
				catch(Exception e) { }
				if(labelStr == null) {
					labelStr = sourceUri.substring(sourceUri.lastIndexOf('/')+1).replace('-', '.');
				}
				JSONObject mapping = new JSONObject();
				mapping.put("uri", sourceUri);
				mapping.put("label", labelStr);
				data.put(mapping);
			}
			responseStr = response.toString();
		} catch (JSONException e) {
			log.error("Unable to parse JSON results", e);
		}
		return responseStr;
	}
	
	/**
	 * 
	 */
	@QueryMethod
	public String queryForPollutionData(Map<String, String> params) {
		return null;
	}
	
	/**
	 * Retrieves a list of graphs from {@link #SEMANTAQUA_METADATA} related to
	 * the specified state and source.
	 * 
	 * @param state State abbreviation, e.g. "CA", "NY"
	 * @param source Source entity, e.g. http://sparql.tw.rpi.edu/source/epa-gov
	 * @return List of URIs representing graphs in the SPARQL endpoint
	 */
	private List<String> retrieveStateGraphsForSource(final String state, final String source) {
		log.trace("retrieveStateGraphsForSource");
		List<String> graphs = new ArrayList<String>();
		String stateUri = null;
		
		/**
		 * PREFIX dc: <http://purl.org/dc/terms/>
		 * SELECT *
		 * WHERE {
		 *   GRAPH <http://logd.tw.rpi.edu/source/twc-rpi-edu/dataset/instance-hub-us-states-and-territories/version/2011-Jun-02>  {
		 *     ?s dc:identifier "CA"
		 *   }
		 * }
		 */
		Query query = config.getQueryFactory().newQuery(Type.SELECT);
		query.setVariables(null);
		NamedGraphComponent graph = query.getNamedGraph(INSTANCE_HUB_STATES);
		QueryResource stateVar = query.getVariable(QUERY_NS+STATE_VAR);
		QueryResource identifier = query.getResource(DC_NS+"identifier");
		graph.addPattern(stateVar, identifier, state, null);
		
		// execute query
		String results = config.getQueryExecutor().execute(LOGD_ENDPOINT, query);
		if(results != null) {
			JSONObject response = (JSONObject)JSONObject.stringToValue(results);
			try {
				if(response.getJSONObject(RESULTS_BLOCK) != null && 
					response.getJSONObject(RESULTS_BLOCK).getJSONArray("bindings") != null &&
					response.getJSONObject(RESULTS_BLOCK).getJSONArray("bindings").length() > 0) {
					JSONArray bindings = response.getJSONObject(RESULTS_BLOCK).getJSONArray("bindings");
					for(int i=0;i<bindings.length();i++) {
						JSONObject binding = bindings.getJSONObject(i);
						if(binding.getJSONObject(STATE_VAR) != null && binding.getJSONObject(STATE_VAR).getString("value") != null) {
							stateUri = binding.getJSONObject(STATE_VAR).getString("value");
							break;
						}
					}
				}
			} catch (JSONException e) {
				log.error("Could not parse JSON results", e);
			}
		}
		if(stateUri == null) {
			return graphs;
		}
		
		// get graphs from sparql.tw.rpi.edu related to (stateUri, source)
		query = config.getQueryFactory().newQuery(Type.SELECT);
		query.setVariables(null);
		graph = query.getNamedGraph(SEMANTAQUA_METADATA);
		QueryResource graphVar = query.getVariable(QUERY_NS+"graph");
		QueryResource topicProp = query.getResource(SIOC_NS+"topic");
		QueryResource sourceProp = query.getResource(DC_NS+"source");
		graph.addPattern(graphVar, topicProp, query.getResource(stateUri));
		graph.addPattern(graphVar, sourceProp, query.getResource(source));
		
		// execute query
		results = config.getQueryExecutor().execute(query);
		graphs.addAll(processResults(results));
		return graphs;
	}
	
	/**
	 * Processes the results from metadata graph query
	 * @param results application/sparql-results+xml encoded results from a SPARQL endpoint
	 * @return
	 */
	private Collection<String> processResults(final String results) {
		log.trace("processResults");
		Set<String> graphs = new HashSet<String>();
		try {
			ByteArrayInputStream data = new ByteArrayInputStream(results.getBytes("UTF-8"));
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(data);
			NodeList nodesToProcess = doc.getElementsByTagName("result");
			for(int i=0;i<nodesToProcess.getLength();i++) {
				Element e = (Element)nodesToProcess.item(i);
				NodeList bindings = e.getElementsByTagName("binding");
				for(int j=0;j<bindings.getLength();j++) {
					Element binding = (Element)bindings.item(j);
					if(binding.getAttribute("name").equals(STATE_VAR)) {
						graphs.add(binding.getTextContent().trim());
					}
				}
			}
		} catch (SAXException e) {
			log.error("Could not parse SPARQL results", e);
		} catch (IOException e) {
			log.error("Could not read SPARQL results", e);
		} catch (ParserConfigurationException e) {
			log.error("Parser configuration error", e);
		}
		return graphs;
	}
	
	/**
	 * Augments the Query object for the specific data source
	 * @param query
	 * @param params
	 * @param source
	 * @return
	 */
	private Query augmentQueryForSource(final Query query, final Map<String, String> params, final String source) {
		log.trace("augmentQueryForSource");
		String stateAbbr = params.get("state");
		List<String> graphs = retrieveStateGraphsForSource(stateAbbr, source);
		QueryResource site = query.getVariable(QUERY_NS+"s");
		QueryResource polHasSite = query.getResource(POL_NS+"hasSite");
		QueryResource id = query.getVariable(QUERY_NS+"id");
		for(String graph : graphs) {
			NamedGraphComponent component = query.getNamedGraph(graph);
			if(graph.contains("measurements")) {
				component.addPattern(site, polHasSite, id);
			}
		}
		return query;
	}
	
	/**
	 * Builds a Query object used for constructing a subset of the data
	 * @param query
	 * @param params
	 * @return
	 */
	private Query buildQueryObject(final Query query, final Map<String, String> params) {
		log.trace("buildQueryObject");
		String temp = params.get("sources");
		List<String> sources = null;
		if(temp != null && !temp.equals("")) {
			JSONArray arr = (JSONArray)JSONObject.stringToValue(temp);
			if(arr == null) {
				return query;
			}
			sources = JSONUtils.toList(arr);
		}
		for(String source : sources) {
			augmentQueryForSource(query, params, source);
		}
		return query;
	}

}
