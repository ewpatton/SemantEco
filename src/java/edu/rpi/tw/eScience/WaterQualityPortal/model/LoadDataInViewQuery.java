package edu.rpi.tw.eScience.WaterQualityPortal.model;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
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

public class LoadDataInViewQuery extends Query {
	String state,county,stateURI,source,site,naicsCode;
	String lngLow, lngHigh, latLow, latHigh;	
	boolean isFoia;
	Calendar time;
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	String geoFilter=null;

	public LoadDataInViewQuery(String source, Map<String,String> params) {
		super(null);
		this.source = source;
		state = params.get("state");
		stateURI = WaterAgentInstance.getStateURI(state);
		county = params.get("countyCode");
		time = WaterAgentInstance.processTimeParam(params.get("time"));
		naicsCode= params.get("industry");
		site = params.get("site");
		lngLow = params.get("lngLow");
		lngHigh = params.get("lngHigh");
		latLow = params.get("latLow");
		latHigh = params.get("latHight");
		geoFilter=getGeoFilter();
		if(state.compareTo("CA")==0 || state.compareTo("NY")==0
				|| state.compareTo("MA")==0 || state.compareTo("RI")==0)
			this.isFoia=false;
		else
			this.isFoia=true;
	}

	String makeCountyID(String state, String county) {
		while(county.length()<3) county = "0"+county;
		return state+county;
	}

	@Override
	public Object execute(String endpoint) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	private String getGeoFilter(){
		//	String lngLow, lngHigh, latLow, latHigh;	
		if(geoFilter==null){
			boolean firstBound=true;
			geoFilter = "FILTER (";
			if(latLow!=null&&!latLow.isEmpty()){
				geoFilter+= "?lat >= "+latLow; 
				firstBound=false;
			}
			if(latHigh!=null&&!latHigh.isEmpty()){
				if(firstBound){
					geoFilter+=	" ?lat <= "+latHigh;
					firstBound=false;
				}
				else
					geoFilter+=	" && ?lat <= "+latHigh;
			}
			if(lngLow!=null&&!lngLow.isEmpty()){
				if(firstBound){
					geoFilter+= " ?long >= "+lngLow; 
					firstBound=false;
				}
				geoFilter+= " && ?long >= "+lngLow; 
			}
			if(lngHigh!=null&&!lngHigh.isEmpty()){
				if(firstBound){
					geoFilter+=	" ?long <= "+lngHigh;	
					firstBound=false;
				}
				geoFilter+=	" && ?long <= "+lngHigh;	
			}
			geoFilter += ") ";
		}
		return geoFilter;
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
				loadUSGSData(endpoint, model, sites, measures);
			}
			else if(source.equals("http://sparql.tw.rpi.edu/source/epa-gov")) {
				loadEPAData(endpoint, model, sites, measures);
			}
			else {
				throw new IOException("Attempted to read data from unknown source.");
			}
		}
		catch(Exception e) {//catch(JSONException e)
			throw new IOException("Unable to parse response", e);
		}
		return model;
	}

	public void loadUSGSData(String endpoint, Model model, String sites, String measures) throws UnsupportedEncodingException, JSONException {
		String inClause=null;

		//construct for the sites
		String queryStringPart1 = 
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
						"?s pol:hasCountyCode " + county + " . " +
						//"?s pol:hasStateCode ?state . " +
						"?s wgs:lat ?lat . " +
						"?s wgs:long ?long . " +
						"?s pol:hasMeasurement ?measurement . " +
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
						//"pol:hasStateCode ?state ; " +
						"wgs:lat ?lat ; " +
						"wgs:long ?long . " +
						"OPTIONAL { ?s rdfs:label ?label } ";

		String queryStringPart2="} " +
				"graph <"+measures+"> {" +
				"?measurement pol:hasSiteId ?id ; " +
				"pol:hasCharacteristic ?element ; "+
				"pol:hasValue ?value ; " + 
				"repr:hasUnit ?unit ; " +
				"time:inXSDDateTime ?time . " +
				(time == null ? "" : "FILTER( ?time > xsd:dateTime(\""+sdf.format(time.getTime())+"\")) ")+
				"} " +
				"}";				

		if(site!=null){
			inClause = "IN ( <"+site+"> )";
			queryString	= queryStringPart1+	"FILTER( ?s "+inClause+") "+queryStringPart2;
		}
		else
			queryString	= queryStringPart1+getGeoFilter()+queryStringPart2;

		System.out.println(queryString);
		Logger.getRootLogger().trace(queryString);
		model.read(endpoint+"?query="+URLEncoder.encode(queryString, "UTF-8")
				+"&format="+URLEncoder.encode("application/rdf+xml","UTF-8"), "", null);	
	}

	public void loadUSGSDataBySite(String endpoint, Model model, String sites, String measures) throws UnsupportedEncodingException, JSONException {
		JSONArray ids = null;
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
							getGeoFilter()+							
							//"graph <" + measures + "> { ?m pol:hasSiteId ?x . " +
							//"FILTER(bif:exists((SELECT (1) WHERE { ?m time:inXSDDateTime ?t "+
							//(time==null? "" : "FILTER(?t > xsd:dateTime(\""+sdf.format(time.getTime())+"\"))")+
							//" }))) } "+
							"} ";
			JSONObject ans = WaterAgentInstance.executeJSONQuery(endpoint, queryString);
			ids = new JSONArray();
			JSONArray bindings = ans.getJSONObject("results").getJSONArray("bindings");
			for(int i=0;i<bindings.length();i++) {
				JSONObject result = bindings.getJSONObject(i).getJSONObject("s");
				String uri = result.getString("value");
				ids.put("<"+uri+">");
			}
			if(ids.length()==0) return;
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

		if(ids==null || ids.length()==0) return;
		for(int i=0;i<ids.length();i++) {	
			//construct for the sites
			String curId=ids.getString(i);

			String consStringForSite = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
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
					"?s pol:hasCountyCode " + county + " . " +
					"?s wgs:lat ?lat . " +
					"?s wgs:long ?long . " +
					"} where {" +
					"graph <"+sites+"> {" +
					curId +" a water:WaterSite . " +
					"?s a water:WaterSite ; " +
					"dc:identifier ?id ; " +
					"pol:hasCountyCode " + county + " ; " +
					"wgs:lat ?lat ; " +
					"wgs:long ?long . " +
					"OPTIONAL { ?s rdfs:label ?label } "+
					//getGeoFilter()+
					//"FILTER( ?s "+inClause+") "+
					"} " +
					"}";
			System.out.println(consStringForSite);
			Logger.getRootLogger().trace(consStringForSite);
			model.read(endpoint+"?query="+URLEncoder.encode(consStringForSite, "UTF-8")
					+"&format="+URLEncoder.encode("application/rdf+xml","UTF-8"), "", null);	
		}

		for(int i=0;i<ids.length();i++) {	
			//construct for the sites
			String curId=ids.getString(i);
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
							/*					"?s rdf:type water:WaterSite . " +
					"?s rdfs:label ?label . "+
					"?s pol:hasCountyCode " + county + " . " +
					//"?s pol:hasStateCode ?state . " +
					"?s wgs:lat ?lat . " +
					"?s wgs:long ?long . " +*/
					curId + " pol:hasMeasurement ?measurement . " +
					"?measurement a water:WaterMeasurement . " +
					"?measurement pol:hasCharacteristic ?element . " +
					"?measurement pol:hasValue ?value . " +
					"?measurement unit:hasUnit ?unit . " +
					"?measurement time:inXSDDateTime ?time ." +
					"} where {" +
					/*					"graph <"+sites+"> {" +
					"?s a water:WaterSite ; " +
					"dc:identifier ?id ; " +
					"pol:hasCountyCode " + county + " ; " +
					//"pol:hasStateCode ?state ; " +
					"wgs:lat ?lat ; " +
					"wgs:long ?long . " +
					"OPTIONAL { ?s rdfs:label ?label } "+
					getGeoFilter()+
					//"FILTER( ?s "+inClause+") "+
					"} " +*/
					"graph <"+measures+"> {" +
					"?measurement pol:hasSiteId "+curId+" ; " +
					"pol:hasCharacteristic ?element ; "+
					"pol:hasValue ?value ; " + 
					"repr:hasUnit ?unit ; " +
					"time:inXSDDateTime ?time . " +
					(time == null ? "" : "FILTER( ?time > xsd:dateTime(\""+sdf.format(time.getTime())+"\")) ")+
					"} " +
					"}";
			System.out.println(queryString);
			Logger.getRootLogger().trace(queryString);
			model.read(endpoint+"?query="+URLEncoder.encode(queryString, "UTF-8")
					+"&format="+URLEncoder.encode("application/rdf+xml","UTF-8"), "", null);	
		}	
	}

	public void loadEPAData(String endpoint, Model model, String sites, String measures) throws JSONException, IOException {
		JSONArray ids = null, permits=null;
		String inClause = null; 
		String naicsClause="";
		//Example, "FILTER regex(?naics, \"^3[123]\") ";
		String naicsReg=CountInstanceQuery.code2RegExp(naicsCode);
		if(!naicsReg.isEmpty())
			naicsClause="FILTER regex(?naics, "+naicsReg+") ";

		if(site==null) {
			String queryStringPart1 =
					"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
							"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
							"prefix time: <http://www.w3.org/2006/time#> " +
							"prefix pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#> " +
							"prefix water: <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#> " +
							"prefix unit: <http://sweet.jpl.nasa.gov/2.1/reprSciUnits.owl#> " +
							"prefix repr: <http://sweet.jpl.nasa.gov/2.1/repr.owl#> " +
							"prefix wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#> " +
							"prefix dc: <http://purl.org/dc/terms/> " +
							"select distinct ?s ?x where { graph <"+sites+"> { ?s a water:WaterFacility ; pol:hasCountyCode \""+makeCountyID(state, county)+"\" ; " +
							"pol:hasPermit ?x ; wgs:lat ?lat ; wgs:long ?long ";

			String queryStringPart2="graph <" + measures + "> { ?m pol:hasPermit ?x . " +
					"FILTER(bif:exists((SELECT (1) WHERE { ?m dc:date ?t " +
					(time==null?"":"FILTER( ?t > xsd:dateTime(\""+sdf.format(time.getTime())+"\")) ")+
					"}))) "+
					"} }";

			/*			if(naicsClause.isEmpty())
				queryString= queryStringPart1 +". } " + getGeoFilter()+ queryStringPart2;
			else
				queryString = queryStringPart1 +"; pol:hasNAICS ?naics. } "+ getGeoFilter() + naicsClause + queryStringPart2;
			 */
			if(naicsClause.isEmpty())
				queryString= queryStringPart1 +". } " + getGeoFilter()+ "}";
			else
				queryString = queryStringPart1 +"; pol:hasNAICS ?naics. } "+ getGeoFilter() + naicsClause + "}";


			System.out.println(queryString);//for debug
			JSONObject ans = WaterAgentInstance.executeJSONQuery(endpoint, queryString);					
			ids = new JSONArray();
			permits = new JSONArray();
			JSONArray bindings = ans.getJSONObject("results").getJSONArray("bindings");
			for(int i=0;i<bindings.length();i++) {
				JSONObject result = bindings.getJSONObject(i).getJSONObject("s");
				String uri = result.getString("value");
				ids.put("<"+uri+">");
				JSONObject permit = bindings.getJSONObject(i).getJSONObject("x");
				String permitVal = permit.getString("value");
				permits.put("<"+permitVal+">");
			}
			if(ids.length()==0) return;
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
		System.out.println("inClause:" + inClause);

		if(ids==null || ids.length()==0) return;
		for(int i=0;i<ids.length();i++) {	
			String curId=ids.getString(i);
			String curPermit=permits.getString(i);
			//construct for the facilities
			if(curId.endsWith("facility->"))
				continue;
			String consStringForFac = 
					"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
							"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
							//"prefix time: <http://www.w3.org/2006/time#> " +
							"prefix pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#> " +
							"prefix water: <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#> " +
							//"prefix unit: <http://sweet.jpl.nasa.gov/2.1/reprSciUnits.owl#> " +
							//"prefix repr: <http://sweet.jpl.nasa.gov/2.1/repr.owl#> " +
							"prefix wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#> " +
							//"prefix dc: <http://purl.org/dc/terms/> " +
							"construct { " +
							curId+"rdf:type water:WaterFacility ; " +
							"rdfs:label ?label ; "+
							//"?s pol:hasPermit ?id . "+
							//"pol:hasPermit "+curPermit+" ; " +
							"wgs:lat ?lat ; " +
							"wgs:long ?long . " +
							"} where {" +
							"graph <"+sites+"> {" +
							curId+" a water:WaterFacility ; " +
							//"pol:hasPermit "+curPermit+" ; " +
							"pol:hasCountyCode \"" + makeCountyID(state, county) + "\" ; " +
							"wgs:lat ?lat ; " +
							"wgs:long ?long . " +
							"OPTIONAL { "+ curId +" rdfs:label ?label } "+
							//"FILTER( ?s "+inClause+") "+
							//getGeoFilter()+
							"}} ";	
			/*			String consStringForFac = 
					"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
					"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
					//"prefix time: <http://www.w3.org/2006/time#> " +
					"prefix pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#> " +
					"prefix water: <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#> " +
					//"prefix unit: <http://sweet.jpl.nasa.gov/2.1/reprSciUnits.owl#> " +
					//"prefix repr: <http://sweet.jpl.nasa.gov/2.1/repr.owl#> " +
					"prefix wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#> " +
					//"prefix dc: <http://purl.org/dc/terms/> " +
					"construct { " +
					"?s rdf:type water:WaterFacility . " +
					"?s rdfs:label ?label . "+
					//"?s pol:hasMeasurement ?measurement . " +
					//"?s pol:hasPermit ?id . "+
					"?s pol:hasPermit "+curPermit+" . " +
					"?s wgs:lat ?lat . " +
					"?s wgs:long ?long . " +
					"} where {" +
					"graph <"+sites+"> {" +
					curId+" a water:WaterFacility ; " +
					"pol:hasPermit "+curPermit+" ; " +
					"pol:hasCountyCode \"" + makeCountyID(state, county) + "\" ; " +
					"wgs:lat ?lat ; " +
					"wgs:long ?long . " +
					"OPTIONAL { ?s rdfs:label ?label } "+
					//"FILTER( ?s "+inClause+") "+
					//getGeoFilter()+
					"}} ";	*/
			System.out.println(consStringForFac);
			Logger.getRootLogger().trace(consStringForFac);
			URL urlFac = new URL(endpoint+"?query="+URLEncoder.encode(consStringForFac, "UTF-8")
					+"&format="+URLEncoder.encode("text/rdf+n3","UTF-8"));
			String contentFac = CharStreams.toString(new InputStreamReader(urlFac.openStream()));
			//for debug System.out.println(content);
			//Logger.getRootLogger().trace(content);
			model.read(new StringReader(contentFac), "", "TTL");
		}

		for(int i=0;i<ids.length();i++) {			
			//inClause += ids.getString(i);		
			String curId=ids.getString(i);
			String curPermit=permits.getString(i);
			if(curId.endsWith("facility-"))
				continue;
			//construct for the measurements
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
							/*			"?s rdf:type water:WaterFacility . " +
					"?s rdfs:label ?label . "+
					"?s pol:hasPermit ?id . "+
					"?s wgs:lat ?lat . " +
					"?s wgs:long ?long . " +*/
					curId+" pol:hasMeasurement ?measurement . " +
					"?measurement a water:WaterMeasurement . " +
					"?measurement pol:hasCharacteristic ?element . " +
					"?measurement pol:hasValue ?value . " +
					"?measurement unit:hasUnit ?unit . " +
					"?measurement time:inXSDDateTime ?time ." +
					//"?measurement pol:hasLimitOperator ?op . " +
					//"?measurement pol:hasLimitValue ?lval . "+
					"} where {" +
					/*			"graph <"+sites+"> {" +
					"?s a water:WaterFacility ; " +
					"pol:hasPermit ?id ; " +
					"pol:hasCountyCode \"" + makeCountyID(state, county) + "\" ; " +
					"wgs:lat ?lat ; " +
					"wgs:long ?long . " +
					"OPTIONAL { ?s rdfs:label ?label } "+
					//"FILTER( ?lat != 0 && ?long != 0 ) "+
					//"FILTER( ?s "+inClause+") "+
					getGeoFilter()+
					"} " +*/					
					"graph <"+measures+"> {" +
					"?measurement pol:hasPermit "+curPermit+" ; " +
					"pol:hasCharacteristic ?element ; "+
					"repr:hasUnit ?unit ; " +
					"dc:date ?time . ";
			if(isFoia)
				queryString+="?measurement pol:hasValue ?value. ";
			else
				queryString+="?measurement rdf:value ?value. ";
			//"{ ?measurement rdf:value ?value } UNION { ?measurement pol:hasValue ?value } " +
			//"?measurement rdf:value ?value. " +
			queryString+= (time==null?"":"FILTER( ?time > xsd:dateTime(\""+sdf.format(time.getTime())+"\")) ")+
					//"OPTIONAL {"+
					//"?measurement pol:hasLimitOperator ?op ; "+
					//"pol:hasLimitValue ?lval . "+
					//"} " +
					"} " +
					//					"graph <"+sites+"> {" +
					//					"?s pol:hasPermit "+curId+" . }" +
					"}"//end of where
					;
			System.out.println(queryString);
			Logger.getRootLogger().trace(queryString);
			URL url = new URL(endpoint+"?query="+URLEncoder.encode(queryString, "UTF-8")
					+"&format="+URLEncoder.encode("text/rdf+n3","UTF-8"));
			String content = CharStreams.toString(new InputStreamReader(url.openStream()));
			//for debug System.out.println(content);
			//Logger.getRootLogger().trace(content);
			model.read(new StringReader(content), "", "TTL");
		}
		/*
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
				//"{ ?m rdf:value ?value } UNION { ?m pol:hasValue ?value } " +
				"{ ?m rdf:value ?value }" +
				"?m pol:hasPermit ?x ; pol:hasLimitOperator ?op ; pol:hasLimitValue ?lval . "+
				"FILTER((?op = \"<=\" && ?value > ?lval) || " +
				"(?op = \">=\" && ?value < ?lval) || " +
				"(?op = \">\" && ?value <= ?lval))"+
				"} }";
		url = new URL(endpoint+"?query="+URLEncoder.encode(queryString, "UTF-8")
				+"&format="+URLEncoder.encode("application/sparql-results+xml", "UTF-8"));
		System.err.println("LoadDataQuery.execute, url: "+url);//added by ping
		content = CharStreams.toString(new InputStreamReader(url.openStream()));				
		//for debug System.out.println(content);
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
			//for debug System.out.println("Facility as PollutedSite: "+sub);
		}
		seen = null;
		rs = null;*/		
	}

}
