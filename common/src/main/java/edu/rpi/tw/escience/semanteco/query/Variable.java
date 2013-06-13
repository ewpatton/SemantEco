package edu.rpi.tw.escience.semanteco.query;

/**
 * Represents a variable in a Query
 * @author ewpatton
 *
 */
public interface Variable extends QueryResource {
	/**
	 * Gets the name of the variable as it would appear in the result set.
	 * @return
	 */
	String getName();
}
