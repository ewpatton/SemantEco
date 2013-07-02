package edu.rpi.tw.escience.semanteco.query;

import java.util.List;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

/**
 * Represents a collection of GraphComponents
 * 
 * @author ewpatton
 *
 */
public interface GraphComponentCollection extends GraphComponent {

	/**
	 * Adds a triple pattern to this graph component collection
	 * 
	 * @param subject Subject of triple
	 * @param predicate Predicate of triple
	 * @param object Object of triple
	 */
	void addPattern(QueryResource subject,
			QueryResource predicate,
			QueryResource object);

	/**
	 * Adds a triple pattern to this graph component collection. This
	 * method should only be used to construct queries against Virtuoso
	 * where option(transitive) is allowed. Endpoints that support SPARQL
	 * @param subject Subject of triple
	 * @param predicate Predicate of triple
	 * @param object Object of triple
	 * @param transitive true if option(transitive) should be enabled for this pattern.
	 */
	void addPattern(QueryResource subject,
			QueryResource predicate,
			QueryResource object,
			boolean transitive);
	
	/**
	 * Adds a triple pattern to this graph component collection
	 * 
	 * @param subject Subject of triple
	 * @param predicate Predicate of triple
	 * @param object Object (value) of triple
	 * @param type XSD type of the object
	 */
	void addPattern(QueryResource subject,
			QueryResource predicate,
			String object,
			XSDDatatype type);
	
	/**
	 * Adds a filter statement to this component collection
	 * 
	 * @param cond A valid filter expression for a SPARQL query
	 */
	void addFilter(String cond);
	
	/**
	 * Adds a graph component to this collection
	 * 
	 * @param component
	 */
	void addGraphComponent(GraphComponent component);
	
	/**
	 * Returns a list of the current components contained in this
	 * GraphComponentCollection instance
	 * @return A list of GraphComponent objects contained within this query.
	 */
	List<GraphComponent> getComponents();
	
	/**
	 * Adds a BIND statement to the SPARQL query.
	 * @param expr Expression to be evaluated and bound.
	 * @param var Variable to bind the result of expr to.
	 */
	void addBind(String expr, Variable var);
}
