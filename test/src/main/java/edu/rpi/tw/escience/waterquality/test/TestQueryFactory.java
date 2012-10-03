package edu.rpi.tw.escience.waterquality.test;

import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.Query.Type;

public class TestQueryFactory extends MockQueryFactory {

	public Query newQuery() {
		return newQuery(Type.SELECT);
	}
	
	public Query newQuery(Type type) {
		return new TestQuery(type);
	}
	
}
