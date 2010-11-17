package edu.rpi.tw.eScience.WaterQualityPortal.usgs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.rpi.tw.eScience.WaterQualityPortal.model.Ontology;

public final class USGSParser {
	
	static String downloadSiteInfo1(String state, String county) {
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
			return null;
		}
		return finalQuery;
	}
	
	static String downloadSiteInfo2(String state, String county) {
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
		return null;
	}
	return finalQuery;
}


	public static String[] split(String x, String regex) {
		int count=1;
		int start=0;
		while(x.indexOf(regex, start)>-1) {
			start = x.indexOf(regex, start)+regex.length();
			count++;
		}
		String[] parts = new String[count];
		start = 0;
		while(true) {
			int end = x.indexOf(regex);
			if(end==-1) {
				parts[start] = x;
				break;
			}
			parts[start] = x.substring(0,end);
			x = x.substring(end+regex.length());
			start++;
		}
		return parts;
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
    		parts = split(line1,"\t");
            String loc_id = parts[2];
            String label = parts[3];
    		String lat = parts[11];
    		String longitude = parts[12];
    		String country_code = parts[24]; 
    		String state_code = parts[25];
    		String county_code = parts[26];
    		MeasurementSite x = new MeasurementSite(loc_id, label, Double.parseDouble(lat), Double.parseDouble(longitude),country_code,Integer.parseInt(state_code),Integer.parseInt(county_code)); 
    		data.put(x.getID(), x);
    		
    		}
    	
    
    	downloadSiteInfo2("44", "44%3A001");
        BufferedReader readbuffer2 = new BufferedReader(new FileReader("/tmp/data.txt"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	String line2;
    	boolean first2 = true;
    	int counter = 0;
    	while ((line2=readbuffer2.readLine())!=null){
    		if(first2) {
    			first2 = false;
    			continue;
    		}

    		String parts[] = new String[1000];
    		parts = split(line2,"\t");
    		String ID = parts[21];
    		String date= parts[6];
    		String time = parts[7];
    		String chemical = parts[31]; 
    		String value = parts[33];
    		String unit = parts[34];
    		
    		MeasurementSite temp = data.get(ID);
    		if(value.equals("")) value = "0";
    		if(date.equals("") || time.equals("")) continue;
			Measurement x = new Measurement(ID,counter++,sdf.parse(date),time,chemical,Double.parseDouble(value),unit);
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

 	
	Collection<MeasurementSite> process(String stateCode, String countyCode) throws Exception {
		HashMap<String,MeasurementSite> data = new HashMap<String,MeasurementSite>();
		
		String source = downloadSiteInfo1(stateCode, countyCode.replaceAll(":", "%3A"));
    	
    	BufferedReader readbuffer1 = new BufferedReader(new FileReader("/tmp/sites.txt"));
    	String line1;
    	boolean first1 = true;
    	int counter = 0;
    	while ((line1=readbuffer1.readLine())!=null){
    		if(first1) {
    			first1 = false;
    			continue;
    		}
    		counter++;
    		String parts[];
    		parts = split(line1,"\t");
            String loc_id = parts[2];
            String label = parts[3];
    		String lat = parts[11];
    		String longitude = parts[12];
    		String country_code = parts[24]; 
    		String state_code = parts[25];
    		String county_code = parts[26];
    		MeasurementSite x = new MeasurementSite(loc_id, label, Double.parseDouble(lat), Double.parseDouble(longitude),country_code,Integer.parseInt(state_code),Integer.parseInt(county_code));
    		x.setSourceDocument(source, counter);
    		data.put(x.getID(), x);
    	}
    	
    	source = downloadSiteInfo2(stateCode, countyCode.replaceAll(":", "%3A"));
        BufferedReader readbuffer2 = new BufferedReader(new FileReader("/tmp/data.txt"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	String line2;
    	boolean first2 = true;
    	counter = 0;
    	while ((line2=readbuffer2.readLine())!=null){
    		if(first2) {
    			first2 = false;
    			continue;
    		}
    		String parts[];
    		parts = split(line2,"\t");
    		String ID = parts[21];
    		String date= parts[6];
    		String time = parts[7];
    		String chemical = parts[31]; 
    		String value = parts[33];
    		String unit = parts[34];
    		
    		MeasurementSite temp = data.get(ID);
    		if(value.equals("")) value = "0";
    		if(date.equals("") || time.equals("")) continue;
			Measurement x = new Measurement(ID,counter++,sdf.parse(date),time,chemical,Double.parseDouble(value),unit);
			x.setSourceDocument(source, counter);
    		temp.addData(x);
    	}
    	return data.values();
	}

	public boolean getData(String stateCode, String countyCode,
			OntModel owlModel, Model pmlModel) {
		try {
			Resource usgs = pmlModel.createResource(Ontology.EPA.NS+"USGS",pmlModel.createResource(Ontology.PMLP.Organization));
			usgs.addLiteral(RDFS.label, "United States Geological Survey");
			Collection<MeasurementSite> data = process(stateCode, countyCode);
			for(MeasurementSite m : data) {
				m.asIndividual(owlModel, pmlModel);
			}
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
