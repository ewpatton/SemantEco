package edu.rpi.tw.eScience.WaterQualityPortal.model;

import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;

public class HealthEffectsQuery {
	static String healthOntoPrefix="healtheffect:";
	static HashMap<String, JSONObject> cache=new HashMap<String, JSONObject>();
	
	public static JSONObject queryForHealthEffectsV1(String element, String species, Model model) {
		QueryExecution qe;
		ResultSet queryResults;
		JSONObject effects =null;
		
		String queryString = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
				"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"prefix health: <http://escience.rpi.edu/ontology/semanteco/2/0/health.owl#> "+
				"prefix healtheffect: <http://escience.rpi.edu/ontology/semanteco/2/0/healtheffect.owl#> "+
				"select ?effect ?effectURL " +
				"where {" +
				"?effect healtheffect:forSpecies healtheffect:"+species+"; "+
				"healtheffect:isCausedBy <"+element+">; " +
				"rdfs:seeAlso ?info. " +
				"?info healtheffect:hasURL ?effectURL. } ";
		System.out.println(queryString);
		qe = QueryExecutionFactory.create(QueryFactory.create(queryString, Syntax.syntaxSPARQL_11), model);
		queryResults = qe.execSelect();			
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ResultSetFormatter.outputAsJSON(baos, queryResults);
		try {
			//for debug 
			System.out.println(baos.toString("UTF-8"));
			effects = new JSONObject(baos.toString("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}		
		qe.close();
		return effects;
	}
	

	
	public static JSONObject queryForHealthEffects(String endpoint, String element, String species, Model model) throws UnsupportedEncodingException {
		String elementName=element.substring(element.indexOf('#')+1, element.length());

		String queryString = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
				"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"prefix health: <http://escience.rpi.edu/ontology/semanteco/2/0/health.owl#> "+
				"prefix healtheffect: <http://escience.rpi.edu/ontology/semanteco/2/0/healtheffect.owl#> "+
				"select ?effect ?effectURL " +
				"where { " +
				"graph <http://sparql.tw.rpi.edu/ontology/semanteco/2/0/wildlife-healtheffect.owl> {" +
				"?effect healtheffect:forSpecies healtheffect:"+species+"; "+
				"healtheffect:isCausedBy healtheffect:"+elementName+"; " +
				//"healtheffect:isCausedBy <"+element+">; " +
				"rdfs:seeAlso ?info. " +
				"?info healtheffect:hasURL ?effectURL. }} ";
		System.out.println(queryString);
		String target=endpoint+"?query="+URLEncoder.encode(queryString, "UTF-8")
				+"&format="+URLEncoder.encode("application/json","UTF-8");
		
		JSONObject effects=cache.get(elementName);
		if(effects==null){
			System.out.println("Query from the endpoint for the health effectos for "+element+" and "+species);
			effects=Util.readJsonFromUrl(target);
			cache.put(elementName, effects);
		}			
		
		return effects;
	}
	

	
	
/*	public static String getHealthEffects(JSONObject health){
		String effect="";
		JSONArray bindings = health.getJSONObject("results").getJSONArray("bindings");
		for(int i=0;i<bindings.length();i++) {
			try {
			JSONObject binding  = bindings.getJSONObject(i);
			
			effect = binding.getJSONObject("effect").getString("value");

			}
			catch(Exception e) { }
		}
	}
	*/
}
