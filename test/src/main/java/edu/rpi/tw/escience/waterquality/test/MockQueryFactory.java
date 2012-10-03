package edu.rpi.tw.escience.waterquality.test;

import edu.rpi.tw.escience.waterquality.QueryFactory;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.Query.Type;

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
