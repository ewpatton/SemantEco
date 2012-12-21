package edu.rpi.tw.escience.waterquality.test;

import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.Query.Type;

/**
 * TestQueryFactory provides a basic implementation of
 * QueryFactory that can be used for writing unit tests.
 * @author ewpatton
 *
 */
public class TestQueryFactory extends MockQueryFactory {

	@Override
	public Query newQuery() {
		return newQuery(Type.SELECT);
	}
	
	@Override
	public Query newQuery(Type type) {
		return new TestQuery(type);
	}
	
}
