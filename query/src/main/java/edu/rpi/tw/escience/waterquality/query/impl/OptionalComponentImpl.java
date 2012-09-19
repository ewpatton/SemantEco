package edu.rpi.tw.escience.waterquality.query.impl;

import edu.rpi.tw.escience.waterquality.query.OptionalComponent;

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
