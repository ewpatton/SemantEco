package edu.rpi.tw.escience.waterquality.test;

import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.QueryExecutor;
import edu.rpi.tw.escience.waterquality.Request;
import edu.rpi.tw.escience.waterquality.query.Query;

public class MockQueryExecutor implements QueryExecutor {

	@Override
	public String execute(Query query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryExecutor execute(Query query, Model model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String execute(String endpoint, Query query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryExecutor execute(String endpoint, Query query, Model model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDefaultSparqlEndpoint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryExecutor accept(String mimeType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String executeLocalQuery(Request request, Query query) {
		// TODO Auto-generated method stub
		return null;
	}

}
