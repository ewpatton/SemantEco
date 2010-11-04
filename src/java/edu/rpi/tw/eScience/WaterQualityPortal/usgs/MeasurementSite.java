package edu.rpi.tw.eScience.WaterQualityPortal.usgs;


import java.util.ArrayList;
import java.util.Date;
import java.util.*;

public class MeasurementSite  {
    USGSParser parser = new USGSParser();
	private String loc_id;
	double lat = Double.parseDouble(parts[2]);
	double longtitude = Double.parseDouble(parts[11]);
	String country_code;
	Integer state_code = Integer.parseInt(parts[24]);
	Integer county_code = Integer.parseInt(parts[12]);
	ArrayList<Measurement> data = new ArrayList<Measurement>(); 
    	
	    class Measurement {
	    	String ID; 
	    	Date date;
	    	String time;
	    	String chemical;
	    	double value;
	    	String unit;  //g/ml
	    	
	    }

		public void setId(String loc_id) {
			this.loc_id = loc_id;
		}

		public String getId() {
			return loc_id;
		}
		   


}
