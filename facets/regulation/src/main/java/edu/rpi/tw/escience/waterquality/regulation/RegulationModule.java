package edu.rpi.tw.escience.waterquality.regulation;

import java.util.HashSet;
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

import edu.rpi.tw.escience.waterquality.Module;
import edu.rpi.tw.escience.waterquality.ModuleConfiguration;
import edu.rpi.tw.escience.waterquality.QueryMethod;
import edu.rpi.tw.escience.waterquality.Request;
import edu.rpi.tw.escience.waterquality.SemantAquaUI;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.Query.Type;
import edu.rpi.tw.escience.waterquality.query.QueryResource;
import edu.rpi.tw.escience.waterquality.query.Variable;

import static edu.rpi.tw.escience.waterquality.query.Query.RDF_NS;
import static edu.rpi.tw.escience.waterquality.query.Query.VAR_NS;

public class RegulationModule implements Module {

	private static final String SITE_VAR = "site";
	private static final String FACILITY_VAR = "facility";
	private static final String POLLUTED_VAR = "polluted";
	private static final String POL_NS = "http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#";
	private static final String WATER_NS = "http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#";
	private static final String WGS_NS = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	
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
		log.info("Computing EPA closure took "+(System.currentTimeMillis()-start)+" ms");
	}

	@Override
	public void visit(OntModel model, Request request) {
		// load the appropriate regulation ontology here
		String regulation = (String)request.getParam("regulation");
		model.read(POL_NS);
		model.read(WATER_NS);
		model.read(regulation);
	}

	@Override
	public void visit(Query query, Request request) {
		
	}

	@Override
	public void visit(SemantAquaUI ui, Request request) {
		// TODO autogen this in the future
		ui.addFacet(config.getResource("regulations.jsp"));
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
		Query query = config.getQueryFactory().newQuery(Type.SELECT);
		
		query.setNamespace("pol", POL_NS);
		
		// Variables
		final Variable site = query.getVariable(VAR_NS+SITE_VAR);
		final Variable lat = query.getVariable(VAR_NS+"lat");
		final Variable lng = query.getVariable(VAR_NS+"lng");
		final Variable facility = query.createVariableExpression("EXISTS { ?"+SITE_VAR+" a pol:Facility } as ?"+FACILITY_VAR);
		final Variable polluted = query.createVariableExpression("EXISTS { ?"+SITE_VAR+" a pol:PollutedSite } as ?"+POLLUTED_VAR);
		
		// known uris
		final QueryResource rdfType = query.getResource(RDF_NS+"type");
		final QueryResource polMeasurementSite = query.getResource(POL_NS+"MeasurementSite");
		final QueryResource wgsLat = query.getResource(WGS_NS+"lat");
		final QueryResource wgsLong = query.getResource(WGS_NS+"long");
		
		// build query
		Set<Variable> vars = new HashSet<Variable>();
		vars.add(site);
		vars.add(lat);
		vars.add(lng);
		vars.add(facility);
		vars.add(polluted);
		query.setVariables(vars);
		
		query.addPattern(site, rdfType, polMeasurementSite);
		query.addPattern(site, wgsLat, lat);
		query.addPattern(site, wgsLong, lng);
		
		return config.getQueryExecutor(request).accept("application/json").executeLocalQuery(request, query);
	}
	
	@QueryMethod
	public String queryForSitePollution(Request request) {
		return null;
	}
	
}
