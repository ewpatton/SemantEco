package edu.rpi.tw.eScience.WaterQualityPortal.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.io.CharStreams;

import edu.rpi.tw.eScience.WaterQualityPortal.WebService.WaterAgentInstance;

public class CountInstanceQuery extends Query {

	Calendar time;
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	protected CountInstanceQuery(String str) {
		super(str);
	}
	
	static public String code2RegExp(String code){
		if(code==null || code.isEmpty() || code.compareTo("all")==0)
			return "";
		StringBuilder reg=new StringBuilder();
		String[] parts=code.split("-");
		reg.append("\"^");
		if(parts.length==1){
			reg.append(code);
		}
		else{
			reg.append(code.charAt(0));
			reg.append("[");
			for(String part:parts)
				reg.append(part.charAt(1));
			reg.append("]");
		}
		reg.append("\"");
		return reg.toString();
	}
	
	public CountInstanceQuery(String source, String sites, String measures, Map<String,String> params) throws Exception {
		super(null);
		String state = params.get("state");
		String county = params.get("countyCode");
		time = WaterAgentInstance.processTimeParam(params.get("time"));
		String naicsCode = params.get("industry");
		if(source.equals("http://sparql.tw.rpi.edu/source/epa-gov")) {
		//"FILTER regex(?naics, \"^3[123]\") ";
		String naicsReg=code2RegExp(naicsCode);
		String naicsClause="";
		if(!naicsReg.isEmpty())
			naicsClause="FILTER regex(?naics, "+naicsReg+") ";
			
			while(county.length()<3) county = "0"+county;
/*			queryString = "prefix pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#> " +
					"prefix water: <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#> " + 
					"prefix repr: <http://sweet.jpl.nasa.gov/2.1/repr.owl#> " +
					"prefix wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#> " +
					"prefix dc: <http://purl.org/dc/terms/> " +
					"select (count(distinct ?s) as ?cnt) where { graph <"+sites+"> { "+
					"?s a water:WaterFacility ; pol:hasCountyCode \""+state+county+"\" ; "+
					"pol:hasPermit ?p ; wgs:lat ?lat ; wgs:long ?long ; " +
					"pol:hasNAICS ?naics. } " + naicsClause +" graph <"+measures+"> { "+
					"?m pol:hasPermit ?p ; dc:date ?t ; pol:hasCharacteristic ?e ; repr:hasUnit ?unit . " +
					(time==null?"":"FILTER(?t > xsd:dateTime(\""+sdf.format(time.getTime())+"\"))")+
					" } }";*/			
			String part1= "prefix pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#> " +
					"prefix water: <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#> " + 
					"prefix repr: <http://sweet.jpl.nasa.gov/2.1/repr.owl#> " +
					"prefix wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#> " +
					"prefix dc: <http://purl.org/dc/terms/> " +
					"select (count(distinct ?s) as ?cnt) where { graph <"+sites+"> { "+
					"?s a water:WaterFacility ; pol:hasCountyCode \""+state+county+"\" ; "+
					"pol:hasPermit ?p ; wgs:lat ?lat ; wgs:long ?long ";
			String part2 = " graph <"+measures+"> { "+
					"?m pol:hasPermit ?p ; dc:date ?t ; pol:hasCharacteristic ?e ; repr:hasUnit ?unit . " +
					(time==null?"":"FILTER(?t > xsd:dateTime(\""+sdf.format(time.getTime())+"\"))")+
					" } }";
			if(naicsClause.isEmpty())
				queryString= part1 +". } "+part2;
			else
				queryString = part1 +"; pol:hasNAICS ?naics. } " + naicsClause + part2;			
			System.out.println(queryString);//for dug
		}
		else if(source.equals("http://sparql.tw.rpi.edu/source/usgs-gov")) {
			queryString = "prefix pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#> " +
					"prefix water: <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#> " + 
					"prefix dc: <http://purl.org/dc/terms/> " +
					"prefix time: <http://www.w3.org/2006/time#> " +
					"select (count(distinct ?s) as ?cnt) where { graph <"+sites+"> { "+
					"?s a water:WaterSite ; pol:hasCountyCode "+county+" ; dc:identifier ?x . "+
					"} graph <"+measures+"> { "+
					"?m pol:hasSiteId ?x ; time:inXSDDateTime ?t ; pol:hasCharacteristic ?e ."+
					(time==null?"":"FILTER(?t > xsd:dateTime(\""+sdf.format(time.getTime())+"\"))")+
					"} }";
		}
		else {
			throw new Exception("Unknown source agency <"+source+">");
		}
	}

	@Override
	public Object execute(String endpoint) throws IOException {
		Logger.getRootLogger().trace("Counting sites with query: "+queryString);
		URL url = new URL(endpoint+"?query="+URLEncoder.encode(queryString, "UTF-8")+"&format=json");
		InputStream is = url.openStream();
		String content = CharStreams.toString(new InputStreamReader(is));
		JSONObject result = null;
		try {
			result = new JSONObject(content);
			int i = result.getJSONObject("results").getJSONArray("bindings").getJSONObject(0).getJSONObject("cnt").getInt("value");
			return new Integer(i);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}
