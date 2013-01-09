package edu.rpi.tw.escience.semanteco.query.impl;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

import edu.rpi.tw.escience.semanteco.query.GraphPattern;
import edu.rpi.tw.escience.semanteco.query.QueryResource;

/**
 * GraphPatternImpl provides the default implementation of the
 * GraphPattern interface.
 * @author ewpatton
 *
 */
public class GraphPatternImpl implements GraphPattern {

	private QueryResource subject = null;
	private QueryResource predicate = null;
	private QueryResource object = null;
	private String value = null;
	private XSDDatatype valueType = null;
	
	/**
	 * Constructs a graph pattern such as ?s ?p ?o where any can be
	 * a known URI, a variable, or a blank node.
	 * @param subject
	 * @param predicate
	 * @param object
	 */
	public GraphPatternImpl(QueryResource subject, QueryResource predicate,
			QueryResource object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	/**
	 * Constructs a graph pattern such as ?s ?p "..."^^xsd:... where the
	 * datatype is optional and ?s and ?p are either a known URI, a variable,
	 * or a blank node
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param type
	 */
	public GraphPatternImpl(QueryResource subject, QueryResource predicate,
			String object, XSDDatatype type) {
		this.subject = subject;
		this.predicate = predicate;
		this.value = object;
		this.valueType = type;
	}

	@Override
	public void setSubject(QueryResource subject) {
		this.subject = subject;
	}

	@Override
	public QueryResource getSubject() {
		return subject;
	}

	@Override
	public void setPredicate(QueryResource predicate) {
		this.predicate = predicate;
	}

	@Override
	public QueryResource getPredicate() {
		return predicate;
	}

	@Override
	public void setObject(QueryResource object) {
		this.object = object;
		this.value = null;
		this.valueType = null;
	}

	@Override
	public QueryResource getObject() {
		return object;
	}

	@Override
	public void setObject(String value, XSDDatatype type) {
		this.object = null;
		this.value = value;
		this.valueType = type;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public XSDDatatype getValueType() {
		return valueType;
	}
	
	@Override
	public String toString() {
		String result = subject+" "+predicate+" ";
		if(object == null) {
			if(value == null) {
				result += "null";
			}
			else if(value.contains("\"")) {
				result += "\"\"\""+value+"\"\"\"";
			}
			else {
				result += "\""+value+"\"";
			}
			if(valueType != null) {
				result += "^^xsd:"+valueType.getURI().replace(XSDDatatype.XSD+"#", "");
			}
		}
		else {
			result += object;
		}
		result += " . ";
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		result = prime * result
				+ ((predicate == null) ? 0 : predicate.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result
				+ ((valueType == null) ? 0 : valueType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		GraphPatternImpl other = (GraphPatternImpl) obj;
		if(!compare(subject, other.subject)) {
			return false;
		}
		if(!compare(predicate, other.predicate)) {
			return false;
		}
		if(!compare(object, other.object)) {
			return false;
		}
		if(!compare(value, other.value)) {
			return false;
		}
		if(!compare(valueType, other.valueType)) {
			return false;
		}
		return true;
	}
	
	private boolean compare(Object left, Object right) {
		if(left == null && right == null) {
			return true;
		}
		if(left == null) {
			return false;
		}
		return left.equals(right);
	}
	
	/**
	 * Determines whether a fully specified graph pattern
	 * matches one where S, P, or O are optionally null.
	 * @param other A (potentially underspecified) graph pattern
	 * @return true if this graph pattern is a grounding of other, otherwise false.
	 */
	public boolean matches(GraphPatternImpl other) {
		boolean matches = true;
		matches = matches && (other.subject == null || subject.equals(other.subject));
		matches = matches && (other.predicate == null || predicate.equals(other.predicate));
		if(other.object == null && other.value == null) {
			return matches;
		}
		if(other.object != null) {
			matches = matches && object.equals(other.object);
		}
		else {
			matches = matches && value.equals(other.value);
			matches = matches && (other.valueType == null || valueType.equals(other.valueType)); 
		}
		return matches;
	}

}
