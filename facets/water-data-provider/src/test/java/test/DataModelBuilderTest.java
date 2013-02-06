package test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.Resource;
import edu.rpi.tw.escience.semanteco.test.TestModuleConfiguration;
import edu.rpi.tw.escience.semanteco.test.TestRequest;
import edu.rpi.tw.escience.waterquality.dataprovider.QueryUtils;

import junit.framework.TestCase;

public class DataModelBuilderTest extends TestCase {

	TestModuleConfiguration config = null;
	TestRequest request = null;
	PublicDataModelBuilder builder = null;
	
	class TestModuleConfiguration2 extends TestModuleConfiguration {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2306498997584351413L;

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
	
	@Before
	public void setUp() {
		config = new TestModuleConfiguration2();
		request = new TestRequest();
		config.executor.setDefault("endpoint", "http://sparql.tw.rpi.edu/virtuoso/sparql");
		config.executor.setDefault("Content-Type", "application/json");
	}
	
	@Test
	public void testBuildModel() {
		request.setParam("county", "7");
		request.setParam("state", "RI");
		request.setParam("zip", "02888");
		request.setParam("lat", "41.74936");
		request.setParam("lng", "-71.40836");
		request.setParam("limits", "{\"facility\":{\"offset\":0,\"limit\":0},\"site\":{\"offset\":0,\"limit\":10}}");
		request.setParam("source", "[\"http://sparql.tw.rpi.edu/source/usgs-gov\"]");
		
		config.executor.setDefault("endpoint", "http://sparql.tw.rpi.edu/virtuoso/sparql");
		config.executor.setDefault("Content-Type", "applicaction/json");

		config.executor.expect("endpoint", "http://logd.tw.rpi.edu/sparql?output=sparqljson")
			.expect("query", "state-query-ri.rq")
			.andReturn("state-response-ri.json")
			
			.expect("query", "graph-query-ri.rq")
			.andReturn("graph-response-ri.json")
			
			.expect("query", "site-query-ri.rq")
			.andReturn("site-response-ri.json")
			
			.expect("Content-Type", "text/turtle")
			.expect("query", "data-query-ri.rq")
			.expect("model", "true")
			.andReturn("data-response-ri.ttl")
		;

		Model model = ModelFactory.createDefaultModel();
		builder = new PublicDataModelBuilder(request, config);
		builder.build(model);
	}
	
}
