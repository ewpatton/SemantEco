package edu.rpi.tw.escience.waterquality.query;

import edu.rpi.tw.escience.waterquality.QueryFactory;
import edu.rpi.tw.escience.waterquality.query.Query.Type;
import edu.rpi.tw.escience.waterquality.query.impl.QueryImpl;

/**
 * QueryFactoryImpl provides a mechanism for various SemantAqua subsystems
 * to create Query objects.
 * 
 * @author ewpatton
 *
 */
public class QueryFactoryImpl implements QueryFactory {

	private static QueryFactoryImpl instance = null;
	
	protected QueryFactoryImpl() {
		
	}
	
	@Override
	public Query newQuery() {
		return new QueryImpl(Type.SELECT);
	}

	@Override
	public Query newQuery(Type type) {
		return new QueryImpl(type);
	}

	/**
	 * Gets an instance of the current QueryFactory
	 * @return
	 */
	public static QueryFactory getInstance() {
		if(instance == null) {
			instance = new QueryFactoryImpl();
		}
		return instance;
	}

}
