package edu.rpi.tw.eScience.WaterQualityPortal.model;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;

public class Ontology {
	
	public static class SWEET {
		public static class REPR {
			public static final String NS="http://sweet.jpl.nasa.gov/2.1/repr.owl#";
			
			// Classes
			public static final String Measurement=NS+"Measurement";
		}
		public static class ELEM {
			public static final String NS="http://sweet.jpl.nasa.gov/2.1/matrElement.owl#";
			
			// Classes
			public static final String Element=NS+"Element";
		}
		public static class BODY {
			public static final String NS="http://sweet.jpl.nasa.gov/2.1/realmHydroBody.owl#";
			
			// Classes
			public static final String BodyOfWater=NS+"BodyOfWater";
		}
	}
	
	public static class EPA {
		public static final String NS="http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#";
		
		// Classes
		public static final String MeasurementSite=NS+"MeasurementSite";
		public static final String FacilityMeasurement=NS+"FacilityMeasurement";
		public static final String WaterMeasurement=NS+"WaterMeasurement";
		
		// Properties
		public static final String hasCountryCode=NS+"hasCountryCode";
		public static final String hasStateCode=NS+"hasStateCode";
		public static final String hasCountyCode=NS+"hasCountyCode";
		public static final String hasLocation=NS+"hasLocation";
		public static final String hasMeasurement=NS+"hasMeasurement";
	}
	
	public static class GEO {
		public static final String NS="http://www.w3.org/2003/01/geo/wgs84_pos#";
		
		// Classes
		public static final String Point=NS+"Point";
		
		// Properties
		public static final String lat=NS+"lat";
		public static final String lng=NS+"long";
	}
	
	public static OntClass Measurement(OntModel m) {
		return m.createClass(SWEET.REPR.Measurement);
	}
	
	public static OntClass FacilityMeasurement(OntModel m) {
		return m.createClass(EPA.FacilityMeasurement);
	}
	
	public static OntClass WaterMeasurement(OntModel m) {
		return m.createClass(EPA.WaterMeasurement);
	}
	
	public static OntClass MeasurementSite(OntModel m) {
		return m.createClass(EPA.MeasurementSite);
	}
	
	public static OntProperty hasMeasurement(OntModel m) {
		return m.createObjectProperty(EPA.hasMeasurement);
	}
	
	public static OntProperty hasCountryCode(OntModel m) {
		return m.createDatatypeProperty(EPA.hasCountryCode);
	}
	
	public static OntProperty hasStateCode(OntModel m) {
		return m.createDatatypeProperty(EPA.hasStateCode);
	}
	
	public static OntProperty hasCountyCode(OntModel m) {
		return m.createDatatypeProperty(EPA.hasCountyCode);
	}
	
	public static OntProperty hasLocation(OntModel m) {
		return m.createObjectProperty(EPA.hasLocation);
	}
	
	public static OntClass Point(OntModel m) {
		return m.createClass(GEO.Point);
	}
	
	public static OntProperty lat(OntModel m) {
		return m.createDatatypeProperty(GEO.lat);
	}
	
	public static OntProperty lng(OntModel m) { 
		return m.createDatatypeProperty(GEO.lng);
	}
}
