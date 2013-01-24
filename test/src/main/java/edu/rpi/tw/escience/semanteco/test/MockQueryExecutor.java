package edu.rpi.tw.escience.semanteco.test;

import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.QueryExecutor;
import edu.rpi.tw.escience.semanteco.query.Query;

/**
 * MockQueryExecutor provides a base implementation
 * of QueryExecutor that can be subclassed to provide
 * necessary implementations for unit tests. All
 * methods throw UnsupportedOperationException by
 * default unless explicitly overridden by subclasses.
 * @author ewpatton
 *
 */
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
