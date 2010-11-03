package edu.rpi.tw.eScience.WaterQualityPortal.usgs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;


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
    		

    		String parts[] = new String[10000];
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
		
    }
}