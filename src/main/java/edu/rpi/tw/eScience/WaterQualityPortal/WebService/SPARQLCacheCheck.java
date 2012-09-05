package edu.rpi.tw.eScience.WaterQualityPortal.WebService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import edu.rpi.tw.eScience.WaterQualityPortal.WebService.Cache.CacheStalenessCheck;

public class SPARQLCacheCheck implements CacheStalenessCheck {
	
	TreeMap<String, Calendar> graphs = new TreeMap<String, Calendar>();
	static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	String endpoint;
	Calendar modifiedDate = null;
	Calendar expirationDate = Calendar.getInstance();

	protected String subquery(Map.Entry<String, Calendar> e) {
		return "{ graph <"+e.getKey()+"> { [] pmlj:hasConclusion [ skos:broader "+
			"[ sd:name <"+e.getKey()+"> ] ] ; pmlj:isConsequentOf [ dc:date ?when ] ."+
			"filter(?when > \""+format.format(e.getValue())+"\"^^xsd:dateTime) } }";
	}
	
	protected String datequery(Map.Entry<String, Calendar> e) {
		return "{ graph <"+e.getKey()+"> { [] pmlj:hasConclusion [ skos:broader "+
			"[ sd:name <"+e.getKey()+"> ] ] ; pmlj:isConsequentOf [ dc:date ?when ] } }";
	}
	
	@Override
	public String isStale(String uri, Calendar modified) throws IOException {
		// Construct query string:
		if(graphs.size()==0) return null;
		String check = 
			"prefix dc: <http://purl.org/dc/terms/> "+
			"prefix sd: <http://www.w3.org/ns/sparql-service-description#> "+
			"prefix sioc: <http://rdfs.org/sioc/ns#> "+
			"prefix skos: <http://www.w3.org/2004/02/skos/core#> "+
			"prefix pmlj: <http://inference-web.org/2.0/pml-justification.owl#> "+
			"prefix hartigprov: <http://purl.org/net/provenance/ns#> "+
			"prefix conversion: <http://purl.org/twc/vocab/conversion/> "+
			"prefix xsd: <http://www.w3.org/2001/XMLSchema#> "+
			"ask {";
		boolean first=true;
		for(Entry<String, Calendar> e : graphs.entrySet()) {
			if(!first)
				check += " union ";
			else
				first = false;
			check += subquery(e);
		}
		check += "}";
		
		// Execute query
		URL url = new URL(endpoint+"?default-graph-uri=&should-sponge=&format=application%2Fsparql-results%2Bjson&debug=on&query="+java.net.URLEncoder.encode(check, "ASCII"));
		URLConnection conn = url.openConnection();
		conn.connect();
		InputStream is = conn.getInputStream();
		int i;
		String response = "";
		while((i=is.read())!=-1) {
			response += Character.toString((char)i);
		}
		is.close();
		
		// Check results
		if(response.equalsIgnoreCase("true")) {
			// Update dates of graphs
			check =
				"prefix dc: <http://purl.org/dc/terms/>"+
				"prefix sd: <http://www.w3.org/ns/sparql-service-description#>"+
				"prefix sioc: <http://rdfs.org/sioc/ns#>"+
				"prefix skos: <http://www.w3.org/2004/02/skos/core#>"+
				"prefix pmlj: <http://inference-web.org/2.0/pml-justification.owl#>"+
				"prefix hartigprov: <http://purl.org/net/provenance/ns#>"+
				"prefix conversion: <http://purl.org/twc/vocab/conversion/>"+
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#>"+
				"select (max(?when) as ?modified) where { ";
			first = true;
			for(Entry<String, Calendar> e : graphs.entrySet()) {
				if(!first)
					check += " union ";
				else
					first = false;
				check += subquery(e);
			}
			check += "}";
			url = new URL(check);
			conn = url.openConnection();
			conn.connect();
			is = conn.getInputStream();
			response = "";
			while((i=is.read())==-1) {
				response += Character.toString((char)i);
			}
			is.close();
			JSONObject results=null;
			try {
				results = new JSONObject(response);
				JSONObject modifiedObject = results.getJSONObject("results").getJSONArray("bindings").getJSONObject(0);
				String date = modifiedObject.getJSONObject("modified").getString("value");
				modifiedDate = Calendar.getInstance();
				modifiedDate.setTime(format.parse(date));
			} catch (JSONException e1) {
				throw new IOException(e1);
			} catch (ParseException e) {
				throw new IOException(e);
			}
			
			// Re-execute query
			url = new URL(uri);
			conn = url.openConnection();
			conn.connect();
			if(0!=conn.getExpiration()) {
				expirationDate.setTimeInMillis(conn.getExpiration());
			}
			else {
				expirationDate = Calendar.getInstance();
				expirationDate.add(Calendar.MONTH, 1);
			}
			is = conn.getInputStream();
			response = "";
			while((i=is.read())!=-1) {
				response += Character.toString((char)i);
			}
			is.close();
			return response;
		}
		else {
			return null;
		}
	}
	
	public void addGraph(String uri, Calendar modified) {
		graphs.put(uri, modified);
	}
	
	public void setEndpoint(String uri) {
		endpoint = uri;
	}
	
	public String getEndpoint() {
		return endpoint;
	}

	@Override
	public Calendar getModifiedDate() {
		return modifiedDate;
	}

	@Override
	public Calendar getExpirationDate() {
		return expirationDate;
	}

}
