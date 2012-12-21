package edu.rpi.tw.escience.waterquality.query.impl;

import edu.rpi.tw.escience.waterquality.query.Variable;

/**
 * VariableExprImpl encapsulates a variable binding expression
 * for the use in the SPARQL SELECT variable clause, e\.g\.
 * SELECT (SUM(?x) AS ?sum) WHERE { \.\.\. }
 * @author ewpatton
 *
 */
public class VariableExprImpl implements Variable {

	private final String expr;
	
	/**
	 * Constructs a VariableExprImpl to wrap the
	 * given expression. This DOES NOT check the
	 * syntax or semantics for correctness. These
	 * should be ensured by the caller.
	 * @param expr A valid SPARQL variable expression
	 */
	public VariableExprImpl(String expr) {
		this.expr = expr;
	}

	@Override
	public String getUri() {
		return null;
	}
	
	@Override
	public String toString() {
		return "("+expr+")";
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
		VariableExprImpl other = (VariableExprImpl)o;
		if(expr == null) {
			if(other.expr != null) {
				return false;
			}
		}
		else if(!expr.equals(other.expr)) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expr == null) ? 0 : expr.hashCode());
		return result;
	}

}
