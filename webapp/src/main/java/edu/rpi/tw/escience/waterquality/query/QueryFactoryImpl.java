package edu.rpi.tw.escience.waterquality.query;

import edu.rpi.tw.escience.waterquality.QueryFactory;
import edu.rpi.tw.escience.waterquality.query.Query.Type;
import edu.rpi.tw.escience.waterquality.query.impl.QueryImpl;

public class QueryFactoryImpl implements QueryFactory {

	private static QueryFactoryImpl instance = null;
	
	@Override
	public Query newQuery() {
		return new QueryImpl(Type.SELECT);
	}

	@Override
	public Query newQuery(Type type) {
		return new QueryImpl(type);
	}

	public static QueryFactory getInstance() {
		if(instance == null) {
			instance = new QueryFactoryImpl();
		}
		return instance;
	}

}
