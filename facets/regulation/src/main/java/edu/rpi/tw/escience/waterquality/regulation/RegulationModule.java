package edu.rpi.tw.escience.waterquality.regulation;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.rpi.tw.escience.waterquality.Domain;
import edu.rpi.tw.escience.waterquality.Module;
import edu.rpi.tw.escience.waterquality.ModuleConfiguration;
import edu.rpi.tw.escience.waterquality.QueryMethod;
import edu.rpi.tw.escience.waterquality.Request;
import edu.rpi.tw.escience.waterquality.SemantAquaUI;
import edu.rpi.tw.escience.waterquality.query.GraphComponentCollection;
import edu.rpi.tw.escience.waterquality.query.OptionalComponent;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.Query.SortType;
import edu.rpi.tw.escience.waterquality.query.Query.Type;
import edu.rpi.tw.escience.waterquality.query.QueryResource;
import edu.rpi.tw.escience.waterquality.query.Variable;
import edu.rpi.tw.escience.waterquality.util.NameUtils;

import static edu.rpi.tw.escience.waterquality.query.Query.RDF_NS;
import static edu.rpi.tw.escience.waterquality.query.Query.VAR_NS;

public class RegulationModule implements Module {

	private static final String SITE_VAR = "site";
	private static final String FACILITY_VAR = "facility";
	private static final String POLLUTED_VAR = "polluted";
	private static final String LABEL_VAR = "label";
	private static final String POL_NS = "http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#";
	private static final String WGS_NS = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	private static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
	private static final String UNIT_NS = "http://sweet.jpl.nasa.gov/2.1/reprSciUnits.owl#";
	private static final String TIME_NS = "http://www.w3.org/2006/time#";
	private static final String OWL_NS = "http://www.w3.org/2002/07/owl#";
	private static final String XSD_NS = "http://www.w3.org/2001/XMLSchema#";
	private static final String PROP_VAR = "p";
	
	private ModuleConfiguration config = null;
	
	@Override
	public void visit(Model model, Request request) {
		// handle EPA regulations
		final Logger log = request.getLogger();
		long start = System.currentTimeMillis();
		String query = 
				"PREFIX water: <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#>" +
				"PREFIX pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#>" +
				"SELECT ?m WHERE { " +
				"?m a water:WaterMeasurement ; " +
				"pol:hasValue ?val ; " +
				"pol:hasLimitOperator ?op ; " +
				"pol:hasLimitValue ?lval " +
				"FILTER((?op = \"<=\" && ?val > ?lval) || " +
				"(?op = \">=\" && ?val < ?lval) || " +
				"(?op = \">\" && ?val <= ?lval))" +
				"}";
		QueryExecution qe = 
				QueryExecutionFactory.create(QueryFactory.create(query, Syntax.syntaxSPARQL_11), model);
		ResultSet rs = qe.execSelect();
		final Resource RegulationViolation = model.getResource(POL_NS+"RegulationViolation");
		final List<Resource> violations = new LinkedList<Resource>();
		while(rs.hasNext()) {
			QuerySolution qs = rs.next();
			Resource m = qs.getResource("m");
			log.debug("Found violating measurement "+m);
			violations.add(m);
		}
		for(Resource m : violations) {
			model.add(m, RDF.type, RegulationViolation);
		}
		qe.close();
		log.info("Computing EPA closure took "+(System.currentTimeMillis()-start)+" ms");
	}

	@Override
	public void visit(OntModel model, Request request) {
		// load the appropriate regulation ontology here
		String regulation = (String)request.getParam("regulation");
		model.read(POL_NS);
		model.read(regulation);
	}

	@Override
	public void visit(Query query, Request request) {
		
	}

	@Override
	public void visit(SemantAquaUI ui, Request request) {
		ui.addScript(config.getResource("regulation.js"));
		String responseStr = "<div id=\"RegulationFacet\" class=\"facet\">";
		List<Domain> domains = config.listDomains();
		for(Domain i : domains) {
			List<URI> regulations = i.getRegulations();
			if(regulations.size()>0) {
				responseStr += i.getLabel() + ": ";
				responseStr += "<select name=\"regulation."+NameUtils.cleanName(i.getLabel())+"\">";
				for(URI j : regulations) {
					responseStr += "<option value=\""+j.toString()+"\">";
					responseStr += i.getLabelForRegulation(j);
					responseStr += "</option>";
				}
				responseStr += "</select>";
			}
		}
		responseStr += "</div>";
		ui.addFacet(config.generateStringResource(responseStr));
	}

	@Override
	public String getName() {
		return "Regulation";
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
	 * 
	 */
	@QueryMethod
	public String queryForPollutedSites(final Request request) {
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		
		query.setNamespace("pol", POL_NS);
		
		// Variables
		final Variable site = query.getVariable(VAR_NS+SITE_VAR);
		final Variable lat = query.getVariable(VAR_NS+"lat");
		final Variable lng = query.getVariable(VAR_NS+"lng");
		final Variable facility = query.createVariableExpression("EXISTS { ?"+SITE_VAR+" a pol:Facility } as ?"+FACILITY_VAR);
		final Variable polluted = query.createVariableExpression("EXISTS { ?"+SITE_VAR+" a pol:PollutedSite } as ?"+POLLUTED_VAR);
		final Variable label = query.createVariable(VAR_NS+LABEL_VAR);
		
		// known uris
		final QueryResource rdfType = query.getResource(RDF_NS+"type");
		final QueryResource polMeasurementSite = query.getResource(POL_NS+"MeasurementSite");
		final QueryResource wgsLat = query.getResource(WGS_NS+"lat");
		final QueryResource wgsLong = query.getResource(WGS_NS+"long");
		final QueryResource rdfsLabel = query.getResource(RDFS_NS+LABEL_VAR);
		
		// build query
		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(site);
		vars.add(lat);
		vars.add(lng);
		vars.add(facility);
		vars.add(polluted);
		vars.add(label);
		query.setVariables(vars);
		
		query.addPattern(site, rdfType, polMeasurementSite);
		query.addPattern(site, wgsLat, lat);
		query.addPattern(site, wgsLong, lng);
		final OptionalComponent optional = query.createOptional();
		query.addGraphComponent(optional);
		optional.addPattern(site, rdfsLabel, label);
		
		return config.getQueryExecutor(request).accept("application/json").executeLocalQuery(query);
	}
	
	@QueryMethod
	public String queryForSitePollution(Request request) {
		final String siteUri = (String)request.getParam("uri");
		if(siteUri == null) {
			return "{\"error\":\"No uri parameter supplied\"}";
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
		final QueryResource rdfType = query.getResource(RDF_NS+"type");
		final QueryResource polHasMeasurement = query.getResource(POL_NS+"hasMeasurement");
		final QueryResource polHasPermit = query.getResource(POL_NS+"hasPermit");
		final QueryResource polRegulationViolation = query.getResource(POL_NS+"RegulationViolation");
		final QueryResource polHasCharacteristic = query.getResource(POL_NS+"hasCharacteristic");
		final QueryResource polHasValue = query.getResource(POL_NS+"hasValue");
		final QueryResource unitHasUnit = query.getResource(UNIT_NS+"hasUnit");
		final QueryResource timeInXSDDateTime = query.getResource(TIME_NS+"inXSDDateTime");
		final QueryResource rdfsSubClassOf = query.getResource(RDFS_NS+"subClassOf");
		
		query.addPattern(site, polHasMeasurement, measurement);
		query.addPattern(measurement, rdfType, polRegulationViolation);
		query.addPattern(measurement, polHasCharacteristic, element);
		query.addPattern(measurement, polHasValue, value);
		query.addPattern(measurement, unitHasUnit, unit);
		query.addPattern(measurement, timeInXSDDateTime, time);
		OptionalComponent optional = query.createOptional();
		query.addGraphComponent(optional);
		optional.addPattern(measurement, polHasPermit, permit);
		optional = query.createOptional();
		query.addGraphComponent(optional);
		optional.addPattern(measurement, rdfType, type);
		optional.addPattern(type, rdfsSubClassOf, polRegulationViolation);
		
		extendQueryForLimits(query);

		query.addOrderBy(time, SortType.ASC);

		return config.getQueryExecutor(request).accept("application/json").executeLocalQuery(query);
	}
	
	protected void extendQueryForLimits(final Query query) {
		final Variable limit = query.getVariable(VAR_NS+"limit");
		final Variable op = query.getVariable(VAR_NS+"op");
		
		Set<Variable> vars = new LinkedHashSet<Variable>(query.getVariables());
		vars.add(op);
		vars.add(limit);
		query.setVariables(vars);
		
		final Variable measurement = query.getVariable(VAR_NS+"measurement");
		final Variable cls = query.getVariable(VAR_NS+"cls");
		final Variable list = query.getVariable(VAR_NS+"list");
		final Variable supers = query.getVariable(VAR_NS+"supers");
		final Variable dt = query.getVariable(VAR_NS+"dt");
		final Variable res = query.getVariable(VAR_NS+"res");
		final Variable bn = query.createBlankNode();
		final Variable p = query.getVariable(VAR_NS+PROP_VAR);

		final QueryResource rdfType = query.getResource(RDF_NS+"type");
		final QueryResource polHasValue = query.getResource(POL_NS+"hasValue");
		final QueryResource polHasLimitOperator = query.getResource(POL_NS+"hasLimitOperator");
		final QueryResource polHasLimitValue = query.getResource(POL_NS+"hasLimitValue");
		final QueryResource owlIntersectionOf = query.getResource(OWL_NS+"intersectionOf");
		final QueryResource owlOnProperty = query.getResource(OWL_NS+"onProperty");
		final QueryResource owlSomeValuesFrom = query.getResource(OWL_NS+"someValuesFrom");
		final QueryResource owlWithRestrictions = query.getResource(OWL_NS+"withRestrictions");
		final QueryResource propPath = query.createPropertyPath("rdf:rest*/rdf:first");

		OptionalComponent optional = query.createOptional();
		query.addGraphComponent(optional);
		optional.addPattern(measurement, rdfType, cls);
		optional.addPattern(cls, owlIntersectionOf, list);
		optional.addPattern(list, propPath, supers);
		optional.addPattern(supers, owlOnProperty, polHasValue);
		optional.addPattern(supers, owlSomeValuesFrom, dt);
		optional.addPattern(dt, owlWithRestrictions, res);
		optional.addPattern(res, propPath, bn);
		optional.addPattern(bn, p, limit);
		optional.addFilter("datatype(?limit) = xsd:double");

		addOpMatch(query, optional, "xsd:minInclusive", "<=", op);
		addOpMatch(query, optional, "xsd:maxInclusive", ">=", op);
		addOpMatch(query, optional, "xsd:minExclusive", "<", op);
		addOpMatch(query, optional, "xsd:maxExclusive", ">", op);
		
		optional = query.createOptional();
		query.addGraphComponent(optional);
		optional.addPattern(measurement, polHasLimitOperator, op);
		optional.addPattern(measurement, polHasLimitValue, op);
	}
	
	protected void addOpMatch(final Query query, 
			final GraphComponentCollection graph, 
			final String xsdOp, final String mathOp, 
			final Variable mathVar) {
		OptionalComponent optional = query.createOptional();
		graph.addGraphComponent(optional);
		optional.addFilter("?"+PROP_VAR+" = "+xsdOp);
		optional.addBind("\""+mathOp+"\"", mathVar);
	}
	
}
