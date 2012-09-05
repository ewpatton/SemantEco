package edu.rpi.tw.escience.waterquality.query.impl;

import edu.rpi.tw.escience.waterquality.query.NamedGraphComponent;

public class NamedGraphComponentImpl extends GraphComponentCollectionImpl implements NamedGraphComponent {

	private String uri = null;
	
	public NamedGraphComponentImpl(String uri) {
		this.uri = uri;
	}

	@Override
	public String getUri() {
		return uri;
	}

}
