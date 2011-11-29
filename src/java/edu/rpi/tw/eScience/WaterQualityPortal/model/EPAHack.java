package edu.rpi.tw.eScience.WaterQualityPortal.model;

import java.io.IOException;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;

public class EPAHack extends Query {

	String county,state;
	
	public EPAHack(Map<String,String> params) {
		super(null);
		state = params.get("state");
		county = params.get("countyCode");
	}
	
	@Override
	public Object execute(String endpoint) throws IOException {
		return null;
	}

	public Object execute(String endpoint, Model model) throws IOException {
		String url = "http://was.tw.rpi.edu/swqp/facilityData2.php?state="+state;
		url += "&county="+county;
		url += "&start=0&limit=5000&source=EPA&type=facility";
		model.read(url);
		url = "http://was.tw.rpi.edu/swqp/facilityData2.php?state="+state;
		url += "&county="+county;
		url += "&start=0&limit=5000&source=EPA&type=ViolatingFacility";
		model.read(url);
		return null;
	}
}
