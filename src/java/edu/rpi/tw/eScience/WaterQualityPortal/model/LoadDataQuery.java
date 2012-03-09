package edu.rpi.tw.eScience.WaterQualityPortal.model;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.io.CharStreams;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.rpi.tw.eScience.WaterQualityPortal.WebService.WaterAgentInstance;

public class LoadDataQuery extends Query {

	String state,county,stateURI,source,site,lat,lng;
	int offset,limit;
	Calendar time;
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	String makeCountyID(String state, String county) {
		while(county.length()<3) county = "0"+county;
		return state+county;
	}
	
	public LoadDataQuery(String source, Map<String,String> params) {
		super(null);
		this.source = source;
		state = params.get("state");
		stateURI = WaterAgentInstance.getStateURI(state);
		county = params.get("countyCode");
		time = WaterAgentInstance.processTimeParam(params.get("time"));
		site = params.get("site");
		lat = params.get("lat");
		lng = params.get("lng");
		try {
			JSONObject limits = new JSONObject(params.get("limit"));
			if(source.equals("http://sparql.tw.rpi.edu/source/usgs-gov")) {
				JSONObject site = limits.getJSONObject("site");
				offset = site.getInt("offset");
				limit = site.getInt("limit");
			}
			else if(source.equals("http://sparql.tw.rpi.edu/source/epa-gov")) {
				JSONObject facility = limits.getJSONObject("facility");
				offset = facility.getInt("offset");
				limit = facility.getInt("limit");
			}
		}
		catch(Exception e) {
			
		}
	}
	
	@Override
	public Object execute(String endpoint) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Object execute(String endpoint, Model model) throws IOException {
		try {
			SourceGraphQuery q = new SourceGraphQuery(source, stateURI);
			@SuppressWarnings("unchecked")
			ArrayList<String> graphs = (ArrayList<String>)q.execute(endpoint);
			if(graphs==null) throw new IOException("Unable to obtain list of graphs from triple store.");
			String sites, measures;
			if(graphs.get(0).contains("measurements")) {
				measures = graphs.get(0);
				sites = graphs.get(1);
			}
			else {
				sites = graphs.get(0);
				measures = graphs.get(1);
			}
			if(source.equals("http://sparql.tw.rpi.edu/source/usgs-gov")) {
				String inClause = null;
				if(site==null) {
					queryString =
							"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
							"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
							"prefix time: <http://www.w3.org/2006/time#> " +
							"prefix pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#> " +
							"prefix water: <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#> " +
							"prefix unit: <http://sweet.jpl.nasa.gov/2.1/reprSciUnits.owl#> " +
							"prefix repr: <http://sweet.jpl.nasa.gov/2.1/repr.owl#> " +
							"prefix wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#> " +
							"prefix dc: <http://purl.org/dc/terms/> " +
							"select distinct ?s where { graph <"+sites+"> { ?s a water:WaterSite ; pol:hasCountyCode "+county+
							" ; dc:identifier ?x ; wgs:lat ?lat ; wgs:long ?long } " +
							"graph <" + measures + "> { ?m pol:hasSiteId ?x . " +
							"FILTER(bif:exists((SELECT (1) WHERE { ?m time:inXSDDateTime ?t "+
							(time==null? "" : "FILTER(?t > xsd:dateTime(\""+sdf.format(time.getTime())+"\"))")+
							" }))) "+
							"} } order by ((?lat - "+lat+")*(?lat - "+lat+")+(?long - "+lng+")*(?long - "+lng+")) offset " + offset + " limit "+limit;
					JSONObject ans = WaterAgentInstance.executeJSONQuery(endpoint, queryString);
					JSONArray ids = new JSONArray();
					JSONArray bindings = ans.getJSONObject("results").getJSONArray("bindings");
					for(int i=0;i<bindings.length();i++) {
						JSONObject result = bindings.getJSONObject(i).getJSONObject("s");
						String uri = result.getString("value");
						ids.put("<"+uri+">");
					}
					if(ids.length()==0) return model;
					inClause = "IN (";
					for(int i=0;i<ids.length();i++) {
						if(i!=0) inClause += ",";
						inClause += ids.getString(i);
					}
					inClause += ") ";
				}
				else {
					inClause = "IN ( <"+site+"> )";
				}
				queryString = 
					"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
					"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
					"prefix time: <http://www.w3.org/2006/time#> " +
					"prefix pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#> " +
					"prefix water: <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#> " +
					"prefix unit: <http://sweet.jpl.nasa.gov/2.1/reprSciUnits.owl#> " +
					"prefix repr: <http://sweet.jpl.nasa.gov/2.1/repr.owl#> " +
					"prefix wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#> " +
					"prefix dc: <http://purl.org/dc/terms/> " +
					"prefix xsd: <http://www.w3.org/2001/XMLSchema#> "+
					"construct { " +
					"?s rdf:type water:WaterSite . " +
					"?s rdfs:label ?label . "+
					"?s pol:hasMeasurement ?measurement . " +
					"?s pol:hasCountyCode " + county + " . " +
					"?s pol:hasStateCode ?state . " +
					"?s wgs:lat ?lat . " +
					"?s wgs:long ?long . " +
					"?measurement a water:WaterMeasurement . " +
					"?measurement pol:hasCharacteristic ?element . " +
					"?measurement pol:hasValue ?value . " +
					"?measurement unit:hasUnit ?unit . " +
					"?measurement time:inXSDDateTime ?time ." +
					"} where {" +
					"graph <"+sites+"> {" +
					"?s a water:WaterSite ; " +
					"dc:identifier ?id ; " +
					"pol:hasCountyCode " + county + " ; " +
					"pol:hasStateCode ?state ; " +
					"wgs:lat ?lat ; " +
					"wgs:long ?long . " +
					"OPTIONAL { ?s rdfs:label ?label } "+
					"FILTER( ?s "+inClause+") "+
					"} " +
					"graph <"+measures+"> {" +
					"?measurement pol:hasSiteId ?id ; " +
					"pol:hasCharacteristic ?element ; "+
					"pol:hasValue ?value ; " + 
					"repr:hasUnit ?unit ; " +
					"time:inXSDDateTime ?time . " +
					(time == null ? "" : "FILTER( ?time > xsd:dateTime(\""+sdf.format(time.getTime())+"\")) ")+
					"} " +
					"}"
					;
				Logger.getRootLogger().trace(queryString);
				model.read(endpoint+"?query="+URLEncoder.encode(queryString, "UTF-8")
					   +"&format="+URLEncoder.encode("application/rdf+xml","UTF-8"), "", null);
			}
			else if(source.equals("http://sparql.tw.rpi.edu/source/epa-gov")) {
				String inClause = null;
				if(site==null) {
					queryString =
							"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
							"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
							"prefix time: <http://www.w3.org/2006/time#> " +
							"prefix pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#> " +
							"prefix water: <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#> " +
							"prefix unit: <http://sweet.jpl.nasa.gov/2.1/reprSciUnits.owl#> " +
							"prefix repr: <http://sweet.jpl.nasa.gov/2.1/repr.owl#> " +
							"prefix wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#> " +
							"prefix dc: <http://purl.org/dc/terms/> " +
							"select distinct ?s where { graph <"+sites+"> { ?s a water:WaterFacility ; pol:hasCountyCode \""+makeCountyID(state, county)+"\" ; " +
									"pol:hasPermit ?x ; wgs:lat ?lat ; wgs:long ?long } " +
							"graph <" + measures + "> { ?m pol:hasPermit ?x . " +
							"FILTER(bif:exists((SELECT (1) WHERE { ?m dc:date ?t " +
							(time==null?"":"FILTER( ?t > xsd:dateTime(\""+sdf.format(time.getTime())+"\")) ")+
							"}))) "+
							"} } order by ((?lat - "+lat+")*(?lat - "+lat+")+(?long - "+lng+")*(?long - "+lng+")) offset " + offset + " limit "+limit;
					JSONObject ans = WaterAgentInstance.executeJSONQuery(endpoint, queryString);
					JSONArray ids = new JSONArray();
					JSONArray bindings = ans.getJSONObject("results").getJSONArray("bindings");
					for(int i=0;i<bindings.length();i++) {
						JSONObject result = bindings.getJSONObject(i).getJSONObject("s");
						String uri = result.getString("value");
						ids.put("<"+uri+">");
					}
					if(ids.length()==0) return model;
					inClause = "IN (";
					for(int i=0;i<ids.length();i++) {
						if(i!=0) inClause += ",";
						inClause += ids.getString(i);
					}
					inClause += ") ";
				}
				else {
					inClause = "IN ( <"+site+"> )";
				}
				queryString = 
					"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
					"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
					"prefix time: <http://www.w3.org/2006/time#> " +
					"prefix pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#> " +
					"prefix water: <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#> " +
					"prefix unit: <http://sweet.jpl.nasa.gov/2.1/reprSciUnits.owl#> " +
					"prefix repr: <http://sweet.jpl.nasa.gov/2.1/repr.owl#> " +
					"prefix wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#> " +
					"prefix dc: <http://purl.org/dc/terms/> " +
					"construct { " +
					"?s rdf:type water:WaterFacility . " +
					"?s rdfs:label ?label . "+
					"?s pol:hasMeasurement ?measurement . " +
					"?s pol:hasPermit ?id . "+
					"?s wgs:lat ?lat . " +
					"?s wgs:long ?long . " +
					"?measurement a water:WaterMeasurement . " +
					"?measurement pol:hasCharacteristic ?element . " +
					"?measurement pol:hasValue ?value . " +
					"?measurement unit:hasUnit ?unit . " +
					"?measurement time:inXSDDateTime ?time ." +
					"?measurement pol:hasLimitOperator ?op . " +
					"?measurement pol:hasLimitValue ?lval . "+
					"} where {" +
					"graph <"+sites+"> {" +
					"?s a water:WaterFacility ; " +
					"pol:hasPermit ?id ; " +
					"pol:hasCountyCode \"" + makeCountyID(state, county) + "\" ; " +
					"wgs:lat ?lat ; " +
					"wgs:long ?long . " +
					"OPTIONAL { ?s rdfs:label ?label } "+
					//"FILTER( ?lat != 0 && ?long != 0 ) "+
					"FILTER( ?s "+inClause+") "+
					"} " +
					"graph <"+measures+"> {" +
					"?measurement pol:hasPermit ?id ; " +
					"pol:hasCharacteristic ?element ; "+
					"repr:hasUnit ?unit ; " +
					"dc:date ?time . " +
					"{ ?measurement rdf:value ?value } UNION { ?measurement pol:hasValue ?value } " +
					(time==null?"":"FILTER( ?time > xsd:dateTime(\""+sdf.format(time.getTime())+"\")) ")+
					"OPTIONAL {"+
					"?measurement pol:hasLimitOperator ?op ; "+
					"pol:hasLimitValue ?lval . "+
					"} " +
					"} " +
					"}"
					;
				Logger.getRootLogger().trace(queryString);
				URL url = new URL(endpoint+"?query="+URLEncoder.encode(queryString, "UTF-8")
						+"&format="+URLEncoder.encode("text/rdf+n3","UTF-8"));
				String content = CharStreams.toString(new InputStreamReader(url.openStream()));
				//Logger.getRootLogger().trace(content);
				model.read(new StringReader(content), "", "TTL");
				queryString = 
						"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
						"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
						"prefix time: <http://www.w3.org/2006/time#> " +
						"prefix pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#> " +
						"prefix water: <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#> " +
						"prefix unit: <http://sweet.jpl.nasa.gov/2.1/reprSciUnits.owl#> " +
						"prefix repr: <http://sweet.jpl.nasa.gov/2.1/repr.owl#> " +
						"prefix wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#> " +
						"prefix dc: <http://purl.org/dc/terms/> " +
						"select ?s ?m where { graph <"+sites+"> {" +
						"?s pol:hasPermit ?x ; pol:hasCountyCode \""+makeCountyID(state, county)+"\" " +
						"FILTER( ?s "+inClause+" ) }" +
						"graph <"+measures+"> { "+
						"{ ?m rdf:value ?value } UNION { ?m pol:hasValue ?value } ?m pol:hasPermit ?x ; pol:hasLimitOperator ?op ; pol:hasLimitValue ?lval . "+
						"FILTER((?op = \"<=\" && ?value > ?lval) || " +
						"(?op = \">=\" && ?value < ?lval) || " +
						"(?op = \">\" && ?value <= ?lval))"+
						"} }";
				url = new URL(endpoint+"?query="+URLEncoder.encode(queryString, "UTF-8")
						+"&format="+URLEncoder.encode("application/sparql-results+xml", "UTF-8"));
				System.err.println("LoadDataQuery.execute, url: "+url);//added by ping
				content = CharStreams.toString(new InputStreamReader(url.openStream()));				
				ResultSet rs = ResultSetFactory.fromXML(content);
				HashSet<String> seen = new HashSet<String>();
				while(rs.hasNext()) {
					QuerySolution qs = rs.next();
					String uri = qs.getResource("m").getURI();
					String sub = qs.getResource("s").getURI();
					//Logger.getRootLogger().trace("Measurement "+uri+" is a violation.");
					model.add(model.getResource(uri), RDF.type, model.getResource("http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#RegulationViolation"));
					if(seen.contains(sub)) continue;
					seen.add(sub);
					model.add(model.getResource(sub), RDF.type, model.getResource("http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#PollutedSite"));
				}
				seen = null;
				rs = null;
			}
			else {
				throw new IOException("Attempted to read data from unknown source.");
			}
		}
		catch(JSONException e) {
			throw new IOException("Unable to parse response", e);
		}
		return model;
	}

}
