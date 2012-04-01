package edu.rpi.tw.eScience.WaterQualityPortal.species;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.*;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.sun.net.httpserver.HttpExchange;

import edu.rpi.tw.eScience.WaterQualityPortal.WebService.Configuration;
import edu.rpi.tw.eScience.WaterQualityPortal.WebService.WaterAgentInstance;
import edu.rpi.tw.eScience.WaterQualityPortal.epa.foia.FoiaUtil;
import edu.rpi.tw.eScience.WaterQualityPortal.zip.GeonameIdLookup;

import java.io.FileReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WaterEntityAgent {
	static String hucFile = "./wa_huc_0.001.json";

	static HashMap<String, HashSet<String>> countyCode2huc = new HashMap<String, HashSet<String>>();
	
	static{
		WaterEntityAgent.buildHUCLookup();
	}
	
	static public void getHUC8Codes(HttpExchange req, Map<String,String> params, Logger log){
		boolean err = false;
		OutputStream os = req.getResponseBody();
		String response=null;
		try {
			String fips = params.get("fips");
			JSONObject hucObj= getHUC(fips);		
			response = hucObj.toString();
			//System.out.println(response);
			req.getResponseHeaders().add("Content-Type", "application/sparql-results+json");
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

	static public void getHUC8CodesOneState(HttpExchange req, Map<String,String> params, Logger log){
		boolean err = false;
		OutputStream os = req.getResponseBody();
		String response=null;
		try {
			String state = params.get("state");
			JSONObject hucObj= getHUCOneState(state);		
			response = hucObj.toString();
			//System.out.println(response);
			req.getResponseHeaders().add("Content-Type", "application/sparql-results+json");
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
	
	static public void buildHUCLookup(){
		JSONParser parser = new JSONParser();
		 
		try {
	 
			Object obj = parser.parse(new FileReader(hucFile));	 
			JSONObject jsonObject = (JSONObject) obj;	 
			String ftCollection = (String) jsonObject.get("FeatureCollection");			
			// loop array
			JSONArray features = (JSONArray) jsonObject.get("features");
			Iterator<JSONObject> iterator = features.iterator();
			while (iterator.hasNext()) {
				//System.out.println(iterator.next());
				//Feature
				JSONObject curFeature = iterator.next();
				JSONObject prop = (JSONObject) curFeature.get("properties");
				String huc8 = (String) prop.get("HUC_8");
				String fipsCodeList = (String) prop.get("FIPS_C");
				//System.out.println(huc8);
				//System.out.println(fipsCodeList);						
				String[] fipsCodeArr = fipsCodeList.split(" ");
				for(String curFips:fipsCodeArr){
					HashSet<String> hucSet = countyCode2huc.get(curFips);
					if(hucSet !=null){
						hucSet.add(huc8);
					}
					else{
						hucSet = new HashSet<String>();
						hucSet.add(huc8);
						countyCode2huc.put(curFips, hucSet);
					}
				}				
			}			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}	 
	}
	
	static public JSONObject getHUC(String fipsCode){
		System.out.println("getHUC for "+fipsCode);
		JSONObject obj = new JSONObject();
		
		JSONArray hucArr = null;		
		HashSet<String> hucSet = countyCode2huc.get(fipsCode);
		if(hucSet!=null){
			hucArr = new JSONArray();
			for(String curHuc:hucSet)
				hucArr.add(curHuc);		
		}	
		System.out.println("hucArr "+hucArr);
		obj.put("HUC_8", hucArr);
		
		return obj;		
	}
	
	static public JSONObject getHUCOneState(String curState){    
		JSONObject obj = new JSONObject();
		Iterator<String> iteratorKey = countyCode2huc.keySet().iterator();
		HashSet<String> hucSet = new HashSet<String>();
		
		while(iteratorKey.hasNext()){     
			String key=iteratorKey.next();
			System.out.println(key);
			if(key.startsWith(curState)){
				HashSet<String> curHucSet = countyCode2huc.get(key);
				hucSet.addAll(curHucSet);
			}
		}   
		
		JSONArray hucArr = new JSONArray();
		for(String curHuc:hucSet)
			hucArr.add(curHuc);	
		obj.put("HUC_8", hucArr);
		return obj;		
	}
	static public void printHashMap(HashMap<String, HashSet<String>> curMap){
		System.out.println("The HashMap:");        
		Iterator<String> iteratorKey = curMap.keySet().iterator();

		while(iteratorKey. hasNext()){     
			String key=iteratorKey.next();
			System.out.println(key+", "+curMap.get(key));
		}       
	}
	
	public static void main(String[] args) {
		WaterEntityAgent.buildHUCLookup();
		WaterEntityAgent.printHashMap(WaterEntityAgent.countyCode2huc);
		//JSONObject hucObj=WaterEntityAgent.getHUC("41059");
		JSONObject hucObj=WaterEntityAgent.getHUCOneState("53");
		System.out.println(hucObj.toString());
	}
}

