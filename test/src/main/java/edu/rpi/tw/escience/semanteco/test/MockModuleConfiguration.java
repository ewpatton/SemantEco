package edu.rpi.tw.escience.semanteco.test;

import java.net.URI;
import java.util.List;

import org.apache.log4j.Logger;

import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.QueryExecutor;
import edu.rpi.tw.escience.semanteco.QueryFactory;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.Resource;

/**
 * The TestModuleConfiguration is a marker class that can be subclassed by unit tests
 * in order to provide mock configurations to modules.
 * 
 * @author ewpatton
 *
 */
public class MockModuleConfiguration extends ModuleConfiguration {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6080189361308350793L;

	/**
	 * Default constructor used to generate test module configurations
	 */
	public MockModuleConfiguration() {
	}

	@Override
	public String getSparqlEndpoint() {
		throw new UnsupportedOperationException();
	}

	@Override
	public QueryFactory getQueryFactory() {
		throw new UnsupportedOperationException();
	}

	@Override
	public QueryExecutor getQueryExecutor(Request request) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Resource getResource(String path) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Resource generateStringResource(String content) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Logger getLogger() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Domain getDomain(URI uri, boolean create) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Domain> listDomains() {
		throw new UnsupportedOperationException();
	}

}
