package edu.rpi.tw.escience.waterquality.test;

import java.util.Map;

import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.Request;

public class TestRequest implements Request {

	private Map<String, String[]> params = new TreeMap<String, String[]>();
	
	@Override
	public String[] getParam(String key) {
		return params.get(key);
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
	
	public void setParam(String key, String value) {
		params.put(key, new String[] { value });
	}
	
	public void setParam(String key, String[] values) {
		params.put(key, values);
	}

}
