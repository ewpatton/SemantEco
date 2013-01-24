package edu.rpi.tw.escience.semanteco.query.impl;

import edu.rpi.tw.escience.semanteco.query.OptionalComponent;

/**
 * OptionalComponentImpl provides a mechanism for representing an OPTIONAL block
 * within a SPARQL query.
 * 
 * @author ewpatton
 *
 */
public class OptionalComponentImpl extends GraphComponentCollectionImpl implements OptionalComponent {

	@Override
	public String toString() {
		return "OPTIONAL "+super.toString();
	}
	
}
