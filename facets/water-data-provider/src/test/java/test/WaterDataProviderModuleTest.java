package test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.QueryExecutor;
import edu.rpi.tw.escience.semanteco.QueryFactory;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.Resource;
import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.Query.Type;
import edu.rpi.tw.escience.semanteco.query.impl.QueryImpl;
import edu.rpi.tw.escience.semanteco.test.MockModuleConfiguration;
import edu.rpi.tw.escience.semanteco.test.MockQueryExecutor;
import edu.rpi.tw.escience.semanteco.test.MockQueryFactory;
import edu.rpi.tw.escience.semanteco.test.MockRequest;
import edu.rpi.tw.escience.semanteco.test.MockResource;
import edu.rpi.tw.escience.semanteco.test.MockUI;
import edu.rpi.tw.escience.semanteco.test.TestQuery;
import edu.rpi.tw.escience.waterquality.dataprovider.QueryUtils;
import edu.rpi.tw.escience.waterquality.dataprovider.WaterDataProviderModule;

public class WaterDataProviderModuleTest extends TestCase {

	private static class TestRequest extends MockRequest {

		Map<String, String[]> params = new TreeMap<String, String[]>();
		
		@Override
		public Object getParam(String key) {
			String[] value = params.get(key);
			if(value == null) {
				return null;
			}
			if(value[0].startsWith("{")) {
				try {
					return new JSONObject(value[0]);
				}
				catch(JSONException e) {
					
				}
				return null;
			}
			else if(value[0].startsWith("[")) {
				try {
					return new JSONArray(value[0]);
				}
				catch(JSONException e) {
					
				}
				return null;
			}
			else {
				return value[0];
			}
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
		public QueryExecutor getQueryExecutor(Request request) {
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
		
		@Override
		public Domain getDomain(URI uri, boolean create) {
			TestCase.assertEquals(uri.toASCIIString(), QueryUtils.WATER_NS);
			Domain d = new Domain() {

				@Override
				public URI getUri() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public void addSource(URI sourceUri, String label) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void addDataType(String id, String name, Resource icon) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void addRegulation(URI regulationUri, String label) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public List<URI> getSources() {
					List<URI> uris = new ArrayList<URI>();
					uris.add(URI.create("http://sparql.tw.rpi.edu/source/epa-gov"));
					uris.add(URI.create("http://sparql.tw.rpi.edu/source/usgs-gov"));
					return uris;
				}

				@Override
				public List<URI> getRegulations() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public List<String> getDataTypes() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public String getDataTypeName(String id) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public Resource getDataTypeIcon(String id) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public String getLabelForSource(URI uri) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public String getLabel() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public void setLabel(String label) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public String getLabelForRegulation(URI uri) {
					// TODO Auto-generated method stub
					return null;
				}
				
			};
			return d;
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
		Set<Resource> scripts = new HashSet<Resource>();
		
		@Override
		public void addFacet(Resource res) {
			facets.add(res);
		}
		
		@Override
		public void addScript(Resource res) {
			scripts.add(res);
		}
	}
	
	@Test
	public void testGetName() {
		WaterDataProviderModule module = new WaterDataProviderModule();
		assertEquals("Water Data Provider", module.getName());
	}
	
	@Test
	public void testVersion() {
		WaterDataProviderModule module = new WaterDataProviderModule();
		assertEquals(1, module.getMajorVersion());
		assertEquals(0, module.getMinorVersion());
		assertNull(module.getExtraVersion());
	}
	
	@Test
	public void testModuleConfiguration() {
		TestModuleConfiguration config = new TestModuleConfiguration();
		WaterDataProviderModule module = new WaterDataProviderModule();
		module.setModuleConfiguration(config);
	}
	
	@Test
	public void testVisitModel() throws Exception {
		TestModuleConfiguration config = new TestModuleConfiguration();
		Model model = ModelFactory.createDefaultModel();
		TestRequest request = new TestRequest();
		WaterDataProviderModule module = new WaterDataProviderModule();
		module.setModuleConfiguration(config);
		try {
			module.visit(model, request);
			fail();
		}
		catch(IllegalArgumentException e) {
			
		}
		try {
			request.setParam("source", new String[] { "[\"http://sparql.tw.rpi.edu/garbage\"]" });
			module.visit(model, request);
			fail();
		}
		catch(IllegalArgumentException e) {
			
		}
		request.setParam("source", new String[] { "[\"http://sparql.tw.rpi.edu/source/usgs-gov\"]", "[\"http://sparql.tw.rpi.edu/source/epa-gov\"]" });
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
		WaterDataProviderModule module = new WaterDataProviderModule();
		module.setModuleConfiguration(config);
		module.visit(model, request);
	}
	
	@Test
	public void testVisitQuery() {
		TestModuleConfiguration config = new TestModuleConfiguration();
		TestQuery query = new TestQuery(Type.SELECT);
		TestRequest request = new TestRequest();
		WaterDataProviderModule module = new WaterDataProviderModule();
		module.setModuleConfiguration(config);
		module.visit(query, request);
	}
	
	@Test
	public void testVisitUI() throws Exception {
		TestModuleConfiguration config = new TestModuleConfiguration();
		TestUI ui = new TestUI();
		TestRequest request = new TestRequest();
		WaterDataProviderModule module = new WaterDataProviderModule();
		module.setModuleConfiguration(config);
		module.visit(ui, request);
		((TestQueryExecutor)config.queryExecutor).response = getResource("/test001.json");
		module.visit(ui, request);
	}
	
	@Test
	public void testQueryForDataSources() throws Exception {
		TestModuleConfiguration config = new TestModuleConfiguration();
		TestRequest request = new TestRequest();
		WaterDataProviderModule module = new WaterDataProviderModule();
		module.setModuleConfiguration(config);
		module.queryForDataSources(request);
		((TestQueryExecutor)config.queryExecutor).response = getResource("/test001.json");
		module.queryForDataSources(request);
	}
	
	protected static String getResource(String name) throws Exception {
		final int BUFSIZE = 1024;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = WaterDataProviderModuleTest.class.getResourceAsStream(name);
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
