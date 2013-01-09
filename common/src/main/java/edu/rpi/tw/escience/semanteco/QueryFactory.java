package edu.rpi.tw.escience.semanteco;

import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.query.Query.Type;

/**
 * A QueryFactory object generates new Query
 * objects that modules can use for retrieving
 * data from external services.
 * 
 * @author ewpatton
 *
 */
public interface QueryFactory {
	
	/**
	 * Generates a new Query object
	 * @return
	 */
	Query newQuery();
	
	/**
	 * Generates a new Query object of the specified type
	 * @param type The type of the query desired by the caller
	 * @return
	 */
	Query newQuery(Type type);
}
