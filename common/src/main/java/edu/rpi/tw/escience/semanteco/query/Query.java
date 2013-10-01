package edu.rpi.tw.escience.semanteco.query;

import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

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
	 * Base URI used to represent variables and blank nodes in the SPARQL query
	 */
	String VAR_NS = "http://aquarius.tw.rpi.edu/projects/semantaqua/data-source/query-variable/";
	String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	
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
	 * Specifies the different sort orders allowed
	 * by a SPARQL query.
	 * @author ewpatton
	 *
	 */
	enum SortType {
		ASC,
		DESC
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
	 * Gets a subcomponent for the query that represents a named graph. Calling
	 * this method is equivalent to calling {@link #getNamedGraph(String, boolean)}
	 * with autoAdd = true.
	 * @param uri The named graph to query against
	 * @return NamedGraphComponent named by uri
	 */
	NamedGraphComponent getNamedGraph(String uri);

	/**
	 * Gets a subcomponent for the query that represents a named graph.
	 * @param uri The named graph to query against
	 * @param autoAdd If true, automatically add the collection to the query's
	 * WHERE { } clause
	 * @return NamedGraphComponent named by uri
	 */
	NamedGraphComponent getNamedGraph(String uri, boolean autoAdd);
	
	/**
	 * Creates a union graph component for this query.
	 * @return A fresh UnionComponent
	 */
	UnionComponent createUnion();
	
	/**
	 * Creates an optional graph component for this query
	 * @return A fresh OptionalComponent
	 */
	OptionalComponent createOptional();
	
	/**
	 * Gets an existing variable for use in this Query
	 * @param uri URI naming a variable in the {@link #VAR_NS} namespace
	 * @return A (potentially new) reference to a Variable object
	 */
	Variable getVariable(String uri);
	
	/**
	 * Creates a variable for use in this Query. Will throw an
	 * IllegalArgumentException if a Variable with the same URI already
	 * exists.
	 * @param uri URI naming a variable in the {@link #VAR_NS} namespace
	 * @return A new reference to a Variable object. 
	 */
	Variable createVariable(String uri);
	
	/**
	 * Creates a variable expression for a SELECT
	 * prolog, e\.g\. count(distinct ?var)
	 * @param expr A valid SPARQL variable expression
	 * @return A fresh Variable representing the SPARQL
	 * variable expression binding
	 */
	Variable createVariableExpression(String expr);
	
	/**
	 * Creates a blank node for use in this Query
	 * @return A fresh BlankNode object
	 */
	BlankNode createBlankNode();
	
	/**
	 * Sets the type of SPARQL query to be executed
	 * @param type The type of query, see {@link #Type}
	 */
	void setType(Type type);
	
	/**
	 * Gets the type of SPARQL query to be executed
	 * @return The Type of this SPARQL query, see {@link #Type}
	 */
	Type getType();
	
	/**
	 * Adds a FROM clause to the SPARQL query
	 * @param uri A dereferencable URI containing an RDF graph.
	 */
	void addFrom(String uri);
	
	/**
	 * Gets the list of FROM clauses in the SPARQL query
	 * @return A set of URIs that will be used for FROM &lt;...&gt;
	 * clauses in this SPARQL query
	 */
	Set<String> getFrom();
	
	/**
	 * Adds a FROM NAMED clause to the SPARQL query
	 * @param uri A URI representing a named graph within the triple store
	 */
	void addFromNamed(String uri);
	
	/**
	 * Gets the list of FROM NAMED clauses in the SPARQL query
	 * @param uri
	 */
	Set<String> getFromNamed();
	
	/**
	 * Gets a QueryResource object representing
	 * the specific URI
	 * @param uri
	 * @return
	 */
	QueryResource getResource(String uri);
	
	/**
	 * Sets the collection of variables used in SELECT queries. Passing
	 * null will result in a SELECT *
	 * @param object
	 */
	void setVariables(Set<Variable> object);
	
	/**
	 * Gets the collection of variables used in SELECT queries.
	 * @return
	 */
	Set<Variable> getVariables();
	
	/**
	 * Adds a group by statement to the query
	 * @param var Variable to group by
	 */
	void addGroupBy(Variable var);
	
	/**
	 * Adds an order by statement to the query
	 * @param var Variable to order by
	 * @param sort Direction to sort
	 */
	void addOrderBy(Variable var, SortType sort);
	
	/**
	 * Sets whether a SELECT query should return only distinct results
	 * @param distinct true if the selection should be distinct
	 */
	void setDistinct(boolean distinct);
	
	/**
	 * Returns whether this query is a SELECT DISTINCT query or not
	 * @return
	 */
	boolean isDistinct();
	
	/**
	 * Sets whether a SELECT query should return reduced results
	 * @param reduced true if the selection should be reduced
	 */
	void setReduced(boolean reduced);
	
	/**
	 * Returns whether this query is a SELECT REDUCED query or not
	 * @return
	 */
	boolean isReduced();
	
	/**
	 * Sets the value of the LIMIT clause in the SPARQL query
	 * @param limit
	 */
	void setLimit(long limit);
	
	/**
	 * Gets the value of the LIMIT clause in the SPARQL query, -1 if no limit is set
	 * @return
	 */
	long getLimit();
	
	/**
	 * Sets the value of the OFFSET clause in the SPARQL query
	 * @param offset
	 */
	void setOffset(long offset);
	
	/**
	 * Gets the value of the OFFSET caluse in the SPARQL query, -1 if no limit is set
	 * @return
	 */
	long getOffset();

	/**
	 * Adds an arbitrary expression used to order SPARQL query results.
	 * It is not guaranteed that the Query engine will validate this
	 * string.
	 * @param expr A valid expression in the SPARQL algebra that can be used in an ORDER BY statement
	 * @param sort Direction to sort
	 */
	void addOrderBy(String expr, SortType sort);
	
	/**
	 * Checks whether a variable has been referenced by a Query
	 * @param var Fully qualified name of a variable
	 * @return true if the variable has been used, otherwise false
	 */
	boolean hasVariable(String var);
	
	/**
	 * Searches the query for triple patterns that match the specified pattern
	 * and returns the list of graph components that contains them. Specifying
	 * null for a parameter will match all triple patterns on that field.
	 * @param subject A QueryResource to match or null to match any subject
	 * @param predicate A QueryResource to match or null to match any predicate
	 * @param object A QueryResource to match or null to match any object
	 * @return A list of GraphComponentCollection objects containing the specified pattern. 
	 * The list will be of length zero if no collections were found.
	 */
	List<GraphComponentCollection> findGraphComponentsWithPattern(QueryResource subject,
			QueryResource predicate, QueryResource object);
	
	/**
	 * Searches the query for triple patterns that match the specified pattern
	 * and returns the list of graph components that contains them. Specifying
	 * null for a parameter will match all triple patterns on that field.
	 * @param subject A QueryResource to match or null to match any subject
	 * @param predicate A QueryResource to match or null to match any predicate
	 * @param value A String to match or null to match any string
	 * @param type A type to match or null to match any type
	 * @return A list of GraphComponentCollection objects containing the specified pattern. 
	 * The list will be of length zero if no collections were found.
	 */
	List<GraphComponentCollection> findGraphComponentsWithPattern(QueryResource subject,
			QueryResource predicate, String value, XSDDatatype type);
	
	/**
	 * Searches the query for triples patterns that match the specified pattern
	 * and returns the list of graph comoponents that contains them. If the pattern
	 * has a null field it will match any triple pattern on that field.
	 * @param pattern A GraphPattern object containing the test triple pattern
	 * @return A list of GraphComponentCollection items that contain triples patterns
	 * matching the input pattern.
	 */
	List<GraphComponentCollection> findGraphComponentsWithPattern(GraphPattern pattern);
	
	/**
	 * Sets a namespace prefix in the SPARQL query.
	 * @param prefix A valid prefix in SPARQL
	 * @param namespace URI represented by the prefix
	 */
	void setNamespace(String prefix, String namespace);
	
	/**
	 * Gets the URI for a specific prefix
	 * @param prefix Prefix to look up
	 * @return URI for the prefix, or null if no prefix is set
	 */
	String getNamespace(String prefix);

	/**
	 * Creates a generic graph component collection
	 * @return A fresh GraphComponentCollection
	 */
	GraphComponentCollection createGraphComponentCollection();

	/**
	 * Creates a QueryResource representing a SPARQL 1\.1 property path.
	 * @param string A valid SPARQL 1\.1 property path
	 * @return QueryResource encoding the property path
	 */
	QueryResource createPropertyPath(String string);
}
