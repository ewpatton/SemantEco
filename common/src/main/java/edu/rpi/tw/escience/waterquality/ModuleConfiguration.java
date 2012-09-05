package edu.rpi.tw.escience.waterquality;

import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Each module receives a configuration object that allows it to
 * interact with the core SemantAqua components as well as other
 * modules. This class serves as the base class for the actual module
 * configurations implemented in the SemantAqua core.
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
	 * @return The QueryExecutor for the module
	 */
	public abstract QueryExecutor getQueryExecutor();
	
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

}
