package edu.rpi.tw.escience.waterquality.query.impl;

import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.Variable;

public class VariableImpl implements Variable {

	private String uri = null;
	
	public VariableImpl(String uri) {
		this.uri = uri;
	}
	
	protected void setUri(String uri) {
		this.uri = uri;
	}
	
	public String getUri() {
		return uri;
	}
	
	@Override
	public String toString() {
		return "?"+uri.replace(Query.VAR_NS, "");
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(o == null) {
			return false;
		}
		if(getClass() != o.getClass()) {
			return false;
		}
		VariableImpl other = (VariableImpl)o;
		if(uri == null) {
			if(other.uri != null) {
				return false;
			}
		}
		else if(!uri.equals(other.uri)) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

}
