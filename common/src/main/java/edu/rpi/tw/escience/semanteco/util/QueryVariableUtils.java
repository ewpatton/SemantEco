package edu.rpi.tw.escience.semanteco.util;

import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.Variable;

/**
 * Utilities for referencing commonly used variables in queries. Modules can
 * also use these to test whether a query contains shared variables (e.g.
 * measurement).
 * @author ewpatton
 *
 */
public class QueryVariableUtils {

	protected final Variable getVariable(final String var) {
		return query.getVariable(Query.VAR_NS + var);
	}

	private final Query query;

	/**
	 * Constructs a new variable set for the given query.
	 * @param query
	 */
	public QueryVariableUtils(final Query query) {
		this.query = query;
	}

	public final Variable characteristic() {
		return getVariable( "characteristic" );
	}

	/**
	 * Label, often used with rdfs:label or skos:prefLabel
	 */
	public final Variable label() {
		return getVariable( "label" );
	}

	/**
	 * Latitude, often used with geo:lat
	 */
	public final Variable latitude() {
		return getVariable( "lat" );
	}

	public final Variable limit() {
		return getVariable( "limit" );
	}

	/**
	 * Longitude, often used with geo:long
	 */
	public final Variable longitude() {
		return getVariable( "long" );
	}

	/**
	 * Measurement, used in queries related to measurements
	 */
	public final Variable measurement() {
		return getVariable( "measurement" );
	}

	public final Variable operation() {
		return getVariable( "op" );
	}

	public final Variable permit() {
		return getVariable( "permit" );
	}

	/**
	 * Site, used in queries for measurement sites
	 */
	public final Variable site() {
		return getVariable( "site" );
	}

	public final Variable test() {
		return getVariable( "test" );
	}

	public final Variable time() {
		return getVariable( "time" );
	}

	public final Variable unit() {
		return getVariable( "unit" );
	}

	/**
	 * URI, usually used for getting identifying information from an endpoint
	 */
	public final Variable uri() {
		return getVariable( "uri" );
	}

	public final Variable value() {
		return getVariable( "value" );
	}
}
