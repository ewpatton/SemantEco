package edu.rpi.tw.eScience.WaterQualityPortal.model;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.io.CharStreams;
import com.hp.hpl.jena.query.ResultSetFactory;

import edu.rpi.tw.eScience.WaterQualityPortal.WebService.WaterAgentInstance;

public class ListCharacteristicsQuery extends Query {

	String state,county,stateURI,source,site;
	int offset,limit;
	Calendar time;
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	String makeCountyID(String state, String county) {
		while(county.length()<3) county = "0"+county;
		return state+county;
	}
	
	public ListCharacteristicsQuery(String source, Map<String,String> params) {
		super(null);
		this.source = source;
		state = params.get("state");
		stateURI = WaterAgentInstance.getStateURI(state);
		county = params.get("countyCode");
	}
	
	public Object execute(String endpoint) throws IOException {
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
				"select distinct ?c where {"+
				"graph <"+sites+"> {" +
				"?s dc:identifier ?id ; pol:hasCountyCode " + county +
				"} "+
				"graph <"+measures+"> {" +
				"?measurement pol:hasSiteId ?id ; " +
				"pol:hasCharacteristic ?c "+
				"} " +
				"}"
				;
			Logger.getRootLogger().trace(queryString);
			URL url = new URL(endpoint+"?query="+URLEncoder.encode(queryString, "UTF-8")
					+"&format="+URLEncoder.encode("application/sparql-results+xml","UTF-8"));
			String content = CharStreams.toString(new InputStreamReader(url.openStream()));
			return ResultSetFactory.fromXML(content);
		}
		else if(source.equals("http://sparql.tw.rpi.edu/source/epa-gov")) {
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
				"select distinct ?c where {" +
				"graph <"+sites+"> {" +
				"?s pol:hasPermit ?id ; " +
				"pol:hasCountyCode \"" + makeCountyID(state, county) + "\" " +
				"} " +
				"graph <"+measures+"> {" +
				"?measurement pol:hasPermit ?id ; " +
				"pol:hasCharacteristic ?c "+
				"} " +
				"}"
				;
			Logger.getRootLogger().trace(queryString);
			URL url = new URL(endpoint+"?query="+URLEncoder.encode(queryString, "UTF-8")
					+"&format="+URLEncoder.encode("application/sparql-results+xml","UTF-8"));
			String content = CharStreams.toString(new InputStreamReader(url.openStream()));
			return ResultSetFactory.fromXML(content);
		}
		else {
			throw new IOException("Attempted to read data from unknown source.");
		}
	}

}
