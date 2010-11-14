package edu.rpi.tw.eScience.WaterQualityPortal.model;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class Ontology {
	
	public static class PMLP {
		public static final String NS="http://inferenceweb.stanford.edu/2006/06/pml-provenance.owl#";
		
		public static final String Information=NS+"Information";
		public static final String SourceUsage=NS+"SourceUsage";
		public static final String Dataset=NS+"Dataset";
		public static final String DocumentFragmentByRowCol=NS+"DocumentFragmentByRowCol";
		public static final String Organization=NS+"Organization";
		
		public static final String hasSource=NS+"hasSource";
		public static final String hasDocument=NS+"hasDocument";
		public static final String hasToCol=NS+"hasToCol";
		public static final String hasToRow=NS+"hasToRow";
		public static final String hasFromCol=NS+"hasFromCol";
		public static final String hasFromRow=NS+"hasFromRow";
		public static final String hasReferenceSourceUsage=NS+"hasReferenceSourceUsage";
		public static final String hasPublisher=NS+"hasPublisher";
	}
	
	public static class RDFS {
		public static final String NS="http://www.w3.org/2000/01/rdf-schema#";
		
		// Properties
		public static final String label=NS+"label";
	}
	
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
		public static final String hasUnit=NS+"hasUnit";
		public static final String hasValue=NS+"hasValue";
		public static final String hasElement=NS+"hasElement";
	}
	
	public static class GEO {
		public static final String NS="http://www.w3.org/2003/01/geo/wgs84_pos#";
		
		// Classes
		public static final String Point=NS+"Point";
		
		// Properties
		public static final String lat=NS+"lat";
		public static final String lng=NS+"long";
	}
	
	public static class TIME {
		public static final String NS="http://www.w3.org/2006/time#";
		
		// Classes
		public static final String Interval=NS+"Interval";
		public static final String Instant=NS+"Instant";
		
		// Properties
		public static final String inXSDDateTime=NS+"inXSDDateTime";
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
	
	public static OntClass Instant(OntModel m) {
		return m.createClass(TIME.Instant);
	}
	
	public static OntProperty inXSDDateTime(OntModel m) {
		return m.createDatatypeProperty(TIME.inXSDDateTime);
	}
	
	public static OntProperty hasUnit(OntModel m) {
		return m.createDatatypeProperty(EPA.hasUnit);
	}
	
	public static OntProperty hasValue(OntModel m) {
		return m.createDatatypeProperty(EPA.hasValue);
	}
	
	public static OntProperty hasElement(OntModel m) {
		return m.createDatatypeProperty(EPA.hasElement);
	}
	
	public static OntClass Element(OntModel m) {
		return m.createClass(SWEET.ELEM.Element);
	}
	
	public static OntProperty label(OntModel m) {
		return m.createOntProperty(RDFS.label);
	}
	
	public static Resource Information(Model m) {
		return m.createResource(PMLP.Information);
	}
}
