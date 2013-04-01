package edu.rpi.tw.escience.semanteco.annotator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.XMLGregorianCalendar;

import org.inference_web.pml.v2.pmlj.IWInferenceStep;
import org.inference_web.pml.v2.pmlj.IWNodeSet;
import org.inference_web.pml.v2.pmlp.IWInferenceEngine;
import org.inference_web.pml.v2.pmlp.IWInformation;
import org.inference_web.pml.v2.util.PMLObjectManager;
import org.inference_web.pml.v2.vocabulary.PMLJ;
import org.inference_web.pml.v2.vocabulary.PMLP;


import org.openrdf.model.Namespace;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.sail.memory.MemoryStore;

import edu.rpi.tw.data.csv.CSVParser;
import edu.rpi.tw.data.csv.CSVRecord;
import edu.rpi.tw.data.csv.Conversion;
import edu.rpi.tw.data.csv.EnhancementParameters;
import edu.rpi.tw.data.csv.RowHandler;
import edu.rpi.tw.data.csv.TemplateFiller;
import edu.rpi.tw.data.csv.ValueHandler;
import edu.rpi.tw.data.csv.impl.CSVRecordTemplateFiller;
import edu.rpi.tw.data.csv.impl.DefaultEnhancementParameters;
import edu.rpi.tw.data.csv.impl.PropertyNameFactory;
import edu.rpi.tw.data.csv.impl.TemplateFillerColumnContext;
import edu.rpi.tw.data.csv.impl.ValueHandlerFactory;
import edu.rpi.tw.data.csv.querylets.ParametersImportQuerylet;
import edu.rpi.tw.data.csv.querylets.column.ColumnEnhancementQuerylet;
import edu.rpi.tw.data.csv.querylets.deprecation.OldNamespaceQuerylet;
import edu.rpi.tw.data.csv.valuehandlers.DateTimeValueHandler;
import edu.rpi.tw.data.csv.valuehandlers.ResourceValueHandler;
import edu.rpi.tw.data.digest.GraphDigest;
import edu.rpi.tw.data.rdf.sesame.query.QueryletProcessor;
import edu.rpi.tw.data.rdf.sesame.vocabulary.CoIN;
import edu.rpi.tw.data.rdf.sesame.vocabulary.DCTerms;
import edu.rpi.tw.data.rdf.sesame.vocabulary.DOAP;
import edu.rpi.tw.data.rdf.sesame.vocabulary.DataCube;
import edu.rpi.tw.data.rdf.sesame.vocabulary.FOAF;
import edu.rpi.tw.data.rdf.sesame.vocabulary.FRBR;
import edu.rpi.tw.data.rdf.sesame.vocabulary.OpenVocab;
import edu.rpi.tw.data.rdf.sesame.vocabulary.PML2;
import edu.rpi.tw.data.rdf.sesame.vocabulary.PML3;
import edu.rpi.tw.data.rdf.sesame.vocabulary.PMLFormat;
import edu.rpi.tw.data.rdf.sesame.vocabulary.PMLRoles;
import edu.rpi.tw.data.rdf.sesame.vocabulary.PROVO;
import edu.rpi.tw.data.rdf.sesame.vocabulary.SCOVO;
import edu.rpi.tw.data.rdf.sesame.vocabulary.SDMX;
import edu.rpi.tw.data.rdf.sesame.vocabulary.SWAP_PIM_CON;
import edu.rpi.tw.data.rdf.sesame.vocabulary.VANN;
import edu.rpi.tw.data.rdf.sesame.vocabulary.VoID;
import edu.rpi.tw.data.rdf.sesame.vocabulary.W3CFormats;
import edu.rpi.tw.data.rdf.utils.pipes.Constants;
import edu.rpi.tw.data.rdf.utils.pipes.starts.Cat;
import edu.rpi.tw.string.BaseNamespace;
import edu.rpi.tw.string.NameFactory;
import edu.rpi.tw.string.NameFactory.NameType;
import edu.rpi.tw.string.WikimediaURIMapper;
import edu.rpi.tw.string.pmm.DefaultPrefixMappings;
import edu.rpi.tw.string.pmm.IPrefixMappings;
import edu.rpi.tw.string.pmm.PrefixMappings;

/**
 * CSVtoRDF converts a file in the generic comma-separated-value (CSV) format to a file in the 
 * Resource Description Framework (RDF) format. 
 * 
 * The first row header information is used to determine the predicates in the resulting RDF. 
 * If a column title contains "://", the whole title is accepted as the name (URI) of a rdf:Property. Otherwise,
 * a URI is created using the column title.
 * If the header titles are not URIs, a URI will be minted for each. The same non-URI header will result in different 
 * predicate URIs with subsequent runs of CSVtoRDF. This is because CSVtoRDF can not be sure that identical strings 
 * across instantiations are in fact identical relationships. rdfs:subPropertyOf can be used to resolve the equivalence 
 * of two predicates.
 */
public class CSVtoRDF implements RowHandler {

   // Logging and error reporting.
   private static Logger logger = Logger.getLogger(CSVtoRDF.class.getName());
   protected int numRepeated  = 0;
   protected int numSkipped   = 0;
   protected int REPORT_LIMIT = 5; // Number of errors to print to stderr; stop after hitting limit.
   
   protected static ValueFactory vf = ValueFactoryImpl.getInstance();
   
   // Who we are.
   protected static String VERSION = "2013-Jan-16";
   protected URI translationActivity = null;
   
   //
   private   URI      csv2rdf4lodClassR = vf.createURI("http://data-gov.tw.rpi.edu/wiki/Special:URIResolver/csv2rdf4lod");
   protected String   converterIdentifier;
   private   Resource csv2rdf4lodInstanceR = null;
   private   Resource csv2rdf4lodProjectR  = vf.createURI("http://data-gov.tw.rpi.edu/wiki/Special:URIResolver/","csv2rdf4lod");
   
   // When we started converting.
   protected long startTime = System.currentTimeMillis();
   protected XMLGregorianCalendar startTimeCal = DateTimeValueHandler.getNowXMLGregorianCalendar();
   
   // What to convert and how.
   protected String                inFileName;
   protected EnhancementParameters eParams;
   protected TemplateFiller        eParamsTF; // Second reference to eParams but as a TemplateFiller.

   // The conversion VoID dataset hierarchy.
   protected URI topLevelVoID         = null;
   protected URI topLevelDatasetR     = null;
   protected URI abstractDatasetR     = null; // http://data-gov.tw.rpi.edu/source/data-gov/dataset/92
   protected URI versionedDatasetR    = null; // http://data-gov.tw.rpi.edu/source/data-gov/dataset/92/version/1st-anniversary
   protected URI layerDatasetR        = null; // http://data-gov.tw.rpi.edu/source/data-gov/dataset/92/version/1st-anniversary#raw
   protected URI thisDatasetLevelR    = null;
   //
   protected URI datasetSameAsSubsetR = null;
   protected URI metaSubsetR          = null;
   protected URI sampleSubsetR        = null;
   
   // Example resources
   protected Set<URI> exampleResources = new HashSet<URI>();
   protected Resource firstSubjectR    = null;
   protected Resource lastSubjectR     = null;

   
   // Property naming from columns (and vice versa).
   protected HashMap<Integer,String> originalColumnHeaders = new HashMap<Integer,String>();  // late addition to fulfill naming cell-based up objects. Should probably be incorporated into PropertyNameFactory.
   protected PropertyNameFactory     columnNameFactory;    // Column names for enhanced layer.
   protected PropertyNameFactory     rawColumnNameFactory; // R A W (as in not enhanced) "simulating" to cite back to raw layer.
   protected ArrayList<Integer>      cellBasedColumnsSorted;
   
   // Predicates from row to implicit bundle resource.
   private Hashtable<Integer,URI> predicatesToImplicitBundles = new Hashtable<Integer,URI>(); 
   
   // Metadata on vocabulary use.
   protected Set<URI> predicatesUsedInData     = new HashSet<URI>();
   protected Set<URI> predicatesUsedInMetadata = new HashSet<URI>();
   protected Set<URI> classesUsedInData        = new HashSet<URI>();
   protected Set<URI> classesUsedInMetadata    = new HashSet<URI>();
   
   // Value handling
   protected CSVRecord previousRec = null;
   protected HashMap<Integer,ValueHandler> valueHandlers = new HashMap<Integer,ValueHandler>();
   protected ValueHandlerFactory           vhFactory;
   
   // Namespaces, typing, and local names
   protected String subjectNS   = BaseNamespace.forResource();
   protected String vocabNS     = BaseNamespace.forProperty();
   protected String predicateNS = BaseNamespace.forProperty();
   protected String objectNS    = BaseNamespace.forResource();
   protected String objectNSunversioned = objectNS;
   protected String                      instanceLocalPrefix       = "thing_";
   protected Set<URI>                    subjectRowTypeRs          = new HashSet<URI>();
   protected URI                         subjectDiscriminatorR     = null;
   protected HashMap<URI,Resource>       additionalDescriptions    = new HashMap<URI,Resource>();   
   protected CSVRecordTemplateFiller     csvRecordFiller;
   protected Hashtable<Integer,String>   implicitBundleIdentifiers = new Hashtable<Integer,String>();   // 
   protected Hashtable<Integer,Set<URI>> implicitBundleTypes       = new Hashtable<Integer,Set<URI>>(); // 


   
   // Where to accumulate output
   protected Repository           primaryRepos;   // For primary triples about columns and rows.
   protected RepositoryConnection primary;
   protected Repository           ancillaryRepos; // For ancillary triples that should be serialized at bottom.
   protected RepositoryConnection ancillary;
   protected Repository           ontologyRepos;   boolean assertedClasses = false;    // 
   protected RepositoryConnection ontology;
   protected int     commitInterval  = 1000;
   
   // Flushing variables
   protected boolean     BIG_MODE           = false;
   protected String      outputFileName;
   public    String      bigextension       = "ttl";   // Overrides requested extension if flushing b/c too large.
   private   Set<String> voidFileExtensions = new HashSet<String>();
   //
   protected int         FLUSH_THRESHOLD    = 500000;  //1000000; // First time to flush: r x c > FLUSH_THRESHOLD  TODO: consolidate this logic, goofed up to handle Ginos 50 column cell-based conversion.
   protected long        flushCount         = 0;
   protected int         FLUSH_INTERVAL     = 500000;  // How often to flush after first flush:     r x c % FLUSH_INTERVAL == 0
   private   boolean     appendToOutputFile;        // Overwrite file contents on first write, append thereafter.
   protected long        numberTriples      = 0;       // Tallys through flushes. (numTriples += rep.size(); rep.clear)

   // Provenance references.
   protected GraphDigest digest;
   protected boolean     justProvenance;
   protected String      sourceUsageURL;
   protected String      sourceUsageDateTime;
   private   String      provenanceBaseURI;
   private   String      enhancementParametersURL;

   // Counters/flags for terminating early for conversion sample.
   protected boolean onlyConvertExampleResources = false;
   protected int     sampleLimit = -1; // The number of samples to output (if >0)
   protected int     samplesMade =  0; // The number of samples that have been output.
   
   //
   //
   //
   public final static String USAGE = 
      "usage: CSVtoRDF <file> [-ep enhancementParams.ttl [-prov documentRetrievalPML]] [-w outputFile] [-oe outputExtension] [-id converterHash]\n"+
      "where \n"+
      "  enhancementParams.ttl: an RDF description of how to interpret the table, encoded using http://purl.org/twc/vocab/conversion/ vocabulary.\n"+
      "  documentRetrievalPML:  where did <file> come from? The PML describing where.\n"+
      "  outputFile:            the filename to write conversion output\n"+
      "  outputExtension:       file extensions to append to the URL of the void:dataDump, e.g. ttl,ttl.gz,nt,nt.gz\n"+
      "  converterHash:         md5 hash value of the converter's Java jar. Used to identify and distinguish different implementations.";   
   
//   public final static String OLD_USAGE = 
//   "usage: CSVtoRDF <file> [-h@ lineNum] \n"+
//   "                       [-pk  primaryKeyCol | -uk uriKeyCol]\n"+
//   "                       [-dns datasetNamespace]\n"+
//   "                       [-dit datasetIdentifier]\n"+
//   "                       [-et  enhancementIdentifier]\n"+
//   "                       [-c classURI] [-s subjectNamespace] [-p predicateNamespace] [-o objectNamespace] \n"+
//   "                       [-us] [-up] [-uo] \n"+
//   "                       [-r resourceOrLiteralBitString]\n"+
//   "                       [-ep enhancementParameters] [-oe outputExtension]\n"
//    + "where \n"
//    + "  file       : a csv file with column titles.\n"
//    + "  -h@        : the line number of the header. Will try header on first line.\n"
//    + "  -{us,up,uo}: append UUID to namespace of {subject, predicate, object}\n"
//    + "  resourceOrLiteralBitString: (l|r)+ e.g., lrlrrrrlllrrlr (if omitted, everything is a literal)\n"
//    + "  outputExtension: rdf, ttl, trig, pttl, nt";   
   
   /**
    * Run without params for usage.
    * 
    * @param args CSVtoRDF <file> <output format> [resourceOrLiteralBitString]
    */
   public static void main(String[] args) {
   	
      if (args.length < 1) {
         System.out.println(CSVtoRDF.USAGE);
         System.exit(1);
      }
      
      if( "--version".equals(args[0]) ) {
         System.out.println("CSVtoRDF: version "+VERSION);
         System.exit(1);
      }

      String inFilename                 = null;
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
      String outputFileName             = null;
      String metaOutputFileName         = null;
      String outputExtension            = "ttl";
      Set<String> voidFileExtensions    = null;
      //String resourceOrLiteralBitString = null; // TODO: Deprecate
      String enhancementParametersURL   = null;
      String provenanceParametersURL    = null;
      String converterIdentifier        = null;
      boolean examplesOnly              = false;
      int     sampleLimit               = -1;
      
      String NL = "";//"\n     ";
      System.err.println("========== edu.rpi.tw.data.csv.CSVtoRDF version "+VERSION+" initiated:");
      for( int i=0; i < args.length; i++ ) {
         String arg = args[i];
         boolean hasNext = i < args.length - 1;
         //System.err.println(arg + " " + hasNext);
         if( 0 == i ) {
            inFilename  = arg;
            System.err.println("fileName:                     "+NL+inFilename);
         }else if( "-h@".equalsIgnoreCase(arg)  || "-header@".equalsIgnoreCase(arg) && hasNext ) {
            header = Integer.parseInt(args[++i]);
            System.err.println("header:                      "+NL+header);
            
         }else if( "-pk".equalsIgnoreCase(arg)  || "-primaryKeyColumn".equalsIgnoreCase(arg) && hasNext ) {
            primaryKeyColumn = Integer.parseInt(args[++i]);
            System.err.println("primaryKeyColumn:            "+primaryKeyColumn);
            
         }else if( "-uk".equalsIgnoreCase(arg)  || "-uriKeyColumn".equalsIgnoreCase(arg) && hasNext ) {
            uriKeyColumn = Integer.parseInt(args[++i]);
            System.err.println("uriKeyColumn:                "+NL+uriKeyColumn);
            
         }else if( "-dns".equalsIgnoreCase(arg) || "-surrogateNS".equalsIgnoreCase(arg) && hasNext ) {
            baseURI = args[++i]; // NOTE: This one is still important, CSV2RDF4LOD_BASE_URI_OVERRIDE
            System.err.println("surrogateNS:                  "+NL+baseURI);
         }else if( "-dit".equalsIgnoreCase(arg) || "-datasetIdentifier".equalsIgnoreCase(arg) && hasNext ) {
            datasetIDTag = args[++i];
            System.err.println("datasetTag:                  "+datasetIDTag);
            
         }else if( "-ct".equalsIgnoreCase(arg)  || "-conversionTag".equalsIgnoreCase(arg) && hasNext ) {
            conversionTag = args[++i];
            System.err.println("enhancementIdentifier:       "+NL+conversionTag);
         }else if( "-c".equalsIgnoreCase(arg)   || "-classURI".equalsIgnoreCase(arg) && hasNext ) {
            classURI = args[++i];
            System.err.println("classURI:                    "+NL+classURI);
            
            // Subject
         }else if( "-s".equalsIgnoreCase(arg)  || "-subjectNS".equalsIgnoreCase(arg) && hasNext ) {
            subjectNS = args[++i];
            System.err.println("subjectNS:                   "+NL+subjectNS);
         }else if( "-us".equalsIgnoreCase(arg)  || "-uuidSubject".equalsIgnoreCase(arg) && hasNext ) {
            uuidSubject = Boolean.parseBoolean(args[++i]);
            System.err.println("uuidSubject:                 "+NL+uuidSubject);
            
            // Predicate
         }else if( "-p".equalsIgnoreCase(arg)  || "-predicateNS".equalsIgnoreCase(arg) && hasNext ) {
            predicateNS = args[++i];
            System.err.println("objectNS:                    "+NL+predicateNS);
         }else if( "-up".equalsIgnoreCase(arg)  || "-uuidPredicate".equalsIgnoreCase(arg) && hasNext ) {
            uuidPredicate = Boolean.parseBoolean(args[++i]);
            System.err.println("uuidPredicate:                "+NL+uuidPredicate);
            
            // Object
         }else if( "-o".equalsIgnoreCase(arg)  || "-objectNS".equalsIgnoreCase(arg) && hasNext ) {
            objectNS = args[++i];
            System.err.println("objectNS:                    "+NL+objectNS);
         }else if( "-uo".equalsIgnoreCase(arg)  || "-uuidObject".equalsIgnoreCase(arg) && hasNext ) {
            uuidObject = Boolean.parseBoolean(args[++i]);
            System.err.println("uuidObject:                   "+NL+uuidObject);
            
            // Predicate range types
         }/*deprecated else if( "-r".equalsIgnoreCase(arg)  || "-ranges".equalsIgnoreCase(arg) && hasNext ) {
            resourceOrLiteralBitString = args[++i];
            System.err.println("resourceOrLiteralBitString:  "+resourceOrLiteralBitString);
            
            // Enhancement parameters
         }*/else if( "-ep".equalsIgnoreCase(arg)  || "-eParams".equalsIgnoreCase(arg) && hasNext ) {
            enhancementParametersURL = args[++i];
            System.err.println("enhancementParametersURL:     "+NL+enhancementParametersURL);
            
            // Provenance extension
         }else if( "-prov".equalsIgnoreCase(arg)  || "-provenance".equalsIgnoreCase(arg) && hasNext ) {
            provenanceParametersURL = args[++i];
            System.err.println("provenanceParametersURL:      "+NL+provenanceParametersURL);
            
            // Converter identifier has
         }else if( "-id".equalsIgnoreCase(arg)  || "-identifier".equalsIgnoreCase(arg) && hasNext ) {
            converterIdentifier = args[++i];
            System.err.println("converterIdentifier:          "+NL+converterIdentifier);
            
            // Output extension
         }else if( "-oe".equalsIgnoreCase(arg) || "-outputExtension".equalsIgnoreCase(arg) && hasNext ) {
            outputExtension = args[++i];
            System.err.println("outputExtension:              "+NL+outputExtension);
            
            // Output file
         }else if( "-w".equalsIgnoreCase(arg) || "-outputFileName".equalsIgnoreCase(arg) && hasNext ) {
            
            outputFileName = args[++i];
            System.err.println("outputFileName:               "+NL+outputFileName);
            
            // Output file for metadata
         }else if( "-wm".equalsIgnoreCase(arg) || "-metadataOutputFileName".equalsIgnoreCase(arg) && hasNext ) {
            
            metaOutputFileName = args[++i];
            System.err.println("metaOutputFileName:           "+NL+metaOutputFileName);
            
            // Optional extensions to append to values of void:dataDump
            // The ONLY affect this parameter has is the void:dataDump triple.
         }else if( "-vde".equalsIgnoreCase(arg) || "-VoIDDumpExtensions".equalsIgnoreCase(arg) && hasNext ) {
            
            String extensionsString = args[++i];
            System.err.println("VoIDDumpExtensions:           "+NL+extensionsString);
            voidFileExtensions = new HashSet<String>();
            String[] extensions = extensionsString.split(",");
            for( int t = 0; t < extensions.length; t++) {
               if( extensions.length > 1 || t > 0 ) {
                  System.err.println("VoIDDumpExtension:              "+NL+extensions[t]);
               }
               voidFileExtensions.add("."+extensions[t].replaceAll("^\\.","")); // remove initial period.
            }
            
            // Examples (those explicitly stated in enhancement params)
         }else if( "-ego".equalsIgnoreCase(arg) || "-onlyConvertExampleResources".equalsIgnoreCase(arg) ) {

            examplesOnly = true;
            System.err.println("examplesOnly:                 "+NL+examplesOnly);
            
            // Samples
         }else if( "-sample".equalsIgnoreCase(arg) || "-onlyConvertSomeSamples".equalsIgnoreCase(arg) ) {

            sampleLimit = Integer.parseInt(args[++i]);
            System.err.println("sampleLimit:                  "+NL+sampleLimit);
         }else {
            System.err.println("unrecognized: "+arg);
         }
      }
      System.err.println("==============================");
      
      if (inFilename == null) {
         System.out.println(CSVtoRDF.USAGE);
         System.exit(1);
      }
      
      // Load the initial enhancement parameters.
      Repository enhancementParamsRep = Cat.load(enhancementParametersURL);
      if( Cat.ENCOUNTERED_PARSE_ERROR ) {
         System.err.println("ERROR; invalid RDF syntax in " + enhancementParametersURL);
         System.exit(3);
      }

      
      // Load any enhancement parameters that the initial parameters imports.
      ParametersImportQuerylet importsQ = new ParametersImportQuerylet(null);
      QueryletProcessor.processQuery(enhancementParamsRep, importsQ);
      for( URI importee : importsQ.get() ) {
         System.err.println("Initial parameters requests import of "+importee.stringValue());
         Cat.load(importee.stringValue(), enhancementParamsRep);
         // TODO: remove the source_identifier etc. triples.
      }
      
      // Load the file-level provenance to associate with triple-level assertion provenance.
      if( provenanceParametersURL != null ) {
         System.err.println("trying to load provenance @ "+provenanceParametersURL);
         Cat.load(provenanceParametersURL, enhancementParamsRep); // Load the provenance as well
      }

      OldNamespaceQuerylet oldNSq = new OldNamespaceQuerylet(null);
      QueryletProcessor.processQuery(enhancementParamsRep, oldNSq);
      if( oldNSq.get() ) {
         System.err.println("\n\n\nERROR\nhttp://data-gov.tw.rpi.edu/vocab/conversion/ is deprecated; "+
                            "use http://purl.org/twc/vocab/conversion/\n\n\n\n");
         System.exit(1);
      }
      
      DefaultEnhancementParameters enhancementParams = new DefaultEnhancementParameters(enhancementParamsRep, baseURI);
      // DEBUG
      //enhancementParams.printNamespaces(System.err); 
      // DEBUG
      
      CSVtoRDF csv2rdf = new CSVtoRDF(inFilename,
                                      classURI,
                                      subjectNS,uuidSubject,  predicateNS,uuidPredicate,  objectNS,uuidObject,
                                      // deprecated resourceOrLiteralBitString, 
                                      enhancementParams, 
                                      converterIdentifier, enhancementParametersURL, voidFileExtensions,
                                      examplesOnly, sampleLimit);
      
      // TODO: A Repository should be returned so that the csv can be manipulated as RDF at the API level.
      // need to not dump to sys.out if this is the case.
      // If dumping to sysout, then repository should be null
      Repository toRDF = csv2rdf.toRDF(outputFileName, metaOutputFileName);
      System.err.println("========== edu.rpi.tw.data.csv.CSVtoRDF complete. ==========");
   }
   
   /**
    * @param inFileName - the path of the csv file to load.
    * 
    * @param classURI - 
    * 
    * @param subjectNS - 
    * @param uuidSubject - 
    * 
    * @param predicateNS - 
    * @param uuidPredicate - 
    * 
    * @param objectNS -
    * @param uuidObject -
    * 
    * @param enhancementParams - 
    * 
    * @param converterIdentifier - 
    * @param enhancementParametersURL - 
    * 
    * @param voidFileExtensions - a set of extensions (e.g. 'ttl', 'nt', 'ttl.gz') to append to the URIs constructed as 
    *                             void:dataDumps of any dataset described during the conversion. Empty set OK, but 
    *                             you will need to add HTTP multiviews, etc. to serve the files.
    * 
    * @param examplesOnly - If true, only convert rows that are annotated in the enhancement parameters as being an 
    *                       example resource. If 'sampleLimit' is greater than 0, include those as well.
    * 
    * @param sampleLimit - If less than 0, convert all. If equal to 0, output everything BUT the data triples (provenance, etc).
    *                      If greater than 0, only convert the first 'sampleLimit'.
    */
   public CSVtoRDF(String inFileName,
                   String classURI,                           // This is outdated, but could become the generalization.
                   String subjectNS,   boolean uuidSubject,   // This is outdated, but could become the generalization.
                   String predicateNS, boolean uuidPredicate, // This is outdated, but could become the generalization.
                   String objectNS,    boolean uuidObject,    // This is outdated, but could become the generalization.
                   //deprecated String resourceOrLiteralBitString, 
                   EnhancementParameters enhancementParams, 
                   String converterIdentifier, String enhancementParametersURL,
                   Set<String> voidFileExtensions,
                   boolean examplesOnly, int sampleLimit) {
      
      this.inFileName          = inFileName;
      this.eParams             = enhancementParams; 
      this.eParamsTF           = (TemplateFiller) enhancementParams;
      boolean converterIdentifiable = converterIdentifier != null && 
                                      converterIdentifier.length() > 0 &&
                                    !"csv2rdf4lod_".equals(converterIdentifier);
      // Graph hash
      try {
         this.digest = new GraphDigest();
      } catch (NoSuchAlgorithmException e) {
         e.printStackTrace();
      }
      this.onlyConvertExampleResources = examplesOnly;
      this.sampleLimit                 = sampleLimit;
      
      this.primaryRepos   = new SailRepository(new MemoryStore());
      this.ancillaryRepos = new SailRepository(new MemoryStore());
      this.ontologyRepos  = new SailRepository(new MemoryStore());
      //   metaRep is created in toRDF()
      try { 
           primaryRepos.initialize();
         ancillaryRepos.initialize();
          ontologyRepos.initialize();
         this.primary   =   primaryRepos.getConnection();
         this.ancillary = ancillaryRepos.getConnection();
         this.ontology  =  ontologyRepos.getConnection();
      } catch(Exception e) {
         e.printStackTrace();
      }

      
      //
      // Figure out namespaces
      //
      
      String surrogateNS = this.eParams.getBaseURI()+"/";
      
      String datasetIdentifier;
      if( eParams.getTokenDatasetIdentifier()         != null && 
          eParams.getTokenDatasetIdentifier().length() > 0 ) {
         datasetIdentifier = eParams.getTokenDatasetIdentifier();
      }else {
         datasetIdentifier = NameFactory.getMillisecondToMinuteName("");         
      }

      String subjectDiscriminator = this.eParams.getTokenSubjectDiscriminator();
      
      // "T" for token
      String datasetSourceT        = NameFactory.slashIfThere("source",  this.eParams.getTokenDatasetSourceIdentifier()); 
      String datasetIdentifierT    = NameFactory.slashIfThere("dataset", this.eParams.getTokenDatasetIdentifier());
      String datasetVersionT       = NameFactory.slashIfThere("version", this.eParams.getTokenDatasetVersionIdentifier());
      String subjectDiscriminatorT = NameFactory.slashIfThere(           this.eParams.getTokenSubjectDiscriminator());
      String conversionIdentifierT = NameFactory.slashIfThere(           this.eParams.getStepConversionIdentifier());
      
      if( subjectNS == null ) {
         this.subjectNS   = surrogateNS + datasetSourceT + datasetIdentifierT + subjectDiscriminatorT + datasetVersionT;     
      }else {
         if( uuidSubject ) {
            this.subjectNS = subjectNS + "TODO-UUID";
         }else {
            this.subjectNS = subjectNS;
         }  
      }
      
      this.vocabNS = this.eParams.getNamespaceOfVocab();
      if( predicateNS == null ) {
         this.predicateNS = this.vocabNS + conversionIdentifierT;   
      }else {
         if( uuidPredicate ) {
            this.predicateNS = predicateNS + "TODO-UUID";
         }else {
            this.predicateNS = predicateNS;
         }
      }
      this.columnNameFactory    = new PropertyNameFactory("column", this.predicateNS, eParams);
      this.rawColumnNameFactory = new PropertyNameFactory("column", this.predicateNS, eParams);
      
      if( objectNS == null ) {
         this.objectNS   = surrogateNS + datasetSourceT + datasetIdentifierT + datasetVersionT;
         this.objectNSunversioned = surrogateNS + datasetSourceT + datasetIdentifierT;
         // NOTE: outside of version, will overlap with other versions.
         this.objectNS   = surrogateNS + datasetSourceT + datasetIdentifierT + subjectDiscriminatorT; 
      }else {
         if( uuidObject ) {
            this.objectNS = objectNS + "TODO-UUID";
         }else {
            this.objectNS = objectNS;
         }
      }

      //
      // Name datasets
      //
      this.topLevelVoID         = vf.createURI(this.eParams.getURIOfSiteLevelVoID());
      this.topLevelDatasetR     = vf.createURI(this.eParams.getURIOfSiteLevelDataset());
      this.abstractDatasetR     = vf.createURI(this.eParams.getURIOfAbstractDataset());
      this.versionedDatasetR    = vf.createURI(this.eParams.getURIOfVersionedDataset());
      this.layerDatasetR        = vf.createURI(this.eParams.getURIOfLayerDataset());
      this.thisDatasetLevelR    = vf.createURI(this.eParams.getURIOfVersionedDiscriminatedDatasetLayer());
      
      this.datasetSameAsSubsetR = vf.createURI(this.eParams.getURIOfDatasetSameAsSubset());
      this.metaSubsetR          = vf.createURI(this.eParams.getURIOfDatasetMetaData());
      this.sampleSubsetR        = vf.createURI(this.eParams.getURIOfLayerDatasetSample());
      
      
      //
      // Figure out namespace prefixes.
      //
      
      String dsPrefix = datasetIdentifier;
      if( datasetIdentifier.matches("^[0-9].*") ) {
         dsPrefix = "ds" + dsPrefix;
      }// this ns augmented again below
      String subjectPrefix = dsPrefix;
      if( eParams.getTokenSubjectDiscriminator().length() > 0 ) {
         subjectPrefix = subjectPrefix + "-" + this.eParams.getTokenSubjectDiscriminator().toLowerCase();// + 
                                         //"-" + this.eParams.getDatasetVersion().toLowerCase();
      }
      if( subjectPrefix.length() > 20 ) {
         subjectPrefix = "";
      }
      if( dsPrefix.length() > 20) {
         dsPrefix = "local";
      }
      
      String predicatePrefix = "raw";
      if( eParams.getTokenEnhancementIdentifier() != null && eParams.getTokenEnhancementIdentifier().length() >= 0 ) {
         // eParams returns "raw" or "enhancement/1" (when the input params includes :enhancement_identifier "1").
         // Grab the value after "enhancement/"
         predicatePrefix = "e" + eParams.getTokenEnhancementIdentifier();
      }
      
      String objectPrefix = subjectPrefix + "_global_value";
      
      System.err.println();
      System.err.println("subjectNS:   " + subjectPrefix   + ":  " + this.subjectNS);
      System.err.println("predicateNS: " + predicatePrefix + ":  " + this.predicateNS);
      System.err.println("objectNS:    " + objectPrefix    + ":  " + this.objectNS);
      System.err.println();
      
      try {
         this.primary.setNamespace("rdf",                           org.openrdf.model.vocabulary.RDF.NAMESPACE);
         this.primary.setNamespace("rdfs",                          org.openrdf.model.vocabulary.RDFS.NAMESPACE);
         this.primary.setNamespace("xsd",                          "http://www.w3.org/2001/XMLSchema#");
         this.primary.setNamespace("xsd2",                         "http://www.w3.org/TR/xmlschema-2/#");
         this.primary.setNamespace("owl",                           OWL.NAMESPACE);
         if( this.eParams.getSourceUsageURL() != null || converterIdentifiable ) {
            this.primary.setNamespace("pmlp",                      "http://inference-web.org/2.0/pml-provenance.owl#");
            this.primary.setNamespace("pmlj",                      "http://inference-web.org/2.0/pml-justification.owl#");
            this.primary.setNamespace("tw_service",                 surrogateNS+"source/tw-rpi-edu/service/");
            this.primary.setNamespace("tw_converter",               surrogateNS+"source/tw-rpi-edu/service/csv2rdf4lod/version/"+VERSION+"/");
            this.primary.setNamespace("provenance_"+eParams.getTokenConversionIdentifier(""),eParams.getNamespaceOfVersionedProvenance()); // Added layer token b/c virtuoso doesn't like redefining prefixes.
         }
         this.primary.setNamespace("conversion",                   Conversion.BASE_URI);
         this.primary.setNamespace("ov",                           "http://open.vocab.org/terms/");
         this.primary.setNamespace("dcterms",                       DCTerms.BASE_URI);
         this.primary.setNamespace("foaf",                          FOAF.BASE_URI);
         this.primary.setNamespace("void",                          VoID.BASE_URI);
         this.primary.setNamespace("doap",                          DOAP.BASE_URI);
         this.primary.setNamespace("scovo",                         SCOVO.BASE_URI);
         //huh? this.primary.setNamespace("sdmx",                          SDMX.BASE_URI);
         this.primary.setNamespace("qb",                            DataCube.BASE_URI);
         this.primary.setNamespace("vann",                          VANN.BASE_URI);
         this.primary.setNamespace("frbrcore",                      FRBR.BASE_URI);
         this.primary.setNamespace(CoIN.namespace.getPrefix(),      CoIN.namespace.getName());
         // @prefix ns1: <http://logd.tw.rpi.edu/source/congress-gov/dataset/biographical-directory-of-the-united-states-congress/> .
         this.primary.setNamespace(eParams.getTokenDatasetIdentifier(), eParams.getNamespaceOfAbstractDataset());
         this.primary.setNamespace(eParams.getTokenDatasetSourceIdentifier()+"_vocab", eParams.getNamespaceOfSource()+"vocab/");
         this.primary.setNamespace("base_vocab",                    eParams.getBaseURI()+"/vocab/");
         this.primary.setNamespace(dsPrefix+"_vocab",               this.vocabNS);
         this.primary.setNamespace("raw",                           eParams.getNamespaceOfRawProperty());
         this.primary.setNamespace(predicatePrefix,                 this.predicateNS);
         this.primary.setNamespace(subjectPrefix,                   this.subjectNS);
         this.primary.setNamespace(objectPrefix,                    this.objectNS);
         if( this.eParams.getAdditionalDescriptions().size() > 0 ) {
            this.primary.setNamespace("add_"+datasetIdentifier,     this.objectNS + "additional/");
         }
         if( subjectDiscriminator.length() > 0 ) {
            String discrimURI = this.eParams.getURIOfDiscriminator();
            //this.conn.setNamespace(datasetIdentifier+"_discrim", this.eParams.getDatasetNamespace() + "discriminator/");
            
            this.subjectDiscriminatorR = vf.createURI(discrimURI);
            if( this.eParams.hasIdentifiersSpecified() ) {
               primary.add(this.subjectDiscriminatorR, RDFS.LABEL,             vf.createLiteral(subjectDiscriminator));
               primary.add(this.subjectDiscriminatorR, DCTerms.isReferencedBy, this.versionedDatasetR);
            }
         }
         for( String promotionType : this.eParams.getRangeLocalNames() ) {
            if( promotionType != null ) {
               promotionType = NameFactory.label2URI(promotionType.toLowerCase());
               this.primary.setNamespace("typed_"+promotionType,    this.objectNS + "typed/" + promotionType + "/");
            }
         }
         for( Namespace ns : this.eParams.getOutputNamespaces() ) {
            this.primary.setNamespace(ns.getPrefix(), ns.getName());            
         }
         for( Namespace ns : this.primary.getNamespaces().asList() ) {
            ancillary.setNamespace(ns.getPrefix(), ns.getName());
             ontology.setNamespace(ns.getPrefix(), ns.getName());
         }
      } catch (RepositoryException e) {
         e.printStackTrace();
      }
      

      
      String subjectTypeLabel = this.eParams.getSubjectTypeLocalName();
      
      // Determine the non-numeric component of the local name of the URIs created for the row of each column.
      if( this.eParams.getPrimaryKeyColumnIndex() > 0 ) {
         this.instanceLocalPrefix = "";                       // Will be using value from primary key column.
      }else if( subjectTypeLabel != null && subjectTypeLabel.length() > 0 ) {
         this.instanceLocalPrefix = NameFactory.lowerFirst(NameFactory.label2URI(subjectTypeLabel)+"_");
      }else {
         this.instanceLocalPrefix = "thing_";
      }
      
      this.subjectRowTypeRs = getTypeRsFromLabel(subjectTypeLabel);
      
      this.vhFactory = new ValueHandlerFactory(eParams);
      
      prepareAdditionalDescriptions(); // Applied to all rows (or cells)

      // Setup URIs for PROVENANCE of conversion.
      if ( converterIdentifiable ) {
         this.converterIdentifier  = converterIdentifier;
         this.csv2rdf4lodInstanceR = vf.createURI(surrogateNS+"source/tw-rpi-edu/service/csv2rdf4lod/version/"+VERSION+"/"+converterIdentifier);
         this.csv2rdf4lodClassR    = vf.createURI(surrogateNS+"source/tw-rpi-edu/service/csv2rdf4lod");
      }else {
         this.csv2rdf4lodInstanceR = vf.createBNode();
      }
      this.provenanceBaseURI        = eParams.getNamespaceOfVersionedProvenance();
      this.enhancementParametersURL = enhancementParametersURL;
      
      // Extensions to append to dump file URLs.
      if( voidFileExtensions != null && voidFileExtensions.size() > 0 ) {
         this.voidFileExtensions = voidFileExtensions;
      }else {
         this.voidFileExtensions.add(""); // Will append nothing (leaving the extensionless URL for the dump file)
      }
   }
   
   /**
    * Uses a class rdfs:label to create a URI within the internal namespace.
    * Also collects the superclasses mentioned in the parameters.
    * 
    * @param classLabel - the local label of a class within the versioned namespace.
    * @return Set of URIs for class created from 'type' and all of its other superclasses.
    */
   private Set<URI> getTypeRsFromLabel(String classLabel) {

      Set<URI> typeRs = new HashSet<URI>();
      
      if( classLabel == null || classLabel.length() == 0 ) return typeRs;
      

      if( classLabel.contains("://") ) {
         
         // If the label looks like a URI, just return it.
         // Gathering superclasses of an existing URI is reasonably out of scope of a converter promoting data to semweb.
         typeRs.add(vf.createURI(classLabel));
         
      }else {
         
         // Determine if the row URI is typed with a local class.
         String typeLN = NameFactory.label2URI(classLabel);
         URI    typeR  = vf.createURI(this.vocabNS + typeLN);
         try {
            this.ontology.add(  typeR, RDF.TYPE,   RDFS.CLASS);
            this.ontology.add(  typeR, RDF.TYPE,   OWL.CLASS);
            this.ontology.add(  typeR, RDFS.LABEL, vf.createLiteral(classLabel));
            
            // Add the local type.
            typeRs.add(typeR);
            
            // Add all external types.
            for( URI superClass : eParams.getExternalSuperClassesOfLocalClass(classLabel) ) {
               typeRs.add(superClass);
               this.ontology.add(typeR,      RDFS.SUBCLASSOF, superClass);
               this.ontology.add(superClass, RDF.TYPE,        RDFS.CLASS);
               this.ontology.add(superClass, RDF.TYPE,        OWL.CLASS);
               //this.ontConn.add(superClass, RDFS.LABEL,      vf.createLiteral(classLabel));
            }
            
            classLabel = typeLN; // MUST be after type is used twice above.   --- what?
         } catch (RepositoryException e) {
            e.printStackTrace();
         }catch (Exception e) {
            System.err.println("local class: "+classLabel);
            e.printStackTrace();
         }
      }
      
      return typeRs;
   }
   
   /**
    * Create the predicates for the additional descriptions
    * 
    * @return
    */
   private void prepareAdditionalDescriptions() {
      
      for( String additionalPredicate : eParams.getAdditionalDescriptions().keySet() ) {   
         // TODO: use PropertyNameFactory to name predicate.
         URI predicateR = vf.createURI(predicateNS + PropertyNameFactory.getLocalNameFromLabel(additionalPredicate));
         
         String    o = eParams.getAdditionalDescriptions().get(additionalPredicate);
         Resource oR = ResourceValueHandler.isURI(o) 
                           ? vf.createURI(o)
                           : vf.createURI(objectNS+"additional/"+WikimediaURIMapper.map(o,false));
        
         this.additionalDescriptions.put(predicateR,oR);
         System.err.println("will add to all subjects:\n"+
                            "         "+predicateR.stringValue()+"\n"+
                            "         "+oR.stringValue());
         if( !this.onlyConvertExampleResources ) {
            try {
               primary.add(predicateR, RDFS.LABEL,                   vf.createLiteral(additionalPredicate));
               primary.add(predicateR, Conversion.enhancement_layer, vf.createLiteral(eParams.getTokenEnhancementIdentifier()));
               if( !ResourceValueHandler.isURI(o)  ) {
                  primary.add(oR, RDFS.LABEL, vf.createLiteral(o));
               }
            } catch (RepositoryException e) {
               e.printStackTrace();
            }
         }
      }
      
      try {
         primary.commit();
      } catch (RepositoryException e) {
         e.printStackTrace();
      }
      
      // For cell-based secondary dimensions (populated in visitHeader(columnIndex,headerLabel)
      //this.additionalColumnDescriptions = new HashMap<Integer,HashMap<URI,Value>>(); // Populated during visitHeader.
      //this.eParams.setAdditionalColumnDescriptions(additionalColumnDescriptions);
   }

   /**
    * The given cell value ('headerLabel') is a header, according to the enhancement parameters and
    * implemented by the parser (for example, the 15th row could be the header). Create the 
    * vocabulary that will be used when visiting the rows and cell values in this table. Further,
    * assert RDFS/OWL descriptions about the properties that will be used
    * (e.g. rdf:type rdf:Property, owl:DatatypeProperty) as well as provenance 
    * (e1:col_1 conversion:enhances raw:col_1_).
    * 
    * This method defers the predicate URI naming to PropertyNameFactory, but handles all other
    * vocabulary and provenance descriptions for whatever predicate is created.
    * 
    * @param rowIndex    - the row from which 'headerLabel' was taken.
    * @param columnIndex - the column from which 'headerLabel' was taken.
    * @param headerLabel - the value of the header, to be used to name the property with a URI.
    */
   @Override
   public void visitHeader(long rowIndex, int columnIndex, String headerLabel) {
      
      ValueHandler valueHandler = vhFactory.getValueHandler(columnIndex); 
      valueHandlers.put(columnIndex, valueHandler);
      
      // PROVENANCE
      if( this.eParams.getSourceUsageURL() != null && columnIndex == 1 ) {
         System.out.println(
         "@prefix rdf:        <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . \n" +
         "@prefix rdfs:       <http://www.w3.org/2000/01/rdf-schema#> . \n" +
         "@prefix xsd:        <http://www.w3.org/2001/XMLSchema#> . \n" +
         "@prefix owl:        <http://www.w3.org/2002/07/owl#> . \n" +
         "\n"+
         "@prefix pmlds:      <http://inference-web.org/2.0/ds.owl#> . \n" +
         "@prefix pmlp:       <http://inference-web.org/2.0/pml-provenance.owl#> . \n" +
         "@prefix j:          <http://inference-web.org/2.0/pml-justification.owl#> . \n" +
         "@prefix pmlt:       <http://inference-web.org/2.0/pml-trust.owl#> . \n" +
         "\n" +
         "@prefix ov:         <http://open.vocab.org/terms/> . \n" +
         "\n" +
         "@prefix conversion: <http://data-gov.tw.rpi.edu/vocab/conversion/> . \n" +
         "\n" +
         "@prefix our:        <http://provenance.rpi.edu/projects/emerging-semtech-2010/AJT/vocab/>  . \n" +
         //"@prefix :           <http://provenance.rpi.edu/projects/emerging-semtech-2010/AJT/instances/>  . \n" +
         "\n"+
         "<"+this.eParams.getSourceUsageURL()+">\n" +
         "   pmlp:hasModificationDateTime \""+eParams.getSourceUsageURLModificiationTime()+"\"^^xsd:dateTime; \n" +
         ".\n");
      }
      
      try {
         String csvHeader = CSVRecord.stripQuotes(headerLabel);
         this.originalColumnHeaders.put(columnIndex, csvHeader); // only for naming the up Object in cell-based. (and filling cell-based domain templates with [H])

         URI    predicateR  = columnNameFactory.namePropertyFromHeader(csvHeader, columnIndex);
         String propertyLN  = columnNameFactory.getPropertyLocalName(columnIndex);
         String columnLabel = columnNameFactory.getLabel(columnIndex);
         
         URI range           = valueHandler.getRange();
         URI owlPropertyType = range.equals(RDFS.RESOURCE) ? OWL.OBJECTPROPERTY : OWL.DATATYPEPROPERTY;
         
         if( !this.onlyConvertExampleResources ) {
            ontology.add(predicateR, RDF.TYPE,                     RDF.PROPERTY);
            ontology.add(predicateR, OpenVocab.csvRow,             vf.createLiteral(""+rowIndex,   XMLSchema.INTEGER));
            ontology.add(predicateR, OpenVocab.csvCol,             vf.createLiteral(""+columnIndex,XMLSchema.INTEGER));
            ontology.add(predicateR, OpenVocab.csvHeader,          vf.createLiteral(csvHeader));
            this.usedPredicateInMetaData(RDF.TYPE, OpenVocab.csvRow, OpenVocab.csvCol, OpenVocab.csvHeader);
            if( eParams.getTokenEnhancementIdentifier() != null ) {
               ontology.add(predicateR, Conversion.enhancement_layer, vf.createLiteral(eParams.getTokenEnhancementIdentifier()));
               this.usedPredicateInMetaData(Conversion.enhancement_layer);
            }
            ontology.add(predicateR, DCTerms.isReferencedBy,       vf.createURI(eParams.getURIOfLayerDataset()));
            ontology.add(predicateR, RDFS.LABEL,                   vf.createLiteral(columnLabel));
            ontology.add(predicateR, RDFS.RANGE,                   range);
            this.usedPredicateInMetaData(DCTerms.isReferencedBy, RDFS.LABEL, RDFS.RANGE);

            for( Value comment : eParams.getColumnComment(columnIndex) ) {
               ontology.add(predicateR, RDFS.COMMENT, comment);
               this.usedPredicateInMetaData(RDFS.COMMENT);
            }
         }
         
         if( ! eParams.isColumnOmitted(columnIndex) ) {
            System.err.println(columnIndex+": \"" + csvHeader+ "\" -> "+
                               "\"" + columnLabel+"\" -> "+
                               predicateR.stringValue()+" ("+DefaultPrefixMappings.getInstance().bestQNameForU(range)+")");
            System.err.println("----------------------------------------------------------------------------------------------------");
         }
                     
         if( RDFS.RESOURCE.equals(valueHandlers.get(columnIndex).getRange()) ) {
            String property_name = columnNameFactory.getPropertyLocalName(columnIndex);
              primary.setNamespace("value_of_" + property_name, this.objectNS + "value-of/" + property_name + "/");
            ancillary.setNamespace("value_of_" + property_name, this.objectNS + "value-of/" + property_name + "/");
             ontology.setNamespace("value_of_" + property_name, this.objectNS + "value-of/" + property_name + "/");            
         }
   
         // Handle provenance of predicate enhancement.
         // Recreate what the raw local name would be without stepping on the uniqueness calculations.
         // raw local name needed for e1:x conversion:enhances raw:column_1 (when x is provided as enhancement).
         
         String headerColumnLabel       = rawColumnNameFactory.createUniqueColumnLabel(csvHeader,columnIndex-1,!eParams.isCellBased(columnIndex));
         String headerPropertyLocalName = rawColumnNameFactory.getLocalNameFromLabel(headerColumnLabel);
         URI    headerPredicateR        = vf.createURI(eParams.getNamespaceOfRawProperty() + headerPropertyLocalName);
   
         URI    rawPredicateR           = vf.createURI(eParams.getNamespaceOfRawProperty() + propertyLN);
         
         if( !this.onlyConvertExampleResources ) {
            if( !predicateR.equals(headerPredicateR) ) {
               ontology.add(predicateR, Conversion.enhances, headerPredicateR); // Derived from pure header
               this.usedPredicateInMetaData(Conversion.enhances);
            }
            if( !predicateR.equals(rawPredicateR) ) {
               ontology.add(predicateR, Conversion.enhances, rawPredicateR);    // Using property_label column renaming.
               this.usedPredicateInMetaData(Conversion.enhances);
            }
            for( URI superProperty : eParams.getSuperProperties(columnIndex) ) {
               ontology.add(predicateR,    RDFS.SUBPROPERTYOF, superProperty);
               ontology.add(superProperty, RDF.TYPE,           RDF.PROPERTY);
               ontology.add(superProperty, RDF.TYPE,           owlPropertyType);
               this.usedPredicateInMetaData(RDFS.SUBPROPERTYOF, RDF.TYPE);
            }
            if( eParams.isCellBased(columnIndex) ) {
               // rdf:type cell-based predicates to http://purl.org/linked-data/cube#DimensionProperty
               // sdmx:DimensionProperty
               // see  http://publishing-statistical-data.googlecode.com/svn/trunk/specs/src/main/html/index.html#@@@
               // isCellBased(c)
               ontology.add(predicateR, RDF.TYPE, SDMX.DimensionProperty);
               this.usedPredicateInMetaData(RDF.TYPE);
               // TODO: still need to type the secondary (and the left-right predicates?)
            }
         }
         
         IPrefixMappings pmap = new DefaultPrefixMappings();
         String propertyNameToBundle = eParams.getImplicitBundlePropertyName(columnIndex);
         if(    propertyNameToBundle != null ) {
            boolean propertyIsURI = propertyNameToBundle.contains("://");
            
            // Property to bundle (property_name can be "My Awesome Predicate" or geonames:ParentFeature. TODO: separate predicates to cite each.
            String bundlePropertyLN = propertyIsURI ? pmap.bestLocalNameFor(propertyNameToBundle) : 
                                                      NameFactory.label2URI(propertyNameToBundle).toLowerCase(); // NameFactory.lowerFirst
                                                      // NOTE: Predicate will collide with similarly labeled column properties.
            URI bundlePredicateR    = propertyIsURI ? vf.createURI(propertyNameToBundle) : 
                                                      vf.createURI(this.predicateNS + bundlePropertyLN);
            if( columnNameFactory.isPropertyLocalNameDefined(bundlePropertyLN) ) {
               System.err.println(ColumnEnhancementQuerylet.REPORT_INDENT+
                   "WARNING: implicit bundle property overlaps with existing column-based property: "+bundlePropertyLN);
            } 
            this.predicatesToImplicitBundles.put(columnIndex, bundlePredicateR);
            
            // Bundle (type_name can be "My Awesome Class" or foaf:Person - both are handled. TODO: separate predicates used to cite each.
            String bundleTypeLabel  = eParams.getImplicitBundleTypeLocalName(columnIndex); 
            boolean bundleTypeIsURI = bundleTypeLabel != null && bundleTypeLabel.contains("://");
            String bundleTypeLN     = bundleTypeIsURI ? NameFactory.lowerFirst(pmap.bestLocalNameFor(bundleTypeLabel))
                                                      : NameFactory.lowerFirst(NameFactory.label2URI(bundleTypeLabel));
            
            String valORtype        = "";                      //bundlePropertyTypeLabel != null ? "typed" : "value-of";
            String bundleIdentifier = NameFactory.lowerFirst(NameFactory.label2URI(eParams.getImplicitBundleIdentifier(columnIndex)));
            String implicitName     = bundleTypeLN != null ? bundleTypeLN : "thing";
            String bundleLocalName  = valORtype + bundleIdentifier + "/" + implicitName;
            
            this.implicitBundleIdentifiers.put(columnIndex, bundleLocalName);
            this.implicitBundleTypes.put(columnIndex, this.getTypeRsFromLabel(bundleTypeLabel));
            System.err.println(ColumnEnhancementQuerylet.REPORT_INDENT+
                              "PO from "+columnIndex+" bundled by implicit "+bundleLocalName+"; "+
                              "property from row to bundle: \""+bundlePropertyLN+"\"");
            
              primary.setNamespace("implicit_" + bundlePropertyLN, eParams.getURIOfVersionedDataset() + "/" + bundleIdentifier + "/");
            ancillary.setNamespace("implicit_" + bundlePropertyLN, eParams.getURIOfVersionedDataset() + "/" + bundleIdentifier + "/");
             ontology.setNamespace("implicit_" + bundlePropertyLN, eParams.getURIOfVersionedDataset() + "/" + bundleIdentifier + "/");
         }
         
         // PROVENANCE
         if( eParams.getSourceUsageURL() != null ) {
            reportColumnEnhancementProvenance(
                                              eParams.getSourceUsageURL(),
                                              eParams.getSourceUsageURLModificiationTime(),
                                              eParams.getSourceUsageDateTime(),
                                              subjectDiscriminatorR, eParams.getTokenSubjectDiscriminator(),
                                              columnIndex, eParams.getMultipliers(columnIndex), eParams.getAuthors(),
                                              rawPredicateR,
                                              predicateR,
                                              csv2rdf4lodClassR
                                             );  
         }
         
         // Assert classes
         if( !assertedClasses ) {
            for( String classLabel : eParams.getTypeLocalNames() ) {
               if( classLabel != null && classLabel.length() > 0 ) {
                  getTypeRsFromLabel(classLabel); // Adds super classes. 
                                                  // Asserts a Class, rdfs:label, and rdfs:subClassOf.
               }
            }
            assertedClasses = true;
         }
         
         // Assert ontology descriptions into conn, too.
         Resource nullR = (Resource) null;
         for( Statement triple : ontology.getStatements(null, null, nullR, false, nullR).asList() ) {
            primary.add(triple);
         }
      } catch (RepositoryException e) {
         e.printStackTrace();
      }
      
      // If the range is rdfs:Resource or the cell is cell based.
      if( RDFS.RESOURCE.equals(valueHandlers.get(columnIndex).getRange()) || eParams.isCellBased(columnIndex) ) {

         HashMap<Value,Set<Value>> cellDescriptionsV = eParams.getCellBasedUpPredicateValueSecondaries(columnIndex);
        	
         // For each predicate
         for( Value predicate: cellDescriptionsV.keySet() ) {
         
            //System.err.println("    additional description: ."+predicate.stringValue()+".");
            
            String propertyS = predicate.stringValue(); /* replaced by logic below: 
                                  ResourceValueHandler.isURI(predicate.stringValue()) 
                                  ? predicate.stringValue() 
                                  : predicateNS + PropertyNameFactory.getLocalNameFromLabel(predicate.stringValue());*/
                                  
            if( eParamsTF.doesExpand(predicate.stringValue()) ) {
               // Predicate was a template that needs to expand (could be to URI or still a local).
               propertyS = eParams.fillTemplate(predicate.stringValue());
               //System.err.println("       filled template:     "+propertyS);
            }
            if( !ResourceValueHandler.isURI(propertyS) ) {
               // Property needs to become a URI.
               propertyS = predicateNS + PropertyNameFactory.getLocalNameFromLabel(predicate.stringValue());
               //System.err.println("       gotta make it a URI: "+propertyS);
            }else {
               // Property is a URI
               //System.err.println("       already a URI:       "+propertyS);
            }
            
            URI predicateR = vf.createURI(propertyS);
            for( Value o : cellDescriptionsV.get(predicate) ) {
               
               Value object   = eParamsTF.tryExpand(o.stringValue());

               //System.err.println("       final predicate URI: "+predicateR.stringValue());
               //System.err.println("       object:              "+object.stringValue());
               
               if( !eParams.getConstantAdditionalDescriptions(columnIndex).containsKey(predicateR)) {
                  eParams.getConstantAdditionalDescriptions(columnIndex).put(predicateR, new HashSet<Value>());
               }
               eParams.getConstantAdditionalDescriptions(columnIndex).get(predicateR).add(object);
            }
         }
      }
   }
   
   @Override
   public void visitRelativeToHeader(CSVRecord record, long rowNum) {   
      logger.finer("visiting RELATIVE Record "+rowNum);
      // Cache up any rows referenced relative to the header (by templates).
      for (int c = 1; c <= columnNameFactory.getProperties().size(); c++ ) {
         String fullCellValue = record.getQuotelessCommadValue(c-1);
         this.csvRecordFiller.setCellReferencedRelativeToHeader(rowNum - eParams.getHeaderRow(), c, fullCellValue);
      }
   }
   
   /**
    * 
    * @param sourceUsageURL
    * @param sourceUsageURLModificiationTime
    * @param sourceUsageDateTime
    * @param subjectDiscriminatorR
    * @param subjectDiscriminator
    * @param columnIndex
    * @param multipliers
    * @param authors
    * @param rawPredicateR
    * @param ePredicateR
    * @param csv2rdfR2
    */                 // PROVENANCE
   private static void reportColumnEnhancementProvenance(String      sourceUsageURL,
                                                         String      sourceUsageURLModificiationTime, 
                                                         String      sourceUsageDateTime,
                                                         URI         subjectDiscriminatorR, String subjectDiscriminator,
                                                         int         columnIndex, 
                                                         Set<Double> multipliers,
                                                         Set<URI>    authors,
                                                         URI         rawPredicateR, 
                                                         URI         ePredicateR, 
                                                         URI         csv2rdfR) {
      
      /*System.err.println("REPORTING PROVENANCE OF COLUMN.\n   " +
            sourceUsageURL+"\n   "+
            sourceUsageURLModificiationTime+" - url mod datetime\n   "+
            sourceUsageDateTime+" - usage datetime\n   "+
            subjectDiscriminatorR+"\n   "+
            columnIndex+"\n   "+
            rawPredicateR.stringValue()+"\n   "+
            ePredicateR.stringValue()+"\n   "+
            csv2rdf4lodClassR.stringValue()
      );*/

      StringBuffer multString = new StringBuffer();
      for( Double multiplier : multipliers ) {
         multString.append(" conversion:multiplier "+multiplier+";");
      }
      
      StringBuffer authString = new StringBuffer();
      for( URI author : authors ) {
         multString.append(" conversion:author <"+author.stringValue()+">;");
      }
      
      String under = subjectDiscriminator != null && subjectDiscriminator.length() > 0 ? "_" : "";
      String prov1 = 
      "<"+ePredicateR.stringValue()+"/justification"+under+subjectDiscriminator+">\n"+
      "   a j:NodeSet; \n" +
      "   a our:NodeSetTemplate;\n" +
      "   j:hasConclusion [ \n" +
      "      a pmlp:Information; \n";

      String discriminatorProv = "\n";
      if( subjectDiscriminatorR != null ) {
      discriminatorProv = "      rdf:subject  [ ov:subjectDiscriminator <"+subjectDiscriminatorR.stringValue()+"> ];\n";
      }

      String prov3 =
      "      rdf:predicate  <"+ePredicateR.stringValue()+">; \n"+
      "   ];\n" +
      "   j:isConsequentOf [ \n" +
      "      a j:InferenceStep; \n" +
      "      j:hasInferenceEngine <"+csv2rdfR.stringValue()+">; \n" +
      "      j:hasInferenceRule  [ conversion:enhance [ ov:csvCol "+columnIndex+";"+multString+" "+authString+"] ];\n"+
      "      j:hasAntecedentList ( \n" +
      "         [ a j:NodeSet; \n" +
      "           j:hasConclusion [ \n"+
      "              a pmlp:Information; \n" +
      "              a our:InformationTemplate; \n" +
      "        "+discriminatorProv+
      "              rdf:predicate  <"+rawPredicateR.stringValue()+">; \n" +
      "              pmlp:hasReferenceSourceUsage [  \n" +
      "                 a pmlp:SourceUsage; \n" +
      "                 pmlp:hasSource [  \n" +
      "                    a pmlp:DocumentFragmentByRowCol; \n" +
      "                    pmlp:hasDocument <"+sourceUsageURL+">; \n" +
      "                    pmlp:hasFromCol "+columnIndex+"; \n" +
      "                    pmlp:hasToCol   "+columnIndex+"; \n" +
      "                 ];\n" +
      "                 pmlp:hasUsageDateTime \""+sourceUsageDateTime+"\"^^xsd:dateTime; \n" +
      "              ];\n" +
      "           ]\n" +
      "        ] # NodeSet    \n" +
      "      ); # NodeSetList \n" +
      "   ]; # InferenceStep \n" +
      ". \n";
      System.out.println(prov1 + discriminatorProv + prov3);
   }

   /**
    * @return true if void, provenance, and other metadata should be output; 
    *         false otherwise (only data triples will be produced). 
    */
   private boolean includeMetadata() {
      // Be careful if you change this; it is used in a variety of places.
      return ! (this.onlyConvertExampleResources || eParams.getSourceUsageURL() != null);
   }
   
   /**
    * @param outputFileName - file name to dump converted RDF to.
    * @param metaOutputFileName - file name to dump metadata to.
    * 
    * @return if 'outputFileName' is specified, dump to (and overwrite) given file name and return its summary.
    *         if 'outputFileName' is empty string or "-", dump to stdout and return its summary.
    *         if 'outputFileName' is null, do not dump and return the entire converted RDF (including its summary).
    */
   public Repository toRDF(String outputFileName, String metaOutputFileName) {
      
   	this.translationActivity = vf.createURI(eParams.getNamespaceOfVersionedProvenance()+NameFactory.getUUIDName("translationActivity"));
   	
      this.outputFileName     = outputFileName;
      this.appendToOutputFile = false;
      
      // -----------------------------------
      //   primaryRepos, 
      // ancillaryRepos, and 
      //  ontologyRepos  are created earlier.
      // -----------------------------------
      // metaRepos       is created here.
      // -----------------------------------
      Repository metaRepos = new SailRepository(new MemoryStore()); // Created and used to make a comment if
      try {
         metaRepos.initialize();
      } catch (RepositoryException e1) {
         e1.printStackTrace();
      }
      RepositoryConnection meta = null;                             // error XOR to describe the completed
                                                                    // conversion using VoID, PML, etc.
      if( this.eParams.hasIdentifiersSpecified() ) { // Avoid SSS DDD VVV
         //
         // Assert data triples; source_identifier, dataset_identifier, and version_identifier are set.
         //
         this.justProvenance      = this.eParams.getSourceUsageURL() != null;
         this.sourceUsageURL      = this.eParams.getSourceUsageURL();    // PROVENANCE stuff
         this.sourceUsageDateTime = this.eParams.getSourceUsageDateTime();
         this.startTime           = System.currentTimeMillis();
         
         this.cellBasedColumnsSorted = new ArrayList<Integer>();
         for( Integer column : this.eParams.getCellBasedColumns() ) {
            this.cellBasedColumnsSorted.add(column);
         }
         Collections.sort(this.cellBasedColumnsSorted);
         
         this.eParams.getCellBasedColumns();
         char delimiter = this.eParams.getDelimiter();
         this.csvRecordFiller = new CSVRecordTemplateFiller(this.columnNameFactory, this.eParams);
         try {
            //
            // visitRecords invokes visits to all records.
            //
            //
            // Uses http://javacsv.sourceforge.net; has error on ,",My Company Inc.",
            CSVParser.visitRecords(new BufferedInputStream(new FileInputStream(inFileName)), 
                                   delimiter, eParams.hasLargeValues(), eParams.getCharset(),
                                   eParams.getHeaderRow(), eParams.getRowsReferencedRelativeToHeader(),
                                   eParams.getDataStartRow(), eParams.getDataEndRow(), this.sampleLimit,
                                   this); // TODO: csvReader.setUseComments(true); (need to encode in params first)
            //
            //
            // By here, all records have been visited and RDF has been produced.
            //
            // RDF could have already been flushed from this.primaryRepos and this.ancillaryRepos.
            // Unflushed RDF can still be in this.primaryRepos and this.ancillaryRepos
            //
            //
         }catch( FileNotFoundException e ) {
            e.printStackTrace();
         }catch( IOException e ) {
            e.printStackTrace();
         }


         // Assert VoID metadata and provenance of dump files:
         
         
         //
         // -wm metaOutputFileName puts descriptions of the dataset into a separate repository.
         //
         try {
            Set<URI> dataDumps = new HashSet<URI>(); // PROVENANCE
            
            // 2012 May links_via pass through (now done above) metaRepos.initialize();
            meta = metaRepos.getConnection();
            for( Namespace ns : this.primary.getNamespaces().asList() ) {
               meta.setNamespace(ns.getPrefix(), ns.getName());
            }
                    
            Value modifiedV = vf.createLiteral(DateTimeValueHandler.getNowXMLGregorianCalendar());
   
            // BASE_URI/void.ttl
            //
            // c.f. https://github.com/timrdf/csv2rdf4lod-automation/issues/303
            //      http://www.w3.org/TR/void/#void-file
            //
            meta.add(topLevelVoID,     RDF.TYPE,          VoID.DatasetDescription);
            meta.add(topLevelVoID,     FOAF.topic,        abstractDatasetR);
            meta.add(topLevelVoID,     FOAF.primaryTopic, topLevelDatasetR);
            meta.add(topLevelDatasetR, RDF.TYPE,          VoID.DATASET);
            meta.add(topLevelDatasetR, VoID.subset,       abstractDatasetR);
            this.usedPredicateInMetaData(RDF.TYPE, FOAF.topic, FOAF.primaryTopic, VoID.subset);
            this.usedClassInMetaData(VoID.DatasetDescription, VoID.DATASET);
            

            
            // conversion:AbstractDataset
            //
            // e.g. http://logd.tw.rpi.edu/source/data-gov/dataset/1008                                <-- single   CSV
            // e.g. http://logd.tw.rpi.edu/source/data-gov/dataset/1033                                <-- multiple CSV
            String identifier = eParams.getTokenDatasetSourceIdentifier() + " " + eParams.getTokenDatasetIdentifier();
            meta.add(abstractDatasetR, RDF.TYPE,                      vf.createURI(this.eParams.getURIOfDatasetType()));
            meta.add(abstractDatasetR, RDF.TYPE,                      Conversion.DATASET);
            meta.add(abstractDatasetR, RDF.TYPE,                      Conversion.AbstractDataset);
            meta.add(abstractDatasetR, RDF.TYPE,                      VoID.DATASET);
            if( eParams.getTokenEnhancementIdentifier() != null ) {
            	meta.add(abstractDatasetR, RDF.TYPE,                   Conversion.EnhancedDataset);
            }
            meta.add(abstractDatasetR, Conversion.base_uri,           vf.createLiteral(eParams.getBaseURI()));
            meta.add(abstractDatasetR, Conversion.source_identifier,  vf.createLiteral(eParams.getTokenDatasetSourceIdentifier()));
            meta.add(abstractDatasetR, Conversion.dataset_identifier, vf.createLiteral(eParams.getTokenDatasetIdentifier()));
            meta.add(abstractDatasetR, DCTerms.contributor,           vf.createURI(    eParams.getURIOfDatasetSource()));
            meta.add(abstractDatasetR, DCTerms.identifier,            vf.createLiteral(identifier));
            meta.add(abstractDatasetR, FOAF.isPrimaryTopicOf,         vf.createURI(    eParams.getURIOfDatasetPage()));
            meta.add(abstractDatasetR, VoID.subset,                   versionedDatasetR);
            meta.add(abstractDatasetR, VoID.subset,                   metaSubsetR);
            if( objectsLinkedWithSameAs() ) {
               meta.add(abstractDatasetR, VoID.subset,                datasetSameAsSubsetR);
            }
            meta.add(abstractDatasetR, DCTerms.modified,              modifiedV);
            this.usedPredicateInMetaData(RDF.TYPE, Conversion.base_uri, Conversion.source_identifier, 
            		                       Conversion.dataset_identifier, DCTerms.contributor, DCTerms.identifier,
            		                       FOAF.isPrimaryTopicOf, VoID.subset);
            this.usedClassInMetaData(vf.createURI(this.eParams.getURIOfDatasetType()), 
            								 Conversion.DATASET, Conversion.AbstractDataset, VoID.DATASET);
            
            
            // conversion:VersionedDataset
            //
            // e.g. http://logd.tw.rpi.edu/source/data-gov/dataset/1008/version/2010-Jul-21            <-- single   CSV
            // e.g. http://logd.tw.rpi.edu/source/data-gov/dataset/1033/version/2010-Jul-21            <-- multiple CSV
            // System.err.println(indent+ versionedDatasetR.stringValue());
            identifier += " " + eParams.getTokenDatasetVersionIdentifier(); // Now has S D V
            //NOTE: removed when adding optional extensions: URI dataDump = vf.createURI(eParams.getURIOfDumpFileVersioned());
            meta.add(versionedDatasetR, RDF.TYPE,                      vf.createURI(eParams.getURIOfDatasetType()));
            meta.add(versionedDatasetR, RDF.TYPE,                      Conversion.DATASET);
            meta.add(versionedDatasetR, RDF.TYPE,                      Conversion.VersionedDataset);
            meta.add(versionedDatasetR, RDF.TYPE,                      VoID.DATASET);
            if( eParams.getTokenEnhancementIdentifier() != null ) {
            	meta.add(versionedDatasetR, RDF.TYPE,                   Conversion.EnhancedDataset);
            }
            meta.add(versionedDatasetR, Conversion.base_uri,           vf.createLiteral(eParams.getBaseURI()));
            meta.add(versionedDatasetR, Conversion.source_identifier,  vf.createLiteral(eParams.getTokenDatasetSourceIdentifier()));
            meta.add(versionedDatasetR, Conversion.dataset_identifier, vf.createLiteral(eParams.getTokenDatasetIdentifier()));
            meta.add(versionedDatasetR, Conversion.version_identifier, vf.createLiteral(eParams.getTokenDatasetVersionIdentifier()));
            meta.add(versionedDatasetR, Conversion.dataset_version,    vf.createLiteral(eParams.getTokenDatasetVersionIdentifier())); // TODO: remove. dataset_identifier more consistent.
            meta.add(versionedDatasetR, DCTerms.contributor,           vf.createURI(eParams.getURIOfDatasetSource()));
            meta.add(versionedDatasetR, DCTerms.identifier,            vf.createLiteral(identifier));
            meta.add(versionedDatasetR, DCTerms.modified,              modifiedV);
            meta.add(versionedDatasetR, FOAF.isPrimaryTopicOf,         vf.createURI(eParams.getURIOfVersionedDatasetPage()));
            assertVoIDdataDumps(versionedDatasetR, eParams.getURIOfDumpFileVersioned(), meta, dataDumps);
            meta.add(versionedDatasetR, VoID.subset,                   layerDatasetR);
            this.usedPredicateInMetaData(Conversion.version_identifier, DCTerms.modified); // others asserted above.
            this.usedPredicateInMetaData(Conversion.VersionedDataset);

            // conversion:LayerDataset
            //
            // These descriptions apply to the third level in BOTH the 3- and 4-level cases.
            //
            Value conversionIdentifierV = vf.createLiteral("raw");
            if( eParams.getTokenEnhancementIdentifier() != null ) {
               conversionIdentifierV = vf.createLiteral(eParams.getStepConversionIdentifier()); 
            }
            meta.add(layerDatasetR, RDF.TYPE,                         vf.createURI(eParams.getURIOfDatasetType()));
            meta.add(layerDatasetR, RDF.TYPE,                         Conversion.DATASET);
            meta.add(layerDatasetR, RDF.TYPE,                         Conversion.LayerDataset);
            meta.add(layerDatasetR, RDF.TYPE,                         VoID.DATASET);
            if( eParams.getTokenEnhancementIdentifier() != null ) {
            	meta.add(layerDatasetR, RDF.TYPE,                      Conversion.EnhancedDataset);
            }else {
            	meta.add(layerDatasetR, RDF.TYPE,                      Conversion.RawDataset);
            }
            meta.add(layerDatasetR, DCTerms.modified,                 modifiedV);
            meta.add(layerDatasetR, Conversion.base_uri,              vf.createLiteral(eParams.getBaseURI())); // TODO: these 5 could be removed if depend on VersionedDatasetDescriptions
            meta.add(layerDatasetR, Conversion.source_identifier,     vf.createLiteral(eParams.getTokenDatasetSourceIdentifier()));
            meta.add(layerDatasetR, Conversion.dataset_identifier,    vf.createLiteral(eParams.getTokenDatasetIdentifier()));
            meta.add(layerDatasetR, Conversion.version_identifier,    vf.createLiteral(eParams.getTokenDatasetVersionIdentifier()));
            meta.add(layerDatasetR, Conversion.dataset_version,       vf.createLiteral(eParams.getTokenDatasetVersionIdentifier())); // TODO: remove. dataset_identifier more consistent.
            meta.add(layerDatasetR, Conversion.conversion_identifier, conversionIdentifierV);
            meta.add(layerDatasetR, FOAF.isPrimaryTopicOf,            vf.createURI(eParams.getURIOfVersionedDatasetLayerPage()));
            assertVoIDdataDumps(layerDatasetR, eParams.getURIOfDumpFileVersionedLayer(), meta, dataDumps); // raw.sample.ttl and e1.sample.ttl
            meta.add(layerDatasetR, VoID.subset,                      sampleSubsetR); 
            this.usedPredicateInMetaData(Conversion.conversion_identifier); // others asserted above.
            this.usedPredicateInMetaData(Conversion.LayerDataset);

            // Assert metadata about vocabulary use.
            // https://github.com/timrdf/csv2rdf4lod-automation/issues/300
            for( int c = 1; c <= columnNameFactory.getProperties().size(); c++ ) {
         		this.predicatesUsedInData.addAll(this.valueHandlers.get(c).getAssertedPredicates());
         		this.classesUsedInData.addAll(   this.valueHandlers.get(c).getAssertedClasses());
         	}
            CSVtoRDF.assertVocabularyUse(layerDatasetR, Conversion.uses_predicate, this.predicatesUsedInData, meta);
            CSVtoRDF.assertVocabularyUse(layerDatasetR, Conversion.uses_class,     this.classesUsedInData,    meta);
           	if( this.predicatesUsedInData.size() > 0 ) this.usedPredicateInMetaData(Conversion.uses_predicate);
            if( this.classesUsedInData.size()    > 0 ) this.usedPredicateInMetaData(Conversion.uses_class);


            // Example resources (in VoID)
            if( this.firstSubjectR != null ) {
            	meta.add(layerDatasetR, VoID.exampleResource, this.firstSubjectR);
            	this.usedPredicateInMetaData(VoID.exampleResource);
            }
            if( this.lastSubjectR  != null ) {
            	meta.add(layerDatasetR, VoID.exampleResource, this.lastSubjectR);
            	this.usedPredicateInMetaData(VoID.exampleResource);
            }
            for( URI example : this.exampleResources ) {
               meta.add(layerDatasetR, VoID.exampleResource, example);
               this.usedPredicateInMetaData(VoID.exampleResource);
            }

            // Get arbitrary annotations as specified by the enhancement parameters.
            HashMap<Value,HashSet<Value>> datasetDescriptions = eParams.getLayerDatasetDescriptions();
            for( Value predicate : datasetDescriptions.keySet() ) {
               if( predicate instanceof URI ) {
                  HashSet<Value> objects = datasetDescriptions.get(predicate);
                  for( Value object : objects) {
                     meta.add(layerDatasetR, (URI) predicate, object);
                     this.usedPredicateInMetaData((URI) predicate);
                     //logger.finer("describing dataset "+layerDatasetR.stringValue()+ " with "+predicate.stringValue() + " " +object.stringValue());
                  }
               }else {
                  // TODO: mint predicate for this in local namespace.
                  System.err.println("WARNING: NOT describing dataset "+layerDatasetR.stringValue()+ " with "+predicate.stringValue());
               }
            }
            

            
            // conversionn:LayerDataset  or  void:subset [ a void:Dataset ]
            //
            // thisDatasetLevelR could be layerDatasetR or a Dataset one level down.
            //
            if( ! thisDatasetLevelR.equals(layerDatasetR) ) { // change to: eParams.isMultiPart()
               identifier += " " + eParams.getTokenConversionIdentifier("e");
               meta.add(layerDatasetR, DCTerms.contributor, vf.createURI(eParams.getURIOfDatasetSource()));
               meta.add(layerDatasetR, DCTerms.identifier,  vf.createLiteral(identifier));
               meta.add(layerDatasetR, VoID.subset,         thisDatasetLevelR);
               identifier = eParams.getTokenDatasetSourceIdentifier() + " " + 
                            eParams.getTokenDatasetIdentifier() + " " + 
                            eParams.getTokenSubjectDiscriminator() + " " + // <-- insert [sd] Now has: S D sd V
                            eParams.getTokenDatasetVersionIdentifier(); 
               
               // This CSV was 'one of multiple' CSVs within the versioned dataset.
               //
               // The URI for this dataset level includes the subjectDiscriminator, 
               // making an extra level in the VoID hierarchy.
               //
               // e.g.                  http://logd.tw.rpi.edu/source/data-gov/dataset/1033
               //                       http://logd.tw.rpi.edu/source/data-gov/dataset/1033/version/2010-Jul-21
               // (versionedLayerLevel) http://logd.tw.rpi.edu/source/data-gov/dataset/1033/version/2010-Jul-21/conversion/enhancement/1                  (has void:dataDump)
               // (thisLevel)           http://logd.tw.rpi.edu/source/data-gov/dataset/1033/fm_facility_file/version/2010-Jul-21/conversion/enhancement/1 (has conversion:num_triples)
            }else {
               // This CSV is the ONLY CSV within the versioned dataset.
               //
               // e.g. http://logd.tw.rpi.edu/source/data-gov/dataset/1008
               //      http://logd.tw.rpi.edu/source/data-gov/dataset/1008/version/2010-Jul-21
               // >    http://logd.tw.rpi.edu/source/data-gov/dataset/1008/version/2010-Jul-21/conversion/enhancement/1 (has void:dataDump and conversion:num_triples)
               //      XxXxXxXxX (there is no fourth level in the VoID hierarchy)
               assertVoIDdataDumps(thisDatasetLevelR, eParams.getURIOfDumpFileVersionedLayer(), meta, dataDumps);
            }
   
            
            // This describes the last level in the VoID hiearchy, whether that be the 3rd or 4th level.
            // (3rd level in VoID hierarchy): versioned dataset from one CSV. 
            // (4th level in VoID hierarchy): subject-discriminated from multiple CSVs.
            identifier += " " + eParams.getTokenConversionIdentifier("e");
            meta.add(thisDatasetLevelR, RDF.TYPE,                      vf.createURI(eParams.getURIOfDatasetType()));
            meta.add(thisDatasetLevelR, RDF.TYPE,                      Conversion.DATASET);
            meta.add(thisDatasetLevelR, RDF.TYPE,                      VoID.DATASET);
            if( eParams.getTokenEnhancementIdentifier() != null ) {
            	meta.add(thisDatasetLevelR, RDF.TYPE,                   Conversion.EnhancedDataset);
            }else {
            	meta.add(thisDatasetLevelR, RDF.TYPE,                   Conversion.RawDataset);
            }
            if( eParams.isMultiPart() ) {
            	meta.add(thisDatasetLevelR, DCTerms.contributor,        vf.createURI(eParams.getURIOfDatasetSource()));
            }
            meta.add(thisDatasetLevelR, DCTerms.created,               vf.createLiteral(this.startTimeCal));
            meta.add(thisDatasetLevelR, DCTerms.modified,              modifiedV);
            meta.add(thisDatasetLevelR, Conversion.base_uri,           vf.createLiteral(eParams.getBaseURI()));
            meta.add(thisDatasetLevelR, Conversion.source_identifier,  vf.createLiteral(eParams.getTokenDatasetSourceIdentifier()));
            meta.add(thisDatasetLevelR, Conversion.dataset_identifier, vf.createLiteral(eParams.getTokenDatasetIdentifier()));
            if( eParams.isMultiPart() ) {
            	meta.add(thisDatasetLevelR, Conversion.subject_discriminator, vf.createLiteral(eParams.getTokenSubjectDiscriminator()));
            	this.usedPredicateInMetaData(Conversion.subject_discriminator);
            }
            meta.add(thisDatasetLevelR, Conversion.version_identifier,    vf.createLiteral(eParams.getTokenDatasetVersionIdentifier()));
            meta.add(thisDatasetLevelR, Conversion.dataset_version,       vf.createLiteral(eParams.getTokenDatasetVersionIdentifier())); // TODO: remove. dataset_identifier more consistent.
            meta.add(thisDatasetLevelR, Conversion.conversion_identifier, vf.createLiteral(eParams.getStepConversionIdentifier()));  
            meta.add(thisDatasetLevelR, DCTerms.contributor,              vf.createURI(    eParams.getURIOfDatasetSource()));
            meta.add(thisDatasetLevelR, DCTerms.identifier,               vf.createLiteral(identifier));
            this.usedPredicateInMetaData(Conversion.conversion_identifier);
            


            // owl:sameAs void:Linkset
            //
            //
            if( objectsLinkedWithSameAs() ) {
               meta.add(datasetSameAsSubsetR, RDF.TYPE,                      vf.createURI(eParams.getURIOfDatasetType()));
               meta.add(datasetSameAsSubsetR, RDF.TYPE,                      Conversion.DATASET);
               meta.add(datasetSameAsSubsetR, RDF.TYPE,                      Conversion.SameAsDataset);
               meta.add(datasetSameAsSubsetR, RDF.TYPE,                      VoID.DATASET);
               meta.add(datasetSameAsSubsetR, RDF.TYPE,                      VoID.Linkset); 
               meta.add(datasetSameAsSubsetR, Conversion.base_uri,           vf.createLiteral(eParams.getBaseURI()));
               meta.add(datasetSameAsSubsetR, Conversion.source_identifier,  vf.createLiteral(eParams.getTokenDatasetSourceIdentifier()));
               meta.add(datasetSameAsSubsetR, Conversion.dataset_identifier, vf.createLiteral(eParams.getTokenDatasetIdentifier()));
               // sameas subset does NOT have a version; it is across versions.
               meta.add(datasetSameAsSubsetR, DCTerms.modified,              modifiedV);
               meta.add(datasetSameAsSubsetR, FOAF.isPrimaryTopicOf,         vf.createURI(eParams.getURIOfDatasetSameAsSubsetPage()));
               meta.add(datasetSameAsSubsetR, VoID.vocabulary,               vf.createURI(OWL.NAMESPACE));
               assertVoIDdataDumps(datasetSameAsSubsetR, eParams.getURIOfDumpFileSubsetSameAs(), meta, dataDumps);
               this.usedPredicateInMetaData(RDF.TYPE, Conversion.base_uri, 
               									  Conversion.source_identifier, 
               									  Conversion.dataset_identifier, DCTerms.modified, 
               									  FOAF.isPrimaryTopicOf, VoID.vocabulary);
               this.usedClassInMetaData(Conversion.SameAsDataset,VoID.Linkset);
            }
   
       
            // conversion:DatasetSample
            //
            //
            meta.add(sampleSubsetR, RDF.TYPE,                         vf.createURI(this.eParams.getURIOfDatasetType()));
            meta.add(sampleSubsetR, RDF.TYPE,                         Conversion.DATASET);
            meta.add(sampleSubsetR, RDF.TYPE,                         Conversion.DatasetSample);
            meta.add(sampleSubsetR, RDF.TYPE,                         VoID.DATASET);
            meta.add(sampleSubsetR, Conversion.base_uri,              vf.createLiteral(eParams.getBaseURI()));
            meta.add(sampleSubsetR, Conversion.source_identifier,     vf.createLiteral(eParams.getTokenDatasetSourceIdentifier()));
            meta.add(sampleSubsetR, Conversion.dataset_identifier,    vf.createLiteral(eParams.getTokenDatasetIdentifier()));
            meta.add(sampleSubsetR, Conversion.version_identifier,    vf.createLiteral(eParams.getTokenDatasetVersionIdentifier()));
            meta.add(sampleSubsetR, Conversion.dataset_version,       vf.createLiteral(eParams.getTokenDatasetVersionIdentifier())); // TODO: remove
            meta.add(sampleSubsetR, Conversion.conversion_identifier, vf.createLiteral(eParams.getStepConversionIdentifier())); 
            meta.add(sampleSubsetR, DCTerms.modified,                 modifiedV);
            meta.add(sampleSubsetR, FOAF.isPrimaryTopicOf,            vf.createURI(eParams.getURIOfLayerDatasetSamplePage()));
            //for( String extension : this.voidFileExtensions ) {
            URI dataDump = vf.createURI(eParams.getURIOfDumpFileDatasetSample()+".ttl"); // ALL are turtle; never compressed.
            meta.add(sampleSubsetR, VoID.dataDump, dataDump); // Samples are never compressed.
            meta.add(dataDump, DCTerms.format, W3CFormats.Turtle);
            dataDumps.add(dataDump);
            //}
            this.usedPredicateInMetaData(VoID.dataDump, DCTerms.format);
            this.usedClassInMetaData(VoID.DATASET, Conversion.DATASET, Conversion.DatasetSample);
               
            
            // Describe the source
            URI sourceR = vf.createURI(eParams.getURIOfDatasetSource());
            meta.add(sourceR, RDF.TYPE,              FOAF.Agent);
            meta.add(sourceR, DCTerms.identifier,    vf.createLiteral(eParams.getTokenDatasetSourceIdentifier()));
            meta.add(sourceR, FOAF.isPrimaryTopicOf, vf.createURI(    eParams.getURIOfDatasetSourcePage()));
            this.usedClassInMetaData(FOAF.Agent);
            
            //  includeMetadata() : return 
            //  ! (this.onlyConvertExampleResources || eParams.getSourceUsageURL() != null)
            if( includeMetadata() ) { // 
               PrefixMappings.encodePrefixesAsVANN(metaRepos); // Switched from ancillaryRepos Sept 2011 for FRBR graph digesting.
               this.usedPredicateInMetaData(VANN.preferredNamespacePrefix, 
               									  VANN.preferredNamespaceUri, RDFS.SEEALSO);
               		
               // Add PROVENANCE for data dumps.                  <----------------------------------------------------
               assertConversionProvenance(meta, dataDumps);    // <--------- dataDumps --------------------------------
            }                                                  // <----------------------------------------------------
            
            // Add rdfs:isDefinedBy for each of the conversion: terms.
            for( URI term : Conversion.getTerms() ) {
               meta.add(term, RDFS.ISDEFINEDBY, vf.createURI(Conversion.BASE_URI));
               this.usedPredicateInMetaData(RDFS.ISDEFINEDBY);
            }
            
            // Add all enhancement parameters RDF to metaConn.
            eParams.assertEnhancementsRepository(meta); // TODO: eparams have non-unique bnodes
            
            
            // conversion:MetaDataset
            //
            //
            meta.add(metaSubsetR, RDF.TYPE,                      vf.createURI(eParams.getURIOfDatasetType()));
            meta.add(metaSubsetR, RDF.TYPE,                      Conversion.DATASET);
            meta.add(metaSubsetR, RDF.TYPE,                      Conversion.MetaDataset);
            meta.add(metaSubsetR, RDF.TYPE,                      VoID.DATASET);
            meta.add(metaSubsetR, Conversion.base_uri,           vf.createLiteral(eParams.getBaseURI()));
            meta.add(metaSubsetR, Conversion.source_identifier,  vf.createLiteral(eParams.getTokenDatasetSourceIdentifier()));
            meta.add(metaSubsetR, Conversion.dataset_identifier, vf.createLiteral(eParams.getTokenDatasetIdentifier()));
            // NOTE: meta subset should NOT have a version; it is across versions.
            meta.add(metaSubsetR, DCTerms.modified,              modifiedV);
            meta.add(metaSubsetR, FOAF.isPrimaryTopicOf,         vf.createURI(eParams.getURIOfDatasetMetaDataPage()));
            /*meta.add(metaSubsetR, VoID.vocabulary,               vf.createURI(VoID.BASE_URI));
            meta.add(metaSubsetR, VoID.vocabulary,               vf.createURI(PML.P_NAMESPACE));
            meta.add(metaSubsetR, VoID.vocabulary,               vf.createURI(PML.J_NAMESPACE));
            meta.add(metaSubsetR, VoID.vocabulary,               FOAF.Namespace);
            meta.add(metaSubsetR, VoID.vocabulary,               DCTerms.Namespace);*/
            assertVoIDdataDumps(metaSubsetR, eParams.getURIOfDumpFileSubsetMeta(), meta, dataDumps);
            this.usedPredicateInMetaData(VoID.vocabulary);
            this.usedClassInMetaData(Conversion.MetaDataset);
            
            meta.commit();
            
            // Add all triples in metaConn to the actual dataset.
            /*if( ! (this.onlyConvertExampleResources || eParams.getSourceUsageURL() != null) ) {
               Resource nullR = (Resource) null;
               for( Statement triple : meta.getStatements(null, null, nullR, false, nullR).asList() ) {
                  ancillary.add(triple); // This is adding bnodes; the graph digest needs to avoid bnodes.
               }*/

              primary.commit();
            ancillary.commit();
            
            this.numberTriples += primary.size() + ancillary.size() + VoID.NUM_TRIPLES_TO_ASSERT_NUM_TRIPLES + 1;
            System.err.println("Resulting size: " + this.numberTriples + " triples");
   
            if( includeMetadata() ) { // PROVENANCE
               // avoiding bnodes for graph hash:
               //VoID.addStatItemNumTriples(ancillary, thisDatasetLevelR, this.numberTriples);
               //ancillary.add(thisDatasetLevelR, Conversion.num_triples, vf.createLiteral(""+this.numberTriples,XMLSchema.INTEGER));
      
               // DEP: VoID.addStatItemNumTriples(meta,      thisDatasetLevelR, this.numberTriples);
               meta.add(     thisDatasetLevelR, Conversion.num_triples, vf.createLiteral(""+this.numberTriples,XMLSchema.INTEGER));
               meta.add(     thisDatasetLevelR, VoID.triples,           vf.createLiteral(""+this.numberTriples,XMLSchema.INTEGER));
               this.usedPredicateInMetaData(Conversion.num_triples, VoID.triples);
               

               DecimalFormat df = new DecimalFormat("#.#");
               double duration = (System.currentTimeMillis() - this.startTime) / (1000.0 * 60);
               double speed = numberTriples / duration;
               System.err.println("Generated "+numberTriples+" triples in " + df.format(duration) + " min. "+
                               "( " + df.format(speed) + " triples/min )");

               meta.add(thisDatasetLevelR,   PROVO.wasGeneratedBy, translationActivity);
               meta.add(translationActivity, RDF.TYPE, PROVO.Activity);
               meta.add(translationActivity, RDF.TYPE, PML3.TranslationActivity);
               meta.add(translationActivity, Conversion.triples_per_minute, vf.createLiteral(speed));
               CSVtoRDF.assertVocabularyUse(translationActivity, PROVO.used, this.predicatesUsedInData, meta);
               CSVtoRDF.assertVocabularyUse(translationActivity, PROVO.used, this.classesUsedInData,    meta);
            }

            // conversion:uses_predicate
            //
            //
            CSVtoRDF.assertVocabularyUse(metaSubsetR, Conversion.uses_predicate, this.predicatesUsedInMetadata, meta);
            CSVtoRDF.assertVocabularyUse(metaSubsetR, Conversion.uses_class,     this.classesUsedInMetadata,    meta);

            
            //ancillary.commit();
            meta.commit();
            meta.close();
         } catch (RepositoryException e) {
            e.printStackTrace();
         } finally {
            if( meta != null ) {
               try {
                  meta.close();
               } catch (RepositoryException e) {
                  e.printStackTrace();
               }
            }
         }      
      } else {
         // BAIL with error message in RDF; source_identifier, dataset_identifier, and version_identifier MUST be set.
         try {
            metaRepos.initialize();
            meta = metaRepos.getConnection();
            Resource bnode = vf.createBNode();
            meta.add(bnode, RDFS.COMMENT, 
                            vf.createLiteral("Error: source_identifier, dataset_identifier, and version_identifier " +
                                             "must be specified for all conversions."));
            meta.add(bnode, RDFS.SEEALSO, vf.createURI("https://github.com/timrdf/csv2rdf4lod-automation/wiki/Relaxing-3-layer-%28source,-dataset,-version%29-naming"));
            meta.add(bnode, RDFS.SEEALSO, vf.createURI("https://github.com/timrdf/csv2rdf4lod-automation/wiki/Conversion-process-phase:-name"));
            meta.setNamespace("rdfs", org.openrdf.model.vocabulary.RDFS.NAMESPACE);
            meta.commit();
            meta.close();
         } catch (RepositoryException e) {
            e.printStackTrace();
         }
      } // end if(this.eParams.hasIdentifiersSpecified())

      
      try { 
         digest.update(primaryRepos,   false, false); // don't include inferred statements; don't hash against quads instead of triples.
         digest.update(ancillaryRepos, false, false); // don't include inferred statements; don't hash against quads instead of triples.
         
         meta = metaRepos.getConnection();
         meta.add(this.thisDatasetLevelR, RDFS.COMMENT, vf.createLiteral("hash: " + digest.digest().toString(16)));
         this.usedPredicateInMetaData(RDFS.COMMENT);
         
         // NOTE: earlier parts of the RDF model may have been serialized and flushed earlier in processing, 
         // which would have been done in #flushIfBig() during #visit(record)
         if( outputFileName == null || outputFileName.length() == 0 || "-".equals(outputFileName) ) {
            //
            // Serialize to stdout.
            //
            if(   primary.size() > 0 )    primary.export(Constants.forFileExtension(bigextension));
            if( ancillary.size() > 0 )  ancillary.export(Constants.forFileExtension(bigextension));
            
            if( this.eParams.includeLODLinksGraph(1) ) {
               //System.err.println("\n# LODLink Graph pass through:\n");
               for( Repository lodlinkRep : this.eParams.getLODLinksRepositories() ) {
                  logger.fine("re size: " + Cat.size(lodlinkRep));
                  RepositoryConnection conn = null;
                  try {
                     conn = lodlinkRep.getConnection();
                     conn.export(Constants.forFileExtension(bigextension));
                  } catch(Exception e) {
                     e.printStackTrace();
                  }
               }
            }
            
            if( includeMetadata() ) {
               // TODO: ? write metadata to disk if metaOutputFileName != null even if outputFileName == null?
               System.out.println("\n# CSV2RDF4LOD DATASET METADATA (reproduced from above):\n");
               if( ontology.size() > 0 ) ontology.export(Constants.forFileExtension(bigextension)); // This is being phased in; assertions are currently in primaryRepos.
               if(     meta.size() > 0 )     meta.export(Constants.forFileExtension(bigextension));
            }
         } else { // - - - - - - - - - - - - - - - - - SERIALIZATION MUST HAPPEN ABOVE and BELOW - - - - - - - - - - -
            // Serialize to given file name.      
            try {
               
               FileOutputStream fos = new FileOutputStream(new File(outputFileName), this.appendToOutputFile); // Could be true or false, 
               this.appendToOutputFile = true;                                                                 // depending if we had to flush while visit()ing
   
               if(   primary.size() > 0 )   primary.export(Constants.handlerForFileExtension(bigextension, fos));
               if( ancillary.size() > 0 ) ancillary.export(Constants.handlerForFileExtension(bigextension, fos));
               if( this.eParams.includeLODLinksGraph(1) ) {
                  //System.err.println("\n# LODLink Graph pass through:\n");
                  for( Repository lodlinkRep : this.eParams.getLODLinksRepositories() ) {
                     logger.fine("re size: " + Cat.size(lodlinkRep)+" to " + outputFileName);
                     RepositoryConnection conn = null;
                     try {
                        conn = lodlinkRep.getConnection();
                        conn.export(Constants.handlerForFileExtension(bigextension, fos));
                     } catch(Exception e) {
                        e.printStackTrace();
                     }
                  }
               }
               
               if( includeMetadata() ) {
                  if( metaOutputFileName == null || metaOutputFileName.length() == 0 ) {
                     // Write metadata to SAME file as data
                     logger.info("writing metadata to same file as data "+outputFileName);
                     OutputStreamWriter fowriter = new OutputStreamWriter(fos);
                     fowriter.write("\n# CSV2RDF4LOD DATASET METADATA (reproduced from above):\n");
                     fowriter.close();
                     fos = new FileOutputStream(new File(outputFileName), this.appendToOutputFile);
                  } else {
                     // Write metadata to SEPARATE file from data
                     logger.info("writing metadata to separate file from data "+metaOutputFileName);
                     fos.close();
                     fos = new FileOutputStream(new File(metaOutputFileName), false); // overwrite file.
                  }
                  if( ontology.size() > 0 ) ontology.export(Constants.handlerForFileExtension(bigextension, fos));
                  if(     meta.size() > 0 )     meta.export(Constants.handlerForFileExtension(bigextension, fos));
               }
               
               fos.close();
            } catch (IOException e) {
               e.printStackTrace();
            } catch (RDFHandlerException e) {
               e.printStackTrace();
            }
         }
           primary.close();
         ancillary.close();
              meta.close();
      } catch (RepositoryException e) {
         e.printStackTrace();
      } catch (RDFHandlerException e) {
         e.printStackTrace();
      }
      return metaRepos;
   }

   /**
    * @param dataset    - the dataset to annotate with vocabulary use.
    * @param annotation - e.g. conversion:uses_predicate or conversion:uses_class.
    * @param terms      - 
    * @param conn       - a connection to a Repository.
    */
   protected static void assertVocabularyUse(URI dataset, URI annotation, Set<URI> terms,
   									               RepositoryConnection conn) {
      for( URI term : terms ) {
      	try {
	         conn.add(dataset, annotation, term);
         } catch (RepositoryException e) {
	         e.printStackTrace();
         }
      }
      PrefixMappings pmap = new DefaultPrefixMappings();
      for( URI predicate : terms ) {
      	URI ns = vf.createURI(pmap.bestNamespaceFor(predicate.stringValue()));
      	try {
	         conn.add(dataset, VoID.vocabulary, ns);
         } catch (RepositoryException e) {
	         e.printStackTrace();
         }
      }
      for( URI predicate : terms ) {
      	URI ns = vf.createURI(pmap.bestNamespaceFor(predicate.stringValue()));
      	try {
	         conn.add(predicate, RDFS.ISDEFINEDBY, ns);
	         // From cr-create-dataset-dirs-from-ckan.py:
	         // re.sub('(http://[^/]*)/.*$','\\1',ckanAPI)
	         
            Pattern pattern = Pattern.compile("(http://purl.org/[^/]*)/.*$");
            Matcher matcher = pattern.matcher(predicate.stringValue());
            if( matcher.matches() ) {
            	// Hack for just purl.org, since it has many domains within it (.
            	conn.add(predicate, PROVO.wasAttributedTo, vf.createURI(matcher.group(1)));
            }else {
            	pattern = Pattern.compile("(http://[^/]*)/.*$"); // TODO: use NameFactory.uriDomain()
            	matcher = pattern.matcher(predicate.stringValue());
	            if( matcher.matches() ) {
	            	conn.add(predicate, PROVO.wasAttributedTo, vf.createURI(matcher.group(1)));
	            }
            }
	         
         } catch (RepositoryException e) {
	         e.printStackTrace();
         }
      }
      
   }

	/**
    * 
    * @param datasetR - the void:Dataset that needs a void:dataDump
    * @param dumpURI - the value of void:dataDump (before adding an extension).
    * @param meta - the repository to add the triples.
    * @param dataDumps - add the dump URL to this set. (OUTPUT)
    */
   private void assertVoIDdataDumps(URI datasetR, String dumpURI, RepositoryConnection meta, Set<URI> dataDumps) {
      try {
         Set<String> compressions = new HashSet<String>();
         compressions.add("");
         //compressions.add(".tgz");
         //compressions.add(".gz"); // TODO: format is not turtle if it's compresssed!
         //compressions.add(".zip");
         //compressions.add(".bz2");
         for( String extension : this.voidFileExtensions ) {
            URI dataDump = vf.createURI(dumpURI+extension);
            meta.add(datasetR, VoID.dataDump, dataDump);
            
            boolean matched = false;
            for( String compression : compressions ) {
               if( (".ttl"+compression).equals(extension)) {
                  meta.add(dataDump, DCTerms.format, W3CFormats.Turtle);    matched = true;
               }else if( (".rdf"+compression).equals(extension) ) {
                  meta.add(dataDump, DCTerms.format, W3CFormats.RDF_XML);   matched = true;
               }else if( (".nt"+compression).equals(extension) ) {
                  meta.add(dataDump, DCTerms.format, W3CFormats.N_TRIPLES); matched = true; 
               }
            }
            if( !matched ) {
               System.err.println("TODO: add dcterms format for extension "+extension);
            }
            dataDumps.add(dataDump);
         }
      } catch (RepositoryException e) {
         e.printStackTrace();
      }
   }

   /**
    * Serialize current contents of repositories to stdout. Done to save memory.
    * 
    * Called by {@link #visit(CSVRecord, long)} after it processes a row.
    * {@link #toRDF(String, String)} also writes the Repository to stdout/disk at the very end. 
    */
   private void flushIfBig() {
      
      //
      // NOTE: The last dump occurs in toRDF()
      //
      
      try {
         if( this.BIG_MODE ) {   
            primary.commit();
            ancillary.commit();
            
            System.err.println("Flushing "+primary.size()+" + "+ancillary.size()+" triples as "+bigextension+".");
            
            if( this.outputFileName == null || this.outputFileName.length() == 0 || "-".equals(this.outputFileName) ) {
               if(   primary.size() > 0 )   primary.export(Constants.forFileExtension(bigextension));
               if( ancillary.size() > 0 ) ancillary.export(Constants.forFileExtension(bigextension));
            } else {
               FileOutputStream fos = null;
               try {
                  fos = new FileOutputStream(new File(this.outputFileName), this.appendToOutputFile);
                  this.appendToOutputFile = true;
                  if(   primary.size() > 0 )   primary.export(Constants.handlerForFileExtension(bigextension,fos));
                  if( ancillary.size() > 0 ) ancillary.export(Constants.handlerForFileExtension(bigextension,fos));
               } catch (FileNotFoundException e) {
                  e.printStackTrace();
               } finally {
                  if( fos != null ) {
                     try {
                        fos.close();
                     } catch (IOException e) {
                        e.printStackTrace();
                     }
                  }
               }
            }
            numberTriples += primary.size() + ancillary.size();
            
            digest.update(primaryRepos,   false, false); // don't include inferred statements and don't hash against quads instead of triples.
            primary.clear();
            primary.commit();
            
            digest.update(ancillaryRepos, false, false); // don't include inferred statements and don't hash against quads instead of triples.
            ancillary.clear();
            ancillary.commit();
            
            flushCount = 0;
         } else {
            System.err.println("("+primary.size()+" + "+ancillary.size()+" triples in memory.)");
         }
      } catch (RepositoryException e) {
         e.printStackTrace();
      } catch (RDFHandlerException e) {
         e.printStackTrace();
      }
   }

   /**
    * 
    * @param metaConn  - the RepositoryConnection to assert the provenance.
    * @param dataDumps - the URIs of data dumps created by this conversion (and need to be justified by its invocation).
    */
   private void assertConversionProvenance(RepositoryConnection metaConn, Set<URI> dataDumps) { // PROVENANCE

      boolean USE_PML_API = false;

      try {             
         Resource infStepR = NameFactory.getResource(provenanceBaseURI, "infstep", NameType.INCREMENTING);
         for( URI dataDump : dataDumps ) {
            Resource nodeSetR = NameFactory.getResource(provenanceBaseURI, "nodeset", NameType.INCREMENTING);
            metaConn.add(nodeSetR, RDF.TYPE,                PML2.NODE_SET);
            metaConn.add(nodeSetR, PML2.HAS_CONCLUSION,      dataDump);
            metaConn.add(nodeSetR, PML2.IS_CONSEQUENT_OF,    infStepR);
            this.usedPredicateInMetaData(RDF.TYPE, PML2.HAS_CONCLUSION, PML2.IS_CONSEQUENT_OF);
         }

         Resource antecedentsListR = vf.createBNode();

         URI csvR    = vf.createURI(eParams.getFilespaceOfVersionedProvenance()+inFileName);
         URI paramsR = vf.createURI(eParams.getFilespaceOfVersionedProvenance()+enhancementParametersURL);
         Map<Resource,Resource> antecedentRoles = new HashMap<Resource,Resource>();
         antecedentRoles.put(csvR,    PMLRoles.INPUT);
         antecedentRoles.put(paramsR, PMLRoles.PARAMETERS);
         Map<Resource,Resource> antecedentRoleNodes = new HashMap<Resource,Resource>();
         antecedentRoleNodes.put(csvR,    vf.createBNode());
         antecedentRoleNodes.put(paramsR, vf.createBNode());

         metaConn.add(infStepR, RDF.TYPE,                 PML2.INFERENCE_STEP);
         metaConn.add(infStepR, PML2.HAS_INFERENCE_ENGINE, csv2rdf4lodInstanceR);
         metaConn.add(infStepR, PML2.HAS_ANTECEDENT_LIST,  antecedentsListR);
         this.usedPredicateInMetaData(RDF.TYPE, PML2.HAS_INFERENCE_ENGINE, PML2.HAS_ANTECEDENT_LIST);
         for( Resource antecedentRoleNode : antecedentRoleNodes.values() ) {
            metaConn.add(infStepR, PMLRoles.HAS_ANTECEDENT_ROLE, antecedentRoleNode);
            this.usedPredicateInMetaData(PMLRoles.HAS_ANTECEDENT_ROLE);
         }

         metaConn.add(vf.createURI("http://purl.org/twc/id/software/csv2rdf4lod"),                    SWAP_PIM_CON.preferredURI, csv2rdf4lodInstanceR);
         metaConn.add(vf.createURI("http://data.lod2.eu/2011/tools/CSV2RDF4LOD"),                     SWAP_PIM_CON.preferredURI, csv2rdf4lodInstanceR);
         metaConn.add(vf.createURI("http://data-gov.tw.rpi.edu/wiki/Special:URIResolver/csv2rdf4lod"),SWAP_PIM_CON.preferredURI, csv2rdf4lodInstanceR);
         //metaConn.add(csv2rdf4lodInstanceR, RDF.TYPE,           csv2rdf4lodClassR);
         metaConn.add(csv2rdf4lodInstanceR, RDF.TYPE,           PML2.INFERENCE_ENGINE);
         metaConn.add(csv2rdf4lodInstanceR, RDF.TYPE,           DOAP.Version);
         metaConn.add(csv2rdf4lodInstanceR, DCTerms.identifier, vf.createLiteral(converterIdentifier));  // hash of jar.
         metaConn.add(csv2rdf4lodInstanceR, DCTerms.version,    vf.createLiteral(VERSION));              // of this converter.
         metaConn.add(csv2rdf4lodInstanceR, DOAP.revision,      vf.createLiteral(VERSION));
         metaConn.add(csv2rdf4lodInstanceR, DOAP.platform,      vf.createLiteral("Java"));
         metaConn.add(csv2rdf4lodInstanceR, PROVO.specializationOf, csv2rdf4lodProjectR);
         this.usedPredicateInMetaData(SWAP_PIM_CON.preferredURI, RDF.TYPE, DCTerms.identifier, DCTerms.version, DOAP.revision, DOAP.platform);

         // TODO: fit this new URI in.
         metaConn.add(vf.createURI("http://purl.org/twc/id/software/csv2rdf4lod"), RDFS.SEEALSO, vf.createURI("https://github.com/timrdf/csv2rdf4lod-automation/wiki"));
         metaConn.add(csv2rdf4lodProjectR, RDF.TYPE,         DOAP.Project);

         metaConn.add(csv2rdf4lodProjectR, DOAP.implementsP, vf.createURI("http://data-gov.tw.rpi.edu/wiki/Special:URIResolver/URI_design_for_RDF_conversion_of_CSV-based_data"));
         metaConn.add(csv2rdf4lodProjectR, DOAP.implementsP, vf.createURI("http://purl.org/twc/vocab/conversion/"));
         metaConn.add(csv2rdf4lodProjectR, DCTerms.author,   vf.createURI("http://tw.rpi.edu/instances/TimLebo"));
         metaConn.add(csv2rdf4lodProjectR, DOAP.developer,   vf.createURI("http://tw.rpi.edu/instances/TimLebo"));
         metaConn.add(csv2rdf4lodProjectR, DOAP.developer,   vf.createURI("http://tw.rpi.edu/instances/GregoryToddWilliams"));
         metaConn.add(csv2rdf4lodProjectR, DOAP.developer,   vf.createURI("http://kasei.us/about/foaf.xrdf#greg"));
         metaConn.add(csv2rdf4lodProjectR, DOAP.developer,   vf.createURI("http://tw.rpi.edu/instances/JamesMcCusker"));
         metaConn.add(csv2rdf4lodProjectR, DOAP.developer,   vf.createURI("http://tw.rpi.edu/instances/ZhenningShangguan"));
         metaConn.add(csv2rdf4lodProjectR, DOAP.maintainer,  vf.createURI("http://tw.rpi.edu/instances/TimLebo"));
         metaConn.add(csv2rdf4lodProjectR, DOAP.documenter,  vf.createURI("http://tw.rpi.edu/instances/TimLebo"));
         metaConn.add(csv2rdf4lodProjectR, DOAP.documenter,  vf.createURI("http://tw.rpi.edu/instances/JohannaFlores"));
         metaConn.add(csv2rdf4lodProjectR, DOAP.documenter,  vf.createURI("http://tw.rpi.edu/instances/LiDing"));
         metaConn.add(csv2rdf4lodProjectR, DOAP.documenter,  vf.createURI("http://tw.rpi.edu/instances/GinoGervasio"));
         metaConn.add(csv2rdf4lodProjectR, DOAP.helper,      vf.createURI("http://tw.rpi.edu/instances/AlvaroGraves"));
         metaConn.add(csv2rdf4lodProjectR, DOAP.helper,      vf.createURI("http://tw.rpi.edu/instances/DominicDiFranzo"));
         metaConn.add(csv2rdf4lodProjectR, DOAP.helper,      vf.createURI("http://tw.rpi.edu/instances/GinoGervasio"));
         metaConn.add(csv2rdf4lodProjectR, DOAP.helper,      vf.createURI("http://tw.rpi.edu/instances/PingWang"));
         metaConn.add(csv2rdf4lodProjectR, DOAP.helper,      vf.createURI("http://tw.rpi.edu/instances/XianLi"));
         metaConn.add(csv2rdf4lodProjectR, DOAP.helper,      vf.createURI("http://tw.rpi.edu/instances/JamesMcCusker"));
         metaConn.add(csv2rdf4lodProjectR, DOAP.helper,      vf.createURI("http://www.w3.org/People/Eric/ericP-foaf#ericP"));     // perl blow away ,""
         metaConn.add(csv2rdf4lodProjectR, DOAP.helper,      vf.createURI("http://tw.rpi.edu/instances/AnkitSrivastava"));        //bash help . justify.sh
         metaConn.add(csv2rdf4lodProjectR, DOAP.helper,      vf.createURI("http://purl.org/twc/id/person/Maryam_Fazel-Zarandi")); // used cellify.awk!
         metaConn.add(csv2rdf4lodProjectR, DOAP.helper,      vf.createURI("http://tw.rpi.edu/instances/AlexanderLahuerta"));      // connecting to Virtuoso on aquarius.
         metaConn.add(csv2rdf4lodProjectR, DOAP.homepage,    vf.createURI("https://github.com/timrdf/csv2rdf4lod-automation/wiki"));
         metaConn.add(csv2rdf4lodProjectR, DOAP.audience,    vf.createLiteral("Data modelers and architects that need (potentially non-RDB) tabular data encoded within the Resource Description Framework."));
         metaConn.add(csv2rdf4lodProjectR, DOAP.shortdesc,   vf.createLiteral("Converts CSV files to RDF using parameters specified by http://purl.org/twc/vocab/conversion/"));
         metaConn.add(csv2rdf4lodProjectR, DOAP.description, vf.createLiteral("Converts CSV files to RDF using parameters specified by http://purl.org/twc/vocab/conversion/. Organizes datasets using 'source', 'dataset', and 'version' identifiers to allow for third-party aggregation, refinement, and integration of third-party data. Uses predicate 'layers' to allow backward-compatible refinements of Resources' descriptions. Minimizes human error and increases transparancy of conversion by using RDF-encoded interpretation parameters to control all processing."));
         this.usedPredicateInMetaData(RDFS.SEEALSO, RDF.TYPE, DOAP.revision, DOAP.implementsP, 
         		DCTerms.author, DOAP.developer, DOAP.maintainer, DOAP.documenter, DOAP.helper, 
         		DOAP.homepage, DOAP.homepage, DOAP.audience, DOAP.shortdesc, DOAP.description);
         
         Resource doapRepR = vf.createBNode();
         metaConn.add(doapRepR, RDF.TYPE,              DOAP.GitRepository);
         metaConn.add(doapRepR, FOAF.isPrimaryTopicOf, vf.createURI("https://github.com/timrdf/csv2rdf4lod-automation"));
         metaConn.add(doapRepR, DOAP.browse,           vf.createURI("https://github.com/timrdf/csv2rdf4lod-automation"));
         this.usedPredicateInMetaData(RDF.TYPE, FOAF.isPrimaryTopicOf, DOAP.browse);

         metaConn.add(vf.createURI("http://tw.rpi.edu/instances/GregoryToddWilliams"),     SWAP_PIM_CON.preferredURI, vf.createURI("http://kasei.us/about/foaf.xrdf#greg"));
         //metaConn.add(vf.createURI("http://tw.rpi.edu/instances/AlvaroGraves"),          SWAP_PIM_CON.preferredURI, vf.createURI("http://graves.cl/foaf.rdf#me"));
         metaConn.add(vf.createURI("http://tw.rpi.edu/instances/AlvaroGraves"),            SWAP_PIM_CON.preferredURI, vf.createURI("http://alvaro.graves.cl"));
         metaConn.add(vf.createURI("http://purl.org/twc/id/person/TimLebo"),               SWAP_PIM_CON.preferredURI, vf.createURI("http://tw.rpi.edu/instances/TimLebo"));
         metaConn.add(vf.createURI("http://tw.rpi.edu/wiki/Special:URIResolver/Tim_Lebo"), SWAP_PIM_CON.preferredURI, vf.createURI("http://tw.rpi.edu/instances/TimLebo"));
         metaConn.add(vf.createURI("http://www.cs.rpi.edu/~weavej3/r/Tim_Lebo"),           SWAP_PIM_CON.preferredURI, vf.createURI("http://tw.rpi.edu/instances/TimLebo"));
         metaConn.add(vf.createURI("http://purl.org/twc/id/person/Maryam_Fazel-Zarandi"),  SWAP_PIM_CON.preferredURI, vf.createURI("http://tw.rpi.edu/instances/MaryamFazel-Zarandi"));
         this.usedPredicateInMetaData(SWAP_PIM_CON.preferredURI);
         
         Map<Resource,Resource> antNSs = PML2.addAntecedentList(metaConn, provenanceBaseURI, 
         																			antecedentsListR, csvR, paramsR);
               /*PROVENANCE*/   						   
         //System.err.println("input ns:  " + antNSs.get(csvR));
         //System.err.println("params ns: " + antNSs.get(paramsR));
         for( Resource antecedent : antecedentRoleNodes.keySet() ) { // PROVENANCE
            metaConn.add(antecedentRoleNodes.get(antecedent), RDF.TYPE,                PMLRoles.ANTECEDENT_ROLE);
            metaConn.add(antecedentRoleNodes.get(antecedent), PMLRoles.HAS_ANTECEDENT, antecedent);
            metaConn.add(antecedentRoleNodes.get(antecedent), PMLRoles.HAS_ROLE,       antecedentRoles.get(antecedent));
            metaConn.commit();
            this.usedPredicateInMetaData(RDF.TYPE, PMLRoles.HAS_ANTECEDENT, PMLRoles.HAS_ROLE);
         }

         metaConn.commit();
         // TODO: when a full URL, enhancement ref will break.
         // TODO: add provenance param (row/col frags)

         metaConn.add(csvR,    RDF.TYPE,       PML2.INFORMATION);
         metaConn.add(csvR,    PML2.HAS_FORMAT, PMLFormat.csv);

         metaConn.add(paramsR, RDF.TYPE,       PML2.INFORMATION);
         metaConn.add(paramsR, PML2.HAS_FORMAT, PMLFormat.rdfAbstract);
         this.usedPredicateInMetaData(RDF.TYPE, PML2.HAS_FORMAT);

      } catch (RepositoryException e) {
         e.printStackTrace();
      }

      //
      //
      //

      if( USE_PML_API ) {

         IWInferenceStep infStepIWR = (IWInferenceStep) PMLObjectManager.createPMLObject(PMLJ.InferenceStep_lname);
         infStepIWR.setIdentifier(PMLObjectManager.getObjectID(NameFactory.getResource(provenanceBaseURI, "infstep", NameType.INCREMENTING).stringValue()));
         
         for( URI dataDump : dataDumps ) {
            Resource nodeSetR = NameFactory.getResource(provenanceBaseURI, "nodeset", NameType.INCREMENTING);
            //metaConn.add(nodeSetR, RDF.TYPE,                PML.NODE_SET);
            //metaConn.add(nodeSetR, PML.HAS_CONCLUSION,      dataDump);
            //metaConn.add(nodeSetR, PML.IS_CONSEQUENT_OF,    infStepIWR);
            
            IWNodeSet nodeSetIWR = (IWNodeSet) PMLObjectManager.createPMLObject(PMLJ.NodeSet_lname);
            nodeSetIWR.setIdentifier(PMLObjectManager.getObjectID(NameFactory.getResource(provenanceBaseURI, "nodeset", NameType.INCREMENTING).stringValue()));
            
            IWInformation infIWR = (IWInformation) PMLObjectManager.createPMLObject(PMLP.Information_lname);
            //infIWR.setIdentifier(IWInformation)
            //PMLObjectManager.getObjectID(dataDump.stringValue())
            //nodeSetIWR.setHasConclusion();
         }

         IWInferenceEngine engine = (IWInferenceEngine) PMLObjectManager.createPMLObject(PMLP.InferenceEngine_lname);

         engine.setHasName("MyLocalInferenceEngine"); // pmlp:hasName
         engine.setIdentifier(PMLObjectManager.getObjectID("http://data.lod2.eu/2011/tools/CSV2RDF4LOD"));
         
         System.err.println("READY");
         IWInferenceEngine inf = (IWInferenceEngine)PMLObjectManager.getPMLObjectFromFile("http://purl.org/twc/vocab/conversion/curl_md5_5670dffdc5533a4c57243fc97b19a654","file:/Users/lebot/Desktop/primer.html.pml.ttl.rdf2");
         System.err.println("YOYOYO: " + inf);
         inf.getProperty(DCTerms.description.stringValue());
      }
   }

   /**
    * 
    * @return
    */
   private boolean objectsLinkedWithSameAs() {
      boolean sameasAsserted = false;
      // Find out if any handlers linked with a sameas.
      for( ValueHandler handler : this.valueHandlers.values() ) {
         if( handler instanceof ResourceValueHandler &&
            ((ResourceValueHandler) handler).numSameAsAssertions() > 0 ) {
            sameasAsserted = true;
            break;
         }
      }
      return sameasAsserted;
   }

   @Override
   public void visitTopMatter(String topMatter) {
      if( topMatter != null && topMatter.length() > 0 ) {
         //System.err.println("===========");
         //System.err.println("consumed top matter:\n"+topMatter);
         try {
            ancillary.add(vf.createURI(this.eParams.getURIOfVersionedDataset()), OpenVocab.csvTopM, vf.createLiteral(topMatter));
         } catch (RepositoryException e) {
            e.printStackTrace();
         }
      }
   } 
   
   @Override
   public void visitBottomMatter(String bottomMatter) {
      // TODO: if bottomMatter is the entire file, do not output.
      if( bottomMatter != null && bottomMatter.length() > 0 ) {
         System.err.println("consumed bottom matter:\n"+bottomMatter);
         System.err.println("===========");
         try {
            ancillary.add(vf.createURI(this.eParams.getURIOfVersionedDataset()),OpenVocab.csvBottomM,vf.createLiteral(bottomMatter));
         } catch (RepositoryException e) {
            e.printStackTrace();
         }
      }
   }
   

   /**
    * 
    * @param record - 
    * @param rowNum - 
    */
   @Override
   public void visit(CSVRecord record, long rowNum) {
      
      //
      // Decide to bail on row if sample limit reached or only examples requested.
      //
      //
      //
      // Example resources are asserted explicitly in the enhancement parameters.
      // If not an example resource, don't process. (unless we need more samples)
      if( this.onlyConvertExampleResources && !eParams.isExampleResourceRow(rowNum) &&
        !(this.sampleLimit >= 0 && this.samplesMade < this.sampleLimit)              )                           return;

      // If the desired number of samples has been reached, don't process. (unless this is an example resource)
      if( this.sampleLimit >= 0 && this.samplesMade >= this.sampleLimit
                                           && !eParams.isExampleResourceRow(rowNum) )                            return;

      /*System.err.println("only: " +onlyConvertExampleResources+" eg: "+eParams.isExampleResourceRow(rowNum)+" "+
            samplesMade + " > " + sampleLimit);*/
      
      
      if( this.previousRec == null ) {
         System.err.println("\n");
      }
      // DEBUG
      logger.finer("\n\n\n\n\n\n------------------------------------------------------------------------------------\n"+
                   "visiting row "+rowNum + " -----------------------------------------------------------------------");
      // DEBUG
      
      
      //
      // Handle flushing.
      //
      
      /*logger.finest("BIG_MODE: " + this.BIG_MODE + 
                    " b/c "+rowNum*columnNameFactory.getProperties().size()+" < flush threshold: "+this.FLUSH_THRESHOLD);*/
      try {
         if( this.flushCount >= FLUSH_INTERVAL ) {
            // Check to flush every 50,000 cells.
            this.BIG_MODE = this.BIG_MODE || // TODO: does not accurately estimate cell-based triple count.
            		         rowNum * columnNameFactory.getProperties().size() > this.FLUSH_THRESHOLD;

            this.BIG_MODE = this.BIG_MODE || this.primary.size() > this.FLUSH_THRESHOLD;

            long duration = System.currentTimeMillis() - this.startTime;
            System.err.print("Processed "+rowNum+" rows in " + (duration / (1000 * 60))+ " min. ");
            flushIfBig();
         }
      } catch (RepositoryException e2) {
         e2.printStackTrace();
      }
      
      
      
      
      // Skip this row if ANY of the only-if-columns are empty.
      boolean notEnoughValues = false;
      for( int onlyIfCol : eParams.getOnlyIfColumns() ) {
         String onlyIfColValue = record.getQuotelessCommadValue(onlyIfCol-1);
         //logger.finest("checking onlyIfCol "+onlyIfCol+" \""+ onlyIfColValue +"\" " + " : "+eParams.getInterpetAsNullStrings(onlyIfCol).contains(onlyIfColValue));
         if( onlyIfColValue == null              || 
             onlyIfColValue.trim().length() == 0 || 
             eParams.getInterpetAsNullStrings(onlyIfCol).contains(onlyIfColValue) ) {
            
            notEnoughValues = true;
            if( numSkipped < REPORT_LIMIT ) {
               System.err.println(rowNum+": skipping row b/c only-if-col "+onlyIfCol+" "+
                               "("+columnNameFactory.getUniqueColumnLabel(onlyIfCol)+") value is empty.");
               // TODO: not recognizing overriding conversion:label
            }
            numSkipped++;
         }
      }
      if( notEnoughValues )                                                                                      return;
      
      // Provide values from previous row if:
      // 1) this row's value is empty and the column is repeatable.
      // 2) this row's value indicates that we should.
      for (int c = 1; c <= columnNameFactory.getProperties().size(); c++ ) { 
         String value = record.getQuotelessCommadValue(c-1);
         if( ((value == null || value.length() < 1) && eParams.getRepeatPreviousIfEmptyColumns().contains(c)) ||
              (value != null                        && eParams.getRepeatPreviousSymbols().contains(value))
             && previousRec != null ) {
            
            record.addItem(c-1, previousRec.getValue(c-1));
            if( numRepeated < REPORT_LIMIT ) {
               System.err.println(rowNum+": repeating value "+previousRec.getValue(c-1)+" for column "+c);
            }
            numRepeated++;
         }
      }
      
      
      
      
      samplesMade++;
      List<Resource>            subjects   = new ArrayList<Resource>();
      HashMap<Resource,Integer> subjectCol = new HashMap<Resource,Integer>(); // Only populated when cell-based.
      
      //
      // Name the subjects in this row. 
      //
      // If  row-based subjects, then only one.
      // If cell-based subjects, then one for each cell-based subject.
      //
      Resource rawSubjectR = vf.createURI(this.subjectNS + "thing_" + rowNum); // Original name for rows or cells, 
                                                                               // before applying subject template.
      this.csvRecordFiller.setCSVRecord(record, rowNum);
      
      if( eParams.isCellBased() ) {
         logger.finest("Naming cells as subjects in columns "+this.cellBasedColumnsSorted);
         for( Integer cellBasedColumnIndex : this.cellBasedColumnsSorted ) {  
            
            String localName = ""+rowNum+"_"+cellBasedColumnIndex; // TODO: apply domain template to cell names.
            

            
            Resource rawSubjectRc = vf.createURI(this.subjectNS + "thing_" +                 localName);
            Resource subjectR     = vf.createURI(this.subjectNS + this.instanceLocalPrefix + localName);
            if( eParams.getDomainTemplate() != null ) {
            	// Global:
               String templated = eParams.fillTemplate(eParams.getDomainTemplate());
               // Row-based context:
               templated = csvRecordFiller.fillTemplate(templated);
               // Column-based context:
               templated = TemplateFillerColumnContext.fillTemplateWithColumnContext(templated, 
               						cellBasedColumnIndex, 
               	   				this.originalColumnHeaders.get(cellBasedColumnIndex), 
               	   				eParams.getColumnLabel(cellBasedColumnIndex), 
                   					this.columnNameFactory.getPropertyLocalName(cellBasedColumnIndex));
               subjectR = vf.createURI(templated);
            }
            
            String  value         = record.getValue(cellBasedColumnIndex-1); // record is 0 based.
            
            boolean valueBecomesNull = this.valueHandlers.containsKey(cellBasedColumnIndex) && 
                                       this.valueHandlers.get(cellBasedColumnIndex).interpretsAsNull(value);
            
            if( !valueBecomesNull && 
               (!this.onlyConvertExampleResources ||
                 this.onlyConvertExampleResources && cellBasedColumnIndex == eParams.getFirstCellBasedColumn()) ) {

               logger.finest(subjectR.stringValue() + " @ " + cellBasedColumnIndex + " " + value);
               
               subjects.add(subjectR);                         // Include this cell as a subject.
               subjectCol.put(subjectR, cellBasedColumnIndex); // Remember from which column the subject came.
               assertSameAs(subjectR, rawSubjectRc);           // Associate templated name to default name.
            }else {
               logger.finest(subjectR.stringValue() + " @ " + cellBasedColumnIndex + " OMITTED b/c " + value + " becomes null");
            }
         }
      }else if( eParams.getDomainTemplate() != null ) {
         logger.finer("Naming subject by domain template.");
         String template = eParams.getDomainTemplate();
         logger.finer("Naming subject by domain template: "+template);
         if( eParams.getDomainTemplateColumn() != null ) {
            this.csvRecordFiller.setColumnIndex(eParams.getDomainTemplateColumn()); 
            // TODO: if ov:csvCol not set and not referenced, should still work -- but getting NULL POINTER. 
            // Dominic issue https://github.com/timrdf/csv2rdf4lod-automation/issues/95
         }
         
         // Template could be something like:
         //     "[.]"        (which ends up a local name)
         //     "http://bio2rdf.org/geneid:[#1]"
         //     "[/]id/fiscal-year/country/[@country]/purpose/[@purpose]/year/[@year]/FY_[@year]"
         //     "http://dbpedia.org/resource/United_States_House_Administration_Subcommittee_on_Elections"
         //     "[#11]" (which evaluates to tag:cet.ncsa.uiuc.edu,2008:/bean/Collection/ece2a356-c7ef-42e8-a662-adae66574338)

         String templatedNameL = this.csvRecordFiller.fillTemplate(template, CSVRecordTemplateFiller.AS_LITERAL);
         
         String templatedNameR = this.csvRecordFiller.fillTemplate(template, //!ResourceValueHandler.isURI(template)         // This does NOT apply to "[.]" that expands to a URI.
                                                                             //!ResourceValueHandler.isURI(templatedNameL)); // This slickness failed Dom's "US Treasury"
                                                                             CSVRecordTemplateFiller.AS_RESOURCE);
         
         logger.finer("Naming subject by domain template (L) : "+ CSVRecordTemplateFiller.AS_LITERAL          +" "+ templatedNameL);
         logger.finer("Naming subject by domain template (R) : "+ CSVRecordTemplateFiller.AS_RESOURCE         +" "+ this.csvRecordFiller.fillTemplate(template,CSVRecordTemplateFiller.AS_RESOURCE));
         logger.finer("Naming subject by domain template (R?): "+ !ResourceValueHandler.isURI(templatedNameL) +" "+ templatedNameR);
         logger.finer("templatedR !startsLikeURI: " + !ResourceValueHandler.startsLikeURI(templatedNameR));
         
         Resource subjectR = null;
         if( ResourceValueHandler.isURI(templatedNameL) ) {
            subjectR = vf.createURI(templatedNameL);                 // "http://dbpedia.org/resource/United_States_House_Administration_Subcommittee_on_Elections"
         }else if( !ResourceValueHandler.startsLikeURI(templatedNameR) ) {
            subjectR = vf.createURI(this.subjectNS + templatedNameR); // The typical case is "[.]" -> "Treasury Department"; we need to prepend a base.
            logger.finer("  prepending default base: " + subjectR.stringValue());
            try {
               //String templatedNameL = this.csvRecordFiller.fillTemplate(template, CSVRecordTemplateFiller.AS_LITERAL);
               primary.add(subjectR, RDFS.LABEL,         vf.createLiteral(templatedNameL));
               primary.add(subjectR, DCTerms.identifier, vf.createLiteral(templatedNameL));
               primary.add(subjectR, CoIN.slug,          vf.createLiteral(templatedNameR));
            } catch (RepositoryException e) {
               e.printStackTrace();
            }
         }else {
            subjectR = vf.createURI(templatedNameR);                 // "http://bio2rdf.org/geneid:[#1]" or "[/]..." templates provide their own base.
         }

         // TODO: System.err.println(rowNum+": WARNING: primary key null or zero length. Using non-key row name.");
         subjects.add(subjectR);
         subjectCol.put(subjectR, 0);
         assertSameAs(subjectR, rawSubjectR);
      }else if( eParams.getURIKeyColumnIndex() > 0 ) { // TODO: is this replaced by domain_template? e.g. [#4] when 4 has the URI?
         System.err.println("Naming subject by URI in column "+eParams.getURIKeyColumnIndex());
         // Entire URI is being used.
         int uriCol = eParams.getURIKeyColumnIndex();
         String subjectURI = record.getQuotelessCommadValue(uriCol-1);
         URI subjectR = vf.createURI(subjectURI.replace("<","").replace(">",""));
         subjects.add(subjectR);
         assertSameAs(subjectR, rawSubjectR);
      }else {   
         // Local name is being placed within namespace
         // Name the row. Use rowNum if no primary key column provided. // TODO: standardize column index at 1, not 0
         String localName = ""+rowNum;//NOTE: was thingCount, but need to be consistent with row naming b/w raw and e1.
         if( eParams.getPrimaryKeyColumnIndex() > 0 ) { // TODO: is this replaced by domain_template?
            //System.err.println("Naming row using value from column "+eParams.getPrimaryKeyColumnIndex());
            int primaryKeyCol = eParams.getPrimaryKeyColumnIndex();
            String primaryKey = record.getQuotelessCommadValue(primaryKeyCol-1);
            //System.err.println(rowNum+" primary key col: "+primaryKeyCol+" value = " +primaryKey);
            if( primaryKey != null && primaryKey.length() > 0 ) {
               // Predicate-scoped naming when no subject type, typed naming when typed.
               localName = getResourcePromotionToken(primaryKeyCol) + NameFactory.trimChars(primaryKey.replaceAll("\\W", "_"),"_");
            }else {
               System.err.println(rowNum+": WARNING: primary key null or zero length. Using non-key row name.");
            }
         }
         URI subjectR = vf.createURI(this.subjectNS + this.instanceLocalPrefix + localName);
         //System.err.println("Naming row "+subjectR);
         subjects.add(subjectR);
         subjectCol.put(subjectR, 0);
         assertSameAs(subjectR, rawSubjectR);
         
         if( false ) {
            // Preparing to hack the URIs to insert directory level for easy LOD-MAT'ing.
            // Smarter .htaccess redirecting to php might be a better solution, but ...
            byte[] bytesOfMessage;
            try {
               // http://stackoverflow.com/questions/415953/generate-md5-hash-in-java
               bytesOfMessage = (this.instanceLocalPrefix + localName).getBytes("UTF-8");
               MessageDigest md = MessageDigest.getInstance("MD5");
               byte[] digest = md.digest(bytesOfMessage);
               BigInteger bigInt = new BigInteger(1,digest);
               String hashtext = bigInt.toString(16);
               System.err.println(hashtext+" -> "+hashtext.substring(0, 3));
               
               // TODO: can this be used to sha1sum, too? lebot rpi -> cdecab02f3fb1d3d9c79e1a8a8730bd11ef9f2d3
            } catch (UnsupportedEncodingException e) {
               e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
               e.printStackTrace();
            }
         }
      }
      logger.finer("");
      
      
      //
      // Describe subjects
      //
      // If row-based, only one subjectR; if cell-based, one subjectR for each cell-based column.
      //
      for( Resource subjectR : subjects ) {
    
         logger.finest("r " + rowNum + " subject " + subjectR + " of " + subjects.size());
         
         if( justProvenance ) {
            //System.err.println(rowNum+": just provenance: " +subjectR.stringValue());
   
            String prov = 
            "<"+subjectR.stringValue()+"/sourceUsage> \n"+
            "   a pmlp:Information; \n" +
            "   a our:InformationTemplate; \n" +
            "   rdf:subject <"+subjectR.stringValue()+">; \n" +
            "   pmlp:hasReferenceSourceUsage [  \n" +
            "      a pmlp:SourceUsage; \n" +
            "      pmlp:hasSource [  \n" +
            "         a pmlp:DocumentFragmentByRowCol; \n" +
            "         pmlp:hasDocument <"+sourceUsageURL+">; \n" +
            "         pmlp:hasFromRow "+rowNum+"; \n" +
            "         pmlp:hasToRow   "+rowNum+"; \n" +
            "      ];\n" +
            "      pmlp:hasUsageDateTime \""+sourceUsageDateTime+"\"^^xsd:dateTime; \n" +
            "   ];\n"+
            ".\n";
            System.out.println(prov);
            return;
         }
         
         
         try {
            // Link row/cell subject URI to dataset
            primary.add(subjectR, DCTerms.isReferencedBy, this.versionedDatasetR);
            primary.add(subjectR, VoID.inDataset,         this.versionedDatasetR);
            this.usedPredicateInData(DCTerms.isReferencedBy, VoID.inDataset);
            // Remember examples to dump at end for nicer serialization.
            if( eParams.isExampleResourceRow(rowNum) ) {
               this.exampleResources.add((URI)subjectR);
            }
            // Add types to the row/cell subject URI.
            for( URI subjectRowTypeR : this.subjectRowTypeRs ) {
               primary.add(subjectR, RDF.TYPE, subjectRowTypeR);
               this.usedPredicateInData(RDF.TYPE);
               this.usedClassInData(subjectRowTypeR);
            }
            // Link row/cell subject URI to human pages.
            for( String baseURI : eParams.getSubjectHumanRedirects() ) {
               ancillary.add(vf.createURI(baseURI+subjectR), FOAF.primaryTopic, subjectR); // DEPRECATE
            }
            // Add additional descriptions to the row/cell subject URI (fixed predicates - templated objects).
            for( URI predicate : eParams.getConstantAdditionalDescriptions(0).keySet() ) {
               for( Value object : eParams.getConstantAdditionalDescriptions(0).get(predicate) ) {
                  //Value object = eParams.getAdditionalDescriptions(0).get(predicate);
                  logger.finest("ANNOTATING: " + predicate + " " + object);                
                  primary.add(subjectR, predicate, csvRecordFiller.tryExpand(object));
                  this.usedPredicateInData(predicate);
                  if( predicate.equals(RDF.TYPE) ) {
                  	try {
                  		this.usedClassInData((URI)csvRecordFiller.tryExpand(object));
                  	}catch (ClassCastException e) {
                  		System.err.println("Trying to expand to subject annotation failed: " + object +"\n"+
                  				"   -> " +csvRecordFiller.tryExpand(object));
                  	}
                  }
               }
            }
            // Add additional descriptions with string or templated predicates.
            // https://github.com/timrdf/csv2rdf4lod-automation/issues/279
            for( String predicate : eParams.getContextualAdditionalDescriptions(0).keySet() ) {
               for( Value object : eParams.getContextualAdditionalDescriptions(0).get(predicate) ) {
                  // Where is "string" -> :predicate done?
                  //System.err.println(predicate + " " + object);
                  //System.err.println(columnNameFactory.namePropertySDV(csvRecordFiller.tryExpand(predicate).stringValue()) + " " + 
                  //                   csvRecordFiller.tryExpand(object));
               	URI p = columnNameFactory.namePropertySDV(csvRecordFiller.tryExpand(predicate).stringValue());
                  primary.add(subjectR, p, csvRecordFiller.tryExpand(object));
                  this.usedPredicateInData(p);
               }
            }
         } catch (RepositoryException e1) {
            e1.printStackTrace();
         }
         

         // Set of strings that should be ignored, regardless of the column they are in.
         Set<String> interpretAsNullStrings = eParams.getInterpetAsNullStrings();
         
         //
         // For each column of the csv file.
         //
         for (int c = 1; c <= columnNameFactory.getProperties().size(); c++ ) {

            //System.err.println(subjectR + "  ["+c+"]");
            //System.err.println("  ["+c+"] " + eParams.isCellBased(c)  + " " + subjectCol.get(subjectR) );
            
            if( eParams.isColumnOmitted(c) ) {
               logger.finest("r " + rowNum + " c "+ c +" skipping b/c column explicitly omitted.");
                                                                                                               continue;
            }
            
            // Cell-based subjects do not relate to other cell-based subjects.
            // They only relate to other non-cell-based subject values.
            if( eParams.isCellBased(c) && c != subjectCol.get(subjectR) ) {
               logger.finest("r " + rowNum + " c "+ c +" skipping b/c "+
                             "a cell-based subject and not "+subjectCol.get(subjectR));
                                                                                                               continue;
            }

            //this.csvRecordFiller.setContext("c", ""+c);
            //this.csvRecordFiller.setContext("H", this.originalColumnHeaders.get(c));
            //this.csvRecordFiller.setContext("L", this.eParams.getColumnLabel(c));
            //this.csvRecordFiller.setContext("@", this.columnNameFactory.getPropertyLocalName(c)); // TODO: better than what CSVRecordTemplateFiller is already doing?

            
            // By default, all cell-based subjects associate to all other values that are not cell-based subjects.
            // For example, cell-based subjects from 4, 5, 6, 7, 8 and 9 point to objects in 1, 2, and 3:
            //
            //                 <^   <^   <^   <^   <^   <^
            // [\./][\./][\./][ S ][ S ][ S ][ S ][ S ][ S ]
            //   1    2    3    4    5    6    7    8    9   
            //
            // This works in the "dense" cell-based subject case (i.e., statistical tables), but can be undesireable
            // in less dense ("sparse") data organizations, where some values "belong" to _just_ one of the cell-based
            // subjects and NOT the others.
            // For example, we want to prevent #10 from pointing to #8, while continuing #5 to point to #8.
            //
            //
            // <TODO: diagram chaos of 4 bundled by 5 and 10...>
            //
            //
            // This can be overridden by directly asserting which columns should be bundled into the cell-based subjects.
            // When done, the other cell-based subjects do not associate to the value that was bundled into another 
            // cell-based subject.
            //
            //   5    5   5
            //  10   10  10
            //                     __     __   __   __          __     __   __   __
            // [\./][\./][\./]  [ / ][ S ][ \ ][ \ ][ \ ]    [ / ][ S ][ \ ][ \ ][ \ ]  
            //   1    2    3      4    5    6    7    8        9   10   11   12   13
            //
            // However, we must permit other values to be bundled into non-cell-based subjects if specified:
            //
            //   2    5   5
            //       10  10
            //                     __  .  __   __   __          __  .  __   __   __
            // [\./][\./][\./]  [ / ][ S ][ \ ][ \ ][ \ ]    [ / ][ S ][ \ ][ \ ][ \ ]  
            //   1    2    3      4    5    6    7    8        9   10   11   12   13
            //
            HashSet<Integer> cellBasedIntersectBundledBy = new HashSet<Integer>(eParams.getCellBasedColumns());
                             cellBasedIntersectBundledBy.retainAll(eParams.getBundledByColumns(c));
            if( //////has been commented out for quite some time: eParams.isCellBased() && 
                // :bundledBy [1,2]: eParams.getBundledByColumn(c) > 0                         &&             // e.g.     8 bundled
                // :bundledBy [1,2]: eParams.getCellBasedColumns().contains(eParams.getBundledByColumn(c)) && // e.g. c = 8 bundled by 5, which is a cell-based subject {5, 10}
                // :bundledBy [1,2]: eParams.getBundledByColumn(c) != subjectCol.get(subjectR) &&             // e.g.     8 bundled by 5, but subject derived from 10 (!=5)
                
                eParams.getBundledByColumns(c).size() > 0 &&
                   cellBasedIntersectBundledBy.size() > 0 &&
               !eParams.getBundledByColumns(c).contains(subjectCol.get(subjectR)) ) {   
               
                  logger.finest("r "+ rowNum +" c "+ c +" "+
                                "skipping b/c " +
                                c + " is a cell-based subject, "+
                                "this value is bundled by "+eParams.getBundledByColumns(c) + ", " +
                                "the subject was derived from " + subjectCol.get(subjectR));
               continue;
            }
            
            
            
            
            // TODO: cite column BY PROPERTY NAME instead of just column index.
            // TODO: Dataset 1930: "" value becomes "
            
            
          
            
                                                                                                 //  0   1   2   3
            URI predicate        = columnNameFactory.getProperty(c,rowNum,this.csvRecordFiller); //     [ ] [ ] [ ]        predicates
            String predicateLN   = columnNameFactory.getPropertyLocalName(c);                    //     [ ] [ ] [ ]
            String fullCellValue = record.getQuotelessCommadValue(c-1);                          // [ ] [ ] [ ]            values
            
            logger.finest("r " + rowNum + " c "+c+"'s value: \""+fullCellValue +"\"");
            
            
            // In the typical case, there will only be one value in a cell
            // But a cell could contain multiple values and need to be split up. If more than one, then we will have:
            // "val 1, val 2" -> :row :col "val 1", "val 2" .
            //
            // NOTE: this only handles one of the two ways to handle multiple objects, 
            // not duplicating rows (the second type of multi-value)
            Set<String> values = new HashSet<String>();
            if( eParams.getObjectDelimiter(c) != null && !valueHandlers.get(c).interpretsAsNull(fullCellValue) ) {
               logger.finest("object delimiter: " + eParams.getObjectDelimiter(c) + " "  + fullCellValue);
               String[] vals = fullCellValue.split(eParams.getObjectDelimiter(c)); // TODO: this has thrown a null pointer before.
               for( int val = 0; val < vals.length; val++ ) {
                  logger.finest("parsed subvalue: "+vals[val]);
                  values.add(vals[val]);               
               }
            }else {
               //logger.finest("not delimiting: \"" + fullCellValue + "\"");
               values.add(fullCellValue);
            }
            
            //
            // For each value in the cell
            // Usually just one, but conversion:delimits_object could specify a delimiter to produce more.
            //
            for( String value : values ) {
               
               if( values.size() > 2 ) {
                  logger.finest("r " + rowNum + " c "+c+"'s subvalue: " + value + " of \""+ fullCellValue +"\"");
               }               
            
               // Skip PO if the value is null
               // TODO: Not giving Boolean value handler to assert false if value is "".
               // TODO: Should avoid pre-mature skipping; allow value handlers to decide.
               // TODO: parameterize value handlers to ignore empty (or assert empty string).
               //if( value == null || value.length() < 1 )                                                     continue;
               if( value == null )                                                                             continue;
   
               // Skip PO if the value should be interpreted as NULL.
               //   The decision to skip here is ONLY for the GLOBAL interpret-as-null strings, NOT the column-specific.
               //   We can't decide here b/c the column's value handler might use a codebook to reinterpret the value 
               //   into something that it wants to assert.
               if( (interpretAsNullStrings.contains((record.getValue(c-1))) || // The original cell value (before conv:delimit_object is applied)
                    interpretAsNullStrings.contains(value))                    // The delimited sub-value (because of conv:delimits_object)
                   &&
                    valueHandlers.get(c).interpretsAsNull(value) ) {           // Column-specific 
                  logger.finest("r " + rowNum + " c "+c+" skipping b/c " + 
                                "(cell value global as-null: " + interpretAsNullStrings.contains((record.getValue(c-1))) + " or "+
                                 "sub-value global as-null: "+ interpretAsNullStrings.contains(value) + ") and " +
                                 "col-specific as-null: " + valueHandlers.get(c).interpretsAsNull(value));
                  // TODO: column should be the authoritative source of this decision (and should include global in its decision).
                  continue;
               }
               
               this.flushCount += 1;
               
               this.csvRecordFiller.setColumnIndex(c);
   
               
               
               
               if( eParams.isCellBased(c) ) {
                  // Instead of the triple coming from the row to the cell value,
                  // the triple is coming from the cell and going up to the "up object"
                  //
                  //                 |   |   |   | col
                  //                  ------------------
                  //                 |
                  //                 |
                  //  DEFAULT:    _3 | ----col---> [.]
                  //                 |
                  //
                  //
                  //                 |   |   |   | col
                  //                  ------------------
                  //  YES:           |             / \
                  //                 |              |
                  //                 |              |
                  //              _3 |    [#2] <-- [.]
                  try {
                     //System.err.println(rowNum+": "+subjectR);
                     Value  upObject    = this.eParams.getCellBasedValue(c); // Could be a Literal or Resource (or null - meaning use header).
                     String propertyLN  = this.columnNameFactory.getPropertyLocalName(c);
                     String header      = this.originalColumnHeaders.get(c);
                     String headerLN    = NameFactory.label2URI(header);
                     if( upObject == null ) {
                        // If no object is specified, the header is promoted to a Resource using a predicate-scoped name
                        // and used as the object. Defaults to promoting "within" the subjectDiscriminator -- a 
                        // template needs to be specified to promote the Resource "outside of" the subjectDiscriminator.
                        upObject = vf.createURI(this.objectNS + "value-of/" + propertyLN + "/" + headerLN);
                        ancillary.add( (URI) upObject, DCTerms.identifier, vf.createLiteral(header));
                        this.usedPredicateInData(DCTerms.identifier);
                     }else {
                        String upObjectS  = upObject.stringValue();
                        boolean objectIsTemplated = csvRecordFiller.doesExpand(upObjectS) || 
                                                    eParams.fillTemplate(upObjectS).length() != upObjectS.length(); // TODO: length hack b/c can't talk to eParams#doesExpand b/c reference is EnhancementParams not TemplateFiller.
                        if( "[/sd]/value-of/[@]/[.]".equals(upObjectS) ||   // <-- This is here for backward compatability.
                            "[/sd]/value-of/[@]/[H]".equals(upObjectS)  ) { // <-- This is what it should be.
                           // Promotes "out of" the subjectDiscriminator
                           // TODO: use template fillers instead of hard coding the special case.
                           upObject = vf.createURI(this.objectNSunversioned + "value-of/" + propertyLN + "/" + headerLN);
                           ancillary.add( (URI) upObject, DCTerms.identifier, vf.createLiteral(header));
                           this.usedPredicateInData(DCTerms.identifier);
                        //}else if( upObject.stringValue().matches("\\[/sd\\]typed/[^/]*/\\[H\\]")) { // This case is handled by the general case below.
                        }else if( objectIsTemplated ) { 
                           String template = upObjectS.replaceFirst("\\[H\\]", "[.]");
                           template     = csvRecordFiller.fillTemplate(template, header, CSVRecordTemplateFiller.AS_RESOURCE);
                           //String uri   = eParams.fillTemplate(template); // TODO: This should be done by CSVRecordFiller.
                           //System.err.println(upObjectS + " --> " + template);
                           upObject = vf.createURI(template);
                           ancillary.add( (URI) upObject, DCTerms.identifier, vf.createLiteral(header));
                           this.usedPredicateInData(DCTerms.identifier);
                        }else {
                           //System.err.println("cell-based object is plain old literal/resource: "+upObject.stringValue());
                        }
                     }
                     primary.add(subjectR, predicate, upObject);
                     this.usedPredicateInData(predicate);
                     
                     // Assert superproperties
                     for( URI superPredicate : eParams.getSuperProperties(c) ) {
                        primary.add(subjectR, superPredicate, upObject);
                        this.usedPredicateInData(superPredicate);
                     }
   
                     //System.err.println("    first cell description: "+predicate+" = "+eParams.getCellBasedValue(c));
                     
                     // Add additional dimension predicates.
                     // TODO: this is done elsewhere for row-based.
                     //HashMap<URI,Value> additionalDescriptions = eParams.getAdditionalDescriptions(c);
                     //System.err.println(c + " " + additionalDescriptions.size());
                     for( URI additionalPredicate : eParams.getConstantAdditionalDescriptions(c).keySet() ) {
                        //Value additionalValue = additionalDescriptions.get(additionalPredicate);
                        for( Value additionalObject : eParams.getConstantAdditionalDescriptions(c).get(additionalPredicate) ) {
                           //System.err.println("    describing s with "+additionalPredicate+" = "+additionalObject);
                           // Note: This is cell based. Subject annotations for row-based are done above.
                           primary.add(subjectR, additionalPredicate, this.csvRecordFiller.tryExpand(additionalObject));
                           this.usedPredicateInData(additionalPredicate);
                           this.usedClassInData((URI)this.csvRecordFiller.tryExpand(additionalObject));
                        }
                     }
                  } catch (RepositoryException e) {
                     e.printStackTrace();
                  }
                  //System.err.println("r " + rowNum + " c "+ c + " " + eParams.getCellBasedOutPredicate(c));
                  
                  predicate =    eParams.getCellBasedOutPredicate(c) != null 
                  		      && eParams.getCellBasedOutPredicate(c) instanceof URI
                              ? (URI) eParams.getCellBasedOutPredicate(c) 
                              : RDF.VALUE;
                  predicateLN = "value"; // TODO: doesn't align with overriding predicate, if set.
               } // end if cell based
               
               
               
               
               
               
               
               //
               // Implicit bundle
               //
               if( eParams.getImplicitBundlePropertyName(c) != null ) { // B(1,2,3) bundlePropertyName(1) -> B, bundleID(3) -> 1 .
                  // Implicit column bundling
                  // s h o   ->   s  P_b  B .   B h o .
                  // The predicate/value should be describing a newly-minted URI instead of the row URI.
                  // The row URI links to the newly-minted URI with a new predicate named after the bundlePropertyName.
                  logger.finest("r " + rowNum + " c " + c + ": <"+subjectCol.get(subjectR)+" :" + predicateLN + " \""+value+"\"> Implicit column bundling");

   
                  // Determine predicate from ROW_URI to IMPLICIT_BUNDLE
                  URI bundlePredicateR = this.predicatesToImplicitBundles.get(c); // TODO: why plural?
                  
                  
                  
                  // Construct name of intermediary IMPLICIT_BUNDLE (NOTE: these are created WITHIN THE VERSION)
                  String bundleIdentifier = this.implicitBundleIdentifiers.get(c) + "_" + rowNum;
                  Resource bundleR = vf.createURI(this.subjectNS + bundleIdentifier);
                  
                  // version/2009-Oct-02/PROPERTY_NAME/thing_1     (1 == row's thing_1) (when implicit bundle is NOT typed)
                  // version/2009-Oct-02/PROPERTY_NAME/type_name_1 (1 == row's thing_1) (when implicit bundle is     typed with type_name)
                  if( eParams.getImplicitBundleNameTemplate(c) != null ) {
                     bundleIdentifier = this.csvRecordFiller.fillTemplate(eParams.getImplicitBundleNameTemplate(c));
                     if( ResourceValueHandler.isURI(bundleIdentifier) ) {
                        bundleR = vf.createURI(bundleIdentifier);
                     }else {
                        bundleIdentifier = this.csvRecordFiller.fillTemplate(eParams.getImplicitBundleNameTemplate(c), 
                                                                             CSVRecordTemplateFiller.AS_RESOURCE);
                        bundleR = vf.createURI(this.subjectNS + bundleIdentifier);
                     }
                  }// NOTE: if property_name is a URI, get goofy /http_purl_org_dc_terms_spatial/location_32604
    

                  // TODO: con:preferredURI from type_1 to thing_1 
                  try {
                     primary.add(subjectR, bundlePredicateR, bundleR); // Associate the subjectR with the intermediary.
                     for( URI bundleType : this.implicitBundleTypes.get(c) ) {
                        ancillary.add(bundleR, RDF.TYPE, bundleType);  // Assert types of implicit bundle.
                        this.usedPredicateInData(RDF.TYPE);
                        this.usedClassInData(bundleType);
                     }
                     for( URI annotationP : eParams.getImplicitBundleAnnotations(c).keySet() ) {
                        for( Value annotationO : eParams.getImplicitBundleAnnotations(c).get(annotationP) ) {
                           ancillary.add(bundleR, annotationP, annotationO);
                           this.usedPredicateInData(annotationP);
                           if( annotationP.equals(RDF.TYPE) ) {
                           	this.usedClassInData((URI)annotationO);
                           }
                        }
                     }
                  } catch (RepositoryException e) {
                     e.printStackTrace();
                     System.err.println("(implicit bundle) ERROR:");
                  }
                 
                  // Value handlers are called with a subject of IMPLICIT_BUNDLE instead of ROW_URI
                  //                                      .-----------|
                  //                                      |
                  //                                     \|/
                  handleValue(   valueHandlers.get(c), bundleR, predicate,predicateLN,                    value, ancillary, ancillary);
                  handleValues(c,valueHandlers.get(c), bundleR, eParams.getSuperProperties(c),predicateLN,value, ancillary, ancillary);
               //}else if( eParams.getBundledByColumn(c) > 0 && eParams.getBundledByColumn(c) != subjectCol.get(subjectR) ) { // Should be replaced by calls to getBundledByColumns(c); attempt below.
               }else if(  eParams.getBundledByColumns(c).size() > 0                         && 
                         !eParams.getBundledByColumns(c).contains(subjectCol.get(subjectR)) /*&& 
                          subjectCol.get(subjectR) > 0*/) {
                    //   eParams.getBundledByColumn(c) > 0 && !eParams.getCellBasedColumns().contains(c) ) {
                    //   eParams.getBundledByColumn(c) > 0 && !eParams.isCellBased() ) { //|| // Switched from null to 0 b/c 0 is a good representation for default row-based behavior.
                    //  (eParams.isCellBased() && eParams.getBundledByColumn(c) == subjectCol.get(subjectR)) ) { 
                  
                  
                  // NOTE: even though this condition is fading, we STILL want to toggle to ancillary for secondary triples.
                  
                  //
                  // Explicit bundle
                  //
                  for( int bundleColumnIndex : eParams.getBundledByColumns(c) ) { // TODO: this should be pushed up to subject creation, NOT DUPLICATED here.
                     //Integer bundleColumnIndex = subjectCol.get(subjectR);  // replaced for :bundledBy [1,2]: eParams.getBundledByColumn(c);
                     logger.finest("r " + rowNum + " c " + c + ": "+
                                   "<"+subjectCol.get(subjectR)+" :" + predicateLN + " \""+value+"\"> "+
                                   "Existing column bundling to "+bundleColumnIndex + " from " + eParams.getBundledByColumns(c));
                     
   
   
                     // The predicate/value should be describing the promoted resource of another column instead of 
                     // the row resource. The other column's value needs to be re/pre-promoted.
   
                     
                     
                     // NOTE: some/most of the logic in here was probably OBE by the final solution to the 
                     // "sparse bundled-bys" from the EPA water quality data.
                     
   
                     
                     Resource existingBundleR = null;
                     if( eParams.isCellBased(bundleColumnIndex) ) {
                        existingBundleR = subjectR; // The subject was already figured out.
                     }else {
                        try {
                           ResourceValueHandler bundleHandler = (ResourceValueHandler) this.valueHandlers.get(bundleColumnIndex);
                           //System.err.println("bundle handler: " + c + " " + bundleHandler);
                           String   bundlesValue    = record.getQuotelessCommadValue(bundleColumnIndex-1);
                           String bundlePredicateLN = this.columnNameFactory.getPropertyLocalName(bundleColumnIndex);
                           
                           csvRecordFiller.setColumnIndex(bundleColumnIndex);//pushColumnIndex(bundleColumnIndex);
                           existingBundleR = bundleHandler.promote(bundlePredicateLN, objectNS, bundlesValue, csvRecordFiller);
                           csvRecordFiller.setColumnIndex(c);
                        }catch( ClassCastException e ) {
                           //e.printStackTrace();
                           System.err.println("(existing bundle) ERROR: "+ c +" failed to bundle into "+ bundleColumnIndex +
                                              " - it is not a resource. change it's conversion:range to rdfs:Resource." );
                           System.err.println("    bundle range: " + this.valueHandlers.get(bundleColumnIndex).getClass().getSimpleName());
                           System.err.println("    "+subjectR + " " + eParams.isCellBased(c)+ " " + eParams.isCellBased(bundleColumnIndex));//eParams.getBundledByColumn(c)));
                        }catch (Exception e) {
                           e.printStackTrace();
                           System.err.println("change Literal to Resource?");
                        }
                     }
   
   
                     //System.err.println("r " + rowNum + " c " + c + " (subject is from col "+ subjectCol.get(subjectR) +") ("+ predicateLN + " \"" + value + "\" bundled to "+bundleColumnIndex+")");
                     if( subjectCol.get(subjectR) != bundleColumnIndex ) {
                        // Respect another cell-subject's bundle (i.e., DO NOTHING)
                        
                        logger.finest("skipping explicit bundle b/c subject's column "+subjectCol.get(subjectR)+" != "+bundleColumnIndex);
                     }
                     if( existingBundleR != null ) {
                                                                     /*Apr 2011 - this is probably ready to be deleted: String valueS = 
                                                                     this.valueHandlers.get(c).getRange().equals(RDFS.RESOURCE) ? 
                                                                           ((ResourceValueHandler) currentHandler).promote(predicateLN, objectNS, value, csvRecordFiller).stringValue() : 
                                                                           value; 
                                                                  Bug in ds 1492 when valueS is a Resource -- want to pass a resource to be cast, but does not have the label. 
                                                                  String valueS = value; // <-- Bug fix. This should be ok, b/c the value handler should know to promote it.      */ 
      
                        handleValue(   valueHandlers.get(c), existingBundleR, predicate,                    predicateLN,value, ancillary, ancillary);
                        //this.valueHandlers.get(c).handleValue(existingBundleR, predicate,predicateLN,value, ancillary, objectNS, this.csvRecordFiller, ancillary);
                        handleValues(c,valueHandlers.get(c), existingBundleR, eParams.getSuperProperties(c),predicateLN,value, ancillary, ancillary);
                     }else {
                        System.err.println(rowNum+": could not name existing bundle: ["+bundleColumnIndex+"] " +predicateLN + " ["+c+"] => " + 
                              "("+record.getQuotelessCommadValue(bundleColumnIndex-1)+", "+predicateLN+", "+value+") => "+
                              "("+existingBundleR+", "+predicateLN+", "+value+")");
                     }
                  }
               }else if( RDFS.RESOURCE.equals(this.valueHandlers.get(c).getRange()) ) {
                  // Promoting to resource
                  logger.finest("r " + rowNum + " c " + c + ": "+
                                "<"+ subjectCol.get(subjectR) +" :" + predicateLN + " \""+ value +"\"> "+
                                "Letting "+ valueHandlers.get(c).getClass().getSimpleName() +" assert as resource.");
                  
                  // TODO: consider distinction between a crutch and template promotion, where the template pattern begins
                  // with a URI.
                  ResourceValueHandler rvh = null;
                  try {
                     rvh = (ResourceValueHandler) this.valueHandlers.get(c);
                  } catch (ClassCastException e) {
                     //e.printStackTrace();
                     System.err.println("ERROR: conversion value for column "+c+" is not a rdfs:Resource: "+value);
                  }
                  /*if( eParams.getObjectTemplates(c).size() > 0 && false ) {// NOTE: loop should be done by value handler.
                     // CrutchResourcePromotion
                     //System.err.println("      Promoting to resource with range template");
                     
                     // Cell value is being promoted to a Resource within this dataset's local namespace
                     // using other cell values as part of its URI.
                     for( String crutchPattern : eParams.getObjectTemplates(c) ) {
                        // TODO: no need to promote? handled by handler?
                        String crutchValue = csvRecordFiller.fillTemplate(crutchPattern, CSVRecordTemplateFiller.AS_RESOURCE);  
                        try {
                           ancillary.add(rvh.promote(predicateLN,objectNS,crutchValue,csvRecordFiller), RDFS.LABEL, vf.createLiteral(value));
                        } catch (RepositoryException e) {
                           e.printStackTrace();
                        }
                        handleValue(   valueHandlers.get(c), subjectR,  predicate,                    predicateLN, crutchValue);
                        handleValues(c,valueHandlers.get(c), subjectR,  eParams.getSuperProperties(c),predicateLN, crutchValue); 
                     }
                     // replaced by handleValues: this.valueHandlers.get(c).handleValue(subjectR,  predicate,predicateLN,crutch, conn, objectNS, ancillary); 
                  }else if( eParams.getObjectTemplates(c) != null && false ) {// NOTE: object template handled by ResourceValueHandler.
                     //System.err.println("      Promoting to resource with promotion template");
                     
                     // TODO: make sure this is OBE by Crutch switching to range_template with multiple values.
                     
                     //String valueURI = csvRecordFiller.fillPattern(this.eParams.getTemplatePromotionPattern(c));
                     // NOT USED: URI valueURI = rvh.promote(predicateLN, objectNS, value, csvRecordFiller);
                     //System.err.println("template: "+value+" -> "+valueURI.stringValue());
                     
                     handleValue(   valueHandlers.get(c), subjectR,  predicate,                     predicateLN, value);
                     handleValues(c,valueHandlers.get(c), subjectR,  eParams.getSuperProperties(c), predicateLN, value); 
                     
                     //TODO: valueURI is not getting a label. OK?
                     // add back in: ancillary.add(valueURI, RDFS.LABEL, vf.createLiteral(value));
                  }else {*/
                     //System.err.println("      Letting "+valueHandlers.get(c).getClass().getSimpleName()+" promote to resource.");
                     handleValue(   valueHandlers.get(c), subjectR,  predicate,                     predicateLN, value);
                     handleValues(c,valueHandlers.get(c), subjectR,  eParams.getSuperProperties(c), predicateLN, value); // NOTE: this same as literal case.
                  /*}*/
               }else {
                  logger.finest("r " + rowNum + " c " + c + ": "+
                                "<"+subjectCol.get(subjectR)+" :" + predicateLN + " \""+value+"\"> "+
                                "Letting "+ valueHandlers.get(c).getClass().getSimpleName() +" assert as literal."+valueHandlers.get(c).getClass().getSimpleName());
                  
                  
                  if( ("DateTimeValueHandler".equals(valueHandlers.get(c).getClass().getSimpleName()) || 
                           "DateValueHandler".equals(valueHandlers.get(c).getClass().getSimpleName()) ) 
                       && eParams.getObjectTemplates(c).size() > 0) { 
                     
                     //
                     // NOTE: loop _should_ be done by value handler; like EnhancedLiteralValueHandler does
                     for( String template : eParams.getObjectTemplates(c) ) {
                        logger.finest("literal object template: "+template+ " -> "+csvRecordFiller.fillTemplate(template));
                        handleValue(   valueHandlers.get(c), subjectR,  predicate,                    predicateLN,  csvRecordFiller.fillTemplate(template));
                        handleValues(c,valueHandlers.get(c), subjectR,  eParams.getSuperProperties(c),predicateLN, csvRecordFiller.fillTemplate(template));
                     }
                     //
                     // ^^^ This should get moved into the value handlers. Like EnhancedLiteralValueHandler does.
                  }else {
                     handleValue(   valueHandlers.get(c), subjectR,  predicate,                    predicateLN, value);
                     handleValues(c,valueHandlers.get(c), subjectR,  eParams.getSuperProperties(c),predicateLN, value);
                  }
               }
               
               
               
               // Cell-based subjects have a column, too. (Instead of just a row).
               if( eParams.isCellBased(c) ) {
                  try {
                     primary.add(subjectR, OpenVocab.csvRow, vf.createLiteral(""+rowNum,XMLSchema.INTEGER));
                     primary.add(subjectR, OpenVocab.csvCol, vf.createLiteral(""+c,     XMLSchema.INTEGER));
                     this.usedPredicateInData(OpenVocab.csvRow, OpenVocab.csvCol);
                  } catch (RepositoryException e) {
                     e.printStackTrace();
                  }
               }
               
               // Add the first subjectR as an exampleResource. Add the last subjectR as well.
               if( this.firstSubjectR == null ) {
                  this.firstSubjectR = subjectR;
               }
               this.lastSubjectR = subjectR;
               
               logger.finest(rowNum +"-["+ c +"] done.\n");
            }
            
         } // Done with all sub-values caused by object_delimiter

         // Add additional predicate-objects as listed in enhancement parameters.
         for( URI addP : this.additionalDescriptions.keySet() ) {
            try {
               //System.err.println(addP+" "+ this.additionalDescriptions.get(addP));
               primary.add(subjectR, addP, this.additionalDescriptions.get(addP));
               this.usedPredicateInData(addP);
            } catch (RepositoryException e) {
               e.printStackTrace();
            }
         }
         
         // Add subject discriminator
         if( this.subjectDiscriminatorR != null ) {
            try {
               primary.add(subjectR, OpenVocab.subDis,       this.subjectDiscriminatorR);
               primary.add(subjectR, DCTerms.isReferencedBy, this.subjectDiscriminatorR);
               this.usedPredicateInData(OpenVocab.subDis, DCTerms.isReferencedBy);
            } catch (RepositoryException e) {
               e.printStackTrace();
            }
         }
         
         // Commit
         try {
            if( commitInterval > 0 && rowNum % commitInterval == 0 ) {
            	primary.commit();
            }
            primary.add(subjectR, OpenVocab.csvRow, vf.createLiteral(""+rowNum,XMLSchema.INTEGER));
            this.usedPredicateInData(OpenVocab.csvRow);
         } catch (RepositoryException e) {
            e.printStackTrace();
         }   
      }
      this.previousRec = record;
   }

   /**
    * URI for a promoted resource can be 'version/2000-Sep-07/state/Alabama' or 'version/2000-Sep-07/name/Alabama'
    * depending on whether the instance is typed.
    * 
    * @return e.g. "", "state/", or "name/"
    */
   private String getResourcePromotionToken(int columnIndex) {
      String step = "";
      if( eParams.getSubjectTypeLocalName() != null && eParams.getSubjectTypeLocalName().length() > 0 ) {
         step = eParams.getSubjectTypeLocalName().toLowerCase() + "/";
      }else {
         step = columnNameFactory.getUniqueColumnLabel(columnIndex).replaceAll("\\W", "_").toLowerCase()+"/";
      }
      return step;
      // TODO: see where else this logic is done and call here instead (note: distiction between subject and object).
   }

   /**
    * 
    * @param subjectR
    * @param rawSubjectR
    */
   private void assertSameAs(Resource subjectR, Resource rawSubjectR) {
      try {
         // Assert owl:sameAs between keyed local name and default local name.
         if( !subjectR.equals(rawSubjectR) ) {
            //ancillary.add(subjectR, OWL.SAMEAS, rawSubjectR); // DEPRECATED b/c sameAs between layers is less interesting than between datasets.
            ancillary.add(rawSubjectR, SWAP_PIM_CON.preferredURI, subjectR); // But we still want to draw the association.
            this.usedPredicateInData(SWAP_PIM_CON.preferredURI);
         }
      } catch (RepositoryException e) {
         e.printStackTrace();
      }
   }
   
   /**
    * Notify that 'predicate' was asserted on a data triple.
    * 
    * @param predicate - the predicate used in a data triple.
    */
   protected void usedPredicateInData(URI... predicates) {
   	for( URI predicate : predicates ) {
   		this.predicatesUsedInData.add(predicate);
   	}
   }
   
   /**
    * Notify that 'predicate' was asserted on a data triple.
    * 
    * @param predicate - the predicate used in a data triple.
    */
   protected void usedPredicateInMetaData(URI... predicates) {
   	for( URI predicate : predicates ) {
   		this.predicatesUsedInMetadata.add(predicate);
   	}
   }
   
   /**
    * Notify that 'classes' was asserted on a data triple.
    * 
    * @param class - the class used in a data triple.
    */
   protected void usedClassInData(URI... classes) {
   	for( URI c : classes ) {
   		this.classesUsedInData.add(c);
   	}
   }
   
   /**
    * Notify that 'class' was asserted on a data triple.
    * 
    * @param class - the class used in a data triple.
    */
   protected void usedClassInMetaData(URI... classes) {
   	for( URI c : classes ) {
   		this.classesUsedInMetadata.add(c);
   	}
   }
   
   //
   // There are four handleValue[s] methods.
   // All four call valueHandler.handleValue.
   //
   // Two methods take the columnIndex _sets_ of predicates to assert; the other two 
   // do not take the columnIndex and only take one predicate.
   //
   // Two methods (one from each of the pairs above) accept the primary and ancillary repositories
   // as parameters, so that the place that the main triples are asserted can be controlled.
   //
   
   /**
    * Shorthand to call valueHandler.handleValue() for external predicates.
    * 
    * @param valueHandler
    * @param subjectR
    * @param predicate
    * @param predicateLN
    * @param value
    */
   private void handleValues(int columnIndex, ValueHandler valueHandler, 
                             Resource subjectR,                       // Subject
                             Set<URI> predicates, String predicateLN, // Predicate
                             String   value) {                        // Object
   	
      if( !eParams.isCellBased(columnIndex) ) {
         for( URI predicate : predicates ) {
            valueHandler.handleValue(subjectR,              // Subject
            								 predicate,predicateLN, // Predicate
            								 value,                 // Object
            								 primary,
            								 objectNS, this.csvRecordFiller,
            								 ancillary);
         }
      }
   }
   
   /**
    * Shorthane to call valueHandler.handleValue() for external predicates
    * (plus control of the the primary and secondary repository).
    * 
    * @param columnIndex - 
    * 
    * @param valueHandler - 
    * 
    * @param subjectR - 
    * @param predicates - 
    * @param predicateLN - 
    * @param value - 
    * 
    * @param primary - 
    * @param ancillary - 
    */
   private void handleValues(int columnIndex, ValueHandler valueHandler, 
   		
                             Resource subjectR,                       // Subject
                             Set<URI> predicates, String predicateLN, // Predicate
                             String   value,                          // Object
                             
                             RepositoryConnection primary, RepositoryConnection ancillary) {
   	
      if( !eParams.isCellBased(columnIndex) ) {
         for( URI predicate : predicates ) {
            valueHandler.handleValue(subjectR,               // Subject
            								 predicate, predicateLN, // Predicate
            		                   value,                  // Object
            		                   primary, 
            		                   objectNS, this.csvRecordFiller,
            		                   ancillary);
         }
      }
   }
   

   /**
    * Handle original internal predicate.
    * 
    * @param valueHandler - 
    * 
    * @param subjectR - 
    * @param predicate - 
    * @param predicateLN - 
    * @param value - 
    */
   private void handleValue(ValueHandler valueHandler,
                            Resource     subjectR,                      // Subject
                            URI          predicate, String predicateLN, // Predicate
                            String       value) {                       // Object
   	
      valueHandler.handleValue(subjectR,               // Subject
      		                   predicate, predicateLN, // Predicate
      		                   value,                  // Object
      		                   primary,
      		                   objectNS, this.csvRecordFiller,
      		                   ancillary);
      this.usedPredicateInData(predicate);
   }
   
   /**
    * 
    * @param valueHandler - 
    * 
    * @param subjectR - 
    * @param predicate - 
    * @param predicateLN - 
    * @param value - 
    * 
    * @param primary - 
    * @param ancillary - 
    */
   private void handleValue(ValueHandler valueHandler, 
                            Resource     subjectR,                      // Subject
                            URI          predicate, String predicateLN, // Predicate
                            String       value,                         // Object
                            RepositoryConnection primary, RepositoryConnection ancillary) {
   	
      logger.finest(valueHandler.getClass().getSimpleName()+" handle :"+predicateLN+" "+value);
      
      valueHandler.handleValue(subjectR,               // Subject
      								 predicate, predicateLN, // Predicate
      								 value,                  // Object
      		                   primary,
      		                   objectNS, this.csvRecordFiller,
      		                   ancillary);
      this.usedPredicateInData(predicate);
   }
   

   
   
   
   
   
   
   
   
   

   
   // Please move these to CSVParser   
   
   /**
    * @param fileName - file path of the file to open.
    * @return the CSVRecords in 'fileName'.
    * @throws IOException if bad things happen when opening the file.
    */
   public static Collection<CSVRecord> load(String fileName, int headerDelay) throws IOException {
      return CSVParser.doReader(new BufferedReader(
            new InputStreamReader(
                  new FileInputStream(fileName))),headerDelay);
      // TODO: This should be a utility function on CSVParser.
   }
   
   /**
    * @param fileName - file path of the file to open.
    * @return the CSVRecords in 'fileName'.
    * @throws IOException if bad things happen when opening the file.
    */
   public static Collection<CSVRecord> load(String fileName) throws IOException {
      return CSVParser.doReader(new BufferedReader(
            new InputStreamReader(
                  new FileInputStream(fileName))));
      // TODO: This should be a utility function on CSVParser.
   }

   /**
    * 
    * @return
    */
   public static List<String> getKeys( Collection<CSVRecord> records ) {
      Iterator<CSVRecord> iter = records.iterator();
      if (iter.hasNext()) {
         return iter.next().getKeys();
      } else {
         return null;
      } 
      // TODO: This should go to CSVParser
   }


}