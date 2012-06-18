package edu.rpi.tw.eScience.WaterQualityPortal.oboe;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class ReasonAgent {
	static String OBOE_HOST_DIR="http://localhost/ontologies/oboe/";
	static String TWC_HOST_DIR="http://localhost/ontologies/twc/";	
	protected static boolean FOR_TEST=false; //true false
	
	protected Model loadOntologies(String ontoType) {
		// Load ontologies
		Model owlModel;
		owlModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

		if(ontoType.compareTo("OBOE")==0)
			loadOBOEOntologies(owlModel);
		else if(ontoType.compareTo("TWC")==0)
			loadTWCOntologies(owlModel);
		else {
			System.err.println("In loadOntologies, the type of the ontoloy family "+
					ontoType+" is not supported");
			System.exit(-1);
		}
		
		return owlModel;
	}
	
	protected Model loadOBOEOntologies(Model owlModel) {
		//owlModel.read(OBOE_HOST_DIR+"testPol.owl");
		//owlModel.read(OBOE_HOST_DIR+"oboe-pol.owl");		
		if(FOR_TEST){
			//owlModel.read(OBOE_HOST_DIR+"testRegRI.owl");
			//owlModel.read(OBOE_HOST_DIR+"ri-test.owl");
			owlModel.read(OBOE_HOST_DIR+"ri-regulation-OBOE.owl");
			//owlModel.read(OBOE_HOST_DIR+"US-44-003-site.csv.e1.ttl", "TTL");
			//owlModel.read(OBOE_HOST_DIR+"US-44-003-result.sample.ttl", "TTL");	
			owlModel.read(OBOE_HOST_DIR+"US-44-003-result.ArsenicSample.csv.ttl", "TTL");	
		}
		else{			
			//owlModel.read(OBOE_HOST_DIR+"ri-test.owl");
			owlModel.read(OBOE_HOST_DIR+"ri-regulation-OBOE.owl");
			//owlModel.read(OBOE_HOST_DIR+"US-44-003-site.csv.e1.ttl", "TTL");
			//owlModel.read(OBOE_HOST_DIR+"US-44-003-result.csv.ttl", "TTL");
			owlModel.read(OBOE_HOST_DIR+"US-44-003-result.csv.h1000.ttl", "TTL");	
		}
		return owlModel;
	}
	
	protected Model loadTWCOntologies(Model owlModel) {
		owlModel.read(TWC_HOST_DIR+"ri-regulation.owl");
		owlModel.read(TWC_HOST_DIR+"US-44-003-result.csv.h1000.e1.ttl", "TTL");
		return owlModel;
	}
	
	protected String buildQueryForOBOE(){
		String	testQuery = "prefix oboe-core: <http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#> "+
				"prefix oboe-pol: <http://escience.rpi.edu/ontology/semanteco/2/0/oboe-pollution.owl#> "+
				"prefix ri-regulation-OBOE: <http://escience.rpi.edu/ontology/semanteco/2/0/data/oboe/reg/ri-regulation-OBOE.owl#> "+					
					"select ?violation "+ //?obsOfViolation ?violation ?measurement
					"where{ "+
					"?violation a ri-regulation-OBOE:ExcessiveArsenicMeasurement. "+
					//"?obsOfViolation a oboe-pol:ObservationOfRegulationViolation. "+
					//"?violation a oboe-pol:RegulationViolation. "+					
					/*"?violation a oboe-pol::RegulationViolation. "+
					"?violation a  ri-oboe:ExcessiveNitrateMeasurement. "+
					"?measurement a oboe-core:Measurement. "+*/
					"}";

		String	queryString = "prefix oboe-core: <http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#> "+
		"prefix oboe-pol: <http://escience.rpi.edu/ontology/semanteco/2/0/oboe-pollution.owl#> "+
				//"prefix epa: <http://escience.rpi.edu/ontology/semanteco/2/0/EPA-regulation.owl#> "+
				"prefix ri-regulation-OBOE: <http://escience.rpi.edu/ontology/semanteco/2/0/data/oboe/reg/ri-regulation-OBOE.owl#> "+						
					"prefix wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#> "+
					"select distinct ?pollutedSite "+ //?lat ?long ?obsOfViolation ?violation ?measurement
					"where{ "+
					"?obsOfViolation a oboe-pol:ObservationOfRegulationViolation. "+
					"?obsOfViolation oboe-core:hasContext ?obsContext. "+
					"?context oboe-core:ofEntity oboe-pol:SpatialLocationEntity. "+
					"?context oboe-core:hasMeasurement ?locationMea. "+
					"?locationMea oboe-core:hasValue ?pollutedSite. "+
					//"?pollutedSite wgs:lat ?lat. "+
					//"?pollutedSite wgs:long ?long. "+
					/*"?violation a pol:RegulationViolation. "+
					"?violation a epa:ExcessiveNitrateMeasurement. "+
					"?measurement a oboe-core:Measurement. "+*/
					"}";		
		
		if(FOR_TEST)
			queryString = testQuery;
	
		return queryString;
	}
	
	protected String buildQueryForTWC(){
		String	testQuery = "prefix pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#> "+
					"select ?violation "+ //?obsOfViolation ?violation ?measurement
					"where{ "+
					"?violation a pol:RegulationViolation. "+
					//"?obsOfViolation a oboe-pol:ObservationOfRegulationViolation. "+
					//"?violation a oboe-pol:RegulationViolation. "+					
					/*"?violation a oboe-pol::RegulationViolation. "+
					"?violation a  ri-oboe:ExcessiveNitrateMeasurement. "+
					"?measurement a oboe-core:Measurement. "+*/
					"}";
		
		String	queryString = "prefix pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#> "+
					"prefix wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#> "+
					"select distinct ?pollutedSite "+ //?lat ?long ?obsOfViolation ?violation ?measurement
					"where{ "+
					"?violation a pol:RegulationViolation. "+
					"?violation pol:hasSite ?pollutedSite. "+
					//"?pollutedSite wgs:lat ?lat. "+
					//"?pollutedSite wgs:long ?long. "+
					/*"?violation a pol:RegulationViolation. "+
					"?violation a epa:ExcessiveNitrateMeasurement. "+
					"?measurement a oboe-core:Measurement. "+*/
					"}";
		
		if(FOR_TEST)
			queryString = testQuery;
		
		return queryString;
	}
	
	protected void executeReasoning(Model model, String ontoType){
		QueryExecution qe;
		ResultSet queryResults;
		String queryString="";
		if(ontoType.compareTo("OBOE")==0)
			queryString=buildQueryForOBOE();
		else if(ontoType.compareTo("TWC")==0)
			queryString=buildQueryForTWC();
		else {
			System.err.println("In executeReasoning, the type of the ontoloy family "+
					ontoType+" is not supported");
			System.exit(-1);
		}
			
		qe = QueryExecutionFactory.create(QueryFactory.create(queryString, Syntax.syntaxSPARQL_11), model);
		queryResults = qe.execSelect();			
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ResultSetFormatter.outputAsJSON(baos, queryResults);
		//result = baos.toString("UTF-8");
		//for debug 
		try {
			System.out.println(baos.toString("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			System.err.println("In testReason "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void execute(String ontoType){
		long exeStart = System.currentTimeMillis();
		Model model=loadOntologies(ontoType);
		executeReasoning(model, ontoType);
		System.err.println("Finished in "+(System.currentTimeMillis()-exeStart)+" ms");
	}
	
	public static void main(String[] args) {
		ReasonAgent agent = new ReasonAgent();
		agent.execute("TWC");
	}

}
