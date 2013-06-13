package edu.rpi.tw.escience.semanteco.test;

import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.Request;

/**
 * MockRequest provides a base implementation of
 * the Request object for unit tests. All methods
 * throw UnsupportedOperationException by default
 * unless explicitly overridden by a subclass.
 * @author ewpatton
 *
 */
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
	public OntModel getModel(Domain domain) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Model getDataModel(Domain domain) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Model getCombinedModel(Domain domain) {
		throw new UnsupportedOperationException();
	}

	@Override
	public URL getOriginalURL() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean canLogProvenance() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void logProvenance(String graph, String contents) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Domain> listActiveDomains() {
		throw new UnsupportedOperationException();
	}

}
