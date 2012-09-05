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
import edu.rpi.tw.escience.waterquality.query.NamedGraphComponent;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.QueryResource;
import edu.rpi.tw.escience.waterquality.query.Query.Type;
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
	private static final String STATE_VAR = "state";
	private static final String RESULTS_BLOCK = "results";
	
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
	public void visit(SemantAquaUI ui) {
		Resource res = null;
		res = config.getResource("web/data-source.js");
		ui.addScript(res);
		try {
			String responseText = "<div id=\"DataSourceFacet\" class=\"facet\">";
			JSONObject data = (JSONObject)JSONObject.stringToValue(queryForDataSources(null));
			if(data.getBoolean("success")) {
				JSONArray sources = (JSONArray)data.get("data");
				for(int i=0;i<sources.length();i++) {
					
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
			config.getLogger().error("Unable to generate UI component", e);
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
		Query query = config.getQueryFactory().newQuery();
		config.getQueryExecutor().execute(query);
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
				config.getLogger().error("Could not parse JSON results", e);
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
			config.getLogger().error("Could not parse SPARQL results", e);
		} catch (IOException e) {
			config.getLogger().error("Could not read SPARQL results", e);
		} catch (ParserConfigurationException e) {
			config.getLogger().error("Parser configuration error", e);
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
