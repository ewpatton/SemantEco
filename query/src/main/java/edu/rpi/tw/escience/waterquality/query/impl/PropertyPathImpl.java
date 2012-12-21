package edu.rpi.tw.escience.waterquality.query.impl;

import edu.rpi.tw.escience.waterquality.query.QueryResource;

/**
 * PropertyPathImpl provides a wrapper around a
 * SPARQL 1.1 compliant property path string so
 * that property paths can be used in place of
 * Variable and URI-based query resources.
 * @author ewpatton
 *
 */
public class PropertyPathImpl implements QueryResource {

	private final String path;
	
	/**
	 * Constructs a PropertyPathImpl for the given
	 * path. It DOES NOT check for syntactic or
	 * semantic correctness. It is up to the caller
	 * to perform such checks.
	 * @param path
	 */
	public PropertyPathImpl(final String path) {
		this.path = path;
	}
	
	@Override
	public String getUri() {
		return null;
	}
	
	@Override
	public String toString() {
		return path;
	}

}
