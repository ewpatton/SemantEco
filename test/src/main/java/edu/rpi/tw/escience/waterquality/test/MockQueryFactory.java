package edu.rpi.tw.escience.waterquality.test;

import edu.rpi.tw.escience.waterquality.QueryFactory;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.Query.Type;

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
