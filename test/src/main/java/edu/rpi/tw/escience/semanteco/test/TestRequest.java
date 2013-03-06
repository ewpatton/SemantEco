package edu.rpi.tw.escience.semanteco.test;

import java.net.URL;
import java.util.Map;

import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.Request;

/**
 * TestRequest provides a mechanism for a unit test
 * to mimic a call via the RESTful interface to
 * SemantEco.
 * @author ewpatton
 *
 */
public class TestRequest implements Request {

	private Map<String, String[]> params = new TreeMap<String, String[]>();
	
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

	@Override
	public Logger getLogger() {
		return Logger.getRootLogger();
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
	
	/**
	 * Sets a parameter to be a single value
	 * @param key Parameter to set
	 * @param value Value for the specified parameter
	 */
	public void setParam(String key, String value) {
		params.put(key, new String[] { value });
	}
	
	/**
	 * Sets a parameter to be an array of values
	 * @param key Parameter to set
	 * @param values An array of values to store for the specified parameter
	 */
	public void setParam(String key, String[] values) {
		params.put(key, values);
	}

	@Override
	public URL getOriginalURL() {
		return null;
	}

}
