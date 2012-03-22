package edu.rpi.tw.eScience.WaterQualityPortal.zip;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.rpi.tw.eScience.WaterQualityPortal.epa.foia.FoiaUnitConverter;
import edu.rpi.tw.eScience.WaterQualityPortal.epa.foia.FoiaUtil;

public class GeonameIdLookup {
	static String stateNameFile = "./states.txt"; 
	static String queryBase="http://ws.geonames.org/search?name_equals=";
	static String adminCode1="&adminCode1=";
	static String queryEnd="&country=US&maxRows=5&type=json";
	static HashMap<String, String> stateGeonameIdCache = null;
	static HashMap<String, String> stateCode2Name=null;
	static {
		stateGeonameIdCache = new HashMap<String, String>();
		stateCode2Name=new HashMap<String, String>();
		buildStateCode2Name();
		FoiaUtil.printHashMap(stateCode2Name);
	}
	
	public static String execute(String stateAbbr) throws Exception {
		String geonameId=stateGeonameIdCache.get(stateAbbr);
		if(geonameId!=null)
			return geonameId;

		String stateName=stateCode2Name.get(stateAbbr);
		if(stateName==null){
			System.err.println("GeonameIdLookup can't get the name for state: "+stateAbbr);
			throw new Exception();
		}
		//http://api.geonames.org/search?name_equals=
		//washington&adminCode1=WA&country=US&maxRows=10&username=demo
		String query = queryBase+stateName+adminCode1+stateAbbr+queryEnd;	
		try {
			URL requestURL = new URL(query);
			URLConnection conn = requestURL.openConnection();
			conn.setReadTimeout(5000);
			InputStream o = (InputStream)conn.getContent();
			InputStreamReader isr = new InputStreamReader(o);
			BufferedReader br = new BufferedReader(isr);
			String result="";
			String line;
			while((line=br.readLine())!=null) result += line;
			System.out.println(result);
			JSONObject content = new JSONObject(result);
			JSONArray codes = content.getJSONArray("geonames");
			if(codes.length()>0) {
				content = codes.getJSONObject(0);				
				geonameId = content.getString("geonameId");
				if(geonameId!=null&&!geonameId.isEmpty())
					stateGeonameIdCache.put(stateAbbr, geonameId);
			}
		}
		catch(java.net.SocketTimeoutException e) {
			throw e;
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return geonameId;
	}

	public static void buildStateCode2Name(){
		FileInputStream fIn = null;
		BufferedReader reader = null;

		try{
			fIn =  new FileInputStream(stateNameFile);
			reader = new BufferedReader(new InputStreamReader(fIn));		
			String strLine;
			String abbr=null, name=null;
			//skip the first line
			reader.readLine();
			while ((strLine = reader.readLine()) != null)   {
				System.out.println(strLine);
				abbr=strLine.substring(0, 2);
				name=strLine.substring(8).trim();
				name=capitalizeString(name);
				//System.out.println(abbr+" "+name);
				if(stateCode2Name.get(abbr)==null)
					stateCode2Name.put(abbr,name);
			}
		}
		catch (Exception e) {
			System.err.println("In buildStateCode2Name, err");
			e.printStackTrace();
		}finally {
			//Close the BufferedReader 			
			try {
				if (reader!=null)
					reader.close ();
			} catch (IOException ex) {
				System.err.println("In buildStateCode2Name, closing the reader");
				ex.printStackTrace();
			}
		}
	}
	
	public static String capitalizeString(String string) {
		char[] chars = string.toLowerCase().toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return String.valueOf(chars);
	}

	
	public static void main(String[] args) {
		GeonameIdLookup.buildStateCode2Name();
		FoiaUtil.printHashMap(GeonameIdLookup.stateCode2Name);
		try {
			System.out.println(GeonameIdLookup.execute("MI"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
