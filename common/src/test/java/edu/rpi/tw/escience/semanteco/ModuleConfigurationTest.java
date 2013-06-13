package edu.rpi.tw.escience.semanteco;

import java.net.URI;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.QueryExecutor;
import edu.rpi.tw.escience.semanteco.QueryFactory;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.Resource;
import junit.framework.TestCase;

public class ModuleConfigurationTest extends TestCase {

	private static class TestModuleConfiguration extends ModuleConfiguration {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5077757212122286462L;

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
		public QueryExecutor getQueryExecutor(Request request) {
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

		@Override
		public Domain getDomain(URI uri, boolean create) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Domain> listDomains() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	@Test
	public void testConstructor() {
		new TestModuleConfiguration();
	}
}
