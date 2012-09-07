package edu.rpi.tw.escience.WaterQualityPortal.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.jena.rdf.model.Model;

public abstract class Query {
	protected String queryString;
	
	public static interface ModelUpdater {
		public void setModel(Model m);
		public Model getModel();
	}
	
	public static boolean LOG_PERFORMANCE=true;
	
	protected String query(String endpoint) throws IOException, JSONException {
		long start = System.currentTimeMillis();
		String command = endpoint+
				"?default-graph-uri=&should-sponge=&"+
				"query="+java.net.URLEncoder.encode(queryString, "ASCII")+"&"+
				"format=application%2Fsparql-results%2Bjson&" +
				"debug=on";
		URL url = new URL(command);
		URLConnection conn = url.openConnection();
		conn.connect();
		InputStream is = conn.getInputStream();
		int i;
		String response = "";
		while((i=is.read())!=-1) {
			response += Character.toString((char)i);
		}
		try {
			is.close();
		}
		catch(Exception e) { }
		if(LOG_PERFORMANCE)
			System.err.println("Query time: "+(System.currentTimeMillis()-start)+" ms");
		return response;
	}
	
	public static class StateQuery extends Query {
		public StateQuery(String str) {
			super(str);
		}

		public Set<String> execute(String endpoint) throws IOException {
			Set<String> result = new TreeSet<String>();
			try {
				String response = query(endpoint);
				JSONObject js = new JSONObject(response);
				JSONArray bindings = js.getJSONObject("results").getJSONArray("bindings");
				Pattern p = Pattern.compile("source/.*/dataset/nwis-measurements-(.*)/version/.*");
				for(int i=0;i<bindings.length();i++) {
					JSONObject binding = bindings.getJSONObject(i);
					String uri = binding.getJSONObject("g").getString("value");
					System.err.println("Found uri: "+uri);
					Matcher m = p.matcher(uri);
					if(m.find())
						result.add(m.group(1));
					else
						System.err.println("Doesn't match!");
				}
			}
			catch(JSONException e) {
				throw new IOException(e);
			}
			return result;
		}
	}
	
	public Query(String str) {
		queryString = str;
	}
	
	protected static String graphQuery = "prefix pmlj: <http://inference-web.org/2.0/pml-justification.owl#> "+
			"prefix sd: <http://www.w3.org/ns/sparql-service-description#> "+
			"prefix skos: <http://www.w3.org/2004/02/skos/core#> "+
			"select distinct ?g where { "+
			"graph ?g { "+
			"[] pmlj:hasConclusion [ skos:broader [ sd:name ?g ] ] "+
			"} "+
			"filter(regex(str(?g), \"^http://sparql.tw.rpi.edu/source/.*/dataset/nwis-measurements-.*/version/.*\")) "+
			"}";
	
	public static StateQuery getStatesQuery() {
		return new StateQuery(graphQuery);
	}
	
	public static class DateQuery extends Query {

		static SimpleDateFormat format =
				new SimpleDateFormat("yyyy-MMM-dd");
		
		public DateQuery(String str) {
			super(str);
		}
		
		public Set<Date> execute(String endpoint) throws IOException {
			Set<Date> result = new TreeSet<Date>();
			try {
				String response = query(endpoint);
				JSONObject js = new JSONObject(response);
				JSONArray bindings = js.getJSONObject("results").getJSONArray("bindings");
				Pattern p = Pattern.compile("source/.*/dataset/nwis-measurements-.*/version/(.*)");
				for(int i=0;i<bindings.length();i++) {
					JSONObject binding = bindings.getJSONObject(i);
					String uri = binding.getJSONObject("g").getString("value");
					System.err.println("Found uri: "+uri);
					Matcher m = p.matcher(uri);
					if(m.find())
						result.add(format.parse(m.group(1)));
					else
						System.err.println("Doesn't match!");
				}
			}
			catch(JSONException e) {
				throw new IOException(e);
			}
			catch(ParseException e) {
				throw new IOException(e);
			}
			return result;
		}
		
	}
	
	public static DateQuery getImportDates() {
		return new DateQuery(graphQuery);
	}
	
	public static Query getSourceAgencies() {
		return null;
	}
	
	public static class WaterDataQuery extends Query implements ModelUpdater {

		Model model;
		
		public WaterDataQuery(String str) {
			super(str);
		}
		
		public WaterDataQuery(String state, String county, String offset, String limit) {
			super(null);
			queryString =
				"prefix pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#> "+
				"prefix time: <http://www.w3.org/2006/time#> "+
				"prefix wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#> "+
				"prefix rdfs: <http://www.w3.org/2000/02/rdf-schema#> "+
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#> "+
				"prefix water: <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#> "+
				"prefix unit: <http://sweet.jpl.nasa.gov/2.1/reprSciUnits.owl#> "+
				"prefix repr: <http://sweet.jpl.nasa.gov/2.1/repr.owl#> "+
				"prefix dc: <http://purl.org/dc/terms/> "+
				"construct {"+
				"?s rdf:type water:WaterSite ; "+
				"  pol:hasMeasurement ?measure ; "+
				"  pol:hasCountyCode "+county+" ; "+
				"  pol:hasStateCode ?state ; "+
				"  wgs:lat ?lat ; "+
				"  wgs:long ?long . "+
				"?measure a water:WaterMeasurement ; "+
				"  pol:hasCharacteristic ?elem ; "+
				"  pol:hasValue ?value ; "+
				"  unit:hasUnit ?unit ; "+
				"  time:inXSDDateTime ?time . "+
				"} where {"+
				"graph <http://sparql.tw.rpi.edu/source/usgs-gov/dataset/nwis-sites-"+state.toLowerCase()+"/version/2011-Mar-20> { "+
				"?s a water:WaterSite ; "+
				"  dc:identifier ?id ; "+
				"  pol:hasCountyCode "+county+" ; "+
				"  pol:hasStateCode ?state ; "+
				"  wgs:lat ?lat ; "+
				"  wgs:long ?long . "+
				"} "+
				"?measure pol:hasSiteId ?id ; "+
				"  pol:hasCharacteristic ?elem ; "+
				"  pol:hasValue ?value ; "+
				"  repr:hasUnit ?unit ; "+
				"  time:inXSDDateTime ?time . "+
				"} "+
				"} order by desc(?time) offset "+offset+" limit "+limit
				;
		}
		
		public Model execute(String endpoint, Model model) {
			try {
				model.read(endpoint+"?default-graph-uri=&should-sponge=&"+
						"query="+java.net.URLEncoder.encode(queryString, "ASCII")+"&"+
						"format=application%2Frdf%2Bxml&" +
						"debug=on");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return model;
		}

		@Override
		public void setModel(Model m) {
			model = m;
		}

		@Override
		public Model getModel() {
			return model;
		}

		@Override
		public Object execute(String endpoint) throws IOException {
			return execute(endpoint, model);
		}
		
	}
	
	public static WaterDataQuery getWaterData(String state, String county, String offset, String limit) {
		return new WaterDataQuery(state, county, offset, limit);
	}
	
	public static class FacilityDataQuery extends Query implements ModelUpdater {
		String if_last_modified = "";
		Model model;
		
		public FacilityDataQuery(String state, String county, String zip, String offset, String limit, String type) {
			super("");
			queryString = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
				"prefix pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#> "+
				"prefix time: <http://www.w3.org/2006/time#> "+
				"prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> "+
				"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
				"prefix water: <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#> "+
				"prefix unit: <http://sweet.jpl.nasa.gov/2.1/reprSciUnits.owl#> "+
				"prefix dc: <http://purl.org/dc/terms/> "+
				"construct { "+
				"?s a water:WaterFacility ;" +
				"  pol:hasPermit ?permit ; "+
				"  wgs:lat ?lat ; "+
				"  wgs:long ?long ; "+
				"  rdfs:label ?label ; "+
				"  pol:hasMeasurement ?measure . "+
				"?measure a water:WaterMeasurement ; "+
				"  pol:hasCharacteristic ?elem ; "+
				"  pol:hasValue ?value ; "+
				"  water:hasValueTypeCode ?test ; "+
				"  unit:hasUnit ?unit ; "+
				"  dc:date ?date . "+
				"} where { "+
				"graph <http://sparql.tw.rpi.edu/source/epa-gov/dataset/echo-facilities-"+state.toLowerCase()+"/version/2011-Mar-19> {"+
				"?s a water:WaterFacility ;" +
				"  rdfs:label ?label ; "+
				"  wgs:lat ?lat ; "+
				"  wgs:long ?long ; "+
				"  pol:hasPermit ?permit . "+
				"FILTER(?lat != 0 && ?long != 0)"+
				"} "+
				"graph <http://sparql.tw.rpi.edu/source/epa-gov/dataset/foia-measurements-"+state.toLowerCase()+"/version/2011-Jul-23> {"+
				"?measure a water:WaterMeasurement ;" +
				"  pol:hasPermit ?permit ;" +
				"  repr:hasUnit ?unit ;" +
				"  pol:hasCharacteristic ?elem ; "+
				"  pol:hasValue ?value ; "+
				"  water:hasValueTypeCode ?test ; "+
				"  dc:date ?date ; "+
				"  pol:hasZip \""+zip+"\" . "+
				"} "+
				"} order by desc(?date)";
			if_last_modified = "prefix dc: <http://purl.org/dc/terms/> "+
				"prefix sd: <http://www.w3.org/ns/sparql-service-description#> "+
				"prefix sioc: <http://rdfs.org/sioc/ns#> "+
				"prefix skos: <http://www.w3.org/2004/02/skos/core#> "+
				"prefix pmlj: <http://inference-web.org/2.0/pml-justification.owl#> "+
				"prefix hartigprov: <http://purl.org/net/provenance/ns#> "+
				"prefix conversion: <http://purl.org/twc/vocab/conversion/> "+
				"prefix xsd: <http://www.w3.org/2001/XMLSchema#> "+
				"ask { "+
				"{ "+
				"graph <http://sparql.tw.rpi.edu/source/epa-gov/dataset/echo-facilities-"+state.toLowerCase()+"/version/2011-Mar-19> { "+
				"[] pmlj:hasConclusion [ skos:broader [ sd:name ?graph ] ]; "+
				"  pmlj:isConsequentOf [ dc:date ?when ] . "+
				"filter(?when > \""+"\"^^xsd:dateTime) "+
				"} "+
				"} union { "+
				"graph <http://sparql.tw.rpi.edu/source/epa-gov/dataset/foia-measurements-"+state.toLowerCase()+"/version/2011-Jul-23> { "+
				"[] pmlj:hasConclusion [ skos:broader [ sd:name ?graph ] ]; "+
				"  pmlj:isConsequentOf [ dc:date ?when ] . "+
				"filter(?when > \""+"\"^^xsd:dateTime) "+
				"} "+
				"} "+
				"}";
		}
		
		public Model execute(String endpoint, Model model) {
			try {
				model.read(endpoint+"?default-graph-uri=&should-spong=&"+
						"query="+java.net.URLEncoder.encode(queryString, "ASCII")+"&"+
						"format=application%2Frdf%2Bxml&"+
						"debug=on");
			}
			catch(UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return model;
		}

		@Override
		public void setModel(Model m) {
			model = m;
		}

		@Override
		public Model getModel() {
			return model;
		}

		@Override
		public Object execute(String endpoint) throws IOException {
			return execute(endpoint, model);
		}
	}
	
	public static FacilityDataQuery getFacilityData(String state, String county, String zip, String offset, String limit, String type) {
		return new FacilityDataQuery(state, county, zip, offset, limit, type);
	}
	
	public abstract Object execute(String endpoint) throws IOException;
}
