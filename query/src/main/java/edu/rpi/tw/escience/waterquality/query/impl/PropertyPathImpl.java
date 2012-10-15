package edu.rpi.tw.escience.waterquality.query.impl;

import edu.rpi.tw.escience.waterquality.query.QueryResource;

public class PropertyPathImpl implements QueryResource {

	private final String path;
	
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
