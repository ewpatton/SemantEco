package edu.rpi.tw.escience.semanteco.request;

import java.net.URL;

import org.apache.log4j.Logger;
import org.json.JSONArray;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.impl.ModuleManagerFactory;

public class DummyRequest implements Request {

	@Override
	public Object getParam(String key) {
		if("available-domains".equals(key)) {
			return ModuleManagerFactory.getInstance().getManager().listDomains();
		}
		return new JSONArray();
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(DummyRequest.class);
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

}