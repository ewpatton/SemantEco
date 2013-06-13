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

	/**
	 * Label, often used with rdfs:label or skos:prefLabel
	 */
	public final Variable LABEL;
	/**
	 * Latitude, often used with geo:lat
	 */
	public final Variable LAT;
	/**
	 * Longitude, often used with geo:long
	 */
	public final Variable LONG;
	/**
	 * Measurement, used in queries related to measurements
	 */
	public final Variable MEASUREMENT;
	/**
	 * Site, used in queries for measurement sites
	 */
	public final Variable SITE;
	/**
	 * URI, usually used for getting identifying information from an endpoint
	 */
	public final Variable URI;

	/**
	 * Constructs a new variable set for the given query.
	 * @param query
	 */
	public QueryVariableUtils(Query query) {
		LABEL = query.createVariable(Query.VAR_NS + "label");
		LAT = query.createVariable(Query.VAR_NS + "lat");
		LONG = query.createVariable(Query.VAR_NS + "long");
		MEASUREMENT = query.createVariable(Query.VAR_NS + "measurement");
		SITE = query.createVariable(Query.VAR_NS + "site");
		URI = query.createVariable(Query.VAR_NS + "uri");
	}

}
