package edu.rpi.tw.eScience.WaterQualityPortal.usgs;


import java.util.*;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.eScience.WaterQualityPortal.model.Ontology;

public class MeasurementSite {
	double lat,longitude;
	String loc_id;
	String country_code;
	Integer state_code;
	Integer county_code;
	ArrayList<Measurement> data = new ArrayList<Measurement>(); 
	
	public void setID(String ID) {
		loc_id = ID;
	}
	
	public void addData(Measurement x) {
		data.add(x);
	}
	
	public String getID() {
		return loc_id;
	}
	
	@Override
	public String toString() {
		String result = "";
		result += "<TestingSite rdf:ID=\""+loc_id+"\">\n";
		result += "<hasCountryCode>"+country_code+"</hasCountryCode>\n";
		result += "<hasStateCode>"+state_code+"</hasStateCode>\n";
		result += "<hasCountyCode>"+county_code+"</hasCountyCode>\n";
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
	
	public Individual asIndividual(OntModel owlModel, Model pmlModel) {
		String uri = Ontology.EPA.NS+"site-"+loc_id;
		OntClass MeasurementSite = Ontology.MeasurementSite(owlModel);
		Individual site = owlModel.createIndividual(uri, MeasurementSite);
		OntProperty prop = Ontology.hasCountryCode(owlModel);
		site.addLiteral(prop, country_code);
		prop = Ontology.hasStateCode(owlModel);
		site.addLiteral(prop, state_code);
		prop = Ontology.hasCountyCode(owlModel);
		site.addLiteral(prop, county_code);
		site.addOntClass(Ontology.Point(owlModel));
		prop = Ontology.lat(owlModel);
		site.addLiteral(prop, lat);
		prop = Ontology.lng(owlModel);
		site.addLiteral(prop, longitude);
		prop = Ontology.hasMeasurement(owlModel);
		for(Measurement m : data) {
			Individual item = m.asIndividual(owlModel, pmlModel);
			site.addProperty(prop, item);
		}
		return site;
	}
	
	public MeasurementSite(String locationID, double latitude, double longitude, String countrycode, Integer statecode, Integer countycode)  {
	    this.loc_id = locationID;
	    this.lat = latitude;
	    this.longitude = longitude;
	    this.country_code = countrycode;
	    this.state_code = statecode;
	    this.county_code = countycode;
	}
}