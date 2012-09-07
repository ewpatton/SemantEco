package edu.rpi.tw.escience.WaterQualityPortal.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Util {
	static int BUFFER_SIZE = 4096;
	
	public static String makeCountyID(String state, String county) {
		while(county.length()<3) county = "0"+county;
		return state+county;
	}
	
	public static JSONObject queryEndpoint(String endpoint, String queryStr){
		//System.out.println(queryStr);
		String target=null;
		try {
			target = endpoint+"?query="+URLEncoder.encode(queryStr, "UTF-8")
					+"&format="+URLEncoder.encode("application/json","UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.err.println("In queryForCloseSites, "+ e.getMessage());
			e.printStackTrace();
		}
		
		JSONObject res=Util.readJsonFromUrl(target);
		return res;
	}
	
	public static JSONObject readJsonFromUrl(String target){
    	//Phase1: do the get
		URL url = null;
		HttpURLConnection conn = null; 
		StringBuilder replyBuffer=new StringBuilder();
		
		try {			
			url = new URL(target);
			conn = (HttpURLConnection)url.openConnection();			
			// Set connection parameters.
			conn.setDoInput (true);
			conn.setDoOutput (true);
			conn.setUseCaches (false);			
			//conn.setRequestMethod("GET");			
			conn.connect();
		} catch (MalformedURLException e) {
			System.err.println("readJsonFromUrl: " + e.getMessage());
			e.printStackTrace();
		}	
		catch (IOException e) {
			System.out.println("In readJsonFromUrl(), writing");
			e.printStackTrace();
		}
		
		//Phase2: get the reply
		@SuppressWarnings("unused")
		String contentType = conn.getContentType();		
		char[] buffer = null;
		int numChars=0;
		BufferedReader in = null;
		// Read response from the input stream.
		try{	
			in = new BufferedReader (new InputStreamReader(conn.getInputStream ()));
			// Read (and print) till end of file
			buffer = new char[BUFFER_SIZE];
		    while ((numChars=in.read(buffer,0, BUFFER_SIZE))!= -1)
		    	//bufferedWriter.write(buffer,0, numChars);
		    	replyBuffer.append(buffer,0, numChars);	
			
		} catch (IOException e) {
			System.err.println("In readJsonFromUrl(), reading data and writing to file");
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} finally {
            //Close the BufferedReader		
            try {
            	conn = null;
            	if (in!=null)
            		in.close ();
            } catch (IOException ex) {
            	System.out.println("In readJsonFromUrl(), " + ex.getMessage());
            }
        }
		
		System.out.print(replyBuffer);
		JSONObject effects=null;
		try {
			effects = new JSONObject(replyBuffer.toString());
		} catch (JSONException e) {
			System.out.println("In getCSVFile(), closing the BufferedReader");
			e.printStackTrace();
		}
		 return effects;
	}
	
	public static boolean isEmpty(JSONObject obj){
		JSONArray bindings=null;
		try {
			bindings = obj.getJSONObject("results").getJSONArray("bindings");
		} catch (JSONException e) {
			//e.printStackTrace();
			System.err.println(e.getMessage());
			return true;
		}
		return (bindings==null || bindings.length()==0);
	}
}
