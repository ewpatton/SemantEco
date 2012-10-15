package edu.rpi.tw.escience.waterquality.query.impl;

import edu.rpi.tw.escience.waterquality.query.GraphComponent;
import edu.rpi.tw.escience.waterquality.query.Variable;

public class BindingImpl implements GraphComponent {

	private final String expression;
	private final Variable bindVar;
	
	public BindingImpl(final String expr, final Variable var) {
		if(expr == null) {
			throw new IllegalArgumentException("expr cannot be null");
		}
		if(expr.isEmpty()) {
			throw new IllegalArgumentException("expr must be a valid SPARQL expression");
		}
		if(var == null) {
			throw new IllegalArgumentException("No variable specified in BIND");
		}
		expression = expr;
		bindVar = var;
	}
	
	@Override
	public String toString() {
		return "BIND ("+expression+" AS "+bindVar+")";
	}
	
	@Override
	public boolean equals(final Object o) {
		if(o == null) {
			return false;
		}
		if(getClass() != o.getClass()) {
			return false;
		}
		if(this == o) {
			return true;
		}
		boolean result = true;
		BindingImpl other = (BindingImpl)o;
		result &= expression.equals(other.expression);
		result &= bindVar.equals(other.bindVar);
		return result;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int res = 1;
		res = res * prime + expression.hashCode();
		res = res * prime + bindVar.hashCode();
		return res;
	}

}
