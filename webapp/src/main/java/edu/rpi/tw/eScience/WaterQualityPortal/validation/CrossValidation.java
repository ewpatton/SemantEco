package edu.rpi.tw.eScience.WaterQualityPortal.validation;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import edu.rpi.tw.eScience.WaterQualityPortal.WebService.Configuration;
import edu.rpi.tw.eScience.WaterQualityPortal.model.Util;

public class CrossValidation {
	static String polPrefix="http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#";
	static String waterPrefix="http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#";
	static String testTypePrefix1="http://sparql.tw.rpi.edu/source/epa-gov/dataset/echo-measurements-";
	static String testTypePrefix2=	"/typed/test/";
	static String datahost="http://sparql.tw.rpi.edu";
	static HashMap<String, Object> echoStates=new HashMap<String, Object>();
	static Object echoObj= new Object();
	static {
		//"ca", "ma", "ri", "ny"
		echoStates.put("ca", echoObj);
		echoStates.put("ma", echoObj);
		echoStates.put("ny", echoObj);
		echoStates.put("ri", echoObj);
	}

	static public void getCloseSites(HttpExchange req, Map<String,String> params, Logger log){
		//String result = null;
		boolean err = false;
		OutputStream os = req.getResponseBody();
		JSONObject res=null;
		String response=null;
		try {
			String state = params.get("state");
			//String stateFips = params.get("stateFips");
			String county = params.get("county");	
			String delta = params.get("delta");	

			// Query
			long start = System.currentTimeMillis();
			log.debug("Querying CloseSites...");
			res = queryForCloseSites(Configuration.TRIPLE_STORE, state.toLowerCase(), county, delta);
			response = res.toString();
			System.out.println(response);
			req.getResponseHeaders().add("Content-Type", "application/sparql-results+json");
			log.info("Query finished in "+(System.currentTimeMillis()-start)+" ms");
		}
		catch(Exception e) {
			err = true;
			e.printStackTrace();
			response = "An error occurred on the server. Please contact the system administrator.";			
		}		
		try {
			req.sendResponseHeaders(err ? 500 : 200, response.length());			
			os.write(response.getBytes());
		}
		catch(IOException e1) {
			e1.printStackTrace();
		}
		finally {
			try {
				os.flush();
				os.close();
			}
			catch(Exception e) {}
		}		
	}

	protected static JSONObject queryForCloseSites(String endpoint, String state, String county, String delta){
		String queryStr ="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"+
				"prefix pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#>\r\n" +
				"prefix water: <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#>\r\n" + 
				"prefix wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#>\r\n" +
				"prefix dc: <http://purl.org/dc/terms/> " +
				"SELECT DISTINCT ?fac ?siteId ?epaPermit \r\n"+
				"from <http://sparql.tw.rpi.edu/source/epa-gov/dataset/echo-facilities-"+state+"/version/2011-Mar-19>\r\n"+
				"from <http://sparql.tw.rpi.edu/source/usgs-gov/dataset/nwis-sites-"+state+"/version/2011-Mar-20>\r\n"+
				"WHERE {\r\n"+
				"?fac rdf:type water:WaterFacility . \r\n"+
				"?fac pol:hasPermit ?epaPermit . \r\n"+
				"?fac pol:hasCountyCode \"" + Util.makeCountyID(state.toUpperCase(), county) + "\". \r\n"+
				"?fac wgs:lat ?facLat.\r\n"+
				"?fac wgs:long ?facLong.\r\n"+
				"?site rdf:type water:WaterSite . \r\n"+
				"?site dc:identifier ?siteId . \r\n"+
				//"?site pol:hasCountyCode \"" + county + "\". \r\n"+ 
				"?site wgs:lat ?siteLat.\r\n"+
				"?site wgs:long ?siteLong.\r\n"+
				"FILTER ( ?facLat < (?siteLat+"+delta+") && ?facLat > (?siteLat-"+delta+") && ?facLong < (?siteLong+"+delta+") && ?facLong > (?siteLong-"+delta+"))\r\n"+
				"} offset 600 limit 300 \r\n";

		System.out.println(queryStr);	
		JSONObject sites=Util.queryEndpoint(endpoint, queryStr);
		return purgeSites(endpoint, state, sites);
	}
	
	static public void getMeasurements(HttpExchange req, Map<String,String> params, Logger log){
		boolean err = false;
		OutputStream os = req.getResponseBody();
		JSONObject res=null;
		String response=null;
		try {
			//state, permit, element
			String state = params.get("state");	
			String element = params.get("element");
			String testType = params.get("testType");
			String sitePair = params.get("sitePair");
			// Query
			long start = System.currentTimeMillis();
			log.debug("Querying Measurements...");
			res = queryForMeasurements(Configuration.TRIPLE_STORE, 
					state.toLowerCase(), sitePair, element, testType);
			response = res.toString();
			System.out.println(response);
			req.getResponseHeaders().add("Content-Type", "application/sparql-results+json");
			log.info("Query finished in "+(System.currentTimeMillis()-start)+" ms");
		}
		catch(Exception e) {
			err = true;
			e.printStackTrace();
			response = "An error occurred on the server. Please contact the system administrator.";			
		}		
		try {
			req.sendResponseHeaders(err ? 500 : 200, response.length());			
			os.write(response.getBytes());
		}
		catch(IOException e1) {
			e1.printStackTrace();
		}
		finally {
			try {
				os.flush();
				os.close();
			}
			catch(Exception e) {}
		}		
	}

	protected static JSONObject queryForMeasurements(String endpoint, 
			String state, String sitePair, String element, String testType){		
		JSONObject res=new JSONObject();
		
		try {
			String[] parts = sitePair.split(",");
			if(parts.length!=2)
				throw new Exception();
			String epaPermit = parts[0].trim();
			String usgsSiteId = parts[1].trim();

			JSONObject epa=queryForEPAMeasurements(endpoint, 
					state, epaPermit, element, testType);
			JSONObject usgs= queryForUSGSMeasurements(endpoint, state, 
					usgsSiteId, element);
			res.put("epa", epa);
			res.put("usgs", usgs);

		} catch (Exception e) {
			System.err.println("In queryForMeasurements, "+e.getMessage());
			e.printStackTrace();
		}
		return res;		
	}

	protected static JSONObject purgeSites(String endpoint, String state, JSONObject sites){	
		JSONObject processed=new JSONObject();
		JSONObject res=new JSONObject();

		try {
			JSONArray bindings = sites.getJSONObject("results").getJSONArray("bindings");
			JSONArray pairs = new JSONArray();
			for(int i=0;i<bindings.length();i++) {
				JSONObject fac = bindings.getJSONObject(i).getJSONObject("fac");
				String facValue = fac.getString("value");
				JSONObject siteIdUri = bindings.getJSONObject(i).getJSONObject("siteId");
				String siteIdUriValue = siteIdUri.getString("value");
				//System.err.println(siteIdUri);
				if(facValue.endsWith("facility-"))
					continue;
				//permit: http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#FacilityPermit-RIR600121
				JSONObject epaPermit = bindings.getJSONObject(i).getJSONObject("epaPermit");
				String epaPermitUri = epaPermit.getString("value");	
				String epaPermitStr = epaPermitUri.substring(epaPermitUri.indexOf('#')+1);
				//String epaFacId = facValue.substring(facValue.indexOf('#')+1);
				String usgsSiteId = siteIdUriValue.substring(siteIdUriValue.indexOf('#')+1);
				JSONObject elements=queryForCommonElements(endpoint, state, epaPermitStr, usgsSiteId);
				if(Util.isEmpty(elements)) 					continue;
				//fac.put("value", epaFacId);
				epaPermit.put("value", epaPermitStr);
				siteIdUri.put("value", usgsSiteId);
				JSONObject inst=new JSONObject();
				//inst.put("fac", fac);
				inst.put("fac", epaPermit);
				inst.put("site", siteIdUri);
				pairs.put(inst);				
			}
			res.put("bindings", pairs);
			processed.put("results", res);

		} catch (JSONException e) {
			System.err.println("In removeInvalidData, " + e.getMessage());
			e.printStackTrace();
		}
		return processed;		
	}

	static public void getCommonElements(HttpExchange req, Map<String,String> params, Logger log){
		//String result = null;
		boolean err = false;
		OutputStream os = req.getResponseBody();
		JSONObject res=null;
		String response=null;
		try {
			//String endpoint, String state, String epaPermit, String usgsSiteId)
			String state = params.get("state");	
			String sitePair = params.get("sitePair");
			System.err.println("sitePair: "+ sitePair);
			String[] parts = sitePair.split(",");
			if(parts.length!=2)
				throw new Exception();			
			String epaPermit = parts[0].trim();
			String usgsSiteId = parts[1].trim();				
			// Query
			long start = System.currentTimeMillis();
			log.debug("Querying Common Char...");
			res = queryForCommonElements(Configuration.TRIPLE_STORE, state.toLowerCase(), epaPermit, usgsSiteId);
			response = res.toString();
			System.out.println(response);
			req.getResponseHeaders().add("Content-Type", "application/sparql-results+json");
			log.info("Query finished in "+(System.currentTimeMillis()-start)+" ms");
		}
		catch(Exception e) {
			err = true;
			e.printStackTrace();
			response = "An error occurred on the server. Please contact the system administrator.";			
		}		
		try {
			req.sendResponseHeaders(err ? 500 : 200, response.length());			
			os.write(response.getBytes());
		}
		catch(IOException e1) {
			e1.printStackTrace();
		}
		finally {
			try {
				os.flush();
				os.close();
			}
			catch(Exception e) {}
		}		
	}

	protected static JSONObject queryForCommonElements(String endpoint, String state, 
			String epaPermit, String usgsSiteId){
		//String epaFacUri=polPrefix+epaFacId;
		String epaPermitUri=polPrefix+epaPermit;
		String usgsSiteUri=waterPrefix+usgsSiteId;

		String queryStr = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"+
				"prefix pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#>\r\n" +
				"prefix water: <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#>\r\n" + 
				"SELECT DISTINCT ?element\r\n"+
				"from <http://sparql.tw.rpi.edu/source/epa-gov/dataset/echo-measurements-"+state+"/version/2011-Mar-19>\r\n"+
				"from <http://sparql.tw.rpi.edu/source/usgs-gov/dataset/nwis-measurements-"+state+"/version/2011-Mar-20>\r\n"+
				"WHERE {\r\n"+
				//"<"+epaFacUri+"> pol:hasPermit ?permit . \r\n"+
				//"?epaMeasure pol:hasPermit ?permit . \r\n"+
				"?epaMeasure pol:hasPermit <" + epaPermitUri + "> . \r\n"+    
				"?epaMeasure pol:hasCharacteristic ?element . \r\n"+
				//usgsSiteUri: http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#WaterSite-USGS-420027071281901
				//e.g. http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#USGS-01115670
				"?usgsMeasure pol:hasSiteId <"+usgsSiteUri+"> . \r\n"+
				"?usgsMeasure pol:hasCharacteristic ?element .\r\n"+
				"}";

		System.out.println(queryStr);
		JSONObject elements=Util.queryEndpoint(endpoint, queryStr);
		//System.err.println(elements.toString());
		return procElements(elements);
	}

	protected static void procUris(JSONObject uris, String itemName, String delimiter){	
		try {
			JSONArray bindings = uris.getJSONObject("results").getJSONArray("bindings");
			for(int i=0;i<bindings.length();i++) {
				JSONObject item = bindings.getJSONObject(i).getJSONObject(itemName);
				String itemUri = item.getString("value");
				String itemStr = itemUri.substring(itemUri.lastIndexOf(delimiter)+1);
				item.put("value", itemStr);
			}
		} catch (JSONException e) {
			System.err.println("In procUris, " + e.getMessage());
			e.printStackTrace();
		}
	}

	protected static JSONObject procElements(JSONObject elements){	
		/*JSONObject processed=new JSONObject();
		JSONObject res=new JSONObject();*/

		try {
			JSONArray bindings = elements.getJSONObject("results").getJSONArray("bindings");
			//JSONArray proc = new JSONArray();
			for(int i=0;i<bindings.length();i++) {
				JSONObject element = bindings.getJSONObject(i).getJSONObject("element");
				String elementUri = element.getString("value");
				String elementStr = elementUri.substring(elementUri.indexOf('#')+1);
				element.put("value", elementStr);
				/*JSONObject inst=new JSONObject();
				inst.put("element", element);
				proc.put(inst);			*/	
			}
			//res.put("bindings", proc);
			//processed.put("results", res);

		} catch (JSONException e) {
			System.err.println("In removeInvalidData, " + e.getMessage());
			e.printStackTrace();
		}
		return elements;		
	}

	@SuppressWarnings("unused")
	static public void getTestTypes(HttpExchange req, Map<String,String> params, Logger log){
		boolean err = false;
		OutputStream os = req.getResponseBody();
		JSONObject res=null;
		String response=null;
		try {
			//state, permit, element
			String state = params.get("state");	
			String element = params.get("element");
			String sitePair = params.get("sitePair");
			System.err.println("sitePair: "+ sitePair);
			String[] parts = sitePair.split(",");
			if(parts.length!=2)
				throw new Exception();			
			String epaPermit = parts[0].trim();
			String usgsSiteId = parts[1].trim();				
			// Query
			long start = System.currentTimeMillis();
			log.debug("Querying Common Char...");
			res = queryForTestTypes(Configuration.TRIPLE_STORE, state.toLowerCase(), epaPermit, element);
			response = res.toString();
			System.out.println(response);
			req.getResponseHeaders().add("Content-Type", "application/sparql-results+json");
			log.info("Query finished in "+(System.currentTimeMillis()-start)+" ms");
		}
		catch(Exception e) {
			err = true;
			e.printStackTrace();
			response = "An error occurred on the server. Please contact the system administrator.";			
		}		
		try {
			req.sendResponseHeaders(err ? 500 : 200, response.length());			
			os.write(response.getBytes());
		}
		catch(IOException e1) {
			e1.printStackTrace();
		}
		finally {
			try {
				os.flush();
				os.close();
			}
			catch(Exception e) {}
		}		
	}

	protected static JSONObject queryForTestTypes(String endpoint, String state, String permit, String element){
		String queryStr = genEPATestTypeQuery(state, permit, element);

		System.out.println(queryStr);	
		JSONObject types=Util.queryEndpoint(endpoint, queryStr);
		procUris(types, "testType", "/");
		return types;
	}

	private static boolean isEcho(String state){
		return (echoStates.get(state.toLowerCase())!=null);		
	}

	private static String genEPATestTypeQuery(String stateAbbr, String permit, String element){
		String sparqlEPATestType="";
		String EPADataset="foia";
		String EPADataVersion="2011-Jul-23";
		String epaPermitUri=polPrefix+permit;
		String elementUri=polPrefix+element;

		if(!isEcho(stateAbbr))//"foia"
		{
			sparqlEPATestType+="PREFIX water: <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#>\r\n";
		}
		else//echo
		{
			EPADataset="echo";
			EPADataVersion="2011-Mar-19";
			sparqlEPATestType+="PREFIX e1: <"+datahost+"/source/epa-gov/dataset/echo-measurements-"+stateAbbr+"/vocab/enhancement/1/>\r\n";
		}
		//the common body
		sparqlEPATestType+="PREFIX pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#>\r\n"+
				"SELECT DISTINCT ?testType\r\n"+
				"WHERE {\r\n"+
				"graph <http://sparql.tw.rpi.edu/source/epa-gov/dataset/"+EPADataset+"-measurements-"+stateAbbr+"/version/"+EPADataVersion+">\r\n"+
				"{\r\n"+
				"?measure pol:hasPermit <"+epaPermitUri+"> .\r\n"+
				"?measure pol:hasCharacteristic <" + elementUri + "> .\r\n";
		//the final
		if(!isEcho(stateAbbr))//"foia"
			sparqlEPATestType+="?measure water:hasValueTypeCode ?testType\r\n"+"}}";
		else//echo
			sparqlEPATestType+="?measure e1:test_type ?testType\r\n"+"}}";

		return sparqlEPATestType;
	}

	protected static JSONObject queryForUSGSMeasurements(String endpoint, String state, 
			String usgsSiteId, String element){
		String usgsSiteUri=waterPrefix+usgsSiteId;
		String elementUri=polPrefix+element;

		String queryStr = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"+
				"PREFIX pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#>\r\n"+
				"PREFIX water: <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#>\r\n"+
				"PREFIX repr: <http://sweet.jpl.nasa.gov/2.1/repr.owl#>\r\n"+
				"PREFIX time: <http://www.w3.org/2006/time#>\r\n"+
				"\r\n"+
				"SELECT DISTINCT ?date ?value ?unit\r\n"+
				"WHERE {\r\n"+
				"graph <http://sparql.tw.rpi.edu/source/usgs-gov/dataset/nwis-measurements-"+state+"/version/2011-Mar-20>\r\n"+
				"{\r\n"+
				"?measure rdf:type water:WaterMeasurement .\r\n"+
				"?measure pol:hasSiteId <" + usgsSiteUri + "> .\r\n"+
				"?measure pol:hasCharacteristic <" + elementUri + "> .\r\n"+
				"?measure time:inXSDDateTime ?date .\r\n"+
				"?measure pol:hasValue ?value .\r\n"+
				"?measure repr:hasUnit ?unit .\r\n"+
				"}} ORDER BY ?date";

		System.out.println(queryStr);
		JSONObject measurements=Util.queryEndpoint(endpoint, queryStr);
		//System.err.println(measurements.toString());
		return measurements;
	}

	protected static JSONObject queryForEPAMeasurements(String endpoint, 
			String state, String permit, String element, String testType){
		String queryStr = genEPAMeasurementsQuery(state, permit, element, testType);
		System.out.println(queryStr);	
		JSONObject measurements=Util.queryEndpoint(endpoint, queryStr);
		return measurements;
	}

	protected static String genEPAMeasurementsQuery(String stateAbbr, 
			String permit, String element, String testType){
		String sparqlEPAMeasurements="";
		String EPADataset="foia";
		String EPADataVersion="2011-Jul-23";
		String epaPermitUri=polPrefix+permit;
		String elementUri=polPrefix+element;
		String testTypeUri=testTypePrefix1+stateAbbr+testTypePrefix2+testType;

		if(!isEcho(stateAbbr))//"foia"
		{
			sparqlEPAMeasurements+="PREFIX water: <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#>\r\n";
		}
		else//echo
		{
			EPADataset="echo";
			EPADataVersion="2011-Mar-19";
			sparqlEPAMeasurements+="PREFIX e1: <"+datahost+"/source/epa-gov/dataset/echo-measurements-"+stateAbbr+"/vocab/enhancement/1/>\r\n";
		}

		//the common body
		sparqlEPAMeasurements+="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"+        
				"PREFIX dcterms: <http://purl.org/dc/terms/>\r\n"+ 
				"PREFIX pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#>\r\n"+
				"PREFIX repr: <http://sweet.jpl.nasa.gov/2.1/repr.owl#>\r\n"+
				"SELECT DISTINCT ?date ?value ?unit\r\n"+ 
				"WHERE {\r\n"+
				"graph <http://sparql.tw.rpi.edu/source/epa-gov/dataset/"+EPADataset+"-measurements-"+stateAbbr+"/version/"+EPADataVersion+">\r\n"+
				"{\r\n"+
				"?measure pol:hasPermit <" + epaPermitUri + "> .\r\n"+
				"?measure pol:hasCharacteristic <" + elementUri + "> .\r\n"+
				"?measure dcterms:date ?date .\r\n";
		//the final
		if(!isEcho(stateAbbr))//"foia"
			sparqlEPAMeasurements+="?measure repr:hasUnit ?unitURI .\r\n"+
					"?unitURI rdfs:label ?unit .\r\n"+
					"?measure pol:hasValue ?value .\r\n"+
					"?measure water:hasValueTypeCode \""+testTypeUri+"\" .}} ORDER BY ?date\r\n";
		else//echo
			sparqlEPAMeasurements+= "?measure repr:hasUnit ?unit .\r\n"+
					"?measure rdf:value ?value .\r\n"+
					"?measure e1:test_type <"+testTypeUri+"> .}} ORDER BY ?date\r\n";

		return sparqlEPAMeasurements;
	}


	@SuppressWarnings("unused")
	public static void main(String[] args){
		CrossValidation inst = new CrossValidation();
		String state="ri";
		String epaFacId="facility-110000772085";
		String usgsSiteId="WaterSite-USGS-01114500";
		String permit="http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#FacilityPermit-RI0020401";
		/*http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#FacilityPermit-RI0023779
http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#FacilityPermit-RIR130007
http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#FacilityPermit-RI0020401*/
		CrossValidation.queryForCommonElements(Configuration.TRIPLE_STORE, state, permit, usgsSiteId);
	}

}
