package edu.rpi.tw.escience.semanteco.zipcode;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.zipcode.ZipCodeModule;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.test.TestRequest;

import junit.framework.TestCase;

public class ZipCodeModuleTest extends TestCase {

	@Test
	public void testLookup() throws JSONException {
		ZipCodeModule module = new ZipCodeModule();
		TestRequest request = new TestRequest();
		request.setParam("zip", "02888");
		String response = module.decodeZipCode(request);
		JSONObject data = new JSONObject(response);
		if(response.contains("error")) {
			return;
		}
		assertEquals("003", data.getJSONObject("result").getString("countyCode"));
		request.setParam("zip", "02809");
		response = module.decodeZipCode(request);
		data = new JSONObject(response);
		assertEquals("001", data.getJSONObject("result").getString("countyCode"));
		request.setParam("zip", "99990");
		response = module.decodeZipCode(request);
		data = new JSONObject(response);
		data.getString("error");
	}
	
	@Test
	public void testVisits() {
		ZipCodeModule module = new ZipCodeModule();
		module.visit((Model)null, null);
		module.visit((OntModel)null, null);
		module.visit((Query)null, null);
		module.visit((SemantEcoUI)null, null);
	}
	
	@Test
	public void testProperties() {
		ZipCodeModule module = new ZipCodeModule();
		assertEquals("Zip Code", module.getName());
		assertEquals(1, module.getMajorVersion());
		assertEquals(0, module.getMinorVersion());
		assertNull(module.getExtraVersion());
	}
	
	@Test
	public void testConstructor() {
		ZipCodeModule module = new ZipCodeModule();
		module.setModuleConfiguration(null);
	}

}
