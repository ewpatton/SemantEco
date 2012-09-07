package edu.rpi.tw.escience.WaterQualityPortal.data;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

@Deprecated
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
	public boolean getData(OntModel owlModel, Model pmlModel) {
		owlModel.read("src/rdf/cleanwater.owl");
		Resource FacilityClass = owlModel.getResource(ns+"Facility");
		Property hasMeasurement = owlModel.getProperty(ns+"hasMeasurement");
		Individual facility = owlModel.createIndividual(ns+"ID", FacilityClass);
		for(Measurement item : getMeasurements()) {
			Individual measurement = item.asIndividual(owlModel);
			facility.addProperty(hasMeasurement, measurement);
		}
		return true;
	}

	@Override
	public boolean getData(OntModel owlModel, Model pmlModel, 
			Date start, Date end) {
		// TODO Auto-generated method stub
		return true;
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
