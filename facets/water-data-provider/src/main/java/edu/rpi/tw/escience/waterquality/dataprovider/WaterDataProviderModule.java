package edu.rpi.tw.escience.waterquality.dataprovider;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.Domain;
import edu.rpi.tw.escience.waterquality.Module;
import edu.rpi.tw.escience.waterquality.ModuleConfiguration;
import edu.rpi.tw.escience.waterquality.ProvidesDomain;
import edu.rpi.tw.escience.waterquality.QueryMethod;
import edu.rpi.tw.escience.waterquality.Request;
import edu.rpi.tw.escience.waterquality.Resource;
import edu.rpi.tw.escience.waterquality.SemantAquaUI;
import edu.rpi.tw.escience.waterquality.query.BlankNode;
import edu.rpi.tw.escience.waterquality.query.GraphComponentCollection;
import edu.rpi.tw.escience.waterquality.query.NamedGraphComponent;
import edu.rpi.tw.escience.waterquality.query.OptionalComponent;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.Query.Type;
import edu.rpi.tw.escience.waterquality.query.QueryResource;
import edu.rpi.tw.escience.waterquality.query.Variable;

import static edu.rpi.tw.escience.waterquality.query.Query.RDF_NS;
import static edu.rpi.tw.escience.waterquality.query.Query.VAR_NS;

public class WaterDataProviderModule implements Module, ProvidesDomain {

	private static final String SEMANTAQUA_METADATA = "http://sparql.tw.rpi.edu/semanteco/data-source";
	private static final String SITE_VAR = "site";
	private static final String POL_NS = "http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#";
	private static final String DC_NS = "http://purl.org/dc/terms/";
	private static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
	private static final String SOURCE_VAR = "source";
	private static final String WATER_NS = "http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#";
	private static final String ISWATER_VAR = "isWater";
	private static final String FAILURE = "{\"success\":false}";
	private static final String BINDINGS = "bindings";
	private static final String VALUE = "value";
	private static final String LABEL_VAR = "label";
	private ModuleConfiguration config = null;
	private static final Logger log = Logger.getLogger(WaterDataProviderModule.class);
	
	@Override
	public void visit(Model model, Request request) {
		DataModelBuilder builder = new DataModelBuilder(request, config);
		builder.build(model);
	}

	@Override
	public void visit(OntModel model, Request request) {
		model.read(WATER_NS);
	}

	@Override
	public void visit(Query query, Request request) {
		if(query.getType() != Type.SELECT) {
			return;
		}
		final Variable site = query.getVariable(VAR_NS+SITE_VAR);
		final QueryResource rdfType = query.getResource(RDF_NS+"type");
		final QueryResource polMeasurementSite = query.getResource(POL_NS+"MeasurementSite");
		List<GraphComponentCollection> graphs = query.findGraphComponentsWithPattern(site, rdfType, polMeasurementSite);
		if(graphs != null && graphs.size() > 0) {
			query.setNamespace("water", WATER_NS);
			final Variable isWater = query.createVariableExpression("EXISTS { ?"+SITE_VAR+" a water:WaterSite } as ?"+ISWATER_VAR);
			Set<Variable> vars = new LinkedHashSet<Variable>(query.getVariables());
			vars.add(isWater);
			query.setVariables(vars);
		}
	}

	@Override
	public void visit(SemantAquaUI ui, Request request) {
		Resource res = config.getResource("water-data-provider.js");
		if(res != null) {
			ui.addScript(res);
		}
	}

	@Override
	public List<Domain> getDomains(final Request request) {
		List<Domain> domains = new ArrayList<Domain>();
		Domain water = config.getDomain(URI.create("http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#"), true);
		water.setLabel("Water");
		addDataSources(water, request);
		addRegulations(water);
		addDataTypes(water);
		domains.add(water);
		return domains;
	}

	@Override
	public String getName() {
		return "Water Data Provider";
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
	public String queryForDataSources(final Request request) {
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
	
	protected void addRegulations(final Domain domain) {
		domain.addRegulation(URI.create("http://escience.rpi.edu/ontology/semanteco/2/0/EPA-regulation.owl"), "EPA Regulation");
		domain.addRegulation(URI.create("http://escience.rpi.edu/ontology/semanteco/2/0/ca-regulation.owl"), "CA Regulation");
		domain.addRegulation(URI.create("http://escience.rpi.edu/ontology/semanteco/2/0/ma-regulation.owl"), "MA Regulation");
		domain.addRegulation(URI.create("http://escience.rpi.edu/ontology/semanteco/2/0/ny-regulation.owl"), "NY Regulation");
		domain.addRegulation(URI.create("http://escience.rpi.edu/ontology/semanteco/2/0/ri-regulation.owl"), "RI Regulation");
	}
	
	protected void addDataTypes(final Domain domain) {
		Resource res = config.getResource("clean-water.png");
		domain.addDataType("clean-water", "Clean Water", res);
		res = config.getResource("facility.png");
		domain.addDataType("clean-facility", "Facility", res);
		res = config.getResource("polluted-water.png");
		domain.addDataType("polluted-water", "Polluted Water", res);
		res = config.getResource("polluted-facility.png");
		domain.addDataType("polluted-facility", "Polluted Facility", res);
	}
	
	protected void addDataSources(final Domain domain, final Request request) {
		String response = queryForDataSources(request);
		try {
			JSONObject data = new JSONObject(response);
			JSONArray entries = data.getJSONArray("data");
			for(int i=0;i<entries.length();i++) {
				JSONObject entry = entries.getJSONObject(i);
				String uri = entry.getString("uri");
				String label = entry.getString(LABEL_VAR);
				domain.addSource(URI.create(uri), label);
			}
		}
		catch(JSONException e) {
			log.warn("Unexpected exception", e);
		}
	}
	
}
