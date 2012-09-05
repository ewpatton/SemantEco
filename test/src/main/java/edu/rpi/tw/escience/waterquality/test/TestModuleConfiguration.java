package edu.rpi.tw.escience.waterquality.test;

import org.apache.log4j.Logger;

import edu.rpi.tw.escience.waterquality.ModuleConfiguration;
import edu.rpi.tw.escience.waterquality.QueryExecutor;
import edu.rpi.tw.escience.waterquality.QueryFactory;
import edu.rpi.tw.escience.waterquality.Resource;

public class TestModuleConfiguration extends ModuleConfiguration {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6080189361308350793L;

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
