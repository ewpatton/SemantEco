package edu.rpi.tw.eScience.WaterQualityPortal.zip;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public class ZipCodeLookup {
	final String queryBase="http://ws.geonames.org/postalCodeLookupJSON?postalcode=";
	final String queryEnd="&country=US";
	
	static HashMap<String, ZipCodeLookup> cached = new HashMap<String, ZipCodeLookup>();
	static HashMap<String, String> stateLookup = null;
	
	public static void doStateLookup() {
		try {
			stateLookup = new HashMap<String,String>();
			URL requestURL = new URL("http://qwwebservices.usgs.gov/Codes/statecode");
			InputStream o = (InputStream)requestURL.getContent();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(o);
			NodeList nl = doc.getElementsByTagName("Code");
			for(int i=0;i<nl.getLength();i++) {
				Node n = nl.item(i);
				String value = n.getAttributes().getNamedItem("value").getTextContent();
				String desc = n.getAttributes().getNamedItem("desc").getTextContent();
				stateLookup.put(desc.toLowerCase(), value);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static ZipCodeLookup execute(String zipCode) throws Exception {
		if(stateLookup==null) doStateLookup();
		ZipCodeLookup zcl = null;
		if(cached.containsKey(zipCode)) return cached.get(zipCode);
		zcl = new ZipCodeLookup(zipCode);
		if(zcl.loaded()) cached.put(zipCode, zcl);
		else throw new Exception("Invalid zip code");
		return zcl;
	}
	
	String state="";
	String stateAbbr="";
	String zip="";
	String county="";
	String city="";
	String countyNum="",stateNum="";
	double lat,lng;
	boolean loaded;
	
	ZipCodeLookup(String zip) {
		this.zip = zip;
		String query = queryBase+zip+queryEnd;
		try {
			URL requestURL = new URL(query);
			InputStream o = (InputStream)requestURL.getContent();
			InputStreamReader isr = new InputStreamReader(o);
			BufferedReader br = new BufferedReader(isr);
			String result="";
			String line;
			while((line=br.readLine())!=null) result += line;
			JSONObject content = new JSONObject(result);
			JSONArray codes = content.getJSONArray("postalcodes");
			if(codes.length()>0) {
				content = codes.getJSONObject(0);
				state = content.getString("adminName1");
				stateAbbr = content.getString("adminCode1");
				stateNum = stateLookup.get(state.toLowerCase());
				county = content.getString("adminName2");
				countyNum = content.getString("adminCode2");
				city = content.getString("placeName");
				lat = content.getDouble("lat");
				lng = content.getDouble("lng");
				loaded = true;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getCity() {
		return city;
	}
	
	public String getStateName() {
		return state;
	}
	
	public String getStateAbbreviation() {
		return stateAbbr;
	}
	
	public String getStateCode() {
		return stateNum;
	}
	
	public String getCountyName() {
		return county;
	}
	
	public String getCountyCode() {
		return stateNum+":"+countyNum;
	}
	
	public String getZipCode() {
		return zip;
	}
	
	public double getLatitude() {
		return lat;
	}
	
	public double getLongitude() {
		return lng;
	}
	
	public boolean loaded() {
		return loaded;
	}
	
	public String toString() {
		JSONObject result = new JSONObject();
		JSONObject description = new JSONObject();
		try {
			description.put("zipCode", zip);
			description.put("state", state);
			description.put("stateAbbr", stateAbbr);
			description.put("county", county);
			description.put("city", city);
			description.put("stateCode", stateNum);
			description.put("countyCode", getCountyCode());
			description.put("lat", lat);
			description.put("lng", lng);
			result.put("result", description);
		} catch (JSONException e) {
			return "{\"error\":\""+e.toString()+"\"";
		}
		return result.toString();
	}
}
