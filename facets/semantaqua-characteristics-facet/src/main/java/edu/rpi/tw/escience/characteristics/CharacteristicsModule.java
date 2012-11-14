package edu.rpi.tw.escience.characteristics;

import static edu.rpi.tw.escience.waterquality.query.Query.RDF_NS;
import static edu.rpi.tw.escience.waterquality.query.Query.VAR_NS;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.Module;
import edu.rpi.tw.escience.waterquality.ModuleConfiguration;
import edu.rpi.tw.escience.waterquality.QueryMethod;
import edu.rpi.tw.escience.waterquality.Request;
import edu.rpi.tw.escience.waterquality.Resource;
import edu.rpi.tw.escience.waterquality.SemantAquaUI;
import edu.rpi.tw.escience.characteristics.DataModelBuilder;
import edu.rpi.tw.escience.waterquality.query.GraphComponentCollection;
import edu.rpi.tw.escience.waterquality.query.OptionalComponent;
//import edu.rpi.tw.escience.waterquality.dataprovider.WaterDataProviderModule;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.QueryResource;
import edu.rpi.tw.escience.waterquality.query.Variable;
import edu.rpi.tw.escience.waterquality.query.Query.SortType;
import edu.rpi.tw.escience.waterquality.query.Query.Type;

public class CharacteristicsModule implements Module {
	
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
		return null;	
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
		final Variable type = query.getVariable(VAR_NS+"type");
		final Variable value = query.getVariable(VAR_NS+"value");
		final Variable unit = query.getVariable(VAR_NS+"unit");
		final Variable time = query.getVariable(VAR_NS+"time");
		final Variable measurement = query.getVariable(VAR_NS+"measurement");
		
		final Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(element);
		vars.add(permit);
		vars.add(type);
		vars.add(value);
		vars.add(unit);
		vars.add(time);
		vars.add(measurement);
		
		// Resources
		final QueryResource site = query.getResource(siteUri);
		final QueryResource chemical = query.getResource(chemicalString);
		final QueryResource rdfType = query.getResource(RDF_NS+"type");
		final QueryResource polHasMeasurement = query.getResource(POL_NS+"hasMeasurement");
		final QueryResource polHasPermit = query.getResource(POL_NS+"hasPermit");
		//final QueryResource polRegulationViolation = query.getResource(POL_NS+"RegulationViolation");
		final QueryResource polHasCharacteristic = query.getResource(POL_NS+"hasCharacteristic");
		final QueryResource polHasValue = query.getResource(POL_NS+"hasValue");
		final QueryResource unitHasUnit = query.getResource(UNIT_NS+"hasUnit");
		final QueryResource timeInXSDDateTime = query.getResource(TIME_NS+"inXSDDateTime");
		final QueryResource rdfsSubClassOf = query.getResource(RDFS_NS+"subClassOf");
		
		query.addPattern(site, polHasMeasurement, measurement);
		//query.addPattern(measurement, rdfType, polRegulationViolation);
		query.addPattern(measurement, polHasCharacteristic, chemical);
		query.addPattern(measurement, polHasValue, value);
		query.addPattern(measurement, unitHasUnit, unit);
		query.addPattern(measurement, timeInXSDDateTime, time);
		OptionalComponent optional = query.createOptional();
		query.addGraphComponent(optional);
		optional.addPattern(measurement, polHasPermit, permit);
		optional = query.createOptional();
		query.addGraphComponent(optional);
		optional.addPattern(measurement, rdfType, type);
		//optional.addPattern(type, rdfsSubClassOf, polRegulationViolation);
		
		//query this 
		//extendQueryForLimits(query);

		query.addOrderBy(time, SortType.ASC);

		return config.getQueryExecutor(request).accept("application/json").executeLocalQuery(query);
	}

}
