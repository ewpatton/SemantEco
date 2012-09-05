package edu.rpi.tw.escience.waterquality.query;

/**
 * Top interface for all graph component objects
 * used in the construction of SPARQL queries
 * 
 * @author ewpatton
 *
 */
public interface GraphComponent {
	/**
	 * Checks whether this graph component is equal to another
	 * @param obj
	 * @return
	 */
	boolean equals(Object obj);
	
	/**
	 * Returns the hash code for this graph component
	 * @return
	 */
	int hashCode();
}
