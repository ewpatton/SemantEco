package edu.rpi.tw.escience.WaterQualityPortal.species;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.*;

/*import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;*/

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.sun.net.httpserver.HttpExchange;

import edu.rpi.tw.escience.WaterQualityPortal.WebService.Configuration;
import edu.rpi.tw.escience.WaterQualityPortal.WebService.WaterAgentInstance;

public class BirdObservation {
	//FipsCodeAgent fipsAgent=null;
	static String fipsFileDir="data/fips/";
	static String fipsFilePost="-county-code.txt";
	//String stateFips=null;
	HashMap<String, Integer> fips2Count=null;
	HashMap<String, FipsCodeAgent> fipsAgents=null;

	public BirdObservation(String stateFips, String fipsFile){
		fipsAgents = new HashMap<String, FipsCodeAgent>();
		//fipsAgent = new FipsCodeAgent(fipsFile);	
		//this.stateFips=stateFips;
		fips2Count = new HashMap<String, Integer>();
	}

	private void initCountTable(String stateFips){
		FipsCodeAgent agent=fipsAgents.get(stateFips);
		if(agent==null){
			String fipsFile=fipsFileDir+stateFips+fipsFilePost;
			agent = new FipsCodeAgent(fipsFile);	
			fipsAgents.put(stateFips, agent);
		}		
		fips2Count.clear();
		Iterator<String> it = agent.getCountyFips().iterator();
		while(it.hasNext()) { 
			String ctyFips = it.next(); 
			fips2Count.put(stateFips+ctyFips, 0);
		}
	}

	public void getSpeciesDistributionByCounty(HttpExchange req, Map<String,String> params, Logger log){
		//String result = null;
		boolean err = false;
		OutputStream os = req.getResponseBody();
		org.json.JSONObject res=null;
		String response=null;
		try {
			String usState = params.get("state");
			//remove the quotations
			usState=usState.substring(1, usState.length()-1);
			String stateCode=params.get("stateCode");
			stateCode=stateCode.substring(1, stateCode.length()-1);	
			String spcCommonName = params.get("comName");
			//remove the quotations
			spcCommonName=spcCommonName.substring(1, spcCommonName.length()-1);	
			String year = params.get("year");	
			String month = params.get("month");	
			if(month.length()==1)
				month="0"+month;
			@SuppressWarnings("unused")
			Model model = ModelFactory.createDefaultModel();

			// Query
			long start = System.currentTimeMillis();
			log.debug("Querying SpeciesDistributionByCounty...");
			String queryString;

			queryString ="PREFIX geospecies: <http://rdf.geospecies.org/ont/geospecies#> "  +
					"PREFIX wildlife: <http://www.semanticweb.org/ontologies/2012/2/wildlife.owl#> "  +
					"SELECT ?ctyName SUM(?count) as ?total "  +
					"WHERE {graph <http://sparql.tw.rpi.edu/source/akn/dataset/bird-observations/version/2012-Apr-05>{ "  +
					"?obv wildlife:hasState \""+usState+"\"; "  +
					" wildlife:hasCounty ?ctyName ; "  +
					" geospecies:hasCommonName \""+spcCommonName+"\"; "  +
					" wildlife:hasYearCollected \""+year+"\"; "  +
					" wildlife:hasMonthCollected \""+month+"\"; "  +
					" wildlife:hasObservationCount ?count. "  +
					"}} GROUP BY ?ctyName";

			System.out.println(queryString);
			res = WaterAgentInstance.executeJSONQuery(Configuration.TRIPLE_STORE, queryString);
			getFipsForBirdCount(stateCode, res);
			response=outputBirdCountJson().toString();
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

	public JSONObject outputBirdCountJson(){
		JSONObject obj = new JSONObject();
		Iterator<Map.Entry<String, Integer>> it = fips2Count.entrySet().iterator();
		try {
			while (it.hasNext()) {
				Map.Entry<String, Integer> curEntry = (Map.Entry<String, Integer>)it.next();
				String fips = curEntry.getKey();
				Integer count = curEntry.getValue();	
				obj.put(fips, count);

			}}
		catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}

	public void writeBirdCountJson(String outputFile){
		BufferedWriter bufferedWriter = null;		
		JSONObject obj = new JSONObject();
		Iterator<Map.Entry<String, Integer>> it = fips2Count.entrySet().iterator();
		try {
			while (it.hasNext()) {
				Map.Entry<String, Integer> curEntry = (Map.Entry<String, Integer>)it.next();
				String fips = curEntry.getKey();
				Integer count = curEntry.getValue();	
				obj.put(fips, count);

			}}
		catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
			bufferedWriter.write(obj.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try{
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			}catch (IOException ex) {
				System.err.println("In writeBirdCountJson, closing the reader and BufferedWriter");
				ex.printStackTrace();
			}
		}		
	}


	public void getFipsForBirdCount(String stateFips, JSONObject input) {
		initCountTable(stateFips); 
		FipsCodeAgent agent=fipsAgents.get(stateFips);
		if(agent==null){
			System.err.println("In getFipsForBirdCount, no fips agent for the state: "+stateFips);
			return;
		}
		
		try {
			JSONObject results = (JSONObject) input.get("results");	
			JSONArray bindings = (JSONArray) results.get("bindings");
			for(int i=0;i<bindings.length();i++) {
				JSONObject curBinding = bindings.getJSONObject(i);
				String ctyName = (String)((JSONObject) curBinding.get("ctyName")).get("value");
				String total = (String)((JSONObject) curBinding.get("total")).get("value");
				String ctyFips=agent.name2Code(ctyName);
				fips2Count.put(stateFips+ctyFips, Integer.parseInt(total));
			}} catch (JSONException e) {
				e.printStackTrace();
			}	
	}


	/*	public void getFipsForBirdCount(String input){
		JSONParser parser = new JSONParser();
		try {	 
			Object obj = parser.parse(new FileReader(input));	 
			JSONObject jsonObject = (JSONObject) obj;	 
			getFipsForBirdCount(jsonObject);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}	 
	}*/

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		String stateFips="53";
		String fipsFile="./53-county-code.txt";
		BirdObservation inst=new BirdObservation(stateFips, fipsFile);
		String input = "data/bird/WA-200706-Canada-Goose-count.json";
		String output = "data/bird/WA-200706-Canada-Goose-count-fips.json";		
		//inst.getFipsForBirdCount(input);
		//inst.writeBirdCountJson(output);
	}
}
