package edu.rpi.tw.escience.semanteco.query;

import edu.rpi.tw.escience.semanteco.QueryFactory;
import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.Query.Type;
import edu.rpi.tw.escience.semanteco.query.impl.QueryImpl;

/**
 * QueryFactoryImpl provides a mechanism for various SemantEco subsystems
 * to create Query objects.
 * 
 * @author ewpatton
 *
 */
public class QueryFactoryImpl implements QueryFactory {

	private static volatile QueryFactoryImpl instance = null;
	
	protected QueryFactoryImpl() {
		
	}
	
	@Override
	public Query newQuery() {
		return newQuery(Type.SELECT);
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
