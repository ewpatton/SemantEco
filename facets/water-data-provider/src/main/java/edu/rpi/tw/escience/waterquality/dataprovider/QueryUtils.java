package edu.rpi.tw.escience.waterquality.dataprovider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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

import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.query.NamedGraphComponent;
import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.QueryResource;
import edu.rpi.tw.escience.semanteco.query.Query.Type;

/**
 * Query utilities used to find graphs and process SPARQL results.
 * See {@link DataModelBuilder} and {@link InstanceCounter} for
 * cases where QueryUtils is used.
 * @author ewpatton
 *
 */
public class QueryUtils {

	public static final String INSTANCE_HUB_STATES = "http://logd.tw.rpi.edu/source/twc-rpi-edu/dataset/instance-hub-us-states-and-territories/version/2011-Apr-09";
	public static final String LOGD_ENDPOINT = "http://logd.tw.rpi.edu/sparql?output=sparqljson";
	public static final String STATE_VAR = "state";
	public static final String RESULTS_BLOCK = "results";
	public static final String QUERY_NS = "http://aquarius.tw.rpi.edu/projects/semantaqua/data-source/query-variable/";
	public static final String BINDINGS = "bindings";
	public static final String DC_NS = "http://purl.org/dc/terms/";
	public static final String VALUE = "value";
	public static final String GRAPH_VAR = "graph";
	public static final String SEMANTECO_METADATA = "http://sparql.tw.rpi.edu/semanteco/data-source";
	public static final String SIOC_NS = "http://rdfs.org/sioc/ns#";
	public static final String SOURCE_VAR = "source";
	public static final String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
	public static final String POL_NS = "http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#";
	public static final String WATER_NS = "http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#";
	public static final String WGS_NS = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	public static final String REPR_NS = "http://sweet.jpl.nasa.gov/2.1/repr.owl#";
	public static final String UNIT_NS = "http://sweet.jpl.nasa.gov/2.1/reprSciUnits.owl#";
	public static final String TIME_NS = "http://www.w3.org/2006/time#";

	private final Logger log;
	private final ModuleConfiguration config;
	private final Request request;
	
	private static final Map<String, String> stateUriMap = new HashMap<String, String>();
	
	protected QueryUtils(final Request request, final ModuleConfiguration config) {
		this.request = request;
		this.log = request.getLogger();
		this.config = config;
	}
	
	protected final String getStateURI(final String state) {
		log.trace("retrieveStateGraphsForSource");
		if(stateUriMap.containsKey(state)) {
			return stateUriMap.get(state);
		}
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
		if(stateUri != null) {
			stateUriMap.put(state, stateUri);
		}
		return stateUri;
	}
	
	/**
	 * Retrieves a list of graphs from {@link #SEMANTECO_METADATA} related to
	 * the specified state and source.
	 * 
	 * @param state State uri in instance hub
	 * @param source Source entity, e.g. http://sparql.tw.rpi.edu/source/epa-gov
	 * @return List of URIs representing graphs in the SPARQL endpoint
	 */
	protected List<String> retrieveStateGraphsForSource(final String state, final String source) {
		log.trace("retrieveStateGraphsForSource");
		final List<String> graphs = new ArrayList<String>();
		
		// get graphs from sparql.tw.rpi.edu related to (stateUri, source)
		Query query = config.getQueryFactory().newQuery(Type.SELECT);
		query.setVariables(null);
		NamedGraphComponent graph = query.getNamedGraph(SEMANTECO_METADATA);
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
	 * Processes the results from metadata graph query
	 * @param results application/sparql-results+xml encoded results from a SPARQL endpoint
	 * @return
	 */
	protected final Collection<String> processResults(final String results) {
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
					if(binding.getAttribute("name").equals(GRAPH_VAR)) {
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
