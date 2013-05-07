package edu.rpi.tw.escience.semanteco.util;

import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.util.LimitUtils;

import junit.framework.TestCase;

public class LimitUtilsTest extends TestCase {

	class MockRequest implements Request {

		String response = "{\"site\":{\"offset\":10,\"limit\":20,\"count\":40}}";
		
		@Override
		public Object getParam(String key) {
			if(!key.equals("limits")) {
				throw new IllegalArgumentException("Did not expect key "+key);
			}
			try {
				return new JSONObject(response);
			}
			catch(JSONException e) {
				
			}
			return null;
		}

		@Override
		public Logger getLogger() {
			return null;
		}

		@Override
		public OntModel getModel(Domain domain) {
			return null;
		}

		@Override
		public Model getDataModel(Domain domain) {
			return null;
		}

		@Override
		public Model getCombinedModel(Domain domain) {
			return null;
		}

		@Override
		public URL getOriginalURL() {
			return null;
		}

		@Override
		public boolean canLogProvenance() {
			return false;
		}

		@Override
		public void logProvenance(String graph, String contents) {

		}

		@Override
		public List<Domain> listActiveDomains() {
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
