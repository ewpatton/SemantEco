package edu.rpi.tw.eScience.WaterQualityPortal.usgs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

import edu.rpi.tw.eScience.WaterQualityPortal.usgs.MeasurementSite.Measurement;


public final class USGSParser {
	
	boolean downloadSiteInfo(String state, String county) {
		String firstPart="/Station/search?north=&west=&east=&south=&within=&lat=&long=&statecode=";
		String secondPart="&countycode=";
		String thirdPart="&siteType=&organization=&siteid=&huc=&sampleMedia=&characteristicType=&characteristicName=&pCode=&startDateLo=&startDateHi=&mimeType=tab&bBox=&zip=yes";
		String server="http://qwwebservices.usgs.gov";

		// User selects two values in interface.
		//state = "44";
		//county = "44%3A001";

		String query=firstPart+state+secondPart+county+thirdPart;
		String finalQuery = server+query;
		
		// Execute query and save results to /tmp/mydata.zip
		try {
			URL command = new URL(finalQuery);
			InputStream zipContents = command.openStream();
			File temp = new File("/tmp/mydata.zip");
			temp.createNewFile();
			FileOutputStream out = new FileOutputStream(temp);
			int x;
			while((x = zipContents.read())!=-1) {
				out.write(x);
			}
			out.close();		
			// Extract the zip file.
			String cmdarray[] = new String[4];
			cmdarray[0] = "/usr/bin/unzip";
			cmdarray[1] = "-d";
			cmdarray[2] = "/tmp/";
			cmdarray[3] = "/tmp/mydata.zip";
			Process unzip = Runtime.getRuntime().exec(cmdarray);
			unzip.waitFor();
			temp.delete();
			
			// There should now be a file at /tmp/data.tsv
			temp = new File("/tmp/data.tsv");
			File sites = new File("/tmp/sites.txt");
			temp.renameTo(sites);
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * @param args
	 * @return 
	 * @return 
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws Exception{
		
        //@SuppressWarnings("unused")
		HashMap<String,MeasurementSite> data = new HashMap<String,MeasurementSite>();
    	
    	BufferedReader readbuffer = new BufferedReader(new FileReader("/tmp/sites.txt"));
    	String line;
    	while ((line=readbuffer.readLine())!=null){
    		

    		String parts[] = new String[1000];
    		parts = line.split("\t");
    		String loc_id = parts[2];//I am confused about what position the elements have to take
    		String lat = parts[11];
    		String longitude = parts[12];
    		String country_code = parts[24]; 
    		String state_code = parts[25];
    		String county_code = parts[26];// I have problem with the array bounds here
    		MeasurementSite x = new MeasurementSite();//i cannot include the id, lat,long here 
    		data.put(x.getId(), x);//I cannot use get here
    		
    		}
    	
    	/*MeasurementSite()  {
    	    USGSParser parser = new USGSParser();
    	    String parts [];//not sure for that
    		private String loc_id;
    		double lat = Double.parseDouble(parts[2]);
    		double longtitude = Double.parseDouble(parts[11]);
    		String country_code;
    		Integer state_code = Integer.parseInt(parts[24]);
    		Integer county_code = Integer.parseInt(parts[12]);
    		ArrayList<Measurement> data = new ArrayList<Measurement>(); 
    	    	
    	}
    		    void Measurement() {
    		    	String ID; 
    		    	Date date;
    		    	String time;
    		    	String chemical;
    		    	double value;
    		    	String unit;  //g/ml
    		    	
    		    }*/

    			public void setId(String loc_id) {
    				this.loc_id = loc_id;
    			}

    			public String getId() {
    				return loc_id;
    			}
    			   


    	
		
    	
    	
    	
    	
    
    	
    	
	
}  
	
}
