package edu.rpi.tw.escience.semanteco.query.impl;

import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.Variable;

/**
 * VariableImpl provides a default implementation of the Variable interface.
 * 
 * @author ewpatton
 *
 */
public class VariableImpl implements Variable {

	private String uri = null;
	
	/**
	 * Creates a new VariableImpl representing the specified URI. The URI should
	 * start with {@link Query#VAR_NS}.
	 * @param uri
	 */
	public VariableImpl(String uri) {
		if(uri != null && !uri.startsWith(Query.VAR_NS)) {
			throw new IllegalArgumentException("URI "+uri+" not a valid variable identifier");
		}
		this.uri = uri;
	}
	
	protected void setUri(String uri) {
		this.uri = uri;
	}
	
	@Override
	public String getUri() {
		return uri;
	}
	
	@Override
	public String toString() {
		return "?" + getName();
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

	@Override
	public String getName() {
		return uri.replace(Query.VAR_NS, "");
	}
}
