package edu.rpi.tw.escience.semanteco.util;

import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.QueryResource;

public class QueryResourceUtils {

	public static final String RDF_NS   = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String RDFS_NS  = "http://www.w3.org/2000/01/rdf-schema#";
	public static final String OWL_NS   = "http://www.w3.org/2002/07/owl#";
	public static final String XSD_NS   = "http://www.w3.org/2001/XMLSchema#";
	public static final String DC_NS    = "http://purl.org/dc/terms/";
	public static final String UNIT_NS  = "http://sweet.jpl.nasa.gov/2.1/reprSciUnits.owl#";
	public static final String POL_NS   = "http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#";
	public static final String ESCIM_NS = "http://escience.rpi.edu/ontology/semanteco/4/0/measure.owl#";

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

	public final QueryResource owlClass() {
		return getResource(OWL_NS, "Class");
	}

	public final QueryResource rdfsSubClassOf() {
		return getResource(RDFS_NS, "subClassOf");
	}

	public final QueryResource owlOnProperty() {
		return getResource(OWL_NS, "onProperty");
	}

	public final QueryResource owlHasValue() {
		return getResource(OWL_NS, "hasValue");
	}

	public final QueryResource unitHasUnit() {
		return getResource(UNIT_NS, "hasUnit");
	}

	public final QueryResource owlIntersectionOf() {
		return getResource(OWL_NS, "intersectionOf");
	}

	public final QueryResource rdfListPropPath() {
		return query.createPropertyPath("rdf:rest*/rdf:first");
	}

	public final QueryResource escimHasMeasurement() {
		return getResource(ESCIM_NS, "hasMeasurement");
	}

	public final QueryResource escimHasCharacteristic() {
		return getResource(ESCIM_NS, "hasCharacteristic");
	}

	public final QueryResource rdfsSubPropertyOf() {
		return getResource( RDFS_NS, "subPropertyOf" );
	}
}
