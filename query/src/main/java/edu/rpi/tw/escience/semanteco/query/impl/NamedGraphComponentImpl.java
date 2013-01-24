package edu.rpi.tw.escience.semanteco.query.impl;

import edu.rpi.tw.escience.semanteco.query.NamedGraphComponent;
import edu.rpi.tw.escience.semanteco.query.Query;

/**
 * NamedGraphComponentImpl provides a mechanism for representing
 * a named graph pattern within a SPARQL query.
 * 
 * @author ewpatton
 *
 */
public class NamedGraphComponentImpl extends GraphComponentCollectionImpl implements NamedGraphComponent {

	private String uri = null;
	
	/**
	 * Creates a new NamedGraphComponentImpl that queries
	 * the specified named graph URI
	 * @param uri
	 */
	public NamedGraphComponentImpl(String uri) {
		this.uri = uri;
	}

	@Override
	public String getUri() {
		return uri;
	}
	
	@Override
	public String toString() {
		String res = "graph ";
		if(uri.startsWith(Query.VAR_NS)) {
			res += "?"+uri.replace(Query.VAR_NS, "")+" ";
		}
		else {
			res += "<"+uri+"> ";
		}
		res += super.toString();
		return res;
	}

}
