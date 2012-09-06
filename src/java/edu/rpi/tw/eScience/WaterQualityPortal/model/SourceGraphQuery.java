package edu.rpi.tw.eScience.WaterQualityPortal.model;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.rpi.tw.eScience.WaterQualityPortal.WebService.WaterAgentInstance;

public class SourceGraphQuery extends Query {

	public SourceGraphQuery(String source, String stateURI) {
		super(null);
		queryString = "PREFIX sioc: <http://rdfs.org/sioc/ns#> PREFIX dc: <http://purl.org/dc/terms/> "+
				"SELECT ?graph WHERE { GRAPH <http://sparql.tw.rpi.edu/semanteco/data-source> { ?graph sioc:topic <"+stateURI+"> "+
				"; dc:source <"+source+"> } }";
	}
	
	@Override
	public Object execute(String endpoint) throws IOException {
		ArrayList<String> list = null;
		try {
			JSONObject graphs = WaterAgentInstance.executeJSONQuery(endpoint, queryString);
			JSONArray arr = graphs.getJSONObject("results").getJSONArray("bindings");
			list = new ArrayList<String>();
			for(int i=0;i<arr.length();i++) {
				list.add(arr.getJSONObject(i).getJSONObject("graph").getString("value"));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
