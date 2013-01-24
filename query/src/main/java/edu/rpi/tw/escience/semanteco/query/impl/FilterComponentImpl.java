package edu.rpi.tw.escience.semanteco.query.impl;

import edu.rpi.tw.escience.semanteco.query.GraphComponent;

/**
 * FilterComponentImpl provides a mechanism for storing FILTER statements
 * inside of a GraphComponentCollection.
 * 
 * @author ewpatton
 *
 */
public class FilterComponentImpl implements GraphComponent {

	private String condition = null;
	
	/**
	 * Generates a FILTER representing the given condition
	 * @param cond
	 */
	public FilterComponentImpl(String cond) {
		if(cond == null) {
			throw new IllegalArgumentException("cond cannot be null");
		}
		this.condition = cond;
	}
	
	/**
	 * Returns the condition string for this filter
	 * @return
	 */
	public String getCondition() {
		return condition;
	}
	
	@Override
	public String toString() {
		return "FILTER("+condition+")";
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null) {
			return false;
		}
		if(getClass() != o.getClass()) {
			return false;
		}
		if(this == o) {
			return true;
		}
		FilterComponentImpl other = (FilterComponentImpl)o;
		return condition.equals(other.condition);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int res = 1;
		res = res * prime + condition.hashCode();
		return res;
	}

}
