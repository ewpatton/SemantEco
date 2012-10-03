package edu.rpi.tw.escience.waterquality;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

public interface Request {
	String[] getParam(String key);
	Logger getLogger();
	OntModel getModel();
	Model getDataModel();
	Model getCombinedModel();
}
