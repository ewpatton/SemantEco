package edu.rpi.tw.escience.semanteco.regulation;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.QueryMethod;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.query.GraphComponentCollection;
import edu.rpi.tw.escience.semanteco.query.OptionalComponent;
import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.Query.Type;
import edu.rpi.tw.escience.semanteco.query.QueryResource;
import edu.rpi.tw.escience.semanteco.query.Variable;
import edu.rpi.tw.escience.semanteco.util.NameUtils;

import static edu.rpi.tw.escience.semanteco.query.Query.RDF_NS;
import static edu.rpi.tw.escience.semanteco.query.Query.VAR_NS;

/**
 * The Regulation module provides the main query mechanisms
 * over the combined model to retrieve information about
 * sites and any polluted measurements found at them.
 * @author ewpatton
 *
 */
public class RegulationModule implements Module {

	private static final String SITE_VAR = "site";
	private static final String FACILITY_VAR = "facility";
	private static final String POLLUTED_VAR = "polluted";
	private static final String LABEL_VAR = "label";
	private static final String POL_NS = "http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#";
	private static final String WGS_NS = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	private static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
	private static final String UNIT_NS = "http://sweet.jpl.nasa.gov/2.1/reprSciUnits.owl#";
	private static final String OWL_NS = "http://www.w3.org/2002/07/owl#";
	private static final String XSD_NS = "http://www.w3.org/2001/XMLSchema#";
	private static final String PROP_VAR = "p";
	
	private ModuleConfiguration config = null;
	
	@Override
	public void visit(Model model, Request request) {
	}

	@Override
	public void visit(OntModel model, Request request) {
		final Logger log = request.getLogger();
		log.debug("Loading '"+POL_NS+"'");
		model.read(POL_NS);
		// load the appropriate regulation ontology here
		JSONObject regulation = (JSONObject)request.getParam("regulation");
		JSONArray domains = (JSONArray)request.getParam("domain");
		Map<String, Domain> domainMap = new HashMap<String, Domain>();
		List<Domain> allDomains = config.listDomains();
		for(Domain i : allDomains) {
			domainMap.put(i.getUri().toString(), i);
		}
		Map<String, Domain> activeDomains = new HashMap<String, Domain>();
		for(int i=0;i<domains.length();i++) {
			String uri = domains.optString(i);
			if(domainMap.containsKey(uri)) {
				activeDomains.put(NameUtils.cleanName(domainMap.get(uri).getLabel()), domainMap.get(uri));
			}
		}
		@SuppressWarnings("unchecked")
		Iterator<String> keys = (Iterator<String>)regulation.keys();
		while(keys.hasNext()) {
			String key = keys.next();
			String reg = regulation.optString(key);
			if(reg == null || reg.equals("")) {
				continue;
			}
			if(activeDomains.get(key) != null) {
				log.debug("Loading regulation ontology '"+reg+"' for domain '"+key+"'");
				try {
					final URL url = new URL(reg);
					final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
					conn.connect();
					final String type = conn.getContentType();
					final InputStream content = conn.getInputStream();
					if(type.equals("application/rdf+xml") || type.equals("text/xml")) {
						model.read(content, reg);
					}
					else if(type.equals("text/turtle")) {
						model.read(content, reg, "TTL");
					}
					else if(type.equals("text/n3")) {
						model.read(content, reg, "N3");
					}
					else {
						log.warn("Unexpected content type "+type+" received for '"+reg+"'");
					}
				}
				catch(Exception e) {
					log.warn("Had a problem reading the regulation file '"+reg+"'", e);
				}
			}
		}
	}

	@Override
	public void visit(Query query, Request request) {
		
	}

	@Override
	public void visit(SemantEcoUI ui, Request request) {
		ui.addScript(config.getResource("regulation.js"));
		StringBuilder responseStr = new StringBuilder("<div id=\"RegulationFacet\" class=\"facet\">");
		List<Domain> domains = config.listDomains();
		for(Domain i : domains) {
			List<URI> regulations = i.getRegulations();
			if(regulations.size()>0) {
				responseStr.append(i.getLabel() + ": ");
				responseStr.append("<select name=\"regulation."+NameUtils.cleanName(i.getLabel())+"\">");
				for(URI j : regulations) {
					responseStr.append("<option value=\""+j.toString()+"\">");
					responseStr.append(i.getLabelForRegulation(j));
					responseStr.append("</option>");
				}
				responseStr.append("</select><br />");
			}
		}
		responseStr.append("</div>");
		ui.addFacet(config.generateStringResource(responseStr.toString()));
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
	 * Queries for sites identified across all modules based on the
	 * parameters sent by the client.
	 * @param request Application state sent from client
	 * @return JSON encoded SPARQL results containing, at a minimum,
	 * ?site, ?lat, ?lng, ?facility, ?polluted, and ?label.
	 */
	@QueryMethod
	public String queryForSites(final Request request) {
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
	
	/**
	 * Queries for the polluted measurements at a particular site.
	 * @param request Application state sent from client
	 * @return JSON encoded SPARQL results containing, at a minimum,
	 * ?element, ?permit, ?type, ?value, ?unit, and ?measurement
	 */
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
		//final Variable time = query.getVariable(VAR_NS+"time");
		final Variable measurement = query.getVariable(VAR_NS+"measurement");
		
		final Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(element);
		vars.add(permit);
		vars.add(type);
		vars.add(value);
		vars.add(unit);
		//vars.add(time);
		vars.add(measurement);
		query.setVariables(vars);
		
		// Resources
		String ENHANCE_NS = "http://sparql.tw.rpi.edu/source/epa-gov/dataset/echo-measurements-ri/vocab/enhancement/1/";
		final QueryResource site = query.getResource(siteUri);
		final QueryResource rdfType = query.getResource(RDF_NS+"type");
		final QueryResource polHasMeasurement = query.getResource(POL_NS+"hasMeasurement");
		final QueryResource polHasPermit = query.getResource(POL_NS+"hasPermit");
		final QueryResource polRegulationViolation = query.getResource(POL_NS+"RegulationViolation");
		final QueryResource polHasCharacteristic = query.getResource(POL_NS+"hasCharacteristic");
		final QueryResource polHasValue = query.getResource(POL_NS+"hasValue");
		final QueryResource unitHasUnit = query.getResource(UNIT_NS+"hasUnit");
		final QueryResource rdfsSubClassOf = query.getResource(RDFS_NS+"subClassOf");
		final QueryResource test_type = query.getResource(ENHANCE_NS+"test_type");
		final Variable testVar = query.getVariable(VAR_NS+"test");


		
		query.addPattern(site, polHasMeasurement, measurement);
		query.addPattern(measurement, rdfType, polRegulationViolation);
		query.addPattern(measurement, polHasCharacteristic, element);
		query.addPattern(measurement, polHasValue, value);
		query.addPattern(measurement, unitHasUnit, unit);
		OptionalComponent optional = query.createOptional();
		query.addGraphComponent(optional);
		optional.addPattern(measurement, polHasPermit, permit);
		optional = query.createOptional();
		query.addGraphComponent(optional);
		optional.addPattern(measurement, rdfType, type);
		optional.addPattern(measurement, test_type, testVar);

		optional.addPattern(measurement, rdfsSubClassOf, polRegulationViolation);
		optional.addPattern(type, rdfsSubClassOf, polRegulationViolation);
		
		extendQueryForLimits(query);

		//query.addOrderBy(time, SortType.ASC);

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

		final QueryResource polHasValue = query.getResource(POL_NS+"hasValue");
		final QueryResource polHasLimitOperator = query.getResource(POL_NS+"hasLimitOperator");
		final QueryResource polHasLimitValue = query.getResource(POL_NS+"hasLimitValue");
		final QueryResource owlIntersectionOf = query.getResource(OWL_NS+"intersectionOf");
		final QueryResource owlOnProperty = query.getResource(OWL_NS+"onProperty");
		final QueryResource owlSomeValuesFrom = query.getResource(OWL_NS+"someValuesFrom");
		final QueryResource owlWithRestrictions = query.getResource(OWL_NS+"withRestrictions");
		final QueryResource propPath = query.createPropertyPath("rdf:rest*/rdf:first");
		final QueryResource propPath2 = query.createPropertyPath("rdf:type/owl:equivalentClass?");

		OptionalComponent optional = query.createOptional();
		query.addGraphComponent(optional);
		optional.addPattern(measurement, propPath2, cls);
		optional.addPattern(cls, owlIntersectionOf, list);
		optional.addPattern(list, propPath, supers);
		optional.addPattern(supers, owlOnProperty, polHasValue);
		optional.addPattern(supers, owlSomeValuesFrom, dt);
		optional.addPattern(dt, owlWithRestrictions, res);
		optional.addPattern(res, propPath, bn);
		optional.addPattern(bn, p, limit);
		optional.addFilter("datatype(?limit) = xsd:decimal");

		addOpMatch(query, optional, "xsd:minInclusive", "<=", op);
		addOpMatch(query, optional, "xsd:maxInclusive", ">=", op);
		addOpMatch(query, optional, "xsd:minExclusive", "<", op);
		addOpMatch(query, optional, "xsd:maxExclusive", ">", op);
		
		optional = query.createOptional();
		query.addGraphComponent(optional);
		optional.addPattern(measurement, polHasLimitOperator, op);
		optional.addPattern(measurement, polHasLimitValue, limit);
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
