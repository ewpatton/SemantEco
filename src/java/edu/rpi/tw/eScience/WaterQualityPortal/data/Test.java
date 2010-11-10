package edu.rpi.tw.eScience.WaterQualityPortal.data;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class Test implements WaterDataProvider {

	final static String ns = "http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#";
	
	static class Measurement {

		public Individual asIndividual(OntModel m) {
			Resource MeasurementClass = m.getResource(ns+"Measurement");
			Individual me = m.createIndividual(MeasurementClass);
			
			return me;
		}
		
	}
	
	public List<Measurement> getMeasurements() {
		return new ArrayList<Measurement>();
	}
	
	@Override
	public Model getData() {
		OntModel m = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		m.read("src/rdf/cleanwater.owl");
		Resource FacilityClass = m.getResource(ns+"Facility");
		Property hasMeasurement = m.getProperty(ns+"hasMeasurement");
		Individual facility = m.createIndividual(ns+"ID", FacilityClass);
		for(Measurement item : getMeasurements()) {
			Individual measurement = item.asIndividual(m);
			facility.addProperty(hasMeasurement, measurement);
		}
		// TODO Auto-generated method stub
		return m;
	}

	@Override
	public Model getData(Date start, Date end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUserSource(String county, String state, String zip) {
		// TODO Auto-generated method stub

	}

}
