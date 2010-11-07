package edu.rpi.tw.eScience.WaterQualityPortal.usgs;

import java.io.File;
import java.io.FileNotFoundException;
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
	
	boolean downloadSiteInfo1(String state, String county) {
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
	
	boolean downloadSiteInfo2(String state, String county) {
		String firstPart="/Result/search?north=&west=&east=&south=&within=&lat=&long=&statecode=";
		String secondPart="&countycode=";
		String thirdPart="&siteType=&organization=&siteid=&huc=&sampleMedia=&characteristicType=&characteristicName=&pCode=&startDateLo=&startDateHi=&mimeType=tab&bBox=&zip=yes";
		String server="http://qwwebservices.usgs.gov";
		
		//String state;
		//String county;
		
		// User selects two values in interface
		//state = "44";
		//county = "44%3A001";
		
		String query=firstPart+state+secondPart+county+thirdPart;
		String finalQuery = server+query;
		
		// Execute query and save results to /tmp/mydata.zip
		try{
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
		// Extract the zip file
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
		File data = new File("/tmp/data.txt");
		temp.renameTo(data);
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
    	
    	final BufferedReader readbuffer1 = new BufferedReader(new FileReader("/tmp/data.txt"));
    	String line1;
    	while ((line1=readbuffer1.readLine())!=null){
    		

    		String parts[] = new String[1000];
    		parts = line1.split("\t");
    		String loc_id = parts[2];//I am confused about what position the elements have to take
    		String lat = parts[11];
    		String longitude = parts[12];
    		String country_code = parts[24]; 
    		String state_code = parts[25];
    		String county_code = parts[26];// I have problem with the array bounds here
    		MeasurementSite x = new MeasurementSite();//i cannot include the id, lat,long here 
    		data.put(x.getId(), x);//I cannot use get here
    		
    		}
    	
    
    	
        BufferedReader readbuffer2 = new BufferedReader(new FileReader("/tmp/data.txt"));
    	String line2;
    	while ((line2=readbuffer2.readLine())!=null){
    		

    		String parts[] = new String[1000];
    		parts = line2.split("\t");
    		String ID = parts[21];//I am confused about what position the elements have to take
    		String date= parts[6];
    		String time = parts[7];
    		String chemical = parts[31]; 
    		String value = parts[33];
    		String unit = parts[34];// I have problem with the array bounds here
    		Measurement x = new Measurement();//i cannot include the id, lat,long here 
    		MeasurementSite temp = data.getID(); 
    		temp.adddata(x);
    		
    		
    		}
	}  	
    	void MeasurementSite()  {
    	    USGSParser parser = new USGSParser();
    	    String parts[];//not sure for that
    		String loc_id;
    		double lat = Double.parseDouble(parts[11]);
    		double longtitude = Double.parseDouble(parts[12]);
    		String country_code;
    		Integer state_code = Integer.parseInt(parts[25]);
    		Integer county_code = Integer.parseInt(parts[26]);
    		ArrayList<Measurement> data = new ArrayList<Measurement>(); 
    	    	
    	}
    		    void Measurement() {
    		        USGSParser parser = new USGSParser();
    	            String parts [];//not sure for that
    		    	String ID; 
    		    	Date date;
    		    	String time;
    		    	String chemical;
    		    	double value = Double.parseDouble(parts[33]);
    		    	String unit;  //g/ml
    		    	
    		    }

    	/*private static BufferedReader extracted() throws FileNotFoundException {
		final BufferedReader readbuffer = new BufferedReader(new FileReader("/tmp/sites.txt"));
		return readbuffer;
	}*/

    			public void setId(String loc_id) {
    				this.loc_id = loc_id;
    			}

    			public String getId(String loc_id) {
    				return loc_id;
    			}
    			   

    			public void setID(String ID) {
    				this.ID = ID;
    			}

    			public String getID(String ID) {
    				return ID;
    			}
    	
		
    	
    	
    	
    	
    
    	
    	
	
  
	
}
