package edu.rpi.tw.eScience.WaterQualityPortal.data;

import java.net.URL;
import java.util.Date;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

@Deprecated
public interface WaterDataProvider {
	public void setUserSource(String county, String state, String zip);
	public boolean getData(OntModel owlModel, Model pmlModel);
	public boolean getData(OntModel owlModel, Model pmlModel, Date start, Date end);
	public String getName();
	public URL getURL();
}
