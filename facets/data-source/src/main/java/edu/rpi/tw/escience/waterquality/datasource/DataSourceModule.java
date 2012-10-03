package edu.rpi.tw.escience.waterquality.datasource;

import java.util.HashSet;
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
import edu.rpi.tw.escience.waterquality.query.BlankNode;
import edu.rpi.tw.escience.waterquality.query.NamedGraphComponent;
import edu.rpi.tw.escience.waterquality.query.OptionalComponent;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.QueryResource;
import edu.rpi.tw.escience.waterquality.query.Variable;
import edu.rpi.tw.escience.waterquality.util.NameUtils;

/**
 * The Data Source Module provides the mechanisms for constructing the primary
 * graph of data used for populating the data model before reasoning occurs.
 * 
 * @author ewpatton
 *
 */
public class DataSourceModule implements Module {

	private ModuleConfiguration config = null;
	private static final String SEMANTAQUA_METADATA = "http://sparql.tw.rpi.edu/semanteco/data-source";
	private static final String DC_NS = "http://purl.org/dc/terms/";
	private static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
	private static final String SOURCE_VAR = "source";
	private static final String LABEL_VAR = "label";
	private static final String FAILURE = "{\"success\":false}";
	private static final String BINDINGS = "bindings";
	private static final String VALUE = "value";
	private Logger log = Logger.getLogger(DataSourceModule.class);
	
	/**
	 * Queries for data from the SPARQL endpoint and uses that data to
	 * populate the data model.
	 */
	@Override
	public void visit(Model model, Request request) {
		DataModelBuilder builder = new DataModelBuilder(request, config);
		builder.build(model);
	}

	@Override
	public void visit(OntModel model, Request request) {
		// do nothing as we are simply a data provider, not an ontology provider
	}

	@Override
	public void visit(Query query, Request request) {
		// the data source module handles all of its customization when
		// constructing the data model see visit(Model, Map)
	}

	@Override
	public void visit(SemantAquaUI ui, Request request) {
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
					responseText += "<input name=\"source\" type=\"checkbox\" checked=\"checked\" value=\""+mapping.getString("uri")+"\" id=\""+NameUtils.cleanName(mapping.getString(LABEL_VAR))+"\" />";
					responseText += "<label for=\""+NameUtils.cleanName(mapping.getString(LABEL_VAR))+"\">"+mapping.getString(LABEL_VAR)+"</label>";
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
	public String queryForDataSources(Request request) {
		final Logger log = request.getLogger();
		log.trace("queryForDataSources");
		Query query = config.getQueryFactory().newQuery();
		
		// generate variables and resources for query
		Variable source = query.createVariable(Query.VAR_NS+SOURCE_VAR);
		Variable label = query.createVariable(Query.VAR_NS+LABEL_VAR);
		QueryResource dcSource = query.getResource(DC_NS+SOURCE_VAR);
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
			JSONArray bindings = results.getJSONArray(BINDINGS);
			for(int i=0;i<bindings.length();i++) {
				JSONObject binding = bindings.getJSONObject(i);
				String sourceUri = binding.getJSONObject(SOURCE_VAR).getString(VALUE);
				String labelStr = null;
				try {
					labelStr = binding.getJSONObject(LABEL_VAR).getString(VALUE);
				}
				catch(Exception e) { }
				if(labelStr == null) {
					labelStr = sourceUri.substring(sourceUri.lastIndexOf('/')+1).replace('-', '.');
				}
				JSONObject mapping = new JSONObject();
				mapping.put("uri", sourceUri);
				mapping.put(LABEL_VAR, labelStr);
				data.put(mapping);
			}
			responseStr = response.toString();
		} catch (JSONException e) {
			log.error("Unable to parse JSON results", e);
		}
		return responseStr;
	}
	
	@QueryMethod
	public String getSiteCounts(Request request) {
		final Logger log = request.getLogger();
		log.trace("getSiteCounts");

		String responseStr = FAILURE;
		InstanceCounter counter = new InstanceCounter(request, config);
		JSONObject result = counter.build();
		responseStr = result.toString();
		return responseStr;
	}
	
}
