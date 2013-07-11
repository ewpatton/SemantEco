package edu.rpi.tw.escience.semanteco.annotator;
import static edu.rpi.tw.escience.semanteco.query.Query.VAR_NS;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.openrdf.repository.Repository;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.FileUtils;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;

import edu.rpi.tw.data.csv.impl.DefaultEnhancementParameters;
import edu.rpi.tw.data.rdf.utils.pipes.starts.Cat;
import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.HierarchicalMethod;
import edu.rpi.tw.escience.semanteco.HierarchyEntry;
import edu.rpi.tw.escience.semanteco.HierarchyVerb;
import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.QueryMethod;
import edu.rpi.tw.escience.semanteco.QueryMethod.HTTP;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.query.OptionalComponent;
import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.QueryResource;
import edu.rpi.tw.escience.semanteco.query.Variable;
import edu.rpi.tw.escience.semanteco.query.Query.Type;

import rpi.AnnotatorTester;

/*
 * treat enhancements atomically
 * 
 * literal:
 * replace conversion:range range with mapping
 * if the mapping is an owl class then it should always be Resource.
 * you can check against a literal list on the server side.
 * 
 * 
 * 
 * for class:
 * range of csvHeader is used to assert range_name string.
 * 
 * if it is 
 * 
 * 
 * 
 CO.csv.e1.params.ttl

1. A column "siteID"
if a class ns1:Site is dragged into the column "siteID",
then  enhancement with ov:csvHeader "siteID" is updated to:
 "conversion:range   rdfs:Resource;",

"conversion:range   X;" where X is whatever datatype is dragged.

rangeName becomes as below. re-use csvHeader string for range_name.

 *we should automatically add namespaces.

conversion:enhance [
         conversion:class_name "Site";
         conversion:subclass_of wgs:SpatialThing;
      ];

      conversion:enhance [
         ov:csvCol          1;
         ov:csvHeader       "AQS Site ID";
         #conversion:label   "AQS Site ID";
         conversion:comment "";
         conversion:range   rdfs:Resource;
         conversion:range_name "Site";
      ];

2. When a property is dragged:
"conversion:equivalent_property wildlife:hasStateProvince;"
 */


public class AnnotatorModule implements Module {

	private ModuleConfiguration config = null;
	private static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
	private static final String BINDINGS = "bindings";
	private static final String FAILURE = "{\"success\":false}";
	private static OntModel model = null;
	private PrintWriter csvFileWriter = null;
	private FileOutputStream enhancementFileStream;
	private PrintWriter enhancementFileWriter = null;
	private String dataSetName;
	private String sourceName;
	private String csvFileLocation="/Users/apseyed/Documents/rpi/csvFile.csv";
	private String outputRdfFileLocation="/Users/apseyed/Documents/rpi/output.ttl";
	private String paramsFile = "/Users/apseyed/Documents/rpi/sample-enhancement.ttl";  
	AnnotatorTester annotatorTester = null;


	
	public void setDataSetName(String dataSetName){this.dataSetName = dataSetName;}
	public void setSourceName(String sourceName){this.sourceName = sourceName;}
	public String getDataSetName(){return this.dataSetName;}
	public String getSourceName(){return this.sourceName;}

	public void setModel(OntModel model){
		AnnotatorModule.model = model;
	}
	
	
	public OntModel getModel(){
		return AnnotatorModule.model;
	}
	
	@QueryMethod
	public String initOWLModel(Request request) throws OWLOntologyCreationException, JSONException, OWLOntologyStorageException, UnsupportedEncodingException{


			if (request.getParam("listOfOntologies") != null){
				JSONArray listOfOntologies = (JSONArray) request.getParam("listOfOntologies") ;
				this.annotatorTester = new AnnotatorTester(listOfOntologies);	
			}
			else{
				this.annotatorTester = new AnnotatorTester();
			}
			
			return "done";

		}
		

	public void initModel(Request request) {
		String ontology = "";

		//show where we load from URL the ontologies, for example ncbi taxonomy

		//JSONArray ontologies = (JSONArray) request.getParam("ontologies");
		//for each item in the JSON Array, check through ontology conditionals


		if(model == null) {


			//check for what the ontology request is, and load that only into the model.

			model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

			try{
				//model.read(is, "http://aquarius.tw.rpi.edu/projects/semantaqua/", "TTL");
			/* works */
				//model.read("https://code.ecoinformatics.org/code/semtools/trunk/dev/oboe-ext/sbclter/sbc.1.0/oboe-sbc.owl");
				//model.read("http://purl.org/dc/terms/");			
				model.read("http://www.w3.org/2003/01/geo/wgs84_pos#"); //loads
				//dcat //void

				//pellet error.


			}
			catch(Exception e)
			{
				//FileManager.get().readModel(model, config.getResource("owl-files/oboe-sbclter.owl").toString()) ;

			}

			/*
			if(ontology.equals("dcterms")){			
			model.read("http://purl.org/dc/terms/");
			}*/


			/*
			//model = ModelFactory.createOntologyModel();

			FileManager.get().readModel(model, config.getResource("owl-files/oboe-biology-sans-imports.owl").toString()) ;

			//FileManager.get().readModel(model, config.getResource("owl-files/oboe-characteristics.owl").toString()) ;

			//FileManager.get().readModel(model, config.getResource("owl-files/oboe-core.owl").toString()) ;	

			//FileManager.get().readModel(model, config.getResource("owl-files/oboe-sbclter.owl").toString()) ;
			
			FileManager.get().readModel(model, config.getResource("owl-files/oboe-temporal.owl").toString()) ;
			FileManager.get().readModel(model, config.getResource("owl-files/oboe-spatial.owl").toString()) ;
			FileManager.get().readModel(model, config.getResource("owl-files/oboe-chemistry.owl").toString()) ;
			//FileManager.get().readModel(model, config.getResource("owl-files/oboe-taxa.owl").toString()) ;
			FileManager.get().readModel(model, config.getResource("owl-files/oboe-taxa.owl").toString()) ;
			//FileManager.get().readModel(model, config.getResource("owl-files/oboe-standards.owl").toString()) ;
			 * 
			 *
			 */

		}
	}

	
////	public void initModel(Request request) throws JSONException {
		
		
		
		
	//	if(model == null) {
			
		//	try {
			//	if(annotatorTester == null){
					
					//JSONArray listOfOntologies = (JSONArray) request.getParam("listOfOntologies");
					///JSONArray listOfOntologies  = new JSONArray();
					///listOfOntologies.put("chebi");
					//annotatorTester = new AnnotatorTester(listOfOntologies);
					//System.out.println("chebi test");
					//System.out.println("chebi test2");
				//	System.out.println(annotatorTester.getChildClasses("http://purl.obolibrary.org/obo/CHEBI_50906").toString());

					
					
					
			//	}
				
				
				

		//	} 
			
		//	catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
		//		e.printStackTrace();
		//	}
			
			
	////		model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
	////		model.read("http://www.w3.org/2003/01/geo/wgs84_pos#"); //loads
			/*
			//model = ModelFactory.createOntologyModel();

			FileManager.get().readModel(model, config.getResource("owl-files/oboe-biology-sans-imports.owl").toString()) ;

			//FileManager.get().readModel(model, config.getResource("owl-files/oboe-characteristics.owl").toString()) ;

			FileManager.get().readModel(model, config.getResource("owl-files/oboe-core.owl").toString()) ;	

			FileManager.get().readModel(model, config.getResource("owl-files/oboe-sbclter.owl").toString()) ;
			
			FileManager.get().readModel(model, config.getResource("owl-files/oboe-temporal.owl").toString()) ;
			FileManager.get().readModel(model, config.getResource("owl-files/oboe-spatial.owl").toString()) ;
			FileManager.get().readModel(model, config.getResource("owl-files/oboe-chemistry.owl").toString()) ;
			FileManager.get().readModel(model, config.getResource("owl-files/oboe-taxa.owl").toString()) ;
			FileManager.get().readModel(model, config.getResource("owl-files/oboe-taxa.owl").toString()) ;
			FileManager.get().readModel(model, config.getResource("owl-files/oboe-standards.owl").toString()) ;
			*/
			
	//	}
////	}
	
	
	@QueryMethod
	public String getListofOntologies(final Request request) throws JSONException{
		//JSONArray j = new JSONArray();
		//wgs
		//semanteco ontologies
		//
		//prefix sd: http://www.w3.org/ns/sparql-service-description#
		//prefix void: http://rdfs.org/ns/void#
		//	prefix prov: http://www.w3.org/ns/prov#

	// "dcterms", "semanteco
		//j.put("dcterms");
		//j.put("prov");
		//j.put("void");
		//j.put("semanteco-water");
		return this.annotatorTester.getListOfOntologies().toString();
		//in the future allow a client to indicate what to load, maybe...
	}

	/**
	 * Reads the csvFile from params and writes to csvFileWriter to be used for conversion to RDF by csv2rdf4lod
	 * This is currently functional.
	 * @param request
	 * @return
	 * @throws FileNotFoundException 
	 */
	@QueryMethod(method=HTTP.POST)
	public String readCsvFileForInitialConversion(final Request request) throws FileNotFoundException{
		System.out.println("the request object is : " + request.toString());
		String csvFileAsString = (String) request.getParam("csvFile");
		//System.out.println("file : " + file);
		System.out.println(request.getParam("csvFile"));
		
		request.getLogger().debug("The file object is of type : " + request.getParam("csvFile").getClass());
		csvFileWriter = new PrintWriter( csvFileLocation);
		csvFileWriter.println(csvFileAsString);
		csvFileWriter.close();
		request.getLogger().debug("CSV file written to : " + csvFileLocation);
		//FileUtils.writeStringToFile(new File("test.txt"), "Hello File");
		return null;
	}


	@QueryMethod
	public String queryForEnhancing(final Request request) throws FileNotFoundException, JSONException, OWLOntologyStorageException, OWLOntologyCreationException{

		System.out.println("source is : " +request.getParam("sourceName"));
		System.out.println("datasetName is : " + request.getParam("dataSetName"));		
		//set the source, dataset, and csv file names based on user input
		this.setDataSetName((String) request.getParam("sourceName"));
		this.setSourceName((String) request.getParam("dataSetName"));
		PrintWriter csvFile = this.csvFileWriter;
		//String csvFileString = (String) request.getParam("csvFile");
		String[] arguments = new String[] {csvFileLocation," --header-line '1'"," --delimiter ,"};
        String eId = "1";
        String surrogate = "https://github.com/timrdf/csv2rdf4lod-automation/wiki/CSV2RDF4LOD_BASE_URI#";
        String cellDelimiter = ",";
        //get these from the request object
        String username = "user";
        String sourceId = "sourceX";
        String datasetId = "datasetX";
        String machineUri = "machineX";
        String datasetVersion = "2011-Jan-24";
        String conversionID = "1";
		
		//1) run the initial conversion from here and get the enhancement file
        //get headers
		List<String> headerList = CSVHeadersForAnnotator.getHeaders(arguments);
		request.getLogger().debug("headers are : " + headerList.toString());
		//generate params file
	       generateParmsFileFromHeaders(headerList, paramsFile, surrogate, sourceId, 
	    			datasetId, datasetVersion, null, 
	    			conversionID, cellDelimiter, null, null, null,
	    			null, null, null, username, 
	    			machineUri, username);
		
		//2) get the json object from bbq statement for input to the enhancement work
		System.out.println("annotation mappings" + request.getParam("annotationMappings").toString());
		request.getLogger().debug("annotation Mappings are: " + request.getParam("annotationMappings").toString());
       // String annotationMappings = (String) request.getParam("annotationMappings");
        
		//3)do the conversion calling
		//queryForPropertyToEnhance
		//hard coded linking of class for Deviated Well
		
		//[{"url":
		//{"Property":"http://www.co-ode.org/ontologies/ont.owl#testProperty1",
		//"RangeClass":"http://ecoinformatics.org/oboe/oboe.1.0/oboe-biology.owl#AdultStageFish"}}]
		
		//queryForPropertyToEnhance(request);
		//queryForHeaderToEnhance(request);
		
		JSONArray annotations = (JSONArray) request.getParam("annotationMappings");
		System.out.println("annotationMappings JSONObject " +  annotations.toString());
		HashSet<String> rangeClasses = new HashSet<String>();
		HashSet<String> properties = new HashSet<String>();

		//for every object in that array
		for (int i = 0; i < annotations.length(); i++){
			System.out.println("current object " +  annotations.getString(i));
				System.out.println("class type is : " + annotations.get(i).getClass());
				JSONObject o = (JSONObject) annotations.get(i);
				Iterator<?> keys = o.keys();
				String headerKey = null;
				while( keys.hasNext() ){headerKey = keys.next().toString();}
				System.out.println("The key is : " + headerKey);
				//next get the object of the key and get "Property" and "RangeClass" from that key
				JSONObject propAndRangeObj =  (JSONObject) o.get(headerKey);
				System.out.println("the property is : " + propAndRangeObj.get("Property"));
				System.out.println("the range class is : " + propAndRangeObj.get("RangeClass"));
				//property must be done first!
				queryForPropertyToEnhance(request, headerKey, propAndRangeObj.get("Property").toString());
				queryForHeaderToEnhance(request, headerKey, propAndRangeObj.get("RangeClass").toString());		
				rangeClasses.add(propAndRangeObj.get("RangeClass").toString());
				properties.add(propAndRangeObj.get("Property").toString());		
		}
        //(original: writeToEnhancement)
       // writeEnhancementForRange(request);
        //writeEnhancementForRangeTester
        convertToRdfWithEnhancementsFile(csvFileLocation, paramsFile); 
        //do we have a uri for every ontology, and if a class is sleceted, how do we map it back to the uri?
        
        //iterate over all classes in annotation mappings
        for(String rangeClass : rangeClasses){
        mireotClass(rangeClass, null, outputRdfFileLocation);
       // mireotProperty(rangeClass, null, outputRdfFileLocation);
        }
        //iterate over all properties in annotation mappings
     //   for(String property : properties){
     //   mireotProperty(property, null, outputRdfFileLocation);
       // mireotProperty(rangeClass, null, outputRdfFileLocation);
      //  }
		//4) should we send the rdf file back to the client?

		return null;
		//FileManager.get().readModel(model, "/Users/apseyed/Desktop/source/scraperwiki-com/uk-offshore-oil-wells/version/2011-Jan-24/manual/uk-offshore-oil-wells-short.csv.e1.params.ttl");
	}
	
	public void convertToRdfWithEnhancementsFile(String inFilename, String enhancementParametersURL ) {
		// TODO Auto-generated method stub
		//String inFilename                 = null;
	      int    header                     = 1;
	      int    primaryKeyColumn           = 0;
	      int    uriKeyColumn               = 0;
	      String baseURI                    = null;
	      String datasetIDTag               = null;
	      String conversionTag              = null;
	      String classURI                   = null;
	      String subjectNS                  = null; boolean uuidSubject   = true;
	      String predicateNS                = null; boolean uuidPredicate = true;
	      String objectNS                   = null; boolean uuidObject    = true;
	     // String outputFileName             = null;
	      String metaOutputFileName         = null;
	      String outputExtension            = "ttl";
	      Set<String> voidFileExtensions    = null;
	      //String resourceOrLiteralBitString = null; // TODO: Deprecate
	    //  String enhancementParametersURL   = null;
	      String provenanceParametersURL    = null;
	      String converterIdentifier        = null;
	      boolean examplesOnly              = false;
	      int     sampleLimit               = -1;
	      voidFileExtensions = new HashSet<String>(); 
	    //java -Xmx3060m edu.rpi.tw.data.csv.CSVtoRDF 
			//source/uk-offshore-oil-wells-short.csv -sample 10 -ep automatic/uk-offshore-oil-wells-short.csv.raw.params.ttl 
			//-VoIDDumpExtensions ttl.gz -w automatic/uk-offshore-oil-wells-short.csv.raw.sample.ttl 
			//-id csv2rdf4lod_96add9a1c2a9b862527cd8d6e795a606   
		//outputFileName = "/Users/apseyed/Desktop/source/scraperwiki-com/uk-offshore-oil-wells/version/2011-Jan-24/automatic/uk-offshore-oil-wells-short.csv.raw.sample.ttl";
	      
	    /* shouldn't need these next two as they are passed in */  
		//inFilename = "/Users/apseyed/Desktop/source/p-scraperwiki-com/uk-offshore-oil-wells/version/2011-Jan-24/source/uk-offshore-oil-wells-short.csv";
		
		//outputFileName = "/Users/apseyed/Desktop/source/p-scraperwiki-com/uk-offshore-oil-wells/version/2011-Jan-24/automatic/uk-offshore-oil-wells-short.csv.ttl";
		//outputFileName = "/Users/apseyed/Documents/rpi/output.ttl";
		
		//enhancementParametersURL = "/Users/apseyed/Desktop/source/scraperwiki-com/uk-offshore-oil-wells/version/2011-Jan-24/automatic/uk-offshore-oil-wells-short.csv.raw.params.ttl";
		//enhancementParametersURL = "/Users/apseyed/Desktop/source/p-scraperwiki-com/uk-offshore-oil-wells/version/2011-Jan-24/manual/uk-offshore-oil-wells-short.csv.e1.params.ttl";
		converterIdentifier = "csv2rdf4lod_96add9a1c2a9b862527cd8d6e795a606";
		
		
		voidFileExtensions.add(".ttl.gz");
		// Load the initial enhancement parameters.
	      Repository enhancementParamsRep = Cat.load(enhancementParametersURL);
	      if( Cat.ENCOUNTERED_PARSE_ERROR ) {
	         System.err.println("ERROR; invalid RDF syntax in " + enhancementParametersURL);
	         System.exit(3);
	      }
	      DefaultEnhancementParameters enhancementParams = new DefaultEnhancementParameters(enhancementParamsRep, baseURI);


	         System.out.println("calling demo");

	         CSV2RDFForAnnotator csv2rdfObject = new CSV2RDFForAnnotator(inFilename,classURI, subjectNS,  uuidSubject,  predicateNS, uuidPredicate, 
                objectNS, uuidObject, enhancementParams, converterIdentifier, enhancementParametersURL,
                voidFileExtensions, examplesOnly, sampleLimit);
		
		 Repository toRDF = csv2rdfObject.toRDF(outputRdfFileLocation, metaOutputFileName);
	      System.err.println("========== edu.rpi.tw.data.csv.CSVtoRDF complete. ==========");
	          
		/*
		 * public CSVtoRDF(String inFileName,
                   String classURI,                           // This is outdated, but could become the generalization.
                   String subjectNS,   boolean uuidSubject,   // This is outdated, but could become the generalization.
                   String predicateNS, boolean uuidPredicate, // This is outdated, but could become the generalization.
                   String objectNS,    boolean uuidObject,    // This is outdated, but could become the generalization.
                   //deprecated String resourceOrLiteralBitString, 
                   EnhancementParameters enhancementParams, 
                   String converterIdentifier, String enhancementParametersURL,
                   Set<String> voidFileExtensions,
                   boolean examplesOnly, int sampleLimit)
		 */

	}
	/**
	 * @throws FileNotFoundException 
	 * 
	 */
	public void generateParmsFileFromHeaders(List<String> headers, String paramsFile, String surrogate, String sourceId, 
			String datasetId, String datasetVersion, String subjectDiscriminator, 
			String conversionID, String cellDelimiter, String header, String dataStart, String dataEnd,
			String onlyIfCol, String repeatAboveIfEmptyCol, String interpretAsNull, String username, 
			String machine_uri, String person_uri) throws FileNotFoundException{
		//header2params2.sh
		//simulates header2params2.awk
		//at /Users/apseyed/Documents/rpi/csv2rdf4lod-automation/bin/util/header2params2.awk

		//open a file stream
		enhancementFileWriter = new PrintWriter(paramsFile);

		//how do i assert prefix statements in jena?
		String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
		String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
		String todo = "http://www.w3.org/2000/01/rdf-schema#";
		String skos = "http://www.w3.org/2004/02/skos/core#";
		String time = "http://www.w3.org/2006/time#";
		String was = "http://www.w3.org/2003/01/geo/wgs84_pos#";
		String geonames = "http://www.geonames.org/ontology#";
		String geonamesid = "http://sws.geonames.org/";
		//String rdfs = "http://www.w3.org/2000/01/rdf-schema#";	
		//can you wrire curis in jena?	
		if(person_uri != null){
			//printf("<%s> foaf:holdsAccount <%s#%s> .\n",person_uri,   machine_uri,whoami);
			//create an rdf statement 
			//skipping this for now as its not crucial for conversion
		}
		enhancementFileWriter.println("@prefix owl:           <http://www.w3.org/2002/07/owl#> .");
		enhancementFileWriter.println("@prefix vann:          <http://purl.org/vocab/vann/> .");
		enhancementFileWriter.println("@prefix skos:          <http://www.w3.org/2004/02/skos/core#> .");
		enhancementFileWriter.println("@prefix time:          <http://www.w3.org/2006/time#> .");
		enhancementFileWriter.println("@prefix wgs:           <http://www.w3.org/2003/01/geo/wgs84_pos#> .");
		enhancementFileWriter.println("@prefix geonames:      <http://www.geonames.org/ontology#> .");
		enhancementFileWriter.println("@prefix geonamesid:    <http://sws.geonames.org/> .");
		enhancementFileWriter.println("@prefix govtrackusgov: <http://www.rdfabout.com/rdf/usgov/geo/us/> .");
		enhancementFileWriter.println("@prefix dbpedia:       <http://dbpedia.org/resource/> .");
		enhancementFileWriter.println("@prefix dbpediaprop:   <http://dbpedia.org/property/> .");
		enhancementFileWriter.println("@prefix dbpediaowl:    <http://dbpedia.org/ontology/> .");
		enhancementFileWriter.println("@prefix con:           <http://www.w3.org/2000/10/swap/pim/contact#> .");
		enhancementFileWriter.println( "@prefix muo:           <http://purl.oclc.org/NET/muo/muo#> .");
		enhancementFileWriter.println( "@prefix vs:            <http://www.w3.org/2003/06/sw-vocab-status/ns#> .");
		enhancementFileWriter.println( "@prefix frbr:          <http://purl.org/vocab/frbr/core#> .");
		enhancementFileWriter.println( "@prefix bibo:          <http://purl.org/ontology/bibo/> .");
		enhancementFileWriter.println("@prefix doap:          <http://usefulinc.com/ns/doap#> .");
		enhancementFileWriter.println("@prefix qb:            <http://purl.org/linked-data/cube#> .");
		enhancementFileWriter.println("@prefix dgtwc:         <http://data-gov.tw.rpi.edu/2009/data-gov-twc.rdf#> .");
		enhancementFileWriter.println("@prefix conversion:    <http://purl.org/twc/vocab/conversion/> .");
		enhancementFileWriter.println("@prefix void:          <http://rdfs.org/ns/void#> .");
		enhancementFileWriter.println("@prefix xsd:          <http://www.w3.org/2001/XMLSchema#> .");
		enhancementFileWriter.println("@prefix dcterms:         <http://purl.org/dc/terms/> .");
		enhancementFileWriter.println("@prefix foaf:         <http://xmlns.com/foaf/0.1/> .");
		enhancementFileWriter.println("@prefix ov:         <http://open.vocab.org/terms/> .");
		enhancementFileWriter.println("@prefix todo:         <http://www.w3.org/2000/01/rdf-schema#> .");
		enhancementFileWriter.println("@prefix rdf:         <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .");
		enhancementFileWriter.println("@prefix rdfs:         <http://www.w3.org/2000/01/rdf-schema#> .");


		enhancementFileWriter.println();
		enhancementFileWriter.println();

		surrogate = "https://github.com/timrdf/csv2rdf4lod-automation/wiki/CSV2RDF4LOD_BASE_URI#";
		String STEP =  "enhancement/1";
		if(conversionID != null){
			String dataset = "<" + surrogate + "/source/" + sourceId + "/dataset/" + datasetId + "/version/" + datasetVersion + "/conversion/" + STEP + ">"  ;
			enhancementFileWriter.println(dataset);
			enhancementFileWriter.println("  a conversion:LayerDataset, void:Dataset;");
			enhancementFileWriter.println();
			enhancementFileWriter.println("conversion:base_uri           " + "\"" + surrogate + "\"^^xsd:anyURI;");
			enhancementFileWriter.println("conversion:source_identifier \"" + sourceId + "\";");
			enhancementFileWriter.println("conversion:dataset_identifier \"" + datasetId + "\";");
			enhancementFileWriter.println("conversion:version_identifier \"" + datasetVersion + "\";");
			enhancementFileWriter.println("conversion:enhancement_identifier \"" + conversionID + "\";");
			enhancementFileWriter.println();
			enhancementFileWriter.println("conversion:conversion_process [");
			enhancementFileWriter.println("   a conversion:EnhancementConversionProcess;");
			enhancementFileWriter.println("   conversion:enhancement_identifier \"" + conversionID + "\";");
			enhancementFileWriter.println();
			enhancementFileWriter.println("dcterms:creator [ a foaf:OnlineAccount; foaf:accountName " + "\"" + username + "\" ];");
			//     dcterms:created "2013-03-15T01:07:39-04:00"^^xsd:dateTime;
			enhancementFileWriter.println("conversion:delimits_cell \",\";");
			//loop on columns
			int columnNumber = 1;
			for(String header1 : headers ){
				System.out.println("header: " + header1);
				enhancementFileWriter.println("     conversion:enhance [ ");
				enhancementFileWriter.println("       ov:csvCol     " + columnNumber + ";");
				enhancementFileWriter.println("       ov:csvHeader     \"" + header1 + "\" ;");
				enhancementFileWriter.println("       #conversion:label \"" + header1 + "\" ;");


				enhancementFileWriter.println("       conversion:comment     " + columnNumber + ";");
				enhancementFileWriter.println("       conversion:range     todo:Literal ; ");
				enhancementFileWriter.println("    ];");
				columnNumber++;
			}

		}
		enhancementFileWriter.println("];");
		enhancementFileWriter.println(".");
		enhancementFileWriter.close();
	}

	/**
	 * need to create new enhancement
	 * @param request
	 * @return
	 * @throws FileNotFoundException 
	 */
	@QueryMethod
	public String queryForPropertyToEnhance(final Request request, String Header, String Property) throws FileNotFoundException{
		Model model = ModelFactory.createDefaultModel();
		String conversionPrefix = "http://purl.org/twc/vocab/conversion/";
		//String enhancementFile2 = "/Users/apseyed/Desktop/source/scraperwiki-com/uk-offshore-oil-wells/version/2011-Jan-24/manual/uk-offshore-oil-wells-short.csv.e1.params2.ttl";
		//FileManager.get().readModel(model, "/Users/apseyed/Desktop/source/scraperwiki-com/uk-offshore-oil-wells/version/2011-Jan-24/manual/uk-offshore-oil-wells-short.csv.e1.params.ttl");
		FileManager.get().readModel(model, paramsFile);

		//FileOutputStream enhancementFileStream2 = new FileOutputStream(enhancementFile2);
		//Literal literalHeader = model.createLiteral("Deviated_Well");
		//Resource propertyhasSpatialLocation = model.createResource(conversionPrefix + "hasSpatialLocation");
		Literal literalHeader = model.createLiteral(Header);
		Resource propertyhasSpatialLocation = model.createResource(Property);

		Property propertyEquiv = model.createProperty(conversionPrefix + "equivalent_property");
		StmtIterator enhanceStatements1 =  model.listStatements((Resource) null, (Property) null , (Literal) literalHeader );
		Statement s = null;
		while (enhanceStatements1.hasNext()) {
			s = enhanceStatements1.next();
			System.out.println("statement is : " + s);	
		}
		Resource subjectOfHeader = s.getSubject();
		Statement equivStatement = ResourceFactory.createStatement(subjectOfHeader, propertyEquiv, propertyhasSpatialLocation);
		model.add(equivStatement);
		
		FileOutputStream paramsStream = new FileOutputStream(paramsFile);
		model.write(paramsStream, "TURTLE");
		//model.write(enhancementFileStream2, "N-TRIPLE");
		return null;	
	}
	
	/**
	 * updates the params file with a range class enhancements. 
	 * e.g., 		
		 * conversion:enhance [
         conversion:class_name "Site";
         conversion:subclass_of wgs:SpatialThing;
      ];	 
	 * @param request
	 * @return
	 * @throws FileNotFoundException
	 */
	@QueryMethod
	public String queryForHeaderToEnhance(final Request request, String header, String rangeClass) throws FileNotFoundException{
		Model model = ModelFactory.createDefaultModel();
		//Model newModel = ModelFactory.createDefaultModel();
		//String enhancementFile = "/Users/apseyed/Desktop/source/scraperwiki-com/uk-offshore-oil-wells/version/2011-Jan-24/manual/uk-offshore-oil-wells-short.csv.e1.params.ttl";
		//String enhancementFile2 = "/Users/apseyed/Desktop/source/scraperwiki-com/uk-offshore-oil-wells/version/2011-Jan-24/manual/uk-offshore-oil-wells-short.csv.e1.params2.ttl";
		//FileOutputStream enhancementFileStream = new FileOutputStream(enhancementFile);
		//FileOutputStream enhancementFileStream2 = new FileOutputStream(enhancementFile2);
		//FileManager.get().readModel(model, "/Users/apseyed/Desktop/source/scraperwiki-com/uk-offshore-oil-wells/version/2011-Jan-24/manual/uk-offshore-oil-wells-short.csv.e1.params.ttl");
		FileManager.get().readModel(model, paramsFile);

		String conversionPrefix = "http://purl.org/twc/vocab/conversion/";
		String ov = "http://open.vocab.org/terms/";
		//this should be retrieved
		//how? list statements . null,property,voiddataset
		Property propertyEnhance = model.createProperty(conversionPrefix + "enhance");
		Property propertyRange = model.createProperty(conversionPrefix + "range");
		Property propertyRangeName = model.createProperty(conversionPrefix + "range_name");
		Property propertyClassName = model.createProperty(conversionPrefix + "class_name");
		Property propertySubclassOf = model.createProperty(conversionPrefix + "subclass_of");
		Property propertyCsvHeader = model.createProperty(ov + "csvHeader");
		
		//Literal literalHeader = model.createLiteral("Deviated_Well");
		//Resource superClass = model.createResource( conversionPrefix + "SpatialLocation");
		Literal literalHeader = model.createLiteral(header);
		Resource superClass = model.createResource(rangeClass);

		StmtIterator enhanceStatements1 =  model.listStatements((Resource) null, (Property) propertyCsvHeader , (Literal) literalHeader );
		Statement s = null;
		while (enhanceStatements1.hasNext()) {
			s = enhanceStatements1.next();
			System.out.println("statement is : " + s);	
		}

		Resource subjectOfHeader = s.getSubject();
		//this is an object of what triple and what is the subject? that subject becomes subject of new triple with anonymous node enhancement.
		StmtIterator getStatement =  model.listStatements((Resource) null, (Property) propertyEnhance , (Resource) subjectOfHeader );
		Statement s2 = null;
		while (getStatement.hasNext()) {
			s2 = getStatement.next();
			System.out.println("other statement is : " + s2);	
		}
		Resource conversionProcess = s2.getSubject();
		//triple with anonymous node.
		//Node anonNode = Node.createAnon();
		Resource newAnon = model.createResource();
		Statement conversionProcessEnhanceAnon = ResourceFactory.createStatement(conversionProcess, propertyEnhance, newAnon);
		System.out.println("anon node output is : " + conversionProcessEnhanceAnon);
		model.add(conversionProcessEnhanceAnon);
		//now just add triples from anon

		Statement classNameStatement = ResourceFactory.createStatement(newAnon, propertyClassName, literalHeader);
		Statement subclassStatement = ResourceFactory.createStatement(newAnon, propertySubclassOf, superClass);
		model.add(classNameStatement);
		model.add(subclassStatement);



		//find the statement that mentions "Deviated_Well" in the ov:csvHeader

		//query for statement with header in the range.

		Statement st = null;
		StmtIterator enhanceStatements =  model.listStatements((Resource) subjectOfHeader, (Property) propertyRange, (Literal) null);
		while (enhanceStatements.hasNext()) {
			st = enhanceStatements.next();
			System.out.println("statement is : " + st);	
		}  

		Statement replacement = ResourceFactory.createStatement(st.getSubject(), st.getPredicate(), model.createResource("http://www.w3.org/2000/01/rdf-schema#" +
				"Resource"));
		Statement newst = ResourceFactory.createStatement(st.getSubject(), propertyRangeName, literalHeader);
		model.remove(st);
		//st.changeObject(model.createResource("Resource"));
		model.add(replacement);
		model.add(newst);

		FileOutputStream paramsStream = new FileOutputStream(paramsFile);
		//model.write(paramsStream, "N-TRIPLE");
		model.write(paramsStream, "TURTLE");
		return null;
	}
	//can you just change the statement in the non-iterated model?
	
	public OWLOntology mireotClass(String classUri,String ontologyUri, String ontologyFileToUpdate) throws OWLOntologyCreationException, OWLOntologyStorageException{
		//hard code both uris for testing
		//classUri = "http://www.co-ode.org/ontologies/pizza/pizza.owl#ArtichokeTopping";
		//classUri = "http://www.co-ode.org/ontologies/pizza/pizza.owl#InterestingPizza";
		if(ontologyUri == null){
		if (classUri.contains("#")){
			//split on the #
			String[] temp = classUri.split("#");
			ontologyUri = temp[0];
			System.out.println("ontology Uri is: " + classUri);
		}
		/*
		else{
			String[] temp2 = classUri.split("/");
			ontologyUri = temp2[temp2.length-1];
			System.out.println("ontology Uri is: " + classUri);
		}	
		*/
		}
		//match everything before the first pound
		//ontologyUri = "http://www.co-ode.org/ontologies/pizza/pizza.owl";
		//load the ontology from ontologyUri
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();

		OWLDataFactory dataFactory = manager.getOWLDataFactory();
		IRI documentIRI = IRI.create(ontologyUri);
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(documentIRI);
        OWLClass classForAnno = dataFactory.getOWLClass(IRI.create(classUri));
        
        //need to incorporate into an existing file
       // OWLOntology newOntology = manager2.createOntology(IRI.create("file:/Users/apseyed/Documents/rpi/mireot.owl"));
       // OWLOntology newOntology = manager2.createOntology(IRI.create("file:" + ontologyFileToUpdate));
        OWLOntology newOntology = manager.loadOntologyFromOntologyDocument(IRI.create("file:" + ontologyFileToUpdate));

        
        //first collect all annotations for a class
        Set<OWLAnnotation> annotations = classForAnno.getAnnotations(ontology);
        System.out.println("annotations: " + annotations.toString());
		// "importedFrom" annotation property		
		OWLAnnotationProperty importedFromProperty = dataFactory.getOWLAnnotationProperty(IRI.create("http://purl.obolibrary.org/obo/IAO_0000412"));
		OWLAnnotationProperty label = dataFactory.getRDFSLabel();

		//getting annotations for class from the imports, too
		for(OWLOntology ont : ontology.getImports()){
			annotations.addAll(classForAnno.getAnnotations(ont));
		}
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		HashSet<OWLAnnotation> annotationsCopy = new HashSet<OWLAnnotation>(annotations);
		
		//processing annotations
		//iterate through all annotations for the class
		for(OWLAnnotation annotation : annotationsCopy){
			//checking for any existing 'imported from' annotations
			//exclude annotations that are import annotations
			if(annotation.getProperty().getIRI().toString().equals(importedFromProperty.getIRI().toString())){
				annotations.remove(annotation);
				continue;
			}
			
			//checking for annotation property labels
			//collect all labels
			//gets the annotations for the annotation property, specifically the range of rdfs:label
			//getAnnotations(OWLOntology ontology, OWLAnnotationProperty annotationProperty)
			//Obtains the annotations on this entity where the annotation has the specified annotation property.
			Set<OWLAnnotation> propertyAnnotations = annotation.getProperty().getAnnotations(ontology, label);

			//getting annotations from the imports, too
			for(OWLOntology ont : ontology.getImports()){
				propertyAnnotations.addAll(annotation.getProperty().getAnnotations(ont, label));
			}
			
			
			for (OWLAnnotation propertyAnnotation : propertyAnnotations) {
				//Annotations are used in the various types of annotation axioms, which bind annotations to their subjects (i.e. axioms or declarations).
				// getOWLAnnotation(OWLAnnotationProperty property, OWLAnnotationValue value)
				//getOWLAnnotationAssertionAxiom(OWLAnnotationSubject subject, OWLAnnotation annotation)
				//here asserting annotation property as subject and property assertion with label info as annotation
				OWLAxiom ax = dataFactory.getOWLAnnotationAssertionAxiom(annotation.getProperty().getIRI(), propertyAnnotation);
				changes.add(new AddAxiom(newOntology, ax));
			}			
		}
		OWLAnnotation importedFromAnnotation = null;
		
		//create annotation from "imported from"
				if(ontology.getOntologyID().getOntologyIRI() != null){
					//
					importedFromAnnotation = dataFactory.getOWLAnnotation(importedFromProperty, dataFactory.getOWLLiteral(ontology.getOntologyID().getOntologyIRI().toString()));
				} else {
					importedFromAnnotation = dataFactory.getOWLAnnotation(importedFromProperty, dataFactory.getOWLLiteral(ontologyUri));
				}
				
				OWLAxiom ax = dataFactory.getOWLAnnotationAssertionAxiom(label, importedFromProperty.getIRI(), dataFactory.getOWLLiteral("imported from", "en"));
				changes.add(new AddAxiom(newOntology, ax));
				annotations.add(importedFromAnnotation);
				
				for (OWLAnnotation an : annotations) {
					OWLAxiom ax1 = dataFactory.getOWLAnnotationAssertionAxiom(classForAnno.getIRI(), an);
					changes.add(new AddAxiom(newOntology, ax1));
				}
				try{
				manager.applyChanges(changes);
				manager.saveOntology(newOntology);
				}
				catch(UnknownOWLOntologyException e){
					e.printStackTrace();
				}
				
				
				//manager2.applyChanges(annotations);
				//manager2.addAxioms(newOntology, annotations);

		System.out.println("ontology is : " + newOntology.toString());
		return null;
	}
	
	private void mireotProperty(String propertyUri,String ontologyUri, String ontologyFileToUpdate) throws OWLOntologyCreationException, OWLOntologyStorageException {
		if(ontologyUri == null){
			if (propertyUri.contains("#")){
				//split on the #
				String[] temp = propertyUri.split("#");
				ontologyUri = temp[0];
				System.out.println("ontology Uri is: " + propertyUri);
			}
			/*
			else{
				String[] temp2 = classUri.split("/");
				ontologyUri = temp2[temp2.length-1];
				System.out.println("ontology Uri is: " + classUri);
			}	
			*/
			}
			//match everything before the first pound
			//ontologyUri = "http://www.co-ode.org/ontologies/pizza/pizza.owl";
			//load the ontology from ontologyUri
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();

			OWLDataFactory dataFactory = manager.getOWLDataFactory();
			IRI documentIRI = IRI.create(ontologyUri);
	        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(documentIRI);
	        OWLObjectProperty objProp = dataFactory.getOWLObjectProperty(IRI.create(propertyUri));
	        
	        //need to incorporate into an existing file
	       // OWLOntology newOntology = manager2.createOntology(IRI.create("file:/Users/apseyed/Documents/rpi/mireot.owl"));
	       // OWLOntology newOntology = manager2.createOntology(IRI.create("file:" + ontologyFileToUpdate));
	        OWLOntology newOntology = manager.loadOntologyFromOntologyDocument(IRI.create("file:" + ontologyFileToUpdate));

	        
	        //first collect all annotations for a class
	        Set<OWLAnnotation> annotations = objProp.getAnnotations(ontology);
	        System.out.println("annotations: " + annotations.toString());
			// "importedFrom" annotation property		
			OWLAnnotationProperty importedFromProperty = dataFactory.getOWLAnnotationProperty(IRI.create("http://purl.obolibrary.org/obo/IAO_0000412"));
			OWLAnnotationProperty label = dataFactory.getRDFSLabel();

		//OWLObjectProperty objProp = msg.getObjectProperty();
		//OWLOntology ontology = msg.getOntology();

		//Set<OWLAnnotation> annotations = objProp.getAnnotations(ontology);

		//getting annotations from the imports, too
		for(OWLOntology ont : ontology.getImports()){
			annotations.addAll(objProp.getAnnotations(ont));
		}

		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		HashSet<OWLAnnotation> annotationsCopy = new HashSet<OWLAnnotation>(annotations);

		//processing annotations
		for(OWLAnnotation annotation : annotationsCopy){
			//checking for any existing 'imported from' annotations
			if(annotation.getProperty().getIRI().toString().equals(importedFromProperty.getIRI().toString())){
				annotations.remove(annotation);
				continue;
			}

			//checking for annotation property labels
			Set<OWLAnnotation> propertyAnnotations = annotation.getProperty().getAnnotations(ontology, label);

			//getting annotations from the imports, too
			for(OWLOntology ont : ontology.getImports()){
				propertyAnnotations.addAll(annotation.getProperty().getAnnotations(ont, label));
			}
			
			
			for (OWLAnnotation propertyAnnotation : propertyAnnotations) {
				OWLAxiom ax = manager.getOWLDataFactory().getOWLAnnotationAssertionAxiom(annotation.getProperty().getIRI(), propertyAnnotation);
				changes.add(new AddAxiom(newOntology, ax));
			}
			


		}

		OWLAnnotation importedFromAnnotation = null;

		if(ontology.getOntologyID().getOntologyIRI() != null){
			importedFromAnnotation = dataFactory.getOWLAnnotation(importedFromProperty, dataFactory.getOWLLiteral(ontology.getOntologyID().getOntologyIRI().toString()));
		} else {
			importedFromAnnotation = dataFactory.getOWLAnnotation(importedFromProperty, dataFactory.getOWLLiteral(ontologyUri));
		}
		
		OWLAxiom ax = dataFactory.getOWLAnnotationAssertionAxiom(label, importedFromProperty.getIRI(), dataFactory.getOWLLiteral("imported from", "en"));
		changes.add(new AddAxiom(newOntology, ax));
		
		annotations.add(importedFromAnnotation);

		manager.applyChanges(changes);
		manager.saveOntology(newOntology);
		
		//addObjectProperty(objProp, active, annotations);
	}

	@QueryMethod
	public String writeEnhancementForRangeTester(final Request request) throws FileNotFoundException{

		String type = "xsd:double";
		//you need to know what the property is.

		//		model.createLiteral(arg0)
		//can i test if a resource is a datatype?
		//we cannot assign the datatype until the dataproperty is also assigned.

		String hasProperty = "oboe:hasMeasurement";

		String header = "Deviated_Well";
		String rangeClass = "hasWell";


		FileOutputStream newEnhancementFile = new FileOutputStream("/Users/apseyed/Desktop/source/scraperwiki-com/uk-offshore-oil-wells/version/2011-Jan-24/manual/uk-offshore-oil-wells-short.csv.e1.params.ttl.new");

		//writeEnhancementForRangeTesterModel(request, header, rangeClass);		
		Model newModel = writeEnhancementForRange(header, rangeClass);

		//write model
		newModel.write(newEnhancementFile, "N-TRIPLE");

		//what patterns do we supply when the symbol refers to a code?
		//A: just that its an instance of code, or also that its "about" country Y?


		//"Derviated_Well":oboe:DeviatedWell.
		//test: replace "conversion:range todo:Literal;" with "conversion:range rdf:Resource"
		return request.toString();	
	}

	/*
public String writeEnhancementForRangeTesterModel(Request request, String header, String rangeClass) throws FileNotFoundException{


	Model model = ModelFactory.createDefaultModel();
	FileOutputStream newEnhancementFile = new FileOutputStream("/Users/apseyed/Desktop/source/scraperwiki-com/uk-offshore-oil-wells/version/2011-Jan-24/manual/uk-offshore-oil-wells-short.csv.e1.params.ttl.new");
	FileManager.get().readModel(model, "/Users/apseyed/Desktop/source/scraperwiki-com/uk-offshore-oil-wells/version/2011-Jan-24/manual/uk-offshore-oil-wells-short.csv.e1.params.ttl");
	Model newModel = ModelFactory.createDefaultModel();

	//a method: model, type of args, returns a model.
	//should modify the model not create a new one. for testing create a new file to write the model to.
	newModel = writeEnhancementForRange(model, newModel, header, rangeClass);

	//write model
	newModel.write(newEnhancementFile, "N-TRIPLE");


		return null;	
	}
	 */

	public Model writeEnhancementForRange(String header, String rangeClass){
		Model model = ModelFactory.createDefaultModel();
		Model newModel = ModelFactory.createDefaultModel();

		FileManager.get().readModel(model, "/Users/apseyed/Desktop/source/scraperwiki-com/uk-offshore-oil-wells/version/2011-Jan-24/manual/uk-offshore-oil-wells-short.csv.e1.params.ttl");

		String conversionPrefix = "http://purl.org/twc/vocab/conversion/";
		//this should be retrieved
		//how? list statements . null,property,voiddataset
		String dataset = "<https://github.com/timrdf/csv2rdf4lod-automation/wiki/CSV2RDF4LOD_BASE_URI#/source/scraperwiki-com/dataset/uk-offshore-oil-wells/version/2011-Jan-24/conversion/enhancement/1>";
		Resource subjectDataSet = model.createResource(dataset);
		Property propertyConversionProcess = model.createProperty(conversionPrefix + "conversion_process");
		Property propertyEnhance = model.createProperty(conversionPrefix + "enhance");
		Property propertyRange = model.createProperty(conversionPrefix + "range");
		Property propertyRangeName = model.createProperty(conversionPrefix + "range");
		StmtIterator iter = model.listStatements();
		// print out the predicate, subject and object of each statement
		while (iter.hasNext()) {
			Statement stmt      = iter.nextStatement();  // get next statement
			Resource  subject   = stmt.getSubject();     // get the subject
			Property  predicate = stmt.getPredicate();   // get the predicate
			RDFNode   object    = stmt.getObject();      // get the object
			String predString = predicate.toString();
			//if statement with property 'propertyConversionProcess'
			if(predicate.toString().trim().equals(propertyConversionProcess.toString().trim())){
				System.out.println("\nmatched: propertyConversionProcess!\n");
				if (object instanceof Resource) {
					//find triples with 'propertyEnhance'
					NodeIterator enhancements = model.listObjectsOfProperty((Resource) object, propertyEnhance);
					//iterate thru all enhancement blocks
					while(enhancements.hasNext()){
						System.out.println("\nmatched: propertyEnhance!\n");
						RDFNode node = enhancements.nextNode();
						System.out.println("node: " + ((Object) node).toString());			
						StmtIterator enhanceStatements =  model.listStatements((Resource) node, (Property) null, (Resource) null);
						// NodeIterator enhancementParameters = model.listObjectsOfProperty((Resource) node, enhance);	    
						while(enhanceStatements.hasNext()){
							//match and update range
							System.out.println("\n\ngot inside enhance!\n");
							Statement enhanceStatement = enhanceStatements.nextStatement();
							subject   = enhanceStatement.getSubject();     // get the subject
							predicate = enhanceStatement.getPredicate();   // get the predicate
							object    = enhanceStatement.getObject();      // get the object
							if (object instanceof Resource) {
								System.out.print(object.toString());
							} else {
								System.out.print(" \"" + object.toString() + "\"");
							}
							//here we can check properties for rewriting
							//match for range.	    
							if(predicate.toString().trim().equals(propertyRange.toString().trim())){
								Statement s = ResourceFactory.createStatement(subject,propertyRange, model.createResource("rdf:Resource"));
								newModel.add(s);
								return null;
								//after the iterator is done make change
								//enhanceStatement.changeObject(model.createResource("rdf:Resource"));
								//change the object of the statement (S, P, X) to (S, P, o).
							}
						}			    
					}	    
					//Statement s = ResourceFactory.createStatement(subject,propertyRangeName , model.createResource("test"));
					//model.add(s); // add the statement (triple) to the model
					//here new code
				}		        
			}
		}	    
		return newModel;
	}



	@QueryMethod
	public String writeEnhancement(final Request request){
		return request.toString();

	}


	@QueryMethod
	public String applyEnhancement(final Request request) throws FileNotFoundException{
		//


		return null;
	}


	@QueryMethod
	public String writeToEnhancement(final Request request) throws FileNotFoundException{
		//read in the csv file.
		//read enhancement into rdf model
		//find the triples with appropriate properies and rewrite it
		//how did you handle rewrite in your snomed stuff?

		//if you are modifying an enhancement you just need to assert triples on the bnode for that enhancement
		//if you are creating a new enhancement after asserting triple, you need to assert a triple with the 
		//conversion process.
		//how do i construct and assert a new triple in jena?

		//// add the property
		// johnSmith.addProperty(VCARD.FN, fullName);

		//add statements to a model
		//model.add(Statement s)
		//remove(Statement s)
		//Removes a statement.

		//Statement s = ResourceFactory.createStatement(subject, predicate, object);
		//model.add(s); // add the statement (triple) to the model

		//Statement.changeObject(String o)
		//change the object of the statement (S, P, X) to (S, P, o).

		Model model = ModelFactory.createDefaultModel();
		Model newModel = ModelFactory.createDefaultModel();

		//Node anonNode = Node.createAnon();
		//anonNode.


		//Model model = null;
		//model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);


		//load certain ontologies
		//model.read("http://was.tw.rpi.edu/semanteco/air/air.owl", "TTL");
		FileOutputStream newEnhancementFile =new FileOutputStream("/Users/apseyed/Desktop/source/scraperwiki-com/uk-offshore-oil-wells/version/2011-Jan-24/manual/uk-offshore-oil-wells-short.csv.e1.params.ttl.new");
		FileManager.get().readModel(model, "/Users/apseyed/Desktop/source/scraperwiki-com/uk-offshore-oil-wells/version/2011-Jan-24/manual/uk-offshore-oil-wells-short.csv.e1.params.ttl") ;
		String conversionPrefix = "http://purl.org/twc/vocab/conversion/";
		String dataset = "<https://github.com/timrdf/csv2rdf4lod-automation/wiki/CSV2RDF4LOD_BASE_URI#/source/scraperwiki-com/dataset/uk-offshore-oil-wells/version/2011-Jan-24/conversion/enhancement/1>";
		Resource subjectDataSet = model.createResource(dataset);
		Property propertyConversionProcess = model.createProperty(conversionPrefix + "conversion_process");
		Property propertyEnhance = model.createProperty(conversionPrefix + "enhance");
		Property propertyRange = model.createProperty(conversionPrefix + "range");
		Property propertyRangeName = model.createProperty(conversionPrefix + "range");


		StmtIterator iter = model.listStatements();
		// print out the predicate, subject and object of each statement
		while (iter.hasNext()) {
			Statement stmt      = iter.nextStatement();  // get next statement
			Resource  subject   = stmt.getSubject();     // get the subject
			Property  predicate = stmt.getPredicate();   // get the predicate
			RDFNode   object    = stmt.getObject();      // get the object


			System.out.print(subject.toString());
			System.out.print(" " + predicate.toString() + " ");
			if (object instanceof Resource) {
				System.out.print(object.toString());
			} else {
				// object is a literal
				System.out.print(" \"" + object.toString() + "\"");
			}

			System.out.println(" .");
			String predString = predicate.toString();
			System.out.println("comparing " + predString + " with :  " + propertyConversionProcess.toString());
			//if statement with property 'propertyConversionProcess'
			if(predicate.toString().trim().equals(propertyConversionProcess.toString().trim())){
				System.out.println("\nmatched: propertyConversionProcess!\n");
				System.out.println("object string is!!!: " + " \"" + object.toString() + "\"");

				if (object instanceof Resource) {
					//fine triples with 'propertyEnhance'
					NodeIterator enhancements = model.listObjectsOfProperty((Resource) object, propertyEnhance);
					while(enhancements.hasNext()){
						System.out.println("\nmatched: propertyEnhance!\n");
						RDFNode node = enhancements.nextNode();
						System.out.println("node: " + ((Object) node).toString());		

						StmtIterator enhanceStatements =  model.listStatements((Resource) node, (Property) null, (Resource) null);
						// NodeIterator enhancementParameters = model.listObjectsOfProperty((Resource) node, enhance);


						while(enhanceStatements.hasNext()){
							System.out.println("\n\ngot inside enhance!!!\n");
							Statement enhanceStatement = enhanceStatements.nextStatement();
							//System.out.println("object string is!!!: " + " \"" + object.toString() + "\"");

							subject   = enhanceStatement.getSubject();     // get the subject
							predicate = enhanceStatement.getPredicate();   // get the predicate
							object    = enhanceStatement.getObject();      // get the object
							System.out.print("enhancing statement is: ");
							System.out.print(subject.toString());
							System.out.print(" " + predicate.toString() + " ");
							if (object instanceof Resource) {
								System.out.print(object.toString());
							} else {
								// object is a literal
								System.out.print(" \"" + object.toString() + "\"");
							}
							//here we can check properties for rewriting
							//match for range.
						}

						Statement s = ResourceFactory.createStatement(subject,propertyRangeName , model.createResource("test"));
						newModel.add(s); // add the statement (triple) to the model
						//here new code
					}		        
				}
				//return null;
			}else{
				//a triple without conversion process
				//write stmt to a new model
			}	    
		}	
		/*
		System.out.println("************Only triples with predicate conversion property!!!");
		StmtIterator triplesWithConversionProcess = subjectDataSet.listProperties(propertyConversionProcess);
		//model.listObjectsOfProperty(edward, siblingOf);
		while(triplesWithConversionProcess.hasNext()){
			System.out.println("got inside triplesWithConversionProcess!!!");
			Statement stmt      = triplesWithConversionProcess.nextStatement();  // get next statement
		    Resource  subject   = stmt.getSubject();     // get the subject
		    Property  predicate = stmt.getPredicate();   // get the predicate
		    RDFNode   object    = stmt.getObject();      // get the object
		    System.out.println("sub: " + subject + " pred: " + predicate + " object: " + object);			
		}
		 */	

		newModel.write(newEnhancementFile, "N-TRIPLE");

		return null;		
	}



	public String getShortName(String inName)
	{
		int pAt = inName.indexOf("#");
		return (inName.substring(pAt+1));
	}

	public String jsonWrapper(Hashtable<String, String> table, String parent) throws JSONException{
		JSONArray data = new JSONArray();
		JSONObject response = new JSONObject();
		response.put("success", true);
		response.put("data", data);
		String str;
		Set<String> set = table.keySet();
		Iterator<String> itr = set.iterator();
		while (itr.hasNext()) {
			str = itr.next();
			System.out.println(str + ": " + table.get(str));

			JSONObject mapping = new JSONObject();
			mapping.put("id", str);
			//should use "short name" if there is no label.

			if(table.get(str) == ""){
				table.put(str, getShortName(str));
			}

			mapping.put("label", table.get(str));
			mapping.put("parent", parent);
			data.put(mapping);
		}
		return response.toString();
	}


	public String jsonWrapperForEntries(Hashtable<String, String> table, String parent) throws JSONException{

		Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();

		JSONArray data = new JSONArray();
		JSONObject response = new JSONObject();
		response.put("success", true);
		response.put("data", data);
		String str;
		Set<String> set = table.keySet();
		Iterator<String> itr = set.iterator();
		while (itr.hasNext()) {
			str = itr.next();
			System.out.println(str + ": " + table.get(str));

			JSONObject mapping = new JSONObject();
			mapping.put("id", str);
			//should use "short name" if there is no label.

			if(table.get(str) == ""){
				table.put(str, getShortName(str));
			}			
			mapping.put("label", table.get(str));
			mapping.put("parent", parent);
			data.put(mapping);
		}
		return response.toString();
	}

/*
	@HierarchicalMethod(parameter = "annoProperties")
	public Collection<HierarchyEntry> queryAnnotatorPropertyHM(final Request request, final HierarchyVerb action) throws JSONException {
		List<HierarchyEntry> items = new ArrayList<HierarchyEntry>();

		this.initModel(request);

		if(action == HierarchyVerb.ROOTS) {
			return  queryAnnotatorPropertyHMRoots(request);
		} else if ( action == HierarchyVerb.CHILDREN ) {
			return  queryAnnotatorPropertyHMChildren(request, (String) request.getParam("annotatorProperties"));
		} else if ( action == HierarchyVerb.SEARCH ) {
			return searchAnnotatorProperty( request, (String) request.getParam("string") );
		} else if ( action == HierarchyVerb.PATH_TO_NODE ) {
			return annotatorPropertyToNode( request, (String) request.getParam("node") );
		}
		return items;		
	}
	*/
	
	@HierarchicalMethod(parameter = "objProperties")
	public Collection<HierarchyEntry> queryObjPropertyHM(final Request request, final HierarchyVerb action) throws JSONException, OWLOntologyCreationException, OWLOntologyStorageException, UnsupportedEncodingException {
		List<HierarchyEntry> items = new ArrayList<HierarchyEntry>();

		if(action == HierarchyVerb.ROOTS) {
			return  queryObjPropertyHMRoots(request);
		} else if ( action == HierarchyVerb.CHILDREN ) {
			return  queryObjPropertyHMChildren(request, (String) request.getParam("objProperties"));
		} else if ( action == HierarchyVerb.SEARCH ) {
			return searchAnnotatorProperty( request, (String) request.getParam("string") );
		} else if ( action == HierarchyVerb.PATH_TO_NODE ) {
			return annotatorPropertyToNode( request, (String) request.getParam("node") );
		}
		return items;		
	}
	
	@HierarchicalMethod(parameter = "dataProperties")
	public Collection<HierarchyEntry> queryDataPropertyHM(final Request request, final HierarchyVerb action) throws JSONException, OWLOntologyCreationException, OWLOntologyStorageException, UnsupportedEncodingException {
		List<HierarchyEntry> items = new ArrayList<HierarchyEntry>();

		if(action == HierarchyVerb.ROOTS) {
			return  queryDataPropertyHMRoots(request);
		} else if ( action == HierarchyVerb.CHILDREN ) {
			return  queryDataPropertyHMChildren(request, (String) request.getParam("dataProperties"));
		} else if ( action == HierarchyVerb.SEARCH ) {
			return searchAnnotatorProperty( request, (String) request.getParam("string") );
		} else if ( action == HierarchyVerb.PATH_TO_NODE ) {
			return annotatorPropertyToNode( request, (String) request.getParam("node") );
		}
		return items;		
	}
	
	@HierarchicalMethod(parameter = "annoProperties")
	public Collection<HierarchyEntry> queryAnnoPropertyHM(final Request request, final HierarchyVerb action) throws JSONException, OWLOntologyCreationException, OWLOntologyStorageException, UnsupportedEncodingException {
		List<HierarchyEntry> items = new ArrayList<HierarchyEntry>();

		if(action == HierarchyVerb.ROOTS) {
			return  queryAnnoPropertyHMRoots(request);
		} else if ( action == HierarchyVerb.CHILDREN ) {
			return  queryAnnoPropertyHMChildren(request, (String) request.getParam("annoProperties"));
		} else if ( action == HierarchyVerb.SEARCH ) {
			return searchAnnotatorProperty( request, (String) request.getParam("string") );
		} else if ( action == HierarchyVerb.PATH_TO_NODE ) {
			return annotatorPropertyToNode( request, (String) request.getParam("node") );
		}
		return items;		
	}
	
	@HierarchicalMethod(parameter = "dataTypes")
	public Collection<HierarchyEntry> queryDataTypesHM(final Request request, final HierarchyVerb action) throws JSONException, OWLOntologyCreationException, OWLOntologyStorageException, UnsupportedEncodingException {
		List<HierarchyEntry> items = new ArrayList<HierarchyEntry>();

		if(action == HierarchyVerb.ROOTS) {
			return  queryDataTypeHMRoots(request);
		} else if ( action == HierarchyVerb.CHILDREN ) {
			return  queryDataTypeHMChildren(request, (String) request.getParam("dataTypes"));
		} else if ( action == HierarchyVerb.SEARCH ) {
			return searchAnnotatorProperty( request, (String) request.getParam("string") );
		} else if ( action == HierarchyVerb.PATH_TO_NODE ) {
			return annotatorPropertyToNode( request, (String) request.getParam("node") );
		}
		return items;		
	}
	
	protected Collection<HierarchyEntry> searchAnnotatorProperty(final Request request, final String str) {
		return null;
	}

	protected Collection<HierarchyEntry> annotatorPropertyToNode(final Request request, final String str) {
		return null;
	}
	
	protected Collection<HierarchyEntry> queryObjPropertyHMRoots(final Request request) throws JSONException, OWLOntologyCreationException {
		
		
		//AnnotatorTester ann = new AnnotatorTester();
		System.out.println("queryPropertyHMRoots");

		//String thing = "http://www.w3.org/2002/07/owl#Thing";
		System.out.println("for root objproperties:");
		//Collection<rpi.HierarchyEntry> entries =  ann.getOWLClasses("root");
		Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();

		JSONArray classes =  this.annotatorTester.getOWLProperties("root");	
		for(int i=0;i<classes.length();i++) {
			HierarchyEntry entry = new HierarchyEntry();

			String binding = (String)  classes.get(i);
			entry.setUri(binding);
	
			JSONObject annot = this.annotatorTester.getAnnotationsForProperty(binding.toString());
			System.out.println("class: " + binding.toString() + " has annotations: " + annot);		
			if(annot.has("label")  && !annot.get("label").equals("")){
				entry.setLabel((String)annot.get("label"));					
			}
			if(annot.has("comment")  && !annot.get("comment").equals("")){
				entry.setComment(((String)annot.get("comment")));			
			}		
			entries.add(entry);
		}
		
		
		/*
		Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();

		OntProperty topObjProp = model.getOntProperty("http://www.w3.org/2002/07/owl#topObjectProperty");
		for (Iterator<? extends OntProperty> i = topObjProp.listSubProperties(true); i.hasNext(); ) { //true here is for direct
			HierarchyEntry entry = new HierarchyEntry();
			OntProperty propertyRoot = i.next();	
			System.out.println("root: " + propertyRoot.toString());
			System.out.println("label: " + propertyRoot.getLabel(null));

			entry.setUri(propertyRoot.getURI());
			

			if(propertyRoot.getLabel(null) == "" || propertyRoot.getLabel(null) == null){
				entry.setLabel(getShortName(propertyRoot.toString()));
			}
			else{
				entry.setLabel(propertyRoot.getLabel(null));
			}     
			entries.add(entry);
		}	
		System.out.println("return entries: " + entries.toString());
		*/
		return entries;
	}
	
	protected Collection<HierarchyEntry> queryDataPropertyHMRoots(final Request request) throws JSONException, OWLOntologyCreationException {
		
		
		//AnnotatorTester ann = new AnnotatorTester();
		System.out.println("queryDataPropertyHMRoots");

		//String thing = "http://www.w3.org/2002/07/owl#Thing";
		System.out.println("for root dataproperties:");
		//Collection<rpi.HierarchyEntry> entries =  ann.getOWLClasses("root");
		Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();

		JSONArray classes =  this.annotatorTester.getOWLDataProperties("root");	
		for(int i=0;i<classes.length();i++) {
			HierarchyEntry entry = new HierarchyEntry();

			String binding = (String)  classes.get(i);
			entry.setUri(binding);
	
		
			JSONObject annot = this.annotatorTester.getAnnotationsForProperty(binding.toString());
			System.out.println("class: " + binding.toString() + " has annotations: " + annot);		
			if(annot.has("label")  && !annot.get("label").equals("")){
				entry.setLabel((String)annot.get("label"));					
			}
			if(annot.has("comment")  && !annot.get("comment").equals("")){
				entry.setComment(((String)annot.get("comment")));			
			}	
		
			
			
			
			
			entries.add(entry);
		}
		return entries;
	}
	
	protected Collection<HierarchyEntry> queryAnnoPropertyHMRoots(final Request request) throws JSONException, OWLOntologyCreationException {
		
		
		//AnnotatorTester ann = new AnnotatorTester();
		System.out.println("queryAnnoPropertyHMRoots");

		//String thing = "http://www.w3.org/2002/07/owl#Thing";
		System.out.println("for root annoproperties:");
		//Collection<rpi.HierarchyEntry> entries =  ann.getOWLClasses("root");
		Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();

		JSONArray classes =  this.annotatorTester.getOWLAnnoProperties("root");	
		for(int i=0;i<classes.length();i++) {
			HierarchyEntry entry = new HierarchyEntry();

			String binding = (String)  classes.get(i);
			entry.setUri(binding);
	
			JSONObject annot = this.annotatorTester.getAnnotationsForProperty(binding.toString());
			System.out.println("class: " + binding.toString() + " has annotations: " + annot);		
			if(annot.has("label")  && !annot.get("label").equals("")){
				entry.setLabel((String)annot.get("label"));					
			}
			if(annot.has("comment")  && !annot.get("comment").equals("")){
				entry.setComment(((String)annot.get("comment")));			
			}		
			entries.add(entry);
		}
		return entries;
	}
	
	
	protected Collection<HierarchyEntry> queryObjPropertyHMChildren(final Request request, String classRequiresSubPropertyString)throws JSONException, OWLOntologyCreationException {
		
		System.out.println("queryPropertyHMRoots");

		//String thing = "http://www.w3.org/2002/07/owl#Thing";
		System.out.println("for root objproperties:");
		//Collection<rpi.HierarchyEntry> entries =  ann.getOWLClasses("root");
		Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();

		JSONArray classes =  this.annotatorTester.getOWLProperties(classRequiresSubPropertyString);	
		for(int i=0;i<classes.length();i++) {
			HierarchyEntry entry = new HierarchyEntry();

			String binding = (String)  classes.get(i);
			entry.setUri(binding);
	
			JSONObject annot =this.annotatorTester.getAnnotationsForProperty(binding.toString());
			System.out.println("class: " + binding.toString() + " has annotations: " + annot);
			if(annot.has("label") && !annot.get("label").equals("")){
			entry.setLabel((String)annot.get("label"));	
			}
			
			if(annot.has("comment") && !annot.get("comment").equals("")){
				entry.setComment(((String)annot.get("comment")));			
			}
			
			entries.add(entry);
		}return entries;
		
	}
	
	protected Collection<HierarchyEntry> queryDataPropertyHMChildren(final Request request, String classRequiresSubPropertyString)throws JSONException, OWLOntologyCreationException {
		
		System.out.println("queryDataPropertyHMChildren");

		//String thing = "http://www.w3.org/2002/07/owl#Thing";
		System.out.println("for child dataproperties:");
		//Collection<rpi.HierarchyEntry> entries =  ann.getOWLClasses("root");
		Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();

		JSONArray classes =  this.annotatorTester.getOWLDataProperties(classRequiresSubPropertyString);	
		for(int i=0;i<classes.length();i++) {
			HierarchyEntry entry = new HierarchyEntry();

			String binding = (String)  classes.get(i);
			entry.setUri(binding);
	
			JSONObject annot =this.annotatorTester.getAnnotationsForProperty(binding.toString());
			System.out.println("class: " + binding.toString() + " has annotations: " + annot);
			if(annot.has("label") && !annot.get("label").equals("")){
			entry.setLabel((String)annot.get("label"));	
			}
			
			if(annot.has("comment") && !annot.get("comment").equals("")){
				entry.setComment(((String)annot.get("comment")));			
			}
			
			entries.add(entry);
		}return entries;
		
	}
	
	protected Collection<HierarchyEntry> queryDataTypeHMRoots(final Request request)throws JSONException, OWLOntologyCreationException {
		
		System.out.println("queryDataTypeHMRoots");
		//String thing = "http://www.w3.org/2002/07/owl#Thing";
		System.out.println("for child datatypes:");
		//Collection<rpi.HierarchyEntry> entries =  ann.getOWLClasses("root");
		Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();
		JSONArray classes =  this.annotatorTester.getOWLDataTypes(null);	
		for(int i=0;i<classes.length();i++) {
			HierarchyEntry entry = new HierarchyEntry();

			String binding = (String)  classes.get(i);
			entry.setUri(binding);
	
			//JSONObject annot =this.annotatorTester.getAnnotationsForProperty(binding.toString());
			//System.out.println("class: " + binding.toString() + " has annotations: " + annot);
			//if(annot.has("label") && !annot.get("label").equals("")){
			//entry.setLabel((String)annot.get("label"));	
			//}
			
			//if(annot.has("comment") && !annot.get("comment").equals("")){
			//	entry.setComment(((String)annot.get("comment")));			
			
			
			
			if (binding.contains("#")){
				//split on the #
				String[] temp = binding .split("#");
				binding  = temp[0];
				System.out.println("datatype label is: " + binding );
			}
		
			else{
				String[] temp2 = binding .split("/");
				binding = temp2[temp2.length-1];
				System.out.println("datatype label is: " + binding );
			}	
			
			entry.setComment(binding);			

			
			
			entries.add(entry);
		}return entries;
		
	}
	
	
	protected Collection<HierarchyEntry> queryDataTypeHMChildren(final Request request, String classRequiresSubPropertyString)throws JSONException, OWLOntologyCreationException {
		
		System.out.println("queryDataTypeHMChildren");
		//String thing = "http://www.w3.org/2002/07/owl#Thing";
		System.out.println("for child datatypes:");
		//Collection<rpi.HierarchyEntry> entries =  ann.getOWLClasses("root");
		Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();
		JSONArray classes =  this.annotatorTester.getOWLDataTypes(classRequiresSubPropertyString);	
		for(int i=0;i<classes.length();i++) {
			HierarchyEntry entry = new HierarchyEntry();

			String binding = (String)  classes.get(i);
			entry.setUri(binding);
	
			//JSONObject annot =this.annotatorTester.getAnnotationsForProperty(binding.toString());
			//System.out.println("class: " + binding.toString() + " has annotations: " + annot);
			//if(annot.has("label") && !annot.get("label").equals("")){
			//entry.setLabel((String)annot.get("label"));	
			//}
			
			//if(annot.has("comment") && !annot.get("comment").equals("")){
			//	entry.setComment(((String)annot.get("comment")));			
			
			
			entries.add(entry);
		}return entries;
		
	}
	
	protected Collection<HierarchyEntry> queryAnnoPropertyHMChildren(final Request request, String classRequiresSubPropertyString)throws JSONException, OWLOntologyCreationException {
		
		System.out.println("queryAnnoPropertyHMChildren");

		//String thing = "http://www.w3.org/2002/07/owl#Thing";
		System.out.println("for child dataproperties:");
		//Collection<rpi.HierarchyEntry> entries =  ann.getOWLClasses("root");
		Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();

		JSONArray classes =  this.annotatorTester.getOWLAnnoProperties(classRequiresSubPropertyString);	
		for(int i=0;i<classes.length();i++) {
			HierarchyEntry entry = new HierarchyEntry();

			String binding = (String)  classes.get(i);
			entry.setUri(binding);
	
			JSONObject annot =this.annotatorTester.getAnnotationsForProperty(binding.toString());
			System.out.println("class: " + binding.toString() + " has annotations: " + annot);
			if(annot.has("label") && !annot.get("label").equals("")){
			entry.setLabel((String)annot.get("label"));	
			}
			
			if(annot.has("comment") && !annot.get("comment").equals("")){
				entry.setComment(((String)annot.get("comment")));			
			}
			
			entries.add(entry);
		}return entries;
		
	}

	protected Collection<HierarchyEntry> queryAnnotatorPropertyHMRoots(final Request request) {
		Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();

		OntProperty topObjProp = model.getOntProperty("http://www.w3.org/2002/07/owl#topObjectProperty");
		for (Iterator<? extends OntProperty> i = topObjProp.listSubProperties(true); i.hasNext(); ) { //true here is for direct
			HierarchyEntry entry = new HierarchyEntry();
			OntProperty propertyRoot = i.next();	
			System.out.println("root: " + propertyRoot.toString());
			System.out.println("label: " + propertyRoot.getLabel(null));

			entry.setUri(propertyRoot.getURI());
			

			if(propertyRoot.getLabel(null) == "" || propertyRoot.getLabel(null) == null){
				entry.setLabel(getShortName(propertyRoot.toString()));
			}
			else{
				entry.setLabel(propertyRoot.getLabel(null));
			}     
			entries.add(entry);
		}	
		System.out.println("return entries: " + entries.toString());
		return entries;
	}

	protected Collection<HierarchyEntry> queryAnnotatorPropertyHMChildren(final Request request, String classRequiresSubPropertyString) {
		Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();
		if(classRequiresSubPropertyString  == null){
			return null;
		}

		OntProperty subProp = model.getOntProperty( classRequiresSubPropertyString );

		for (Iterator<? extends OntProperty> i = subProp.listSubProperties(true); i.hasNext(); ) { //true here is for direct
			HierarchyEntry entry = new HierarchyEntry();
			OntProperty subProperty = i.next();

			entry.setUri(subProperty.getURI());
			if(subProperty.getLabel(null) == "" || subProperty.getLabel(null) == null){
				//table.put(subClass.toString(), getShortName(subClass.toString()));
				entry.setLabel(getShortName(subProperty.toString()));
			}
			else{
				entry.setLabel(subProperty.getLabel(null));
			}
			entry.setParent(URI.create(subProp.getURI()));
			entries.add(entry);
		}	
		return entries;		
	}

	@HierarchicalMethod(parameter = "annotatorClasses")
	public Collection<HierarchyEntry> queryAnnotatorClassHM(final Request request, final HierarchyVerb action) throws JSONException {
		List<HierarchyEntry> items = new ArrayList<HierarchyEntry>();
		this.initModel(request);

		if(action == HierarchyVerb.ROOTS) {
			//			HierarchyEntry entry = new HierarchyEntry();
			//			entry.setUri(URI.create("http://example.com/bird1"));
			//			entry.setLabel("bird1");
			//			entry.setAltLabel("birdicus uno");
			//			items.add(entry);
			//			entry = new HierarchyEntry();
			//			entry.setUri(URI.create("http://example.com/bird2"));
			//			entry.setLabel("bird2");
			//			entry.setAltLabel("birdicus dos");
			//			items.add(entry);
			return  queryAnnotatorClassHMRoots(request);
		} else if ( action == HierarchyVerb.CHILDREN ) {
			//			if ( request.getParam("species").equals("http://example.com/bird1") ) {
			//				HierarchyEntry entry = new HierarchyEntry();
			//				entry.setUri(URI.create("http://example.com/bird3"));
			//				entry.setLabel("bird3");
			//				entry.setAltLabel("birdicus tres");
			//				items.add(entry);
			//				entry = new HierarchyEntry();
			//				entry.setUri(URI.create("http://example.com/bird4"));
			//				entry.setLabel("bird4");
			//				entry.setAltLabel("birdicus quatro");
			//				items.add(entry);
			//			}
			return  queryAnnotatorClassHMChildren(request, (String) request.getParam("annotatorClasses"));
		} else if ( action == HierarchyVerb.SEARCH ) {
			return searchAnnotatorClass( request, (String) request.getParam("string") );
		} else if ( action == HierarchyVerb.PATH_TO_NODE ) {
			return annotatorClassToNode( request, (String) request.getParam("node") );
		}
		return items;
	}
	
	//protected Collection<HierarchyEntry> queryAnnotatorClassHMRoots(final Request request) {
		protected Collection<HierarchyEntry> queryAnnotatorClassHMRoots(final Request request) {


			//this.initModel();

			//construct an owlontology and pose sparql queries against it.
			//OntModel model = null;
			//model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

			//setModel(model);
			final Query query = config.getQueryFactory().newQuery(Type.SELECT);
			final Variable id = query.getVariable(VAR_NS+ "child");
			final Variable label = query.getVariable(VAR_NS+ "label");
			final Variable comment = query.getVariable(VAR_NS+ "comment");
			final Variable parent = query.getVariable(VAR_NS+ "parent");		

			final QueryResource PollutedThing = query.getResource("http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#PollutedThing");
			final QueryResource Measurement = query.getResource("http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Measurement");
			final QueryResource Thing = query.getResource("http://www.w3.org/2002/07/owl#Thing");
			//http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Entity
			final QueryResource Entity = query.getResource("http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Entity");

			final QueryResource subClassOf = query.getResource(RDFS_NS+"subClassOf");
			final Variable site = query.getVariable(VAR_NS+"site");
			final QueryResource hasLabel = query.getResource(RDFS_NS + "label");
			final QueryResource hasComment = query.getResource(RDFS_NS + "comment");


			Set<Variable> vars = new LinkedHashSet<Variable>();
			vars.add(id);
			vars.add(parent);
			vars.add(label);
			query.setVariables(vars);
			//query.addPattern(site, subClassOf, PollutedThing);
			//query.addPattern(site, subClassOf, Measurement);
			query.addPattern(id, subClassOf, parent);
			query.addPattern(id, subClassOf, Entity);
			query.addPattern(id, hasLabel, label);

			//add an optional here
	        final OptionalComponent optional = query.createOptional();
	        query.addPattern(id, comment, comment);


			//construct.addPattern(site, subClassOf, PollutedThing);
	        //String results = config.getQueryExecutor(request).accept("application/json").execute(query);
			String results  = executeLocalQuery(query, model);
			String responseStr = FAILURE;
			//String resultStr = config.getQueryExecutor(request).accept("application/json").executeLocalQuery(query, model);	
			//Set master = new HashSet();		//model.
			//Set<OntClass> classes = new HashSet<OntClass>();		//model.
			//Set<String> labels = new HashSet<String>();		//model.

			//iterate over results now
			System.out.println("result: " + results.toString());



			Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();

			OntClass thing = model.getOntClass( OWL.Thing.getURI() );
			//OntClass entity = model.getOntClass( "http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Entity" );
			for (Iterator<OntClass> i = thing.listSubClasses(true); i.hasNext(); ) { //true here is for direct
				HierarchyEntry entry = new HierarchyEntry();

				OntClass hierarchyRoot = i.next();	
				System.out.println("root: " + hierarchyRoot.toString());
				System.out.println("label: " + hierarchyRoot.getLabel(null));

				entry.setUri(hierarchyRoot.getURI());
				Map<String, Set<String>> axioms = this.getAxiomsForClass(request, hierarchyRoot);
				entry.setAxioms(axioms);
				//call a method that given the class, returns a hashSet with all the axioms.		
				//I think I will put into HierarchyEntry object a HashTable<String,HashSet<String>> where 
				//e.g. "annotation" is a key and we have a set of strings that represent annotations... what do you think




				if(hierarchyRoot.getLabel(null) == "" || hierarchyRoot.getLabel(null) == null){
					entry.setLabel(getShortName(hierarchyRoot.toString()));
				}
				else{
					entry.setLabel(hierarchyRoot.getLabel(null));
				}     

				//entry.setParent(URI.create(thing.getURI()));
				entries.add(entry);
			}	
			System.out.println("return entries: " + entries.toString());
			return entries;
			//return jsonWrapper(table, OWL.Thing.getURI().toString());	
		}
	
	@HierarchicalMethod(parameter = "classes")
	public Collection<HierarchyEntry> queryClassHM(final Request request, final HierarchyVerb action) throws JSONException, OWLOntologyCreationException, OWLOntologyStorageException, UnsupportedEncodingException {
		List<HierarchyEntry> items = new ArrayList<HierarchyEntry>();
		//this.initModel(request);
		//this.initOWLModel(request);


		if(action == HierarchyVerb.ROOTS) {
			//			HierarchyEntry entry = new HierarchyEntry();
			//			entry.setUri(URI.create("http://example.com/bird1"));
			//			entry.setLabel("bird1");
			//			entry.setAltLabel("birdicus uno");
			//			items.add(entry);
			//			entry = new HierarchyEntry();
			//			entry.setUri(URI.create("http://example.com/bird2"));
			//			entry.setLabel("bird2");
			//			entry.setAltLabel("birdicus dos");
			//			items.add(entry);
			return  queryClassHMRoots(request);
		} else if ( action == HierarchyVerb.CHILDREN ) {
			//			if ( request.getParam("species").equals("http://example.com/bird1") ) {
			//				HierarchyEntry entry = new HierarchyEntry();
			//				entry.setUri(URI.create("http://example.com/bird3"));
			//				entry.setLabel("bird3");
			//				entry.setAltLabel("birdicus tres");
			//				items.add(entry);
			//				entry = new HierarchyEntry();
			//				entry.setUri(URI.create("http://example.com/bird4"));
			//				entry.setLabel("bird4");
			//				entry.setAltLabel("birdicus quatro");
			//				items.add(entry);
			//			}
			return  queryClassHMChildren(request, (String) request.getParam("classes"));
		} 
		/*
		else if ( action == HierarchyVerb.SEARCH ) {
			return searchAnnotatorClass( request, (String) request.getParam("string") );
		} else if ( action == HierarchyVerb.PATH_TO_NODE ) {
			return annotatorClassToNode( request, (String) request.getParam("node") );
		}
		*/
		
		return items;
	}
	
	protected Collection<HierarchyEntry> queryClassHMRoots(final Request request) throws OWLOntologyCreationException, JSONException, OWLOntologyStorageException, UnsupportedEncodingException {
		
		//initModel();
	//	this.initOWLModel(request);
		
		//AnnotatorTester ann = new AnnotatorTester();
		System.out.println("queryClassHMRoots");

		//String thing = "http://www.w3.org/2002/07/owl#Thing";
		System.out.println("for root:");
		//Collection<rpi.HierarchyEntry> entries =  ann.getOWLClasses("root");
		Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();
		JSONArray classes =  this.annotatorTester.getOWLClasses("root");	
		for(int i=0;i<classes.length();i++) {
			HierarchyEntry entry = new HierarchyEntry();
			String binding = (String)  classes.get(i);
			entry.setUri(binding);		
			JSONObject annot = this.annotatorTester.getAnnotationsForClass(binding.toString());
			System.out.println("class: " + binding.toString() + " has annotations: " + annot);		
			
			if(annot.has("label") && !annot.get("label").equals("") ){
			entry.setLabel((String)annot.get("label"));		
			}
			if(annot.has("comment")  && !annot.get("comment").equals("")){
				entry.setComment(((String)annot.get("comment")));			
			}
			
			entry.setHasChild(this.annotatorTester.hasClassChildren(binding));
			entries.add(entry);
			
		}
		//JSONArray classes = ann.getOnlyChildOWLClasses("root");		
		return entries;
		
	}
	
	protected Collection<HierarchyEntry> queryClassHMChildren(final Request request, String clazz) throws OWLOntologyCreationException, JSONException {
		//AnnotatorTester ann = new AnnotatorTester();
		System.out.println("queryClassHMChildren");

		//String thing = "http://www.w3.org/2002/07/owl#Thing";
		System.out.println("for children:");
		//Collection<rpi.HierarchyEntry> entries =  ann.getOWLClasses("root");
		Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();

		JSONArray classes =  this.annotatorTester.getOWLClasses(clazz);	
		for(int i=0;i<classes.length();i++) {
			HierarchyEntry entry = new HierarchyEntry();

			String binding = (String)  classes.get(i);
			entry.setUri(binding);

			
			JSONObject annot = this.annotatorTester.getAnnotationsForClass(binding.toString());
			System.out.println("class: " + binding.toString() + " has annotations: " + annot);
			if(annot.has("label") && !annot.get("label").equals("")){
			entry.setLabel((String)annot.get("label"));		
			}
			if(annot.has("comment") && !annot.get("comment").equals("")){
				entry.setComment(((String)annot.get("comment")));			
			}
			entry.setHasChild(this.annotatorTester.hasClassChildren(binding));
			entries.add(entry);
		}

		//JSONArray classes = ann.getOnlyChildOWLClasses("root");		
		return entries;
		
	}
	/*
	protected Collection<rpi.HierarchyEntry> queryClassHMChildren(final Request request, String classes) throws OWLOntologyCreationException, JSONException {
		AnnotatorTester ann = new AnnotatorTester();
		System.out.println("queryClassHMChildren");

		//String thing = "http://www.w3.org/2002/07/owl#Thing";
		System.out.println("for non-root:");
		Collection<rpi.HierarchyEntry> entries =  ann.getOWLClasses(classes);
		
		return entries;		
	}
*/
	
	
	/*
	protected Collection<HierarchyEntry> queryAnnotatorClassHMRoots(final Request request) {

		//this.initModel();

		//construct an owlontology and pose sparql queries against it.
		//OntModel model = null;
		//model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

		//setModel(model);
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		final Variable id = query.getVariable(VAR_NS+ "child");
		final Variable label = query.getVariable(VAR_NS+ "label");
		final Variable parent = query.getVariable(VAR_NS+ "parent");		

		final QueryResource PollutedThing = query.getResource("http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#PollutedThing");
		final QueryResource Measurement = query.getResource("http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Measurement");
		final QueryResource Thing = query.getResource("http://www.w3.org/2002/07/owl#Thing");
		//http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Entity
		final QueryResource Entity = query.getResource("http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Entity");

		final QueryResource subClassOf = query.getResource(RDFS_NS+"subClassOf");
		final Variable site = query.getVariable(VAR_NS+"site");
		final QueryResource hasLabel = query.getResource(RDFS_NS + "label");

		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(id);
		vars.add(parent);
		vars.add(label);
		query.setVariables(vars);
		//query.addPattern(site, subClassOf, PollutedThing);
		//query.addPattern(site, subClassOf, Measurement);
		query.addPattern(id, subClassOf, parent);
		query.addPattern(id, subClassOf, Entity);
		query.addPattern(id, hasLabel, label);
		//construct.addPattern(site, subClassOf, PollutedThing);
		//return executeLocalQuery(query, model);
		String responseStr = FAILURE;
		//String resultStr = config.getQueryExecutor(request).accept("application/json").executeLocalQuery(query, model);	
		//Set master = new HashSet();		//model.
		//Set<OntClass> classes = new HashSet<OntClass>();		//model.
		//Set<String> labels = new HashSet<String>();		//model.

		Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();

		OntClass thing = model.getOntClass( OWL.Thing.getURI() );
		//OntClass entity = model.getOntClass( "http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Entity" );
		for (Iterator<OntClass> i = thing.listSubClasses(true); i.hasNext(); ) { //true here is for direct
			HierarchyEntry entry = new HierarchyEntry();

			OntClass hierarchyRoot = i.next();	
			System.out.println("root: " + hierarchyRoot.toString());
			System.out.println("label: " + hierarchyRoot.getLabel(null));

			entry.setUri(hierarchyRoot.getURI());
			Map<String, Set<String>> axioms = this.getAxiomsForClass(request, hierarchyRoot);
			entry.setAxioms(axioms);
			//call a method that given the class, returns a hashSet with all the axioms.		
			//I think I will put into HierarchyEntry object a HashTable<String,HashSet<String>> where 
			//e.g. "annotation" is a key and we have a set of strings that represent annotations... what do you think

			if(hierarchyRoot.getLabel(null) == "" || hierarchyRoot.getLabel(null) == null){
				entry.setLabel(getShortName(hierarchyRoot.toString()));
			}
			else{
				entry.setLabel(hierarchyRoot.getLabel(null));
			}     

			//entry.setParent(URI.create(thing.getURI()));
			entries.add(entry);
		}	
		System.out.println("return entries: " + entries.toString());
		return entries;
		//return jsonWrapper(table, OWL.Thing.getURI().toString());	
	}
*/
	protected Collection<HierarchyEntry> queryAnnotatorClassHMChildren(final Request request, final String classRequiresSubclassesString) {

		if(classRequiresSubclassesString == null){
			return null;
		}
		Collection<HierarchyEntry> entries = new ArrayList<HierarchyEntry>();
		OntClass superclass = model.getOntClass( classRequiresSubclassesString );

		for (Iterator<OntClass> i = superclass.listSubClasses(true); i.hasNext(); ) { //true here is for direct
			HierarchyEntry entry = new HierarchyEntry();

			OntClass subClass = i.next();
			entry.setUri(subClass.getURI());
			Map<String, Set<String>> axioms = this.getAxiomsForClass(request, subClass);
			entry.setAxioms(axioms);

			if(subClass.getLabel(null) == "" || subClass.getLabel(null) == null){
				//table.put(subClass.toString(), getShortName(subClass.toString()));
				entry.setLabel(getShortName(subClass.toString()));
			}
			else{
				//table.put(hierarchyRoot.toString(), hierarchyRoot.getLabel(null));
				entry.setLabel(subClass.getLabel(null));
			}
			entry.setParent(URI.create(superclass.getURI()));
			entries.add(entry);
		}	
		return entries;
	}



	protected Collection<HierarchyEntry> searchAnnotatorClass(final Request request, final String str) {
		return null;

	}

	protected Collection<HierarchyEntry> annotatorClassToNode(final Request request, final String str) {
		return null;
	}


	//would it be better to have one model and reasoner for this module, instead of per query method? yes.
	//do that through a constructor?
	@QueryMethod
	public String queryForAnnotatorRootClasses(final Request request) throws JSONException{
		// initialize the model if it doesn't already exist...
		initModel(request);	
		//setModel(model);
		//model.
		//InputStream is = new BufferedInputStream(new FileInputStream("blah.turtle"));	
		//apply sparql queries against it
		//final Query query = config.getQueryFactory().newQuery(Type.CONSTRUCT);
		//final GraphComponentCollection construct = query.getConstructComponent();
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		final Variable id = query.getVariable(VAR_NS+ "child");
		final Variable label = query.getVariable(VAR_NS+ "label");
		final Variable parent = query.getVariable(VAR_NS+ "parent");		

		final QueryResource PollutedThing = query.getResource("http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#PollutedThing");
		final QueryResource Measurement = query.getResource("http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Measurement");
		final QueryResource Thing = query.getResource("http://www.w3.org/2002/07/owl#Thing");
		//http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Entity
		final QueryResource Entity = query.getResource("http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Entity");

		final QueryResource subClassOf = query.getResource(RDFS_NS+"subClassOf");
		final Variable site = query.getVariable(VAR_NS+"site");
		final QueryResource hasLabel = query.getResource(RDFS_NS + "label");

		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(id);
		vars.add(parent);
		vars.add(label);

		query.setVariables(vars);
		//query.addPattern(site, subClassOf, PollutedThing);
		//query.addPattern(site, subClassOf, Measurement);
		query.addPattern(id, subClassOf, parent);
		query.addPattern(id, subClassOf, Entity);
		query.addPattern(id, hasLabel, label);

		//construct.addPattern(site, subClassOf, PollutedThing);

		//return executeLocalQuery(query, model);
		String responseStr = FAILURE;
		//String resultStr = config.getQueryExecutor(request).accept("application/json").executeLocalQuery(query, model);

		Set master = new HashSet();		//model.
		Set<OntClass> classes = new HashSet<OntClass>();		//model.
		Set<String> labels = new HashSet<String>();		//model.

		OntClass thing = model.getOntClass( OWL.Thing.getURI() );
		//OntClass entity = model.getOntClass( "http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Entity" );

		Hashtable<String, String> table = new Hashtable<String, String>();

		for (Iterator<OntClass> i = thing.listSubClasses(true); i.hasNext(); ) { //true here is for direct
			OntClass hierarchyRoot = i.next();

			//classes.add( hierarchyRoot);
			//labels.add( hierarchyRoot.getLabel(null));
			System.out.println("root: " + hierarchyRoot.toString());
			System.out.println("label: " + hierarchyRoot.getLabel(null));

			if(hierarchyRoot.getLabel(null) == "" || hierarchyRoot.getLabel(null) == null){
				table.put(hierarchyRoot.toString(), getShortName(hierarchyRoot.toString()));
			}
			else{
				table.put(hierarchyRoot.toString(), hierarchyRoot.getLabel(null));
			}

		}	



		/*
		 * 
		for (Iterator<OntClass> i = model.listHierarchyRootClasses(); i.hasNext(); ) {
		    OntClass hierarchyRoot = i.next();
		    classes.add( hierarchyRoot);
		    labels.add( hierarchyRoot.getLabel(null));
		}
		 */
		//master.add(classes);
		//master.add(labels);

		return jsonWrapper(table, OWL.Thing.getURI().toString());

		//return master.toString();
		//return config.getQueryExecutor(request).accept("application/json").executeLocalQuery(query, model);

		/*	if(resultStr == null) {
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
			//log.error("Unable to parse JSON results", e);
		}
		return responseStr;		
		 */
	}

	@QueryMethod
	public String queryForAnnotatorSubClasses(final Request request) throws JSONException{

		String classRequiresSubclassesString = (String) request.getParam("SubClass");	
		if(classRequiresSubclassesString == null){
			return null;
		}

		initModel(request);

		//apply sparql queries against it
		//final Query query = config.getQueryFactory().newQuery(Type.CONSTRUCT);
		//final GraphComponentCollection construct = query.getConstructComponent();
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);
		final Variable id = query.getVariable(VAR_NS+ "child");
		final Variable label = query.getVariable(VAR_NS+ "label");
		final Variable parent = query.getVariable(VAR_NS+ "parent");		

		final QueryResource PollutedThing = query.getResource("http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#PollutedThing");
		final QueryResource Measurement = query.getResource("http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Measurement");
		final QueryResource Thing = query.getResource("http://www.w3.org/2002/07/owl#Thing");
		//http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Entity
		final QueryResource Entity = query.getResource("http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Entity");

		final QueryResource subClassOf = query.getResource(RDFS_NS+"subClassOf");
		final Variable site = query.getVariable(VAR_NS+"site");
		final QueryResource hasLabel = query.getResource(RDFS_NS + "label");

		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(id);
		vars.add(parent);
		vars.add(label);

		query.setVariables(vars);
		//query.addPattern(site, subClassOf, PollutedThing);
		//query.addPattern(site, subClassOf, Measurement);
		query.addPattern(id, subClassOf, parent);
		query.addPattern(id, subClassOf, Entity);
		query.addPattern(id, hasLabel, label);

		//construct.addPattern(site, subClassOf, PollutedThing);

		//return executeLocalQuery(query, model);
		String responseStr = FAILURE;
		//String resultStr = config.getQueryExecutor(request).accept("application/json").executeLocalQuery(query, model);

		Set master = new HashSet();		//model.
		Set<OntClass> classes = new HashSet<OntClass>();		//model.
		Set<String> labels = new HashSet<String>();		//model.

		//OntClass thing = model.getOntClass( OWL.Thing.getURI() );
		//OntClass subclass = model.getOntClass( classRequiresSubclassesString );

		OntClass subclass = model.getOntClass( classRequiresSubclassesString );

		Hashtable<String, String> table = new Hashtable<String, String>();

		for (Iterator<OntClass> i = subclass.listSubClasses(true); i.hasNext(); ) { //true here is for direct
			OntClass hierarchyRoot = i.next();


			if(hierarchyRoot.getLabel(null) == "" || hierarchyRoot.getLabel(null) == null){
				table.put(hierarchyRoot.toString(), getShortName(hierarchyRoot.toString()));
			}
			else{
				table.put(hierarchyRoot.toString(), hierarchyRoot.getLabel(null));
			}
			//classes.add( hierarchyRoot);
			//labels.add( hierarchyRoot.getLabel(null));

		}	



		/*
		 * 
		for (Iterator<OntClass> i = model.listHierarchyRootClasses(); i.hasNext(); ) {
		    OntClass hierarchyRoot = i.next();
		    classes.add( hierarchyRoot);
		    labels.add( hierarchyRoot.getLabel(null));
		}
		 */
		master.add(classes);
		master.add(labels);

		return jsonWrapper(table, classRequiresSubclassesString);

		//return master.toString();
		//return config.getQueryExecutor(request).accept("application/json").executeLocalQuery(query, model);

		/*	if(resultStr == null) {
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
			//log.error("Unable to parse JSON results", e);
		}
		return responseStr;		
		 */
	}


	@QueryMethod
	public String queryForAnnotatorRootObjectProperties(Request request) throws JSONException{
		//construct an owlontology and pose sparql queries against it.
		//OntModel model = null;
		//model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

		initModel(request);


		//load certain ontologies
		//model.read("http://was.tw.rpi.edu/semanteco/air/air.owl", "TTL");
		/*
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-sbclter.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-temporal.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-spatial.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-biology.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-chemistry.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-anatomy.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-characteristics.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-taxa.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-standards.owl") ;
				FileManager.get().readModel(model, "/Users/apseyed/Documents/rpi/semanteco-products/obo-e-ontologies/oboe-core.owl") ;
		 */
		//InputStream is = new BufferedInputStream(new FileInputStream("blah.turtle"));


		//apply sparql queries against it
		//final Query query = config.getQueryFactory().newQuery(Type.CONSTRUCT);
		//final GraphComponentCollection construct = query.getConstructComponent();
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);


		final QueryResource topObjectProperty = query.getResource("http://www.w3.org/2002/07/owl#topObjectProperty");
		final QueryResource subPropertyOf = query.getResource(RDFS_NS+"subPropertyOf");
		final Variable site = query.getVariable(VAR_NS+"site");
		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(site);
		query.setVariables(vars);
		//query.addPattern(site, subClassOf, PollutedThing);
		//query.addPattern(site, subClassOf, Measurement);
		query.addPattern(site, subPropertyOf, topObjectProperty);

		//construct.addPattern(site, subClassOf, PollutedThing);

		//return executeLocalQuery(query, model);

		//Set master = new HashSet();		//model.
		//Set<OntProperty> props = new HashSet<OntProperty>();		//model.
		//Set<String> labels = new HashSet<String>();		//model.

		//OntClass thing = model.getOntClass( OWL.Thing.getURI() );
		//OntClass entity = model.getOntClass( "http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Entity" );
		OntProperty topObjProp = model.getOntProperty("http://www.w3.org/2002/07/owl#topObjectProperty");

		Hashtable<String, String> table = new Hashtable<String, String>();
		topObjProp.getSubProperty();
		System.out.println("properties are : " + topObjProp.listSubProperties(false).toString());
		for (Iterator<? extends OntProperty> i = topObjProp.listSubProperties(true); i.hasNext(); ) { //true here is for direct
			//System.out.println("properties are : " + i.toString());
			OntProperty hierarchyRoot = i.next();

			// props.add( hierarchyRoot);
			//labels.add( hierarchyRoot.getLabel(null));
			if(hierarchyRoot.getLabel(null) == "" || hierarchyRoot.getLabel(null) == null){
				table.put(hierarchyRoot.toString(), getShortName(hierarchyRoot.toString()));
			}
			else{
				table.put(hierarchyRoot.toString(), hierarchyRoot.getLabel(null));
			}

		}	




		/*
		 * 
				for (Iterator<OntClass> i = model.listHierarchyRootClasses(); i.hasNext(); ) {
				    OntClass hierarchyRoot = i.next();
				    classes.add( hierarchyRoot);
				    labels.add( hierarchyRoot.getLabel(null));
				}
		 */

		return jsonWrapper(table, topObjProp.toString());
		//return master.toString();



		//return config.getQueryExecutor(request).accept("application/json").executeLocalQuery(query, model);
	}

	/**
	 * need a method to collect axioms for a given class. can use HM pattern where a constant is suppied
	 * to indicate objectProperty, dataPropery, of annotationProperty axioms
	 */


	//@QueryMethod
	//public String  getAxiomsForClass(OWLClass c){
//
//		return null;
//	}

	//@QueryMethod
	public Map<String, Set<String>> getAxiomsForClass(final Request request, OntClass clazz){
		request.getLogger().debug("got get getAxiomsForClass");
		System.out.println("got to getAxioms for Class");
		Map<String, Set<String>> axioms = new Hashtable<String, Set<String>>();
		//		axioms.put("annotations", statements);
//		axioms.put("annotations", statements);
	//	axioms.put("annotations", statements);
	//	axioms.put("annotations", statements);
	//	axioms.put("annotations", statements);


		//this.initModel();
		//OntModel model = null;
		//model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

		//FileManager.get().readModel(model, config.getResource("owl-files/oboe-core.owl").toString()) ;	

		//for clazz collect all the statements for it.
		//StmtIterator sTest = clazz.listProperties(null);

		//model.getOntClass(clazz);
		//one method is to get the class in connection to the model:
		//OntClass c = model.getOntClass(clazz.toString());
		//OntClass c = model.getOntClass("http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Measurement");
		OntClass c = model.getOntClass(clazz.getURI().toString());

		//StmtIterator sTest = c.listProperties(null);

		//HashSet<String> statements = new HashSet<String> ();
		HashSet<String> annotations = new HashSet<String> ();
		HashSet<String> objectPropertyAssertions = new HashSet<String> ();
		HashSet<String> dataPropertyAssertions = new HashSet<String> ();
		HashSet<String> subClassOfAssertions = new HashSet<String> ();
		HashSet<String> equivalentClassAssertions = new HashSet<String> ();
		HashSet<String> disjointUnionOfAssertions = new HashSet<String> ();
		HashSet<String> disjointWithAssertions = new HashSet<String> ();


		
		for(StmtIterator statementsIter = c.listProperties(null) ; statementsIter.hasNext() ;){
			Statement statement = statementsIter.next();			
			//need to also check if it is a subClassOf, EquivalentClass, DisjointWith, DisjointUnionOf 

			
			
			if(statement.getPredicate().canAs(OntProperty.class)){
				//check what type of statements these are.
				OntProperty o = (OntProperty) statement.getPredicate().as(OntProperty.class);			
				//OntProperty o = s1.getProperty(null);
				request.getLogger().debug("ontproperty : " + o.getURI().toString());
				request.getLogger().debug("disjoint with : " + OWL.disjointWith.getURI());
				request.getLogger().debug("disjoint union of : " + OWL2.disjointUnionOf.getURI());
				request.getLogger().debug("subclass of : " + com.hp.hpl.jena.vocabulary.RDFS.subClassOf.getURI());	
				/*
				o.equals(OWL2.disjointWith);
				o.equals(OWL2.disjointUnionOf);
				o.equals(OWL2.equivalentClass);
				*/
				if(o.isObjectProperty()){
					System.out.println("is an object Property");
					//axioms.put("objectPropertyAssertions", statement);
					request.getLogger().debug("is an object property");
					//return null;
					objectPropertyAssertions.add(statement.toString());
				}
				else if(o.isDatatypeProperty()){
					System.out.println("is an data Property");
					request.getLogger().debug("is a data property");
					dataPropertyAssertions.add(statement.toString());
				}
				else if(o.isAnnotationProperty()){
					System.out.println("is an annotation Property");
					request.getLogger().debug("is an anno property");
					annotations.add(statement.toString());
				}	
			}
			
				Property p = statement.getPredicate();
				
				if (p.getURI().equals(OWL.disjointWith.getURI())){
					request.getLogger().debug("disjoint with");
					disjointWithAssertions.add(statement.toString());
				}
				else if (p.getURI().equals(OWL2.disjointUnionOf.getURI())){
					request.getLogger().debug("disjoint Union of");
					disjointUnionOfAssertions.add(statement.toString());
				}
				else if (p.getURI().equals(OWL2.equivalentClass.getURI())){
					request.getLogger().debug("equiv class");
					equivalentClassAssertions.add(statement.toString());
					
				}			
				else if(p.getURI().equals(com.hp.hpl.jena.vocabulary.RDFS.subClassOf.getURI())){				
					request.getLogger().debug("is subclassOf");
					subClassOfAssertions.add(statement.toString());
				}
			
				

			//for indivs
			//else if(o.isSameAs(null)){
			//	request.getLogger().debug("is sameAs ");
			//}
			//else if (o.is){
			//	
			//}
			//com.hp.hpl.jena.vocabulary.RDFS.subClassOf
			

			
			//statements.add(statement.toString());
			//s1.getPredicate();
		}
		axioms.put("annotations", annotations);
		axioms.put("objectPropertyAssertions", objectPropertyAssertions);
		axioms.put("datatypePropertyAssertions", dataPropertyAssertions);
		axioms.put("disjointWithAssertions", disjointWithAssertions);
		axioms.put("disjointUnionOfAssertions", disjointUnionOfAssertions);
		axioms.put("equivalentClassAssertions", equivalentClassAssertions);
		axioms.put("subClassOfAssertions", equivalentClassAssertions);


		//http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Measurement


		//OR


		//StmtIterator enhanceStatements3 =  model.listStatements((OntClass) clazz, (Property) null , (Literal) null );
		/*
		HashSet<String> statements = new HashSet<String> ();
		for(StmtIterator enhanceStatements3 =  model.listStatements((OntClass) clazz, (Property) null , (Literal) null) ; enhanceStatements3.hasNext() ;){
			Statement s1 = enhanceStatements3.next();
			statements.add(s1.toString());
			//s1.getPredicate();
		}
		 */


		return axioms;
		//return null;

		//put into a set of toString() 's


		//StmtIterator sTest = c.listProperties(null);
		//return sTest.next();

		/*
		for(StmtIterator s = c.listProperties(null); ; s.hasNext()){
			Statement s1 = s.next();
			s1.getPredicate();
		}
		 */

		//	s.next().getPredicate();

		//////s.
		//isObjectProperty
		//isDatatypeProperty
		//isAnnotationProperty

		//asAnnotationProperty
		//asDatatypeProperty
		//asObjectProperty		
	}


	@QueryMethod
	public String getAxiomsForClassTester(final Request request){
		//this.initModel();
		OntModel model = null;
		model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

		FileManager.get().readModel(model, config.getResource("owl-files/oboe-core.owl").toString()) ;	

		StmtIterator enhanceStatements1 =  model.listStatements((Resource) null, (Property) null , (Literal) null );
		return enhanceStatements1.next().toString();

		//StmtIterator sTest = c.listProperties(null);
		//return sTest.next();

		/*
		for(StmtIterator s = c.listProperties(null); ; s.hasNext()){
			Statement s1 = s.next();
			s1.getPredicate();
		}
		 */

		//	s.next().getPredicate();

		//////s.
		//isObjectProperty
		//isDatatypeProperty
		//isAnnotationProperty

		//asAnnotationProperty
		//asDatatypeProperty
		//asObjectProperty		
	}


	@QueryMethod
	public String queryForAnnotatorSubObjectProperties(Request request) throws JSONException{

		String classRequiresSubpropertyString = (String) request.getParam("SubProperty");	
		if(classRequiresSubpropertyString == null){
			return null;
		}
		//construct an owlontology and pose sparql queries against it.
		OntModel model = null;
		model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);

		initModel(request);
		//load certain ontologies
		//model.read("http://was.tw.rpi.edu/semanteco/air/air.owl", "TTL");
		//InputStream is = new BufferedInputStream(new FileInputStream("blah.turtle"));			
		//apply sparql queries against it
		//final Query query = config.getQueryFactory().newQuery(Type.CONSTRUCT);
		//final GraphComponentCollection construct = query.getConstructComponent();
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);


		final QueryResource topObjectProperty = query.getResource("http://www.w3.org/2002/07/owl#topObjectProperty");
		final QueryResource subPropertyOf = query.getResource(RDFS_NS+"subPropertyOf");
		final Variable site = query.getVariable(VAR_NS+"site");
		Set<Variable> vars = new LinkedHashSet<Variable>();
		vars.add(site);
		query.setVariables(vars);
		//query.addPattern(site, subClassOf, PollutedThing);
		//query.addPattern(site, subClassOf, Measurement);
		query.addPattern(site, subPropertyOf, topObjectProperty);

		//construct.addPattern(site, subClassOf, PollutedThing);

		//return executeLocalQuery(query, model);

		Set master = new HashSet();		//model.
		Set<OntProperty> props = new HashSet<OntProperty>();		//model.
		Set<String> labels = new HashSet<String>();		//model.

		//OntClass thing = model.getOntClass( OWL.Thing.getURI() );
		//OntClass entity = model.getOntClass( "http://ecoinformatics.org/oboe/oboe.1.0/oboe-core.owl#Entity" );
		OntProperty topObjProp = model.getOntProperty("http://www.w3.org/2002/07/owl#topObjectProperty");
		OntProperty subProp = model.getOntProperty( classRequiresSubpropertyString );

		Hashtable<String, String> table = new Hashtable<String, String>();

		for (Iterator<? extends OntProperty> i = subProp.listSubProperties(true); i.hasNext(); ) { //true here is for direct
			OntProperty hierarchyRoot = i.next();

			// props.add( hierarchyRoot);
			//  labels.add( hierarchyRoot.getLabel(null));
			if(hierarchyRoot.getLabel(null) == "" || hierarchyRoot.getLabel(null) == null){
				table.put(hierarchyRoot.toString(), getShortName(hierarchyRoot.toString()));
			}
			else{
				table.put(hierarchyRoot.toString(), hierarchyRoot.getLabel(null));
			}

		}	
		/*
		 * 
				for (Iterator<OntClass> i = model.listHierarchyRootClasses(); i.hasNext(); ) {
				    OntClass hierarchyRoot = i.next();
				    classes.add( hierarchyRoot);
				    labels.add( hierarchyRoot.getLabel(null));
				}
		 */
		//master.add(props);
		//master.add(labels);
		return jsonWrapper(table, classRequiresSubpropertyString);
		//return master.toString();
		//return config.getQueryExecutor(request).accept("application/json").executeLocalQuery(query, model);
	}



	public String executeLocalQuery(Query query, Model model) {

		if(System.getProperty("edu.rpi.tw.escience.writemodel", "false").equals("true")) {
			try {
				FileOutputStream fos = new FileOutputStream(System.getProperty("java.io.tmpdir")+"/model.rdf");
				model.write(fos);
				fos.close();
			}
			catch(Exception e) {
				// do nothing
			}
		}

		Model resultModel = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		long start = System.currentTimeMillis();
		QueryExecution qe = QueryExecutionFactory.create(query.toString(), model);
		try {
			switch(query.getType()) {
			case SELECT:
				ResultSet results = qe.execSelect();
				ResultSetFormatter.outputAsJSON(baos, results);
				//log.debug("Local query took "+(System.currentTimeMillis()-start)+" ms");
				return baos.toString("UTF-8");
			case DESCRIBE:
				resultModel = qe.execDescribe();
				resultModel.write(baos);
				//log.debug("Local query took "+(System.currentTimeMillis()-start)+" ms");
				return baos.toString("UTF-8");
			case CONSTRUCT:
				resultModel = qe.execConstruct();
				resultModel.write(baos);
				//log.debug("Local query took "+(System.currentTimeMillis()-start)+" ms");
				return baos.toString("UTF-8");
			case ASK:
				if(qe.execAsk()) {
					//log.debug("Local query took "+(System.currentTimeMillis()-start)+" ms");
					return "{\"result\":true}";
				}
				else {
					//	log.debug("Local query took "+(System.currentTimeMillis()-start)+" ms");
					return "{\"result\":false}";
				}
			}
		}
		catch(Exception e) {
			//	log.warn("Unable to execute query due to exception", e);
		}
		return null;
	}


	@Override
	public void visit(final Model model, final Request request, final Domain domain) {
		// TODO populate data model
	}

	@Override
	public void visit(final OntModel model, final Request request, final Domain domain) {
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
		return "Annotator";
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
