package edu.rpi.tw.escience.waterquality.test;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.Request;

public class MockRequest implements Request {

	@Override
	public Object getParam(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Logger getLogger() {
		throw new UnsupportedOperationException();
	}

	@Override
	public OntModel getModel() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Model getDataModel() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Model getCombinedModel() {
		throw new UnsupportedOperationException();
	}

}
