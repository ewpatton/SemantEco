package edu.rpi.tw.eScience.WaterQualityPortal.usgs;

import java.net.URL;
import java.util.Date;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.eScience.WaterQualityPortal.data.WaterDataProvider;

public class DataService implements WaterDataProvider {

	String countyCode="";
	String stateCode="";
	String zipCode="";
	final URL location = new URL("http://qwwebservices.usgs.gov/portal.html");
	
	public DataService() throws Exception {
		
	}
	
	@Override
	public boolean getData(OntModel owlModel, Model pmlModel) {
		USGSParser parser = new USGSParser();
		return parser.getData(stateCode,countyCode,owlModel,pmlModel);
	}

	@Override
	public boolean getData(OntModel owlModel, Model pmlModel, Date start, Date end) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getName() {
		return "USGS National Water Information System";
	}

	@Override
	public URL getURL() {
		return location;
	}

	@Override
	public void setUserSource(String county, String state, String zip) {
		countyCode = county;
		stateCode = state;
		zipCode = zip;
	}

}
