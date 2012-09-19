package edu.rpi.tw.escience.waterquality.test;

import org.apache.log4j.Logger;

import edu.rpi.tw.escience.waterquality.ModuleConfiguration;
import edu.rpi.tw.escience.waterquality.QueryExecutor;
import edu.rpi.tw.escience.waterquality.QueryFactory;
import edu.rpi.tw.escience.waterquality.Resource;

/**
 * The TestModuleConfiguration is a marker class that can be subclassed by unit tests
 * in order to provide mock configurations to modules.
 * 
 * @author ewpatton
 *
 */
public class TestModuleConfiguration extends ModuleConfiguration {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6080189361308350793L;

	/**
	 * Default constructor used to generate test module configurations
	 */
	public TestModuleConfiguration() {
	}

	@Override
	public String getSparqlEndpoint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryFactory getQueryFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryExecutor getQueryExecutor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource getResource(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource generateStringResource(String content) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

}
