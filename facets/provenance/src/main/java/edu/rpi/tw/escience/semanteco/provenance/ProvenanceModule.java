package edu.rpi.tw.escience.semanteco.provenance;

import static edu.rpi.tw.escience.semanteco.query.Query.RDF_NS;
import static edu.rpi.tw.escience.semanteco.query.Query.VAR_NS;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.ModuleManager;
import edu.rpi.tw.escience.semanteco.QueryMethod;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.Resource;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
//import edu.rpi.tw.escience.semanteco.impl.ModuleManagerFactory;
import edu.rpi.tw.escience.semanteco.query.GraphComponentCollection;
import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.QueryResource;
import edu.rpi.tw.escience.semanteco.query.Variable;
import edu.rpi.tw.escience.semanteco.query.Query.Type;

public class ProvenanceModule implements Module {

	private ModuleConfiguration config = null;
	private String site = null;
	public static final String QUERY_NS = "http://aquarius.tw.rpi.edu/projects/semantaqua/data-source/query-variable/";
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
	
	public ProvenanceModule(){
		//test if this executes once tomcat is restarted
		System.out.println("Provenance Module has started");
		
	}

	/**
	 * Based on the measurement site parmeter, gets RDF data adding provenance using a CONSTRUCT query and returns to client corresponding RDF
	 * @param request
	 * @return
	 */
	@QueryMethod
	public String queryForProvenance(final Request request){
		
		//get the site URI
		site = (String)request.getParam("uri");
		

		//get the combined model
		Model model = request.getCombinedModel();
		
		//sparql construct (not graph specific, on the model)
		final Query query = config.getQueryFactory().newQuery(Type.CONSTRUCT);
		final GraphComponentCollection construct = query.getConstructComponent();

		//set up variables and resources
		final QueryResource siteResource = query.getResource(site);
		final QueryResource rdfType = query.getResource(RDF_NS+"type");
		final QueryResource polMeasurementSite = query.getResource(POL_NS+"MeasurementSite");
		final QueryResource wgsLat = query.getResource(WGS_NS+"lat");
		final QueryResource wgsLong = query.getResource(WGS_NS+"long");
		final Variable lat = query.getVariable(VAR_NS+"lat");
		final Variable lng = query.getVariable(VAR_NS+"lng");
		//no need to set variables in a construct
		final QueryResource hasMeasurement = query.getResource(POL_NS +"hasMeasurement");
		final Variable measurement = query.getVariable(VAR_NS+"measurement");
		final QueryResource polHasCounty = query.getResource(POL_NS+"hasCounty");
		final QueryResource polHasState = query.getResource(POL_NS+"hasState");
		final QueryResource polHasCharacteristic = query.getResource(POL_NS+"hasCharacteristic");
		final QueryResource polHasValue = query.getResource(POL_NS+"hasValue");
		final QueryResource unitHasUnit = query.getResource(UNIT_NS+"hasUnit");
		final QueryResource subClassOf = query.getResource(RDFS_NS+"subClassOf");
		final QueryResource type = query.getResource(RDF_NS+"type");
		final QueryResource WaterMeasurement = query.getResource("http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#WaterMeasurement");
		final QueryResource RegulationViolation = query.getResource("http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#RegulationViolation");
		final Variable element = query.getVariable(QUERY_NS+"element");
		final Variable unit = query.getVariable(QUERY_NS+"unit");
		final Variable value = query.getVariable(QUERY_NS+"value");
		final Variable stateCode = query.getVariable(QUERY_NS+"state");
		final Variable countyCode = query.getVariable(QUERY_NS+"county");
		final Variable MeasurementType = query.getVariable(QUERY_NS+"MeasurementType");
		
		construct.addPattern(siteResource, rdfType, polMeasurementSite);
		construct.addPattern(siteResource, wgsLat, lat);
		construct.addPattern(siteResource, wgsLong, lng);
		
		construct.addPattern(siteResource, hasMeasurement, measurement);
		construct.addPattern(measurement, polHasCharacteristic, element);
		construct.addPattern(measurement, polHasValue, value);
		construct.addPattern(measurement, unitHasUnit, unit);
		construct.addPattern(measurement, type, MeasurementType);

		
		//we also want to query which regulationViolation class it is a member of
		
		
		query.addPattern(siteResource, rdfType, polMeasurementSite);
		query.addPattern(siteResource, wgsLat, lat);
		query.addPattern(siteResource, wgsLong, lng);
		
		query.addPattern(siteResource, hasMeasurement, measurement);
		query.addPattern(measurement, polHasCharacteristic, element);
		query.addPattern(measurement, polHasValue, value);
		query.addPattern(measurement, unitHasUnit, unit);
		
		query.addPattern(measurement, type, MeasurementType);
		query.addPattern(MeasurementType, subClassOf, RegulationViolation);
		
		//Request.getURL() (URL Object3  but do .toString())

		
		
		/*
		 * semantecodata : violation - excessiveArsenic - Meas12
			a semantecovocab : Violation ;
			prov : specializationOf src1 : Measurement -12;
			prov : wasDerivedFrom src1 : Measurement -12;
			prov : wasDerivedFrom src2 : ExcessiveArsenic ;
			void : inDataSet semantecodata : bbqDataSet -223.
		 */
		
		/*	 
		 semantecodata : bbqDataSet -223
		pml3 : authoredBy
		<http :// purl .org/twc/ semanteco / source / semanteco >;
		dcterms : contributor
		<http :// purl .org/twc/ semanteco / source /epa -gov >.		 	 
		 */
		
		/*
		 src1 : Measurement -12
		void : inDataSet semantecodata : graph11 .
		semantecodata : graph11
		prov : wasDerivedFrom src1 : CSVFile112 .
		src1 : CSVFile112
		prov : wasQuotedFrom http :// epa.gov/ DataURL4 .
		http :// epa.gov/ DataURL4
		pml3 : authoredBy
		<http :// purl .org/twc/ semanteco / source /epa -gov > .
		 */
		
		//even though I am retrieving the combined model , best still to use "execute" ?
		//return config.getQueryExecutor(request).accept("text/turtle").execute(query, model);			
		return config.getQueryExecutor(request).accept("text/turtle").executeLocalQuery(query);			

		
	}
	
	@QueryMethod
	public String birdTester(final Request request){
		Model model = request.getCombinedModel();
		ByteArrayOutputStream bufferedModel = new ByteArrayOutputStream();
		model.write(bufferedModel);
		ByteArrayInputStream byteStream = new ByteArrayInputStream(bufferedModel.toByteArray());
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		
		try {
			
			OWLOntology ontology = manager.loadOntologyFromOntologyDocument(byteStream);
			PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
			OWLDataFactory dataFactory = manager.getOWLDataFactory();
			OWLClass birdSite = dataFactory.getOWLClass(IRI.create("http://escience.rpi.edu/ontology/semanteco/2/0/bird.owl#BirdSite"));
			reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS);
			//compute the time it takes to execute
		   // long startTimeInNs = threadMXBean.getCurrentThreadCpuTime();

			Set<OWLNamedIndividual> birdSites = reasoner.getInstances(birdSite, false).getFlattened();
	    	//stopTimeInNs = threadMXBean.getCurrentThreadCpuTime();
	    	//totalTimeInMs = (stopTimeInNs - startTimeInNs)/1000000;
	    	//System.out.println("Classification time is: " + totalTimeInMs + " milliseconds with " + reasonerName);	

			return birdSites.toString();


			
		}
		catch(Exception e){
			
		}
		
		return null;
	}
	
	@QueryMethod
	public String owlApiTester(final Request request){
		
		
		
		//assume you have the jena model converted into an owl model, by calling OwlapiQueryExecutorImpl.executeLocalQuery
		Model model = request.getCombinedModel();
		ByteArrayOutputStream bufferedModel = new ByteArrayOutputStream();
		model.write(bufferedModel);
		ByteArrayInputStream byteStream = new ByteArrayInputStream(bufferedModel.toByteArray());
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		try {
			OWLOntology ontology = manager.loadOntologyFromOntologyDocument(byteStream);
			//return executeOwlapiQuery(query, ontology, manager);
			PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
			OWLDataFactory dataFactory = manager.getOWLDataFactory();
			//dataFactory.
			
			OWLClass MeasurementSite = dataFactory.getOWLClass(IRI.create("http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#MeasurementSite"));
			OWLClass PollutedSite = dataFactory.getOWLClass(IRI.create("http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#PollutedSite"));	
			OWLClass WaterSite = dataFactory.getOWLClass(IRI.create("http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#WaterSite"));
			OWLClass Facility = dataFactory.getOWLClass(IRI.create("http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#Facility"));
			
			//test just for bird only
			
			//one question is , does the query for a specific site's measurement use the reasoner?
			
			
			reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS);
			Set<OWLNamedIndividual> measurementSites = reasoner.getInstances(MeasurementSite, false).getFlattened();
			Set<OWLNamedIndividual> pollutedSites = reasoner.getInstances(PollutedSite, false).getFlattened();
			Set<OWLNamedIndividual> waterSites = reasoner.getInstances(WaterSite, false).getFlattened();
			Set<OWLNamedIndividual> facilities = reasoner.getInstances(Facility, false).getFlattened();
			
			Set<OWLClassExpression> newOperands = new HashSet<OWLClassExpression>();
			newOperands.add(Facility); //collecting all the operands
			newOperands.add(PollutedSite); //collecting all the operands
			OWLObjectIntersectionOf exp;
			exp = dataFactory.getOWLObjectIntersectionOf(newOperands);
			Set<OWLNamedIndividual> facilitiesAndPollutedsites = reasoner.getInstances(exp, false).getFlattened();
			
			newOperands = new HashSet<OWLClassExpression>();
			OWLObjectComplementOf notPollutedSite = dataFactory.getOWLObjectComplementOf(PollutedSite);
			newOperands.add(notPollutedSite);
			newOperands.add(Facility);
			exp = dataFactory.getOWLObjectIntersectionOf(newOperands);
			Set<OWLNamedIndividual> facilitiesAndNotPollutedsites = reasoner.getInstances(exp, false).getFlattened();
			
			//not facility and polluted
			newOperands = new HashSet<OWLClassExpression>();
			OWLObjectComplementOf notFacility = dataFactory.getOWLObjectComplementOf(Facility);
			Set<OWLNamedIndividual> notFacilities = reasoner.getInstances(notFacility, false).getFlattened();

			newOperands.add(PollutedSite); //collecting all the operands
			newOperands.add(notFacility); //collecting all the operands
			Set<OWLNamedIndividual> pollutedSitesOnly = reasoner.getInstances(PollutedSite, false).getFlattened();

			exp = dataFactory.getOWLObjectIntersectionOf(newOperands);
			Set<OWLNamedIndividual> notFacilitiesAndPollutedSites = reasoner.getInstances(exp, false).getFlattened();
			//dataFactory.
			
			Hashtable<String, String> table = new Hashtable<String, String>();
			
			table.put("notFacilities", notFacilities.toString()); // wrongfully empty still
			table.put("notFacilitiesAndPollutedSites",notFacilitiesAndPollutedSites.toString()); // !!!this is empty because there is nothing disjointed with facilities!
			//water facility is under water site. it would be better is usgs water sites were something more specific than water site to differentiate it logically from epa facilities.
			//if you change this in the ontology you have to also check where this is modified in the queries.
			//would have to change <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#WaterSite-USGS-374730122235701>
			// from watersite to usgswater site.
			
			//water facility->water site
			//polluted facility or polluted site -> polluted thing
			//polluting facility -> polluted site
			//polluted site = measurement site and polluted thing
			
			//how about:
			//water facility (epa) or usgs site -> water site. (didn't i write something about this down?)
			
			//if you were to do the intended complement  "manually":
			//for each individual in polluted sites,
			//check if it is in the set of facilities. if it is: throw away, otherwise put it in a set.
			Set pollutedNotFacility = new HashSet();
			for(final OWLNamedIndividual pollutedSite : pollutedSites){
				boolean  pollutedSiteAFacility = false;
				for(final OWLNamedIndividual facility : facilities){
					
					if(pollutedSite.toString().equals(facility.toString())){
						pollutedSiteAFacility = true; break;
					}
				}			
				if (pollutedSiteAFacility == false){pollutedNotFacility.add(pollutedSite);}
			}

			table.put("pollutedNotFacility", pollutedNotFacility.toString()); //works
			table.put("facilities", facilities.toString()); // works


			table.put("pollutedSitesOnly", pollutedSitesOnly.toString()); // 8 entries, some are usgs and some are facilities (epa)
			table.put("facilitiesAndNotPollutedsites",facilitiesAndNotPollutedsites.toString()); //should be empty and is
			table.put("facilitiesAndPollutedsites",facilitiesAndPollutedsites.toString()); // should have four entries and does

			//{pollutedSitesOnly=[], notFacilities=[], 
			//facilitiesAndPollutedsites=[<http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#facility-110025229697>, 
			//<http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#facility-110000730521>, 
			//<http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#facility-110010468403>, 
			//<http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#facility-110000786178>], facilitiesAndNotPollutedsites=[]}
			
			/*
			 * {notFacilitiesAndPollutedSites=[], 
			 * pollutedSitesOnly=[<http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#facility-110025229697>, 
			 * <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#facility-110000730521>, <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#WaterSite-USGS-374730122235701>, 
			 * <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#WaterSite-USGS-374541122251201>, <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#facility-110010468403>, 
			 * <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#facility-110000786178>, <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#WaterSite-USGS-374451122251301>, 
			 * <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#WaterSite-USGS-374658122242401>], 
			 * notFacilities=[], 
			 * facilitiesAndPollutedsites=[<http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#facility-110025229697>, 
			 * <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#facility-110000730521>, <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#facility-110010468403>, 
			 * <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#facility-110000786178>], 
			 * facilitiesAndNotPollutedsites=[]}
			 */
			
			

			return table.toString();
			//return notFacilitiesAndPollutedSites.toString();
			
			//doesn'tw rok show so both facilities and show pollutes sites in two different lists.
			//put each results into a hash table that you do 'toString" with.

			
			
			//compute the instances of the intersection of facilities and polluted
			//compute the instances of the intersection of facilites and not polluted (compliment?)
			//compute the instances of the intersection of not facility but polluted
			//compute the instances of the intersection of not facility and not polluted.
			/*
			327               var iconfile;
		    328               if(facility && polluted)
		    329                 iconfile = "image/facilitypollute.png";
		    330               else if(facility && !polluted)
		    331                 iconfile = "image/facility.png";
		    332               else if(!facility && polluted)
		    333                 iconfile = "image/pollutedwater.png";
		    334               else if(!facility && !polluted)
		    335                 iconfile = "image/cleanwater2.png";
			*/

			
			//Set<OWLAxiom> axioms = ontology.getAxioms();
			//for (OWLAxiom axiom : axioms){
				//axiom.			
			//}
			
			//loop over each individual
			//for(final OWLNamedIndividual indiv : indivSet){
				
				//http://owlapi.sourceforge.net/javadoc/org/semanticweb/owlapi/model/OWLNamedIndividual.html
				//System.out.println(indiv.getDataPropertiesInSignature().toString());
				//System.out.println(indiv.getDataPropertyValues(ontology).toString());
				
				//return indiv.getDataPropertyValues(ontology).toString();
				//return indiv.getObjectPropertyValues(ontology).toString();
				
				//the above currently works and outputs:
				//<http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#hasMeasurement>=[<http://sparql.tw.rpi.edu/source/epa-gov/dataset/echo-measurements-ca/ca0110116/version/2011-Mar-19/waterMeasurement_100_31>, <http://sparql.tw.rpi.edu/source/epa-gov/dataset/echo-measurements-ca/ca0110116/version/2011-Mar-19/waterMeasurement_100_42>,
				//need to reshape this to be a format similar to below.
				//check on how pol:Facility and pol:Polluted Site are used on the javascript side.
				
				/*
				 * needs to look like the following:
				 * 
				 * 
				 * RegulationModule.queryForSites({}, function(d)
                   { console.log(d); });
				 * 
				 * {
  "head": {
    "vars": [ "site" , "lat" , "lng" , "facility" , "polluted" , "label" , "isWater" ]
  } ,
  "results": {
    "bindings": [
      {
        "site": { "type": "uri" , "value": "http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#WaterSite-USGS-374730122235701" } ,
        "lat": { "datatype": "http://www.w3.org/2001/XMLSchema#decimal" , "type": "typed-literal" , "value": "37.7915955" } ,
        "lng": { "datatype": "http://www.w3.org/2001/XMLSchema#decimal" , "type": "typed-literal" , "value": "-122.4002487" } ,
        "facility": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "false" } ,
        "polluted": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "true" } ,
        "label": { "type": "literal" , "value": "002S005W03C001M" } ,
        "isWater": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "true" }
      } ,
      {
        "site": { "type": "uri" , "value": "http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#WaterSite-USGS-374438122244501" } ,
        "lat": { "datatype": "http://www.w3.org/2001/XMLSchema#decimal" , "type": "typed-literal" , "value": "37.7438194" } ,
        "lng": { "datatype": "http://www.w3.org/2001/XMLSchema#decimal" , "type": "typed-literal" , "value": "-122.413582" } ,
        "facility": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "false" } ,
        "polluted": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "false" } ,
        "label": { "type": "literal" , "value": "002S005W21KS01M" } ,
        "isWater": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "true" }
      } ,
      {
        "site": { "type": "uri" , "value": "http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#WaterSite-USGS-374632122251001" } ,
        "lat": { "datatype": "http://www.w3.org/2001/XMLSchema#decimal" , "type": "typed-literal" , "value": "37.7754851" } ,
        "lng": { "datatype": "http://www.w3.org/2001/XMLSchema#decimal" , "type": "typed-literal" , "value": "-122.4205266" } ,
        "facility": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "false" } ,
        "polluted": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "false" } ,
        "label": { "type": "literal" , "value": "002S005W09F001M" } ,
        "isWater": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "true" }
      } ,
      {
        "site": { "type": "uri" , "value": "http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#WaterSite-USGS-374658122242401" } ,
        "lat": { "datatype": "http://www.w3.org/2001/XMLSchema#decimal" , "type": "typed-literal" , "value": "37.782707" } ,
        "lng": { "datatype": "http://www.w3.org/2001/XMLSchema#decimal" , "type": "typed-literal" , "value": "-122.4077488" } ,
        "facility": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "false" } ,
        "polluted": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "true" } ,
        "label": { "type": "literal" , "value": "002S005W04R001M" } ,
        "isWater": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "true" }
      } ,
      {
        "site": { "type": "uri" , "value": "http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#facility-110000730521" } ,
        "lat": { "datatype": "http://www.w3.org/2001/XMLSchema#decimal" , "type": "typed-literal" , "value": "37.740265" } ,
        "lng": { "datatype": "http://www.w3.org/2001/XMLSchema#decimal" , "type": "typed-literal" , "value": "122.390274" } ,
        "facility": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "true" } ,
        "polluted": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "true" } ,
        "label": { "type": "literal" , "value": "SOUTHEAST WPCP" } ,
        "isWater": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "true" }
      } ,
      {
        "site": { "type": "uri" , "value": "http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#WaterSite-USGS-374541122251201" } ,
        "lat": { "datatype": "http://www.w3.org/2001/XMLSchema#decimal" , "type": "typed-literal" , "value": "37.7613188" } ,
        "lng": { "datatype": "http://www.w3.org/2001/XMLSchema#decimal" , "type": "typed-literal" , "value": "-122.421082" } ,
        "facility": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "false" } ,
        "polluted": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "true" } ,
        "label": { "type": "literal" , "value": "002S005W16E001M" } ,
        "isWater": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "true" }
      } ,
      {
        "site": { "type": "uri" , "value": "http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#WaterSite-USGS-374451122251301" } ,
        "lat": { "datatype": "http://www.w3.org/2001/XMLSchema#decimal" , "type": "typed-literal" , "value": "37.7474304" } ,
        "lng": { "datatype": "http://www.w3.org/2001/XMLSchema#decimal" , "type": "typed-literal" , "value": "-122.4213598" } ,
        "facility": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "false" } ,
        "polluted": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "true" } ,
        "label": { "type": "literal" , "value": "002S005W21E001M" } ,
        "isWater": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "true" }
      } ,
      {
        "site": { "type": "uri" , "value": "http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#facility-110025229697" } ,
        "lat": { "datatype": "http://www.w3.org/2001/XMLSchema#decimal" , "type": "typed-literal" , "value": "37.830322" } ,
        "lng": { "datatype": "http://www.w3.org/2001/XMLSchema#decimal" , "type": "typed-literal" , "value": "122.369858" } ,
        "facility": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "true" } ,
        "polluted": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "true" } ,
        "label": { "type": "literal" , "value": "TREASURE ISLAND  WWTP" } ,
        "isWater": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "true" }
      } ,
      {
        "site": { "type": "uri" , "value": "http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#facility-110000786178" } ,
        "lat": { "datatype": "http://www.w3.org/2001/XMLSchema#decimal" , "type": "typed-literal" , "value": "37.756638" } ,
        "lng": { "datatype": "http://www.w3.org/2001/XMLSchema#decimal" , "type": "typed-literal" , "value": "122.386993" } ,
        "facility": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "true" } ,
        "polluted": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "true" } ,
        "label": { "type": "literal" , "value": "POTRERO POWER PLANT" } ,
        "isWater": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "true" }
      } ,
      {
        "site": { "type": "uri" , "value": "http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#facility-110010468403" } ,
        "lat": { "datatype": "http://www.w3.org/2001/XMLSchema#decimal" , "type": "typed-literal" , "value": "37.728027" } ,
        "lng": { "datatype": "http://www.w3.org/2001/XMLSchema#decimal" , "type": "typed-literal" , "value": "122.504486" } ,
        "facility": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "true" } ,
        "polluted": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "true" } ,
        "label": { "type": "literal" , "value": "OCEANSIDE WWTP" } ,
        "isWater": { "datatype": "http://www.w3.org/2001/XMLSchema#boolean" , "type": "typed-literal" , "value": "true" }
      }
    ]
  }
}

327               var iconfile;
    328               if(facility && polluted)
    329                 iconfile = "image/facilitypollute.png";
    330               else if(facility && !polluted)
    331                 iconfile = "image/facility.png";
    332               else if(!facility && polluted)
    333                 iconfile = "image/pollutedwater.png";
    334               else if(!facility && !polluted)
    335                 iconfile = "image/cleanwater2.png";
    
    webapp/src/main/resources/web/water/map.js
    
    //class for facility, polluted
    //we have a class for all sites.
     //for each site, 
      * 
      * query for sites:
      * 
      * PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#>
PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
SELECT ?site ?lat ?lng (EXISTS { ?site a pol:Facility } as ?facility) (EXISTS { ?site a pol:PollutedSite } as ?polluted) ?label (EXISTS { ?site a water:WaterSite } as ?isWater) 
WHERE 
{
?site <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#MeasurementSite> . 
?site <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat . 
?site <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?lng . 
OPTIONAL {
?site <http://www.w3.org/2000/01/rdf-schema#label> ?label . 
}
}
 

				 */

				
				
				//((Object) indiv).getDataPropertyValues();
				
				//ontology.getAxioms(AxiomType<T> OWLIndividualAxiom);
				/*
				 * ont.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION);
		ont.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION);
		ont.getAxioms(AxiomType.)
				 */
				
			//your test oppl query is:
				// String query1 = "?A:CLASS SELECT ?A SubClassOf 'Heart disease (disorder)' BEGIN ADD ?A SubClassOf !Candidate END;";
				// String query1 = "?A:INDIVIDUAL SELECT ?A instanceOf 'Water measurement' BEGIN ADD ?A instanceOf !Candidate END;";

				//but does oppl support queries on instances?
			
	//		}
			
			
			//ontology.getAxioms();
			
			//if this works, use the owlapi to iterate over the axioms of an individual and get the measurement data based on properties.
			//OWLNamedIndividual name;
			//name.
			
			/*
			 * Alternatively you may want to try using OPPL
			 */
			
		//	return temp.toString();
			
		
			

		} catch (OWLOntologyCreationException e) {
			//log.warn("OWL API unable to parse combined model exported by Jena.");
		}
		
		
		//what you are implementing here is the executeOwlapiQuery of that class.
		
	
		
		return null;
	}
	
	@Override
	public void visit(final Model model, final Request request) {
		// TODO populate data model
	}

	@Override
	public void visit(final OntModel model, final Request request) {
		// TODO populate ontology model
	}

	@Override
	public void visit(final Query query, final Request request) {
		// TODO modify queries
	}
	
	@Override
	public void visit(final SemantEcoUI ui, final Request request) {
		// TODO add resources to display
	}

	@Override
	public String getName() {
		return "Provenance";
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

}
