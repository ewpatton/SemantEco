package edu.rpi.tw.escience.semanteco;

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
	OntModel getModel();
	
	/**
	 * Gets the A-Box model for the current request
	 * @return
	 */
	Model getDataModel();
	
	/**
	 * Gets a model combining the A- and T-Boxes into
	 * a single model
	 * @return
	 */
	Model getCombinedModel();
}
