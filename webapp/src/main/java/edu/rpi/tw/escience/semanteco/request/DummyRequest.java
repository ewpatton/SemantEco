package edu.rpi.tw.escience.semanteco.request;

import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.impl.ModuleManagerFactory;

/**
 * An implementatino of the Request interface used to simulate a client request
 * during the construction of certain SemantEco features, such as the user
 * interface.
 * @author ewpatton
 *
 */
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
