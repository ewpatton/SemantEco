package test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.Request;
import edu.rpi.tw.escience.waterquality.util.LimitUtils;

import junit.framework.TestCase;

public class LimitUtilsTest extends TestCase {

	class MockRequest implements Request {

		String response = "{\"site\":{\"offset\":10,\"limit\":20,\"count\":40}}";
		
		@Override
		public String[] getParam(String key) {
			if(!key.equals("limit")) {
				throw new IllegalArgumentException("Did not expect key "+key);
			}
			return new String[] { response };
		}

		@Override
		public Logger getLogger() {
			return null;
		}

		@Override
		public OntModel getModel() {
			return null;
		}

		@Override
		public Model getDataModel() {
			return null;
		}

		@Override
		public Model getCombinedModel() {
			return null;
		}
		
	}
	
	@Test
	public void testGetLimit() {
		MockRequest request = new MockRequest();
		assertEquals(20, LimitUtils.getLimit(request, "site"));
	}
	
	@Test
	public void testGetOffset() {
		MockRequest request = new MockRequest();
		assertEquals(10, LimitUtils.getOffset(request, "site"));
	}
	
	@Test
	public void testCatchLimit() {
		MockRequest request = new MockRequest();
		assertEquals(0, LimitUtils.getLimit(request, "undefined"));
	}
	
	@Test
	public void testCatchOffset() {
		MockRequest request = new MockRequest();
		assertEquals(0, LimitUtils.getOffset(request, "undefined"));
	}

}
