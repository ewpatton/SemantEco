package edu.rpi.tw.escience.waterquality.test;

import org.apache.log4j.Logger;

import edu.rpi.tw.escience.waterquality.ModuleConfiguration;
import edu.rpi.tw.escience.waterquality.QueryExecutor;
import edu.rpi.tw.escience.waterquality.QueryFactory;
import edu.rpi.tw.escience.waterquality.Request;
import edu.rpi.tw.escience.waterquality.Resource;

public class TestModuleConfiguration extends ModuleConfiguration {

	private static final long serialVersionUID = 1L;
	
	public TestQueryExecutor executor = new TestQueryExecutor();
	public TestQueryFactory factory = new TestQueryFactory();

	@Override
	public String getSparqlEndpoint() {
		return executor.getDefaultSparqlEndpoint();
	}

	@Override
	public QueryFactory getQueryFactory() {
		return factory;
	}

	@Override
	public QueryExecutor getQueryExecutor(final Request request) {
		return executor;
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
		return Logger.getRootLogger();
	}

}
