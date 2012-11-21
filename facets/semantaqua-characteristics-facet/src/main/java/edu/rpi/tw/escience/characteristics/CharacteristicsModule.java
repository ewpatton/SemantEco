package edu.rpi.tw.escience.characteristics;

import static edu.rpi.tw.escience.waterquality.query.Query.RDF_NS;
import static edu.rpi.tw.escience.waterquality.query.Query.VAR_NS;

import java.io.IOException;
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
import edu.rpi.tw.escience.waterquality.SemantAquaUI;
import edu.rpi.tw.escience.waterquality.query.GraphComponentCollection;
import edu.rpi.tw.escience.waterquality.query.OptionalComponent;
import edu.rpi.tw.escience.waterquality.query.NamedGraphComponent;
//import edu.rpi.tw.escience.waterquality.dataprovider.WaterDataProviderModule;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.QueryResource;
import edu.rpi.tw.escience.waterquality.query.Variable;
import edu.rpi.tw.escience.waterquality.query.Query.SortType;
import edu.rpi.tw.escience.waterquality.query.Query.Type;

public class CharacteristicsModule implements Module {
	
	private static final String POL_NS = "http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#";
	private static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
	private static final String FAILURE = "{\"success\":false}";
	private static final String BINDINGS = "bindings";
	private static final String XSD_NS = "http://www.w3.org/2001/XMLSchema#";
	private static final String UNIT_NS = "http://sweet.jpl.nasa.gov/2.1/reprSciUnits.owl#";
	private static final String TIME_NS = "http://www.w3.org/2006/time#";
	private ModuleConfiguration config = null;
	private static final Logger log = Logger.getLogger(CharacteristicsModule.class);

	//private ModuleConfiguration config = null;
	
	public void visit(Model model, Request request) {
		//DataModelBuilder builder = new DataModelBuilder(request, config);
		//builder.build(model);
		
		
		
	}

	@Override
	public void visit(final OntModel model, final Request request) {
		// TODO populate ontology model
	}
	
	//first check ifg bbbq state is null for chemical
	//new Query().findGraphComponentsWithPattern(?measurement, pol:hasCharacteristic, null)
	//returns a list of graphComponent. should be a singleon list, check on what it returns when empty
    //if not empty, graph.addpattern();
	@Override
	public void visit(final Query query, final Request request) {
		String characteristic = (String)request.getParam("characteristic");
		if(characteristic != null && characteristic.length() > 0) {
			//throw new IllegalArgumentException("The source parameter must be supplied");
			final Variable measurement = query.getVariable(VAR_NS+"measurement");
			final QueryResource hasCharacteristic = query.getResource(POL_NS+"hasCharacteristic");
			List<GraphComponentCollection> graphs = query.findGraphComponentsWithPattern(measurement, hasCharacteristic, null);
			if ( graphs.size() > 0){
				GraphComponentCollection graph = graphs.get(0);
				final QueryResource characteristicResource = query.getResource(POL_NS+characteristic);			
				graph.addPattern(measurement, hasCharacteristic, characteristicResource);			
			}	
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
	public void visit(final SemantAquaUI ui, final Request request) {
		// TODO add resources to display
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
		
		query.addPattern(site, polHasMeasurement, measurement);
		query.addPattern(measurement, polHasCharacteristic, chemical);
		query.addPattern(measurement, polHasValue, value);
		query.addPattern(measurement, unitHasUnit, unit);
		query.addPattern(measurement, timeInXSDDateTime, time);
		OptionalComponent optional = query.createOptional();
		query.addGraphComponent(optional);
		optional.addPattern(measurement, polHasPermit, permit);
		optional = query.createOptional();
		query.addGraphComponent(optional);

		query.addOrderBy(time, SortType.ASC);

		return config.getQueryExecutor(request).accept("application/json").executeLocalQuery(query);
	}

}
