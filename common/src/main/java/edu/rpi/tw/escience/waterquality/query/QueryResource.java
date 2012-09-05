package edu.rpi.tw.escience.waterquality.query;

/**
 * QueryResource is the base interface for all of the different
 * objects used in triple patterns as part of a Query.
 * 
 * @author ewpatton
 *
 */
public interface QueryResource {
	/**
	 * Gets the URI associated with this resource, if any
	 * 
	 * @return
	 */
	String getUri();
}
