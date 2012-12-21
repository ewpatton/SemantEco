package edu.rpi.tw.escience.semanteco.query.impl;

import edu.rpi.tw.escience.semanteco.query.BlankNode;
import edu.rpi.tw.escience.semanteco.query.Query;

/**
 * BlankNodeImpl provides a default implementation of the BlankNode interface.
 * 
 * @author ewpatton
 *
 */
public class BlankNodeImpl extends VariableImpl implements BlankNode {

	private Object ref = null;
	
	/**
	 * Default constructor. Generates a new 
	 */
	public BlankNodeImpl() {
		super(null);
		ref = new Object();
		setUri(Query.VAR_NS+"_bn"+ref.hashCode());
	}
	
}
