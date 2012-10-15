package edu.rpi.tw.escience.waterquality.test;

import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.QueryExecutor;
import edu.rpi.tw.escience.waterquality.query.Query;

public class MockQueryExecutor implements QueryExecutor {

	@Override
	public String execute(Query query) {
		throw new UnsupportedOperationException();
	}

	@Override
	public QueryExecutor execute(Query query, Model model) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String execute(String endpoint, Query query) {
		throw new UnsupportedOperationException();
	}

	@Override
	public QueryExecutor execute(String endpoint, Query query, Model model) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getDefaultSparqlEndpoint() {
		throw new UnsupportedOperationException();
	}

	@Override
	public QueryExecutor accept(String mimeType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String executeLocalQuery(Query query) {
		throw new UnsupportedOperationException();
	}

}
