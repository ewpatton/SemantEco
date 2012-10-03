package test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.rpi.tw.escience.waterquality.QueryExecutor;
import edu.rpi.tw.escience.waterquality.QueryFactory;
import edu.rpi.tw.escience.waterquality.Resource;
import edu.rpi.tw.escience.waterquality.datasource.DataSourceModule;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.Query.Type;
import edu.rpi.tw.escience.waterquality.query.impl.QueryImpl;
import edu.rpi.tw.escience.waterquality.test.MockModuleConfiguration;
import edu.rpi.tw.escience.waterquality.test.MockQuery;
import edu.rpi.tw.escience.waterquality.test.MockQueryExecutor;
import edu.rpi.tw.escience.waterquality.test.MockQueryFactory;
import edu.rpi.tw.escience.waterquality.test.MockRequest;
import edu.rpi.tw.escience.waterquality.test.MockResource;
import edu.rpi.tw.escience.waterquality.test.MockUI;

import junit.framework.TestCase;

public class DataSourceModuleTest extends TestCase {

	private static class TestRequest extends MockRequest {

		Map<String, String[]> params = new TreeMap<String, String[]>();
		
		@Override
		public String[] getParam(String key) {
			return params.get(key);
		}

		public void setParam(String key, String[] values) {
			params.put(key, values);
		}
		
		@Override
		public Logger getLogger() {
			return Logger.getRootLogger();
		}
		
	}
	
	private static class TestModuleConfiguration extends MockModuleConfiguration {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		TestQueryFactory queryFactory = new TestQueryFactory();
		QueryExecutor queryExecutor = new TestQueryExecutor();
		
		@Override
		public QueryFactory getQueryFactory() {
			return queryFactory;
		}
		
		@Override
		public QueryExecutor getQueryExecutor() {
			return queryExecutor;
		}
		
		@Override
		public Resource getResource(String name) {
			return new TestResource(name);
		}
		
		@Override
		public Resource generateStringResource(String content) {
			return new TestStringResource(content);
		}
	}
	
	private static class TestResource extends MockResource {
		@SuppressWarnings("unused")
		String path = null;
		public TestResource(String path) {
			this.path = path;
		}
	}
	
	private static class TestStringResource extends MockResource {
		@SuppressWarnings("unused")
		String content = null;
		public TestStringResource(String content) {
			this.content = content;
		}
	}
	
	private static class TestQueryFactory extends MockQueryFactory {
		@Override
		public Query newQuery() {
			return newQuery(Type.SELECT);
		}
		
		@Override
		public Query newQuery(Type type) {
			return new TestQueryImpl(type);
		}
	}
	
	private static class TestQueryImpl extends QueryImpl {
		public TestQueryImpl(Type type) {
			super(type);
		}
	}
	
	private static class TestQueryExecutor extends MockQueryExecutor {
		
		public String response = null;
		public String logdResponse = null;
		
		@Override
		public QueryExecutor accept(String mimeType) {
			try {
				response = getResource("/test001.json");
			} catch (Exception e) { }
			return this;
		}
		
		@Override
		public String execute(Query query) {
			return response;
		}
		
		@Override
		public String execute(String endpoint, Query query) {
			return logdResponse;
		}
	}
	
	private static class TestUI extends MockUI {
		Set<Resource> facets = new HashSet<Resource>();
		
		@Override
		public void addFacet(Resource res) {
			facets.add(res);
		}
	}
	
	@Test
	public void testGetName() {
		DataSourceModule module = new DataSourceModule();
		assertEquals("Data Source", module.getName());
	}
	
	@Test
	public void testVersion() {
		DataSourceModule module = new DataSourceModule();
		assertEquals(1, module.getMajorVersion());
		assertEquals(0, module.getMinorVersion());
		assertNull(module.getExtraVersion());
	}
	
	@Test
	public void testModuleConfiguration() {
		TestModuleConfiguration config = new TestModuleConfiguration();
		DataSourceModule module = new DataSourceModule();
		module.setModuleConfiguration(config);
	}
	
	@Test
	public void testVisitModel() throws Exception {
		TestModuleConfiguration config = new TestModuleConfiguration();
		Model model = ModelFactory.createDefaultModel();
		TestRequest request = new TestRequest();
		DataSourceModule module = new DataSourceModule();
		module.setModuleConfiguration(config);
		try {
			module.visit(model, request);
			fail();
		}
		catch(IllegalArgumentException e) {
			
		}
		try {
			request.setParam("source", new String[] { "http://sparql.tw.rpi.edu/garbage" });
			module.visit(model, request);
			fail();
		}
		catch(IllegalArgumentException e) {
			
		}
		request.setParam("source", new String[] { "http://sparql.tw.rpi.edu/source/usgs-gov", "http://sparql.tw.rpi.edu/source/epa-gov" });
		request.setParam("state",new String[] { "RI" });
		request.setParam("county",new String[] { "1" });
		request.setParam("zip",new String[] { "02809" });
		request.setParam("lat", new String[] { "41.6842" });
		request.setParam("lng", new String[] { "-71.26866" });
		request.setParam("limit", new String[] { "{\"facility\":{\"offset\":0,\"limit\":0},\"site\":{\"offset\":0,\"limit\":2}}" });
//		module.visit(model, request);
//		try {
//			config.queryExecutor = new TestQueryExecutor2();
//		}
//		catch(Exception e) { }
//		module.visit(model, request);
	}
	
	@Test
	public void testVisitOntModel() {
		TestModuleConfiguration config = new TestModuleConfiguration();
		OntModel model = ModelFactory.createOntologyModel();
		TestRequest request = new TestRequest();
		DataSourceModule module = new DataSourceModule();
		module.setModuleConfiguration(config);
		module.visit(model, request);
	}
	
	@Test
	public void testVisitQuery() {
		TestModuleConfiguration config = new TestModuleConfiguration();
		MockQuery query = new MockQuery();
		TestRequest request = new TestRequest();
		DataSourceModule module = new DataSourceModule();
		module.setModuleConfiguration(config);
		module.visit(query, request);
	}
	
	@Test
	public void testVisitUI() throws Exception {
		TestModuleConfiguration config = new TestModuleConfiguration();
		TestUI ui = new TestUI();
		TestRequest request = new TestRequest();
		DataSourceModule module = new DataSourceModule();
		module.setModuleConfiguration(config);
		module.visit(ui, request);
		((TestQueryExecutor)config.queryExecutor).response = getResource("/test001.json");
		module.visit(ui, request);
	}
	
	@Test
	public void testQueryForDataSources() throws Exception {
		TestModuleConfiguration config = new TestModuleConfiguration();
		TestRequest request = new TestRequest();
		DataSourceModule module = new DataSourceModule();
		module.setModuleConfiguration(config);
		module.queryForDataSources(request);
		((TestQueryExecutor)config.queryExecutor).response = getResource("/test001.json");
		module.queryForDataSources(request);
	}
	
	protected static String getResource(String name) throws Exception {
		final int BUFSIZE = 1024;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = DataSourceModuleTest.class.getResourceAsStream(name);
		final byte[] buffer = new byte[BUFSIZE];
		int read = 0;
		while((read = is.read(buffer))>0) {
			baos.write(buffer, 0, read);
		}
		is.close();
		String result = baos.toString();
		baos.close();
		return result;
	}
	
}
