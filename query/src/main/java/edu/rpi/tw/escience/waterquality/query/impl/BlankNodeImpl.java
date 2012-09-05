package edu.rpi.tw.escience.waterquality.query.impl;

import edu.rpi.tw.escience.waterquality.query.BlankNode;
import edu.rpi.tw.escience.waterquality.query.Query;

public class BlankNodeImpl extends VariableImpl implements BlankNode {

	public BlankNodeImpl() {
		super(null);
		setUri(Query.VAR_NS+"_bn"+hashCode());
	}
	
}
