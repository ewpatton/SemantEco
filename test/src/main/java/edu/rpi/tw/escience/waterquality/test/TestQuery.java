package edu.rpi.tw.escience.waterquality.test;

import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.query.impl.QueryImpl;

public class TestQuery extends QueryImpl implements Query {
	
	public TestQuery(Type type) {
		super(type);
	}

}
