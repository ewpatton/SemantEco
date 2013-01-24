package edu.rpi.tw.escience.semanteco.zipcode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

/**
 * ZipCodeLookup provides the core functionality of the ZipCodeModule
 * by interacting with external web services to determine the appropriate
 * state, county, latitude, and longitude for the specified ZIP code.
 * 
 * @author ewpatton
 *
 */
public final class ZipCodeLookup {
	private static final String QUERY_BASE="http://ws.geonames.org/postalCodeLookupJSON?postalcode=";
	private static final String QUERY_END="&country=US";
	@SuppressWarnings("unused")
	private final Logger log;
	
	private static final int TIMEOUT = 5000;
	private static final double DEFAULT_LAT = 41.6842;
	private static final double DEFAULT_LONG = -71.26866;
	private static final String US_PREFIX="US:";
	
	private static Map<String, ZipCodeLookup> cached = new HashMap<String, ZipCodeLookup>();
	private static Map<String, String> stateLookup = null;

	private String state="";
	private String stateAbbr="";
	private String zip="";
	private String county="";
	private String city="";
	private String countyNum="",stateNum="";
	private double lat,lng;
	private boolean loaded;
	
	/**
	 * Retrieves a mapping of state codes from the USGS
	 * 
	 * @param log
	 */
	protected static void doStateLookup(Logger log) {
		log.trace("doStateLookup");
		try {
			log.debug("Contacting USGS state code web service...");
			long start = System.currentTimeMillis();
			stateLookup = new HashMap<String,String>();
			URL requestURL = new URL("http://qwwebservices.usgs.gov/Codes/statecode");
			InputStream o = (InputStream)requestURL.getContent();
			log.debug("...finished in "+(System.currentTimeMillis()-start)+" ms");
			log.debug("Processing USGS response");
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
			log.warn("Unexpected exception", e);
		}
	}
	
	/**
	 * Initializes the cache system
	 * 
	 * @param log
	 */
	protected static void init(Logger log) {
		ZipCodeLookup zcl = new ZipCodeLookup(log);
		cached.put("02809", zcl);
	}
	
	/**
	 * Executes a ZIP code lookup and returns an object
	 * containing the results
	 * @param zipCode The ZIP Code to look up
	 * @param log A log were status information can be logged
	 * @return
	 * @throws ServerFailedToRespondException
	 */
	public static ZipCodeLookup execute(String zipCode, Logger log) throws ServerFailedToRespondException {
		log.trace("execute");
		if(stateLookup==null) {
			doStateLookup(log);
		}
		if(!cached.containsKey("02809")) {
			init(log);
		}
		ZipCodeLookup zcl = null;
		if(cached.containsKey(zipCode)) {
			return cached.get(zipCode);
		}
		zcl = new ZipCodeLookup(zipCode, log);
		if(zcl.loaded()) {
			cached.put(zipCode, zcl);
		}
		else {
			throw new IllegalArgumentException("Invalid zip code");
		}
		return zcl;
	}
	
	/**
	 * ServerFailedToRespondException is thrown when an invalid response
	 * or a communication timeout occurs.
	 * 
	 * @author ewpatton
	 *
	 */
	public final class ServerFailedToRespondException extends Exception {
		private static final long serialVersionUID = 4312453358087597654L;
		
		protected ServerFailedToRespondException(Exception e) {
			super(e);
		}
	}
	
	private ZipCodeLookup(Logger log) {
		this.log = log;
		state="Rhode Island";
		stateAbbr="RI";
		zip="02809";
		county="Bristol";
		city="Bristol";
		countyNum="001";
		stateNum="44";
		lat = DEFAULT_LAT;
		lng = DEFAULT_LONG;
	}
	
	private ZipCodeLookup(String zip, Logger log)
			throws ServerFailedToRespondException {
		log.trace("ZipCodeLookup");
		this.log = log;
		this.zip = zip;
		String query = QUERY_BASE+zip+QUERY_END;
		BufferedReader br = null;
		try {
			log.debug("Connecting to Geonames service...");
			long start = System.currentTimeMillis();
			URL requestURL = new URL(query);
			URLConnection conn = requestURL.openConnection();
			conn.setReadTimeout(TIMEOUT);
			InputStream o = (InputStream)conn.getContent();
			InputStreamReader isr = new InputStreamReader(o);
			br = new BufferedReader(isr);
			String result="";
			String line;
			while((line=br.readLine())!=null) {
				result += line;
			}
			log.debug("...finished in "+(System.currentTimeMillis()-start)+" ms");
			JSONObject content = new JSONObject(result);
			JSONArray codes = content.getJSONArray("postalcodes");
			if(codes.length()>0) {
				content = codes.getJSONObject(0);
				state = content.getString("adminName1");
				stateAbbr = content.getString("adminCode1");
				stateNum = stateLookup.get(state.toLowerCase());
				if(stateNum.contains(US_PREFIX)) {
					stateNum = stateNum.replace(US_PREFIX, "");
				}
				county = content.getString("adminName2");
				countyNum = content.getString("adminCode2");
				if(countyNum.contains(US_PREFIX)) {
					countyNum = countyNum.replace(US_PREFIX, "");
				}
				if(countyNum.contains(":")) {
					countyNum = countyNum.split(":")[1];
				}
				city = content.getString("placeName");
				lat = content.getDouble("lat");
				lng = content.getDouble("lng");
				loaded = true;
			}
		}
		catch(java.net.SocketTimeoutException e) {
			throw new ServerFailedToRespondException(e);
		}
		catch(Exception e) {
			log.warn("Unable to perform zip code lookup", e);
		}
		finally {
			try {
				if(br != null) {
					br.close();
				}
			}
			catch(IOException e) {
				// do nothing if we fail trying to close the socket
			}
		}
	}
	
	/**
	 * Gets the city returned by Geonames for the ZIP code
	 * @return
	 */
	public String getCity() {
		return city;
	}
	
	/**
	 * Gets the full name of the state returned by Geonames for the ZIP code
	 * @return
	 */
	public String getStateName() {
		return state;
	}
	
	/**
	 * Gets the state abbreviation returned by Geonames for the ZIP code
	 * @return
	 */
	public String getStateAbbreviation() {
		return stateAbbr;
	}
	
	/**
	 * Gets the state code returned by Geonames for the ZIP code
	 * @return
	 */
	public String getStateCode() {
		return stateNum;
	}
	
	/**
	 * Gets the county name returned by Geonames for the ZIP code
	 * @return
	 */
	public String getCountyName() {
		return county;
	}
	
	/**
	 * Gets the full county code as it would be returned by Geonames
	 * @return
	 */
	public String getCountyCode() {
		return stateNum+":"+countyNum;
	}
	
	/**
	 * Gets the ZIP code that was requested
	 * @return
	 */
	public String getZipCode() {
		return zip;
	}
	
	/**
	 * Gets the latitude returned by Geonames for the ZIP code
	 * @return
	 */
	public double getLatitude() {
		return lat;
	}
	
	/**
	 * Gets the longitude returned by Geonames for the ZIP code
	 * @return
	 */
	public double getLongitude() {
		return lng;
	}
	
	/**
	 * Returns whether the information was correctly loaded
	 * @return
	 */
	public boolean loaded() {
		return loaded;
	}
	
	@Override
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
			description.put("countyCode", countyNum);
			description.put("lat", lat);
			description.put("lng", lng);
			result.put("result", description);
		} catch (JSONException e) {
			return "{\"error\":\""+e.toString()+"\"";
		}
		return result.toString();
	}
}
