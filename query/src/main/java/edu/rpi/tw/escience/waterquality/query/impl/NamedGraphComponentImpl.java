package edu.rpi.tw.escience.waterquality.query.impl;

import edu.rpi.tw.escience.waterquality.query.NamedGraphComponent;
import edu.rpi.tw.escience.waterquality.query.Query;

public class NamedGraphComponentImpl extends GraphComponentCollectionImpl implements NamedGraphComponent {

	private String uri = null;
	
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
