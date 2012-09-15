package edu.rpi.tw.escience.waterquality.query.impl;

import edu.rpi.tw.escience.waterquality.query.OptionalComponent;

public class OptionalComponentImpl extends GraphComponentCollectionImpl implements OptionalComponent {

	@Override
	public String toString() {
		return "OPTIONAL "+super.toString();
	}
	
}
