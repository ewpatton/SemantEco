package edu.rpi.tw.escience.semanteco;

import java.net.URI;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Each module receives a configuration object that allows it to
 * interact with the core SemantEco components as well as other
 * modules. This class serves as the base class for the actual module
 * configurations implemented in the SemantEco core.
 * 
 * @author ewpatton
 *
 */
public abstract class ModuleConfiguration extends Properties {

	/**
	 * Generated unique serialial identifier
	 */
	private static final long serialVersionUID = 2756682567606291417L;

	/**
	 * Gets the URL to the default SPARQL endpoint for this configuration
	 * @return URI as a UTF-8 String
	 */
	public abstract String getSparqlEndpoint();
	
	/**
	 * Gets a QueryFactory object for constructing SPARQL queries
	 * @return The QueryFactory for the module
	 */
	public abstract QueryFactory getQueryFactory();
	
	/**
	 * Gets a QueryExecutor object for executing SPARQL queries
	 * @param request Original client request
	 * @return The QueryExecutor for the module
	 */
	public abstract QueryExecutor getQueryExecutor(Request request);
	
	/**
	 * Gets a Resource object from the JAR file for this module
	 * @param path Path within the JAR to find a file resource
	 * @return New Resource handle
	 */
	public abstract Resource getResource(String path);
	
	/**
	 * Generates a Resource object from a String in situations
	 * where a module dynamically constructs a resource in memory
	 * @param content String representing content for a Resource
	 * @return New Resource handle
	 */
	public abstract Resource generateStringResource(String content);
	
	/**
	 * Gets a log4j logger that can be used for providing logging
	 * statements from the module
	 * @return
	 */
	public abstract Logger getLogger();
	
	/**
	 * Gets the domain named by the specified URI, optionally creating 
	 * it if no other module has done so.
	 * @param uri URI naming a domain
	 * @param create true if the system should create a new domain object
	 * @return A Domain object containing the state of the domain thus far, or null if create was false and the domain doesn't exist.
	 */
	public abstract Domain getDomain(URI uri, boolean create);

	/**
	 * Returns a list of all domains known by SemantEco.
	 * @return
	 */
	public abstract List<Domain> listDomains();

}
