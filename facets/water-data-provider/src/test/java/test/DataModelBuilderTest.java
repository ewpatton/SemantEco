package test;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.rpi.tw.escience.semanteco.test.TestModuleConfiguration;
import edu.rpi.tw.escience.semanteco.test.TestRequest;

import junit.framework.TestCase;

public class DataModelBuilderTest extends TestCase {

	TestModuleConfiguration config = null;
	TestRequest request = null;
	PublicDataModelBuilder builder = null;
	
	@Before
	public void setUp() {
		config = new TestModuleConfiguration();
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
