package edu.rpi.tw.escience.waterquality.query;

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
	 * Adds a triple patter to this graph component collection
	 * 
	 * @param subject Subject of triple
	 * @param predicate Predicate of triple
	 * @param object Object of triple
	 */
	void addPattern(QueryResource subject,
			QueryResource predicate,
			QueryResource object);
	
	/**
	 * Adds a triple patter to this graph component collection
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
	 * @return
	 */
	List<GraphComponent> getComponents();
}
