package edu.rpi.tw.eScience.WaterQualityPortal.model;

import java.io.IOException;
import java.util.List;

public class GraphLookupQuery extends Query {

	public GraphLookupQuery(String state, List<String> sources) {
		super(null);
		queryString =
				"prefix sioc: <http://rdfs.org/sioc/ns#> " +
				"prefix dc: <http://purl.org/dc/terms/> " +
				"select ?src ?graph where {" +
				"graph <http://sparql.tw.rpi.edu/semanteco/data-source> {" +
				"?graph sioc:topic <"+state+"> ; dc:source ?src . " +
				"FILTER(";
		boolean first=true;
		for(String s : sources) {
			if(!first) {
				queryString += "||";
			}
			else {
				first = false;
			}
			queryString += "str(?src)=\""+s+"\"";
		}
		queryString += ") }}";
	}

	@Override
	public Object execute(String endpoint) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
