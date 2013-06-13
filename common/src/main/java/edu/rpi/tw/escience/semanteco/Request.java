package edu.rpi.tw.escience.semanteco;

import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * The Request interface provides a contract between a client's RESTful call
 * to SemantEco and how the system interacts with the parameters of the
 * request.
 * @author ewpatton
 *
 */
public interface Request {
	/**
	 * Gets a parameter specified in the URI of the REST call
	 * @param key Named key
	 * @return An array of strings
	 */
	Object getParam(String key);
	
	/**
	 * Gets a logger instance for this request that the system
	 * can log messages to
	 * @return
	 */
	Logger getLogger();
	
	/**
	 * Gets the T-Box model for the current request
	 * @return
	 */
	OntModel getModel(Domain domain);
	
	/**
	 * Gets the A-Box model for the current request
	 * @return
	 */
	Model getDataModel(Domain domain);
	
	/**
	 * Gets a model combining the A- and T-Boxes into
	 * a single model
	 * @return
	 */
	Model getCombinedModel(Domain domain);

	/**
	 * Gets the URL that generated this request
	 * @return A URL object that encodes this request.
	 */
	URL getOriginalURL();

	/**
	 * Returns whether or not this Request can log provenance information
	 * to clients.
	 * @return
	 */
	boolean canLogProvenance();

	/**
	 * Logs provenance information related to the processing of the request.
	 * @param graph Graph name for use in provenance capture
	 * @param contents Contents of the named graph
	 */
	void logProvenance(String graph, String contents);

	/**
	 * Lists the active domains based on the information passed by the client
	 * cross-referenced with the ModuleManager's list of domains.
	 * @return
	 */
	List<Domain> listActiveDomains();
}
