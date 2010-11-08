package edu.rpi.tw.eScience.WaterQualityPortal.usgs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

import edu.rpi.tw.eScience.WaterQualityPortal.usgs.MeasurementSite.Measurement;


public final class USGSParser {
	
	static boolean downloadSiteInfo1(String state, String county) {
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
	
	static boolean downloadSiteInfo2(String state, String county) {
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
		
		downloadSiteInfo1("44", "44%3A001");
    	
    	BufferedReader readbuffer1 = new BufferedReader(new FileReader("/tmp/sites.txt"));
    	String line1;
    	boolean first1 = true;
    	while ((line1=readbuffer1.readLine())!=null){
    		if(first1) {
    			first1 = false;
    			continue;
    		}

    		String parts[] = new String[1000];
    		parts = line1.split("\t");
    		String loc_id = parts[2];
    		String lat = parts[11];
    		String longitude = parts[12];
    		String country_code = parts[24]; 
    		String state_code = parts[25];
    		String county_code = parts[26];
    		USGSParser.MeasurementSite x = new USGSParser.MeasurementSite(loc_id, Double.parseDouble(lat), Double.parseDouble(longitude),Integer.parseInt(country_code),Integer.parseInt(state_code),Integer.parseInt(county_code)); 
    		data.put(x.getID(), x);
    		
    		}
    	
    
    	downloadSiteInfo2("44", "44%3A001");
        BufferedReader readbuffer2 = new BufferedReader(new FileReader("/tmp/data.txt"));
    	String line2;
    	boolean first2 = true;
    	while ((line2=readbuffer2.readLine())!=null){
    		if(first2) {
    			first2 = false;
    			continue;
    		}

    		String parts[] = new String[1000];
    		parts = line2.split("\t");
    		String ID = parts[21];//I am confused about what position the elements have to take
    		String date= parts[6];
    		String time = parts[7];
    		String chemical = parts[31]; 
    		String value = parts[33];
    		String unit = parts[34];
    		
    		MeasurementSite temp = data.get(ID);
    		@SuppressWarnings("deprecation")
			USGSParser.MeasurementSite.Measurement x = new USGSParser.MeasurementSite.Measurement(ID,new Date(date),time,chemical,Double.parseDouble(value),unit);
    		temp.addData(x);
    		}
    	for(MeasurementSite x : data.values()) {
    		System.out.println(x.toString());
    	}
    	
    	/*catch(Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    	return true;*/
    	
    	
	}

 	
    	static class MeasurementSite {
    		double lat,longitude;
    		String loc_id;
    		Integer country_code;
    		Integer state_code;
    		Integer county_code;
    		ArrayList<Measurement> data = new ArrayList<Measurement>(); 

			public void setID(String ID) {
				loc_id = ID;
			}

			public void addData(
					edu.rpi.tw.eScience.WaterQualityPortal.usgs.USGSParser.MeasurementSite.Measurement x) {
				data.add(x);
			}

			public String getID() {
				return loc_id;
			}
			
			@Override
			public String toString() {
				String result = "";
				result += "<TestingSite rdf:ID=\"";
				result += "<TestingSite rdf:Country_code=\"";
				result += "<TestingSite rdf:State_code=\"";
				result += "<TestingSite rdf:County_code=\"";
				result += loc_id;
				result += country_code;
				result += state_code;
				result += county_code;
				result += "\">\n";
				result += "<hasLocation>\n";
				result += "<geo:Point>\n";
				result += "<geo:lat rdf:datatype=\"&xsd;float\">"+lat+"</geo:lat>\n";
				result += "<geo:long rdf:datatype=\"&xsd;float\">"+longitude+"</geo:long>\n";
				result += "</geo:Point>\n";
				result += "</hasLocation>\n";
                for(Measurement m : data) {
    				result += "<hasMeasurement>\n";
                	result += m.toString();
                    result += "</hasMeasurement>\n";
                }
				result += "</TestingSite>\n";
				return result;
			}
	
    		public MeasurementSite(String locationID, double latitude, double longitude, Integer countrycode, Integer statecode, Integer countycode)  {
	    	    this.loc_id = locationID;
	    	    this.lat = latitude;
	    	    this.longitude = longitude;
	    	    this.country_code = countrycode;
	    	    this.state_code = statecode;
	    	    this.county_code = countycode;
	    	}
    		
    		static class Measurement {
    			String ID;
    			Date date;
    			String time;
    			String chemical;
    			double value;
    			String unit;
    			
    			@Override
    			public String toString() {
    				String result = "";
    				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    				result += "<Measurement>\n";
    				result += "<hasDate rdf:datatype=\"&xsd;dateTime\">"+df.format(date)+"</hasDate>\n";
                    result += "<hasUnit>"+unit+"</hasUnit>\n";
                    result += "<hasMeasurement>"+value+"</hasMeasurement>\n";
                    result += "<hasElement>\n";
                    result += "<Element rdf:about=\"#"+chemical+"\"/>\n";
                    result += "</hasElement>\n";
    				result += "</Measurement>\n";
    				return result;
    			}
    			
    			@SuppressWarnings("deprecation")
				public Measurement(String identification, Date Startdate, String StartTime, String chemicalname, double value_measurement, String unit_measurement) {
    		    	this.ID = identification; 
    		    	this.date = Startdate;
    		    	this.time = StartTime;
    		    	String[] parts = time.split(":");
    		    	this.date.setHours(Integer.parseInt(parts[0]));
    		    	this.date.setMinutes(Integer.parseInt(parts[1]));
    		    	this.date.setSeconds(Integer.parseInt(parts[2]));
    		        this.chemical = chemicalname;
    		    	this.value = value_measurement;
    		    	this.unit = unit_measurement;  
    		    }

    		}
    	}
}
