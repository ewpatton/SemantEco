package edu.rpi.tw.escience.semanteco.test;

import edu.rpi.tw.escience.semanteco.QueryFactory;
import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.Query.Type;

/**
 * MockQueryFactory provides a base implementation of
 * QueryFactory. All methods throw UnsupportedOperationException
 * by default unless explicitly overridden by subclasses.
 * @author ewpatton
 *
 */
public class MockQueryFactory implements QueryFactory {

	@Override
	public Query newQuery() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Query newQuery(Type type) {
		throw new UnsupportedOperationException();
	}

}
