package edu.rpi.tw.escience.semanteco.util;

import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.QueryResource;

public class QueryResourceUtils {

	public static final String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";
	public static final String OWL_NS = "http://www.w3.org/2002/07/owl#";
	public static final String XSD_NS = "http://www.w3.org/2001/XMLSchema#";
	public static final String DC_NS = "http://purl.org/dc/terms/";
	public static final String POL_NS = "http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#";

	protected final QueryResource getResource(final String ns, final String part) {
		return query.getResource( ns + part );
	}

	private final Query query;

	public QueryResourceUtils(final Query query) {
		this.query = query;
	}

	public final QueryResource rdfType() {
		return getResource(RDF_NS, "type");
	}

	public final QueryResource rdfsLabel() {
		return getResource(RDFS_NS, "label");
	}

	public final QueryResource dcIdentifier() {
		return getResource(DC_NS, "identifier");
	}

	public final QueryResource dcDate() {
		return getResource(DC_NS, "date");
	}
}
