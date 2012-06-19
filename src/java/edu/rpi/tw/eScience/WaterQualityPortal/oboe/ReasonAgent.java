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
	//static String OBOEHostDir="http://localhost/ontologies/oboe/";
	//static String TWCHostDir="http://localhost/ontologies/twc/";	
	//protected static boolean FOR_TEST=false; //true false
	
	private ConfigReader confReader = null;
	private String OBOEHostDir=null;
	private String TWCHostDir=null;
	private boolean ForTest=false;
	private String OntologyFamily=null;
	private String Regulation=null;
	private String MeasurementData=null;
	private String SiteData=null;
	
	public ReasonAgent(String configFile){
		confReader = new ConfigReader(configFile);
		confReader.printPropertyList();
		initFromConfiguration();
	}
	
	private void initFromConfiguration(){
		OBOEHostDir=confReader.getProperty("OBOEHostDir");
		TWCHostDir=confReader.getProperty("TWCHostDir");
		ForTest=(confReader.getProperty("ForTest").compareTo("true")==0?true:false);
		OntologyFamily=confReader.getProperty("OntologyFamily");
		Regulation=confReader.getProperty("Regulation");
		MeasurementData=confReader.getProperty("MeasurementData");
		SiteData=confReader.getProperty("SiteData");
		
		if(Regulation==null || Regulation.length()==0){
			System.err.println("Regulation is not correctly specified.");
			System.exit(-1);
		}
		if(MeasurementData==null || MeasurementData.length()==0){
			System.err.println("MeasurementData is not correctly specified.");
			System.exit(-1);
		}
		//SiteData can be null, since we sometimes do reasoning without site data
	}
	
	protected Model loadOntologies(String ontoType) {
		// Load ontologies
		Model owlModel;
		owlModel = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

		if(ontoType.compareTo("OBOE")==0)
			readOntologies(owlModel, OBOEHostDir);
		else if(ontoType.compareTo("TWC")==0)
			readOntologies(owlModel, TWCHostDir);
		else {
			System.err.println("In loadOntologies, the type of the ontoloy family "+
					ontoType+" is not supported");
			System.exit(-1);
		}				
		
		return owlModel;
	}
	
	protected Model readOntologies(Model owlModel, String dir) {
		owlModel.read(dir+Regulation);
		owlModel.read(dir+MeasurementData, "TTL");	
		if(SiteData!=null && SiteData.length()!=0)
			owlModel.read(dir+SiteData, "TTL");
		return owlModel;
	}
	
	protected Model loadOBOEOntologies(Model owlModel) {
		//owlModel.read(OBOEHostDir+"testPol.owl");
		//owlModel.read(OBOEHostDir+"oboe-pol.owl");		
		if(ForTest){
			//owlModel.read(OBOEHostDir+"testRegRI.owl");
			//owlModel.read(OBOEHostDir+"ri-test.owl");
			owlModel.read(OBOEHostDir+"ri-regulation-OBOE.owl");
			//owlModel.read(OBOEHostDir+"US-44-003-site.csv.e1.ttl", "TTL");
			//owlModel.read(OBOEHostDir+"US-44-003-result.sample.ttl", "TTL");	
			owlModel.read(OBOEHostDir+"US-44-003-result.ArsenicSample.csv.ttl", "TTL");				
		}
		else{			
			//owlModel.read(OBOEHostDir+"ri-test.owl");
			owlModel.read(OBOEHostDir+"ri-regulation-OBOE.owl");
			//owlModel.read(OBOEHostDir+"US-44-003-site.csv.e1.ttl", "TTL");
			//owlModel.read(OBOEHostDir+"US-44-003-result.csv.ttl", "TTL");
			owlModel.read(OBOEHostDir+"US-44-003-result.csv.h1000.ttl", "TTL");	
		}
		
		return owlModel;
	}
	
	protected Model loadTWCOntologies(Model owlModel) {
		owlModel.read(TWCHostDir+"ri-regulation.owl");
		owlModel.read(TWCHostDir+"US-44-003-result.csv.h1000.e1.ttl", "TTL");
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
		
		if(ForTest)
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
		
		if(ForTest)
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
	
	public void execute(){
		long exeStart = System.currentTimeMillis();
		Model model=loadOntologies(OntologyFamily);
		executeReasoning(model, OntologyFamily);
		System.err.println("Finished in "+(System.currentTimeMillis()-exeStart)+" ms");
	}
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: ./ReasonAgent configFile");
			System.exit(0);
		}
		String config=args[0];
		ReasonAgent agent = new ReasonAgent(config);
		agent.execute();
	}

}
