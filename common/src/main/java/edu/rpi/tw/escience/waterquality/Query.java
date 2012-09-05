package edu.rpi.tw.escience.waterquality;

import java.util.List;

/**
 * The Query interface defines how modules construct
 * SPARQL queries so that multiple modules can change
 * the conditions on a shared query.
 * 
 * @author ewpatton
 *
 */
public interface Query extends GraphComponentCollection {
	/**
	 * Specifies the various types of queries supported
	 * by the Query engine.
	 * @author ewpatton
	 *
	 */
	enum Type {
		SELECT,
		CONSTRUCT,
		DESCRIBE,
		ASK
	}
	
	/**
	 * Gets the GraphComponentCollection associated with this query
	 * if it is a SPARQL CONSTRUCT query.
	 * @return If getType() == Type.CONSTRUCT, returns the GraphComponentCollection
	 * representing the first portion of the CONSTRUCT query before the WHERE clause.
	 * Otherwise, returns null.
	 */
	GraphComponentCollection getConstructComponent();

	/**
	 * Gets a subcomponent for the query that represents
	 * a named graph.
	 * @param uri The named graph to query against
	 * @return
	 */
	NamedGraphComponent getNamedGraph(String uri);
	
	/**
	 * Creates a union graph component for this query
	 * @return
	 */
	UnionComponent createUnion();
	
	/**
	 * Creates an optional graph component for this query
	 * @return
	 */
	OptionalComponent createOptional();
	
	/**
	 * Gets an existing variable for use in this Query
	 * @param uri
	 * @return
	 */
	Variable getVariable(String uri);
	
	/**
	 * Creates a variable for use in this Query
	 * @param uri
	 * @return
	 */
	Variable createVariable(String uri);
	
	/**
	 * Creates a blank node for use in this Query
	 * @return
	 */
	BlankNode createBlankNode();
	
	/**
	 * Sets the type of SPARQL query to be executed
	 * @param type
	 */
	void setType(Type type);
	
	/**
	 * Gets the type of SPARQL query to be executed
	 * @return
	 */
	Type getType();
	
	/**
	 * Adds a FROM clause to the SPARQL query
	 * @param uri
	 */
	void addFrom(String uri);
	
	/**
	 * Adds a FROM NAMED clause to the SPARQL query
	 * @param uri
	 */
	void addFromNamed(String uri);
	
	/**
	 * Gets a QueryResource object representing
	 * the specific URI
	 * @param uri
	 * @return
	 */
	QueryResource getResource(String uri);
	
	/**
	 * Sets the list of variables used in SELECT queries. Passing
	 * null will result in a SELECT *
	 * @param object
	 */
	void setVariables(List<QueryResource> object);
}
