
package edu.rpi.tw.eScience.WaterQualityPortal.model;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;

public class EPAHack extends Query {

	String county,state;
	
	public EPAHack(Map<String,String> params) {
		super(null);
		state = params.get("state");
		county = params.get("countyCode");
		while(county.length() < 3) county = "0"+county;
	}
	
	@Override
	public Object execute(String endpoint) throws IOException {
		return null;
	}

	public Object execute(String endpoint, Model model) throws IOException {
		throw new IOException();
		/*
		if(state.compareTo("CA")==0 				
				|| state.compareTo("NY")==0){
			//|| state.compareTo("MA")==0 
				//|| state.compareTo("RI")==0)
		String url = "http://aquarius.tw.rpi.edu/projects/semantaqua/facilityData2.php?state="+state;
		url += "&county="+county;
		url += "&start=0&limit=5000&source=EPA&type=facility";
		System.err.println("In EPAHack.execute, url"+url);
		model.read(url);
		url = "http://aquarius.tw.rpi.edu/projects/semantaqua/facilityData2.php?state="+state;
		url += "&county="+county;
		url += "&start=0&limit=5000&source=EPA&type=ViolatingFacility";
		System.err.println("In EPAHack.execute, url"+url);
		model.read(url);
		return null;
		}
		else
			throw new IOException();*/
	}
}
