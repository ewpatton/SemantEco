package edu.rpi.tw.eScience.WaterQualityPortal.species;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.sun.net.httpserver.HttpExchange;

import edu.rpi.tw.eScience.WaterQualityPortal.WebService.Configuration;
import edu.rpi.tw.eScience.WaterQualityPortal.WebService.WaterAgentInstance;
import edu.rpi.tw.eScience.WaterQualityPortal.zip.GeonameIdLookup;

public class DistributionWebService {
	
	static public void getSpeciesNames(HttpExchange req, Map<String,String> params, Logger log){
		//String result = null;
		boolean err = false;
		OutputStream os = req.getResponseBody();
		JSONObject res=null;
		String response=null;
		try {
			String stateAbbr = params.get("state");
			String spcClass = params.get("spcClass");
			Model model = ModelFactory.createDefaultModel();			
			// Query
			long start = System.currentTimeMillis();
			log.debug("getSpeciesNames...");
			String queryString;

			queryString ="PREFIX geospecies:  <http://rdf.geospecies.org/ont/geospecies#> "  +
					"PREFIX wildlife:        <http://www.semanticweb.org/ontologies/2012/2/wildlife.owl#> "  +
					"SELECT DISTINCT ?name "  +
					"WHERE {graph <http://sparql.tw.rpi.edu/source/wdfw-wa-gov/dataset/species-distribution-by-county/version/2012-Mar-23>{ "  +
					"?obv wildlife:hasStateAbbr \""+stateAbbr+"\"; "  +
					" wildlife:hasSpeciesClassName \""+spcClass+"\"; "  +
					" wildlife:hasSpeciesName ?name. "  +
					"}} order by ?name";

			
			//System.out.println(queryString);
			res = WaterAgentInstance.executeJSONQuery(Configuration.TRIPLE_STORE, queryString);
			response = res.toString();
			//System.out.println(response);
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
	
	static public void getSpeciesDistributionByCounty(HttpExchange req, Map<String,String> params, Logger log){
		//String result = null;
		boolean err = false;
		OutputStream os = req.getResponseBody();
		JSONObject res=null;
		String response=null;
		try {
			String stateAbbr = params.get("state");
			String spcClass = params.get("spcClass");	
			String spcName = params.get("spcName");	
			Model model = ModelFactory.createDefaultModel();
			
			// Query
			long start = System.currentTimeMillis();
			log.debug("Querying SpeciesDistributionByCounty...");
			String queryString;
			
			queryString ="PREFIX geospecies:  <http://rdf.geospecies.org/ont/geospecies#> "  +
					"PREFIX wildlife:        <http://www.semanticweb.org/ontologies/2012/2/wildlife.owl#> "  +
					"SELECT DISTINCT ?ctyName "  +
					"WHERE {graph <http://sparql.tw.rpi.edu/source/wdfw-wa-gov/dataset/species-distribution-by-county/version/2012-Mar-23>{ "  +
					"?obv wildlife:hasStateAbbr \""+stateAbbr+"\"; "  +
					" wildlife:hasSpeciesClassName \""+spcClass+"\"; "  +
					" wildlife:hasSpeciesName \""+spcName+"\"; "  +
					" wildlife:hasCountyName ?ctyName. "  +
					"}} order by ?ctyName";
			
			System.out.println(queryString);
			res = WaterAgentInstance.executeJSONQuery(Configuration.TRIPLE_STORE, queryString);
			response = res.toString();
			//System.out.println(response);
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
	
	static public void getSpeciesDistributionByCountyFromGeospecies(HttpExchange req, Map<String,String> params, Logger log){
		//String result = null;
		boolean err = false;
		OutputStream os = req.getResponseBody();
		JSONObject res=null;
		String response=null;
		try {
			String stateAbbr = params.get("state");
			String stateGeoNameId=GeonameIdLookup.execute(stateAbbr);			
			Model model = ModelFactory.createDefaultModel();
			
			// Query
			long start = System.currentTimeMillis();
			log.debug("Querying SpeciesDistributionByCounty...");
			String queryString;
			//QueryExecution qe;
			//ResultSet queryResults;
			
			queryString = "PREFIX geospecies:  <http://rdf.geospecies.org/ont/geospecies#> "  +
					"PREFIX "+stateAbbr+": <http://sws.geonames.org/"+stateGeoNameId+"/> " +
					"SELECT DISTINCT ?countyName "+
					"WHERE {graph <http://sparql.tw.rpi.edu/source/geospecies-org/dataset/geospecies/version/2010-Apr-11>{ "+
					" ?x geospecies:hasCommonName ?commonName; "+
					"    geospecies:hasObservation ?obv; "+
					"geospecies:isExpectedIn "+stateAbbr+":. "+
					" ?obv geospecies:hasCountyName ?countyName. "+
					"}} order by asc(?countyName)";
			
			System.out.println(queryString);
			res = WaterAgentInstance.executeJSONQuery(Configuration.TRIPLE_STORE, queryString);
			response = res.toString();
			System.out.println(response);
/*			JSONArray arr = graphs.getJSONObject("results").getJSONArray("bindings");
			ArrayList<String> list = new ArrayList<String>();
			for(int j=0;j<arr.length();j++) {
				list.add(arr.getJSONObject(j).getJSONObject("countyName").getString("value"));
			}
			System.out.print(list);*/
			req.getResponseHeaders().add("Content-Type", "application/sparql-results+json");
			//result = res.toString("UTF-8");
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
}
