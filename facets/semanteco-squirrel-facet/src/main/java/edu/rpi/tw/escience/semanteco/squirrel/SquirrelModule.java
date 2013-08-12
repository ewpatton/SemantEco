package edu.rpi.tw.escience.semanteco.squirrel;

import static edu.rpi.tw.escience.semanteco.query.Query.VAR_NS;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import java.lang.reflect.InvocationTargetException;
import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.QueryMethod;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.Resource;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.query.GraphComponentCollection;
import edu.rpi.tw.escience.semanteco.query.NamedGraphComponent;
import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.QueryResource;
import edu.rpi.tw.escience.semanteco.query.Variable;
import edu.rpi.tw.escience.semanteco.query.Query.Type;
import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.ProvidesDomain;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SquirrelModule implements Module {
	//SQUIRREL
	private ModuleConfiguration config = null;
	private static final String FAILURE = "{\"success\":false}";
	//private static final JSONArray failed = new JSONArray("{\"success\":false}"");
	private static final Logger log = Logger.getLogger(SquirrelModule.class);
	
	//Endpoint
	private static final String LOGD_ENDPOINT = "http://hercules.tw.rpi.edu:8083/parliament/sparql";
	private static final String LOGD_ENDPOINT_RETURN_JSON = "http://hercules.tw.rpi.edu:8083/parliament/sparql?output=json";
	
	//Prefixes
	private static final String SF = "http://www.opengis.net/ont/sf#";
    private static final String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
    private static final String GEO = "http://www.opengis.net/ont/geosparql#";
    private static final String GEOF = "http://www.opengis.net/def/function/geosparql/"; 
	
    //Dataset
    private static final String NHD = "http://nhd.usgs.gov/";
   
    //NHD feature
    private static final String NHDF = "http://cegis.usgs.gov/rdf/nhd/Features/";
    
    //TEST METHOD
    //All features within in NYS polygon
	@QueryMethod
	public String testMethod(final Request request) 
	{
		//Build query
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);	
		final NamedGraphComponent graph = query.getNamedGraph("http://nhd.usgs.gov/");
		
		//?f geo:hasGeometry ?fGeom .
		final QueryResource hasGeometry = query.getResource(GEO + "hasGeometry");
		final Variable fGeometry = query.getVariable(VAR_NS + "fGeom");
		final Variable feature = query.getVariable(VAR_NS + "f");
		graph.addPattern(feature, hasGeometry, fGeometry);
		
		//?fGeom geo:asWKT ?fWKT .
		final QueryResource asWKT = query.getResource(GEO + "asWKT");
		final Variable featureWKT = query.getVariable(VAR_NS + "fWKT");
		graph.addPattern(fGeometry, asWKT, featureWKT);
		
		//FILTER(geof:sfWithin(?fWKT, "POLYGON((-81.587906 45.336702, -81.148453 39.774769,    -69.964371 39.30029, -70.403824 45.58329, -81.587906 45.336702))"^^geo:wktLiteral))
		graph.addFilter("<" + GEOF + "sfWithin>(?fWKT, \"POLYGON((-81.587906 45.336702, -81.148453 39.774769,    -69.964371 39.30029, -70.403824 45.58329, -81.587906 45.336702))\"^^" + "<" + GEO + "wktLiteral>)");
		
		// Execute query
		String responseStr = FAILURE;
		String resultStr = config.getQueryExecutor(request).execute(LOGD_ENDPOINT, query);	
			
		// DEBUGGING
		log.debug("Results: " + resultStr);
		
		// If there was a failure to query
		if(resultStr == null) { return responseStr; }
			System.out.println(resultStr);
		 return resultStr;	

	}
	
	//FEATURES WITHIN POLYGON WITHOUT JSON AND WKT RETURN
		//All features within a user drawn polygon
		@QueryMethod
		public String featuresWithinPolygonWITHOUTJSONANDWKTRETURN(final Request request) 
		{
			//Build query
			final Query query = config.getQueryFactory().newQuery(Type.SELECT);	
			final NamedGraphComponent graph = query.getNamedGraph("http://nhd.usgs.gov/");

			//?f geo:hasGeometry ?fGeom .
			final QueryResource hasGeometry = query.getResource(GEO + "hasGeometry");
			final Variable fGeometry = query.getVariable(VAR_NS + "fGeom");
			final Variable feature = query.getVariable(VAR_NS + "f");
			graph.addPattern(feature, hasGeometry, fGeometry);

			//?fGeom geo:asWKT ?fWKT .
			final QueryResource asWKT = query.getResource(GEO + "asWKT");
			final Variable featureWKT = query.getVariable(VAR_NS + "fWKT");
			graph.addPattern(fGeometry, asWKT, featureWKT);

			 //Get polygon coordinates
			 JSONArray coords = (JSONArray) request.getParam("UserDrawnMapPolygon");
			 String[] coordsStr = new String[coords.length()];
			 for(int a = 0; a < coords.length(); a++)
				 coordsStr[a] = coords.optString(a);

			 String[][] longlat = new String[coordsStr.length][2];

			 //Parse out () and ,
			 //Separate lat and long b/c polygon takes long lat, not lat long
			 for(int a = 0; a < coordsStr.length; a++)
			 {
			  coordsStr[a] = coordsStr[a].replace("(", "");
			  coordsStr[a] = coordsStr[a].replace(")", "");
			  coordsStr[a] = coordsStr[a].replace(",", "");
			  longlat[a][0] = (coordsStr[a].split("\\s+"))[1];
			  longlat[a][1] = (coordsStr[a].split("\\s+"))[0];
			 }

			 //Construct condition
			 String filterCondition = "<" + GEOF + "sfWithin>(?fWKT, \"POLYGON((" ;
			 for(int b = 0; b < coordsStr.length; b++)
			  filterCondition += longlat[b][0] + " " + longlat[b][1] + "," ;
			 filterCondition += longlat[0][0] + " " + longlat[0][1];
			 filterCondition += "))\"^^" + "<" + GEO + "wktLiteral>)" ;

			 //Filter for features in polygon
			 graph.addFilter(filterCondition);

			// Execute query
			String responseStr = FAILURE;
			String resultStr = config.getQueryExecutor(request).execute(LOGD_ENDPOINT, query);	

			// DEBUGGING
			log.debug("Results: " + resultStr);

			// If there was a failure to query
			if(resultStr == null) { return responseStr; }
				System.out.println(resultStr);
			 return resultStr;	

		}

	//FEATURES WITHIN POLYGON
	//All features within a user drawn polygon
	@QueryMethod
	public String featuresWithinPolygon(final Request request) throws JSONException
	{
		//Build query
		final Query query = config.getQueryFactory().newQuery(Type.SELECT);	
		final NamedGraphComponent graph = query.getNamedGraph("http://nhd.usgs.gov/");
		
		//?f geo:hasGeometry ?fGeom .
		final QueryResource hasGeometry = query.getResource(GEO + "hasGeometry");
		final Variable fGeometry = query.getVariable(VAR_NS + "fGeom");
		final Variable feature = query.getVariable(VAR_NS + "f");
		graph.addPattern(feature, hasGeometry, fGeometry);
		
		//?f rdfs:hasLabel ?fName .
		final QueryResource hasLabel = query.getResource(RDFS + "label");
		final Variable featureName = query.getVariable(VAR_NS + "fName");
		graph.addPattern(feature, hasLabel, featureName);
		
		//?fGeom geo:asWKT ?fWKT .
		final QueryResource asWKT = query.getResource(GEO + "asWKT");
		final Variable featureWKT = query.getVariable(VAR_NS + "fWKT");
		graph.addPattern(fGeometry, asWKT, featureWKT);
		
		 //Get polygon coordinates
		 JSONArray coords = (JSONArray) request.getParam("UserDrawnMapPolygon");
		 String[] coordsStr = new String[coords.length()];
		 for(int a = 0; a < coords.length(); a++)
			 coordsStr[a] = coords.optString(a);
		 
		 String[][] longlat = new String[coordsStr.length][2];
		
		 //Parse out () and ,
		 //Separate lat and long b/c polygon takes long lat, not lat long
		 for(int a = 0; a < coordsStr.length; a++)
		 {
		  coordsStr[a] = coordsStr[a].replace("(", "");
		  coordsStr[a] = coordsStr[a].replace(")", "");
		  coordsStr[a] = coordsStr[a].replace(",", "");
		  longlat[a][0] = (coordsStr[a].split("\\s+"))[1];
		  longlat[a][1] = (coordsStr[a].split("\\s+"))[0];
		 }
		 
		 //Construct condition
		 String filterCondition = "<" + GEOF + "sfWithin>(?fWKT, \"POLYGON((" ;
		 for(int b = 0; b < coordsStr.length; b++)
		  filterCondition += longlat[b][0] + " " + longlat[b][1] + "," ;
		 filterCondition += longlat[0][0] + " " + longlat[0][1];
		 filterCondition += "))\"^^" + "<" + GEO + "wktLiteral>)" ;
		 
		 //Filter for features in polygon
		 graph.addFilter(filterCondition);

		// Execute query
		String result = config.getQueryExecutor(request).execute(LOGD_ENDPOINT_RETURN_JSON, query);
		//System.out.println(result);
		
		// DEBUGGING
		log.debug("Results: " + result);
					
		//Instantiate JSONObjects and JSONArray
		JSONObject resultJSON = null;
		JSONObject results = null;
		JSONArray bindings = null;
		JSONObject bindingsElement = null; //TESTING				//OK
		String value = null;
		JSONObject fWKT = null;
		JSONObject f = null;
		JSONObject fGeom = null;
		JSONObject fName = null;
		
		//Put query result WKTs in resultWKTs String array
		resultJSON = new JSONObject(result); 						//OK
		results = resultJSON.getJSONObject("results"); 				//OK
		bindings = (JSONArray) results.optJSONArray("bindings"); 	//OK
		String[] resultWKTs = new String[bindings.length()];
		
		for (int a = 0; a < bindings.length(); a++) 
		{
			resultWKTs[a] = "";
			bindingsElement = bindings.getJSONObject(a);
			fWKT = bindingsElement.optJSONObject("fWKT");
			f = bindingsElement.optJSONObject("f");
			fGeom = bindingsElement.optJSONObject("fGeom");
			fName = bindingsElement.optJSONObject("fName");
			value = fWKT.getString("value");
			if(value != null)
			 resultWKTs[a] = value;
			
			if(resultWKTs[a].contains("POLYGON"))
			{
				System.out.println("FEATURE " + a + "\n");
				System.out.println("FEATURE URI : " + f.toString() + "\n");
				System.out.println("FEATURE GEOMETRY : " + fGeom.toString() + "\n");
				System.out.println("FEATURE NAME : " + fName.toString() + "\n\n");
				//System.out.println("FEATURE WKT : " + resultWKTs[a] + "\n\n");
			}
		}

		//Return String of WKTs
		String output = "";
		for(int a = 0; a < resultWKTs.length-1; a++)
			output += resultWKTs[a] + "\n";
		output += resultWKTs[resultWKTs.length-1];
		return output;
		//return resultWKTs;
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
		Resource res = config.getResource("squirrel.js");
		ui.addScript(res);
	}

	@Override
	public String getName() {
		return "Squirrel";
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
