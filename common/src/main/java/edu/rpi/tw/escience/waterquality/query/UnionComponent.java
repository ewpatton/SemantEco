package edu.rpi.tw.escience.waterquality.query;

/**
 * This GraphComponentCollection represents a set of graph patterns
 * composed into a set of UNION statements
 * @author ewpatton
 *
 */
public interface UnionComponent extends GraphComponentCollection {
	/**
	 * Gets the number of subcomponents for this UnionComponent
	 * @return
	 */
	int size();
	
	/**
	 * Gets a specific graph component in this union
	 * @param i
	 * @return
	 */
	GraphComponentCollection getUnionComponent(int i);
}
